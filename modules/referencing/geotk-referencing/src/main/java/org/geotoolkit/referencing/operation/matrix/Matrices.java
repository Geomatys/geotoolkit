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
package org.geotoolkit.referencing.operation.matrix;

import java.util.Objects;
import java.awt.geom.AffineTransform;
import javax.vecmath.MismatchedSizeException;
import javax.vecmath.SingularMatrixException;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.lang.Static;
import org.apache.sis.math.MathFunctions;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.transform.LinearTransform;

import static org.geotoolkit.internal.InternalUtilities.COMPARISON_THRESHOLD;


/**
 * Static utility methods for creating and manipulating matrices. The factory methods select one of
 * the {@link Matrix1}, {@link Matrix2}, {@link Matrix3}, {@link Matrix4} or {@link GeneralMatrix}
 * implementations according the desired matrix size. Note that if the matrix size is know at compile
 * time, it may be more efficient to invoke directly the constructor of the appropriate class instead.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.21
 *
 * @since 3.20 (derived from 2.2)
 * @module
 */
public class Matrices extends Static {
    /**
     * Do not allows instantiation of this class.
     */
    Matrices() {
    }

    /**
     * Creates a square identity matrix of size {@code size}&nbsp;&times;&nbsp;{@code size}.
     *
     * @param size For an affine transform, this is the number of source and target dimensions + 1.
     * @return An identity matrix of the given size.
     */
    public static XMatrix create(final int size) {
        switch (size) {
            case 1: return new Matrix1();
            case 2: return new Matrix2();
            case 3: return new Matrix3();
            case 4: return new Matrix4();
        }
        return new GeneralMatrix(size);
    }

    /**
     * Creates a matrix of size {@code numRow}&nbsp;&times;&nbsp;{@code numCol}.
     * Elements on the diagonal (<var>j</var> == <var>i</var>) are set to 1.
     *
     * @param numRow For an affine transform, this is the number of {@linkplain MathTransform#getTargetDimensions target dimensions} + 1.
     * @param numCol For an affine transform, this is the number of {@linkplain MathTransform#getSourceDimensions source dimensions} + 1.
     * @return An identity matrix of the given size.
     */
    public static XMatrix create(final int numRow, final int numCol) {
        if (numRow == numCol) {
            return create(numRow);
        } else {
            return new GeneralMatrix(numRow, numCol);
        }
    }

    /**
     * Creates a matrix of size {@code numRow}&nbsp;&times;&nbsp;{@code numCol}
     * initialized to the given elements. The elements array size must be equals
     * to {@code numRow*numCol}. Column indices vary fastest, as expected by the
     * {@link GeneralMatrix#GeneralMatrix(int,int,double[])} constructor.
     *
     * @param  numRow   Number of rows.
     * @param  numCol   Number of columns.
     * @param  elements Elements of the matrix. Column indices vary fastest.
     * @return A matrix initialized to the given elements.
     */
    public static XMatrix create(final int numRow, final int numCol, final double[] elements) {
        if (numRow * numCol != elements.length) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
        if (numRow == numCol) switch (numRow) {
            case 1: return new Matrix1(elements[0]);
            case 2: return new Matrix2(elements);
            case 3: return new Matrix3(elements);
            case 4: return new Matrix4(elements);
        }
        return new GeneralMatrix(numRow, numCol, elements);
    }

    /**
     * Creates a matrix for an affine transform that keep only a subset of source ordinate values.
     * The matrix size will be ({@code toKeep.length}+1) &times; ({@code sourceDim}+1).
     *
     * @param  sourceDim the dimension of source coordinates.
     * @param  toKeep the indices of source ordinate values to keep.
     * @return The matrix for an affine transform keeping only the given dimensions, and
     *         discarding all others.
     * @throws IndexOutOfBoundsException if a value of {@code toKeep}
     *         is lower than 0 or not smaller than {@code sourceDim}.
     *
     * @see org.geotoolkit.referencing.operation.MathTransforms#dimensionFilter(int, int[])
     */
    public static XMatrix createDimensionFilter(final int sourceDim, final int[] toKeep)
            throws IndexOutOfBoundsException
    {
        final int targetDim = toKeep.length;
        final XMatrix matrix = create(targetDim+1, sourceDim+1);
        matrix.setZero();
        for (int j=0; j<targetDim; j++) {
            matrix.setElement(j, toKeep[j], 1);
        }
        matrix.setElement(targetDim, sourceDim, 1);
        return matrix;
    }

