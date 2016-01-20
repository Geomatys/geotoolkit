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
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.referencing.operation.transform.LinearTransform;

import static java.lang.StrictMath.*;


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
}
