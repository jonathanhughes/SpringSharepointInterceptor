package com.spring.sharepoint.interceptor.test.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.spring.sharepoint.interceptor.SharepointInterceptor;

@Configuration
public class ServiceConfig {

	@Bean 
	public SharepointInterceptor sharepointInterceptor() {
		SharepointInterceptor sharepointInterceptor = new SharepointInterceptor();
		
		sharepointInterceptor.setAuthUrl("https://login.microsoftonline.com/extSTS.srf");
		sharepointInterceptor.setSiteUrl("https://yourcompany.sharepoint.com/");
		sharepointInterceptor.setAccessTokenUrl("https://yourcompany.sharepoint.com/_forms/default.aspx?wa=wsignin1.0");
		sharepointInterceptor.setDigestUrl("https://yourcompany.sharepoint.com/_api/contextinfo");
		sharepointInterceptor.setUser("youraccount@yourcompany.onmicrosoft.com");
		sharepointInterceptor.setPassword("yourpassword");
		
		return sharepointInterceptor;
		
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(sharepointInterceptor());
		
		restTemplate.setInterceptors(interceptors);
		
		return restTemplate;
	}

}
