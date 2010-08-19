/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.shp;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.storage.DataStoreException;

/**
 * A collection of utility methods for use with JTS and the shapefile package.
 * 
 * @author aaime
 * @author Ian Schneider
 * @module pending
 */
public final class JTSUtilities {

    static final GeometryFactory FACTORY = new GeometryFactory();

    private JTSUtilities() {
    }

    /**
     * Determine the min and max "z" values in an array of Coordinates.
     * 
     * @param cs The array to search.
     * @param target
     *            array with at least two elements where to hold the min and max
     *            zvalues. target[0] will be filled with the minimum zvalue,
     *            target[1] with the maximum. The array current values, if not
     *            NaN, will be taken into acount in the computation.
     */
    public static void zMinMax(final CoordinateSequence cs, double[] target) {
        if (cs.getDimension() < 3) {
            return;
        }
        double zmin;
        double zmax;
        boolean validZFound = false;

        zmin = Double.NaN;
        zmax = Double.NaN;

        double z;
        final int size = cs.size();
        for (int t = size - 1; t >= 0; t--) {
            z = cs.getOrdinate(t, 2);

            if (!(Double.isNaN(z))) {
                if (validZFound) {
                    if (z < zmin) {
                        zmin = z;
                    }

                    if (z > zmax) {
                        zmax = z;
                    }
                } else {
                    validZFound = true;
                    zmin = z;
                    zmax = z;
                }
            }
        }

        if(!Double.isNaN(zmin)){
            target[0] = zmin;
        }
        if(!Double.isNaN(zmax)){
            target[1] = zmax;
        }
    }

    /**
     * Does what it says, reverses the order of the Coordinates in the ring.
     * 
     * @param lr The ring to reverse.
     * @return A new ring with the reversed Coordinates.
     */
    public static LinearRing reverseRing(LinearRing lr) {
        final int numPoints = lr.getNumPoints()-1;
        final Coordinate[] newCoords = new Coordinate[numPoints+1];

        for (int t = numPoints; t >= 0; t--) {
            newCoords[t] = lr.getCoordinateN(numPoints - t);
        }

        return FACTORY.createLinearRing(newCoords);
    }

    /**
     * Create a nice Polygon from the given Polygon. Will ensure that shells are
     * clockwise and holes are counter-clockwise.
     * 
     * @param p The Polygon to make "nice".
     * @return The "nice" Polygon.
     */
    public static Polygon makeGoodShapePolygon(Polygon p) {
        final LinearRing outer;
        final LinearRing[] holes = new LinearRing[p.getNumInteriorRing()];
        Coordinate[] coords;

        coords = p.getExteriorRing().getCoordinates();

        if (CGAlgorithms.isCCW(coords)) {
            outer = reverseRing((LinearRing) p.getExteriorRing());
        } else {
            outer = (LinearRing) p.getExteriorRing();
        }

        for (int t = 0, tt = p.getNumInteriorRing(); t < tt; t++) {
            coords = p.getInteriorRingN(t).getCoordinates();

            if (!(CGAlgorithms.isCCW(coords))) {
                holes[t] = reverseRing((LinearRing) p.getInteriorRingN(t));
            } else {
                holes[t] = (LinearRing) p.getInteriorRingN(t);
            }
        }

        return FACTORY.createPolygon(outer, holes);
    }

    /**
     * @see JTSUtilities#makeGoodShapePolygon(com.vividsolutions.jts.geom.Polygon)
     * Like makeGoodShapePolygon, but applied towards a multi polygon.
     * 
     * @param mp The MultiPolygon to "niceify".
     * @return The "nicified" MultiPolygon.
     */
    public static MultiPolygon makeGoodShapeMultiPolygon(MultiPolygon mp) {
        final MultiPolygon result;
        Polygon[] ps = new Polygon[mp.getNumGeometries()];

        // check each sub-polygon
        for (int t = 0; t < mp.getNumGeometries(); t++) {
            ps[t] = makeGoodShapePolygon((Polygon) mp.getGeometryN(t));
        }

        result = FACTORY.createMultiPolygon(ps);

        return result;
    }

