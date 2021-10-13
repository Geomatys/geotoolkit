/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.display2d.jts;

import org.locationtech.jts.geom.Point;
import java.awt.Shape;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import java.awt.geom.PathIterator;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.geotoolkit.geometry.jts.awt.JTSMultiLineStringJ2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PathIteratorTest extends org.geotoolkit.test.TestBase {

    private static double DELTA = 0.000000001d;
    public static final GeometryFactory GF = new GeometryFactory();

    public PathIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testPoint() {

        final Point point1 = GF.createPoint(new Coordinate(10, 20));

        final Shape shape = new JTSGeometryJ2D(point1);
        final PathIterator ite = shape.getPathIterator(null);

        double[] buffer = new double[2];
        int type;

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(10, buffer[0], DELTA);
        assertEquals(20, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertTrue(ite.isDone());
    }

    @Test
    public void testLineString() {

        final LineString line2 = GF.createLineString(new Coordinate[]{
            new Coordinate(3, 1),
            new Coordinate(7, 6),
            new Coordinate(5, 2)
        });

        final Shape shape = new JTSGeometryJ2D(line2);
        final PathIterator ite = shape.getPathIterator(null);

        double[] buffer = new double[2];
        int type;

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(3, buffer[0], DELTA);
        assertEquals(1, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(7, buffer[0], DELTA);
        assertEquals(6, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(5, buffer[0], DELTA);
        assertEquals(2, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertTrue(ite.isDone());
    }

    @Test
    public void testMultiLineString() {

        final LineString line1 = GF.createLineString(new Coordinate[]{
            new Coordinate(10, 12),
            new Coordinate(5, 2)
        });
        final LineString line2 = GF.createLineString(new Coordinate[]{
            new Coordinate(3, 1),
            new Coordinate(7, 6),
            new Coordinate(5, 2)
        });

        final MultiLineString ml = GF.createMultiLineString(new LineString[]{line1,line2});
        final Shape shape = new JTSMultiLineStringJ2D(ml);
        final PathIterator ite = shape.getPathIterator(null);

        double[] buffer = new double[2];
        int type;

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(10, buffer[0], DELTA);
        assertEquals(12, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(5, buffer[0], DELTA);
        assertEquals(2, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(3, buffer[0], DELTA);
        assertEquals(1, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(7, buffer[0], DELTA);
        assertEquals(6, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(5, buffer[0], DELTA);
        assertEquals(2, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertTrue(ite.isDone());
    }

    @Test
    public void testMultiLineString2() throws ParseException{

        final WKTReader reader = new WKTReader();
        final Geometry geom = reader.read("MULTILINESTRING ((-5 0, 5 0), (355 0, 365 0), (-365 0, -355 0))");

        final JTSGeometryJ2D shape = new JTSGeometryJ2D(geom);

        final PathIterator ite = shape.getPathIterator(null);

        double[] buffer = new double[2];
        int type;

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(-5, buffer[0], DELTA);
        assertEquals(0, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(5, buffer[0], DELTA);
        assertEquals(0, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(355, buffer[0], DELTA);
        assertEquals(0, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(365, buffer[0], DELTA);
        assertEquals(0, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(-365, buffer[0], DELTA);
        assertEquals(0, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(-355, buffer[0], DELTA);
        assertEquals(0, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertTrue(ite.isDone());

    }

    @Test
    public void testPolygon() {

        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(3, 1),
            new Coordinate(7, 6),
            new Coordinate(5, 2),
            new Coordinate(3, 1)
        });

        final Polygon polygon = GF.createPolygon(ring, new LinearRing[0]);

        final Shape shape = new JTSGeometryJ2D(polygon);
        final PathIterator ite = shape.getPathIterator(null);

        double[] buffer = new double[2];
        int type;

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(3, buffer[0], DELTA);
        assertEquals(1, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(7, buffer[0], DELTA);
        assertEquals(6, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(5, buffer[0], DELTA);
        assertEquals(2, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(PathIterator.SEG_CLOSE, type);
        ite.next();

        assertTrue(ite.isDone());
    }

    @Test
    public void testMultiPolygon() {

        final LinearRing ring1 = GF.createLinearRing(new Coordinate[]{
            new Coordinate(3, 1),
            new Coordinate(7, 6),
            new Coordinate(5, 2),
            new Coordinate(3, 1)
        });

        final LinearRing ring2 = GF.createLinearRing(new Coordinate[]{
            new Coordinate(12, 3),
            new Coordinate(1, 9),
            new Coordinate(4, 6),
            new Coordinate(12, 3)
        });

        final Polygon polygon1 = GF.createPolygon(ring1, new LinearRing[0]);
        final Polygon polygon2 = GF.createPolygon(ring2, new LinearRing[0]);
        final MultiPolygon poly = GF.createMultiPolygon(new Polygon[]{polygon1,polygon2});

        final Shape shape = new JTSGeometryJ2D(poly);
        final PathIterator ite = shape.getPathIterator(null);

        double[] buffer = new double[2];
        int type;

        //first polygon
        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(3, buffer[0], DELTA);
        assertEquals(1, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(7, buffer[0], DELTA);
        assertEquals(6, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(5, buffer[0], DELTA);
        assertEquals(2, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(PathIterator.SEG_CLOSE, type);
        ite.next();

        // second polygon
        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(12, buffer[0], DELTA);
        assertEquals(3, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_MOVETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(1, buffer[0], DELTA);
        assertEquals(9, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(4, buffer[0], DELTA);
        assertEquals(6, buffer[1], DELTA);
        assertEquals(PathIterator.SEG_LINETO, type);
        ite.next();

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(PathIterator.SEG_CLOSE, type);
        ite.next();



        assertTrue(ite.isDone());
    }

}
