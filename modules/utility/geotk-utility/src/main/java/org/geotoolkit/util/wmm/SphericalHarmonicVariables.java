/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2018, Geomatys.
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.util.wmm;

/**
 * Java port of the NOAA's MAGtype_SphericalHarmonicVariables structure
 * @author Hasdenteufel Eric (Geomatys)
 */
class SphericalHarmonicVariables {

    double[] RelativeRadiusPower; /* [earth_reference_radius_km / sph. radius ]^n  */
    double[] cos_mlambda; /*cp(m)  - cosine of (m*spherical coord. longitude)*/
    double[] sin_mlambda; /* sp(m)  - sine of (m*spherical coord. longitude) */

    SphericalHarmonicVariables(int numTerms) {
        this.RelativeRadiusPower = new double[numTerms + 1];
        this.cos_mlambda = new double[numTerms + 1];
        this.sin_mlambda = new double[numTerms + 1];
    }



}
