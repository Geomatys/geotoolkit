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
 * This class offer a set of methods which should be called by client modules
 * to handle authentication or encryption processes required for security.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ClientSecurity {
    
    /**
     * If an securisation process require addition parameters to be encoded
     * in the url, then the manager should returned a modified url. Otherwise
     * the original url is returned.
     * 
     * @param url : original url
     * @return modified url with additional parameters.
     */
    URL secure(URL url);
    
    /**
     * If an securisation process require addition parameters to be encoded
     * in the header map, then the manager can add then here.
     * 
     * @param cnx urlConnection
     * @return modified URL connection
     */
    URLConnection secure(URLConnection cnx);
    
    /**
     * If the securisation imply some encryption. The manager
     * should encapsulate the given stream.
     * 
     * @param stream
     * @return OutputStream
     */
    OutputStream encrypt(OutputStream stream);
    
    /**
     * If the securisation imply some encryption. The manager
     * should encapsulate the given stream.
     * 
     * @param stream
     * @return 
     */
    InputStream decrypt(InputStream stream);
    
}
