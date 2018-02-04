package sociality.server.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sociality.server.dao.UserDao;
import sociality.server.model.User;
import sociality.server.services.UserDetailsServiceImpl;

@Service
@Transactional
public class TokenOauthEnhancerService extends JwtAccessTokenConverter {

	@Autowired
	private UserDao userDao;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		UserDetailsServiceImpl.Account account = (UserDetailsServiceImpl.Account) authentication.getPrincipal();
		User user = userDao.getById(Long.valueOf(account.getUserId()));

		DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
		Map<String, Object> additionalInfo = new HashMap<>(accessToken.getAdditionalInformation());

		String tokenId = result.getValue();
		if (!additionalInfo.containsKey(TOKEN_ID)) {
			additionalInfo.put(TOKEN_ID, tokenId);
		}

		additionalInfo.put("user", user);
		result.setAdditionalInformation(additionalInfo);
		result.setValue(encode(result, authentication));
		return result;// super.enhance(customAccessToken, authentication);
	}

	@Override
	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		if (isRefreshToken(token)) {
			return token.getAdditionalInformation();
		}
		return super.convertAccessToken(token, authentication);
	}

}
