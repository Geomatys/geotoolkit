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
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseTo;
import static org.geotoolkit.dggs.a5.internal.Fixtures.assertCloseToArray;
import static org.geotoolkit.dggs.a5.internal.Fixtures.doubles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/projections/crs.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CRSTest {

    private static final JsonNode expectedVertices = Fixtures.load("fixtures/crs-vertices.json");

    // ---- describe: CRS ----
    @Test
    void should_have_exactly_62_vertices() {
        final CRS crs = new CRS();
        final List<double[]> vertices = crs.vertices();

        assertEquals(62, vertices.size());
    }

    @Test
    void should_match_expected_vertices_from_JSON_file() {
        final CRS crs = new CRS();
        final List<double[]> vertices = crs.vertices();

        assertEquals(expectedVertices.size(), vertices.size());
        for (int index = 0; index < vertices.size(); index++) {
            final double[] expected = doubles(expectedVertices.get(index));
            assertCloseToArray(expected, vertices.get(index));
        }
    }

    @Test
    void should_throw_error_for_non_existent_vertex() {
        final CRS crs = new CRS();
        final double[] nonVertexPoint = {1, 0, 0};
        final Exception ex = assertThrows(IllegalArgumentException.class, () -> crs.getVertex(nonVertexPoint));
        assertEquals("Failed to find vertex in CRS", ex.getMessage());
    }

    @Test
    void should_validate_vertex_structure() {
        final CRS crs = new CRS();
        final List<double[]> vertices = crs.vertices();

        // All vertices should be normalized (unit length)
        for (final double[] vertex : vertices) {
            assertCloseTo(1, Vec3.length(vertex), 15);
        }
    }
}
