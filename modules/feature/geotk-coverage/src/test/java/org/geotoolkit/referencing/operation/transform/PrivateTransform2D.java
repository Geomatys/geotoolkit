/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.awt.Shape;
import java.awt.geom.Point2D;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.apache.sis.referencing.operation.transform.MathTransformWrapper;


/**
 * A math transform that delegates the work to an other transform. This is used only in order
 * to "hide" the backing transform from {@link WarpFactory} eyes, in order to force it to go
 * through the complete code path instead than some optimized path.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 */
@SuppressWarnings("serial")
final strictfp class PrivateTransform2D extends MathTransformWrapper implements MathTransform2D {
    /**
     * Wraps the given transform.
     */
    PrivateTransform2D(final MathTransform2D transform) {
        super(transform);
    }

    /**
     * Transforms a single coordinate.
     */
    @Override
    public Point2D transform(final Point2D ptSrc, final Point2D ptDst) throws TransformException {
        return ((MathTransform2D) transform).transform(ptSrc, ptDst);
    }

    /**
     * Transforms a shape.
     */
    @Override
    public Shape createTransformedShape(final Shape shape) throws TransformException {
        return ((MathTransform2D) transform).createTransformedShape(shape);
    }

    /**
     * Returns the derivative at the given position.
     */
    @Override
    public Matrix derivative(final Point2D point) throws TransformException {
        return ((MathTransform2D) transform).derivative(point);
    }

    /**
     * Returns the inverse of this math transform.
     */
    @Override
    public MathTransform2D inverse() throws NoninvertibleTransformException {
        return new PrivateTransform2D((MathTransform2D) transform.inverse());
    }
}
