/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
     * Grid align and simplify geometry.
     * - duplicated points are removed
     * - geometry type is never changed
     *
     * @param createEmpty if true, line and polygon which degenerate to a single point are transformed to empty
     */
    public Geometry alignAndSimplify(Geometry geometry, boolean createEmpty) {
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
            final CoordinateSequence coordinates = filterAndReduce(gf.getCoordinateSequenceFactory(), cdt.getCoordinateSequence(), createEmpty ? 0 : 3);
            if (coordinates.size() < 3) {
                result = gf.createLinearRing();
            } else {
                result = gf.createLinearRing(coordinates);
            }
        } else if (geometry instanceof LineString) {
            final LineString cdt = (LineString) geometry;
            final CoordinateSequence coordinates = filterAndReduce(gf.getCoordinateSequenceFactory(), cdt.getCoordinateSequence(), createEmpty ? 0 : 2);
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
     * Align given coordinate sequence removing duplicates.
     *
     * @param cf
     * @param cs
     * @param minPoints minimum number of points in the sequence
     * @return
     */
    public CoordinateSequence filterAndReduce(CoordinateSequenceFactory cf, CoordinateSequence cs, int minPoints) {

        final int size = cs.size();
        if (size == 0) {
            return cs;
        } else if (size == 1) {
            return cs.copy();
        }

        final List<Coordinate> coords = new ArrayList<>(size);
        Coordinate previous = filter(cs.getCoordinateCopy(0));
        coords.add(previous);

        for (int i = 1; i < size; i++) {
            final Coordinate c = filter(cs.getCoordinateCopy(i));
            if (c.x != previous.x || c.y != previous.y) {
                coords.add(c);
                previous = c;
            }
        }

        while (coords.size() < minPoints) {
            coords.add(previous.copy());
        }

        return cf.create(coords.toArray(new Coordinate[coords.size()]));
    }

    /**
     * Align given coordinate sequence removing duplicates.
     *
     * @param cs
     * @param minPoints minimum number of points in the sequence
     * @return
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

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean isGeometryChanged() {
        return true;
    }
}
