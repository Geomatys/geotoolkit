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
package org.geotoolkit.internal;

import java.util.Locale;
import java.text.NumberFormat;

import org.junit.*;
import static org.junit.Assert.*;

import static org.geotoolkit.internal.InternalUtilities.*;


/**
 * Tests {@link InternalUtilities} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.18 (derived from 3.00)
 */
public final strictfp class InternalUtilitiesTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests the {@link InternalUtilities#getSeparator} method.
     */
    @Test
    public void testGetSeparator() {
        assertEquals(';', getSeparator(NumberFormat.getInstance(Locale.FRANCE)));
    }

    /**
     * Tests the {@link InternalUtilities#convert10} method.
     */
    @Test
    public void testConvert10() {
        final double converted = InternalUtilities.convert10(99.99f);
        assertFalse (99.99f == 99.99);
        assertEquals("99.98999786376953", Double.toString(99.99f));
        assertEquals("99.99", Double.toString(converted));
        assertFalse (99.99f == converted);
        assertTrue  (99.99f == (float) converted);
    }

    /**
     * Tests the {@link InternalUtilities#parseColor(String)} method.
     *
     * @since 3.19
     */
    @Test
    public void testParseColor() {
        assertEquals("#23456789", 0x23456789, parseColor("#23456789"));
        assertEquals("#456789",   0xFF456789, parseColor("#456789"));
        assertEquals("#A0BC",     0xAA00BBCC, parseColor("#A0BC"));
        assertEquals("#0BC",      0xFF00BBCC, parseColor("#0BC"));
        assertEquals("#D2787034", 0xD2787034, parseColor("#D2787034"));
        try {
            parseColor("#1D2787034");
            fail("Should not accept values greater than 32 bits.");
        } catch (NumberFormatException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("#1D2787034"));
        }
    }
}
