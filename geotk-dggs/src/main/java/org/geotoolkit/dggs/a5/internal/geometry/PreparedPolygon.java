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
package org.geotoolkit.dggs.a5.internal.geometry;

import java.util.Arrays;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.pointInSphericalPolygon;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.ringSegmentNormals;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.ringWindingSign;

/**
 * Polygon prepared for repeated containment tests: rings, per-edge great-circle
 * normals, bounding cap, and a reference point for the crossing-number test.
 *
 * <p>The reference point sits just OUTSIDE the cap (angle capAngle + 0.2 from its
 * center) rather than at the antipode: probes come from inside the cap, so
 * the probe->ref arc plane stays well conditioned (|p × ref| >= sin 0.2). The
 * fast path is disabled for very large polygons (cap over ~79°), where that
 * construction can't keep the arc short — those fall back to the winding test.</p>
 *
 * <p>Port of upstream {@code geometry/prepared-polygon.ts}: spherical polygon (with holes)
 * prepared for repeated point-containment tests: bounding-cap prefilter, then a trig-free
 * crossing-number test with the winding-number test as a robust fallback.
 * The upstream {@code PreparedPolygon} interface is this record; the module functions
 * are its static methods. Instances are immutable (arrays must not be modified) and thread-safe.</p>
 *
 * @param ringVecsList rings of Cartesian [x, y, z] unit vectors, outer ring first, then holes (not closed)
 * @param ringNormals per ring, great-circle plane normal of every segment (see {@link SphericalPolygon#ringSegmentNormals(double[][])})
 * @param cap bounding cap of the polygon
 * @param ref Cartesian [x, y, z] reference point outside the polygon for the crossing-number test
 * @param useFast whether the crossing-number test against {@code ref} can be used
 * @param insideRefs Reference points known to be INSIDE the polygon, for polygons whose
 *        bounding cap reaches a hemisphere; empty otherwise (see {@code interiorRefs}).
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public record PreparedPolygon(double[][][] ringVecsList, double[][][] ringNormals, BoundingCap cap,
        double[] ref, boolean useFast, double[][] insideRefs) {

    private static final double[] Z_AXIS = Vec3.fromValues(0, 0, 1);
    private static final double[] X_AXIS = Vec3.fromValues(1, 0, 0);

    /**
     * Point-in-polygon for a polygon with holes: inside the outer ring and
     * outside every hole ring. Winding-number test — robust but O(atan2) per
     * edge; used as the fallback for the crossing-number fast path below.
     */
    private static boolean pointInPolygonRings(double[] point, double[][][] ringVecsList) {
        if (!pointInSphericalPolygon(point, ringVecsList[0])) return false;
        for (int r = 1; r < ringVecsList.length; r++) {
            if (pointInSphericalPolygon(point, ringVecsList[r])) return false;
        }
        return true;
    }

    /**
     * Bounding cap of the polygon: every polygon point is within the cap.
     * The winding-number PIP is blind at the polygon's ANTIPODE (the angle sum is
     * ±2π there too), so distant probes MUST be rejected by the cap first. The cap
     * angle is bounded by the farthest ring vertex plus half the longest edge (any
     * point of an edge arc is within half the edge length of an endpoint).
     *
     * @param center Cartesian [x, y, z] unit vector, center of the cap
     * @param angle Cap half-angle in radians; kept alongside minDot so no lossy acos(minDot) round-trip is needed
     * @param minDot cosine of the cap half-angle
     */
    public record BoundingCap(double[] center, double angle, double minDot) {
    }

    private static BoundingCap boundingCap(double[][][] ringVecsList) {
        final double[] center = Vec3.create();
        for (double[] v : ringVecsList[0]) {
            Vec3.add(center, center, v);
        }
        final double len = Vec3.length(center);
        if (len < 1e-12) return new BoundingCap(Vec3.clone(Z_AXIS), Math.PI, -1);
        Vec3.scale(center, center, 1 / len);

        double maxAngle = 0;
        double maxEdge = 0;
        for (double[][] ringVecs : ringVecsList) {
            for (int i = 0; i < ringVecs.length; i++) {
                final double[] v = ringVecs[i];
                final double[] w = ringVecs[(i + 1) % ringVecs.length];
                maxAngle = Math.max(maxAngle, Vec3.angle(center, v));
                maxEdge = Math.max(maxEdge, Vec3.angle(v, w));
            }
        }
        final double capAngle = Math.min(Math.PI, maxAngle + maxEdge / 2);
        return new BoundingCap(center, capAngle, Math.cos(capAngle));
    }

    /**
     * @param ringVecsList rings of Cartesian [x, y, z] unit vectors, outer ring first, then holes (not closed)
     * @return prepared polygon
     */
    public static PreparedPolygon preparePolygon(double[][][] ringVecsList) {
        final BoundingCap cap = boundingCap(ringVecsList);
        final double[][][] ringNormals = new double[ringVecsList.length][][];
        for (int r = 0; r < ringVecsList.length; r++) {
            ringNormals[r] = ringSegmentNormals(ringVecsList[r]);
        }
        final double capAngle = cap.angle;
        final boolean useFast = cap.minDot > -1 && capAngle < 1.37;
        final double[] c = cap.center;
        // unit vector perpendicular to the cap center
        final double[] perp = Vec3.create();
        Vec3.cross(perp, c, Math.abs(c[2]) < 0.9 ? Z_AXIS : X_AXIS);
        final double perpLen = Vec3.length(perp);
        final double dLen = (perpLen == 0 || Double.isNaN(perpLen)) ? 1 : perpLen; // JS `|| 1`
        final double theta = capAngle + 0.2;
        final double cosT = Math.cos(theta);
        final double sinT = Math.sin(theta) / dLen;
        final double[] ref = Vec3.create();
        Vec3.scale(ref, c, cosT);
        Vec3.scaleAndAdd(ref, ref, perp, sinT);
        final double[][] insideRefs = capAngle >= Math.PI / 2 ? interiorRefs(ringVecsList[0], ringNormals[0]) : new double[0][];
        return new PreparedPolygon(ringVecsList, ringNormals, cap, ref, useFast, insideRefs);
    }

    // How far the interior reference points sit from the ring edge, in radians.
    // Far above CROSSING_EPS so crossing tests against the edge stay well
    // conditioned, far below any cell size so they can't clip another edge.
    private static final double INTERIOR_REF_OFFSET = 1e-7;
    private static final int INTERIOR_REF_COUNT = 3;

    /**
     * Points just inside the midpoints of the outer ring's longest edges.
     *
     * The winding-number test answers "is the point in the region that does not
     * contain the point's own antipode", which is only containment when the
     * polygon lies within a hemisphere. For larger polygons a crossing test is
     * needed, and that needs a reference point whose containment is known. The
     * interior side of each edge follows the ring's winding ({@code ringWindingSign}),
     * the same convention the boundary-cell filter uses. Several are kept in case
     * a probe falls near-degenerately against one.
     */
    private static double[][] interiorRefs(double[][] ring, double[][] normals) {
        final int side = ringWindingSign(ring);
        final double[] lengths = new double[ring.length];
        final Integer[] edges = new Integer[ring.length];
        for (int i = 0; i < ring.length; i++) {
            edges[i] = i;
            lengths[i] = Vec3.angle(ring[i], ring[(i + 1) % ring.length]);
        }
        // stable sort, descending length (same as JS `b.length - a.length` comparator)
        Arrays.sort(edges, (a, b) -> {
            final double d = lengths[b] - lengths[a];
            return d < 0 ? -1 : (d > 0 ? 1 : 0);
        });
        final double[][] refs = new double[Math.min(INTERIOR_REF_COUNT, edges.length)][];
        for (int k = 0; k < Math.min(INTERIOR_REF_COUNT, edges.length); k++) {
            final int i = edges[k];
            final double[] mid = Vec3.create();
            Vec3.add(mid, ring[i], ring[(i + 1) % ring.length]);
            Vec3.normalize(mid, mid);
            // For a counter-clockwise ring, each edge's normal points to its interior side
            final double[] inward = Vec3.normalize(Vec3.create(), normals[i]);
            final double[] refPoint = Vec3.create();
            Vec3.scaleAndAdd(refPoint, mid, inward, side * INTERIOR_REF_OFFSET);
            Vec3.normalize(refPoint, refPoint);
            refs[k] = refPoint;
        }
        return refs;
    }

    private static final double CROSSING_EPS = 1e-14;

    private static Boolean crossingParity(double[] p, PreparedPolygon prep) {
        return crossingParity(p, prep, prep.ref);
    }

    /**
     * Crossing-number containment: count proper crossings of the arc probe->ref
     * with every ring edge (just sign tests — no trig); odd parity = inside
     * ({@code ref} is outside the polygon, and the even-odd rule handles holes for
     * free). Returns null (upstream undefined) on any near-degenerate sign (probe or a vertex on
     * an arc plane) — the caller falls back to the winding test, which also keeps
     * on-edge tie-breaking identical to the previous implementation.
     *
     * The per-vertex loop deliberately uses scalar component math, not vec3: the
     * shared vec3.dot/cross see both plain arrays and Float64Arrays across the
     * codebase, so V8 keeps them polymorphic here (+17% on country-scale
     * polygonToCells, measured with both Float64Array and plain-array scratch).
     */
    private static Boolean crossingParity(double[] p, PreparedPolygon prep, double[] r) {
        // normal of the probe->ref arc plane
        final double abx = p[1] * r[2] - p[2] * r[1];
        final double aby = p[2] * r[0] - p[0] * r[2];
        final double abz = p[0] * r[1] - p[1] * r[0];
        int crossings = 0;
        for (int ri = 0; ri < prep.ringVecsList.length; ri++) {
            final double[][] verts = prep.ringVecsList[ri];
            final double[][] norms = prep.ringNormals[ri];
            final int n = verts.length;
            final double sFirst = abx * verts[0][0] + aby * verts[0][1] + abz * verts[0][2];
            if (Math.abs(sFirst) < CROSSING_EPS) return null;
            double sPrev = sFirst;
            for (int i = 0; i < n; i++) {
                final double sNext;
                if (i + 1 == n) {
                    sNext = sFirst;
                } else {
                    final double[] v = verts[i + 1];
                    sNext = abx * v[0] + aby * v[1] + abz * v[2];
                    if (Math.abs(sNext) < CROSSING_EPS) return null;
                }
                if (sPrev * sNext < 0) {
                    // edge endpoints straddle the probe arc's plane: test whether the
                    // probe arc straddles the edge's plane on the matching side
                    final double[] cd = norms[i];
                    final double cbd = -(cd[0] * r[0] + cd[1] * r[1] + cd[2] * r[2]);
                    final double dac = cd[0] * p[0] + cd[1] * p[1] + cd[2] * p[2];
                    if (Math.abs(cbd) < CROSSING_EPS || Math.abs(dac) < CROSSING_EPS) return null;
                    final double acb = -sPrev;
                    if (acb * cbd > 0 && acb * dac > 0) crossings++;
                }
                sPrev = sNext;
            }
        }
        return (crossings & 1) == 1;
    }

    /**
     * Full containment test of a point: cap prefilter, then crossing test with winding fallback.
     *
     * @param p Cartesian [x, y, z] unit vector
     * @param prep prepared polygon
     * @return true if the point is inside the polygon
     */
    public static boolean pointInPreparedPolygon(double[] p, PreparedPolygon prep) {
        final BoundingCap cap = prep.cap;
        if (p[0] * cap.center[0] + p[1] * cap.center[1] + p[2] * cap.center[2] < cap.minDot) return false;
        // Polygons reaching a hemisphere: crossing parity against a point known to
        // be inside (even parity = same side = inside)
        for (int i = 0; i < prep.insideRefs.length; i++) {
            final Boolean result = crossingParity(p, prep, prep.insideRefs[i]);
            if (result != null) return !result;
        }
        if (prep.useFast) {
            final Boolean result = crossingParity(p, prep);
            if (result != null) return result;
        }
        return pointInPolygonRings(p, prep.ringVecsList);
    }
}
