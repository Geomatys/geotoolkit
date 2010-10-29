/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.test.image;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * The base class for {@link ImageReader} tests.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.08 (derived from 3.06)
 */
public abstract class ImageReaderTestBase extends ImageTestBase {
    /**
     * The precision for comparison of sample values. The values in the test files provided
     * in this package have 3 significant digits, so the precision is set to the next digit.
     */
    protected static final float EPS = 0.0001f;

    /**
     * Creates a new test suite for the given class.
     *
     * @param testing The class to be tested.
     */
    protected ImageReaderTestBase(final Class<? extends ImageReader> testing) {
        super(testing);
    }

    /**
     * Creates the image reader initialized to the input to read.
     *
     * @return The reader to test.
     * @throws IOException If an error occurred while creating the format.
     */
    protected abstract ImageReader createImageReader() throws IOException;

    /**
     * Loads the full image, then load random regions.
     * The results are then compared with the pixels in the original image.
     *
     * @throws IOException If an error occurred while reading the images.
     */
    @Test
    public void testRandomRegions() throws IOException {
        testRandom(true, false);
    }

    /**
     * Loads the full image, then load with random subsamplings.
     * The results are then compared with the pixels in the original image.
     *
     * @throws IOException If an error occurred while reading the images.
     */
    @Test
    public void testRandomSubsamplings() throws IOException {
        testRandom(false, true);
    }

    /**
     * Loads the full image, then load random regions with random subsamplings.
     * The results are then compared with the pixels in the original image.
     *
     * @throws IOException If an error occurred while reading the images.
     */
    @Test
    public void testRandomRegionsAndSubsamplings() throws IOException {
        testRandom(true, true);
    }

    /**
     * Implementation of the {@code testRandomXXX()} method.
     *
     * @param  regions      {@code true} for setting random regions.
     * @param  subsamplings {@code true} for setting random subsamplings.
     * @throws IOException  If an error occurred while reading the images.
     */
    private void testRandom(final boolean regions, final boolean subsamplings) throws IOException {
        final ImageReader reader   = createImageReader();
        final Object      input    = reader.getInput();
        final Raster      original = reader.read(0).getRaster();
        final Random      random   = new Random();
        for (int i=0; i<100; i++) {
            if (reader.getMinIndex() != 0) {
                reader.reset();
                reader.setInput(input);
            }
            final ImageReadParam param = reader.getDefaultReadParam();
            Rectangle region = getBounds(original);
            if (regions) {
                region.x     += random.nextInt(region.width);
                region.y     += random.nextInt(region.height);
                region.width  = random.nextInt(region.width)  + 1;
                region.height = random.nextInt(region.height) + 1;
                region = region.intersection(getBounds(original));
                param.setSourceRegion(region);
            }
            int xSubsampling=1, ySubsampling=1;
            if (subsamplings) {
                xSubsampling = random.nextInt(region.width)  + 1;
                ySubsampling = random.nextInt(region.height) + 1;
                param.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
            }
            final Raster raster = reader.read(0, param).getRaster();
            final int xmin = raster.getMinX();
            final int ymin = raster.getMinY();
            final int xmax = raster.getWidth()  + xmin;
            final int ymax = raster.getHeight() + ymin;
            for (int y=ymin; y<ymax; y++) {
                final int sy = (y - ymin) * ySubsampling + region.y;
                for (int x=xmin; x<xmax; x++) {
                    final int sx = (x - xmin) * xSubsampling + region.x;
                    final float expected = original.getSampleFloat(sx, sy, 0);
                    final float actual   = raster  .getSampleFloat( x,  y, 0);
                    if (Float.floatToIntBits(expected) != Float.floatToIntBits(actual)) {
                        fail("Source origin: " + region.x      + ',' + region.y      + '\n' +
                             "Source size:   " + region.width  + ',' + region.height + '\n' +
                             "Target origin: " + xmin          + ',' + ymin          + '\n' +
                             "Target size:   " + (xmax - xmin) + ',' + (ymax - ymin) + '\n' +
                             "Subsampling:   " + xSubsampling  + ',' + ySubsampling  + '\n' +
                             "Sample coord.: " + x             + ',' + y             + '\n' +
                             "Expected " + expected + " but got " + actual);
                    }
                }
            }
        }
        reader.dispose();
    }

    /**
     * Returns the bounds of the given raster as a new rectangle.
     *
     * @param  raster The raster for which to get the bounds.
     * @return The bounds of the given raster.
     */
    public static Rectangle getBounds(final Raster raster) {
        return new Rectangle(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight());
    }

    /**
     * Saves the first band of the given raster in a binary float format in the given file.
     * This is sometime useful for comparison purpose, and is used only as a helper tools
     * for tuning the test suites.
     *
     * @param  raster The raster to write in binary format.
     * @param  file The file to create.
     * @throws IOException If an error occurred while writing the file.
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
     * @throws IOException If an error occurred while writing the file.
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
        assertTrue(ImageIO.write(image, "png", file));
    }

    /**
     * Checks in a "best effort" way if the {@linkplain javax.imageio.stream.ImageInputStream}s
     * have been closed. This method relies on the {@code finalize()} method declared in the
     * {@link org.geotoolkit.internal.image.io.CheckedImageInputStream} class.
     * <p>
     * This method is invoked automatically by JUnit after each test and doesn't need to be
     * invoked explicitly.
     *
     * @since 3.14
     */
    @After
    public final void ensureStreamAreClosed() {
        System.gc();
        System.runFinalization();
    }
}
