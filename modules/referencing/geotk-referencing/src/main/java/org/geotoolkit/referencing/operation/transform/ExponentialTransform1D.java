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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform1D;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.parameter.FloatParameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.referencing.operation.MathTransforms;

import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.referencing.operation.provider.Exponential.*;


/**
 * A one dimensional exponential transform. Input values <var>x</var> are converted into
 * output values <var>y</var> using the following equation:
 *
 * <blockquote><var>y</var> &nbsp;=&nbsp; {@linkplain #scale} &middot;
 * {@linkplain #base}<sup><var>x</var></sup></blockquote>
 *
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Exponential}</li>
 * </ul>
 * <p>
 * <b>Tip:</b> If a linear transform is applied before this exponential transform,
 * then the equation can be rewritten as:
 *
 * <blockquote><code>scale</code> &middot; <code>base</code><sup><var>a</var> + <var>b</var>&middot;<var>x</var></sup>
 * &nbsp;=&nbsp; <code>scale</code> &middot; <code>base</code><sup><var>a</var></sup> &middot;
 * (<code>base</code><sup><var>b</var></sup>)<sup><var>x</var></sup></blockquote>
 *
 * It is possible to find back the coefficients of the original linear transform by
 * pre-concatenating a logarithmic transform before the exponential one, as below:
 *
 * {@preformat java
 *   LinearTransform1D linear = (LinearTransform1D) ConcatenatedTransform.create(exponentialTransform,
 *           LogarithmicTransform1D.create(base, -Math.log(scale) / Math.log(base)));
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see LogarithmicTransform1D
 * @see LinearTransform1D
 *
 * @since 2.0
 * @module
 */
