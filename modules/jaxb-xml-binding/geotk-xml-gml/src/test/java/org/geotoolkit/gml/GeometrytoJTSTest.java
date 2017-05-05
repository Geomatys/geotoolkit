/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.v321.CoordinatesType;
import org.geotoolkit.gml.xml.v321.DirectPositionListType;
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.LineStringType;
import org.geotoolkit.gml.xml.v321.LinearRingType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.gml.xml.v321.PolygonType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GeometrytoJTSTest extends org.geotoolkit.test.TestBase {

    @Test
    public void gmlPolygonToJTSTest2D() throws Exception {
        GeometryFactory fact = new GeometryFactory();
        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0);
        coordinates[1] = new Coordinate(0, 1);
        coordinates[2] = new Coordinate(1, 1);
        coordinates[3] = new Coordinate(1, 0);
        coordinates[4] = new Coordinate(0, 0);

        LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
        Polygon expected = new Polygon(linear, null, fact);
        expected.setSRID(2154);

        LinearRingType exterior = new LinearRingType();
        List<Double> coords = new ArrayList<>();
        coords.add(0.0); coords.add(0.0);
        coords.add(0.0); coords.add(1.0);
        coords.add(1.0); coords.add(1.0);
        coords.add(1.0); coords.add(0.0);
        coords.add(0.0); coords.add(0.0);

        exterior.setPosList(new DirectPositionListType(coords));
        PolygonType gml = new PolygonType(exterior, null);

        final Geometry result = GeometrytoJTS.toJTS((org.geotoolkit.gml.xml.Polygon)gml);

        Assert.assertEquals(expected, result);

    }

    @Test
    public void gmlPolygonToJTSTest3D() throws Exception {
        GeometryFactory fact = new GeometryFactory();
        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0, 1);
        coordinates[1] = new Coordinate(0, 1, 1);
        coordinates[2] = new Coordinate(1, 1, 1);
        coordinates[3] = new Coordinate(1, 0, 1);
        coordinates[4] = new Coordinate(0, 0, 1);

        LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
        Polygon expected = new Polygon(linear, null, fact);
        expected.setSRID(2154);

        LinearRingType exterior = new LinearRingType();
        List<Double> coords = new ArrayList<>();
        coords.add(0.0); coords.add(0.0); coords.add(1.0);
        coords.add(0.0); coords.add(1.0); coords.add(1.0);
        coords.add(1.0); coords.add(1.0); coords.add(1.0);
        coords.add(1.0); coords.add(0.0); coords.add(1.0);
        coords.add(0.0); coords.add(0.0); coords.add(1.0);

        exterior.setPosList(new DirectPositionListType(coords));
        exterior.setSrsDimension(3);
        PolygonType gml = new PolygonType(exterior, null);


        final Geometry result = GeometrytoJTS.toJTS((org.geotoolkit.gml.xml.Polygon)gml);

        Assert.assertEquals(expected, result);

    }

    @Test
    public void gmlLineStringToJTSTest2D() throws Exception {

        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0);
        coordinates[1] = new Coordinate(0, 1);
        coordinates[2] = new Coordinate(1, 1);
        coordinates[3] = new Coordinate(1, 0);
        coordinates[4] = new Coordinate(0, 0);

        LineString expected = new GeometryFactory().createLineString(coordinates);
        expected.setSRID(2154);



        List<DirectPositionType> coords = new ArrayList<>();
        coords.add(new DirectPositionType(0.0, 0.0));
        coords.add(new DirectPositionType(0.0, 1.0));
        coords.add(new DirectPositionType(1.0, 1.0));
        coords.add(new DirectPositionType(1.0, 0.0));
        coords.add(new DirectPositionType(0.0, 0.0));
        LineStringType gml = new LineStringType("", coords);
        gml.setSrsName("EPSG:2154");

        final Geometry result = GeometrytoJTS.toJTS(gml);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void gmlLineStringToJTSTest3D() throws Exception {

        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0, 1);
        coordinates[1] = new Coordinate(0, 1, 1);
        coordinates[2] = new Coordinate(1, 1, 1);
        coordinates[3] = new Coordinate(1, 0, 1);
        coordinates[4] = new Coordinate(0, 0, 1);

        LineString expected = new GeometryFactory().createLineString(coordinates);
        expected.setSRID(2154);



        List<DirectPositionType> coords = new ArrayList<>();
        coords.add(new DirectPositionType(0.0, 0.0, 1.0));
        coords.add(new DirectPositionType(0.0, 1.0, 1.0));
        coords.add(new DirectPositionType(1.0, 1.0, 1.0));
        coords.add(new DirectPositionType(1.0, 0.0, 1.0));
        coords.add(new DirectPositionType(0.0, 0.0, 1.0));
        LineStringType gml = new LineStringType("", coords);
        gml.setSrsName("EPSG:2154");

        final Geometry result = GeometrytoJTS.toJTS(gml);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void gmlPointToJTSTest2D() throws Exception {

        Point expected = new GeometryFactory().createPoint(new Coordinate(0, 1));
        expected.setSRID(2154);

        PointType gml = new PointType(new DirectPositionType(0.0, 1.0));

        final Geometry result = GeometrytoJTS.toJTS(gml);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void gmlPointToJTSTest3D() throws Exception {

        Point expected = new GeometryFactory().createPoint(new Coordinate(0, 1, 1));
        expected.setSRID(2154);

        PointType gml = new PointType(new DirectPositionType(0.0, 1.0, 1.0));

        final Geometry result = GeometrytoJTS.toJTS(gml);

        Assert.assertEquals(expected, result);
    }

}