    /**
     * Returns: <br>
     * 2 for 2d (default) <br>
     * 4 for 3d - one of the oordinates has a non-NaN z value <br>
     * (3 is for x,y,m but thats not supported yet) <br>
     * 
     * @param cs The array of Coordinates to search.
     * @return The dimension.
     */
    public static int guessCoorinateDims(final Coordinate[] cs) {

        for(final Coordinate c : cs) {
            if (!(Double.isNaN(c.z))) {
                return 4;
            }
        }

        return 2;
    }

    public static Geometry convertToCollection(Geometry geom, ShapeType type) {
        Geometry retVal = null;

        if (type.isPointType()) {
            if (geom instanceof Point) {
                retVal = geom;
            } else {
                Point[] pNull = null;
                retVal = FACTORY.createMultiPoint(pNull);
            }
        } else if (type.isLineType()) {
            if (geom instanceof LineString) {
                retVal = FACTORY.createMultiLineString(new LineString[] { (LineString) geom });
            } else if (geom instanceof MultiLineString) {
                retVal = geom;
            } else {
                retVal = FACTORY.createMultiLineString(null);
            }
        } else if (type.isPolygonType()) {
            if (geom instanceof Polygon) {
                final Polygon p = makeGoodShapePolygon((Polygon) geom);
                retVal = FACTORY.createMultiPolygon(new Polygon[] { p });
            } else if (geom instanceof MultiPolygon) {
                retVal = JTSUtilities.makeGoodShapeMultiPolygon((MultiPolygon) geom);
            } else {
                retVal = FACTORY.createMultiPolygon(null);
            }
        } else if (type.isMultiPointType()) {
            if (geom instanceof Point) {
                retVal = FACTORY.createMultiPoint(new Point[] { (Point) geom });
            } else if (geom instanceof MultiPoint) {
                retVal = geom;
            } else {
                Point[] pNull = null;
                retVal = FACTORY.createMultiPoint(pNull);
            }
        } else {
            throw new RuntimeException("Could not convert " + geom.getClass()+ " to " + type);
        }

        return retVal;
    }

    /**
     * Determine the best ShapeType for a geometry with the given dimension.
     * 
     * @param geom
     *                The Geometry to examine.
     * @param shapeFileDimentions
     *                The dimension 2,3 or 4.
     * @throws ShapefileException
     *                 If theres a problem, like a bogus Geometry.
     * @return The best ShapeType.
     */
    public static ShapeType getShapeType(Geometry geom,
            int shapeFileDimentions) throws DataStoreException {

        ShapeType type = null;

        if (geom instanceof Point) {
            switch (shapeFileDimentions) {
            case 2: type = ShapeType.POINT;  break;
            case 3: type = ShapeType.POINTM; break;
            case 4: type = ShapeType.POINTZ; break;
            default:
                throw new DataStoreException("Too many dimensions for shapefile : "+ shapeFileDimentions);
            }
        } else if (geom instanceof MultiPoint) {
            switch (shapeFileDimentions) {
            case 2: type = ShapeType.MULTIPOINT;  break;
            case 3: type = ShapeType.MULTIPOINTM; break;
            case 4: type = ShapeType.MULTIPOINTZ; break;
            default:
                throw new DataStoreException("Too many dimensions for shapefile : "+ shapeFileDimentions);
            }
        } else if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
            switch (shapeFileDimentions) {
            case 2: type = ShapeType.POLYGON;  break;
            case 3: type = ShapeType.POLYGONM; break;
            case 4: type = ShapeType.POLYGONZ; break;
            default:
                throw new DataStoreException("Too many dimensions for shapefile : "+ shapeFileDimentions);
            }
        } else if ((geom instanceof LineString)
                || (geom instanceof MultiLineString)) {
            switch (shapeFileDimentions) {
            case 2: type = ShapeType.ARC;  break;
            case 3: type = ShapeType.ARCM; break;
            case 4: type = ShapeType.ARCZ; break;
            default:
                throw new DataStoreException("Too many dimensions for shapefile : "+ shapeFileDimentions);
            }
        }

        if (type == null) {
            throw new DataStoreException("Cannot handle geometry type : "
                    + (geom == null ? "null" : geom.getClass().getName()));
        }
        return type;
    }

}
