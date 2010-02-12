/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.util;

import static org.geotoolkit.util.Characters.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Characters} utility methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.00
 */
public final class CharactersTest {
    /**
     * Tests {@link Characters#toSuperScript}.
     */
    @Test
    public void testSuperscript() {
        for (char c='0'; c<='9'; c++) {
            final char s = toSuperScript(c);
            assertFalse(s == c);
            assertFalse(isSuperScript(c));
            assertTrue (isSuperScript(s));
            assertEquals(c, toNormalScript(s));
        }
        final char c = 'A';
        assertEquals(c, toSuperScript(c));
        assertEquals(c, toNormalScript(c));
        assertFalse(isSuperScript(c));
    }

    /**
     * Tests {@link Characters#toSubScript}.
     */
    @Test
    public void testSubscript() {
        for (char c='0'; c<='9'; c++) {
            final char s = toSubScript(c);
            assertFalse(s == c);
            assertFalse(isSubScript(c));
            assertTrue (isSubScript(s));
            assertEquals(c, toNormalScript(s));
        }
        final char c = 'a';
        assertEquals(c, toSubScript(c));
        assertEquals(c, toNormalScript(c));
        assertFalse(isSubScript(c));
    }

    /**
     * Tests {@link Characters#matchingBracket}.
     *
     * @since 3.09
     */
    @Test
    public void testMatchingBracket() {
        assertEquals('[', matchingBracket(']'));
        assertEquals('}', matchingBracket('{'));
        assertEquals('X', matchingBracket('X'));
    }
}
