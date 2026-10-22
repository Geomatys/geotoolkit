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
import java.util.HashSet;
import java.util.function.LongConsumer;
import org.geotoolkit.dggs.a5.internal.core.A5Cell;
import org.geotoolkit.dggs.a5.internal.core.Cell.CellShape;
import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape;
import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape.SegmentClip;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import org.geotoolkit.dggs.a5.internal.projections.DodecahedronProjection;
import org.geotoolkit.dggs.a5.internal.utils.LongList;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellIntersectsSegment;
import static org.geotoolkit.dggs.a5.internal.core.Cell.lastCellShape;
import static org.geotoolkit.dggs.a5.internal.core.Cell.lastProjection;
import static org.geotoolkit.dggs.a5.internal.core.Cell.lonLatToCell;
import static org.geotoolkit.dggs.a5.internal.core.Cell.sphericalToCell;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.fromLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toSpherical;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.walkFaces;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonVertices;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleFlavor;
import static org.geotoolkit.dggs.a5.internal.traversal.Cap.estimateCellRadius;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.cellIdsToTriples;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellToId;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.walkTripleCells;
import static org.geotoolkit.dggs.a5.internal.utils.GreatCircle.sampleGreatCircleArc;

/**
 * Lines: the cells a path of great-circle arcs touches.
 *
 * <p>Port of upstream {@code traversal/line.ts}. The module scratch arrays
 * ({@code faceA}, {@code faceB}) are local to each {@code tracePath} call: thread-safe.
 * The TS {@code CellShape} type is {@link CellShape} and {@code SegmentClip}
 * is {@link SegmentClip}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Line {

    private static final DodecahedronProjection dodecahedron = new DodecahedronProjection();

    /**
     * Visitor of {@link #tracePath}: upstream {@code (cell: bigint, arc: number) => void}.
     */
    @FunctionalInterface
    public interface PathVisitor {
        /**
         * @param cell cell id (unsigned 64 bits)
         * @param arc index of the arc of the path
         */
        void visit(long cell, int arc);
    }

    private Line() {
    }

    /**
     * Resolution 0 version of the sub-segment BFS below: the cells are the 12
     * dodecahedron faces, adjacent across their edges.
     */
    private static void traceFaces(long cellA, long cellB, double[] a, double[] b, LongConsumer addCell) {
        walkFaces(new int[]{deserialize(cellA).origin().id(), deserialize(cellB).origin().id()}, face -> {
            final long cell = serialize(new A5Cell(origins.get(face), 0, 0L, 0));
            if (!cellIntersectsSegment(cell, a, b)) return false;
            addCell.accept(cell);
            return true;
        });
    }

    // Tolerance on where (as a fraction of the sub-segment) the part in one cell
    // ends and the part in the next begins, and how far (as a fraction of the
    // edge) the crossing must be from the edge's ends for no third cell to touch it
    private static final double SHARED_EDGE_EPS = 1e-9;
    private static final double SHARED_EDGE_MARGIN = 1e-6;

    /**
     * Visit every cell a path of great-circle arcs touches, exactly.
     *
     * @see #tracePath(double[][], boolean, int, PathVisitor, boolean)
     */
    public static void tracePath(double[][] points, boolean closed, int resolution, PathVisitor visit) {
        tracePath(points, closed, resolution, visit, true);
    }

    /**
     * Visit every cell a path of great-circle arcs touches, arc by arc and in order
     * along each arc, with the index of the arc (a cell may be visited more than
     * once). The path joins consecutive `points`, and the last back to the first
     * when `closed`. With `exact` false only the cells holding the samples are
     * visited, which can miss a cell whose corner an arc clips between samples.
     *
     * <p>Each arc is sampled at half-cell-radius intervals. A pair of consecutive
     * samples within one cell needs nothing more: cells are convex and the
     * sub-segment between them is short enough to be straight (projected onto the
     * cell's Face). Between two cells, clipping the sub-segment to their pentagons
     * usually shows it crossing straight from one into the other, or clipping one
     * cell between them; otherwise a strict local search finds every cell whose
     * pentagon it touches.</p>
     *
     * <p>The search runs in triple space: a cell's neighbors come from its flavor's
     * triple deltas plus the boundary delta tables, and its pentagon straight from
     * its triple, so a candidate is never decoded and only touched cells are
     * encoded.</p>
     *
     * @param points LonLat [longitude, latitude] points in degrees
     * @param closed whether the last point joins back to the first
     * @param resolution resolution of the cells
     * @param visit receives each touched cell and the index of its arc
     * @param exact false to visit only the cells holding the samples (default true)
     */
    public static void tracePath(double[][] points, boolean closed, int resolution, PathVisitor visit, boolean exact) {
        final Trace tr = new Trace(resolution, visit);

        // Each point once: on the sphere, as a vector, and its cell
        final int n = points.length;
        final double[][] pointSpherical = new double[n][];
        final double[][] pointVecs = new double[n][];
        final long[] pointCells = new long[n];
        final CellShape[] pointShapes = new CellShape[n];
        final double[][] pointFaces = new double[n][];
        for (int i = 0; i < n; i++) {
            pointSpherical[i] = fromLonLat(points[i]);
            pointVecs[i] = toCartesian(pointSpherical[i]);
            pointCells[i] = sphericalToCell(pointSpherical[i], resolution);
            // Only the exact trace uses the shapes
            pointShapes[i] = exact ? lastCellShape(pointCells[i]) : null;
            final CellShape shape = pointShapes[i];
            pointFaces[i] = shape == null ? null : lastProjection(pointSpherical[i], shape.originId());
        }

        final int arcs = closed ? n : n - 1;
        for (int arc = 0; arc < arcs; arc++) {
            final int end = (arc + 1) % n;
            // Sample the great-circle at half-cell-radius spacing, endpoints included
            final double[][] interior = sampleGreatCircleArc(pointVecs[arc], pointVecs[end], tr.sampleInterval);
            final int last = interior.length + 1;

            long cellA = pointCells[arc];
            tr.shapeA = pointShapes[arc];
            tr.faceOfA = pointFaces[arc];
            tr.b = pointSpherical[arc];
            visit.visit(cellA, arc);
            // Walk pairwise. Each (P_j, P_{j+1}) sub-segment is short enough that its
            // projection onto any nearby cell's Face is essentially straight, so we
            // can use exact 2D segment-vs-pentagon intersection.
            for (int j = 1; j <= last; j++) {
                tr.a = tr.b;
                // sampleAt(j)
                tr.b = j == 0 ? pointSpherical[arc] : j == last ? pointSpherical[end] : toSpherical(interior[j - 1]);
                final long cellB = j == last ? pointCells[end] : sphericalToCell(tr.b, resolution);
                tr.shapeB = j == last ? pointShapes[end] : exact ? lastCellShape(cellB) : null;
                tr.faceOfB = j == last ? pointFaces[end] : faceOf(tr.b, tr.shapeB);
                visit.visit(cellB, arc);
                final boolean search = cellA != cellB && exact;
                if (search && resolution == 0) {
                    final int arcIndex = arc;
                    traceFaces(cellA, cellB, toLonLat(tr.a), toLonLat(tr.b), cell -> visit.visit(cell, arcIndex));
                } else if (search) {
                    Arrays.fill(tr.faceA, null);
                    Arrays.fill(tr.faceB, null);
                    if (tr.shapeA == null || tr.shapeB == null || !tr.settle(cellA, tr.shapeA, cellB, tr.shapeB, arc)) {
                        tr.searchSubsegment(cellA, cellB, arc);
                    }
                }
                cellA = cellB;
                tr.shapeA = tr.shapeB;
                tr.faceOfA = tr.faceOfB;
            }
        }
    }

    /** Upstream closure {@code faceOf}. */
    private static double[] faceOf(double[] point, CellShape shape) {
        return shape == null ? null : lastProjection(point, shape.originId());
    }

    /**
     * Whether the sub-segment, clipped to each cell it passes through in turn,
     * hands over cleanly: from a (in the first part) to b (in the last), each part
     * ends where the next begins, at a point well inside an edge, so no other
     * cell meets the sub-segment there.
     */
    private static boolean handsOver(SegmentClip... parts) {
        double prevEnd = 0;
        for (int i = 0; i < parts.length; i++) {
            final SegmentClip part = parts[i];
            if (part == null) return false;
            if (i == 0 ? part.start() > SHARED_EDGE_EPS : Math.abs(part.start() - prevEnd) > SHARED_EDGE_EPS) return false;
            if (i == parts.length - 1) return part.end() >= 1 - SHARED_EDGE_EPS;
            if (part.exitEdgeT() <= SHARED_EDGE_MARGIN || part.exitEdgeT() >= 1 - SHARED_EDGE_MARGIN) return false;
            prevEnd = part.end();
        }
        return false;
    }

    /**
     * The state of one {@code tracePath} call shared by its upstream closures
     * ({@code project}, {@code pentagonOf}, {@code touches}, {@code settle},
     * {@code searchSubsegment}), and the upstream module scratch arrays.
     */
    private static final class Trace {
        final int resolution;
        final PathVisitor visit;
        final double sampleInterval;
        final int curveLevel;
        final int maxRow;

        // Scratch: the current sub-segment's endpoints projected onto each face, filled on demand
        final double[][] faceA = new double[origins.size()][];
        final double[][] faceB = new double[origins.size()][];

        // The current sub-segment, on the sphere, with each end's cell pentagon and
        // origin and its own projection there, as the lookup of its cell made them
        double[] a;
        CellShape shapeA = null;
        CellShape shapeB = null;
        double[] b;
        // Each sub-segment end as its cell's lookup projected it, if it did: the
        // projections below reuse them rather than projecting again
        double[] faceOfA = null;
        double[] faceOfB = null;

        Trace(int resolution, PathVisitor visit) {
            this.resolution = resolution;
            this.visit = visit;
            this.sampleInterval = estimateCellRadius(resolution) * 0.5;
            this.curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
            // Unused at resolution 0 (curve level -1)
            this.maxRow = curveLevel < 0 ? 0 : (1 << curveLevel) - 1;
        }

        void project(int originId) {
            if (faceA[originId] == null) {
                faceA[originId] =
                        faceOfA != null && shapeA != null && originId == shapeA.originId()
                        ? faceOfA
                        : dodecahedron.forward(a, originId);
                faceB[originId] =
                        faceOfB != null && shapeB != null && originId == shapeB.originId()
                        ? faceOfB
                        : dodecahedron.forward(b, originId);
            }
        }

        PentagonShape pentagonOf(int quintant, Triple triple) {
            return getPentagonVertices(curveLevel, quintant, triple, tripleFlavor(triple, maxRow));
        }

        boolean touches(int originId, int quintant, Triple triple) {
            project(originId);
            return pentagonOf(quintant, triple).intersectsSegment(faceA[originId], faceB[originId]);
        }

        /**
         * Settle the sub-segment from cell A to cell B (on one origin) without the
         * full search. It usually runs straight from A into B; failing that, it
         * usually clips one cell C between them, found at the middle of the gap and
         * then visited. False sends the sub-segment to the full search.
         */
        boolean settle(long cellA, CellShape shapeA, long cellB, CellShape shapeB, int arc) {
            final int originId = shapeA.originId();
            if (shapeB.originId() != originId) return false;
            project(originId);
            final double[] fa = faceA[originId];
            final double[] fb = faceB[originId];
            final SegmentClip inA = shapeA.pentagon().clipSegment(fa, fb);
            final SegmentClip inB = shapeB.pentagon().clipSegment(fa, fb);
            if (handsOver(inA, inB)) return true;
            if (inA == null || inB == null || inB.start() <= inA.end()) return false;
            final double t = (inA.end() + inB.start()) / 2;
            final double[] mid = Vec3.normalize(Vec3.create(), Vec3.lerp(Vec3.create(), toCartesian(a), toCartesian(b), t));
            final long cellC = sphericalToCell(toSpherical(mid), resolution);
            final CellShape shapeC = lastCellShape(cellC);
            if (shapeC == null || shapeC.originId() != originId || cellC == cellA || cellC == cellB) return false;
            if (!handsOver(inA, shapeC.pentagon().clipSegment(fa, fb), inB)) return false;
            visit.visit(cellC, arc);
            return true;
        }

        /**
         * Strict local search: walk out from A and B, keeping every cell whose
         * pentagon the sub-segment crosses. Terminates as soon as no new touching
         * cells are found — typically 1–2 hops, since a sub-segment ≤ cellRadius/2
         * reaches at most a couple of cells beyond its endpoint cells.
         */
        void searchSubsegment(long cellA, long cellB, int arc) {
            walkTripleCells(cellIdsToTriples(new long[]{cellA, cellB}), maxRow, (originId, quintant, x, y, z) -> {
                if (!touches(originId, quintant, new Triple(x, y, z))) return false;
                visit.visit(tripleCellToId(originId, quintant, x, y, z, curveLevel, resolution), arc);
                return true;
            });
        }
    }

    /**
     * Trace cells along a polyline defined by a sequence of waypoints.
     *
     * <p>Consecutive waypoints are connected with great-circle arcs, traced by
     * `tracePath`: every cell whose pentagon an arc touches. Cells at waypoint
     * junctions are deduplicated.</p>
     *
     * <p>Pass `[start, end]` for a simple two-point line segment.</p>
     *
     * @param waypoints LonLat [longitude, latitude] waypoints in degrees
     * @param resolution resolution of the cells
     * @return Array of unique cell IDs along the polyline, in order
     */
    public static long[] lineStringToCells(double[][] waypoints, int resolution) {
        if (waypoints.length == 0) return new long[0];
        if (waypoints.length == 1) return new long[]{lonLatToCell(waypoints[0], resolution)};

        final HashSet<Long> seen = new HashSet<>();
        final LongList result = new LongList();
        tracePath(waypoints, false, resolution, (cell, arc) -> {
            if (seen.add(cell)) {
                result.push(cell);
            }
        });
        return result.toArray();
    }
}
