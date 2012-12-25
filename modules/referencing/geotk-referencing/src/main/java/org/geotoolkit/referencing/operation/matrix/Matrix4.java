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

import javax.vecmath.Matrix4d;
import org.opengis.referencing.operation.Matrix;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.ComparisonMode;


/**
 * A matrix of fixed {@value #SIZE}&times;{@value #SIZE} size. This specialized matrix provides
 * better accuracy than {@link GeneralMatrix} for matrix inversion and multiplication. It is used
 * primarily for supporting datum shifts.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @module
 */
public class Matrix4 extends Matrix4d implements XMatrix {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5685762518066856310L;

    /**
     * The matrix size, which is {@value}.
     */
    public static final int SIZE = 4;

    /**
     * Creates a new identity matrix.
     */
    public Matrix4() {
        setIdentity();
    }

    /**
     * Creates a new matrix initialized to the specified values.
     *
     * @param m00 The first matrix element in the first row.
     * @param m01 The second matrix element in the first row.
     * @param m02 The third matrix element in the first row.
     * @param m03 The forth matrix element in the first row.
     * @param m10 The first matrix element in the second row.
     * @param m11 The second matrix element in the second row.
     * @param m12 The third matrix element in the second row.
     * @param m13 The forth matrix element in the second row.
     * @param m20 The first matrix element in the third row.
     * @param m21 The second matrix element in the third row.
     * @param m22 The third matrix element in the third row.
     * @param m23 The forth matrix element in the third row.
     * @param m30 The first matrix element in the forth row.
     * @param m31 The second matrix element in the forth row.
     * @param m32 The third matrix element in the forth row.
     * @param m33 The forth matrix element in the forth row.
     */
    public Matrix4(double m00, double m01, double m02, double m03,
                   double m10, double m11, double m12, double m13,
                   double m20, double m21, double m22, double m23,
                   double m30, double m31, double m32, double m33)
    {
        super(m00, m01, m02, m03,
              m10, m11, m12, m13,
              m20, m21, m22, m23,
              m30, m31, m32, m33);
    }

    /**
     * Creates a new matrix initialized to the specified values.
     * The length of the given array must be 16 and the values in
     * the same order than the above constructor.
     *
     * @param elements Elements of the matrix. Column indices vary fastest.
     *
     * @since 3.00
     */
    public Matrix4(final double[] elements) {
        super(elements[ 0], elements[ 1], elements[ 2], elements[ 3],
              elements[ 4], elements[ 5], elements[ 6], elements[ 7],
              elements[ 8], elements[ 9], elements[10], elements[11],
              elements[12], elements[13], elements[14], elements[15]);
        /*
         * Should have been first if Sun fixed RFE #4093999 in their bug database
         * ("Relax constraint on placement of this()/super() call in constructors").
         */
        if (elements.length != (SIZE*SIZE)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
    }

    /**
     * Creates a new matrix initialized to the same value than the specified one.
     * The specified matrix size must be {@value #SIZE}&times;{@value #SIZE}.
     *
     * @param matrix The matrix to copy.
     * @throws IllegalArgumentException if the given matrix is not of the expected size.
     */
    public Matrix4(final Matrix matrix) throws IllegalArgumentException {
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
        return m30==0 && m31==0 && m32==0 && m33==1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void multiply(final Matrix matrix) {
        final Matrix4d m;
        if (matrix instanceof Matrix4d) {
            m = (Matrix4d) matrix;
        } else {
            m = new Matrix4(matrix);
        }
        mul(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void normalizeColumns() {
        double m;
        final double[] v = new double[4];
        v[0]=m00; v[1]=m10; v[2]=m20; v[3]=m30; m = MathFunctions.magnitude(v); m00 /= m; m10 /= m; m20 /= m; m30 /= m;
        v[0]=m01; v[1]=m11; v[2]=m21; v[3]=m31; m = MathFunctions.magnitude(v); m01 /= m; m11 /= m; m21 /= m; m31 /= m;
        v[0]=m02; v[1]=m12; v[2]=m22; v[3]=m32; m = MathFunctions.magnitude(v); m02 /= m; m12 /= m; m22 /= m; m32 /= m;
        v[0]=m03; v[1]=m13; v[2]=m23; v[3]=m33; m = MathFunctions.magnitude(v); m03 /= m; m13 /= m; m23 /= m; m33 /= m;
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
    public Matrix4 clone() {
        return (Matrix4) super.clone();
    }
}
