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

import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.barycentricToFace;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.degToRad;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.faceToBarycentric;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.fromLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.normalizeLongitudes;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.radToDeg;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toSpherical;
import static org.geotoolkit.dggs.a5.internal.projections.EqualAreaTestData.TEST_POINTS;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/coordinate-transforms.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CoordinateTransformsTest {

    private static final double[][] TEST_POINTS_LONLAT = {
        {0, 0}, // Equator
        {90, 0}, // Equator
        {180, 0}, // Equator
        {0, 45}, // Mid latitude
        {0, -45}, // Mid latitude
        {-90, -45}, // West hemisphere mid-latitude
        {180, 45}, // Date line mid-latitude
        {90, 45}, // East hemisphere mid-latitude
        {0, 90}, // North pole
        {0, -90}, // South pole
        {123, 45} // Arbitrary point
    };

    // Test triangle for barycentric tests
    private static final double[][] TEST_TRIANGLE = {
        {0, 0},
        {1, 0},
        {0, 1}
    };

    // ---- describe: angle conversions ----
    @Test
    void converts_degrees_to_radians() {
        assertEquals(Math.PI, degToRad(180));
        assertEquals(Math.PI / 2, degToRad(90));
        assertEquals(0.0, degToRad(0));
    }

    @Test
    void converts_radians_to_degrees() {
        assertEquals(180.0, radToDeg(Math.PI));
        assertEquals(90.0, radToDeg(Math.PI / 2));
        assertEquals(0.0, radToDeg(0));
    }

    // ---- describe: barycentric coordinate functions ----
    @Test
    void faceToBarycentric_and_barycentricToFace_round_trip_preserves_coordinates() {
        final int TOLERANCE = 12;
        for (final double[] point : TEST_POINTS) {
            // Convert to barycentric coordinates
            final double[] bary = faceToBarycentric(point, TEST_TRIANGLE);

            // Convert back to face coordinates
            final double[] result = barycentricToFace(bary, TEST_TRIANGLE);

            // Check round-trip accuracy
            assertCloseToArray(point, result, TOLERANCE);

            // Check that barycentric coordinates sum to 1
            assertCloseTo(1, bary[0] + bary[1] + bary[2], TOLERANCE);

            // Check that all barycentric coordinates are non-negative (point is inside triangle)
            assertTrue(bary[0] >= 0);
            assertTrue(bary[1] >= 0);
            assertTrue(bary[2] >= 0);
        }
    }

    @Test
    void barycentricToFace_and_faceToBarycentric_round_trip_preserves_barycentric_coordinates() {
        // Test barycentric coordinates starting with the specific case
        final double[][] testBaryCoords = {
            {0.043821975867140296, 0.9561208684797726, 0.00005715565308705983},
            {0.5, 0.3, 0.2},
            {0.1, 0.8, 0.1},
            {0.33, 0.33, 0.34},
            {0.9, 0.05, 0.05},
            {0.001, 0.999, 0.0}
        };

        for (final double[] bary : testBaryCoords) {
            // Convert barycentric to face coordinates
            final double[] face = barycentricToFace(bary, TEST_TRIANGLE);

            // Convert back to barycentric
            final double[] resultBary = faceToBarycentric(face, TEST_TRIANGLE);

            // Check round-trip accuracy
            assertCloseToArray(bary, resultBary, 12);

            // Check that barycentric coordinates sum to 1
            assertCloseTo(1, resultBary[0] + resultBary[1] + resultBary[2], 12);
        }
    }

    @Test
    void handles_triangle_vertices_correctly() {
        // Test each vertex
        final double[][] vertices = {TEST_TRIANGLE[0], TEST_TRIANGLE[1], TEST_TRIANGLE[2]};
        final double[][] expectedBary = {
            {1, 0, 0}, // pai -> [1, 0, 0]
            {0, 1, 0}, // pbi -> [0, 1, 0]
            {0, 0, 1} // pci -> [0, 0, 1]
        };

        for (int i = 0; i < vertices.length; i++) {
            final double[] bary = faceToBarycentric(vertices[i], TEST_TRIANGLE);

            // Check barycentric coordinates
            assertCloseToArray(expectedBary[i], bary, 12);

            // Round-trip test
            final double[] result = barycentricToFace(bary, TEST_TRIANGLE);
            assertCloseToArray(vertices[i], result, 12);
        }
    }

    @Test
    void handles_edge_midpoints_correctly() {
        final double[][] edgeMidpoints = {
            {0.5, 0}, // Midpoint of pai-pbi edge
            {0, 0.5}, // Midpoint of pai-pci edge
            {0.5, 0.5} // Midpoint of pbi-pci edge
        };

        final double[][] expectedBary = {
            {0.5, 0.5, 0}, // pai-pbi midpoint
            {0.5, 0, 0.5}, // pai-pci midpoint
            {0, 0.5, 0.5} // pbi-pci midpoint
        };

        for (int i = 0; i < edgeMidpoints.length; i++) {
            final double[] bary = faceToBarycentric(edgeMidpoints[i], TEST_TRIANGLE);

            // Check barycentric coordinates
            assertCloseToArray(expectedBary[i], bary, 12);

            // Round-trip test
            final double[] result = barycentricToFace(bary, TEST_TRIANGLE);
            assertCloseToArray(edgeMidpoints[i], result, 12);
        }
    }

    // ---- describe: coordinate conversions ----
    @Test
    void converts_spherical_to_cartesian_coordinates() {
        // Test north pole
        final double[] northPole = toCartesian(new double[]{0, 0});
        assertCloseToArray(new double[]{0, 0, 1}, northPole);

        // Test equator at 0 longitude
        final double[] equator0 = toCartesian(new double[]{0, Math.PI / 2});
        assertCloseToArray(new double[]{1, 0, 0}, equator0);

        // Test equator at 90° longitude
        final double[] equator90 = toCartesian(new double[]{Math.PI / 2, Math.PI / 2});
        assertCloseToArray(new double[]{0, 1, 0}, equator90);
    }

    @Test
    void converts_cartesian_to_spherical_coordinates() {
        // Test round trip conversion
        final double[] original = {Math.PI / 4, Math.PI / 6};
        final double[] cartesian = toCartesian(original);
        final double[] spherical = toSpherical(cartesian);

        assertCloseToArray(original, spherical);
    }

    @Test
    void keeps_full_precision_for_phi_near_the_poles_Kahan_12() {
        // acos(z/r) returns 0 for any phi below ~1e-8 and loses half the digits
        // carried near both poles; the atan2 form must round-trip these at full
        // relative precision.
        final double[] phis = {1e-12, 1e-9, 1e-6, 1e-3, Math.PI - 1e-3, Math.PI - 1e-6, Math.PI - 1e-9};
        for (final double phi : phis) {
            final double[] cartesian = toCartesian(new double[]{0.5, phi});
            final double phiOut = toSpherical(cartesian)[1];
            final double distFromPole = Math.min(phi, Math.PI - phi);
            assertTrue(Math.abs(phiOut - phi) <= 1e-12 * Math.max(distFromPole, 1e-3), "phi " + phi);
        }
    }

    // ---- describe: LonLat to/from spherical ----
    @Test
    void converts_longitude_latitude_to_spherical_coordinates() {
        // Test Greenwich equator
        final double[] greenwich = fromLonLat(new double[]{0, 0});
        // Match OFFSET_LON: 93
        assertCloseToArray(new double[]{degToRad(93), Math.PI / 2}, greenwich);

        // Test north pole
        final double[] northPole = fromLonLat(new double[]{0, 90});
        assertCloseToArray(new double[]{degToRad(93), 0}, northPole);

        // Test south pole
        final double[] southPole = fromLonLat(new double[]{0, -90});
        assertCloseToArray(new double[]{degToRad(93), Math.PI}, southPole);
    }

    @Test
    void converts_spherical_to_longitude_latitude_coordinates() {
        // Test round trip conversion
        for (final double[] lonLat : TEST_POINTS_LONLAT) {
            final double lon = lonLat[0], lat = lonLat[1];
            final double[] spherical = fromLonLat(new double[]{lon, lat});
            final double[] result = toLonLat(spherical);

            // 180 and -180 are equivalent longitudes (antimeridian)
            final double expectedLon = lon == 180 ? -180 : lon;
            assertCloseToArray(new double[]{expectedLon, lat}, result);
        }
    }

    // ---- describe: normalizeLongitudes ----
    @Test
    void handles_simple_contour_without_wrapping() {
        final double[][] contour = {
            {0, 0},
            {10, 0},
            {10, 10},
            {0, 10},
            {0, 0}
        };
        final double[][] normalized = normalizeLongitudes(contour);
        assertEquals(contour.length, normalized.length);
        for (int i = 0; i < contour.length; i++) {
            assertArrayEquals(contour[i], normalized[i]);
        }
    }

    @Test
    @Disabled("it.skip upstream")
    void normalizes_contour_crossing_antimeridian() {
        final double[][] contour = {
            {170, 0},
            {175, 0},
            {-175, 0}, // This should become 185
            {-170, 0} // This should become 190
        };
        final double[][] normalized = normalizeLongitudes(contour);
        assertCloseTo(185, normalized[3][0]);
        assertCloseTo(190, normalized[4][0]);
    }

    @Test
    void normalizes_contour_crossing_antimeridian_in_opposite_direction() {
        final double[][] contour = {
            {-170, 0},
            {-175, 0},
            {-180, 0},
            {175, 0}, // This should become -185
            {170, 0} // This should become -190
        };
        final double[][] normalized = normalizeLongitudes(contour);
        assertCloseTo(-185, normalized[3][0]);
        assertCloseTo(-190, normalized[4][0]);
    }
}
