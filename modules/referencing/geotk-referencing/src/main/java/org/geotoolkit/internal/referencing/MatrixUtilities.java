/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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

import static org.geotoolkit.referencing.operation.matrix.MatrixFactory.*;


/**
 * Utilities methods working on matrix. Those methods are not in public package (for now)
 * because they are unsafe: either they may returns one of the argument without cloning
 * (for efficiency), or the apply the operation directly on one of the argument (for
 * example multiplication) for efficiency again.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
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
     * If the given transform is an affine transform, returns its matrix resized to the given
     * dimensions. Otherwise returns {@code null}. This method is invoked for adding or removing
     * dimensions to an affine transform. The added or removed dimensions are always the last
     * ones. Special cases:
     * <p>
     * <ul>
     *   <li>If source and target dimensions are added, the corresponding offset and scale factors
     *       will be 0 and 1 respectively. In other words, new dimensions are propagated unchanged.</li>
     *   <li>New source dimensions have no impact on existing dimensions (the corresponding scale
     *       factors are set to zero).</li>
     * </ul>
     *
     * @param  transform The math transform from which to extract and resize the matrix.
     * @param  sourceDimension The desired number of source dimensions.
     * @param  targetDimension The desired number of target dimensions.
     * @return The transform matrix for the given number of dimensions, or {@code null}.
     *
     * @since 3.16
     */
    public static Matrix forDimensions(final MathTransform transform,
            final int sourceDimension, final int targetDimension)
    {
        if (transform instanceof LinearTransform) {
            Matrix matrix = getMatrix(transform);
            if (matrix != null && isAffine(matrix)) {
                final int oldSrcDim = matrix.getNumCol() - 1;
                final int oldTgtDim = matrix.getNumRow() - 1;
                if (oldSrcDim != sourceDimension && oldTgtDim != targetDimension) {
                    final XMatrix resized = create(targetDimension+1, sourceDimension+1);
                    final int commonRows = Math.min(targetDimension, oldTgtDim);
                    final int commonCols = Math.min(sourceDimension, oldSrcDim);
                    for (int j=0; j<commonRows; j++) {
                        // Set the scale factor to zero only for existing dimensions
                        // (not for new target dimensions added by this method call).
                        if (j >= commonCols && j < targetDimension) {
                            resized.setElement(j, j, 0);
                        }
                        // Copy the scale and shear factors.
                        for (int i=0; i<commonCols; i++) {
                            resized.setElement(j, i, matrix.getElement(j, i));
                        }
                        // Copy the translation term.
                        resized.setElement(j, sourceDimension, matrix.getElement(j, oldSrcDim));
                    }
                    matrix = resized;
                }
                return matrix;
            }
        }
        return null;
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
     * Returns {@code true} if the given matrix can be converted to a two-dimensional
     * {@link AffineTransform} object.
     *
     * @param  matrix The matrix to check for convertibility.
     * @return {@code true} if the given matrix can be converted to an {@link AffineTransform}.
     */
    private static boolean isAffine2D(final Matrix matrix) {
        return matrix.getNumRow() == 3 && matrix.getNumCol() == 3 && isAffine(matrix);
    }

    /**
     * Returns {@code true} if the given matrix is affine.
     *
     * @param matrix The matrix to test.
     * @return {@code true} if the matrix is affine.
     *
     * @see XMatrix#isAffine()
     *
     * @since 3.17
     */
    public static boolean isAffine(final Matrix matrix) {
        if (matrix instanceof AffineTransform) {
            return true;
        }
        if (matrix instanceof XMatrix) {
            return ((XMatrix) matrix).isAffine();
        }
        double expected = 1;
        final int lastRow = matrix.getNumRow() - 1;
        for (int i=matrix.getNumCol(); --i>=0;) {
            if (matrix.getElement(lastRow, i) != expected) {
                return false;
            }
            expected = 0;
        }
        return true;
    }

    /**
     * Modifies the given matrix in order to reverse the axis at the given dimension.
     * The matrix is assumed affine, but this is not verified.
     *
     * @param matrix    The matrix to modify.
     * @param dimension The dimension of the axis to reverse.
     * @param span      The envelope span at the dimension of the axis to be reversed,
     *                  in units of the source coordinate system.
     *
     * @since 3.16
     */
    public static void reverseAxis(final Matrix matrix, final int dimension, final double span) {
        final int numRows = matrix.getNumRow();
        final int lastCol = matrix.getNumCol() - 1;
        for (int j=0; j<numRows; j++) {
            final double scale = matrix.getElement(j, dimension);
            matrix.setElement(j, dimension, -scale);
            matrix.setElement(j, lastCol, matrix.getElement(j, lastCol) + scale*span);
        }
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
                Matrix squareMatrix = create(numRow);
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
                final XMatrix inverse = create(numCol, numRow);
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
    public static Matrix invertSquare(Matrix matrix) throws NoninvertibleTransformException {
        /*
         * Searches for NaN values. If some NaN values are found but the matrix is written
         * in such a way that the NaN value is used for exactly one ordinate value (i.e. a
         * matrix row is used for a one-dimensional conversion which is independent of all
         * other dimensions), then we will edit the matrix in such a way that this NaN value
         * does not prevent the inverse matrix to be computed.
         */
        int   numIndex = 0;
        int[] indexNaN = null; // Pairs of (i,j) followed by the column of scale factor.
        final int numCol = matrix.getNumCol() - 1; // Exclude the translation column
        final int numRow = matrix.getNumRow() - 1; // Exclude the (0, 0, ..., 1) row
        if (isAffine(matrix)) {
search:     for (int j=numRow; --j>=0;) {
                for (int i=numCol; i>=0; i--) { // Scan also the translation column.
                    if (Double.isNaN(matrix.getElement(j, i))) {
                        /*
                         * Found a NaN value. First, if the we are not in the translation
                         * column, ensure that the column contains only zero values except
                         * on the current line.
                         */
                        int scaleColumn = -1;
                        if (i != numCol) {
                            scaleColumn = i; // The non-translation element is the scale factor.
                            for (int k=numRow; --k>=0;) {
                                if (k != j && matrix.getElement(k, i) != 0) {
                                    indexNaN = null;
                                    numIndex = 0;
                                    break search;
                                }
                            }
                        }
                        /*
                         * Next, ensure that the row contains only zero elements except for
                         * the scale factor and the offset (the element in the translation
                         * column, which is not checked by the loop below).
                         */
                        for (int k=numCol; --k>=0;) {
                            if (k != i && matrix.getElement(j, k) != 0) {
                                if (scaleColumn >= 0) {
                                    // If there is more than 1 non-zero element,
                                    // abandon the attempt to handle NaN values.
                                    indexNaN = null;
                                    numIndex = 0;
                                    break search;
                                }
                                scaleColumn = k;
                            }
                        }
                        /*
                         * At this point, the NaN element has been determined as replaceable.
                         * Remember its index; the replacement will be performed later.
                         */
                        if (indexNaN == null) {
                            indexNaN = new int[numRow * 6]; // At most one scale and one offset per row.
                        }
                        indexNaN[numIndex++] = i;
                        indexNaN[numIndex++] = j;
                        indexNaN[numIndex++] = scaleColumn; // May be -1 (while uncommon)
                    }
                }
            }
            /*
             * IF there is any NaN value to edit, replace them by 0 if they appear in the
             * translation column or by 1 otherwise (scale or shear).
             */
            for (int k=0; k<numIndex; k+=3) {
                final int i = indexNaN[k];
                final int j = indexNaN[k+1];
                matrix.setElement(j, i, (i == numCol) ? 0 : 1);
            }
        }
        /*
         * Now apply the inversion.
         */
        Exception failure = null;
        if (isAffine2D(matrix)) {
            /*
             * Uses AffineTransform path if possible, because it leads to more accurate
             * result. The JDK AffineTransform implementation contains a lot of special
             * paths for common cases like a transform having only a translation, etc.
             */
            final AffineMatrix3 at = toAffineMatrix3(matrix);
            try {
                at.invert();
                matrix = at;
            } catch (java.awt.geom.NoninvertibleTransformException exception) {
                failure = exception;
            }
        } else {
            /*
             * For all other cases, tries to use the most specialized class
             * for similar reason than above (slightly more accurate result).
             */
            final XMatrix m = toOptimalMatrix(matrix);
            try {
                m.invert();
                matrix = m;
            } catch (SingularMatrixException exception) {
                failure = exception;
            } catch (MismatchedSizeException exception) {
                // This exception is thrown if the matrix is not square.
                failure = exception;
            }
        }
        if (failure != null) {
            throw new NoninvertibleTransformException(Errors.format(Errors.Keys.NONINVERTIBLE_TRANSFORM), failure);
        }
        /*
         * At this point, the matrix has been inverted. If they were any NaN value in the original
         * matrix, set the corresponding scale factor and offset to NaN in the resulting matrix.
         */
        for (int k=0; k<numIndex;) {
            final int i = indexNaN[k++];
            final int j = indexNaN[k++];
            final int s = indexNaN[k++];
            if (i != numCol) {
                // Found a scale factor to set to NaN.
                matrix.setElement(i, j, Double.NaN); // Note that i,j indices are interchanged.
                if (matrix.getElement(i, numRow) != 0) {
                    matrix.setElement(i, numRow, Double.NaN); // = -offset/scale, so 0 stay 0.
                }
            } else if (s >= 0) {
                // Found a translation factory to set to NaN.
                matrix.setElement(s, numRow, Double.NaN);
            }
        }
        return matrix;
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
            if (isAffine2D(matrix1) && isAffine2D(matrix2)) {
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
     * Returns {@code true} if the elements values do not differ by a value greater than the
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
