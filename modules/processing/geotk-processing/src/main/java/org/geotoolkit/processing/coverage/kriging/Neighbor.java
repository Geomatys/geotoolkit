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


/**
 * Information about a neighbor intersection point.
 *
 * @version 3.20
 * @author Martin Desruisseaux
 * @module
 *
 * @since 3.20
 */
final class Neighbor {
    /**
     * The array of grid lines, either {@link IntersectionGrid#horizontal} or
     * {@link IntersectionGrid#vertical}.
     */
    Intersections[] gridLines;

    /**
     * Index in the {@link #gridLines} array of the grid line on which a neighbor intersection
     * point has been found.
     */
    int gridLineIndex;

    /**
     * The ordinate value of the neighbor intersection point.
     */
    double ordinate;

    /**
     * Index in the {@link Intersections} instance where the {@link #ordinate} value has been
     * found. The following relationship must hold:
     *
     * <pre>gridLines[gridLineIndex].ordinate(ordinateIndex) == ordinate;</pre>
     *
     * However this {@code ordinateIndex} is not completely reliable; it may become invalidate
     * if intersection points are removed from the grid line. Only the {@link #ordinate} value
     * is reliable. Consequently the {@code ordinateIndex} validity must be verified before to
     * be used, and recomputed if needed.
     */
    int ordinateIndex;

    /**
     * Square of the distance between this neighbor point and the point proceeded by the caller.
     */
    double distanceSquared;

    /**
     * Create a new {@code Neighbor} structure.
     */
    Neighbor() {
    }

    /**
     * Sets the {@link #gridLines} and {@link #gridLineIndex} fields to the given values,
     * then return the grid line at that index.
     *
     * @return The grid line, or {@code null} if none.
     */
    final Intersections setGridLine(final Intersections[] gridLines, final int gridLineIndex) {
        if (gridLineIndex < 0 || gridLineIndex >= gridLines.length) {
            return null;
        }
        this.gridLines     = gridLines;
        this.gridLineIndex = gridLineIndex;
        return gridLines[gridLineIndex];
    }

    /**
     * Verifies the {@link #ordinateIndex} condition.
     */
    final boolean isValid() {
        return gridLines[gridLineIndex].ordinate(ordinateIndex) == ordinate;
    }

    /**
     * Fixes the {@link #ordinateIndex} value if necessary. This method needs to be invoked
     * if some {@link Intersections#removeAt(int)} method call may have occurred between the
     * index definition and its use.
     */
    final boolean fixOrdinateIndex() {
        final Intersections gridLine = gridLines[gridLineIndex];
        if (gridLine != null) {
            ordinateIndex = gridLine.binarySearch(ordinate, ordinateIndex);
            return ordinateIndex >= 0;
        }
        return false;
    }

    /**
     * Returns a string representation of this neighbor point, for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(20);
        buffer.append("Neighbor[(").append(gridLineIndex).append(", ").append(ordinate).append(')');
        final Intersections gridLine = setGridLine(gridLines, gridLineIndex);
        if (gridLine != null) {
            buffer.append(", index=");
            if (ordinateIndex >= 0 && ordinateIndex < gridLine.size()
                    && gridLine.ordinate(ordinateIndex) == ordinate)
            {
                buffer.append(ordinateIndex);
            } else {
                buffer.append("INVALID");
            }
        }
        buffer.append(", distance=").append(Math.sqrt(distanceSquared));
        return buffer.append(']').toString();
    }
}
