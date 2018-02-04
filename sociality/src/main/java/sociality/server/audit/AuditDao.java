package sociality.server.audit;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import sociality.server.model.AuditObject;
import sociality.server.model.User;

@Repository
public class AuditDao {

	@Autowired
	private SessionFactory factory;

	public Long save(AuditObject audit) {
		return (Long) factory.getCurrentSession().save(audit);
	}

	public List<AuditObject> getAuditsForUser(User user) {
		String hqlQuery = "from audits a where a.username = :username order by createdAt desc";
		return factory.getCurrentSession().createQuery(hqlQuery).setString("username", user.getUsername()).list();
	}
}
