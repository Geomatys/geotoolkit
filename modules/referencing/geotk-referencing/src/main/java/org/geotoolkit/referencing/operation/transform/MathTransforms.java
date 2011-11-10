/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.referencing.operation.transform;

import java.awt.geom.AffineTransform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.internal.referencing.DirectPositionView;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransform2D;


/**
 * Utility methods for creating Geotk implementations of {@link MathTransform},
 * and for performing some operations on arbitrary instances.
 * <p>
 * The factory static methods are provided as convenient alternatives to the GeoAPI
 * {@link org.opengis.referencing.operation.MathTransformFactory} interface. However
 * users seeking for more implementation neutrality are encouraged to limit themselves
 * to the GeoAPI factory interfaces instead.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see org.opengis.referencing.operation.MathTransformFactory
 *
 * @since 3.20
 * @module
 */
public final class MathTransforms extends Static {
    /**
     * Identity transforms for dimensions ranging from to 0 to 7.
     * Elements in this array will be created only when first requested.
     *
     * @see #identity(int)
     */
    private static final LinearTransform[] IDENTITIES = new LinearTransform[8];

    /**
     * Do not allow instantiation of this class.
     */
    private MathTransforms() {
    }

    /**
     * Returns an identity transform of the specified dimension. In the special case of
     * dimension 1 and 2, this method returns instances of {@link LinearTransform1D} or
     * {@link AffineTransform2D} respectively.
     *
     * @param dimension The dimension of the transform to be returned.
     * @return An identity transform of the specified dimension.
     */
    public static LinearTransform identity(final int dimension) {
        LinearTransform candidate;
        synchronized (IDENTITIES) {
            if (dimension < IDENTITIES.length) {
                candidate = IDENTITIES[dimension];
                if (candidate != null) {
                    return candidate;
                }
            }
            switch (dimension) {
                case 1:  candidate = LinearTransform1D.IDENTITY;       break;
                case 2:  candidate = new AffineTransform2D();          break;
                default: candidate = new IdentityTransform(dimension); break;
            }
            if (dimension < IDENTITIES.length) {
                IDENTITIES[dimension] = candidate;
            }
        }
        return candidate;
    }

    /**
     * Creates an affine transform that apply the same linear conversion for all dimensions.
     * For each dimension, input values <var>x</var> are converted into output values
     * <var>y</var> using the following equation:
     *
     * <blockquote><var>y</var> &nbsp;=&nbsp; <var>x</var> &times; {@code scale} + {@code offset}</blockquote>
     *
     * @param dimension The input and output dimensions.
     * @param scale  The {@code scale}  term in the linear equation.
     * @param offset The {@code offset} term in the linear equation.
     * @return The linear transform for the given scale and offset.
     */
    public static LinearTransform linear(final int dimension, final double scale, final double offset) {
        if (offset == 0 && scale == 1) {
            return identity(dimension);
        }
        if (dimension == 1) {
            return LinearTransform1D.create(scale, offset);
        }
        final Matrix matrix = Matrices.create(dimension + 1);
        for (int i=0; i<dimension; i++) {
            matrix.setElement(i, i, scale);
            matrix.setElement(i, dimension, offset);
        }
        return linear(matrix);
    }

