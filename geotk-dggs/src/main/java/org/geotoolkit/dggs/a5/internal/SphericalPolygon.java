/**
 * Copyright (C) 2025 Geomatys and Felix Palmer
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
package org.geotoolkit.dggs.a5.internal;

import java.util.Arrays;
import java.util.Collections;
import org.apache.sis.geometries.math.Quaternion;
import org.apache.sis.geometries.math.Vector3D;

/**
 * Use Cartesian system for all calculations for greater accuracy
 * Using [x, y, z] gives equal precision in all directions, unlike spherical coordinates
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class SphericalPolygon {

    private static final Vector3D.Double UP = new Vector3D.Double(0, 0, 1);

    public static class SphericalPolygonShape {
        private final Vector3D.Double[] vertices;

        public SphericalPolygonShape(double[][] vertices) {
            this.vertices = new Vector3D.Double[vertices.length];
            for (int i = 0; i < vertices.length; i++) {
                this.vertices[i] = new Vector3D.Double(vertices[i]);
            }
            if (!this.isWindingCorrect()) {
                Collections.reverse(Arrays.asList(vertices));
            }
        }

        public SphericalPolygonShape(Vector3D.Double[] vertices) {
            this.vertices = vertices;
            if (!this.isWindingCorrect()) {
                Collections.reverse(Arrays.asList(vertices));
            }
        }

        /**
         *
         * @param nSegments Returns a close boundary of the polygon, with nSegments points per edge, default is 1
         * @param closedRing default is true
         * @returns SphericalPolygon
         */
        public Vector3D.Double[] getBoundary(int nSegments, boolean closedRing) {
            final int N = vertices.length;
            Vector3D.Double[] points = new Vector3D.Double[N * nSegments + (closedRing ? 1 : 0)];
            for (int s = 0, n = N * nSegments; s < n; s++) {
                double t = (double)(s) / nSegments;
                points[s] = slerp(t);
            }
            if (closedRing) {
                points[points.length-1] = points[0];
            }
            return points;
        }

        /**
         * Interpolates along boundary of polygon. Pass t = 1.5 to get the midpoint between 2nd and 3rd vertices
         * @param t
         * @returns Cartesian coordinate
         */
        public Vector3D.Double slerp(double t) {
            final int N = this.vertices.length;
            final double f = t % 1;
            final int i = (int) Math.floor(t % N);
            final int j = (i + 1) % N;

            // Points A & B
            final Vector3D.Double A = this.vertices[i];
            final Vector3D.Double B = this.vertices[j];

            // Quaternions
            final Quaternion identity = new Quaternion();
            final Quaternion qOA = new Quaternion().fromUnitVectors(UP, A);
            final Quaternion qAB = new Quaternion().fromUnitVectors(A, B);
            final Quaternion qPartial = new Quaternion(identity).slerp(qAB, f);
            final Quaternion qCombined = new Quaternion(qPartial).multiply(qOA);

            final Vector3D.Double out = new Vector3D.Double(0, 0, 1);
            qCombined.rotate(out, out);
            return out;
        }

        /**
         * Returns the vertex given by index t, along with the vectors:
         * - VA: Vector from vertex to point A
         * - VB: Vector from vertex to point B
         * @param t
         * @returns
         */
        public Vector3D.Double[] getTransformedVertices(int t) {
            final int N = this.vertices.length;
            final int i = (int) Math.floor(t % N);
            final int j = (i + 1) % N;
            final int k = (i + N - 1) % N;

            // Points A & B (vertex before and after)
            final Vector3D.Double V = this.vertices[i].copy();
            final Vector3D.Double VA = this.vertices[j].copy();
            final Vector3D.Double VB = this.vertices[k].copy();
            VA.subtract(V);
            VB.subtract(V);
            return new Vector3D.Double[]{V, VA, VB};
        }

        public double containsPoint(Vector3D.Double point) {
            // Adaption of algorithm from:
            // 'Locating a point on a spherical surface relative to a spherical polygon'
            // Using only the condition of 'necessary strike'
            final int N = this.vertices.length;
            double thetaDeltaMin = Double.POSITIVE_INFINITY;

            for (int i = 0; i < N; i++) {
                // Transform point and neighboring vertices into coordinate system centered on vertex
                final Vector3D.Double[] array = getTransformedVertices(i);
                final Vector3D.Double V = array[0];
                final Vector3D.Double VA = array[1];
                final Vector3D.Double VB = array[2];

                final Vector3D.Double VP = point.copy().subtract(V);

                // Normalize to obtain unit direction vectors
                VP.normalize();
                VA.normalize();
                VB.normalize();

                // Cross products will point away from the center of the sphere when
                // point P is within arc formed by VA and VB
                final Vector3D.Double crossAP = VA.cross(VP);
                final Vector3D.Double crossPB = VP.cross(VB);

                // Dot product will be positive when point P is within arc formed by VA and VB
                // The magnitude of the dot product is the sine of the angle between the two vectors
                // which is the same as the angle for small angles.
                final double sinAP = V.dot(crossAP);
                final double sinPB = V.dot(crossPB);

                // By returning the minimum value we find the arc where the point is closest to being outside
                thetaDeltaMin = Math.min(thetaDeltaMin, Math.min(sinAP, sinPB));
            }

            // If point is inside all arcs, will return a position value
            // If point is on edge of arc, will return 0
            // If point is outside all arcs, will return -1, the further away from 0, the further away from the arc
            return thetaDeltaMin;
        }

        private boolean isWindingCorrect() {
            final Vector3D.Double[] array = getTransformedVertices(0);
            final Vector3D.Double V = array[0];
            final Vector3D.Double VA = array[1];
            final Vector3D.Double VB = array[2];
            final Vector3D.Double cross = VA.cross(VB);
            return V.dot(cross) >= 0;
        }
    }

}
