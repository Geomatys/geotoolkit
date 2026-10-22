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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.compactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.ORIGIN_SLOTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.QUINTANT_SHIFT;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.QUINTANT_SLOTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.RES30_TAG_BITS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.RESOLUTION_TAGS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.SLOT_COUNTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.S_MASK;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_CELL;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_SLOTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellFirstSlot;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellSlotCount;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.slotToCell;
import org.geotoolkit.dggs.a5.internal.utils.BigInts;

/**
 * Sorted, disjoint, half-open runs of leaf slots [lo, hi), flattened as
 * [lo0, hi0, lo1, hi1, ...]: the form set operations work on.
 *
 * <p>The covering engine. Each cell covers a block of leaf slots (see
 * core/serialization), so a set of cells is a list of sorted, disjoint slot runs:
 * cells are turned into slot runs, set operations merge runs, and runs are
 * turned back into the coarsest cells covering them. Nothing is uncompacted.</p>
 *
 * <p>Port of upstream {@code coverings/types.ts} (type {@code SlotRuns = bigint[]}, here a growable
 * array of unsigned 64 bits slots: {@link #length()}, {@link #get(int)}, {@link #set(int, long)},
 * {@link #push(long, long)}, {@link #setLength(int)} map the TS array operations) and of
 * {@code coverings/slot-runs.ts} (the static functions). The TS type {@code Cells} is {@code long[]}.
 * Slots go up to {@link org.geotoolkit.dggs.a5.internal.core.Serialization#WORLD_SLOTS} &gt; 2^63:
 * every order comparison is unsigned.</p>
 *
 * <p>Instances are not thread-safe; the static functions are.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class SlotRuns {

    private long[] data;
    private int size;

    /**
     * Creates empty slot runs.
     */
    public SlotRuns() {
        data = new long[16];
    }

    /**
     * Creates slot runs with the given values.
     *
     * @param values flattened runs [lo0, hi0, lo1, hi1, ...] (copied)
     */
    public SlotRuns(long... values) {
        data = Arrays.copyOf(values, Math.max(16, values.length));
        size = values.length;
    }

    /**
     * Equivalent of the TS array {@code length}: twice the number of runs.
     *
     * @return number of values
     */
    public int length() {
        return size;
    }

    /**
     * @param index value index
     * @return slot at index (unsigned)
     */
    public long get(int index) {
        return data[Objects.checkIndex(index, size)];
    }

    /**
     * @param index value index
     * @param value slot (unsigned)
     */
    public void set(int index, long value) {
        data[Objects.checkIndex(index, size)] = value;
    }

    /**
     * Equivalent of the TS {@code runs.push(lo, hi)}.
     *
     * @param lo first slot of the run (unsigned)
     * @param hi end slot of the run, exclusive (unsigned)
     */
    public void push(long lo, long hi) {
        if (size + 2 > data.length) {
            data = Arrays.copyOf(data, Math.max(16, data.length * 2));
        }
        data[size++] = lo;
        data[size++] = hi;
    }

    /**
     * Equivalent of the TS {@code runs.length = length}, only for truncation.
     *
     * @param length new length, not greater than the current one
     */
    public void setLength(int length) {
        if (length < 0 || length > size) throw new IllegalArgumentException("Invalid length " + length);
        size = length;
    }

    /**
     * @return a copy of the flattened runs
     */
    public long[] toArray() {
        return Arrays.copyOf(data, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SlotRuns)) return false;
        final SlotRuns other = (SlotRuns) obj;
        return Arrays.equals(data, 0, size, other.data, 0, other.size);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toArray());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(Long.toUnsignedString(data[i], 16));
        }
        return sb.append(']').toString();
    }

    // ---- module slot-runs.ts ----

    /**
     * Merge cells, in the given order, into sorted and disjoint slot runs in {@code out}.
     * With {@code checkOrder}, stop and return false at the first cell starting before
     * the one before it.
     */
    private static boolean mergeCells(long[] cells, SlotRuns out, boolean checkOrder) {
        long previousLo = 0L;
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            if (isCompactionMarker(cell)) continue;
            final long lo = cellFirstSlot(cell);
            final long hi = lo + cellSlotCount(cell);
            if (checkOrder && Long.compareUnsigned(lo, previousLo) < 0) return false;
            previousLo = lo;
            // Drop earlier runs this one contains, then merge with the one before it
            int top = out.length() - 2;
            while (top >= 0 && Long.compareUnsigned(out.get(top), lo) >= 0 && Long.compareUnsigned(out.get(top + 1), hi) <= 0) {
                out.setLength(top);
                top -= 2;
            }
            appendSlotRun(out, lo, hi);
        }
        return true;
    }

    /** Whether a cell's ID lies inside its own slot run, so IDs sort like runs: res 1-29. */
    private static boolean idInsideRun(long cell) {
        final long tag = cell & -cell;
        return cell != WORLD_CELL && tag != RESOLUTION_TAGS[0] && (tag & RES30_TAG_BITS) == 0L;
    }

    /**
     * Cells sorted by a slot inside each cell's run: then disjoint runs come out
     * in order, and a run can only be preceded by runs it contains or that
     * contain it. Below res 30 and above res 0 the ID itself is such a slot, so a
     * native sort of the IDs does it.
     *
     * @return a new sorted array
     */
    private static long[] sortCells(long[] cells) {
        boolean nativ = true;
        for (int i = 0; i < cells.length && nativ; i++) nativ = idInsideRun(cells[i]);
        if (nativ) {
            // BigUint64Array.from(cells).sort(): unsigned
            final long[] sorted = cells.clone();
            BigInts.sortUnsigned(sorted);
            return sorted;
        }

        final Map<Long, Long> starts = new HashMap<>();
        for (int i = 0; i < cells.length; i++) {
            final long cell = cells[i];
            starts.put(cell, isCompactionMarker(cell) ? WORLD_SLOTS : cellFirstSlot(cell));
        }
        // Array.prototype.sort is stable, so is Arrays.sort on objects
        final Long[] boxed = new Long[cells.length];
        for (int i = 0; i < cells.length; i++) boxed[i] = cells[i];
        Arrays.sort(boxed, (a, b) -> {
            final long la = starts.get(a);
            final long lb = starts.get(b);
            return Long.compareUnsigned(la, lb);
        });
        final long[] sorted = new long[cells.length];
        for (int i = 0; i < cells.length; i++) sorted[i] = boxed[i];
        return sorted;
    }

    /**
     * The slot runs covered by a set of cells, sorted and merged. Compaction
     * markers are skipped.
     *
     * <p>Coverings come sorted in curve order, so the cells are first merged as
     * given, checking the order as they go; only input found out of order is
     * sorted, and merged again.</p>
     *
     * @param cells cells (unsigned), compaction markers allowed
     * @return new slot runs
     * @throws IllegalArgumentException if a value is neither an A5 cell ID nor a compaction marker
     */
    public static SlotRuns toSlotRuns(long[] cells) {
        final SlotRuns runs = new SlotRuns();
        if (!mergeCells(cells, runs, true)) {
            runs.setLength(0);
            mergeCells(sortCells(cells), runs, false);
        }
        return runs;
    }

    /**
     * Append a slot run [lo, hi) to sorted runs starting at or before lo, merging if they touch or overlap.
     *
     * @param runs runs to modify
     * @param lo first slot of the run (unsigned)
     * @param hi end slot of the run, exclusive (unsigned)
     */
    public static void appendSlotRun(SlotRuns runs, long lo, long hi) {
        final int last = runs.length() - 1;
        if (last > 0 && Long.compareUnsigned(runs.get(last), lo) >= 0) {
            if (Long.compareUnsigned(hi, runs.get(last)) > 0) runs.set(last, hi);
        } else {
            runs.push(lo, hi);
        }
    }

    /**
     * The coarsest cells covering the slot runs, in curve order. Runs built from
     * cells at resolution r or coarser are aligned to res-r cells, so no cell finer
     * than r is needed.
     *
     * @param runs slot runs
     * @return new array of cells (unsigned)
     */
    public static long[] slotRunsToCells(SlotRuns runs) {
        long[] out = new long[Math.max(16, runs.length())];
        int n = 0;
        for (int i = 0; i < runs.length(); i += 2) {
            long lo = runs.get(i);
            final long hi = runs.get(i + 1);
            while (Long.compareUnsigned(lo, hi) < 0) {
                if (n == out.length) out = Arrays.copyOf(out, n * 2);
                if (lo == 0L && hi == WORLD_SLOTS) {
                    out[n++] = WORLD_CELL;
                    break;
                }
                if ((lo & S_MASK) == 0L) {
                    // Whole origins, then whole quintants
                    // (lo is quintant aligned and below WORLD_SLOTS: the sums below cannot wrap)
                    if ((lo >>> QUINTANT_SHIFT) % 5L == 0L && Long.compareUnsigned(lo + ORIGIN_SLOTS, hi) <= 0) {
                        out[n++] = slotToCell(lo, 0);
                        lo += ORIGIN_SLOTS;
                        continue;
                    }
                    if (Long.compareUnsigned(lo + QUINTANT_SLOTS, hi) <= 0) {
                        out[n++] = slotToCell(lo, 1);
                        lo += QUINTANT_SLOTS;
                        continue;
                    }
                }
                // The coarsest curve-level cell that starts at lo and fits: its span is
                // 4^k slots, at most the alignment of lo and the length of the run (the
                // float log2 can round up, so shrink until it fits)
                // Java: exact integer log2 (as the Rust port): hi - lo may be above 2^63, so
                // numberOfLeadingZeros on the raw bits; the loop below is kept from TS.
                final long offset = lo & S_MASK;
                int bits = offset == 0L ? 56 : Long.numberOfTrailingZeros(offset & -offset);
                final int fit = 63 - Long.numberOfLeadingZeros(hi - lo);
                if (fit < bits) bits = fit;
                // Math.ceil((60 - bits) / 2) with 60 - bits >= 4
                int r = Math.max(2, (60 - bits + 1) / 2);
                while (Long.compareUnsigned(lo + SLOT_COUNTS[r], hi) > 0) r++;
                out[n++] = slotToCell(lo, r);
                lo += SLOT_COUNTS[r];
            }
        }
        return Arrays.copyOf(out, n);
    }

    /**
     * The coarsest cells covering slot runs, in curve order, then the compaction marker for {@code resolution}.
     *
     * @param runs slot runs
     * @param resolution covering resolution, no compaction marker if negative
     * @return new array of cells (unsigned)
     */
    public static long[] slotRunsToCovering(SlotRuns runs, int resolution) {
        long[] cells = slotRunsToCells(runs);
        if (resolution >= 0) {
            cells = Arrays.copyOf(cells, cells.length + 1);
            cells[cells.length - 1] = compactionMarker(resolution);
        }
        return cells;
    }

    /**
     * Compact cells without appending a compaction marker: the coarsest cells covering
     * them, sorted in curve order. For internal use on intermediate results.
     *
     * @param cells cells (unsigned)
     * @return new array of cells (unsigned)
     * @throws IllegalArgumentException if a value is neither an A5 cell ID nor a compaction marker
     */
    public static long[] compactCells(long[] cells) {
        return slotRunsToCells(toSlotRuns(cells));
    }

    /**
     * Compact cells, at resolution {@code resolution} or coarser, into a covering: the
     * coarsest cells covering them, sorted in curve order, then the compaction
     * marker for {@code resolution}. The resolution is given, so an empty fill still
     * records it.
     *
     * @param cells cells (unsigned)
     * @param resolution covering resolution
     * @return new array of cells (unsigned)
     * @throws IllegalArgumentException if a value is neither an A5 cell ID nor a compaction marker
     */
    public static long[] toCovering(long[] cells, int resolution) {
        return slotRunsToCovering(toSlotRuns(cells), resolution);
    }
}
