package sociality.server.conf;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.HiddenHttpMethodFilter;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
//
//	@Override
//	protected Set<SessionTrackingMode> getSessionTrackingModes() {
//		return Collections.emptySet();
//	}

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
		CustomCorsFilter cf = new CustomCorsFilter();
		FilterRegistration.Dynamic corsFilterRegistration = servletContext.addFilter("corsFilter", cf);
		corsFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, "/*");

		HiddenHttpMethodFilter hiddenFilterConverter = new HiddenHttpMethodFilter();
		FilterRegistration.Dynamic hiddenFilterRegistration = servletContext.addFilter("hiddenFilterConverter",
				hiddenFilterConverter);
		hiddenFilterRegistration.addMappingForUrlPatterns(dispatcherTypes, false, "/*");
	}

	public static class CustomCorsFilter implements Filter {

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {

		}

		@Override
		public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
				throws IOException, ServletException {

			HttpServletResponse response = (HttpServletResponse) servletResponse;
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			// response.setHeader("Access-Control-Allow-Origin", "*");
			// response.setHeader("Access-Control-Allow-Credentials", "true");
			// response.setHeader("Access-Control-Allow-Methods", "POST, GET,
			// HEAD, OPTIONS");
			// response.setHeader("Access-Control-Allow-Headers", "*");
			// filterChain.doFilter(servletRequest, servletResponse);

			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
			response.setHeader("Access-Control-Max-Age", "3600");
			// response.setHeader("Access-Control-Allow-Headers",
			// "Authorization");
			response.addHeader("Access-Control-Allow-Headers", "Authorization");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type");
			// response.addHeader("Access-Control-Expose-Headers",
			// "xsrf-token");
			if ("OPTIONS".equals(request.getMethod())) {
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				filterChain.doFilter(request, response);
			}

		}

		@Override
		public void destroy() {

		}
	}
}
