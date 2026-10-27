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
package org.geotoolkit.dggs.a5.internal.coverings;

import java.math.BigInteger;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.cellArea;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.getNumChildren;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.checkedResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.Resolution.coveringResolution;

/**
 * Measures of a set of cells: count and area.
 *
 * <p>Port of upstream {@code coverings/measures.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Measures {

    private Measures() {
    }

    /**
     * The number of cells in a set at its resolution: the length of the
     * uncompacted set. This differs from the length of a compacted array. Each
     * cell given is counted, so overlapping cells are counted more than once; use
     * {@code union} to merge them first.
     *
     * @param cells Set of cells (compacted or not, unsigned)
     * @return Number of cells at the set's resolution (TS bigint: may exceed 2^64)
     * @throws IllegalArgumentException If a value is neither an A5 cell ID nor a compaction marker
     */
    public static BigInteger count(long[] cells) {
        final BigInteger resolution = BigInteger.valueOf(coveringResolution(cells));
        // Children per cell, by cell resolution + 1 (the world cell is -1)
        final BigInteger[] children = new BigInteger[32];
        BigInteger total = BigInteger.ZERO;
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            if (isCompactionMarker(cell)) continue;
            final int r = checkedResolution(cell);
            if (children[r + 1] == null) children[r + 1] = getNumChildren(BigInteger.valueOf(r), resolution);
            total = total.add(children[r + 1]);
        }
        return total;
    }

    /**
     * The area of a set of cells, in square meters. Exact, as A5 cells are
     * equal-area. Overlapping cells each add their area; use {@code union} to merge them
     * first.
     *
     * @param cells Set of cells (compacted or not, unsigned)
     * @return Area in square meters
     * @throws IllegalArgumentException If a value is neither an A5 cell ID nor a compaction marker
     */
    public static double area(long[] cells) {
        double total = 0;
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            if (isCompactionMarker(cell)) continue;
            total += cellArea(checkedResolution(cell));
        }
        return total;
    }
}
