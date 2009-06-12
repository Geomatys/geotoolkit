/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image.jai;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.image.SampleImage;
import org.geotoolkit.image.ImageTestCase;
import org.geotoolkit.internal.image.jai.SilhouetteMaskDescriptor;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link FloodFill}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 */
public class FloodFillTest extends ImageTestCase {
    /**
     * Creates a new test case.
     */
    public FloodFillTest() {
        super(FloodFill.class);
    }

    /**
     * Ensures that the JAI registration has been done.
     */
    @Test
    @Ignore
    public void testRegistration() {
        final RegistryElementDescriptor descriptor = JAI.getDefaultInstance().getOperationRegistry()
                .getDescriptor(RenderedRegistryMode.MODE_NAME, FloodFill.OPERATION_NAME);
        assertNotNull("Descriptor not found.", descriptor);
        assertTrue(descriptor instanceof SilhouetteMaskDescriptor);
    }

    /**
     * Tests the {@linl FloodFill#fill} static method on an indexed image.
     */
    @Test
    public void testIndexed() {
        loadSampleImage(SampleImage.INDEXED);
        final BufferedImage image = copyImage();
        this.image = image;
        assertChecksumEquals(1873283205L);
        /*
         * Replaces the color of Madagascar island (index 240) and its border (black: index 0)
         * by a white color (index 255).
         */
        FloodFill.fill(image, new double[][] {{240}, {0}}, new double[] {255}, new Point(125, 220));
        assertChecksumEquals(2607717604L);
        /*
         * Do the same for the continent.
         */
        FloodFill.fill(image, new double[][] {{240}, {0}}, new double[] {255}, new Point(0, 0));
        assertChecksumEquals(649828117L);
        view("fill");
    }

    /**
     * Tests the {@linl FloodFill#fill} static method on a RGB image.
     */
    @Test
    public void testRGB() {
        loadSampleImage(SampleImage.RGB_ROTATED);
        final BufferedImage image = copyImage();
        this.image = image;
        assertChecksumEquals(3650654124L);
        /*
         * Replaces the black color of the upper-left corner.
         */
        FloodFill.fill(image, new Color[] {Color.BLACK}, Color.BLUE, new Point(0, 0));
        assertChecksumEquals(2215625664L);
        /*
         * Do the same for the other corner.
         */
        FloodFill.fill(image, new Color[] {Color.BLACK}, Color.CYAN, new Point(259, 299));
        assertChecksumEquals(3983761906L);
        view("fill");
    }
}
