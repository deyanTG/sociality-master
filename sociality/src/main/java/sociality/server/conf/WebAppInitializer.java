	package sociality.server.conf;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		// rootContext.scan(ClassUtils.getPackageName(this.getClass()));
		rootContext.register(WebConfig.class);

		servletContext.setInitParameter("spring.profiles.default", "web");

		// Make the request context (session etc.) available to filters
		servletContext.addListener(RequestContextListener.class);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
				new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);

		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		FilterRegistration.Dynamic characterEncoding = servletContext.addFilter("characterEncoding",
				characterEncodingFilter);
		characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

		// FilterRegistration.Dynamic sitemesh =
		// servletContext.addFilter("sitemesh", new SiteMeshFilter());
		// sitemesh.addMappingForUrlPatterns(dispatcherTypes, true, "*.jsp");
		// sitemesh.setInitParameter("contextConfigLocation",
		// "classpath:/WEB-INF/sitemesh3.xml");
		// sitemesh.setInitParameter("decorators-file",
		// "classpath:/WEB-INF/sitemesh3.xml");
		servletContext.addListener(new ContextLoaderListener(rootContext));

	}

}
