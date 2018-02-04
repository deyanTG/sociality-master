package sociality.server.conf;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.facebook.web.DisconnectController;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import sociality.server.controllers.CustomSocialityConnectController;
import sociality.server.facebook.PostToWallInterceptor;
import sociality.server.facebook.UsernameInSessionFacebookInterceptor;
import sociality.server.twitter.TweetInterceptor;
import sociality.server.twitter.UsernameInSessionTwitterInterceptor;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

	@Autowired
	private DataSource dataSource;

	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
		cfConfig.addConnectionFactory(
				new TwitterConnectionFactory(env.getProperty("twitter.appKey"), env.getProperty("twitter.appSecret")));
		cfConfig.addConnectionFactory(new CustomOauth2FacebookConnectionFactory(env.getProperty("facebook.appKey"),
				env.getProperty("facebook.appSecret")));
	}

	private class CustomOauth2FacebookConnectionFactory extends FacebookConnectionFactory {

		public CustomOauth2FacebookConnectionFactory(String appId, String appSecret) {
			super(appId, appSecret);
		}

		@Override
		public boolean supportsStateParameter() {
			return true;
		}

	}

	@Override
	public UserIdSource getUserIdSource() {
		return new UserIdSource() {
			@Override
			public String getUserId() {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication == null) {
					throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
				}

				if (authentication.getName().equalsIgnoreCase("anonymousUser")) {
					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
							.currentRequestAttributes()).getRequest();
					// FACEBOOK flow
					Object user = request.getSession().getAttribute(AppConfig.Constants.FACEBOOK_USERNAME_KEY);
					if (user != null) {
						return user.toString();
					}

					// TWITTER flow
					user = request.getSession().getAttribute(AppConfig.Constants.TWITTER_USERNAME_KEY);
					if (user != null) {
						return user.toString();
					}
				}
				return authentication.getName();
			}
		};
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
	}

	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		ConnectController connectController = new CustomSocialityConnectController(connectionFactoryLocator,
				connectionRepository);
		connectController.addInterceptor(new UsernameInSessionFacebookInterceptor());
		connectController.addInterceptor(new UsernameInSessionTwitterInterceptor());
		connectController.addInterceptor(new PostToWallInterceptor());
		connectController.addInterceptor(new TweetInterceptor());
		return connectController;
	}

	@Bean
	public DisconnectController disconnectController(UsersConnectionRepository usersConnectionRepository,
			Environment environment) {
		return new DisconnectController(usersConnectionRepository, environment.getProperty("facebook.appSecret"));
	}

	@Bean
	public ReconnectFilter apiExceptionHandler(UsersConnectionRepository usersConnectionRepository,
			UserIdSource userIdSource) {
		return new ReconnectFilter(usersConnectionRepository, userIdSource);
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Facebook facebook(ConnectionRepository repository) {
		Connection<Facebook> connection = repository.findPrimaryConnection(Facebook.class);
		return connection != null ? connection.getApi() : null;
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public Twitter twitter(ConnectionRepository repository) {
		Connection<Twitter> connection = repository.findPrimaryConnection(Twitter.class);
		return connection != null ? connection.getApi() : null;
	}

}