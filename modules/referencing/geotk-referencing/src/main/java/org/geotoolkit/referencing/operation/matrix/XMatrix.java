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

import javax.vecmath.GMatrix;
import javax.vecmath.SingularMatrixException;

import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.util.Cloneable;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.LenientComparable;


/**
 * A matrix able to perform some operations. The GeoAPI {@link Matrix} interface is
 * basically a two dimensional array of numbers. The {@code XMatrix} interface adds
 * {@linkplain #invert inversion} and {@linkplain #multiply multiplication} capabilities
 * among others. It is used as a bridge across various matrix implementations in Java3D
 * ({@link javax.vecmath.Matrix3d}, {@link javax.vecmath.Matrix4d}, {@link GMatrix}).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 * @version 3.20
 *
 * @see MatrixFactory#toXMatrix(Matrix)
 *
 * @since 2.2
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.referencing.operation.matrix.MatrixSIS}.
 */
@Deprecated
public interface XMatrix extends Matrix, LenientComparable, Cloneable {
    /**
     * Sets all the values in this matrix to zero.
     */
    void setZero();

    /**
     * Sets this matrix to the identity matrix.
     */
    void setIdentity();

    /**
     * Returns {@code true} if this matrix is an identity matrix using the provided tolerance.
     * This method is equivalent to computing the difference between this matrix and an identity
     * matrix of identical size, and returning {@code true} if and only if all differences are
     * smaller than or equal to {@code tolerance}.
     *
     * @param tolerance The tolerance value.
     * @return {@code true} if this matrix is close enough to the identity matrix
     *         given the tolerance value.
     *
     * @since 2.4
     */
    boolean isIdentity(double tolerance);

    /**
     * Returns {@code true} if this matrix is an affine transform.
     * A transform is affine if the matrix is square and last row contains
     * only zeros, except in the last column which contains 1.
     *
     * @return {@code true} if this matrix is affine.
     */
    boolean isAffine();

    /**
     * Negates the value of this matrix: {@code this} = {@code -this}.
     */
    void negate();

    /**
     * Sets the value of this matrix to its transpose.
     */
    void transpose();

    /**
     * Inverts this matrix in place.
     *
     * @throws SingularMatrixException if this matrix is not invertible.
     */
    void invert() throws SingularMatrixException;

    /**
     * Sets the value of this matrix to the result of multiplying itself with the specified matrix.
     * In other words, performs {@code this} = {@code this} &times; {@code matrix}. In the context
     * of coordinate transformations, this is equivalent to
     * {@link java.awt.geom.AffineTransform#concatenate AffineTransform.concatenate}:
     * first transforms by the supplied transform and then transform the result by
     * the original transform.
     *
     * @param matrix The matrix to multiply to this matrix.
     */
    void multiply(Matrix matrix);

    /**
     * Normalizes all columns in-place. Each columns in this matrix is considered as a vector.
     * For each column (vector), this method computes the magnitude (vector length) as the square
     * root of the sum of all square values. Then, all values in the column are divided by that
     * magnitude.
     * <p>
     * This method is useful when the matrix is a
     * {@linkplain org.opengis.referencing.operation.MathTransform#derivative transform derivative}.
     * In such matrix, each column is a vector representing the displacement in target space when an
     * ordinate in the source space is increased by one. Invoking this method turns those vectors
     * into unitary vectors, which is useful for forming the basis of a new coordinate system.
     *
     * @since 3.20
     */
    void normalizeColumns();

    /**
     * Compares the given matrices for equality, using the given absolute tolerance threshold.
     * The given matrix does not need to be the same implementation class than this matrix.
     * <p>
     * The matrix elements are compared as below:
     * <p>
     * <ul>
     *   <li>{@link Double#NaN} values are considered equals to all other NaN values</li>
     *   <li>Infinite values are considered equal to other infinite values of the same sign</li>
     *   <li>All other values are considered equal if the absolute value of their difference is
     *       smaller than or equals to the given threshold.</li>
     * </ul>
     *
     * {@note The name of this method is intentionally different than <code>epsilonEquals</code> in
     * order to avoid compile-time ambiguity with the method inherited from <code>GMatrix</code>.}
     *
     * @param matrix    The matrix to compare.
     * @param tolerance The tolerance value.
     * @return {@code true} if this matrix is close enough to the given matrix given the tolerance value.
     *
     * @see Matrices#equals(Matrix, Matrix, double, boolean)
     * @see GMatrix#epsilonEquals(GMatrix, double)
     *
     * @since 2.5
     */
    boolean equals(Matrix matrix, double tolerance);

    /**
     * Compares this matrix with the given object for equality. To be considered equal, the two
     * objects must meet the following conditions, which depend on the {@code mode} argument:
     * <p>
     * <ul>
     *   <li><b>{@link ComparisonMode#STRICT STRICT}:</b> the two matrixes must be of the same
     *       class, have the same size and the same element values.</li>
     *   <li><b>{@link ComparisonMode#BY_CONTRACT BY_CONTRACT}/{@link ComparisonMode#IGNORE_METADATA
     *       IGNORE_METADATA}:</b> the two matrixes must have the same size and the same element
     *       values, but are not required to be the same implementation class (any {@link Matrix}
     *       is okay).</li>
     *   <li><b>{@link ComparisonMode#APPROXIMATIVE APPROXIMATIVE}:</b> the two matrixes must have
     *       the same size, but the element values can differ up to some threshold. The threshold
     *       value is determined empirically and may change in future Geotk versions.</li>
     * </ul>
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if both objects are equal.
     *
     * @see Matrices#equals(Matrix, Matrix, ComparisonMode)
     *
     * @since 3.18
     */
    @Override
    boolean equals(Object object, ComparisonMode mode);

    /**
     * Returns a clone of this matrix.
     */
    @Override
    XMatrix clone();
}
