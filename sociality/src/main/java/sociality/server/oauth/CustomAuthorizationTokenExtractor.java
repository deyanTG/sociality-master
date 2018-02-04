package sociality.server.oauth;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationTokenExtractor extends BearerTokenExtractor{
	
	private final static Log logger = LogFactory.getLog(CustomAuthorizationTokenExtractor.class);
	private final static String TOKEN_KEY = "token";
	
	@Override
	public Authentication extract(HttpServletRequest request){
		Authentication auth = super.extract(request);
		if(auth == null){
			String token = request.getParameter(TOKEN_KEY);
			if(token == null){
				logger.debug("Token not found in 'token' parameter neither.");
			}else{
				logger.debug("Token  found in 'token' parameter.");
				request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
				auth = new PreAuthenticatedAuthenticationToken(token, "");
			}
		}
		return auth;
	}
}
