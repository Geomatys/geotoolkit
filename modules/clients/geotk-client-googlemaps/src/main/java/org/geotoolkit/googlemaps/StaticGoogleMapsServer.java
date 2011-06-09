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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.client.Server;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.logging.Logging;

/**
 * Client for google static maps.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StaticGoogleMapsServer implements Server{

    private static final Logger LOGGER = Logging.getLogger(StaticGoogleMapsServer.class);
    
    public static final URL DEFAULT_GOOGLE_STATIC_MAPS;
    
    static {
        try {
            DEFAULT_GOOGLE_STATIC_MAPS = new URL("http://maps.google.com/maps/api/staticmap");
        } catch (MalformedURLException ex) {
            //will not happen
            throw new RuntimeException(ex.getLocalizedMessage(),ex);
        }
    }
    
    private final URL serverURL;
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
        ArgumentChecks.ensureNonNull("server url", serverURL);
        this.serverURL = serverURL;
        this.key = key;
    }
    
    @Override
    public URI getURI() {
        try {
            return serverURL.toURI();
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    @Override
    public URL getURL() {
        return serverURL;
    }
    
    public String getKey(){
        return key;
    }
    
    /**
     * Returns the map request object.
     */
    public GetMapRequest createGetMap() {
        return new DefaultGetMap(serverURL.toString(),getKey());
    }
    
}
