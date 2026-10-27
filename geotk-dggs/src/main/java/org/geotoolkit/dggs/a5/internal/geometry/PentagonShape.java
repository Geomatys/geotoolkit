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

import org.geotoolkit.dggs.a5.internal.math.Vec2;

/**
 * Planar polygon (usually a pentagon) in Face coordinates.
 *
 * <p>Port of upstream {@code geometry/pentagon.ts}. The upstream {@code Pentagon} type
 * (a tuple of five Face points) is represented by a {@code double[][]} of [x, y] Face
 * coordinates; after {@link #splitEdges(int)} it may contain more than five vertices.</p>
 *
 * <p>Instances are mutable (transformations modify the vertices in place, as upstream)
 * and not thread-safe; {@link #clone()} before transforming a shared instance.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class PentagonShape {

    // How close (as a fraction of its length) to either end of the p3→p4 segment
    // a crossing counts as touching only that endpoint
    private static final double VERTEX_MARGIN = 1e-9;

    /**
     * 2D segment-vs-segment intersection test.
     * Returns true iff the closed segment p1→p2 crosses p3→p4 away from p3 and p4.
     */
    private static boolean segments2dIntersect(double[] p1, double[] p2, double[] p3, double[] p4) {
        final double d1x = p2[0] - p1[0],
                d1y = p2[1] - p1[1];
        final double d2x = p4[0] - p3[0],
                d2y = p4[1] - p3[1];
        final double denom = d1x * d2y - d1y * d2x;
        // Parallel (or degenerate) when the sine of the angle between them is ~0.
        // Relative to the segment lengths: an absolute threshold swallows every
        // crossing once cells are small (res 20+, where |d1|·|d2| < 1e-12).
        if (denom * denom <= 1e-24 * (d1x * d1x + d1y * d1y) * (d2x * d2x + d2y * d2y)) return false;

        final double dx = p3[0] - p1[0],
                dy = p3[1] - p1[1];
        final double t = (dx * d2y - dy * d2x) / denom;
        final double u = (dx * d1y - dy * d1x) / denom;
        // A crossing within float noise of p3 or p4 (a pentagon vertex, as
        // `intersectsSegment` passes them) only grazes the corner: no shared area,
        // and which side of the vertex it falls on is a last-bit decision that
        // differs between languages. A segment that truly enters through a corner
        // also crosses another edge or ends inside, so it is still found.
        return t >= 0 && t <= 1 && u > VERTEX_MARGIN && u < 1 - VERTEX_MARGIN;
    }

    /**
     * Result of {@link #clipSegment(double[], double[])}.
     *
     * @param start parameter along the segment line where it enters the pentagon
     * @param end parameter along the segment line where it leaves the pentagon
     * @param exitEdgeT where along the exit edge the segment leaves (0..1 from the edge's first vertex)
     */
    public record SegmentClip(double start, double end, double exitEdgeT) {
    }

    private double[][] vertices;

    /**
     * Creates a shape from the given vertices. The array is used directly (not copied)
     * and is reversed in place if its winding is not counter-clockwise.
     *
     * @param vertices Face coordinates [x, y] of the vertices
     */
    public PentagonShape(double[][] vertices) {
        this.vertices = vertices;
        if (!this.isWindingCorrect()) {
            reverse(this.vertices);
        }
    }

    /**
     * Equivalent of JavaScript {@code Array.prototype.reverse()}: reverses the array in place.
     */
    private static void reverse(double[][] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            final double[] tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }

    public double getArea() {
        double signedArea = 0;
        final int N = this.vertices.length;
        for (int i = 0; i < N; i++) {
            final int j = (i + 1) % N;
            signedArea += (this.vertices[j][0] - this.vertices[i][0]) * (this.vertices[j][1] + this.vertices[i][1]);
        }
        return signedArea;
    }

    private boolean isWindingCorrect() {
        return this.getArea() >= 0;
    }

    /**
     * @return the vertices, not copied: modifying them modifies this shape
     */
    public double[][] getVertices() {
        return this.vertices;
    }

    public PentagonShape scale(double scale) {
        for (final double[] vertex : this.vertices) {
            Vec2.scale(vertex, vertex, scale);
        }
        return this;
    }

    /**
     * Rotates the pentagon 180 degrees (equivalent to negating x &amp; y)
     * @return The rotated pentagon
     */
    public PentagonShape rotate180() {
        for (final double[] vertex : this.vertices) {
            Vec2.negate(vertex, vertex);
        }
        return this;
    }

    /**
     * Reflects the pentagon over the x-axis (equivalent to negating y)
     * and reverses the winding order to maintain consistent orientation
     * @return The reflected pentagon
     */
    public PentagonShape reflectY() {
        // First reflect all vertices
        for (final double[] vertex : this.vertices) {
            vertex[1] = -vertex[1];
        }

        // Then reverse the winding order to maintain consistent orientation
        reverse(this.vertices);

        return this;
    }

    /**
     * @param translation Vec2 [x, y]
     */
    public PentagonShape translate(double[] translation) {
        for (final double[] vertex : this.vertices) {
            Vec2.add(vertex, vertex, translation);
        }
        return this;
    }

    /**
     * @param transform column-major Mat2
     */
    public PentagonShape transform(double[] transform) {
        for (final double[] vertex : this.vertices) {
            Vec2.transformMat2(vertex, vertex, transform);
        }
        return this;
    }

    /**
     * @param transform Mat2d [a, b, c, d, tx, ty]
     */
    public PentagonShape transform2d(double[] transform) {
        for (final double[] vertex : this.vertices) {
            Vec2.transformMat2d(vertex, vertex, transform);
        }
        return this;
    }

    @Override
    public PentagonShape clone() {
        final double[][] copy = new double[this.vertices.length][];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = Vec2.clone(this.vertices[i]);
        }
        final PentagonShape newPentagon = new PentagonShape(copy);
        return newPentagon;
    }

    /**
     * @return Face [x, y] average of the vertices
     */
    public double[] getCenter() {
        final int n = this.vertices.length;
        double sum0 = 0, sum1 = 0;
        for (final double[] v : this.vertices) {
            sum0 = sum0 + v[0] / n;
            sum1 = sum1 + v[1] / n;
        }
        return new double[]{sum0, sum1};
    }

    /**
     * Tests if a point is inside the pentagon by checking if it's on the correct side of all edges.
     * Assumes consistent winding order (counter-clockwise).
     * @param point The point to test
     * @return 1 if point is inside, otherwise a negative value proportional to the distance from the point to the edge
     */
    public double containsPoint(double[] point) {
        // TODO later we can likely remove this, but for now it's useful for debugging
        if (!this.isWindingCorrect()) {
            throw new IllegalStateException("Pentagon is not counter-clockwise");
        }

        final int N = this.vertices.length;
        double dMax = 1;
        for (int i = 0; i < N; i++) {
            final double[] v1 = this.vertices[i];
            final double[] v2 = this.vertices[(i + 1) % N];

            // Calculate the cross product to determine which side of the line the point is on
            // (v1 - v2) × (point - v1)
            final double dx = v1[0] - v2[0];
            final double dy = v1[1] - v2[1];
            final double px = point[0] - v1[0];
            final double py = point[1] - v1[1];

            // Cross product: dx * py - dy * px
            // If positive, point is on the wrong side
            // If negative, point is on the correct side
            final double crossProduct = dx * py - dy * px;
            if (crossProduct < 0) {
                // Only normalize by distance of point to edge as we can assume the edges of the
                // pentagon are all the same length
                final double pLength = Math.sqrt(px * px + py * py);
                dMax = Math.min(dMax, crossProduct / pLength);
            }
        }

        return dMax;
    }

    /**
     * The part of the segment a→b inside this (convex) pentagon, as parameters
     * {@code start} ≤ {@code end} along the segment's line, with where along the edge it
     * leaves through ({@code exitEdgeT}, 0..1 from the edge's first vertex); null when
     * the line misses the pentagon. Uses the same edge sides as {@code containsPoint}.
     *
     * @param a Vec2 segment start
     * @param b Vec2 segment end
     * @return clipped segment parameters, or null
     */
    public SegmentClip clipSegment(double[] a, double[] b) {
        final int N = this.vertices.length;
        final double sx = b[0] - a[0];
        final double sy = b[1] - a[1];
        double start = Double.NEGATIVE_INFINITY;
        double end = Double.POSITIVE_INFINITY;
        int exitEdge = -1;
        for (int i = 0; i < N; i++) {
            final double[] v1 = this.vertices[i];
            final double[] v2 = this.vertices[(i + 1) % N];
            // Inside the edge where (v1 - v2) × (p - v1) >= 0, along p = a + t·(b - a)
            final double ex = v1[0] - v2[0];
            final double ey = v1[1] - v2[1];
            final double f = ex * (a[1] - v1[1]) - ey * (a[0] - v1[0]);
            final double g = ex * sy - ey * sx;
            if (g == 0) {
                if (f < 0) return null;
            } else if (g > 0) {
                start = Math.max(start, -f / g);
            } else {
                final double t = -f / g;
                if (t < end) {
                    end = t;
                    exitEdge = i;
                }
            }
        }
        if (start > end || exitEdge < 0) return null;
        // Where the exit point falls along the exit edge, from its first vertex
        final double[] v1 = this.vertices[exitEdge];
        final double[] v2 = this.vertices[(exitEdge + 1) % N];
        final double px = a[0] + end * sx - v1[0];
        final double py = a[1] + end * sy - v1[1];
        final double ex = v2[0] - v1[0];
        final double ey = v2[1] - v1[1];
        return new SegmentClip(start, end, (px * ex + py * ey) / (ex * ex + ey * ey));
    }

    /**
     * Tests whether a 2D segment intersects this pentagon.
     * True if either endpoint is inside, or any pentagon edge crosses the segment.
     * Operates entirely in Face coordinates — pentagon edges are exact straight lines
     * here, so the test has no projection-induced approximation.
     *
     * @param a Face segment start
     * @param b Face segment end
     */
    public boolean intersectsSegment(double[] a, double[] b) {
        if (this.containsPoint(a) > 0 || this.containsPoint(b) > 0) return true;

        final int N = this.vertices.length;
        for (int i = 0; i < N; i++) {
            final double[] v1 = this.vertices[i];
            final double[] v2 = this.vertices[(i + 1) % N];
            if (segments2dIntersect(a, b, v1, v2)) return true;
        }
        return false;
    }

    /**
     * Splits each edge of the pentagon into the specified number of segments
     * @param segments Number of segments to split each edge into
     * @return A new PentagonShape with more vertices, or the original PentagonShape if segments &lt;= 1
     */
    public PentagonShape splitEdges(int segments) {
        if (segments <= 1) {
            return this;
        }

        final int N = this.vertices.length;
        final double[][] newVertices = new double[N * segments][];
        int k = 0;

        for (int i = 0; i < N; i++) {
            final double[] v1 = this.vertices[i];
            final double[] v2 = this.vertices[(i + 1) % N];

            // Add the current vertex
            newVertices[k++] = Vec2.clone(v1);

            // Add interpolated points along the edge (excluding the endpoints)
            for (int j = 1; j < segments; j++) {
                final double t = j / (double) segments;
                final double[] interpolated = Vec2.create();
                Vec2.lerp(interpolated, v1, v2, t);
                newVertices[k++] = interpolated;
            }
        }

        return new PentagonShape(newVertices);
    }
}
