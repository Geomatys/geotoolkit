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

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.referencing.CRS;

import static org.geotoolkit.util.Strings.trimFractionalPart;


/**
 * Base class for {@linkplain Envelope envelope} implementations. This base class
 * provides default implementations for {@link #toString()}, {@link #equals(Object)}
 * and {@link #hashCode()} methods.
 * <p>
 * This class do not holds any state. The decision to implement {@link java.io.Serializable}
 * or {@link org.geotoolkit.util.Cloneable} interfaces is left to implementors.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 2.4
 * @module
 */
public abstract class AbstractEnvelope implements Envelope {
    /**
     * Enumeration of the 4 corners in an envelope, with repetition of the first point.
     * The values are (x,y) pairs with {@code false} meaning "minimal value" and {@code true}
     * meaning "maximal value". This is used by {@link #toPolygonString(Envelope)} only.
     */
    private static final boolean[] CORNERS = {
        false, false,
        false, true,
        true,  true,
        true,  false,
        false, false
    };

    /**
     * Constructs an envelope.
     */
    protected AbstractEnvelope() {
    }

    /**
     * Makes sure an argument is non-null.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws NullArgumentException if {@code object} is null.
     */
    static void ensureNonNull(String name, Object object) throws NullArgumentException {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
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
     * A coordinate position consisting of all the {@linkplain #getMinimum minimal ordinates}.
     * The default implementation returns a direct position backed by this envelope, so changes
     * in this envelope will be immediately reflected in the direct position.
     *
     * @return The lower corner.
     */
    @Override
    public DirectPosition getLowerCorner() {
        return new LowerCorner();
    }

    /**
     * A coordinate position consisting of all the {@linkplain #getMaximum maximal ordinates}.
     * The default implementation returns a direct position backed by this envelope, so changes
     * in this envelope will be immediately reflected in the direct position.
     *
     * @return The upper corner.
     */
    @Override
    public DirectPosition getUpperCorner() {
        return new UpperCorner();
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
     * Formats a {@code BOX} element from an envelope. This method formats the given envelope in
     * the <cite>Well Known Text</cite> (WKT) format. The output is like below, where <var>n</var>
     * is the {@linkplain Envelope#getDimension() number of dimensions}:
     *
     * <blockquote>{@code BOX}<var>n</var>{@code D(}{@linkplain #getLowerCorner() lower corner}{@code ,}
     * {@linkplain #getUpperCorner() upper corner}{@code )}</blockquote>
     *
     * The output of this method can be {@linkplain GeneralEnvelope#GeneralEnvelope(String) parsed}
     * by the {@link GeneralEnvelope} constructor.
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
    public static String toString(final Envelope envelope) {
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
     */
    public static String toPolygonString(final Envelope envelope) {
        /*
         * Get the dimension, ignoring the trailing ones which have infinite values.
         */
        int dimension = envelope.getDimension();
        while (dimension != 0) {
            final double length = envelope.getSpan(dimension - 1);
            if (!Double.isNaN(length) && !Double.isInfinite(length)) {
                break;
            }
            dimension--;
        }
        final StringBuilder buffer = new StringBuilder("POLYGON(");
        String separator = "(";
        for (int corner=0; corner<CORNERS.length; corner+=2) {
            for (int i=0; i<dimension; i++) {
                final double value;
                switch (i) {
                    case  0: // Fall through
                    case  1: value = CORNERS[corner+i] ? envelope.getMaximum(i) : envelope.getMinimum(i); break;
                    default: value = envelope.getMedian(i); break;
                }
                trimFractionalPart(buffer.append(separator).append(value));
                separator = " ";
            }
            separator = ", ";
        }
        if (separator == ", ") { // NOSONAR
            buffer.append(')');
        }
        return buffer.append(')').toString();
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
        if (object!=null && object.getClass().equals(getClass())) {
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
                if (Utilities.equals(this.getCoordinateReferenceSystem(),
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
     *        For performance reason, it will no be verified unless Java assertions are enabled.}
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
