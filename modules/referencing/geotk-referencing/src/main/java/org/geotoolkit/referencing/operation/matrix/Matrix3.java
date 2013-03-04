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

import javax.vecmath.Matrix3d;
import java.awt.geom.AffineTransform;
import org.opengis.referencing.operation.Matrix;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;


/**
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size. This specialized matrix provides
 * better accuracy than {@link GeneralMatrix} for matrix inversion and multiplication.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @module
 */
public class Matrix3 extends Matrix3d implements XMatrix {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8902061778871586611L;

    /**
     * The matrix size, which is {@value}.
     */
    public static final int SIZE = 3;

    /**
     * Creates a new identity matrix.
     */
    public Matrix3() {
        setIdentity();
    }

    /**
     * Creates a new matrix initialized to the specified values.
     *
     * @param m00 The first matrix element in the first row.
     * @param m01 The second matrix element in the first row.
     * @param m02 The third matrix element in the first row.
     * @param m10 The first matrix element in the second row.
     * @param m11 The second matrix element in the second row.
     * @param m12 The third matrix element in the second row.
     * @param m20 The first matrix element in the third row.
     * @param m21 The second matrix element in the third row.
     * @param m22 The third matrix element in the third row.
     */
    public Matrix3(double m00, double m01, double m02,
                   double m10, double m11, double m12,
                   double m20, double m21, double m22)
    {
        super(m00, m01, m02,
              m10, m11, m12,
              m20, m21, m22);
    }

    /**
     * Creates a new matrix initialized to the specified values.
     * The length of the given array must be 9 and the values in
     * the same order than the above constructor.
     *
     * @param elements Elements of the matrix. Column indices vary fastest.
     *
     * @since 3.00
     */
    public Matrix3(final double[] elements) {
        super(elements[0], elements[1], elements[2],
              elements[3], elements[4], elements[5],
              elements[6], elements[7], elements[8]);
        /*
         * Should have been first if Sun fixed RFE #4093999 in their bug database
         * ("Relax constraint on placement of this()/super() call in constructors").
         */
        if (elements.length != (SIZE*SIZE)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
    }

    /**
     * Constructs a 3&times;3 matrix from the specified affine transform.
     *
     * @param transform The affine transform to copy.
     *
     * @see #toAffineTransform
     */
    public Matrix3(final AffineTransform transform) {
        setMatrix(transform);
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one.
     * The specified matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     *
     * @param matrix The matrix to copy.
     * @throws IllegalArgumentException if the given matrix is not of the expected size.
     */
    public Matrix3(final Matrix matrix) throws IllegalArgumentException {
        if (matrix.getNumRow() != SIZE || matrix.getNumCol() != SIZE) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_MATRIX_SIZE));
        }
        for (int j=0; j<SIZE; j++) {
            for (int i=0; i<SIZE; i++) {
                setElement(j,i, matrix.getElement(j,i));
            }
        }
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
     * Returns {@code true} if this matrix is an identity matrix.
     */
    @Override
    public final boolean isIdentity() {
        for (int j=0; j<SIZE; j++) {
            for (int i=0; i<SIZE; i++) {
                if (getElement(j,i) != ((i==j) ? 1 : 0)) {
                    return false;
                }
            }
        }
        return true;
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
        return m20==0 && m21==0 && m22==1;
    }

    /**
     * Returns {@code true} if at least one coefficient value is {@code NaN}.
     *
     * @return {@code true} if at least one coefficient value is {@code NaN}.
     *
     * @since 2.3
     */
    public final boolean isNaN() {
        return Double.isNaN(m00) || Double.isNaN(m01) || Double.isNaN(m02) ||
               Double.isNaN(m10) || Double.isNaN(m11) || Double.isNaN(m12) ||
               Double.isNaN(m20) || Double.isNaN(m21) || Double.isNaN(m22);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void multiply(final Matrix matrix) {
        final Matrix3d m;
        if (matrix instanceof Matrix3d) {
            m = (Matrix3d) matrix;
        } else {
            m = new Matrix3(matrix);
        }
        mul(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void normalizeColumns() {
        double m;
        final double[] v = new double[3];
        v[0]=m00; v[1]=m10; v[2]=m20; m = MathFunctions.magnitude(v); m00 /= m; m10 /= m; m20 /= m;
        v[0]=m01; v[1]=m11; v[2]=m21; m = MathFunctions.magnitude(v); m01 /= m; m11 /= m; m21 /= m;
        v[0]=m02; v[1]=m12; v[2]=m22; m = MathFunctions.magnitude(v); m02 /= m; m12 /= m; m22 /= m;
    }

    /**
     * Sets this matrix to the specified affine transform.
     *
     * @param transform The affine transform to copy.
     *
     * @since 2.3
     */
    public void setMatrix(final AffineTransform transform) {
        m00=transform.getScaleX(); m01=transform.getShearX(); m02=transform.getTranslateX();
        m10=transform.getShearY(); m11=transform.getScaleY(); m12=transform.getTranslateY();
        m20=0;                     m21=0;                     m22=1;
    }

    /**
     * Returns an affine transform for this matrix.
     * This is a convenience method for inter-operability with Java2D.
     *
     * @return The affine transform for this matrix.
     * @throws IllegalStateException if the last row is not {@code [0 0 1]}.
     *
     * @since 3.00
     */
    public AffineTransform toAffineTransform() throws IllegalStateException {
        if (isAffine()) {
            return new AffineMatrix3(m00, m10, m01, m11, m02, m12);
        }
        throw new IllegalStateException(Errors.format(Errors.Keys.NOT_AN_AFFINE_TRANSFORM));
    }

    /**
     * Returns {@code true} if this matrix is equal to the specified affine transform.
     *
     * @param transform The affine transform to test for equality.
     * @return {@code true} if the given affine transform has the same coefficients than this matrix.
     *
     * @since 2.3
     */
    public boolean equalsAffine(final AffineTransform transform) {
        return m20==0 && m21==0 && m22==1 &&
               Utilities.equals(m00, transform.getScaleX()) &&
               Utilities.equals(m11, transform.getScaleY()) &&
               Utilities.equals(m01, transform.getShearX()) &&
               Utilities.equals(m10, transform.getShearY()) &&
               Utilities.equals(m02, transform.getTranslateX()) &&
               Utilities.equals(m12, transform.getTranslateY());
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
    public Matrix3 clone() {
        return (Matrix3) super.clone();
    }
}
