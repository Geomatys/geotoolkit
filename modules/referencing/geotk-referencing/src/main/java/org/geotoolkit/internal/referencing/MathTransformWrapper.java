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
package org.geotoolkit.internal.referencing;

import java.util.Objects;
import java.io.Serializable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.DirectPosition;

import org.geotoolkit.lang.Decorator;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.io.wkt.Formatter;
import org.geotoolkit.io.wkt.Formattable;
import org.geotoolkit.io.wkt.UnformattableObjectException;


/**
 * The base class of math transform wrappers. Despite being a concrete class, there is no
 * point to instantiate directly this base class. Instantiate one of the subclasses instead.
 * <p>
 * <strong>Do not implement {@code MathTransform2D} in this base class<strong>. This wrapper is
 * sometime used for hiding the fact that a transform implements the {@code MathTransform2D}
 * interface, typically for testing a different code path in a JUnit test.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.20 (derived from 2.2)
 * @level hidden
 * @module
 */
@Decorator(MathTransform.class)
public class MathTransformWrapper implements MathTransform, Formattable, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5180954407422828265L;

    /**
     * The math transform on which to delegate the work.
     */
    public final MathTransform transform;

    /**
     * Creates a new wrapper which delegates its work to the specified math transform.
     * This constructor is only for subclasses, and sometime for testing purpose.
     *
     * @param transform The math transform created by provider.
     */
    protected MathTransformWrapper(final MathTransform transform) {
        ArgumentChecks.ensureNonNull("transform", transform);
        this.transform = transform;
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public final int getSourceDimensions() {
        return transform.getTargetDimensions();
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public final int getTargetDimensions() {
        return transform.getSourceDimensions();
    }

    /**
     * Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}.
     *
     * @throws MismatchedDimensionException if {@code ptSrc} or
     *         {@code ptDst} doesn't have the expected dimension.
     * @throws TransformException if the point can't be transformed.
     */
    @Override
    public final DirectPosition transform(final DirectPosition ptSrc, final DirectPosition ptDst)
            throws MismatchedDimensionException, TransformException
    {
        return transform.transform(ptSrc, ptDst);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public final void transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public final void transform(final float[] srcPts, final int srcOff,
                                final float[] dstPts, final int dstOff,
                                final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public final void transform(final float [] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public final void transform(final double[] srcPts, final int srcOff,
                                final float [] dstPts, final int dstOff,
                                final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Gets the derivative of this transform at a point.
     */
    @Override
    public final Matrix derivative(final DirectPosition point) throws TransformException {
        return transform.derivative(point);
    }

    /**
     * Returns the inverse of this math transform.
     */
    @Override
    public MathTransform inverse() throws NoninvertibleTransformException {
        return transform.inverse();
    }

    /**
     * Tests whether this transform does not move any points.
     */
    @Override
    public final boolean isIdentity() {
        return transform.isIdentity();
    }

    /**
     * Compares the specified object with this math transform for equality.
     *
     * @param object The object to compare with this transform.
     * @return {@code true} if the given object is of the same class and if the wrapped
     *         transforms are equal.
     */
    @Override
    public final boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final MathTransformWrapper that = (MathTransformWrapper) object;
            return Objects.equals(this.transform, that.transform);
        }
        return false;
    }

    /**
     * Returns a hash code value for this math transform.
     */
    @Override
    public final int hashCode() {
        return getClass().hashCode() ^ transform.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Returns a <cite>Well Known Text</cite> (WKT) for this transform.
     *
     * @throws UnsupportedOperationException If this object can't be formatted as WKT.
     */
    @Override
    public final String toWKT() throws UnsupportedOperationException {
        return transform.toWKT();
    }

    /**
     * Returns a string representation for this transform.
     */
    @Override
    public final String toString() {
        return transform.toString();
    }

    /**
     * Delegates the WKT formatting to the wrapped math transform. This class is usually used
     * with Geotk implementations of math transform, so the exception is unlikely to be thrown.
     */
    @Override
    public final String formatTo(final Formatter formatter) {
        if (transform instanceof Formattable) {
            return ((Formattable) transform).formatTo(formatter);
        }
        throw new UnformattableObjectException(getClass());
    }
}
