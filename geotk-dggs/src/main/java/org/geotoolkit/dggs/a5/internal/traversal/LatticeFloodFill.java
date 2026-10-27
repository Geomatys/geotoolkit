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

import java.util.HashSet;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleInBounds;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellKey;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellToId;

/**
 * Triple-space flood fill over the parity-valid lattice moves.
 *
 * <p>Port of upstream {@code traversal/lattice-flood-fill.ts}. The TS firewall union
 * {@code number[] | {state, delta}} becomes two overloads; the optional
 * {@code maxLayers} becomes overloads (absent = run to convergence). Stateless,
 * thread-safe (a {@link FloodState} must not be shared between threads).</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class LatticeFloodFill {

    /**
     * Flood state, reusable across calls at one resolution: the keys of every cell visited so far.
     *
     * @param visited keys (see {@link TripleCells#tripleCellKey}) of the visited cells
     */
    public record FloodState(HashSet<Long> visited) {
        /** Creates an empty state. */
        public FloodState() {
            this(new HashSet<>());
        }
    }

    /**
     * Result of {@link #tripleSpaceFloodFill}.
     *
     * @param interiorCells   cell ids discovered by the call (seeds excluded)
     * @param frontierCellIds cell ids of the final frontier
     * @param frontier        final frontier, as flat (originId, quintant, x, y, z)
     * @param state           state for a follow-up call
     */
    public record FloodFillResult(long[] interiorCells, long[] frontierCellIds, IntList frontier, FloodState state) {
    }

    private LatticeFloodFill() {
    }

    /**
     * Flood fill run to convergence, with a fresh state.
     *
     * @see #tripleSpaceFloodFill(FloodState, IntList, IntList, int, int)
     */
    public static FloodFillResult tripleSpaceFloodFill(IntList firewall, IntList seeds, int resolution) {
        return tripleSpaceFloodFill(new FloodState(), firewall, seeds, resolution, Integer.MAX_VALUE);
    }

    /**
     * Flood fill with a fresh state.
     *
     * @see #tripleSpaceFloodFill(FloodState, IntList, IntList, int, int)
     */
    public static FloodFillResult tripleSpaceFloodFill(IntList firewall, IntList seeds, int resolution, int maxLayers) {
        return tripleSpaceFloodFill(new FloodState(), firewall, seeds, resolution, maxLayers);
    }

    /**
     * Flood fill run to convergence, reusing a state.
     *
     * @see #tripleSpaceFloodFill(FloodState, IntList, IntList, int, int)
     */
    public static FloodFillResult tripleSpaceFloodFill(FloodState state, IntList delta, IntList seeds, int resolution) {
        return tripleSpaceFloodFill(state, delta, seeds, resolution, Integer.MAX_VALUE);
    }

    /**
     * Triple-space flood fill over the 3 parity-valid lattice moves. Those never
     * cross a quintant edge, so each quintant floods independently. All cells —
     * firewall, seeds, the returned frontier — are flat (originId, quintant, x, y,
     * z), so nothing is decoded; discovered cells are encoded once, on output.
     *
     * @param state       a reused state from a previous call, or a new one (TS: firewall given as an array)
     * @param blocked     Cells the flood may not enter (TS: the firewall array, or {@code delta}
     *                    cells joining the reused state's firewall).
     * @param seeds       BFS seeds. Always added to the frontier, even if already
     *                    visited — reusing state with the same seeds restarts BFS.
     * @param resolution  resolution of the cells
     * @param maxLayers   Max BFS layers; {@code Integer.MAX_VALUE} (TS undefined) = run to convergence.
     * @return The cells discovered by this call (seeds excluded) and the final
     *         frontier, both as cell IDs (the frontier also in triple space), and
     *         the state for a follow-up call.
     */
    public static FloodFillResult tripleSpaceFloodFill(FloodState state, IntList blocked, IntList seeds, int resolution, int maxLayers) {
        final int curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
        final int maxRow = (1 << curveLevel) - 1;

        final HashSet<Long> visited = state.visited();
        for (int c = 0; c < blocked.length(); c += 5) {
            visited.add(tripleCellKey(blocked.get(c), blocked.get(c + 1), blocked.get(c + 2), blocked.get(c + 3), blocked.get(c + 4)));
        }
        for (int c = 0; c < seeds.length(); c += 5) {
            visited.add(tripleCellKey(seeds.get(c), seeds.get(c + 1), seeds.get(c + 2), seeds.get(c + 3), seeds.get(c + 4)));
        }

        final IntList discovered = new IntList();
        IntList frontier = seeds;
        for (int layers = 0; frontier.length() > 0 && layers < maxLayers; layers++) {
            final IntList next = new IntList();
            for (int c = 0; c < frontier.length(); c += 5) {
                final int originId = frontier.get(c);
                final int quintant = frontier.get(c + 1);
                final int x = frontier.get(c + 2);
                final int y = frontier.get(c + 3);
                final int z = frontier.get(c + 4);
                // +1 on one axis from a parity 0 triple, -1 from parity 1
                final int step = x + y + z == 0 ? 1 : -1;
                add(visited, discovered, next, maxRow, originId, quintant, x + step, y, z);
                add(visited, discovered, next, maxRow, originId, quintant, x, y + step, z);
                add(visited, discovered, next, maxRow, originId, quintant, x, y, z + step);
            }
            frontier = next;
        }

        return new FloodFillResult(toIds(discovered, curveLevel, resolution), toIds(frontier, curveLevel, resolution), frontier, state);
    }

    /** The {@code add} closure of upstream. */
    private static void add(HashSet<Long> visited, IntList discovered, IntList next, int maxRow,
            int originId, int quintant, int x, int y, int z) {
        if (!tripleInBounds(new Triple(x, y, z), maxRow)) return;
        final long key = tripleCellKey(originId, quintant, x, y, z);
        if (!visited.add(key)) return;
        discovered.push(originId, quintant, x, y, z);
        next.push(originId, quintant, x, y, z);
    }

    /** The {@code toIds} closure of upstream. */
    private static long[] toIds(IntList cells, int curveLevel, int resolution) {
        final long[] ids = new long[cells.length() / 5];
        for (int c = 0, i = 0; c < cells.length(); c += 5, i++) {
            ids[i] = tripleCellToId(cells.get(c), cells.get(c + 1), cells.get(c + 2), cells.get(c + 3), cells.get(c + 4), curveLevel, resolution);
        }
        return ids;
    }
}
