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
package org.geotoolkit.dggs.a5.internal.geometry;

import com.fasterxml.jackson.databind.JsonNode;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles2;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/geometry/spherical-triangle.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SphericalTriangleShapeTest {

    private static final JsonNode fixtures = Fixtures.load("geometry/fixtures/spherical-triangle.json");

    // ---- describe: constructor ----
    @Test
    void requires_exactly_3_vertices() {
        final String message = "SphericalTriangleShape requires exactly 3 vertices";
        assertEquals(message, assertThrows(IllegalArgumentException.class,
                () -> new SphericalTriangleShape(new double[0][])).getMessage());
        assertEquals(message, assertThrows(IllegalArgumentException.class,
                () -> new SphericalTriangleShape(new double[][] {{1, 0, 0}, {0, 1, 0}})).getMessage());
        assertEquals(message, assertThrows(IllegalArgumentException.class,
                () -> new SphericalTriangleShape(new double[][] {
                    {1, 0, 0},
                    {0, 1, 0},
                    {0, 0, 1},
                    {1, 1, 1}
                })).getMessage());
    }

    @Test
    void accepts_exactly_3_vertices() {
        assertDoesNotThrow(() -> new SphericalTriangleShape(new double[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}));
    }

    // ---- describe: getBoundary ----
    @Test
    void returns_boundary_points_with_different_segment_counts() {
        for (JsonNode fixture : fixtures) {
            final SphericalTriangleShape triangle = new SphericalTriangleShape(doubles2(fixture.get("vertices")));

            // Test boundaries with 1-3 segments
            for (int nSegments = 1; nSegments <= 3; nSegments++) {
                final double[][] boundary = triangle.getBoundary(nSegments, true);
                final double[][] expectedBoundary = doubles2(fixture.get("boundary" + nSegments));
                assertEquals(expectedBoundary.length, boundary.length);
                for (int j = 0; j < boundary.length; j++) {
                    assertCloseToArray(expectedBoundary[j], boundary[j], 6);
                }
            }
        }
    }

    // ---- describe: slerp ----
    @Test
    void interpolates_between_vertices() {
        for (JsonNode fixture : fixtures) {
            final SphericalTriangleShape triangle = new SphericalTriangleShape(doubles2(fixture.get("vertices")));

            for (JsonNode test : fixture.get("slerpTests")) {
                final double[] actual = triangle.slerp(test.get("t").asDouble());
                assertCloseToArray(doubles(test.get("result")), actual, 6);
                // Should be normalized
                assertTrue(Math.abs(Vec3.length(actual) - 1) < 1e-10);
            }
        }
    }

    // ---- describe: containsPoint ----
    @Test
    void correctly_identifies_points_inside_and_outside_triangle() {
        for (JsonNode fixture : fixtures) {
            final SphericalTriangleShape triangle = new SphericalTriangleShape(doubles2(fixture.get("vertices")));

            for (JsonNode test : fixture.get("containsPointTests")) {
                final double actual = triangle.containsPoint(doubles(test.get("point")));
                assertCloseTo(test.get("result").asDouble(), actual, 6);
            }
        }
    }

    // ---- describe: getArea ----
    @Test
    void returns_correct_area_for_all_triangles() {
        for (JsonNode fixture : fixtures) {
            final SphericalTriangleShape triangle = new SphericalTriangleShape(doubles2(fixture.get("vertices")));
            final double area = triangle.getArea();
            assertCloseTo(fixture.get("area").asDouble(), area, 6);
            // Area can be negative for some winding orders, so check absolute value
            assertTrue(Math.abs(area) > 0);
            assertTrue(Math.abs(area) <= 2 * Math.PI);
        }
    }
}
