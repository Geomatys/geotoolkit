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

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Constants {

    private Constants(){}

    // Golden ratio
    public static final double φ = (1 + Math.sqrt(5)) / 2;

    public static final double TWO_PI = 2 * Math.PI;
    public static final double TWO_PI_OVER_5 = 2 * Math.PI / 5;
    public static final double PI_OVER_5 = Math.PI / 5;
    public static final double PI_OVER_10 = Math.PI / 10;

    // Angles between faces

    /**
     * Angle between pentagon faces (radians) = 116.565°
     *
     */
    public static final double dihedralAngle = 2 * Math.atan(φ);
    /**
     * Angle between pentagon faces (radians) = 63.435°
     * in radians.
     */
    public static final double interhedralAngle = Math.PI - dihedralAngle;
    /**
     * 58.28252558853899
     * in radians.
     */
    public static final double faceEdgeAngle = -0.5 * Math.PI + Math.acos(-1 / Math.sqrt(3 - φ));

    /**
     * Distance from center to edge of pentagon face
     */
    public static final double distanceToEdge = φ - 1;
    /**
    * TODO cleaner derivation?
    */
    public static final double distanceToVertex = distanceToEdge / Math.cos(PI_OVER_5);

    /**
     * Warping parameters
     */
    public static enum WarpType {
        high(WARP_FACTORS_HIGH),
        low(WARP_FACTORS_LOW);

        public final WarpFactors factors;

        private WarpType(WarpFactors factors) {
            this.factors = factors;
        }
    }

    public static final class WarpFactors {
        public final double BETA_SCALE;
        public final double RHO_SHIFT;
        public final double RHO_SCALE;
        public final double RHO_SCALE2;

        public WarpFactors(double BETA_SCALE, double RHO_SHIFT, double RHO_SCALE, double RHO_SCALE2) {
            this.BETA_SCALE = BETA_SCALE;
            this.RHO_SHIFT = RHO_SHIFT;
            this.RHO_SCALE = RHO_SCALE;
            this.RHO_SCALE2 = RHO_SCALE2;
        }
    }
    public static final WarpFactors WARP_FACTORS_HIGH = new WarpFactors(
            0.5115918059668587,
            0.9461616498962347,
            0.04001633808056544,
            0.008305829720486808);
    public static final WarpFactors WARP_FACTORS_LOW = new WarpFactors(
            0.5170052913652168,
            0.939689240972851,
            0.008891290305379163,
            0.03962853541477156);

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
    public static final double Rcircumscribed = Math.sqrt(3) * Rmidedge / φ;

}
