package sociality.server.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import sociality.server.facebook.FacebookPaging;

public class TimelineList {

	private Comparator<SocialEntity> comparator;
	private FacebookPaging facebookPaging;

	public TimelineList(Comparator<SocialEntity> comparator) {
		super();
		this.comparator = comparator;
	}

	private List<SocialEntity> entities = new ArrayList<>();

	public List<SocialEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<SocialEntity> entities) {
		this.entities = entities;
	}

	public void addEntity(SocialEntity entity) {
		entities.add(entity);
	}

	public void addAllEntities(List<SocialEntity> entities) {
		this.entities.addAll(entities);
	}

	public TimelineList sort() {
		entities.sort(comparator);
		return this;
	}

	public FacebookPaging getFacebookPaging() {
		return facebookPaging;
	}

	public void setFacebookPaging(FacebookPaging facebookPaging) {
		this.facebookPaging = facebookPaging;
	}

}
