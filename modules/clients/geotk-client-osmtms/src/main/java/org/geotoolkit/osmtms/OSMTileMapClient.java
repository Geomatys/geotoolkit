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

import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.ClientFinder;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.osmtms.model.OSMTMSPyramidSet;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
import org.geotoolkit.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Represent a Tile Map Server instance.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTileMapClient extends AbstractCoverageClient {

    private final OSMTMSPyramidSet pyramidSet;
    private final DataNode rootNode = new DefaultDataNode();

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
        final Name name = new DefaultName(serverURL.toString(),"main");
        pyramidSet = new OSMTMSPyramidSet(this,getMaxZoomLevel(),getCacheImage());
        final OSMTMSCoverageReference ref = new OSMTMSCoverageReference(this,name);
        rootNode.getChildren().add(ref);
    }

    private static ParameterValueGroup toParameters(
            final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel, boolean cacheImage){
        final ParameterValueGroup params = create(OSMTMSClientFactory.PARAMETERS, serverURL, security);
        Parameters.getOrCreate(OSMTMSClientFactory.MAX_ZOOM_LEVEL, params).setValue(maxZoomLevel);
        Parameters.getOrCreate(OSMTMSClientFactory.IMAGE_CACHE, params).setValue(cacheImage);
        return params;
    }

    @Override
    public OSMTMSClientFactory getFactory() {
        return (OSMTMSClientFactory)ClientFinder.getFactoryById(OSMTMSClientFactory.NAME);
    }

    @Override
    public DataNode getRootNode() {
        return rootNode;
    }

    public boolean getCacheImage(){
        return (Boolean)Parameters.getOrCreate(OSMTMSClientFactory.IMAGE_CACHE, parameters).getValue();
    }

    public PyramidSet getPyramidSet(){
        return pyramidSet;
    }

    /**
     * @return maximum scale level available on this server.
     */
    public int getMaxZoomLevel() {
        return Parameters.value(OSMTMSClientFactory.MAX_ZOOM_LEVEL, parameters);
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

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public void delete(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

	@Override
	public CoverageType getType() {
		return CoverageType.PYRAMID;
	}
}
