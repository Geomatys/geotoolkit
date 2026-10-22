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
 * Mathematical constants of the A5 DGGS (all angles in radians).
 *
 * <p>Port of upstream {@code core/constants.ts}.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Constants {

    private Constants() {
    }

    // Golden ratio
    public static final double φ = (1 + Math.sqrt(5)) / 2;

    public static final double TWO_PI = 2 * Math.PI;
    public static final double TWO_PI_OVER_5 = (2 * Math.PI) / 5;
    public static final double PI_OVER_5 = Math.PI / 5;
    public static final double PI_OVER_10 = Math.PI / 10;

    // Angles between faces
    public static final double dihedralAngle = 2 * Math.atan(φ); // Angle between pentagon faces (radians) = 116.565°
    public static final double interhedralAngle = Math.PI - dihedralAngle; // Angle between pentagon faces (radians) = 63.435°
    public static final double faceEdgeAngle = -0.5 * Math.PI + Math.acos(-1 / Math.sqrt(3 - φ)); // = 58.28252558853899

    // Distance from center to edge of pentagon face
    public static final double distanceToEdge = (Math.sqrt(5) - 1) / 2; // φ - 1;
    public static final double distanceToVertex = 3 - Math.sqrt(5); // 2 * (2 - φ);

    // Dodecahedron sphere radii (normalized to unit radius for inscribed sphere)
    /**
     * Radius of the inscribed sphere in dodecahedron
     */
    public static final double Rinscribed = 1;

    /**
     * Radius of the sphere that touches the dodecahedron's edge midpoints
     */
    public static final double Rmidedge = Math.sqrt(3 - φ);

    /**
     * Radius of the circumscribed sphere for dodecahedron
     */
    public static final double Rcircumscribed = (Math.sqrt(3) * Rmidedge) / φ;

    /**
     * Authalic radius of Earth (meters)
     */
    public static final double AUTHALIC_RADIUS_EARTH = 6371007.2; // m

    /**
     * Authalic surface area of Earth (square meters)
     */
    public static final double AUTHALIC_AREA_EARTH = 4 * Math.PI * AUTHALIC_RADIUS_EARTH * AUTHALIC_RADIUS_EARTH; // m^2
}
