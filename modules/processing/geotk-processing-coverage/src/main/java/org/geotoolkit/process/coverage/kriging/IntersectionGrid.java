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

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;


/**
 * Contains all intersection points in an image for a single level.
 *
 * @version 3.20
 * @author Martin Desruisseaux
 * @module pending
 *
 * @since 3.20
 */
final class IntersectionGrid {
    /**
     * The maximal length (exclusive) that we expect for any segment is the diagonal of a cell.
     * The square value of the diagonal in a 1x1 square is 2, and the smallest IEEE value greater
     * than 2 is 2.0000000000000004.
     */
    static final double MAX_DISTANCE_SQUARED = 2.0000000000000004;

    /**
     * Maximal number of horizontal or vertical grid lines to format in the {@link #toString()}
     * method. If we reach this limit and there is more lines to format, skip some of them. We do
     * that because formating all intersections from a full image could produce a huge string.
     */
    private static final int MAX_LINE_COUNT = 20;

    /**
     * All intersections found on horizontal or vertical grid lines.
     * The elements in this array will be created when first needed.
     */
    private final Intersections[] horizontal, vertical;

    /**
     * Polylines created for this level. The keys are created by the
     * {@link Polyline#key(int, double)} method.
     */
    private final Map<Long,Polyline> polylines;

    /**
     * Temporary variable for tracking content changes.
     */
    transient int modCount;

    /**
     * Workaround for the lack of multi-values return in the Java language.
     *
     * @see #findExclusion(Intersections[], int, double)
     */
    transient double excludedOrdinate;

    /**
     * Creates a new instance for the given level.
     *
     * @param level  The isoline <var>z</var> value.
     * @param width  The image width.
     * @param height The image height.
     */
    IntersectionGrid(final int width, final int height) {
        horizontal = new Intersections[height];
        vertical   = new Intersections[width];
        polylines  = new HashMap<Long,Polyline>();
    }

    /**
     * Stores the intersections of a whole row on the horizontal grid line at location <var>y</var>.
     * Horizontal rows can be written in one single operation because we iterate over the images on
     * a row-major fashion.
     */
    final void setRow(final int y, final Intersections row) {
        assert horizontal[y] == null;
        horizontal[y] = new Intersections(row);
    }

    /**
     * Adds an intersection on the given vertical grid lines.
     *
     * @param x The <var>x</var> ordinate value of the intersection point, which must be on a vertical grid line.
     * @param y The <var>y</var> ordinate value of the intersection point.
     */
    final void add(final int x, final double y) {
        Intersections column = vertical[x];
        if (column == null) {
            vertical[x] = column = new Intersections((horizontal.length - (int) y) / 32);
        }
        column.add(y);
    }

    /**
     * Creates the isolines by joining all intersections in this level.
     * This method shall be invoked only after all intersection points have been added.
     */
    final Collection<Polyline> createPolylines() {
        do {
            modCount = 0;
            createPolylines(horizontal, vertical);
            createPolylines(vertical, horizontal);
        } while (modCount != 0);
        return polylines.values();
    }

    /**
     * Creates the isolines by joining all intersections in the given {@code gridLines},
     * using also the {@code perpendicular} lines when searching for nearest points. Not
     * all {@code perpendicular} lines may be used.
     */
    private void createPolylines(final Intersections[] gridLines, final Intersections[] perpendicular) {
        for (int j=gridLines.length; --j>=0;) {
            final Intersections gridLine = gridLines[j];
            if (gridLine != null) {
                for (int i=gridLine.size(); --i>=0;) {
                    gridLine.joinNearest(this, gridLines, perpendicular, j, i, MAX_DISTANCE_SQUARED);
                    final int size = gridLine.size();
                    if (i > size) {
                        i = size; // Above operation may have removed more than one point.
                    }
                }
            }
        }
    }

