package sociality.server.model;

import java.util.Date;

import org.springframework.social.facebook.api.Post;
import org.springframework.social.twitter.api.Tweet;

import sociality.server.facebook.FacebookSocialEntity;
import sociality.server.twitter.TwitterSocialEntity;

public abstract class SocialEntity {

	public static SocialEntity getSocialEntity(Object o) {
		if (o instanceof Tweet) {
			return new TwitterSocialEntity((Tweet) o);
		}
		if (o instanceof Post) {
			return new FacebookSocialEntity((Post) o);
		}
		return null;
	}

	public abstract SocialEntityType getSocialEntityType();

	public abstract Date getCreationTime();
	
	public abstract Date getLastUpdatedTime();

	public abstract String getCreatorName();

	public abstract String getLink();

	public abstract String getText();
	
	public abstract String getType();
	
	public abstract String getProfilePicture();
	
	public abstract String getId();
	
	public abstract String getTitle();
	
	public abstract String getPostPicture();
	
	public abstract String getSource();
	
	public abstract String getProfileSourceUrl();
}
