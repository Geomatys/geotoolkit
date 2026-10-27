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

import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.compactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.QUINTANT_SHIFT;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_CELL;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_SLOTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellToChildren;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getRes0Cells;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.slotToCell;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.appendSlotRun;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.slotRunsToCells;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toCovering;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toSlotRuns;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Java only tests of {@link SlotRuns} (upstream has no dedicated test file): the growable
 * structure, and the unsigned handling of slots above 2^63.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SlotRunsTest {

    @Test
    void appendSlotRun_merges_touching_runs() {
        final SlotRuns runs = new SlotRuns();
        appendSlotRun(runs, 0, 4);
        appendSlotRun(runs, 4, 8);
        appendSlotRun(runs, 6, 7);
        appendSlotRun(runs, 10, 12);
        assertEquals(new SlotRuns(0, 8, 10, 12), runs);
        // slots above 2^63
        final long q59 = 59L << QUINTANT_SHIFT;
        appendSlotRun(runs, q59, q59 + 16);
        appendSlotRun(runs, q59 + 16, WORLD_SLOTS);
        assertArrayEquals(new long[] {0, 8, 10, 12, q59, WORLD_SLOTS}, runs.toArray());
        for (int i = 0; i < 40; i++) runs.push(i, i + 1);
        assertEquals(86, runs.length());
        runs.setLength(4);
        assertEquals(new SlotRuns(0, 8, 10, 12), runs);
    }

    @Test
    void world_and_res0_cells() {
        assertArrayEquals(new long[] {WORLD_CELL}, slotRunsToCells(new SlotRuns(0, WORLD_SLOTS)));
        assertEquals(new SlotRuns(0, WORLD_SLOTS), toSlotRuns(new long[] {WORLD_CELL}));
        // The 12 res-0 cells, in reverse order, compact to the world cell
        final long[] res0 = getRes0Cells();
        final long[] reversed = new long[res0.length];
        for (int i = 0; i < res0.length; i++) reversed[i] = res0[res0.length - 1 - i];
        assertArrayEquals(new long[] {WORLD_CELL, compactionMarker(0)}, toCovering(reversed, 0));
        // All but the first: the other 11 origins
        final long[] last11 = new long[11];
        System.arraycopy(reversed, 0, last11, 0, 11);
        final long[] expected = new long[12];
        for (int i = 0; i < 11; i++) expected[i] = res0[i + 1];
        expected[11] = compactionMarker(0);
        assertArrayEquals(expected, toCovering(last11, 0));
    }

    @Test
    void cells_above_2_pow_63_sort_unsigned() {
        final long q59 = slotToCell(59L << QUINTANT_SHIFT, 1);
        final long q0 = slotToCell(0L, 1);
        final long[] children59 = cellToChildren(q59, 3);
        final long[] children0 = cellToChildren(q0, 3);
        // Out of order: forces the native unsigned sort
        final long[] cells = new long[children59.length + children0.length];
        for (int i = 0; i < children59.length; i++) cells[i] = children59[children59.length - 1 - i];
        System.arraycopy(children0, 0, cells, children59.length, children0.length);
        assertArrayEquals(new long[] {q0, q59, compactionMarker(3)}, toCovering(cells, 3));
        // Out of order with a res-0 cell: forces the sort by first slot
        final long origin0 = getRes0Cells()[0];
        final long[] c = cellToChildren(q59, 2);
        final long[] mixed = {c[3], origin0, c[0], c[2], c[1]};
        final long[] covering = toCovering(mixed, 3);
        assertArrayEquals(new long[] {origin0, q59, compactionMarker(3)}, covering);
        assertArrayEquals(new long[] {0L, 5L << QUINTANT_SHIFT, 59L << QUINTANT_SHIFT, WORLD_SLOTS}, toSlotRuns(mixed).toArray());
    }
}
