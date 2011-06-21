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
package org.geotoolkit.wmsc;

import java.net.URL;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.xml.WMSVersion;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WebMapServerCached extends WebMapServer{
    
    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param version A string representation of the service version.
     * @throws IllegalArgumentException if the version specified is not applyable.
     */
    public WebMapServerCached(final URL serverURL, final String version) {
        super(serverURL,version);
        init();
    }
    
    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     * @param version The service version.
     */
    public WebMapServerCached(final URL serverURL, final ClientSecurity security, final WMSVersion version) {
        super(serverURL, security, version, null);
        init();
    }
    
    private void init(){
    }

    @Override
    public GetMapRequest createGetMap() {
        final GetMapRequest request = super.createGetMap();
        request.dimensions().put("TILED", "true");
        return request;
    }
    
}