    /**
     * Returns the square of the distance between the two given coordinates.
     * This is used for assertions purpose only.
     */
    private static double distanceSquared(final Intersections[] gridLines1, final int gridLineIndex1, final double ordinate1,
                                          final Intersections[] gridLines2, final int gridLineIndex2, final double ordinate2)
    {
        final double dx, dy;
        if (gridLines1 == gridLines2) {
            dx = gridLineIndex1 - gridLineIndex2;
            dy = ordinate1      - ordinate2;
        } else {
            dx = gridLineIndex1 - ordinate2;
            dy = gridLineIndex2 - ordinate1;
        }
        return dx*dx + dy*dy;
    }

    /**
     * Creates a line segment from the given starting and ending points. This method is called
     * back by {@link Intersections}.
     *
     * @return The grid lines that the caller shall remove after this method call:
     *         <ul>
     *           <li>0 if none</li>
     *           <li>1 for grid line 1 point</li>
     *           <li>2 for grid line 2 point</li>
     *           <li>3 for both</li>
     *         </ul>
     */
    final int createLineSegment(final Intersections[] gridLines1, int gridLineIndex1, final double ordinate1,
                                final Intersections[] gridLines2, int gridLineIndex2, final double ordinate2)
    {
        final double dsq;
        assert (dsq = distanceSquared(gridLines1, gridLineIndex1, ordinate1,
                                      gridLines2, gridLineIndex2, ordinate2)) < MAX_DISTANCE_SQUARED : dsq;
        // Applies the sign convention documented in the Polyline class.
        if (gridLines1 == horizontal) gridLineIndex1 = ~gridLineIndex1;
        if (gridLines2 == horizontal) gridLineIndex2 = ~gridLineIndex2;
        Long key1 = Polyline.key(gridLineIndex1, ordinate1);
        Long key2 = Polyline.key(gridLineIndex2, ordinate2);
        final Polyline p1 = polylines.remove(key1);
        final Polyline p2 = polylines.remove(key2);
        final int toRemove;
        if ((p1 == null) == (p2 == null)) {
            final Polyline merged;
            if (p1 == null) {
                /*
                 * Creating a new line segment where no segment existed prior this method
                 * call. Do not remove any intersection point, since we will need to join
                 * the two extremities with other line segments when we will find them.
                 */
                merged = new Polyline(gridLineIndex1, ordinate1, gridLineIndex2, ordinate2);
                toRemove = 0;
            } else {
                /*
                 * Merging two existing polylines as a single polyline. We will need to
                 * remove both intersection points, since no other segments can ba attached
                 * to those points (as they will be in the middle of the merged polylines).
                 */
                merged = p1.merge(key1, key2, p2);
                key1 = merged.key(true);
                key2 = merged.key(false);
                toRemove = 3;
            }
            Polyline p;
            p = polylines.put(key1, merged); assert (p == null || p == p1 || p == p2) : p;
            p = polylines.put(key2, merged); assert (p == null || p == p1 || p == p2) : p;
            // If we performed a merge, then the second polyline instance should be discarted.
            assert (p1 == p2) || !polylines.values().contains(merged == p1 ? p2 : p1);
        } else {
            /*
             * Adding a single point to an existing polyline. We will need to remove only
             * the intersection point for the extremity of the existing polyline where we
             * added a new point. The opposite polyline extremity will still available.
             */
            final Long added;
            final Polyline expand;
            if (p1 != null) {
                expand = p1;
                expand.append(key1, gridLineIndex2, ordinate2);
                added = key2;
                toRemove = 1;
            } else {
                expand = p2;
                expand.append(key2, gridLineIndex1, ordinate1);
                added = key1;
                toRemove = 2;
            }
            assert added.equals(expand.key(false)) : expand;  // Last point shall be the one we added.
            assert polylines.get(expand.key(true)) == expand; // First point shall still be referenced.
            final Polyline p = polylines.put(added, expand);
            assert (p == null) : p;
        }
        modCount++;
        return toRemove;
    }

