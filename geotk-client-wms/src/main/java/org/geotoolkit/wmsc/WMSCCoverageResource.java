/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2014, Geomatys
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.CapabilitiesException;
import org.geotoolkit.storage.coverage.TileMatrixSetCoverageReader;
import org.geotoolkit.storage.multires.TileMatrixSet;
import org.geotoolkit.storage.multires.TiledResource;
import org.geotoolkit.wmsc.model.WMSCTileMatrixSets;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCCoverageResource extends AbstractGridCoverageResource implements TiledResource, StoreResource {

    private final WebMapClientCached server;
    private final GenericName name;
    private final WMSCTileMatrixSets set;

    public WMSCCoverageResource(final WebMapClientCached server,
            final GenericName name) throws CapabilitiesException{
        super(null);
        this.server = server;
        this.name = name;
        set = new WMSCTileMatrixSets(server, name.tip().toString());
    }

    @Override
    public DataStore getOriginator() {
        return server;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(name);
    }

    public WMSCTileMatrixSets getPyramidSet() {
        return set;
    }

    @Override
    public Collection<TileMatrixSet> getTileMatrixSets() throws DataStoreException {
        return set.getTileMatrixSets();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new TileMatrixSetCoverageReader<>(this).read(domain, range);
    }

}
