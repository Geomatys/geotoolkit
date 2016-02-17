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
package org.geotoolkit.util;

import java.util.Arrays;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests {@link Strings} methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @since 3.09 (derived from 3.00).
 */
public final strictfp class StringsTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests {@link Strings#isJavaIdentifier}.
     */
    @Test
    public void testIsJavaIdentifier() {
        assertTrue (Strings.isJavaIdentifier("T1"));
        assertFalse(Strings.isJavaIdentifier("1T"));
    }

    /**
     * Tests the {@link Strings#toString(Iterable, String)} method.
     */
    @Test
    public void testToString() {
        assertEquals("4, 8, 12, 9", Strings.toString(Arrays.asList(4, 8, 12, 9), ", "));
        assertSame  ("singleton",   Strings.toString(Arrays.asList("singleton"), ", "));
        assertNull  (               Strings.toString(null, ", "));
    }
}
