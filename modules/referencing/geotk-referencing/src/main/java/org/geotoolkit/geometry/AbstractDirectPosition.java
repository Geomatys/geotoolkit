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
package org.geotoolkit.geometry;

import java.util.Arrays;
import java.util.Objects;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.StringBuilders;


/**
 * Base class for {@linkplain DirectPosition direct position} implementations. This base class
 * provides default implementations for {@link #toString}, {@link #equals} and {@link #hashCode}
 * methods.
 * <p>
 * This class do not holds any state. The decision to implement {@link java.io.Serializable}
 * or {@link org.geotoolkit.util.Cloneable} interfaces is left to implementors.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.4
 * @module
 */
public abstract class AbstractDirectPosition implements DirectPosition {
    /**
     * Constructs a direct position.
     */
    protected AbstractDirectPosition() {
    }

    /**
     * Returns always {@code this}, the direct position for this
     * {@linkplain org.opengis.geometry.coordinate.Position position}.
     *
     * @since 2.5
     */
    @Override
    public DirectPosition getDirectPosition() {
        return this;
    }

    /**
     * Sets this direct position to the given position. If the given position is
     * {@code null}, then all ordinate values are set to {@linkplain Double#NaN NaN}.
     * <p>
     * If this position and the given position have a non-null CRS, then the default implementation
     * requires the CRS to be {@linkplain CRS#equalsIgnoreMetadata equals (ignoring metadata)}
     * otherwise a {@link MismatchedReferenceSystemException} is thrown. However subclass may
     * choose to assign the CRS of this position to the CRS of the given position.
     *
     * @param position The new position, or {@code null}.
     * @throws MismatchedDimensionException If the given position doesn't have the expected dimension.
     * @throws MismatchedReferenceSystemException If the given position doesn't use the expected CRS.
     *
     * @since 3.16 (derived from 2.5)
     */
    public void setLocation(final DirectPosition position) throws MismatchedDimensionException,
            MismatchedReferenceSystemException
    {
        final int dimension = getDimension();
        if (position != null) {
            ensureDimensionMatch("position", position.getDimension(), dimension);
            final CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
            if (crs != null) {
                final CoordinateReferenceSystem other = position.getCoordinateReferenceSystem();
                if (other != null && !CRS.equalsIgnoreMetadata(crs, other)) {
                    throw new MismatchedReferenceSystemException(Errors.format(
                            Errors.Keys.MISMATCHED_COORDINATE_REFERENCE_SYSTEM));
                }
            }
            for (int i=0; i<dimension; i++) {
                setOrdinate(i, position.getOrdinate(i));
            }
        } else {
            for (int i=0; i<dimension; i++) {
                setOrdinate(i, Double.NaN);
            }
        }
    }

