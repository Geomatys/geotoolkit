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

import java.awt.geom.AffineTransform;
import org.opengis.referencing.operation.Matrix;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Cloneable;


/**
 * An affine matrix of fixed {@value #SIZE}&times;{@value #SIZE} size. Here, the term "affine"
 * means a matrix with the last row fixed to {@code [0,0,1]} values. Such matrices are used for
 * affine transformations in a 2D space.
 * <p>
 * This class both extends the <cite>Java2D</cite> {@link AffineTransform} class and implements
 * the {@link Matrix} interface. It allows inter-operability for code that need to pass the same
 * matrix to both <cite>Java2D</cite> API and more generic API working with coordinates of
 * arbitrary dimension.
 *
 * {@note This class does not implement the <code>XMatrix</code> interface because the
 *        inherited <code>invert()</code> method (new in Java 6) declares a checked exception,
 *        <code>setZero()</code> would be an unsupported operation since it is not possible to
 *        change the value of element <code>(2,2)</code>, <code>transpose()</code> would fail
 *        in most cases and <code>isAffine()</code> would be useless.}
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
public class AffineMatrix3 extends AffineTransform implements Matrix, Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -9104194268576601386L;

    /**
     * The matrix size, which is {@value}.
     */
    public static final int SIZE = 3;

    /**
     * Creates a new identity matrix.
     */
    public AffineMatrix3() {
    }

    /**
     * Constructs a 3&times;3 matrix from the specified element values.
     *
     * @param m00 the X scaling element.
     * @param m10 the Y shearing element.
     * @param m01 the X shearing element.
     * @param m11 the Y scaling element.
     * @param m02 the X translation element.
     * @param m12 the Y translation element.
     *
     * @since 3.00
     */
    public AffineMatrix3(final double m00, final double m10,
                         final double m01, final double m11,
                         final double m02, final double m12)
    {
        super(m00, m10, m01, m11, m02, m12);
    }

    /**
     * Constructs a 3&times;3 matrix from the specified affine transform.
     *
     * @param transform The affine transform to copy.
     */
    public AffineMatrix3(final AffineTransform transform) {
        super(transform);
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one.
     * The specified matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     *
     * @param matrix The matrix to copy.
     * @throws IllegalArgumentException if the given matrix is not of the expected size.
     */
    public AffineMatrix3(final Matrix matrix) throws IllegalArgumentException {
        super(getElements(matrix));
    }

    /**
     * Work around for RFE #4093999 ("relax contraint on placement of call to super constructor").
     */
    private static double[] getElements(final Matrix matrix) {
        if (matrix.getNumRow() != SIZE || matrix.getNumCol() != SIZE) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_MATRIX_SIZE));
        }
        for (int i=0; i<SIZE; i++) {
            checkLastRow(i, matrix.getElement(SIZE-1, i));
        }
        int c = 0;
        final double[] values = new double[6];
        for (int j=0; j<SIZE-1; j++) {
            for (int i=0; i<SIZE; i++) {
                values[c++] = matrix.getElement(j,i);
            }
        }
        assert c == values.length : c;
        return values;
    }

    /**
     * Sets this affine transform to the specified flat matrix.
     */
    private void setTransform(final double[] matrix) {
        setTransform(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
    }

    /**
     * Returns the number of rows in this matrix, which is always {@value #SIZE}
     * in this implementation.
     */
    @Override
    public final int getNumRow() {
        return SIZE;
    }

    /**
     * Returns the number of colmuns in this matrix, which is always {@value #SIZE}
     * in this implementation.
     */
    @Override
    public final int getNumCol() {
        return SIZE;
    }

    /**
     * Retrieves the value at the specified row and column of this matrix.
     *
     * @param row    The row number to be retrieved (zero indexed).
     * @param column The column number to be retrieved (zero indexed).
     * @return The value at the indexed element.
     */
    @Override
    public double getElement(final int row, final int column) {
        switch (row) {
            case 0: {
                switch (column) {
                    case 0: return getScaleX();
                    case 1: return getShearX();
                    case 2: return getTranslateX();
                }
                break;
            }
            case 1: {
                switch (column) {
                    case 0: return getShearY();
                    case 1: return getScaleY();
                    case 2: return getTranslateY();
                }
                break;
            }
            case 2: {
                switch (column) {
                    case 0: // fall through
                    case 1: return 0;
                    case 2: return 1;
                }
                break;
            }
            default: {
                throw new IndexOutOfBoundsException(
                        Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "column", column));
            }
        }
        throw new IndexOutOfBoundsException(Errors.format(
                Errors.Keys.ILLEGAL_ARGUMENT_2, "row", row));
    }

    /**
     * Modifies the value at the specified row and column of this matrix.
     *
     * @param row    The row number to be retrieved (zero indexed).
     * @param column The column number to be retrieved (zero indexed).
     * @param value  The new matrix element value.
     */
    @Override
    public void setElement(final int row, final int column, final double value) {
        if (row<0 || row>=SIZE) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "row", row));
        }
        if (column<0 || column>=SIZE) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "column", column));
        }
        if (row == SIZE-1) {
            checkLastRow(column, value);
            return; // Nothing to set.
        }
        final double[] matrix = new double[6];
        getMatrix(matrix);
        matrix[row*SIZE + column] = value;
        setTransform(matrix);
        assert Double.compare(getElement(row, column), value) == 0 : value;
    }

    /**
     * Check if the specified value is valid for the last row if this matrix.
     * The last row contains only 0 values except the last column which is set to 1.
     * This method throws an exception if the specified value is not the expected one.
     */
    private static void checkLastRow(final int column, final double value)
            throws IllegalArgumentException
    {
        if (value != (column == SIZE-1 ? 1 : 0)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2,
                      "matrix[" + (SIZE-1) + ',' + column + ']', value));
        }
    }

    /**
     * Returns a string representation of this matrix. The returned string is implementation
     * dependent. It is usually provided for debugging purposes only.
     */
    @Override
    public String toString() {
        return GeneralMatrix.toString(this);
    }

    /**
     * Returns a clone of this affine transform.
     */
    @Override
    public AffineMatrix3 clone() {
        return (AffineMatrix3) super.clone();
    }
}
