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
package org.geotoolkit.gml.xml;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.util.GeometricShapeFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v321.AngleType;
import org.geotoolkit.gml.xml.v321.ArcByCenterPointType;
import org.geotoolkit.gml.xml.v321.CurveSegmentArrayPropertyType;
import org.geotoolkit.gml.xml.v321.CurveType;
import org.geotoolkit.gml.xml.v321.DirectPositionListType;
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.gml.xml.v321.GeodesicStringType;
import org.geotoolkit.gml.xml.v321.LengthType;
import org.geotoolkit.gml.xml.v321.LineStringType;
import org.geotoolkit.gml.xml.v321.LinearRingType;
import org.geotoolkit.gml.xml.v321.PointPropertyType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.gml.xml.v321.PolygonType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GeometrytoJTSTest {

    static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    @Test
    public void gmlPolygonToJTSTest2D() throws Exception {
        GeometryFactory fact = GF;
        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0);
        coordinates[1] = new Coordinate(0, 1);
        coordinates[2] = new Coordinate(1, 1);
        coordinates[3] = new Coordinate(1, 0);
        coordinates[4] = new Coordinate(0, 0);

        LinearRing linear = GF.createLinearRing(coordinates);
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
        GeometryFactory fact = GF;
        final Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(0, 0, 1);
        coordinates[1] = new Coordinate(0, 1, 1);
        coordinates[2] = new Coordinate(1, 1, 1);
        coordinates[3] = new Coordinate(1, 0, 1);
        coordinates[4] = new Coordinate(0, 0, 1);

        LinearRing linear = GF.createLinearRing(coordinates);
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

        LineString expected = GF.createLineString(coordinates);
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

        LineString expected = GF.createLineString(coordinates);
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

        Point expected = GF.createPoint(new Coordinate(0, 1));
        expected.setSRID(2154);

        PointType gml = new PointType(new DirectPositionType(0.0, 1.0));

        final Geometry result = GeometrytoJTS.toJTS(gml);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void gmlPointToJTSTest3D() throws Exception {

        Point expected = GF.createPoint(new Coordinate(0, 1, 1));
        expected.setSRID(2154);

        PointType gml = new PointType(new DirectPositionType(0.0, 1.0, 1.0));

        final Geometry result = GeometrytoJTS.toJTS(gml);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void gmlGeodesicStringToJTSTest2D() throws Exception {

        final DirectPositionListType posLst = new DirectPositionListType(
                Arrays.asList(10.0,20.0,30.0,40.0,50.0,60.0));
        final GeodesicStringType s = new GeodesicStringType();
        s.setPosList(posLst);
        final CurveSegmentArrayPropertyType segments = new CurveSegmentArrayPropertyType();
        segments.setAbstractCurveSegment(s);
        final CurveType curve = new CurveType();
        curve.setSegments(segments);

        final LineString expected = GF.createLineString(new Coordinate[]{
            new Coordinate(10, 20),
            new Coordinate(30, 40),
            new Coordinate(50, 60),
        });

        final Geometry result = GeometrytoJTS.toJTS(curve);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void gmlEnvelopeToJTSTest2D() throws Exception {
        final EnvelopeType env = new EnvelopeType(new DirectPositionType(2.0, 2.0), new DirectPositionType(4.0, 4.0), "EPSG:4326");
        final Geometry geom = GeometrytoJTS.toJTS(env);

        Coordinate[] expectedPoints = {
            new Coordinate(2.0, 2.0),
            new Coordinate(2.0, 4.0),
            new Coordinate(4.0, 4.0),
            new Coordinate(4.0, 2.0),
            new Coordinate(2.0, 2.0)
        };

        Assert.assertTrue(GF.createPolygon(expectedPoints).equalsTopo(geom));
    }

    @Test
    public void gmlArcToJTSTest() throws Exception {
        final LengthType radius = new LengthType();
        radius.setValue(2.0);
        radius.setUom("m");
        final AngleType startAngle = new AngleType();
        startAngle.setValue(0);
        startAngle.setUom("rad");

        final AngleType endAngle = new AngleType();
        endAngle.setValue(270);
        endAngle.setUom("°");

        final ArcByCenterPointType arc = new ArcByCenterPointType();
        arc.setStartAngle(startAngle);
        arc.setEndAngle(endAngle);
        arc.setRadius(radius);
        arc.setPointProperty(new PointPropertyType(new PointType(new DirectPosition2D(0, 0))));

        final CurveType gmlCurve = new CurveType();
        gmlCurve.setSrsName("EPSG:3857");

        gmlCurve.setSegments(new CurveSegmentArrayPropertyType(Collections.singletonList(arc)));
        Geometry geom = GeometrytoJTS.toJTS(gmlCurve);

        // For geometric comparison, we oppose pure jts solution to the SIS geodetic calculator.
        final GeometricShapeFactory f = new GeometricShapeFactory(GF);
        f.setCentre(new Coordinate(0, 0));
        f.setSize(radius.getValue()*2);
        // with this value, we should get points on trigonometric circle cardinalities (0, PI/2, PI, etc.), which eases comparison in debug.
        f.setNumPoints(17);
        // JTS angles are not azimuth, but pure trigonometric
        Geometry expectedArc = f.createArc(Math.PI, 3/2.0 * Math.PI);

        Assert.assertTrue(expectedArc.buffer(0.2).contains(geom));

        // Now, test in reverse (counter-clockwise) order
        endAngle.setValue(-90);
        geom = GeometrytoJTS.toJTS(gmlCurve);
        expectedArc = f.createArc(0, Math.PI);

        Assert.assertTrue(expectedArc.buffer(0.2).contains(geom));
    }
}
