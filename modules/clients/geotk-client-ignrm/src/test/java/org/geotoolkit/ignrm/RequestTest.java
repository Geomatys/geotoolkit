/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.ignrm;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RequestTest {
   
    @Test
    public void testGetTokenURL() throws MalformedURLException{
        
        final IGNRMServer server = new IGNRMServer(new URL("http://test.com"));
        
        final GetTokenRequest request =server.createGetToken();
        request.setKey("AAAAABBBBB");
        
        final String url = request.getURL().toString();
        assertTrue(url.startsWith("http://test.com/getToken?"));
        assertTrue(url.contains("key=AAAAABBBBB"));
        assertTrue(url.contains("output=xml"));
    }
    
    @Test
    public void testGetConfigURL() throws MalformedURLException{
        
        final IGNRMServer server = new IGNRMServer(new URL("http://test.com"));
        
        final GetConfigRequest request = server.createGetConfig();
        request.setKey("AAAAABBBBB");
        
        final String url = request.getURL().toString();
        assertTrue(url.startsWith("http://test.com/getConfig?"));
        assertTrue(url.contains("key=AAAAABBBBB"));
        assertTrue(url.contains("output=xml"));
    }
    
    @Test
    public void testgReleaseTokenURL() throws MalformedURLException{
        
        final IGNRMServer server = new IGNRMServer(new URL("http://test.com"));
        
        final ReleaseTokenRequest request = server.createReleaseToken();
        request.setToken(new Token(server, "aaa", "gppkey", "123456"));
        
        final String url = request.getURL().toString();
        assertTrue(url.startsWith("http://test.com/releaseToken?"));
        assertTrue(url.contains("gppkey=123456"));
    }
    
}
