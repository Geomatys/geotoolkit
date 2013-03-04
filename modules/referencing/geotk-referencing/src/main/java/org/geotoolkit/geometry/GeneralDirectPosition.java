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
import java.io.Serializable;
import java.awt.geom.Point2D;
import java.lang.reflect.Field;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.resources.Errors;


/**
 * Holds the coordinates for a position within some coordinate reference system. Since
 * {@code DirectPosition}s, as data types, will often be included in larger objects
 * (such as {@linkplain org.opengis.geometry.Geometry geometries}) that have references
 * to {@code CoordinateReferenceSystem}, the {@link #getCoordinateReferenceSystem} method
 * may returns {@code null} if this particular {@code DirectPosition} is included in such
 * larger object. In this case, the coordinate reference system is implicitly assumed to take
 * on the value of the containing object's {@link CoordinateReferenceSystem}.
 * <p>
 * This particular implementation of {@code DirectPosition} is said "General" because it
 * uses an {@linkplain #ordinates array of ordinates} of an arbitrary length. If the direct
 * position is know to be always two-dimensional, then {@link DirectPosition2D} may provides
 * a more efficient implementation.
 * <p>
 * Most methods in this implementation are final for performance reason.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @see DirectPosition1D
 * @see DirectPosition2D
 * @see java.awt.geom.Point2D
 *
 * @since 1.2
 * @module
 */
