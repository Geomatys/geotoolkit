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
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size. This trivial matrix is returned as a
 * result of {@linkplain org.opengis.referencing.operation.MathTransform1D} derivative computation.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @module
 */
public class Matrix1 implements XMatrix, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4829171016106097031L;

    /**
     * The only element in this matrix.
     */
    public double m00;

    /**
     * The matrix size, which is {@value}.
     */
    public static final int SIZE = 1;

    /**
     * Creates a new identity matrix.
     */
    public Matrix1() {
        m00 = 1;
    }

    /**
     * Creates a new matrix initialized to the specified value.
     *
     * @param m00 The element in this matrix.
     */
    public Matrix1(final double m00) {
        this.m00 = m00;
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one.
     * The specified matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     *
     * @param matrix The matrix to copy.
     * @throws IllegalArgumentException if the given matrix is not of the expected size.
     */
    public Matrix1(final Matrix matrix) throws IllegalArgumentException {
        if (matrix.getNumRow() != SIZE || matrix.getNumCol() != SIZE) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_MATRIX_SIZE));
        }
        m00 = matrix.getElement(0,0);
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
        if (row == 0 && column == 0) {
            return m00;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Modifies the value at the specified row and column of this matrix.
     */
    @Override
    public final void setElement(final int row, final int column, final double value) {
        if (row == 0 && column == 0) {
            m00 = value;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setZero() {
        m00 = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setIdentity() {
        m00 = 1;
    }

    /**
     * Returns {@code true} if this matrix is an identity matrix.
     */
    @Override
    public final boolean isIdentity() {
        return m00 == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isIdentity(double tolerance) {
        return Math.abs(m00 - 1) <= Math.abs(tolerance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isAffine() {
        return m00 == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void negate() {
        m00 = -m00;
    }

    /**
     * Sets the value of this matrix to its transpose.
     * For a 1&times;1 matrix, this method does nothing.
     */
    @Override
    public final void transpose() {
        // Nothing to do for a 1x1 matrix.
    }

    /**
     * Inverts this matrix in place.
     */
    @Override
    public final void invert() {
        if (m00 == 0) {
            throw new SingularMatrixException();
        }
        m00 = 1/m00;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void multiply(final Matrix matrix) {
        if (matrix.getNumRow()!=SIZE || matrix.getNumCol()!=SIZE) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_MATRIX_SIZE));
        }
        m00 *= matrix.getElement(0,0);
    }

    /**
     * Normalizes all columns in-place. For a 1&times;1 matrix,
     * this method just sets unconditionally the {@link #m00} value to 1.
     */
    @Override
    public final void normalizeColumns() {
        m00 = 1;
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
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        return (object instanceof Matrix) && Matrices.equals(this, (Matrix) object, mode);
    }

    /**
     * Returns {@code true} if the specified object is of type {@code Matrix1} and
     * all of the data members are equal to the corresponding data members in this matrix.
     *
     * @param object The object to compare with this matrix for equality.
     * @return {@code true} if the given object is equal to this matrix.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final Matrix1 that = (Matrix1) object;
            return Utilities.equals(m00, that.m00);
        }
        return false;
    }

    /**
     * Returns a hash code value based on the data values in this object.
     */
    @Override
    public int hashCode() {
        final long code = Double.doubleToLongBits(m00) ^ serialVersionUID;
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
    public Matrix1 clone() {
        try {
            return (Matrix1) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should not happen, since we are cloneable.
            throw new AssertionError(e);
        }
    }
}