    /**
     * Finds the intersection point to exclude, if any. If this method returns a non-null value,
     * then the ordinate value of the excluded point is stored in {@link #excludedOrdinate}.
     */
    final Intersections findExclusion(final Intersections[] gridLines, int gridLineIndex, final double ordinate) {
        if (gridLines == horizontal) {
            gridLineIndex = ~gridLineIndex;
        }
        final Long key = Polyline.key(gridLineIndex, ordinate);
        final Polyline polyline = polylines.get(key);
        if (polyline == null) {
            return null;
        }
        final int i = polyline.startsWith(key) ? polyline.size()-1 : 0; // Opposite extremity.
        excludedOrdinate = polyline.ordinate(i);
        gridLineIndex = polyline.gridLine(i);
        return (gridLineIndex >= 0) ? vertical[gridLineIndex] : horizontal[~gridLineIndex];
    }

    /**
     * Tests the validity of this grid line, for assertion purposes only.
     * This method tests that every {@link #polylines} key are associated
     * to the correct {@link Polyline} instance, and that every polylines
     * have its two extremities in the key set. Then, if the polyline is
     * not closed, this method verifies that the two extremities are still
     * available as intersection points.
     * <p>
     * This method does not invoke {@link Polyline#isValid()}, since this
     * check is rather performed when the polylines are modified.
     */
    final boolean isValid() {
        for (final Map.Entry<Long, Polyline> entry : polylines.entrySet()) {
            final Polyline polyline = entry.getValue();
            final Long     key      = entry.getKey();
            boolean startsWith = false;
            while (!key.equals(polyline.key(startsWith))) {
                if ((startsWith = !startsWith) == false) {
                    throw new AssertionError(polyline); // Invalid key.
                }
            }
            if (polylines.get(polyline.key(!startsWith)) != polyline) {
                throw new AssertionError(polyline); // Missing key.
            }
            /*
             * At this point, we verified that the polylines map is correct regarding this
             * polyline instance. Now check the grid of intersection points: intersections
             * shall stil exist for the polyline extremities (if not closed), but shall not
             * exist anymore for any point between the extremities.
             */
            final boolean isClosed = polyline.isClosed();
            final int last = polyline.size() - 1;
            for (int i=0; i<=last; i++) {
                final int j = polyline.gridLine(i);
                final Intersections gridLine = (j >= 0) ? vertical[j] : horizontal[~j];
                final boolean exists = (gridLine != null) && gridLine.indexOf(polyline.ordinate(i), 0) >= 0;
                if (exists != ((i == 0 || i == last) ? !isClosed : false)) {
                    throw new AssertionError("exists=" + exists + " for i=" + i + " in " + polyline);
                }
            }
        }
        return true;
    }

    /**
     * Returns a partial list of intersection points, for debugging purpose.
     */
    @Override
    public String toString() {
        final String lineSeparator = System.getProperty("line.separator", "\n");
        final StringBuilder buffer = new StringBuilder(256);
        boolean isVertical = false;
        do { // Executed exactly twice.
            final String section;
            final char gridLines;
            final char ordinates;
            final Intersections[] intersections;
            if (isVertical) {
                intersections = vertical;
                section       = "Vertical";
                gridLines     = 'x';
                ordinates     = 'y';
            } else {
                intersections = horizontal;
                section       = "Horizontal";
                gridLines     = 'y';
                ordinates     = 'x';
            }
            buffer.append(section).append(lineSeparator);
            for (int n=0,i=0; i<intersections.length; i++) {
                final Intersections inter = intersections[i];
                if (inter != null) {
                    inter.toString(buffer.append("  ").append(gridLines).append('=').append(i),
                            "; " + ordinates + '=');
                    buffer.append(lineSeparator);
                    if (++n == MAX_LINE_COUNT/2) {
                        for (int j=intersections.length; --j>i;) {
                            if (intersections[j] != null) {
                                if (++n == MAX_LINE_COUNT+1) {
                                    buffer.append("  …").append(lineSeparator);
                                    i = j; // Skip some lines, including this 'j' line.
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } while ((isVertical = !isVertical) == true);
        return buffer.toString();
    }
}
