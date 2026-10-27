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
import java.util.Collections;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.Fixtures.hexArray;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.traversal.LatticeFloodFill.tripleSpaceFloodFill;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.cellIdsToTriples;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/traversal/lattice-flood-fill.test.ts}
 * (one upstream {@code it} per fixture case: looped here, case name in messages).
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class LatticeFloodFillTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/traversal/lattice-flood-fill.json");

    /** Hex strings, lexicographically sorted (JS default sort). */
    private static List<String> sortedHex(long[] cells) {
        final List<String> list = new ArrayList<>();
        for (long c : cells) list.add(u64ToHex(c));
        Collections.sort(list);
        return list;
    }

    private static List<String> texts(JsonNode array) {
        final List<String> list = new ArrayList<>();
        for (JsonNode n : array) list.add(n.asText());
        return list;
    }

    // ---- describe: tripleSpaceFloodFill ----
    @Test
    void tripleSpaceFloodFill_fixture_cases() {
        int count = 0;
        for (final JsonNode f : FIXTURES.get("cases")) {
            final String name = f.get("name").asText();
            final IntList seeds = cellIdsToTriples(hexArray(f.get("seedCells")));
            final IntList firewall = cellIdsToTriples(hexArray(f.get("firewallCells")));
            final int resolution = f.get("resolution").asInt();

            final LatticeFloodFill.FloodFillResult result = f.has("maxLayers")
                    ? tripleSpaceFloodFill(firewall, seeds, resolution, f.get("maxLayers").asInt())
                    : tripleSpaceFloodFill(firewall, seeds, resolution);

            assertEquals(texts(f.get("interiorCells")), sortedHex(result.interiorCells()), name + ": interior");
            assertEquals(texts(f.get("frontierCells")), sortedHex(result.frontierCellIds()), name + ": frontier");
            count++;
        }
        assertTrue(count > 0);
    }
}
