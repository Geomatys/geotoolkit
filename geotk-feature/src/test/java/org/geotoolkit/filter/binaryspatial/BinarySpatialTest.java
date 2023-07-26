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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;

import org.junit.Test;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.opengis.filter.Filter;
import org.opengis.filter.ValueReference;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
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
        Filter bbox = FF.bbox("testGeometry", 1, 1, 6, 6, "EPSG:4326");
        assertTrue(bbox.test(CANDIDATE_1));

        bbox = FF.bbox("testGeometry", -3, -2, 4, 1, "EPSG:4326");
        assertFalse(bbox.test(CANDIDATE_1));
    }

    @Test
    public void testBeyond() {
        //we can not test units while using jts geometries

        Filter beyond = FF.beyond(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_1), 1.5d, "m");
        assertFalse(beyond.test(CANDIDATE_1));

        beyond = FF.beyond(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_3), 1.5d, "m");
        assertTrue(beyond.test(CANDIDATE_1));
    }

    @Test
    public void testContains() {
        Filter contains = FF.contains(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertTrue(contains.test(CANDIDATE_1));

        contains = FF.contains(FF.literal(GEOM_DISTANCE_1),FF.property("testGeometry"));
        assertFalse(contains.test(CANDIDATE_1));

    }

    @Test
    public void testCrosses() {
        Filter crosses = FF.crosses(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertFalse(crosses.test(CANDIDATE_1));

        crosses = FF.crosses(FF.literal(GEOM_CROSSES),FF.property("testGeometry"));
        assertTrue(crosses.test(CANDIDATE_1));

        crosses = FF.crosses(FF.literal(GEOM_DISTANCE_1),FF.property("testGeometry"));
        assertFalse(crosses.test(CANDIDATE_1));
    }

    @Test
    public void testDWithin() {
        //we can not test units while using jts geometries

        Filter within = FF.dwithin(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_1), 1.5d, "m");
        assertTrue(within.test(CANDIDATE_1));

        within = FF.dwithin(FF.property("testGeometry"), FF.literal(GEOM_DISTANCE_3), 1.5d, "m");
        assertFalse(within.test(CANDIDATE_1));
    }

    @Test
    public void testDisjoint() {
        Filter disjoint = FF.disjoint(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertFalse(disjoint.test(CANDIDATE_1));

        disjoint = FF.disjoint(FF.literal(GEOM_CROSSES),FF.property("testGeometry"));
        assertFalse(disjoint.test(CANDIDATE_1));

        disjoint = FF.disjoint(FF.literal(GEOM_DISTANCE_1),FF.property("testGeometry"));
        assertTrue(disjoint.test(CANDIDATE_1));
    }

    @Test
    public void testEquals() {
        Filter equal = FF.equals(FF.literal(GEOM_CONTAINS),FF.property("testGeometry"));
        assertFalse(equal.test(CANDIDATE_1));

        equal = FF.equals(FF.literal(GEOM_CROSSES),FF.property("testGeometry"));
        assertFalse(equal.test(CANDIDATE_1));

        equal = FF.equals(FF.literal(GF.createGeometry(RIGHT_GEOMETRY)),FF.property("testGeometry"));
        assertTrue(equal.test(CANDIDATE_1));
    }

    @Test
    public void testIntersect() {
        Filter intersect = FF.intersects(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertTrue(intersect.test(CANDIDATE_1));

        intersect = FF.intersects(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertTrue(intersect.test(CANDIDATE_1));

        intersect = FF.intersects(FF.literal(GEOM_INTERSECT), FF.property("testGeometry"));
        assertTrue(intersect.test(CANDIDATE_1));

        intersect = FF.intersects(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(intersect.test(CANDIDATE_1));

        intersect = FF.intersects(FF.literal(GEOM_DISTANCE_3), FF.property("testGeometry"));
        assertFalse(intersect.test(CANDIDATE_1));
    }

    @Test
    public void testOverlaps() {
        Filter overlaps = FF.overlaps(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertFalse(overlaps.test(CANDIDATE_1));

        overlaps = FF.overlaps(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(overlaps.test(CANDIDATE_1));

        overlaps = FF.overlaps(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertFalse(overlaps.test(CANDIDATE_1));

        overlaps = FF.overlaps(FF.literal(GEOM_INTERSECT), FF.property("testGeometry"));
        assertTrue(overlaps.test(CANDIDATE_1));
    }

    @Test
    public void testTouches() {
        Filter touches = FF.touches(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertFalse(touches.test(CANDIDATE_1));

        touches = FF.touches(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertFalse(touches.test(CANDIDATE_1));

        touches = FF.touches(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(touches.test(CANDIDATE_1));

        touches = FF.touches(FF.literal(GEOM_TOUCHES), FF.property("testGeometry"));
        assertTrue(touches.test(CANDIDATE_1));
    }

    @Test
    public void testWithin() {
        Filter within = FF.within(FF.literal(GEOM_CONTAINS), FF.property("testGeometry"));
        assertFalse(within.test(CANDIDATE_1));

        within = FF.within(FF.literal(GEOM_CROSSES), FF.property("testGeometry"));
        assertFalse(within.test(CANDIDATE_1));

        within = FF.within(FF.literal(GEOM_DISTANCE_1), FF.property("testGeometry"));
        assertFalse(within.test(CANDIDATE_1));

        within = FF.within(FF.literal(GEOM_TOUCHES), FF.property("testGeometry"));
        assertFalse(within.test(CANDIDATE_1));

        within = FF.within(FF.property("testGeometry"), FF.literal(GEOM_CONTAINS) );
        assertTrue(within.test(CANDIDATE_1));
    }

    /**
     * We want to be sure that geometries created with ESRI or an unknown CRS authority (one without factory) does not
     * break bbox testing.
     *
     * Note: This test has been copied from Apache SIS, and can be removed once BBOX filter has been fully replaced with
     * SIS implementation.
     */
    @Test
    public void bbox_filter_does_not_fail_on_esri_crs() throws Exception {
        final Coordinate[] coords = new Coordinate[3];
        coords[0] = new Coordinate(4, 2);
        coords[1] = new Coordinate(7, 5);
        coords[2] = new Coordinate(9, 3);
        final Geometry esriGeom = GF.createLineString(coords);
        final String crsWkt;
        try (
                InputStream is = BinarySpatialTest.class.getResourceAsStream("/io/wkt/extra_ESRI_CRS.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
        ) {
            crsWkt = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        final CoordinateReferenceSystem esriCRS = CRS.fromWKT(crsWkt);
        esriGeom.setUserData(esriCRS);
        esriGeom.setSRID(Integer.parseInt(esriCRS.getIdentifiers().iterator().next().getCode()));
        Envelope2D envelope = new Envelope2D(esriCRS, 0, 0, 10, 10);

        final ValueReference property = FF.property("geometry");
        final Map<String, Geometry> geometry = Collections.singletonMap(property.getXPath(), esriGeom);

        Filter filter = FF.bbox(property, envelope);
        assertTrue(filter.test(geometry));

        // Ensure no error is raised, even if a reprojection is involved
        envelope = new Envelope2D(CommonCRS.defaultGeographic(), 0, 0, 10, 10);
        filter = FF.bbox(property, envelope);
        filter.test(geometry);
    }
}
