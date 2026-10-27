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
import org.geotoolkit.dggs.a5.internal.coverings.SlotRuns;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.CurveNode;
import org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.CurveNodeResult;
import org.geotoolkit.dggs.a5.internal.math.Vec2;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.SLOT_COUNTS;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonCenter;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.appendSlotRun;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.curveChild;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.tripleToCurveNode;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.QUINTANT_ORIENTATION;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.QUINTANT_PREFIX;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.fillQuintantTables;

/**
 * A region found by descending the cell hierarchy: a cell wholly inside is kept
 * whole, one wholly outside is dropped, and the rest split, so the work follows
 * the region's boundary rather than its area. The descent runs in curve order,
 * stepping the curve one digit per level (see lattice curveChild), so the cells
 * come out as sorted slot runs, with no cell IDs to encode and nothing to sort.
 *
 * <p>Port of upstream {@code traversal/curve-descent.ts}. The descent scratch
 * state is local to each call: thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CurveDescent {

    /** How a cell lies relative to the region: see {@link CurveDescentClassifier}. */
    public static final int OUTSIDE = 0;
    public static final int INSIDE = 1;
    public static final int SPLIT = 2;

    /**
     * Classifies a cell of the descent by its center, given in its face's frame:
     * INSIDE keeps the cell whole (all its cells at the target resolution are in
     * the region), OUTSIDE drops it (none are), and SPLIT descends into its
     * children. At the target resolution it must decide, INSIDE or OUTSIDE.
     *
     * <p>{@code center} is reused between calls: read it, don't keep it.</p>
     */
    @FunctionalInterface
    public interface CurveDescentClassifier {
        /**
         * @param originId origin (face) id
         * @param resolution resolution of the cell
         * @param center Face [x, y] of the cell center, in its face's frame (reused, do not keep)
         * @param slot first slot of the cell (unsigned 64 bits)
         * @return {@link #OUTSIDE}, {@link #INSIDE} or {@link #SPLIT}
         */
        int classify(int originId, int resolution, double[] center, long slot);
    }

    /**
     * The descent's inputs, shared by every level of `descend`, and its scratch:
     * the triple and descent state of the cell being visited at each level (a
     * level's are only overwritten once its cell's children are done)
     */
    private static final class Descent {
        CurveDescentClassifier classify;
        /** Curve level of the target resolution */
        int targetLevel;
        int originId;
        int quintant;
        Orientation orientation;
        /** Output, as sorted slot runs */
        SlotRuns runs;
        /** Triples [x, y, z] by level (curveChild out params). */
        int[][] triples;
        CurveNode[] nodes;
        double[] center;
    }

    private CurveDescent() {
    }

    /**
     * Descend from `starts` — non-overlapping cells at one curve level, as flat
     * triples (originId, quintant, x, y, z), in any order — to `resolution`,
     * appending the cells of the region `classify` describes to `runs`, as slot
     * runs in curve order.
     *
     * @param starts flat (originId, quintant, x, y, z) start cells, not modified
     * @param startLevel curve level of the start cells
     * @param resolution target resolution
     * @param classify region classifier
     * @param runs output slot runs, appended to
     */
    public static void descendInCurveOrder(IntList starts, int startLevel, int resolution,
            CurveDescentClassifier classify, SlotRuns runs) {
        fillQuintantTables();
        final int count = starts.length() / 5;
        final long[] slots = new long[count];
        final CurveNodeResult[] states = new CurveNodeResult[count];
        final int shift = 58 - 2 * startLevel;
        for (int i = 0; i < count; i++) {
            final int c = 5 * i;
            final int q = starts.get(c) * 5 + starts.get(c + 1);
            final Triple triple = new Triple(starts.get(c + 2), starts.get(c + 3), starts.get(c + 4));
            states[i] = tripleToCurveNode(triple, startLevel, QUINTANT_ORIENTATION[q]);
            slots[i] = QUINTANT_PREFIX[q] | (states[i].s() << shift);
        }
        final Integer[] order = new Integer[count];
        for (int i = 0; i < count; i++) order[i] = i;
        // Slots are unsigned 64 bits
        Arrays.sort(order, (a, b) -> Long.compareUnsigned(slots[a], slots[b]));

        final int targetLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
        final Descent d = new Descent();
        d.classify = classify;
        d.targetLevel = targetLevel;
        d.originId = 0;
        d.quintant = 0;
        d.orientation = Orientation.uv;
        d.runs = runs;
        d.triples = new int[targetLevel + 1][];
        d.nodes = new CurveNode[targetLevel + 1];
        d.center = Vec2.create();
        for (int level = 0; level <= targetLevel; level++) {
            d.triples[level] = new int[3];
            d.nodes[level] = new CurveNode(0, 0, 0, 0);
        }
        for (int k = 0; k < count; k++) {
            final int i = order[k];
            final int c = 5 * i;
            final int q = starts.get(c) * 5 + starts.get(c + 1);
            d.originId = starts.get(c);
            d.quintant = starts.get(c + 1);
            d.orientation = QUINTANT_ORIENTATION[q];
            final int[] triple = d.triples[startLevel];
            triple[0] = starts.get(c + 2);
            triple[1] = starts.get(c + 3);
            triple[2] = starts.get(c + 4);
            final CurveNode node = d.nodes[startLevel];
            final CurveNode startNode = states[i].node();
            node.motif = startNode.motif;
            node.flip = startNode.flip;
            node.posA = startNode.posA;
            node.posB = startNode.posB;
            descend(d, startLevel, states[i].flavor(), slots[i]);
        }
    }

    /** Visit the cell at curve `level` (its triple and descent state in the scratch) with the given flavor and first slot. */
    private static void descend(Descent d, int level, int flavor, long slot) {
        final int resolution = level + FIRST_CURVE_RESOLUTION - 1;
        final int[] t = d.triples[level];
        getPentagonCenter(level, d.quintant, new Triple(t[0], t[1], t[2]), flavor, d.center);
        final int kind = d.classify.classify(d.originId, resolution, d.center, slot);
        if (kind == INSIDE) {
            appendSlotRun(d.runs, slot, slot + SLOT_COUNTS[resolution]);
            return;
        }
        if (kind == OUTSIDE || level == d.targetLevel) return;
        // The children's slots follow one another in curve order
        final CurveNode node = d.nodes[level];
        final int[] childTriple = d.triples[level + 1];
        final CurveNode childNode = d.nodes[level + 1];
        final long childSlots = SLOT_COUNTS[resolution + 1];
        long childSlot = slot;
        for (int digit = 0; digit < 4; digit++) {
            final int childFlavor = curveChild(node, digit, level + 1, d.orientation, childTriple, childNode);
            descend(d, level + 1, childFlavor, childSlot);
            childSlot += childSlots;
        }
    }
}
