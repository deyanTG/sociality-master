package sociality.server.facebook;

import java.util.Date;

import org.springframework.social.facebook.api.Post;

import sociality.server.model.SocialEntity;
import sociality.server.model.SocialEntityType;

public class FacebookSocialEntity extends SocialEntity {

	private SocialEntityType socialEntityType = SocialEntityType.FACEBOOK;
	private Post entity;
	// Couldn't find easy way for getting user's profile picture so we do this
	// ugly thing
	private String profilePictureUrl;

	private String profileSourceUrl;

	public FacebookSocialEntity(Post p) {
		this.entity = p;
		this.socialEntityType = SocialEntityType.FACEBOOK;
	}

	@Override
	public SocialEntityType getSocialEntityType() {
		return socialEntityType;
	}

	@Override
	public Date getCreationTime() {
		return entity.getCreatedTime();
	}

	@Override
	public String getCreatorName() {
		return entity.getFrom().getName();
	}

	@Override
	public String getLink() {
		return entity.getLink();
	}

	@Override
	public String getText() {
		return entity.getMessage();
	}

	@Override
	public String getType() {
		return entity.getType().toString();
	}

	@Override
	public String getProfilePicture() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	@Override
	public Date getLastUpdatedTime() {
		return entity.getUpdatedTime();
	}

	@Override
	public String getId() {
		return entity.getId();
	}

	@Override
	public String getTitle() {
		return entity.getStory();
	}

	@Override
	public String getPostPicture() {
		return entity.getPicture();
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public String getProfileSourceUrl() {
		return "https://www.facebook.com/app_scoped_user_id/" + entity.getFrom().getId();
//		return null;
	}

	public void setProfileSourceUrl(String profileSourceUrl) {
		this.profileSourceUrl = profileSourceUrl;
	}

}
