/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.data.memory.mapping;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Set of Utility function and methods for mapping
 * @author Quentin Boileau
 * @module pending
 */
public final class MappingUtils {


     private MappingUtils(){}


     public static Geometry convertType(final Geometry geom, final Class targetClass){
        if(geom == null) return null;

        if(targetClass.isInstance(geom)){
            return geom;
        }

        if(targetClass == Point.class){
            return convertToPoint(geom);
        }else if(targetClass == MultiPoint.class){
            return convertToMultiPoint(geom);
        }else if(targetClass == LineString.class){
            return convertToLineString(geom);
        }else if(targetClass == MultiLineString.class){
            return convertToMultiLineString(geom);
        }else if(targetClass == Polygon.class){
            return convertToPolygon(geom);
        }else if(targetClass == MultiPolygon.class){
            return convertToMultiPolygon(geom);
        }

        return null;
    }

    private static GeometryFactory GF = new GeometryFactory();

    // Convert to Point --------------------------------------------------------

    private static Point convertToPoint(final Geometry geom){
        if(geom instanceof Point){
            return convertToPoint((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToPoint((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToPoint((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToPoint((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToPoint((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToPoint((MultiPolygon)geom);
        }
        return null;
    }

    private static Point convertToPoint(final Point pt){
        return pt;
    }

    private static Point convertToPoint(final MultiPoint pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(final LineString pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(final MultiLineString pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(final Polygon pt){
        return pt.getCentroid();
    }

    private static Point convertToPoint(final MultiPolygon pt){
        return pt.getCentroid();
    }

    // Convert to MultiPoint ---------------------------------------------------

    private static MultiPoint convertToMultiPoint(final Geometry geom){
        if(geom instanceof Point){
            return convertToMultiPoint((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToMultiPoint((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToMultiPoint((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToMultiPoint((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToMultiPoint((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToMultiPoint((MultiPolygon)geom);
        }
        return null;
    }

    private static MultiPoint convertToMultiPoint(final Point pt){
        return GF.createMultiPoint(new Point[]{pt});
    }

    private static MultiPoint convertToMultiPoint(final MultiPoint pt){
        return pt;
    }

    private static MultiPoint convertToMultiPoint(final LineString pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(final MultiLineString pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(final Polygon pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    private static MultiPoint convertToMultiPoint(final MultiPolygon pt){
        return GF.createMultiPoint(pt.getCoordinates());
    }

    // Convert to LineString ---------------------------------------------------

    private static LineString convertToLineString(final Geometry geom){
        if(geom instanceof Point){
            return convertToLineString((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToLineString((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToLineString((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToLineString((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToLineString((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToLineString((MultiPolygon)geom);
        }
        return null;
    }

    private static LineString convertToLineString(final Point pt){
        return GF.createLineString(new Coordinate[]{pt.getCoordinate(),pt.getCoordinate()});
    }

    private static LineString convertToLineString(final MultiPoint pt){
        final Coordinate[] coords = pt.getCoordinates();
        if(coords.length == 1){
            return GF.createLineString(new Coordinate[]{coords[0],coords[0]});
        }else{
            return GF.createLineString(coords);
        }
    }

    private static LineString convertToLineString(final LineString pt){
        return pt;
    }

    private static LineString convertToLineString(final MultiLineString pt){
        return GF.createLineString(pt.getCoordinates());
    }

    private static LineString convertToLineString(final Polygon pt){
        return GF.createLineString(pt.getCoordinates());
    }

    private static LineString convertToLineString(final MultiPolygon pt){
        return GF.createLineString(pt.getCoordinates());
    }

    // Convert to MultiLineString ----------------------------------------------

    private static MultiLineString convertToMultiLineString(final Geometry geom){
        if(geom instanceof Point){
            return convertToMultiLineString((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToMultiLineString((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToMultiLineString((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToMultiLineString((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToMultiLineString((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToMultiLineString((MultiPolygon)geom);
        }
        return null;
    }

    private static MultiLineString convertToMultiLineString(final Point pt){
        return convertToMultiLineString(convertToLineString(pt));
    }

    private static MultiLineString convertToMultiLineString(final MultiPoint pt){
        return convertToMultiLineString(convertToLineString(pt));
    }

    private static MultiLineString convertToMultiLineString(final LineString pt){
        return GF.createMultiLineString(new LineString[]{pt});
    }

    private static MultiLineString convertToMultiLineString(final MultiLineString pt){
        return pt;
    }

    private static MultiLineString convertToMultiLineString(final Polygon pt){
        return convertToMultiLineString(GF.createLineString(pt.getCoordinates()));
    }

    private static MultiLineString convertToMultiLineString(final MultiPolygon pt){
        final int n = pt.getNumGeometries();
        final LineString[] geoms = new LineString[n];
        for(int i=0; i<n;i++){
            geoms[i] = convertToLineString(pt.getGeometryN(i));
        }
        return GF.createMultiLineString(geoms);
    }

    // Convert to Polygon ------------------------------------------------------

    private static Polygon convertToPolygon(final Geometry geom){
        if(geom instanceof Point){
            return convertToPolygon((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToPolygon((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToPolygon((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToPolygon((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToPolygon((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToPolygon((MultiPolygon)geom);
        }
        return null;
    }

    private static Polygon convertToPolygon(final Point pt){
        LinearRing ring = GF.createLinearRing(new Coordinate[]{pt.getCoordinate(),pt.getCoordinate(),pt.getCoordinate()});
        return GF.createPolygon(ring, new LinearRing[0]);
    }

    private static Polygon convertToPolygon(final MultiPoint pt){
        return convertToPolygon(convertToLineString(pt));
    }

    private static Polygon convertToPolygon(final LineString pt){
        return GF.createPolygon(GF.createLinearRing(pt.getCoordinates()), new LinearRing[0]);
    }

    private static Polygon convertToPolygon(final MultiLineString pt){
        return convertToPolygon(convertToLineString(pt));
    }

    private static Polygon convertToPolygon(final Polygon pt){
        return pt;
    }

    private static Polygon convertToPolygon(final MultiPolygon pt){
        return convertToPolygon(pt.convexHull());
    }

    // Convert to MultiPolygon -------------------------------------------------

    private static MultiPolygon convertToMultiPolygon(final Geometry geom){
        if(geom instanceof Point){
            return convertToMultiPolygon((Point)geom);
        }else if(geom instanceof MultiPoint){
            return convertToMultiPolygon((MultiPoint)geom);
        }else if(geom instanceof LineString){
            return convertToMultiPolygon((LineString)geom);
        }else if(geom instanceof MultiLineString){
            return convertToMultiPolygon((MultiLineString)geom);
        }else if(geom instanceof Polygon){
            return convertToMultiPolygon((Polygon)geom);
        }else if(geom instanceof MultiPolygon){
            return convertToMultiPolygon((MultiPolygon)geom);
        }
        return null;
    }

    private static MultiPolygon convertToMultiPolygon(final Point pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final MultiPoint pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final LineString pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final MultiLineString pt){
        return convertToMultiPolygon(convertToPolygon(pt));
    }

    private static MultiPolygon convertToMultiPolygon(final Polygon pt){
        return GF.createMultiPolygon(new Polygon[]{pt});
    }

    private static MultiPolygon convertToMultiPolygon(final MultiPolygon pt){
        return pt;
    }




}
