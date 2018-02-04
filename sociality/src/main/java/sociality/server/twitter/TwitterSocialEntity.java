package sociality.server.twitter;

import java.util.Date;

import org.springframework.social.twitter.api.Tweet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import sociality.server.model.SocialEntity;
import sociality.server.model.SocialEntityType;

public class TwitterSocialEntity extends SocialEntity {

	@JsonIgnore
	private Tweet entity;
	private SocialEntityType socialEntityType = SocialEntityType.TWITTER;

	public TwitterSocialEntity(Tweet t) {
		this.entity = t;
		socialEntityType = SocialEntityType.TWITTER;
	}

	@Override
	public SocialEntityType getSocialEntityType() {
		return socialEntityType;
	}

	@Override
	public Date getCreationTime() {
		return entity.getCreatedAt();
	}

	@Override
	public String getCreatorName() {
		return entity.getFromUser();
	}

	@Override
	public String getLink() {
		return entity.getUser().getUrl();
	}

	@Override
	public String getText() {
		return entity.getText();
	}

	@Override
	public String getType() {
		return "Tweet";
	}

	@Override
	public String getProfilePicture() {
		return entity.getProfileImageUrl();
	}

	@Override
	public Date getLastUpdatedTime() {
		return null;
	}

	@Override
	public String getId() {
		return String.valueOf(entity.getId());
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getPostPicture() {
		return null;
	}

	@Override
	public String getSource() {
		return entity.getUser().getUrl();
	}

	@Override
	public String getProfileSourceUrl() {
		return "https://www.twitter.com/" + entity.getFromUser();
	}

}
