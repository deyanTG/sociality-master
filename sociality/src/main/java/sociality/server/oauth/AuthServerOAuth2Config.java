package sociality.server.oauth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.google.common.collect.Lists;

import sociality.server.services.UserDetailsServiceImpl;

@Configuration
@EnableAuthorizationServer
public class AuthServerOAuth2Config extends AuthorizationServerConfigurerAdapter {

	private String resourceId = "1";

	int accessTokenValiditySeconds = 3600;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier(value = "userDetailsService")
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	DefaultTokenServices tokenServices;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenServices(tokenServices()).authenticationManager(authenticationManager);
		endpoints.exceptionTranslator(exceptionTranslator());

	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.allowFormAuthenticationForClients();

	}

	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.withClientDetails(clientDetails());

	}

	@Bean	
	public WebResponseExceptionTranslator exceptionTranslator() {
		return new CustomWebResponseExceptionTranslator();
	}

	@Bean
	@Qualifier("jwtTokenStore")
	public JwtCustomTokenStore tokenStore() {
		return new JwtCustomTokenStore(jwtTokenEnhancer(), new JdbcTokenStore(dataSource));
	}

	@Bean
	@Qualifier("jwtConverter")
	protected JwtAccessTokenConverter jwtTokenEnhancer() {
		JwtAccessTokenConverter jatc = new TokenOauthEnhancerService();
		jatc.setSigningKey("123");
		return jatc;
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Lists.newArrayList(jwtTokenEnhancer()));

		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setClientDetailsService(clientDetails());
		defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setReuseRefreshToken(true);
		return defaultTokenServices;
	}
	
	@Bean
	public JdbcClientDetailsService clientDetails(){
		return new JdbcClientDetailsService(dataSource);
	}

}
