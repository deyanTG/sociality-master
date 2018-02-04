package sociality.server.conf;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;

import sociality.server.resolvers.JsonArgArgumentResolver;
import sociality.server.resolvers.UserArgumentResolver;

@Configuration
@EnableWebMvc
@Import(AppConfig.class)
@Profile("web")
public class WebConfig extends WebMvcConfigurerAdapter {

	Boolean disableCors = true;

	@Bean
	public HandlerMethodArgumentResolver jsonParamArgumentResolver() {
		return new JsonArgArgumentResolver();
	}

	@Bean
	public UserArgumentResolver userArgumentResolver() {
		return new UserArgumentResolver();
	}

	@Bean
	public MappingJackson2HttpMessageConverter customConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON));
		return converter;
	}

	// @Bean
	// public ViewResolver getViewResolver() {
	// InternalResourceViewResolver resolver = new
	// InternalResourceViewResolver();
	// resolver.setPrefix("WEB-INF/");
	// resolver.setSuffix(".jsp");
	// return resolver;
	// }

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// registry.addMapping("/**").allowedOrigins("http://domain2.com").allowedMethods("POST",
		// "GET", "OPTIONS")
		// .allowedHeaders("*").maxAge(3600);
		//
		registry.addMapping("/**").allowedOrigins(CrossOrigin.DEFAULT_ORIGINS)
				.allowedHeaders(CrossOrigin.DEFAULT_ALLOWED_HEADERS)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").maxAge(3600L);
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		converters.add(new MappingJackson2HttpMessageConverter(builder.build())); 
		converters.add(new StringHttpMessageConverter());
		converters.add(customConverter());
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(jsonParamArgumentResolver());
		argumentResolvers.add(userArgumentResolver());
	}

}
