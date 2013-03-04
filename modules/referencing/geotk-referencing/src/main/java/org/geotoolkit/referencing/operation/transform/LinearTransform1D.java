/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;

import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.referencing.operation.matrix.Matrix1;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.referencing.operation.provider.Affine;

// We really want to use doubleToRawLongBits, not doubleToLongBits, because the
// coverage module needs the raw bits for differentiating various NaN values.
import static java.lang.Double.doubleToRawLongBits;
import static org.geotoolkit.util.Utilities.hash;


/**
 * A one dimensional, linear transform. Input values <var>x</var> are converted into
 * output values <var>y</var> using the following equation:
 *
 * <blockquote><var>y</var> &nbsp;=&nbsp;
 * <var>x</var> &times; {@linkplain #scale} + {@linkplain #offset}</blockquote>
 *
 * This class is the same as a 2&times;2 affine transform. However, this specialized
 * {@code LinearTransform1D} class is faster. This kind of transform is extensively
 * used by {@link org.geotoolkit.coverage.grid.GridCoverage2D}.
 * <p>
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Affine}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see LogarithmicTransform1D
 * @see ExponentialTransform1D
 *
 * @since 2.0
 * @module
 */
@Immutable
public class LinearTransform1D extends AbstractMathTransform1D implements LinearTransform, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -7595037195668813000L;

    /**
     * The identity transform.
     */
    public static final LinearTransform1D IDENTITY = new IdentityTransform1D();

    /**
     * The value which is multiplied to input values.
     */
    public final double scale;

    /**
     * The value to add to input values.
     */
    public final double offset;

    /**
     * The inverse of this transform. Created only when first needed.
     */
    private transient MathTransform1D inverse;

    /**
     * Constructs a new linear transform. This constructor is provided for subclasses only.
     * Instances should be created using the {@linkplain #create factory method}, which
     * may returns optimized implementations for some particular argument values.
     *
     * @param scale  The {@code scale}  term in the linear equation.
     * @param offset The {@code offset} term in the linear equation.
     *
     * @see #create(double, double)
     */
    protected LinearTransform1D(final double scale, final double offset) {
        this.scale  = scale;
        this.offset = offset;
    }

    /**
     * Constructs a new linear transform.
     *
     * @param scale  The {@code scale}  term in the linear equation.
     * @param offset The {@code offset} term in the linear equation.
     * @return The linear transform for the given scale and offset.
     *
     * @see org.geotoolkit.referencing.operation.MathTransforms#linear(int, double, double)
     */
    public static LinearTransform1D create(final double scale, final double offset) {
        if (scale == 0) {
            return new ConstantTransform1D(offset);
        }
        if (scale == 1 && offset == 0) {
            return IDENTITY;
        }
        return new LinearTransform1D(scale, offset);
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return Affine.PARAMETERS;
    }

    /**
     * Returns the matrix elements as a group of parameters values. The number of parameters
     * depends on the matrix size. Only matrix elements different from their default value
     * will be included in this group.
     *
     * @return A copy of the parameter values for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return ProjectiveTransform.getParameterValues(getMatrix());
    }

    /**
     * Returns this transform as an affine transform matrix.
     */
    @Override
    public Matrix getMatrix() {
        return new Matrix2(scale, offset, 0, 1);
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform1D inverse() throws NoninvertibleTransformException {
        if (inverse == null) {
            if (isIdentity()) {
                inverse = this;
            } else if (scale != 0) {
                final LinearTransform1D inverse;
                inverse = create(1/scale, -offset/scale);
                inverse.inverse = this;
                this.inverse = inverse;
            } else {
                inverse = super.inverse();
            }
        }
        return inverse;
    }

    /**
     * Tests whether this transform does not move any points.
     */
    @Override
    public boolean isIdentity() {
       return isIdentity(0);
    }

    /**
     * Tests whether this transform does not move any points by using the provided tolerance.
     *
     * @since 2.3.1
     *
     * @see org.geotoolkit.referencing.operation.matrix.XMatrix#isIdentity(double)
     */
    @Override
    public boolean isIdentity(double tolerance) {
        tolerance = Math.abs(tolerance);
        return Math.abs(offset) <= tolerance && Math.abs(scale-1) <= tolerance;
    }

    /**
     * Gets the derivative of this transform at a point.
     *
     * @param point Ignored for a linear transform. Can be null.
     * @return The derivative at the given point.
     */
    @Override
    public Matrix derivative(final DirectPosition point) throws TransformException {
        return new Matrix1(scale);
    }

    /**
     * Gets the derivative of this function at a value.
     *
     * @param  value Ignored for a linear transform. Can be {@link Double#NaN NaN}.
     * @return The derivative at the given point.
     */
    @Override
    public double derivative(final double value) {
        return scale;
    }

    /**
     * Transforms the specified value.
     */
    @Override
    public double transform(double value) {
        return offset + scale*value;
    }

    /**
     * Transforms a single point in the given array and opportunistically computes its derivative
     * if requested. The default implementation computes all those values from the {@link #scale}
     * and {@link #offset} coefficients.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate)
    {
        if (dstPts != null) {
            dstPts[dstOff] = offset + scale*srcPts[srcOff];
        }
        return derivate ? new Matrix1(scale) : null;
    }

    /**
     * Transforms many coordinates in a list of ordinal values. The default implementation
     * computes the values from the {@link #scale} and {@link #offset} coefficients.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        if (srcPts!=dstPts || srcOff>=dstOff) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = offset + scale*srcPts[srcOff++];
            }
        } else {
            srcOff += numPts;
            dstOff += numPts;
            while (--numPts >= 0) {
                dstPts[--dstOff] = offset + scale*srcPts[--srcOff];
            }
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values. The default implementation
     * computes the values from the {@link #scale} and {@link #offset} coefficients using
     * the {@code double} precision, then casts the result to the {@code float} type.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        if (srcPts!=dstPts || srcOff>=dstOff) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = (float) (offset + scale*srcPts[srcOff++]);
            }
        } else {
            srcOff += numPts;
            dstOff += numPts;
            while (--numPts >= 0) {
                dstPts[--dstOff] = (float) (offset + scale*srcPts[--srcOff]);
            }
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values. The default implementation
     * computes the values from the {@link #scale} and {@link #offset} coefficients using
     * the {@code double} precision, then casts the result to the {@code float} type.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final float [] dstPts, int dstOff, int numPts)
    {
        while (--numPts >= 0) {
            dstPts[dstOff++] = (float) (offset + scale*srcPts[srcOff++]);
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values. The default implementation
     * computes the values from the {@link #scale} and {@link #offset} coefficients.
     */
    @Override
    public void transform(final float [] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
    {
        while (--numPts >= 0) {
            dstPts[dstOff++] = offset + scale*srcPts[srcOff++];
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(doubleToRawLongBits(offset), hash(scale, super.computeHashCode()));
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
            final LinearTransform1D that = (LinearTransform1D) object;
            return doubleToRawLongBits(this.scale)  == doubleToRawLongBits(that.scale) &&
                   doubleToRawLongBits(this.offset) == doubleToRawLongBits(that.offset);
            /*
             * NOTE: 'LinearTransform1D' and 'ConstantTransform1D' are heavily used by 'Category'
             * from 'org.geotoolkit.coverage' package. It is essential for Cateory to differenciate
             * various NaN values. Because 'equals' is used by WeakHashSet.unique(Object) (which
             * is used by 'DefaultMathTransformFactory'), test for equality can't use the non-raw
             * doubleToLongBits method because it collapse all NaN into a single canonical value.
             * The 'doubleToRawLongBits' method instead provides the needed functionality.
             */
        }
        return false;
    }
}
