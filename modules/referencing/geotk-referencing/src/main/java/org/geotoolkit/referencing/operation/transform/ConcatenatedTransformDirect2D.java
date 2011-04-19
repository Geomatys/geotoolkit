/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import static org.geotoolkit.internal.referencing.MatrixUtilities.*;


/**
 * Concatenated transform where both transforms are two-dimensional.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
@Immutable
final class ConcatenatedTransformDirect2D extends ConcatenatedTransformDirect implements MathTransform2D {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6009454091075588885L;

    /**
     * Constructs a concatenated transform.
     */
    public ConcatenatedTransformDirect2D(final MathTransform2D transform1,
                                         final MathTransform2D transform2)
    {
        super(transform1, transform2);
    }

    /**
     * Checks if transforms are compatibles with this implementation.
     */
    @Override
    boolean isValid() {
        return super.isValid() && (getSourceDimensions() == 2) && (getTargetDimensions() == 2);
    }

    /**
     * Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}.
     */
    @Override
    public Point2D transform(final Point2D ptSrc, Point2D ptDst) throws TransformException {
        assert isValid();
        final MathTransform2D transform1 = (MathTransform2D) this.transform1;
        final MathTransform2D transform2 = (MathTransform2D) this.transform2;
        ptDst = transform1.transform(ptSrc, ptDst);
        return  transform2.transform(ptDst, ptDst);
    }

    /**
     * Gets the derivative of this transform at a point.
     *
     * @param  point The coordinate point where to evaluate the derivative.
     * @return The derivative at the specified point (never {@code null}).
     * @throws TransformException if the derivative can't be evaluated at the specified point.
     */
    @Override
    public Matrix derivative(final Point2D point) throws TransformException {
        final MathTransform2D transform1 = (MathTransform2D) this.transform1;
        final MathTransform2D transform2 = (MathTransform2D) this.transform2;
        final Matrix matrix1 = transform1.derivative(point);
        final Matrix matrix2 = transform2.derivative(transform1.transform(point,null));
        return multiply(matrix2, matrix1);
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform2D inverse() throws NoninvertibleTransformException {
        return (MathTransform2D) super.inverse();
    }
}
