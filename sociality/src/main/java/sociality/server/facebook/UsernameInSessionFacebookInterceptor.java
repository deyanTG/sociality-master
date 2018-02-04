package sociality.server.facebook;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import sociality.server.conf.AppConfig;

public class UsernameInSessionFacebookInterceptor implements ConnectInterceptor<Facebook> {

	public void preConnect(ConnectionFactory<Facebook> connectionFactory, MultiValueMap<String, String> parameters,
			WebRequest request) {
		String username = request.getUserPrincipal().getName();
		request.setAttribute(AppConfig.Constants.FACEBOOK_USERNAME_KEY, username, WebRequest.SCOPE_SESSION);
	}

	public void postConnect(Connection<Facebook> connection, WebRequest request) {
		request.removeAttribute(AppConfig.Constants.FACEBOOK_USERNAME_KEY, WebRequest.SCOPE_SESSION);
	}

}