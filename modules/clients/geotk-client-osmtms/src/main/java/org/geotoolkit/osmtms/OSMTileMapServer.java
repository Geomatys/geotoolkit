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
import java.util.Collections;
import java.util.Set;

import org.geotoolkit.client.AbstractCoverageServer;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.coverage.PyramidSet;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.osmtms.model.OSMTMSPyramidSet;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Represent a Tile Map Server instance.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMTileMapServer extends AbstractCoverageServer implements CoverageStore{

    private final OSMTMSPyramidSet pyramidSet;
    private final Name name;

    /**
     * Builds a tile map server with the given server url and version.
     *
     * @param serverURL The server base url. must not be null.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public OSMTileMapServer(final URL serverURL, final int maxZoomLevel) {
        this(serverURL,null,maxZoomLevel, false);
    }

    /**
     * Builds a tile map server with the given server url, security and max zoom level.
     *
     * @param serverURL The server base url. must not be null.
     * @param security ClientSecurity.
     * @param maxZoomLevel maximum zoom level supported on server.
     */
    public OSMTileMapServer(final URL serverURL, final ClientSecurity security,
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
    public OSMTileMapServer(final URL serverURL, final ClientSecurity security,
            final int maxZoomLevel, boolean cacheImage) {
        super(create(OSMTMSServerFactory.PARAMETERS, serverURL, security));
        Parameters.getOrCreate(OSMTMSServerFactory.MAX_ZOOM_LEVEL, parameters).setValue(maxZoomLevel);
        this.name = new DefaultName(serverURL.toString(),"main");
        pyramidSet = new OSMTMSPyramidSet(this,maxZoomLevel,cacheImage);
    }

    public OSMTileMapServer(ParameterValueGroup params){
        super(params);
        this.name = new DefaultName(serverURL.toString(),"main");
        pyramidSet = new OSMTMSPyramidSet(this,getMaxZoomLevel(),getCacheImage());
    }

    @Override
    public OSMTMSServerFactory getFactory() {
        return (OSMTMSServerFactory)ServerFinder.getFactoryById(OSMTMSServerFactory.NAME);
    }

    public boolean getCacheImage(){
        return (Boolean)Parameters.getOrCreate(OSMTMSServerFactory.IMAGE_CACHE, parameters).getValue();
    }

    public PyramidSet getPyramidSet(){
        return pyramidSet;
    }

    /**
     * @return maximum scale level available on this server.
     */
    public int getMaxZoomLevel() {
        return Parameters.value(OSMTMSServerFactory.MAX_ZOOM_LEVEL, parameters);
    }

    /**
     * Returns the request object.
     */
    public GetTileRequest createGetTile() {
        return new DefaultGetTile(this);
    }

    @Override
    public Set<Name> getNames() {
        return Collections.singleton(name);
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        if(DefaultName.match(this.name, name)){
            return new OSMTMSCoverageReference(this,name);
        }
        throw new DataStoreException("No coverage for name : " + name);
    }

    @Override
    public void dispose() {
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
