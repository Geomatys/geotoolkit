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
package org.geotoolkit.resources;

import java.util.Locale;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Locales} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
public strictfp class LocalesTest {
    /**
     * Tests the {@link Locales#parse} method.
     */
    @Test
    public void testParse() {
        assertSame(Locale.FRENCH,        Locales.parse("fr"));
        assertSame(Locale.FRENCH,        Locales.parse("fra"));
        assertSame(Locale.CANADA_FRENCH, Locales.parse("fr_CA"));
        assertSame(Locale.CANADA_FRENCH, Locales.parse("fra_CA"));
        assertSame(Locale.CANADA_FRENCH, Locales.parse("fr_CAN"));
        assertSame(Locale.CANADA_FRENCH, Locales.parse("fra_CAN"));
        assertSame(Locale.ENGLISH,       Locales.parse("en"));

        assertEquals(new Locale("de", "DE"),        Locales.parse("de_DE"));
        assertEquals(new Locale("",   "GB"),        Locales.parse("_GB"));
        assertEquals(new Locale("en", "US", "WIN"), Locales.parse("en_US_WIN"));
        assertEquals(new Locale("de", "", "POSIX"), Locales.parse("de__POSIX"));
        assertEquals(new Locale("fr", "", "MAC"),   Locales.parse("fr__MAC"));
    }
}
