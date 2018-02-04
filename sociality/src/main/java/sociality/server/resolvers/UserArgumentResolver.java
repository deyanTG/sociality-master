package sociality.server.resolvers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import sociality.server.model.User;

import java.util.Map;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	private JwtTokenStore jwtTokenStore;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return User.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth instanceof OAuth2Authentication)) {
			throw new InsufficientAuthenticationException("no auth token provided");
		}
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
		Map<String, Object> info = jwtTokenStore.readAccessToken(details.getTokenValue()).getAdditionalInformation();

		if (info.get("user") == null) {
			throw new InsufficientAuthenticationException("no user in auth token");
		}
		Map<String, Object> infoUser = (Map<String, Object>) info.get("user");

		User user = new User();
		user.setUsername(infoUser.get("username").toString());
		user.setId(Long.valueOf(infoUser.get("id").toString()));
		return user;
	}

	private Long getId(Map<String, Object> infoUser) {
		Object id = infoUser.get("id");
		if (id != null && id instanceof Integer) {
			return ((Integer) id).longValue();
		}
		return (Long) id;
	}

}
