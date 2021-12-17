/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.geometry.jts.coordinatesequence;

import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Tests for GridAlignedFilter.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GridAlignedFilterTest {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    @Test
    public void testPoint() {

        final Point geometry = GF.createPoint(new Coordinate(0.6, 1.4));

        geometry.apply(new GridAlignedFilter(0.5, -0.5, 1, 1));

        assertEquals(0.5, geometry.getX(), 0.0);
        assertEquals(1.5, geometry.getY(), 0.0);
    }

    @Test
    public void testLineString() {

        final LineString geometry = GF.createLineString(new Coordinate[]{
            new Coordinate(0.6, 1.4),
            new Coordinate(-2.3, -7.8),
            new Coordinate(6.1, 8.1),
        });

        geometry.apply(new GridAlignedFilter(0.5, -0.5, 1, 1));

        Coordinate c0 = geometry.getCoordinateN(0);
        Coordinate c1 = geometry.getCoordinateN(1);
        Coordinate c2 = geometry.getCoordinateN(2);

        assertEquals(0.5, c0.x, 0.0);
        assertEquals(1.5, c0.y, 0.0);
        assertEquals(-2.5, c1.x, 0.0);
        assertEquals(-7.5, c1.y, 0.0);
        assertEquals(6.5, c2.x, 0.0);
        assertEquals(8.5, c2.y, 0.0);
    }

    /**
     * Null geometry must remain unchanged.
     */
    @Test
    public void testNullAlignAndSimplify() {
        final Geometry geometry = null;
        final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
        {//without empty flag
            final Geometry result = filter.alignAndSimplify(geometry, false);
            assertEquals(geometry, result);
        }
        {//with empty flag
            final Geometry result = filter.alignAndSimplify(geometry, true);
            assertEquals(geometry, result);
        }

    }

    /**
     * Empty geometry must remain unchanged.
     */
    @Test
    public void testEmptyAlignAndSimplify() {
        final Geometry geometry = GF.createEmpty(2);
        final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
        {//without empty flag
            final Geometry result = filter.alignAndSimplify(geometry, false);
            assertEquals(geometry, result);
        }
        {//with empty flag
            final Geometry result = filter.alignAndSimplify(geometry, true);
            assertEquals(geometry, result);
        }
    }

    /**
     * Point geometry must remain unchanged.
     */
    @Test
    public void testPointAlignAndSimplify() {
        final Point geometry = GF.createPoint(new Coordinate(1.2, 5.6));
        final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
        {//without empty flag
            final Geometry result = filter.alignAndSimplify(geometry, false);
            assertEquals(GF.createPoint(new Coordinate(1,6)), result);
        }
        {//with empty flag
            final Geometry result = filter.alignAndSimplify(geometry, true);
            assertEquals(GF.createPoint(new Coordinate(1,6)), result);
        }
    }

    /**
     * MultiPoint geometry must be simplified to a single point
     *
     * In this test first and second point degenerate to the same point,
     * only one of them must remain.
     */
    @Test
    public void testMultiPointAlignAndSimplify() {
        final MultiPoint geometry = GF.createMultiPointFromCoords(new Coordinate[]{new Coordinate(1.2, 5.6),new Coordinate(1.4, 6.3), new Coordinate(-1.6,-8)});
        final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
        {//without empty flag
            final Geometry result = filter.alignAndSimplify(geometry, false);
            assertEquals(GF.createMultiPointFromCoords(new Coordinate[]{new Coordinate(1,6), new Coordinate(-2,-8)}), result);
        }
        {//with empty flag
            final Geometry result = filter.alignAndSimplify(geometry, true);
            assertEquals(GF.createMultiPointFromCoords(new Coordinate[]{new Coordinate(1,6), new Coordinate(-2,-8)}), result);
        }
    }

    /**
     * LineString geometry tests.
     */
    @Test
    public void testLineStringAlignAndSimplify() {
        {// geometry degenerate to a point
            final LineString geometry = GF.createLineString(new Coordinate[]{new Coordinate(1.2, 5.6), new Coordinate(1.4, 6.3)});
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(GF.createLineString(new Coordinate[]{new Coordinate(1,6), new Coordinate(1,6)}), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(GF.createLineString(), result);
            }
        }
        {// geometry gets simplified
            final LineString geometry = GF.createLineString(new Coordinate[]{new Coordinate(1.2, 5.6),new Coordinate(1.4, 6.3), new Coordinate(-1.6,-8)});
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(GF.createLineString(new Coordinate[]{new Coordinate(1,6), new Coordinate(-2,-8)}), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(GF.createLineString(new Coordinate[]{new Coordinate(1,6), new Coordinate(-2,-8)}), result);
            }
        }
    }

    /**
     * MultiLineString geometry tests.
     */
    @Test
    public void testMultiLineStringAlignAndSimplify() {
        {// geometry degenerate to a point
            final MultiLineString geometry = GF.createMultiLineString(new LineString[]{
                    GF.createLineString(),
                    GF.createLineString(new Coordinate[]{new Coordinate(1.2, 5.6), new Coordinate(1.4, 6.3)})
            });
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(
                    GF.createMultiLineString(new LineString[]{
                        GF.createLineString(new Coordinate[]{new Coordinate(1, 6), new Coordinate(1, 6)})
                    }), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(GF.createMultiLineString(), result);
            }
        }
        {// geometry gets simplified
            final MultiLineString geometry = GF.createMultiLineString(new LineString[]{
                    GF.createLineString(),
                    GF.createLineString(new Coordinate[]{new Coordinate(1.2, 5.6),new Coordinate(1.4, 6.3), new Coordinate(-1.6,-8)})
            });
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(
                    GF.createMultiLineString(new LineString[]{
                        GF.createLineString(new Coordinate[]{new Coordinate(1, 6), new Coordinate(-2, -8)})
                    }), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(
                    GF.createMultiLineString(new LineString[]{
                        GF.createLineString(new Coordinate[]{new Coordinate(1, 6), new Coordinate(-2, -8)})
                    }), result);
            }
        }
    }

    /**
     * Polygon geometry tests.
     */
    @Test
    public void testPolygonAlignAndSimplify() throws ParseException {
        {// geometry degenerate to a point
            final Polygon geometry = fromWkt("POLYGON((1.2 5.6, 1.4 6.3, 1.4 6.3, 1.2 5.6))");
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(fromWkt("POLYGON EMPTY"), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(fromWkt("POLYGON EMPTY"), result);
            }
        }
        {// geometry gets simplified
            final Polygon geometry = fromWkt("POLYGON((1.2 5.6, 1.4 6.3, 3.4 6.3, -1.6 -8, 1.2 5.6))");
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(fromWkt("POLYGON((1 6, 3 6, -2 -8, 1 6))"), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(fromWkt("POLYGON((1 6, 3 6, -2 -8, 1 6))"), result);
            }
        }
        {// geometry holes get removed
            final Polygon geometry = fromWkt("POLYGON((0.1 0.2, 10.5 0.1, 10.4 10.3, 0.4 10.1, 0.1 0.2), EMPTY, (2.1 7.1, 4.4 7.2, 3.6 3.3, 2.9 2.8, 3.4 3.3, 2.1 7.1), (4.3 3.1, 3.6 2.7, 3.6 3.1, 4.3 3.1))");
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(fromWkt("POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0), (2 7, 3 3, 4 3, 4 7, 2 7))"), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(fromWkt("POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0), (2 7, 3 3, 4 3, 4 7, 2 7))"), result);
            }
        }
    }

    /**
     * MultiPolygon geometry tests.
     */
    @Test
    public void testMultiPolygonAlignAndSimplify() throws ParseException {
        {// geometry degenerate to a point
            final MultiPolygon geometry = fromWkt("MULTIPOLYGON(((1.2 5.6, 1.4 6.3, 1.4 6.3, 1.2 5.6)))");
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(fromWkt("MULTIPOLYGON EMPTY"), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(fromWkt("MULTIPOLYGON EMPTY"), result);
            }
        }
        {// geometry gets simplified
            final MultiPolygon geometry = fromWkt("MULTIPOLYGON(((1.2 5.6, 1.4 6.3, 3.4 6.3, -1.6 -8, 1.2 5.6)), EMPTY)");
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(fromWkt("MULTIPOLYGON(((1 6, 3 6, -2 -8, 1 6)))"), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(fromWkt("MULTIPOLYGON(((1 6, 3 6, -2 -8, 1 6)))"), result);
            }
        }
        {// geometry holes get removed
            final MultiPolygon geometry = fromWkt("MULTIPOLYGON(EMPTY,((0.1 0.2, 10.5 0.1, 10.4 10.3, 0.4 10.1, 0.1 0.2), EMPTY, (2.1 7.1, 4.4 7.2, 3.6 3.3, 2.9 2.8, 3.4 3.3, 2.1 7.1), (4.3 3.1, 3.6 2.7, 3.6 3.1, 4.3 3.1)), ((1.2 5.6, 1.4 6.3, 3.4 6.3, -1.6 -8, 1.2 5.6)))");
            final GridAlignedFilter filter = new GridAlignedFilter(0,0,1,1);
            {//without empty flag
                final Geometry result = filter.alignAndSimplify(geometry, false);
                assertEquals(fromWkt("MULTIPOLYGON (((0 0, 0 10, 10 10, 10 0, 0 0), (2 7, 3 3, 4 3, 4 7, 2 7)), ((1 6, 3 6, -2 -8, 1 6)))"), result);
            }
            {//with empty flag
                final Geometry result = filter.alignAndSimplify(geometry, true);
                assertEquals(fromWkt("MULTIPOLYGON (((0 0, 0 10, 10 10, 10 0, 0 0), (2 7, 3 3, 4 3, 4 7, 2 7)), ((1 6, 3 6, -2 -8, 1 6)))"), result);
            }
        }
    }

    private static <T extends Geometry> T fromWkt(String wkt) throws ParseException {
        return (T) new WKTReader().read(wkt);
    }
}