public class GeneralDirectPosition extends AbstractDirectPosition implements Serializable, Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 9071833698385715524L;

    /**
     * The ordinates of the direct position. The length of this array is the
     * {@linkplain #getDimension() dimension} of this direct position.
     */
    public final double[] ordinates;

    /**
     * The coordinate reference system for this position, or {@code null}.
     */
    private CoordinateReferenceSystem crs;

    /**
     * Constructs a position using the specified coordinate reference system.
     * The number of dimensions is inferred from the coordinate reference system.
     *
     * @param crs The coordinate reference system to be given to this position.
     *
     * @since 2.2
     */
    public GeneralDirectPosition(final CoordinateReferenceSystem crs) {
        this(crs.getCoordinateSystem().getDimension());
        this.crs = crs;
    }

    /**
     * Constructs a position with the specified number of dimensions.
     *
     * @param  numDim Number of dimensions.
     * @throws NegativeArraySizeException if {@code numDim} is negative.
     */
    public GeneralDirectPosition(final int numDim) throws NegativeArraySizeException {
        ordinates = new double[numDim];
    }

    /**
     * Constructs a position with the specified ordinates.
     * The {@code ordinates} array will be copied.
     *
     * @param ordinates The ordinate values to copy.
     */
    public GeneralDirectPosition(final double... ordinates) {
        this.ordinates = ordinates.clone();
    }

    /**
     * Constructs a position from the specified {@link Point2D}.
     * Despite their name, the (<var>x</var>,<var>y</var>) ordinates don't need to be oriented
     * toward ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
     * See the {@link DirectPosition2D} javadoc for details.
     *
     * @param point The position to copy.
     */
    public GeneralDirectPosition(final Point2D point) {
        ordinates = new double[] {point.getX(), point.getY()};
    }

    /**
     * Constructs a position initialized to the same values than the specified point.
     *
     * @param point The position to copy.
     *
     * @since 2.2
     */
    public GeneralDirectPosition(final DirectPosition point) {
        ordinates = point.getCoordinate(); // Should already be cloned.
        crs = point.getCoordinateReferenceSystem();
    }

    /**
     * Constructs a position initialized to the values parsed from the given string in
     * <cite>Well Known Text</cite> (WKT) format. The given string is typically a {@code POINT}
     * element like below:
     *
     * {@preformat wkt
     *     POINT(6 10)
     * }
     *
     * However this constructor is lenient to other types like {@code POINT ZM}.
     *
     * @param  wkt The {@code POINT} or other kind of element to parse.
     * @throws NumberFormatException If a number can not be parsed.
     * @throws IllegalArgumentException If the parenthesis are not balanced.
     *
     * @see #toString(DirectPosition)
     * @see org.geotoolkit.measure.CoordinateFormat
     *
     * @since 3.09
     */
    public GeneralDirectPosition(final String wkt) throws NumberFormatException, IllegalArgumentException {
        double[] ordinates = parse(wkt);
        if (ordinates == null) {
            ordinates = ArraysExt.EMPTY_DOUBLE;
        }
        this.ordinates = ordinates;
    }

    /**
     * Returns the coordinate reference system in which the coordinate is given.
     * May be {@code null} if this particular {@code DirectPosition} is included
     * in a larger object with such a reference to a CRS.
     *
     * @return The coordinate reference system, or {@code null}.
     */
    @Override
    public final CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Sets the coordinate reference system in which the coordinate is given.
     *
     * @param crs The new coordinate reference system, or {@code null}.
     * @throws MismatchedDimensionException if the specified CRS doesn't have the expected
     *         number of dimensions.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException
    {
        checkCoordinateReferenceSystemDimension(crs, getDimension());
        this.crs = crs;
    }

    /**
     * The length of coordinate sequence (the number of entries).
     * This may be less than or equal to the dimensionality of the
     * {@linkplain #getCoordinateReferenceSystem() coordinate reference system}.
     *
     * @return The dimensionality of this position.
     */
    @Override
    public final int getDimension() {
        return ordinates.length;
    }

    /**
     * Returns a sequence of numbers that hold the coordinate of this position in its
     * reference system.
     *
     * @return A copy of the {@linkplain #ordinates coordinates}.
     */
    @Override
    public final double[] getCoordinate() {
        return ordinates.clone();
    }

    /**
     * Returns the ordinate at the specified dimension.
     *
     * @param  dimension The dimension in the range 0 to {@linkplain #getDimension dimension}-1.
     * @return The coordinate at the specified dimension.
     * @throws IndexOutOfBoundsException if the specified dimension is out of bounds.
     */
    @Override
    public final double getOrdinate(final int dimension) throws IndexOutOfBoundsException {
        return ordinates[dimension];
    }

    /**
     * Sets the ordinate value along the specified dimension.
     *
     * @param dimension the dimension for the ordinate of interest.
     * @param value the ordinate value of interest.
     * @throws IndexOutOfBoundsException if the specified dimension is out of bounds.
     */
    @Override
    public final void setOrdinate(final int dimension, final double value) throws IndexOutOfBoundsException {
        ordinates[dimension] = value;
    }

    /**
     * Sets the ordinate values along all dimensions.
     *
     * @param  ordinates The new ordinates values. or a {@code null} array
     *         for setting all ordinate values to {@link Double#NaN NaN}.
     * @throws MismatchedDimensionException If the length of the specified array is not
     *         equals to the {@linkplain #getDimension() dimension} of this position.
     *
     * @since 3.16
     */
    public final void setLocation(final double... ordinates) throws MismatchedDimensionException {
        if (ordinates == null) {
            Arrays.fill(this.ordinates, Double.NaN);
        } else {
            ensureDimensionMatch("ordinates", ordinates.length, this.ordinates.length);
            System.arraycopy(ordinates, 0, this.ordinates, 0, ordinates.length);
        }
    }

    /**
     * Sets this coordinate to the specified direct position. If the specified position
     * contains a {@linkplain CoordinateReferenceSystem coordinate reference system},
     * then the CRS for this position will be set to the CRS of the specified position.
     *
     * @param  position The new position for this point, or {@code null} for setting all ordinate
     *         values to {@link Double#NaN NaN}.
     * @throws MismatchedDimensionException if the given position doesn't have the expected dimension.
     *
     * @since 2.2
     */
    @Override
    public final void setLocation(final DirectPosition position) throws MismatchedDimensionException {
        if (position == null) {
            Arrays.fill(ordinates, Double.NaN);
        } else {
            ensureDimensionMatch("position", position.getDimension(), ordinates.length);
            setCoordinateReferenceSystem(position.getCoordinateReferenceSystem());
            for (int i=0; i<ordinates.length; i++) {
                ordinates[i] = position.getOrdinate(i);
            }
        }
    }

    /**
     * Sets this coordinate to the specified direct position. This method is identical to
     * {@link #setLocation(DirectPosition)}, but is slightly faster in the special case
     * of an {@code GeneralDirectPosition} implementation.
     *
     * @param  position The new position for this point, or {@code null} for setting all ordinate
     *         values to {@link Double#NaN NaN}.
     * @throws MismatchedDimensionException if the given position doesn't have the expected dimension.
     */
    public final void setLocation(final GeneralDirectPosition position) throws MismatchedDimensionException {
        if (position == null) {
            Arrays.fill(ordinates, Double.NaN);
        } else {
            ensureDimensionMatch("position", position.ordinates.length, ordinates.length);
            setCoordinateReferenceSystem(position.crs);
            System.arraycopy(position.ordinates, 0, ordinates, 0, ordinates.length);
        }
    }

    /**
     * Sets this coordinate to the specified {@link Point2D}.
     * This coordinate must be two-dimensional.
     *
     * @param  point The new coordinate for this point.
     * @throws MismatchedDimensionException if this coordinate point is not two-dimensional.
     */
    public final void setLocation(final Point2D point) throws MismatchedDimensionException {
        if (ordinates.length != 2) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_$1, ordinates.length));
        }
        ordinates[0] = point.getX();
        ordinates[1] = point.getY();
    }

    /**
     * Returns a {@link Point2D} with the same coordinate as this direct position.
     * This is a convenience method for inter-operability with Java2D.
     *
     * @return This position as a two-dimensional point.
     * @throws IllegalStateException if this coordinate point is not two-dimensional.
     */
    public Point2D toPoint2D() throws IllegalStateException {
        if (ordinates.length != 2) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_$1, ordinates.length));
        }
        return new Point2D.Double(ordinates[0], ordinates[1]);
    }

    /**
     * Returns a hash value for this coordinate.
     */
    @Override
    public int hashCode() {
        int code = Arrays.hashCode(ordinates);
        if (crs != null) {
            code += crs.hashCode();
        }
        assert code == super.hashCode();
        return code;
    }

    /**
     * Returns a deep copy of this position.
     */
    @Override
    public GeneralDirectPosition clone() {
        try {
            GeneralDirectPosition e = (GeneralDirectPosition) super.clone();
            final Field field = GeneralDirectPosition.class.getDeclaredField("ordinates");
            field.setAccessible(true);
            field.set(e, ordinates.clone());
            return e;
        } catch (Exception exception) { // Too many exception here to catch all of them.
            // Should not happen, since we are cloneable.
            // Should not happen, since the "ordinates" field exists.
            // etc...
            throw new AssertionError(exception);
        }
    }
}
