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


/**
 * Contains the intersection points on a single grid line, either horizontal or vertical.
 * Whatever the grid line is horizontal or vertical is determined by the {@link #gridLine}
 * sign: positive for horizontal grid lines, or negative (using the {@code ~} operator,
 * not the minus one) for vertical grid lines.
 *
 * @version 3.20
 * @author Martin Desruisseaux
 * @module pending
 *
 * @since 3.20
 */
final class Intersections {
    /**
     * {@code true} for restricting the search for intersections to adjacent edges only.
     * This value should always be {@code true} in production environment, since a value
     * of {@code false} can produce geometric impossibilities (e.g. given three parallel
     * lines, and an oblique line crossing the first and last parallel lines but not the
     * one in the middle). However a value of {@code false} is occasionally useful for
     * debugging purpose, in order to verify if the search for nearest intersections work.
     */
    static final boolean RESTRICT_TO_ADJACENT = true;

    /**
     * Intersections on the horizontal or vertical grid line in increasing order.
     * Values vary from 0 to the image width or height, depending on whatever the
     * grid line is horizontal or vertical.
     */
    private double[] ordinates;

    /**
     * Number of valid elements in the {@link #ordinates} array.
     */
    private int size;

    /**
     * Creates an initially empty instance having the given initial capacity.
     * This capacity is usually set to a fraction of the image width or height,
     * since it will growth if needed.
     */
    Intersections(final int size) {
        ordinates = new double[Math.max(1, size)];
    }

    /**
     * Creates a copy of the given instance, using only the required array size.
     */
    Intersections(final Intersections toCopy) {
        ordinates = Arrays.copyOf(toCopy.ordinates, toCopy.size);
        size = toCopy.size;
    }

    /**
     * Removes all intersections stored in this object.
     */
    final void clear() {
        size = 0;
    }

    /**
     * Returns the number of elements in this object.
     */
    final int size() {
        return size;
    }

    /**
     * Returns the ordinate value at the given index.
     */
    final double ordinate(final int i) {
        return ordinates[i];
    }

    /**
     * Adds an intersection on this grid line. This method shall be invoked only for intersection
     * in increasing order.
     */
    final void add(final double ordinate) {
        assert (size == 0) || (ordinate > ordinates[size-1]) : ordinate;
        if (size >= ordinates.length) {
            ordinates = Arrays.copyOf(ordinates, size*2);
        }
        ordinates[size++] = ordinate;
    }

    /**
     * Removes the value at the given index.
     *
     * @return {@code true} if this instance became empty as a result of this method call.
     */
    final boolean removeAt(final int index) {
        assert (index >= 0 && index < size) : index;
        System.arraycopy(ordinates, index+1, ordinates, index, --size - index);
        return size == 0;
    }

