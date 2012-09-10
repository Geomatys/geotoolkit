/*
 * Map and oceanographical data visualisation
 * Copyright (C) 2012 Geomatys
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details (http://www.gnu.org/).
 */
package org.geotoolkit.process.coverage.kriging;

import java.util.Arrays;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;


/**
 * A polyline in process of being created by {@link IsolineCreator}. The ranges of coordinate
 * values are (0,0) inclusive to (<var>width</var>, <var>height</var>) exclusive. For every
 * (<var>x</var>,<var>y</var>) coordinate, at least one of <var>x</var> or <var>y</var> is an
 * integer, because of the way intersections are calculated by {@link IsolineCreator}.
 * <p>
 * Every coordinates in this object are stored as ({@code int}, {@code double}) tuples.
 * The {@code int} value is positive if it stands for the <var>x</var> ordinate values,
 * or negative (with all bits reversed by the {@code ~} operator) if it stands for the
 * <var>y</var> ordinate value. This is a slightly more compact storage scheme than the
 * plain {@code double} pairs (25% less space) and make slightly easier (only one
 * comparison instead of two) to identify on which grid line a point is located.
 *
 * @version 3.20
 * @author Martin Desruisseaux
 * @module pending
 *
 * @since 3.20
 */
final class Polyline implements CoordinateSequence {
    /**
     * The grid line on which the intersection is calculated. Values range from
     * {@code 0} inclusive to {@code width} exclusive for vertical grid lines, or from
     * {@code ~0} inclusive to {@code ~height} exclusive for horizontal grid line.
     */
    private int[] gridLines;

    /**
     * The intersection location on a grid line. For each element in this array, the value
     * is a <var>x</var> ordinate if the corresponding {@link #gridLines} is negative, or
     * a <var>y</var> ordinate otherwise.
     */
    private double[] ordinates;

    /**
     * The number of points in this object. This determines the length of the valid part
     * in the {@link #gridLines} and {@link #ordinates} arrays.
     */
    private int size;

    /**
     * Creates a new polyline initialized to the given line segment. The {@code gridLine}
     * arguments must be compliant with the {@link #gridLines} documentation.
     */
    Polyline(final int gridLine1, final double ordinate1,
             final int gridLine2, final double ordinate2)
    {
        gridLines = new int   [] {gridLine1, gridLine2};
        ordinates = new double[] {ordinate1, ordinate2};
        size = 2;
    }

    /**
     * Returns the grid line number at the given index.
     * See class javadoc for a note about sign convention.
     */
    final int gridLine(final int i) {
        return gridLines[i];
    }

    /**
     * Returns the ordinate at the given index. It may be either the <var>x</var> or
     * <var>y</var> ordinate value, depending on the {@linkplain #gridLine(int)} sign.
     */
    final double ordinate(final int i) {
        return ordinates[i];
    }

    /**
     * Encodes the given coordinate in a single long value. The {@code gridLine} argument shall
     * be compliant with the sign convention documented in the {@link #gridLines} javadoc. The
     * {@code ordinate} value will be casted to an integer since we should have at most one
     * intersection per cell edge.
     */
    static Long key(final int gridLine, final double ordinate) {
        return (((long) gridLine) << Integer.SIZE) | (int) ordinate;
    }

    /**
     * Returns the key at the start or end of this polyline.
     *
     * @param start {@code true} for the key value at the polyline start,
     *        or {@code false} for the key value at the polyline end.
     */
    final Long key(final boolean start) {
        final int index = start ? 0 : size-1;
        final long key = key(gridLines[index], ordinates[index]);
        assert startsWith(key) == start || isClosed();
        return key;
    }

