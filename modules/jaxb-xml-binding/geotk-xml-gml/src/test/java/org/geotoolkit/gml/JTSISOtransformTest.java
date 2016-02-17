/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.GMLMarshallerPool;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JTSISOtransformTest extends org.geotoolkit.test.TestBase {

    @Test
    public void polygonToISOTest() throws Exception {
        GeometryFactory fact = new GeometryFactory();
        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0);
        coordinates[1] = new Coordinate(0, 1);
        coordinates[2] = new Coordinate(1, 1);
        coordinates[3] = new Coordinate(1, 0);
        coordinates[4] = new Coordinate(0, 0);

        LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
        Polygon poly = new Polygon(linear, null, fact);
        poly.setSRID(2154);

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", poly);
        final Geometry geom = GeometrytoJTS.toJTS(gml);

         //System.out.println(geom);
        System.out.println("SRID:"+ geom.getSRID());

    }

    @Test
    public void multiPolygonToISOTest() throws Exception {
        GeometryFactory fact = new GeometryFactory();
        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0);
        coordinates[1] = new Coordinate(0, 1);
        coordinates[2] = new Coordinate(1, 1);
        coordinates[3] = new Coordinate(1, 0);
        coordinates[4] = new Coordinate(0, 0);

        LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
        Polygon poly = new Polygon(linear, null, fact);
        poly.setSRID(2154);

        final Coordinate[] coordinates2 = new Coordinate[5];
        coordinates2[0] = new Coordinate(10, 10);
        coordinates2[1] = new Coordinate(10, 11);
        coordinates2[2] = new Coordinate(11, 11);
        coordinates2[3] = new Coordinate(11, 10);
        coordinates2[4] = new Coordinate(10, 10);

        LinearRing linear2 = new GeometryFactory().createLinearRing(coordinates2);
        Polygon poly2 = new Polygon(linear2, null, fact);
        poly2.setSRID(2154);

        Polygon[] polygons = new Polygon[2];
        polygons[0] = poly;
        polygons[1] = poly2;

        MultiPolygon m = new MultiPolygon(polygons, fact);
        m.setSRID(2154);

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", m);
        final Geometry geom = GeometrytoJTS.toJTS(gml);

        //System.out.println(geom);
        System.out.println("SRID:"+ geom.getSRID());

    }
}
