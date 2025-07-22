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
import java.util.Objects;
import org.apache.sis.geometries.math.Matrix2D;
import org.apache.sis.geometries.math.Quaternion;
import org.apache.sis.geometries.math.Vector;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import org.geotoolkit.dggs.a5.internal.Hilbert.Orientation;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class Utils {

    public static final class Origin {
        final int id;
        final Vector2D.Double axis; //Spherical
        final Quaternion quat;
        final double angle; //radians
        final Orientation[] orientation;
        final int firstQuintant;

        public Origin(int id, Vector2D.Double axis, Quaternion quat, double angle, Orientation[] orientation, int firstQuintant) {
            this.id = id;
            this.axis = axis;
            this.quat = quat;
            this.angle = angle;
            this.orientation = orientation;
            this.firstQuintant = firstQuintant;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + this.id;
            hash = 37 * hash + Objects.hashCode(this.axis);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Origin other = (Origin) obj;
            if (this.id != other.id) {
                return false;
            }
            if (Double.doubleToLongBits(this.angle) != Double.doubleToLongBits(other.angle)) {
                return false;
            }
            if (this.firstQuintant != other.firstQuintant) {
                return false;
            }
            if (!Objects.equals(this.axis, other.axis)) {
                return false;
            }
            if (!Objects.equals(this.quat, other.quat)) {
                return false;
            }
            return Arrays.deepEquals(this.orientation, other.orientation);
        }
    };

    public static final class A5Cell {
        /**
         * Origin representing one of pentagon face of the dodecahedron
         */
        public Origin origin;
        /**
         * Index (0-4) of triangular segment within pentagonal dodecahedron face
         */
        public int segment;
        /**
         * Position along Hilbert curve within triangular segment
         */
        public long S;
        /**
         * Resolution of the cell
         */
        public int resolution;

        public A5Cell(Origin origin, int segment, long S, int resolution) {
            this.origin = origin;
            this.segment = segment;
            this.S = S;
            this.resolution = resolution;
        }

    }

    public static final class Id {
        public double i;
        public double j;
        public double k;
        public int resolution;
        public Integer segment;
        public Orig origin;

        public Id(double i, double j, double k, int resolution, Integer segment, Orig origin) {
            this.i = i;
            this.j = j;
            this.k = k;
            this.resolution = resolution;
            this.segment = segment;
            this.origin = origin;
        }
    }

    public static final class PentagonShape {
        private final Vector2D.Double[] vertices;
        public Id id;

        public PentagonShape(Vector2D.Double ... vertices) {
            this.vertices = vertices;
            this.id = new Id(0, 0, 0, 1, null, null);
            if (!this.isWindingCorrect()) {
                Collections.reverse(Arrays.asList(vertices));
            }
        }

        private double getArea() {
            double signedArea = 0;
            final int N = this.vertices.length;
            for (int i = 0; i < N; i++) {
                int j = (i + 1) % N;
                signedArea += (this.vertices[j].x - this.vertices[i].x) * (this.vertices[j].y + this.vertices[i].y);
            }
            return signedArea;
        }

        private boolean isWindingCorrect() {
            return this.getArea() >= 0;
        }

        public Vector2D.Double[] getVertices() {
            return this.vertices;
        }

        public PentagonShape scale(double scale) {
            for (Vector2D.Double vertex : vertices) {
                vertex.scale(scale);
            }
            return this;
        }

        /**
         * Rotates the pentagon 180 degrees (equivalent to negating x & y)
         * @returns The rotated pentagon
         */
        public PentagonShape rotate180() {
            for (Vector2D.Double vertex : vertices) {
                vertex.x = -vertex.x;
                vertex.y = -vertex.y;
            }
            return this;
        }

        /**
         * Reflects the pentagon over the x-axis (equivalent to negating y)
         * and reverses the winding order to maintain consistent orientation
         * @returns The reflected pentagon
         */
        public PentagonShape reflectY() {
            // First reflect all vertices
            for (Vector2D.Double vertex : vertices) {
                vertex.y = -vertex.y;
            }

            // Then reverse the winding order to maintain consistent orientation
            Collections.reverse(Arrays.asList(vertices));

            return this;
        }

        public PentagonShape translate(Vector2D translation) {
            for (Vector2D.Double vertex : vertices) {
                vertex.add(translation);
            }
            return this;
        }

        public PentagonShape transform(Matrix2D transform) {
            for (Vector2D.Double vertex : vertices) {
                transform.transform(vertex, vertex);
            }
            return this;
        }

        public PentagonShape clone() {
            final Vector2D.Double[] cp = new Vector2D.Double[this.vertices.length];
            for (int i = 0; i < cp.length; i++) cp[i] = this.vertices[i].copy();
            return new PentagonShape(cp);
        }

        public Vector2D.Double getCenter() {
            final Vector2D.Double center = new Vector2D.Double();
            for (Vector2D.Double d : vertices) {
                center.add(d);
            }
            center.scale(1.0 / vertices.length);
            return center;
        }

        /**
         * Tests if a point is inside the pentagon by checking if it's on the correct side of all edges.
         * Assumes consistent winding order (counter-clockwise).
         *
         * @param point The point to test
         * @returns -1 if point is inside, otherwise a value proportional to the distance from the point to the edge
         */
        public double containsPoint(Vector2D.Double point) {
            // TODO later we can likely remove this, but for now it's useful for debugging
            if (!this.isWindingCorrect()) {
                throw new IllegalStateException("Pentagon is not counter-clockwise");
            }

            // For each edge of the pentagon
            final int N = this.vertices.length;
            for (int i = 0; i < N; i++) {
                final Vector2D.Double v1 = this.vertices[i];
                final Vector2D.Double v2 = this.vertices[(i + 1) % N];

                // Calculate the cross product to determine which side of the line the point is on
                // (v2 - v1) × (point - v1)
                final double dx = v2.x - v1.x;
                final double dy = v2.y - v1.y;
                final double px = point.x - v1.x;
                final double py = point.y - v1.y;

                // Cross product: dx * py - dy * px
                // If positive, point is on the wrong side
                // If negative, point is on the correct side
                final double crossProduct = (dx * py - dy * px);
                if (crossProduct > 0) {
                    // Only normalize by distance of point to edge as we can assume the edges of the
                    // pentagon are all the same length
                    final double pLength = Math.sqrt(px * px + py * py);
                    return crossProduct / pLength;
                }
            }

            return -1;
        }

        /**
         * Normalizes longitude values in a contour to handle antimeridian crossing
         *
         * @param contour Array of [longitude, latitude] points
         * @returns Normalized contour with consistent longitude values
         */
        public static void normalizeLongitudes(Vector2D.Double[] contour) {

            // Calculate center longitude
            double centerLon = 0.0;
            for (Vector2D.Double ll: contour) {
                centerLon += ((ll.x + 180) % 360 + 360) % 360 - 180;
            }
            centerLon /= contour.length;

            // Normalize center longitude to be in the range -180 to 180
            centerLon = ((centerLon + 180) % 360 + 360) % 360 - 180;

            // Normalize each point relative to center
            for (Vector2D.Double ll: contour) {
                while (ll.x - centerLon > 180) ll.x = ll.x - 360;
                while (ll.x - centerLon < -180) ll.x = ll.x + 360;
            }
        }
    }

    /**
     * Calculate the area of a triangle given three vertices in 3D space
     */
    public static double triangleArea(Vector3D.Double v1, Vector3D.Double v2, Vector3D.Double v3) {
        // Create vectors for two edges of the triangle
        final Vector3D.Double edge1 = v2.copy().subtract(v1);
        final Vector3D.Double edge2 = v3.copy().subtract(v1);

        // Calculate cross product
        Vector cross = edge1.cross(edge2);

        // Area is half the magnitude of the cross product
        return 0.5 * cross.length();
    }

    public static double pentagonArea(Vector3D.Double[] pentagon) {
        double area = 0;
        final Vector3D.Double v1 = pentagon[0];
        for (int i = 1; i < 4; i++) {
            final Vector3D.Double v2 = pentagon[(i)];
            final Vector3D.Double v3 = pentagon[(i + 1)];
            area += Math.abs(triangleArea(v1, v2, v3));
        }
        return area;
    }

}