    /**
     * Creates a new matrix which is a copy of the specified matrix.
     *
     * @param matrix The matrix to copy.
     * @return A copy of the given matrix.
     *
     * @since 3.20 (derived from 2.2)
     */
    public static XMatrix copy(final Matrix matrix) {
        final int size = matrix.getNumRow();
        if (size == matrix.getNumCol()) {
            switch (size) {
                case 1: return new Matrix1(matrix);
                case 2: return new Matrix2(matrix);
                case 3: return new Matrix3(matrix);
                case 4: return new Matrix4(matrix);
            }
        }
        return new GeneralMatrix(matrix);
    }

    /**
     * If the given transform is linear, returns its coefficients as a matrix.
     * More specifically:
     * <p>
     * <ul>
     *   <li>If the given transform is an instance of {@link LinearTransform}, returns
     *       {@link LinearTransform#getMatrix()}.</li>
     *   <li>Otherwise if the given transform is an instance of {@link AffineTransform},
     *       returns its coefficients in a {@link Matrix3} instance.</li>
     *   <li>Otherwise returns {@code null}.</li>
     * </ul>
     *
     * @param  transform The transform, or {@code null}.
     * @return The matrix of the given transform, or {@code null} if none.
     *
     * @since 3.16 (derived from 3.15)
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
     * an instance of {@code XMatrix}, then it is returned unchanged. Otherwise all elements
     * are copied in a new {@code XMatrix} object.
     *
     * @param  matrix The matrix to convert, or {@code null}.
     * @return The matrix argument if it can be safely casted (including {@code null} argument),
     *         or a copy of the given matrix otherwise.
     *
     * @since 3.16 (derived from 3.00)
     */
    public static XMatrix toXMatrix(final Matrix matrix) {
        if (matrix == null || matrix instanceof XMatrix) {
            return (XMatrix) matrix;
        }
        return copy(matrix);
    }

    /**
     * Converts the specified matrix to the most suitable Geotk implementation.
     * This method process as below:
     * <p>
     * <ul>
     *   <li>If the given matrix is already an instance of the {@link Matrix1}, {@link Matrix2},
     *       {@link Matrix3} or {@link Matrix4} specialized classes, then it is returned unchanged.</li>
     *   <li>Otherwise if the given matrix is square and the number of rows & columns is 1, 2, 3
     *       or 4, then the elements are copied in an instance of {@link Matrix1}, {@link Matrix2},
     *       {@link Matrix3} or {@link Matrix4} respectively, and the new instance is returned.</li>
     *   <li>Otherwise this method delegates to {@link #toXMatrix(Matrix)}.</li>
     * </ul>
     * <p>
     * Using those specialized implementations brings a little bit of performance and,
     * more important, precision in the floating point results of matrix operations.
     *
     * @param  matrix The matrix to convert, or {@code null}.
     * @return The matrix argument if it can be safely casted (including {@code null} argument),
     *         or a copy of the given matrix otherwise.
     *
     * @since 3.16 (derived from 3.00)
     */
    public static XMatrix toOptimalMatrix(final Matrix matrix) {
        if (matrix != null) {
            final int size = matrix.getNumRow();
            if (size == matrix.getNumCol()) switch (size) {
                case 1: return (matrix instanceof Matrix1) ? (Matrix1) matrix : new Matrix1(matrix);
                case 2: return (matrix instanceof Matrix2) ? (Matrix2) matrix : new Matrix2(matrix);
                case 3: return (matrix instanceof Matrix3) ? (Matrix3) matrix : new Matrix3(matrix);
                case 4: return (matrix instanceof Matrix4) ? (Matrix4) matrix : new Matrix4(matrix);
            }
        }
        return toXMatrix(matrix);
    }

