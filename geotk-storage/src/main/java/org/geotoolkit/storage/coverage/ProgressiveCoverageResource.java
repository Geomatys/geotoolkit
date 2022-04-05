/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.multires.GeneralProgressiveResource;
import org.geotoolkit.storage.multires.TileGenerator;
import org.geotoolkit.storage.multires.TiledResource;
import org.geotoolkit.storage.multires.WritableTiledResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ProgressiveCoverageResource<T extends GridCoverageResource & WritableTiledResource>
        extends GeneralProgressiveResource implements GridCoverageResource, TiledResource, IProgressiveCoverageResource {

    private final T base;

    public ProgressiveCoverageResource(T resource, TileGenerator generator) {
        super(resource, generator);
        this.base = resource;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new TileMatrixSetCoverageReader(this).getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return base.getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        TileMatrixSetCoverageReader reader = new TileMatrixSetCoverageReader(this);
        return reader.read(domain, range);
    }

}
