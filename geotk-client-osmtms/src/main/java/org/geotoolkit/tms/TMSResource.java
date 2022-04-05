/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TiledResource;
import org.geotoolkit.tms.model.TMSTileMatrixSets;
import org.opengis.util.GenericName;

/**
 * Tile Map Server client.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSResource extends AbstractGridCoverageResource implements TiledResource, StoreResource {

    private final TileMapClient client;
    private final GenericName name;

    TMSResource(TileMapClient client, GenericName name){
        super(null);
        this.client = client;
        this.name = name;
    }

    @Override
    public DataStore getOriginator() {
        return client;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    public TMSTileMatrixSets getPyramidSet() {
        return ((TileMapClient)client).getPyramidSet();
    }

    @Override
    public Collection<TileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return ((TileMapClient)client).getPyramidSet().getTileMatrixSets();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new TileMatrixSetCoverageReader(this).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new TileMatrixSetCoverageReader(this).read(domain, range);
    }

}
