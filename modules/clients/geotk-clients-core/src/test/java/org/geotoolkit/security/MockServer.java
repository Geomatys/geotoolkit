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

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.client.AbstractServer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MockServer extends AbstractServer{

    public MockServer(final ClientSecurity security) throws MalformedURLException {
        super(new URL("http://test.com"),security);
    }
    
    public MockRequest createRequest(){
        return new MockRequest(this);
    }
    
}
