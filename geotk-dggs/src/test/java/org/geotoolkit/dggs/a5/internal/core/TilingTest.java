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
import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getFaceVertices;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonVertices;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getQuintantPolar;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getQuintantVertices;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/tiling.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class TilingTest {

    private static final JsonNode fixtures = Fixtures.load("fixtures/tiling.json");

    private static void assertPentagon(JsonNode output, PentagonShape pentagon) {
        final double[][] vertices = pentagon.getVertices();
        final double area = pentagon.getArea();
        final double[] center = pentagon.getCenter();

        // Check that vertices match (within floating point tolerance)
        final JsonNode expectedVertices = output.get("vertices");
        assertEquals(expectedVertices.size(), vertices.length);
        for (int i = 0; i < vertices.length; i++) {
            assertCloseTo(expectedVertices.get(i).get(0).asDouble(), vertices[i][0], 15);
            assertCloseTo(expectedVertices.get(i).get(1).asDouble(), vertices[i][1], 15);
        }

        // Check area matches
        assertCloseTo(output.get("area").asDouble(), area, 15);

        // Check center matches
        assertCloseTo(output.get("center").get(0).asDouble(), center[0], 15);
        assertCloseTo(output.get("center").get(1).asDouble(), center[1], 15);
    }

    // ---- describe: tiling > getPentagonVertices ----
    @Test
    void should_generate_pentagon_vertices_with_correct_properties() {
        for (final JsonNode testCase : fixtures.get("getPentagonVertices")) {
            final JsonNode input = testCase.get("input");
            final JsonNode t = input.get("triple");
            final Triple triple = new Triple(t.get("x").asInt(), t.get("y").asInt(), t.get("z").asInt());

            final PentagonShape pentagon = getPentagonVertices(
                    input.get("resolution").asInt(), input.get("quintant").asInt(), triple, input.get("flavor").asInt());
            assertPentagon(testCase.get("output"), pentagon);
        }
    }

    // ---- describe: tiling > getQuintantVertices ----
    @Test
    void should_generate_correct_quintant_vertices_for_all_quintants() {
        for (final JsonNode testCase : fixtures.get("getQuintantVertices")) {
            final int quintant = testCase.get("input").get("quintant").asInt();
            final PentagonShape pentagon = getQuintantVertices(quintant);
            assertPentagon(testCase.get("output"), pentagon);
        }
    }

    // ---- describe: tiling > getFaceVertices ----
    @Test
    void should_generate_face_vertices_with_correct_properties() {
        final PentagonShape pentagon = getFaceVertices();
        assertPentagon(fixtures.get("getFaceVertices"), pentagon);
    }

    // ---- describe: tiling > getQuintantPolar ----
    @Test
    void should_return_correct_quintant_for_polar_coordinates() {
        for (final JsonNode testCase : fixtures.get("getQuintantPolar")) {
            final double[] polar = Fixtures.doubles(testCase.get("input").get("polar"));
            final int quintant = testCase.get("output").get("quintant").asInt();

            final int result = getQuintantPolar(polar);
            assertEquals(quintant, result);
        }
    }
}
