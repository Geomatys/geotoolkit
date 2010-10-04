/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.awt.geom.AffineTransform;
import javax.vecmath.MismatchedSizeException;
import javax.vecmath.SingularMatrixException;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.matrix.*;
import org.geotoolkit.referencing.operation.transform.LinearTransform;


/**
 * Utilities methods working on matrix. Those methods are not in public package (for now)
 * because they are unsafe: either they may returns one of the argument without cloning
 * (for efficiency), or the apply the operation directly on one of the argument (for
 * example multiplication) for efficiency again.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.00
 * @module
 */
@Static
public final class MatrixUtilities {
    /**
     * Do not allow instantiation of this class.
     */
    private MatrixUtilities() {
    }

    /**
     * Returns the underlying matrix for the specified transform,
     * or {@code null} if the matrix is unavailable.
     *
     * @param  transform The transform, or {@code null}.
     * @return The matrix of the given transform, or {@code null} if none.
     *
     * @since 3.15
     */
    public static Matrix getMatrix(final MathTransform transform) {
        if (transform instanceof LinearTransform) {
            return ((LinearTransform) transform).getMatrix();
        }
        if (transform instanceof AffineTransform) {
            return new Matrix3((AffineTransform) transform);
        }
        return null;
    }

    /**
     * Converts the specified matrix to a Geotk implementation. If {@code matrix} is already
     * an instance of {@code XMatrix}, then it is returned unchanged. Otherwise all elements are
     * copied in a new {@code XMatrix} object.
     *
     * @param  matrix The matrix to convert, or {@code null}.
     * @return The given matrix, or a copy of it as a {@link XMatrix} object, or {@code null}
     *         if the given matrix was {@code null}.
     */
    public static XMatrix toXMatrix(final Matrix matrix) {
        if (matrix == null) {
            return null;
        }
        if (matrix instanceof XMatrix) {
            return (XMatrix) matrix;
        }
        return MatrixFactory.create(matrix);
    }

    /**
     * Converts the specified matrix to the most suitable Geotk implementation. This method
     * copies the matrix elements in the specialized {@link Matrix1}, {@link Matrix2}, {@link
     * Matrix3} or {@link Matrix4} implementation if the matrix size fits and the given matrix
     * is not already one of those implementations. Otherwise it invokes {@link #toXMatrix}.
     * <p>
     * Using those specialized implementations brings a little bit of performance and,
     * more important, precision in the floating point results of matrix operations.
     *
     * @param  matrix The matrix to convert.
     * @return The given matrix, or a copy of it as a {@link XMatrix} object.
     */
    public static XMatrix toOptimalMatrix(final Matrix matrix) {
        final int size = matrix.getNumRow();
        if (size == matrix.getNumCol()) switch (size) {
            case 1: return (matrix instanceof Matrix1) ? (Matrix1) matrix : new Matrix1(matrix);
            case 2: return (matrix instanceof Matrix2) ? (Matrix2) matrix : new Matrix2(matrix);
            case 3: return (matrix instanceof Matrix3) ? (Matrix3) matrix : new Matrix3(matrix);
            case 4: return (matrix instanceof Matrix4) ? (Matrix4) matrix : new Matrix4(matrix);
        }
        return toXMatrix(matrix);
    }

    /**
     * Converts the specified matrix to a Geotk implementation of {@link Matrix}.
     * If {@code matrix} is already an instance of {@code GeneralMatrix}, then it is
     * returned unchanged. Otherwise, all elements are copied in a new {@code GeneralMatrix} object.
     * <p>
     * Before to use this method, check if a {@link XMatrix} (to be obtained with {@link #toXMatrix})
     * would be sufficient. Use this method only if a {@code GeneralMatrix} is really necessary.
     *
     * @param  matrix The matrix to convert.
     * @return The given matrix, or a copy of it as a {@link GeneralMatrix} object.
     */
    public static GeneralMatrix toGeneralMatrix(final Matrix matrix) {
        if (matrix instanceof GeneralMatrix) {
            return (GeneralMatrix) matrix;
        } else {
            return new GeneralMatrix(matrix);
        }
    }

