/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains formulas from the PROJ package of USGS.
 *    USGS's work is fully acknowledged here. This derived work has
 *    been relicensed under LGPL with Frank Warmerdam's permission.
 */
package org.geotoolkit.referencing.operation.projection;

import static java.lang.Math.*;

import org.geotoolkit.resources.Errors;


/**
 * Oblique Stereographic projection (EPSG code 9809).
 * The formulas used below are not from the EPSG, but rather those of the
 * "<cite>Oblique Stereographic Alternative</cite>" in the {@code libproj4} package
 * written by Gerald Evenden. His work is acknowledged here and greatly appreciated.
 * <p>
 * The forward equations used in {@code libproj4} are the same as those given in the
 * UNB reports for the Double Stereographic. The inverse equations are similar,
 * but use different methods to iterate for the latitude.
 *
 * {@section References}
 * <ul>
 *   <li>{@code libproj4} is available at
 *       <A HREF="http://members.bellatlantic.net/~vze2hc4d/proj4/">libproj4 Miscellanea</A>.<br>
 *        Relevent files are: {@code PJ_sterea.c}, {@code pj_gauss.c},
 *        {@code pj_fwd.c}, {@code pj_inv.c} and {@code lib_proj.h}</li>
 *   <li>Gerald Evenden. <A HREF="http://members.bellatlantic.net/~vze2hc4d/proj4/sterea.pdf">
 *       "Supplementary PROJ.4 Notes - Oblique Stereographic Alternative"</A>.</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidence Note Number 7, Version 19.</li>
 *   <li>Krakiwsky, E.J., D.B. Thomson, and R.R. Steeves. 1977.<br>
 *       A Manual for Geodetic Coordinate Transformations in the Maritimes.<br>
 *       Geodesy and Geomatics Engineering, UNB. Technical Report No. 48.</li>
 *   <li>Thomson, D.B., M.P. Mepham and R.R. Steeves. 1977.<br>
 *       The Stereographic Double Projection.<br>
 *       Surveying Engineering, University of New Brunswick. Technical Report No. 46.</li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see PolarStereographic
 * @see EquatorialStereographic
 *
 * @since 2.4
 * @module
 */
public class ObliqueStereographic extends Stereographic {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = -1454098847621943639L;

    /**
     * The tolerance used for the inverse iteration.
     * This is smaller than the tolerance in the superclass.
     */
    @SuppressWarnings("hiding")
    private static final double ITERATION_TOLERANCE = 1E-14;

    /**
     * Contstants used in the forward and inverse gauss methods.
     */
    private final double C, K, ratexp;

    /**
     * Constants for the EPSG stereographic transform.
     */
    private final double phic0, cosc0, sinc0;

    /**
     * Constructs an oblique stereographic projection (EPSG equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected ObliqueStereographic(final Parameters parameters) {
        super(parameters, parameters.latitudeOfOrigin);
        final double cphi = cosphi0 * cosphi0;
        final double R2 = 2 * sqrt(1-excentricitySquared) / (1 - excentricitySquared*(sinphi0*sinphi0));
        C      = sqrt(1 + excentricitySquared*(cphi*cphi) / (1 - excentricitySquared));
        phic0  = asin(sinphi0 / C);
        sinc0  = sin(phic0);
        cosc0  = cos(phic0);
        ratexp = 0.5 * C * excentricity;
        K      = tan(0.5 * phic0 + PI/4) /
                 (pow(tan(0.5 * phi0 + PI/4), C) * srat(excentricity*sinphi0, ratexp));
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        parameters.normalize(true).scale(C, 1);
        parameters.validate();
        parameters.normalize(false).scale(R2, R2);
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        double x = rollLongitude(srcPts[srcOff]);
        double y = srcPts[srcOff + 1];
        y = 2 * atan(K * pow(tan(0.5*y + PI/4), C) * srat(excentricity * sin(y), ratexp)) - PI/2;
        final double sinc = sin(y);
        final double cosc = cos(y);
        final double cosl = cos(x);
        final double k = 1 + sinc0*sinc + cosc0*cosc*cosl;
        dstPts[dstOff] = cosc * sin(x) / k;
        dstPts[dstOff+1] = (cosc0*sinc - sinc0*cosc*cosl) / k;
        /*
         * We can not compare easily with the calculation performed by the superclass
         * because the (de)normalize affine transforms are not set in the same way.
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void inverseTransform(final double[] srcPts, final int srcOff,
                                    final double[] dstPts, final int dstOff)
            throws ProjectionException
    {
        double x = unrollLongitude(srcPts[srcOff]);
        double y = srcPts[srcOff + 1];
        final double rho = hypot(x, y);
        if (abs(rho) < EPSILON) {
            x = 0.0;
            y = phic0;
        } else {
            final double ce   = 2 * atan(rho);
            final double sinc = sin(ce);
            final double cosc = cos(ce);
            x = atan2(x*sinc, rho*cosc0*cosc - y*sinc0*sinc);
            y = (cosc * sinc0) + (y * sinc * cosc0 / rho);
            if (abs(y) >= 1) {
                y = copySign(PI/2, y);
            } else {
                y = asin(y);
            }
        }
        // Begin pj_inv_gauss(...) method inlined
        double num = pow(tan(0.5*y + PI/4)/K, 1/C);
        for (int i=MAXIMUM_ITERATIONS;;) {
            double phi = 2 * atan(num * srat(excentricity*sin(y), -0.5*excentricity)) - PI/2;
            if (abs(phi - y) < ITERATION_TOLERANCE) {
                break;
            }
            y = phi;
            if (--i < 0) {
                throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
            }
        }
        // End pj_inv_gauss(...) method inlined

        dstPts[dstOff]   = x;
        dstPts[dstOff+1] = y;
        /*
         * We can not compare easily with the calculation performed by the superclass
         * because the (de)normalize affine transforms are not set in the same way.
         */
    }

    /**
     * A simple function used by the transforms.
     */
    private static double srat(double esinp, double exp) {
        return pow((1.0 - esinp) / (1.0 + esinp), exp);
    }
}
