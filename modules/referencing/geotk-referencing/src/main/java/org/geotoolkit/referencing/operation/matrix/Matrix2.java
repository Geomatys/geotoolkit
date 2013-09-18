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

import java.io.Serializable;
import javax.vecmath.SingularMatrixException;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;


/**
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.operation.matrix.Matrix2}.
 */
@Deprecated
public class Matrix2 implements XMatrix, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7116561372481474290L;

    /**
     * The matrix size, which is {@value}.
     */
    public static final int SIZE = 2;

    /** The first matrix element in the first row.   */ public double m00;
    /** The second matrix element in the first row.  */ public double m01;
    /** The first matrix element in the second row.  */ public double m10;
    /** The second matrix element in the second row. */ public double m11;

    /**
     * Creates a new identity matrix.
     */
    public Matrix2() {
        m00 = m11 = 1;
    }

    /**
     * Creates a new matrix initialized to the specified values.
     *
     * @param m00 The first matrix element in the first row.
     * @param m01 The second matrix element in the first row.
     * @param m10 The first matrix element in the second row.
     * @param m11 The second matrix element in the second row.
     */
    public Matrix2(final double m00, final double m01,
                   final double m10, final double m11)
    {
        this.m00 = m00;
        this.m01 = m01;
        this.m10 = m10;
        this.m11 = m11;
    }

    /**
     * Creates a new matrix initialized to the specified values.
     * The length of the given array must be 4 and the values in
     * the same order than the above constructor.
     *
     * @param elements Elements of the matrix. Column indices vary fastest.
     *
     * @since 3.00
     */
    public Matrix2(final double[] elements) {
        if (elements.length != (SIZE*SIZE)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
        m00 = elements[0];
        m01 = elements[1];
        m10 = elements[2];
        m11 = elements[3];
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one.
     * The specified matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     *
     * @param matrix The matrix to copy.
     * @throws IllegalArgumentException if the given matrix is not of the expected size.
     */
    public Matrix2(final Matrix matrix) throws IllegalArgumentException {
        if (matrix.getNumRow() != SIZE || matrix.getNumCol() != SIZE) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_MATRIX_SIZE));
        }
        m00 = matrix.getElement(0,0);
        m01 = matrix.getElement(0,1);
        m10 = matrix.getElement(1,0);
        m11 = matrix.getElement(1,1);
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
     * Returns the number of columns in this matrix, which is always {@value #SIZE}
     * in this implementation.
     */
    @Override
    public final int getNumCol() {
        return SIZE;
    }

    /**
     * Retrieves the value at the specified row and column of this matrix.
     */
    @Override
    public final double getElement(final int row, final int column) {
        switch (row) {
            case 0: {
                switch (column) {
                    case 0: return m00;
                    case 1: return m01;
                }
                break;
            }
            case 1: {
                switch (column) {
                    case 0: return m10;
                    case 1: return m11;
                }
                break;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Modifies the value at the specified row and column of this matrix.
     */
    @Override
    public final void setElement(final int row, final int column, final double value) {
        switch (row) {
            case 0: {
                switch (column) {
                    case 0: m00 = value; return;
                    case 1: m01 = value; return;
                }
                break;
            }
            case 1: {
                switch (column) {
                    case 0: m10 = value; return;
                    case 1: m11 = value; return;
                }
                break;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setZero() {
        m00 = m01 = m10 = m11 = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setIdentity() {
        m01 = m10 = 0;
        m00 = m11 = 1;
        assert isIdentity();
    }

    /**
     * Returns {@code true} if this matrix is an identity matrix.
     */
    @Override
    public final boolean isIdentity() {
        return m01==0 && m10==0 && m00==1 && m11==1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isIdentity(double tolerance) {
        return GeneralMatrix.isIdentity(this, tolerance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isAffine() {
        return m10 == 0 && m11 == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void negate() {
        m00 = -m00;
        m01 = -m01;
        m10 = -m10;
        m11 = -m11;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void transpose() {
        final double swap = m10;
        m10 = m01;
        m01 = swap;
    }

    /**
     * Inverts this matrix in place.
     *
     * @throws SingularMatrixException If the matrix can't be inverted.
     */
    @Override
    public final void invert() throws SingularMatrixException {
        final double det = m00*m11 - m01*m10;
        if (det == 0) {
            throw new SingularMatrixException();
        }
        final double swap = m00;
        m00 =  m11 / det;
        m11 = swap / det;
        m10 = -m10 / det;
        m01 = -m01 / det;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void multiply(final Matrix matrix) {
        final Matrix2 k;
        if (matrix instanceof Matrix2) {
            k = (Matrix2) matrix;
        } else {
            k = new Matrix2(matrix);
        }
        double m0, m1;
        m0=m00; m1=m01;
        m00 = m0*k.m00 + m1*k.m10;
        m01 = m0*k.m01 + m1*k.m11;
        m0=m10; m1=m11;
        m10 = m0*k.m00 + m1*k.m10;
        m11 = m0*k.m01 + m1*k.m11;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void normalizeColumns() {
        double m;
        m = Math.hypot(m00, m10); m00 /= m; m10 /= m;
        m = Math.hypot(m01, m11); m01 /= m; m11 /= m;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Matrix matrix, final double tolerance) {
        return Matrices.equals(this, matrix, tolerance, false);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.18
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        return (object instanceof Matrix) && Matrices.equals(this, (Matrix) object, mode);
    }

    /**
     * Returns {@code true} if the specified object is of type {@code Matrix2} and
     * all of the data members are equal to the corresponding data members in this matrix.
     *
     * @param object The object to compare with this matrix for equality.
     * @return {@code true} if the given object is equal to this matrix.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final Matrix2 that = (Matrix2) object;
            return Utilities.equals(this.m00, that.m00) &&
                   Utilities.equals(this.m01, that.m01) &&
                   Utilities.equals(this.m10, that.m10) &&
                   Utilities.equals(this.m11, that.m11);
        }
        return false;
    }

    /**
     * Returns a hash code value based on the data values in this object.
     */
    @Override
    public int hashCode() {
        final long code = serialVersionUID ^
                (((Double.doubleToLongBits(m00)  +
              31 * Double.doubleToLongBits(m01)) +
              31 * Double.doubleToLongBits(m10)) +
              31 * Double.doubleToLongBits(m11));
        return ((int) code) ^ ((int) (code >>> 32));
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
     * Returns a clone of this matrix.
     */
    @Override
    public Matrix2 clone() {
        try {
            return (Matrix2) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen, since we are cloneable.
            throw new AssertionError(e);
        }
    }
}
