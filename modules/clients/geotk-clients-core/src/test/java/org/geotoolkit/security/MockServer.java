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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.ServerFactory;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MockServer extends AbstractServer{

    private static final ParameterValueGroup PARAM;
    static {
        final ParameterDescriptorGroup desc = new DefaultParameterDescriptorGroup("mock", 
                AbstractServerFactory.URL,AbstractServerFactory.SECURITY);
        PARAM = desc.createValue();
        try {
            PARAM.parameter("url").setValue(new URL("http://test.com"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(MockServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MockServer(final ClientSecurity security) throws MalformedURLException {
        super(appendSecurity(security));
    }
        
    public MockRequest createRequest(){
        return new MockRequest(this);
    }

    @Override
    public ServerFactory getFactory() {
        return null;
    }
    
    private static ParameterValueGroup appendSecurity(final ClientSecurity security){
        ParameterValueGroup param = PARAM.clone();
        param.parameter(AbstractServerFactory.SECURITY.getName().getCode()).setValue(security);
        return param;
    }
    
}
