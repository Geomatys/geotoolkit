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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon;
import org.geotoolkit.dggs.a5.internal.traversal.TripleCells.TripleCellVisitor;
import org.geotoolkit.dggs.a5.internal.utils.IntList;
import org.geotoolkit.dggs.a5.internal.utils.LongList;
import static org.geotoolkit.dggs.a5.internal.core.Cell.cellToSpherical;
import static org.geotoolkit.dggs.a5.internal.core.CoordinateTransforms.toCartesian;
import static org.geotoolkit.dggs.a5.internal.geometry.PreparedPolygon.pointInPreparedPolygon;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.ringWindingSign;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.sphericalTriangleArea;
import static org.geotoolkit.dggs.a5.internal.traversal.Line.tracePath;
import static org.geotoolkit.dggs.a5.internal.traversal.TripleCells.tripleCellKey;

/**
 * The boundary of a polygon in cells: sampled densely along every ring,
 * classified by whether each cell's center is inside, and used to classify the
 * cells next to it without a full point-in-polygon test.
 *
 * <p>Port of upstream {@code regions/polygon-boundary.ts}. TS {@code LonLat[][]} rings are
 * {@code double[][][]} ([ring][vertex][lon, lat] in degrees), {@code Cartesian} vectors are
 * {@code double[3]}. The TS {@code SegmentMap} ({@code Map<bigint, number[]>}) is a
 * {@code HashMap<Long, IntList>}, flat triple arrays ({@code number[]}) are {@link IntList}.
 * Stateless, thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class PolygonBoundary {

    /**
     * Every ring segment, flattened across rings and indexed like the segment map:
     * endpoints, great-circle normal, and the side the polygon interior lies on.
     *
     * @param starts segment start points (Cartesian)
     * @param ends segment end points (Cartesian)
     * @param normals segment great-circle normals (Cartesian)
     * @param signs side of the interior (1 or -1)
     */
    public record Segments(double[][] starts, double[][] ends, double[][] normals, int[] signs) {
    }

    /**
     * The sampled boundary cells (upstream return type of {@code sampleBoundary}).
     *
     * @param cells Cell IDs in the order they were sampled
     * @param set the same cells, as a set
     * @param segmentMap Maps each boundary cell to the indices of the ring segments that
     *        produced it. Segment indices are global across rings (outer ring first, then holes).
     */
    public record SampledBoundary(long[] cells, HashSet<Long> set, HashMap<Long, IntList> segmentMap) {
    }

    /**
     * The polygon's boundary cells, classified, with what's needed to classify their neighbors.
     *
     * @param cells Cell IDs in the order they were sampled
     * @param set the same cells, as a set
     * @param inside 1 when the cell's center is inside the polygon, by index into {@code cells}
     * @param centers Cell centers (Cartesian), by index into {@code cells}
     * @param segmentMap see {@link SampledBoundary#segmentMap()}
     * @param segments the ring segments
     * @param prep the prepared polygon
     */
    public record Boundary(long[] cells, HashSet<Long> set, byte[] inside, double[][] centers,
            HashMap<Long, IntList> segmentMap, Segments segments, PreparedPolygon prep) {
    }

    /**
     * The cells next to the boundary (upstream return type of {@code boundaryNeighbors}).
     *
     * @param cells flat triples (originId, quintant, x, y, z)
     * @param parents for each cell, the index into the boundary of the cell it was first found from
     */
    public record BoundaryNeighbors(IntList cells, IntList parents) {
    }

    /** Calls {@code visit} for neighbors of the cell at offset {@code c} of a flat triple array. */
    @FunctionalInterface
    public interface NeighborWalk {
        /**
         * @param cells flat triples (originId, quintant, x, y, z)
         * @param c offset of the cell in {@code cells}
         * @param visit called for each neighbor
         */
        void walk(IntList cells, int c, TripleCellVisitor visit);
    }

    private static final double CROSSING_EPS = 1e-14;

    private PolygonBoundary() {
    }

    /**
     * The boundary cells: every cell a ring segment (outer ring or hole) touches,
     * each recorded with every segment touching it.
     *
     * @param rings rings of LonLat [longitude, latitude] vertices in degrees (open), outer first
     * @param resolution target resolution
     * @return the sampled boundary
     */
    public static SampledBoundary sampleBoundary(double[][][] rings, int resolution) {
        final LongList cells = new LongList();
        final HashSet<Long> set = new HashSet<>();
        final HashMap<Long, IntList> segmentMap = new HashMap<>();

        int segOffset = 0;
        for (int r = 0; r < rings.length; r++) {
            final int offset = segOffset;
            tracePath(rings[r], true, resolution, (cell, arc) -> recordCell(cells, set, segmentMap, cell, offset + arc), true);
            segOffset += rings[r].length;
        }

        return new SampledBoundary(cells.toArray(), set, segmentMap);
    }

    private static void recordCell(LongList cells, HashSet<Long> set, HashMap<Long, IntList> segmentMap, long cell, int segIdx) {
        if (set.add(cell)) {
            cells.push(cell);
        }
        final IntList existing = segmentMap.get(cell);
        if (existing != null) {
            if (existing.get(existing.length() - 1) != segIdx) existing.push(segIdx);
        } else {
            segmentMap.put(cell, IntList.of(segIdx));
        }
    }

    /**
     * The polygon's ring segments, flattened. The polygon interior lies on the
     * <em>outside</em> of a hole ring, so hole segments get the opposite sign.
     */
    private static Segments ringSegments(double[][][] ringVecsList, PreparedPolygon prep) {
        final List<double[]> starts = new ArrayList<>();
        final List<double[]> ends = new ArrayList<>();
        final List<double[]> normalsList = new ArrayList<>();
        final IntList signs = new IntList();
        for (int r = 0; r < ringVecsList.length; r++) {
            final int sign = (r == 0 ? 1 : -1) * ringWindingSign(ringVecsList[r]);
            final double[][] vecs = ringVecsList[r];
            final double[][] normals = prep.ringNormals()[r];
            for (int i = 0; i < normals.length; i++) {
                starts.add(vecs[i]);
                ends.add(vecs[(i + 1) % vecs.length]);
                normalsList.add(normals[i]);
                signs.push(sign);
            }
        }
        return new Segments(starts.toArray(new double[0][]), ends.toArray(new double[0][]),
                normalsList.toArray(new double[0][]), signs.toArray());
    }

    /**
     * Whether {@code p} lies in the lune of the segment a-&gt;b (normal {@code n} = a × b): its
     * projection onto the great circle falls between a and b.
     */
    private static boolean projectsOntoSegment(double[] p, double[] a, double[] b, double[] n) {
        // n × a points along the arc from a towards b, b × n from b back towards a
        final double fromA =
                p[0] * (n[1] * a[2] - n[2] * a[1]) + p[1] * (n[2] * a[0] - n[0] * a[2]) + p[2] * (n[0] * a[1] - n[1] * a[0]);
        final double fromB =
                p[0] * (b[1] * n[2] - b[2] * n[1]) + p[1] * (b[2] * n[0] - b[0] * n[2]) + p[2] * (b[0] * n[1] - b[1] * n[0]);
        return fromA > 0 && fromB > 0;
    }

    /**
     * Classify the sampled boundary cells by whether their center is inside the
     * polygon.
     *
     * <p>For each cell we know which ring segment(s) sampled it. When all of those
     * segments place the cell on the same side (cheap signed-dot test), that
     * decides it. When they disagree (vertex / concave corner) or the cell wasn't
     * recorded, fall back to full PIP.</p>
     *
     * @param sampled the sampled boundary
     * @param ringVecsList rings as Cartesian vectors, outer first
     * @param prep the prepared polygon
     * @return the classified boundary
     */
    public static Boundary classifyBoundary(SampledBoundary sampled, double[][][] ringVecsList, PreparedPolygon prep) {
        final long[] cells = sampled.cells();
        final HashMap<Long, IntList> segmentMap = sampled.segmentMap();
        final Segments segments = ringSegments(ringVecsList, prep);
        final byte[] inside = new byte[cells.length];
        final double[][] centers = new double[cells.length][];
        for (int c = 0; c < cells.length; c++) {
            final double[] cv = toCartesian(cellToSpherical(cells[c]));
            centers[c] = cv;
            final IntList segs = segmentMap.get(cells[c]);
            if (segs == null) {
                inside[c] = (byte) (pointInPreparedPolygon(cv, prep) ? 1 : 0);
                continue;
            }
            boolean allInside = true;
            boolean anyInside = false;
            boolean ambiguous = false;
            for (int k = 0; k < segs.length(); k++) {
                final int segIdx = segs.get(k);
                final double[] n = segments.normals()[segIdx];
                final double dot = n[0] * cv[0] + n[1] * cv[1] + n[2] * cv[2];
                if (Math.abs(dot) < 1e-14) {
                    ambiguous = true;
                    break;
                } // on segment within float epsilon
                // The side of the segment's great circle only decides when the center
                // projects onto the segment itself, not beyond one of its endpoints
                if (!projectsOntoSegment(cv, segments.starts()[segIdx], segments.ends()[segIdx], n)) {
                    ambiguous = true;
                    break;
                }
                if (dot * segments.signs()[segIdx] > 0) anyInside = true;
                else allInside = false;
            }
            if (ambiguous || (anyInside && !allInside)) {
                inside[c] = (byte) (pointInPreparedPolygon(cv, prep) ? 1 : 0);
            } else {
                inside[c] = (byte) (allInside ? 1 : 0);
            }
        }
        return new Boundary(cells, sampled.set(), inside, centers, segmentMap, segments, prep);
    }

    /**
     * The boundary cells in the output. In 'overlapping' mode the boundary is
     * traced exactly, so every boundary cell touches the polygon — keep them all.
     * In 'center' mode keep those whose center lies inside.
     *
     * @param boundary the classified boundary
     * @param c index into the boundary cells
     * @param overlapping whether the containment is 'overlapping'
     * @return whether the boundary cell is in the output
     */
    public static boolean emitsBoundaryCell(Boundary boundary, int c, boolean overlapping) {
        return overlapping || (boundary.inside()[c] & 0xFF) == 1;
    }

    /**
     * The boundary cells in the output (see {@code emitsBoundaryCell}), as a new array.
     *
     * @param boundary the classified boundary
     * @param overlapping whether the containment is 'overlapping'
     * @return new growable list of cell ids
     */
    public static LongList boundaryOutput(Boundary boundary, boolean overlapping) {
        final LongList out = new LongList();
        for (int c = 0; c < boundary.cells().length; c++) {
            if (emitsBoundaryCell(boundary, c, overlapping)) out.push(boundary.cells()[c]);
        }
        return out;
    }

    /**
     * Parity of the crossings of the short arc p-&gt;q with the given ring segments
     * (proper crossings, by the signs of four triple products), or undefined (null) on a
     * near-degenerate sign.
     */
    private static Boolean arcCrossingParity(double[] p, double[] q, IntList segIdxs, Segments segments) {
        final double abx = p[1] * q[2] - p[2] * q[1];
        final double aby = p[2] * q[0] - p[0] * q[2];
        final double abz = p[0] * q[1] - p[1] * q[0];
        boolean odd = false;
        for (int k = 0; k < segIdxs.length(); k++) {
            final int seg = segIdxs.get(k);
            final double[] c = segments.starts()[seg];
            final double[] d = segments.ends()[seg];
            final double acb = -(abx * c[0] + aby * c[1] + abz * c[2]);
            final double bda = abx * d[0] + aby * d[1] + abz * d[2];
            if (Math.abs(acb) < CROSSING_EPS || Math.abs(bda) < CROSSING_EPS) return null;
            if (acb * bda < 0) continue;
            final double[] cd = segments.normals()[seg];
            final double cbd = -(cd[0] * q[0] + cd[1] * q[1] + cd[2] * q[2]);
            final double dac = cd[0] * p[0] + cd[1] * p[1] + cd[2] * p[2];
            if (Math.abs(cbd) < CROSSING_EPS || Math.abs(dac) < CROSSING_EPS) return null;
            if (acb * cbd > 0 && acb * dac > 0) odd = !odd;
        }
        return odd;
    }

    /**
     * Whether a point next to boundary cell {@code parent} (see {@code boundaryNeighbors}) is
     * inside the polygon: the parent's class, flipped by each of its ring segments
     * crossed on the way. Full PIP only on a near-degenerate crossing.
     *
     * @param boundary the classified boundary
     * @param center Cartesian point
     * @param parent index into the boundary cells
     * @return whether the point is inside the polygon
     */
    public static boolean insideNextTo(Boundary boundary, double[] center, int parent) {
        final IntList segIdxs = boundary.segmentMap().get(boundary.cells()[parent]);
        final Boolean odd = arcCrossingParity(center, boundary.centers()[parent], segIdxs, boundary.segments());
        return odd == null ? pointInPreparedPolygon(center, boundary.prep()) : ((boundary.inside()[parent] & 0xFF) == 1) != odd;
    }

    /**
     * The cells next to the boundary (flat triples), found by {@code walks} in turn, each
     * with the boundary cell it was first found from ({@code parents}, an index into the
     * boundary). Listing edge-neighbor walks first gives every cell an edge-sharing
     * parent when it has one, which {@code insideNextTo} needs: the arc between their
     * centers then crosses no other cell holding boundary samples. A cell found
     * only by a vertex has no boundary cell across any of its edges, which covers
     * every other cell around that vertex.
     *
     * @param boundary boundary cells as flat triples (originId, quintant, x, y, z)
     * @param walks neighbor walks, applied in turn
     * @return the neighbor cells and their parents
     */
    public static BoundaryNeighbors boundaryNeighbors(IntList boundary, NeighborWalk... walks) {
        final HashSet<Long> seen = new HashSet<>();
        for (int c = 0; c < boundary.length(); c += 5) {
            seen.add(tripleCellKey(boundary.get(c), boundary.get(c + 1), boundary.get(c + 2), boundary.get(c + 3), boundary.get(c + 4)));
        }
        final IntList cells = new IntList();
        final IntList parents = new IntList();
        final int[] parent = {0};
        final TripleCellVisitor visit = (originId, quintant, x, y, z) -> {
            final long key = tripleCellKey(originId, quintant, x, y, z);
            if (!seen.add(key)) return;
            cells.push(originId, quintant, x, y, z);
            parents.push(parent[0]);
        };
        for (final NeighborWalk walk : walks) {
            for (int c = 0; c < boundary.length(); c += 5) {
                parent[0] = c / 5;
                walk.walk(boundary, c, visit);
            }
        }
        return new BoundaryNeighbors(cells, parents);
    }

    /**
     * Area of the polygon (outer ring minus holes) on the unit sphere, in steradians.
     *
     * @param ringVecsList rings as Cartesian vectors, outer first
     * @return area in steradians
     */
    public static double polygonArea(double[][][] ringVecsList) {
        double total = 0;
        for (int r = 0; r < ringVecsList.length; r++) {
            // Signed fan from the first vertex: concave rings come out right too
            final double[][] ring = ringVecsList[r];
            double area = 0;
            for (int i = 1; i + 1 < ring.length; i++) area += sphericalTriangleArea(ring[0], ring[i], ring[i + 1]);
            total += r == 0 ? Math.abs(area) : -Math.abs(area);
        }
        return total;
    }
}
