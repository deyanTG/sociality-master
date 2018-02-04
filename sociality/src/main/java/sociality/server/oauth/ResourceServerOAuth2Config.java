package sociality.server.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerOAuth2Config extends ResourceServerConfigurerAdapter {

	private String resourceId = "1";

	@Autowired
	private DefaultTokenServices tokenServices;

	@Autowired
	private CustomAuthorizationTokenExtractor customAuthorizationTokenExtractor;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		final OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
		oAuth2AuthenticationManager.setTokenServices(tokenServices);
		resources.tokenExtractor(customAuthorizationTokenExtractor).resourceId(resourceId)
				.authenticationManager(oAuth2AuthenticationManager);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().authorizeRequests().antMatchers("/oauth/revokeToken", "/connect/**", "/register")
				.permitAll().antMatchers("/admin/**").hasAnyRole("MODERATOR","ADMIN").antMatchers("/**").authenticated().and()
				.formLogin().permitAll().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and().apply(new SpringSocialConfigurer());
	}

}
