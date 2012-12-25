/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import java.io.Serializable;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.operation.provider.Affine;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.MathTransforms;


/**
 * The identity transform. The data are only copied without any transformation. Instance of this
 * class are created for identity transform of dimension greater than 2. For 1D and 2D identity
 * transforms, {@link LinearTransform1D} and {@link AffineTransform2D} already provide their own
 * optimizations.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@Immutable
final class IdentityTransform extends AbstractMathTransform implements LinearTransform, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5339040282922138164L;

    /**
     * The input and output dimension.
     */
    private final int dimension;

    /**
     * Constructs an identity transform of the specified dimension.
     *
     * @param dimension The dimension of the transform to be created.
     *
     * @see MathTransforms#identity(int)
     */
    protected IdentityTransform(final int dimension) {
        this.dimension = dimension;
    }

    /**
     * Tests whether this transform does not move any points.
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean isIdentity() {
        return true;
    }

    /**
     * Tests whether this transform does not move any points.
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean isIdentity(double tolerance) {
        return true;
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public int getSourceDimensions() {
        return dimension;
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public int getTargetDimensions() {
        return dimension;
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return Affine.PARAMETERS;
    }

    /**
     * Returns the matrix elements as a group of parameters values.
     *
     * @return A copy of the parameter values for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return ProjectiveTransform.getParameterValues(getMatrix());
    }

    /**
     * Returns a copy of the identity matrix.
     */
    @Override
    public Matrix getMatrix() {
        return Matrices.create(dimension+1);
    }

    /**
     * Gets the derivative of this transform at a point. For an identity transform,
     * the derivative is the same everywhere.
     */
    @Override
    public Matrix derivative(final DirectPosition point) {
        return Matrices.create(dimension);
    }

    /**
     * Copies the values from {@code ptSrc} to {@code ptDst}.
     * Overrides the super-class method for performance reason.
     *
     * @since 2.2
     */
    @Override
    public DirectPosition transform(final DirectPosition ptSrc, final DirectPosition ptDst) {
        ArgumentChecks.ensureDimensionMatches("ptSrc", dimension, ptSrc);
        if (ptDst == null) {
            return new GeneralDirectPosition(ptSrc);
        }
        ArgumentChecks.ensureDimensionMatches("ptDst", dimension, ptDst);
        for (int i=0; i<dimension; i++) {
            ptDst.setOrdinate(i, ptSrc.getOrdinate(i));
        }
        return ptDst;
    }

    /**
     * Transforms a single coordinate in a list of ordinal values, and optionally returns
     * the derivative at that location.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate)
    {
        if (dstPts != null) {
            System.arraycopy(srcPts, srcOff, dstPts, dstOff, dimension);
        }
        return derivate ? derivative((DirectPosition) null) : null;
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts*dimension);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        System.arraycopy(srcPts, srcOff, dstPts, dstOff, numPts*dimension);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final float [] dstPts, int dstOff, int numPts)
    {
        numPts *= dimension;
        while (--numPts >= 0) {
            dstPts[dstOff++] = (float) srcPts[srcOff++];
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float [] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        numPts *= dimension;
        while (--numPts >= 0) {
            dstPts[dstOff++] = srcPts[srcOff++];
        }
    }

    /**
     * Returns the inverse transform of this object, which
     * is this transform itself
     */
    @Override
    public MathTransform inverse() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) { // Slight optimization
            return true;
        }
        if (mode != ComparisonMode.STRICT) {
            return equals(this, object, mode);
        }
        if (super.equals(object, mode)) {
            final IdentityTransform that = (IdentityTransform) object;
            return this.dimension == that.dimension;
        }
        return false;
    }
}
