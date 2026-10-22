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
package org.geotoolkit.dggs.a5.internal.core;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.geometry.PentagonShape;
import org.geotoolkit.dggs.a5.internal.lattice.Orientation;
import org.geotoolkit.dggs.a5.internal.lattice.Triple;
import org.geotoolkit.dggs.a5.internal.math.Mat2;
import org.geotoolkit.dggs.a5.internal.math.Vec2;
import org.geotoolkit.dggs.a5.internal.projections.DodecahedronProjection;
import static org.geotoolkit.dggs.a5.internal.core.CompactionMarker.isCompactionMarker;
import static org.geotoolkit.dggs.a5.internal.core.Constants.PI_OVER_5;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.FaceToIJ;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.fromLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.normalizeLongitudes;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toLonLat;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toPolar;
import static org.geotoolkit.dggs.a5.internal.core.Origins.clockwiseJump;
import static org.geotoolkit.dggs.a5.internal.core.Origins.clockwiseStep;
import static org.geotoolkit.dggs.a5.internal.core.Origins.findNearestOrigin;
import static org.geotoolkit.dggs.a5.internal.core.Origins.findNearestOrigins;
import static org.geotoolkit.dggs.a5.internal.core.Origins.haversine;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Origins.quintantToSegment;
import static org.geotoolkit.dggs.a5.internal.core.Origins.segmentToQuintant;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.MAX_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.RES30_QUINTANTS;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.WORLD_CELL;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.cellMarginScaled;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getFaceVertices;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonCenter;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getPentagonVertices;
import static org.geotoolkit.dggs.a5.internal.core.Tiling.getQuintantPolar;
import static org.geotoolkit.dggs.a5.internal.lattice.Curve.roundToTriple;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.NO_S;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleFlavor;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleInBounds;
import static org.geotoolkit.dggs.a5.internal.lattice.Triples.tripleToS;
import static org.geotoolkit.dggs.a5.internal.lattice.lsystem.LSystem.sToCell;
import static org.geotoolkit.dggs.a5.internal.traversal.Neighbors.NEIGHBOR_DELTAS;

