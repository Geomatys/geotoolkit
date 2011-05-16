/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.opengis.metadata.quality.Result;
import org.opengis.metadata.quality.ConformanceResult;

import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.quality.AbstractPositionalAccuracy;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Tests {@link Citations} and related constants.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.2
 */
public final class CitationsTest {
    /**
     * Tests the {@link AbstractMetadata#toString()} method first, since debugging
     * will relying a lot on this method for the remaining of the test suite.
     */
    @Test
    public void testToString() {
        final String text = Citations.EPSG.toString();
        /*
         * Reminder: (?s) allows .* to skip new line characters.
         *           (?m) enable the multi-lines mode for ^ and $.
         *           ^ and $ match the beginning and end of a line respectively.
         */
        assertTrue(text.matches("(?s)(?m).*^\\s+Identifiers:\\s+Code:\\s+EPSG$.*"));
        assertTrue(text.matches("(?s)(?m).*^\\s+Linkage:\\s+http://www.epsg.org$.*"));
    }

    /**
     * Makes sure that {@link Citations} constants are immutables.
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
     * Tests {@link AbstractPositionalAccuracy} constants.
     */
    @Test
    public void testPositionalAccuracy() {
        assertEquals("Identity comparison",
                     AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED,
                     AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED);

        assertEquals("Identity comparison",
                     AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED,
                     AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED);

        assertNotSame(AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED,
                      AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED);

        final Collection<? extends Result> appliedResults = AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED.getResults();
        final Collection<? extends Result> omittedResults = AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED.getResults();
        final ConformanceResult applied = (ConformanceResult) appliedResults.iterator().next();
        final ConformanceResult omitted = (ConformanceResult) omittedResults.iterator().next();
        assertNotSame(applied, omitted);
        assertTrue (applied.pass());
        assertFalse(omitted.pass());
        assertFalse(applied.equals(omitted));
        assertFalse(appliedResults.equals(omittedResults));
        assertFalse(AbstractPositionalAccuracy.DATUM_SHIFT_APPLIED.equals(
                    AbstractPositionalAccuracy.DATUM_SHIFT_OMITTED));
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
            assertSame("Deserialization shall give the singleton.", constant, serialize(constant));
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
        assertFalse(copy.equals(Citations.EPSG));
        assertFalse(copy.equals(Citations.EPSG, ComparisonMode.STRICT));
        assertTrue (copy.equals(Citations.EPSG, ComparisonMode.BY_CONTRACT));
        assertTrue (copy.equals(Citations.EPSG, ComparisonMode.IGNORE_METADATA));
    }
}
