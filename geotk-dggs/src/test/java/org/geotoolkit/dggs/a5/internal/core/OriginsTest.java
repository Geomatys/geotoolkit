/**
 * Copyright (C) 2026 Geomatys and A5 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.dggs.a5.internal.core;

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.core.Constants.PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.Origins.findNearestOrigin;
import static org.geotoolkit.dggs.a5.internal.core.Origins.haversine;
import static org.geotoolkit.dggs.a5.internal.core.Origins.isNearestOrigin;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Origins.quintantToSegment;
import static org.geotoolkit.dggs.a5.internal.core.Origins.segmentToQuintant;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/origin.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class OriginsTest {

    // ---- describe: origin constants ----
    @Test
    void has_12_origins_for_dodecahedron_faces() {
        assertEquals(12, origins.size());
    }

    @Test
    void should_match_expected_origins_from_fixture() {
        final JsonNode expectedOrigins = Fixtures.load("fixtures/origins.json");
        assertEquals(expectedOrigins.size(), origins.size());

        for (int index = 0; index < origins.size(); index++) {
            final Origin origin = origins.get(index);
            final JsonNode expected = expectedOrigins.get(index);

            // Check id
            assertEquals(expected.get("id").asInt(), origin.id());

            // Check axis (spherical coordinates)
            assertCloseToArray(Fixtures.doubles(expected.get("axis")), origin.axis());

            // Check quaternion
            assertCloseToArray(Fixtures.doubles(expected.get("quat")), origin.quat());

            // Check angle
            assertCloseTo(expected.get("angle").asDouble(), origin.angle());

            // Check orientation array
            final JsonNode orientation = expected.get("orientation");
            assertEquals(orientation.size(), origin.orientation().length);
            for (int i = 0; i < orientation.size(); i++) {
                assertEquals(Orientation.valueOf(orientation.get(i).asText()), origin.orientation()[i]);
            }

            // Check firstQuintant
            assertEquals(expected.get("firstQuintant").asInt(), origin.firstQuintant());
        }
    }

    // ---- describe: origin properties ----
    @Test
    void each_origin_has_required_properties() {
        for (final Origin origin : origins) {
            // Check properties exist
            assertNotNull(origin.axis());
            assertNotNull(origin.quat());

            // Check axis is unit vector when converted to cartesian
            final double[] cartesian = toCartesian(origin.axis());
            final double length = Vec3.length(cartesian);
            assertCloseTo(1, length);

            // Check quaternion is normalized
            final double[] q = origin.quat();
            final double qLength = Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);
            assertCloseTo(1, qLength);
        }
    }

    // ---- describe: findNearestOrigin ----
    @Test
    void finds_correct_origin_for_points_at_face_centers() {
        for (final Origin origin : origins) {
            final double[] point = origin.axis();
            final Origin nearest = findNearestOrigin(point);
            assertSame(origin, nearest);
        }
    }

    @Test
    void finds_correct_origin_for_points_at_face_boundaries() {
        // Test points halfway between adjacent origins
        final double[][] points = {
            // Between north pole and equatorial faces
            {0, PI_OVER_5 / 2},
            // Between equatorial faces
            {2 * PI_OVER_5, PI_OVER_5},
            // Between equatorial and south pole
            {0, Math.PI - PI_OVER_5 / 2}
        };
        final int[][] expectedOrigins = {{0, 1}, {3, 4}, {9, 10}};

        for (int i = 0; i < points.length; i++) {
            final Origin nearest = findNearestOrigin(points[i]);
            final int id = nearest.id();
            assertTrue(expectedOrigins[i][0] == id || expectedOrigins[i][1] == id, "nearest " + id);
        }
    }

    @Test
    void handles_antipodal_points() {
        // Test points opposite to face centers
        for (final Origin origin : origins) {
            final double theta = origin.axis()[0], phi = origin.axis()[1];
            // Add π to theta and π-phi to get antipodal point
            final double[] antipodal = {theta + Math.PI, Math.PI - phi};

            final Origin nearest = findNearestOrigin(antipodal);
            // Should find one of the faces near the antipodal point
            assertNotSame(origin, nearest);
        }
    }

    // ---- describe: haversine ----
    @Test
    void returns_0_for_identical_points() {
        final double[] point = {0, 0};
        assertEquals(0.0, haversine(point, point));

        final double[] point2 = {Math.PI / 4, Math.PI / 3};
        assertEquals(0.0, haversine(point2, point2));
    }

    @Test
    void is_symmetric() {
        final double[] p1 = {0, Math.PI / 4};
        final double[] p2 = {Math.PI / 2, Math.PI / 3};

        final double d1 = haversine(p1, p2);
        final double d2 = haversine(p2, p1);

        assertCloseTo(d2, d1);
    }

    @Test
    void increases_with_angular_separation() {
        final double[] origin = {0, 0};
        final double[][] distances = {
            {0, Math.PI / 6}, // 30°
            {0, Math.PI / 4}, // 45°
            {0, Math.PI / 3}, // 60°
            {0, Math.PI / 2} // 90°
        };

        double lastDistance = 0;
        for (final double[] point : distances) {
            final double distance = haversine(origin, point);
            assertTrue(distance > lastDistance);
            lastDistance = distance;
        }
    }

    @Test
    void handles_longitude_separation() {
        final double lat = Math.PI / 4; // Fixed latitude
        final double[] p1 = {0, lat};
        final double[] p2 = {Math.PI, lat};
        final double[] p3 = {Math.PI / 2, lat};

        final double d1 = haversine(p1, p2); // 180° separation
        final double d2 = haversine(p1, p3); // 90° separation

        assertTrue(d1 > d2);
    }

    @Test
    void matches_expected_values_for_known_cases() {
        // Test against some pre-calculated values
        assertCloseTo(0.5, haversine(new double[]{0, 0}, new double[]{0, Math.PI / 2}), 4); // sin²(π/4) = 0.5
        assertCloseTo(0.25, haversine(new double[]{0, Math.PI / 4}, new double[]{Math.PI / 2, Math.PI / 4}), 4); // For points at same latitude
    }

    // ---- describe: quintant conversion ----
    @Test
    void converts_between_quintants_and_segments() {
        final Origin origin = origins.get(0);
        for (int quintant = 0; quintant < 5; quintant++) {
            final Origins.SegmentOrientation so = quintantToSegment(quintant, origin);
            final int roundTripQuintant = segmentToQuintant(so.segment(), origin).quintant();
            assertEquals(quintant, roundTripQuintant);
        }
    }

    // ---- describe: isNearestOrigin ----
    @Test
    void returns_true_for_points_at_face_centers() {
        for (final Origin origin : origins) {
            final Origin nearest = findNearestOrigin(origin.axis());
            assertSame(origin, nearest);
        }
    }

    @Test
    void returns_false_for_points_at_face_boundaries() {
        // Test points halfway between adjacent origins
        final double[][] points = {
            // Between north pole and equatorial faces
            {0, PI_OVER_5 / 2},
            // Between equatorial faces
            {2 * PI_OVER_5, PI_OVER_5},
            // Between equatorial and south pole
            {0, Math.PI - PI_OVER_5 / 2}
        };
        final Origin[] boundaryOrigins = {origins.get(0), origins.get(3), origins.get(9)};

        for (int i = 0; i < points.length; i++) {
            assertFalse(isNearestOrigin(points[i], boundaryOrigins[i]));
        }
    }
}
