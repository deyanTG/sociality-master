package sociality.server.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sociality.server.model.User;

@Repository
@Transactional
public class UserDao {

	@Autowired
	private SessionFactory sessionFactory;


	public User getByUsername(String username) {
		String queryString = "SELECT u FROM users u " + "WHERE u.username = :username";
		return (User) sessionFactory.getCurrentSession().createQuery(queryString).setString("username", username)
				.uniqueResult();
	}

	public User getById(Long id) {
		String queryString = "SELECT u FROM users u WHERE u.id = :id ";
		return (User) sessionFactory.getCurrentSession().createQuery(queryString).setLong("id", id).uniqueResult();
	}

	public Long save(User user) {
		return (Long) sessionFactory.getCurrentSession().save(user);
	}

	public List<Object[]> userConnections(String username) {
		String connectionsQuery = "SELECT * FROM USERCONNECTION uc WHERE uc.userId=:username";
		@SuppressWarnings("unchecked")
		List<Object[]> connections = sessionFactory.getCurrentSession().createSQLQuery(connectionsQuery)
				.setString("username", username).list();
		return connections;
	}
}