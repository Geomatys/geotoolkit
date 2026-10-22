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

import org.geotoolkit.dggs.a5.internal.lattice.Triple;

/**
 * The neighbors of a cell, in triple space, are a fixed function of its
 * pentagon flavor: pentagons tile edge-to-edge, so the arrangement around a
 * pentagon is forced. Every cell has exactly 5 edge-sharing and 2 vertex-only
 * neighbors, at the triple deltas below. No validation is needed — each delta
 * IS a neighbor (bounds permitting).
 *
 * <p>Derived geometrically (shared pentagon vertices) and verified exhaustively
 * over all interior cells at res 4-5, all orientations, zero conflicts. The
 * flavor-1/3 lists are the flavor-0/2 lists negated (they are the 180°-rotated
 * shapes).</p>
 *
 * <p>Port of upstream {@code traversal/neighbors.ts}. The table is built in the static
 * initialiser and immutable by contract (shared arrays: never modify): thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Neighbors {

    /**
     * Neighbor triple deltas of one flavor (shared arrays, never modify).
     *
     * @param edge 5 edge-sharing neighbors
     * @param vertex 2 vertex-only neighbors
     * @param all edge ++ vertex, spelled out so this stays a pure data table (guarded by a test)
     */
    public record NeighborDeltas(Triple[] edge, Triple[] vertex, Triple[] all) {
    }

    private static Triple D(int x, int y, int z) {
        return new Triple(x, y, z);
    }

    /**
     * Neighbor deltas indexed by flavor (0-3). Shared: never modify.
     */
    public static final NeighborDeltas[] NEIGHBOR_DELTAS = {
        new NeighborDeltas( // flavor 0
            new Triple[]{D(0, 0, 1), D(0, 1, -1), D(0, 1, 0), D(1, -1, 0), D(1, 0, 0)},
            new Triple[]{D(1, -1, 1), D(1, 1, -1)},
            new Triple[]{D(0, 0, 1), D(0, 1, -1), D(0, 1, 0), D(1, -1, 0), D(1, 0, 0), D(1, -1, 1), D(1, 1, -1)}
        ),
        new NeighborDeltas( // flavor 1 (= flavor 0 rotated 180°: deltas negated)
            new Triple[]{D(0, 0, -1), D(0, -1, 1), D(0, -1, 0), D(-1, 1, 0), D(-1, 0, 0)},
            new Triple[]{D(-1, 1, -1), D(-1, -1, 1)},
            new Triple[]{D(0, 0, -1), D(0, -1, 1), D(0, -1, 0), D(-1, 1, 0), D(-1, 0, 0), D(-1, 1, -1), D(-1, -1, 1)}
        ),
        new NeighborDeltas( // flavor 2
            new Triple[]{D(-1, 1, 0), D(0, -1, 1), D(0, 0, 1), D(0, 1, 0), D(1, 0, 0)},
            new Triple[]{D(-1, 1, 1), D(1, -1, 1)},
            new Triple[]{D(-1, 1, 0), D(0, -1, 1), D(0, 0, 1), D(0, 1, 0), D(1, 0, 0), D(-1, 1, 1), D(1, -1, 1)}
        ),
        new NeighborDeltas( // flavor 3 (= flavor 2 rotated 180°: deltas negated)
            new Triple[]{D(1, -1, 0), D(0, 1, -1), D(0, 0, -1), D(0, -1, 0), D(-1, 0, 0)},
            new Triple[]{D(1, -1, -1), D(-1, 1, -1)},
            new Triple[]{D(1, -1, 0), D(0, 1, -1), D(0, 0, -1), D(0, -1, 0), D(-1, 0, 0), D(1, -1, -1), D(-1, 1, -1)}
        )
    };

    private Neighbors() {
    }
}
