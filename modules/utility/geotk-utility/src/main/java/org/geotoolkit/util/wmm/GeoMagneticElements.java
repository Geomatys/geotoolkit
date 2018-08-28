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
 * Java port of the NOAA's MAGtype_GeoMagneticElements structure
 * @author Hasdenteufel Eric (Geomatys)
 */
public class GeoMagneticElements {

    /**
     * Angle between the magnetic field vector and true north, positive east, in degrees.
     */
    public double Decl;

    /**
     * Angle between the magnetic field vector and the horizontal plane, positive down, in degrees.
     */
    public double Incl;

    /**
     * Magnetic Field Strength, in nT.
     */
    public double F;

    /**
     * Horizontal Magnetic Field Strength, in nT.
     */
    public double H;

    /**
     * Northern component of the magnetic field vector, in nT.
     */
    public double X;

    /**
     * Eastern component of the magnetic field vector, in nT.
     */
    public double Y;

    /**
     * Downward component of the magnetic field vector, in nT.
     */
    public double Z;

    /**
     * The Grid Variation, in degrees.
     */
    public double GV;

    /**
     * Yearly Rate of change in declination, in °/yr.
     */
    public double Decldot;

    /**
     * Yearly Rate of change in inclination, in °/yr.
     */
    public double Incldot;

    /**
     * Yearly rate of change in Magnetic field strength, in nT/yr.
     */
    public double Fdot;

    /**
     * Yearly rate of change in horizontal field strength, in nT/yr.
     */
    public double Hdot;

    /**
     * Yearly rate of change in the northern component, in nT/yr.
     */
    public double Xdot;

    /**
     * Yearly rate of change in the eastern component, in nT/yr.
     */
    public double Ydot;

    /**
     * Yearly rate of change in the downward component, in nT/yr.
     */
    public double Zdot;

    /**
     * Yearly rate of change in grid variation, in °/yr.
     */
    public double GVdot; /*16. */

    /*These error values are the NGDC error model*/
    private static final double WMM_UNCERTAINTY_F           = 152.0;
    private static final double WMM_UNCERTAINTY_H           = 133.0;
    private static final double WMM_UNCERTAINTY_X           = 138.0;
    private static final double WMM_UNCERTAINTY_Y           = 89.0;
    private static final double WMM_UNCERTAINTY_Z           = 165.0;
    private static final double WMM_UNCERTAINTY_I           = 0.22;
    private static final double WMM_UNCERTAINTY_D_OFFSET    = 0.24;
    private static final double WMM_UNCERTAINTY_D_COEF      = 5432.0;

    /**
     * compute range error for each elements (+- fields)
     * @return
     */
    public GeoMagneticElements computeErrors() {
        GeoMagneticElements uncertainty = new GeoMagneticElements();
        uncertainty.F = WMM_UNCERTAINTY_F;
        uncertainty.H = WMM_UNCERTAINTY_H;
        uncertainty.X = WMM_UNCERTAINTY_X;
        uncertainty.Z = WMM_UNCERTAINTY_Z;
        uncertainty.Incl = WMM_UNCERTAINTY_I;
        uncertainty.Y = WMM_UNCERTAINTY_Y;
        final double decl_variable = (WMM_UNCERTAINTY_D_COEF / H);
        final double decl_constant = (WMM_UNCERTAINTY_D_OFFSET);
        uncertainty.Decl = Math.sqrt(decl_constant * decl_constant + decl_variable * decl_variable);
        if (uncertainty.Decl > 180.0) {
            uncertainty.Decl = 180.0;
        }
        return uncertainty;
    }
}
