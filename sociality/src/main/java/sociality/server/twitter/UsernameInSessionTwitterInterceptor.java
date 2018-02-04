package sociality.server.twitter;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import sociality.server.conf.AppConfig;

public class UsernameInSessionTwitterInterceptor implements ConnectInterceptor<Twitter> {

	public void preConnect(ConnectionFactory<Twitter> provider, MultiValueMap<String, String> parameters,
			WebRequest request) {
		String username = request.getUserPrincipal().getName();
		request.setAttribute(AppConfig.Constants.TWITTER_USERNAME_KEY, username, WebRequest.SCOPE_SESSION);
	}

	public void postConnect(Connection<Twitter> connection, WebRequest request) {
		request.removeAttribute(AppConfig.Constants.TWITTER_USERNAME_KEY, WebRequest.SCOPE_SESSION);
	}


}
