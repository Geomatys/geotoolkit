/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image;

import java.util.Arrays;
import java.awt.image.RenderedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.ComponentColorModel;

import org.geotoolkit.image.jai.Registry;
import org.opengis.coverage.PaletteInterpretation;

import org.junit.*;
import static org.junit.Assert.*;
import static java.awt.image.DataBuffer.*;


/**
 * Tests {@link ImageWorker}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
public final class ImageWorkerTest extends ImageTestCase {
    /**
     * Whatever platform-dependent tests should be enabled. This is used for
     * marking the tests where the result appears to vary between platforms.
     */
    private static final boolean RUN_PLATFORM_DEPENDENT_TESTS = false;

    /**
     * Creates a new test case.
     */
    public ImageWorkerTest() {
        super(ImageWorker.class);
    }

    /**
     * To ensure strict reproductibility of the test suite accross different platforms,
     * disable native acceleration for some image operations that {@link ImageWorker}
     * uses through this test suite.
     */
    @Before
    public void disableNativeAcceleration() {
        Registry.setNativeAccelerationAllowed("ColorConvert", false);
    }

    /**
     * Resets the native acceleration that were disabled by {@link #disableNativeAcceleration()}.
     */
    @After
    public void enableNativeAcceleration() {
        Registry.setNativeAccelerationAllowed("ColorConvert", true);
    }

    /**
     * Tests on an indexed image.
     */
    @Test
    public void testOnIndexed() {
        viewEnabled = false; // Set to true for visualizing the images.
        loadSampleImage(SampleImage.INDEXED);
        final ImageWorker worker = new ImageWorker(image);
        /*
         * Query informations about the image.
         */
        assertSame(image, worker.getRenderedImage());
        assertSame(image, worker.getBufferedImage());
        assertEquals(1,   worker.getNumBands());
        assertEquals(-1,  worker.getTransparentPixel());
        assertFalse (     worker.isTranslucent());
        assertTrue  (     worker.isBytes());
        assertFalse (     worker.isBinary());
        assertTrue  (     worker.isIndexed());
        assertEquals(PaletteInterpretation.RGB, worker.getColorSpaceType());
        assertTrue(Arrays.equals(new double[] {  0}, worker.getMinimums()));
        assertTrue(Arrays.equals(new double[] {255}, worker.getMaximums()));
        final RenderedImage original = image;
        image = worker.image; // Now a StatisticsOpImage
        assertSame(image, worker.getPlanarImage());
        assertSame(image, worker.getRenderedOperation());
        /*
         * Following operations should be no-op because the image
         * is already of appropriate type.
         */
        worker.format(TYPE_BYTE, true);
        assertSame("Expected no-op because already bytes.", image, worker.image);
        worker.setColorModelType(IndexColorModel.class);
        assertSame("Expected no-op because already indexed.", image, worker.image);
        worker.setColorSpaceType(PaletteInterpretation.RGB);
        assertSame("Expected no-op because already RGB.", image, worker.image);
        assertTrue(worker.isIndexed());
        view("new ImageWorker(INDEXED)");
        /*
         * Applies an operation that just change the color model, not the data.
         */
        worker.forceBitmaskIndexColorModel(240);
        assertNotSame(image, image = worker.image);
        assertNotSame(original.getColorModel(), image.getColorModel());
        assertSame(((DataBufferByte) original.getTile(0,0).getDataBuffer()).getData(0),
                   ((DataBufferByte)    image.getTile(0,0).getDataBuffer()).getData(0));
        view("forceBitmaskIndexColorModel(240)");
        /*
         * Now applies somes operation that does real work.
         */
        worker.setImage(original);
        worker.setColorModelType(ComponentColorModel.class);
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(1941874976L);
        view("forceComponentColorModel()");

        final RenderedImage rgb = worker.image;
        worker.setColorSpaceType(PaletteInterpretation.GRAY);
        assertNotSame(rgb, image = worker.image);
        assertChecksumEquals(2283780390L);
        view("setColorSpaceType(GRAY)");

        final RenderedImage grayscale = worker.image;
        worker.intensity();
        assertSame("Intensity on a grayscale image should be a no-op.", grayscale, worker.image);
        worker.setImage(original);
        worker.intensity();
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(3180171915L);
        view("intensity()");

        final RenderedImage intensity = worker.image;
        worker.binarize(true);
        assertNotSame(intensity, image = worker.image);
        assertChecksumEquals(312929432L);
        worker.binarize(0.25);
        assertSame("Should be already binarized.", image, worker.image);
        view("binarize()");

        final RenderedImage binarize = worker.image;
        worker.binarize(192, 64);
        assertNotSame(binarize, image = worker.image);
        assertChecksumEquals(4034897437L);
        view("binarize(192,64)");

        worker.setImage(original);
        worker.mask(binarize, new double[] {0});
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(2185221001L);
        view("mask(binarize,0)");

        worker.setImage(original);
        worker.maskBackground(new double[] {255}, new double[] {0});
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(3577749049L);
        view("maskBackground(255,0)");

        worker.setImage(original);
        worker.maskBackground(new double[] {255}, null);
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(1873283205L);
        view("maskBackground(255,null)");
    }

    /**
     * Tests on an opaque RGB image.
     */
    @Test
    public void testOnRGB() {
        viewEnabled = false; // Set to true for visualizing the images.
        loadSampleImage(SampleImage.RGB_ROTATED);
        final ImageWorker worker = new ImageWorker(image);
        /*
         * Query informations about the image.
         */
        assertSame(image, worker.getRenderedImage());
        assertSame(image, worker.getBufferedImage());
        assertEquals(3,   worker.getNumBands());
        assertEquals(-1,  worker.getTransparentPixel());
        assertFalse (     worker.isTranslucent());
        assertTrue  (     worker.isBytes());
        assertFalse (     worker.isBinary());
        assertFalse (     worker.isIndexed());
        assertEquals(PaletteInterpretation.RGB, worker.getColorSpaceType());
        assertTrue(Arrays.equals(new double[] {  0,   0,   0}, worker.getMinimums()));
        assertTrue(Arrays.equals(new double[] {255, 255, 255}, worker.getMaximums()));
        final RenderedImage original = image;
        image = worker.image; // Now a StatisticsOpImage
        assertSame(image, worker.getPlanarImage());
        assertSame(image, worker.getRenderedOperation());
        /*
         * Following operations should be no-op because the image
         * is already of appropriate type.
         */
        worker.format(TYPE_BYTE, true);
        assertSame("Expected no-op because already bytes.", image, worker.image);
        worker.setColorModelType(ComponentColorModel.class);
        assertSame("Expected no-op because already a component CM.", image, worker.image);
        worker.setColorSpaceType(PaletteInterpretation.RGB);
        assertSame("Expected no-op because already RGB.", image, worker.image);
        assertFalse(worker.isIndexed());
        view("new ImageWorker(RGB_ROTATED)");
        /*
         * Now applies somes operation that does real work.
         */
        worker.setImage(original);
        worker.setColorModelType(IndexColorModel.class);
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(2550205381L);
        view("forceIndexColorModel()");

        final RenderedImage indexed = worker.image;
        worker.setImage(original);
        worker.setColorSpaceType(PaletteInterpretation.GRAY);
        assertNotSame(indexed, image = worker.image);
        if (RUN_PLATFORM_DEPENDENT_TESTS) {
            assertChecksumEquals(163325088L);
        }
        view("setColorSpaceType(GRAY)");

        final RenderedImage grayscale = worker.image;
        worker.intensity();
        assertSame("Intensity on a grayscale image should be a no-op.", grayscale, worker.image);
        worker.setImage(original);
        worker.intensity();
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(529810612L);
        view("intensity()");

        final RenderedImage intensity = worker.image;
        worker.binarize(true);
        assertNotSame(intensity, image = worker.image);
        assertChecksumEquals(564364433L);
        worker.binarize(0.25);
        assertSame("Should be already binarized.", image, worker.image);
        view("binarize()");

        final RenderedImage binarize = worker.image;
        worker.binarize(192, 64);
        assertNotSame(binarize, image = worker.image);
        assertChecksumEquals(1507269011L);
        view("binarize(192,64)");

        worker.setImage(original);
        worker.mask(binarize, new double[] {255,128,64});
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(3974692828L);
        view("mask(binarize,orange)");

        worker.setImage(original);
        worker.maskBackground(new double[] {0,0,0}, new double[] {64,128,255});
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(346825169L);
        view("maskBackground(black,blue)");

        worker.setImage(original);
        worker.maskBackground(new double[] {0,0,0}, null);
        assertNotSame(original, image = worker.image);
        assertChecksumEquals(1508270032L);
        view("maskBackground(black,transparent)");
    }
}
