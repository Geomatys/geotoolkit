/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage.io;

import java.awt.color.ColorSpace;
import java.awt.image.DataBuffer;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.image.io.plugin.TextMatrixImageReaderTest;

import org.junit.*;
import static java.lang.Float.NaN;
import static org.junit.Assert.*;


/**
 * Tests {@link ImageReaderAdapter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
@DependsOn(ImageCoverageReaderTest.class)
public final strictfp class ImageReaderAdapterTest {
    /**
     * The precision for comparison of sample values. The values in this test file
     * have 3 significant digits, so the precision is set to the next digit.
     */
    private static final float EPS = 0.0001f;

    /**
     * Registers a "matrix" reader forced to the US format.
     */
    @BeforeClass
    public static void registerReaderUS() {
        ImageCoverageReaderTest.registerReaderUS();
    }

    /**
     * Deregisters the reader defined by {@link #registerReaderUS()}.
     */
    @AfterClass
    public static void deregisterReaderUS() {
        ImageCoverageReaderTest.deregisterReaderUS();
    }

    /**
     * Returns the image reader to use for the test.
     */
    private static ImageReaderAdapter createImageReader() throws IOException {
        final ImageReaderAdapter reader = new ImageReaderAdapter(new ImageCoverageReader());
        reader.setInput(TestData.file(TextMatrixImageReaderTest.class, "matrix.txt"));
        return reader;
    }

    /**
     * Tests the full image. This test is a modified copy of
     * {@link org.geotoolkit.image.io.plugin.TextMatrixImageReaderTest#testReadFile()};
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testReadFile() throws IOException {
        final ImageReaderAdapter reader = createImageReader();
        final BufferedImage image = reader.read(0);
        reader.dispose();
        assertEquals(20, image.getWidth());
        assertEquals(42, image.getHeight());
        assertEquals(DataBuffer.TYPE_FLOAT, image.getSampleModel().getDataType());

        final ColorSpace cs = image.getColorModel().getColorSpace();
        assertEquals(1, cs.getNumComponents());
        assertEquals(-1.893, cs.getMinValue(0), EPS);
        assertEquals(31.140, cs.getMaxValue(0), EPS);

        final Raster raster = image.getRaster();
        assertEquals(-1.123f, raster.getSampleFloat( 0,  0, 0), EPS);
        assertEquals( 0.273f, raster.getSampleFloat( 2,  3, 0), EPS);
        assertEquals(   NaN , raster.getSampleFloat( 3,  4, 0), EPS);
        assertEquals(-1.075f, raster.getSampleFloat( 0, 41, 0), EPS);
        assertEquals(   NaN , raster.getSampleFloat(19,  4, 0), EPS);
    }

    /**
     * Reads a region of the image. This test is a modified copy of
     * {@link org.geotoolkit.image.io.plugin.TextMatrixImageReaderTest#testSubRegion()};
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testSubRegion() throws IOException {
        final ImageReaderAdapter reader = createImageReader();
        final SpatialImageReadParam param = reader.getDefaultReadParam();
        param.setSourceRegion(new Rectangle(5, 10, 10, 20));
        param.setSourceSubsampling(2, 3, 1, 2);
        final BufferedImage image = reader.read(0, param);
        reader.dispose();

        assertEquals(5, image.getWidth());
        assertEquals(6, image.getHeight());
        assertEquals(DataBuffer.TYPE_FLOAT, image.getSampleModel().getDataType());

        final Raster raster = image.getRaster();
        assertEquals(  NaN , raster.getSampleFloat(0, 0, 0), EPS);
        assertEquals(16.470, raster.getSampleFloat(1, 0, 0), EPS);
        assertEquals(22.161, raster.getSampleFloat(1, 1, 0), EPS);
        assertEquals(27.619, raster.getSampleFloat(1, 3, 0), EPS);
        assertEquals(29.347, raster.getSampleFloat(4, 3, 0), EPS);
    }
}
