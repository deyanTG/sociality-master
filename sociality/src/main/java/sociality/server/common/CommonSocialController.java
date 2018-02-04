package sociality.server.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sociality.server.audit.Audit;
import sociality.server.conf.AppConfig;
import sociality.server.miscellaneous.Responses;
import sociality.server.model.TimelineList;
import sociality.server.model.User;
import sociality.server.resolvers.JsonArg;

@Controller
@RequestMapping("/timeline")
public class CommonSocialController {

	@Autowired
	private CommonOperationsService commonOperationsService;

	@RequestMapping(method = RequestMethod.GET)
	@Audit(action = "aggregate")
	public ResponseEntity<TimelineList> showFeed(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "page_size", defaultValue = AppConfig.Constants.DEFAULT_PAGE_SIZE_STR) int pageSize,
			@RequestParam(name = "max_id", required = false) Long maxId,
			@RequestParam(name = "paging_token", required = false) String pagingToken,
			@RequestParam(required = false) Long until, User currentUser) {
		return Responses
				.ok(commonOperationsService.getTimeline(page, pageSize, maxId, pagingToken, until, currentUser));
	}

	@RequestMapping(value = "/post", method = RequestMethod.POST)
//	@Audit(action = "post_in_social_networks")
	public ResponseEntity<?> postTweet(@JsonArg(required = true) String message, @JsonArg Boolean twitterOn,
			@JsonArg Boolean facebookOn, @JsonArg(required = false) List<String> groupsId,
			@JsonArg(required = false) List<String> hashTags, User currentUser) {
		commonOperationsService.multiplicatePosts(message, twitterOn, facebookOn, groupsId, hashTags, currentUser);
		return Responses.ok();
	}

}
