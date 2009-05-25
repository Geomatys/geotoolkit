/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import static org.geotoolkit.internal.referencing.MatrixUtilities.*;


/**
 * Tests {@link MatrixUtilities} static methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final class MatrixTest {
    /**
     * The tolerance value for floating point comparisons.
     */
    private static final double TOLERANCE = 1E-10;

    /**
     * The tolerance value when floating point are required to be strictly equal.
     */
    private static final double STRICT = 0;

    /**
     * Tests inversion of squares matrix.
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
                final XMatrix matrix = MatrixFactory.create(reference);
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
                assertSame  (matrix, toXMatrix(matrix));
                assertSame  (reference, toGeneralMatrix(reference));
                /*
                 * Following is actually more a test of vecmath than Geotoolkit. We make sure that
                 * the specialized implementations return the same result than the general one.
                 */
                final GeneralMatrix clone = reference.clone(); // We will need that later.
                assertNotSame(reference, clone);
                assertEquals(reference, clone);
                reference.invert();
                matrix.invert();
                assertTrue("Inconsistent result using different implementation.",
                        matrix.equals(reference, TOLERANCE));
                /*
                 * Tries the inversion again, now using the code that try to use the
                 * most specific matrix implementation.
                 */
                final Matrix result = invert(clone);
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
     * Tests matrix multiplication.
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
                        final Matrix matrix = multiply(first, second);
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
}
