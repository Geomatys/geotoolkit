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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import javax.vecmath.Point3d;

import org.opengis.coverage.grid.SequenceType;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

import static java.lang.Double.isNaN;


/**
 * Creates isolines at the given levels from the grid values provided in an {@link RenderedImage}
 * or {@link PixelIterator}.
 * <p>
 * Uses example:
 * <pre><blockquote>
 * final {@link RenderedImage} ri = ...; // Grid values in a user image.
 * final double[] isolineLevels = new double[] {10, 50, 100}; // Example values.
 * {@link IsolineCreator} isolineContour = new IsolineCreator(ri, isolineLevels);
 * final {@link Map}&lt;{@link Point3d},List&lt;{@link Coordinate}&gt;&gt; isoline = {@link #createIsolines()};
 * </blockquote></pre>
 *
 * Note: By default, this class iterates over all sample values in the given rendered image,
 * which shall have only one band. In order to create isolines for a subarea of a subset of
 * the bands, use the constructor expecting a {@link PixelIterator} instead.
 *
 * @version 3.20
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @author Rémi Marechal (Geomatys)
 * @module pending
 *
 * @since 3.20 (derived from 1.1)
 */
public class IsolineCreator {
    /**
     * Values for which to compute isolines, sorted in ascending order.
     */
    private final double[] levels;

    /**
     * Iterator use for fetching grid values.
     */
    private final PixelIterator iterator;

    /**
     * Area where to compute isolines.
     */
    private final int xMin, yMin, width, height;

    /**
     * The intersections on rows and columns. The length of this array is the number of isoline levels.
     */
    private final IntersectionGrid[] intersections;

    /**
     * Creates a new object which will creates isolines from the data provided by the given image.
     *
     * @param image  Image where caller search isoline.
     * @param levels Values for which to compute isolines.
     */
    public IsolineCreator(final RenderedImage image, final double... levels) {
        this(PixelIteratorFactory.createRowMajorIterator(image), levels);
    }

    /**
     * Creates a new object which will creates isolines from the data provided by the given iterator.
     *
     * @param iterator The iterator over grid values. Iteration must be row-major.
     * @param levels Values for which to compute isolines.
     */
    public IsolineCreator(final PixelIterator iterator, final double... levels) {
        ArgumentChecks.ensureNonNull("iterator", iterator);
        ArgumentChecks.ensureNonNull("levels", levels);
        if (iterator.getNumBands() != 1) {
            throw new IllegalArgumentException("Image must have a single band.");
        }
        if (!SequenceType.LINEAR.equals(iterator.getIterationDirection())) {
            throw new IllegalArgumentException("PixelIterator must be row-major.");
        }
        this.iterator = iterator;
        this.levels = levels.clone();
        Arrays.sort(this.levels);
        final Rectangle area = iterator.getBoundary(true);
        xMin   = area.x;
        yMin   = area.y;
        width  = area.width;
        height = area.height;
        intersections = new IntersectionGrid[levels.length];
    }

    /**
     * Returns the intersection grid for the given level.
     * This method will create the grid when first needed.
     *
     * @param k Index of the desired level.
     */
    private IntersectionGrid gridAt(final int k) {
        IntersectionGrid grid = intersections[k];
        if (grid == null) {
            intersections[k] = grid = new IntersectionGrid(width, height);
        }
        return grid;
    }

