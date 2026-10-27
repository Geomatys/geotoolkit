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

import java.util.Arrays;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell;
import org.geotoolkit.dggs.a5.internal.utils.BigInts;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleInBounds;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleToS;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.sToCell;
import static org.geotoolkit.dggs.a5.internal.traversal.Neighbors.NEIGHBOR_DELTAS;

/**
 * Neighbor finding within one quintant, via triple coordinates and pentagon flavor.
 *
 * <p>Port of upstream {@code traversal/quintant-neighbors.ts} (test-only upstream).
 * The TS options object {@code {edgeOnly}} becomes an overload. Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class QuintantNeighbors {

    private QuintantNeighbors() {
    }

    /**
     * {@link #getCellNeighbors(long, int, Orientation, boolean)} with orientation {@code uv},
     * edge and vertex neighbors.
     */
    public static long[] getCellNeighbors(long s, int resolution) {
        return getCellNeighbors(s, resolution, Orientation.uv, false);
    }

    /**
     * {@link #getCellNeighbors(long, int, Orientation, boolean)} with edge and vertex neighbors.
     */
    public static long[] getCellNeighbors(long s, int resolution, Orientation orientation) {
        return getCellNeighbors(s, resolution, orientation, false);
    }

    /**
     * Neighbor finding via triple coordinates and pentagon flavor.
     *
     * <p>Triple coordinates are orientation-independent — the same geometric cell
     * always has the same triple coords regardless of curve orientation. Only the
     * s-value changes between orientations, so neighbors are found in triple
     * space and converted back to the requested orientation.</p>
     *
     * @param s Cell s-value (curve index)
     * @param resolution Resolution level
     * @param orientation Curve orientation (default: 'uv')
     * @param edgeOnly If true, return only the 5 edge-sharing neighbors.
     *   Default false also returns the 2 vertex-only neighbors.
     * @return Array of neighbor s-values, sorted ascending
     */
    public static long[] getCellNeighbors(long s, int resolution, Orientation orientation, boolean edgeOnly) {
        final Cell cell = sToCell(s, resolution, orientation);
        final Triple triple = cell.triple();
        final int maxRow = (1 << resolution) - 1;
        final Triple[] deltas = edgeOnly ? NEIGHBOR_DELTAS[cell.flavor()].edge() : NEIGHBOR_DELTAS[cell.flavor()].all();
        final long[] neighbors = new long[deltas.length];
        int n = 0;
        for (int i = 0; i < deltas.length; i++) {
            final Triple d = deltas[i];
            final Triple neighbor = new Triple(triple.x() + d.x(), triple.y() + d.y(), triple.z() + d.z());
            if (tripleInBounds(neighbor, maxRow)) neighbors[n++] = tripleToS(neighbor, resolution, orientation);
        }
        BigInts.sortUnsigned(neighbors, 0, n);
        return Arrays.copyOf(neighbors, n);
    }
}
