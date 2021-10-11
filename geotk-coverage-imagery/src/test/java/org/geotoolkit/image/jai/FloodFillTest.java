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
package org.geotoolkit.image.jai;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.TiledImage;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.registry.RenderedRegistryMode;

import org.geotoolkit.image.SampleImage;
import org.geotoolkit.image.SampleImageTestBase;
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
public final strictfp class FloodFillTest extends SampleImageTestBase {
    /**
     * Creates a new test case.
     */
    public FloodFillTest() {
        super(FloodFill.class);
    }

    /**
     * Ensures that the JAI registration has been done.
     *
     * @todo The "Flood fill" operation is not yet a registered JAI operation.
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
     * Tests the {@linl FloodFill#fill} static method on a contour.
     */
    @Test
    public void dummy() {
        WritableRenderedImage image;
        loadSampleImage(SampleImage.CONTOUR);
        this.image = image = copyCurrentImage();
        final Point[] points = new Point[] {
            new Point( 50,  30),  // Québec
            new Point(120, 300),  // Maritime
            new Point(270, 360),  // Prince-Edward Island
            new Point(300, 100),  // Anticosti Island
            new Point(650, 200)   // New-Found land
        };
        FloodFill.fill(image, new double[][] {{2}}, new double[] {1}, points);
        assertCurrentChecksumEquals("fill(CONTOUR - untiled)", 2609270527L);
        showCurrentImage("fill(CONTOUR - untiled)");
        /*
         * Tests again the same filling, but on a tiled image. The visual result should be idential
         * but the expected checksum is different because checksum computation is sensitive to tile
         * layout.
         */
        loadSampleImage(SampleImage.CONTOUR);
        this.image = image = new TiledImage(this.image, 50, 50);
        FloodFill.fill(image, new double[][] {{2}}, new double[] {1}, points);
        assertCurrentChecksumEquals("fill(CONTOUR - tiled)", 3271811962L);
        showCurrentImage("fill(CONTOUR - tiled)");
    }

    /**
     * Tests the {@linl FloodFill#fill} static method on an indexed image.
     */
    @Test
    public void testIndexed() {
        WritableRenderedImage image;
        loadSampleImage(SampleImage.INDEXED);
        this.image = image = copyCurrentImage();
        assertCurrentChecksumEquals("copy", 1873283205L);
        /*
         * Replaces the color of Madagascar island (index 240 at location (125,220)) and its border
         * (black: index 0) by a white color (index 255). Do the same for the continent (starting
         * from the point at location (0,0)).
         */
        FloodFill.fill(image, new double[][] {{240}, {0}}, new double[] {255},
                new Point(125, 220), // Madagascar
                new Point(0, 0));    // Africa
        assertCurrentChecksumEquals("fill(INDEXED - untiled)", 649828117L);
        showCurrentImage("fill(INDEXED - untiled)");
    }

    /**
     * Tests the {@linl FloodFill#fill} static method on a RGB image.
     */
    @Test
    public void testRGB() {
        WritableRenderedImage image;
        loadSampleImage(SampleImage.RGB_ROTATED);
        this.image = image = copyCurrentImage();
        assertCurrentChecksumEquals(null, 3650654124L, 4050219331L);
        /*
         * Replaces the black color of the upper-left corner.
         */
        FloodFill.fill(image, new Color[] {Color.BLACK}, Color.BLUE, new Point(0, 0));
        assertCurrentChecksumEquals(null, 2215625664L, 1196099012L);
        /*
         * Do the same for the other corner.
         */
        FloodFill.fill(image, new Color[] {Color.BLACK}, Color.CYAN, new Point(259, 299));
        assertCurrentChecksumEquals("fill(RGB - untiled)", 3983761906L, 2415208678L);
        showCurrentImage("fill(RGB - untiled)");
        /*
         * Same test on a tiles image.
         */
        loadSampleImage(SampleImage.RGB_ROTATED);
        this.image = image = new TiledImage(this.image, 50, 50);
        FloodFill.fill(image, new Color[] {Color.BLACK}, Color.BLUE, new Point(0, 0), new Point(259, 299));
        assertCurrentChecksumEquals("fill(CONTOUR - tiled)", 1202618797L, 2266942802L);
        showCurrentImage("fill(CONTOUR - tiled)");
    }
}
