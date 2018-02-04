package sociality.server.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sociality.server.facebook.FacebookService;
import sociality.server.model.TimelineList;
import sociality.server.model.User;
import sociality.server.resolvers.JsonArg;
import sociality.server.twitter.TwitterService;

@Service
@Transactional
public class CommonOperationsService {

	@Autowired
	private TimelineEntityComparator comparator;

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private FacebookService facebookService;

	public TimelineList getTimeline(int page, int pageSize, Long maxId, String pagingToken, Long until,
			User currentUser) {
		int twitterPageSize, facebookPageSize;
		twitterPageSize = facebookPageSize = pageSize;
		TimelineList facebookList = facebookService.getPaginatedFeed(twitterPageSize, facebookPageSize, maxId,
				pagingToken, until);
		TimelineList twitterList = twitterService.getPaginatedTimeline(maxId, twitterPageSize);
		TimelineList result = new TimelineList(comparator);
		result.addAllEntities(facebookList.getEntities());
		result.addAllEntities(twitterList.getEntities());
		return result.sort();
	}

	public void multiplicatePosts(String message, Boolean twitterOn, @JsonArg Boolean facebookOn, List<String> groupsId,
			List<String> hashTags, User currentUser) {
		if (twitterOn) {
			twitterService.multiplicateTwitterTweet(message, hashTags, currentUser);
		}
		if (facebookOn) {
			facebookService.multiplicateFacebookPost(message, groupsId, currentUser);
		}
	}

}