    /**
     * Returns the given matrix as an affine transform. Matrix
     * validity shall be checked before this method is invoked.
     *
     * @param  matrix The matrix to returns as an affine transform.
     * @return The matrix as an affine transform. May be {@code matrix} itself.
     * @throws IllegalStateException if the given matrix is not affine or 3 by 3. Actually this
     *         exception is just propagated from the matrix package. If this method was public,
     *         we would need to throw {@link IllegalArgumentException} instead.
     */
    public static AffineTransform toAffineTransform(final Matrix matrix) throws IllegalStateException {
        if (matrix instanceof AffineTransform) {
            return (AffineTransform) matrix;
        }
        final Matrix3 m3;
        if (matrix instanceof Matrix3) {
            m3 = (Matrix3) matrix;
        } else if (matrix instanceof GeneralMatrix) {
            return ((GeneralMatrix) matrix).toAffineTransform2D();
        } else {
            m3 = new Matrix3(matrix);
        }
        return m3.toAffineTransform();
    }

    /**
     * Returns the given matrix as an affine transform. Matrix
     * validity shall be checked before this method is invoked.
     *
     * @param  matrix The matrix to returns as an affine transform.
     * @return The matrix as an affine transform. May be {@code matrix} itself.
     * @throws IllegalStateException if the given matrix is not affine or 3 by 3.
     */
    private static AffineMatrix3 toAffineMatrix3(final Matrix matrix) throws IllegalStateException {
        final AffineTransform at = toAffineTransform(matrix);
        return (at instanceof AffineMatrix3) ? (AffineMatrix3) at : new AffineMatrix3(at);
        // Geotk implementation already returns instances of AffineMatrix3, so the
        // above code should just cast in most cases. The "else" case which creates
        // a new AffineMatrix3 is defensive code.
    }

    /**
     * Returns {@code true} if the given matrix can be converted to an {@link AffineTransform}
     * object.
     *
     * @param  matrix The matrix to check for convertibility.
     * @return {@code true} if the given matrix can be converted to an {@link AffineTransform}.
     */
    private static boolean isAffineTransform(final Matrix matrix) {
        return matrix.getNumRow() == 3 &&
               matrix.getNumCol() == 3 &&
               matrix.getElement(2,0) == 0 &&
               matrix.getElement(2,1) == 0 &&
               matrix.getElement(2,2) == 1;
    }

    /**
     * Inverts the specified matrix, maybe (but not always) in place. If the matrix can't be
     * inverted (for example because of a {@link SingularMatrixException}), then the exception
     * is wrapped into a {@link NoninvertibleTransformException}.
     * <p>
     * This method performs a special check for non-square matrix in an attempt to invert
     * them anyway. This is possible only if some columns or rows contain contains only 0
     * elements.
     *
     * @param  matrix The matrix to invert.
     * @return The inverse of the given matrix. May be {@code matrix} itself.
     * @throws NoninvertibleTransformException if the given matrix is not invertible.
     */
    public static Matrix invert(final Matrix matrix) throws NoninvertibleTransformException {
        final int numRow = matrix.getNumRow();
        final int numCol = matrix.getNumCol();
        if (numRow < numCol) {
            /*
             * Target points have fewer ordinates than source point. If a column contains ony
             * zero values, then this means that the ordinate at the corresponding column is
             * simply deleted. We can omit that column. We check the last columns before the
             * first columns on the assumption that last dimensions are more likely to be
             * independant dimensions like time.
             */
            int oi = numCol - numRow;
            final int[] omitted = new int[oi];
skipColumn: for (int i=numCol; --i>=0;) {
                for (int j=numRow; --j>=0;) {
                    if (matrix.getElement(j, i) != 0) {
                        continue skipColumn;
                    }
                }
                // Found a column which contains only 0 elements.
                omitted[--oi] = i;
                if (oi == 0) {
                    break; // Found enough columns to skip.
                }
            }
            if (oi == 0) {
                /*
                 * Now create a square matrix which omit some or all columns
                 * containing only 0 elements, and invert that matrix. Finally,
                 * create a new matrix with new rows added for the omitted ordinates.
                 */
                Matrix squareMatrix = MatrixFactory.create(numRow);
                for (int k=0,i=0; i<numCol; i++) {
                    if (oi != omitted.length && i == omitted[oi]) {
                        oi++;
                    } else {
                        for (int j=numRow; --j>=0;) {
                            squareMatrix.setElement(j, k, matrix.getElement(j, i));
                        }
                        k++;
                    }
                }
                squareMatrix = invertSquare(squareMatrix);
                // From this point, the meaning of 'numCol' and 'numRow' are interchanged.
                final XMatrix inverse = MatrixFactory.create(numCol, numRow);
                oi = 0;
                for (int k=0,j=0; j<numCol; j++) {
                    if (oi != omitted.length && j == omitted[oi]) {
                        if (j < numRow) {
                            inverse.setElement(j, j, 0);
                        }
                        inverse.setElement(j, numRow-1, Double.NaN);
                        oi++;
                    } else {
                        for (int i=numRow; --i>=0;) {
                            inverse.setElement(j, i, squareMatrix.getElement(k, i));
                        }
                        k++;
                    }
                }
                return inverse;
            }
        }
        return invertSquare(matrix);
    }

