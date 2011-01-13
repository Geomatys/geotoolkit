/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import java.awt.geom.AffineTransform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.transform.LinearTransform;


/**
 * Static utility methods for creating matrix. This factory selects one of the {@link Matrix1},
 * {@link Matrix2}, {@link Matrix3}, {@link Matrix4} or {@link GeneralMatrix} implementation
 * according the desired matrix size. Note that if the matrix size is know at compile time,
 * it may be more efficient to invoke directly the constructor of the appropriate class instead.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.2
 * @module
 */
@Static
public final class MatrixFactory {
    /**
     * Do not allows instantiation of this class.
     */
    private MatrixFactory() {
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
     * @param numRow For an affine transform, this is the number of
     *        {@linkplain org.opengis.referencing.operation.MathTransform#getTargetDimensions
     *        target dimensions} + 1.
     * @param numCol For an affine transform, this is the number of
     *        {@linkplain org.opengis.referencing.operation.MathTransform#getSourceDimensions
     *        source dimensions} + 1.
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
     *
     * @since 3.00
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
     * Creates a new matrix which is a copy of the specified matrix.
     *
     * @param matrix The matrix to copy.
     * @return A copy of the given matrix.
     */
    public static XMatrix create(final Matrix matrix) {
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
     * @return The given matrix, or a copy of it as a {@link XMatrix} object, or {@code null}
     *         if the given matrix was {@code null}.
     *
     * @since 3.16 (derived from 3.00)
     */
    public static XMatrix toXMatrix(final Matrix matrix) {
        if (matrix == null || matrix instanceof XMatrix) {
            return (XMatrix) matrix;
        }
        return create(matrix);
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
     * @return The given matrix, or a copy of it as a {@link XMatrix} object.
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
     * @return The given matrix, or a copy of it as a {@link GeneralMatrix} object.
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
}
