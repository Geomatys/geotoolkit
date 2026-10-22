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

import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.utils.Vector.tripleProduct;

/**
 * Module-level functions of upstream {@code geometry/spherical-polygon.ts}
 * (the {@code SphericalPolygonShape} class of that module is {@link SphericalPolygonShape}).
 *
 * <p>Use Cartesian system for all calculations for greater accuracy.
 * Using [x, y, z] gives equal precision in all directions, unlike spherical coordinates.
 * The upstream {@code SphericalPolygon} type ({@code Cartesian[]}) is represented by a
 * {@code double[][]} of Cartesian [x, y, z] unit vectors.</p>
 *
 * <p>Upstream module-level scratch vectors are locals here: all functions are thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class SphericalPolygon {

    private SphericalPolygon() {
    }

    /**
     * Signed area (spherical excess) of the spherical triangle (v1, v2, v3) on the
     * unit sphere, in radians.
     *
     * Uses the Van Oosterom–Strackee formula
     * atan2 keeps full precision for tiny triangles (numerator → area/2) and
     * does not fold areas above π back into [-π, π].
     * Free-function form avoids the class/array allocations of
     * {@code new SphericalTriangleShape([…])} on the lonLatToCell / cellToLonLat hot path.
     *
     * @param v1 Cartesian [x, y, z] unit vector
     * @param v2 Cartesian [x, y, z] unit vector
     * @param v3 Cartesian [x, y, z] unit vector
     * @return signed area in radians
     */
    public static double sphericalTriangleArea(double[] v1, double[] v2, double[] v3) {
        final double norm =
                1 +
                (v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2]) +
                (v2[0] * v3[0] + v2[1] * v3[1] + v2[2] * v3[2]) +
                (v3[0] * v1[0] + v3[1] * v1[1] + v3[2] * v1[2]);
        return 2 * Math.atan2(tripleProduct(v1, v2, v3), norm);
    }

    /**
     * Spherical point-in-polygon via signed-angle summation. Works for concave
     * polygons (unlike {@code SphericalPolygonShape.containsPoint}, which assumes convex
     * "necessary strike"). The math is fully inlined as it's called per-cell in
     * polygon-fill hot paths.
     *
     * @param point Cartesian [x, y, z] unit vector
     * @param vertices ring of Cartesian [x, y, z] unit vectors (not closed)
     * @return true if the point is inside the polygon
     */
    public static boolean pointInSphericalPolygon(double[] point, double[][] vertices) {
        double angleSum = 0;
        for (int i = 0; i < vertices.length; i++) {
            final double[] av = vertices[i];
            final double[] bv = vertices[(i + 1) % vertices.length];
            final double dotPA = point[0] * av[0] + point[1] * av[1] + point[2] * av[2];
            final double dotPB = point[0] * bv[0] + point[1] * bv[1] + point[2] * bv[2];
            final double apx = av[0] - dotPA * point[0],
                    apy = av[1] - dotPA * point[1],
                    apz = av[2] - dotPA * point[2];
            final double bpx = bv[0] - dotPB * point[0],
                    bpy = bv[1] - dotPB * point[1],
                    bpz = bv[2] - dotPB * point[2];
            final double cx = apy * bpz - apz * bpy;
            final double cy = apz * bpx - apx * bpz;
            final double cz = apx * bpy - apy * bpx;
            angleSum += Math.atan2(cx * point[0] + cy * point[1] + cz * point[2], apx * bpx + apy * bpy + apz * bpz);
        }
        return Math.abs(angleSum) > Math.PI;
    }

    /**
     * Ring winding direction: +1 for CCW (interior to the left of edge direction), -1 for CW.
     * Sums (v_i × v_{i+1}) · centroid across the ring.
     *
     * @param ringVecs ring of Cartesian [x, y, z] unit vectors (not closed)
     * @return 1 or -1
     */
    public static int ringWindingSign(double[][] ringVecs) {
        final double[] windingCentroid = Vec3.create();
        Vec3.set(windingCentroid, 0, 0, 0);
        for (double[] v : ringVecs) Vec3.add(windingCentroid, windingCentroid, v);
        Vec3.normalize(windingCentroid, windingCentroid);

        double sum = 0;
        for (int i = 0; i < ringVecs.length; i++) {
            sum += tripleProduct(windingCentroid, ringVecs[i], ringVecs[(i + 1) % ringVecs.length]);
        }
        return sum > 0 ? 1 : -1;
    }

    /**
     * Great-circle plane normals for every segment of the ring.
     *
     * @param ringVecs ring of Cartesian [x, y, z] unit vectors (not closed)
     * @return one (non normalized) normal per segment, segment i going from vertex i to vertex i+1
     */
    public static double[][] ringSegmentNormals(double[][] ringVecs) {
        final double[][] normals = new double[ringVecs.length][];
        for (int i = 0; i < ringVecs.length; i++) {
            final double[] n = Vec3.create();
            Vec3.cross(n, ringVecs[i], ringVecs[(i + 1) % ringVecs.length]);
            normals[i] = n;
        }
        return normals;
    }
}
