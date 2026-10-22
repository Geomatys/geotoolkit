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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.math.Quat;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.core.DodecahedronQuaternions.quaternions;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/dodecahedron-quaternions.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class DodecahedronQuaternionsTest {

    private static final JsonNode quaternionsFixture = Fixtures.load("fixtures/dodecahedron-quaternions.json");

    private static List<double[]> faceCenters() {
        final double[] northPole = Vec3.fromValues(0, 0, 1);
        final List<double[]> facecenters = new ArrayList<>();
        for (double[] q : quaternions) {
            final double[] rotated = Vec3.create();
            Vec3.transformQuat(rotated, northPole, q);
            facecenters.add(rotated);
        }
        return facecenters;
    }

    // ---- describe: QuaternionArray ----
    @Test
    void has_12_quaternions_for_12_dodecahedron_faces() {
        assertEquals(quaternionsFixture.get("metadata").get("totalQuaternions").asInt(), quaternions.length);
    }

    @Test
    void all_quaternions_are_normalized() {
        assertTrue(quaternionsFixture.get("validationTests").get("allNormalized").asBoolean());
        for (JsonNode data : quaternionsFixture.get("quaternions")) {
            assertCloseTo(1.0, data.get("magnitude").asDouble(), 10);
        }
    }

    @Test
    void first_quaternion_is_identity_north_pole() {
        assertTrue(quaternionsFixture.get("validationTests").get("northPoleIdentity").asBoolean());
        assertArrayEquals(new double[]{0, 0, 0, 1}, quaternions[0]);
    }

    @Test
    void last_quaternion_represents_south_pole_rotation() {
        assertTrue(quaternionsFixture.get("validationTests").get("southPoleCorrect").asBoolean());
        assertArrayEquals(new double[]{0, -1, 0, 0}, quaternions[11]);
    }

    @Test
    void quaternions_are_valid() {
        assertTrue(quaternionsFixture.get("validationTests").get("allFinite").asBoolean());
        for (double[] q : quaternions) {
            assertEquals(4, q.length);
            for (double component : q) {
                assertTrue(Double.isFinite(component));
                assertFalse(Double.isNaN(component));
            }
        }
    }

    // ---- describe: QuaternionProperties ----
    @Test
    void first_ring_quaternions_indices_1_5_have_consistent_structure() {
        for (int i = 1; i <= 5; i++) {
            final double[] q = quaternions[i];
            // Third component should be 0 for first ring
            assertCloseTo(0, q[2], 15);
            // Fourth component should be cosAlpha for first ring
            final double cosAlpha = Math.sqrt((1 + Math.sqrt(0.2)) / 2);
            assertCloseTo(cosAlpha, q[3], 10);
        }
    }

    @Test
    void second_ring_quaternions_indices_6_10_have_consistent_structure() {
        for (int i = 6; i <= 10; i++) {
            final double[] q = quaternions[i];
            // Third component should be 0 for second ring
            assertCloseTo(0, q[2], 15);
            // Fourth component should be sinAlpha for second ring
            final double sinAlpha = Math.sqrt((1 - Math.sqrt(0.2)) / 2);
            assertCloseTo(sinAlpha, q[3], 10);
        }
    }

    @Test
    void quaternions_represent_rotations_around_axes_perpendicular_to_face_centers() {
        // Test that rotating the north pole (0, 0, 1) by each quaternion
        // gives a valid point on the unit sphere
        final double[] northPole = Vec3.fromValues(0, 0, 1);

        for (int i = 0; i < quaternions.length; i++) {
            final double[] rotated = Vec3.create();
            Vec3.transformQuat(rotated, northPole, quaternions[i]);

            // Should be on unit sphere
            final double magnitude = Vec3.length(rotated);
            assertCloseTo(1.0, magnitude, 10);

            // For non-identity quaternions, should be different from north pole
            if (i != 0) {
                final double distance = Vec3.distance(rotated, northPole);
                assertTrue(distance > 0.1);
            }
        }
    }

    @Test
    void quaternions_produce_distinct_face_centers() {
        final List<double[]> facecenters = faceCenters();

        // All face centers should be distinct
        for (int i = 0; i < facecenters.size(); i++) {
            for (int j = i + 1; j < facecenters.size(); j++) {
                final double distance = Vec3.distance(facecenters.get(i), facecenters.get(j));
                assertTrue(distance > 0.1);
            }
        }
    }

    // ---- describe: MathematicalConsistency ----
    @Test
    void quaternion_conjugates_reverse_rotations() {
        final double[] testVector = Vec3.fromValues(1, 0, 0);

        for (double[] q : quaternions) {
            final double[] conjugate = Quat.create();
            Quat.conjugate(conjugate, q);

            final double[] rotated = Vec3.create();
            final double[] backRotated = Vec3.create();

            Vec3.transformQuat(rotated, testVector, q);
            Vec3.transformQuat(backRotated, rotated, conjugate);

            assertCloseTo(0, Vec3.distance(testVector, backRotated), 10);
        }
    }

    @Test
    void quaternions_maintain_orthogonality() {
        // Test that quaternion rotations preserve orthogonal vectors
        final double[] v1 = Vec3.fromValues(1, 0, 0);
        final double[] v2 = Vec3.fromValues(0, 1, 0);

        for (double[] q : quaternions) {
            final double[] rotated1 = Vec3.create();
            final double[] rotated2 = Vec3.create();

            Vec3.transformQuat(rotated1, v1, q);
            Vec3.transformQuat(rotated2, v2, q);

            final double dotProduct = Vec3.dot(rotated1, rotated2);
            assertCloseTo(0, dotProduct, 10);
        }
    }

    @Test
    void quaternions_preserve_vector_magnitudes() {
        final double[][] testVectors = {
            Vec3.fromValues(1, 0, 0),
            Vec3.fromValues(0, 1, 0),
            Vec3.fromValues(0, 0, 1),
            Vec3.fromValues(1, 1, 1)
        };

        for (double[] q : quaternions) {
            for (double[] v : testVectors) {
                final double originalLength = Vec3.length(v);
                final double[] rotated = Vec3.create();
                Vec3.transformQuat(rotated, v, q);
                final double newLength = Vec3.length(rotated);

                assertCloseTo(originalLength, newLength, 10);
            }
        }
    }

    // ---- describe: DodecahedronGeometry ----
    @Test
    void face_centers_have_correct_distribution() {
        // Generate all face centers by rotating north pole
        final List<double[]> facecenters = faceCenters();

        // Check that we have correct z-distribution:
        // 1 at z=1 (north), 1 at z=-1 (south), 5 at each intermediate level
        final double[] zValues = new double[facecenters.size()];
        for (int i = 0; i < zValues.length; i++) zValues[i] = facecenters.get(i)[2];
        // sort descending
        Arrays.sort(zValues);
        for (int i = 0, j = zValues.length - 1; i < j; i++, j--) {
            final double tmp = zValues[i];
            zValues[i] = zValues[j];
            zValues[j] = tmp;
        }

        assertCloseTo(1, zValues[0], 10); // North pole
        assertCloseTo(-1, zValues[11], 10); // South pole

        // Two rings of 5 each at intermediate z values
        final double INV_SQRT5 = Math.sqrt(0.2);
        for (int i = 1; i < 6; i++) assertCloseTo(INV_SQRT5, zValues[i], 5);
        for (int i = 6; i < 11; i++) assertCloseTo(-INV_SQRT5, zValues[i], 5);
    }

    @Test
    void face_centers_form_regular_pentagonal_arrangements() {
        final List<double[]> facecenters = faceCenters();

        // Check angular distribution for first ring (indices 1-5)
        final List<double[]> firstRing = facecenters.subList(1, 6);
        for (int i = 0; i < 5; i++) {
            final int next = (i + 1) % 5;
            final double angle1 = Math.atan2(firstRing.get(i)[1], firstRing.get(i)[0]);
            final double angle2 = Math.atan2(firstRing.get(next)[1], firstRing.get(next)[0]);
            double angleDiff = angle2 - angle1;
            if (angleDiff < 0) angleDiff += 2 * Math.PI;
            if (angleDiff > Math.PI) angleDiff = 2 * Math.PI - angleDiff;

            // Should be approximately 2π/5 = 72 degrees
            assertCloseTo((2 * Math.PI) / 5, angleDiff, 1);
        }
    }

    // ---- describe: FixtureValidation ----
    @Test
    void fixture_metadata_is_current() {
        assertEquals(12, quaternionsFixture.get("metadata").get("totalQuaternions").asInt());
        assertCloseTo(Math.sqrt(0.2), quaternionsFixture.get("constants").get("INV_SQRT5").asDouble(), 15);
        assertCloseTo((2 * Math.PI) / 5, quaternionsFixture.get("constants").get("expectedPentagonAngle").asDouble(), 15);
    }
}
