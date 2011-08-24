/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.internal.referencing;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.Double.NaN;


/**
 * Tests the {@link MatrixUtilities} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.10
 */
public final strictfp class MatrixUtilitiesTest {
    /**
     * Small tolerance factor for comparison of floating point numbers.
     */
    private static final double EPS = 1E-12;

    /**
     * Tests {@link MatrixUtilities#forDimensions}.
     *
     * @since 3.16
     */
    @Test
    public void testForDimensions() {
        final Matrix matrix = MatrixFactory.create(3, 3, new double[] {
            2, 0, 8,
            0, 4, 5,
            0, 0, 1
        });
        final double[] expected = {
            2, 0, 0, 8,
            0, 4, 0, 5,
            0, 0, 1, 0,
            0, 0, 0, 1
        };
        final Matrix m3 = MatrixUtilities.forDimensions(ProjectiveTransform.create(matrix), 3, 3);
        assertMatrixEquals(expected, 4, 4, m3);
    }

    /**
     * Tests {@link MatrixUtilities#invert(Matrix)} with a non-square matrix.
     *
     * @throws NoninvertibleTransformException Should not happen.
     */
    @Test
    public void testInvertNonSquare() throws NoninvertibleTransformException {
        final Matrix matrix = MatrixFactory.create(3, 5, new double[] {
            2, 0, 0, 0, 8,
            0, 0, 4, 0, 5,
            0, 0, 0, 0, 1
        });
        final double[] expected = {
            0.5, 0,    -4,
            0,   0,     NaN,
            0,   0.25, -1.25,
            0,   0,     NaN,
            0,   0,     1
        };
        final Matrix inverse = MatrixUtilities.invert(matrix);
        assertMatrixEquals(expected, 5, 3, inverse);
    }

    /**
     * Tests {@link MatrixUtilities#invert(Matrix)} with a square matrix that
     * contains a {@link Double#NaN} value.
     *
     * @throws NoninvertibleTransformException Should not happen.
     *
     * @since 3.17
     */
    @Test
    public void testInvertSquareNaN() throws NoninvertibleTransformException {
        Matrix matrix = MatrixFactory.create(5, 5, new double[] {
            20,  0,   0,   0, -3000,
            0, -20,   0,   0,  4000,
            0,   0,   0,   2,    20,
            0,   0, 400,   0,  2000,
            0,   0,   0,   0,     1
        });
        double[] expected = {
            0.05,  0,  0,      0,  150,
            0, -0.05,  0,      0,  200,
            0,     0,  0, 0.0025,   -5,
            0,     0,  0.5,    0,  -10,
            0,     0,  0,      0,    1
        };
        // Just for making sure that our matrix is correct.
        Matrix inverse = MatrixUtilities.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);

        // Set the scale factor to NaN. The offset become invalid.
        matrix = MatrixFactory.create(5, 5, new double[] {
            20,  0,   0,   0, -3000,
            0, -20,   0,   0,  4000,
            0,   0,   0, NaN,    20,
            0,   0, 400,   0,  2000,
            0,   0,   0,   0,     1
        });
        expected = new double[] {
            0.05,  0,  0,      0,  150,
            0, -0.05,  0,      0,  200,
            0,     0,  0, 0.0025,   -5,
            0,     0,  NaN,    0,  NaN,
            0,     0,  0,      0,    1
        };
        inverse = MatrixUtilities.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);

        // Set the scale factor to NaN with an offset equals to 0.
        // The zero value should be preserved.
        matrix = MatrixFactory.create(5, 5, new double[] {
            20,  0,   0,   0, -3000,
            0, -20,   0,   0,  4000,
            0,   0,   0, NaN,     0,
            0,   0, 400,   0,  2000,
            0,   0,   0,   0,     1
        });
        expected = new double[] {
            0.05,  0,  0,      0,  150,
            0, -0.05,  0,      0,  200,
            0,     0,  0, 0.0025,   -5,
            0,     0,  NaN,    0,    0,
            0,     0,  0,      0,    1
        };
        inverse = MatrixUtilities.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);

        // Set the offset to NaN.
        matrix = MatrixFactory.create(5, 5, new double[] {
            20,  0,   0,   0, -3000,
            0, -20,   0,   0,  4000,
            0,   0,   0,   2,   NaN,
            0,   0, 400,   0,  2000,
            0,   0,   0,   0,     1
        });
        expected = new double[] {
            0.05,  0,  0,      0,  150,
            0, -0.05,  0,      0,  200,
            0,     0,  0, 0.0025,   -5,
            0,     0,  0.5,    0,  NaN,
            0,     0,  0,      0,    1
        };
        inverse = MatrixUtilities.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);
    }

    /**
     * Asserts that the given matrix is equals to the given expected values. This method tests
     * individual values before to test {@link MatrixUtilities#epsilonEqual} in order to produce
     * better error message.
     *
     * @since 3.16
     */
    private static void assertMatrixEquals(final double[] expected,
            final int numRows, final int numCols, final Matrix matrix)
    {
        assertEquals("numRows", numRows, matrix.getNumRow());
        assertEquals("numCols", numCols, matrix.getNumCol());
        int k = 0;
        for (int j=0; j<numRows; j++) {
            for (int i=0; i<numCols; i++) {
                assertEquals(expected[k++], matrix.getElement(j, i), EPS);
            }
        }
        assertTrue("epsilonEqual", MatrixUtilities.epsilonEqual(
                MatrixFactory.create(numRows, numCols, expected), matrix, EPS, false));
    }
}
