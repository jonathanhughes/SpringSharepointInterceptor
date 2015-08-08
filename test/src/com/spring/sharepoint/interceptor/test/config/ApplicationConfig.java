package com.spring.sharepoint.interceptor.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
//@PropertySource( "classpath:application.properties" )
public class ApplicationConfig {

	/**
	 * http://www.javacodegeeks.com/2013/07/spring-bean-and-propertyplaceholderconfigurer.html
	 *
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
