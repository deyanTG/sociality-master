package sociality.server.facebook;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.Reference;
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
@RequestMapping("/facebook")
public class MainFacebookController {

	@Autowired
	private FacebookService facebookService;

	@ResponseBody
	@RequestMapping(value = "/user/isConnected", method = RequestMethod.GET)
	public ResponseEntity<Status> isUserConnected(User currentUser) {
		if (facebookService.isFacebookConnected()) {
			return Responses.ok(new Status(true));
		}
		return Responses.ok(new Status(false));
	}

	@RequestMapping(value = "/user/profile", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> facebookProfile(User currentUser) {
		Map<String, Object> profile = facebookService.getFacebookProfile();
		return Responses.ok(profile);
	}

	@RequestMapping(value = "/feed", method = RequestMethod.GET)
	@Audit(action="facebook_feed")
	public ResponseEntity<TimelineList> showFeed(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "page_size", defaultValue = AppConfig.Constants.DEFAULT_PAGE_SIZE_STR) int pageSize,
			@RequestParam(name = "max_id", required = false) Long maxId,
			@RequestParam(name = "paging_token", required = false) String pagingToken,
			@RequestParam(required = false) Long until, User currentUser) {
		TimelineList list = facebookService.getPaginatedFeed(page, pageSize, maxId, pagingToken, until);
		return Responses.ok(list);
	}

	@Audit(action="post_facebook")
	@RequestMapping(value = "/post", method = RequestMethod.POST)
	public ResponseEntity<?> post(String message, User currentUser) {
		facebookService.postMessage(message, currentUser);
		return Responses.ok();
	}

	@ResponseBody
	@RequestMapping(value = "/settings", method = RequestMethod.GET)
	public ResponseEntity<FacebookSettings> facebookSettings(User currentUser) {
		FacebookSettings settings = facebookService.facebookSettingsFor(currentUser);
		return Responses.ok(settings);
	}

	@ResponseBody
	@RequestMapping(value = "/settings/sharePost", method = RequestMethod.POST)
	public ResponseEntity<FacebookSettings> facebookSharePost(@JsonArg Boolean share, User currentUser) {
		FacebookSettings settings = facebookService.setFacebookShare(share, currentUser);
		return Responses.ok(settings);
	}

	@RequestMapping(value = "/getGroups", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Group>> showGroup(User currentUser) {
		return Responses.ok(facebookService.getUserGroups());
	}

	@RequestMapping(value = "/friends", method = RequestMethod.GET)
	public ResponseEntity<List<Reference>> friends() {
		return Responses.ok(facebookService.getFriends());
	}

}
