package sociality.server.admin;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import sociality.server.model.User;

@Repository
public class AdminDao {

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() {
		return sessionFactory.getCurrentSession().createCriteria(User.class).list();
	}
	
	public User searchUsers(String query) {
		String sqlQuery = "SELECT i FROM USERS where u.username = :username";
		return (User) sessionFactory.getCurrentSession().createQuery(sqlQuery).setString("username", query)
				.uniqueResult();
	}
}
