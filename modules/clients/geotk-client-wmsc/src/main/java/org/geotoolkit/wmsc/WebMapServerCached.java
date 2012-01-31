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
import java.util.Set;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.wms.GetMapRequest;
import org.geotoolkit.wms.WebMapServer;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.feature.type.Name;

/**
 * WMS-C is a osgeo profile for WMS 1.1.1.
 * 
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WebMapServerCached extends WebMapServer implements CoverageStore{
    
    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     */
    public WebMapServerCached(final URL serverURL) {
        super(serverURL,"1.1.1");
    }
    
    /**
     * Builds a web map server with the given server url and version.
     *
     * @param serverURL The server base url.
     */
    public WebMapServerCached(final URL serverURL, final ClientSecurity security) {
        super(serverURL, security, WMSVersion.v111, null);
    }
    
    @Override
    public GetMapRequest createGetMap() {
        final GetMapRequest request = super.createGetMap();
        request.dimensions().put("TILED", "true");
        return request;
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }
    
}
