package sociality.server.twitter;

import org.springframework.social.DuplicateStatusException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.WebRequest;

public class TweetInterceptor implements ConnectInterceptor<Twitter> {

	public void preConnect(ConnectionFactory<Twitter> provider, MultiValueMap<String, String> parameters, WebRequest request) {
		if (StringUtils.hasText(request.getParameter(POST_TWEET_PARAMETER))) {
			request.setAttribute(POST_TWEET_ATTRIBUTE, Boolean.TRUE, WebRequest.SCOPE_SESSION);
		}
	}

	public void postConnect(Connection<Twitter> connection, WebRequest request) {
		if (request.getAttribute(POST_TWEET_ATTRIBUTE, WebRequest.SCOPE_SESSION) != null) {
			try {
				connection.updateStatus("I am using SOCIALITY now!!!");
			} catch (DuplicateStatusException e) {
			}
			request.removeAttribute(POST_TWEET_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		}
	}

	private static final String POST_TWEET_PARAMETER = "postTweet";

	private static final String POST_TWEET_ATTRIBUTE = "twitterConnect." + POST_TWEET_PARAMETER;
}