    /**
     * Calculate the intersection grids.
     */
    private void calculateIntersectionGrids() {
        // Temporary buffer when calculating intersections on a single row.
        final Intersections[] rows = new Intersections[levels.length];
        /*
         * For each pixel (x,y) having the value z, we will interpolate the intersection
         * with the isolines on the bottom (0) and right (1) edges.
         *
         *            zOnTopRow[x]
         *       ☐─────────☐
         *       │         ↑ (1)
         *       ☐←──(0)───☒
         *    zOnLeft     z(x,y)
         */
        final double[] zOnTopRow = new double[width];
        Arrays.fill(zOnTopRow, Double.NaN);
        for (int y=0; y<height; y++) {
            double zOnLeft = Double.NaN;
            for (int x=0; x<width; x++) {
                /*
                 * zOnLeft and zOnTop are z values in the previous column and in the previous row.
                 * Note that zOnLeft will always be NaN when we are in the first column (i == 0),
                 * and zOnTop will always be NaN when we are in the first row (j == 0).
                 */
                if (!iterator.next()) {
                    throw new NoSuchElementException();
                }
                final double z = iterator.getSampleDouble();
                double zmin = z; // May be non-NaN even if zOnTop or zOnLeft is NaN.
                double zmax = z;
                final double zOnTop = zOnTopRow[x];
                if (zOnTop  < zmin) zmin = zOnTop;
                if (zOnTop  > zmax) zmax = zOnTop;
                if (zOnLeft < zmin) zmin = zOnLeft;
                if (zOnLeft > zmax) zmax = zOnLeft;
                int k = Arrays.binarySearch(levels, zmin);
                if (k < 0) {
                    k = ~k;  // Tild operator, not minus.
                }
                for (; k<levels.length; k++) {
                    final double level = levels[k];
                    if (!(level <= zmax)) { // Use '!' for catching NaN values.
                        break;
                    }
                    /*
                     * Find the intersection point between the edge and the isoline. The dx or dy
                     * variables are outside the [0…1] range when there is no intersection on the
                     * edge (i.e. the intersection is outside the cell area). NaN could mean that
                     * there is intersection everywhere (i.e. the line is vertical or horizontal).
                     */
                    final double dx, dy;
                    if (z != level) {
                        dx = (level - z) / (zOnLeft - z);
                        dy = (level - z) / (zOnTop  - z);
                    } else {
                        /*
                         * If (z == level), then the above formulas produce 0 except if we also
                         * have (zOnLeft == level) or (zOnTop == level), in which case 0/0 give
                         * NaN. In this case, we actually have intersections everywhere in the
                         * [0…1] range (the line segment is fully horizontal or fully vertical).
                         * By convention we will add an intersection point only at 0, since the
                         * point at 1 should have been added by the previous iteration.
                         *
                         * Note that we intentionally ignore zOnLeft and zOnTop, which allow the
                         * algorithm to work even when the above variables are NaN. This is the
                         * case while iterating in the first row and first column.
                         */
                        dx = 0;
                        dy = 0;
                    }
                    /*
                     * If (zOnLeft == level), then dx == 1 in this iteration while it was 0
                     * in the previous iteration since we had (z == level) at that time.
                     * Concequently excluding 1 should avoid adding the same point twice.
                     */
                    if (dx >= 0 && dx < 1) {
                        Intersections row = rows[k];
                        if (row == null) {
                            rows[k] = row = new Intersections(width / 16);
                        }
                        // If (zOnLeft == level), then x-1 should have been added previously.
                        assert (zOnLeft != level) || row.ordinate(row.size()-1) == x-1 : row;
                        row.add(x-dx);
                    }
                    /*
                     * For the vertical grid lines, we do not accept dy == 0 or 1 because
                     * the same points should have been added in the horizontal grid lines.
                     * We verify that with the assertion in the 'else' block.
                     */
                    if (dy > 0 && dy < 1) {
                        gridAt(k).add(x, y-dy);
                    } else {
                        assert (dy != 0 && !isNaN(dy)) || isNaN(zOnTop) || rows[k].binarySearch(x, 0) >= 0 : rows[k];
                        assert (dy != 1 && !isNaN(dy)) || isNaN(zOnTop) || gridAt(k).contains(x, y-1);
                    }
                }
                // Remember the z value for next iterations.
                zOnTopRow[x] = zOnLeft = z;
            }
            // Flush the row content before to move to next row.
            for (int k=0; k<levels.length; k++) {
                final Intersections row = rows[k];
                if (row != null && row.size() != 0) {
                    gridAt(k).setRow(y, row);
                    row.clear();
                }
            }
        }
    }

    /**
     * Creates the polylines.
     *
     * @return The isolines for each level.
     *
     * @todo Current implementation ignore the image (x,y) origin. This should be handled
     *       with an affine transform.
     */
    public CoordinateSequence[][] createPolylines() {
        calculateIntersectionGrids();
        final CoordinateSequence[][] polylines = new CoordinateSequence[intersections.length][];
        for (int i=0; i<intersections.length; i++) {
            final Collection<Polyline> p = intersections[i].createPolylines();
            polylines[i] = p.toArray(new CoordinateSequence[p.size()]);
            // We convert to array in order to allow the garbage collector to collect
            // the map entries, since the key values are not of interest to the user.
        }
        return polylines;
    }

    /**
     * Creates isolines from the grid data specified at construction time.
     *
     * @return The isolines for each level.
     *
     * @deprecated Use {@link #createPolylines()} instead.
     */
    @Deprecated
    public Map<Point3d, List<Coordinate>> createIsolines() {
        final CoordinateSequence[][] polylines = createPolylines();
        final Map<Point3d,List<Coordinate>> cellMapResult = new HashMap<Point3d,List<Coordinate>>();
        for (int i=0; i<polylines.length; i++) {
            int n = 0;
            for (final CoordinateSequence polyline : polylines[i]) {
                final Coordinate[] coords = polyline.toCoordinateArray();
                for (final Coordinate coord : coords) {
                    coord.x += xMin;
                    coord.y += yMin;
                }
                // The (i,j) coordinates below are totally arbitrary; only the z one is significant.
                cellMapResult.put(new Point3d(i, n++, levels[i]), Arrays.asList(coords));
            }
        }
        return cellMapResult;
    }
}
