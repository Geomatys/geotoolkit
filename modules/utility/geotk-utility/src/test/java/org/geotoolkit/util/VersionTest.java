/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link Version} class, especially the {@code compareTo} method.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
public final strictfp class VersionTest {
    /**
     * Tests a numeric-only version.
     */
    @Test
    public void testNumeric() {
        final Version version = new Version("6.11.2");
        assertEquals("6.11.2", version.toString());
        assertEquals( 6, version.getMajor());
        assertEquals(11, version.getMinor());
        assertEquals( 2, version.getRevision());

        assertTrue(version.compareTo(new Version("6.11.2")) == 0);
        assertTrue(version.compareTo(new Version("6.8"   )) >  0);
        assertTrue(version.compareTo(new Version("6.12.0")) <  0);
        assertTrue(version.compareTo(new Version("6.11"  )) >  0);
    }

    /**
     * Tests a alpha-numeric version.
     */
    @Test
    public void testAlphaNumeric() {
        final Version version = new Version("1.6.b2");
        assertEquals("1.6.b2", version.toString());
        assertEquals( 1, version.getMajor());
        assertEquals( 6, version.getMinor());
        assertEquals("b2", version.getRevision());

        assertTrue(version.compareTo(new Version("1.6.b2")) == 0);
        assertTrue(version.compareTo(new Version("1.6.b1"))  > 0);
        assertTrue(version.compareTo(new Version("1.07.b1")) < 0);
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        final Version version = new Version("1.6.b2");
        assertNotSame(version, assertSerializable(version));
    }
}
