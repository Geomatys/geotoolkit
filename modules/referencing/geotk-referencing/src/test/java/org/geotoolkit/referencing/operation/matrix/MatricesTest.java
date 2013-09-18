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
package org.geotoolkit.referencing.operation.matrix;

import java.util.Random;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.Double.NaN;


/**
 * Tests {@link Matrices} static methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 */
public final strictfp class MatricesTest {
    /**
     * The tolerance value for floating point comparisons.
     */
    private static final double TOLERANCE = 1E-10;

    /**
     * The tolerance value when floating point are required to be strictly equal.
     */
    private static final double STRICT = 0;

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
                assertEquals(expected[k++], matrix.getElement(j, i), TOLERANCE);
            }
        }
        assertTrue(Matrices.create(numRows, numCols, expected).equals(matrix, TOLERANCE));
    }

    /**
     * Fills a matrix of the given size with random numbers.
     *
     * @param numRow    The number of rows of the matrix to be returned.
     * @param numCol    The number of columns of the matrix to be returned.
     * @param generator The random number generator to use.
     * @param affine    {@code true} for letting the last row to its default value.
     * @return          A matrix of the given size filled with random number.
     */
    private static GeneralMatrix randomMatrix(int numRow, final int numCol,
            final Random generator, final boolean affine)
    {
        final GeneralMatrix matrix = new GeneralMatrix(numRow, numCol);
        if (affine) {
            numRow--;
        }
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<numCol; i++) {
                matrix.setElement(j, i, generator.nextDouble() * 200 - 100);
            }
        }
        return matrix;
    }

    /**
     * Tests matrix multiplication on all matrix implementations provided in this package.
     */
    @Test
    public void testMultiply() {
        final Random generator = new Random(942268082);
        for (int numRow=1; numRow<=5; numRow++) {
            for (int numCol=1; numCol<=5; numCol++) {
                final GeneralMatrix reference = new GeneralMatrix(numRow, numCol);
                for (int numJoin=1; numJoin<=5; numJoin++) {
                    boolean affine = false;
                    do {
                        final GeneralMatrix first  = randomMatrix(numRow, numJoin, generator, affine);
                        final GeneralMatrix second = randomMatrix(numJoin, numCol, generator, affine);
                        reference.mul(first, second);
                        final Matrix matrix = Matrices.multiply(first, second);
                        Class<?> actualType = matrix.getClass();
                        Class<?> expectedType = GeneralMatrix.class;
                        if (numRow == numCol && numCol == numJoin) {
                            switch (numRow) {
                                case 1: expectedType = Matrix1.class; break;
                                case 2: expectedType = Matrix2.class; break;
                                case 3: expectedType = affine ? AffineMatrix3.class : Matrix3.class; break;
                                case 4: expectedType = Matrix4.class; break;
                            }
                        }
                        assertEquals(expectedType, actualType);
                        assertTrue(reference.equals(matrix, TOLERANCE));
                    } while ((affine = !affine) == true);
                }
            }
        }
    }

    /**
     * Tests inversion of squares matrix on all matrix implementations provided in this package.
     *
     * @throws NoninvertibleTransformException Should never happen.
     */
    @Test
    public void testInvert() throws NoninvertibleTransformException {
        final Random generator = new Random(519403389);
        for (int size=1; size<=5; size++) {
            boolean affine = false;
            do {
                final GeneralMatrix reference = randomMatrix(size, size, generator, affine);
                final XMatrix matrix = Matrices.copy(reference);
                assertNotSame("MatrixFactory should have created a new object.", reference, matrix);
                Class<?> expectedType, actualType = matrix.getClass();
                switch (size) {
                    case 1:  expectedType = Matrix1.class; break;
                    case 2:  expectedType = Matrix2.class; break;
                    case 3:  expectedType = Matrix3.class; break;
                    case 4:  expectedType = Matrix4.class; break;
                    default: expectedType = GeneralMatrix.class; break;
                }
                assertEquals(expectedType, actualType);
                assertTrue  (matrix.equals(reference, STRICT));
                assertSame  (matrix, Matrices.toXMatrix(matrix));
                assertSame  (reference, Matrices.toGeneralMatrix(reference));
                /*
                 * Following is actually more a test of vecmath than Geotk. We make sure that
                 * the specialized implementations return the same result than the general one.
                 */
                final GeneralMatrix clone = reference.clone(); // We will need that later.
                assertNotSame(reference, clone);
                assertEquals(reference, clone);
                reference.invert();
                matrix.invert();
                assertTrue("Inconsistent result using different implementation.", matrix.equals(reference, TOLERANCE));
                /*
                 * Tries the inversion again, now using the code that try to use the
                 * most specific matrix implementation.
                 */
                final Matrix result = Matrices.invert(clone);
                actualType = result.getClass();
                if (affine && size == 3) {
                    expectedType = AffineMatrix3.class;
                }
                assertEquals("Unexpected specialized class.", expectedType, actualType);
                if (expectedType.equals(GeneralMatrix.class)) {
                    assertSame("Should have recycled existing object.", clone, result);
                }
                assertTrue("Unexpected result in matrix inversion.", matrix.equals(result, TOLERANCE));
            } while ((affine = !affine) == true);
        }
    }

    /**
     * Tests {@link Matrices#invert(Matrix)} with a non-square matrix.
     *
     * @throws NoninvertibleTransformException Should not happen.
     */
    @Test
    public void testInvertNonSquare() throws NoninvertibleTransformException {
        final Matrix matrix = Matrices.create(3, 5, new double[] {
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
        final Matrix inverse = Matrices.invert(matrix);
        assertMatrixEquals(expected, 5, 3, inverse);
    }

    /**
     * Tests {@link Matrices#invert(Matrix)} with a square matrix that
     * contains a {@link Double#NaN} value.
     *
     * @throws NoninvertibleTransformException Should not happen.
     *
     * @since 3.17
     */
    @Test
    public void testInvertSquareNaN() throws NoninvertibleTransformException {
        Matrix matrix = Matrices.create(5, 5, new double[] {
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
        Matrix inverse = Matrices.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);

        // Set the scale factor to NaN. The offset become invalid.
        matrix = Matrices.create(5, 5, new double[] {
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
        inverse = Matrices.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);

        // Set the scale factor to NaN with an offset equals to 0.
        // The zero value should be preserved.
        matrix = Matrices.create(5, 5, new double[] {
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
        inverse = Matrices.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);

        // Set the offset to NaN.
        matrix = Matrices.create(5, 5, new double[] {
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
        inverse = Matrices.invertSquare(matrix);
        assertMatrixEquals(expected, 5, 5, inverse);
    }
}
