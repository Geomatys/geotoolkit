/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.matrix;

import org.opengis.referencing.operation.Matrix;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Static;


/**
 * Static utility methods for creating matrix. This factory selects one of the {@link Matrix1},
 * {@link Matrix2}, {@link Matrix3}, {@link Matrix4} or {@link GeneralMatrix} implementation
 * according the desired matrix size. Note that if the matrix size is know at compile time,
 * it may be more efficient to invoke directly the constructor of the appropriate class instead.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
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
     * to {@code numRow*numCol}. Column indice vary fastest, as expected by the
     * {@link GeneralMatrix#GeneralMatrix(int,int,double[])} constructor.
     *
     * @param  numRow   Number of rows.
     * @param  numCol   Number of columns.
     * @param  elements Elements of the matrix. Column indice vary fastest.
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
}