    /**
     * Creates an arbitrary linear transform from the specified matrix.
     * If the transform input dimension is {@code M}, and output dimension is {@code N}, then the
     * given matrix shall have size {@code [N+1][M+1]}. The +1 in the matrix dimensions allows the
     * matrix to do a shift, as well as a rotation. The {@code [M][j]} element of the matrix will
     * be the <var>j</var>'th ordinate of the moved origin.
     * <p>
     * The matrix is usually square and affine, but this is not mandatory. Non-affine transforms
     * are allowed.
     *
     * @param matrix The matrix used to define the linear transform.
     * @return The linear transform.
     *
     * @see org.opengis.referencing.operation.MathTransformFactory#createAffineTransform(Matrix)
     */
    public static LinearTransform linear(final Matrix matrix) {
        final int sourceDimension = matrix.getNumCol() - 1;
        final int targetDimension = matrix.getNumRow() - 1;
        if (sourceDimension == targetDimension) {
            if (matrix.isIdentity()) {
                return identity(sourceDimension);
            }
            if (Matrices.isAffine(matrix)) {
                switch (sourceDimension) {
                    case 1: return LinearTransform1D.create(matrix.getElement(0,0), matrix.getElement(0,1));
                    case 2: return linear(Matrices.toAffineTransform(matrix));
                }
            } else if (sourceDimension == 2) {
                return new ProjectiveTransform2D(matrix);
            }
        }
        final LinearTransform candidate = CopyTransform.create(matrix);
        if (candidate != null) {
            return candidate;
        }
        return new ProjectiveTransform(matrix);
    }

    /**
     * Creates an affine transform from the specified
     * <A HREF="http://java.sun.com/products/java-media/2D/index.jsp">Java2D</A> object.
     * The matrix coefficients are used in the same way than {@link #linear(Matrix)}.
     *
     * @param matrix The matrix used to define the affine transform.
     * @return The affine transform.
     */
    public static LinearTransform linear(final AffineTransform matrix) {
        if (matrix instanceof AffineTransform2D) {
            return (AffineTransform2D) matrix;
        }
        return matrix.isIdentity() ? identity(2) : new AffineTransform2D(matrix);
    }

    /**
     * Creates an affine transform that keep only a subset of the source ordinate values.
     * The dimension of source coordinates is {@code sourceDim} and
     * the dimension of target coordinates is {@code toKeep.length}.
     *
     * @param  sourceDim the dimension of source coordinates.
     * @param  toKeep the indices of source ordinate values to keep.
     * @return The affine transform keeping only the given dimensions, and discarding all others.
     * @throws IndexOutOfBoundsException if a value of {@code toKeep}
     *         is lower than 0 or not smaller than {@code sourceDim}.
     *
     * @see Matrices#createDimensionFilter(int, int[])
     */
    public static LinearTransform dimensionFilter(final int sourceDim, final int[] toKeep) throws IndexOutOfBoundsException {
        return linear(Matrices.createDimensionFilter(sourceDim, toKeep));
    }

    /**
     * Concatenates the two given transforms. The returned transform will implement
     * {@link MathTransform1D} or {@link MathTransform2D} if the dimensions of the
     * concatenated transform are equal to 1 or 2 respectively.
     *
     * @param tr1 The first math transform.
     * @param tr2 The second math transform.
     * @return    The concatenated transform.
     *
     * @see org.opengis.referencing.operation.MathTransformFactory#createConcatenatedTransform(MathTransform, MathTransform)
     * @see ConcatenatedTransform#create(MathTransform, MathTransform)
     */
    public static MathTransform concatenate(MathTransform tr1, MathTransform tr2) {
        return ConcatenatedTransform.create(tr1, tr2);
    }

    /**
     * Concatenates the given two-dimensional transforms. This is a convenience methods
     * delegating to {@link #concatenate(MathTransform, MathTransform)} and casting the
     * result to a {@link MathTransform2D} instance.
     *
     * @param tr1 The first math transform.
     * @param tr2 The second math transform.
     * @return    The concatenated transform.
     */
    public static MathTransform2D concatenate(MathTransform2D tr1, MathTransform2D tr2) {
        return (MathTransform2D) concatenate((MathTransform) tr1, (MathTransform) tr2);
    }

    /**
     * Concatenates the given one-dimensional transforms. This is a convenience methods
     * delegating to {@link #concatenate(MathTransform, MathTransform)} and casting the
     * result to a {@link MathTransform1D} instance.
     *
     * @param tr1 The first math transform.
     * @param tr2 The second math transform.
     * @return    The concatenated transform.
     */
    public static MathTransform1D concatenate(MathTransform1D tr1, MathTransform1D tr2) {
        return (MathTransform1D) concatenate((MathTransform) tr1, (MathTransform) tr2);
    }

