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
package org.geotoolkit.osmtms;

import java.net.URL;

import org.geotoolkit.client.AbstractServer;
import org.geotoolkit.client.map.PyramidSet;
import org.geotoolkit.osmtms.model.OSMTMSPyramidSet;
import org.geotoolkit.security.ClientSecurity;

/**
 * Represent a Tile Map Server instance.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTileMapServer extends AbstractServer{
    
    private final OSMTMSPyramidSet pyramidSet;
    private final int maxZoomLevel;
    
    /**
     * Builds a tile map server with the given server url and version.
     *
     * @param serverURL The server base url. must not be null.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public OSMTileMapServer(final URL serverURL, final int maxZoomLevel) {
        this(serverURL,null,maxZoomLevel);
    }
    
    /**
     * Builds a tile map server with the given server url and version.
     *
     * @param serverURL The server base url. must not be null.
     * @param security ClientSecurity.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public OSMTileMapServer(final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel) {
        super(serverURL,security);
        this.maxZoomLevel = maxZoomLevel;
        pyramidSet = new OSMTMSPyramidSet(maxZoomLevel);
    }

    public PyramidSet getPyramidSet(){
        return pyramidSet;
    }
    
    /**
     * @return maximum scale level available on this server.
     */
    public int getMaxZoomLevel() {
        return maxZoomLevel;
    }

    /**
     * Returns the request object.
     */
    public GetTileRequest createGetTile() {
        return new DefaultGetTile(serverURL.toString(),securityManager);
    }

}
