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
import org.geotoolkit.dggs.a5.internal.core.Origin;
import org.geotoolkit.dggs.a5.internal.core.Origins;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.projections.DodecahedronProjection;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Origins.quintantToSegment;
import static org.geotoolkit.dggs.a5.internal.core.Origins.segmentToQuintant;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.QUINTANT_SHIFT;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonCenter;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleFlavor;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleInBounds;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleToS;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.sToTriple;
import static org.geotoolkit.dggs.a5.internal.traversal.LatticeBoundary.getBoundaryNeighborTriples;
import static org.geotoolkit.dggs.a5.internal.traversal.Neighbors.NEIGHBOR_DELTAS;

/**
 * Cells handled in triple space — (originId, quintant, x, y, z) — by the
 * traversal algorithms that walk many neighboring cells: they key and dedup
 * cells as plain integers and encode a cell to its ID only when it is output.
 *
 * <p>Port of upstream {@code traversal/triple-cells.ts}. Flat triple arrays (TS
 * {@code number[]}) are {@link IntList}. Cell keys (TS numbers up to 2^51) are
 * {@code long}. The quintant tables, filled lazily upstream by
 * {@code fillQuintantTables}, are built in the static initialiser
 * ({@link #fillQuintantTables()} is kept as a no-op); the scratch boundary array is a
 * local: thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class TripleCells {

    /**
     * Receives a cell given in triple space.
     */
    @FunctionalInterface
    public interface TripleCellVisitor {
        void visit(int originId, int quintant, int x, int y, int z);
    }

    /**
     * Tests a cell given in triple space (the {@code expand} callback of
     * {@link #walkTripleCells(IntList, int, TripleCellPredicate)}).
     */
    @FunctionalInterface
    public interface TripleCellPredicate {
        boolean test(int originId, int quintant, int x, int y, int z);
    }

    private static final DodecahedronProjection dodecahedron = new DodecahedronProjection();

    // A cell's key packs the quintant (origin.id * 5 + quintant, < 60), parity,
    // and the low KEY_BITS bits of -x and -z (y follows) into 51 bits, exact as a
    // JS number. Up to curve level 21 the coordinates fit whole; above it,
    // two cells of one quintant share a key only if they are 2^22 rows apart, and a
    // walk holding both would need ~2^21 steps (~10^12 cells for a disk) — far past
    // what fits in memory. Keys are only built, never decoded (division and modulo
    // on doubles are slow).
    private static final int KEY_BITS = 22;
    private static final int KEY_MASK = (1 << KEY_BITS) - 1;
    private static final long KEY_SIDE = 1L << KEY_BITS;

    /**
     * The integer key of a cell, unique among the cells of any one traversal.
     *
     * @return key, in [0, 2^51)
     */
    public static long tripleCellKey(int originId, int quintant, int x, int y, int z) {
        return (((0 - x) & KEY_MASK) * KEY_SIDE + ((0 - z) & KEY_MASK)) * 2
                + x
                + y
                + z
                + (originId * 5 + quintant) * 2 * KEY_SIDE * KEY_SIDE;
    }

    // Each of the 60 quintants, by triple quintant (origin.id * 5 + quintant): its
    // segment, curve orientation and slot prefix (its place in ID order, shifted
    // into the top bits of a slot); and the triple quintant in each place of ID
    // order. Upstream fills them on first use (fillQuintantTables); here they are
    // built eagerly in the static initialiser. Shared arrays: never modify.

    /** Segment of each triple quintant. Shared: never modify. */
    public static final int[] QUINTANT_SEGMENT = new int[60];
    /** Curve orientation of each triple quintant. Shared: never modify. */
    public static final Orientation[] QUINTANT_ORIENTATION = new Orientation[60];
    /** Slot prefix of each triple quintant. Shared: never modify. */
    public static final long[] QUINTANT_PREFIX = new long[60];
    /** Triple quintant in each place of ID order. Shared: never modify. */
    public static final int[] TRIPLE_QUINTANT_BY_ID_ORDER = new int[60];

    static {
        int i = 0;
        for (final Origin origin : origins) {
            for (int quintant = 0; quintant < 5; quintant++) {
                final Origins.SegmentOrientation so = quintantToSegment(quintant, origin);
                final int segment = so.segment();
                final int idOrder = 5 * origin.id() + ((segment - origin.firstQuintant() + 5) % 5);
                QUINTANT_SEGMENT[i] = segment;
                QUINTANT_ORIENTATION[i] = so.orientation();
                QUINTANT_PREFIX[i] = ((long) idOrder) << QUINTANT_SHIFT;
                TRIPLE_QUINTANT_BY_ID_ORDER[idOrder] = 5 * origin.id() + quintant;
                i++;
            }
        }
    }

    private TripleCells() {
    }

    /**
     * Fill the quintant tables above, if not yet filled. No-op in Java: the
     * tables are built in the static initialiser (kept for diff-ability with upstream).
     */
    public static void fillQuintantTables() {
    }

    /**
     * The cell ID of a cell given in triple space.
     *
     * @return cell id (unsigned 64 bits)
     */
    public static long tripleCellToId(int originId, int quintant, int x, int y, int z, int curveLevel, int resolution) {
        final int q = originId * 5 + quintant;
        final long s = tripleToS(new Triple(x, y, z), curveLevel, QUINTANT_ORIENTATION[q]);
        return serialize(new A5Cell(origins.get(originId), QUINTANT_SEGMENT[q], s, resolution));
    }

    /**
     * Decode cell IDs (each at resolution 1 or above) into triple space.
     *
     * @param cellIds cell ids
     * @return new flat (originId, quintant, x, y, z) list
     */
    public static IntList cellIdsToTriples(long[] cellIds) {
        return cellIdsToTriples(cellIds, new IntList(cellIds.length * 5));
    }

    /**
     * Decode cell IDs (each at resolution 1 or above) into triple space, appending
     * them to {@code out} as flat (originId, quintant, x, y, z).
     *
     * @param cellIds cell ids
     * @param out list appended to
     * @return {@code out}
     */
    public static IntList cellIdsToTriples(long[] cellIds, IntList out) {
        for (final long cellId : cellIds) {
            final A5Cell cell = deserialize(cellId);
            final Origin origin = cell.origin();
            final Origins.QuintantOrientation qo = segmentToQuintant(cell.segment(), origin);
            final Triple t = sToTriple(cell.S(), cell.resolution() - FIRST_CURVE_RESOLUTION + 1, qo.orientation());
            out.push(origin.id(), qo.quintant(), t.x(), t.y(), t.z());
        }
        return out;
    }

    /**
     * The center of a cell given in triple space, on the sphere.
     *
     * @return Spherical [theta, phi] in radians (new array)
     */
    public static double[] tripleCellCenter(int originId, int quintant, int x, int y, int z, int curveLevel, int maxRow) {
        final Triple triple = new Triple(x, y, z);
        final double[] face = getPentagonCenter(curveLevel, quintant, triple, tripleFlavor(triple, maxRow));
        return dodecahedron.inverse(face, originId);
    }

    /**
     * Visit every neighbor of a cell given in triple space: within its quintant the
     * fixed per-flavor triple deltas, and, for a cell on a quintant edge (x = 0,
     * z = 0 or y = maxRow), the boundary delta tables. {@code edgeOnly} restricts to the
     * 5 edge-sharing neighbors; otherwise the vertex-only neighbors come too. A
     * neighbor may be visited more than once; visitors deduplicate.
     */
    public static void forEachTripleNeighbor(int originId, int quintant, int x, int y, int z, int maxRow,
            boolean edgeOnly, TripleCellVisitor visit) {
        final Triple triple = new Triple(x, y, z);

        // Within the quintant: the fixed per-flavor deltas
        final int flavor = tripleFlavor(triple, maxRow);
        final Triple[] deltas = edgeOnly ? NEIGHBOR_DELTAS[flavor].edge() : NEIGHBOR_DELTAS[flavor].all();
        for (int i = 0; i < deltas.length; i++) {
            final Triple d = deltas[i];
            final Triple neighbor = new Triple(x + d.x(), y + d.y(), z + d.z());
            if (tripleInBounds(neighbor, maxRow)) visit.visit(originId, quintant, neighbor.x(), neighbor.y(), neighbor.z());
        }

        // Across a quintant edge: the boundary delta tables
        if (x == 0 || z == 0 || y == maxRow) visitBoundary(originId, quintant, triple, maxRow, edgeOnly, false, visit);
    }

    /**
     * Breadth-first walk from {@code seeds} (flat triples) through neighbors (edge and
     * vertex, across quintant edges too): each cell reached is passed to {@code expand}
     * once, and the walk continues from those it returns true for. The seeds count
     * as reached but are not passed to {@code expand}.
     *
     * @param seeds flat (originId, quintant, x, y, z) seeds, not modified
     */
    public static void walkTripleCells(IntList seeds, int maxRow, TripleCellPredicate expand) {
        final HashSet<Long> visited = new HashSet<>();
        for (int c = 0; c < seeds.length(); c += 5) {
            visited.add(tripleCellKey(seeds.get(c), seeds.get(c + 1), seeds.get(c + 2), seeds.get(c + 3), seeds.get(c + 4)));
        }
        IntList frontier = seeds;
        while (frontier.length() > 0) {
            final IntList next = new IntList();
            final TripleCellVisitor visit = (originId, quintant, x, y, z) -> {
                final long key = tripleCellKey(originId, quintant, x, y, z);
                if (!visited.add(key)) return;
                if (expand.test(originId, quintant, x, y, z)) next.push(originId, quintant, x, y, z);
            };
            final IntList f = frontier;
            for (int c = 0; c < f.length(); c += 5) {
                forEachTripleNeighbor(f.get(c), f.get(c + 1), f.get(c + 2), f.get(c + 3), f.get(c + 4), maxRow, false, visit);
            }
            frontier = next;
        }
    }

    /**
     * Visit every lattice neighbor of a cell given in triple space: the 3
     * parity-valid single-axis moves within its quintant (the connectivity
     * {@code tripleSpaceFloodFill} floods by), and, for a cell on a quintant edge, its
     * edge-sharing boundary neighbors — but not the vertex corner, which the
     * lattice moves don't traverse either. A neighbor may be visited more than
     * once; visitors deduplicate.
     */
    public static void forEachLatticeNeighbor(int originId, int quintant, int x, int y, int z, int maxRow, TripleCellVisitor visit) {
        // Within the quintant: +1 on one axis from a parity 0 triple, -1 from parity 1
        final int step = x + y + z == 0 ? 1 : -1;
        if (tripleInBounds(new Triple(x + step, y, z), maxRow)) visit.visit(originId, quintant, x + step, y, z);
        if (tripleInBounds(new Triple(x, y + step, z), maxRow)) visit.visit(originId, quintant, x, y + step, z);
        if (tripleInBounds(new Triple(x, y, z + step), maxRow)) visit.visit(originId, quintant, x, y, z + step);

        // Across a quintant edge: the boundary delta tables
        if (x == 0 || z == 0 || y == maxRow) visitBoundary(originId, quintant, new Triple(x, y, z), maxRow, true, true, visit);
    }

    /** Visit the neighbors of a cell on a quintant edge that lie across it. */
    private static void visitBoundary(int originId, int quintant, Triple triple, int maxRow,
            boolean edgeOnly, boolean skipCorners, TripleCellVisitor visit) {
        // Upstream module scratch array: a local here (thread safety)
        final IntList boundary = new IntList(40);
        final LatticeBoundary.BoundaryTripleContext ctx = new LatticeBoundary.BoundaryTripleContext(
            triple,
            triple.x() + triple.y() + triple.z(),
            quintant,
            origins.get(originId),
            maxRow
        );
        getBoundaryNeighborTriples(ctx, edgeOnly, skipCorners, boundary);
        for (int i = 0; i < boundary.length(); i += 5) {
            visit.visit(boundary.get(i), boundary.get(i + 1), boundary.get(i + 2), boundary.get(i + 3), boundary.get(i + 4));
        }
    }
}