@Immutable
public class ExponentialTransform1D extends AbstractMathTransform1D implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 5331178990358868947L;

    /**
     * The base to be raised to a power.
     */
    public final double base;

    /**
     * Natural logarithm of {@link #base}.
     */
    final double lnBase;

    /**
     * The scale value to be multiplied.
     *
     * {@note The scale could be handled by a concatenation with <code>LinearTransform1D</code>
     *        instead than an explicit field in this class. However the <var>scale</var> &middot;
     *        <var>base</var><sup><var>x</var></sup> formula is extensively used as a <cite>transfer
     *        function</cite> in grid coverages. Consequently we keep this explicit field for
     *        performance reasons.}
     */
    public final double scale;

    /**
     * The inverse of this transform. Created only when first needed.
     * Serialized in order to avoid rounding error if this transform
     * is actually the one which was created from the inverse.
     */
    private MathTransform1D inverse;

    /**
     * Constructs a new exponential transform which is the
     * inverse of the supplied logarithmic transform.
     */
    ExponentialTransform1D(final LogarithmicTransform1D inverse) {
        this.base     = inverse.base;
        this.lnBase   = inverse.lnBase;
        this.scale    = Math.pow(base, -inverse.offset);
        this.inverse  = inverse;
    }

    /**
     * Constructs a new exponential transform. This constructor is provided for subclasses only.
     * Instances should be created using the {@linkplain #create factory method}, which
     * may returns optimized implementations for some particular argument values.
     *
     * @param base   The base to be raised to a power.
     * @param scale  The scale value to be multiplied.
     */
    protected ExponentialTransform1D(final double base, final double scale) {
        this.base   = base;
        this.scale  = scale;
        this.lnBase = Math.log(base);
    }

    /**
     * Constructs a new exponential transform with no scale.
     *
     * @param base The base to be raised to a power.
     * @return The math transform.
     *
     * @since 3.17
     */
    public static MathTransform1D create(final double base) {
        return create(base, 1);
    }

    /**
     * Constructs a new exponential transform which include the given scale factor applied
     * after the exponentiation.
     *
     * @param base   The base to be raised to a power.
     * @param scale  The scale value to be multiplied.
     * @return The math transform.
     */
    public static MathTransform1D create(final double base, final double scale) {
        if (base == 0 || scale == 0) {
            return LinearTransform1D.create(0, 0);
        }
        if (base == 1) {
            return LinearTransform1D.create(0, scale);
        }
        return new ExponentialTransform1D(base, scale);
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return PARAMETERS;
    }

    /**
     * Returns the parameter values for this math transform.
     *
     * @return A copy of the parameter values for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return new ParameterGroup(getParameterDescriptors(),
                new FloatParameter(BASE,  base),
                new FloatParameter(SCALE, scale));
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform1D inverse() {
        if (inverse == null) {
            inverse = LogarithmicTransform1D.create(this);
        }
        return inverse;
    }

    /**
     * Gets the derivative of this function at a value.
     */
    @Override
    public double derivative(final double value) {
        return lnBase * transform(value);
    }

    /**
     * Transforms the specified value.
     */
    @Override
    public double transform(final double value) {
        return scale * Math.pow(base, value);
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts) {
        if (srcPts!=dstPts || srcOff>=dstOff) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = scale * Math.pow(base, srcPts[srcOff++]);
            }
        } else {
            srcOff += numPts;
            dstOff += numPts;
            while (--numPts >= 0) {
                dstPts[--dstOff] = scale * Math.pow(base, srcPts[--srcOff]);
            }
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts) {
        if (srcPts!=dstPts || srcOff>=dstOff) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = (float) (scale * Math.pow(base, srcPts[srcOff++]));
            }
        } else {
            srcOff += numPts;
            dstOff += numPts;
            while (--numPts >= 0) {
                dstPts[--dstOff] = (float) (scale * Math.pow(base, srcPts[--srcOff]));
            }
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts) {
        while (--numPts >= 0) {
            dstPts[dstOff++] = (float) (scale * Math.pow(base, srcPts[srcOff++]));
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts) {
        while (--numPts >= 0) {
            dstPts[dstOff++] = scale * Math.pow(base, srcPts[srcOff++]);
        }
    }

    /**
     * Concatenates in an optimized way a {@link MathTransform} {@code other} to this
     * {@code MathTransform}. This implementation can optimize some concatenation with
     * {@link LinearTransform1D} and {@link LogarithmicTransform1D}.
     *
     * @param  other The math transform to apply.
     * @param  applyOtherFirst {@code true} if the transformation order is {@code other}
     *         followed by {@code this}, or {@code false} if the transformation order is
     *         {@code this} followed by {@code other}.
     * @return The combined math transform, or {@code null} if no optimized combined
     *         transform is available.
     */
    @Override
    final MathTransform concatenate(final MathTransform other, final boolean applyOtherFirst) {
        if (other instanceof LinearTransform) {
            final LinearTransform1D linear = (LinearTransform1D) other;
            if (applyOtherFirst) {
                final double newBase  = Math.pow(base, linear.scale);
                final double newScale = Math.pow(base, linear.offset) * scale;
                if (!Double.isNaN(newBase) && !Double.isNaN(newScale)) {
                    return create(newBase, newScale);
                }
            } else {
                if (linear.offset == 0) {
                    return create(base, scale * linear.scale);
                }
            }
        } else if (other instanceof LogarithmicTransform1D) {
            return concatenateLog((LogarithmicTransform1D) other, applyOtherFirst);
        }
        return super.concatenate(other, applyOtherFirst);
    }

    /**
     * Concatenates in an optimized way a {@link LogarithmicTransform1D} {@code other} to this
     * {@code ExponentialTransform1D}.
     *
     * @param  other The math transform to apply.
     * @param  applyOtherFirst {@code true} if the transformation order is {@code other}
     *         followed by {@code this}, or {@code false} if the transformation order is
     *         {@code this} followed by {@code other}.
     * @return The combined math transform, or {@code null} if no optimized combined
     *         transform is available.
     */
    final MathTransform concatenateLog(final LogarithmicTransform1D other, final boolean applyOtherFirst) {
        if (applyOtherFirst) {
            return MathTransforms.concatenate(
                    PowerTransform1D.create(lnBase / other.lnBase),
                    LinearTransform1D.create(scale * Math.pow(base, other.offset), 0));
        } else {
            final double newScale = lnBase / other.lnBase;
            final double newOffset;
            if (scale > 0) {
                newOffset = other.log(scale) + other.offset;
            } else {
                // Maybe the Math.log(...) argument will become
                // positive if we rewrite the equation that way...
                newOffset = other.log(scale * other.offset * other.lnBase);
            }
            if (!Double.isNaN(newOffset)) {
                return LinearTransform1D.create(newScale, newOffset);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(base, hash(scale, super.computeHashCode()));
    }

    /**
     * Compares the specified object with this math transform for equality.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object, mode)) {
            final ExponentialTransform1D that = (ExponentialTransform1D) object;
            return Utilities.equals(this.base,  that.base) &&
                   Utilities.equals(this.scale, that.scale);
        }
        return false;
    }
}
