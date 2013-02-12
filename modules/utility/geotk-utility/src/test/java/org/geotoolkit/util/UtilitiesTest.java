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
package org.geotoolkit.util;

import org.apache.sis.util.ComparisonMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Utilities} static methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.5
 */
public final strictfp class UtilitiesTest {
    /**
     * Tests {@link Utilities#deepEquals(Object, Object, ComparisonMode)}.
     *
     * @since 3.18
     */
    @Test
    public void testDeepEquals() {
        testDeepEquals(null, true);
        testDeepEquals(null, false);

        testDeepEquals(ComparisonMode.STRICT, true);
        testDeepEquals(ComparisonMode.STRICT, false);
    }

    /**
     * Tests {@link Utilities#deepEquals(Object, Object, ComparisonMode)} using the given
     * comparison mode with the given collections.
     */
    private static void testDeepEquals(final ComparisonMode mode, final boolean orderIsSignificant) {
        final DummyLenient e1 = new DummyLenient("Janvier", mode);
        final DummyLenient e2 = new DummyLenient("Juin",    mode);
        final DummyLenient e3 = new DummyLenient("Janvier", mode);
        final DummyLenient e4 = new DummyLenient("Juin",    mode);
        assertTrue (Utilities.deepEquals(e1, e1, mode));
        assertFalse(Utilities.deepEquals(e1, e2, mode));
        assertTrue (Utilities.deepEquals(e1, e3, mode));
        assertFalse(Utilities.deepEquals(e1, e4, mode));
        assertFalse(Utilities.deepEquals(e2, e3, mode));
        assertTrue (Utilities.deepEquals(e2, e4, mode));
        assertFalse(Utilities.deepEquals(e3, e4, mode));

        final Collection<DummyLenient> c1, c2;
        if (orderIsSignificant) {
            c1 = new ArrayList<>();
            c2 = new ArrayList<>();
        } else {
            c1 = new LinkedHashSet<>();
            c2 = new LinkedHashSet<>();
        }
        assertTrue(c1.add(e1)); assertTrue(c1.add(e2));
        assertTrue(c2.add(e3)); assertTrue(c2.add(e4));
        assertTrue(Utilities.deepEquals(c1, c2, mode));
        assertTrue(c2.remove(e3));
        assertFalse(Utilities.deepEquals(c1, c2, mode));
        assertTrue(c2.add(e3));
        assertEquals(!orderIsSignificant, Utilities.deepEquals(c1, c2, mode));

        assertTrue(e1.comparisonCount != 0);
        assertTrue(e2.comparisonCount != 0);
        assertTrue(e3.comparisonCount != 0);
    }

    /**
     * For {@link #testDeepEquals()} purpose only.
     */
    private static final strictfp class DummyLenient implements LenientComparable {
        /** Label to be used in comparison. */
        private final String label;

        /** The expected comparison mode. */
        private final ComparisonMode expected;

        /** Number of comparison performed. */
        int comparisonCount;

        /** Creates a new instance expecting the given comparison mode. */
        DummyLenient(final String label, final ComparisonMode expected) {
            this.label = label;
            this.expected = expected;
        }

        /** Compares this object with the given one. */
        @Override public boolean equals(final Object other, final ComparisonMode mode) {
            assertEquals(label, expected, mode);
            comparisonCount++;
            return equals(other);
        }

        /** Compares this dummy object with the given object. */
        @Override public boolean equals(final Object other) {
            return (other instanceof DummyLenient) && label.equals(((DummyLenient) other).label);
        }

        /** For consistency with {@link #equals(Object)}. */
        @Override public int hashCode() {
            return label.hashCode();
        }

        /** For debugging purpose only. */
        @Override public String toString() {
            return label;
        }
    }
}
