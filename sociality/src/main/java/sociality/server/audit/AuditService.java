package sociality.server.audit;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sociality.server.model.AuditObject;
import sociality.server.model.User;

@Service
@Transactional
public class AuditService {

	@Autowired
	private AuditDao auditDao;

	enum Actions {
		REGISTERED("registered"), GET_AGGREGATED_CONTENT("got aggregated content"), POST_TWITTER(
				"posted in twitter"), POST_FACEBOOK("posted in facebook"), FACEBOOK_FEED(
						"got facebook feed"), TWITTER_TIMELINE(
								"got twitter timeline"), POST_IN_SOCIAL_NETWORKS("posted in social networks");
		private String nameOfAction;

		private Actions(String nameOfAction) {
			this.nameOfAction = nameOfAction;
		}

		public String getNameOfAction() {
			return nameOfAction;
		}

	}

	public void audit(String action, User currentUser) {
		createAudit(action, currentUser);
	}

	public void auditRegister(List<Object> args) {
		AuditObject audit = new AuditObject();
		String username = args.iterator().next().toString();
		audit.setActionDescription(Actions.REGISTERED.getNameOfAction());
		audit.setCreatedAt(LocalDateTime.now());
		audit.setUsername(username);
		auditDao.save(audit);

	}

	public List<AuditObject> getAuditsForUser(User user) {
		List<AuditObject> audits = auditDao.getAuditsForUser(user);
		return audits;
	}

	private Long createAudit(String auditMessage, User u) {
		AuditObject audit = new AuditObject();
		audit.setActionDescription(determineAction(auditMessage));
		audit.setCreatedAt(LocalDateTime.now());
		audit.setUsername(u.getUsername());
		return auditDao.save(audit);
	}

	private String determineAction(String action) {
		switch (action) {
		case "post_facebook":
			return Actions.POST_FACEBOOK.getNameOfAction();
		case "post_twitter":
			return Actions.POST_TWITTER.getNameOfAction();
		case "aggregate":
			return Actions.GET_AGGREGATED_CONTENT.getNameOfAction();
		case "facebook_feed":
			return Actions.FACEBOOK_FEED.getNameOfAction();
		case "twitter_timeline":
			return Actions.TWITTER_TIMELINE.getNameOfAction();
		case "post_in_social_networks":
			return Actions.POST_IN_SOCIAL_NETWORKS.getNameOfAction();
		default:
			return null;
		}
	}
}
