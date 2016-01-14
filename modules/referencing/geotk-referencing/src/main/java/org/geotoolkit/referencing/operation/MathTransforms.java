/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.awt.geom.AffineTransform;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.geotoolkit.lang.Static;
import org.apache.sis.referencing.operation.matrix.Matrices;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;

import static org.apache.sis.util.ArgumentChecks.*;


/**
 * Utility methods related to {@link MathTransform}s. This class centralizes in one place some of
 * the most commonly used functions from the {@link org.geotoolkit.referencing.operation.transform}
 * package, thus reducing the need to explore that low-level package. The {@code MathTransforms}
 * class provides the following services:
 * <p>
 * <ul>
 *   <li>Create various Geotk implementations of {@link MathTransform}</li>
 *   <li>Perform non-standard operations on arbitrary instances</li>
 * </ul>
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
     * Do not allow instantiation of this class.
     */
    private MathTransforms() {
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
     *
     * @deprecate Moved to Apache SIS {@link org.apache.sis.referencing.operation.transform.MathTransforms}.
     */
    @Deprecated
    public static LinearTransform linear(final int dimension, final double scale, final double offset) {
        ensureStrictlyPositive("dimension", dimension);
        if (offset == 0 && scale == 1) {
            return org.apache.sis.referencing.operation.transform.MathTransforms.identity(dimension);
        }
        if (dimension == 1) {
            return org.apache.sis.referencing.operation.transform.MathTransforms.linear(scale, offset);
        }
        final Matrix matrix = Matrices.createIdentity(dimension + 1);
        for (int i=0; i<dimension; i++) {
            matrix.setElement(i, i, scale);
            matrix.setElement(i, dimension, offset);
        }
        return org.apache.sis.referencing.operation.transform.MathTransforms.linear(matrix);
    }

    /**
     * Creates an affine transform from the specified Java2D object.
     * The matrix coefficients are used in the same way than {@link #linear(Matrix)}.
     *
     * @param matrix The matrix used to define the affine transform.
     * @return The affine transform.
     */
    public static LinearTransform linear(final AffineTransform matrix) {
        ensureNonNull("matrix", matrix);
        if (matrix instanceof LinearTransform) {
            return (LinearTransform) matrix;
        }
        return matrix.isIdentity() ? org.apache.sis.referencing.operation.transform.MathTransforms.identity(2) : new AffineTransform2D(matrix);
    }
}