    /**
     * Searches for the intersection point nearest to the point at the given index. If such point
     * is found, then this method callbacks {@link IntersectionGrid#createLineSegment} and removes
     * zero, one or the two points depending on the {@code createLineSegment} return value.
     * <p>
     * This method invokes itself recursively, because when a "nearest" point has been found
     * in our neighbor, we need to verify if that point has a yet nearest point in its own
     * neighbor. Consequently the {@link IntersectionGrid} callback method may be invoked an
     * arbitrary amount of time.
     *
     * @param  grid            The grid which is invoking this method. This is also the grid to callback.
     * @param  gridLines       The set of horizontal or vertical grid lines from the {@code grid}.
     * @param  perpendicular   The set of grid lines perpendicular to {@link #gridLines}.
     * @param  gridLineIndex   Index of this {@code Intersections} instance in {@code gridLines}.
     * @param  ordinateIndex   Index of the intersection point in this grid line.
     * @param  distanceSquared A value greater than the square of the maximal allowed distance.
     *                         Only points nearest than this distance will be considered.
     * @return {@code true} if a point has been found, or {@code false} otherwise. When this method
     *         returns {@code true}, the point at the given index does not exist anymore as an
     *         available intersection point (i.e. it has been integrated in a polyline).
     */
    final boolean joinNearest(final IntersectionGrid grid, final Intersections[] gridLines,
            final Intersections[] perpendicular, final int gridLineIndex, int ordinateIndex,
            final double distanceSquared)
    {
        final double ordinate = ordinates[ordinateIndex];
        Intersections excludedGridLine;
        int modCount;
        /*
         * We may need to execute the algorithm twice because the point can be associated to
         * up to 2 line segments, in which case we need to examine those two segments before
         * to report correctly if the given point still available after this method call.
         */
        do {
            modCount = grid.modCount;
            assert gridLines[gridLineIndex] == this : gridLineIndex;
            assert (ordinateIndex >= 0 && ordinateIndex < size) : ordinateIndex;
            assert ordinates[ordinateIndex] == ordinate;
            /*
             * This method needs the coordinate of a point to exclude, in order to prevent polylines
             * to be closed with themselves after only 2 points. When the point specified by the
             * (gridLineIndex, ordinateIndex) arguments is part of an existing polyline, then the
             * (excludedGridLine, excludedOrdinate) point shall be the opposite extremity of that
             * polyline.
             */
            excludedGridLine = grid.findExclusion(gridLines, gridLineIndex, ordinate);
            final double excludedOrdinate = grid.excludedOrdinate; // Must be stored now.
            double smallestDistanceSquared = distanceSquared;

            // All following fields will have a meaning only if 'nearest' is non-null.
            Intersections   nearest                = null;
            Intersections[] gridLinesOfNearest     = null;
            int             gridLineIndexOfNearest = 0;
            int             ordinateIndexOfNearest = 0;
            double          ordinateOfNearest      = 0;
            /*
             * We will inspect the intersection points on various grid lines,
             * which will be identified by the 'gridLineId' identifier as below:
             *
             *   -1  :  The grid line before this one (above for horizontal grid lines).
             *    0  :  The grid line represented by this instance.
             *    1  :  The grid line after this one (below for horizontal grid lines).
             *    2  :  The grid line perpendicular to this one at ((int) ordinate) - 1.
             *    3  :  The grid line perpendicular to this one at ((int) ordinate).
             *    4  :  The grid line perpendicular to this one at ((int) ordinate) + 1.
             *    5  :  The grid line perpendicular to this one at ((int) ordinate) + 2.
             *    6  :  Iteration limit (should never be reached).
             *
             * On each grid line, we will consider two points: one having an ordinate value
             * lower than the target ordinate value, and one having a grater ordinate value.
             * Consequently the range of pointId identifiers is twice the one of gridLineId.
             */
nextPoint:  for (int pointId=-2; pointId<(RESTRICT_TO_ADJACENT ? 10 : 12); pointId++) {
                final Intersections   neighbor;
                final Intersections[] gridLinesOfNeighbor;
                final int             gridLineIndexOfNeighbor;
                      int             ordinateIndexOfNeighbor;
                      double          ordinateOfNeighbor;
                      double          neighborDistanceSquared;
                      int             pointDirection  = (pointId & 1) == 0 ? -1 : +1;
                final int             gridLineId      = pointId >> 1;
                final boolean         isPerpendicular = (gridLineId >= 2);
                if (!isPerpendicular) {
                    gridLinesOfNeighbor     = gridLines;
                    gridLineIndexOfNeighbor = gridLineIndex + gridLineId;
                    neighborDistanceSquared = gridLineId & 1; // == (id*id) when id = -1, 0 or +1.
                } else {
                    gridLinesOfNeighbor     = perpendicular;
                    gridLineIndexOfNeighbor = ((int) ordinate) + (gridLineId - 3); // See above comment for the meaning of '3'.
                    neighborDistanceSquared = ordinate - gridLineIndexOfNeighbor;
                    neighborDistanceSquared *= neighborDistanceSquared;
                }
                if (!(neighborDistanceSquared < smallestDistanceSquared)) {
                    continue nextPoint;
                }
                /*
                 * If the "neighbor" grid line is this instance, we already know the index of the
                 * ordinate value since it was given in argument to this method. Otherwise we need
                 * to perform a binary search.
                 */
                if (gridLineId == 0) {
                    neighbor = this;
                    ordinateIndexOfNeighbor = ordinateIndex;
                } else {
                    if (gridLineIndexOfNeighbor < 0 || gridLineIndexOfNeighbor >= gridLinesOfNeighbor.length) {
                        pointId |= 1;       // If the next iteration is on the same line, skip it.
                        continue nextPoint; // Out of grid bounds, check the next grid line.
                    }
                    neighbor = gridLinesOfNeighbor[gridLineIndexOfNeighbor];
                    if (neighbor == null) {
                        pointId |= 1;       // If the next iteration is on the same line, skip it.
                        continue nextPoint; // No intersection on that particular grid line.
                    }
                    ordinateIndexOfNeighbor = Arrays.binarySearch(neighbor.ordinates, 0, neighbor.size,
                            isPerpendicular ? gridLineIndex : ordinate); // The x or y ordinate value.
                    if (ordinateIndexOfNeighbor < 0) {
                        /*
                         * The ~ordinateIndexOfNeighbor initial value is the index of an ordinate greater
                         * than the one we were searching for. Consequently if and only if pointDirection
                         * is +1 (i.e. we are indeed searching for greater values), we need to substract 1
                         * in order to cancel the effect of the '+= pointDirection' statement in the loop.
                         * The '- (pointId & 1)' trick to exactly that.
                         */
                        ordinateIndexOfNeighbor = ~ordinateIndexOfNeighbor - (pointId & 1);
                    } else if (neighbor != excludedGridLine || neighbor.ordinates[ordinateIndexOfNeighbor] != excludedOrdinate) {
                        /*
                         * If there is an exact match, set 'pointDirection' to 0 in order to
                         * use that value directly without moving to a value before or after.
                         */
                        pointDirection = 0;
                        pointId |= 1; // Skip the next iteration on the same line.
                    }
                }
                /*
                 * At this point we have the index of an ordinate value close to the one we
                 * are looking for. Now fetch the neighbor ordinate value. If the neighbor
                 * is the excluded coordinate, search for the next ordinate value.
                 */
                do {
                    ordinateIndexOfNeighbor += pointDirection;
                    if (ordinateIndexOfNeighbor < 0 || ordinateIndexOfNeighbor >= neighbor.size) {
                        continue nextPoint; // Out of line bounds, check an other point.
                    }
                    ordinateOfNeighbor = neighbor.ordinates[ordinateIndexOfNeighbor];
                } while (neighbor == excludedGridLine && ordinateOfNeighbor == excludedOrdinate);
                /*
                 * At this point we found the ordinate value of a neighbor point to consider in
                 * this iteration step. Accept this point only if it is nearer than the nearest
                 * point considered so far. It is important to use the < operator, not <=, in
                 * order to avoid potentially infinite recursive invocations of this method.
                 */
                final double delta = ordinateOfNeighbor - (isPerpendicular ? gridLineIndex : ordinate);
                neighborDistanceSquared += delta*delta;
                if (!(neighborDistanceSquared < smallestDistanceSquared)) {
                    continue nextPoint;
                }
                /*
                 * Check if 'ordinateOfNeighbor' is in the same column (for horizontal grid
                 * lines) or row (for vertical grid lines) than the ordinate we searched for.
                 * This condition help to avoid geometric impossibilities.
                 */
                if (RESTRICT_TO_ADJACENT) {
                    final int intSearched = isPerpendicular ? gridLineIndex : (int) ordinate;
                    final int intNeighbor = (int) ordinateOfNeighbor;
                    switch (intNeighbor - intSearched) {
                        default: continue nextPoint; // Points too far away
                        case 0:  break;              // Points on the same row or column.
                        case 1:  {
                            // 'ordinateOfNeighbor' may be on the next row/column. Accept only if
                            // the neighbor is on the edge between the current cell and the other
                            // cell, since it could actually belong to any of those two cells.
                            if (intNeighbor != ordinateOfNeighbor) {
                                continue nextPoint;
                            }
                            break;
                        }
                        case -1: {
                            // 'ordinateOfNeighbor' may be on the previous row or column. Accept
                            // only if the ordinate is on the edge between the current cell and
                            // the other cell, since it could actually belong to any of those two
                            // cells. Note: search for perpendicular values are always on the edge.
                            if (!isPerpendicular && ordinate != intSearched) {
                                continue nextPoint;
                            }
                            break;
                        }
                    }
                }
                /*
                 * If the distance is small enough, we may have found the nearest point one.
                 * But before to retain that point, we need check if it has itself a nearer
                 * neighbor. So we invoke this 'jointNearest' method resursively here.
                 */
                final boolean used = neighbor.joinNearest(grid,
                        gridLinesOfNeighbor,
                        isPerpendicular ? gridLines : perpendicular,
                        gridLineIndexOfNeighbor,
                        ordinateIndexOfNeighbor,
                        neighborDistanceSquared);
                /*
                 * The above method call may have removed some intersections points from
                 * the grid. Consequently all ordinate indices computed so far may become
                 * invalid. We need to update the indices (which are instable) using the
                 * ordinate values (which are stable).
                 */
                ordinateIndex = binarySearch(ordinate, ordinateIndex);
                if (ordinateIndex < 0) {
                    return true;  // Our point doesn't exist anymore.
                }
                if (used) {
                    if (!RESTRICT_TO_ADJACENT) {
                        // If the point that we planed to use has been taken by someone else,
                        // then we will need to reanalyse again the same case because an other
                        // point behind the used point may be suitable.
                        pointId--;
                    }
                    continue nextPoint;
                }
                nearest                 = neighbor;
                ordinateOfNearest       = ordinateOfNeighbor;
                ordinateIndexOfNearest  = ordinateIndexOfNeighbor;
                gridLinesOfNearest      = gridLinesOfNeighbor;
                gridLineIndexOfNearest  = gridLineIndexOfNeighbor;
                smallestDistanceSquared = neighborDistanceSquared;
            }
            /*
             * Done inspecting neighbor intersection points. If we found a point nearest
             * than any other point, call back the grid method for creating line segments.
             */
            if (nearest != null) {
                assert gridLines         [gridLineIndex]          == this;
                assert gridLinesOfNearest[gridLineIndexOfNearest] == nearest;
                assert ordinates[ordinateIndex] == ordinate;
                assert nearest.ordinates[nearest.binarySearch(ordinateOfNearest, ordinateIndexOfNearest)] == ordinateOfNearest;
                final int toRemove = grid.createLineSegment(
                        gridLines,          gridLineIndex,          ordinate,
                        gridLinesOfNearest, gridLineIndexOfNearest, ordinateOfNearest);
                /*
                 * We need to remove the intersection points which have been incorporated
                 * in the middle of a polyline. Note that the intersection points at the
                 * extremities of any polylines are still available.
                 */
                if ((toRemove & 1) != 0) {
                    if (removeAt(ordinateIndex)) {
                        gridLines[gridLineIndex] = null;
                    }
                }
                if ((toRemove & 2) != 0) {
                    ordinateIndexOfNearest = nearest.binarySearch(ordinateOfNearest, ordinateIndexOfNearest);
                    if (nearest.removeAt(ordinateIndexOfNearest)) {
                        gridLinesOfNearest[gridLineIndexOfNearest] = null;
                    }
                    if (nearest == this && ordinateIndexOfNearest < ordinateIndex) {
                        ordinateIndex--; // Must be kept up to date.
                    }
                }
                assert grid.checkConsistency();
                if ((toRemove & 1) != 0) {
                    return true;
                }
            }
        } while (excludedGridLine == null && modCount != grid.modCount);
        return false;
    }

    /**
     * Returns the index of the given ordinate value. This method will first check if the given
     * {@code expected} index is actually the right one. If the given value is not found, then
     * this method returns a negative value.
     */
    final int binarySearch(final double ordinate, final int expected) {
        if (expected >= 0 && expected < size && ordinates[expected] == ordinate) {
            return expected;
        }
        return Arrays.binarySearch(ordinates, 0, size, ordinate);
    }

    /**
     * Appends a partial list of intersection points in the given buffer.
     */
    final void toString(final StringBuilder buffer, String separator) {
        for (int i=0; i<size; i++) {
            buffer.append(separator);
            if (i == 4 && size > 10) {
                buffer.append(" … ");
                i = size - 5; // Skip some values.
            }
            buffer.append(ordinates[i]);
            separator = ", ";
        }
    }

    /**
     * Returns a partial list of intersection points, for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(128);
        toString(buffer.append("Intersections["), "");
        return buffer.append(']').toString();
    }
}
