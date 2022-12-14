/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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
package org.geotoolkit.geometry.jts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.locationtech.jts.geom.Coordinate;
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
 * Set of Utility function and methods for mapping
 *
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public final class JTSMapping {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    private JTSMapping() {
    }

    /**
     * Force geometry type. If the given geometry is not of given class it will
     * be adapted.
     */
    public static <T extends Geometry> T convertType(final Geometry geom, final Class<T> targetClass) {
        if (geom == null) {
            return null;
        }

        if (targetClass.isInstance(geom)) {
            return (T) geom;
        }

        Geometry result;
        if (targetClass == Point.class) {
            result = convertToPoint(geom);
        } else if (targetClass == MultiPoint.class) {
            result = convertToMultiPoint(geom);
        } else if (targetClass == LineString.class) {
            result = convertToLineString(geom);
        } else if (targetClass == MultiLineString.class) {
            result = convertToMultiLineString(geom);
        } else if (targetClass == Polygon.class) {
            result = convertToPolygon(geom);
        } else if (targetClass == MultiPolygon.class) {
            result = convertToMultiPolygon(geom);
        } else if (targetClass == GeometryCollection.class) {
            result = convertToGeometryCollection(geom);
        } else {
            result = null;
        }

        if (result != null) {
            //copy srid and user data
            result.setSRID(geom.getSRID());
            result.setUserData(geom.getUserData());
        }

        return targetClass.cast(result);
    }

    // Convert to Point --------------------------------------------------------
    private static Point convertToPoint(final Geometry geom) {
        if (geom instanceof Point) {
            return convertToPoint((Point) geom);
        } else if (geom instanceof MultiPoint) {
            return convertToPoint((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return convertToPoint((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return convertToPoint((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return convertToPoint((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return convertToPoint((MultiPolygon) geom);
        }
        return null;
    }

    private static Point convertToPoint(final Point pt) {
        return pt;
    }

    private static Point convertToPoint(final MultiPoint pt) {
        return pt.getCentroid();
    }

    private static Point convertToPoint(final LineString pt) {
        return pt.getCentroid();
    }

    private static Point convertToPoint(final MultiLineString pt) {
        return pt.getCentroid();
    }

    private static Point convertToPoint(final Polygon pt) {
        return pt.getCentroid();
    }

    private static Point convertToPoint(final MultiPolygon pt) {
        return pt.getCentroid();
    }

    // Convert to MultiPoint ---------------------------------------------------
    private static MultiPoint convertToMultiPoint(final Geometry geom) {
        if (geom instanceof Point) {
            return convertToMultiPoint((Point) geom);
        } else if (geom instanceof MultiPoint) {
            return convertToMultiPoint((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return convertToMultiPoint((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return convertToMultiPoint((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return convertToMultiPoint((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return convertToMultiPoint((MultiPolygon) geom);
        }
        return null;
    }

    private static MultiPoint convertToMultiPoint(final Point pt) {
        return GF.createMultiPoint(new Point[]{pt});
    }

    private static MultiPoint convertToMultiPoint(final MultiPoint pt) {
        return pt;
    }

    private static MultiPoint convertToMultiPoint(final LineString pt) {
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(final MultiLineString pt) {
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(final Polygon pt) {
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(final MultiPolygon pt) {
        return GF.createMultiPoint(pt.getCoordinates());
    }

    // Convert to LineString ---------------------------------------------------
    private static LineString convertToLineString(final Geometry geom) {
        if (geom instanceof Point) {
            return convertToLineString((Point) geom);
        } else if (geom instanceof MultiPoint) {
            return convertToLineString((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return convertToLineString((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return convertToLineString((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return convertToLineString((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return convertToLineString((MultiPolygon) geom);
        }
        return null;
    }

    private static LineString convertToLineString(final Point pt) {
        return GF.createLineString(new Coordinate[]{pt.getCoordinate(), pt.getCoordinate()});
    }

    private static LineString convertToLineString(final MultiPoint pt) {
        final Coordinate[] coords = pt.getCoordinates();
        if (coords.length == 1) {
            return GF.createLineString(new Coordinate[]{coords[0], coords[0]});
        } else {
            return GF.createLineString(coords);
        }
    }

    private static LineString convertToLineString(final LineString pt) {
        return pt;
    }

    private static LineString convertToLineString(final MultiLineString pt) {
        return GF.createLineString(pt.getCoordinates());
    }

    private static LineString convertToLineString(final Polygon pt) {
        return GF.createLineString(pt.getCoordinates());
    }

    private static LineString convertToLineString(final MultiPolygon pt) {
        return GF.createLineString(pt.getCoordinates());
    }

    // Convert to MultiLineString ----------------------------------------------
    private static MultiLineString convertToMultiLineString(final Geometry geom) {
        if (geom instanceof Point) {
            return convertToMultiLineString((Point) geom);
        } else if (geom instanceof MultiPoint) {
            return convertToMultiLineString((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return convertToMultiLineString((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return convertToMultiLineString((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return convertToMultiLineString((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return convertToMultiLineString((MultiPolygon) geom);
        }
        return null;
    }

    private static MultiLineString convertToMultiLineString(final Point pt) {
        return convertToMultiLineString(convertToLineString(pt));
    }

    private static MultiLineString convertToMultiLineString(final MultiPoint pt) {
        return convertToMultiLineString(convertToLineString(pt));
    }

    private static MultiLineString convertToMultiLineString(final LineString pt) {
        return GF.createMultiLineString(new LineString[]{pt});
    }

    private static MultiLineString convertToMultiLineString(final MultiLineString pt) {
        return pt;
    }

    private static MultiLineString convertToMultiLineString(final Polygon pt) {
        final int nbHoles = pt.getNumInteriorRing();
        final List<LineString> strings = new ArrayList<>(nbHoles+1);
        strings.add(pt.getExteriorRing());
        for (int i = 0; i < nbHoles; i++) {
            strings.add(pt.getInteriorRingN(i));
        }
        return GF.createMultiLineString(strings.toArray(new LineString[nbHoles+1]));
    }

    private static MultiLineString convertToMultiLineString(final MultiPolygon pt) {
        final int n = pt.getNumGeometries();
        final LineString[] geoms = new LineString[n];
        for (int i = 0; i < n; i++) {
            geoms[i] = convertToLineString(pt.getGeometryN(i));
        }
        return GF.createMultiLineString(geoms);
    }

    // Convert to Polygon ------------------------------------------------------
    private static Polygon convertToPolygon(final Geometry geom) {
        if (geom instanceof Point) {
            return convertToPolygon((Point) geom);
        } else if (geom instanceof MultiPoint) {
            return convertToPolygon((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return convertToPolygon((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return convertToPolygon((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return convertToPolygon((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return convertToPolygon((MultiPolygon) geom);
        }
        return null;
    }

    private static Polygon convertToPolygon(final Point pt) {
        LinearRing ring = GF.createLinearRing(new Coordinate[]{pt.getCoordinate(), pt.getCoordinate(), pt.getCoordinate(), pt.getCoordinate()});
        return GF.createPolygon(ring, new LinearRing[0]);
    }

    private static Polygon convertToPolygon(final MultiPoint pt) {
        return convertToPolygon(convertToLineString(pt));
    }

    /**
     * If linestring is a valid ring returns a closed polygon otherwise return
     * an empty polygon.
     */
    private static Polygon convertToPolygon(final LineString pt) {
        if (pt.isEmpty() || pt.isRing()) {
            return GF.createPolygon(GF.createLinearRing(pt.getCoordinates()), new LinearRing[0]);
        } else {
            Logger.getLogger("org.geotoolkit.geometry").log(Level.FINE, "LineString {0} is not a valid linear ring to build a polygon,", pt);
            return GF.createPolygon();
        }
    }

    private static Polygon convertToPolygon(final MultiLineString pt) {
        return convertToPolygon(convertToLineString(pt));
    }

    private static Polygon convertToPolygon(final Polygon pt) {
        return pt;
    }

    private static Polygon convertToPolygon(final MultiPolygon pt) {
        final int nbGeom = pt.getNumGeometries();
        if (nbGeom == 0) {
            return GF.createPolygon();
        } else if (nbGeom == 1) {
            return (Polygon) pt.getGeometryN(0);
        } else {
            return convertToPolygon(pt.convexHull());
        }
    }

    // Convert to MultiPolygon -------------------------------------------------
    private static MultiPolygon convertToMultiPolygon(final Geometry geom) {
        if (geom instanceof Point) {
            return convertToMultiPolygon((Point) geom);
        } else if (geom instanceof MultiPoint) {
            return convertToMultiPolygon((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return convertToMultiPolygon((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return convertToMultiPolygon((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return convertToMultiPolygon((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return convertToMultiPolygon((MultiPolygon) geom);
        }
        return null;
    }

    private static MultiPolygon convertToMultiPolygon(final Point pt) {
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final MultiPoint pt) {
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final LineString pt) {
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final MultiLineString pt) {
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final Polygon pt) {
        return GF.createMultiPolygon(new Polygon[]{pt});
    }

    private static MultiPolygon convertToMultiPolygon(final MultiPolygon pt) {
        return pt;
    }

    private static GeometryCollection convertToGeometryCollection(final Geometry geom) {
        if (geom instanceof GeometryCollection) {
            return (GeometryCollection) geom;
        }

        if (geom instanceof Point) {
            return convertToMultiPoint(geom);
        } else if (geom instanceof LineString) {
            return convertToMultiLineString(geom);
        } else if (geom instanceof Polygon) {
            return convertToMultiPolygon(geom);
        }
        return null;
    }

    /**
     * Filter given geometry to a Point or MultiPoint.
     * If the given geometry already matches it is returned unchanged.
     * GeometryCollection are filtered and only matching geometries are preserved.
     * Any other geometry types are skipped.
     * Returned geometry is null only if given geometry is also null otherwise
     * the geometry is never null but can be an empty geometry.
     *
     * @param geometry can be null
     * @return geometry composed of ponctual elements only.
     */
    public static Geometry preservePonctuals(Geometry geometry) {
        if (geometry == null) return null;

        final Object userData = geometry.getUserData();
        final String geometryType = geometry.getGeometryType();

        if (Geometry.TYPENAME_POINT.equals(geometryType)
         || Geometry.TYPENAME_MULTIPOINT.equals(geometryType)) {
            //already valid type
        } else if (Geometry.TYPENAME_GEOMETRYCOLLECTION.equals(geometryType)) {
            final GeometryCollection gc = (GeometryCollection) geometry;
            final List<Point> parts = new ArrayList<>();
            for (int i = 0, in = gc.getNumGeometries(); i < in; i++) {
                Geometry cdt = preservePonctuals(gc.getGeometryN(i));
                if (cdt instanceof Point) {
                    parts.add((Point) cdt);
                } else if (cdt instanceof MultiPoint) {
                    MultiPoint mp = (MultiPoint) cdt;
                    for (int k = 0, kn = mp.getNumGeometries(); k < kn; k++) {
                        parts.add((Point) mp.getGeometryN(k));
                    }
                }
            }
            int size = parts.size();
            if (size == 0) {
                geometry = geometry.getFactory().createPoint(); //empty
            } else if (size == 1) {
                geometry = parts.get(0);
            } else {
                geometry = geometry.getFactory().createMultiPoint(GeometryFactory.toPointArray(parts));
            }
            geometry.setUserData(userData);
        } else {
            geometry = geometry.getFactory().createPoint(); //empty
            geometry.setUserData(userData);
        }

        return geometry;
    }

    /**
     * Filter given geometry to a LineString, LinearRing or MultiLineString.
     * If the given geometry already matches it is returned unchanged.
     * GeometryCollection are filtered and only matching geometries are preserved.
     * Any other geometry types are skipped.
     * Returned geometry is null only if given geometry is also null otherwise
     * the geometry is never null but can be an empty geometry.
     *
     * @param geometry can be null
     * @return geometry composed of linear elements only.
     */
    public static Geometry preserveLinears(Geometry geometry) {
        if (geometry == null) return null;

        final Object userData = geometry.getUserData();
        final String geometryType = geometry.getGeometryType();

        if (Geometry.TYPENAME_LINESTRING.equals(geometryType)
         || Geometry.TYPENAME_LINEARRING.equals(geometryType)
         || Geometry.TYPENAME_MULTILINESTRING.equals(geometryType)) {
            //already valid type
        } else if (Geometry.TYPENAME_GEOMETRYCOLLECTION.equals(geometryType)) {
            final GeometryCollection gc = (GeometryCollection) geometry;
            final List<LineString> parts = new ArrayList<>();
            for (int i = 0, in = gc.getNumGeometries(); i < in; i++) {
                Geometry cdt = preserveLinears(gc.getGeometryN(i));
                if (cdt instanceof LineString) {
                    parts.add((LineString) cdt);
                } else if (cdt instanceof MultiLineString) {
                    MultiLineString mp = (MultiLineString) cdt;
                    for (int k = 0, kn = mp.getNumGeometries(); k < kn; k++) {
                        parts.add((LineString) mp.getGeometryN(k));
                    }
                }
            }
            int size = parts.size();
            if (size == 0) {
                geometry = geometry.getFactory().createLineString(); //empty
            } else if (size == 1) {
                geometry = parts.get(0);
            } else {
                geometry = geometry.getFactory().createMultiLineString(GeometryFactory.toLineStringArray(parts));
            }
            geometry.setUserData(userData);
        } else {
            geometry = geometry.getFactory().createLineString(); //empty
            geometry.setUserData(userData);
        }

        return geometry;
    }

    /**
     * Filter given geometry to a Polygon or MultiPolygon.
     * If the given geometry already matches it is returned unchanged.
     * GeometryCollection are filtered and only matching geometries are preserved.
     * Any other geometry types are skipped.
     * Returned geometry is null only if given geometry is also null otherwise
     * the geometry is never null but can be an empty geometry.
     *
     * @param geometry can be null
     * @return geometry composed of polygon elements only.
     */
    public static Geometry preservePolygonals(Geometry geometry) {
        if (geometry == null) return null;

        final Object userData = geometry.getUserData();
        final String geometryType = geometry.getGeometryType();

        if (Geometry.TYPENAME_POLYGON.equals(geometryType)
         || Geometry.TYPENAME_MULTIPOLYGON.equals(geometryType)) {
            //already valid type
        } else if (Geometry.TYPENAME_GEOMETRYCOLLECTION.equals(geometryType)) {
            final GeometryCollection gc = (GeometryCollection) geometry;
            final List<Polygon> parts = new ArrayList<>();
            for (int i = 0, in = gc.getNumGeometries(); i < in; i++) {
                Geometry cdt = preservePolygonals(gc.getGeometryN(i));
                if (cdt instanceof Polygon) {
                    parts.add((Polygon) cdt);
                } else if (cdt instanceof MultiPolygon) {
                    MultiPolygon mp = (MultiPolygon) cdt;
                    for (int k = 0, kn = mp.getNumGeometries(); k < kn; k++) {
                        parts.add((Polygon) mp.getGeometryN(k));
                    }
                }
            }
            int size = parts.size();
            if (size == 0) {
                geometry = geometry.getFactory().createPolygon(); //empty
            } else if (size == 1) {
                geometry = parts.get(0);
            } else {
                geometry = geometry.getFactory().createMultiPolygon(GeometryFactory.toPolygonArray(parts));
            }
            geometry.setUserData(userData);
        } else {
            geometry = geometry.getFactory().createPolygon(); //empty
            geometry.setUserData(userData);
        }

        return geometry;
    }
}
