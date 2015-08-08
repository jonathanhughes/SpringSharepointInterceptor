This is a simple spring interceptor that can be added to a rest template for automatically handling authentication with sharepoint when accessing their rest API.

To use:
Build the project with gradle.  Add the jar to your build path and then define spring beans like so:

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