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

import java.util.Collection;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.data.multires.GeneralProgressiveResource;
import org.geotoolkit.data.multires.MultiResolutionResource;
import org.geotoolkit.data.multires.Pyramid;
import org.geotoolkit.data.multires.TileGenerator;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ProgressiveCoverageResource<T extends GridCoverageResource & MultiResolutionResource>
        extends GeneralProgressiveResource implements GridCoverageResource, MultiResolutionResource {

    private T base = null;

    public ProgressiveCoverageResource(T resource, TileGenerator generator) throws DataStoreException {
        super(resource, generator);
        this.base = resource;
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return (Collection<Pyramid>) super.getModels();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return base.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return base.getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        PyramidReader reader = new PyramidReader(this);
        return reader.read(domain, range);
    }

}
