/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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


/**
 * Base class for {@linkplain Envelope envelope} implementations. This base class
 * provides default implementations for {@link #toString()}, {@link #equals(Object)}
 * and {@link #hashCode()} methods.
 * <p>
 * This class do not holds any state. The decision to implement {@link java.io.Serializable}
 * or {@link org.geotoolkit.util.Cloneable} interfaces is left to implementors.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
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
            if (crs2!=null && !crs1.equals(crs2)) {
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
     * <blockquote><code>BOX</code><var>n</var>
     * <code>D(</code>{@linkplain #getLowerCorner() lower corner}<code>,</code>
     * {@linkplain #getUpperCorner() upper corner}<code>)</code></blockquote>
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
     * <blockquote><code>BOX</code><var>n</var>
     * <code>D(</code>{@linkplain Envelope#getLowerCorner() lower corner}<code>,</code>
     * {@linkplain Envelope#getUpperCorner() upper corner}<code>)</code></blockquote>
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
            buffer.append(envelope.getMinimum(i));
        }
        buffer.append(',');
        for (int i=0; i<dimension; i++) {
            buffer.append(' ').append(envelope.getMaximum(i));
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
                buffer.append(separator).append(value);
                separator = " ";
            }
            separator = ", ";
        }
        if (separator == ", ") {
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
     * @param object The object to compare with this envelope.
     * @return {@code true} if the given object is equal to this envelope.
     *
     * @todo Current implementation requires that {@code object} is of the same class.
     *       We can not relax this rule before we ensure that every implementations in
     *       the Geotk code base follow the same contract.
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
