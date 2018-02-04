package sociality.server.common;

import java.util.Comparator;
import java.util.Date;

import org.springframework.stereotype.Component;

import sociality.server.model.SocialEntity;

@Component
public class TimelineEntityComparator implements Comparator<SocialEntity> {

	@Override
	public int compare(SocialEntity o1, SocialEntity o2) {
		Date object1Date = o1.getCreationTime();
		Date object2Date = o2.getCreationTime();
		return object2Date.compareTo(object1Date);
	}

}
