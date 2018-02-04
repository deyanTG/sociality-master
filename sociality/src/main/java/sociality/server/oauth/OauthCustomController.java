package sociality.server.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class OauthCustomController {

	@Autowired
	private JwtCustomTokenStore tokenStore;

	@RequestMapping(value = "/oauth/revokeToken", method = RequestMethod.POST)
	public ResponseEntity<OAuth2AccessToken> getAccessToken(Principal principal, @RequestParam String token)
			throws HttpRequestMethodNotSupportedException {

		DefaultOAuth2RefreshToken defaultOauthRefreshToken = new DefaultOAuth2RefreshToken(token);
		tokenStore.removeRefreshToken(defaultOauthRefreshToken);

		return new ResponseEntity<OAuth2AccessToken>(HttpStatus.OK);
	}

}
