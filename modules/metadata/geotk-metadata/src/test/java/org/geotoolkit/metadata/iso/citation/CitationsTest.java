/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.iso.citation;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.geotoolkit.test.Depend;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.metadata.UnmodifiableMetadataException;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link Citations} constants.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.2
 */
@Depend(DefaultCitationTest.class)
public final strictfp class CitationsTest {
    /**
     * Makes sure that {@link Citations} constants are immutable.
     */
    @Test
    public void testCitation() {
        assertEquals ("Identity comparison", Citations.EPSG, Citations.EPSG);
        assertNotSame(Citations.EPSG, Citations.OGC);
        assertTrue(Citations.EPSG instanceof DefaultCitation);
        try {
            ((DefaultCitation) Citations.EPSG).setISBN("Dummy");
            fail("Pre-defined metadata should be unmodifiable.");
        } catch (UnmodifiableMetadataException e) {
            // This is the expected exception.
        }
        try {
            Citations.EPSG.getIdentifiers().add(null);
            fail("Pre-defined metadata should be unmodifiable.");
        } catch (UnsupportedOperationException e) {
            // This is the expected exception.
        }
        assertSame("Empty attributes of an unmodifiable metadata should be " +
                "set to the Collections.EMPTY_SET or EMPTY_LIST constant.",
                Collections.EMPTY_LIST, Citations.EPSG.getDates());
    }

    /**
     * Tests the serialization of all {@link Citations} constants.
     *
     * @throws IllegalAccessException Should never happen.
     */
    @Test
    public void testSerialization() throws IllegalAccessException {
        final Set<Object> constants = new HashSet<Object>();
        for (final Field field : Citations.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertTrue(constants.add(field.get(null)));
            }
        }
        assertFalse(constants.isEmpty());
        for (final Object constant : constants) {
            assertSame("Deserialization shall give the singleton.", constant, assertSerializable(constant));
        }
    }

    /**
     * Ensures that citations are comparable even if they are not the same class.
     *
     * @see <a href="http://jira.geotoolkit.org/browse/GEOTK-48">GEOTK-48</a>
     *
     * @since 3.18
     */
    @Test
    public void testEquals() {
        assertEquals(Citations.EPSG, Citations.EPSG);
        assertFalse (Citations.EPSG.equals(Citations.OGC));
        final DefaultCitation copy = new DefaultCitation(Citations.EPSG);
        assertTrue(copy.equals(Citations.EPSG));
        assertTrue(copy.equals(Citations.EPSG, ComparisonMode.STRICT));
        assertTrue(copy.equals(Citations.EPSG, ComparisonMode.BY_CONTRACT));
        assertTrue(copy.equals(Citations.EPSG, ComparisonMode.IGNORE_METADATA));
        assertEquals(Citations.EPSG.hashCode(), copy.hashCode());
    }
}
