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

import java.util.LinkedHashSet;
import org.geotoolkit.dggs.a5.internal.core.A5Cell;
import org.geotoolkit.dggs.a5.internal.utils.BigInts;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.FACE_ADJACENCY;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.cellIdsToTriples;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.forEachTripleNeighbor;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellToId;

/**
 * Neighbors of a cell across quintant and face boundaries.
 *
 * <p>Port of upstream {@code traversal/global-neighbors.ts} (test-only upstream).
 * The TS options object {@code {edgeOnly}} becomes an overload. Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class GlobalNeighbors {

    private GlobalNeighbors() {
    }

    /**
     * All neighbors (edge and vertex sharing) of a cell.
     *
     * @see #getGlobalCellNeighbors(long, boolean)
     */
    public static long[] getGlobalCellNeighbors(long cellId) {
        return getGlobalCellNeighbors(cellId, false);
    }

    /**
     * Get all neighbors of a cell across quintant and face boundaries: within its
     * quintant the fixed per-flavor triple deltas, and across a quintant edge the
     * boundary delta tables (see {@code forEachTripleNeighbor}).
     *
     * @param cellId cell id (unsigned 64 bits)
     * @param edgeOnly If true, return only edge-sharing neighbors (5 per cell).
     *   Default false returns all neighbors including vertex-only neighbors (6-8 per cell).
     * @return neighbor cell ids, sorted in unsigned ascending order
     */
    public static long[] getGlobalCellNeighbors(long cellId, boolean edgeOnly) {
        final int resolution = getResolution(cellId);
        final LinkedHashSet<Long> neighbors = new LinkedHashSet<>();
        if (resolution == 0) {
            // The cells are the 12 dodecahedron faces, adjacent across their edges
            for (final int[] adj : FACE_ADJACENCY[deserialize(cellId).origin().id()]) {
                neighbors.add(serialize(new A5Cell(origins.get(adj[0]), 0, 0L, 0)));
            }
        } else {
            final int curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
            final IntList t = cellIdsToTriples(new long[]{cellId});
            final int maxRow = (1 << curveLevel) - 1;
            forEachTripleNeighbor(t.get(0), t.get(1), t.get(2), t.get(3), t.get(4), maxRow, edgeOnly, (o, q, nx, ny, nz) -> {
                neighbors.add(tripleCellToId(o, q, nx, ny, nz, curveLevel, resolution));
            });
        }
        final long[] result = new long[neighbors.size()];
        int i = 0;
        for (final Long n : neighbors) result[i++] = n;
        BigInts.sortUnsigned(result);
        return result;
    }
}
