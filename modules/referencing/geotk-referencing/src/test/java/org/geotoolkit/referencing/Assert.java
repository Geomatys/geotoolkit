/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.Objects;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.io.wkt.Convention;
import org.geotoolkit.io.wkt.WKTFormat;
import org.geotoolkit.io.wkt.FormattableObject;
import org.apache.sis.geometry.AbstractEnvelope;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.operation.transform.LinearTransform;

import static java.lang.StrictMath.*;
import static org.geotoolkit.test.Commons.*;


/**
 * Inherits JUnit assertions methods, and adds Geotk-specific assertion methods. The methods
 * defined in this class requires Geotk-specific API (otherwise they would be defined in the
 * {@code geotk-test} module).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.19 (derived from 3.00)
 */
public strictfp final class Assert extends org.geotoolkit.test.Assert {
    /**
     * Small tolerance for comparisons of floating point values.
     */
    private static final double EPS = 1E-7;

    /**
     * Do not allow instantiation of this class.
     */
    private Assert() {
    }

    /**
     * Asserts that the two given objects are not equal.
     *
     * @param o1  The first object.
     * @param o2  The second object.
     *
     * @since 3.20
     */
    public static void assertNotDeepEquals(final Object o1, final Object o2) {
        assertNotSame("same", o1, o2);
        assertFalse("equals",                      Objects  .equals    (o1, o2));
        assertFalse("deepEquals",                  Objects  .deepEquals(o1, o2));
        assertFalse("deepEquals(STRICT)",          Utilities.deepEquals(o1, o2, ComparisonMode.STRICT));
        assertFalse("deepEquals(IGNORE_METADATA)", Utilities.deepEquals(o1, o2, ComparisonMode.IGNORE_METADATA));
        assertFalse("deepEquals(APPROXIMATIVE)",   Utilities.deepEquals(o1, o2, ComparisonMode.APPROXIMATIVE));
    }

    /**
     * Asserts that the two given objects are approximatively equal.
     * See {@link ComparisonMode#APPROXIMATIVE} for more information.
     *
     * @param expected  The expected object.
     * @param actual    The actual object.
     * @param slightlyDifferent {@code true} if the objects should also be slightly different.
     *
     * @since 3.20
     */
    public static void assertEqualsApproximatively(final Object expected, final Object actual, final boolean slightlyDifferent) {
        assertTrue("Should be approximatively equals",      Utilities.deepEquals(expected, actual, ComparisonMode.DEBUG));
        assertTrue("DEBUG inconsistent with APPROXIMATIVE", Utilities.deepEquals(expected, actual, ComparisonMode.APPROXIMATIVE));
        if (slightlyDifferent) {
            assertFalse("Should be slightly different",     Utilities.deepEquals(expected, actual, ComparisonMode.IGNORE_METADATA));
            assertFalse("Should not be strictly equals",    Utilities.deepEquals(expected, actual, ComparisonMode.STRICT));
        }
    }

    /**
     * Asserts that the two given objects are equal ignoring metadata.
     * See {@link ComparisonMode#IGNORE_METADATA} for more information.
     *
     * @param expected  The expected object.
     * @param actual    The actual object.
     * @param strictlyDifferent {@code true} if the objects should not be strictly equal.
     *
     * @since 3.20
     */
    public static void assertEqualsIgnoreMetadata(final Object expected, final Object actual, final boolean strictlyDifferent) {
        assertTrue("Should be approximatively equals",      Utilities.deepEquals(expected, actual, ComparisonMode.DEBUG));
        assertTrue("DEBUG inconsistent with APPROXIMATIVE", Utilities.deepEquals(expected, actual, ComparisonMode.APPROXIMATIVE));
        assertTrue("Should be equals, ignoring metadata",   Utilities.deepEquals(expected, actual, ComparisonMode.IGNORE_METADATA));
        if (strictlyDifferent) {
            assertFalse("Should not be strictly equals",    Utilities.deepEquals(expected, actual, ComparisonMode.STRICT));
        }
    }

    /**
     * Asserts that the given transform is represented by diagonal matrix where every elements
     * on the diagonal have the given values. The matrix doesn't need to be square. The last
     * row is handled especially if the {@code affine} argument is {@code true}.
     *
     * @param tr     The transform.
     * @param affine If {@code true}, then the last row is expected to contains the value 1
     *               in the last column, and all other columns set to 0.
     * @param values The values which are expected on the diagonal. If this array length is
     *               smaller than the diagonal length, then the last element in the array
     *               is repeated for all remaining diagonal elements.
     *
     * @since 3.07
     */
    public static void assertDiagonalMatrix(final MathTransform tr, final boolean affine, final double... values) {
        assertTrue("The transform shall be linear.", tr instanceof LinearTransform);
        final Matrix matrix = ((LinearTransform) tr).getMatrix();
        final int numRows = matrix.getNumRow();
        final int numCols = matrix.getNumCol();
        for (int j=0; j<numRows; j++) {
            for (int i=0; i<numCols; i++) {
                final double expected;
                if (affine && j == numRows-1) {
                    expected = (i == numCols-1) ? 1 : 0;
                } else if (i == j) {
                    expected = values[min(values.length-1, i)];
                } else {
                    expected = 0;
                }
                assertEquals("matrix(" + j + ',' + i + ')', expected, matrix.getElement(j, i), EPS);
            }
        }
    }

    /**
     * Asserts that the WKT of the given object is equal to the expected one.
     *
     * @param object The object to format in <cite>Well Known Text</cite> format.
     * @param expected The expected text, or {@code null} if {@code object} is expected to be null.
     *        If non-null, the expected text can use the format produced by
     *        {@link org.geotoolkit.test.Tools#printAsJavaCode} for easier reading.
     */
    public static void assertWktEquals(final IdentifiedObject object, final String expected) {
        if (expected == null) {
            assertNull(object);
        } else {
            assertNotNull(object);
            final String wkt;
            if (isSingleLine(expected) && (object instanceof FormattableObject)) {
                wkt = ((FormattableObject) object).toWKT(Convention.OGC, WKTFormat.SINGLE_LINE);
            } else {
                wkt = object.toWKT();
            }
            assertMultilinesEquals(object.getName().getCode(), decodeQuotes(expected), wkt);
        }
    }

    /**
     * Returns {@code true} if the following string has no carriage return or line feed.
     *
     * @param  text The text to check.
     * @return {@code true} if the given text is a single line.
     */
    private static boolean isSingleLine(final String text) {
        return text.lastIndexOf('\n') < 0 && text.lastIndexOf('\r') < 0;
    }

    /**
     * Tests if the given {@code outer} envelope contains the given {@code inner} envelope.
     * This method will also verify class consistency by invoking the {@code intersects}
     * method, and by interchanging the arguments.
     *
     * @param outer The envelope which is expected to contains the given inner envelope.
     * @param inner The envelope which should be contained by the outer envelope.
     *
     * @since 3.20
     */
    public static void assertContains(final AbstractEnvelope outer, final Envelope inner) {
        assertTrue("outer.contains(inner)",   outer.contains  (inner, true));
        assertTrue("outer.contains(inner)",   outer.contains  (inner, false));
        assertTrue("outer.intersects(inner)", outer.intersects(inner, true));
        assertTrue("outer.intersects(inner)", outer.intersects(inner, false));
        if (inner instanceof AbstractEnvelope) {
            final AbstractEnvelope ai = (AbstractEnvelope) inner;
            assertTrue ("inner.intersects(outer)", ai.intersects(outer, true));
            assertTrue ("inner.intersects(outer)", ai.intersects(outer, false));
            assertFalse("inner.contains(outer)",   ai.contains  (outer, true));
            assertFalse("inner.contains(outer)",   ai.contains  (outer, false));
        }
        final GeneralDirectPosition median = new GeneralDirectPosition(inner.getDimension());
        for (int i=median.getDimension(); --i>=0;) {
            median.setOrdinate(i, inner.getMedian(i));
        }
        assertTrue("outer.contains(median)", outer.contains(median));
    }

    /**
     * Tests if the given {@code e1} envelope is disjoint with the given {@code e2} envelope.
     * This method will also verify class consistency by invoking the {@code contains} method,
     * and by interchanging the arguments.
     *
     * @param e1 The first envelope to test.
     * @param e2 The second envelope to test.
     *
     * @since 3.20
     */
    public static void assertDisjoint(final AbstractEnvelope e1, final Envelope e2) {
        assertFalse("e1.intersects(e2)", e1.intersects(e2, false));
        assertFalse("e1.intersects(e2)", e1.intersects(e2, true));
        assertFalse("e1.contains(e2)",   e1.contains  (e2, false));
        assertFalse("e1.contains(e2)",   e1.contains  (e2, true));
        if (e2 instanceof AbstractEnvelope) {
            final AbstractEnvelope ae = (AbstractEnvelope) e2;
            assertFalse("e2.intersects(e1)", ae.intersects(e1, false));
            assertFalse("e2.intersects(e1)", ae.intersects(e1, true));
            assertFalse("e2.contains(e1)",   ae.contains  (e1, false));
            assertFalse("e2.contains(e1)",   ae.contains  (e1, true));
        }
        final int dimension = e1.getDimension();
        final int numCases = (int) round(pow(3, dimension));
        final GeneralDirectPosition pos = new GeneralDirectPosition(dimension);
        for (int index=0; index<numCases; index++) {
            int n = index;
            for (int i=0; i<dimension; i++) {
                final double ordinate;
                switch (n % 3) {
                    case 0: ordinate = e2.getMinimum(i); break;
                    case 1: ordinate = e2.getMedian (i); break;
                    case 2: ordinate = e2.getMaximum(i); break;
                    default: throw new AssertionError(i);
                }
                pos.setOrdinate(i, ordinate);
                n /= 3;
            }
            assertEquals(0, n); // Opportunist check of this assert method.
            assertFalse("e1.contains(" + pos + ')', e1.contains(pos));
        }
    }
}
