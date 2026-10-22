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
package org.geotoolkit.dggs.a5.internal.projections;

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles2;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/projections/equal-area.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class EqualAreaProjectionTest {

    private static final JsonNode TEST_DATA = Fixtures.load("projections/fixtures/equal-area.json");

    // Extract static data from test data
    private static final double[][] TEST_SPHERICAL_TRIANGLE = doubles2(TEST_DATA.get("static").get("TEST_SPHERICAL_TRIANGLE"));
    private static final double[][] TEST_FACE_TRIANGLE = doubles2(TEST_DATA.get("static").get("TEST_FACE_TRIANGLE"));

    private static final double AUTHALIC_RADIUS = 6371.0072; // km
    private static final double MAX_ANGLE = Math.max(
            Vec3.angle(TEST_SPHERICAL_TRIANGLE[0], TEST_SPHERICAL_TRIANGLE[1]),
            Math.max(
                Vec3.angle(TEST_SPHERICAL_TRIANGLE[1], TEST_SPHERICAL_TRIANGLE[2]),
                Vec3.angle(TEST_SPHERICAL_TRIANGLE[2], TEST_SPHERICAL_TRIANGLE[0])));
    private static final double MAX_ARC_LENGTH_MM = AUTHALIC_RADIUS * MAX_ANGLE * 1e9;
    private static final double DESIRED_MM_PRECISION = 0.0024;

    private static final EqualAreaProjection equalArea = new EqualAreaProjection(TEST_SPHERICAL_TRIANGLE);

    // ---- describe: EqualAreaProjection forward ----
    @Test
    void forward_projections() {
        for (JsonNode testCase : TEST_DATA.get("forward")) {
            final double[] result = equalArea.forward(doubles(testCase.get("input")), TEST_SPHERICAL_TRIANGLE, TEST_FACE_TRIANGLE);
            assertCloseToArray(doubles(testCase.get("expected")), result);
        }
    }

    /**
     * Round trip of the forward test cases.
     * @return largest error (upstream: accumulated in a describe-level variable)
     */
    private static double roundTripForward() {
        double largestError = 0;
        for (JsonNode testCase : TEST_DATA.get("forward")) {
            final double[] spherical = doubles(testCase.get("input"));
            final double[] polar = equalArea.forward(spherical, TEST_SPHERICAL_TRIANGLE, TEST_FACE_TRIANGLE);
            final double[] result = equalArea.inverse(polar, TEST_FACE_TRIANGLE, TEST_SPHERICAL_TRIANGLE);
            largestError = Math.max(largestError, Vec3.distance(result, spherical));
            assertCloseToArray(spherical, result);
        }
        return largestError;
    }

    @Test
    void round_trip_forward_projections() {
        roundTripForward();
    }

    @Test
    void is_accurate_to_0_0024mm() {
        // Upstream relies on the previous test having filled largestError: recomputed here
        final double largestError = roundTripForward();
        assertTrue(largestError * MAX_ARC_LENGTH_MM < DESIRED_MM_PRECISION);
    }

    // ---- describe: EqualAreaProjection triangle constants ----
    // The projection caches constants from one canonical triangle and reuses them
    // for every face. That is valid because all dodecahedron face triangles are
    // congruent and consistently wound:
    //   - volumeABC and areaABC are identical on every face (forward relies on this);
    //   - A·B / A·C only ever take the two canonical values — they are equal for
    //     "even" faces and swapped for the mirror-image "odd" faces, which
    //     inverse() handles by swapping B↔C — so on even faces the cached
    //     alphaTransform matrix matches exactly.
    @Test
    void agree_across_all_face_triangles_origins_and_reflections() {
        final DodecahedronProjection dodecahedron = new DodecahedronProjection();
        final EqualAreaProjection.TriangleConstants canonical = EqualAreaProjection.computeConstants(new CRS().getCanonicalTriangle());

        for (int originId = 0; originId < 12; originId++) {
            for (int faceTriangleIndex = 0; faceTriangleIndex < 10; faceTriangleIndex++) {
                for (final boolean reflected : new boolean[]{false, true}) {
                    final double[][] triangle = dodecahedron.getSphericalTriangle(faceTriangleIndex, originId, reflected);
                    final EqualAreaProjection.TriangleConstants c = EqualAreaProjection.computeConstants(triangle);

                    // Invariant on every face.
                    assertCloseTo(canonical.volumeABC(), c.volumeABC(), 12);
                    assertCloseTo(canonical.areaABC(), c.areaABC(), 12);

                    // A·B / A·C take the two canonical values; the orientation is whichever
                    // canonical value A·B is nearer to (the same test inverse() uses).
                    final boolean even = Math.abs(c.AdotB() - canonical.AdotB()) < Math.abs(c.AdotB() - canonical.AdotC());
                    if (even) {
                        assertCloseTo(canonical.AdotB(), c.AdotB(), 12);
                        assertCloseTo(canonical.AdotC(), c.AdotC(), 12);
                        // The cached coefficient matrix matches exactly on even faces.
                        assertCloseToArray(canonical.alphaTransform(), c.alphaTransform());
                    } else {
                        // Mirror-image face: A·B and A·C swapped (inverse() swaps B↔C).
                        assertCloseTo(canonical.AdotC(), c.AdotB(), 12);
                        assertCloseTo(canonical.AdotB(), c.AdotC(), 12);
                    }
                }
            }
        }
    }

    // ---- describe: EqualAreaProjection inverse ----
    @Test
    void inverse_projections() {
        for (JsonNode testCase : TEST_DATA.get("inverse")) {
            final double[] result = equalArea.inverse(doubles(testCase.get("input")), TEST_FACE_TRIANGLE, TEST_SPHERICAL_TRIANGLE);
            assertCloseToArray(doubles(testCase.get("expected")), result);
        }
    }

    @Test
    void round_trip_inverse_projections() {
        for (JsonNode testCase : TEST_DATA.get("inverse")) {
            final double[] facePoint = doubles(testCase.get("input"));
            final double[] spherical = equalArea.inverse(facePoint, TEST_FACE_TRIANGLE, TEST_SPHERICAL_TRIANGLE);
            final double[] result = equalArea.forward(spherical, TEST_SPHERICAL_TRIANGLE, TEST_FACE_TRIANGLE);
            assertCloseToArray(facePoint, result);
        }
    }
}
