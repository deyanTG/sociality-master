package sociality.server.twitter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sociality.server.audit.Audit;
import sociality.server.conf.AppConfig;
import sociality.server.miscellaneous.Responses;
import sociality.server.model.Status;
import sociality.server.model.TimelineList;
import sociality.server.model.User;
import sociality.server.resolvers.JsonArg;

@Controller
@RequestMapping("/twitter")
public class MainTwitterController {

	@Autowired
	private TwitterService twitterService;

	@ResponseBody
	@RequestMapping(value = "/user/isConnected", method = RequestMethod.GET)
	public <T extends Status> ResponseEntity<Status> isUserConnected(User currentUser) {
		if (twitterService.isTwitterConnected()) {
			return Responses.ok(new Status(true));
		}
		return Responses.ok(new Status(false));
	}

	@RequestMapping(value = "/user/profile", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> twitterProfile(User currentUser) {
		Map<String, Object> profile = twitterService.getTwitterProfile();
		return Responses.ok(profile);
	}

	@RequestMapping(value = "/timeline", method = RequestMethod.GET)
	@Audit(action = "twitter_timeline")
	public ResponseEntity<TimelineList> showTimeline(@RequestParam(name = "max_id", required = false) Long maxId,
			@RequestParam(name = "page_size", defaultValue = AppConfig.Constants.DEFAULT_PAGE_SIZE_STR) int pageSize,
			User currentUser) {
		return Responses.ok(twitterService.getPaginatedTimeline(maxId, pageSize));
	}

	@RequestMapping(value = "/friends", method = RequestMethod.GET)
	public ResponseEntity<List<TwitterProfile>> friends() {
		return Responses.ok(twitterService.getFriends());
	}

	@RequestMapping(value = "/followers", method = RequestMethod.GET)
	public ResponseEntity<List<TwitterProfile>> followers() {
		return Responses.ok(twitterService.getFollowers());
	}

	@RequestMapping(value = "/tweet", method = RequestMethod.POST)
	@Audit(action = "post_twitter")
	public ResponseEntity<?> postTweet(String message, User currentUser) {
		twitterService.postTweet(message, currentUser);
		return Responses.ok();
	}

	@ResponseBody
	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public ResponseEntity<TwitterSettings> twitterSettings(User currentUser) {
		TwitterSettings settings = twitterService.getTwitterSettingsFor(currentUser);
		return Responses.ok(settings);
	}

	@ResponseBody
	@RequestMapping(value = "/settings/sharePost", method = RequestMethod.POST)
	public ResponseEntity<TwitterSettings> facebookSharePost(@JsonArg Boolean share, User currentUser) {
		TwitterSettings settings = twitterService.setTwitterShare(share, currentUser);
		return Responses.ok(settings);
	}

	@RequestMapping(value = "/getHashtags", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> showGroup(User currentUser) {
		return null;
	}
}
