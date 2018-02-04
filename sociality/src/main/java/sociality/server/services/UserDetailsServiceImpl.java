package sociality.server.services;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;

import com.google.common.collect.Sets;

import sociality.server.dao.UserDao;
import sociality.server.model.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.getByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username + " not found!");
		}
		return new UserDetailsServiceImpl.Account(user);
	}

	public static class Account implements SocialUserDetails {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5007624212836730989L;
		/**
		 * 
		 */
		private User user;

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return Sets.newHashSet(new SimpleGrantedAuthority(user.getRole().name()));
		}

		@Override
		public String getPassword() {
			return user.getPassword();
		}

		@Override
		public String getUsername() {
			return user.getUsername();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return user.getEnabled();
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public Account(User user) {
			this.user = user;
		}

		@Override
		public String getUserId() {
			return user.getId().toString();
		}
	}
}

