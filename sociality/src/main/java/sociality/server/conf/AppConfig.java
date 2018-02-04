package sociality.server.conf;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import sociality.server.oauth.AuthServerOAuth2Config;
import sociality.server.oauth.ResourceServerOAuth2Config;

@Configuration
@Import({ ResourceServerOAuth2Config.class, AuthServerOAuth2Config.class, SocialConfig.class,
		MethodSecurityConfig.class })
@ComponentScan({ "sociality.server", "sociality.server.filters", "sociality.server.controllers",
		"sociality.server.audit" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
@PropertySource("classpath:application.properties")
public class AppConfig {

	public interface Constants {
		public static final String TWITTER_USERNAME_KEY = "twitter_username";
		public static final String FACEBOOK_USERNAME_KEY = "facebook_username";
		public static final String DEFAULT_PAGE_SIZE_STR = "12";

	}

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(env.getProperty("spring.datasource.driver"));
		hikariConfig.setJdbcUrl(env.getProperty("spring.datasource.url"));
		hikariConfig.setUsername(env.getProperty("spring.datasource.username"));
		hikariConfig.setPassword(env.getProperty("spring.datasource.password"));
		hikariConfig.setPoolName("springHikariCP");
		return new HikariDataSource(hikariConfig);
	}

	@Bean
	public SessionFactory sessionFactory() throws HibernateException, SQLException, PropertyVetoException {
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource());
		sessionBuilder.scanPackages("sociality.server.model", "sociality.server.facebook", "sociality.server.twitter");
//		org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
//		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
//		configuration.setProperty("hibernate.hbm2ddl.auto", "create");
//		configuration.setProperty("hibernate.connection.url", env.getProperty("spring.datasource.url"));
//		configuration.setProperty("hibernate.connection.username", env.getProperty("spring.datasource.username"));
//		configuration.setProperty("hibernate.connection.password", env.getProperty("spring.datasource.password"));
		return sessionBuilder.buildSessionFactory();
//				new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build());
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws PropertyVetoException, SQLException {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory());
		return txManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public ObjectMapper mapper() {
		return Jackson2ObjectMapperBuilder.json().featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.build();
	}

}
