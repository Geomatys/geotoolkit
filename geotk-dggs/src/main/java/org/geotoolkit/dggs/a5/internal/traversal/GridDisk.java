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
import org.geotoolkit.dggs.a5.internal.core.A5Cell;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import org.geotoolkit.dggs.a5.internal.utils.LongList;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.walkFaces;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.compactCells;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toCovering;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.cellIdsToTriples;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.forEachTripleNeighbor;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellKey;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellToId;

/**
 * Grid disks: the cells within k hops of a cell.
 *
 * <p>Port of upstream {@code traversal/grid-disk.ts}. TS {@code BigUint64Array}
 * results are {@code long[]} (unsigned 64 bits ids). Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class GridDisk {

    /** One BFS ring: its dedup keys, and its cells as flat (originId, quintant, x, y, z). */
    private static final class Ring {
        final HashSet<Long> keys = new HashSet<>();
        final IntList cells = new IntList();
    }

    private GridDisk() {
    }

    /** Add a cell to `next` unless it is already in one of the three live rings. */
    private static void addCell(Ring next, Ring prev, Ring current, int originId, int quintant, int x, int y, int z) {
        final Long key = tripleCellKey(originId, quintant, x, y, z);
        if (prev.keys.contains(key) || current.keys.contains(key) || next.keys.contains(key)) return;
        next.keys.add(key);
        next.cells.push(originId, quintant, x, y, z);
    }

    /** Encode a ring's cells as cell IDs, appending them to `out`. */
    private static void pushCellIds(LongList out, IntList cells, int curveLevel, int resolution) {
        for (int c = 0; c < cells.length(); c += 5) {
            out.push(tripleCellToId(cells.get(c), cells.get(c + 1), cells.get(c + 2), cells.get(c + 3), cells.get(c + 4), curveLevel, resolution));
        }
    }

    /**
     * BFS grid disk in triple space, with progressive compaction.
     *
     * <p>Neighbors come from the per-flavor triple deltas, plus the boundary delta
     * tables for cells on a quintant edge, so no cell is decoded and each is
     * encoded exactly once, when it leaves the window.</p>
     *
     * <p>Uses a sliding-window dedup approach: only the previous and current frontier
     * rings are kept in memory for deduplication (BFS guarantees cells ≥2 rings
     * behind the frontier can never be re-discovered). Evicted interior cells are
     * periodically compacted to reduce memory pressure.</p>
     */
    private static long[] gridDiskInternal(long cellId, int k, boolean edgeOnly) {
        final A5Cell cell = deserialize(cellId);
        final int resolution = cell.resolution();
        if (k == 0) return toCovering(new long[]{cellId}, resolution);
        if (resolution == 0) {
            // The cells are the 12 dodecahedron faces
            final int[] faces = walkFaces(new int[]{cell.origin().id()}, face -> true, k);
            final long[] ids = new long[faces.length];
            for (int i = 0; i < faces.length; i++) {
                ids[i] = serialize(new A5Cell(origins.get(faces[i]), 0, 0L, 0));
            }
            return toCovering(ids, 0);
        }
        final int curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
        final int maxRow = (1 << curveLevel) - 1;
        final IntList seed = cellIdsToTriples(new long[]{cellId});
        final int originId = seed.get(0);
        final int quintant = seed.get(1);
        final int x = seed.get(2);
        final int y = seed.get(3);
        final int z = seed.get(4);

        // The seed is `cellId` already, so it goes straight to the output
        LongList interior = LongList.of(cellId);
        Ring prevFrontier = new Ring();
        Ring frontier = new Ring();
        addCell(frontier, prevFrontier, prevFrontier, originId, quintant, x, y, z);

        for (int ring = 1; ring <= k; ring++) {
            final Ring nextFrontier = new Ring();
            final Ring prev = prevFrontier;
            final Ring current = frontier;
            final TripleCells.TripleCellVisitor visit = (o, q, vx, vy, vz) ->
                    addCell(nextFrontier, prev, current, o, q, vx, vy, vz);
            final IntList cells = frontier.cells;
            for (int c = 0; c < cells.length(); c += 5) {
                forEachTripleNeighbor(cells.get(c), cells.get(c + 1), cells.get(c + 2), cells.get(c + 3), cells.get(c + 4), maxRow, edgeOnly, visit);
            }

            // The seed ring is expanded; drop its cell so it isn't encoded again (its key stays)
            if (ring == 1) frontier.cells.clear();

            // Evict prevFrontier — these cells are ≥2 rings behind the new frontier
            // and can never be re-discovered by BFS
            pushCellIds(interior, prevFrontier.cells, curveLevel, resolution);

            // Progressively compact interior to reduce memory pressure
            if (interior.length() > 100) {
                interior = LongList.of(compactCells(interior.toArray()));
            }

            prevFrontier = frontier;
            frontier = nextFrontier;
        }

        // Merge remaining boundary rings with compacted interior
        pushCellIds(interior, prevFrontier.cells, curveLevel, resolution);
        pushCellIds(interior, frontier.cells, curveLevel, resolution);

        return toCovering(interior.toArray(), resolution);
    }

    /**
     * Compute the grid disk of edge-sharing neighbors within k hops.
     * Returns compacted cell IDs including the center cell, sorted in curve
     * order, then a compaction marker recording the resolution.
     *
     * <p>To get all cells at the input resolution, chain with {@code uncompact}:
     * {@code long[] flat = Compact.uncompact(GridDisk.gridDisk(cellId, k));}</p>
     *
     * @param cellId Center cell ID (unsigned 64 bits)
     * @param k Number of hops (must be >= 0)
     * @return Compacted cells in the disk, sorted in curve order, then the compaction marker
     */
    public static long[] gridDisk(long cellId, int k) {
        return gridDiskInternal(cellId, k, true);
    }

    /**
     * Compute the grid disk of all neighbors (edge + vertex sharing) within k hops.
     * Returns compacted cell IDs including the center cell, sorted in curve
     * order, then a compaction marker recording the resolution.
     *
     * <p>To get all cells at the input resolution, chain with {@code uncompact}:
     * {@code long[] flat = Compact.uncompact(GridDisk.gridDiskVertex(cellId, k));}</p>
     *
     * @param cellId Center cell ID (unsigned 64 bits)
     * @param k Number of hops (must be >= 0)
     * @return Compacted cells in the disk, sorted in curve order, then the compaction marker
     */
    public static long[] gridDiskVertex(long cellId, int k) {
        return gridDiskInternal(cellId, k, false);
    }
}
