package com.spring.sharepoint.interceptor.test.service;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.spring.sharepoint.interceptor.test.dataprovider.SharepointInterceptorDataProvider;

public class SharepointInterceptorTest extends SharepointInterceptorDataProvider {

	@BeforeMethod( alwaysRun = true )
	public void beforeMethod( Method m )
	{
		System.out.println( "****************** Starting test: " + m.getName() + "*************************" );
	}

	@AfterMethod( alwaysRun = true )
	public void afterMethod( Method m )
	{
		System.out.println( "****************** Finished test: " + m.getName() + "*************************" );
		System.out.println();
	}
	
	//--------------- Test Cases ---------------------------

	@Test( dataProvider = "SharepointInterceptor", groups = { "SharepointInterceptor" } )
	public void testSharepointInterceptor(String url) {
	
		try {
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> entity = new HttpEntity<String>( "", headers );
			
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> result = restTemplate.exchange( url, HttpMethod.POST, entity, Map.class );
			
			System.out.println(result.getBody());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} 
		
	}
	
}
