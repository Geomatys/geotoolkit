/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020-2022, Geomatys
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
package org.geotoolkit.geometry.jts.coordinatesequence;

import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Used to align geometry coordinates on a regular grid.
 * An instance of this class is thread-safe and concurrent.
 *
 * Coordinates of the geometry will be rounded to the nearest grid column
 * and row intersection.
 * And example of usage is integer rounding in mapbox vector tiles.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GridAlignedFilter implements CoordinateSequenceFilter {

    private final double offsetx;
    private final double offsety;
    private final double stepx;
    private final double stepy;

    private boolean removeColinear = true;
    private boolean createEmpty = false;
    private boolean removeSpikes = false;

    /**
     * Grid aligned filter constructor.
     * X and Y origin are the coordinate of any point of the grid where
     * a column and a row intersect.
     * X and Y spacing define the regular distance between each column and row.
     *
     * @param originx alignement grid X origin
     * @param originy alignement grid Y origin
     * @param stepx alignement grid column spacing
     * @param stepy  alignement grid row spacing
     */
    public GridAlignedFilter(double originx, double originy, double stepx, double stepy) {
        this.offsetx = originx;
        this.offsety = originy;
        this.stepx = stepx;
        this.stepy = stepy;
    }

    /**
     * Set to true to remove colinear points on lines.
     * @param removeColinear true to remove colinear.
     */
    public void setRemoveColinear(boolean removeColinear) {
        this.removeColinear = removeColinear;
    }

    /**
     * @see #setRemoveColinear(boolean)
     */
    public boolean isRemoveColinear() {
        return removeColinear;
    }

    /**
     * If set to true, line and polygon which degenerate to a single point are transformed to empty geometries.
     *
     * @param createEmpty true to create empty geometries when geometries degenerate to points
     */
    public void setCreateEmpty(boolean createEmpty) {
        this.createEmpty = createEmpty;
    }

    /**
     * @see #setCreateEmpty(boolean)
     */
    public boolean isCreateEmpty() {
        return createEmpty;
    }

    /**
     * If set to true, line spkies will be removed.
     * A spike is point with an offset of 1 on X or Y which goes back to it's original point
     * just after.
     *
     * Example :
     *    3
     *    +
     *    |
     * +--+--+
     * 1 2/4 5
     *
     * will be simplified in :
     *
     * +--+--+
     * 1  2  3
     *
     * @param createEmpty true to create empty geometries when geometries degenerate to points
     */
    public void setRemoveSpikes(boolean removeSpikes) {
        this.removeSpikes = removeSpikes;
    }

    /**
     * @see #setRemoveSpikes(boolean)
     */
    public boolean isRemoveSpikes() {
        return removeSpikes;
    }

    /**
     * Grid align and simplify geometry.
     * - duplicated points are removed
     * - geometry type is never changed
     */
    public Geometry alignAndSimplify(Geometry geometry) {
        return alignAndSimplify(geometry, createEmpty);
    }

    /**
     * Grid align and simplify geometry.
     * - duplicated points are removed
     * - geometry type is never changed
     *
     * @param createEmpty if true, line and polygon which degenerate to a single point are transformed to empty
     */
    private Geometry alignAndSimplify(Geometry geometry, boolean createEmpty) {
        if (geometry == null || geometry.isEmpty()) {
            return geometry;
        }

        final GeometryFactory gf = geometry.getFactory();
        final Object userData = geometry.getUserData();

        Geometry result;
        if (geometry instanceof Point) {
            final Point cdt = (Point) geometry;
            final Coordinate coord = cdt.getCoordinate().copy();
            filter(coord);
            result = gf.createPoint(coord);
        } else if (geometry instanceof LinearRing) {
            final LinearRing cdt = (LinearRing) geometry;
            CoordinateSequence coordinates = filterAndReduce(gf.getCoordinateSequenceFactory(), cdt.getCoordinateSequence(), createEmpty ? 0 : 3, true);
            if (removeSpikes) coordinates = removeSpikes(gf.getCoordinateSequenceFactory(), coordinates, createEmpty ? 0 : 3, true);
            if (coordinates.size() < 3) {
                result = gf.createLinearRing();
            } else {
                result = gf.createLinearRing(coordinates);
            }
        } else if (geometry instanceof LineString) {
            final LineString cdt = (LineString) geometry;
            CoordinateSequence coordinates = filterAndReduce(gf.getCoordinateSequenceFactory(), cdt.getCoordinateSequence(), createEmpty ? 0 : 2, false);
            if (removeSpikes) coordinates = removeSpikes(gf.getCoordinateSequenceFactory(), coordinates, createEmpty ? 0 : 2, false);
            if (coordinates.size() < 2) {
                result = gf.createLineString();
            } else {
                result = gf.createLineString(coordinates);
            }
        } else if (geometry instanceof Polygon) {
            final Polygon cdt = (Polygon) geometry;
            final LinearRing exteriorRing = (LinearRing) alignAndSimplify(cdt.getExteriorRing(), createEmpty);
            if (createEmpty && exteriorRing.isEmpty()) {
                result = gf.createPolygon(exteriorRing);
            } else {
                final int numInteriorRing = cdt.getNumInteriorRing();
                if (numInteriorRing == 0) {
                    result = gf.createPolygon(exteriorRing);
                } else {
                    final List<LinearRing> holes = new ArrayList<>(numInteriorRing);
                    for (int i = 0; i < numInteriorRing; i++) {
                        final LinearRing interiorRing = (LinearRing) alignAndSimplify(cdt.getInteriorRingN(i), true);
                        if (!interiorRing.isEmpty()) {
                            holes.add(interiorRing);
                        }
                    }
                    result = gf.createPolygon(exteriorRing, holes.toArray(new LinearRing[holes.size()]));
                }
            }
            //we use the buffer operation to clean holes who touch borders and polygons which touch at a single point.
            result = result.buffer(0);

        } else if (geometry instanceof MultiPoint) {
            final MultiPoint cdt = (MultiPoint) geometry;
            final Coordinate[] coordinates = filterAndReduce(cdt.getCoordinates(), 1);
            result = gf.createMultiPointFromCoords(coordinates);

        } else if (geometry instanceof MultiLineString) {
            final MultiLineString cdt = (MultiLineString) geometry;
            final int nb = cdt.getNumGeometries();
            final List<LineString> geoms = new ArrayList<>(nb);
            for (int i = 0; i < nb; i++) {
                //ensure we have at least one geometry not empty if it is requested
                LineString ls = (LineString) alignAndSimplify((LineString) cdt.getGeometryN(i), createEmpty ? true : !geoms.isEmpty());
                if (!ls.isEmpty()) {
                    geoms.add(ls);
                }
            }
            result = gf.createMultiLineString(geoms.toArray(new LineString[geoms.size()]));
        } else if (geometry instanceof MultiPolygon) {
            final MultiPolygon cdt = (MultiPolygon) geometry;
            final int nb = cdt.getNumGeometries();
            final List<Geometry> geoms = new ArrayList<>(nb);
            boolean allPolygons = true;
            for (int i = 0; i < nb; i++) {
                //ensure we have at least one geometry not empty if it is requested
                Geometry ls = alignAndSimplify((Polygon) cdt.getGeometryN(i), createEmpty ? true : !geoms.isEmpty());
                if (!ls.isEmpty()) {
                    if (ls instanceof GeometryCollection) {
                        final GeometryCollection gc = (GeometryCollection) ls;
                        for (int k = 0, n = gc.getNumGeometries(); k < n; k++) {
                            final Geometry cd = gc.getGeometryN(k);
                            geoms.add(ls);
                            allPolygons &= (ls instanceof Polygon);
                        }
                    } else {
                        geoms.add(ls);
                        allPolygons &= (ls instanceof Polygon);
                    }
                }
            }
            if (allPolygons) {
                result = gf.createMultiPolygon(geoms.toArray(new Polygon[geoms.size()]));
            } else {
                result = gf.createGeometryCollection(geoms.toArray(new Geometry[geoms.size()]));
            }
        } else if (geometry instanceof GeometryCollection) {
            final GeometryCollection cdt = (GeometryCollection) geometry;
            final int nb = cdt.getNumGeometries();
            final List<Geometry> geoms = new ArrayList<>(nb);
            for (int i = 0; i < nb; i++) {
                //ensure we have at least one geometry not empty if it is requested
                Geometry ls = alignAndSimplify(cdt.getGeometryN(i), createEmpty ? true : !geoms.isEmpty());
                if (!ls.isEmpty()) {
                    geoms.add(ls);
                }
            }
            result = gf.createGeometryCollection(geoms.toArray(new Geometry[geoms.size()]));
        } else {
            throw new IllegalArgumentException("Unexpected geometry type " + geometry.getClass().getName());
        }

        result.setUserData(userData);
        return result;
    }

    @Override
    public void filter(CoordinateSequence seq, int i) {
        final Coordinate coordinate = seq.getCoordinate(i);
        final double x = Math.rint((coordinate.x - offsetx) / stepx);
        final double y = Math.rint((coordinate.y - offsety) / stepy);
        seq.setOrdinate(i, 0, stepx * x + offsetx);
        seq.setOrdinate(i, 1, stepy * y + offsety);
    }

    /**
     * Align given coordinate.
     * Coordinate will be modified.
     *
     * @param coordinate not null
     * @return same coordinate as input
     */
    public Coordinate filter(Coordinate coordinate) {
        final double x = Math.rint((coordinate.x - offsetx) / stepx);
        final double y = Math.rint((coordinate.y - offsety) / stepy);
        coordinate.x = stepx * x + offsetx;
        coordinate.y = stepy * y + offsety;
        return coordinate;
    }

    /**
     * Align given coordinate sequence removing duplicates and colinear points.
     *
     * @param minPoints minimum number of points in the sequence
     * @param isRing true if first/last points must be identical
     * @return resulting coordinate sequence
     */
    public CoordinateSequence filterAndReduce(CoordinateSequenceFactory cf, CoordinateSequence cs, int minPoints, boolean isRing) {

        final int size = cs.size();
        if (size == 0) {
            return cs;
        } else if (size == 1) {
            return cs.copy();
        }

        final List<Coordinate> coords = new ArrayList<>(size);
        //transform the first point
        Coordinate previous = filter(cs.getCoordinateCopy(0));
        coords.add(previous);

        double lastSlopeX = Double.POSITIVE_INFINITY;
        double lastSlopeY = Double.POSITIVE_INFINITY;

        for (int i = 1; i < size; i++) {
            final Coordinate c = filter(cs.getCoordinateCopy(i));
            if (c.x != previous.x || c.y != previous.y) {

                if (removeColinear) {
                    if (lastSlopeX == Double.POSITIVE_INFINITY) {
                        //first segment
                        lastSlopeX = (c.x - previous.x);
                        lastSlopeY = (c.y - previous.y);
                        coords.add(c);
                        previous = c;
                    } else {
                        /*
                        Simple colinear test is not enough.
                        Remove previous point if points are in the same direction
                        and new point is after the previous one.
                        */
                        final double slopeX = (c.x - previous.x);
                        final double slopeY = (c.y - previous.y);
                        //final double slopeRatio = slopeX / slopeY;
                        //final double lastSlopeRatio = lastSlopeX / lastSlopeY;
                        // if slopeRatio == lastSlopeRatio
                        // simplified in : slopeX * lastSlopeY == lastSlopeX * slopeY
                        if ( slopeX * lastSlopeY == lastSlopeX * slopeY
                          && Math.signum(slopeX) == Math.signum(lastSlopeX)
                          && Math.signum(slopeY) == Math.signum(lastSlopeY)) {
                            //previous point in on the segment
                            previous.setCoordinate(c);
                        } else {
                            coords.add(c);
                            previous = c;
                            lastSlopeX = slopeX;
                            lastSlopeY = slopeY;
                        }
                    }
                } else {
                    coords.add(c);
                    previous = c;
                }
            }
        }

        //ensure we have the minimum number of points
        while (coords.size() < minPoints) {
            coords.add(previous.copy());
        }

        //ensure we have a closed ring
        if (isRing && minPoints > 0) {
            Coordinate first = coords.get(0);
            if (!previous.equals(first)) {
                coords.add(first.copy());
            }
        }

        return cf.create(coords.toArray(new Coordinate[0]));
    }

    /**
     * Align given coordinate sequence removing duplicates.
     * This method do not remove colinear points.
     *
     * @param minPoints minimum number of points in the sequence
     */
    public Coordinate[] filterAndReduce(Coordinate[] cs, int minPoints) {

        final int size = cs.length;
        if (size == 0) {
            return cs;
        } else if (size == 1) {
            return new Coordinate[]{cs[0].copy()};
        }

        final List<Coordinate> coords = new ArrayList<>(size);
        Coordinate previous = filter(cs[0].copy());
        coords.add(previous);

        for (int i = 1; i < size; i++) {
            final Coordinate c = filter(cs[i].copy());
            if (c.x != previous.x || c.y != previous.y) {
                coords.add(c);
                previous = c;
            }
        }

        while (coords.size() < minPoints) {
            coords.add(previous.copy());
        }

        return coords.toArray(new Coordinate[coords.size()]);
    }

    /**
     * Remove spikes from given coordinate sequence.
     *
     * @param minPoints minimum number of points in the sequence
     * @param isRing true if first/last points must be identical
     * @return resulting coordinate sequence
     */
    public CoordinateSequence removeSpikes(CoordinateSequenceFactory cf, CoordinateSequence cs, int minPoints, boolean isRing) {
        final int size = cs.size();
        if (size == 0) {
            return cs;
        } else if (size == 1) {
            return cs.copy();
        }

        final List<Coordinate> coords = new ArrayList<>(size);
        Coordinate previous2 = cs.getCoordinateCopy(0);
        Coordinate previous1 = cs.getCoordinateCopy(1);
        coords.add(previous2);
        coords.add(previous1);

        for (int i = 2; i < size; i++) {
            final Coordinate c = cs.getCoordinateCopy(i);
            if (previous2.x == c.x && previous2.y == c.y
               && Math.abs(previous1.x - previous2.x) < (stepx * 1.9)
               && Math.abs(previous1.y - previous2.y) < (stepy * 1.9)) {
                previous1.setCoordinate(c);
            } else {
                coords.add(c);
                previous2 = previous1;
                previous1 = c;

            }
        }

        //ensure we have the minimum number of points
        while (coords.size() < minPoints) {
            coords.add(previous1.copy());
        }

        //ensure we have a closed ring
        if (isRing && minPoints > 0) {
            Coordinate first = coords.get(0);
            if (!previous1.equals(first)) {
                coords.add(first.copy());
            }
        }

        return cf.create(coords.toArray(new Coordinate[0]));
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isGeometryChanged() {
        return true;
    }
}
