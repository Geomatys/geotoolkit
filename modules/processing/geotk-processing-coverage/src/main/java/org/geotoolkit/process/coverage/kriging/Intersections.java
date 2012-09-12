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
     * The result of the last call to the {@link #nearest} method, stored as a field because
     * the Java language does not allow multiple return values. We put this temporary field
     * in this class rather than some "result" separated class because its value depends on
     * the {@code Intersections} instance retained after we compared many alternatives.
     */
    private transient int indexOfNearest;

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
     * Returns the last value in this row. It is caller responsibility to ensure that this
     * instance is not empty.
     */
    final double last() {
        return ordinates[size-1];
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
     * Returns the square of the distance between the given ordinate value and the nearest
     * neighbor on this grid line.  The index of the nearest ordinate value will be stored
     * in the {@link #indexOfNearest} field.
     *
     * @param  ordinate The ordinate value for which to find the distance to nearest neighbor.
     * @return The square value of the distance to the nearest neighbor.
     */
    private double nearest(final double ordinate) {
        double distance = 0; // Value to return if we find an exact match.
        int i = Arrays.binarySearch(ordinates, 0, size, ordinate);
        if (i < 0) {
            i = ~i;
            if (i == size) {
                distance = ordinate - ordinates[--i];
            } else {
                distance = ordinates[i] - ordinate; // Distance with the point after.
                if (i != 0) {
                    final double b = ordinate - ordinates[i-1]; // Distance with the point before.
                    if (b < distance) {
                        distance = b;
                        i--;
                    }
                }
            }
        }
        indexOfNearest = i;
        assert (distance >= 0) : distance;
        return distance * distance;
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
            double          ordinateOfNearest      = 0.;
            int             gridLineIndexOfNearest = 0;

            int offset = +1;
            do { // Will be executed exactly twice: once for +1 and once for -1 offset.
                /*
                 * First, check the neighbor points on the same grid line. For horizontal grid
                 * lines, they are points on the right and on the left sides of 'ordinateIndex'.
                 */
                int neighborIndex = ordinateIndex + offset;
                if (neighborIndex >= 0 && neighborIndex < size) {
                    final double candidate = ordinates[neighborIndex];
                    if (excludedGridLine != this || excludedOrdinate != candidate && isAligned((int) ordinate, candidate)) {
                        double d = candidate - ordinate; // Sign doesn't matter here.
                        if ((d *= d) < smallestDistanceSquared) {
                            /*
                             * Found a point which is potentially the nearest one. But before to
                             * retain that point as a candidate, check if it has a nearer neighbor.
                             */
                            final boolean used = joinNearest(grid, gridLines, perpendicular, gridLineIndex, neighborIndex, d);
                            ordinateIndex = indexOf(ordinate, ordinateIndex); // Must be kept up to date.
                            if (ordinateIndex < 0) {
                                return true;  // Our point doesn't exist anymore.
                            }
                            if (!used) {
                                // Do not use neighborIndex since it may not be valid anymore.
                                smallestDistanceSquared = d;
                                nearest                 = this;
                                gridLinesOfNearest      = gridLines;
                                indexOfNearest          = ordinateIndex + offset;
                                ordinateOfNearest       = ordinates[indexOfNearest];
                                gridLineIndexOfNearest  = gridLineIndex;
                            }
                        }
                    }
                }
                /*
                 * Next, check the neighbor grid lines. For horizontal grid lines, they are
                 * the lines on the bottom and the top of this 'Intersections' instance.
                 */
                neighborIndex = gridLineIndex + offset;
                if (neighborIndex >= 0 && neighborIndex < gridLines.length) {
                    final Intersections neighbor = gridLines[neighborIndex];
                    if (neighbor != null) {
                        /*
                         * The +1 below is the distance between adjacent grid lines, which is
                         * one side of the triangle from which to compute the hypothenuse.
                         */
                        final double d = neighbor.nearest(ordinate) + 1;
                        if (d < smallestDistanceSquared) {
                            /*
                             * Found a point which is potentially the nearest one. But before to retain
                             * that point, check if that point has a nearer neighbor.
                             */
                            final double candidate = neighbor.ordinates[neighbor.indexOfNearest];
                            if (neighbor != excludedGridLine || candidate != excludedOrdinate && isAligned((int) ordinate, candidate)) {
                                final boolean used = neighbor.joinNearest(grid, gridLines, perpendicular, neighborIndex, neighbor.indexOfNearest, d);
                                ordinateIndex = indexOf(ordinate, ordinateIndex); // Must be kept up to date.
                                if (ordinateIndex < 0) {
                                    return true;  // Our point doesn't exist anymore.
                                }
                                if (!used) {
                                    // Warning: From this point, value of above 'neighbor.indexOfNearest' may be invalid.
                                    smallestDistanceSquared        = d;
                                    nearest                = neighbor;
                                    gridLinesOfNearest     = gridLines;
                                    ordinateOfNearest      = candidate;
                                    gridLineIndexOfNearest = neighborIndex;
                                }
                            }
                        }
                    }
                }
            } while ((offset = -offset) < 0);
            /*
             * At this point, we finished to consider the neighbor points on parallel grid lines.
             * Now check on perpendicular grid lines.
             */
            final int lower = (int) ordinate;
            int i = lower;
            if (i != ordinate && (i+1) != perpendicular.length) {
                i++;
            }
            do { // Will be executed exactly 1 or 2 time, no more.
                final Intersections p = perpendicular[i];
                if (p != null) {
                    double d = ordinate - i; // Sign doesn't matter here.
                    d = d*d + p.nearest(gridLineIndex);
                    if (d < smallestDistanceSquared) {
                        final double candidate = p.ordinates[p.indexOfNearest];
                        if (p != excludedGridLine || candidate != excludedOrdinate && isAligned(gridLineIndex, candidate)) {
                            final boolean used = p.joinNearest(grid, perpendicular, gridLines, i, p.indexOfNearest, d);
                            ordinateIndex = indexOf(ordinate, ordinateIndex); // Must be kept up to date.
                            if (ordinateIndex < 0) {
                                return true;  // Our point doesn't exist anymore.
                            }
                            if (!used) {
                                // Warning: From this point, value of above 'neighbor.indexOfNearest' may be invalid.
                                smallestDistanceSquared        = d;
                                nearest                = p;
                                gridLinesOfNearest     = perpendicular;
                                ordinateOfNearest      = candidate;
                                gridLineIndexOfNearest = i;
                            }
                        }
                    }
                }
            } while (--i == lower);
            /*
             * Done inspecting neighbor intersection points. If we found a point nearest
             * than any other point, call back the grid method for creating line segments.
             */
            assert gridLines[gridLineIndex] == this;
            if (nearest != null) {
                int ordinateIndexOfNearest = nearest.indexOf(ordinateOfNearest, nearest.indexOfNearest);
                if (ordinateIndexOfNearest >= 0) {
                    assert gridLinesOfNearest[gridLineIndexOfNearest] == nearest;
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
                        ordinateIndexOfNearest = nearest.indexOf(ordinateOfNearest, ordinateIndexOfNearest);
                        if (nearest.removeAt(ordinateIndexOfNearest)) {
                            gridLinesOfNearest[gridLineIndexOfNearest] = null;
                        }
                        if (nearest == this && ordinateIndexOfNearest < ordinateIndex) {
                            ordinateIndex--; // Must be kept up to date.
                        }
                    }
                    assert grid.isValid();
                    if ((toRemove & 1) != 0) {
                        return true;
                    }
                }
            }
        } while (excludedGridLine == null && modCount != grid.modCount);
        return false;
    }

    /**
     * Checks if the given {@code candidate} ordinate value is in the same column (for horizontal
     * grid lines) or row (for vertical grid lines) than the given {@code master} ordinate. If the
     * given candidate is on a edge between two cells (i.e. its coordinate value is an integer),
     * then this method considers that the candidate could belong to any of the two cells on both
     * side of the edge.
     */
    private static boolean isAligned(final int master, final double candidate) {
        final int r = (int) candidate;
        switch (r - master) {
            case 0:  return true;
            case 1:  return r == candidate;
            default: return false;
        }
    }

    /**
     * Returns the index of the given ordinate value. This method will first check if the given
     * {@code expected} index is actually the right one. If the given value is not found, then
     * this method returns a negative value.
     */
    final int indexOf(final double ordinate, final int expected) {
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
