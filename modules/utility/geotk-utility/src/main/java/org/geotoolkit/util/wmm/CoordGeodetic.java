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
 * Java port of the NOAA's MAGtype_CoordGeodetic structure
 * @author Hasdenteufel Eric (Geomatys)
 */
public class CoordGeodetic {
    /**
     * longitude in decimal degrees
     */
    double lambda;

    /**
     * geodetic latitude in decimal degrees
     */
    double phi;

    /**
     * height above the ellipsoid WGS-84 in Km
     */
    double HeightAboveEllipsoid;

    /**
     *
     * @param latitude North Latitude positive in decimal degrees
     * @param longitude East longitude positive, West negative in decimal degrees
     * @param heightAboveEllipsoid height above WGS-84 Ellipsoid in Km
     */
    public CoordGeodetic(double latitude, double longitude, double heightAboveEllipsoid) {
        this.lambda = longitude;
        this.phi = latitude;
        this.HeightAboveEllipsoid = heightAboveEllipsoid;
    }


    CoordSpherical toSpherical() {
        return toSpherical(Ellipsoid.WGS_84);
    }

    CoordSpherical toSpherical(Ellipsoid Ellip) {
        final CoordSpherical cs = new CoordSpherical();

        /*
        ** Convert geodetic coordinates, (defined by the WGS-84
        ** reference ellipsoid), to Earth Centered Earth Fixed Cartesian
        ** coordinates, and then to spherical coordinates.
         */
        final double CosLat = Math.cos(Math.toRadians(this.phi));
        final double SinLat = Math.sin(Math.toRadians(this.phi));

        /* compute the local radius of curvature on the WGS-84 reference ellipsoid */
        final double rc = Ellip.a / Math.sqrt(1.0 - Ellip.epssq * SinLat * SinLat);

        /* compute ECEF Cartesian coordinates of specified point (for longitude=0) */
        final double xp = (rc + this.HeightAboveEllipsoid) * CosLat;
        final double zp = (rc * (1.0 - Ellip.epssq) + this.HeightAboveEllipsoid) * SinLat;
        /* compute spherical radius and angle lambda and phi of specified point */
        cs.r =  Math.sqrt(xp * xp + zp * zp);
        cs.phig = Math.toDegrees( Math.asin(zp / cs.r)); /* geocentric latitude */
        cs.lambda = this.lambda;

       return cs;
    }

}
