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

import org.apache.sis.geometries.math.Vector2D;
import static org.geotoolkit.dggs.a5.internal.Constants.*;

/**
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public final class Warp {

    private static final double betaMax = PI_OVER_5;
    private static final double WARP_SCALER(WarpType type) {
        return _warpBeta(betaMax, type) / betaMax;
    }

    private Warp(){}

    /**
     * @param gamma radians
     * @return radians
     */
    public static final double normalizeGamma(double gamma) {
        final double segment = gamma / TWO_PI_OVER_5;
        final double sCenter = Math.round(segment);
        final double sOffset = segment - sCenter;

        // Azimuthal angle from triangle bisector
        final double beta = sOffset * TWO_PI_OVER_5;
        return beta;
    }

    private static final double _warpBeta(double beta, WarpType warpType) {
        final double BETA_SCALE = warpType.factors.BETA_SCALE;
        final double x = beta * BETA_SCALE;
        return Math.tan(x);
    }

    private static final double _unwarpBeta(double beta, WarpType warpType) {
        final double BETA_SCALE = warpType.factors.BETA_SCALE;
        final double shiftedBeta = Math.atan(beta);
        return shiftedBeta / BETA_SCALE;
    }

    public static final double warpBeta(double beta, WarpType warpType) {
        return _warpBeta(beta, warpType) / WARP_SCALER(warpType);
    }

    public static final double unwarpBeta(double beta, WarpType warpType) {
        return _unwarpBeta(beta * WARP_SCALER(warpType), warpType);
    }

    private static double rhoScaleFactor(double betaRatio, WarpType warpType) {
        final double RHO_SHIFT = warpType.factors.RHO_SHIFT;
        final double RHO_SCALE = warpType.factors.RHO_SCALE;
        final double RHO_SCALE2 = warpType.factors.RHO_SCALE2;
        final double beta2 = betaRatio * betaRatio;
        final double beta4 = beta2 * beta2;
        return (RHO_SHIFT - RHO_SCALE * beta2 - RHO_SCALE2 * beta4);
    }

    private static final double warpRho(double rho, double beta, WarpType warpType) {
        final double betaRatio = Math.abs(beta) / betaMax;
        final double shiftedRho = rho * rhoScaleFactor(betaRatio, warpType);
        return Math.tan(shiftedRho);
    }

    private static final double unwarpRho(double rho, double beta, WarpType warpType) {
        final double betaRatio = Math.abs(beta) / betaMax;
        final double shiftedRho = Math.atan(rho);
        return shiftedRho / rhoScaleFactor(betaRatio, warpType);
    }

    /**
     * @param p Polar
     * @return Polar
     */
    public static final Vector2D.Double warpPolar(Vector2D.Double p, WarpType warpType) {
        final double beta = normalizeGamma(p.y);

        final double beta2 = warpBeta(beta, warpType);
        final double deltaBeta = beta2 - beta;

        // Distance to edge will change, so shift rho to match
        final double scale = Math.cos(beta) / Math.cos(beta2);
        final double rhoOut = scale * p.x;

        final double rhoMax = distanceToEdge / Math.cos(beta2);
        final double scaler2 = warpRho(rhoMax, beta2, warpType) / rhoMax;
        final double rhoWarped = warpRho(rhoOut, beta2, warpType) / scaler2;

        return new Vector2D.Double(rhoWarped, p.y + deltaBeta);
    }

    /**
     * @param p Polar
     * @return Polar
     */
    public static final Vector2D.Double unwarpPolar(Vector2D.Double p, WarpType warpType) {
        final double beta2 = normalizeGamma(p.y);
        final double beta = unwarpBeta(beta2, warpType);
        final double deltaBeta = beta2 - beta;

        // Reverse the rho warping
        final double rhoMax = distanceToEdge / Math.cos(beta2);
        final double scaler2 = warpRho(rhoMax, beta2, warpType) / rhoMax;
        final double rhoUnwarped = unwarpRho(p.x * scaler2, beta2, warpType);

        // Reverse the scale adjustment
        final double scale = Math.cos(beta) / Math.cos(beta2);
        return new Vector2D.Double(rhoUnwarped / scale, p.y - deltaBeta);
    }

}
