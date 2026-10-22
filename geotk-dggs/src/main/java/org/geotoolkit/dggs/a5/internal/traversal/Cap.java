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

import org.geotoolkit.dggs.a5.internal.core.A5Cell;
import org.geotoolkit.dggs.a5.internal.coverings.SlotRuns;
import org.geotoolkit.dggs.a5.internal.projections.DodecahedronProjection;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import org.geotoolkit.dggs.a5.internal.utils.LongList;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToSpherical;
import static org.geotoolkit.dggs.a5.internal.core.CellInfo.cellArea;
import static org.geotoolkit.dggs.a5.internal.core.Constants.AUTHALIC_RADIUS_EARTH;
import static org.geotoolkit.dggs.a5.internal.core.FaceAdjacency.walkFaces;
import static org.geotoolkit.dggs.a5.internal.core.Origins.haversine;
import static org.geotoolkit.dggs.a5.internal.core.Origins.origins;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.FIRST_CURVE_RESOLUTION;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.cellToParent;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.deserialize;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.getResolution;
import static org.geotoolkit.dggs.a5.internal.core.Serialization.serialize;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.slotRunsToCovering;
import static org.geotoolkit.dggs.a5.internal.coverings.SlotRuns.toCovering;
import static org.geotoolkit.dggs.a5.internal.traversal.CurveDescent.INSIDE;
import static org.geotoolkit.dggs.a5.internal.traversal.CurveDescent.OUTSIDE;
import static org.geotoolkit.dggs.a5.internal.traversal.CurveDescent.SPLIT;
import static org.geotoolkit.dggs.a5.internal.traversal.CurveDescent.descendInCurveOrder;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.cellIdsToTriples;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellCenter;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.walkTripleCells;

