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
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.uncompact;
import static org.geotoolkit.dggs.a5.internal.traversal.GridDisk.gridDisk;
import static org.geotoolkit.dggs.a5.internal.traversal.GridDisk.gridDiskVertex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/traversal/grid-disk.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class GridDiskTest {

    private static final JsonNode CASES = Fixtures.load("fixtures/traversal/grid-disk.json");

    /** Sort hex cell IDs by numeric value. */
    private static List<String> sortHex(List<String> cells) {
        cells.sort(Comparator.comparing(a -> "0".repeat(20 - a.length()) + a));
        return cells;
    }

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

    // ---- describe: gridDisk ----
    @Test
    void gridDisk_should_return_correct_cells_for_all_k_values() {
        for (final JsonNode f : CASES) {
            final long cellId = hexToU64(f.get("cellId").asText());
            final List<String> result = sortHex(hexes(uncompact(gridDisk(cellId, f.get("k").asInt()))));
            assertEquals(sortHex(texts(f.get("cells"))), result, () -> "cell " + f.get("cellId").asText() + " k=" + f.get("k"));
        }
    }

    @Test
    void gridDisk_should_return_a_BigUint64Array() {
        final long cellId = hexToU64(CASES.get(0).get("cellId").asText());
        // Java: long[] by signature
        final long[] result = gridDisk(cellId, 1);
        assertTrue(result.length > 0);
    }

    @Test
    void gridDisk_should_return_only_center_cell_for_k_0() {
        final String hex = CASES.get(0).get("cellId").asText();
        final long[] result = gridDisk(hexToU64(hex), 0);
        // The cell itself, then the compaction marker recording its resolution
        assertEquals(2, result.length);
        assertEquals(hex, u64ToHex(result[0]));
        assertTrue(isCompactionMarker(result[1]));
        assertEquals(List.of(hex), hexes(uncompact(result)));
    }

    // ---- describe: gridDiskVertex ----
    @Test
    void gridDiskVertex_should_return_correct_cells_for_all_k_values() {
        for (final JsonNode f : CASES) {
            final long cellId = hexToU64(f.get("cellId").asText());
            final List<String> expected = texts(f.get("cells"));
            expected.addAll(texts(f.get("extraVertexCells")));
            final List<String> result = sortHex(hexes(uncompact(gridDiskVertex(cellId, f.get("k").asInt()))));
            assertEquals(sortHex(expected), result, () -> "cell " + f.get("cellId").asText() + " k=" + f.get("k"));
        }
    }

    @Test
    void gridDiskVertex_should_return_a_BigUint64Array() {
        final long cellId = hexToU64(CASES.get(0).get("cellId").asText());
        // Java: long[] by signature
        final long[] result = gridDiskVertex(cellId, 1);
        assertTrue(result.length > 0);
    }

    @Test
    void gridDiskVertex_should_return_only_center_cell_for_k_0() {
        final String hex = CASES.get(0).get("cellId").asText();
        final long[] result = gridDiskVertex(hexToU64(hex), 0);
        // The cell itself, then the compaction marker recording its resolution
        assertEquals(2, result.length);
        assertEquals(hex, u64ToHex(result[0]));
        assertTrue(isCompactionMarker(result[1]));
        assertEquals(List.of(hex), hexes(uncompact(result)));
    }
}
