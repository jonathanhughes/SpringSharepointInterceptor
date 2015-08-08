package com.spring.sharepoint.interceptor;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class SharepointNamespaceContext implements NamespaceContext {

	@Override
    public Iterator<?> getPrefixes(String arg0) {
        return null;
    }

    @Override
    public String getPrefix(String arg0) {
        return null;
    }

    @Override
    public String getNamespaceURI(String arg0) {
        if("s".equals(arg0)) {
            return "http://www.w3.org/2003/05/soap-envelope";
        }
        else if("a".equals(arg0)) {
            return "http://www.w3.org/2005/08/addressing";
        }
        else if("u".equals(arg0)) {
            return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
        }
        else if("o".equals(arg0)) {
            return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        }
        else if("t".equals(arg0)) {
            return "http://schemas.xmlsoap.org/ws/2005/02/trust";
        }
        else if("wsp".equals(arg0)) {
            return "http://schemas.xmlsoap.org/ws/2004/09/policy";
        }
        else if("wsse".equals(arg0)) {
            return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        }
        else if("wsu".equals(arg0)) {
            return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
        }
        else if("wsa".equals(arg0)) {
        	return "http://www.w3.org/2005/08/addressing";
        }
        else if("wst".equals(arg0)) {
            return "http://schemas.xmlsoap.org/ws/2005/02/trust";
        }
        else if("saml".equals(arg0)) {
        	return "urn:oasis:names:tc:SAML:1.0:assertion";
        }
        else if("psf".equals(arg0)) {
            return "http://schemas.microsoft.com/Passport/SoapServices/SOAPFault";
        }
        else if("ds".equals(arg0)) {
        	return "http://www.w3.org/2000/09/xmldsig#";
        }
        
        return null;
    }

}
