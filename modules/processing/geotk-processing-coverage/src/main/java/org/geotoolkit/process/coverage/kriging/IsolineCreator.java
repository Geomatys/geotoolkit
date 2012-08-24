/*
 * Map and oceanographical data visualisation
 * Copyright (C) 1999 Pêches et Océans Canada
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

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.*;
import javax.vecmath.Point3d;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.coverage.grid.SequenceType;


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
 * @author Martin Desruisseaux
 * @author Howard Freeland
 * @author Johann Sorel (adaptation isoligne et mise a jour sur geotoolkit)
 * @author Rémi Marechal (Geomatys).
 * @module pending
 *
 * @since 3.20 (derived from 1.1)
 */
public class IsolineCreator {
    /**
     * Number of corners in a pixel.
     */
    private static final int NUM_CORNERS = 4;

    /**
     * Mask for the {@code isDone} local variable inside the {@link #createIsolines()} method.
     */
    private static final int ALL_DONE_MASK = (1 << NUM_CORNERS) - 1;

    /**
     * Values for which to compute isolines, sorted in ascending order.
     */
    private final double[] levels;

    /**
     * Iterator use for fetching grid values.
     */
    private final PixelIterator iterator;

    /**
     * Area where to compute isolines, <strong>inclusive</strong> (even the maximal values).
     */
    private final int xMin, yMin, xMax, yMax;

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
        xMin = area.x;
        yMin = area.y;
        xMax = xMin + area.width - 1;
        yMax = yMin + area.height - 1;
    }

    /**
     * Returns {@code true} if the given value is inside the given bounds.
     * It is not necessary for {@code z0} to be lower than {@code z1}.
     * If any value is {@link Double#NaN}, then this method returns {@code false}.
     */
    private static boolean isBetween(final double z0, final double z1, final double value) {
        return (z0 > z1) ? (value >= z1 && value <= z0) : (value >= z0 && value <= z1);
    }

    /**
     * Creates isolines from the grid data specified at construction time.
     *
     * @return The isolines for each level.
     */
    public Map<Point3d, List<Coordinate>> createIsolines() {
        final Map<Point3d,List<Coordinate>> cellMapResult = new HashMap<Point3d,List<Coordinate>>();
        for (final Map.Entry<Point3d, Polyline> toCopy : createPolylines().entrySet()) {
            cellMapResult.put(toCopy.getKey(), toCopy.getValue().toCoordinates());
        }
        return cellMapResult;
    }

    /**
     * Creates isolines from the grid data specified at construction time.
     *
     * @return The isolines for each level.
     */
    private Map<Point3d, Polyline> createPolylines() {
        final Map<Point3d,Polyline> cellMapResult = new HashMap<Point3d,Polyline>();
        final double z[] = new double[NUM_CORNERS];
        /*
         * The four corners of the pixel to process will be numbered from 0 to 3 as below:
         *
         *     [0] ← [3]
         *      ↓     ↑
         *     [1] ➝ [2]
         *
         * The (x,y) pixel coordinate can be calculated as below,
         * where k is the corner index from 0 to 3 inclusive:
         *
         *     ox = k >>> 1;        // Zero for corners [0] and [1], one for [2] and [3]
         *     oy = (k & 1) ^ ox;   // Zero for corners [0] and [3], one for [1] and [2]
         *     x0 = i + ox
         *     y0 = j + oy
         *
         * To compute (x,y) for k+1:
         *
         *     x1 = i + oy
         *     y1 = j + (ox ^ 1)
         */
        for (int i=xMin; i<xMax; i++) {
            iterator.moveTo(i, yMin, 0);
            z[1] = iterator.getSampleDouble();
            iterator.next();
            z[2] = iterator.getSampleDouble();
            for (int j=yMin; j<yMax; j++) {
                z[0] = z[1];
                z[3] = z[2];
                iterator.moveTo(i, j+1, 0);
                z[1] = iterator.getSampleDouble();
                iterator.next();
                z[2] = iterator.getSampleDouble();
                /*
                 * Get the minimum and maximum z values among the 4 corners. The isoline segments
                 * to create will be for levels between those extremums. Example: for zmin = 8.76
                 * and zmax=11.35, we may build segments for levels = 9, 10 and 11.
                 */
                double zmin = Double.POSITIVE_INFINITY;
                double zmax = Double.NEGATIVE_INFINITY;
                for (final double value : z) {
                    if (value < zmin) zmin = value;
                    if (value > zmax) zmax = value;
                }
                /*
                 * At this point, 'zmin' and 'zmax' must be considered final.
                 *
                 * The variables below could be unitialized if we were programming in C/C++,
                 * because all those variables are assigned a value together when 'd2min' takes a
                 * finite value. We have to initialize them for preventing the Java compiler to
                 * report an error, but do that outside the loop for avoiding re-initialization
                 * at every iteration step.
                 */
                int    ci0=0, ci1=0;               // Corner indices.
                double px0=0, py0=0, px1=0, py1=0; // Point (x,y) coordinates.
                /*
                 * Find the highest level lower or equals to 'zmin', and iterates until 'zmax'.
                 */
                int levelIndex = Arrays.binarySearch(levels, zmin);
                if (levelIndex < 0) {
                    levelIndex = ~levelIndex; // Tild operator, not minus.
                }
                while (levelIndex < levels.length) {
                    final double levelValue = levels[levelIndex++];
                    if (!(levelValue <= zmax)) { // Use '!' for catching NaN values.
                        break;
                    }
                    /*
                     * Following loop searches the shortest (px0,py0) - (px1,py1) line segment which
                     * is part of the isoline, and set 'pi0' and 'pi1' to the index (from 0 to 3) of
                     * the corners before P0 and before P1 respectively.
                     */
                    int isDone = 0; // A mask of 4 bits.
                    do {
                        /*
                         * For each pixel corner, find the intersection point between a pixel side
                         * and the isoline. We may execute this loop more than once for the same cell,
                         * so the side already examined in a previous pass will have to be skipped.
                         */
                        double d2min = Double.POSITIVE_INFINITY;
                        for (int ci=0; ci<NUM_CORNERS; ci++) {
                            if ((isDone & (1 << ci)) != 0) {
                                continue; // Skip points that are already done.
                            }
                            final int ni = (ci+1) % NUM_CORNERS; // Index of the next corner.
                            double z0 = z[ci];
                            double z1 = z[ni];
                            if (!isBetween(z0, z1, levelValue)) {
                                isDone |= (1 << ci);
                                continue; // Skip sides that do not intersect the isoline.
                            }
                            /*
                             * Compute the coordinates (in image coordinate system) of the two corners
                             * which are the end points of the pixel side (ci - ni). See the comment
                             * at the begining of this method for more information about the bits
                             * manipulation performed here.
                             */
                            int ox = ci >>> 1;
                            int oy = (ci & 1) ^ ox;
                            int x0 = i + ox;
                            int y0 = j + oy;
                            int x1 = i + oy;
                            int y1 = j + (ox ^ 1);
                            if (z0 == z1) {
                                // Found a horizontal or vertical segment. We can't use the
                                // general block below since it would produce a division by
                                // zero. Store directly the segment coordinates.
                                if (1 < d2min) {
                                    d2min = 1;
                                    ci0 = ci; px0 = x0; py0 = y0;
                                    ci1 = ni; py1 = y1; px1 = x1;
                                }
                                continue;
                            }
                            /*
                             * Compute (tx0,ty0) as the intersection point between the isoline
                             * and the pixel side (ci to ni). Next, search the intersection on
                             * an other side as (tx1,ty1) and keep the shortest line segment.
                             */
                            double slope = (levelValue-z0) / (z1 - z0);
                            final double tx0 =  x0 + slope * (x1 - x0);
                            final double ty0 =  y0 + slope * (y1 - y0);
                            for (int oi=0; oi<NUM_CORNERS; oi++) {
                                if (oi == ci || (isDone & (1 << oi)) != 0) {
                                    continue; // Skip points that are already done.
                                }
                                z0 = z[oi];
                                z1 = z[(oi + 1) % NUM_CORNERS]; // z at the next corner.
                                if (!isBetween(z0, z1, levelValue)) {
                                    isDone |= (1 << oi);
                                    continue; // Skip sides that do not intersect the isoline.
                                }
                                // Compute the coordinates of the pixel side in image CS
                                // (same calculation than in the above enclosing loop).
                                // Then calculate the intersection in the same way than above.
                                ox = oi >>> 1;  oy = (oi & 1) ^ ox;
                                x0 = i + ox;    y0 = j + oy;
                                x1 = i + oy;    y1 = j + (ox ^ 1);
                                slope = (levelValue - z0) / (z1 - z0);
                                final double tx1 = x0 + slope * (x1 - x0);
                                final double ty1 = y0 + slope * (y1 - y0);
                                double d;
                                d = (d = tx0-tx1)*d + (d = ty0-ty1)*d;
                                if (d < d2min) {
                                    d2min = d;
                                    ci0 = ci; px0 = tx0; py0 = ty0;
                                    ci1 = oi; px1 = tx1; py1 = ty1;
                                }
                            }
                        }
                        /*
                         * If after completion of iteration over every pixel sides we didn't
                         * found any isoline segment, then stop this loop. The enclosing
                         * 'while' loop will start the search for the next isoline level.
                         */
                        if (d2min == Double.POSITIVE_INFINITY) {
                            break;
                        }
                        /*
                         * At this point we have the coordinates of an isoline segment.
                         * Add this coordinates to the polyline in process of being built.
                         */
                        isDone |= (1 << ci0);
                        isDone |= (1 << ci1);
                        final Polyline toMerge = new Polyline(px0, py0, px1, py1);
                        /*
                         * Verify if intersection points (px0, py0) and/or (px1, py1) have been
                         * found previously. If yes, append the (px0,py0)-(px1, py1) line segment
                         * to the existing polyline. If not, store a new polyline.
                         */
                        final Point3d pt0 = new Point3d((float) px0, (float) py0, levelValue);
                        final Point3d pt1 = new Point3d((float) px1, (float) py1, levelValue);
                        final Polyline line0 = cellMapResult.remove(pt0);
                        final Polyline line1 = cellMapResult.remove(pt1);
                        if (line0 == null) {
                            if (line1 == null) {
                                cellMapResult.put(pt0, toMerge);
                                cellMapResult.put(pt1, toMerge);
                            } else {
                                Polyline merged = line1.merge(toMerge);
                                merged = assertd(merged, line1);
                                cellMapResult.put(pt0, merged);
                            }
                        } else {
                            Polyline merged = line0.merge(toMerge);
                            if (line1 == null) {
                                merged = assertd(merged, line0);
                                cellMapResult.put(pt1, merged);
                            } else {
                                if (merged == null) {
                                    merged = line1.merge(toMerge);
                                    merged = assertd(merged, line1);
                                }
                                if (line0 != line1) {
                                    final Polyline other = (merged != line0) ? line0 : line1;
                                    merged = merged.merge(other);
                                    merged = assertd(merged, other);
                                    /*
                                     * Since we merged 'line0' with 'line1' and we are going to keep
                                     * only one of them (namely 'merged'), we need to update all the
                                     * references to 'other'.
                                     */
                                    final Iterator<Map.Entry<Point3d,Polyline>> it = cellMapResult.entrySet().iterator();
                                    while (it.hasNext()) {
                                        final Map.Entry<Point3d,Polyline> entry = it.next();
                                        final Polyline line = entry.getValue();
                                        if (line == other) {
                                            entry.setValue(merged);
                                        }
                                    }
                                } else {
                                    /*
                                     * Si on vient de refermer une cle (I0==I1), alors il ne
                                     * reste plus de référence vers celle-ci. On en créera une
                                     * avec un point bidon, choisie de façon à être innacessible
                                     * par le reste de cette méthode.
                                     */
                                    pt0.add(pt1);
                                    pt0.scale(0.5);
                                    pt0.z = levelValue;
                                    cellMapResult.put(pt0, line0);
                                }
                            }
                        }
                    } while (isDone != ALL_DONE_MASK);
                }
            }
        }
        return cellMapResult;
    }

    private static Polyline assertd(final Polyline polyline, final Polyline original) {
        if (polyline == null) {
            System.out.println("ERROR " + original);
            return original;
        }
        return polyline;
    }
}
