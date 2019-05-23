/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import java.util.Arrays;
import java.io.Serializable;

import org.opengis.coverage.grid.Grid;
import org.opengis.coverage.grid.GridPoint;
import org.opengis.coverage.grid.GridCoordinates;

import org.geotoolkit.util.Cloneable;
import org.apache.sis.util.Classes;


/**
 * Holds the set of grid coordinates that specifies the location of the
 * {@linkplain GridPoint grid point} within the {@linkplain Grid grid}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GridCoordinates2D
 *
 * @since 2.4
 * @module
 */
public class GeneralGridCoordinates implements GridCoordinates, Cloneable, Serializable {
    /**
     * The grid coordinates.
     */
    final long[] coordinates;

    /**
     * Creates a grid coordinates of the specified dimension.
     * All coordinates are initially set to 0.
     *
     * @param dimension The number of dimension.
     */
    public GeneralGridCoordinates(final int dimension) {
        coordinates = new long[dimension];
    }

    /**
     * Creates a grid coordinates initialized to the specified values.
     *
     * @param coordinates The grid coordinates to copy.
     */
    @Deprecated
    public GeneralGridCoordinates(final int[] coordinates) {
        this.coordinates = new long[coordinates.length];
        for (int i=0; i<coordinates.length; i++) {
            this.coordinates[i] = coordinates[i];
        }
    }

    /**
     * Creates a grid coordinates initialized to the specified values.
     *
     * @param coordinates The grid coordinates to copy.
     */
    public GeneralGridCoordinates(final long[] coordinates) {
        this.coordinates = coordinates.clone();
    }

    /**
     * Creates a grid coordinates initialized to the specified values in the specified range.
     *
     * @param coordinates The coordinates to copy.
     * @param lower Index of the first value to copy, inclusive.
     * @param upper Index of the last value to copy, exclusive.
     *
     * @since 2.5
     */
    public GeneralGridCoordinates(final long[] coordinates, final int lower, final int upper) {
        this.coordinates = Arrays.copyOfRange(coordinates, lower, upper);
    }

    /**
     * Creates a grid coordinates which is a copy of the specified one.
     *
     * @param coordinates The grid coordinates to copy.
     *
     * @since 2.5
     */
    public GeneralGridCoordinates(final GridCoordinates coordinates) {
        this.coordinates = coordinates.getCoordinateValues();
    }

    /**
     * Returns the number of dimensions. This method is equivalent to
     * <code>{@linkplain #getCoordinateValues()}.length</code>. It is
     * provided for efficiency.
     */
    @Override
    public int getDimension() {
        return coordinates.length;
    }

    /**
     * Returns one integer value for each dimension of the grid. The ordering of these coordinate
     * values shall be the same as that of the elements of {@link Grid#getAxisNames}. The value of
     * a single coordinate shall be the number of offsets from the origin of the grid in the
     * direction of a specific axis.
     *
     * @return A copy of the coordinates. Changes in the returned array will not be reflected
     *         back in this {@code GeneralGridCoordinates} object.
     */
    @Override
    public long[] getCoordinateValues() {
        return coordinates.clone();
    }

    /**
     * Returns the coordinate value at the specified dimension. This method is equivalent to
     * <code>{@linkplain #getCoordinateValues()}[<var>i</var>]</code>. It is provided for
     * efficiency.
     *
     * @param  dimension The dimension from 0 inclusive to {@link #getDimension} exclusive.
     * @return The value at the requested dimension.
     * @throws ArrayIndexOutOfBoundsException if the specified dimension is out of bounds.
     */
    @Override
    public long getCoordinateValue(final int dimension) throws ArrayIndexOutOfBoundsException {
        return coordinates[dimension];
    }

    /**
     * Sets the coordinate value at the specified dimension (optional operation).
     *
     * @param  dimension The index of the value to set.
     * @param  value The new value.
     * @throws ArrayIndexOutOfBoundsException if the specified dimension is out of bounds.
     * @throws UnsupportedOperationException if this grid coordinates is not modifiable.
     */
    @Override
    public void setCoordinateValue(final int dimension, final long value)
            throws ArrayIndexOutOfBoundsException, UnsupportedOperationException
    {
        coordinates[dimension] = value;
    }

    /**
     * Returns a string representation of the specified grid coordinates.
     */
    static String toString(final GridCoordinates coordinates) {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(coordinates));
        final int dimension = coordinates.getDimension();
        for (int i=0; i<dimension; i++) {
            buffer.append(i==0 ? '[' : ',').append(coordinates.getCoordinateValue(i));
        }
        return buffer.append(']').toString();
    }

    /**
     * Returns a string representation of this grid coordinates.
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates) ^ 45;
    }

    /**
     * Compares this grid coordinates with the specified object for equality.
     *
     * @param object The object to compares with this grid coordinates.
     * @return {@code true} if the given object is equal to this grid coordinates.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization.
            return true;
        }
        // We do not require the exact same class because we clones of
        // immutable grid coordinates to be equal to their original coordinates.
        if (object instanceof GeneralGridCoordinates) {
            final GeneralGridCoordinates that = (GeneralGridCoordinates) object;
            return Arrays.equals(this.coordinates, that.coordinates);
        }
        return false;
    }

    /**
     * Returns a clone of this grid coordinates.
     *
     * @return A clone of this grid coordinates.
     */
    @Override
    public GeneralGridCoordinates clone() {
        try {
            return (GeneralGridCoordinates) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * An immutable {@link GridCoordinates}. This is sometime useful for creating a single
     * instance to be shared by many objects without the cost of cloning. This class is
     * final in order to prevent subclasses from making it mutable again.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 2.5
     * @module
     */
    public static final class Immutable extends GeneralGridCoordinates {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -7723383411061425866L;

        /**
         * Creates an immutable grid coordinates with the specified values.
         *
         * @param coordinates The grid coordinates to copy.
         */
        public Immutable(final long[] coordinates) {
            super(coordinates);
        }

        /**
         * Creates an immutable grid coordinates with the specified values in the specified range.
         *
         * @param coordinates The coordinates to copy.
         * @param lower Index of the first value to copy, inclusive.
         * @param upper Index of the last value to copy, exclusive.
         */
        public Immutable(final long[] coordinates, final int lower, final int upper) {
            super(coordinates, lower, upper);
        }

        /**
         * Creates an immutable grid coordinates with the specified values.
         *
         * @param coordinates The grid coordinates to copy.
         */
        public Immutable(final GridCoordinates coordinates) {
            super(coordinates);
        }

        /**
         * Decrements all ordinate values. This method is for internal usage by
         * {@link GeneralGridEnvelope} only, to be invoked only right after construction
         * and before the instance goes public. This method should not be public since it
         * breaks the immutability contract.
         */
        final void decrement() {
            for (int i=0; i<coordinates.length; i++) {
                coordinates[i]--;
            }
        }

        /**
         * Do not allows modification of this grid coordinates.
         *
         * @throws UnsupportedOperationException always thrown.
         */
        @Override
        public void setCoordinateValue(final int dimension, final long value)
                throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns a mutable clone of this grid coordinates. The clone is an instance of
         * {@link GeneralGridCoordinates} rather than this {@code Immutable} subclass.
         *
         * @return A mutable clone of this grid coordinates.
         */
        @Override
        public GeneralGridCoordinates clone() {
            return new GeneralGridCoordinates(coordinates);
        }
    }
}