    /**
     * Converts the specified matrix to {@link GeneralMatrix}. If {@code matrix} is already an
     * instance of {@code GeneralMatrix}, then it is returned unchanged. Otherwise all elements
     * are copied in a new {@code GeneralMatrix} object.
     * <p>
     * Consider using {@link #toXMatrix(Matrix)} instead than this method if a {@link XMatrix}
     * instance is sufficient. Use this method only if a {@code GeneralMatrix} is really necessary.
     *
     * @param  matrix The matrix to convert, or {@code null}.
     * @return The matrix argument if it can be safely casted (including {@code null} argument),
     *         or a copy of the given matrix otherwise.
     *
     * @since 3.16 (derived from 3.00)
     */
    public static GeneralMatrix toGeneralMatrix(final Matrix matrix) {
        if (matrix == null || matrix instanceof GeneralMatrix) {
            return (GeneralMatrix) matrix;
        } else {
            return new GeneralMatrix(matrix);
        }
    }

    /**
     * Returns the given matrix as a Java2D affine transform.
     *
     * @param  matrix The matrix to returns as an affine transform.
     * @return The matrix argument if it can be safely casted (including {@code null} argument),
     *         or a copy of the given matrix otherwise.
     * @throws IllegalArgumentException if the given matrix size is not 3&times;3
     *         or the matrix is not affine.
     *
     * @see Matrix3#toAffineTransform()
     * @see GeneralMatrix#toAffineTransform2D()
     *
     * @since 3.20 (derived from 3.00)
     */
    public static AffineTransform toAffineTransform(final Matrix matrix) throws IllegalArgumentException {
        if (matrix == null || matrix instanceof AffineTransform) {
            return (AffineTransform) matrix;
        }
        try {
            final Matrix3 m3;
            if (matrix instanceof Matrix3) {
                m3 = (Matrix3) matrix;
            } else if (matrix instanceof GeneralMatrix) {
                return ((GeneralMatrix) matrix).toAffineTransform2D();
            } else {
                m3 = new Matrix3(matrix);
            }
            return m3.toAffineTransform();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e);
        }
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
     * @since 3.20 (derived from 3.17)
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
     * Returns the given matrix resized to the given dimensions. This method can be invoked for
     * adding or removing dimensions to an affine transform. The added or removed dimensions are
     * always the last ones. More specifically:
     * <p>
     * <ul>
     *   <li>If source and target dimensions are added, the corresponding offset and scale factors
     *       will be 0 and 1 respectively. In other words, new dimensions are propagated unchanged.</li>
     *   <li>New source dimensions have no impact on existing dimensions (the corresponding scale
     *       factors are set to zero).</li>
     * </ul>
     * <p>
     * The caller should ensure that {@link #isAffine(Matrix)} returns {@code true} before to invoke
     * this method, since this is not verified by this method.
     *
     * @param  matrix The matrix to resize.
     * @param  sourceDimension The desired number of source dimensions.
     * @param  targetDimension The desired number of target dimensions.
     * @return The matrix for the given number of dimensions. This will be the {@code matrix}
     *         argument itself if no resizing was needed.
     *
     * @since 3.20 (derived from 3.16)
     */
    public static Matrix resizeAffine(Matrix matrix, final int sourceDimension, final int targetDimension) {
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

    /**
     * Modifies the given matrix in order to reverse the direction of the axis at the given
     * dimension. The matrix is assumed affine, but this is not verified.
     *
     * @param matrix    The matrix to modify.
     * @param dimension The dimension of the axis to reverse.
     * @param span      The envelope span at the dimension of the axis to be reversed,
     *                  in units of the source coordinate system.
     *
     * @since 3.16
     */
    public static void reverseAxisDirection(final Matrix matrix, final int dimension, final double span) {
        final int numRows = matrix.getNumRow();
        final int lastCol = matrix.getNumCol() - 1;
        for (int j=0; j<numRows; j++) {
            final double scale = matrix.getElement(j, dimension);
            if (scale != 0) {
                // The formula below still work with scale=0, but we don't want
                // to change the scale sign from positive zero to negative zero.
                matrix.setElement(j, dimension, -scale);
                matrix.setElement(j, lastCol, matrix.getElement(j, lastCol) + scale*span);
            }
        }
    }

    /**
     * Computes {@code matrix1} &times; {@code matrix2}, reusing one of the given matrices.
     * The two given matrices shall be considered invalid after this method call.
     *
     * @param  matrix1 The first matrix to multiply. May be overwritten by the result.
     * @param  matrix2 The second matrix to multiply. May be overwritten by the result.
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
    static Matrix invertSquare(Matrix matrix) throws NoninvertibleTransformException {
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
            } catch (SingularMatrixException | MismatchedSizeException exception) {
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
     * Rounds the coefficients that are relatively close to integers, given a threshold value.
     * "Relatively" means that the given threshold is relative to the magnitude of each vector,
     * assuming that the given matrix define an affine transform.
     * More specifically:
     * <p>
     * <ul>
     *   <li>The last column in the given matrix is assumed to contain translation terms,
     *       and will be ignored in the vector magnitude calculation described below.</li>
     *   <li>For each row:<ul>
     *     <li>Compute the {@linkplain MathFunctions#magnitude(double[]) magnitude} of the vector
     *         formed by the coefficients in all columns except the last one.</li>
     *     <li>Multiply the given {@code tolerance} value by the given vector magnitude.</li>
     *     <li>For each element in the current row:<ul>
     *       <li>Compute an adjusted value as {@code adjusted = Math.round(element*scale)/scale}.</li>
     *       <li>If the difference between the element and the above adjusted value is not greater
     *           than the tolerance threshold adjusted for vector magnitude, replace that element
     *           by the adjusted value.</li>
     *     </ul></li>
     *   </ul></li>
     * </ul>
     * <p>
     * In other words, the {@code tolerance} argument given to this method is the value that we
     * would use if we knew that the matrix was close to an identity matrix, without worrying
     * about the {@code scale} argument. This method will take care of adjusting the tolerance
     * threshold for the actual matrix values. However this algorithm is designed for matrix
     * representing affine transforms (not general matrix).
     *
     * @param matrix    The matrix in which to filter rounding errors in-place.
     * @param scale     A scale factor by which to multiply each element before to round the scaled value.
     *                  For example a value of 10 will allow this method to round 0.2000…1 as 0.2.
     * @param tolerance The relative tolerance threshold.
     *
     * @since 3.21
     */
    public static void filterRoundingErrors(final XMatrix matrix, final int scale, double tolerance) {
        ArgumentChecks.ensureStrictlyPositive("scale", scale);
        ArgumentChecks.ensureStrictlyPositive("tolerance", tolerance);
        tolerance *= scale;
        final int numRow = matrix.getNumRow();
        final int numCol = matrix.getNumCol();
        final double[] vector = new double[numCol - 1]; // Exclude translation term.
        for (int j=0; j<numRow; j++) {
            for (int i=0; i<vector.length; i++) {
                vector[i] = matrix.getElement(j, i);
            }
            final double eps = MathFunctions.magnitude(vector) * tolerance;
            for (int i=0; i<numCol; i++) {
                final double value = matrix.getElement(j, i) * scale;
                final double round = Math.rint(value);
                if (Math.abs(value - round) <= eps) {
                    matrix.setElement(j, i, round / scale);
                }
            }
        }
    }

    /**
     * Compares the given matrices for equality, using the given relative or absolute tolerance
     * threshold. The matrix elements are compared as below:
     * <p>
     * <ul>
     *   <li>{@link Double#NaN} values are considered equals to all other NaN values</li>
     *   <li>Infinite values are considered equal to other infinite values of the same sign</li>
     *   <li>All other values are considered equal if the absolute value of their difference is
     *       smaller than or equals to the threshold described below.</li>
     * </ul>
     * <p>
     * If {@code relative} is {@code true}, then for any pair of values <var>v1</var><sub>j,i</sub>
     * and <var>v2</var><sub>j,i</sub> to compare, the tolerance threshold is scaled by
     * {@code max(abs(v1), abs(v2))}. Otherwise the threshold is used as-is.
     *
     * @param  m1       The first matrix to compare, or {@code null}.
     * @param  m2       The second matrix to compare, or {@code null}.
     * @param  epsilon  The tolerance value.
     * @param  relative If {@code true}, then the tolerance value is relative to the magnitude
     *         of the matrix elements being compared.
     * @return {@code true} if the values of the two matrix do not differ by a quantity
     *         greater than the given tolerance threshold.
     *
     * @see XMatrix#equals(Matrix, double)
     *
     * @since 3.20 (derived from 2.2)
     */
    public static boolean equals(final Matrix m1, final Matrix m2, final double epsilon, final boolean relative) {
        if (m1 != m2) {
            if (m1 == null || m2 == null) {
                return false;
            }
            final int numRow = m1.getNumRow();
            if (numRow != m2.getNumRow()) {
                return false;
            }
            final int numCol = m1.getNumCol();
            if (numCol != m2.getNumCol()) {
                return false;
            }
            for (int j=0; j<numRow; j++) {
                for (int i=0; i<numCol; i++) {
                    final double v1 = m1.getElement(j, i);
                    final double v2 = m2.getElement(j, i);
                    double tolerance = epsilon;
                    if (relative) {
                        tolerance *= Math.max(Math.abs(v1), Math.abs(v2));
                    }
                    if (!(Math.abs(v1 - v2) <= tolerance)) {
                        if (Utilities.equals(v1, v2)) {
                            // Special case for NaN and infinite values.
                            continue;
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Compares the given matrices for equality, using the given comparison strictness level.
     * To be considered equal, the two matrices must meet the following conditions, which depend
     * on the {@code mode} argument:
     * <p>
     * <ul>
     *   <li><b>{@link ComparisonMode#STRICT STRICT}:</b> the two matrices must be of the same
     *       class, have the same size and the same element values.</li>
     *   <li><b>{@link ComparisonMode#BY_CONTRACT BY_CONTRACT}/{@link ComparisonMode#IGNORE_METADATA
     *       IGNORE_METADATA}:</b> the two matrices must have the same size and the same element
     *       values, but are not required to be the same implementation class (any {@link Matrix}
     *       is okay).</li>
     *   <li><b>{@link ComparisonMode#APPROXIMATIVE APPROXIMATIVE}:</b> the two matrixes must have
     *       the same size, but the element values can differ up to some threshold. The threshold
     *       value is determined empirically and may change in future Geotk versions.</li>
     * </ul>
     *
     * @param  m1  The first matrix to compare, or {@code null}.
     * @param  m2  The second matrix to compare, or {@code null}.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if both matrices are equal.
     *
     * @see XMatrix#equals(Object, ComparisonMode)
     *
     * @since 3.20 (derived from 3.18)
     */
    public static boolean equals(final Matrix m1, final Matrix m2, final ComparisonMode mode) {
        switch (mode) {
            case STRICT:          return Objects.equals(m1, m2);
            case BY_CONTRACT:     // Fall through
            case IGNORE_METADATA: return equals(m1, m2, 0, false);
            case DEBUG:           // Fall through
            case APPROXIMATIVE:   return equals(m1, m2, COMPARISON_THRESHOLD, true);
            default: throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_ENUM_$1, mode));
        }
    }
}
