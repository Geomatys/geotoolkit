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
import org.geotoolkit.dggs.a5.internal.utils.BigInts;
import static org.geotoolkit.dggs.a5.internal.core.Hex.hexToU64;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.coverings.Compact.uncompact;
import static org.geotoolkit.dggs.a5.internal.traversal.Cap.estimateCellRadius;
import static org.geotoolkit.dggs.a5.internal.traversal.Cap.metersToH;
import static org.geotoolkit.dggs.a5.internal.traversal.Cap.pickCoarseResolution;
import static org.geotoolkit.dggs.a5.internal.traversal.Cap.sphericalCap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/traversal/cap.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class CapTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/traversal/cap.json");

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

    // ---- describe: sphericalCap ----
    @Test
    void sphericalCap_should_return_a_BigUint64Array() {
        final JsonNode f = FIXTURES.get("sphericalCap").get(0);
        final long cellId = hexToU64(f.get("cellId").asText());
        // Java: long[] by signature
        assertNotNull(sphericalCap(cellId, f.get("radius").asDouble()));
    }

    @Test
    void sphericalCap_should_return_correct_cells_when_uncompacted() {
        for (final JsonNode f : FIXTURES.get("sphericalCap")) {
            final long cellId = hexToU64(f.get("cellId").asText());
            final List<String> result = hexes(uncompact(sphericalCap(cellId, f.get("radius").asDouble())));
            assertEquals(texts(f.get("cells")), result, () -> "cell " + f.get("cellId").asText() + " radius " + f.get("radius"));
        }
    }

    @Test
    void sphericalCap_should_return_correct_compacted_cells() {
        for (final JsonNode f : FIXTURES.get("sphericalCapCompact")) {
            final long cellId = hexToU64(f.get("cellId").asText());
            final long[] cells = sphericalCap(cellId, f.get("radius").asDouble()).clone();
            // upstream sorts the bigints: unsigned
            BigInts.sortUnsigned(cells);
            assertEquals(texts(f.get("compactedCells")), hexes(cells), () -> "cell " + f.get("cellId").asText() + " radius " + f.get("radius"));
        }
    }

    // ---- describe: metersToH ----
    @Test
    void metersToH_should_convert_meters_to_haversine_threshold() {
        for (final JsonNode f : FIXTURES.get("helpers").get("metersToH")) {
            assertEquals(f.get("expectedH").asDouble(), metersToH(f.get("meters").asDouble()), 0.0, () -> "meters " + f.get("meters"));
        }
    }

    // ---- describe: estimateCellRadius ----
    @Test
    void estimateCellRadius_should_return_correct_radius_for_each_resolution() {
        for (final JsonNode f : FIXTURES.get("helpers").get("estimateCellRadius")) {
            assertEquals(f.get("expectedMeters").asDouble(), estimateCellRadius(f.get("resolution").asInt()), 0.0, () -> "resolution " + f.get("resolution"));
        }
    }

    @Test
    void estimateCellRadius_should_decrease_with_increasing_resolution() {
        final JsonNode cases = FIXTURES.get("helpers").get("estimateCellRadius");
        for (int i = 1; i < cases.size(); i++) {
            assertTrue(cases.get(i).get("expectedMeters").asDouble() < cases.get(i - 1).get("expectedMeters").asDouble());
        }
    }

    // ---- describe: pickCoarseResolution ----
    @Test
    void pickCoarseResolution_should_return_correct_coarse_resolution() {
        for (final JsonNode f : FIXTURES.get("helpers").get("pickCoarseResolution")) {
            assertEquals(f.get("expectedCoarseRes").asInt(), pickCoarseResolution(f.get("radius").asDouble(), f.get("targetRes").asInt()),
                    () -> "radius " + f.get("radius") + " targetRes " + f.get("targetRes"));
        }
    }

    @Test
    void pickCoarseResolution_should_never_exceed_targetRes() {
        for (final JsonNode f : FIXTURES.get("helpers").get("pickCoarseResolution")) {
            assertTrue(pickCoarseResolution(f.get("radius").asDouble(), f.get("targetRes").asInt()) <= f.get("targetRes").asInt());
        }
    }
}
