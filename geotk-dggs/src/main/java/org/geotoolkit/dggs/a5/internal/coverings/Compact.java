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

import static org.geotoolkit.dggs.a5.internal.core.CellInfo.getNumChildren;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellToChildren;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.checkedResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.Resolution.coveringResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toCovering;

/**
 * compact/uncompact for A5 DGGS. A compacted set of cells is a covering: its
 * cells sorted in curve order, then a compaction marker recording the resolution
 * they stand for (see ../core/compaction-marker).
 *
 * <p>Port of upstream {@code coverings/compact.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Compact {

    /** Java: largest array length the JVM reliably allocates. */
    private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    private Compact() {
    }

    /**
     * Expand a set of cells to all their cells at its resolution: the resolution
     * of its compaction marker, or of its finest cell when it has none.
     *
     * <p><b>Ordering property</b>: If the input is sorted in curve order (as {@code compact}
     * returns it), the output is too. All children of a cell form a contiguous,
     * ordered block on the curve, so {@code children(A) < children(B)} whenever {@code A < B}.</p>
     *
     * @param cells cells (unsigned), compaction markers allowed
     * @return the cells at the set's resolution (unsigned)
     * @throws IllegalArgumentException If a value is neither an A5 cell ID nor a compaction marker,
     *         or (Java) if the result does not fit in a Java array
     */
    public static long[] uncompact(long[] cells) {
        final int targetResolution = coveringResolution(cells);

        // First calculate how much space is needed
        double n = 0;
        final byte[] resolutions = new byte[cells.length];
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            if (isCompactionMarker(cell)) continue;
            final int resolution = checkedResolution(cell);
            resolutions[i] = (byte) resolution;
            n += getNumChildren(resolution, targetResolution);
        }
        // Java: TS fails allocating the BigUint64Array (RangeError) when too large
        if (n > MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("Too many cells to uncompact: " + (long) n);
        }

        // Write directly into pre-allocated array
        final long[] result = new long[(int) n];
        int offset = 0;
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            if (isCompactionMarker(cell)) continue;
            final int resolution = resolutions[i];

            final int numChildren = (int) getNumChildren(resolution, targetResolution);
            if (numChildren == 1) {
                result[offset] = cell;
            } else {
                final long[] children = cellToChildren(cell, targetResolution);
                System.arraycopy(children, 0, result, offset, children.length);
            }

            offset += numChildren;
        }

        return result;
    }

    /**
     * Compact a set of cells: replace every complete group of siblings by their
     * parent, recursively, and append a compaction marker recording the resolution of
     * the input's finest cell, which {@code uncompact} expands back to.
     *
     * @param cells Array of cell indices to compact (unsigned)
     * @return Compacted cells sorted in curve order, then the compaction marker
     * @throws IllegalArgumentException If a value is neither an A5 cell ID nor a compaction marker
     */
    public static long[] compact(long[] cells) {
        return toCovering(cells, coveringResolution(cells));
    }
}
