/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.Client;
import org.geotoolkit.osmtms.model.OSMTMSPyramidSet;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.PyramidSet;
import org.geotoolkit.util.NamesExt;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Represent a Tile Map Server instance.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OSMTileMapClient extends AbstractCoverageClient implements Client, Aggregate {

    private final OSMTMSPyramidSet pyramidSet;
    private final OSMTMSCoverageResource resource;

    /**
     * Builds a tile map server with the given server url and version.
     *
     * @param serverURL The server base url. must not be null.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public OSMTileMapClient(final URL serverURL, final int maxZoomLevel) {
        this(serverURL,null,maxZoomLevel, false);
    }

    /**
     * Builds a tile map server with the given server url, security and max zoom level.
     *
     * @param serverURL The server base url. must not be null.
     * @param security ClientSecurity.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public OSMTileMapClient(final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel) {
        this(serverURL,security,maxZoomLevel, false);
    }

    /**
     * Builds a tile map server with the given server url, security, maximum
     * zoom level and a flag for cache.
     *
     * @param serverURL The server base url. must not be null.
     * @param security ClientSecurity.
     * @param maxZoomLevel maximum zoom level supported on server.
     * @param cacheImage
     */
    public OSMTileMapClient(final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel, boolean cacheImage) {
        this(toParameters(serverURL, security, maxZoomLevel, cacheImage));
    }

    public OSMTileMapClient(ParameterValueGroup params){
        super(params);
        final GenericName name = NamesExt.create(serverURL.toString(), "main");
        pyramidSet = new OSMTMSPyramidSet(this,getMaxZoomLevel(),getCacheImage());
        resource = new OSMTMSCoverageResource(this,name);
    }

    private static ParameterValueGroup toParameters(
            final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel, boolean cacheImage){
        final Parameters params = create(OSMTMSClientFactory.PARAMETERS, serverURL, security);
        params.getOrCreate(OSMTMSClientFactory.MAX_ZOOM_LEVEL).setValue(maxZoomLevel);
        params.getOrCreate(OSMTMSClientFactory.IMAGE_CACHE).setValue(cacheImage);
        return params;
    }

    @Override
    public OSMTMSClientFactory getProvider() {
        return (OSMTMSClientFactory)DataStores.getFactoryById(OSMTMSClientFactory.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return Collections.singletonList(resource);
    }

    public boolean getCacheImage(){
        return parameters.getValue(OSMTMSClientFactory.IMAGE_CACHE);
    }

    public PyramidSet getPyramidSet(){
        return pyramidSet;
    }

    /**
     * @return maximum scale level available on this server.
     */
    public int getMaxZoomLevel() {
        return parameters.getValue(OSMTMSClientFactory.MAX_ZOOM_LEVEL);
    }

    /**
     * Returns the request object.
     */
    public GetTileRequest createGetTile() {
        return new DefaultGetTile(this);
    }

    @Override
    public void close() {
    }
}
