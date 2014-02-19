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
 * A security manager doing nothing, can be used as a base class for other
 * managers.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultClientSecurity implements ClientSecurity {

    /**
     * A security manager doing nothing.
     */
    public static final ClientSecurity NO_SECURITY = new DefaultClientSecurity();
    
    @Override
    public URL secure(URL url) {
        return url;
    }

    @Override
    public URLConnection secure(URLConnection cnx) {
        return cnx;
    }

    @Override
    public OutputStream encrypt(OutputStream stream) {
        return stream;
    }

    @Override
    public InputStream decrypt(InputStream stream) {
        return stream;
    }
    
}
