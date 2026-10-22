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

import org.geotoolkit.dggs.a5.internal.core.Origin;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.FACE_ADJACENCY;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.seamTriple;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleFlavor;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleInBounds;
import static org.geotoolkit.dggs.a5.internal.traversal.Neighbors.NEIGHBOR_DELTAS;

/**
 * The neighbors of a cell that lie outside its quintant, found with delta tables.
 *
 * <p>Port of upstream {@code traversal/lattice-boundary.ts}. Tables are immutable by
 * contract (shared arrays: never modify): thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class LatticeBoundary {

    /**
     * Neighbor delta: [dx, dy, dz, isEdgeSharing].
     */
    public record NeighborDelta(int dx, int dy, int dz, boolean isEdge) {
    }

    private static NeighborDelta D(int dx, int dy, int dz, boolean isEdge) {
        return new NeighborDelta(dx, dy, dz, isEdge);
    }

    /**
     * Cross-quintant left-edge deltas (source z=0), indexed by {@code parity * 2 + (yOdd ? 1 : 0)}.
     * Applied to the swapped base triple [0, y, x] in the previous quintant.
     * Shared: never modify.
     */
    public static final NeighborDelta[][] LEFT_EDGE_DELTAS = {
        /* parity=0, yEven */ {D(0, 0, 0, true), D(0, 0, 1, false)},
        /* parity=0, yOdd  */ {D(0, 0, 0, true), D(0, 1, 0, true), D(0, -1, 1, false), D(0, 1, -1, false)},
        /* parity=1, yEven */ {},
        /* parity=1, yOdd  */ {D(0, -1, 0, true), D(0, 0, -1, false)}
    };

    /**
     * Cross-quintant right-edge deltas (source x=0), indexed by {@code parity * 2 + (yOdd ? 1 : 0)}.
     * Applied to the swapped base triple [z, y, 0] in the next quintant.
     * Shared: never modify.
     */
    public static final NeighborDelta[][] RIGHT_EDGE_DELTAS = {
        /* parity=0, yEven */ {D(0, 0, 0, true), D(0, 1, 0, true), D(-1, 1, 0, false), D(1, -1, 0, false)},
        /* parity=0, yOdd  */ {D(0, 0, 0, true), D(1, 0, 0, false)},
        /* parity=1, yEven */ {D(0, -1, 0, true), D(-1, 0, 0, false)},
        /* parity=1, yOdd  */ {}
    };

    /**
     * The source cell of a boundary-neighbor lookup.
     *
     * @param triple source cell triple
     * @param parity triple parity (x + y + z)
     * @param sourceQuintant source cell quintant
     * @param origin source cell origin
     * @param maxRow maximum row of the quintant
     */
    public record BoundaryTripleContext(Triple triple, int parity, int sourceQuintant, Origin origin, int maxRow) {
    }

    private LatticeBoundary() {
    }

    /** If the triple is a valid cell, append it to `out` as (originId, quintant, x, y, z). */
    private static void pushTriple(IntList out, int x, int y, int z, int originId, int quintant, int maxRow) {
        if (!tripleInBounds(new Triple(x, y, z), maxRow)) return;
        out.push(originId, quintant, x, y, z);
    }

    /** Apply a delta table to a base triple, appending each valid cell. */
    private static void pushDeltas(IntList out, Triple base, NeighborDelta[] deltas, boolean edgeOnly,
            int originId, int quintant, int maxRow) {
        for (final NeighborDelta d : deltas) {
            if (edgeOnly && !d.isEdge()) continue;
            pushTriple(out, base.x() + d.dx(), base.y() + d.dy(), base.z() + d.dz(), originId, quintant, maxRow);
        }
    }

    /**
     * Every neighbor that lies outside the source cell's quintant, appended to
     * {@code out} as flat (originId, quintant, x, y, z) quintuples: cross-quintant
     * lateral edges, cross-face base edge, apex (face center), and (when not
     * {@code skipCorners}) the {@code [-maxRow, maxRow, 0]} vertex corner. The within-quintant
     * ±1 candidates are NOT covered here — callers generate those directly.
     *
     * <p>Only cells on a quintant edge (x = 0, z = 0 or y = maxRow) have any. The
     * result may contain duplicates; callers deduplicate.</p>
     *
     * @param ctx         source-cell context
     * @param edgeOnly    drop apex non-adjacent quintants and other vertex-only neighbors
     * @param skipCorners drop the {@code [-maxRow, maxRow, 0]} corner — used when the caller's
     *                    connectivity (e.g. lattice ±1 moves) doesn't traverse that vertex
     * @param out         flat (originId, quintant, x, y, z) output, appended to
     */
    public static void getBoundaryNeighborTriples(BoundaryTripleContext ctx, boolean edgeOnly, boolean skipCorners, IntList out) {
        final Triple triple = ctx.triple();
        final int parity = ctx.parity();
        final int sourceQuintant = ctx.sourceQuintant();
        final Origin origin = ctx.origin();
        final int maxRow = ctx.maxRow();
        final boolean yOdd = triple.y() % 2 != 0;
        final int deltaIndex = parity * 2 + (yOdd ? 1 : 0);

        // Left edge (z=0): neighbor in previous quintant at swapped [0, y, x]
        if (triple.z() == 0) {
            final int targetQuintant = (sourceQuintant - 1 + 5) % 5;
            pushDeltas(
                out,
                new Triple(0, triple.y(), triple.x()),
                LEFT_EDGE_DELTAS[deltaIndex],
                edgeOnly,
                origin.id(),
                targetQuintant,
                maxRow
            );
        }

        // Right edge (x=0): neighbor in next quintant at swapped [z, y, 0]
        if (triple.x() == 0) {
            final int targetQuintant = (sourceQuintant + 1) % 5;
            pushDeltas(
                out,
                new Triple(triple.z(), triple.y(), 0),
                RIGHT_EDGE_DELTAS[deltaIndex],
                edgeOnly,
                origin.id(),
                targetQuintant,
                maxRow
            );
        }

        // Base edge (y=maxRow): across the face seam the lattice continues, so the
        // neighbors on the adjacent face are those of the cell's image there
        if (triple.y() == maxRow) {
            final int[] adj = FACE_ADJACENCY[origin.id()][sourceQuintant];
            final int adjFaceId = adj[0];
            final int adjQuintant = adj[1];
            final Triple image = seamTriple(triple, maxRow);
            // The image's pentagon is the cell's turned half-way round: its flavor's parity bit flipped
            final Neighbors.NeighborDeltas deltas = NEIGHBOR_DELTAS[tripleFlavor(triple, maxRow) ^ 1];
            final Triple[] list = edgeOnly ? deltas.edge() : deltas.all();
            for (int i = 0; i < list.length; i++) {
                final Triple d = list[i];
                // Only steps back towards the seam (dy < 0) can land inside the neighbor quintant
                if (d.y() < 0) pushTriple(out, image.x() + d.x(), image.y() + d.y(), image.z() + d.z(), adjFaceId, adjQuintant, maxRow);
            }
        }

        // Apex [0,0,0]: cells from all 5 quintants meet at the face center
        if (triple.x() == 0 && triple.y() == 0 && triple.z() == 0) {
            for (int q = 0; q < 5; q++) {
                if (q == sourceQuintant) continue;
                final int distance = Math.min((q - sourceQuintant + 5) % 5, (sourceQuintant - q + 5) % 5);
                if (edgeOnly && distance != 1) continue;
                pushTriple(out, 0, 0, 0, origin.id(), q, maxRow);
            }
        }

        // Base-left corner [-maxRow, maxRow, 0]: 3 dodecahedron faces meet at this vertex.
        // The symmetric base-right corner is implicitly covered: its cross-quintant and
        // cross-face paths land on the [-maxRow, maxRow, 0] cell of neighboring quintants.
        if (!skipCorners && triple.x() == -maxRow && triple.y() == maxRow && triple.z() == 0) {
            // Vertex neighbor 1: across the previous quintant's base edge
            final int prevQuintant = (sourceQuintant - 1 + 5) % 5;
            final int[] prevAdj = FACE_ADJACENCY[origin.id()][prevQuintant];
            pushTriple(out, triple.x(), triple.y(), triple.z(), prevAdj[0], prevAdj[1], maxRow);

            // Vertex neighbor 2: adjacent quintant on the primary cross-face
            final int[] cross = FACE_ADJACENCY[origin.id()][sourceQuintant];
            pushTriple(out, triple.x(), triple.y(), triple.z(), cross[0], (cross[1] + 1) % 5, maxRow);
        }
    }
}
