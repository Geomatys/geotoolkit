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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.Interpolation;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.referencing.internal.shared.AffineTransform2D;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.legacy.DefaultImageCRS;
import org.apache.sis.referencing.cs.DefaultCartesianCS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.legacy.DefaultImageDatum;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform1D;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.storage.StorageCountListener;
import org.geotoolkit.storage.event.AggregationEvent;
import org.geotoolkit.storage.event.ContentEvent;
import org.geotoolkit.storage.event.ModelEvent;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.assertEquals;
import org.opengis.metadata.spatial.DimensionNameType;
import org.apache.sis.coverage.grid.PixelInCell;
import static org.apache.sis.coverage.grid.PixelInCell.CELL_CENTER;


/**
 * Test AggregatedCoverageResource.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AggregatedCoverageResourceTest {

    /**
     * Test aggregation user order is preserved.
     */
    @Test
    public void testModeOrder() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

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

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

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
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.add(resource1);
        aggregate.add(resource2);
        aggregate.add(resource3);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); assertEquals(2, reader.getSample(0));
        reader.moveTo(2, 0); assertEquals(3, reader.getSample(0));
    }

    /**
     * Test aggregation user order is preserved with RGB images.
     */
    @Test
    public void testModeOrderRGB() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | R |NaN|NaN|
        +---+---+---+

        Coverage 2
        +---+---+---+
        | G | G |NaN|
        +---+---+---+

        Coverage 3
        +---+---+---+
        | B | B | B |
        +---+---+---+
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final BufferedImage image1 = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
        final BufferedImage image2 = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
        final BufferedImage image3 = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g1 = image1.createGraphics();
        final Graphics2D g2 = image2.createGraphics();
        final Graphics2D g3 = image3.createGraphics();
        g1.setPaint(Color.RED);
        g2.setPaint(Color.GREEN);
        g3.setPaint(Color.BLUE);
        g1.fillRect(0, 0, 1, 1);
        g2.fillRect(0, 0, 2, 1);
        g3.fillRect(0, 0, 3, 1);

        final GridCoverage coverage1 = new GridCoverageBuilder().setDomain(grid1).setValues(image1).build();
        final GridCoverage coverage2 = new GridCoverageBuilder().setDomain(grid1).setValues(image2).build();
        final GridCoverage coverage3 = new GridCoverageBuilder().setDomain(grid1).setValues(image3).build();
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);


        /*
        We expect a final coverage with values [R,G,B] on a single row
        +---+---+---+
        | R | G | B |
        +---+---+---+
        */
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.add(resource1);
        aggregate.add(resource2);
        aggregate.add(resource3);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertArrayEquals(new double[]{255.0, 0.0, 0.0, 255.0}, reader.getPixel((double[]) null), 0.0);
        reader.moveTo(1, 0); Assert.assertArrayEquals(new double[]{0.0, 255.0, 0.0, 255.0}, reader.getPixel((double[]) null), 0.0);
        reader.moveTo(2, 0); Assert.assertArrayEquals(new double[]{0.0, 0.0, 255.0, 255.0}, reader.getPixel((double[]) null), 0.0);

    }

    /**
     * Test aggregation user order is preserved.
     */
    @Test
    public void testModeScale() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder()
                .setName("data")
                .setBackground(null, Double.NaN)
                .build();
        final List<SampleDimension> bands = Arrays.asList(sd);

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
        final GridGeometry grid1 = new GridGeometry(new GridExtent( 3, 1), CELL_CENTER, new AffineTransform2D(2.0, 0, 0, 2.0, 1.00, 1.00), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent( 6, 2), CELL_CENTER, new AffineTransform2D(1.0, 0, 0, 1.0, 0.50, 0.50), crs);
        final GridGeometry grid3 = new GridGeometry(new GridExtent(12, 4), CELL_CENTER, new AffineTransform2D(0.5, 0, 0, 0.5, 0.25, 0.25), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid3, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);
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
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.SCALE);
        aggregate.add(resource1);
        aggregate.add(resource2);
        aggregate.add(resource3);
        //we must obtain the lowest resolution
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{0.5,0.5}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        Assert.assertTrue(!gridGeometry.isDefined(GridGeometry.EXTENT));
        Assert.assertTrue(gridGeometry.isDefined(GridGeometry.ENVELOPE));

        final GridCoverage coverage = aggregate.read(grid2);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(1, 0); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(2, 0); assertEquals(2, reader.getSample(0));
        reader.moveTo(3, 0); assertEquals(2, reader.getSample(0));
        reader.moveTo(4, 0); assertEquals(3, reader.getSample(0));
        reader.moveTo(5, 0); assertEquals(1, reader.getSample(0));

        reader.moveTo(0, 1); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(1, 1); Assert.assertTrue(Double.isNaN(reader.getSampleDouble(0)));
        reader.moveTo(2, 1); assertEquals(2, reader.getSample(0));
        reader.moveTo(3, 1); assertEquals(2, reader.getSample(0));
        reader.moveTo(4, 1); assertEquals(1, reader.getSample(0));
        reader.moveTo(5, 1); assertEquals(1, reader.getSample(0));

    }

    /**
     * Test aggregating coverages with multiple sample dimensions.
     */
    @Test
    public void testMultiBandAggregation() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd,sd);

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

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

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
        ((AggregatedCoverageResource)aggregate).setInterpolation(Interpolation.NEAREST);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(1, reader.getSample(0)); assertEquals(2, reader.getSample(1));
        reader.moveTo(1, 0); assertEquals(3, reader.getSample(0)); assertEquals(4, reader.getSample(1));
        reader.moveTo(2, 0); assertEquals(5, reader.getSample(0)); assertEquals(6, reader.getSample(1));
    }

    /**
     * Test aggregating coverages one at the time.
     */
    @Test
    public void testModifyAggregation() throws DataStoreException, TransformException {

        //event counters
        StorageCountListener listener = new StorageCountListener();

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
        final GeneralEnvelope expected = new GeneralEnvelope(crs);

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 1.5, 0.5), crs);
        final GridGeometry grid3 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 2.5, 0.5), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid3, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

        final AggregatedCoverageResource agg = new AggregatedCoverageResource();
        agg.addListener(StoreEvent.class, listener);
        Assert.assertNull(agg.getEnvelope().orElse(null));

        agg.add(resource1);
        assertEquals(1, listener.count(ContentEvent.class));
        assertEquals(1, listener.count(ModelEvent.class));
        assertEquals(1, listener.count(AggregationEvent.class));
        Envelope envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 0, 3);
        expected.setRange(1, 0, 1);
        assertEquals(expected, new GeneralEnvelope(envelope));

        agg.add(resource2);
        assertEquals(2, listener.count(ContentEvent.class));
        assertEquals(2, listener.count(ModelEvent.class));
        assertEquals(2, listener.count(AggregationEvent.class));
        envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 0, 4);
        expected.setRange(1, 0, 1);
        assertEquals(expected, new GeneralEnvelope(envelope));

        agg.add(resource3);
        assertEquals(3, listener.count(ContentEvent.class));
        assertEquals(3, listener.count(ModelEvent.class));
        assertEquals(3, listener.count(AggregationEvent.class));
        envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 0, 5);
        expected.setRange(1, 0, 1);
        assertEquals(expected, new GeneralEnvelope(envelope));

        agg.remove(resource1);
        agg.remove(resource2);
        assertEquals(5, listener.count(ContentEvent.class));
        assertEquals(5, listener.count(ModelEvent.class));
        assertEquals(5, listener.count(AggregationEvent.class));
        envelope = agg.getEnvelope().orElse(null);
        expected.setRange(0, 2, 5);
        expected.setRange(1, 0, 1);
        assertEquals(expected, new GeneralEnvelope(envelope));
    }

    /**
     * Test aggregation add a NaN value to fill spaces.
     */
    @Test
    public void testNoDataAdded() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder()
                .setName("data")
                .setBackground("no-data", Short.MIN_VALUE)
                .build();
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

        final GridGeometry grid1 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 2, 0), crs);
        final GridGeometry grid = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

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
        +---+------+---+
        | 1 |-32768| 2 |
        +---+------+---+
        */
        final GridCoverageResource aggregate =  AggregatedCoverageResource.create(
                null, AggregatedCoverageResource.Mode.ORDER,
                resource1, resource2);
        ((AggregatedCoverageResource) aggregate).setInterpolation(Interpolation.NEAREST);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid).forConvertedValues(true);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); assertEquals(Short.MIN_VALUE, reader.getSample(0));
        reader.moveTo(2, 0); assertEquals(2, reader.getSample(0));

    }

    /**
     * Test aggregating coverages with a single band to separate output bands.
     */
    @Test
    public void testAggregateOnSeparateBands() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        |NaN| 2 | 3 |
        +---+---+---+

        Coverage 2
        +---+---+---+
        | 4 |NaN| 6 |
        +---+---+---+

        Coverage 3
        +---+---+---+
        | 7 | 8 |NaN|
        +---+---+---+
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write2 = WritablePixelIterator.create( (WritableRenderedImage) coverage2.render(null));
        final WritablePixelIterator write3 = WritablePixelIterator.create( (WritableRenderedImage) coverage3.render(null));

        write1.moveTo(0, 0); write1.setPixel(new double[]{Double.NaN});
        write1.moveTo(1, 0); write1.setPixel(new double[]{2.0});
        write1.moveTo(2, 0); write1.setPixel(new double[]{3.0});
        write2.moveTo(0, 0); write2.setPixel(new double[]{4.0});
        write2.moveTo(1, 0); write2.setPixel(new double[]{Double.NaN});
        write2.moveTo(2, 0); write2.setPixel(new double[]{6.0});
        write3.moveTo(0, 0); write3.setPixel(new double[]{7.0});
        write3.moveTo(1, 0); write3.setPixel(new double[]{8.0});
        write3.moveTo(2, 0); write3.setPixel(new double[]{Double.NaN});


        /*
        We expect a final coverage with values on a single row
        +-------+-------+-------+
        |NaN:4:7|2:NaN:8|3:6:NaN|
        +-------+-------+-------+
        */

        final AggregatedCoverageResource.VirtualBand band0 = new AggregatedCoverageResource.VirtualBand();
        band0.setSources(new AggregatedCoverageResource.Source(resource1, 0));
        final AggregatedCoverageResource.VirtualBand band1 = new AggregatedCoverageResource.VirtualBand();
        band1.setSources(new AggregatedCoverageResource.Source(resource2, 0));
        final AggregatedCoverageResource.VirtualBand band2 = new AggregatedCoverageResource.VirtualBand();
        band2.setSources(new AggregatedCoverageResource.Source(resource3, 0));

        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource(Arrays.asList(band0,band1,band2), AggregatedCoverageResource.Mode.ORDER, crs);
        aggregate.setInterpolation(Interpolation.NEAREST);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0);
        assertEquals(Double.NaN, reader.getSampleDouble(0), 0.0);
        assertEquals(4, reader.getSampleDouble(1), 0.0);
        assertEquals(7, reader.getSampleDouble(2), 0.0);
        reader.moveTo(1, 0);
        assertEquals(2, reader.getSampleDouble(0), 0.0);
        assertEquals(Double.NaN, reader.getSampleDouble(1), 0.0);
        assertEquals(8, reader.getSampleDouble(2), 0.0);
        reader.moveTo(2, 0);
        assertEquals(3, reader.getSampleDouble(0), 0.0);
        assertEquals(6, reader.getSampleDouble(1), 0.0);
        assertEquals(Double.NaN, reader.getSampleDouble(2), 0.0);
    }

    /**
     * Test sample transform is applied and NaN value is evaluated after transform.
     */
    @Test
    public void testSampleTransform() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | 1 |NaN|NaN|
        +---+---+---+

        Coverage 2
        +---+---+---+
        | 2 | -5 |NaN|
        +---+---+---+

        Coverage 3
        +---+---+---+
        | 3 | 3 | 0 |
        +---+---+---+
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write2 = WritablePixelIterator.create( (WritableRenderedImage) coverage2.render(null));
        final WritablePixelIterator write3 = WritablePixelIterator.create( (WritableRenderedImage) coverage3.render(null));

        write1.moveTo(0, 0); write1.setSample(0, 1);
        write1.moveTo(1, 0); write1.setSample(0, Double.NaN);
        write1.moveTo(2, 0); write1.setSample(0, Double.NaN);
        write2.moveTo(0, 0); write2.setSample(0, 2);
        write2.moveTo(1, 0); write2.setSample(0, -5);
        write2.moveTo(2, 0); write2.setSample(0, Double.NaN);
        write3.moveTo(0, 0); write3.setSample(0, 3);
        write3.moveTo(1, 0); write3.setSample(0, 3);
        write3.moveTo(2, 0); write3.setSample(0, 0);


        /*
        We expect a final coverage with values [1,2,3] on a single row
        +---+---+---+
        | 12 |-3|NaN|
        +---+---+---+
        */
        final MathTransform1D trs1 = new AbstractMathTransform1D() {
            @Override
            public double transform(double value) throws TransformException {
                return value * 12.0;
            }
            @Override
            public double derivative(double value) throws TransformException {
                throw new TransformException("Not supported.");
            }
        };
        final MathTransform1D trs2 = new AbstractMathTransform1D() {
            @Override
            public double transform(double value) throws TransformException {
                return value < 0 ? Double.NaN : value;
            }
            @Override
            public double derivative(double value) throws TransformException {
                throw new TransformException("Not supported.");
            }
        };
        final MathTransform1D trs3 = new AbstractMathTransform1D() {
            @Override
            public double transform(double value) throws TransformException {
                return value > 0 ? -value : Double.NaN;
            }
            @Override
            public double derivative(double value) throws TransformException {
                throw new TransformException("Not supported.");
            }
        };

        final AggregatedCoverageResource.VirtualBand band = new AggregatedCoverageResource.VirtualBand();
        final AggregatedCoverageResource.Source source1 = new AggregatedCoverageResource.Source(resource1, 0, trs1);
        final AggregatedCoverageResource.Source source2 = new AggregatedCoverageResource.Source(resource2, 0, trs2);
        final AggregatedCoverageResource.Source source3 = new AggregatedCoverageResource.Source(resource3, 0, trs3);
        band.setSources(source1, source2, source3);

        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource(Collections.singletonList(band), AggregatedCoverageResource.Mode.ORDER, null);
        aggregate.setInterpolation(Interpolation.NEAREST);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(12.0, reader.getSampleDouble(0), 0.0);
        reader.moveTo(1, 0); assertEquals(-3.0, reader.getSampleDouble(0), 0.0);
        reader.moveTo(2, 0); assertEquals(Double.NaN, reader.getSampleDouble(0), 0.0);
    }

    /**
     * Test the grid geometry is preserved if all coverages have the same.
     */
    @Test
    public void testSharedGridGeometry() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+

        Coverage 2
        +---+---+---+
        | 4 | 5 | 6 |
        +---+---+---+
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write2 = WritablePixelIterator.create( (WritableRenderedImage) coverage2.render(null));

        write1.moveTo(0, 0); write1.setPixel(new double[]{1});
        write1.moveTo(1, 0); write1.setPixel(new double[]{2});
        write1.moveTo(2, 0); write1.setPixel(new double[]{3});
        write2.moveTo(0, 0); write2.setPixel(new double[]{4});
        write2.moveTo(1, 0); write2.setPixel(new double[]{5});
        write2.moveTo(2, 0); write2.setPixel(new double[]{6});


        /*
        We expect a final coverage with values [1:4,2:5,3:6] on a single row
        +---+---+---+
        |1:4|2:5|3:6|
        +---+---+---+
        */
        final AggregatedCoverageResource.VirtualBand band0 = new AggregatedCoverageResource.VirtualBand();
        band0.setSources(new AggregatedCoverageResource.Source(resource1, 0));
        final AggregatedCoverageResource.VirtualBand band1 = new AggregatedCoverageResource.VirtualBand();
        band1.setSources(new AggregatedCoverageResource.Source(resource2, 0));

        final AggregatedCoverageResource aggregate = new AggregatedCoverageResource(Arrays.asList(band0, band1), AggregatedCoverageResource.Mode.ORDER, null);
        aggregate.setInterpolation(Interpolation.NEAREST);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(1, reader.getSample(0)); assertEquals(4, reader.getSample(1));
        reader.moveTo(1, 0); assertEquals(2, reader.getSample(0)); assertEquals(5, reader.getSample(1));
        reader.moveTo(2, 0); assertEquals(3, reader.getSample(0)); assertEquals(6, reader.getSample(1));
    }

    /**
     * Test aggregation generates a NaN between coverages
     */
    @Test
    public void testNaNAdded() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+
        | 1 |
        +---+

        Coverage 3
                +---+
                | 3 |
                +---+
        */

        final GridGeometry grid = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid1 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid3 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 2, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_INT);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid3, bands, DataBuffer.TYPE_INT);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write3 = WritablePixelIterator.create( (WritableRenderedImage) coverage3.render(null));

        write1.moveTo(0, 0); write1.setSample(0, 1);
        write3.moveTo(0, 0); write3.setSample(0, 3);


        /*
        We expect a final coverage with values [1,NaN,3] on a single row
        +---+---+---+
        | 1 |NaN| 3 |
        +---+---+---+
        */
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.setDataType(DataBuffer.TYPE_DOUBLE);
        aggregate.add(resource1);
        aggregate.add(resource3);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridCoverage coverage = aggregate.read(grid);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); assertEquals(Double.NaN, reader.getSampleDouble(0), 0.0);
        reader.moveTo(2, 0); assertEquals(3, reader.getSample(0));
    }

    /**
     * Test aggregation generates a NaN between coverages.
     * One coverage has a special no data value.
     */
    @Test
    public void testDefinedNaNAdded() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final SampleDimension sd1 = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands1 = Arrays.asList(sd1);

        final SampleDimension sd3 = new SampleDimension.Builder()
                .setName("data")
                .addQualitative(null, -1)
                .addQuantitative("data", 0, 10, 1, 0, Units.UNITY)
                .build();
        final List<SampleDimension> bands3 = Arrays.asList(sd3);

        /*
        Coverage 1
        +---+
        | 1 |
        +---+

        Coverage 3
            +---+---+
            |-1 | 3 |
            +---+---+
        */

        final GridGeometry grid = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid1 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid3 = new GridGeometry(new GridExtent(2, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 1, 0), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands1, DataBuffer.TYPE_INT);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid3, bands3, DataBuffer.TYPE_INT);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(Names.createLocalName(null, null, "Coverage1"), coverage1);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(Names.createLocalName(null, null, "Coverage3"), coverage3);

        final WritablePixelIterator write1 = WritablePixelIterator.create( (WritableRenderedImage) coverage1.render(null));
        final WritablePixelIterator write3 = WritablePixelIterator.create( (WritableRenderedImage) coverage3.render(null));

        write1.moveTo(0, 0); write1.setSample(0, 1);
        write3.moveTo(0, 0); write3.setSample(0, -1);
        write3.moveTo(1, 0); write3.setSample(0, 3);


        /*
        We expect a final coverage with values [1,NaN,3] on a single row
        +---+---+---+
        | 1 |NaN| 3 |
        +---+---+---+
        */
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.add(resource1);
        aggregate.add(resource3);
        aggregate.setSampleDimensions(Collections.singletonList(sd));
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();

        final GridCoverage coverage = aggregate.read(grid);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); assertEquals(Double.NaN, reader.getSampleDouble(0), 0.0);
        reader.moveTo(2, 0); assertEquals(3, reader.getSample(0));

    }

    @Test
    public void defaultDataTypeIsAuto() {
        final AggregatedCoverageResource r = new AggregatedCoverageResource();
        Assert.assertTrue("Default data type should be auto: infer type from aggregated sources", r.getDataType() < 0);
    }

    /**
     * Ensure that aggregation properly detect source data types and re-use them for output production.
     */
    @Test
    public void typeInference() throws Exception {
        final CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
        final AffineTransform2D gridToCRS = new AffineTransform2D(1, 0, 0, 1, 0, 0);
        final IntFunction<GridGeometry> translateGrid = i -> new GridGeometry(new GridExtent(1, 1).translate(i, 0), CELL_CENTER, gridToCRS, crs);
        final GridGeometry gridLeft = translateGrid.apply(0);
        final GridGeometry gridCenterLeft = translateGrid.apply(1);
        final GridGeometry gridCenterRight = translateGrid.apply(2);
        final GridGeometry gridRight = translateGrid.apply(3);
        final GridGeometry overallGrid = new GridGeometry(new GridExtent(4, 1), CELL_CENTER, gridToCRS, crs);

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final GridCoverage bandShort = new BufferedGridCoverage(gridLeft, Collections.singletonList(sd), DataBuffer.TYPE_SHORT);
        final GridCoverage bandInt = new BufferedGridCoverage(gridCenterLeft, Collections.singletonList(sd), DataBuffer.TYPE_INT);
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.add(new InMemoryGridCoverageResource(bandInt));
        aggregate.add(new InMemoryGridCoverageResource(bandShort));

        RenderedImage rendering = aggregate.read(overallGrid).render(null);
        assertEquals("Infered sample type is wrong", DataBuffer.TYPE_INT, rendering.getSampleModel().getDataType());

        final GridCoverage bandFloat = new BufferedGridCoverage(gridCenterRight, Collections.singletonList(sd), DataBuffer.TYPE_FLOAT);
        aggregate.add(new InMemoryGridCoverageResource(bandFloat));

        rendering = aggregate.read(overallGrid).render(null);
        assertEquals("Infered sample type is wrong", DataBuffer.TYPE_FLOAT, rendering.getSampleModel().getDataType());

        // Test non-homogeneous aggregation
        final SampleDimension userDefinedSd = new SampleDimension.Builder()
                .setName("data")
                .setBackground("NaN", Double.NaN)
                .addQuantitative("percentage", 0, 1, 100d, 0d, Units.PERCENT)
                .build();
        aggregate.add(new InMemoryGridCoverageResource(new BufferedGridCoverage(gridRight, Collections.singletonList(userDefinedSd), DataBuffer.TYPE_DOUBLE)));

        rendering = aggregate.read(overallGrid).render(null);
        assertEquals("Infered sample type is wrong", DataBuffer.TYPE_DOUBLE, rendering.getSampleModel().getDataType());
    }

    /**
     * Ensure that data-type specified by user is used instead of datasource types.
     */
    @Test
    public void forceOutputDataType() throws Exception {
        final GridGeometry grid = new GridGeometry(new GridExtent(3, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), CommonCRS.defaultGeographic());
        final GridGeometry biggerGrid = new GridGeometry(new GridExtent(4, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), CommonCRS.defaultGeographic());

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final GridCoverage bandShort = new BufferedGridCoverage(grid, Collections.singletonList(sd), DataBuffer.TYPE_SHORT);
        final GridCoverage bandInt = new BufferedGridCoverage(grid, Collections.singletonList(sd), DataBuffer.TYPE_INT);
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.add(new InMemoryGridCoverageResource(bandInt));
        aggregate.add(new InMemoryGridCoverageResource(bandShort));

        aggregate.setDataType(DataBuffer.TYPE_DOUBLE);

        RenderedImage rendering = aggregate.read(biggerGrid).render(null);
        assertEquals("Infered sample type is wrong", DataBuffer.TYPE_DOUBLE, rendering.getSampleModel().getDataType());

        // Test non-homogeneous aggregation
        final SampleDimension userDefinedSd = new SampleDimension.Builder()
                .setName("data")
                .setBackground("NaN", Double.NaN)
                .addQuantitative("percentage", 0, 1, 100d, 0d, Units.PERCENT)
                .build();
        aggregate.add(new InMemoryGridCoverageResource(new BufferedGridCoverage(grid, Collections.singletonList(userDefinedSd), DataBuffer.TYPE_DOUBLE)));

        aggregate.setDataType(DataBuffer.TYPE_INT);

        rendering = aggregate.read(biggerGrid).render(null);
        assertEquals("Infered sample type is wrong", DataBuffer.TYPE_INT, rendering.getSampleModel().getDataType());
    }

    /**
     * Test fail safe when a resource fails a read operation.
     */
    @Test
    public void testResourceReadInError() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | 1 |NaN|NaN|
        +---+---+---+

        Coverage 2
        +---+---+---+
        | 2 | 2 |NaN| <-- will fail at reading time
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
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2) {
            @Override
            public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
                throw new RuntimeException("Read failing");
            }
        };
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

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
        We expect a final coverage with values [1,3,3] on a single row
        since 2 has failed reading
        +---+---+---+
        | 1 | 3 | 3 |
        +---+---+---+
        */
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.add(resource1);
        aggregate.add(resource2);
        aggregate.add(resource3);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        Assert.assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); Assert.assertEquals(3, reader.getSample(0));
        reader.moveTo(2, 0); Assert.assertEquals(3, reader.getSample(0));

    }

    /**
     * Test fail safe when a resource fails on metadatas and read operation.
     */
    @Test
    public void testResourceMetaInError() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | 1 |NaN|NaN|
        +---+---+---+

        Coverage 2
        +---+---+---+
        | 2 | 2 |NaN| <-- will fail at metadata and reading time
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
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2) {
            @Override
            public GridGeometry getGridGeometry() throws DataStoreException {
                throw new RuntimeException("Metadata failing");
            }

            @Override
            public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
                throw new RuntimeException("Read failing");
            }
        };
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

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
        We expect a final coverage with values [1,3,3] on a single row
        since 2 has failed reading
        +---+---+---+
        | 1 | 3 | 3 |
        +---+---+---+
        */
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.add(resource1);
        aggregate.add(resource2);
        aggregate.add(resource3);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        Assert.assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); Assert.assertEquals(3, reader.getSample(0));
        reader.moveTo(2, 0); Assert.assertEquals(3, reader.getSample(0));

    }

    /**
     * Test that an image crs data is ignored.
     */
    @Test
    public void testImageCRSIgnored() throws DataStoreException, TransformException, FactoryException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder().setName("data").build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        +---+---+---+
        | 1 |NaN|NaN|
        +---+---+---+
        Coverage 2
        +---+---+---+
        | 2 | 2 |NaN| <-- uncorrect crs
        +---+---+---+
        Coverage 3
        +---+---+---+
        | 3 | 3 | 3 |
        +---+---+---+
         */
        final CoordinateReferenceSystem imgcrs = new DefaultImageCRS(
                    Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"ImageCRS"),
                    new DefaultImageDatum(
                            Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"ImageDatum"),
                            "cell center"),
                    new DefaultCartesianCS(
                            Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"ImageCS"),
                            new DefaultCoordinateSystemAxis(
                                    Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"AxisX"),
                                    "x", AxisDirection.DISPLAY_LEFT, Units.POINT),
                            new DefaultCoordinateSystemAxis(
                                    Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY,"AxisY"),
                                    "y", AxisDirection.DISPLAY_DOWN, Units.POINT)));

        final GridGeometry grid1 = new GridGeometry(new GridExtent(3, 1), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(10, 30), PixelInCell.CELL_CENTER, new AffineTransform2D(1, 0, 0, 1, 0, 0), imgcrs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverage coverage3 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_DOUBLE);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);
        final GridCoverageResource resource3 = new InMemoryGridCoverageResource(coverage3);

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
        We expect a final coverage with values [1,3,3] on a single row
        since 2 has failed reading
        +---+---+---+
        | 1 | 3 | 3 |
        +---+---+---+
        */
        final AggregatedCoverageResource aggregate =  new AggregatedCoverageResource();
        aggregate.setInterpolation(Interpolation.NEAREST);
        aggregate.setMode(AggregatedCoverageResource.Mode.ORDER);
        aggregate.add(resource1);
        aggregate.add(resource2);
        aggregate.add(resource3);
        final double[] resolution = aggregate.getGridGeometry().getResolution(true);
        Assert.assertArrayEquals(new double[]{1.0,1.0}, resolution, 0.0);

        final GridGeometry gridGeometry = aggregate.getGridGeometry();
        Assert.assertEquals(grid1, gridGeometry);

        final GridCoverage coverage = aggregate.read(grid1);
        final RenderedImage image = coverage.render(null);
        final PixelIterator reader =  PixelIterator.create( image);
        reader.moveTo(0, 0); Assert.assertEquals(1, reader.getSample(0));
        reader.moveTo(1, 0); Assert.assertEquals(3, reader.getSample(0));
        reader.moveTo(2, 0); Assert.assertEquals(3, reader.getSample(0));
    }

    /**
     * Test grid geometry aggreation with negative scale.
     */
    @Test
    public void testGridAggregationWithNegativeScale() throws DataStoreException, TransformException {

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final SampleDimension sd = new SampleDimension.Builder()
                .setName("data")
                .setBackground("no-data", Short.MIN_VALUE)
                .build();
        final List<SampleDimension> bands = Arrays.asList(sd);

        /*
        Coverage 1
        5 +---+
          | 1 |
          +---+
          0

        Coverage 2
             12 +---+
                | 2 |
                +---+
                2
        */

        final GridGeometry grid1 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, -1, 0, 5), crs);
        final GridGeometry grid2 = new GridGeometry(new GridExtent(1, 1), CELL_CENTER, new AffineTransform2D(1, 0, 0, -1, 2, 12), crs);

        final GridCoverage coverage1 = new BufferedGridCoverage(grid1, bands, DataBuffer.TYPE_SHORT);
        final GridCoverage coverage2 = new BufferedGridCoverage(grid2, bands, DataBuffer.TYPE_SHORT);
        final GridCoverageResource resource1 = new InMemoryGridCoverageResource(coverage1);
        final GridCoverageResource resource2 = new InMemoryGridCoverageResource(coverage2);

        final GridCoverageResource aggregate =  AggregatedCoverageResource.create(
                null, AggregatedCoverageResource.Mode.ORDER,
                resource1, resource2);
        ((AggregatedCoverageResource) aggregate).setInterpolation(Interpolation.NEAREST);

        final GridGeometry result = aggregate.getGridGeometry();

        //check envelopes combine both grid envelope
        final Envelope resultEnv = result.getEnvelope();
        final GeneralEnvelope expectedEnv = new GeneralEnvelope(grid1.getEnvelope());
        expectedEnv.add(grid2.getEnvelope());
        assertEquals(expectedEnv, new GeneralEnvelope(resultEnv));

        //check grid geometry
        final GridGeometry expected = new GridGeometry(
                new GridExtent(new DimensionNameType[]{DimensionNameType.COLUMN, DimensionNameType.ROW},
                        new long[]{0,-7}, new long[]{2, 0}, true),
                CELL_CENTER,
                new AffineTransform2D(1, 0, 0, -1, 0, 5), crs);
        assertEquals(expected, result);
    }
}
