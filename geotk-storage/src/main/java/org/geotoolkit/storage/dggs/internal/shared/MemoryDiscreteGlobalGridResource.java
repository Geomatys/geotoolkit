/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.dggs.internal.shared;

import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.opengis.feature.FeatureType;

/**
 * Decorate a DGGS coverage as a DGGS resource.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MemoryDiscreteGlobalGridResource extends AbstractResource implements DiscreteGlobalGridResource{

    private final DiscreteGlobalGridCoverage coverage;

    public MemoryDiscreteGlobalGridResource(DiscreteGlobalGridCoverage coverage) {
        super(null);
        this.coverage = coverage;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return coverage.getSampleDimensions();
    }

    @Override
    public FeatureType getSampleType() throws DataStoreException {
        return coverage.getSampleType();
    }

    @Override
    public DiscreteGlobalGridGeometry getGridGeometry() {
        return coverage.getGeometry();
    }

    @Override
    public NumberRange<Integer> getAvailableDepths() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDefaultDepth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxRelativeDepth() {
        return 0;
    }

    @Override
    public DiscreteGlobalGridCoverage read(CodedGeometry geometry, int... range) throws DataStoreException {
        return coverage;
    }

}
