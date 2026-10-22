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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import static org.geotoolkit.dggs.a5.internal.geometry.SphericalPolygon.sphericalTriangleArea;
import org.geotoolkit.dggs.a5.internal.utils.Vector;

/**
 * Polygon on the unit sphere, vertices in Cartesian [x, y, z] coordinates.
 *
 * <p>Port of the {@code SphericalPolygonShape} class of upstream {@code geometry/spherical-polygon.ts}.
 * The vertices array is used directly (not copied); upstream freezes it, callers must not
 * modify it after construction. The memoized area is computed lazily; the race on first
 * computation is benign (idempotent, immutable boxed value) so instances can be shared.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SphericalPolygonShape {

    /**
     * Cartesian [x, y, z] vertices, must not be modified.
     */
    protected final double[][] vertices;
    private Double area = null;

    /**
     * @param vertices Cartesian [x, y, z] unit vectors, used directly (not copied)
     */
    public SphericalPolygonShape(double[][] vertices) {
        this.vertices = vertices;
        // this.isWindingCorrect();
    }

    /**
     * Returns a closed boundary of the polygon, with 1 point per edge.
     *
     * @return SphericalPolygon
     */
    public double[][] getBoundary() {
        return getBoundary(1, true);
    }

    /**
     * Returns a closed boundary of the polygon, with nSegments points per edge.
     *
     * @param nSegments number of points per edge
     * @return SphericalPolygon
     */
    public double[][] getBoundary(int nSegments) {
        return getBoundary(nSegments, true);
    }

    /**
     *
     * @param nSegments Returns a closed boundary of the polygon, with nSegments points per edge
     * @param closedRing whether to repeat the first point at the end
     * @return SphericalPolygon
     */
    public double[][] getBoundary(int nSegments, boolean closedRing) {
        final List<double[]> points = new ArrayList<>();
        final int N = this.vertices.length;
        for (int s = 0; s < N * nSegments; s++) {
            final double t = s / (double) nSegments;
            points.add(this.slerp(t));
        }
        if (closedRing) {
            points.add(points.get(0));
        }

        return points.toArray(new double[points.size()][]);
    }

    /**
     * Interpolates along boundary of polygon. Pass t = 1.5 to get the midpoint between 2nd and 3rd vertices
     *
     * @param t position along the boundary, in vertex index units
     * @return Cartesian coordinate
     */
    public double[] slerp(double t) {
        final int N = this.vertices.length;
        final double f = t % 1;
        final int i = (int) Math.floor(t % N);
        final int j = (i + 1) % N;
        return Vector.slerp(Vec3.create(), this.vertices[i], this.vertices[j], f);
    }

    /**
     * Returns the vertex given by index t, along with the vectors:
     * - VA: Vector from vertex to point A
     * - VB: Vector from vertex to point B
     *
     * @param t vertex index
     * @return [V, VA, VB], new arrays
     */
    public double[][] getTransformedVertices(double t) {
        final int N = this.vertices.length;
        final int i = (int) Math.floor(t % N);
        final int j = (i + 1) % N;
        final int k = (i + N - 1) % N;

        // Points A & B (vertex before and after)
        final double[] V = Vec3.clone(this.vertices[i]);
        final double[] VA = Vec3.clone(this.vertices[j]);
        final double[] VB = Vec3.clone(this.vertices[k]);
        Vec3.sub(VA, VA, V);
        Vec3.sub(VB, VB, V);
        return new double[][] {V, VA, VB};
    }

    /**
     * @param point Cartesian [x, y, z] unit vector
     * @return positive if the point is inside all arcs, 0 on an edge, negative outside
     *         (the further away from 0, the further away from the arc)
     */
    public double containsPoint(double[] point) {
        // Adaption of algorithm from:
        // 'Locating a point on a spherical surface relative to a spherical polygon'
        // Using only the condition of 'necessary strike'
        final int N = this.vertices.length;
        double thetaDeltaMin = Double.POSITIVE_INFINITY;

        for (int i = 0; i < N; i++) {
            // Transform point and neighboring vertices into coordinate system centered on vertex
            final double[][] tv = this.getTransformedVertices(i);
            final double[] V = tv[0], VA = tv[1], VB = tv[2];
            final double[] VP = Vec3.sub(Vec3.create(), point, V);

            // Normalize to obtain unit direction vectors
            Vec3.normalize(VP, VP);
            Vec3.normalize(VA, VA);
            Vec3.normalize(VB, VB);

            // Cross products will point away from the center of the sphere when
            // point P is within arc formed by VA and VB
            final double[] crossAP = Vec3.cross(Vec3.create(), VA, VP);
            final double[] crossPB = Vec3.cross(Vec3.create(), VP, VB);

            // Dot product will be positive when point P is within arc formed by VA and VB
            // The magnitude of the dot product is the sine of the angle between the two vectors
            // which is the same as the angle for small angles.
            final double sinAP = Vec3.dot(V, crossAP);
            final double sinPB = Vec3.dot(V, crossPB);

            // By returning the minimum value we find the arc where the point is closest to being outside
            thetaDeltaMin = Math.min(Math.min(thetaDeltaMin, sinAP), sinPB);
        }

        // If point is inside all arcs, will return a position value
        // If point is on edge of arc, will return 0
        // If point is outside all arcs, will return -1, the further away from 0, the further away from the arc
        return thetaDeltaMin;
    }

    /**
     * Calculate the area of the spherical polygon by decomposing it into a fan of triangles
     *
     * @return The area of the spherical polygon in radians
     */
    public double getArea() {
        // Memoize the result since vertices are immutable
        Double a = this.area;
        if (a == null) {
            a = this.getAreaInternal();
            this.area = a;
        }
        return a;
    }

    private double getAreaInternal() {
        if (this.vertices.length < 3) {
            return 0;
        }

        if (this.vertices.length == 3) {
            return sphericalTriangleArea(this.vertices[0], this.vertices[1], this.vertices[2]);
        }

        // Calculate center of polygon
        final double[] center = Vec3.create();
        Vec3.set(center, 0, 0, 0);
        for (double[] vertex : this.vertices) {
            Vec3.add(center, center, vertex);
        }
        Vec3.normalize(center, center);

        // Sum fan of triangles around center
        double area = 0;
        for (int i = 0; i < this.vertices.length; i++) {
            final double[] v1 = this.vertices[i];
            final double[] v2 = this.vertices[(i + 1) % this.vertices.length];
            final double triArea = sphericalTriangleArea(center, v1, v2);
            if (!Double.isNaN(triArea)) {
                area += triArea;
            }
        }
        return area;
    }

    /**
     * For debugging purposes, check if the winding order is correct
     * In production, should always be correct
     */
    @SuppressWarnings("unused")
    private void isWindingCorrect() {
        final double area = this.getArea();
        final boolean isCorrect = area > 0;
        if (!isCorrect) {
            // debugger;
        }
    }
}
