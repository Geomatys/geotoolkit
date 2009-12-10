/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.image.io.text;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.color.ColorSpace;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.image.io.SpatialImageReadParam;

import org.junit.*;
import static java.lang.Float.NaN;
import static org.junit.Assert.*;


/**
 * The base class for {@link TextImageReader} tests.
 * <p>
 * This class provides also {@link #loadAndPrint} and {@link #printStatistics} static methods
 * for manual testings.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.06
 */
public abstract class TextImageReaderTestBase {
    /**
     * The precision for comparison of sample values. The values in the test files provided
     * in this package have 3 significant digits, so the precision is set to the next digit.
     */
    protected static final float EPS = 0.0001f;

    /**
     * Creates the image reader initialized to the input to read. The input
     * content shall be the same than the {@code "test-data/matrix.txt"} file.
     *
     * @return The reader to test.
     * @throws IOException If an error occured while creating the format.
     */
    protected abstract TextImageReader createImageReader() throws IOException;

    /**
     * Tests the {@link TextImageReader.Spi#canDecodeInput(Object)}.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testCanRead() throws IOException {
        final TextImageReader reader = createImageReader();
        assertTrue(reader.getOriginatingProvider().canDecodeInput(reader.getInput()));
        /*
         * Ensure that the above check did not caused the lost of data.
         */
        final BufferedImage image = reader.read(0);
        reader.dispose();
        assertEquals(20, image.getWidth());
        assertEquals(42, image.getHeight());
        final Raster raster = image.getRaster();
        assertEquals(-1.123f, raster.getSampleFloat(0, 0, 0), EPS);
    }

    /**
     * Tests the reading of the {@link "matrix.txt"} file.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testReadFile() throws IOException {
        final TextImageReader reader = createImageReader();
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
     * Tests the reading of a sub-region.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testSubRegion() throws IOException {
        final TextImageReader reader = createImageReader();
        final SpatialImageReadParam param = reader.getDefaultReadParam();
        param.setSourceRegion(new Rectangle(5, 10, 10, 20));
        param.setSourceSubsampling(2, 3, 1, 2);
        final BufferedImage image = reader.read(0, param);
        reader.dispose();

        final Raster raster = image.getRaster();
        assertEquals(  NaN , raster.getSampleFloat(0, 0, 0), EPS);
        assertEquals(16.470, raster.getSampleFloat(1, 0, 0), EPS);
        assertEquals(22.161, raster.getSampleFloat(1, 1, 0), EPS);
        assertEquals(27.619, raster.getSampleFloat(1, 3, 0), EPS);
        assertEquals(29.347, raster.getSampleFloat(4, 3, 0), EPS);
    }

    /**
     * Tests the reading with conversion to the {@link DataBuffer#TYPE_FLOAT}.
     * The floating point numbers will be casted to integer types.
     *
     * @throws IOException if an error occured while reading the file.
     */
    @Test
    public void testByteType() throws IOException {
        final TextImageReader reader = createImageReader();
        final SpatialImageReadParam param = reader.getDefaultReadParam();
        final byte[] RGB = new byte[256];
        for (int i=0; i<RGB.length; i++) {
            RGB[i] = (byte) i;
        }
        param.setDestinationType(ImageTypeSpecifier.createIndexed(RGB, RGB, RGB, null, 8, DataBuffer.TYPE_BYTE));
        /*
         * Reads the image twice. 'image' is the reference that we want to test with byte values.
         * 'original' is the one with floating point value, to be used only for comparison.
         */
        final BufferedImage image = reader.read(0, param);
        final BufferedImage original = reader.read(0);
        reader.dispose();
        assertEquals(IndexColorModel.class, image.getColorModel().getClass());
        assertEquals(DataBuffer.TYPE_FLOAT, original.getSampleModel().getDataType());
        /*
         * Only the NaN values in the original data should be zero in the byte data.
         */
        final Raster byteRaster  = image.getRaster();
        final Raster floatRaster = original.getRaster();
        for (int y=byteRaster.getHeight(); --y>=0;) {
            for (int x=byteRaster.getWidth(); --x>=0;) {
                // Use 'float' type even for byteRaster in case we have NaN.
                final float byteValue  =  byteRaster.getSampleFloat(x, y, 0);
                final float floatValue = floatRaster.getSampleFloat(x, y, 0);
                assertEquals(String.valueOf(floatValue), Float.isNaN(floatValue), byteValue == 0);
            }
        }
        /*
         * Test the same pixel values than 'testReadFile()'.
         * We have an offset of 3, rounded toward zero.
         */
        assertEquals(1, byteRaster.getSample( 0,  0, 0));
        assertEquals(3, byteRaster.getSample( 2,  3, 0));
        assertEquals(0, byteRaster.getSample( 3,  4, 0));
        assertEquals(1, byteRaster.getSample( 0, 41, 0));
        assertEquals(0, byteRaster.getSample(19,  4, 0));
    }

    /**
     * Loads the given image using the given provider, and prints information about it.
     * This is used only as a helper tools for tuning the test suites.
     *
     * @param  provider     The provider from which to get a reader.
     * @param  input        The file to read.
     * @param  region       The region in the file to read.
     * @param  xSubsampling Subsampling along the <var>x</var> axis (1 if none).
     * @param  ySubsampling Subsampling along the <var>y</var> axis (1 if none).
     * @return The raster which have been read.
     * @throws IOException In an error occured while reading.
     */
    public static Raster loadAndPrint(final ImageReaderSpi provider, final File input,
            final Rectangle region, final int xSubsampling, final int ySubsampling) throws IOException
    {
        final ImageReader reader = provider.createReaderInstance();
        reader.setInput(input);
        System.out.println(reader.getImageMetadata(0));

        final ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceRegion(region);
        param.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);

        final long timestamp = System.currentTimeMillis();
        BufferedImage image = reader.read(0, param);
        System.out.println("Ellapsed time: " + (System.currentTimeMillis() - timestamp) / 1000f + " seconds.");
        reader.dispose();

        final Raster raster = image.getRaster();
        printStatistics(raster);
        System.out.println();
        return raster;
    }

    /**
     * Prints the minimal and maximal values found in the given raster.
     * This is used only as a helper tools for tuning the test suites.
     *
     * @param  raster The raster for which to print extrema.
     */
    public static void printStatistics(final Raster raster) {
        final Statistics stats = new Statistics();
        final int xmin = raster.getMinX();
        final int ymin = raster.getMinY();
        final int xmax = raster.getWidth()  + xmin;
        final int ymax = raster.getHeight() + ymin;
        final int nb   = raster.getNumBands();
        for (int b=0; b<nb; b++) {
            for (int y=ymin; y<ymax; y++) {
                for (int x=xmin; x<xmax; x++) {
                    stats.add(raster.getSampleDouble(x, y, b));
                }
            }
        }
        System.out.println("Raster bounds: (" + xmin + ',' + ymin + ") - (" + xmax + ',' + ymax + ')');
        System.out.println("Statistics on sample values (" + nb + " bands):");
        System.out.println(stats);
    }

    /**
     * Saves the first band of the given raster in a binary float format in the given file.
     * This is sometime useful for comparison purpose, and is used only as a helper tools
     * for tuning the test suites.
     *
     * @param  raster The raster to write in binary format.
     * @param  file The file to create.
     * @throws IOException If an error occured while writing the file.
     */
    public static void saveBinary(final Raster raster, final File file) throws IOException {
        final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        final int xmin = raster.getMinX();
        final int ymin = raster.getMinY();
        final int xmax = raster.getWidth()  + xmin;
        final int ymax = raster.getHeight() + ymin;
        for (int y=ymin; y<ymax; y++) {
            for (int x=xmin; x<xmax; x++) {
                out.writeFloat(raster.getSampleFloat(x, y, 0));
            }
        }
        out.close();
    }

    /**
     * Saves the first band of the given raster in a PNG format in the given file.
     * This is sometime useful for visual check purpose, and is used only as a helper
     * tools for tuning the test suites. The image is converted to grayscale before to
     * be saved.
     *
     * @param  raster The raster to write in PNG format.
     * @param  file The file to create.
     * @throws IOException If an error occured while writing the file.
     */
    public static void savePNG(final Raster raster, final File file) throws IOException {
        float min = Float.POSITIVE_INFINITY;
        float max = Float.NEGATIVE_INFINITY;
        final int xmin   = raster.getMinX();
        final int ymin   = raster.getMinY();
        final int width  = raster.getWidth();
        final int height = raster.getHeight();
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                final float value = raster.getSampleFloat(x + xmin, y + ymin, 0);
                if (value < min) min = value;
                if (value > min) max = value;
            }
        }
        final float scale = 255 / (max - min);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        final WritableRaster dest = image.getRaster();
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                final double value = raster.getSampleDouble(x + xmin, y + ymin, 0);
                dest.setSample(x, y, 0, Math.round((value - min) * scale));
            }
        }
        ImageIO.write(image, "png", file);
    }
}
