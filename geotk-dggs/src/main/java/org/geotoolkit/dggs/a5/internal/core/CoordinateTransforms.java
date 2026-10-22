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

import org.geotoolkit.dggs.a5.internal.math.Vec2;
import org.geotoolkit.dggs.a5.internal.math.Vec3;
import org.geotoolkit.dggs.a5.internal.projections.AuthalicProjection;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.BASIS;
import static org.geotoolkit.dggs.a5.internal.core.Pentagon.BASIS_INVERSE;

/**
 * Conversions between the coordinate systems of upstream {@code core/coordinate-systems.ts}.
 *
 * <p>Port of upstream {@code core/coordinate-transforms.ts}. The branded tuple types are plain
 * {@code double[]} : Face [x, y], Polar [rho, gamma], IJ [i, j], Cartesian [x, y, z],
 * Spherical [theta, phi] in radians, LonLat [longitude, latitude] in degrees,
 * Barycentric [b0, b1, b2]. A {@code FaceTriangle} and a {@code Contour} are {@code double[][]}.
 * Every function returns a new array and is thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CoordinateTransforms {

    private static final AuthalicProjection authalic = new AuthalicProjection();

    private CoordinateTransforms() {
    }

    /**
     * @param deg angle in degrees
     * @return angle in radians
     */
    public static double degToRad(double deg) {
        return (deg * (Math.PI / 180));
    }

    /**
     * @param rad angle in radians
     * @return angle in degrees
     */
    public static double radToDeg(double rad) {
        return (rad * (180 / Math.PI));
    }

    /**
     * @param xy Face [x, y]
     * @return Polar [rho, gamma]
     */
    public static double[] toPolar(double[] xy) {
        final double rho = Vec2.length(xy); // Radial distance from face center
        final double gamma = Math.atan2(xy[1], xy[0]); // Azimuthal angle
        return new double[]{rho, gamma};
    }

    /**
     * @param polar Polar [rho, gamma]
     * @return Face [x, y]
     */
    public static double[] toFace(double[] polar) {
        final double rho = polar[0], gamma = polar[1];
        final double x = rho * Math.cos(gamma);
        final double y = rho * Math.sin(gamma);
        return new double[]{x, y};
    }

    /**
     * @param face Face [x, y]
     * @return IJ [i, j]
     */
    public static double[] FaceToIJ(double[] face) {
        return Vec2.transformMat2(Vec2.create(), face, BASIS_INVERSE);
    }

    /**
     * @param ij IJ [i, j]
     * @return Face [x, y]
     */
    public static double[] IJToFace(double[] ij) {
        return Vec2.transformMat2(Vec2.create(), ij, BASIS);
    }

    /**
     * Convert face coordinates to barycentric coordinates
     *
     * @param p Face [x, y]
     * @param triangle FaceTriangle, three Face [x, y] points
     * @return Barycentric [b0, b1, b2]
     */
    public static double[] faceToBarycentric(double[] p, double[][] triangle) {
        final double[] p1 = triangle[0], p2 = triangle[1], p3 = triangle[2];
        final double[] d31 = {p1[0] - p3[0], p1[1] - p3[1]};
        final double[] d23 = {p3[0] - p2[0], p3[1] - p2[1]};
        final double[] d3p = {p[0] - p3[0], p[1] - p3[1]};

        final double det = d23[0] * d31[1] - d23[1] * d31[0];
        final double b0 = (d23[0] * d3p[1] - d23[1] * d3p[0]) / det;
        final double b1 = (d31[0] * d3p[1] - d31[1] * d3p[0]) / det;
        final double b2 = 1 - (b0 + b1);
        return new double[]{b0, b1, b2};
    }

    /**
     * Convert barycentric coordinates to face coordinates
     *
     * @param b Barycentric [b0, b1, b2]
     * @param triangle FaceTriangle, three Face [x, y] points
     * @return Face [x, y]
     */
    public static double[] barycentricToFace(double[] b, double[][] triangle) {
        final double[] p1 = triangle[0], p2 = triangle[1], p3 = triangle[2];
        return new double[]{b[0] * p1[0] + b[1] * p2[0] + b[2] * p3[0], b[0] * p1[1] + b[1] * p2[1] + b[2] * p3[1]};
    }

    /**
     * @param xyz Cartesian [x, y, z]
     * @return Spherical [theta, phi] in radians
     */
    public static double[] toSpherical(double[] xyz) {
        final double theta = Math.atan2(xyz[1], xyz[0]);
        // atan2 keeps full precision near the poles, where acos(z/r) loses half of
        // the digits carried (its derivative blows up as z/r → ±1)
        final double rxy = Math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1]);
        final double phi = Math.atan2(rxy, xyz[2]);
        return new double[]{theta, phi};
    }

    /**
     * @param spherical Spherical [theta, phi] in radians
     * @return Cartesian [x, y, z]
     */
    public static double[] toCartesian(double[] spherical) {
        final double theta = spherical[0], phi = spherical[1];
        final double sinPhi = Math.sin(phi);
        final double x = sinPhi * Math.cos(theta);
        final double y = sinPhi * Math.sin(theta);
        final double z = Math.cos(phi);
        return new double[]{x, y, z};
    }

    /**
     * Determine the offset longitude for the spherical coordinate system
     * This is the angle between the Greenwich meridian and vector between the centers
     * of the first two origins (dodecahedron face centers)
     *
     * It is chosen such that the majority of the world's population, around 99.9% (and thus land mass) is located
     * in the first 8.5 dodecahedron faces, and thus come first along the curve.
     */
    private static final double LONGITUDE_OFFSET = 93;

    /**
     * Convert longitude/latitude to spherical coordinates
     *
     * @param lonLat LonLat [longitude, latitude] in degrees, longitude 0 to 360, latitude -90 to 90
     * @return Spherical [theta, phi] in radians
     */
    public static double[] fromLonLat(double[] lonLat) {
        final double longitude = lonLat[0], latitude = lonLat[1];
        final double theta = degToRad((longitude + LONGITUDE_OFFSET));

        final double geodeticLat = degToRad(latitude);
        final double authalicLat = authalic.forward(geodeticLat);
        final double phi = (Math.PI / 2 - authalicLat);
        return new double[]{theta, phi};
    }

    /**
     * Normalize a longitude value to the range [-180, 180)
     *
     * @param lon longitude in degrees
     * @return normalized longitude in degrees
     */
    public static double normalizeLongitude(double lon) {
        return (((((lon + 180) % 360) + 360) % 360) - 180);
    }

    /**
     * Convert spherical coordinates to longitude/latitude
     *
     * @param spherical Spherical [theta, phi] in radians, theta (longitude) 0 to 2π, phi (colatitude) 0 to π
     * @return LonLat [longitude, latitude] in degrees
     */
    public static double[] toLonLat(double[] spherical) {
        final double theta = spherical[0], phi = spherical[1];
        final double longitude = normalizeLongitude((radToDeg(theta) - LONGITUDE_OFFSET));

        final double authalicLat = (Math.PI / 2 - phi);
        final double geodeticLat = authalic.inverse(authalicLat);
        final double latitude = radToDeg(geodeticLat);
        return new double[]{longitude, latitude};
    }

    /**
     * Normalizes longitude values in a contour to handle antimeridian crossing
     *
     * @param contour Array of LonLat [longitude, latitude] points
     * @return Normalized contour with consistent longitude values (new arrays)
     */
    public static double[][] normalizeLongitudes(double[][] contour) {
        // Accumulate the centroid in Cartesian space directly — no intermediate
        // points array, avoids one allocation per cell when called from cellToBoundary.
        final double[] center = Vec3.create();
        for (int i = 0; i < contour.length; i++) {
            Vec3.add(center, center, toCartesian(fromLonLat(contour[i])));
        }
        Vec3.normalize(center, center);
        final double[] centerLonLat = toLonLat(toSpherical(center));
        double centerLon = centerLonLat[0];
        final double centerLat = centerLonLat[1];
        if (centerLat > 89.99 || centerLat < -89.99) {
            // Near poles, use first point's longitude
            centerLon = contour[0][0];
        }

        centerLon = normalizeLongitude(centerLon);

        final double[][] out = new double[contour.length][];
        for (int i = 0; i < contour.length; i++) {
            double longitude = contour[i][0];
            final double latitude = contour[i][1];
            // Adjust longitude to be closer to center
            while (longitude - centerLon > 180) longitude = (longitude - 360);
            while (longitude - centerLon < -180) longitude = (longitude + 360);
            out[i] = new double[]{longitude, latitude};
        }
        return out;
    }
}
