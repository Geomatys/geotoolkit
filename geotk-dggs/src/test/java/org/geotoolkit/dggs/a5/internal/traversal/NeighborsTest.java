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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import static org.geotoolkit.dggs.a5.internal.traversal.Neighbors.NEIGHBOR_DELTAS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Port of the {@code NEIGHBOR_DELTAS} describe block of upstream
 * {@code tests/traversal/quintant-neighbors.test.ts} (the rest of that file tests
 * {@code traversal/quintant-neighbors.ts}, ported with the traversal layer).
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class NeighborsTest {

    // ---- describe: NEIGHBOR_DELTAS ----
    @Test
    void has_all_equals_edge_concat_vertex_for_every_flavor() {
        for (final Neighbors.NeighborDeltas deltas : NEIGHBOR_DELTAS) {
            final List<Triple> expected = new ArrayList<>(Arrays.asList(deltas.edge()));
            expected.addAll(Arrays.asList(deltas.vertex()));
            assertEquals(expected, Arrays.asList(deltas.all()));
        }
    }
}
