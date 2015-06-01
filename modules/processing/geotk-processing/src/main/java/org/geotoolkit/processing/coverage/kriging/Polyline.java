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
package org.geotoolkit.processing.coverage.kriging;

import java.util.Set;
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
     * Creates an initially empty polyline.
     */
    Polyline() {
        gridLines = new int   [8];
        ordinates = new double[8];
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
        return (size >= 2)
                && gridLines[0] == gridLines[size-1]
                && ordinates[0] == ordinates[size-1];
    }

    /**
     * Appends a single point to this polyline. This method shall be invoked only for polyline
     * having at least two points.
     *
     * @param reverse  {@code true} if this polyline need to be reversed before to add the point.
     * @param gridLine The grid line value, must be compliant with the {@link #gridLines} sign convention.
     * @param ordinate The ordinate value.
     */
    final void append(final boolean reverse, final int gridLine, final double ordinate) {
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
        if (reverse) {
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
        assert checkSegmentLengths(size-1) : this;
        assert isClosed() || !contains(gridLine, ordinate, size) : this;
    }

    /**
     * Merges the content of this polyline with the {@code toMerge} polyline.
     * If the polyline to merge is {@code this}, then this method will close
     * the polyline as an island.
     *
     * @param  toMerge The polyline to insert in this polyline.
     * @return The merged polyline.
     */
    final Polyline merge(final Long key, final Long keyOther, final Polyline toMerge, final int skip) {
        assert !isClosed() : this;
        assert !toMerge.isClosed() : toMerge;
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
            assert checkSegmentLengths(0);
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
                toMerge.merge(this, mgStart, reverse, skip);
                return toMerge;
            }
            merge(toMerge, prepend, reverse, skip);
        }
        return this;
    }

    /**
     * Actually performs the merge.
     *
     * @param prepend {@code true} for prepending {@code toMerge}, or {@code false} for appending..
     * @param reverse {@code true} if the {@code toMerge} data shall be copied in reverse order.
     */
    private void merge(final Polyline toMerge, final boolean prepend, final boolean reverse, int skip) {
        int[]     gridLines = this.gridLines;
        double[]  ordinates = this.ordinates;
        int       addLength = toMerge.size - skip;
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
            if (reverse) addLength += skip;
            copyAt = 0;
            skip   = 0;
        }
        if (reverse) {
            while (--addLength >= 0) {
                gridLines[copyAt] = toMerge.gridLines[addLength];
                ordinates[copyAt] = toMerge.ordinates[addLength];
                copyAt++;
            }
        } else {
            System.arraycopy(toMerge.gridLines, skip, gridLines, copyAt, addLength);
            System.arraycopy(toMerge.ordinates, skip, ordinates, copyAt, addLength);
        }
        size = newLength;
        assert checkSegmentLengths(0);
    }

    /**
     * Appends the neighbor points only if there is no ambiguity.
     *
     * @param  gridLines        The set of horizontal or vertical grid lines from the {@code grid}.
     * @param  perpendicular    The set of grid lines perpendicular to {@link #gridLines}.
     * @param  signConvention   0 if {@code gridLines} are vertical, or -1 if they are horizontal.
     * @param  gridLineIndex    Index of this {@code Intersections} instance in {@code gridLines}.
     * @param  ordinateIndex    Index of the intersection point in this grid line.
     * @param  extremities      Positions of extremities of existing polylines.
     */
    @SuppressWarnings("fallthrough")
    final void joinNonAmbiguous(Intersections[] gridLines, Intersections[] perpendicular,
            int signConvention, int gridLineIndex, int ordinateIndex,
            final Set<Long> extremities)
    {
        Intersections  gridLine    = gridLines[gridLineIndex];
        double         ordinate    = gridLine.ordinate(ordinateIndex);
        final Neighbor neighbor1   = new Neighbor();
        Neighbor       neighbor2   = null;
        Neighbor       deferred    = null;
        int            searchIndex = 0; // Keept as a slight optimization.
        Intersections  previousGridLine = null;
        double         previousOrdinate = 0;
        do {
            int       pointCount   = 0; // Number of neighbor points found.
            boolean   isValid      = false;
            Neighbor  neighbor     = neighbor1;
            final int intOrdinate  = (int) ordinate;
            /*
             * We will inspect the intersection points on various grid lines,
             * which will be identified by the 'gridLineId' identifier as below:
             *
             *   -1  :  The grid line after this one (below for horizontal grid lines).
             *    0  :  The grid line represented by this instance.
             *    1  :  The grid line before this one (above for horizontal grid lines).
             *    2  :  The grid line perpendicular to this one at ((int) ordinate) + 1.
             *    3  :  The grid line perpendicular to this one at ((int) ordinate).
             *    4  :  The grid line perpendicular to this one at ((int) ordinate) - 1.
             *    5  :  Iteration limit (should never be reached).
             *
             * On each grid line, we will consider two points: one having an ordinate value
             * lower than the target ordinate value, and one having a grater ordinate value.
             * Consequently the range of pointId identifiers is twice the one of gridLineId.
             * Identifiers are illustrated below. Values in [ ] are 'gridLineId' and values
             * in ( ) are 'pointId', with (*) as the point given in argument to this method.
             *
             *
             *                          [4]           [3]           [2]
             *             │             │             │             │             │
             *      [1]────┼─────────────┼─────────────┼─(3)─────(2)─┼─────────────┼────
             *             │             │             │             │             │
             *             │            (9)           (7)           (5)            │
             *             │             │             │             │             │
             *      [0]────┼─────────────┼───────(1)───┼─────(*)─────┼───(0)───────┼────
             *             │             │             │             │             │
             *             │            (8)           (6)           (4)            │
             *             │             │             │             │             │
             *     [-1]────┼─────────────┼─────────────┼─(-1)───(-2)─┼─────────────┼────
             *             │             │             │             │             │
             *
             *
             * The vertical grid line [4] will be tested only if the (*) point is located
             * on the intersection of [0] and [3].
             */
            final int pointIdStop = (ordinate != intOrdinate) ? 8 : 10;
nextPoint:  for (int pointId=-2; pointId<pointIdStop; pointId++) {
                final int      gridLineId      = pointId >> 1;
                final boolean  isPerpendicular = (gridLineId >= 2);
                final double   ordinateToSearch;
                final Intersections neighborLine;
                if (!isPerpendicular) {
                    neighborLine = neighbor.setGridLine(gridLines, gridLineIndex - gridLineId);
                    if (neighborLine == null) {
                        pointId |= 1; // If the next iteration is on the same line, skip it.
                        continue nextPoint;
                    }
                    neighbor.distanceSquared = gridLineId & 1; // == (id*id) when id = -1, 0 or +1.
                    ordinateToSearch = ordinate;
                } else {
                    neighborLine = neighbor.setGridLine(perpendicular, intOrdinate - (gridLineId - 3));
                    if (neighborLine == null) {
                        pointId |= 1; // If the next iteration is on the same line, skip it.
                        continue nextPoint;
                    }
                    neighbor.distanceSquared = ordinate - neighbor.gridLineIndex;
                    neighbor.distanceSquared *= neighbor.distanceSquared;
                    ordinateToSearch = gridLineIndex;
                }
                /*
                 * If the neighbor grid line is the 'gridLine' instance,  then we already know
                 * the index of the ordinate value to search since it was given in argument to
                 * this method. Otherwise we will need to perform a binary search.
                 */
                if (gridLineId == 0) {
                    neighbor.ordinateIndex = ordinateIndex + ((pointId & 1) == 0 ? +1 : -1);
                } else {
                    if ((pointId & 1) == 0) {
                        /*
                         * Performs the binary search only the first time that we are searching
                         * on a new grid line; the result will be saved for the second time.
                         */
                        searchIndex = neighborLine.binarySearch(ordinateToSearch);
                        if (searchIndex >= 0) {
                            // If we found an exact match, we can skip the next iteration
                            // on the same line. The 'pointId |= 1' will cause that.
                            pointId |= 1;
                        } else {
                            // For this first iteration on the current grid line,
                            // search the ordinate value after the searched one.
                            searchIndex = ~searchIndex;
                        }
                    } else {
                        // The the second iteration on the current grid line,
                        // search the ordinate value before the searched one.
                        assert searchIndex == ~neighborLine.binarySearch(ordinateToSearch);
                        searchIndex--;
                    }
                    neighbor.ordinateIndex = searchIndex;
                }
                /*
                 * At this point we have the index of an ordinate value close to the one we
                 * are looking for. Now fetch the neighbor ordinate value.
                 */
                if (neighbor.ordinateIndex < 0 || neighbor.ordinateIndex >= neighborLine.size()) {
                    continue nextPoint; // Out of line bounds, check an other point.
                }
                neighbor.ordinate = neighborLine.ordinate(neighbor.ordinateIndex);
                if (neighborLine == previousGridLine && neighbor.ordinate == previousOrdinate) {
                    continue nextPoint; // Prevent searching back and forward between 2 points.
                }
                final double delta = neighbor.ordinate - ordinateToSearch;
                neighbor.distanceSquared += delta*delta; // Final value for this variable.
                /*
                 * Check if 'neighbor.ordinate' is in the same column (for horizontal grid
                 * lines) or row (for vertical grid lines) than the ordinate we search for.
                 * This condition help to avoid geometric impossibilities.
                 */
                final int intSearched = isPerpendicular ? gridLineIndex : intOrdinate;
                final int intNeighbor = (int) neighbor.ordinate;
                switch (intNeighbor - intSearched) {
                    case 0: {
                        break; // Accept points on the same row or column.
                    }
                    case 1: {
                        // 'neighbor.ordinate' may be on the next row/column.  Accept only if
                        // the neighbor is on the edge between the current cell and the other
                        // cell, since it could actually belong to any of those two cells.
                        if ((Intersections.ACCEPT_NEIGHBOR_ON_SAME_LINE && gridLineId == 0) || neighbor.ordinate == intNeighbor) {
                            break; // Accept the point.
                        }
                        continue nextPoint; // Reject.
                    }
                    case -1: {
                        // 'neighbor.ordinate' may be on the previous row or column. Accept
                        // only if the ordinate is on the edge between the current cell and
                        // the other cell, since it could actually belong to any of those two
                        // cells. Note: search for perpendicular values are always on the edge.
                        if (isPerpendicular || (Intersections.ACCEPT_NEIGHBOR_ON_SAME_LINE && gridLineId == 0) || ordinate == intOrdinate) {
                            break; // Accept the point.
                        }
                        continue nextPoint; // Reject.
                    }
                    default: {
                        continue nextPoint; // Reject points that are too far away.
                    }
                }
                /*
                 * We have found a point that we could join. If we already found other such points
                 * before, then we have an ambiguity unless we are just starting the first segment.
                 * In the later case, we can accept only two points.
                 */
                switch (++pointCount) {
                    case 1: {
                        // This is the first point that we have found. Switch the working variable
                        // to 'neighbor2' in order to keep 'neighbor1' unchanged for the remaining
                        // of this loop.
                        if (neighbor2 == null) {
                            neighbor2 = new Neighbor();
                        }
                        neighbor = neighbor2;
                        isValid  = true;
                        break;
                    }
                    case 2: {
                        // This is the second point that we have found. This situation may be
                        // non-ambiguous only if we are just starting the first segment. In
                        // such case, create a new working variable in order to keep 'neighbor2'
                        // unchanged for the remaining of this loop.
                        if (size == 0) {
                            neighbor = new Neighbor();
                            break;
                        }
                        // Fallthrough
                    }
                    default: {
                        // Found more than 2 points. There is definitively an ambiguity.
                        isValid = false;
                        break; // TODO: store neighbor1, neighbor2, neighbor and all future points in an array.
                    }
                }
            }
            /*
             * Done inspecting neighbor intersection points. If we found a non-ambiguous point,
             * then add the neighbor point to this polyline and remove the previous point from
             * the list of available intersections.
             */
            assert gridLines        [gridLineIndex] == gridLine;
            assert gridLine.ordinate(ordinateIndex) == ordinate;
                            previousGridLine      = gridLine; // For the next iteration.
                            previousOrdinate      = ordinate;
            Intersections[] previousGridLines     = gridLines;
            final int       previousGridLineIndex = gridLineIndex;
            final int       previousOrdinateIndex = ordinateIndex;
            final Long      previousKey = key(gridLineIndex ^ signConvention, ordinate);
            final boolean   reverse;
            final Neighbor  toAdd;
            if (isValid) {
                /*
                 * If we found two points, remember the second point for deferred processing.
                 * Only after we finished to follow all the points on one side, then we will
                 * follow the points on the deferred side.
                 */
                if (pointCount == 2) {
                    assert neighbor2.isValid();
                    assert deferred == null;
                    assert size == 0;
                    deferred = neighbor2;
                    neighbor2 = null; // For preventing 'deferred' to be modified.
                }
                assert neighbor1.isValid();
                toAdd = neighbor1;
                reverse = false;
            } else {
                /*
                 * If we either had no point to add, or if we had an ambiguity between too many
                 * points, stop the search on side 1. If we have keep the other side for deferred
                 * execution, then continue on that other side.
                 */
                if (deferred == null || !deferred.fixOrdinateIndex()) {
                    return;
                }
                assert size >= 2 : size;
                assert deferred.isValid();
                toAdd             = deferred;
                reverse           = true;
                deferred          = null;
                previousGridLines = null;
            }
            /*
             * Add a single point (or exceptionally add two points if we are just starting the
             * polyline), then remove the previous point from the list of available intersections.
             */
            if (size == 0) {
                // Starting a new line segment: add the first point. We usually have two points
                // to start with. If this is not the case, cancel the point removal since wa are
                // already at a polyline extremity.
                append(false, gridLineIndex ^ signConvention, ordinate);
                if (deferred == null) {
                    previousGridLines = null;
                }
            }
            /*
             * Move to the neighbor point. If the neighbor point has been found on a perpendicular
             * grid lines, then we need to swap the 'gridLines' and 'perpendicular' variables.
             */
            if (toAdd.gridLines != gridLines) {
                assert toAdd.gridLines == perpendicular;
                perpendicular  = gridLines;
                gridLines      = toAdd.gridLines;
                signConvention = ~signConvention;
            }
            gridLineIndex = toAdd.gridLineIndex;
            gridLine      = gridLines[gridLineIndex];
            ordinate      = toAdd.ordinate;
            ordinateIndex = toAdd.ordinateIndex;
            append(reverse, gridLineIndex ^ signConvention, ordinate);
            /*
             * Delete the point only after we added them to the polyline, because this operation
             * may invalidate some 'ordinateIndex' field values and cause NullPointerException if
             * it was executed before the above 'gridLine' field is updated.
             */
            if (previousGridLines != null && !extremities.contains(previousKey)) {
                if (previousGridLines[previousGridLineIndex].removeAt(previousOrdinateIndex)) {
                    previousGridLines[previousGridLineIndex] = null;
                }
                // Index may have been invalidated by the above removal, so fix it.
                ordinateIndex = gridLine.binarySearch(ordinate, ordinateIndex);
            }
        } while (!isClosed());
        /*
         * We reach this point only if the polyline is actually a closed polygon.
         * The 'gridLine' and 'ordinate' variables are back to their original value.
         * Remove that point.
         */
        assert gridLine.ordinate(ordinateIndex) == ordinate;
        if (!extremities.contains(key(gridLineIndex ^ signConvention, ordinate))) {
            if (gridLine.removeAt(ordinateIndex)) {
                gridLines[gridLineIndex] = null;
            }
        }
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

    // ---- Assertions ----------------------------------------------------------------------------

    /**
     * Tests the validity of this {@link Polyline} object. This method verifies that all points
     * are unique, and that the distance between them is less than 2. This method is used for
     * assertions only.
     */
    private boolean checkSegmentLengths(final int from) {
        Coordinate previous = null;
        for (int i=Math.max(0, from); i<size; i++) {
            final Coordinate c = getCoordinate(i);
            if (previous != null) {
                final double d = c.distance(previous);
                if (!(d > 0 && d*d <= IntersectionGrid.MAX_DISTANCE_SQUARED)) { // Use ! for catching NaN.
                    throw new AssertionError("distance(" + (i-1) + '-' + i + ")=" + d + " in "+ this);
                }
            }
            previous = c;
        }
        return true;
    }

    /**
     * Returns {@code true} if this polyline contains the given coordinate.
     */
    private boolean contains(final int gridLine, final double ordinate, final int n) {
        for (int i=0; i<n; i++) {
            if (gridLines[i] == gridLine && ordinates[i] == ordinate) {
                return true;
            }
        }
        return false;
    }
}