/**
 * Conversions between cells and geographic coordinates.
 *
 * <p>Port of upstream {@code core/cell.ts}. Not to be confused with the old v0.2
 * {@code org.geotoolkit.dggs.a5.internal.Cell} class nor with {@code lattice.lsystem.Cell}.</p>
 *
 * <p><b>Thread safety:</b> the upstream module-level single-entry cache of the most recent
 * lookup ({@code _lastResult}, {@code _lastPoint}, {@code _lastPointOrigin}, {@code _lastPointFace})
 * is held per thread in a {@link ThreadLocal}: {@link #lastProjection(double[], int)} and
 * {@link #lastCellShape(long)} answer for the most recent {@link #sphericalToCell(double[], int)}
 * call <em>of the calling thread</em> (the reference-equality test on the input array works as
 * upstream within one thread). The module scratch buffers ({@code rotation}, {@code _scaledPoint})
 * are locals. The shared {@link DodecahedronProjection} is immutable.</p>
 *
 * <p>Coordinate arrays: {@code LonLat} = [longitude, latitude] in degrees, {@code Spherical} =
 * [theta, phi] in radians (A5 rotated authalic frame), {@code Face} = [x, y].</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Cell {

    // Reuse these objects to avoid allocation (immutable, thread-safe)
    private static final DodecahedronProjection dodecahedron = new DodecahedronProjection();

    /**
     * The cell of the most recent successful lookup, with its pre-computed pentagon and origin.
     * Upstream: the object type of {@code _lastResult}, returned by {@code lastCellShape}.
     *
     * @param cellId cell id (unsigned 64 bits)
     * @param pentagon pentagon of the cell in the face coordinates of the origin (do not modify)
     * @param originId origin id
     * @param resolution resolution of the cell
     */
    public record CellShape(long cellId, PentagonShape pentagon, int originId, int resolution) {
    }

    /**
     * Single-entry cache of the most recent successful lookup. Speeds up
     * dense-sample workloads (polygon boundary tracing, line tracing) where
     * consecutive calls often land in the same cell. The cache stores the
     * pre-computed pentagon + origin so the hit-test is just one projection
     * + one pentagon containment check.
     * Upstream module-level variables, held per thread here.
     */
    private static final class LastState {
        CellShape _lastResult = null;
        // The most recent point sphericalToCell projected onto a face, and where it landed
        double[] _lastPoint = null;
        int _lastPointOrigin = -1;
        double[] _lastPointFace = null;
    }

    private static final ThreadLocal<LastState> LAST = ThreadLocal.withInitial(LastState::new);

    /**
     * Options of {@link #cellToBoundary(long, CellToBoundaryOptions)}.
     *
     * @param closedRing Pass true to close the ring with the first point (default true)
     * @param segments Number of segments to use for each edge. Pass {@link #AUTO} to use the
     *        resolution of the cell (default {@code AUTO}, upstream {@code 'auto'})
     */
    public record CellToBoundaryOptions(boolean closedRing, int segments) {
        /** Upstream {@code segments: 'auto'}. */
        public static final int AUTO = -1;
        /** Upstream defaults {@code {closedRing: true, segments: 'auto'}}. */
        public static final CellToBoundaryOptions DEFAULT = new CellToBoundaryOptions(true, AUTO);

        /**
         * @param segments Number of segments to use for each edge, or {@link #AUTO}
         */
        public CellToBoundaryOptions(int segments) {
            this(true, segments);
        }

        /**
         * @param closedRing Pass true to close the ring with the first point
         */
        public CellToBoundaryOptions(boolean closedRing) {
            this(closedRing, AUTO);
        }
    }

    private Cell() {
    }

    /**
     * Where {@code spherical} lands on {@code originId}'s face, when the most recent
     * {@code sphericalToCell} call (of this thread) already projected it there, else null.
     *
     * @param spherical Spherical [theta, phi], compared by reference
     * @param originId origin id
     * @return Face [x, y] (shared, do not modify) or null
     */
    public static double[] lastProjection(double[] spherical, int originId) {
        final LastState st = LAST.get();
        return spherical == st._lastPoint && originId == st._lastPointOrigin ? st._lastPointFace : null;
    }

    /**
     * The pentagon and origin of {@code cellId} when it is the cell the most recent
     * {@code sphericalToCell} call (of this thread) returned, else null: lets dense-sample loops reuse the
     * geometry that lookup already built.
     *
     * @param cellId cell id
     * @return cell shape or null
     */
    public static CellShape lastCellShape(long cellId) {
        final CellShape lastResult = LAST.get()._lastResult;
        return lastResult != null && lastResult.cellId() == cellId ? lastResult : null;
    }

    /**
     * @param lonLat LonLat [longitude, latitude] in degrees
     * @param resolution resolution (-1 for the world cell)
     * @return cell id
     */
    public static long lonLatToCell(double[] lonLat, int resolution) {
        return sphericalToCell(fromLonLat(lonLat), resolution);
    }

    // Haversine of the angle from a face center to its edge midpoints, atan(2)/2:
    // a point closer than that to a face center is on that face.
    private static final double FACE_INRADIUS_HAVERSINE = 0.07467459582398002;

    /**
     * Like {@code lonLatToCell}, but accepts a point already in A5's internal spherical
     * representation (rotated authalic frame, as produced by {@code fromLonLat} or
     * {@code toSpherical(authalicCartesian)}). Skips the redundant authalic
     * inverse/forward round-trip in dense-sample loops where the input already
     * comes from the authalic Cartesian space (e.g. polygon-fill boundary slerp).
     *
     * @param spherical Spherical [theta, phi] in radians
     * @param resolution resolution (-1 for the world cell)
     * @return cell id
     */
    public static long sphericalToCell(double[] spherical, int resolution) {
        // Resolution -1 represents WORLD_CELL, which covers the entire world
        if (resolution == -1) {
            return WORLD_CELL;
        }

        if (resolution == 0) {
            // The dodecahedron face containing the point is exact
            final Origin origin = findNearestOrigin(spherical);
            final double[] dodecPoint = dodecahedron.forward(spherical, origin.id());
            final int quintant = getQuintantPolar(toPolar(dodecPoint));
            final int segment = quintantToSegment(quintant, origin).segment();
            return serialize(new A5Cell(origin, segment, 0L, resolution));
        }

        final LastState st = LAST.get();

        // Try the cached pentagon first — skips the full lookup when consecutive
        // calls land in the same cell (common in dense-sample loops).
        Origin nearestOrigin = null;
        final CellShape _lastResult = st._lastResult;
        if (_lastResult != null && _lastResult.resolution() == resolution) {
            // The projection maps points from other faces into this face's frame
            // too (the antipodal face center onto this one's center), so the cached
            // pentagon only answers for points on its own face: the nearest origin.
            if (haversine(spherical, origins.get(_lastResult.originId()).axis()) >= FACE_INRADIUS_HAVERSINE) {
                nearestOrigin = findNearestOrigin(spherical);
            }
            if (nearestOrigin == null || nearestOrigin.id() == _lastResult.originId()) {
                final double[] projected = dodecahedron.forward(spherical, _lastResult.originId());
                st._lastPoint = spherical;
                st._lastPointOrigin = _lastResult.originId();
                st._lastPointFace = projected;
                if (_lastResult.pentagon().containsPoint(projected) > 0) return _lastResult.cellId();
            }
        }

        // Fast path: locate the containing pentagon directly. Round to the leaf
        // triangle, get the closed-form flavor, and test the pentagon geometrically
        // in the scaled quintant frame; the triangular and pentagonal lattices are
        // not congruent, but the containing pentagon is always the triangle's cell
        // or one of its fixed neighbor deltas (verified exhaustively), so at most
        // one 7-candidate walk resolves it — then a single curve encode.
        final Origin origin = nearestOrigin != null ? nearestOrigin : findNearestOrigin(spherical);
        final double[] dodecPoint = dodecahedron.forward(spherical, origin.id());
        st._lastPoint = spherical;
        st._lastPointOrigin = origin.id();
        st._lastPointFace = dodecPoint;
        final int quintant = getQuintantPolar(toPolar(dodecPoint));
        final CellCandidate best = lookupInQuintant(dodecPoint, origin, quintant, resolution);
        if (best != null && best.margin() > 0) return acceptCandidate(st, best);
        // No strictly-containing pentagon in the assigned frame: the point sits on a
        // cell boundary or within float noise of a quintant/face seam.
        return sphericalToCellBoundary(st, spherical, resolution, origin, quintant, best);
    }

    /**
     * The best cell for {@code dodecPoint} (face frame of {@code origin}) within one quintant:
     * round to the leaf triangle, closed-form flavor, geometric margin, and — when
     * the triangle's cell doesn't strictly contain the point — the best of its
     * fixed neighbor deltas. margin &gt; 0 ⇔ the unique strictly-containing pentagon.
     */
    private record CellCandidate(
            double margin,
            long cellId,
            Triple triple,
            int flavor,
            int quintant,
            int curveLevel,
            int originId,
            int resolution) {
    }

    private static CellCandidate lookupInQuintant(double[] dodecPoint, Origin origin, int quintant, int resolution) {
        // Inlined quintantToSegment (see origin.ts)
        final Orientation[] layout = origin.orientation();
        final int step = layout == clockwiseJump || layout == clockwiseStep ? -1 : 1;
        final int delta = (quintant - origin.firstQuintant() + 5) % 5;
        final int faceRelativeQuintant = (step * delta + 5) % 5;
        final Orientation orientation = layout[faceRelativeQuintant];
        final int segment = (origin.firstQuintant() + faceRelativeQuintant) % 5;

        // Res-30 ids can only encode quintants 0-41 (by design: 64 bits cannot fit
        // res 30 globally, so A5 covers the populous covering). In the unsupported
        // quintants, answer at the finest representable resolution instead — the
        // res-29 cell CONTAINING the point. (Previously the cap lived only in
        // serialize, which swapped in the res-29 parent of a res-30 search result —
        // a cell that fails to contain the query point ~44% of the time there.)
        if (resolution == MAX_RESOLUTION && 5 * origin.id() + ((segment - origin.firstQuintant() + 5) % 5) >= RES30_QUINTANTS) {
            resolution = MAX_RESOLUTION - 1;
        }

        // Scratch for the fast path's scaled quintant-frame point (upstream module scratch)
        final double[] _scaledPoint = Vec2.create();
        Vec2.copy(_scaledPoint, dodecPoint);
        if (quintant != 0) {
            final double[] rotation = Mat2.create();
            Mat2.fromRotation(rotation, -2 * PI_OVER_5 * quintant);
            Vec2.transformMat2(_scaledPoint, _scaledPoint, rotation);
        }
        final int curveLevel = 1 + resolution - FIRST_CURVE_RESOLUTION;
        final double scale = Math.pow(2, curveLevel);
        final double px = _scaledPoint[0] * scale;
        final double py = _scaledPoint[1] * scale;
        final double[] ij = FaceToIJ(new double[]{px, py});

        final Triple base = roundToTriple(ij, curveLevel);
        Triple triple = base;
        final int maxRow = (1 << curveLevel) - 1; // scale - 1
        int flavor = tripleFlavor(base, maxRow);
        double margin = cellMarginScaled(px, py, base.x(), base.y(), flavor);
        if (margin <= 0) {
            // All deltas are relative to the ROUNDED triple (the containing pentagon
            // is always among its fixed neighbors), not to intermediate best cells.
            final Triple[] deltas = NEIGHBOR_DELTAS[flavor].all();
            for (int i = 0; i < deltas.length; i++) {
                final Triple d = deltas[i];
                final Triple neighbor = new Triple(base.x() + d.x(), base.y() + d.y(), base.z() + d.z());
                if (!tripleInBounds(neighbor, maxRow)) continue;
                final int neighborFlavor = tripleFlavor(neighbor, maxRow);
                final double neighborMargin = cellMarginScaled(px, py, neighbor.x(), neighbor.y(), neighborFlavor);
                if (neighborMargin > margin) {
                    triple = neighbor;
                    flavor = neighborFlavor;
                    margin = neighborMargin;
                    if (margin > 0) break;
                }
            }
        }
        final long S = tripleToS(triple, curveLevel, orientation);
        if (S == NO_S) return null;
        final long cellId = serialize(new A5Cell(origin, segment, S, resolution));
        return new CellCandidate(margin, cellId, triple, flavor, quintant, curveLevel, origin.id(), resolution);
    }

    /** Cache the winning pentagon for the dense-sample fast accept and return its id. */
    private static long acceptCandidate(LastState st, CellCandidate c) {
        st._lastResult = new CellShape(
                c.cellId(),
                getPentagonVertices(c.curveLevel(), c.quintant(), c.triple(), c.flavor()),
                c.originId(),
                c.resolution());
        return c.cellId();
    }

    // Tie margin tolerance: containment margins are cross products of unit-scale
    // pentagon edges against coordinates of magnitude up to 2^curveLevel,
    // so their float noise is ~2^(curveLevel - 52); 2^-44 gives a wide
    // safety factor while staying geometrically negligible (cells are unit-size
    // in the scaled frame).
    private static final double TIE_EPS = Math.pow(2, -44);

    /**
     * Boundary resolution: the point has no strictly-containing pentagon in its
     * assigned frame — it lies on a cell edge, or within float noise of a quintant
     * or face seam (where the containing cell belongs to a neighboring frame).
     * Deterministically rerun the same lookup in every frame that could own the
     * point — all 5 quintants of the 3 nearest faces (a dodecahedron vertex joins
     * 3 faces; a face center joins 5 quintants). A strictly-containing pentagon is
     * unique, so the first strict hit wins; if none exists the point is exactly on
     * a boundary shared by the near-best candidates, and the tie-break is the cell
     * that comes FIRST ALONG THE CURVE — the lowest cell id (origin/segment occupy
     * the top id bits in curve order, so numeric order is curve order globally).
     */
    private static long sphericalToCellBoundary(
            LastState st,
            double[] spherical,
            int resolution,
            Origin firstOrigin,
            int firstQuintant,
            CellCandidate first) {
        final List<CellCandidate> candidates = new ArrayList<>();
        if (first != null) candidates.add(first);
        for (final Origin origin : findNearestOrigins(spherical, 3)) {
            final double[] dodecPoint = dodecahedron.forward(spherical, origin.id());
            // Try this origin's assigned quintant first, then its gamma-adjacent
            // neighbors: seam points resolve in the adjacent frame, so this order
            // finds the strict container in 1-2 lookups instead of scanning all 5.
            final int q0 = getQuintantPolar(toPolar(dodecPoint));
            for (final int dq : new int[]{0, 1, 4, 2, 3}) {
                final int quintant = (q0 + dq) % 5;
                if (origin.id() == firstOrigin.id() && quintant == firstQuintant) continue;
                final CellCandidate c = lookupInQuintant(dodecPoint, origin, quintant, resolution);
                if (c == null) continue;
                if (c.margin() > 0) return acceptCandidate(st, c);
                candidates.add(c);
            }
        }
        double best = Double.NEGATIVE_INFINITY;
        for (final CellCandidate c : candidates) if (c.margin() > best) best = c.margin();
        final double eps = TIE_EPS * Math.pow(2, 1 + resolution - FIRST_CURVE_RESOLUTION);
        CellCandidate winner = null;
        for (final CellCandidate c : candidates) {
            if (c.margin() >= best - eps && (winner == null || Long.compareUnsigned(c.cellId(), winner.cellId()) < 0)) winner = c;
        }
        if (winner == null) throw new IllegalStateException("sphericalToCell: no candidate cell found");
        return acceptCandidate(st, winner);
    }

    /**
     * The cell at {@code resolution} containing the center of {@code cell}:
     * {@code sphericalToCell(cellToSpherical(cell), resolution)} without the round trip
     * through the sphere. A cell's center lies on its own face, so the lookup runs
     * in that face's frame. A center within float noise of a cell edge, where the
     * round trip's own noise decides, goes round the sphere so ties resolve exactly
     * as {@code sphericalToCell} resolves them.
     *
     * @param cell cell id
     * @param resolution target resolution
     * @return cell id at the target resolution
     */
    public static long cellCenterToCell(long cell, int resolution) {
        final A5Cell a5cell = deserialize(cell);
        if (resolution >= FIRST_CURVE_RESOLUTION - 1 && a5cell.resolution() >= FIRST_CURVE_RESOLUTION - 1) {
            final Origins.QuintantOrientation qo = segmentToQuintant(a5cell.segment(), a5cell.origin());
            final int quintant = qo.quintant();
            final int curveLevel = a5cell.resolution() - FIRST_CURVE_RESOLUTION + 1;
            final org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell tf = sToCell(a5cell.S(), curveLevel, qo.orientation());
            final double[] center = getPentagonCenter(curveLevel, quintant, tf.triple(), tf.flavor());
            final CellCandidate best = lookupInQuintant(center, a5cell.origin(), getQuintantPolar(toPolar(center)), resolution);
            if (best != null && best.margin() > TIE_EPS * Math.pow(2, best.curveLevel())) return best.cellId();
        }
        return sphericalToCell(cellToSpherical(cell), resolution);
    }

    // TODO move into tiling.ts
    /**
     * Upstream {@code _getPentagon}.
     *
     * @param cell the cell
     * @return pentagon of the cell in the face coordinates of its origin (new shape, except
     *         that resolution 0 returns the face vertices)
     */
    public static PentagonShape getPentagon(A5Cell cell) {
        final long S = cell.S();
        final int segment = cell.segment();
        final Origin origin = cell.origin();
        final int resolution = cell.resolution();
        final Origins.QuintantOrientation qo = segmentToQuintant(segment, origin);
        if (resolution == FIRST_CURVE_RESOLUTION - 2) {
            return getFaceVertices();
        }

        final int curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
        final org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell tf = sToCell(S, curveLevel, qo.orientation());
        return getPentagonVertices(curveLevel, qo.quintant(), tf.triple(), tf.flavor());
    }

    /**
     * @param cell cell id
     * @return Spherical [theta, phi] of the cell center, in radians
     */
    public static double[] cellToSpherical(long cell) {
        final A5Cell a5cell = deserialize(cell);
        final long S = a5cell.S();
        final int segment = a5cell.segment();
        final Origin origin = a5cell.origin();
        final int resolution = a5cell.resolution();
        if (resolution >= FIRST_CURVE_RESOLUTION - 1) {
            // Fast path: the pentagon center is O(1) from (triple, flavor) — no need
            // to construct the pentagon itself.
            final Origins.QuintantOrientation qo = segmentToQuintant(segment, origin);
            final int curveLevel = resolution - FIRST_CURVE_RESOLUTION + 1;
            final org.geotoolkit.dggs.a5.internal.lattice.lsystem.Cell tf = sToCell(S, curveLevel, qo.orientation());
            final double[] center = getPentagonCenter(curveLevel, qo.quintant(), tf.triple(), tf.flavor());
            return dodecahedron.inverse(center, origin.id());
        }
        final PentagonShape pentagon = getPentagon(a5cell);
        return dodecahedron.inverse(pentagon.getCenter(), origin.id());
    }

    /**
     * @param cell cell id
     * @return LonLat [longitude, latitude] of the cell center, in degrees
     */
    public static double[] cellToLonLat(long cell) {
        // WORLD_CELL represents the entire world, return [0, 0] as a reasonable default
        if (cell == WORLD_CELL) {
            return new double[]{0, 0};
        }

        return toLonLat(cellToSpherical(cell));
    }

    /**
     * Boundary with the default options {@code {closedRing: true, segments: 'auto'}}.
     *
     * @param cellId cell id
     * @return boundary as LonLat [longitude, latitude] points in degrees, counter-clockwise
     */
    public static double[][] cellToBoundary(long cellId) {
        return cellToBoundary(cellId, CellToBoundaryOptions.DEFAULT);
    }

    /**
     * @param cellId cell id
     * @param options closed ring and segments options
     * @return boundary as LonLat [longitude, latitude] points in degrees, counter-clockwise.
     *         When the ring is closed, the last point is the same array instance as the first.
     */
    public static double[][] cellToBoundary(long cellId, CellToBoundaryOptions options) {
        final boolean closedRing = options.closedRing();
        int segments = options.segments();
        if (cellId == WORLD_CELL || isCompactionMarker(cellId)) {
            // WORLD_CELL represents the entire world and is unbounded; a compaction marker
            // (recording a covering's resolution) is not a cell at all
            return new double[0][];
        }

        final A5Cell cell = deserialize(cellId);
        if (segments == CellToBoundaryOptions.AUTO) {
            segments = (int) Math.max(1, Math.pow(2, 6 - cell.resolution()));
        }

        final PentagonShape pentagon = getPentagon(cell);

        // Split each edge into segments before projection
        // Important to do before projection to obtain equal area cells
        final PentagonShape splitPentagon = pentagon.splitEdges(segments);
        final double[][] vertices = splitPentagon.getVertices();

        // Unproject to obtain lon/lat coordinates. Fused loop avoids the
        // intermediate `unprojectedVertices` allocation.
        final double[][] boundary = new double[vertices.length][];
        for (int i = 0; i < vertices.length; i++) {
            boundary[i] = toLonLat(dodecahedron.inverse(vertices[i], cell.origin().id()));
        }

        // Normalize longitudes to handle antimeridian crossing
        double[][] normalizedBoundary = normalizeLongitudes(boundary);

        if (closedRing) {
            final double[][] closed = new double[normalizedBoundary.length + 1][];
            System.arraycopy(normalizedBoundary, 0, closed, 0, normalizedBoundary.length);
            closed[normalizedBoundary.length] = normalizedBoundary[0];
            normalizedBoundary = closed;
        }
        // TODO: This is a patch to make the boundary CCW, but we should fix the winding order of the pentagon
        // throughout the whole codebase
        for (int i = 0, j = normalizedBoundary.length - 1; i < j; i++, j--) {
            final double[] tmp = normalizedBoundary[i];
            normalizedBoundary[i] = normalizedBoundary[j];
            normalizedBoundary[j] = tmp;
        }
        return normalizedBoundary;
    }

    /**
     * @param cell the cell
     * @param spherical Spherical [theta, phi] in radians
     * @return value of {@link PentagonShape#containsPoint(double[])} in the cell face frame
     *         (positive inside)
     */
    public static double a5cellContainsPoint(A5Cell cell, double[] spherical) {
        final PentagonShape pentagon = getPentagon(cell);
        final double[] projectedPoint = dodecahedron.forward(spherical, cell.origin().id());
        return pentagon.containsPoint(projectedPoint);
    }

    /**
     * Tests whether the segment between two LonLat points intersects a cell.
     *
     * <p>The test runs entirely in the cell's Face coordinate system: both endpoints
     * are projected via the dodecahedron projection (with face-plane extension for
     * points beyond the face's edge), then checked against the pentagon's straight
     * 2D edges. The segment is treated as a 2D straight line in Face coords —
     * accurate when the segment is short relative to the face (DSEA distortion is
     * negligible at sub-cell scales).</p>
     *
     * @param cellId cell id
     * @param a LonLat [longitude, latitude] in degrees
     * @param b LonLat [longitude, latitude] in degrees
     * @return true if the segment intersects the cell
     */
    public static boolean cellIntersectsSegment(long cellId, double[] a, double[] b) {
        if (cellId == WORLD_CELL) return true;
        final A5Cell cell = deserialize(cellId);
        final PentagonShape pentagon = getPentagon(cell);
        final double[] aFace = dodecahedron.forward(fromLonLat(a), cell.origin().id());
        final double[] bFace = dodecahedron.forward(fromLonLat(b), cell.origin().id());
        return pentagon.intersectsSegment(aFace, bFace);
    }
}
