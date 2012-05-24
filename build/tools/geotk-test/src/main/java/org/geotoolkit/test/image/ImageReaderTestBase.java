/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test.image;

import java.util.Random;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * The base class for {@link ImageReader} tests. This class provides the following tests.
 * Each test loads the same image twice and compare the result:
 * <p>
 * <ul>
 *   <li>{@link #testRandomRegions()}</li>
 *   <li>{@link #testRandomSubsamplings()}</li>
 *   <li>{@link #testRandomRegionsAndSubsamplings()}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.08 (derived from 3.06)
 */
public abstract strictfp class ImageReaderTestBase extends ImageTestBase {
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
     * This method is invoked by each test method in this class.
     *
     * @return The reader to test.
     * @throws IOException If an error occurred while creating the reader.
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
        final ImageReader   reader    = createImageReader();
        final Object        input     = reader.getInput();
        final BufferedImage fullImage = reader.read(0);
        final Raster        original  = fullImage.getRaster();
        final Random        random    = new Random();
        this.image = fullImage;
        for (int i=0; i<100; i++) {
            if (reader.getMinIndex() != 0) {
                reader.reset();
                reader.setInput(input);
            }
            final ImageReadParam param = reader.getDefaultReadParam();
            Rectangle region = original.getBounds();
            if (regions) {
                region.x     += random.nextInt(region.width);
                region.y     += random.nextInt(region.height);
                region.width  = random.nextInt(region.width)  + 1;
                region.height = random.nextInt(region.height) + 1;
                region = region.intersection(original.getBounds());
                param.setSourceRegion(region);
            }
            int xSubsampling=1, ySubsampling=1;
            if (subsamplings) {
                xSubsampling = random.nextInt(region.width)  + 1;
                ySubsampling = random.nextInt(region.height) + 1;
                param.setSourceSubsampling(xSubsampling, ySubsampling, 0, 0);
            }
            final BufferedImage image = reader.read(0, param);
            this.image = image; // Allow subclasses to inspect the image in case of problem.
            final Raster raster = image.getRaster();
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
        this.image = fullImage;
        reader.dispose();
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
