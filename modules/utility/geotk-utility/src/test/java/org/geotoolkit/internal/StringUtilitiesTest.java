/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import static org.geotoolkit.internal.StringUtilities.*;


/**
 * Tests {@link StringUtilities} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.00
 */
public final class StringUtilitiesTest {
    /**
     * Tests the {@link StringUtilities#toASCII} method.
     *
     * @since 3.18
     */
    @Test
    public void testToASCII() {
        final String metre = "metre";
        assertSame  (metre, StringUtilities.toASCII(metre));
        assertEquals(metre, StringUtilities.toASCII("mètre").toString());
    }

    /**
     * Tests the {@link StringUtilities#removeLF} method.
     */
    @Test
    public void testRemoveLF() {
        final StringBuilder buffer = new StringBuilder(" \nOne,\nTwo, \n Three Four\nFive \nSix \n");
        removeLF(buffer);
        assertEquals("One,Two,Three Four Five Six", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#token} method.
     */
    @Test
    public void testToken() {
        assertEquals("Id4", token("..Id4  56B..", 2));
        assertEquals("56",  token("..Id4  56B..", 6));
    }
}
