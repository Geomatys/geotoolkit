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
package org.geotoolkit.dggs.a5.internal.regions;

import java.util.HashMap;
import org.geotoolkit.dggs.a5.internal.coverings.SlotRuns;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.Boundary;
import org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.BoundaryNeighbors;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToSpherical;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.QUINTANT_SHIFT;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.SLOT_COUNTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.S_MASK;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellFirstSlot;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.slotToCell;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.appendSlotRun;
import static org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon.pointInPreparedPolygon;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleFlavor;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleToS;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.sToTriple;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.boundaryNeighbors;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.emitsBoundaryCell;
import static org.geotoolkit.dggs.a5.internal.regions.PolygonBoundary.insideNextTo;
import static org.geotoolkit.dggs.a5.internal.traversal.Neighbors.NEIGHBOR_DELTAS;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.QUINTANT_ORIENTATION;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.QUINTANT_PREFIX;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.TRIPLE_QUINTANT_BY_ID_ORDER;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.fillQuintantTables;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.forEachTripleNeighbor;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellCenter;
import static org.geotoolkit.dggs.a5.internal.utils.BigInts.sortUnsigned;

/**
 * Polygon fill by curve runs. Within a quintant consecutive cells on the curve
 * are neighbors, or at most a step over one or two cells. So the band of
 * boundary cells plus one ring of their neighbors splits each quintant's
 * stretch of the curve (a range of slots) into runs that lie wholly inside or
 * wholly outside the polygon: a step over the boundary would have to land in
 * the band. One probe classifies a run, and an inside run is emitted whole, as
 * a slot run (see coverings/slot-runs), so the interior costs O(boundary), not
 * O(area).
 *
 * <p>Port of upstream {@code regions/curve-runs.ts}. Slots are unsigned 64 bits {@code long}
 * (compared with {@code Long.compareUnsigned}, the band sorted with {@code BigInts.sortUnsigned}).
 * The TS {@code -1n} "no band slot" sentinel is {@link #NONE} ({@code -1L}): no flagged slot
 * can be all ones, slots being below {@code WORLD_SLOTS}. The TS closures sharing the band
 * state live in a private per-call object. Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CurveRuns {

    // Band slots carry two flags: EMIT (the cell is in the output) and RING.
    private static final int EMIT = 1;
    private static final int RING = 2;

    /** Upstream {@code -1n}: no neighboring band slot. */
    private static final long NONE = -1L;

    private CurveRuns() {
    }

    // Cells are ordered on the curve by the leaf slots they occupy (see core/serialization).

    /** The slot of a cell given in triple space. */
    private static long tripleSlot(int originId, int quintant, int x, int y, int z, int curveLevel, int unitShift) {
        final int i = originId * 5 + quintant;
        return QUINTANT_PREFIX[i] | (tripleToS(new Triple(x, y, z), curveLevel, QUINTANT_ORIENTATION[i]) << unitShift);
    }

    /**
     * Fill a polygon by curve runs, given its classified boundary and the boundary
     * cells as flat triples. {@code capHoldsQuintant} says whether the polygon might
     * swallow a quintant whole (one holding no band cells at all). Returns the
     * cells inside as sorted slot runs.
     *
     * @param boundary the classified boundary
     * @param triples the boundary cells as flat triples (originId, quintant, x, y, z)
     * @param resolution target resolution (at least FIRST_CURVE_RESOLUTION)
     * @param overlapping whether the containment is 'overlapping'
     * @param capHoldsQuintant whether the polygon might swallow a quintant whole
     * @return the cells inside, as sorted slot runs
     */
    public static SlotRuns fillByCurveRuns(Boundary boundary, IntList triples, int resolution, boolean overlapping,
            boolean capHoldsQuintant) {
        return new Fill(boundary, triples, resolution, overlapping, capHoldsQuintant).run();
    }

    /** State shared by the upstream closures of {@code fillByCurveRuns}. */
    private static final class Fill {
        private final Boundary boundary;
        private final IntList triples;
        private final int resolution;
        private final boolean overlapping;
        private final boolean capHoldsQuintant;
        private final int curveLevel;
        private final int maxRow;
        private final long unit;
        private final int unitShift;
        // Below resolution 30 a slot has zero low bits to hold the flags; at 30 a map does.
        private final boolean packed;
        private final HashMap<Long, Integer> flagMap = new HashMap<>();
        // Ring cells by flagged slot (as their offset into ringCells), with their class
        private final HashMap<Long, Integer> ringBySlot = new HashMap<>();
        private IntList ringCells;
        private byte[] ringInside;
        private final SlotRuns out = new SlotRuns();

        Fill(Boundary boundary, IntList triples, int resolution, boolean overlapping, boolean capHoldsQuintant) {
            this.boundary = boundary;
            this.triples = triples;
            this.resolution = resolution;
            this.overlapping = overlapping;
            this.capHoldsQuintant = capHoldsQuintant;
            this.curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
            this.maxRow = (1 << curveLevel) - 1;
            this.unit = SLOT_COUNTS[resolution];
            this.unitShift = 58 - 2 * curveLevel;
            this.packed = unitShift >= 2;
        }

        private long withFlags(long slot, int flags) {
            if (packed) return slot | flags;
            flagMap.put(slot, flags);
            return slot;
        }

        private int flagsOf(long slot) {
            return packed ? (int) (slot & 3L) : flagMap.get(slot);
        }

        private long slotOf(long slot) {
            return packed ? slot & ~3L : slot;
        }

        SlotRuns run() {
            // One ring of neighbors (edge and vertex, across quintant edges too), edge neighbors first
            final BoundaryNeighbors neighbors = boundaryNeighbors(triples,
                    (b, c, visit) -> forEachTripleNeighbor(b.get(c), b.get(c + 1), b.get(c + 2), b.get(c + 3), b.get(c + 4), maxRow, true, visit),
                    (b, c, visit) -> forEachTripleNeighbor(b.get(c), b.get(c + 1), b.get(c + 2), b.get(c + 3), b.get(c + 4), maxRow, false, visit));
            ringCells = neighbors.cells();
            final IntList parents = neighbors.parents();

            fillQuintantTables();

            final int nBoundary = boundary.cells().length;
            final int nBand = nBoundary + ringCells.length() / 5;
            final long[] slots = new long[nBand];
            for (int i = 0; i < nBoundary; i++) {
                slots[i] = withFlags(cellFirstSlot(boundary.cells()[i]), emitsBoundaryCell(boundary, i, overlapping) ? EMIT : 0);
            }
            ringInside = new byte[ringCells.length() / 5];
            for (int c = 0, i = nBoundary; c < ringCells.length(); c += 5, i++) {
                final IntList r = ringCells;
                final double[] center = toCartesian(tripleCellCenter(r.get(c), r.get(c + 1), r.get(c + 2), r.get(c + 3), r.get(c + 4), curveLevel, maxRow));
                final boolean inside = insideNextTo(boundary, center, parents.get(c / 5));
                if (inside) ringInside[c / 5] = 1;
                final long slot = withFlags(
                        tripleSlot(r.get(c), r.get(c + 1), r.get(c + 2), r.get(c + 3), r.get(c + 4), curveLevel, unitShift),
                        inside ? EMIT | RING : RING);
                slots[i] = slot;
                ringBySlot.put(slot, c);
            }
            sortUnsigned(slots);

            // Walk each quintant's slots in curve order, emitting the inside band cells and
            // runs as they come, so the output is sorted.
            int i = 0;
            for (int q = 0; q < 60; q++) {
                // Skip straight to the next quintant holding band cells, unless whole ones may be inside
                if (!capHoldsQuintant) {
                    if (i >= nBand) break;
                    q = (int) (slots[i] >>> QUINTANT_SHIFT);
                }
                final long qEnd = ((long) (q + 1)) << QUINTANT_SHIFT;
                long cursor = ((long) q) << QUINTANT_SHIFT;
                if (i >= nBand || Long.compareUnsigned(slots[i], qEnd) >= 0) {
                    if (capHoldsQuintant) probeRun(cursor, qEnd, NONE, NONE);
                    continue;
                }
                long prev = NONE;
                for (; i < nBand && Long.compareUnsigned(slots[i], qEnd) < 0; i++) {
                    final long flagged = slots[i];
                    final long slot = slotOf(flagged);
                    if (Long.compareUnsigned(slot, cursor) > 0) probeRun(cursor, slot, prev, flagged);
                    if ((flagsOf(flagged) & EMIT) != 0) appendSlotRun(out, slot, slot + unit);
                    prev = flagged;
                    cursor = slot + unit;
                }
                if (Long.compareUnsigned(cursor, qEnd) < 0) probeRun(cursor, qEnd, prev, NONE);
            }

            return out;
        }

        /**
         * The class of a run cell from a ring cell next to it on the curve, when the
         * two are lattice neighbors: any boundary cell near the run cell would have
         * put it in the ring, so nothing between them can cross the boundary.
         *
         * @return class, or null (TS undefined) when undecided
         */
        private Boolean classFromRing(long slot, long ringSlot) {
            if ((flagsOf(ringSlot) & RING) == 0) return null;
            final int c = ringBySlot.get(ringSlot);
            final int q = (int) (slot >>> QUINTANT_SHIFT);
            final Triple t = sToTriple((slot & S_MASK) >>> unitShift, curveLevel, QUINTANT_ORIENTATION[TRIPLE_QUINTANT_BY_ID_ORDER[q]]);
            final IntList r = ringCells;
            final int dx = t.x() - r.get(c + 2);
            final int dy = t.y() - r.get(c + 3);
            final int dz = t.z() - r.get(c + 4);
            final Triple[] deltas = NEIGHBOR_DELTAS[tripleFlavor(new Triple(r.get(c + 2), r.get(c + 3), r.get(c + 4)), maxRow)].all();
            for (int k = 0; k < deltas.length; k++) {
                final Triple d = deltas[k];
                if (d.x() == dx && d.y() == dy && d.z() == dz) return ringInside[c / 5] == 1;
            }
            return null;
        }

        private void probeRun(long lo, long hi, long prev, long next) {
            Boolean inside = prev != NONE ? classFromRing(lo, prev) : null;
            if (inside == null && next != NONE) inside = classFromRing(hi - unit, next);
            if (inside == null) {
                inside = pointInPreparedPolygon(toCartesian(cellToSpherical(slotToCell(lo, resolution))), boundary.prep());
            }
            if (inside) appendSlotRun(out, lo, hi);
        }
    }
}
