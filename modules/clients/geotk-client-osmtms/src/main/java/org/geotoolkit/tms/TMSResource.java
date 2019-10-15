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
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.multires.MultiResolutionModel;
import org.geotoolkit.data.multires.MultiResolutionResource;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.storage.coverage.PyramidalModelReader2;
import org.geotoolkit.tms.model.TMSPyramidSet;
import org.opengis.util.GenericName;

/**
 * Tile Map Server client.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TMSResource extends AbstractGridResource implements MultiResolutionResource, StoreResource {

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

    public TMSPyramidSet getPyramidSet() {
        return ((TileMapClient)client).getPyramidSet();
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return ((TileMapClient)client).getPyramidSet().getPyramids();
    }

    @Override
    public MultiResolutionModel createModel(MultiResolutionModel template) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void removeModel(String identifier) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new PyramidalModelReader2(this).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return new PyramidalModelReader2(this).read(domain, range);
    }

}