    /**
     * Inverts the specified matrix, maybe (but not always) in place. If the matrix can't be
     * inverted (for example because of a {@link SingularMatrixException}), then the exception
     * is wrapped into a {@link NoninvertibleTransformException}.
     *
     * @param  matrix The matrix to invert.
     * @return The inverse of the given matrix. May be {@code matrix} itself.
     * @throws NoninvertibleTransformException if the given matrix is not invertible.
     */
    public static Matrix invertSquare(final Matrix matrix) throws NoninvertibleTransformException {
        Exception cause;
        if (isAffineTransform(matrix)) {
            /*
             * Uses AffineTransform path if possible, because it leads to more accurate
             * result. The JDK AffineTransform implementation contains a lot of special
             * paths for common cases like a transform having only a translation, etc.
             */
            final AffineMatrix3 at = toAffineMatrix3(matrix);
            try {
                at.invert();
                return at;
            } catch (java.awt.geom.NoninvertibleTransformException exception) {
                cause = exception;
            }
        } else {
            /*
             * For all other cases, tries to use the most specialized class
             * for similar reason than above (slightly more accurate result).
             */
            final XMatrix m = toOptimalMatrix(matrix);
            try {
                m.invert();
                return m;
            } catch (SingularMatrixException exception) {
                cause = exception;
            } catch (MismatchedSizeException exception) {
                // This exception is thrown if the matrix is not square.
                cause = exception;
            }
        }
        throw new NoninvertibleTransformException(Errors.format(Errors.Keys.NONINVERTIBLE_TRANSFORM), cause);
    }

    /**
     * Computes {@code matrix1} &times; {@code matrix2}. Reuses an existing matrix object
     * if possible, which is always the case when both matrix are square.
     *
     * @param  matrix1 The first matrix to multiply.
     * @param  matrix2 The second matrix to multiply.
     * @return The product of the two matrix. May be one of the above objects.
     */
    public static Matrix multiply(final Matrix matrix1, final Matrix matrix2) {
        final int numRow = matrix1.getNumRow();
        final int numCol = matrix2.getNumCol();
        if (numCol == matrix1.getNumCol()) {
            if (isAffineTransform(matrix1) && isAffineTransform(matrix2)) {
                final AffineMatrix3 matrix = toAffineMatrix3(matrix1);
                matrix.concatenate(toAffineTransform(matrix2));
                return matrix;
            } else {
                final XMatrix matrix = toOptimalMatrix(matrix1);
                matrix.multiply(matrix2);
                return matrix;
            }
        } else {
            final GeneralMatrix matrix = new GeneralMatrix(numRow, numCol);
            matrix.mul(toGeneralMatrix(matrix1), toGeneralMatrix(matrix2));
            return matrix;
        }
    }

    /**
     * Returns {@code true} if the elements values to not differ by a value greater than the
     * given tolerance value. If {@code relative} is {@code true}, then for any pair of values
     * <var>v1</var><sub>j,i</sub> and <var>v2</var><sub>j,i</sub> to compare, the tolerance
     * threshold is scaled by {@code max(abs(v1), abs(v2))}.
     *
     * @param  matrix1  The first matrix to compare.
     * @param  matrix2  The second matrix to compare.
     * @param  epsilon  The tolerance value.
     * @param  relative If {@code true}, then the tolerance value is relative to the magnitude
     *         of the matrix elements being compared.
     * @return {@code true} if the values of the two matrix do not differ by a quantity
     *         greater than the given tolerance threshold.
     */
    public static boolean epsilonEqual(final Matrix matrix1, final Matrix matrix2,
            final double epsilon, final boolean relative)
    {
        final int numRow = matrix1.getNumRow();
        if (numRow != matrix2.getNumRow()) {
            return false;
        }
        final int numCol = matrix1.getNumCol();
        if (numCol != matrix2.getNumCol()) {
            return false;
        }
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<numCol; i++) {
                final double v1 = matrix1.getElement(j, i);
                final double v2 = matrix2.getElement(j, i);
                double tolerance = epsilon;
                if (relative) {
                    tolerance *= Math.max(Math.abs(v1), Math.abs(v2));
                }
                if (!(Math.abs(v1 - v2) <= tolerance)) {
                    if (Double.doubleToLongBits(v1) == Double.doubleToLongBits(v2)) {
                        // Special case for NaN and infinite values.
                        continue;
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
