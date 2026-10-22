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

import java.util.function.BinaryOperator;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellFirstSlot;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellFirstSlotUnchecked;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellSlotCount;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.checkedResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.Resolution.coveringResolution;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.appendSlotRun;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.slotRunsToCovering;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toSlotRuns;

/**
 * Set operations on sets of cells: union, intersection, difference, overlap and containment.
 *
 * <p>Port of upstream {@code coverings/set-operations.ts}. Slots are unsigned 64 bits values:
 * every order comparison is unsigned.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class SetOperations {

    private SetOperations() {
    }

    /** Merge two lists of slot runs, keeping slots in either. */
    private static SlotRuns unionSlotRuns(SlotRuns a, SlotRuns b) {
        final SlotRuns out = new SlotRuns();
        int i = 0;
        int j = 0;
        while (i < a.length() || j < b.length()) {
            if (j >= b.length() || (i < a.length() && Long.compareUnsigned(a.get(i), b.get(j)) <= 0)) {
                appendSlotRun(out, a.get(i), a.get(i + 1));
                i += 2;
            } else {
                appendSlotRun(out, b.get(j), b.get(j + 1));
                j += 2;
            }
        }
        return out;
    }

    /** Slots in both lists of slot runs. */
    private static SlotRuns intersectSlotRuns(SlotRuns a, SlotRuns b) {
        final SlotRuns out = new SlotRuns();
        int i = 0;
        int j = 0;
        while (i < a.length() && j < b.length()) {
            final long lo = Long.compareUnsigned(a.get(i), b.get(j)) > 0 ? a.get(i) : b.get(j);
            final long hi = Long.compareUnsigned(a.get(i + 1), b.get(j + 1)) < 0 ? a.get(i + 1) : b.get(j + 1);
            if (Long.compareUnsigned(lo, hi) < 0) out.push(lo, hi);
            if (Long.compareUnsigned(a.get(i + 1), b.get(j + 1)) < 0) i += 2;
            else j += 2;
        }
        return out;
    }

    /** Slots in the first list of slot runs but not the second. */
    private static SlotRuns differenceSlotRuns(SlotRuns a, SlotRuns b) {
        final SlotRuns out = new SlotRuns();
        int j = 0;
        for (int i = 0; i < a.length(); i += 2) {
            long lo = a.get(i);
            final long hi = a.get(i + 1);
            while (j < b.length() && Long.compareUnsigned(b.get(j + 1), lo) <= 0) j += 2;
            int k = j;
            while (k < b.length() && Long.compareUnsigned(b.get(k), hi) < 0) {
                if (Long.compareUnsigned(b.get(k), lo) > 0) out.push(lo, b.get(k));
                if (Long.compareUnsigned(b.get(k + 1), lo) > 0) lo = b.get(k + 1);
                k += 2;
            }
            if (Long.compareUnsigned(lo, hi) < 0) out.push(lo, hi);
        }
        return out;
    }

    /**
     * The resolution of two sets of cells, which must be the same: A5 resolutions
     * don't nest geometrically, so combining sets at different ones has no meaning.
     */
    private static int sameResolution(long[] a, long[] b) {
        final int resolutionA = coveringResolution(a);
        final int resolutionB = coveringResolution(b);
        if (resolutionA != resolutionB) {
            throw new IllegalArgumentException("Cannot combine cells at resolution " + resolutionA + " with cells at resolution " + resolutionB);
        }
        return resolutionA;
    }

    /** Combine two sets of cells as slot runs, compacted at their resolution. */
    private static long[] combine(long[] a, long[] b, BinaryOperator<SlotRuns> operation) {
        final int resolution = sameResolution(a, b);
        return slotRunsToCovering(operation.apply(toSlotRuns(a), toSlotRuns(b)), resolution);
    }

    /**
     * The union of two sets of cells: cells in either. Both sets must be at the
     * same resolution, and the result is compacted at it.
     *
     * @param a First set of cells (compacted or not, unsigned)
     * @param b Second set of cells (compacted or not, unsigned)
     * @return Compacted cells, with a compaction marker recording the resolution
     * @throws IllegalArgumentException If the sets are at different resolutions, or a value is neither an A5 cell ID nor a compaction marker
     */
    public static long[] union(long[] a, long[] b) {
        return combine(a, b, SetOperations::unionSlotRuns);
    }

    /**
     * The intersection of two sets of cells: cells in both. Both sets must be at
     * the same resolution, and the result is compacted at it.
     *
     * @param a First set of cells (compacted or not, unsigned)
     * @param b Second set of cells (compacted or not, unsigned)
     * @return Compacted cells, with a compaction marker recording the resolution
     * @throws IllegalArgumentException If the sets are at different resolutions, or a value is neither an A5 cell ID nor a compaction marker
     */
    public static long[] intersect(long[] a, long[] b) {
        return combine(a, b, SetOperations::intersectSlotRuns);
    }

    /**
     * The difference of two sets of cells: cells in {@code a} but not in {@code b}. Both sets
     * must be at the same resolution, and the result is compacted at it.
     *
     * @param a Set of cells to subtract from (compacted or not, unsigned)
     * @param b Set of cells to subtract (compacted or not, unsigned)
     * @return Compacted cells, with a compaction marker recording the resolution
     * @throws IllegalArgumentException If the sets are at different resolutions, or a value is neither an A5 cell ID nor a compaction marker
     */
    public static long[] difference(long[] a, long[] b) {
        return combine(a, b, SetOperations::differenceSlotRuns);
    }

    /**
     * Check whether two sets of cells share any cell. Both sets must be at the same
     * resolution.
     *
     * @param a First set of cells (compacted or not, unsigned)
     * @param b Second set of cells (compacted or not, unsigned)
     * @return Whether some cell is in both sets
     * @throws IllegalArgumentException If the sets are at different resolutions, or a value is neither an A5 cell ID nor a compaction marker
     */
    public static boolean overlaps(long[] a, long[] b) {
        sameResolution(a, b);
        return intersectSlotRuns(toSlotRuns(a), toSlotRuns(b)).length() > 0;
    }

    /**
     * Check whether a cell is in a set of cells. The cell must be at the set's
     * resolution: A5 cells don't nest geometrically across resolutions, so for a
     * point-in-polygon test pass {@code lonLatToCell(point, resolution)} with the
     * resolution of the set. Uses a binary search, so {@code cells} must be sorted in
     * curve order, as returned by {@code compact} and the other A5 functions.
     *
     * @param cells Sorted set of cells (compacted or not, unsigned)
     * @param cell Cell to test, at the set's resolution (unsigned)
     * @return Whether the cell is in the set
     * @throws IllegalArgumentException If the cell is at a different resolution from the set, or it, or the set's cell the search
     *   lands on, is not an A5 cell ID
     */
    public static boolean contains(long[] cells, long cell) {
        final int resolution = coveringResolution(cells);
        final int cellResolution = checkedResolution(cell);
        if (cellResolution != resolution) {
            throw new IllegalArgumentException("Cannot test a cell at resolution " + cellResolution + " against cells at resolution " + resolution);
        }
        final long slot = cellFirstSlot(cell);
        int n = cells.length;
        if (n > 0 && isCompactionMarker(cells[n - 1])) n--;

        // The last cell starting at or before the cell's first slot is the only one
        // that can hold it. Only that cell is checked to be a cell: the search steps
        // just need an order
        int low = 0;
        int high = n - 1;
        int found = -1;
        while (low <= high) {
            final int mid = (low + high) >> 1;
            if (Long.compareUnsigned(cellFirstSlotUnchecked(cells[mid]), slot) <= 0) {
                found = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return found >= 0 && Long.compareUnsigned(slot, cellFirstSlot(cells[found]) + cellSlotCount(cells[found])) < 0;
    }
}
