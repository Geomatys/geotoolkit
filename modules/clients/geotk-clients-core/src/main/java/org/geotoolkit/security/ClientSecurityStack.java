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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Wrap several ClientSecurity objects.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ClientSecurityStack implements ClientSecurity{

    private final ClientSecurity[] securities;

    private ClientSecurityStack(ClientSecurity ... securities) {
        this.securities = securities;
    }
    
    @Override
    public URL secure(URL url) {
        for(ClientSecurity security : securities){
            url = security.secure(url);
        }
        return url;
    }

    @Override
    public URLConnection secure(URLConnection cnx) {
        for(ClientSecurity security : securities){
            cnx = security.secure(cnx);
        }
        return cnx;
    }

    @Override
    public OutputStream encrypt(OutputStream stream) {
        for(ClientSecurity security : securities){
            stream = security.encrypt(stream);
        }
        return stream;
    }

    @Override
    public InputStream decrypt(InputStream stream) {
        //decrypt in reverse order
        for(int i=securities.length-1; i>=0;i--){
            final ClientSecurity security = securities[i];
            stream = security.decrypt(stream);
        }
        return stream;
    }
    
    /**
     * 
     * @param securities : ClientSecurity objects to wrap together
     * @return 
     */
    public static ClientSecurity wrap(ClientSecurity ... securities){
        return new ClientSecurityStack(securities);
    }
    
}
