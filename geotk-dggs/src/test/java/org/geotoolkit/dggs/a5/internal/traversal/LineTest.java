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
import java.util.HashSet;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.utils.BigInts;
import static org.geotoolkit.dggs.a5.internal.core.Hex.u64ToHex;
import static org.geotoolkit.dggs.a5.internal.traversal.Line.lineStringToCells;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of upstream {@code tests/traversal/line.test.ts}.
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class LineTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/traversal/line.json");

    // ---- describe: lineStringToCells ----
    @Test
    void lineStringToCells_should_trace_correct_cells_for_each_fixture() {
        for (final JsonNode f : FIXTURES.get("lineSegment")) {
            traceCase(f.get("name").asText(), f);
        }
    }

    private static void traceCase(String name, JsonNode f) {
        final long[] result = lineStringToCells(new double[][]{Fixtures.doubles(f.get("start")), Fixtures.doubles(f.get("end"))}, f.get("resolution").asInt());
        final long[] sorted = result.clone();
        BigInts.sortUnsigned(sorted);
        final List<String> resultHex = new ArrayList<>();
        for (long c : sorted) resultHex.add(u64ToHex(c));
        final List<String> expected = new ArrayList<>();
        for (JsonNode n : f.get("cells")) expected.add(n.asText());
        assertEquals(expected, resultHex, name);
    }

    @Test
    void lineStringToCells_should_return_empty_for_empty_waypoints() {
        assertArrayEquals(new long[0], lineStringToCells(new double[0][], 5));
    }

    @Test
    void lineStringToCells_should_return_single_cell_for_single_waypoint() {
        final long[] result = lineStringToCells(new double[][]{{10, 50}}, 5);
        assertEquals(1, result.length);
    }

    @Test
    void lineStringToCells_should_deduplicate_cells_at_segment_junctions() {
        final double[][] waypoints = {
            {0, 50},
            {10, 50},
            {10, 45}
        };
        final long[] result = lineStringToCells(waypoints, 3);
        final HashSet<Long> unique = new HashSet<>();
        for (long c : result) unique.add(c);
        assertEquals(unique.size(), result.length);
    }
}
