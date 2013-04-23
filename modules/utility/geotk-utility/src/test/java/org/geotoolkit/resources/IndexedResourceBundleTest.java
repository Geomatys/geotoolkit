/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.util.InternationalString;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link IndexedResourceBundle} subclasses, especially {@link Vocabulary}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.2
 */
public final strictfp class IndexedResourceBundleTest {
    /**
     * Tests some error message.
     */
    @Test
    public void testErrors() {
        Errors resources =       Errors.getResources(Locale.ENGLISH);
        assertNotSame(resources, Errors.getResources(Locale.FRENCH));
        assertNotSame(resources, Errors.getResources(Locale.GERMAN));
        String s = resources.getString(Errors.Keys.IN_1);
        assertTrue(s, s.startsWith("Error"));

        resources = Errors.getResources(Locale.FRENCH);
        assertNotSame(resources, Errors.getResources(Locale.CANADA));
        s = resources.getString(Errors.Keys.IN_1);
        assertTrue(s, s.startsWith("Erreur"));
    }

    /**
     * Tests some simple vocabulary words.
     */
    @Test
    public void testVocabulary() {
        Vocabulary resources;

        resources = Vocabulary.getResources(Locale.ENGLISH);
        assertEquals("North", resources.getString(Vocabulary.Keys.NORTH));

        resources = Vocabulary.getResources(Locale.FRENCH);
        assertEquals("Nord", resources.getString(Vocabulary.Keys.NORTH));
    }

    /**
     * Tests the formatting of an international string.
     */
    @Test
    public void testInternationalString() {
        InternationalString i18n = Vocabulary.formatInternational(Vocabulary.Keys.SOUTH);
        assertEquals("South", i18n.toString(Locale.ENGLISH));
        assertEquals("Sud",   i18n.toString(Locale.FRANCE));
        assertEquals("Sud",   i18n.toString(Locale.CANADA_FRENCH));
        assertEquals("South", i18n.toString(null));
    }
}
