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

import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.referencing.operation.provider.Logarithmic.*;


/**
 * A one dimensional, logarithmic transform. This transform is the inverse of
 * {@link ExponentialTransform1D}. Input values <var>x</var> are converted into
 * output values <var>y</var> using the following equation:
 *
 * <blockquote><table><tr>
 *   <td><var>y</var></td>
 *   <td>&nbsp;=&nbsp;</td>
 *   <td>{@linkplain #offset} + log<sub>{@linkplain #base}</sub>(<var>x</var>)</td>
 * </tr><tr>
 *   <td> </td>
 *   <td>&nbsp;=&nbsp;</td>
 *   <td>{@linkplain #offset} + ln(<var>x</var>)/ln({@linkplain #base})</td>
 * </tr></table></blockquote>
 *
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Logarithmic}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see ExponentialTransform1D
 * @see LinearTransform1D
 *
 * @since 2.0
 * @module
 */
@Immutable
public class LogarithmicTransform1D extends AbstractMathTransform1D implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1535101265352133948L;

    /**
     * Tolerance value for floating point comparison.
     */
    private static final double EPS = 1E-8;

    /**
     * The base of the logarithm.
     */
    public final double base;

    /**
     * Natural logarithm of {@link #base}.
     */
    final double lnBase;

    /**
     * The offset to add to the logarithm.
     *
     * {@note The offset could be handled by a concatenation with <code>LinearTransform1D</code>
     *        instead than an explicit field in this class. However the <var>offset</var> +
     *        log<sub>base</sub>(<var>x</var>) formula is extensively used as a <cite>transfer
     *        function</cite> in grid coverages. Consequently we keep this explicit field for
     *        performance reasons.}
     */
    public final double offset;

    /**
     * The inverse of this transform. Created only when first needed.
     * Serialized in order to avoid rounding error if this transform
     * is actually the one which was created from the inverse.
     */
    private MathTransform1D inverse;

    /**
     * Constructs a new logarithmic transform which is the
     * inverse of the supplied exponential transform.
     */
    private LogarithmicTransform1D(final ExponentialTransform1D inverse) {
        this.base    = inverse.base;
        this.lnBase  = inverse.lnBase;
        this.offset  = -Math.log(inverse.scale) / lnBase;
        this.inverse = inverse;
    }

    /**
     * Constructs a new logarithmic transform. This constructor is provided for subclasses only.
     * Instances should be created using the {@linkplain #create factory method}, which
     * may returns optimized implementations for some particular argument values.
     *
     * @param base    The base of the logarithm (typically 10).
     * @param offset  The offset to add to the logarithm.
     */
    protected LogarithmicTransform1D(final double base, final double offset) {
        this.base    = base;
        this.offset  = offset;
        this.lnBase  = Math.log(base);
    }

    /**
     * Constructs a new logarithmic transform which is the
     * inverse of the supplied exponential transform.
     */
    static LogarithmicTransform1D create(final ExponentialTransform1D inverse) {
        if (Math.abs(inverse.base - 10) < EPS) {
            return new Base10(inverse);
        }
        return new LogarithmicTransform1D(inverse);
    }

    /**
     * Constructs a new logarithmic transform without offset.
     *
     * @param base The base of the logarithm (typically 10).
     * @return The math transform.
     *
     * @since 3.17
     */
    public static MathTransform1D create(final double base) {
        return create(base, 0);
    }

    /**
     * Constructs a new logarithmic transform which include the given offset after the
     * logarithm.
     *
     * @param base    The base of the logarithm (typically 10).
     * @param offset  The offset to add to the logarithm.
     * @return The math transform.
     */
    public static MathTransform1D create(final double base, final double offset) {
        if (base == Double.POSITIVE_INFINITY || Math.abs(base) <= EPS) {
            return LinearTransform1D.create(0, offset);
        }
        if (Math.abs(base - 10) < EPS) {
            return new Base10(offset);
        }
        return new LogarithmicTransform1D(base, offset);
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
            new FloatParameter(BASE,   base),
            new FloatParameter(OFFSET, offset));
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform1D inverse() {
        if (inverse == null) {
            inverse = new ExponentialTransform1D(this);
        }
        return inverse;
    }

    /**
     * Gets the derivative of this function at a value.
     */
    @Override
    public double derivative(final double value) {
        return 1 / (lnBase * value);
    }

    /**
     * Returns the logarithm of the given value in the base given to this transform constructor.
     * This method is similar to {@link #transform(double)} except that the offset is not added.
     *
     * @param  value The value for which to compute the log.
     * @return The log of the given value in the base used by this transform.
     *
     * @since 3.17
     */
    double log(final double value) {
        return Math.log(value) / lnBase;
    }

    /**
     * Transforms the specified value.
     */
    @Override
    public double transform(final double value) {
        return Math.log(value) / lnBase + offset;
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts) {
        if (srcPts!=dstPts || srcOff>=dstOff) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = Math.log(srcPts[srcOff++]) / lnBase + offset;
            }
        } else {
            srcOff += numPts;
            dstOff += numPts;
            while (--numPts >= 0) {
                dstPts[--dstOff] = Math.log(srcPts[--srcOff]) / lnBase + offset;
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
                dstPts[dstOff++] = (float) (Math.log(srcPts[srcOff++]) / lnBase + offset);
            }
        } else {
            srcOff += numPts;
            dstOff += numPts;
            while (--numPts >= 0) {
                dstPts[--dstOff] = (float) (Math.log(srcPts[--srcOff]) / lnBase + offset);
            }
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final double[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts) {
        while (--numPts >= 0) {
            dstPts[dstOff++] = (float) (Math.log(srcPts[srcOff++]) / lnBase + offset);
        }
    }

    /**
     * Transforms many coordinates in a list of ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts) {
        while (--numPts >= 0) {
            dstPts[dstOff++] = Math.log(srcPts[srcOff++]) / lnBase + offset;
        }
    }

    /**
     * Special case for base 10 taking advantage of extra precision provided by {@link Math#log10}.
     */
    private static final class Base10 extends LogarithmicTransform1D {
        /** For cross-version compatibility. */
        private static final long serialVersionUID = -5435804027536647558L;

        /** Constructs the inverse of the supplied exponential transform. */
        Base10(final ExponentialTransform1D inverse) {
            super(inverse);
        }

        /** Creates a new instance with the given offset. */
        protected Base10(final double offset) {
            super(10, offset);
        }

        /** {@inheritDoc} */
        @Override
        double log(final double value) {
            return Math.log10(value);
        }

        /** {@inheritDoc} */
        @Override
        public double transform(final double value) {
            return Math.log10(value) + offset;
        }

        /** {@inheritDoc} */
        @Override
        public void transform(final double[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts) {
            if (srcPts!=dstPts || srcOff>=dstOff) {
                while (--numPts >= 0) {
                    dstPts[dstOff++] = Math.log10(srcPts[srcOff++]) + offset;
                }
            } else {
                srcOff += numPts;
                dstOff += numPts;
                while (--numPts >= 0) {
                    dstPts[--dstOff] = Math.log10(srcPts[srcOff++]) + offset;
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public void transform(final float[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts) {
            if (srcPts!=dstPts || srcOff>=dstOff) {
                while (--numPts >= 0) {
                    dstPts[dstOff++] = (float) (Math.log10(srcPts[srcOff++]) + offset);
                }
            } else {
                srcOff += numPts;
                dstOff += numPts;
                while (--numPts >= 0) {
                    dstPts[--dstOff] = (float) (Math.log10(srcPts[srcOff++]) + offset);
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public void transform(final double[] srcPts, int srcOff, final float[] dstPts, int dstOff, int numPts) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = (float) (Math.log10(srcPts[srcOff++]) + offset);
            }
        }

        /** {@inheritDoc} */
        @Override
        public void transform(final float[] srcPts, int srcOff, final double[] dstPts, int dstOff, int numPts) {
            while (--numPts >= 0) {
                dstPts[dstOff++] = Math.log10(srcPts[srcOff++]) + offset;
            }
        }
    }

    /**
     * Concatenates in an optimized way a {@link MathTransform} {@code other} to this
     * {@code MathTransform}. This implementation can optimize some concatenation with
     * {@link LinearTransform1D} and {@link ExponentialTransform1D}.
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
                if (linear.offset == 0 && linear.scale > 0) {
                    return create(base, Math.log(linear.scale) / lnBase + offset);
                }
            } else {
                final double newBase = Math.pow(base, 1/linear.scale);
                if (!Double.isNaN(newBase)) {
                    return create(newBase, linear.scale*offset + linear.offset);
                }
            }
        } else if (other instanceof ExponentialTransform1D) {
            return ((ExponentialTransform1D) other).concatenateLog(this, !applyOtherFirst);
        }
        return super.concatenate(other, applyOtherFirst);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(base, hash(offset, super.computeHashCode()));
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
            final LogarithmicTransform1D that = (LogarithmicTransform1D) object;
            return Utilities.equals(this.base,   that.base) &&
                   Utilities.equals(this.offset, that.offset);
        }
        return false;
    }
}