    /**
     * Concatenates the three given transforms. This is a convenience methods doing its job
     * as two consecutive concatenations.
     *
     * @param tr1 The first math transform.
     * @param tr2 The second math transform.
     * @param tr3 The third math transform.
     * @return    The concatenated transform.
     */
    public static MathTransform concatenate(MathTransform tr1, MathTransform tr2, MathTransform tr3) {
        return concatenate(concatenate(tr1, tr2), tr3);
    }

    /**
     * Concatenates the three given two-dimensional transforms. This is a convenience methods
     * delegating to {@link #concatenate(MathTransform, MathTransform, MathTransform)} and
     * casting the result to a {@link MathTransform2D} instance.
     *
     * @param tr1 The first math transform.
     * @param tr2 The second math transform.
     * @param tr3 The third math transform.
     * @return    The concatenated transform.
     */
    public static MathTransform2D concatenate(MathTransform2D tr1, MathTransform2D tr2, MathTransform2D tr3) {
        return (MathTransform2D) concatenate((MathTransform) tr1, (MathTransform) tr2, (MathTransform) tr3);
    }

    /**
     * Concatenates the three given one-dimensional transforms. This is a convenience methods
     * delegating to {@link #concatenate(MathTransform, MathTransform, MathTransform)} and
     * casting the result to a {@link MathTransform1D} instance.
     *
     * @param tr1 The first math transform.
     * @param tr2 The second math transform.
     * @param tr3 The third math transform.
     * @return    The concatenated transform.
     */
    public static MathTransform1D concatenate(MathTransform1D tr1, MathTransform1D tr2, MathTransform1D tr3) {
        return (MathTransform1D) concatenate((MathTransform) tr1, (MathTransform) tr2, (MathTransform) tr3);
    }

    /**
     * A buckle method for calculating derivative and coordinate transformation in a single step.
     * The results are stored in the given destination objects if non-null. Invoking this method
     * is equivalent to the following code, except that it may execute faster:
     *
     * {@preformat java
     *     DirectPosition ptSrc = ...;
     *     DirectPosition ptDst = ...;
     *     Matrix matrixDst = derivative(ptSrc);
     *     ptDst = transform(ptSrc, ptDst);
     * }
     *
     * @param transform The transform to use.
     * @param srcPts The array containing the source coordinate (can not be {@code null}).
     * @param srcOff The offset to the point to be transformed in the source array.
     * @param dstPts the array into which the transformed coordinate is returned.
     *               May be the same than {@code srcPts}. May be {@code null} if
     *               only the derivative matrix is desired.
     * @param dstOff The offset to the location of the transformed point that is
     *               stored in the destination array.
     * @param derivate {@code true} for computing the derivative, or {@code false} if not needed.
     * @return The matrix of the transform derivative at the given source position, or {@code null}
     *         if the {@code derivate} argument is {@code false} or if this transform does not
     *         support derivative calculation.
     * @throws TransformException If the point can't be transformed or if a problem occurred while
     *         calculating the derivative.
     */
    public static Matrix derivativeAndTransform(
            final MathTransform transform,
            final double[] srcPts, final int srcOff,
            final double[] dstPts, final int dstOff, final boolean derivate)
            throws TransformException
    {
        if (transform instanceof AbstractMathTransform) {
            return ((AbstractMathTransform) transform).transform(srcPts, srcOff, dstPts, dstOff, derivate);
        }
        Matrix derivative = null;
        if (derivate) {
            // Must be calculated before to transform the coordinate.
            derivative = transform.derivative(new DirectPositionView(srcPts, srcOff, transform.getSourceDimensions()));
        }
        if (dstPts != null) {
            transform.transform(srcPts, srcOff, dstPts, dstOff, 1);
        }
        return derivative;
    }
}
