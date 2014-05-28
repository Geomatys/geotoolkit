/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.utils;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.geojson.binding.*;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.lang.Static;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.geotoolkit.data.geojson.binding.GeoJSONGeometry.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * @author Quentin Boileau (Geomatys)
 */
public final class GeometryUtils extends Static {

    private static final Logger LOGGER = Logging.getLogger(GeometryUtils.class);
    private static final GeometryFactory GF = new GeometryFactory();

    /**
     * Convert GeoJSONGeometry into JTS Geometry with included CRS
     * @param jsonGeometry
     * @param crs
     * @return JTS Geometry
     */
    public static Geometry toJTS(GeoJSONGeometry jsonGeometry, CoordinateReferenceSystem crs) {

        if (jsonGeometry != null) {
            if (crs == null) {
                throw new IllegalArgumentException("Null Coordinate Reference System.");
            }

            if (jsonGeometry instanceof GeoJSONPoint) {
                return toJTS((GeoJSONPoint) jsonGeometry, crs);
            } else if (jsonGeometry instanceof GeoJSONLineString) {
                return toJTS((GeoJSONLineString) jsonGeometry, crs);
            } else if (jsonGeometry instanceof GeoJSONPolygon) {
                return toJTS((GeoJSONPolygon) jsonGeometry, crs);
            } else if (jsonGeometry instanceof GeoJSONMultiPoint) {
                return toJTS((GeoJSONMultiPoint) jsonGeometry, crs);
            } else if (jsonGeometry instanceof GeoJSONMultiLineString) {
                return toJTS((GeoJSONMultiLineString) jsonGeometry, crs);
            } else if (jsonGeometry instanceof GeoJSONMultiPolygon) {
                return toJTS((GeoJSONMultiPolygon) jsonGeometry, crs);
            } else if (jsonGeometry instanceof GeoJSONGeometryCollection) {
                return toJTS((GeoJSONGeometryCollection) jsonGeometry, crs);
            } else {
                throw new IllegalArgumentException("Unsupported geometry type : " + jsonGeometry);
            }
        }
        return null;
    }

    private static Coordinate toCoordinate(double[] coord) {
        if (coord.length == 2) {
            return new Coordinate(coord[0], coord[1]);
        } else if (coord.length == 3) {
            return new Coordinate(coord[0], coord[1], coord[2]);
        } else {
            throw new IllegalArgumentException("Coordinates not valid : " + Arrays.toString(coord));
        }
    }

    private static CoordinateSequence toCoordinateSequence(double[][] coords) {

        Coordinate[] coordinates = new Coordinate[coords.length];
        if (coords.length > 0) {
            for (int i = 0; i < coords.length; i++) {
                coordinates[i] = toCoordinate(coords[i]);
            }
        }
        return new CoordinateArraySequence(coordinates);
    }

    private static LinearRing toLinearRing(double[][] coords) {
        return GF.createLinearRing(toCoordinateSequence(coords));
    }

    private static Polygon toPolygon(double[][][] coords, CoordinateReferenceSystem crs) {

        LinearRing exterior = toLinearRing(coords[0]);
        LinearRing[] holes = new LinearRing[coords.length-1];
        if (coords.length > 1) {
            for (int i = 0; i < holes.length; i++) {
                holes[i] = toLinearRing(coords[i+1]);
            }
        }

        Polygon polygon = GF.createPolygon(exterior, holes);
        JTS.setCRS(polygon, crs);
        return polygon;
    }

    private static Point toJTS(GeoJSONPoint jsonPoint, CoordinateReferenceSystem crs) {
        double[] coord = jsonPoint.getCoordinates();

        final Point pt = GF.createPoint(toCoordinate(coord));
        JTS.setCRS(pt, crs);

        return pt;
    }

    private static LineString toJTS(GeoJSONLineString jsonLS, CoordinateReferenceSystem crs) {
        double[][] coord = jsonLS.getCoordinates();

        LineString line = GF.createLineString(toCoordinateSequence(coord));
        JTS.setCRS(line, crs);
        return line;
    }