    /**
     * Returns {@code true} if the given key stands for the starting point of this line segment,
     * or {@code false} if it stands for the ending point. This method shall be invoked only when
     * the given point is known to be either at the polyline beginning or the end, otherwise an
     * {@link AssertionError} may be thrown.
     */
    final boolean startsWith(final long key) {
        final int gridLine = (int) (key >>> Integer.SIZE);
        if (gridLine != gridLines[0]) {
            // If this is not the first point, then it must be the last point.
            assert gridLine == gridLines[size-1] : gridLine;
            return false;
        }
        /*
         * The above check is suffisient in many cases. However we sometime have an
         * ambiguity. In such case, we need to check also the other ordinate value.
         */
        if (gridLine == gridLines[size-1]) {
            final int ordinate = (int) (key & 0xFFFFFFFFL);
            if (ordinate != (int) ordinates[0]) {
                assert ordinate == (int) ordinates[size-1] : ordinate;
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the start point and end point are the same.
     */
    final boolean isClosed() {
        return gridLines[0] == gridLines[size-1]
            && ordinates[0] == ordinates[size-1];
    }

    /**
     * Appends a single point to this polyline. This method shall be invoked only for polyline
     * having at least two points.
     *
     * @param key      The key which was used for storing this polyline in the hash map.
     * @param gridLine The grid line value, must be compliant with the {@link #gridLines} sign convention.
     * @param ordinate The ordinate value.
     */
    final void append(final Long key, final int gridLine, final double ordinate) {
        assert !isClosed();
        final int     size = this.size;
        int[]    gridLines = this.gridLines;
        double[] ordinates = this.ordinates;
        if (size == gridLines.length) {
            this.gridLines = gridLines = Arrays.copyOf(gridLines, size*2);
            this.ordinates = ordinates = Arrays.copyOf(ordinates, size*2);
        }
        /*
         * The given coordinate can be either at the begining or end of this polyline.
         * If it is at the begining, then we need to reverse the coordinate order before
         * to append.
         */
        if (startsWith(key)) {
            int i = size >>> 1;
            int j = i + (size & 1);
            while (--i >= 0) {
                final int    ti = gridLines[i]; gridLines[i] = gridLines[j]; gridLines[j] = ti;
                final double td = ordinates[i]; ordinates[i] = ordinates[j]; ordinates[j] = td;
                j++;
            }
        }
        gridLines[size] = gridLine;
        ordinates[size] = ordinate;
        this.size++;
        assert isValid();
    }

    /**
     * Merges the content of this polyline with the {@code toMerge} polyline.
     * If the polyline to merge is {@code this}, then this method will close
     * the polyline as an island.
     *
     * @param  toMerge The polyline to insert in this polyline.
     * @return The merged polyline.
     */
    final Polyline merge(final Long key, final Long keyOther, final Polyline toMerge) {
        assert !isClosed();
        if (toMerge == this) {
            final int     size = this.size;
            int[]    gridLines = this.gridLines;
            double[] ordinates = this.ordinates;
            if (size == gridLines.length) {
                this.gridLines = gridLines = Arrays.copyOf(gridLines, size+1);
                this.ordinates = ordinates = Arrays.copyOf(ordinates, size+1);
            }
            gridLines[size] = gridLines[0];
            ordinates[size] = ordinates[0];
            this.size++;
            assert isValid();
        } else {
            final boolean prepend = startsWith(key);
            final boolean mgStart = toMerge.startsWith(keyOther);
            final boolean reverse = (prepend == mgStart);
            /*
             * Determine if the merging should be done on the 'toMerge' instance instead.
             * We do that for example if the other instance has sufficient capacity while
             * this instance does not.
             */
            final int newLength = size + toMerge.size;
            if (newLength > ordinates.length && newLength <= toMerge.ordinates.length) {
                toMerge.merge(this, mgStart, reverse);
                return toMerge;
            }
            merge(toMerge, prepend, reverse);
        }
        return this;
    }

    /**
     * Actually perform the merge.
     *
     * @param prepend {@code true} for prepending {@code toMerge}, or {@code false} for appending..
     * @param reverse {@code true} if the {@code toMerge} data shall be copied in reverse order.
     */
    private void merge(final Polyline toMerge, final boolean prepend, final boolean reverse) {
        int[]     gridLines = this.gridLines;
        double[]  ordinates = this.ordinates;
        int       addLength = toMerge.size;
        final int newLength = addLength + size;
        int copyAt;
        if (!prepend) { // Append case
            if (newLength > ordinates.length) {
                this.gridLines = gridLines = Arrays.copyOf(gridLines, newLength * 2);
                this.ordinates = ordinates = Arrays.copyOf(ordinates, newLength * 2);
            }
            copyAt = size;
        } else {
            if (newLength > ordinates.length) {
                gridLines = new int   [newLength * 2];
                ordinates = new double[newLength * 2];
                // Variables and fields will be different for a short time.
            }
            System.arraycopy(this.gridLines, 0, gridLines, addLength, size);
            System.arraycopy(this.ordinates, 0, ordinates, addLength, size);
            this.gridLines = gridLines;
            this.ordinates = ordinates;
            copyAt = 0;
        }
        if (reverse) {
            while (--addLength >= 0) {
                gridLines[copyAt] = toMerge.gridLines[addLength];
                ordinates[copyAt] = toMerge.ordinates[addLength];
                copyAt++;
            }
        } else {
            System.arraycopy(toMerge.gridLines, 0, gridLines, copyAt, addLength);
            System.arraycopy(toMerge.ordinates, 0, ordinates, copyAt, addLength);
        }
        size = newLength;
        assert isValid();
    }

    // ---- JTS methods ---------------------------------------------------------------------------

    /**
     * Returns the number of dimensions.
     */
    @Override
    public int getDimension() {
        return 2;
    }

    /**
     * Returns the number of points.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the coordinate at the given index. This method delegates to
     * {@link #getCoordinateCopy(int)} since this class does not store any
     * coordinate instance.
     */
    @Override
    public Coordinate getCoordinate(final int i) {
        return getCoordinateCopy(i);
    }

    /**
     * Returns a copy of the coordinate at the given index.
     */
    @Override
    public Coordinate getCoordinateCopy(final int i) {
        final Coordinate coord = new Coordinate();
        getCoordinate(i, coord);
        return coord;
    }

    /**
     * Stores the coordinate value in the given object.
     */
    @Override
    public void getCoordinate(final int i, final Coordinate target) {
        final int    x = gridLines[i];
        final double y = ordinates[i];
        if (x >= 0) {
            target.x =  x;
            target.y =  y;
        } else {
            target.x =  y;
            target.y = ~x;
        }
        target.z = Double.NaN;
    }

    /**
     * Returns the <var>x</var> value at the given index.
     */
    @Override
    public double getX(final int i) {
        final int x = gridLines[i];
        return (x >= 0) ? x : ordinates[i];
    }

    /**
     * Returns the <var>x</var> value at the given index.
     */
    @Override
    public double getY(final int i) {
        final int x = gridLines[i];
        return (x >= 0) ? ordinates[i] : ~x;
    }

    /**
     * Returns the ordinate value at the given index in the given dimension.
     */
    @Override
    public double getOrdinate(final int i, final int dim) {
        switch (dim) {
            case 0: return getX(i);
            case 1: return getY(i);
            case 2: return Double.NaN;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Unsupported operation, since we do not allow polyline modification through this API.
     */
    @Override
    public void setOrdinate(final int i, final int dim, final double ordinate) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns all coordinates.
     */
    @Override
    public Coordinate[] toCoordinateArray() {
        final Coordinate[] array = new Coordinate[size];
        for (int i=0; i<array.length; i++) {
            array[i] = getCoordinateCopy(i);
        }
        return array;
    }

    /**
     * Unsupported operation, since we do not allow polyline modification through this API.
     */
    @Override
    public Envelope expandEnvelope(Envelope envlp) {
        throw new UnsupportedOperationException();
    }

    /**
     * Overridden because the JTS API require us to do so, but returns {@code this} since
     * {@code Polyline} are unmodifiable through the JTS API.
     */
    @Override
    public Object clone() {
        return this;
    }

    // Do not override equals(Object) and hashCode(), since identity comparison is okay for
    // IsolineCreator usage (Polylines are put in HashSet).

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(60);
        buffer.append("Polyline[size=").append(size);
        String separator = ": ";
        for (int i=0; i<size; i++) {
            buffer.append(separator).append('(').append(getX(i)).append(", ").append(getY(i)).append(')');
            separator = ", ";
            if (i == 2 && size >= 7) {
                separator = " … ";
                i = size - 3; // Skip some values.
            }
        }
        return buffer.append(']').toString();
    }

    /**
     * Tests the validity of this {@link Polyline} object. This method verifies that all points
     * are unique, and that the distance between them is less than 2. This method is used for
     * assertions only.
     */
    private boolean isValid() {
        Coordinate previous = null;
        for (int i=0; i<size; i++) {
            final Coordinate c = getCoordinate(i);
            if (previous != null) {
                final double d = c.distance(previous);
                if (!(d > 0 && d*d < IntersectionGrid.MAX_DISTANCE_SQUARED)) { // Use ! for catching NaN.
                    throw new AssertionError("distance(" + (i-1) + '-' + i + ")=" + d + " in "+ this);
                }
            }
            previous = c;
        }
        return true;
    }
}
