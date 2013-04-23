/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry;

import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.RangeMeaning;

import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.display.shape.XRectangle2D;
import org.apache.sis.util.StringBuilders;

import static org.apache.sis.internal.util.Utilities.SIGN_BIT_MASK;


/**
 * Base class for {@linkplain Envelope envelope} implementations. This base class does not hold any
 * state and does not implement the {@link java.io.Serializable} or {@link org.geotoolkit.util.Cloneable}
 * interfaces. The internal representation, and the choice to be cloneable or serializable, is left
 * to implementors.
 * <p>
 * Implementors needs to define at least the following methods:
 * <p>
 * <ul>
 *   <li>{@link #getDimension()}</li>
 *   <li>{@link #getCoordinateReferenceSystem()}</li>
 *   <li>{@link #getLower(int)}</li>
 *   <li>{@link #getUpper(int)}</li>
 * </ul>
 * <p>
 * All other methods, including {@link #toString()}, {@link #equals(Object)} and {@link #hashCode()},
 * are implemented on top of the above four methods.
 *
 * {@section Spanning the anti-meridian of a Geographic CRS}
 * The <cite>Web Coverage Service</cite> (WCS) specification authorizes (with special treatment)
 * cases where <var>upper</var> &lt; <var>lower</var> at least in the longitude case. They are
 * envelopes crossing the anti-meridian, like the red box below (the green box is the usual case).
 * The default implementation of methods listed in the right column can handle such cases.
 *
 * <center><table><tr><td style="white-space:nowrap">
 *   <img src="doc-files/AntiMeridian.png">
 * </td><td style="white-space:nowrap">
 * Supported methods:
 * <ul>
 *   <li>{@link #getMinimum(int)}</li>
 *   <li>{@link #getMaximum(int)}</li>
 *   <li>{@link #getMedian(int)}</li>
 *   <li>{@link #getSpan(int)}</li>
 *   <li>{@link #contains(DirectPosition)}</li>
 *   <li>{@link #contains(Envelope, boolean)}</li>
 *   <li>{@link #intersects(Envelope, boolean)}</li>
 * </ul>
 * </td></tr></table></center>
 *
 * {@section Note on positive and negative zeros}
 * The IEEE 754 standard defines two different values for positive zero and negative zero.
 * When used with Geotk envelopes and keeping in mind the above discussion, those zeros have
 * different meanings:
 * <p>
 * <ul>
 *   <li>The [-0…0°] range is an empty envelope.</li>
 *   <li>The [0…-0°] range makes a full turn around the globe, like the [-180…180°]
 *       range except that the former range spans across the anti-meridian.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.AbstractEnvelope}.
 */
@Deprecated
abstract class AbstractEnvelope extends org.apache.sis.geometry.AbstractEnvelope {
    /**
     * Constructs an envelope.
     */
    protected AbstractEnvelope() {
    }

    /**
     * Returns the given envelope as an {@code AbstractEnvelope} instance. If the given envelope
     * is already an instance of {@code AbstractEnvelope}, then it is returned unchanged.
     * Otherwise the coordinate values and the CRS of the given envelope are copied in a
     * new envelope.
     *
     * @param  envelope The envelope to cast, or {@code null}.
     * @return The values of the given envelope as an {@code AbstractEnvelope} instance.
     *
     * @see GeneralEnvelope#castOrCopy(Envelope)
     * @see ImmutableEnvelope#castOrCopy(Envelope)
     *
     * @since 3.20
     */
    public static AbstractEnvelope castOrCopy(final Envelope envelope) {
        if (envelope == null || envelope instanceof AbstractEnvelope) {
            return (AbstractEnvelope) envelope;
        }
        return new GeneralEnvelope(envelope);
    }

