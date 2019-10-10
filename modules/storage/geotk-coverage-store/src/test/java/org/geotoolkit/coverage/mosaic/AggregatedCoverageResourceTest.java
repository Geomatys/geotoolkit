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
package org.geotoolkit.coverage.mosaic;

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.Arrays;
import java.util.Collection;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.coverage.BufferedGridCoverage;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.memory.MemoryCoverageResource;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;


/**
 * Test AggregatedCoverageResource.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AggregatedCoverageResourceTest {

    /**
     * Test aggregation user order is preserved.
     *
     * @throws DataStoreException
     * @throws TransformException
     */
    @Test
    public void testModeOrder() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final Collection<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | 1 |NaN|NaN|
        +---+---+---+

        Coverage 2
        +---+---+---+
        | 2 | 2 |NaN|
        +---+---+---+

        Coverage 3
        +---+---+---+
        | 3 | 3 | 3 |
        +---+---+---+
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new MemoryCoverageResource(coverage1);
        final GridCoverageResource resource2 = new MemoryCoverageResource(coverage2);
        final GridCoverageResource resource3 = new MemoryCoverageResource(coverage3);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write2 = WritablePixelIterator.create( (WritableRenderedImage) coverage2.render(null));
        final WritablePixelIterator write3 = WritablePixelIterator.create( (WritableRenderedImage) coverage3.render(null));

        write1.moveTo(0, 0); write1.setSample(0, 1);
        write1.moveTo(1, 0); write1.setSample(0, Double.NaN);
        write1.moveTo(2, 0); write1.setSample(0, Double.NaN);
        write2.moveTo(0, 0); write2.setSample(0, 2);
        write2.moveTo(1, 0); write2.setSample(0, 2);
        write2.moveTo(2, 0); write2.setSample(0, Double.NaN);
        write3.moveTo(0, 0); write3.setSample(0, 3);
        write3.moveTo(1, 0); write3.setSample(0, 3);
        write3.moveTo(2, 0); write3.setSample(0, 3);


        /*
        We expect a final coverage with values [1,2,3] on a single row
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+
        */
        final GridCoverageResource aggregate =  AggregatedCoverageResource.create(
                null, AggregatedCoverageResource.Mode.ORDER,
                resource1, resource2, resource3);
        ((AggregatedCoverageResource)aggregate).setInterpolation(InterpolationCase.NEIGHBOR);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); Assert.assertEquals(2, reader.getSample(0));
        reader.moveTo(2, 0); Assert.assertEquals(3, reader.getSample(0));

    }

    /**
     * Test aggregation user order is preserved.
     *
     * @throws DataStoreException
     * @throws TransformException
     */
    @Test
    public void testModeScale() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder()
                .setName("data")
                .setBackground(null, Double.NaN)
                .build();
        final Collection<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        |NaN| 1 | 1 |
        +---+---+---+

        Coverage 2
        +---+---+---+---+---+---+
        |NaN|NaN| 2 | 2 |NaN|NaN|
        +---+---+---+---+---+---+
        |NaN|NaN| 2 | 2 |NaN|NaN|
        +---+---+---+---+---+---+

        Coverage 3
        +---+---+---+---+---+---+---+---+---+---+---+---+
        |NaN|NaN|NaN|NaN|NaN|NaN| 3 | 3 | 3 | 3 |NaN|NaN|
        +---+---+---+---+---+---+---+---+---+---+---+---+
        |NaN|NaN|NaN|NaN|NaN|NaN| 3 | 3 | 3 | 3 |NaN|NaN|
        +---+---+---+---+---+---+---+---+---+---+---+---+
        |NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|
        +---+---+---+---+---+---+---+---+---+---+---+---+
        |NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|NaN|
        +---+---+---+---+---+---+---+---+---+---+---+---+
        */
        final GridGeometry grid1 = new GridGeometry(new GridExtent( 3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(2.0, 0, 0, 2.0, 1.00, 1.00), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent( 6, 2), PixelInCell.CELL_CENTER, new AffineTransform2D(1.0, 0, 0, 1.0, 0.50, 0.50), crs);
        final GridGeometry grid3 = new GridGeometry(new GridExtent(12, 4), PixelInCell.CELL_CENTER, new AffineTransform2D(0.5, 0, 0, 0.5, 0.25, 0.25), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid3, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new MemoryCoverageResource(coverage1);
        final GridCoverageResource resource2 = new MemoryCoverageResource(coverage2);
        final GridCoverageResource resource3 = new MemoryCoverageResource(coverage3);
        final WritableRenderedImage img1 = (WritableRenderedImage) coverage1.render(null);
        final WritableRenderedImage img2 = (WritableRenderedImage) coverage2.render(null);
        final WritableRenderedImage img3 = (WritableRenderedImage) coverage3.render(null);

        final WritablePixelIterator write1 = WritablePixelIterator.create(img1);
        final WritablePixelIterator write2 = WritablePixelIterator.create(img2);
        final WritablePixelIterator write3 = WritablePixelIterator.create(img3);
        BufferedImages.setAll(img1, new double[]{Double.NaN});
        BufferedImages.setAll(img2, new double[]{Double.NaN});
        BufferedImages.setAll(img3, new double[]{Double.NaN});

        write1.moveTo(1, 0); write1.setSample(0, 1);
        write1.moveTo(2, 0); write1.setSample(0, 1);

        write2.moveTo(2, 0); write2.setSample(0, 2);
        write2.moveTo(3, 0); write2.setSample(0, 2);
        write2.moveTo(2, 1); write2.setSample(0, 2);
        write2.moveTo(3, 1); write2.setSample(0, 2);

        write3.moveTo(6, 0); write3.setSample(0, 3);
        write3.moveTo(7, 0); write3.setSample(0, 3);
        write3.moveTo(8, 0); write3.setSample(0, 3);
        write3.moveTo(9, 0); write3.setSample(0, 3);
        write3.moveTo(6, 1); write3.setSample(0, 3);
        write3.moveTo(7, 1); write3.setSample(0, 3);
        write3.moveTo(8, 1); write3.setSample(0, 3);
        write3.moveTo(9, 1); write3.setSample(0, 3);


        /*
        We expect a final coverage to be :

        +---+---+---+---+---+---+
        |NaN|NaN| 2 | 2 | 3 | 1 |
        +---+---+---+---+---+---+
        |NaN|NaN| 2 | 2 | 1 | 1 |
        +---+---+---+---+---+---+
        */
        final GridCoverageResource aggregate =  AggregatedCoverageResource.create(
                null, AggregatedCoverageResource.Mode.SCALE,
                resource1, resource2, resource3);
        ((AggregatedCoverageResource)aggregate).setInterpolation(InterpolationCase.NEIGHBOR);

        final GridCoverage coverage = aggregate.read(grid2);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(1, 0); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(2, 0); Assert.assertEquals(2, reader.getSample(0));
        reader.moveTo(3, 0); Assert.assertEquals(2, reader.getSample(0));
        reader.moveTo(4, 0); Assert.assertEquals(3, reader.getSample(0));
        reader.moveTo(5, 0); Assert.assertEquals(1, reader.getSample(0));

        reader.moveTo(0, 1); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(1, 1); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(2, 1); Assert.assertEquals(2, reader.getSample(0));
        reader.moveTo(3, 1); Assert.assertEquals(2, reader.getSample(0));
        reader.moveTo(4, 1); Assert.assertEquals(1, reader.getSample(0));
        reader.moveTo(5, 1); Assert.assertEquals(1, reader.getSample(0));

    }

    /**
     * Test aggregating coverages with multiple sample dimensions.
     */
    @Test
    public void testMultiBandAggregation() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final Collection<SampleDimension> bands = Arrays.asList(sd,sd);

        /*
        Coverage 1
        +---+---+---+
        |1:2|NaN|NaN|
        +---+---+---+

        Coverage 2
        +---+---+---+
        |3:4|3:4|NaN|
        +---+---+---+

        Coverage 3
        +---+---+---+
        |5:6|5:6|5:6|
        +---+---+---+
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new MemoryCoverageResource(coverage1);
        final GridCoverageResource resource2 = new MemoryCoverageResource(coverage2);
        final GridCoverageResource resource3 = new MemoryCoverageResource(coverage3);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write2 = WritablePixelIterator.create( (WritableRenderedImage) coverage2.render(null));
        final WritablePixelIterator write3 = WritablePixelIterator.create( (WritableRenderedImage) coverage3.render(null));

        write1.moveTo(0, 0); write1.setPixel(new double[]{1,2});
        write1.moveTo(1, 0); write1.setPixel(new double[]{Double.NaN, Double.NaN});
        write1.moveTo(2, 0); write1.setPixel(new double[]{Double.NaN, Double.NaN});
        write2.moveTo(0, 0); write2.setPixel(new double[]{3,4});
        write2.moveTo(1, 0); write2.setPixel(new double[]{3,4});
        write2.moveTo(2, 0); write2.setPixel(new double[]{Double.NaN, Double.NaN});
        write3.moveTo(0, 0); write3.setPixel(new double[]{5,6});
        write3.moveTo(1, 0); write3.setPixel(new double[]{5,6});
        write3.moveTo(2, 0); write3.setPixel(new double[]{5,6});


        /*
        We expect a final coverage with values [1:2,3:4,5:6] on a single row
        +---+---+---+
        |1:2|3:4|5:6|
        +---+---+---+
        */
        final GridCoverageResource aggregate =  AggregatedCoverageResource.create(
                null, AggregatedCoverageResource.Mode.ORDER,
                resource1, resource2, resource3);
        ((AggregatedCoverageResource)aggregate).setInterpolation(InterpolationCase.NEIGHBOR);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertEquals(1, reader.getSample(0)); Assert.assertEquals(2, reader.getSample(1));
        reader.moveTo(1, 0); Assert.assertEquals(3, reader.getSample(0)); Assert.assertEquals(4, reader.getSample(1));
        reader.moveTo(2, 0); Assert.assertEquals(5, reader.getSample(0)); Assert.assertEquals(6, reader.getSample(1));
    }

    /**
     * Test aggregating coverages one at the time.
     */
    @Test
    public void testModifyAggregation() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope expected = new GeneralEnvelope(crs);

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final Collection<SampleDimension> bands = Arrays.asList(sd);

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 1.5, 0.5), crs);
        final GridGeometry grid3 = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 2.5, 0.5), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid3, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new MemoryCoverageResource(coverage1);
        final GridCoverageResource resource2 = new MemoryCoverageResource(coverage2);
        final GridCoverageResource resource3 = new MemoryCoverageResource(coverage3);

        final AggregatedCoverageResource agg = new AggregatedCoverageResource();
        Assert.assertNull(agg.getEnvelope().orElse(null));

        agg.add(resource1);
        Envelope envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 0, 3);
        expected.setRange(1, 0, 1);
        Assert.assertEquals(expected, new GeneralEnvelope(envelope));

        agg.add(resource2);
        envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 0, 4);
        expected.setRange(1, 0, 1);
        Assert.assertEquals(expected, new GeneralEnvelope(envelope));

        agg.add(resource3);
        envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 0, 5);
        expected.setRange(1, 0, 1);
        Assert.assertEquals(expected, new GeneralEnvelope(envelope));

        agg.remove(resource1);
        agg.remove(resource2);
        envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 2, 5);
        expected.setRange(1, 0, 1);
        Assert.assertEquals(expected, new GeneralEnvelope(envelope));
    }

}
