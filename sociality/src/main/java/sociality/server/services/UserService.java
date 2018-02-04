package sociality.server.services;

import java.time.LocalDateTime;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sociality.server.dao.UserDao;
import sociality.server.facebook.FacebookSettings;
import sociality.server.model.User;
import sociality.server.twitter.TwitterSettings;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User createRegularUser(String username, String password, String firstName, String lastName) {
		return createUser(username, password, firstName, lastName, User.Role.REGULAR);
	}

	public User createUser(String username, String password, String firstName, String lastName, User.Role role) {
		User u = new User();
		u.setCreatedAt(LocalDateTime.now());
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode(password));
		u.setEnabled(true);
		u.setRole(role);
		u.setTwitterSettings(new TwitterSettings());
		u.setFacebookSettings(new FacebookSettings());
		u.setFirstName(firstName);
		u.setLastName(lastName);
		try {
			userDao.save(u);
		} catch (ConstraintViolationException exception) {
			throw new RuntimeException("username already exists!");
		}
		return u;
	}

	public User getRefreshedCurrentUser(Long id) {
		return userDao.getById(id);
	}

	public User update(Long id, String username, String firstName, String lastName, String password) {
		User u = getRefreshedCurrentUser(id);
		if (username != null) {
			u.setUsername(username);
		}
		if (firstName != null) {
			u.setFirstName(firstName);
		}
		if (lastName != null) {
			u.setLastName(lastName);
		}
		if (password != null) {
			u.setPassword(passwordEncoder.encode(password));
		}
		return u;
	}
}
