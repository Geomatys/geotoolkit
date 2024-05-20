/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage.coverage.mosaic;

import java.awt.Dimension;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.privy.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.apache.sis.coverage.grid.PixelInCell.CELL_CENTER;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AlignedCoverageResourcesTest {

    @Test
    public void testAligned() throws DataStoreException {
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final GridGeometry grid1 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 2, 0), crs);

        final GridCoverage coverage1 = new GridCoverageBuilder().setDomain(grid1).setValues(new DataBufferDouble(new double[]{1}, 1), new Dimension(1,1)).build();
        final GridCoverage coverage2 = new GridCoverageBuilder().setDomain(grid2).setValues(new DataBufferDouble(new double[]{2}, 1), new Dimension(1,1)).build();

        final GridCoverageResource agg = AlignedCoverageResources.create(new InMemoryGridCoverageResource(coverage1), new InMemoryGridCoverageResource(coverage2));

        final GridGeometry grid = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        Assert.assertEquals(grid, agg.getGridGeometry());

        final GridCoverage coverage = agg.read(grid);
        final Raster raster = coverage.render(null).getData();
        Assert.assertEquals(3, raster.getWidth());
        Assert.assertEquals(1, raster.getHeight());
        Assert.assertEquals(1, raster.getNumBands());
        Assert.assertEquals(1.0, raster.getSampleDouble(0, 0, 0), 0.0);
        Assert.assertEquals(Double.NaN, raster.getSampleDouble(1, 0, 0), 0.0);
        Assert.assertEquals(2.0, raster.getSampleDouble(2, 0, 0), 0.0);
    }

}
