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

import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
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
        final Geometry returnedGeom = JTS.ensureWinding(poly, false, false);
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

        final Geometry returnedGeom = JTS.ensureWinding(poly, true, false);

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
        final Geometry returnedGeom = JTS.ensureWinding(poly, false, true);
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
        final Geometry returnedGeom = JTS.ensureWinding(poly, true, true);
        assertFalse(JTS.isCCW3D(returnedGeom.getCoordinates()));

    }

    @Test
    public void testPointShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected = GF.createPoint(new Coordinate(10,20));
        assertEquals(expected, candidate);
    }

    /**
     * Java2d removes consecutive move to operations.
     * only the last is preserved.
     * Java2d Shape can not represent a multipoint.
     */
    @Test
    public void testMultiPointShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);
        path.moveTo(30, 40);
        path.moveTo(50, 60);

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected = GF.createPoint(new Coordinate(50,60));
        assertEquals(expected, candidate);
    }

    @Test
    public void testLineShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);
        path.lineTo(30, 40);
        path.lineTo(50, 60);

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected = GF.createLineString(new Coordinate[]{
            new Coordinate(10,20),
            new Coordinate(30,40),
            new Coordinate(50,60)
        });
        assertEquals(expected, candidate);
    }

    @Test
    public void testMultiLineShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);
        path.lineTo(30, 40);
        path.lineTo(50, 60);
        path.moveTo(70, 80);
        path.lineTo(90, 100);
        path.lineTo(100, 110);

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected = GF.createMultiLineString(new LineString[]{
        GF.createLineString(new Coordinate[]{
            new Coordinate(10,20),
            new Coordinate(30,40),
            new Coordinate(50,60)
        }),
        GF.createLineString(new Coordinate[]{
            new Coordinate(70,80),
            new Coordinate(90,100),
            new Coordinate(100,110)
        })
        });
        assertEquals(expected, candidate);
    }

    @Test
    public void testPolygonShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);
        path.lineTo(10, 10);
        path.lineTo(50, 10);
        path.closePath();

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected = GF.createPolygon(new Coordinate[]{
            new Coordinate(10,20),
            new Coordinate(10,10),
            new Coordinate(50,10),
            new Coordinate(10,20)
        });
        assertEquals(expected, candidate);
    }

    @Test
    public void testMultiPolygonShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);
        path.lineTo(10, 10);
        path.lineTo(50, 10);
        path.closePath();
        path.moveTo(110, 120);
        path.lineTo(110, 110);
        path.lineTo(150, 110);
        path.closePath();

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected = GF.createMultiPolygon(new Polygon[]{
        GF.createPolygon(new Coordinate[]{
            new Coordinate(10,20),
            new Coordinate(10,10),
            new Coordinate(50,10),
            new Coordinate(10,20)
        }),
        GF.createPolygon(new Coordinate[]{
            new Coordinate(110,120),
            new Coordinate(110,110),
            new Coordinate(150,110),
            new Coordinate(110,120)
        })
        });
        assertTrue(expected.equalsTopo(candidate));
    }

    @Test
    public void testMultiGeometryShapeToGeometry() {

        final GeneralPath path = new GeneralPath();
        path.moveTo(10, 20);
        path.lineTo(10, 10);
        path.lineTo(50, 10);
        path.closePath();
        path.moveTo(110, 120);
        path.lineTo(110, 110);

        final Geometry candidate = JTS.fromAwt(GF, path, 0.0001);
        final Geometry expected1 = GF.createPolygon(new Coordinate[]{
            new Coordinate(10,20),
            new Coordinate(10,10),
            new Coordinate(50,10),
            new Coordinate(10,20)
        });
        final Geometry expected2 = GF.createLineString(new Coordinate[]{
            new Coordinate(110,120),
            new Coordinate(110,110)
        });
        assertTrue(candidate instanceof GeometryCollection);
        GeometryCollection gc = (GeometryCollection) candidate;
        assertEquals(2, gc.getNumGeometries());
        assertEquals(expected1, gc.getGeometryN(0));
        assertEquals(expected2, gc.getGeometryN(1));

    }
}
