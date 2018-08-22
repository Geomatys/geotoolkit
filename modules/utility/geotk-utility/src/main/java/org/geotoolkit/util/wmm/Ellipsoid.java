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
 * Java port of the NOAA's MAGtype_Ellipsoid structure
 * @author Hasdenteufel Eric (Geomatys)
 */
public enum Ellipsoid {
    WGS_84( 6378.137, 6356.7523142, 1/298.257223563, 6371.2);

    double a; /*semi-major axis of the ellipsoid*/
    double b; /*semi-minor axis of the ellipsoid*/
    double fla; /* flattening */
    double epssq; /*first eccentricity squared */
    double eps; /* first eccentricity */
    double re; /* mean radius of  ellipsoid*/

    private Ellipsoid(double a, double b, double fla, double re) {
        this.a = a;
        this.b = b;
        this.fla = fla;
        this.re = re;
        this.eps = Math.sqrt(1.0 - (this.b * this.b) / (this.a * this.a));
        this.epssq = (this.eps * this.eps);
    }

}
