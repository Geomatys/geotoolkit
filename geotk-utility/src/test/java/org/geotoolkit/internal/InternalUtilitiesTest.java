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


import org.junit.*;
import static org.junit.Assert.*;

import static org.geotoolkit.internal.InternalUtilities.*;


/**
 * Tests {@link InternalUtilities} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class InternalUtilitiesTest {
    /**
     * Tests the {@link InternalUtilities#parseColor(String)} method.
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
