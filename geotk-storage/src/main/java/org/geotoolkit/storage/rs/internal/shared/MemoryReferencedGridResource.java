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
package org.geotoolkit.storage.rs.internal.shared;

import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.rs.ReferencedGridCoverage;
import org.geotoolkit.storage.rs.ReferencedGridGeometry;
import org.geotoolkit.storage.rs.ReferencedGridResource;
import org.opengis.feature.FeatureType;

/**
 * Decorate a referenced coverage as a referenced resource.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MemoryReferencedGridResource extends AbstractResource implements ReferencedGridResource {

    private final ReferencedGridCoverage coverage;

    public MemoryReferencedGridResource(ReferencedGridCoverage coverage) {
        super(null);
        this.coverage = coverage;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return coverage.getSampleDimensions();
    }

    @Override
    public FeatureType getSampleType() {
        return coverage.getSampleType();
    }

    @Override
    public ReferencedGridGeometry getGridGeometry() {
        return coverage.getGeometry();
    }

    @Override
    public ReferencedGridCoverage read(ReferencedGridGeometry geometry, int... range) throws DataStoreException {
        return coverage;
    }

}
