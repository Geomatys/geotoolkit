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
 * @version 3.17
 *
 * @since 3.00
 */
public final strictfp class ImageWorkerTest extends SampleImageTestBase {
    /**
     * Creates a new test case.
     */
    public ImageWorkerTest() {
        super(ImageWorker.class);
    }

    /**
     * To ensure strict reproducibility of the test suite across different platforms,
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
    @Ignore("Fails randomly, need investigation.")
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
        showCurrentImage("new ImageWorker(INDEXED)");
        /*
         * Applies an operation that just change the color model, not the data.
         */
        worker.forceBitmaskIndexColorModel(240);
        assertNotSame(image, image = worker.image);
        assertNotSame(original.getColorModel(), image.getColorModel());
        assertSame(((DataBufferByte) original.getTile(0,0).getDataBuffer()).getData(0),
                   ((DataBufferByte)    image.getTile(0,0).getDataBuffer()).getData(0));
        showCurrentImage("forceBitmaskIndexColorModel(240)");
        /*
         * Now applies somes operation that does real work.
         */
        worker.setImage(original);
        worker.setColorModelType(ComponentColorModel.class);
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("forceComponentColorModel()", 1941874976L);
        showCurrentImage("forceComponentColorModel()");

        final RenderedImage rgb = worker.image;
        worker.setColorSpaceType(PaletteInterpretation.GRAY);
        assertNotSame(rgb, image = worker.image);
        assertCurrentChecksumEquals("setColorSpaceType(GRAY)", 2283780390L);
        showCurrentImage("setColorSpaceType(GRAY)");

        final RenderedImage grayscale = worker.image;
        worker.intensity();
        assertSame("Intensity on a grayscale image should be a no-op.", grayscale, worker.image);
        worker.setImage(original);
        worker.intensity();
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("intensity()", 3180171915L);
        showCurrentImage("intensity()");

        final RenderedImage intensity = worker.image;
        worker.binarize(true);
        assertNotSame(intensity, image = worker.image);
        assertCurrentChecksumEquals(null, 312929432L, 1768844228L);
        worker.binarize(0.25);
        assertSame("Should be already binarized.", image, worker.image);
        showCurrentImage("binarize()");

        final RenderedImage binarize = worker.image;
        worker.binarize(192, 64);
        assertNotSame(binarize, image = worker.image);
        assertCurrentChecksumEquals("binarize(192,64)", 4034897437L, 2284770832L);
        showCurrentImage("binarize(192,64)");

        worker.setImage(original);
        worker.mask(binarize, new double[] {0});
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("mask(binarize,0)", 2185221001L, 523688203L);
        showCurrentImage("mask(binarize,0)");

        worker.setImage(original);
        worker.maskBackground(new double[][] {{255}}, new double[] {0});
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("maskBackground(255,0)", 3577749049L);
        showCurrentImage("maskBackground(255,0)");

        worker.setImage(original);
        worker.maskBackground(new double[][] {{255}}, null);
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("maskBackground(255,null)", 1873283205L, 2745681538L);
        showCurrentImage("maskBackground(255,null)");
    }

    /**
     * Tests on an opaque RGB image.
     */
    @Test
    @Ignore("Fails randomly, need investigation.")
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
        showCurrentImage("new ImageWorker(RGB_ROTATED)");
        /*
         * Now applies somes operation that does real work.
         */
        worker.setImage(original);
        worker.setColorModelType(IndexColorModel.class);
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("forceIndexColorModel()", 2550205381L);
        showCurrentImage("forceIndexColorModel()");

        final RenderedImage indexed = worker.image;
        worker.setImage(original);
        worker.setColorSpaceType(PaletteInterpretation.GRAY);
        assertNotSame(indexed, image = worker.image);
        assertCurrentChecksumEquals("setColorSpaceType(GRAY)", 163325088L, 3717990294L);
        showCurrentImage("setColorSpaceType(GRAY)");

        final RenderedImage grayscale = worker.image;
        worker.intensity();
        assertSame("Intensity on a grayscale image should be a no-op.", grayscale, worker.image);
        worker.setImage(original);
        worker.intensity();
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("intensity()", 529810612L);
        showCurrentImage("intensity()");

        final RenderedImage intensity = worker.image;
        worker.binarize(true);
        assertNotSame(intensity, image = worker.image);
        assertCurrentChecksumEquals(null, 564364433L, 3551509129L);
        worker.binarize(0.25);
        assertSame("Should be already binarized.", image, worker.image);
        showCurrentImage("binarize()");

        final RenderedImage binarize = worker.image;
        worker.binarize(192, 64);
        assertNotSame(binarize, image = worker.image);
        assertCurrentChecksumEquals("binarize(192,64)", 1507269011L, 1828969399L);
        showCurrentImage("binarize(192,64)");

        worker.setImage(original);
        worker.mask(binarize, new double[] {255,128,64});
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("mask(binarize,orange)", 3974692828L, 3148611825L, 328489765L);
        showCurrentImage("mask(binarize,orange)");

        worker.setImage(original);
        worker.maskBackground(new double[][] {{0,0,0}}, new double[] {64,128,255});
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("maskBackground(black,blue)", 346825169L, 1447818957L);
        showCurrentImage("maskBackground(black,blue)");

        worker.setImage(original);
        worker.maskBackground(new double[][] {{0,0,0}}, null);
        assertNotSame(original, image = worker.image);
        assertCurrentChecksumEquals("maskBackground(black,transparent)", 1508270032L);
        showCurrentImage("maskBackground(black,transparent)");
    }
}
