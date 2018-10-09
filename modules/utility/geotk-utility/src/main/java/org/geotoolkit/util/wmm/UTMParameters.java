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
 * ava port of the NOAA's MAGtype_UTMParameters structure
 * @author Hasdenteufel Eric (Geomatys)
 */
class UTMParameters {
    double Easting; /* (X) in meters*/
    double Northing; /* (Y) in meters */
    int Zone; /*UTM Zone*/
    char HemiSphere;
    double CentralMeridian;
    double ConvergenceOfMeridians;
    double PointScale;
}
