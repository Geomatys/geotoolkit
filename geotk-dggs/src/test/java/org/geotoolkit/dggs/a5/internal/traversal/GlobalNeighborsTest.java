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
package org.geotoolkit.dggs.a5.internal.traversal;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.traversal.GlobalNeighbors.getGlobalCellNeighbors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/traversal/global-neighbors.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class GlobalNeighborsTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/traversal/global-neighbors.json");

    private static List<String> texts(JsonNode array) {
        final List<String> list = new ArrayList<>();
        for (JsonNode n : array) list.add(n.asText());
        return list;
    }

    private static List<String> hexes(long[] cells) {
        final List<String> list = new ArrayList<>();
        for (long c : cells) list.add(u64ToHex(c));
        return list;
    }

    // ---- describe: getGlobalCellNeighbors ----
    @Test
    void should_find_all_vertex_sharing_neighbors() {
        for (final JsonNode f : FIXTURES) {
            final String hex = f.get("input").get("cellId").asText();
            final List<String> result = hexes(getGlobalCellNeighbors(hexToU64(hex)));
            assertEquals(texts(f.get("output").get("neighbors")), result, hex);
        }
    }

    @Test
    void should_find_edge_only_neighbors() {
        for (final JsonNode f : FIXTURES) {
            final String hex = f.get("input").get("cellId").asText();
            final List<String> result = hexes(getGlobalCellNeighbors(hexToU64(hex), true));
            assertEquals(texts(f.get("output").get("edgeNeighbors")), result, hex);
        }
    }

    @Test
    void should_return_5_edge_neighbors_at_every_resolution() {
        // Every cell is a pentagon: the dodecahedron faces at resolution 0, the
        // pentagonal hexecontahedron at resolution 1, the lattice tiling beyond
        for (final JsonNode f : FIXTURES) {
            assertEquals(5, f.get("output").get("edgeNeighbors").size());
        }
    }
}
