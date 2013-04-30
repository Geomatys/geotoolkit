/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.util.Random;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.RenderedOp;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;

import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.MathTransforms;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link SampleTranscoder} implementation. Image adapter depends
 * heavily on {@link CategoryList}, so this one should be tested first.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.11
 *
 * @since 2.1
 */
@DependsOn(SampleDimensionTest.class)
public final strictfp class SampleTranscoderTest {
    /**
     * Small value for comparisons. Remind: transformed values are stored in a new image
     * using the {@code float} data type. So we can't expected as much precision than with
     * a {@code double} data type.
     */
    private static final double EPS = 1E-4;

    /**
     * Random number generator for this test.
     */
    private static final Random random = new Random(6215962897884256696L);

    /**
     * Creates a dummy sample dimensions for temperature and random qualitative categories.
     */
    private GridSampleDimension createTemperatureBand() {
        return new GridSampleDimension("Temperature", new Category[] {
            new Category("No data",     null, 0),
            new Category("Land",        null, 1),
            new Category("Clouds",      null, 2),
            new Category("Temperature", null, 3, 100, 0.1, 5),
            new Category("Foo",         null, 100, 160, -1, 3),
            new Category("Tarzan",      null, 160)
        }, null);
    }

    /**
     * Tests the transformation using a random raster with only one band.
     *
     * @throws TransformException If an error occurred while transforming a value.
     */
    @Test
    public void testOneBand() throws TransformException {
        final GridSampleDimension band1 = createTemperatureBand();
        assertTrue(testOneBand(1,  0) instanceof RenderedImageAdapter);
        assertTrue(testOneBand(.8, 2) instanceof RenderedOp);
        assertTrue(testOneBand(band1) instanceof RenderedOp);
    }

    /**
     * Tests the transformation using a random raster with only one band.
     * A sample dimension with only one category will be created using the given scale and
     * offset factors.
     *
     * @param  scale The scale factor.
     * @param  offset The offset value.
     * @return The transformed image.
     */
    private static RenderedImage testOneBand(final double scale, final double offset) throws TransformException {
        final Category category = new Category("Values", null, 0, 256, scale, offset);
        return testOneBand(new GridSampleDimension("Measure", new Category[] {category}, null));
    }

    /**
     * Tests the transformation using a random raster with only one band.
     *
     * @param  band The sample dimension for the only band.
     * @return The transformed image.
     */
    private static RenderedImage testOneBand(final GridSampleDimension band) throws TransformException {
        final int SIZE = 64;
        /*
         * Constructs a 64x64 image with random values.
         * Samples values are integer in the range 0..160 inclusive.
         */
        final BufferedImage  source = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_INDEXED);
        final DataBufferByte buffer = (DataBufferByte) source.getRaster().getDataBuffer();
        final byte[] array = buffer.getData(0);
        for (int i=0; i<array.length; i++) {
            array[i] = (byte) random.nextInt(161);
        }
        GridCoverage2D coverage = createGridCoverage2D(source, band);
        /*
         * Apply the operation. The SampleTranscoder class is suppose to transform our
         * integers into real-world values. Check if the result use floating-points.
         */
        final RenderedImage target = coverage.view(ViewType.GEOPHYSICS).getRenderedImage();
        assertSame(target, PlanarImage.wrapRenderedImage(target));
        assertEquals(DataBuffer.TYPE_BYTE, source.getSampleModel().getDataType());
        if (coverage.getRenderedImage() != target) {
            assertEquals(DataBuffer.TYPE_FLOAT, target.getSampleModel().getDataType());
        }
        /*
         * Now, gets the data as an array and compare it with the expected values.
         */
        double[] sourceData = source.getData().getSamples(0, 0, SIZE, SIZE, 0, (double[]) null);
        double[] targetData = target.getData().getSamples(0, 0, SIZE, SIZE, 0, (double[]) null);
        band.getSampleToGeophysics().transform(sourceData, 0, sourceData, 0, sourceData.length);
        CategoryListTest.compare(sourceData, targetData, EPS);
        /*
         * Construct a new image with the resulting data, and apply an inverse transformation.
         * Compare the resulting values with the original data.
         */
        RenderedImage back = PlanarImage.wrapRenderedImage(target).getAsBufferedImage();
        coverage = createGridCoverage2D(back, band.geophysics(true));

        back = coverage.view(ViewType.PACKED).getRenderedImage();
        assertEquals(DataBuffer.TYPE_BYTE, back.getSampleModel().getDataType());
        sourceData = source.getData().getSamples(0, 0, SIZE, SIZE, 0, (double[]) null);
        targetData =   back.getData().getSamples(0, 0, SIZE, SIZE, 0, (double[]) null);
        CategoryListTest.compare(sourceData, targetData, 1.0 + EPS);
        /*
         * Returns the "geophysics view" of the image.
         */
        return target;
    }

    /**
     * Creates a {@code GridCoverage2D} instance for the given image using a random envelope.
     * We don't care about the envelope for this test suite, so an identity transform is most
     * convenient in order to work like in pixel coordinates.
     */
    private static GridCoverage2D createGridCoverage2D(RenderedImage image, GridSampleDimension band) {
        final MathTransform identity = MathTransforms.identity(2);
        final GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);
        return factory.create("Test", image, DefaultGeographicCRS.WGS84,
                    identity, new GridSampleDimension[] {band}, null, null);
    }

    /**
     * Creates a {@code GridCoverage2D} instance for the given image using a random envelope.
     * We don't care about the envelope for this test suite, so an identity transform is most
     * convenient in order to work like in pixel coordinates.
     */
    private static GridCoverage2D createGridCoverage2D(RenderedImage image, Category category) {
        return createGridCoverage2D(image, new GridSampleDimension(category.getName(),
                new Category[] {category}, null));
    }

    /**
     * Tests a raster of type {@code TYPE_USHORT}, both with signed and unsigned categories.
     */
    @Test
    public void testTypeUShort() {
        /*
         * Creates a dummy image. We don't care about the colors for this test.
         * However we want the values in the upper-left corner to be negatives,
         * and the values in the lower-right corner to be positives.
         */
        final int[] values = new int[] {0, 1, 2, 10, 100, 255, 256, 1000, 10000};
        final double scale  = 0.1;
        final double offset = 1.0;
        final byte[] RGB = new byte[0x10000];
        for (int i=0; i<RGB.length; i++) {
            RGB[i] = (byte) i;
        }
        final IndexColorModel cm = new IndexColorModel(16, RGB.length, RGB, RGB, RGB);
        final WritableRaster raster = cm.createCompatibleWritableRaster(2, values.length);
        int sign = 1;
        for (int i=0; i<=1; i++) {
            for (int j=0; j<values.length; j++) {
                raster.setSample(i, j, 0, values[j]*sign);
            }
            sign = -sign;
        }
        final BufferedImage image = new BufferedImage(cm, raster, false, null);
        final Point point = new Point();
        double[] buffer = null;
        /*
         * Tests unsigned categories first, then signed categories.
         *
         * NOTE: JAI bug? See the comment in the ViewsManager.geophysics(...) method (look in the
         * code block setting the parameters for the JAI "Rescale" operation). To test this issue
         * in a debugger, put a break point on the 'createGridCoverage2D(...)' line, then jump to
         * the above-cited ViewsManager.geophysics(...) method. The first loop iteration in this
         * test is the interesting one.
         */
        boolean forceSigned = false;
        do {
            final int lower, upper;
            if (forceSigned) {
                lower = Short.MIN_VALUE;
                upper = Short.MAX_VALUE;
            } else {
                lower = 0;
                upper = 0x10000;
            }
            Category category = new Category("Test", null, lower, upper, scale, offset);
            GridCoverage2D coverage = createGridCoverage2D(image, category).view(ViewType.GEOPHYSICS);
            for (int i=0; i<=1; i++) {
                for (int j=0; j<values.length; j++) {
                    point.x = i;
                    point.y = j;
                    int expected = values[j];
                    if (i != 0) {
                        // Testing negative values.
                        expected = -expected;
                        if (!forceSigned) {
                            expected &= 0xFFFF;
                        }
                    }
                    buffer = coverage.evaluate(point, buffer);
                    String message = "Testing the " + expected + " sample value (stored as " +
                            raster.getSample(i, j, 0) + " in the raster)";
                    if (false) { // set to 'true' for tracing the operations.
                        System.out.println(message);
                    }
                    assertEquals(message, expected*scale + offset, buffer[0], EPS);
                }
            }
        } while ((forceSigned = !forceSigned) == true);
    }
}
