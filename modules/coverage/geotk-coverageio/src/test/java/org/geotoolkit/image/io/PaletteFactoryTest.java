/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io;

import java.util.Set;
import java.io.IOException;
import java.awt.image.IndexColorModel;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link PaletteFactory} and a bit of {@link Palette}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.05
 *
 * @since 2.4
 */
public final class PaletteFactoryTest {
    /**
     * Tests the argument check performed by constructor.
     */
    @Test
    public void testConstructor() {
        final PaletteFactory factory = PaletteFactory.getDefault();
        assertEquals(100, new IndexedPalette(factory, "grayscale",    0, 100, 100, 1, 0).upper);
        assertEquals( 50, new IndexedPalette(factory, "grayscale", -100,  50, 100, 1, 0).upper);
        try {
            new IndexedPalette(factory, "grayscale", 0, 100, -100, 1, 0);
            fail("Should not accept negative size.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
        try {
            new IndexedPalette(factory, "grayscale", 100, 50, 256, 1, 0);
            fail("Should not accept invalid range.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
        assertEquals(40000, new IndexedPalette(factory, "grayscale", 1,  40000, 0xFFFF, 1, 0).upper);
        try {
            new IndexedPalette(factory, "grayscale", -1,  40000, 0xFFFF, 1, 0);
            fail("Should not accept value out of range.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
        try {
            new IndexedPalette(factory, "grayscale", 1,  70000, 0xFFFF, 1, 0);
            fail("Should not accept value out of range.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
        try {
            new IndexedPalette(factory, "grayscale", -40000,  0, 0xFFFF, 1, 0);
            fail("Should not accept value out of range.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
        }
    }

    /**
     * Tests {@link PaletteFactory#getAvailableNames}.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testAvailableNames() throws IOException {
        final PaletteFactory factory = PaletteFactory.getDefault();
        final Set<String> names = factory.getAvailableNames();
        assertNotNull(names);
        assertFalse(names.isEmpty());
        assertTrue ("Part of Geotk distribution", names.contains("rainbow"));
        assertTrue ("Part of Geotk distribution", names.contains("grayscale"));
        assertTrue ("Part of Geotk distribution", names.contains("bell"));
        assertFalse("Non-existant",               names.contains("Donald Duck"));
        assertTrue ("Defined in MyPalettes",      names.contains("green-blue"));
        /*
         * Ensures that every palettes exist.
         */
        for (final String name : names) {
            assertTrue(name, factory.getPalette(name, 16).getColorModel() instanceof IndexColorModel);
        }
    }

    /**
     * Tests the cache.
     */
    @Test
    public void testCache() {
        final PaletteFactory factory = PaletteFactory.getDefault();
        final Palette first  = factory.getPalettePadValueFirst("rainbow", 100);
        final Palette second = factory.getPalettePadValueFirst("bell",    100);
        final Palette third  = factory.getPalettePadValueFirst("rainbow", 100);
        assertEquals (first, third);
        assertSame   (first, third);
        assertNotSame(first, second);
    }

    /**
     * Tests the color model.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testColorModel() throws IOException {
        final PaletteFactory  factory = PaletteFactory.getDefault();
        final Palette         palette = factory.getPalettePadValueFirst("rainbow", 100);
        final IndexColorModel icm     = (IndexColorModel) palette.getColorModel();
        assertEquals(100, icm.getMapSize());
        assertEquals(0, icm.getTransparentPixel());
        /*
         * Tests the color values.
         */
        assertEquals("R", 124, icm.getRed  ( 1));
        assertEquals("G", 000, icm.getGreen( 1));
        assertEquals("B", 255, icm.getBlue ( 1));
        assertEquals("R", 255, icm.getRed  (99));
        assertEquals("G", 005, icm.getGreen(99));
        assertEquals("B", 000, icm.getBlue (99));
    }
}
