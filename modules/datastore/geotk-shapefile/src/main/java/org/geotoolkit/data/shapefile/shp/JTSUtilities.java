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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.geometry.jts.JTS;
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

    public static Geometry convertToCollection(final Geometry geom, final ShapeType type) {
        Geometry retVal = null;

        if(type == ShapeType.NULL){
            Point[] pNull = null;
            retVal = FACTORY.createMultiPoint(pNull);
        } else if (type.isPointType()) {
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
            if (geom instanceof Polygon ) {
                final Polygon p = JTS.ensureClockWise((Polygon) geom);
                retVal = FACTORY.createMultiPolygon(new Polygon[] { p });
            } else if (geom instanceof MultiPolygon) {
                retVal = JTS.ensureClockWise((MultiPolygon) geom);
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
    public static ShapeType getShapeType(final Geometry geom,
            final int shapeFileDimentions) throws DataStoreException {

        ShapeType type = null;

        if (geom == null) {
            type = ShapeType.NULL;
        } else if (geom instanceof Point) {
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
