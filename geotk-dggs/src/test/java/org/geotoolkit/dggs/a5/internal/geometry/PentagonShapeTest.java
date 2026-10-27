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
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/geometry/pentagon.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class PentagonShapeTest {

    private static final JsonNode fixtures = Fixtures.load("geometry/fixtures/pentagon.json");

    private static void assertVertices(JsonNode expectedVertices, double[][] vertices) {
        for (int i = 0; i < expectedVertices.size(); i++) {
            final JsonNode expected = expectedVertices.get(i);
            assertCloseTo(expected.get(0).asDouble(), vertices[i][0], 6);
            assertCloseTo(expected.get(1).asDouble(), vertices[i][1], 6);
        }
    }

    // ---- describe: ContainsPoint ----
    @Test
    void returns_correct_results_for_all_test_cases() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));

            for (JsonNode test : fixture.get("containsPointTests")) {
                final double actual = pentagon.containsPoint(doubles(test.get("point")));
                assertCloseTo(test.get("result").asDouble(), actual, 6);
            }
        }
    }

    // ---- describe: GetArea ----
    @Test
    void returns_correct_area_for_all_pentagons() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));
            final double area = pentagon.getArea();
            assertCloseTo(fixture.get("area").asDouble(), area, 6);
        }
    }

    // ---- describe: GetCenter ----
    @Test
    void returns_correct_center_for_all_pentagons() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));
            final double[] center = pentagon.getCenter();
            final double[] expected = doubles(fixture.get("center"));
            assertCloseTo(expected[0], center[0], 6);
            assertCloseTo(expected[1], center[1], 6);
        }
    }

    // ---- describe: Transformations ----
    @Test
    void scale_transformation_works_correctly() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));
            final PentagonShape scaled = pentagon.clone().scale(2);
            assertVertices(fixture.get("transformTests").get("scale"), scaled.getVertices());
        }
    }

    @Test
    void rotate180_transformation_works_correctly() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));
            final PentagonShape rotated = pentagon.clone().rotate180();
            assertVertices(fixture.get("transformTests").get("rotate180"), rotated.getVertices());
        }
    }

    @Test
    void reflectY_transformation_works_correctly() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));
            final PentagonShape reflected = pentagon.clone().reflectY();
            assertVertices(fixture.get("transformTests").get("reflectY"), reflected.getVertices());
        }
    }

    @Test
    void translate_transformation_works_correctly() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));
            final PentagonShape translated = pentagon.clone().translate(new double[]{1, 1});
            assertVertices(fixture.get("transformTests").get("translate"), translated.getVertices());
        }
    }

    // ---- describe: SplitEdges ----
    @Test
    void returns_split_edges_with_different_segment_counts() {
        for (JsonNode fixture : fixtures) {
            final PentagonShape pentagon = new PentagonShape(doubles2(fixture.get("vertices")));

            // Test boundaries with 2-3 segments
            for (int nSegments : new int[]{2, 3}) {
                final PentagonShape split = pentagon.clone().splitEdges(nSegments);
                final double[][] vertices = split.getVertices();
                final JsonNode expectedVertices = fixture.get("splitEdgesTests").get("segments" + nSegments);
                assertEquals(expectedVertices.size(), vertices.length);
                assertVertices(expectedVertices, vertices);
            }
        }
    }
}
