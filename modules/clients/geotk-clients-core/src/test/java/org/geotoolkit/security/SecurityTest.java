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
package org.geotoolkit.security;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic authentication test.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class SecurityTest {
    
    public static AtomicInteger INC = new AtomicInteger();
    
    @Test
    public void testHeader() throws MalformedURLException{
        
        //check it is called
        final ClientSecurity security = new BasicAuthenticationSecurity("me", "mypass"){

            @Override
            public URLConnection secure(URLConnection cnx) {
                INC.incrementAndGet();
                return super.secure(cnx);
            }

        };

        final MockServer server = new MockServer(security);        
        final MockRequest request = server.createRequest();
            
        try {
            request.getResponseStream();
        } catch (IOException ex) {      
            ex.printStackTrace();
            //url is fake, this error will happen but it's not what we are testing
        }
        
        assertEquals(1, INC.get());
        
        
    }
    
}
