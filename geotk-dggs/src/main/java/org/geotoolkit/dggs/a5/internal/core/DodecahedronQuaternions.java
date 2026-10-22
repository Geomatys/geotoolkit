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

/**
 * Quaternions rotating the north pole to each of the twelve dodecahedron face centers.
 *
 * <p>Port of upstream {@code core/dodecahedron-quaternions.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class DodecahedronQuaternions {

    private DodecahedronQuaternions() {
    }

    private static final double SQRT5 = Math.sqrt(5);
    private static final double INV_SQRT5 = Math.sqrt(0.2);

    // Dodecahedron face centers (origins) can be defined exactly using trigonometry
    // The north and south poles are just at z=1 and z=-1
    // Then there are two rings at z = ±INV_SQRT5, with radius 2 * INV_SQRT5

    // Exact values for defining a regular pentagon (with radius 1). It is correct to use a radius
    // of 1 as we want to obtain the axes of rotations, so the vectors need to be normalized.
    // cos0 = 0;
    // cos36 = (SQRT5 + 1) / 4;
    // cos72 = (SQRT5 - 1) / 4;
    // sin0 = 0;
    // sin36 = Math.sqrt(10 - 2 * SQRT5) / 4;
    // sin72 = Math.sqrt(10 + 2 * SQRT5) / 4;
    //
    // To compute the quaternion use the equation:
    // q = [...sin(alpha) * axis, cos(alpha)]
    // where alpha is the half-angle of rotation from the pole to the face center.

    // Sin/cosine of half angle (alpha) of rotation from pole to first ring
    // For the second ring sin -> cos and cos -> -sin by (pi / 2 - x) identities
    private static final double sinAlpha = Math.sqrt((1 - INV_SQRT5) / 2);
    private static final double cosAlpha = Math.sqrt((1 + INV_SQRT5) / 2);

    // The resulting value simplify a set of expressions. It is much better to compute
    // these directly than using trigonometry
    private static final double A = 0.5; // sin72 * sinAlpha or sin36 * cosAlpha
    private static final double B = Math.sqrt((2.5 - SQRT5) / 10); // cos72 * sinAlpha
    private static final double C = Math.sqrt((2.5 + SQRT5) / 10); // cos36 * cosAlpha
    private static final double D = Math.sqrt((1 + INV_SQRT5) / 8); // cos36 * sinAlpha
    private static final double E = Math.sqrt((1 - INV_SQRT5) / 8); // cos72 * cosAlpha
    private static final double F = Math.sqrt((3 - SQRT5) / 8); // sin36 * sinAlpha
    private static final double G = Math.sqrt((3 + SQRT5) / 8); // sin72 * cosAlpha

    // Face centers projected onto the z=0 plane & normalized
    // 0: North pole,
    // 1-5: First pentagon ring
    // 6-10: Second pentagon ring
    // 11: South pole
    private static final double[][] faceCenters = {
        {0, 0}, // Doesn't actually matter as rotation is 0

        // First ring: five vertices, CCW, multiplied by sinAlpha
        {sinAlpha, 0}, // [cos0, sin0]
        {B, A}, // [cos72, sin72]
        {-D, F}, // [-cos36, sin36]
        {-D, -F}, // [-cos36, -sin36]
        {B, -A}, // [cos72, -sin72]

        // Second ring: the same five vertices but negated (180deg rotation), multiplied by cosAlpha
        {-cosAlpha, 0}, // [-cos0, -sin0]
        {-E, -G}, // [-cos72, -sin72]
        {C, -A}, // [cos36, -sin36]
        {C, A}, // [cos36, sin36]
        {-E, G}, // [-cos72, sin72]

        {0, 0}
    };

    /**
     * Quaternions [x, y, z, w] for each origin, indexed by origin id (0 to 11).
     * Shared constant: callers must not modify the arrays.
     */
    public static final double[][] quaternions;
    static {
        // Obtain by cross product with the z-axis
        final double[][] axes = new double[faceCenters.length][];
        for (int i = 0; i < faceCenters.length; i++) {
            final double x = faceCenters[i][0], y = faceCenters[i][1];
            axes[i] = new double[]{-y, x};
        }

        // Quaternions are obtained from axis of rotation & angle of rotation
        quaternions = new double[axes.length][];
        for (int i = 0; i < axes.length; i++) {
            final double[] axis = axes[i];
            if (i == 0) quaternions[i] = new double[]{0, 0, 0, 1};
            else if (i == 11) quaternions[i] = new double[]{0, -1, 0, 0}; // TODO better to use 1, 0, 0, 0?
            else quaternions[i] = new double[]{axis[0], axis[1], 0, i < 6 ? cosAlpha : sinAlpha};
        }
    }
}
