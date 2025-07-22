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

import org.apache.sis.geometries.math.Quaternion;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vector3D;
import static org.geotoolkit.dggs.a5.internal.Authalic.authalicToGeodetic;
import static org.geotoolkit.dggs.a5.internal.Authalic.geodeticToAuthalic;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class CoordinateTransforms {

    /**
     * Determine the offset longitude for the spherical coordinate system
     * This is the angle between the Greenwich meridian and vector between the centers
     * of the first two origins (dodecahedron face centers)
     *
     * It is chose such that the majority of the world's population, around 99.9% (and thus land mass) is located
     * in the first 8.5 dodecahedron faces, and thus come first along the Hilbert curve.
     */
    private static final double LONGITUDE_OFFSET = 93.0;

    private CoordinateTransforms(){}

    /**
     * @param deg
     * @return radians
     */
    public static double degToRad(double deg) {
        return Math.toRadians(deg);
    }

    /**
     * @param rad
     * @return degrees
     */
    public static double radToDeg(double rad) {
        return Math.toDegrees(rad);
    }

    /**
     * @param face Face
     * @return Polar
     */
    public static Vector2D.Double toPolar(Vector2D.Double face) {
        final double rho = face.length(); // Radial distance from face center
        final double gamma = Math.atan2(face.y, face.x); // Azimuthal angle
        return new Vector2D.Double(rho, gamma);
    }

    /**
     * @param polar Polar
     * @return Face
     */
    public static Vector2D.Double toFace(Vector2D.Double polar) {
        final double x = polar.x * Math.cos(polar.y);
        final double y = polar.x * Math.sin(polar.y);
        return new Vector2D.Double(x, y);
    }

    /**
     * @param face Face
     * @return IJ
     */
    public static Vector2D.Double FaceToIJ(Vector2D.Double face) {
        final Vector2D.Double v = new Vector2D.Double();
        Pentagon.BASIS_INVERSE.transform(face, v);
        return v;
    }

    /**
     * @param ij IJ
     * @return Face
     */
    public static Vector2D.Double IJToFace(Vector2D.Double ij) {
        final Vector2D.Double v = new Vector2D.Double();
        Pentagon.BASIS.transform(ij, v);
        return v;
    }

    /**
     * @param cart Cartesian
     * @return Spherical
     */
    public static Vector2D.Double toSpherical(Vector3D.Double cart) {
        final double theta = Math.atan2(cart.y, cart.x);
        final double r = Math.sqrt(cart.x * cart.x + cart.y * cart.y + cart.z * cart.z);
        final double phi = Math.acos(cart.z / r);
        return new Vector2D.Double(theta, phi);
    }

    public static Vector3D.Double toCartesian(Vector2D.Double spherical) {
        final double sinPhi = Math.sin(spherical.y);
        final double x = sinPhi * Math.cos(spherical.x);
        final double y = sinPhi * Math.sin(spherical.x);
        final double z = Math.cos(spherical.y);
        return new Vector3D.Double(x, y, z);
    }

    /**
     * Convert longitude/latitude to spherical coordinates
     * @param lon Longitude in degrees (0 to 360)
     * @param lat Latitude in degrees (-90 to 90)
     * @returns [theta, phi] in radians, spherical
     */
    public static Vector2D.Double fromLonLat(Vector2D.Double lonlat) {
        final double theta = degToRad(lonlat.x + LONGITUDE_OFFSET);
        final double geodeticLat = degToRad(lonlat.y);
        final double authalicLat = geodeticToAuthalic(geodeticLat);
        final double phi = (Math.PI / 2.0 - authalicLat);
        return new Vector2D.Double(theta, phi);
    }

    /**
     * Convert spherical coordinates to longitude/latitude
     * @param theta Longitude in radians (0 to 2π)
     * @param phi latitude in radians (0 to π)
     * @returns [longitude, latitude] in degrees
     */
    public static Vector2D.Double toLonLat(Vector2D.Double spherical) {
        final double longitude = radToDeg(spherical.x) - LONGITUDE_OFFSET;
        final double authalicLat = Math.PI / 2.0 - spherical.y;
        final double geodeticLat = authalicToGeodetic(authalicLat);
        final double latitude = radToDeg(geodeticLat);
        return new Vector2D.Double(longitude, latitude);
    }

    /**
     * Creates a quaternion representing a rotation
     * from the north pole to a given axis.
     * @param axis Spherical coordinate of axis to rotate to
     * @returns quaternion
     */
    public static Quaternion quatFromSpherical(Vector2D.Double axis) {
        final Vector3D.Double cartesian = toCartesian(axis);
        return new Quaternion().fromUnitVectors(new Vector3D.Double(0, 0, 1), cartesian);
    }
}
