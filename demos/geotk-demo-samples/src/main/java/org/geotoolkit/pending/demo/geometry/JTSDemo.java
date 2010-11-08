
package org.geotoolkit.pending.demo.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class JTSDemo {

    public static void main(String[] args) {

        final GeometryFactory gf = new GeometryFactory();

        //creating a point -----------------------------------------------
        final Point point = gf.createPoint(new Coordinate(56, 45));

        //creating a multipoint ------------------------------------------
        final MultiPoint mp = gf.createMultiPoint(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94)});

        //creating a linestring ------------------------------------------
        final LineString ls = gf.createLineString(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94)});

        //creating a multilinestring -------------------------------------
        final LineString ls1 = gf.createLineString(new Coordinate[]{
                                    new Coordinate(30, 45),new Coordinate(56, 29)});
        final LineString ls2 = gf.createLineString(new Coordinate[]{
                                    new Coordinate(98,12),new Coordinate(19, 87)});
        final MultiLineString mls = gf.createMultiLineString(new LineString[]{
                                    ls1,ls2});

        //creating a polygon ---------------------------------------------
        final LinearRing ring = gf.createLinearRing(new Coordinate[]{
                                    new Coordinate(23, 78),
                                    new Coordinate(-10, 43),
                                    new Coordinate(12, 94),
                                    new Coordinate(23, 78)});
        final Polygon polygon = gf.createPolygon(ring, new LinearRing[0]);

        //creating a multipolygon ----------------------------------------
        final MultiPolygon mpolygon = gf.createMultiPolygon(new Polygon[]{polygon});


    }
   
}
