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

import java.awt.geom.Point2D;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.matrix.XMatrix;


/**
 * A {@link MathTransform} capable to {@linkplain #transform(DirectPosition, DirectPosition)
 * transform a point} and compute its {@linkplain #derivative(DirectPosition) derivative} in
 * a single step. This interface does not provide any new functionality compared to the
 * standard methods provided in the {@link MathTransform} interface. However its can be
 * significantly faster because:
 * <p>
 * <ul>
 *   <li>In map projections, a lot of intermediate calculations are the same for coordinate
 *       transforms and the derivative calculations. A merged method avoid performing the
 *       same calculations twice.</li>
 *   <li>This method provides a way to recycle an existing matrix object.</li>
 *   <li>The matrix type is {@link XMatrix} instead than {@link org.opengis.referencing.operation.Matrix},
 *       thus providing more direct access to methods frequently used with derivative like matrix
 *       multiplication, inversion, normalization, <i>etc.</i>.</li>
 *   <li>If derivatives are not supported, then this method returns {@code null}
 *       rather than throwing a {@link TransformException}.
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public interface DerivableTransform extends MathTransform {
    /**
     * Calculates the derivative of the given point and transforms it. The results are stored
     * in the given destination objects if possible. Invoking this method is equivalent to the
     * following code, except that it may execute faster:
     *
     * {@preformat java
     *     DirectPosition ptSrc = ...;
     *     DirectPosition ptDst = ...;
     *     Matrix matrixDst = derivative(ptSrc);
     *     ptDst = transform(ptSrc, ptDst);
     * }
     *
     * The derivative result is stored in the given {@code matrixDst} instance if possible.
     * However the implementation is free to ignore that argument and return a new instance.
     * We allow this flexibility because some implementations may use specialized matrix classes.
     *
     * @param  ptSrc        The coordinate point to transform and where to calculate the derivative.
     * @param  ptDst        A pre-allocated position where to store the transform result.
     * @param  matrixDst    An optional pre-allocated matrix where to store the derivative result,
     *                      or {@code null} if this method should create a new instance itself.
     * @return The derivative matrix, or {@code null} if it can not be calculated. Note that the
     *         returned value is not guaranteed to be the same instance than {@code matrixDst}.
     * @throws MismatchedDimensionException if {@code ptSrc} or {@code ptDst} object don't have
     *         the expected dimension.
     * @throws TransformException if the point can't be transformed or an error occurred
     *         while calculating the derivative.
     *
     * @see #transform(DirectPosition, DirectPosition)
     * @see #derivative(DirectPosition)
     */
    XMatrix derivateAndTransform(DirectPosition ptSrc, DirectPosition ptDst, XMatrix matrixDst)
            throws MismatchedDimensionException, TransformException;

    /**
     * Same as {@link #derivateAndTransform(DirectPosition, DirectPosition, XMatrix)},
     * but with two-dimensional points only. This method is typically used together
     * with {@link MathTransform2D}.
     *
     * {@note This method is provided because it sometime allows a more efficient code then the
     *        general method. However it is user responsibility to ensure that the math transform
     *        is really two-dimensional.}
     *
     * @param  ptSrc        The coordinate point to transform and where to calculate the derivative.
     * @param  ptDst        A pre-allocated position where to store the transform result.
     * @param  matrixDst    An optional pre-allocated matrix where to store the derivative result,
     *                      or {@code null} if this method should create a new instance itself.
     * @return The derivative matrix, or {@code null} if it can not be calculated. Note that the
     *         returned value is not guaranteed to be the same instance than {@code matrixDst}.
     * @throws MismatchedDimensionException if the source and target dimensions of this math
     *         transform are not 2.
     * @throws TransformException if the point can't be transformed or an error occurred
     *         while calculating the derivative.
     *
     * @see MathTransform2D#transform(Point2D, Point2D)
     * @see MathTransform2D#derivative(Point2D)
     */
    XMatrix derivateAndTransform(Point2D ptSrc, Point2D ptDst, XMatrix matrixDst)
            throws MismatchedDimensionException, TransformException;
}
