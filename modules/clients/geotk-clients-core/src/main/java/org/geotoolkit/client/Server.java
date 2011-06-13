/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.client;

import java.net.URI;
import java.net.URL;
import org.geotoolkit.security.ClientSecurity;


/**
 * Default interface for all server-side classes.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Server {
    
    /**
     * @return the server url as an {@link URI}, or {@code null} il the uri syntax
     * is not respected.
     */
    URI getURI();
    
    /**
     * @return the server url as an {@link URL}.
     */
    URL getURL();
    
    /**
     * @return ClientSecurity used by this server. never null.
     */
    ClientSecurity getClientSecurity();
    
}
