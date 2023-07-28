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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test JTS utility class
 * @author Quentin Boileau
 * @module
 */
public class JTSTest {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

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
        final Geometry returnedGeom = JTS.ensureWinding(poly, false);
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

        final Geometry returnedGeom = JTS.ensureWinding(poly, true);

        assertFalse(Orientation.isCCW(returnedGeom.getCoordinates()));

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

    @Test
    public void testFromAwt() {

        { // test line segment
            final Line2D shape = new Line2D.Double(1, 2, 3, 4);
            final Geometry geometry = JTS.fromAwt(GF, shape, 0.1);
            assertTrue(geometry instanceof LineString);
            final LineString ls = (LineString) geometry;
            final Coordinate[] coordinates = ls.getCoordinates();
            assertEquals(2, coordinates.length);
            assertEquals(new Coordinate(1,2), coordinates[0]);
            assertEquals(new Coordinate(3,4), coordinates[1]);
        }

        { // test rectangle
            final Rectangle2D shape = new Rectangle2D.Double(1,2,10,20);
            final Geometry geometry = JTS.fromAwt(GF, shape, 0.1);
            assertTrue(geometry instanceof Polygon);
            final Polygon ls = (Polygon) geometry;
            final Coordinate[] coordinates = ls.getCoordinates();
            assertEquals(5, coordinates.length);
            assertEquals(new Coordinate(1,2), coordinates[0]);
            assertEquals(new Coordinate(11,2), coordinates[1]);
            assertEquals(new Coordinate(11,22), coordinates[2]);
            assertEquals(new Coordinate(1,22), coordinates[3]);
            assertEquals(new Coordinate(1,2), coordinates[4]);
        }

        { // test rectangle with hole
            final Rectangle2D contour = new Rectangle2D.Double(1,2,10,20);
            final Rectangle2D hole = new Rectangle2D.Double(5,6,2,3);
            final Area shape = new Area(contour);
            shape.subtract(new Area(hole));
            final Geometry geometry = JTS.fromAwt(GF, shape, 0.1);
            assertTrue(geometry instanceof Polygon);
            final Polygon ls = (Polygon) geometry;
            final LinearRing exteriorRing = ls.getExteriorRing();
            assertEquals(1, ls.getNumInteriorRing());
            final LinearRing interiorRing = ls.getInteriorRingN(0);

            final Coordinate[] coordinatesExt = exteriorRing.getCoordinates();
            assertEquals(5, coordinatesExt.length);
            assertEquals(new Coordinate(1,2), coordinatesExt[0]);
            assertEquals(new Coordinate(1,22), coordinatesExt[1]);
            assertEquals(new Coordinate(11,22), coordinatesExt[2]);
            assertEquals(new Coordinate(11,2), coordinatesExt[3]);
            assertEquals(new Coordinate(1,2), coordinatesExt[4]);

            final Coordinate[] coordinatesInt = interiorRing.getCoordinates();
            assertEquals(5, coordinatesInt.length);
            assertEquals(new Coordinate(7,6), coordinatesInt[0]);
            assertEquals(new Coordinate(7,9), coordinatesInt[1]);
            assertEquals(new Coordinate(5,9), coordinatesInt[2]);
            assertEquals(new Coordinate(5,6), coordinatesInt[3]);
            assertEquals(new Coordinate(7,6), coordinatesInt[4]);
        }

        { // test a text
            final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = img.createGraphics();
            final FontRenderContext fontRenderContext = g.getFontRenderContext();
            final Font font = new Font("Monospaced", Font.PLAIN, 12);
            final GlyphVector glyphs = font.createGlyphVector(fontRenderContext, "Labi");
            final Shape shape = glyphs.getOutline();
            final GeneralPath gp = new GeneralPath();
            gp.append(shape.getPathIterator(null, 0.1), false);
            final Rectangle2D bounds2D = gp.getBounds2D();

            final Geometry geometry = JTS.fromAwt(GF, shape, 0.1);
            assertTrue(geometry instanceof MultiPolygon);
            final MultiPolygon mp = (MultiPolygon) geometry;
            assertEquals(5, mp.getNumGeometries()); //4 characters but 'i' is split in two ploygons
            Geometry l = mp.getGeometryN(0);
            Geometry a = mp.getGeometryN(1);
            Geometry b = mp.getGeometryN(2);
            Geometry i0 = mp.getGeometryN(3);
            Geometry i1 = mp.getGeometryN(4);
            assertTrue(l instanceof Polygon);
            assertTrue(a instanceof Polygon);
            assertTrue(b instanceof Polygon);
            assertTrue(i0 instanceof Polygon);
            assertTrue(i1 instanceof Polygon);
            //a must contain a hole
            assertEquals(1, ((Polygon) a).getNumInteriorRing());

            //check bounding box
            final Envelope env = geometry.getEnvelopeInternal();
            assertEquals(bounds2D.getMinX(), env.getMinX(), 0.0);
            assertEquals(bounds2D.getMaxX(), env.getMaxX(), 0.0);
            assertEquals(bounds2D.getMinY(), env.getMinY(), 0.0);
            assertEquals(bounds2D.getMaxY(), env.getMaxY(), 0.0);
        }

    }
}
