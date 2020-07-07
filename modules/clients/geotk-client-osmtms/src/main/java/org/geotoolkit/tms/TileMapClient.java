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
package org.geotoolkit.tms;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.Client;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.tms.model.TMSTileMatrixSets;
import org.geotoolkit.util.NamesExt;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Represent a Tile Map Server instance.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TileMapClient extends AbstractCoverageClient implements Client, Aggregate {

    private final TMSTileMatrixSets pyramidSet;
    private final TMSResource resource;

    /**
     * Builds a tile map server with the given server url and version.
     *
     * @param serverURL The server base url. must not be null.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public TileMapClient(final URL serverURL, final int maxZoomLevel) {
        this(serverURL,null,maxZoomLevel, false);
    }

    /**
     * Builds a tile map server with the given server url, security and max zoom level.
     *
     * @param serverURL The server base url. must not be null.
     * @param security ClientSecurity.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public TileMapClient(final URL serverURL, final ClientSecurity security,
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
     */
    public TileMapClient(final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel, boolean cacheImage) {
        this(toParameters(serverURL, security, maxZoomLevel, cacheImage));
    }

    public TileMapClient(ParameterValueGroup params){
        super(params);
        final GenericName name = NamesExt.create(serverURL.toString(), "main");
        pyramidSet = new TMSTileMatrixSets(this,getMaxZoomLevel(),getCacheImage());
        resource = new TMSResource(this,name);
    }

    private static ParameterValueGroup toParameters(
            final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel, boolean cacheImage){
        final Parameters params = create(TMSProvider.PARAMETERS, serverURL, security);
        params.getOrCreate(TMSProvider.MAX_ZOOM_LEVEL).setValue(maxZoomLevel);
        params.getOrCreate(TMSProvider.IMAGE_CACHE).setValue(cacheImage);
        return params;
    }

    @Override
    public TMSProvider getProvider() {
        return (TMSProvider) DataStores.getProviderById(TMSProvider.NAME);
    }

    @Override
    public Collection<org.apache.sis.storage.Resource> components() throws DataStoreException {
        return Collections.singletonList(resource);
    }

    public boolean getCacheImage(){
        return parameters.getValue(TMSProvider.IMAGE_CACHE);
    }

    public TMSTileMatrixSets getPyramidSet(){
        return pyramidSet;
    }

    /**
     * @return maximum scale level available on this server.
     */
    public int getMaxZoomLevel() {
        return parameters.getValue(TMSProvider.MAX_ZOOM_LEVEL);
    }

    /**
     * Returns the request object.
     */
    public GetTileRequest createGetTile() {
        final DefaultGetTile getTile = new DefaultGetTile(this);
        getTile.setPattern(parameters.getValue(TMSProvider.PATTERN));
        return getTile;
    }

    @Override
    public void close() {
    }
}
