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
package org.geotoolkit.image.io.plugin;

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
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.internal.image.ImageUtilities;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * The base class for {@link ImageReader} tests.
 * <p>
 * This class provides also {@link #loadAndPrint} and {@link #printStatistics} static methods
 * for manual testings.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.06
 */
public abstract class ImageReaderTestBase {
    /**
     * The precision for comparison of sample values. The values in the test files provided
     * in this package have 3 significant digits, so the precision is set to the next digit.
     */
    protected static final float EPS = 0.0001f;

    /**
     * Creates the image reader initialized to the input to read.
     *
     * @return The reader to test.
     * @throws IOException If an error occured while creating the format.
     */
    protected abstract ImageReader createImageReader() throws IOException;

    /**
     * Loads the full image, then load random regions.
     * The results are then compared with the pixels in the original image.
     *
     * @throws IOException If an error occured while reading the images.
     */
    @Test
    public void testRandomRegions() throws IOException {
        testRandom(true, false);
    }

    /**
     * Loads the full image, then load with random subsamplings.
     * The results are then compared with the pixels in the original image.
     *
     * @throws IOException If an error occured while reading the images.
     */
    @Test
    public void testRandomSubsamplings() throws IOException {
        testRandom(false, true);
    }

    /**
     * Loads the full image, then load random regions with random subsamplings.
     * The results are then compared with the pixels in the original image.
     *
     * @throws IOException If an error occured while reading the images.
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
     * @throws IOException  If an error occured while reading the images.
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
            Rectangle region = ImageUtilities.getBounds(original);
            if (regions) {
                region.x     += random.nextInt(region.width);
                region.y     += random.nextInt(region.height);
                region.width  = random.nextInt(region.width)  + 1;
                region.height = random.nextInt(region.height) + 1;
                region = region.intersection(ImageUtilities.getBounds(original));
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
                    if (!Utilities.equals(expected, actual)) {
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
