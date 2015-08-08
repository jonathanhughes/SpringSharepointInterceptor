package com.spring.sharepoint.interceptor.test.dataprovider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.spring.sharepoint.interceptor.test.config.ApplicationConfig;
import com.spring.sharepoint.interceptor.test.config.ServiceConfig;

@ContextConfiguration(
	classes = {
		ApplicationConfig.class,
		ServiceConfig.class
	}
)
public class SharepointInterceptorDataProvider extends AbstractTestNGSpringContextTests
{
	
	@Autowired
	protected RestTemplate restTemplate;
	
	// this is used to delete all test data in @AfterClass and @BeforeClass
	@BeforeClass
	public void beforeClass() throws Exception
	{
		
	}

	@AfterClass
	public void afterClass() throws Exception
	{
		
	}
	
	@DataProvider( name = "SharepointInterceptor" )
	public Object[][] getSharepointInterceptorData() throws Exception {
		
		return new Object[][] {new Object[] {"https://fotlinc.sharepoint.com/_api/contextinfo"}};
		
	}
	

	// ---------------------- Utility methods ---------------------------

	protected <T> Object[][] collectionToObjectArray( Collection<T> collection )
	{
		Object[][] objArray = null;

		if( collection != null )
		{
			objArray = new Object[collection.size()][1];

			int i = 0;

			for( T obj : collection )
			{
				objArray[i][0] = obj;

				i++;

			}

		}

		return objArray;

	}

	protected <T> Object[][] collectionToRandomListArray( Collection<T> collection )
	{
		Random random = new Random();

		List<List<T>> embeddedList = new ArrayList<>();

		if( collection != null )
		{
			List<T> randomList = new ArrayList<>();
			for( T obj : collection )
			{
				// Make sure we have at least 1 object in the random list
				if( CollectionUtils.isNotEmpty( randomList ) && random.nextInt( 3 ) % 3 == 0 )
				{
					embeddedList.add( randomList );
					randomList = new ArrayList<>();
				}
				randomList.add( obj );
			}

		}

		return collectionToObjectArray( embeddedList );

	}

}
