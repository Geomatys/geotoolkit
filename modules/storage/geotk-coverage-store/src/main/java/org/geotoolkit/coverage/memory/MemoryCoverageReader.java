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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.coverage.grid.GridCoverage;
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
    public GeneralGridGeometry getGridGeometry(final int i) throws CoverageStoreException, CancellationException {
        return (GeneralGridGeometry) coverage.getGridGeometry();
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(final int i) throws CoverageStoreException, CancellationException {
        return Arrays.asList(coverage.getSampleDimensions());
    }

    @Override
    public GridCoverage read(final int i, final GridCoverageReadParam gcrp) throws CoverageStoreException, CancellationException {
        return coverage;
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        return Collections.emptyList();
    }

}