    /**
     * Returns a sequence of numbers that hold the coordinate of this position in its
     * reference system.
     *
     * @return The coordinates.
     */
    @Override
    public double[] getCoordinate() {
        final double[] ordinates = new double[getDimension()];
        for (int i=0; i<ordinates.length; i++) {
            ordinates[i] = getOrdinate(i);
        }
        return ordinates;
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
                throw new MismatchedDimensionException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_$3,
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
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_$3,
                        name, dimension, expectedDimension));
        }
    }

    /**
     * Formats this position in the <cite>Well Known Text</cite> (WKT) format.
     * The output is like below:
     *
     * <blockquote>{@code POINT(}{@linkplain #getCoordinate() ordinates}{@code )}</blockquote>
     *
     * The output of this method can be
     * {@linkplain GeneralDirectPosition#GeneralDirectPosition(String) parsed} by the
     * {@link GeneralDirectPosition} constructor.
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Formats a {@code POINT} element from a direct position. This method formats the given
     * position in the <cite>Well Known Text</cite> (WKT) format. The output is like below:
     *
     * <blockquote>{@code POINT(}{@linkplain DirectPosition#getCoordinate() ordinates}{@code )}</blockquote>
     *
     * The output of this method can be
     * {@linkplain GeneralDirectPosition#GeneralDirectPosition(String) parsed} by the
     * {@link GeneralDirectPosition} constructor.
     *
     * @param  position The position to format.
     * @return The position as a {@code POINT} in WKT format.
     *
     * @see GeneralDirectPosition#GeneralDirectPosition(String)
     * @see org.geotoolkit.measure.CoordinateFormat
     * @see org.geotoolkit.io.wkt
     *
     * @since 3.09
     */
    public static String toString(final DirectPosition position) {
        final StringBuilder buffer = new StringBuilder("POINT(");
        final int dimension = position.getDimension();
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(' ');
            }
            StringBuilders.trimFractionalPart(buffer.append(position.getOrdinate(i)));
        }
        return buffer.append(')').toString();
    }

    /**
     * Parses the given WKT.
     *
     * @param  wkt The WKT to parse.
     * @return The ordinates, or {@code null) if none.
     * @throws NumberFormatException If a number can not be parsed.
     * @throws IllegalArgumentException If the parenthesis are not balanced.
     */
    static double[] parse(final String wkt) throws NumberFormatException, IllegalArgumentException {
        final int length = wkt.length();
        int i = -1;
        char c;
        /*
         * Skip the leading identifier (typically "POINT" or "POINT ZM")
         * and the following whitespaces, if any.
         */
        do {
            if (++i >= length) return null;
            if (Character.isJavaIdentifierStart(c = wkt.charAt(i))) {
                do if (++i >= length) return null;
                while (Character.isJavaIdentifierPart(c = wkt.charAt(i)));
            }
        } while (Character.isWhitespace(c));
        /*
         * Skip the opening parenthesis, and the following whitespaces if any.
         * We remember the matching parenthesis since we will look for it later.
         * Note: we use character ' ' for "end of string".
         */
        char close = ' ';
        if (c == '(' || c == '[') {
            close = (c == '(') ? ')' : ']';
            do if (++i >= length) {
                c = ' ';
                break;
            } while (Character.isWhitespace(c = wkt.charAt(i)));
        }
        /*
         * Index i is either at the beginning of a number, at the closing parenthesis or at the end
         * of string (in any cases we are not at a whitespace). Now process every space-separated
         * ordinates until we reach the closing parenthesis or the end of string.
         */
        double[] ordinates = new double[2];
        int dimension = 0;
scan:   while (true) {
            if (c == close) {
                /*
                 * We have reached the closing parenthesis. Having any non-whitespace
                 * character after this one is an error. Otherwise we are done.
                 */
                while (++i < length) {
                    if (!Character.isWhitespace(c = wkt.charAt(i))) {
                        throw new IllegalArgumentException(Errors.format(
                                Errors.Keys.UNPARSABLE_STRING_$2, wkt, wkt.substring(i)));
                    }
                }
                break scan;
            }
            /*
             * We are at the beginning of a number. Find where the number ends (at
             * the first whitespace or closing parenthesis), parse it and store it.
             */
            final int start = i;
            do if (++i >= length) {
                i = length;
                c = ' ';
                break;
            } while (!Character.isWhitespace(c = wkt.charAt(i)) && c != close);
            if (dimension == ordinates.length) {
                ordinates = Arrays.copyOf(ordinates, dimension*2);
            }
            ordinates[dimension++] = Double.parseDouble(wkt.substring(start, i));
            /*
             * Skip whitespaces. If we reach the end of string without finding
             * the closing parenthesis, check if we were suppose to have any.
             */
            while (Character.isWhitespace(c)) {
                if (++i >= length) {
                    if (close != ' ') {
                        throw new IllegalArgumentException(Errors.format(
                                Errors.Keys.NON_EQUILIBRATED_PARENTHESIS_$2, wkt, close));
                    }
                    break scan;
                }
                c = wkt.charAt(i);
            }
        }
        return ArraysExt.resize(ordinates, dimension);
    }

    /**
     * Returns a hash value for this coordinate.
     *
     * @return A hash code value for this position.
     */
    @Override
    public int hashCode() {
        return hashCode(this);
    }

    /**
     * Returns a hash value for the given coordinate.
     */
    static int hashCode(final DirectPosition position) {
        final int dimension = position.getDimension();
        int code = 1;
        for (int i=0; i<dimension; i++) {
            final long bits = Double.doubleToLongBits(position.getOrdinate(i));
            code = 31 * code + ((int)(bits) ^ (int)(bits >>> 32));
        }
        final CoordinateReferenceSystem crs = position.getCoordinateReferenceSystem();
        if (crs != null) {
            code += crs.hashCode();
        }
        return code;
    }

    /**
     * Returns {@code true} if the specified object is also a {@linkplain DirectPosition
     * direct position} with equals {@linkplain #getCoordinate coordinate} and
     * {@linkplain #getCoordinateReferenceSystem CRS}.
     *
     * @param object The object to compare with this position.
     * @return {@code true} if the given object is equal to this position.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof DirectPosition) {
            final DirectPosition that = (DirectPosition) object;
            final int dimension = getDimension();
            if (dimension == that.getDimension()) {
                for (int i=0; i<dimension; i++) {
                    if (!Utilities.equals(this.getOrdinate(i), that.getOrdinate(i))) {
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
}
