/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MosaicedCoverageResourceTest {

    /**
     * Test mosaic add a NaN value to fill spaces.
     */
    @Test
    public void testNoDataAdded() throws IOException, DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+
        | 1 |
        +---+

        Coverage 2
                +---+
                | 2 |
                +---+

        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(1, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(1, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 2, 0), crs);
        final GridGeometry grid = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_SHORT);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_SHORT);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write2 = WritablePixelIterator.create( (WritableRenderedImage) coverage2.render(null));

        write1.moveTo(0, 0); write1.setSample(0, 1);
        write2.moveTo(0, 0); write2.setSample(0, 2);


        /*
        We expect a final coverage with values :
        +---+---+---+
        | 1 |NaN| 2 |
        +---+---+---+
        starting at index 30 in data array, for an image of size 9x7.
        The 9x7 size is becausze MosaicedCoverageResource.read(GridGeometry domain, int... range)
        adds a 3x3 margin on each side.
        */
        final GridCoverageResource aggregate =  MosaicedCoverageResource.create(resource1, resource2).get(0);

        final GridCoverage coverage = aggregate.read(grid).forConvertedValues(true);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(3, 3); Assert.assertEquals(1, reader.getSampleDouble(0), 0.0);
        reader.moveTo(4, 3); Assert.assertEquals(Double.NaN, reader.getSampleDouble(0), 0.0);
        reader.moveTo(5, 3); Assert.assertEquals(2, reader.getSampleDouble(0), 0.0);
    }
}
