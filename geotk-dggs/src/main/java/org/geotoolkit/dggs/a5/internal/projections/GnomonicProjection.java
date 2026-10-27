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
package org.geotoolkit.dggs.a5.internal.projections;

/**
 * Gnomonic projection between spherical and polar coordinates.
 *
 * <p>Port of upstream {@code projections/gnomonic.ts}. This class is stateless and thread-safe.</p>
 *
 * @author Felix Palmer - original source code in TypeScript
 * @author Johann Sorel (Geomatys) - code ported to Java
 */
public class GnomonicProjection {

    /**
     * Projects spherical coordinates to polar coordinates using gnomonic projection
     * @param spherical Spherical coordinates [theta, phi]
     * @return Polar coordinates [rho, gamma]
     */
    public double[] forward(double[] spherical) {
        final double theta = spherical[0], phi = spherical[1];
        return new double[]{Math.tan(phi), theta};
    }

    /**
     * Unprojects polar coordinates to spherical coordinates using gnomonic projection
     * @param polar Polar coordinates [rho, gamma]
     * @return Spherical coordinates [theta, phi]
     */
    public double[] inverse(double[] polar) {
        final double rho = polar[0], gamma = polar[1];
        return new double[]{gamma, Math.atan(rho)};
    }
}
