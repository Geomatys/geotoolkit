/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.geometry.jts;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test JTS utility class
 * @author Quentin Boileau
 * @module
 */
public class JTSTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testSetCRS(){

        //empty user data test
        final Point geom = GF.createPoint(new Coordinate(50, 27));
        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        JTS.setCRS(geom, crs);
        final Object userData = geom.getUserData();
        assertEquals(crs, userData);


        //user data contained another CRS
        final Point geom2 = GF.createPoint(new Coordinate(50, 27));
        geom2.setUserData(CommonCRS.SPHERE.normalizedGeographic());

        JTS.setCRS(geom2, crs);
        final Object userData2 = geom2.getUserData();
        assertEquals(crs, userData2);


        //user data contained a Map with another CRS
        final Point geom3 = GF.createPoint(new Coordinate(50, 27));
        Map<String,CoordinateReferenceSystem> dataMap = new HashMap<String,CoordinateReferenceSystem>();
        dataMap.put(org.apache.sis.internal.feature.jts.JTS.CRS_KEY, CommonCRS.SPHERE.normalizedGeographic());
        geom3.setUserData(dataMap);

        JTS.setCRS(geom3, crs);
        final Object userData3 = geom3.getUserData();
        Map values = (Map) userData3;
        assertEquals(crs, values.get(org.apache.sis.internal.feature.jts.JTS.CRS_KEY));

    }


    @Test
    public void testCCW(){

        //empty user data test
        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(50, 27),
            new Coordinate(25, 15),
            new Coordinate(10, 10),
            new Coordinate(28, 30),
            new Coordinate(50, 27)
                });

        final Polygon poly = GF.createPolygon(ring, null);
        final Geometry returnedGeom = JTS.ensureCounterClockWise(poly);
        assertTrue(Orientation.isCCW(returnedGeom.getCoordinates()));

    }


     @Test
    public void testCW(){

        //empty user data test
        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(50, 27),
            new Coordinate(28, 30),
            new Coordinate(10, 10),
            new Coordinate(25, 15),
            new Coordinate(50, 27)
                });

        final Polygon poly = GF.createPolygon(ring, null);

        final Geometry returnedGeom = JTS.ensureClockWise(poly);

        assertFalse(Orientation.isCCW(returnedGeom.getCoordinates()));

    }

     @Test
    public void testCCW3D(){

        //empty user data test
        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(10, 0, 0),
            new Coordinate(10, 0, 10),
            new Coordinate(0, 10, 10),
            new Coordinate(0, 10, 0),
            new Coordinate(10, 0, 0)
                });

        final Polygon poly = GF.createPolygon(ring, null);
        final Geometry returnedGeom = JTS.ensureCounterClockWise3D(poly);
        assertTrue(JTS.isCCW3D(returnedGeom.getCoordinates()));

    }


     @Test
    public void testCW3D(){

        //empty user data test
        final LinearRing ring = GF.createLinearRing(new Coordinate[]{
            new Coordinate(10, 0, 0),
            new Coordinate(0, 10, 0),
            new Coordinate(0, 10, 10),
            new Coordinate(10, 0, 10),
            new Coordinate(10, 0, 0)
                });

        final Polygon poly = GF.createPolygon(ring, null);
        final Geometry returnedGeom = JTS.ensureClockWise3D(poly);
        assertFalse(JTS.isCCW3D(returnedGeom.getCoordinates()));

    }
}
