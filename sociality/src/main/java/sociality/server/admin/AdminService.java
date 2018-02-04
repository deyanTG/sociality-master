package sociality.server.admin;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import sociality.server.audit.AuditService;
import sociality.server.dao.ApiClientsDao;
import sociality.server.dao.UserDao;
import sociality.server.model.AuditObject;
import sociality.server.model.User;
import sociality.server.services.UserService;

@Service
@Transactional
public class AdminService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserService userService;

	@Autowired
	private ApiClientsDao apiClientsDao;

	@Autowired
	private AdminDao adminDao;

	@Autowired
	private AuditService auditService;

	private static final String SERVER_URL = "http://localhost:8081/sociality";

	public void manipulateUser(String username, Boolean banned) {
		User user = userDao.getByUsername(username);
		user.setEnabled(banned);
	}

	public User addAdmin(String username, String password, Boolean createApiClients) {
		User.Role role = createApiClients ? User.Role.ROLE_ADMIN : User.Role.ROLE_MODERATOR;
		return userService.createUser(username, password, null, null, role);
	}

	public List<Map<String, Object>> getApiClients() {
		List<Map<String, Object>> clients = apiClientsDao.getApiClients();
		return clients.stream().filter(client -> !client.get("authorized_grant_types").toString().contains("password"))
				.collect(Collectors.toList());
	}

	public String getOauthLink(String clientId) throws UnsupportedEncodingException {
		Map<String, Object> clientOauthDetails = apiClientsDao.getApiClient(clientId);
		StringBuilder builder = new StringBuilder();
		String oauthLink = SERVER_URL + "/oauth/authorize?response_type=token&client_id=";
		builder.append(clientOauthDetails.get("client_id").toString()).append("&").append("redirect_uri=")
				.append(clientOauthDetails.get("web_server_redirect_uri").toString()).append("&").append("scope=")
				.append(Lists.newArrayList(clientOauthDetails.get("scope").toString().split(",")).stream()
						.collect(Collectors.joining(" ")))
				.append("&").append("state=");
		return oauthLink + builder.toString();
	}

	public void updateApi(String clientId, String oldClientId, String redirectUri, Integer accessTokenValidity,
			Integer refreshTokenValidity) {
		apiClientsDao.updateApi(clientId, oldClientId, redirectUri, accessTokenValidity, refreshTokenValidity);

	}

	public void addApi(String clientId, String redirectUri, Integer accessTokenValidity, Integer refreshTokenValidity) {
		apiClientsDao.addClient(clientId, redirectUri, accessTokenValidity, refreshTokenValidity);

	}

	public Map<String, Object> searchUsersForAdmin(String username) {
		User user = userDao.getByUsername(username);
		List<AuditObject> audits = new ArrayList<>();
		if (user != null) {
			audits = auditService.getAuditsForUser(user);
		}
		return ImmutableMap.of("user", getUserProperties(user), "audits", audits);
	}

	public List<Map<String, Object>> getUsersForAdmin() {
		List<User> users = adminDao.getAllUsers();
		List<Map<String, Object>> result = new ArrayList<>();
		for (User u : users) {
			Map<String, Object> userProperties = getUserProperties(u);
			result.add(userProperties);
		}
		return result;
	}

	private Map<String, Object> getUserProperties(User user) {
		if (user == null) {
			return ImmutableMap.of();
		}
		List<Object[]> userConnections = userDao.userConnections(user.getUsername());
		Map<String, Object> result = new HashMap<>();
		result.put("userData", user);
		List<Map<String, String>> socialProfiles = new ArrayList<>();
		for (Object[] connection : userConnections) {
			String[] keys = { "provider", "displayName", "profileUrl", "imageUrl" };
			String[] values = { connection[1].toString(), connection[4].toString(), connection[5].toString(),
					connection[6].toString() };
			Map<String, String> map = new HashMap<String, String>();
			for (int index = 0; index < keys.length; index++) {
				map.put(keys[index], values[index]);
			}
			socialProfiles.add(map);
		}
		result.put("socialProfiles", socialProfiles);
		return result;
	}
}
