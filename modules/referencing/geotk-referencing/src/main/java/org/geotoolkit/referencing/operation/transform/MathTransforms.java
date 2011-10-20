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

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.internal.referencing.DirectPositionView;


/**
 * Utility methods about {@link MathTransform}s.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public final class MathTransforms extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private MathTransforms() {
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
