package sociality.server.facebook;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import sociality.server.audit.Audit;
import sociality.server.dao.UserDao;
import sociality.server.exceptions.BaseException;
import sociality.server.exceptions.Error;
import sociality.server.model.SocialEntity;
import sociality.server.model.TimelineList;
import sociality.server.model.User;

@Service
@Transactional
public class FacebookService {

	@Autowired
	private ConnectionRepository connectionRepository;

	@Autowired
	private UserDao userDao;

	@Autowired
	private Facebook facebook;

	private RestTemplate restTemplate = new RestTemplate();

	public boolean isFacebookConnected() {
		return !connectionRepository.findConnections(Facebook.class).isEmpty();
	}

	public String getFacebookProfilePicture() {
		return connectionRepository.findPrimaryConnection(Facebook.class).createData().getImageUrl();
	}

	@Transactional(propagation = Propagation.NEVER)
	@SuppressWarnings("rawtypes")
	public String getUserProfilePicture(String userId) {
		String url = String.format("http://graph.facebook.com/%s/picture?type=square&redirect=false", userId);
		HttpEntity<?> entity = HttpEntity.EMPTY;
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		return ((Map) response.getBody().get("data")).get("url").toString();
	}

	public FacebookSettings facebookSettingsFor(User user) {
		return facebookSettings(user);
	}

	public FacebookSettings setFacebookShare(Boolean share, User user) {
		FacebookSettings settings = facebookSettings(user);
		settings.setShare(share);
		return settings;
	}

	@Audit(action = "post_facebook")
	public void multiplicateFacebookPost(String message, List<String> groupsId, User currentUser) {
		FacebookSettings settings = facebookSettingsFor(currentUser);
		if (isFacebookConnected() && settings.getShare()) {
			if (groupsId.contains("personalFeedId")) {
				facebook.feedOperations().updateStatus(message);
			}
			groupsId.remove("personalFeedId");
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.set("message", message);
			for (String groupId : groupsId) {
				facebook.post(groupId, "feed", map);
			}
		}
	}

	public List<Group> getUserGroups() {
		if (isFacebookConnected()) {
			return facebook.fetchConnections("me", "groups", Group.class);
		}
		return ImmutableList.of();
	}

	@Audit(action = "post_facebook")
	public void postMessage(String message, User currentUser) {
		FacebookSettings settings = facebookSettingsFor(currentUser);
		if (isFacebookConnected() && settings.getShare()) {
			facebook.feedOperations().updateStatus(message);
			return;
		}
		// throw new RuntimeException("User not connected!");
	}

	public TimelineList getPaginatedFeed(int page, int pageSize, Long maxId, String pagingToken, Long until) {
		TimelineList list = new TimelineList(null);
		if (isFacebookConnected()) {
			int facebookPageSize = pageSize;

			PagingParameters pagingParameter = null;
			if (pagingToken == null || until == null) {
				pagingParameter = new PagingParameters(facebookPageSize, page, null, null);
			} else {
				pagingParameter = new PagingParameters(facebookPageSize, null, null, until, null, null, pagingToken);
			}

			PagedList<Post> feeds = facebook.feedOperations().getFeed(pagingParameter);
			list.setFacebookPaging(new FacebookPaging(feeds.getNextPage()));
			for (Post p : feeds) {
				SocialEntity entity = SocialEntity.getSocialEntity(p);
				FacebookSocialEntity fEntity = (FacebookSocialEntity) entity;
				String profilePicture = getUserProfilePicture(p.getFrom().getId());
				fEntity.setProfilePictureUrl(profilePicture);
				fEntity.setProfileSourceUrl(entity.getLink());
				list.addEntity(fEntity);
			}
		}
		return list;
	}

	public Map<String, Object> getFacebookProfile() {
		if (isFacebookConnected()) {
			org.springframework.social.facebook.api.User facebookProfile = facebook.userOperations().getUserProfile();
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> mapResult = mapper.convertValue(facebookProfile, Map.class);
			mapResult.put("profileImageUrl", getFacebookProfilePicture());
			mapResult.put("profileType", "facebook");
			mapResult.put("profileUrl", facebookProfile.getLink());
			return mapResult;
		}
		return null;
	}
	
	public List<Reference> getFriends() {
		if (isFacebookConnected()) {
			PagedList<Reference> friends= facebook.friendOperations().getFriends();
			return friends;
		}
		throw new BaseException(Error.Code.BAR_REQUEST);
	}

	public FacebookSettings facebookSettings(User user) {
		user = userDao.getById(user.getId());
		return user.getFacebookSettings();
	}

}
