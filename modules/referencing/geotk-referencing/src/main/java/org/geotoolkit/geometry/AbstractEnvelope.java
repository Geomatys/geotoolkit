/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.Objects;
import java.awt.geom.Rectangle2D;
import javax.measure.unit.Unit;
import javax.measure.converter.ConversionException;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.RangeMeaning;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.display.shape.XRectangle2D;

import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.util.Strings.trimFractionalPart;
import static org.geotoolkit.math.XMath.isNegative;
import static org.geotoolkit.math.XMath.isPositive;


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
 *   <li>{@link #getMinimum(int)}</li>
 *   <li>{@link #getMaximum(int)}</li>
 * </ul>
 * <p>
 * All other methods, including {@link #toString()}, {@link #equals(Object)} and {@link #hashCode()},
 * are implemented on top of the above four methods.
 *
 * {@section Spanning the anti-meridian of a Geographic CRS}
 * The <cite>Web Coverage Service</cite> (WCS) specification authorizes (with special treatment)
 * cases where <var>upper</var> &lt; <var>lower</var> at least in the longitude case. They are
 * envelopes crossing the anti-meridian, like the red box below (the green box is the usual case):
 *
 * <center><img src="doc-files/AntiMeridian.png"></center>
 *
 * The default implementation of the following methods handle
 * such cases for dimensions having {@link RangeMeaning#WRAPAROUND}:
 * <p>
 * <ul>
 *   <li>{@link #getMedian(int)}</li>
 *   <li>{@link #getSpan(int)}</li>
 *   <li>{@link #contains(DirectPosition)}</li>
 *   <li>{@link #contains(Envelope)}</li>
 * </ul>
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
 */
public abstract class AbstractEnvelope implements Envelope {
    /**
     * Constructs an envelope.
     */
    protected AbstractEnvelope() {
    }

    /**
     * Returns {@code true} if at least one of the specified CRS is null, or both CRS are equals.
     * This special processing for {@code null} values is different from the usual contract of an
     * {@code equals} method, but allow to handle the case where the CRS is unknown.
     */
    static boolean equalsIgnoreMetadata(final CoordinateReferenceSystem crs1,
                                        final CoordinateReferenceSystem crs2)
    {
        return (crs1 == null) || (crs2 == null) || CRS.equalsIgnoreMetadata(crs1, crs2);
    }

    /**
     * Returns the common CRS of specified points.
     *
     * @param  minDP The first position.
     * @param  maxDP The second position.
     * @return Their common CRS, or {@code null} if none.
     * @throws MismatchedReferenceSystemException if the two positions don't use the same CRS.
     */
    static CoordinateReferenceSystem getCoordinateReferenceSystem(final DirectPosition minDP,
            final DirectPosition maxDP) throws MismatchedReferenceSystemException
    {
        final CoordinateReferenceSystem crs1 = minDP.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem crs2 = maxDP.getCoordinateReferenceSystem();
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
     * Checks if the given "minimal" value is less than or equals to the "maximal" value.
     * The <var>minimum</var> &lt;= <var>maximum</var> requirement is relaxed only for
     * axis range of type {@link RangeMeaning#WRAPAROUND}.
     *
     * @param  crs       The envelope coordinate reference system, or {@code null}.
     * @param  dimension The dimension for which the range is verified.
     * @param  minimum   The minimal value in the given dimension.
     * @param  maximum   The maximal value in the given dimension.
     * @throws IllegalArgumentException If the given range of ordinate values is invalid.
     */
    static void ensureValidRange(final CoordinateReferenceSystem crs, final int dimension,
            final double minimum, final double maximum) throws IllegalArgumentException
    {
        if (minimum > maximum && !isWrapAround(crs, dimension)) { // We accept 'NaN' values.
            String message = Errors.format(Errors.Keys.ILLEGAL_ENVELOPE_ORDINATE_$1, dimension);
            message = message + ' ' + Errors.format(Errors.Keys.BAD_RANGE_$2, minimum, maximum);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMinimum minimal ordinates}.
     * The default implementation returns a unmodifiable direct position backed by this envelope,
     * so changes in this envelope will be immediately reflected in the direct position.
     *
     * {@note The <cite>Web Coverage Service</cite> (WCS) 1.1 specification uses an extended
     * interpretation of the bounding box definition. In a WCS 1.1 data structure, the lower
     * corner defines the edges region in the directions of <em>decreasing</em> coordinate
     * values in the envelope CRS. This is usually the algebraic minimum coordinates, but not
     * always. For example, an envelope crossing the anti-meridian could have a lower corner
     * longitude greater than the upper corner longitude. This <code>AbstractEnvelope</code>
     * base class accepts such extended interpretation for <code>WRAPAROUND</code> axes.}
     *
     * @return The lower corner.
     */
    @Override
    public DirectPosition getLowerCorner() {
        return new LowerCorner();
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMaximum maximal ordinates}.
     * The default implementation returns a unmodifiable direct position backed by this envelope,
     * so changes in this envelope will be immediately reflected in the direct position.
     *
     * {@note The <cite>Web Coverage Service</cite> (WCS) 1.1 specification uses an extended
     * interpretation of the bounding box definition. In a WCS 1.1 data structure, the upper
     * corner defines the edges region in the directions of <em>increasing</em> coordinate
     * values in the envelope CRS. This is usually the algebraic maximum coordinates, but not
     * always. For example, an envelope crossing the anti-meridian could have an upper corner
     * longitude less than the lower corner longitude. This <code>AbstractEnvelope</code>
     * base class accepts such extended interpretation for <code>WRAPAROUND</code> axes.}
     *
     * @return The upper corner.
     */
    @Override
    public DirectPosition getUpperCorner() {
        return new UpperCorner();
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMedian(int) middle ordinates}
     * for each dimension for all points within the {@code Envelope}.
     *
     * @return The median coordinates.
     *
     * @since 3.20 (derived from 2.5)
     */
    public DirectPosition getMedian() {
        final GeneralDirectPosition position = new GeneralDirectPosition(getDimension());
        for (int i=position.ordinates.length; --i>=0;) {
            position.ordinates[i] = getMedian(i);
        }
        position.setCoordinateReferenceSystem(getCoordinateReferenceSystem());
        return position;
    }

    /**
     * Returns the median ordinate along the specified dimension. In most cases, the result is
     * equals (minus rounding error) to:
     *
     * {@preformat java
     *     median = (getMaximum(dimension) + getMinimum(dimension)) / 2;
     * }
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * If <var>maximum</var> &lt; <var>minimum</var> and the
     * {@linkplain CoordinateSystemAxis#getRangeMeaning() range meaning} for the requested
     * dimension is {@linkplain RangeMeaning#WRAPAROUND wraparound}, then the median calculated
     * above is actually in the middle of the space <em>outside</em> the envelope. In such cases,
     * this method shifts the <var>median</var> value by half of the periodicity (180° in the
     * longitude case) in order to switch from <cite>outer</cite> space to <cite>inner</cite>
     * space.
     *
     * @param  dimension The dimension for which to obtain the ordinate value.
     * @return The median ordinate at the given dimension, or {@link Double#NaN}.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater
     *         than the {@linkplain #getDimension() envelope dimension}.
     *
     * @since 3.20
     */
    @Override
    public double getMedian(final int dimension) throws IndexOutOfBoundsException {
        final double lower = getMinimum(dimension);
        final double upper = getMaximum(dimension);
        double median = 0.5 * (lower + upper);
        if (isNegative(upper - lower)) { // Special handling for -0.0
            median = fixMedian(getAxis(getCoordinateReferenceSystem(), dimension), median);
        }
        return median;
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
     * Returns the envelope span (typically width or height) along the specified dimension.
     * In most cases, the result is equals (minus rounding error) to:
     *
     * {@preformat java
     *     span = getMaximum(dimension) - getMinimum(dimension);
     * }
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * If <var>maximum</var> &lt; <var>minimum</var> and the
     * {@linkplain CoordinateSystemAxis#getRangeMeaning() range meaning} for the requested
     * dimension is {@linkplain RangeMeaning#WRAPAROUND wraparound}, then the span calculated
     * above is negative. In such cases, this method adds the periodicity (typically 360° of
     * longitude) to the span. If the result is a positive number, it is returned. Otherwise
     * this method returns {@link Double#NaN NaN}.
     *
     * @param  dimension The dimension for which to obtain the span.
     * @return The span (typically width or height) at the given dimension, or {@link Double#NaN}.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater
     *         than the {@linkplain #getDimension() envelope dimension}.
     *
     * @since 3.20
     */
    @Override
    public double getSpan(final int dimension) {
        double span = getMaximum(dimension) - getMinimum(dimension);
        if (isNegative(span)) { // Special handling for -0.0
            span = fixSpan(getAxis(getCoordinateReferenceSystem(), dimension), span);
        }
        return span;
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
     * Returns the envelope span along the specified dimension, in terms of the given units.
     * The default implementation invokes {@link #getSpan(int)} and converts the result.
     *
     * @param  dimension The dimension to query.
     * @param  unit The unit for the return value.
     * @return The span in terms of the given unit.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     * @throws ConversionException if the length can't be converted to the specified units.
     *
     * @since 3.20 (derived from 2.5)
     */
    public double getSpan(final int dimension, final Unit<?> unit)
            throws IndexOutOfBoundsException, ConversionException
    {
        double value = getSpan(dimension);
        final CoordinateSystemAxis axis = getAxis(getCoordinateReferenceSystem(), dimension);
        if (axis != null) {
            final Unit<?> source = axis.getUnit();
            if (source != null) {
                value = source.getConverterToAny(unit).convert(value);
            }
        }
        return value;
    }

    /**
     * Tests if a specified coordinate is inside the boundary of this envelope.
     * If it least one ordinate value in the given point is {@link Double#NaN NaN},
     * then this method returns {@code false}.
     *
     * {@note This method assumes that the specified point uses the same CRS than this envelope.
     *        For performance raisons, it will no be verified unless Java assertions are enabled.}
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * For any dimension, if <var>maximum</var> &lt; <var>minimum</var> and the
     * {@linkplain CoordinateSystemAxis#getRangeMeaning() range meaning} is
     * {@linkplain RangeMeaning#WRAPAROUND wraparound}, then this method uses an algorithm which
     * is the opposite of the usual one: rather than testing if the given point is inside the
     * envelope interior, this method tests if the given point is <em>outside</em> the envelope
     * <em>exterior</em>.
     *
     * @param  position The point to text.
     * @return {@code true} if the specified coordinate is inside the boundary
     *         of this envelope; {@code false} otherwise.
     * @throws MismatchedDimensionException if the specified point doesn't have
     *         the expected dimension.
     *
     * @since 3.20 (derived from 3.00)
     */
    public boolean contains(final DirectPosition position) throws MismatchedDimensionException {
        ensureNonNull("position", position);
        final int dimension = getDimension();
        AbstractDirectPosition.ensureDimensionMatch("point", position.getDimension(), dimension);
        assert equalsIgnoreMetadata(getCoordinateReferenceSystem(),
                position.getCoordinateReferenceSystem()) : position;
        for (int i=0; i<dimension; i++) {
            final double value = position.getOrdinate(i);
            final double lower = getMinimum(i);
            final double upper = getMaximum(i);
            final boolean c1   = (value >= lower);
            final boolean c2   = (value <= upper);
            if (c1 & c2) {
                continue; // Point inside the range, check other dimensions.
            }
            if (c1 | c2) {
                final double span = upper - lower;
                if (isNegative(span) && isWrapAround(getCoordinateReferenceSystem(), i)) {
                    /*
                     * "Spanning the anti-meridian" case: if we reach this point, then the
                     * [upper...lower] range  (note the 'lower' and 'upper' interchanging)
                     * is actually a space outside the envelope and we have checked that
                     * the ordinate value is outside that space.
                     */
                    continue;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if this envelope completely encloses the specified envelope.
     * If one or more edges from the specified envelope coincide with an edge from this
     * envelope, then this method returns {@code true} only if {@code edgesInclusive}
     * is {@code true}.
     *
     * {@note This method assumes that the specified envelope uses the same CRS than this envelope.
     *        For performance raisons, it will no be verified unless Java assertions are enabled.}
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * For every cases illustrated below, the yellow box is considered completely enclosed
     * in the blue envelope:
     *
     * <center><img src="doc-files/Contains.png"></center>
     *
     * @param  envelope The envelope to test for inclusion.
     * @param  edgesInclusive {@code true} if this envelope edges are inclusive.
     * @return {@code true} if this envelope completely encloses the specified one.
     * @throws MismatchedDimensionException if the specified envelope doesn't have
     *         the expected dimension.
     *
     * @see #intersects(Envelope, boolean)
     * @see #equals(Envelope, double, boolean)
     *
     * @since 3.20 (derived from 2.2)
     */
    public boolean contains(final Envelope envelope, final boolean edgesInclusive)
            throws MismatchedDimensionException
    {
        ensureNonNull("envelope", envelope);
        final int dimension = getDimension();
        AbstractDirectPosition.ensureDimensionMatch("envelope", envelope.getDimension(), dimension);
        assert equalsIgnoreMetadata(getCoordinateReferenceSystem(),
                envelope.getCoordinateReferenceSystem()) : envelope;
        for (int i=0; i<dimension; i++) {
            final double  min = getMinimum(i);
            final double  max = getMaximum(i);
            final double eMin = envelope.getMinimum(i);
            final double eMax = envelope.getMaximum(i);
            final boolean minIncluded, maxIncluded;
            if (edgesInclusive) {
                minIncluded = (eMin >= min);
                maxIncluded = (eMax <= max);
            } else {
                minIncluded = (eMin > min);
                maxIncluded = (eMax < max);
            }
            if (minIncluded & maxIncluded) {
                /*
                 *              maxInc                  maxInc
                 *     ┌─────────────┐                  ─────┐      ┌─────
                 *     │  ┌───────┐  │        or        ──┐  │      │  ┌──
                 *     │  └───────┘  │                  ──┘  │      │  └──
                 *     └─────────────┘                  ─────┘      └─────
                 *     minInc                                       minInc
                 */
                continue;
            }
            if (minIncluded != maxIncluded && isNegative(max - min) && isPositive(eMax - eMin)
                    && isWrapAround(getCoordinateReferenceSystem(), i))
            {
                /*
                 *          maxInc                     !maxInc
                 *     ──────────┐  ┌─────              ─────┐  ┌─────────
                 *       ┌────┐  │  │           or           │  │  ┌────┐
                 *       └────┘  │  │                        │  │  └────┘
                 *     ──────────┘  └─────              ─────┘  └─────────
                 *                  !minInc                     minInc
                 */
                continue;
            }
            return false;
        }
//        assert intersects(envelope, edgesInclusive) || hasNaN(envelope) : envelope;
        return true;
    }

    /**
     * Returns a {@link Rectangle2D} with the same bounds then this {@code Envelope}.
     * This envelope must be two-dimensional before this method is invoked.
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
                    Errors.Keys.NOT_TWO_DIMENSIONAL_$1, dimension));
        }
        final double xmin = getMinimum(0);
        final double ymin = getMinimum(1);
        final double xmax = getMaximum(0);
        final double ymax = getMaximum(1);
        final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        return (crs != null) ? new Envelope2D(crs, xmin, ymin, xmax-xmin, ymax-ymin)
                             : XRectangle2D.createFromExtremums(xmin, ymin, xmax, ymax);
    }

    /**
     * Formats this envelope in the <cite>Well Known Text</cite> (WKT) format. The output is like
     * below, where <var>n</var> is the {@linkplain #getDimension() number of dimensions}:
     *
     * <blockquote>{@code BOX}<var>n</var>{@code D(}{@linkplain #getLowerCorner() lower corner}{@code ,}
     * {@linkplain #getUpperCorner() upper corner}{@code )}</blockquote>
     *
     * The output of this method can be {@linkplain GeneralEnvelope#GeneralEnvelope(String) parsed}
     * by the {@link GeneralEnvelope} constructor.
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Implementation of {@link Envelopes#toWKT(Envelope)}. Formats a {@code BOX} element from an
     * envelope in <cite>Well Known Text</cite> (WKT) format.
     *
     * @param  envelope The envelope to format.
     * @return The envelope as a {@code BOX2D} or {@code BOX3D} in WKT format.
     *
     * @see GeneralEnvelope#GeneralEnvelope(String)
     * @see org.geotoolkit.measure.CoordinateFormat
     * @see org.geotoolkit.io.wkt
     *
     * @since 3.09
     */
    static String toString(final Envelope envelope) {
        final int dimension = envelope.getDimension();
        final StringBuilder buffer = new StringBuilder("BOX").append(dimension).append("D(");
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(' ');
            }
            trimFractionalPart(buffer.append(envelope.getMinimum(i)));
        }
        buffer.append(',');
        for (int i=0; i<dimension; i++) {
            trimFractionalPart(buffer.append(' ').append(envelope.getMaximum(i)));
        }
        return buffer.append(')').toString();
    }

    /**
     * Formats a {@code POLYGON} element from an envelope. This method formats the given envelope
     * as a geometry in the <cite>Well Known Text</cite> (WKT) format. This is provided as an
     * alternative to the {@code BOX} element formatted by {@link #toString(Envelope)}, because
     * the {@code BOX} element is usually not considered a geometry while {@code POLYGON} is.
     * <p>
     * The output of this method can be {@linkplain GeneralEnvelope#GeneralEnvelope(String) parsed}
     * by the {@link GeneralEnvelope} constructor.
     *
     * @param  envelope The envelope to format.
     * @return The envelope as a {@code POLYGON} in WKT format.
     *
     * @see org.geotoolkit.io.wkt
     *
     * @since 3.09
     *
     * @deprecated Moved to {@link Envelopes#toPolygonWKT(Envelope)}.
     */
    @Deprecated
    public static String toPolygonString(final Envelope envelope) {
        return Envelopes.toPolygonWKT(envelope);
    }

    /**
     * Returns a hash value for this envelope.
     */
    @Override
    public int hashCode() {
        final int dimension = getDimension();
        int code = 1;
        boolean p = true;
        do {
            for (int i=0; i<dimension; i++) {
                final long bits = Double.doubleToLongBits(p ? getMinimum(i) : getMaximum(i));
                code = 31 * code + ((int)(bits) ^ (int)(bits >>> 32));
            }
        } while ((p = !p) == false);
        final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
        if (crs != null) {
            code += crs.hashCode();
        }
        return code;
    }

    /**
     * Returns {@code true} if the specified object is an envelope of the same class
     * with equals coordinates and {@linkplain #getCoordinateReferenceSystem CRS}.
     *
     * {@note This implementation requires that the provided <code>object</code> argument
     * is of the same class than this envelope. We do not relax this rule since not every
     * implementations in the Geotk code base follow the same contract.}
     *
     * @param object The object to compare with this envelope.
     * @return {@code true} if the given object is equal to this envelope.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final Envelope that = (Envelope) object;
            final int dimension = getDimension();
            if (dimension == that.getDimension()) {
                for (int i=0; i<dimension; i++) {
                    if (!Utilities.equals(this.getMinimum(i), that.getMinimum(i)) ||
                        !Utilities.equals(this.getMaximum(i), that.getMaximum(i)))
                    {
                        return false;
                    }
                }
                if (Objects.equals(this.getCoordinateReferenceSystem(),
                                   that.getCoordinateReferenceSystem()))
                {
                    assert hashCode() == that.hashCode() : this;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Compares to the specified envelope for equality up to the specified tolerance value.
     * The tolerance value {@code eps} can be either relative to the {@linkplain #getSpan
     * envelope span} along each dimension or can be an absolute value (as for example some
     * ground resolution of a {@linkplain org.opengis.coverage.grid.GridCoverage.GridCoverage
     * grid coverage}).
     * <p>
     * If {@code epsIsRelative} is set to {@code true}, the actual tolerance value for a given
     * dimension <var>i</var> is {@code eps}&times;{@code span} where {@code span} is the
     * maximum of {@linkplain #getSpan this envelope span} and the specified envelope length
     * along dimension <var>i</var>.
     * <p>
     * If {@code epsIsRelative} is set to {@code false}, the actual tolerance value for a
     * given dimension <var>i</var> is {@code eps}.
     * <p>
     * Relative tolerance value (as opposed to absolute tolerance value) help to workaround the
     * fact that tolerance value are CRS dependent. For example the tolerance value need to be
     * smaller for geographic CRS than for UTM projections, because the former typically has a
     * range of -180 to 180° while the later can have a range of thousands of meters.
     *
     * {@note This method assumes that the specified envelope uses the same CRS than this envelope.
     *        For performance raisons, it will no be verified unless Java assertions are enabled.}
     *
     * @param envelope The envelope to compare with.
     * @param eps The tolerance value to use for numerical comparisons.
     * @param epsIsRelative {@code true} if the tolerance value should be relative to
     *        axis length, or {@code false} if it is an absolute value.
     * @return {@code true} if the given object is equal to this envelope up to the given
     *         tolerance value.
     *
     * @see GeneralEnvelope#contains(Envelope, boolean)
     * @see GeneralEnvelope#intersects(Envelope, boolean)
     *
     * @since 2.4
     */
    public boolean equals(final Envelope envelope, final double eps, final boolean epsIsRelative) {
        ensureNonNull("envelope", envelope);
        final int dimension = getDimension();
        if (envelope.getDimension() != dimension) {
            return false;
        }
        assert equalsIgnoreMetadata(getCoordinateReferenceSystem(), envelope.getCoordinateReferenceSystem()) : envelope;
        for (int i=0; i<dimension; i++) {
            double epsilon;
            if (epsIsRelative) {
                epsilon = Math.max(getSpan(i), envelope.getSpan(i));
                epsilon = (epsilon > 0 && epsilon < Double.POSITIVE_INFINITY) ? epsilon*eps : eps;
            } else {
                epsilon = eps;
            }
            // Comparison below uses '!' in order to catch NaN values.
            if (!(Math.abs(getMinimum(i) - envelope.getMinimum(i)) <= epsilon &&
                  Math.abs(getMaximum(i) - envelope.getMaximum(i)) <= epsilon))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Base class for direct position from an envelope.
     * This class delegates its work to the enclosing envelope.
     */
    private abstract class Corner extends AbstractDirectPosition {
        /** The coordinate reference system in which the coordinate is given. */
        @Override public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            return AbstractEnvelope.this.getCoordinateReferenceSystem();
        }

        /** The length of coordinate sequence (the number of entries). */
        @Override public int getDimension() {
            return AbstractEnvelope.this.getDimension();
        }

        /** Sets the ordinate value along the specified dimension. */
        @Override public void setOrdinate(int dimension, double value) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * The corner returned by {@link AbstractEnvelope#getLowerCorner}.
     */
    private final class LowerCorner extends Corner {
        @Override public double getOrdinate(final int dimension) throws IndexOutOfBoundsException {
            return getMinimum(dimension);
        }
    }

    /**
     * The corner returned by {@link AbstractEnvelope#getUpperCorner}.
     */
    private final class UpperCorner extends Corner {
        @Override public double getOrdinate(final int dimension) throws IndexOutOfBoundsException {
            return getMaximum(dimension);
        }
    }
}