    private static Geometry toJTS(GeoJSONPolygon jsonPolygon, CoordinateReferenceSystem crs) {
        double[][][] coord = jsonPolygon.getCoordinates();

        if (coord.length <= 0) {
            return GF.buildGeometry(Collections.EMPTY_LIST);
        }

        return toPolygon(coord, crs);
    }

    private static MultiPoint toJTS(GeoJSONMultiPoint jsonMP, CoordinateReferenceSystem crs) {
        double[][] coords = jsonMP.getCoordinates();

        Coordinate[] coordinates = new Coordinate[coords.length];
        if (coords.length > 0) {
            for (int i = 0; i < coords.length; i++) {
                coordinates[i] = toCoordinate(coords[i]);
            }
        }

        MultiPoint mpt = GF.createMultiPoint(coordinates);
        JTS.setCRS(mpt, crs);
        return mpt;
    }

    private static MultiLineString toJTS(GeoJSONMultiLineString jsonMLS, CoordinateReferenceSystem crs) {
        double[][][] coords = jsonMLS.getCoordinates();

        LineString[] lines = new LineString[coords.length];
        if (coords.length > 0) {
            for (int i = 0; i < coords.length; i++) {
                lines[i] = GF.createLineString(toCoordinateSequence(coords[i]));
            }
        }

        MultiLineString mls = GF.createMultiLineString(lines);
        JTS.setCRS(mls, crs);
        return mls;
    }

    private static MultiPolygon toJTS(GeoJSONMultiPolygon jsonMP, CoordinateReferenceSystem crs) {
        double[][][][] coords = jsonMP.getCoordinates();

        Polygon[] polygons = new Polygon[coords.length];
        if (coords.length > 0) {
            for (int i = 0; i < coords.length; i++) {
                polygons[i] = toPolygon(coords[i], crs);
            }
        }

        MultiPolygon mp = GF.createMultiPolygon(polygons);
        JTS.setCRS(mp, crs);
        return mp;
    }

    private static GeometryCollection toJTS(GeoJSONGeometryCollection jsonGC, CoordinateReferenceSystem crs) {
        if (jsonGC.getGeometries() != null) {

            int size = jsonGC.getGeometries().size();
            Geometry[] geometries = new Geometry[size];

            for (int i = 0; i < size; i++) {
                geometries[i] = toJTS(jsonGC.getGeometries().get(i), crs);
            }

            GeometryCollection gc = GF.createGeometryCollection(geometries);
            JTS.setCRS(gc, crs);
            return gc;
        }
        return null;
    }


    /**
     * Convert JTS geometry into a GeoJSONGeometry.
     * @param geom JTS Geometry
     * @return GeoJSONGeometry
     */
    public static GeoJSONGeometry toGeoJSONGeometry(Geometry geom) {
        if (geom == null) {
            throw new IllegalArgumentException("Null Geometry.");
        }

        if (geom instanceof Point) {
            return toGeoJSONGeometry((Point) geom);
        } else if (geom instanceof LineString) {
            return toGeoJSONGeometry((LineString) geom);
        } else if (geom instanceof Polygon) {
            return toGeoJSONGeometry((Polygon) geom);
        } else if (geom instanceof MultiPoint) {
            return toGeoJSONGeometry((MultiPoint) geom);
        } else if (geom instanceof MultiLineString) {
            return toGeoJSONGeometry((MultiLineString) geom);
        } else if (geom instanceof MultiPolygon) {
            return toGeoJSONGeometry((MultiPolygon) geom);
        } else if (geom instanceof GeometryCollection) {
            return toGeoJSONGeometry((GeometryCollection) geom);
        } else {
            throw new IllegalArgumentException("Unsupported geometry type : " + geom);
        }
    }

    private static double[] toArray(Coordinate coord) {
        double x = coord.getOrdinate(0);
        double y = coord.getOrdinate(1);
        double z = coord.getOrdinate(2);

        if (Double.isNaN(z)) {
            return new double[] {x, y};
        } else {
            return new double[] {x, y, z};
        }
    }

