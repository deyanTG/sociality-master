package sociality.server.oauth;

import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.transaction.annotation.Transactional;

public class JwtCustomTokenStore extends JwtTokenStore {

	private JdbcTokenStore jdbcTokenStore;

	public JwtCustomTokenStore(JwtAccessTokenConverter jwtTokenEnhancer, JdbcTokenStore jdbcTokenStore) {
		super(jwtTokenEnhancer);
		this.jdbcTokenStore = jdbcTokenStore;
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		jdbcTokenStore.storeRefreshToken(refreshToken, authentication);
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		return jdbcTokenStore.readRefreshToken(tokenValue);
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return jdbcTokenStore.readAuthenticationForRefreshToken(token);
	}

	@Override
	@Transactional
	public void removeRefreshToken(OAuth2RefreshToken token) {
		jdbcTokenStore.removeRefreshToken(token);
	}
	
	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
	}


}
