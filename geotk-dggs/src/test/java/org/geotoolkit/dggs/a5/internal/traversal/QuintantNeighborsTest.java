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
import org.geotoolkit.dggs.a5.internal.Fixtures;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import static org.geotoolkit.dggs.a5.internal.traversal.QuintantNeighbors.getCellNeighbors;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of the {@code getCellNeighbors} describe block of upstream
 * {@code tests/traversal/quintant-neighbors.test.ts} (its {@code NEIGHBOR_DELTAS}
 * block is {@link NeighborsTest}).
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class QuintantNeighborsTest {

    private static final JsonNode FIXTURES = Fixtures.load("fixtures/traversal/quintant-neighbors.json");

    // ---- describe: getCellNeighbors ----
    @Test
    void should_find_correct_neighbors_for_all_orientations() {
        for (final JsonNode f : FIXTURES) {
            final JsonNode input = f.get("input");
            final long s = input.get("s").asLong();
            final int resolution = input.get("resolution").asInt();
            final Orientation orientation = Orientation.valueOf(input.get("orientation").asText());
            final JsonNode exp = f.get("output").get("neighbors");
            final long[] expected = new long[exp.size()];
            for (int i = 0; i < expected.length; i++) expected[i] = exp.get(i).asLong();
            assertArrayEquals(expected, getCellNeighbors(s, resolution, orientation),
                    () -> "s=" + s + " res=" + resolution + " " + orientation);
        }
    }
}