    private static double[][] toArray(Coordinate[] coords) {
        double[][] result = new double[coords.length][];

        for (int i = 0; i < coords.length; i++) {
            result[i] = toArray(coords[i]);
        }
        return result;
    }

    private static double[][] toArray(CoordinateSequence coords) {
        return toArray(coords.toCoordinateArray());
    }

    private static double[][][] toArray(CoordinateSequence[] coords) {
        double[][][] result = new double[coords.length][][];

        for (int i = 0; i < coords.length; i++) {
            result[i] = toArray(coords[i]);
        }
        return result;
    }

    private static double[][][][] toArray(CoordinateSequence[][] coords) {
        double[][][][] result = new double[coords.length][][][];

        for (int i = 0; i < coords.length; i++) {
            result[i] = toArray(coords[i]);
        }
        return result;
    }

    private static GeoJSONPoint toGeoJSONGeometry(Point pt) {
        GeoJSONPoint jsonPt = new GeoJSONPoint();
        jsonPt.setCoordinates(toArray(pt.getCoordinate()));
        return jsonPt;
    }

    private static GeoJSONLineString toGeoJSONGeometry(LineString line) {
        GeoJSONLineString jsonln = new GeoJSONLineString();
        jsonln.setCoordinates(toArray(line.getCoordinateSequence()));
        return jsonln;
    }

    private static GeoJSONPolygon toGeoJSONGeometry(Polygon polygon) {
        GeoJSONPolygon jsonpoly = new GeoJSONPolygon();
        CoordinateSequence[] coords = getCoordinateSequencesFromPolygon(polygon);
        jsonpoly.setCoordinates(toArray(coords));
        return jsonpoly;
    }

    private static CoordinateSequence[] getCoordinateSequencesFromPolygon(Polygon polygon) {
        int totalRings = polygon.getNumInteriorRing() + 1;
        CoordinateSequence[] coords = new CoordinateSequence[totalRings];
        coords[0] = polygon.getExteriorRing().getCoordinateSequence();

        if (totalRings > 1) {
            for (int i = 0; i < totalRings - 1; i++) {
                coords[i+1] = polygon.getInteriorRingN(i).getCoordinateSequence();
            }
        }
        return coords;
    }

    private static GeoJSONMultiPoint toGeoJSONGeometry(MultiPoint mpt) {
        GeoJSONMultiPoint jsonMpt = new GeoJSONMultiPoint();
        jsonMpt.setCoordinates(toArray(mpt.getCoordinates()));
        return jsonMpt;
    }

    private static GeoJSONMultiLineString toGeoJSONGeometry(MultiLineString mln) {
        GeoJSONMultiLineString jsonMln = new GeoJSONMultiLineString();
        int totalRings = mln.getNumGeometries();
        CoordinateSequence[] coords = new CoordinateSequence[totalRings];
        for (int i = 0; i < totalRings; i++) {
            coords[i] = ((LineString)mln.getGeometryN(i)).getCoordinateSequence();
        }
        jsonMln.setCoordinates(toArray(coords));
        return jsonMln;
    }

    private static GeoJSONMultiPolygon toGeoJSONGeometry(MultiPolygon multiPolygon) {
        GeoJSONMultiPolygon jsonMPoly = new GeoJSONMultiPolygon();
        int totalPoly = multiPolygon.getNumGeometries();

        CoordinateSequence[][] coords = new CoordinateSequence[totalPoly][];
        for (int i = 0; i < totalPoly; i++) {
            coords[i] = getCoordinateSequencesFromPolygon((Polygon) multiPolygon.getGeometryN(i));
        }
        
        jsonMPoly.setCoordinates(toArray(coords));
        return jsonMPoly;
    }

    private static GeoJSONGeometryCollection toGeoJSONGeometry(GeometryCollection geometryCollection) {
        GeoJSONGeometryCollection coll = new GeoJSONGeometryCollection();
        int numGeometries = geometryCollection.getNumGeometries();

        for (int i = 0; i < numGeometries; i++) {
            coll.getGeometries().add(toGeoJSONGeometry(geometryCollection.getGeometryN(i)));
        }

        return coll;
    }
}
