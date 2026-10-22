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
package org.geotoolkit.dggs.a5.internal.regions;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.uncompact;
import static org.geotoolkit.dggs.a5.internal.coverings.Measures.count;
import static org.geotoolkit.dggs.a5.internal.coverings.Resolution.coveringResolution;
import static org.geotoolkit.dggs.a5.internal.regions.Subcell.cellToSubcells;
import static org.geotoolkit.dggs.a5.internal.regions.Subcell.cellToSupercell;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/regions/subcell.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SubcellTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/regions/subcell.json");

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

    // ---- describe: cellToSubcells ----
    @Test
    void should_give_the_subcells_of_each_cell() {
        for (final JsonNode f : FIXTURES.get("subcell")) {
            final String where = "subcells of " + f.get("cell").asText() + " at resolution " + f.get("resolution").asInt();
            final long[] result = cellToSubcells(hexToU64(f.get("cell").asText()), f.get("resolution").asInt());
            assertEquals(texts(f.get("cells")), hexes(result), where);
            assertEquals(new BigInteger(f.get("count").asText()), count(result), where);
        }
    }

    @Test
    void should_map_every_subcell_back_to_its_cell() {
        for (final JsonNode f : FIXTURES.get("subcell")) {
            final long cell = hexToU64(f.get("cell").asText());
            final int cellResolution = getResolution(cell);
            for (final long subcell : uncompact(cellToSubcells(cell, f.get("resolution").asInt()))) {
                assertEquals(u64ToHex(cell), u64ToHex(cellToSupercell(subcell, cellResolution)), "subcell " + u64ToHex(subcell));
            }
        }
    }

    @Test
    void should_return_the_cell_itself_at_its_own_resolution() {
        final long cell = hexToU64(FIXTURES.get("subcell").get(5).get("cell").asText());
        final long[] result = cellToSubcells(cell, getResolution(cell));
        assertArrayEquals(new long[]{cell}, uncompact(result));
        assertEquals(getResolution(cell), coveringResolution(result));
    }

    @Test
    void should_throw_for_a_coarser_resolution() {
        final long cell = hexToU64(FIXTURES.get("subcell").get(5).get("cell").asText());
        assertThrows(IllegalArgumentException.class, () -> cellToSubcells(cell, getResolution(cell) - 1));
    }

    // ---- describe: cellToSupercell ----
    @Test
    void should_give_the_supercell() {
        for (final JsonNode f : FIXTURES.get("supercell")) {
            assertEquals(f.get("supercell").asText(), u64ToHex(cellToSupercell(hexToU64(f.get("cell").asText()), f.get("resolution").asInt())),
                    "supercell of " + f.get("cell").asText() + " at resolution " + f.get("resolution").asInt());
        }
    }

    @Test
    void should_throw_for_a_finer_resolution() {
        final long cell = hexToU64(FIXTURES.get("supercell").get(0).get("cell").asText());
        assertThrows(IllegalArgumentException.class, () -> cellToSupercell(cell, getResolution(cell) + 1));
    }
}
