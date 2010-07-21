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

import java.awt.Shape;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import java.awt.geom.PathIterator;
import org.geotoolkit.display2d.primitive.jts.JTSGeometryJ2D;
import org.geotoolkit.display2d.primitive.jts.JTSMultiLineStringJ2D;
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
public class PathIteratorTest {

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
        assertEquals(PathIterator.SEG_CLOSE, type);
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

        assertFalse(ite.isDone());
        type = ite.currentSegment(buffer);
        assertEquals(PathIterator.SEG_CLOSE, type);
        ite.next();

        assertTrue(ite.isDone());
    }


}
