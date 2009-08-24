/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.io.Serializable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.DirectPosition;

import org.geotoolkit.lang.Decorator;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.io.wkt.Formattable;
import org.geotoolkit.io.wkt.UnformattableObjectException;


/**
 * Encapsulates a reference to the {@linkplain #method} in addition of a {@linkplain #transform}.
 * This is a temporary object returned by providers when the transform dimensions are different
 * than the method dimensions. In such case, we need to attach an other method to the transform
 * with matching dimensions.
 * <p>
 * Most implementations of {@code MathTransformProvider} do not need this class. Nevertheless
 * when instances are created, the {@code DefaultMathTransformFactory} implementation unwraps
 * their {@linkplain #method} and {@linkplain #transform}, and the later is given to the user.
 * Consequently this object is short-lived and most client code will not suffer from the
 * indirection level that it brings when performing coordinate transformations.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.2
 * @level hidden
 * @module
 */
@Decorator(MathTransform.class)
public final class MathTransformDecorator implements MathTransform, Formattable, Serializable {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8844242705205498128L;

    /**
     * The math transform on which to delegate the work.
     */
    public final MathTransform transform;

    /**
     * The provider for the {@linkplain #transform transform}.
     */
    public final OperationMethod method;

    /**
     * Creates a new decorator which delegates its work to the specified math transform.
     *
     * @param transform The math transform created by provider.
     * @param method The provider, typically as an instance of {@code MathTransformProvider}.
     */
    public MathTransformDecorator(final MathTransform transform, final OperationMethod method) {
        this.transform = transform;
        if (transform == null) {
            throw new NullArgumentException("transform");
        }
        this.method = method;
        if (method == null) {
            throw new NullArgumentException("method");
        }
        if (transform.getSourceDimensions() != method.getSourceDimensions() ||
            transform.getTargetDimensions() != method.getTargetDimensions())
        {
            throw new MismatchedDimensionException();
        }
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public int getSourceDimensions() {
        return transform.getTargetDimensions();
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public int getTargetDimensions() {
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
    public DirectPosition transform(final DirectPosition ptSrc, final DirectPosition ptDst)
            throws MismatchedDimensionException, TransformException
    {
        return transform.transform(ptSrc, ptDst);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, final int srcOff,
                          final double[] dstPts, final int dstOff,
                          final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, final int srcOff,
                          final float[] dstPts, final int dstOff,
                          final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float [] srcPts, final int srcOff,
                          final double[] dstPts, final int dstOff,
                          final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, final int srcOff,
                          final float [] dstPts, final int dstOff,
                          final int numPts) throws TransformException
    {
        transform.transform(srcPts, srcOff, dstPts, dstOff, numPts);
    }

    /**
     * Gets the derivative of this transform at a point.
     */
    @Override
    public Matrix derivative(final DirectPosition point) throws TransformException {
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
    public boolean isIdentity() {
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
    public boolean equals(final Object object) {
        if (object!=null && object.getClass().equals(getClass())) {
            final MathTransformDecorator that = (MathTransformDecorator) object;
            return Utilities.equals(this.transform, that.transform);
        }
        return false;
    }

    /**
     * Returns a hash code value for this math transform.
     */
    @Override
    public int hashCode() {
        return transform.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Returns a <cite>Well Known Text</cite> (WKT) for this transform.
     *
     * @throws UnsupportedOperationException If this object can't be formatted as WKT.
     */
    @Override
    public String toWKT() throws UnsupportedOperationException {
        return transform.toWKT();
    }

    /**
     * Returns a string representation for this transform.
     */
    @Override
    public String toString() {
        return transform.toString();
    }

    /**
     * Delegates the WKT formatting to the wrapped math transform. This class is usually used
     * with Geotoolkit implementations of math transform, so the exception is unlikely to be thrown.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        if (transform instanceof Formattable) {
            return ((Formattable) transform).formatWKT(formatter);
        }
        throw new UnformattableObjectException(getClass());
    }
}
