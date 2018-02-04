package sociality.server.twitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import sociality.server.audit.Audit;
import sociality.server.dao.UserDao;
import sociality.server.exceptions.BaseException;
import sociality.server.exceptions.Error;
import sociality.server.model.SocialEntity;
import sociality.server.model.TimelineList;
import sociality.server.model.User;

@Service
@Transactional
public class TwitterService {

	@Autowired
	private ConnectionRepository connectionRepository;

	@Autowired
	private UserDao userDao;

	@Autowired
	private Twitter twitter;

	public boolean isTwitterConnected() {
		return !connectionRepository.findConnections(Twitter.class).isEmpty();
	}

	public TwitterSettings getTwitterSettingsFor(User user) {
		return twitterSettings(user);
	}

	public TwitterSettings setTwitterShare(Boolean share, User user) {
		TwitterSettings settings = twitterSettings(user);
		settings.setShare(share);
		return settings;
	}

	@Audit(action = "post_twitter")
	public void postTweet(String message, User currentUser) {
		TwitterSettings settings = getTwitterSettingsFor(currentUser);
		if (isTwitterConnected() && settings.getShare()) {
			twitter.timelineOperations().updateStatus(message);
			return;
		}
	}

	public List<TwitterProfile> getFollowers() {
		if (isTwitterConnected()) {
			CursoredList<TwitterProfile> followers = twitter.friendOperations().getFollowers();
			return followers;
		}
		throw new BaseException(Error.Code.BAR_REQUEST);
	}

	public List<TwitterProfile> getFriends() {
		if (isTwitterConnected()) {
			CursoredList<TwitterProfile> friends = twitter.friendOperations().getFriends();
			return friends;
		}
		throw new BaseException(Error.Code.BAR_REQUEST);
	}

	public TimelineList getPaginatedTimeline(Long maxId, int pageSize) {
		TimelineList resultList = new TimelineList(null);
		if (isTwitterConnected()) {
			int twitterPageSize = pageSize;
			List<Tweet> tweetsList = null;
			if (maxId == null) {
				tweetsList = twitter.timelineOperations().getHomeTimeline(twitterPageSize);
			} else {
				tweetsList = twitter.timelineOperations().getHomeTimeline(twitterPageSize + 1, 0, maxId);
				tweetsList.remove(0);
			}
			for (Tweet t : tweetsList) {
				resultList.addEntity(SocialEntity.getSocialEntity(t));
			}
			return resultList;
		}
		return resultList;
	}

	@Audit(action = "post_twitter")
	public void multiplicateTwitterTweet(String message, List<String> hashTags, User currentUser) {
		TwitterSettings settings = getTwitterSettingsFor(currentUser);
		if (isTwitterConnected() && settings.getShare()) {
			String hashTagsString = hashTags.stream().collect(Collectors.joining(" "));
			twitter.timelineOperations().updateStatus(message + " " + hashTagsString);
			return;
		}
	}

	public Map<String, Object> getTwitterProfile() {
		if (isTwitterConnected()) {
			TwitterProfile twitterProfile = twitter.userOperations().getUserProfile();
			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> mapResult = mapper.convertValue(twitterProfile, Map.class);
			mapResult.put("profileType", "twitter");
			mapResult.put("profileUrl", twitterProfile.getProfileUrl());
			return mapResult;
		}
		return null;
	}

	public TwitterSettings twitterSettings(User user) {
		user = userDao.getById(user.getId());
		return user.getTwitterSettings();
	}

}
