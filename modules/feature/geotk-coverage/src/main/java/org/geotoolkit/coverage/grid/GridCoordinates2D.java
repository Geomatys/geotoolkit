/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.Point;
import org.opengis.coverage.grid.Grid;
import org.opengis.coverage.grid.GridPoint;
import org.opengis.coverage.grid.GridCoordinates;

import org.geotoolkit.util.Cloneable;
import org.geotoolkit.resources.Errors;


/**
 * Holds the set of two-dimensional grid coordinates that specifies the location of the
 * {@linkplain GridPoint grid point} within the {@linkplain Grid grid}. This class extends
 * {@link Point} for inter-operability with Java2D.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GeneralGridCoordinates
 *
 * @since 2.5
 * @module
 */
public class GridCoordinates2D extends Point implements GridCoordinates, Cloneable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4583333545268906740L;

    /**
     * Creates an initially empty grid coordinates.
     */
    public GridCoordinates2D() {
        super();
    }

    /**
     * Creates a grid coordinates initialized to the specified values.
     *
     * @param x The <var>x</var> coordinate value.
     * @param y The <var>y</var> coordinate value.
     */
    public GridCoordinates2D(final int x, final int y) {
        super(x,y);
    }

    /**
     * Creates a grid coordinates initialized to the specified point.
     *
     * @param coordinates The coordinate values to copy.
     */
    public GridCoordinates2D(final Point coordinates) {
        super(coordinates);
    }

    /**
     * Returns the number of dimensions, which is always 2.
     */
    @Override
    public final int getDimension() {
        return 2;
    }

    /**
     * Returns one integer value for each dimension of the grid. This method returns
     * ({@linkplain #x x},{@linkplain #y y}) in an array of length 2.
     */
    @Override
    public long[] getCoordinateValues() {
        return new long[] {x, y};
    }

    /**
     * Returns the coordinate value at the specified dimension. This method is equivalent to
     * <code>{@linkplain #getCoordinateValues()}[<var>i</var>]</code>. It is provided for
     * efficiency.
     *
     * @param  dimension The dimension from 0 inclusive to {@link #getDimension} exclusive.
     * @return The value at the requested dimension.
     * @throws IndexOutOfBoundsException if the specified dimension is out of bounds.
     */
    @Override
    public long getCoordinateValue(final int dimension) throws IndexOutOfBoundsException {
        switch (dimension) {
            case 0:  return x;
            case 1:  return y;
            default: throw new IndexOutOfBoundsException(indexOutOfBounds(dimension));
        }
    }

    /**
     * Sets the coordinate value at the specified dimension.
     *
     * @param  dimension The index of the value to set.
     * @param  value The new value.
     * @throws IndexOutOfBoundsException if the specified dimension is out of bounds.
     * @throws UnsupportedOperationException if this grid coordinates is not modifiable.
     */
    @Override
    public void setCoordinateValue(final int dimension, final long value)
            throws IndexOutOfBoundsException, UnsupportedOperationException
    {
        switch (dimension) {
            case 0:  x = Math.toIntExact(value); break;
            case 1:  y = Math.toIntExact(value); break;
            default: throw new IndexOutOfBoundsException(indexOutOfBounds(dimension));
        }
    }

    /**
     * Formats a message for an index out of 2D bounds.
     */
    static String indexOutOfBounds(final int dimension) {
        return Errors.format(Errors.Keys.IndexOutOfBounds_1, dimension);
    }

    /**
     * Returns a string representation of this grid coordinates.
     */
    @Override
    public String toString() {
        return GeneralGridCoordinates.toString(this);
    }

    // Inherit 'hashCode()' and 'equals' from Point2D, which provides an implementation
    // aimed to be common for every Point2D subclasses (not just the Java2D ones) -  we
    // don't want to change this behavior in order to stay consistent with Java2D.

    /**
     * Returns a clone of this coordinates.
     *
     * @return A clone of this coordinates.
     */
    @Override
    public GridCoordinates2D clone() {
        return (GridCoordinates2D) super.clone();
    }
}
