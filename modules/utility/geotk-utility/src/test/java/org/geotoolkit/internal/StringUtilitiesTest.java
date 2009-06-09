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
package org.geotoolkit.internal;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link StringUtilities} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class StringUtilitiesTest {
    /**
     * Tests the {@link StringUtilities#replace} method.
     */
    @Test
    public void testReplace() {
        final StringBuilder buffer = new StringBuilder("One two three two one");
        StringUtilities.replace(buffer, "two", "zero");
        assertEquals("One zero three zero one", buffer.toString());
        StringUtilities.replace(buffer, "zero", "ten");
        assertEquals("One ten three ten one", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#removeLF} method.
     */
    @Test
    public void testRemoveLF() {
        final StringBuilder buffer = new StringBuilder(" \nOne,\nTwo, \n Three Four\nFive \nSix \n");
        StringUtilities.removeLF(buffer);
        assertEquals("One,Two,Three Four Five Six", buffer.toString());
    }

    /**
     * Tests the {@link StringUtilities#splitLines} method.
     */
    @Test
    public void testSplitLines() {
        final String[] splitted = StringUtilities.splitLines("\nOne\r\nTwo\rThree\rFour\nFive\n\rSix\n");
        assertArrayEquals(new String[] {
            "",
            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "",
            "Six",
            ""
        }, splitted);
    }
}