    /**
     * Convenience method for checking coordinate reference system validity.
     *
     * @param  crs The coordinate reference system to check.
     * @param  expected the dimension expected.
     * @throws MismatchedDimensionException if the CRS dimension is not valid.
     */
    static void checkCoordinateReferenceSystemDimension(final CoordinateReferenceSystem crs,
                                                        final int expected)
            throws MismatchedDimensionException
    {
        if (crs != null) {
            final int dimension = crs.getCoordinateSystem().getDimension();
            if (dimension != expected) {
                throw new MismatchedDimensionException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_3,
                          crs.getName().getCode(), dimension, expected));
            }
        }
    }

    /**
     * Convenience method for checking object dimension validity.
     * This method is usually invoked for argument checking.
     *
     * @param  name The name of the argument to check.
     * @param  dimension The object dimension.
     * @param  expectedDimension The Expected dimension for the object.
     * @throws MismatchedDimensionException if the object doesn't have the expected dimension.
     */
    static void ensureDimensionMatch(final String name,
                                     final int dimension,
                                     final int expectedDimension)
            throws MismatchedDimensionException
    {
        if (dimension != expectedDimension) {
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_3,
                        name, dimension, expectedDimension));
        }
    }

    /**
     * Returns {@code true} if at least one of the specified CRS is null, or both CRS are equals.
     * This special processing for {@code null} values is different from the usual contract of an
     * {@code equals} method, but allow to handle the case where the CRS is unknown.
     * <p>
     * Note that in debug mode (to be used in assertions only), the comparison are actually a bit
     * more relax than just "ignoring metadata", since some rounding errors are tolerated.
     */
    static boolean equalsIgnoreMetadata(final CoordinateReferenceSystem crs1,
                                        final CoordinateReferenceSystem crs2, final boolean debug)
    {
        return (crs1 == null) || (crs2 == null) || Utilities.deepEquals(crs1, crs2,
                debug ? ComparisonMode.DEBUG : ComparisonMode.IGNORE_METADATA);
    }

    /**
     * Returns the common CRS of specified points.
     *
     * @param  lower The first position.
     * @param  upper The second position.
     * @return Their common CRS, or {@code null} if none.
     * @throws MismatchedReferenceSystemException if the two positions don't use the same CRS.
     */
    static CoordinateReferenceSystem getCoordinateReferenceSystem(final DirectPosition lower,
            final DirectPosition upper) throws MismatchedReferenceSystemException
    {
        final CoordinateReferenceSystem crs1 = lower.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem crs2 = upper.getCoordinateReferenceSystem();
        if (crs1 == null) {
            return crs2;
        } else {
            if (crs2 != null && !crs1.equals(crs2)) {
                throw new MismatchedReferenceSystemException(
                          Errors.format(Errors.Keys.MISMATCHED_COORDINATE_REFERENCE_SYSTEM));
            }
            return crs1;
        }
    }

    /**
     * Returns the axis of the given coordinate reference system for the given dimension,
     * or {@code null} if none.
     *
     * @param  crs The envelope CRS, or {@code null}.
     * @param  dimension The dimension for which to get the axis.
     * @return The axis at the given dimension, or {@code null}.
     */
    static CoordinateSystemAxis getAxis(final CoordinateReferenceSystem crs, final int dimension) {
        if (crs != null) {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            if (cs != null) {
                return cs.getAxis(dimension);
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the axis for the given dimension has the
     * {@link RangeMeaning#WRAPAROUND WRAPAROUND} range meaning.
     *
     * @param  crs The envelope CRS, or {@code null}.
     * @param  dimension The dimension for which to get the axis.
     * @return {@code true} if the range meaning is {@code WRAPAROUND}.
     */
    static boolean isWrapAround(final CoordinateReferenceSystem crs, final int dimension) {
        final CoordinateSystemAxis axis = getAxis(crs, dimension);
        return (axis != null) && RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning());
    }

    /**
     * If the range meaning of the given axis is "wraparound", returns the spanning of that axis.
     * Otherwise returns {@link Double#NaN}.
     *
     * @param  axis The axis for which to get the spanning.
     * @return The spanning of the given axis.
     */
    static double getSpan(final CoordinateSystemAxis axis) {
        if (axis != null && RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
            return axis.getMaximumValue() - axis.getMinimumValue();
        }
        return Double.NaN;
    }

    /**
     * Returns {@code true} if the given value is negative, without checks for {@code NaN}.
     * This method should be invoked only when the number is known to not be {@code NaN},
     * otherwise the safer {@link org.geotoolkit.math.XMath#isNegative(double)} method shall
     * be used instead. Note that the check for {@code NaN} doesn't need to be explicit.
     * For example in the following code, {@code NaN} values were implicitly checked by
     * the {@code (a < b)} comparison:
     *
     * {@preformat java
     *     if (a < b && isNegativeUnsafe(a)) {
     *         // ... do some stuff
     *     }
     * }
     */
    static boolean isNegativeUnsafe(final double value) {
        return (Double.doubleToRawLongBits(value) & SIGN_BIT_MASK) != 0;
    }

    /**
     * Shifts the median value when the minimum is greater than the maximum.
     * If no shift can be applied, returns {@code NaN}.
     */
    static double fixMedian(final CoordinateSystemAxis axis, final double median) {
        if (axis != null && RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
            final double minimum = axis.getMinimumValue();
            final double maximum = axis.getMaximumValue();
            final double cycle   = maximum - minimum;
            if (cycle > 0 && cycle != Double.POSITIVE_INFINITY) {
                // The copySign is for shifting in the direction of the valid range center.
                return median + 0.5 * Math.copySign(cycle, 0.5*(minimum + maximum) - median);
            }
        }
        return Double.NaN;
    }

    /**
     * Transforms a negative span into a valid value if the axis range meaning is "wraparound".
     * Returns {@code NaN} otherwise.
     *
     * @param  axis The axis for the span dimension, or {@code null}.
     * @param  span The negative span.
     * @return A positive span, or NaN if the span can not be fixed.
     */
    static double fixSpan(final CoordinateSystemAxis axis, double span) {
        if (axis != null && RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
            final double cycle = axis.getMaximumValue() - axis.getMinimumValue();
            if (cycle > 0 && cycle != Double.POSITIVE_INFINITY) {
                span += cycle;
                if (span >= 0) {
                    return span;
                }
            }
        }
        return Double.NaN;
    }

    /**
     * Returns {@code false} if at least one ordinate value is not {@linkplain Double#NaN NaN}. The
     * {@code isAllNaN()} check is a little bit different than {@link #isEmpty()} since it returns
     * {@code false} for a partially initialized envelope, while {@code isEmpty()} returns
     * {@code false} only after all dimensions have been initialized. More specifically, the
     * following rules apply:
     * <p>
     * <ul>
     *   <li>If <code>isAllNaN() == true</code>, then <code>{@linkplain #isEmpty()} == true</code></li>
     *   <li>If <code>{@linkplain #isEmpty()} == false</code>, then <code>isAllNaN() == false</code></li>
     *   <li>The converse of the above-cited rules are not always true.</li>
     * </ul>
     *
     * @return {@code true} if this envelope has NaN values.
     *
     * @see GeneralEnvelope#setToNull()
     *
     * @since 3.20 (derived from 2.2)
     *
     * @deprecated Renamed {@link #isAllNaN()}.
     */
    @Deprecated
    public final boolean isNull() {
        return isAllNaN();
    }

    /**
     * Returns {@code true} if at least one ordinate in the given envelope
     * is {@link Double#NaN}. This is used for assertions only.
     */
    static boolean hasNaN(final Envelope envelope) {
        return hasNaN(envelope.getLowerCorner()) || hasNaN(envelope.getUpperCorner());
    }

    /**
     * Returns {@code true} if at least one ordinate in the given position
     * is {@link Double#NaN}. This is used for assertions only.
     */
    static boolean hasNaN(final DirectPosition position) {
        for (int i=position.getDimension(); --i>=0;) {
            if (Double.isNaN(position.getOrdinate(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a {@link Rectangle2D} with the {@linkplain #getMinimum(int) minimum}
     * and {@linkplain #getMaximum(int) maximum} values of this {@code Envelope}.
     * This envelope must be two-dimensional before this method is invoked.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * If this envelope spans the anti-meridian, then the longitude dimension will be
     * extended to full range of its coordinate system axis (typically [-180 … 180]°).
     *
     * @return This envelope as a two-dimensional rectangle.
     * @throws IllegalStateException if this envelope is not two-dimensional.
     *
     * @since 3.20 (derived from 3.00)
     */
    public Rectangle2D toRectangle2D() throws IllegalStateException {
        final int dimension = getDimension();
        if (dimension != 2) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_1, dimension));
        }
        return XRectangle2D.createFromExtremums(
                getMinimum(0), getMinimum(1),
                getMaximum(0), getMaximum(1));
    }

    /**
     * Implementation of {@link Envelopes#toWKT(Envelope)}. Formats a {@code BOX} element from an
     * envelope in <cite>Well Known Text</cite> (WKT) format.
     *
     * @param  envelope The envelope to format.
     * @return The envelope as a {@code BOX} or {@code BOX3D}.
     *
     * @see GeneralEnvelope#GeneralEnvelope(String)
     * @see org.geotoolkit.measure.CoordinateFormat
     * @see org.geotoolkit.io.wkt
     *
     * @since 3.09
     */
    static String toString(final Envelope envelope) {
        final int dimension = envelope.getDimension();
        final DirectPosition lower = envelope.getLowerCorner();
        final DirectPosition upper = envelope.getUpperCorner();
        final StringBuilder buffer = new StringBuilder("BOX").append(dimension).append("D(");
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(' ');
            }
            StringBuilders.trimFractionalPart(buffer.append(lower.getOrdinate(i)));
        }
        buffer.append(',');
        for (int i=0; i<dimension; i++) {
            StringBuilders.trimFractionalPart(buffer.append(' ').append(upper.getOrdinate(i)));
        }
        return buffer.append(')').toString();
    }
}
