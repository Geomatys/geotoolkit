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
package org.geotoolkit.googlemaps;

import java.net.MalformedURLException;
import java.net.URL;

import org.geotoolkit.client.AbstractServer;

/**
 * Client for google static maps.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StaticGoogleMapsServer extends AbstractServer{
    
    public static final URL DEFAULT_GOOGLE_STATIC_MAPS;
    
    static {
        try {
            DEFAULT_GOOGLE_STATIC_MAPS = new URL("http://maps.google.com/maps/api/staticmap");
        } catch (MalformedURLException ex) {
            //will not happen
            throw new RuntimeException(ex.getLocalizedMessage(),ex);
        }
    }
    
    private final String key;
    
    /**
     * Builds a google maps server with the default google server address.
     */
    public StaticGoogleMapsServer() {
        this(DEFAULT_GOOGLE_STATIC_MAPS,null);
    }
    
    /**
     * Builds a google maps server with the given server url.
     *
     * @param serverURL The server base url.
     * @param key, account key.
     */
    public StaticGoogleMapsServer(final URL serverURL, final String key) {
        super(serverURL);
        this.key = key;
    }
        
    public String getKey(){
        return key;
    }
    
    /**
     * Returns the map request object.
     */
    public GetMapRequest createGetMap() {
        return new DefaultGetMap(this,getKey());
    }
    
}
