package sociality.server.admin;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;

import sociality.server.miscellaneous.Responses;
import sociality.server.model.User;
import sociality.server.resolvers.JsonArg;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@RequestMapping(value = "/searchUser", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> search(@RequestParam String username) {
		Map<String, Object> result = adminService.searchUsersForAdmin(username);
		return Responses.ok(result);
	}

	@RequestMapping(value = "/getUsers", method = RequestMethod.GET)
	public ResponseEntity<List<Map<String, Object>>> getUsers() {
		List<Map<String, Object>> result = adminService.getUsersForAdmin();
		return Responses.ok(result);
	}


	@RequestMapping(value = "/manipulateUser", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> manipulateUser(@JsonArg String username, @JsonArg Boolean ban) {
		adminService.manipulateUser(username, !ban);
		return Responses.ok("ok");
	}

	@RequestMapping(value = "/addAdmin", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<User> addAdmin(@JsonArg String username, @JsonArg String password,
			@JsonArg Boolean manageUsers) {
		User u = adminService.addAdmin(username, password, manageUsers);
		return Responses.ok(u);
	}

	@RequestMapping(value = "/getClients", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<Map<String, Object>>> getClients() {
		List<Map<String, Object>> clients = adminService.getApiClients();
		return Responses.ok(clients);
	}

	@RequestMapping(value = "/getOauthLink", method = RequestMethod.GET)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ResponseBody
	public ResponseEntity<Map<String, String>> getOauthLink(@RequestParam String clientId)
			throws UnsupportedEncodingException {
		String oauthLink = adminService.getOauthLink(clientId);
		Map<String, String> result = ImmutableMap.of("oauthLink", oauthLink);
		return Responses.ok(result);
	}

	@RequestMapping(value = "/updateApi", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> updateApi(@JsonArg String clientId, @JsonArg String oldClientId,
			@JsonArg String redirectUri, @JsonArg Integer accessTokenValidity) {
		adminService.updateApi(clientId, oldClientId, redirectUri, accessTokenValidity, -1);
		return Responses.ok("ok");
	}

	@RequestMapping(value = "/addClient", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> addApi(@JsonArg String clientId, @JsonArg String redirectUri,
			@JsonArg Integer accessTokenValidity) {
		adminService.addApi(clientId, redirectUri, accessTokenValidity, -1);
		return Responses.ok("ok");
	}
}
