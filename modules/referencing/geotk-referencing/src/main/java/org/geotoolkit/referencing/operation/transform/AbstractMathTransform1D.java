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
package org.geotoolkit.referencing.operation.transform;

import net.jcip.annotations.ThreadSafe;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.referencing.operation.matrix.Matrix1;

import static org.apache.sis.util.ArgumentChecks.ensureDimensionMatches;


/**
 * Base class for math transforms that are known to be one-dimensional in all cases.
 * One-dimensional math transforms are <strong>not</strong> required to extend this
 * class, however doing so may simplify their implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.17
 * @module
 */
@ThreadSafe
public abstract class AbstractMathTransform1D extends AbstractMathTransform implements MathTransform1D {
    /**
     * Constructs a default math transform.
     */
    protected AbstractMathTransform1D() {
    }

    /**
     * Returns the dimension of input points, which is always 1.
     */
    @Override
    public final int getSourceDimensions() {
        return 1;
    }

    /**
     * Returns the dimension of output points, which is always 1.
     */
    @Override
    public final int getTargetDimensions() {
        return 1;
    }

    /**
     * Transforms a single point in the given array and opportunistically computes its derivative
     * if requested. The default implementation delegates to {@link #transform(double)} and
     * potentially to {@link #derivative(double)}. Subclasses may override this method for
     * performance reason.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws TransformException
    {
        final double ordinate = srcPts[srcOff];
        if (dstPts != null) {
            dstPts[dstOff] = transform(ordinate);
        }
        return derivate ? new Matrix1(derivative(ordinate)) : null;
    }

    /**
     * Gets the derivative of this transform at a point. The default implementation ensures that
     * {@code point} is one-dimensional, then delegates to {@link #derivative(double)}.
     *
     * @param  point The coordinate point where to evaluate the derivative, or {@code null}.
     * @return The derivative at the specified point (never {@code null}).
     * @throws MismatchedDimensionException if {@code point} doesn't have the expected dimension.
     * @throws TransformException if the derivative can't be evaluated at the specified point.
     */
    @Override
    public Matrix derivative(final DirectPosition point) throws TransformException {
        final double ordinate;
        if (point == null) {
            ordinate = Double.NaN;
        } else {
            ensureDimensionMatches("point", 1, point);
            ordinate = point.getOrdinate(0);
        }
        return new Matrix1(derivative(ordinate));
    }

    /**
     * Returns the inverse transform of this object. The default implementation
     * returns {@code this} if this transform is an identity transform, and throws
     * a {@link NoninvertibleTransformException} otherwise. Subclasses should override
     * this method.
     */
    @Override
    public MathTransform1D inverse() throws NoninvertibleTransformException {
        return (MathTransform1D) super.inverse();
    }
}
