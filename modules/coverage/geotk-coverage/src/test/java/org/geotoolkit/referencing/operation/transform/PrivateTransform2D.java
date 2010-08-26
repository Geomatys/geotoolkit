/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;


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
final class PrivateTransform2D extends AbstractMathTransform implements MathTransform2D {
    /**
     * The wrapped transform.
     */
    private final MathTransform2D tr;

    /**
     * Wraps the given transform.
     */
    PrivateTransform2D(final MathTransform2D tr) {
        this.tr = tr;
    }

    /**
     * Returns the number of source dimensions (should be 2).
     */
    @Override
    public int getSourceDimensions() {
        return tr.getSourceDimensions();
    }

    /**
     * Returns the number of target dimensions (should be 2).
     */
    @Override
    public int getTargetDimensions() {
        return tr.getTargetDimensions();
    }

    /**
     * Transforms a single coordinate.
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff) throws TransformException {
        tr.transform(srcPts, srcOff, dstPts, dstOff, 1);
    }

    /**
     * Returns the derivative at the given position.
     */
    @Override
    public Matrix derivative(final Point2D point) throws TransformException {
        return tr.derivative(point);
    }

    /**
     * Returns the inverse of this math transform.
     */
    @Override
    public MathTransform2D inverse() throws NoninvertibleTransformException {
        return new PrivateTransform2D(tr.inverse());
    }
}
