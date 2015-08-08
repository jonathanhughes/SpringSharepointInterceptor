package com.spring.sharepoint.interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SharepointInterceptor implements ClientHttpRequestInterceptor {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private RestTemplate restTemplate = new RestTemplate();
	
	private String authUrl;
	
	private String siteUrl;
	
	private String accessTokenUrl;
	
	private String digestUrl;
	
	private String user;
	
	private String password;
	
	private List<String> cookies;
	
	private String digest;
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		
		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
		
		// If we initially aren't authenticated, authenticate.
		if(cookies == null || digest == null) {

			authenticate();

		}
		
		// Setup header
		setupRequestWrapper(requestWrapper);
    	
		logger.info("Request wrapper header: " + requestWrapper.getHeaders());
		ClientHttpResponse response = execution.execute(requestWrapper, body);
		
		// If unauthenticated, reauthenticate and reexecute initial request.
		if(response.getStatusCode().equals(HttpStatus.UNAUTHORIZED) || response.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
			
			authenticate();
			setupRequestWrapper(requestWrapper);
			
			logger.info("Request wrapper header: " + requestWrapper.getHeaders());
			response = execution.execute(requestWrapper, body);
		
		}
		
        return response;
        
	}
	
	protected void setupRequestWrapper(HttpRequestWrapper requestWrapper) {
		
		for( String cookie : cookies ) {
			logger.info("Adding cookie to request header: " + cookie);
    		requestWrapper.getHeaders().add("Cookie", cookie);
    	}
     
		logger.info("Adding X-RequestDigest to request header: " + digest);
    	requestWrapper.getHeaders().add("X-RequestDigest", digest);
    	
    	logger.info("Setting accept header");
    	requestWrapper.getHeaders().set(HttpHeaders.ACCEPT, "application/json;odata=verbose");
		
	}
	
	protected void authenticate() throws IOException {
		try {
			String securityToken = getSecurityToken();
    	
			logger.info("Security Token: " + securityToken);
		
			if(securityToken != null) {
				cookies = getAccessTokenCookies( securityToken );
    	
				for( String cookie : cookies ) {
					logger.info( "Cookie: " + cookie );
				}
    	
				digest = getRequestDigest( cookies );
				logger.info( "Digest: " + digest );
			}
			else {
				throw new Exception("Failed to authenticate.  Security token is null.");
			}
		}
		catch(Exception e) {
			throw new IOException("Failed to authenticate", e);
		}
	}
	
	protected String getSecurityToken() throws Exception {

		String xml = buildAuthXml(siteUrl, user, password);
		
		System.out.println("XML: " + xml);
    	
    	// post xml 
        String response = restTemplate.postForObject( authUrl, xml, String.class );

        // extract security token
        XPath xpath = XPathFactory.newInstance().newXPath();

        xpath.setNamespaceContext( new SharepointNamespaceContext() );
        
        XPathExpression expr = xpath.compile( "//wst:RequestedSecurityToken/wsse:BinarySecurityToken/text()" );

        ByteArrayInputStream responseStream = new ByteArrayInputStream( response.getBytes() );
        
        String token = (String) expr.evaluate( new InputSource( responseStream ), XPathConstants.STRING );

        responseStream.close();
        
        return token;
        
    }
    
    protected List<String> getAccessTokenCookies(String securityToken ) {
    	
    	HttpEntity<String> entity = new HttpEntity<String>( securityToken );
    	
    	ResponseEntity<String> result = restTemplate.exchange( accessTokenUrl, HttpMethod.POST, entity, String.class );
    	
    	HttpHeaders headers = result.getHeaders();
    	
    	List<String> cookies = headers.get("Set-Cookie");
    	
    	return cookies;
    	
    }
    
    protected String getRequestDigest( List<String> cookies ) {
    	
    	HttpHeaders headers = new HttpHeaders();
    	
    	// add headers
    	for( String cookie : cookies ) {
    		headers.add( "Cookie", cookie );
    	}
    	
    	// create entity with empty body
    	HttpEntity<String> entity = new HttpEntity<String>( "", headers );
    	
    	@SuppressWarnings("rawtypes")
		ResponseEntity<Map> result = restTemplate.exchange( digestUrl, HttpMethod.POST, entity, Map.class );
    	
    	@SuppressWarnings("unchecked")
		Map<String,String> json = result.getBody();
    	
    	String digest = json.get( "FormDigestValue" );
    	
    	return digest;
    	
    }
    
    private String buildAuthXml(String host, String user, String password) throws Exception {
		
		ClassPathResource cpr = new ClassPathResource("/com/spring/sharepoint/interceptor/auth.xml");
		
		Document doc = buildDocument(cpr);
		
		updateDocument(doc, "//o:UsernameToken/o:Username/text()", user);
		updateDocument(doc, "//o:UsernameToken/o:Password/text()", password);
		updateDocument(doc, "//wsp:AppliesTo/a:EndpointReference/a:Address/text()", host);
		
		return convertDocToString(doc);
		
	}
	
	private Document buildDocument(ClassPathResource cpr) throws Exception{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		return builder.parse(cpr.getInputStream());
	}
	
	private void updateDocument(Document doc, String xpathExpr, String value) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		
        xpath.setNamespaceContext(new SharepointNamespaceContext());
        
        XPathExpression expr = xpath.compile(xpathExpr);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
       
        NodeList nodes = (NodeList) result;
        if(nodes.getLength() > 0) {
        	nodes.item(0).setNodeValue(value);
        }
        else {
        	throw new Exception("Failed to find node using xpath query: " + xpathExpr);
        }
	}
	
	private String convertDocToString(Document doc) throws Exception {
		
		 TransformerFactory tf = TransformerFactory.newInstance();
	     Transformer transformer = tf.newTransformer();
	     transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	     StringWriter writer = new StringWriter();
	     transformer.transform(new DOMSource(doc), new StreamResult(writer));
	     
	     return writer.getBuffer().toString().replaceAll("\n|\r", "");
	     
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public String getDigestUrl() {
		return digestUrl;
	}

	public void setDigestUrl(String digestUrl) {
		this.digestUrl = digestUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

}
