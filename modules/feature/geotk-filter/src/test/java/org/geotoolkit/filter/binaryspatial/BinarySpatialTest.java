/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.binaryspatial;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;

import org.junit.Test;

import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;

import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BinarySpatialTest {

    private final Geometry GEOM_DISTANCE_1;
    private final Geometry GEOM_DISTANCE_3;
    private final Geometry GEOM_INTERSECT;
    private final Geometry GEOM_CONTAINS;
    private final Geometry GEOM_CROSSES;
    private final Geometry GEOM_TOUCHES;
    
    public BinarySpatialTest() {
        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(5, 1);
        coords[1] = new Coordinate(10, 1);
        coords[2] = new Coordinate(10, 4);
        coords[3] = new Coordinate(5, 4);
        coords[4] = new Coordinate(5, 1);
        LinearRing ring = GF.createLinearRing(coords);
        GEOM_DISTANCE_1 = GF.createPolygon(ring, new LinearRing[0]);

        coords = new Coordinate[5];
        coords[0] = new Coordinate(5, -1);
        coords[1] = new Coordinate(10, -1);
        coords[2] = new Coordinate(10, 2);
        coords[3] = new Coordinate(5, 2);
        coords[4] = new Coordinate(5, -1);
        ring = GF.createLinearRing(coords);
        GEOM_DISTANCE_3 = GF.createPolygon(ring, new LinearRing[0]);

        coords = new Coordinate[5];
        coords[0] = new Coordinate(7, 3);
        coords[1] = new Coordinate(9, 3);
        coords[2] = new Coordinate(9, 6);
        coords[3] = new Coordinate(7, 6);
        coords[4] = new Coordinate(7, 3);
        ring = GF.createLinearRing(coords);
        GEOM_INTERSECT = GF.createPolygon(ring, new LinearRing[0]);

        coords = new Coordinate[5];
        coords[0] = new Coordinate(1, 1);
        coords[1] = new Coordinate(11, 1);
        coords[2] = new Coordinate(11, 20);
        coords[3] = new Coordinate(1, 20);
        coords[4] = new Coordinate(1, 1);
        ring = GF.createLinearRing(coords);
        GEOM_CONTAINS = GF.createPolygon(ring, new LinearRing[0]);

        coords = new Coordinate[3];
        coords[0] = new Coordinate(4, 6);
        coords[1] = new Coordinate(7, 8);
        coords[2] = new Coordinate(12, 9);
        GEOM_CROSSES = GF.createLineString(coords);

        coords = new Coordinate[3];
        coords[0] = new Coordinate(4, 2);
        coords[1] = new Coordinate(7, 5);
        coords[2] = new Coordinate(9, 3);
        GEOM_TOUCHES = GF.createLineString(coords);

    }

    @Test
    public void testBBOX() {
        BBOX bbox = FF.bbox("testGeometry", 1, 1, 6, 6, "EPSG:4326");
        assertTrue(bbox.evaluate(FEATURE_1));

        bbox = FF.bbox("testGeometry", -3, -2, 4, 1, "EPSG:4326");
        assertFalse(bbox.evaluate(FEATURE_1));
    }

    @Test
    public void testBeyond() {
        //we can not test units while using jts geometries

        Beyond beyond = FF.beyond(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_1), 1.5d, "foot");
        assertFalse(beyond.evaluate(FEATURE_1));

        beyond = FF.beyond(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_3), 1.5d, "foot");
        assertTrue(beyond.evaluate(FEATURE_1));
    }

    @Test
    public void testContains() {

        Contains contains = FF.contains(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertTrue(contains.evaluate(FEATURE_1));

        contains = FF.contains(FF.literal(GEOM_DISTANCE_1),FF.property("testGeometry"));
        assertFalse(contains.evaluate(FEATURE_1));

    }

    @Test
    public void testCrosses() {

        Crosses crosses = FF.crosses(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertFalse(crosses.evaluate(FEATURE_1));

        crosses = FF.crosses(FF.literal(GEOM_CROSSES),FF.property("testGeometry"));
        assertTrue(crosses.evaluate(FEATURE_1));

        crosses = FF.crosses(FF.literal(GEOM_DISTANCE_1),FF.property("testGeometry"));
        assertFalse(crosses.evaluate(FEATURE_1));

    }

    @Test
    public void testDWithin() {
        //we can not test units while using jts geometries

        DWithin beyond = FF.dwithin(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_1), 1.5d, "foot");
        assertTrue(beyond.evaluate(FEATURE_1));

        beyond = FF.dwithin(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_3), 1.5d, "foot");
        assertFalse(beyond.evaluate(FEATURE_1));
    }

    @Test
    public void testDisjoint() {

        Disjoint disjoint = FF.disjoint(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertFalse(disjoint.evaluate(FEATURE_1));

        disjoint = FF.disjoint(FF.literal(GEOM_CROSSES),FF.property("testGeometry"));
        assertFalse(disjoint.evaluate(FEATURE_1));

        disjoint = FF.disjoint(FF.literal(GEOM_DISTANCE_1),FF.property("testGeometry"));
        assertTrue(disjoint.evaluate(FEATURE_1));

    }

    @Test
    public void testEquals() {

        Equals equal = FF.equal(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertFalse(equal.evaluate(FEATURE_1));

        equal = FF.equal(FF.literal(GEOM_CROSSES),FF.property("testGeometry"));
        assertFalse(equal.evaluate(FEATURE_1));

        equal = FF.equal(FF.literal(GF.createGeometry(RIGHT_GEOMETRY)),FF.property("testGeometry"));
        assertTrue(equal.evaluate(FEATURE_1));

    }

    @Test
    public void testIntersect() {

        Intersects intersect = FF.intersects(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertTrue(intersect.evaluate(FEATURE_1));

        intersect = FF.intersects(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertTrue(intersect.evaluate(FEATURE_1));

        intersect = FF.intersects(FF.literal(GEOM_INTERSECT), FF.property("testGeometry"));
        assertTrue(intersect.evaluate(FEATURE_1));

        intersect = FF.intersects(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(intersect.evaluate(FEATURE_1));

        intersect = FF.intersects(FF.literal(GEOM_DISTANCE_3), FF.property("testGeometry"));
        assertFalse(intersect.evaluate(FEATURE_1));

    }

    @Test
    public void testOverlaps() {

        Overlaps overlaps = FF.overlaps(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertFalse(overlaps.evaluate(FEATURE_1));

        overlaps = FF.overlaps(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(overlaps.evaluate(FEATURE_1));

        overlaps = FF.overlaps(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertFalse(overlaps.evaluate(FEATURE_1));

        overlaps = FF.overlaps(FF.literal(GEOM_INTERSECT), FF.property("testGeometry"));
        assertTrue(overlaps.evaluate(FEATURE_1));

    }

    @Test
    public void testTouches() {

        Touches touches = FF.touches(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertFalse(touches.evaluate(FEATURE_1));

        touches = FF.touches(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertFalse(touches.evaluate(FEATURE_1));

        touches = FF.touches(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(touches.evaluate(FEATURE_1));

        touches = FF.touches(FF.literal(GEOM_TOUCHES), FF.property("testGeometry"));
        assertTrue(touches.evaluate(FEATURE_1));

    }

    @Test
    public void testWithin() {

        Within within = FF.within(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertFalse(within.evaluate(FEATURE_1));

        within = FF.within(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertFalse(within.evaluate(FEATURE_1));

        within = FF.within(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(within.evaluate(FEATURE_1));

        within = FF.within(FF.literal(GEOM_TOUCHES), FF.property("testGeometry"));
        assertFalse(within.evaluate(FEATURE_1));

        within = FF.within(FF.property("testGeometry"), FF.literal(GEOM_CONTAINS) );
        assertTrue(within.evaluate(FEATURE_1));

    }
    
}