/**
 * Spherical caps: the cells within a great-circle radius of a cell.
 *
 * <p>Port of upstream {@code traversal/cap.ts}. TS {@code BigUint64Array} results
 * are {@code long[]} (unsigned 64 bits ids). Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Cap {

    private static final DodecahedronProjection dodecahedron = new DodecahedronProjection();

    /** Safety factor applied to equal-area circle radius to get conservative circumradius estimate */
    private static final double CELL_RADIUS_SAFETY_FACTOR = 2.0;

    /** Minimum cells in the cap before hierarchical subdivision is worthwhile */
    private static final int MIN_CELLS_FOR_SUBDIVISION = 20;

    private Cap() {
    }

    /**
     * Convert a distance in meters to a haversine threshold value.
     * Since haversine h = sin²(d/2R) is monotonic in d for d ∈ [0, πR],
     * comparing h ≤ threshold is equivalent to comparing dist ≤ radius
     * but avoids the asin/sqrt per point.
     *
     * <p>Java: uses {@link StrictMath#sin} (fdlibm, the algorithm of V8's {@code Math.sin})
     * for bit-identical thresholds with upstream; {@code Math.sin} differs by one ulp
     * for some inputs (fixture {@code meters = 10000000}).</p>
     *
     * @param meters distance in meters
     * @return haversine value
     */
    public static double metersToH(double meters) {
        final double s = StrictMath.sin(meters / (2 * AUTHALIC_RADIUS_EARTH));
        return s * s;
    }

    /*
     * Estimate a conservative cell circumradius in meters for a given resolution.
     *
     * Derived from: cellRadius = SAFETY * sqrt(cellArea / π)
     *             = SAFETY * sqrt(4πR² / (numCells × π))
     *             = SAFETY × 2R / sqrt(numCells)
     *
     * For r ≥ 1: numCells = 60 × 4^(r-1), so sqrt(numCells) = 2√15 × 2^(r-1)
     * giving: cellRadius(r) = BASE_CELL_RADIUS / 2^(r-1)
     * i.e. the radius exactly halves at each resolution level.
     */
    private static final double BASE_CELL_RADIUS = (CELL_RADIUS_SAFETY_FACTOR * AUTHALIC_RADIUS_EARTH) / Math.sqrt(15);
    private static final double[] _cellRadius = new double[31];
    static {
        _cellRadius[0] = (CELL_RADIUS_SAFETY_FACTOR * AUTHALIC_RADIUS_EARTH) / Math.sqrt(3);
        for (int r = 1; r <= 30; r++) {
            _cellRadius[r] = BASE_CELL_RADIUS / (1 << (r - 1));
        }
    }

    /**
     * Estimate a conservative cell circumradius in meters for a given resolution.
     *
     * @param resolution resolution (0-30)
     * @return radius in meters
     */
    public static double estimateCellRadius(int resolution) {
        return _cellRadius[resolution];
    }

    /**
     * Pick the coarsest resolution where the cap contains enough cells
     * to make hierarchical subdivision worthwhile.
     *
     * @param radius cap radius in meters
     * @param targetRes target resolution
     * @return coarse resolution, at most {@code targetRes}
     */
    public static int pickCoarseResolution(double radius, int targetRes) {
        // Spherical cap area in m²: 2πR²(1 − cos(r/R)) computed as 4πR²·sin²(r/2R),
        // which keeps full precision for small radii where 1 − cos cancels
        // (Java: StrictMath for bit-identical results with V8, see metersToH)
        final double halfAngleSin = StrictMath.sin(radius / (2 * AUTHALIC_RADIUS_EARTH));
        final double capAreaM2 = 4 * Math.PI * AUTHALIC_RADIUS_EARTH * AUTHALIC_RADIUS_EARTH * halfAngleSin * halfAngleSin;

        for (int res = FIRST_CURVE_RESOLUTION; res <= targetRes; res++) {
            final double cArea = cellArea(res);
            if (capAreaM2 / cArea >= MIN_CELLS_FOR_SUBDIVISION) {
                return res;
            }
        }
        return targetRes; // No coarsening benefit
    }

    /**
     * BFS at the cap's coarse resolution (1 or above) from `startCell` through every
     * cell whose center lies within `hExpanded` of `center`, returning those cells
     * (the ring just outside lies beyond every threshold the descent applies).
     *
     * Runs in triple space (cells as flat (originId, quintant, x, y, z)): neighbors
     * (edge and vertex) come from the per-flavor triple deltas plus the boundary
     * delta tables, and a cell's center straight from its triple.
     */
    private static IntList coarseCapCells(long startCell, double[] center, double hExpanded) {
        final int curveLevel = getResolution(startCell) - FIRST_CURVE_RESOLUTION + 1;
        final int maxRow = (1 << curveLevel) - 1;
        final IntList cells = cellIdsToTriples(new long[]{startCell});
        walkTripleCells(cells.slice(), maxRow, (originId, q, x, y, z) -> {
            final boolean within = haversine(center, tripleCellCenter(originId, q, x, y, z, curveLevel, maxRow)) <= hExpanded;
            if (within) cells.push(originId, q, x, y, z);
            return within;
        });
        return cells;
    }

    /**
     * Compute all cells within a great-circle radius, returning a compacted result
     * (mix of resolutions), with a compaction marker recording the resolution.
     *
     * <p>Descends the hierarchy (see curve-descent): starts at a coarse resolution and
     * subdivides boundary cells, keeping interior cells at coarser resolutions.
     * Only cells whose centers fall within the radius are included.</p>
     *
     * <p>Distance comparisons use the haversine intermediate value h = sin²(d/2R)
     * directly, avoiding the expensive asin/sqrt per cell. Pre-computed h
     * thresholds replace km-based distance checks.</p>
     *
     * <p>To get all cells at the target resolution, chain with {@code uncompact}:
     * {@code long[] flat = Compact.uncompact(Cap.sphericalCap(cellId, 50_000));}</p>
     *
     * @param cellId Center cell ID (unsigned 64 bits)
     * @param radius Radius in meters
     * @return Compacted cells sorted in curve order, then the compaction marker
     */
    public static long[] sphericalCap(long cellId, double radius) {
        final int targetRes = getResolution(cellId);
        final int coarseRes = pickCoarseResolution(radius, targetRes);
        final double[] center = cellToSpherical(cellId);

        // Pre-compute haversine thresholds: the exact radius, and the radius expanded
        // so the coarse BFS captures every overlapping cell
        final double hRadius = metersToH(radius);
        final double hExpanded = metersToH(radius + estimateCellRadius(coarseRes));
        final long startCell = coarseRes < targetRes ? cellToParent(cellId, coarseRes) : cellId;

        if (coarseRes == 0) {
            // The target is resolution 0: the cells are the 12 dodecahedron faces
            final LongList result = new LongList();
            for (int face : walkFaces(new int[]{deserialize(startCell).origin().id()}, face -> nearFace(center, face, hExpanded))) {
                if (nearFace(center, face, hRadius)) result.push(faceCell(face));
            }
            return toCovering(result.toArray(), targetRes);
        }

        // Descend from the coarse cells to targetRes, classifying each cell by
        // comparing haversine(center, cell) against pre-computed h thresholds:
        // - Interior (h ≤ hInner): keep whole, all descendants inside
        // - Outside  (h > hOuter): discard, no descendants inside
        // - Boundary: split into children
        // At the target resolution both thresholds are the exact radius.
        final double[] hInner = new double[targetRes + 1];
        final double[] hOuter = new double[targetRes + 1];
        for (int res = coarseRes; res <= targetRes; res++) {
            final double cellRadius = estimateCellRadius(res);
            final boolean last = res == targetRes;
            hInner[res] = last ? hRadius : radius > cellRadius ? metersToH(radius - cellRadius) : -1;
            hOuter[res] = last ? hRadius : metersToH(radius + cellRadius);
        }
        final SlotRuns runs = new SlotRuns();
        descendInCurveOrder(
                coarseCapCells(startCell, center, hExpanded),
                coarseRes - FIRST_CURVE_RESOLUTION + 1,
                targetRes,
                (originId, res, face, slot) -> {
                    final double h = haversine(center, dodecahedron.inverse(face, originId));
                    return h <= hInner[res] ? INSIDE : h <= hOuter[res] ? SPLIT : OUTSIDE;
                },
                runs);
        return slotRunsToCovering(runs, targetRes);
    }

    /** Upstream closure {@code faceCell}: the resolution 0 cell of a face. */
    private static long faceCell(int face) {
        return serialize(new A5Cell(origins.get(face), 0, 0L, 0));
    }

    /** Upstream closure {@code near}: whether the face cell center is within {@code h} of {@code center}. */
    private static boolean nearFace(double[] center, int face, double h) {
        return haversine(center, cellToSpherical(faceCell(face))) <= h;
    }
}
