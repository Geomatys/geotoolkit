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
package org.geotoolkit.internal.referencing;

import java.awt.geom.Point2D;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;
import org.geotoolkit.referencing.operation.transform.DerivableTransform;
import org.geotoolkit.resources.Errors;


/**
 * An implementation of {@link DerivableTransform} that redirect the work to the standard
 * {@code MathTransform} methods. This is used only as a fallback when the math transform
 * does not support fused derive-transform operation natively.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public final class DerivableTransformAdapter extends MathTransformWrapper implements DerivableTransform {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4170799187136209970L;

    /**
     * {@code true} if the derivative calculation is disabled. This field is set to
     * {@code true} if a call to {@link MathTransform#derivative(DirectPosition)} fail.
     */
    private transient boolean disabled;

    /**
     * Creates a new adapter for the given math transform.
     *
     * @param transform The math transform to wrap.
     */
    private DerivableTransformAdapter(final MathTransform transform) {
        super(transform);
    }

    /**
     * If the given transform is an implementation of {@link DerivableTransform}, returns it
     * unchanged. Otherwise wraps the given transform in an {@code DerivableTransformAdapter}
     * instance.
     *
     * @param  transform The math transform to wrap or {@code null}.
     * @return The derivable transform, or {@code null} if the given transform was null.
     */
    public static DerivableTransform wrap(final MathTransform transform) {
        if (transform == null || transform instanceof DerivableTransform) {
            return (DerivableTransform) transform;
        }
        return new DerivableTransformAdapter(transform);
    }

    /**
     * Redirects the work to the standard math transform. If the derivative calculation failed,
     * then this method sets a flag in order to not attempt new derivative anymore. We do that
     * on the assumption that a failure to calculate derivative is caused by the implementation
     * not supporting this functionality.
     */
    @Override
    public XMatrix derivateAndTransform(final DirectPosition ptSrc, final DirectPosition ptDst,
            final XMatrix matrixDst) throws MismatchedDimensionException, TransformException
    {
        XMatrix derivative = null;
        if (!disabled) try {
            derivative = MatrixFactory.toXMatrix(transform.derivative(ptSrc));
        } catch (TransformException e) {
            disabled = true;
            log(e);
        }
        /*
         * Transform the point AFTER the derivative in case ptSrc == ptDst.
         */
        final DirectPosition transformed = transform.transform(ptSrc, ptDst);
        if (transformed != ptDst) {
            // Should never happen in compliant implementation, but let be paranoiac.
            final int dimension = transformed.getDimension();
            final int ptDstDim = ptDst.getDimension();
            if (ptDstDim != dimension) {
                throw new MismatchedDimensionException(Errors.format(
                        Errors.Keys.MISMATCHED_DIMENSION_$3, "ptDst", ptDstDim, dimension));
            }
            for (int i=0; i<dimension; i++) {
                ptDst.setOrdinate(i, transformed.getOrdinate(i));
            }
        }
        return derivative;
    }

    /**
     * Redirects the work to the standard math transform. If the derivative calculation failed,
     * then this method sets a flag in order to not attempt new derivative anymore. We do that
     * on the assumption that a failure to calculate derivative is caused by the implementation
     * not supporting this functionality.
     */
    @Override
    public XMatrix derivateAndTransform(final Point2D ptSrc, final Point2D ptDst,
            final XMatrix matrixDst) throws MismatchedDimensionException, TransformException
    {
        final MathTransform2D transform = (MathTransform2D) this.transform;
        XMatrix derivative = null;
        if (!disabled) try {
            derivative = MatrixFactory.toXMatrix(transform.derivative(ptSrc));
        } catch (TransformException e) {
            disabled = true;
            log(e);
        }
        /*
         * Transform the point AFTER the derivative in case ptSrc == ptDst.
         */
        final Point2D transformed = transform.transform(ptSrc, ptDst);
        if (transformed != ptDst) {
            // Should never happen in compliant implementation, but let be paranoiac.
            ptDst.setLocation(transformed);
        }
        return derivative;
    }

    /**
     * Log a message for a failure to calculate a derivative.
     */
    private static void log(final TransformException e) {
        Logging.recoverableException(Logging.getLogger(DerivableTransform.class),
                DerivableTransformAdapter.class, "derivateAndTransform", e);
    }
}
