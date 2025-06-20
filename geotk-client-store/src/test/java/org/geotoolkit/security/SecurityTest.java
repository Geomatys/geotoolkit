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
import java.net.http.HttpRequest;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Basic authentication test.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SecurityTest {

    public static AtomicInteger INC = new AtomicInteger();

    @Test
    @org.junit.Ignore("This test depends on network.")
    public void testHeader() throws MalformedURLException{

        //check it is called
        final ClientSecurity security = new BasicAuthenticationSecurity("me", "mypass"){

            @Override
            public URLConnection secure(URLConnection cnx) {
                INC.incrementAndGet();
                return super.secure(cnx);
            }

            @Override
            public void secure(HttpRequest.Builder request) {
                INC.incrementAndGet();
            }

        };

        final MockClient server = new MockClient(security);
        final MockRequest request = server.createRequest();

        try {
            request.getResponseStream();
        } catch (IOException ex) {
            ex.printStackTrace();
            //url is fake, this error will happen but it's not what we are testing
        }

        //test.com has 1 inderection level
        assertEquals(2, INC.get());
    }
}
