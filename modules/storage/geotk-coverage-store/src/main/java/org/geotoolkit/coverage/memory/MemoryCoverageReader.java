/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.coverage.memory;

import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.util.iso.Names;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.util.GenericName;

/**
 * Coverage reader wrapping a coverage.
 *
 * @author Johann Sorel (Geomatys)
 */
public class MemoryCoverageReader extends GridCoverageReader {
    private final GridCoverage2D coverage;

    public MemoryCoverageReader(final GridCoverage2D coverage) {
        this.coverage = coverage;
    }

    @Override
    public GridGeometry getGridGeometry() throws CoverageStoreException, CancellationException {
        return (GridGeometry) coverage.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws CoverageStoreException, CancellationException {
        return coverage.getSampleDimensions();
    }

    @Override
    public GridCoverage read(final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
        return coverage;
    }

    @Override
    public GenericName getCoverageName() throws CoverageStoreException, CancellationException {
        return Names.createLocalName(null, null, coverage.getName() == null ? "" : coverage.getName());
    }

}
