/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.gml.JTStoGeometry;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JTStoGeometryTest {

    private static final GeometryFactory GF = new GeometryFactory();

    @Test
    public void testPoint_2D_v321() throws FactoryException {

        Point point = GF.createPoint(new Coordinate(10, 20));
        point.setUserData(CommonCRS.WGS84.normalizedGeographic());

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", point);
        assertTrue(gml instanceof org.geotoolkit.gml.xml.v321.PointType);
        org.geotoolkit.gml.xml.v321.PointType gmlPoint = (org.geotoolkit.gml.xml.v321.PointType) gml;

        assertNotNull(gmlPoint.getSrsDimension());
        assertNotNull(gmlPoint.getSrsName());
        assertNotNull(gmlPoint.getPos());
        assertEquals(2, gmlPoint.getSrsDimension().intValue());
        assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", gmlPoint.getSrsName());

        org.geotoolkit.gml.xml.v321.DirectPositionType pos = gmlPoint.getPos();
        assertNull(pos.getSrsDimension());
        assertNull(pos.getSrsName());
        assertNotNull(pos.getValue());
        assertEquals(2, pos.getValue().size());
        assertEquals(10.0, pos.getValue().get(0), 0.0);
        assertEquals(20.0, pos.getValue().get(1), 0.0);

    }

    @Test
    public void testLineString_2D_v321() throws FactoryException {

        org.locationtech.jts.geom.LineString lineString = GF.createLineString(new Coordinate[]{new Coordinate(10, 20), new Coordinate(30, 40)});
        lineString.setUserData(CommonCRS.WGS84.normalizedGeographic());

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", lineString);
        assertTrue(gml instanceof org.geotoolkit.gml.xml.v321.LineStringType);
        org.geotoolkit.gml.xml.v321.LineStringType gmlLineString = (org.geotoolkit.gml.xml.v321.LineStringType) gml;

        assertNotNull(gmlLineString.getSrsDimension());
        assertNotNull(gmlLineString.getSrsName());
        assertNotNull(gmlLineString.getPosList());
        assertEquals(0,gmlLineString.getPos().size());
        assertEquals(2, gmlLineString.getSrsDimension().intValue());
        assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", gmlLineString.getSrsName());

        org.geotoolkit.gml.xml.v321.DirectPositionListType pos = gmlLineString.getPosList();
        assertNull(pos.getSrsDimension());
        assertNull(pos.getSrsName());
        assertNotNull(pos.getValue());
        assertEquals(4, pos.getValue().size());
        assertEquals(10.0, pos.getValue().get(0), 0.0);
        assertEquals(20.0, pos.getValue().get(1), 0.0);
        assertEquals(30.0, pos.getValue().get(2), 0.0);
        assertEquals(40.0, pos.getValue().get(3), 0.0);

    }

    @Test
    public void testPolygon_2D_v321() throws FactoryException {

        org.locationtech.jts.geom.LinearRing lineString = GF.createLinearRing(new Coordinate[]{new Coordinate(10, 20), new Coordinate(30, 40), new Coordinate(50, 60), new Coordinate(10, 20)});
        Polygon polygon = GF.createPolygon(lineString);
        polygon.setUserData(CommonCRS.WGS84.normalizedGeographic());

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", polygon);
        assertTrue(gml instanceof org.geotoolkit.gml.xml.v321.PolygonType);
        org.geotoolkit.gml.xml.v321.PolygonType gmlPolygon = (org.geotoolkit.gml.xml.v321.PolygonType) gml;
        assertNotNull(gmlPolygon.getSrsDimension());
        assertNotNull(gmlPolygon.getSrsName());
        assertEquals(2, gmlPolygon.getSrsDimension().intValue());
        assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", gmlPolygon.getSrsName());

        org.geotoolkit.gml.xml.v321.LinearRingType gmlLinearRing = (org.geotoolkit.gml.xml.v321.LinearRingType) gmlPolygon.getExterior().getAbstractRing();

        assertNull(gmlLinearRing.getSrsDimension());
        assertNull(gmlLinearRing.getSrsName());
        assertNotNull(gmlLinearRing.getPosList());

        org.geotoolkit.gml.xml.v321.DirectPositionListType pos = gmlLinearRing.getPosList();
        assertNull(pos.getSrsDimension());
        assertNull(pos.getSrsName());
        assertNotNull(pos.getValue());
        assertEquals(8, pos.getValue().size());
        assertEquals(10.0, pos.getValue().get(0), 0.0);
        assertEquals(20.0, pos.getValue().get(1), 0.0);
        assertEquals(30.0, pos.getValue().get(2), 0.0);
        assertEquals(40.0, pos.getValue().get(3), 0.0);
        assertEquals(50.0, pos.getValue().get(4), 0.0);
        assertEquals(60.0, pos.getValue().get(5), 0.0);
        assertEquals(10.0, pos.getValue().get(6), 0.0);
        assertEquals(20.0, pos.getValue().get(7), 0.0);

    }

    @Test
    public void testMultiPoint_2D_v321() throws FactoryException {

        Point point = GF.createPoint(new Coordinate(10, 20));
        org.locationtech.jts.geom.MultiPoint mpoint = GF.createMultiPoint(new Point[]{point});
        mpoint.setUserData(CommonCRS.WGS84.normalizedGeographic());

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", mpoint);
        assertTrue(gml instanceof org.geotoolkit.gml.xml.v321.MultiPointType);
        org.geotoolkit.gml.xml.v321.MultiPointType gmlMulti = (org.geotoolkit.gml.xml.v321.MultiPointType) gml;
        assertNotNull(gmlMulti.getSrsDimension());
        assertNotNull(gmlMulti.getSrsName());
        assertEquals(2, gmlMulti.getSrsDimension().intValue());
        assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", gmlMulti.getSrsName());

        org.geotoolkit.gml.xml.v321.PointType gmlPoint = (org.geotoolkit.gml.xml.v321.PointType) gmlMulti.getPointMember().get(0).getPoint();

        assertNull(gmlPoint.getSrsDimension());
        assertNull(gmlPoint.getSrsName());
        assertNotNull(gmlPoint.getPos());

        org.geotoolkit.gml.xml.v321.DirectPositionType pos = gmlPoint.getPos();
        assertNull(pos.getSrsDimension());
        assertNull(pos.getSrsName());
        assertNotNull(pos.getValue());
        assertEquals(2, pos.getValue().size());
        assertEquals(10.0, pos.getValue().get(0), 0.0);
        assertEquals(20.0, pos.getValue().get(1), 0.0);

    }

    @Test
    public void testMultiLineString_2D_v321() throws FactoryException {

        org.locationtech.jts.geom.LineString lineString = GF.createLineString(new Coordinate[]{new Coordinate(10, 20), new Coordinate(30, 40)});
        MultiLineString mline = GF.createMultiLineString(new org.locationtech.jts.geom.LineString[]{lineString});
        mline.setUserData(CommonCRS.WGS84.normalizedGeographic());

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", mline);
        assertTrue(gml instanceof org.geotoolkit.gml.xml.v321.MultiCurveType);
        org.geotoolkit.gml.xml.v321.MultiCurveType gmlMulti = (org.geotoolkit.gml.xml.v321.MultiCurveType) gml;
        assertNotNull(gmlMulti.getSrsDimension());
        assertNotNull(gmlMulti.getSrsName());
        assertEquals(2, gmlMulti.getSrsDimension().intValue());
        assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", gmlMulti.getSrsName());

        org.geotoolkit.gml.xml.v321.LineStringType gmlLineString = (org.geotoolkit.gml.xml.v321.LineStringType) gmlMulti.getCurveMember().get(0).getAbstractCurve();

        assertNull(gmlLineString.getSrsDimension());
        assertNull(gmlLineString.getSrsName());
        assertNotNull(gmlLineString.getPosList());
        assertEquals(0,gmlLineString.getPos().size());

        org.geotoolkit.gml.xml.v321.DirectPositionListType pos = gmlLineString.getPosList();
        assertNull(pos.getSrsDimension());
        assertNull(pos.getSrsName());
        assertNotNull(pos.getValue());
        assertEquals(4, pos.getValue().size());
        assertEquals(10.0, pos.getValue().get(0), 0.0);
        assertEquals(20.0, pos.getValue().get(1), 0.0);
        assertEquals(30.0, pos.getValue().get(2), 0.0);
        assertEquals(40.0, pos.getValue().get(3), 0.0);

    }


    @Test
    public void testMultiPolygon_2D_v321() throws FactoryException {

        org.locationtech.jts.geom.LinearRing lineString = GF.createLinearRing(new Coordinate[]{new Coordinate(10, 20), new Coordinate(30, 40), new Coordinate(50, 60), new Coordinate(10, 20)});
        Polygon polygon = GF.createPolygon(lineString);
        org.locationtech.jts.geom.MultiPolygon mpolygon = GF.createMultiPolygon(new org.locationtech.jts.geom.Polygon[]{polygon});
        mpolygon.setUserData(CommonCRS.WGS84.normalizedGeographic());

        AbstractGeometry gml = JTStoGeometry.toGML("3.2.1", mpolygon);
        assertTrue(gml instanceof org.geotoolkit.gml.xml.v321.MultiSurfaceType);
        org.geotoolkit.gml.xml.v321.MultiSurfaceType gmlMulti = (org.geotoolkit.gml.xml.v321.MultiSurfaceType) gml;
        assertNotNull(gmlMulti.getSrsDimension());
        assertNotNull(gmlMulti.getSrsName());
        assertEquals(2, gmlMulti.getSrsDimension().intValue());
        assertEquals("urn:ogc:def:crs:OGC:1.3:CRS84", gmlMulti.getSrsName());

        org.geotoolkit.gml.xml.v321.PolygonType gmlPolygon = (org.geotoolkit.gml.xml.v321.PolygonType) gmlMulti.getSurfaceMember().get(0).getAbstractSurface();
        assertNull(gmlPolygon.getSrsDimension());
        assertNull(gmlPolygon.getSrsName());

        org.geotoolkit.gml.xml.v321.LinearRingType gmlLinearRing = (org.geotoolkit.gml.xml.v321.LinearRingType) gmlPolygon.getExterior().getAbstractRing();

        assertNull(gmlLinearRing.getSrsDimension());
        assertNull(gmlLinearRing.getSrsName());
        assertNotNull(gmlLinearRing.getPosList());

        org.geotoolkit.gml.xml.v321.DirectPositionListType pos = gmlLinearRing.getPosList();
        assertNull(pos.getSrsDimension());
        assertNull(pos.getSrsName());
        assertNotNull(pos.getValue());
        assertEquals(8, pos.getValue().size());
        assertEquals(10.0, pos.getValue().get(0), 0.0);
        assertEquals(20.0, pos.getValue().get(1), 0.0);
        assertEquals(30.0, pos.getValue().get(2), 0.0);
        assertEquals(40.0, pos.getValue().get(3), 0.0);
        assertEquals(50.0, pos.getValue().get(4), 0.0);
        assertEquals(60.0, pos.getValue().get(5), 0.0);
        assertEquals(10.0, pos.getValue().get(6), 0.0);
        assertEquals(20.0, pos.getValue().get(7), 0.0);

    }
}
