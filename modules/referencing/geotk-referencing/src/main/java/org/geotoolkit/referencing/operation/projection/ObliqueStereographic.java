/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.OperationMethod;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.geotoolkit.resources.Errors;

import static java.lang.Math.*;


/**
 * <cite>Oblique Stereographic</cite> projection (EPSG code 9809).
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
 *        Relevant files are: {@code PJ_sterea.c}, {@code pj_gauss.c},
 *        {@code pj_fwd.c}, {@code pj_inv.c} and {@code lib_proj.h}</li>
 *   <li>Gerald Evenden. <A HREF="http://members.bellatlantic.net/~vze2hc4d/proj4/sterea.pdf">
 *       "Supplementary PROJ.4 Notes - Oblique Stereographic Alternative"</A>.</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7, Version 19.</li>
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
 * @author Rémi Maréchal (Geomatys)
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
     * Constants used in the forward and inverse gauss methods.
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
    protected ObliqueStereographic(final OperationMethod method, final Parameters parameters) {
        super(method, parameters, parameters.doubleValue(org.geotoolkit.referencing.operation.provider.ObliqueStereographic.LATITUDE_OF_ORIGIN));
        final double cphi = cosφ0 * cosφ0;
        final double r = 2 * sqrt(1-excentricitySquared) / (1 - excentricitySquared*(sinφ0*sinφ0));
        C      = sqrt(1 + excentricitySquared*(cphi*cphi) / (1 - excentricitySquared));
        phic0  = asin(sinφ0 / C);
        sinc0  = sin(phic0);
        cosc0  = cos(phic0);
        ratexp = 0.5 * C * excentricity;
        K      = tan(0.5 * phic0 + PI/4) /
                 (pow(tan(0.5 * φ0 + PI/4), C) * srat(excentricity*sinφ0, ratexp));
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        getContextualParameters().getMatrix(true).convertAfter(0, C, null);
        getContextualParameters().getMatrix(false).convertBefore(0, r, null);
        getContextualParameters().getMatrix(false).convertBefore(1, r, null);
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws ProjectionException
    {
        final double λ = srcPts[srcOff];
        final double φ = srcPts[srcOff + 1];
        final double sinλ = sin(λ);
        final double cosλ = cos(λ);
        if (dstPts != null) {
            final double c = 2 * atan(K * pow(tan(0.5*φ + PI/4), C) * srat(excentricity * sin(φ), ratexp)) - PI/2;
            final double sinc = sin(c);
            final double cosc = cos(c);
            final double k = 1 + sinc0*sinc + cosc0*cosc*cosλ;
            dstPts[dstOff] = cosc * sinλ / k;
            dstPts[dstOff+1] = (cosc0*sinc - sinc0*cosc*cosλ) / k;
        }
        /*
         * We can not compare easily with the calculation performed by the superclass
         * because the (de)normalize affine transforms are not set in the same way.
         */
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double sinE     = sin(0.5*φ + PI/4);
        final double cosE     = cos(0.5*φ + PI/4);
        final double esinφ    = excentricity*sin(φ);
        final double ecosφ    = excentricity*cos(φ);
        final double T        = pow(sinE/cosE, C);
        final double dT_dφ    = 0.5*C/(sinE*cosE);
        final double sratKT   = K*T*srat(esinφ, ratexp);
        final double si       = sratKT + 1/sratKT;
        final double di       = sratKT - 1/sratKT;
        final double di_sinc0 = di * sinc0;
        final double di_cosc0 = di * cosc0;
        final double dU_dφ    = 2*(dT_dφ + dsrat_dφ(esinφ, ecosφ, ratexp)) / si;
        final double k        = di_sinc0 + 2*cosλ*cosc0 + si;
        final double r        = di_cosc0 - 2*cosλ*sinc0;
        final double dkφ      = 2*sinc0 - cosλ*di_cosc0;
        final double drφ      = 2*cosc0 + cosλ*di_sinc0;
        final double k2       = k*k;
        return new Matrix2(
                2*(cosλ*(si + di_sinc0) + 2*cosc0) / k2,
                -dU_dφ*sinλ*(di*k + dkφ*2)         / k2,
                2*sinλ*(sinc0*k + cosc0*r)         / k2,
                dU_dφ*(drφ*k - dkφ*r)              / k2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void inverseTransform(final double[] srcPts, final int srcOff,
                                    final double[] dstPts, final int dstOff)
            throws ProjectionException
    {
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        final double ρ = hypot(x, y);
        double λ, φ;
        if (abs(ρ) < EPSILON) {
            λ = 0.0;
            φ = phic0;
        } else {
            final double ce   = 2 * atan(ρ);
            final double sinc = sin(ce);
            final double cosc = cos(ce);
            λ = atan2(x*sinc, ρ*cosc0*cosc - y*sinc0*sinc);
            φ = (cosc * sinc0) + (y * sinc * cosc0 / ρ);
            if (abs(φ) >= 1) {
                φ = copySign(PI/2, φ);
            } else {
                φ = asin(φ);
            }
        }
        // Begin pj_inv_gauss(...) method inlined
        final double num = pow(tan(0.5*φ + PI/4)/K, 1/C);
        for (int i=MAXIMUM_ITERATIONS;;) {
            final double φi = 2 * atan(num * srat(excentricity*sin(φ), -0.5*excentricity)) - PI/2;
            if (abs(φi - φ) < ITERATION_TOLERANCE) {
                break;
            }
            φ = φi;
            if (--i < 0) {
                throw new ProjectionException(Errors.format(Errors.Keys.NoConvergence));
            }
        }
        // End pj_inv_gauss(...) method inlined

        dstPts[dstOff  ] = λ;
        dstPts[dstOff+1] = φ;
        /*
         * We can not compare easily with the calculation performed by the superclass
         * because the (de)normalize affine transforms are not set in the same way.
         */
    }

    /**
     * A simple function used by the transforms.
     *
     * @param  sinφ the sine of latitude multiplicate by {@code excentricity}.
     * @param  cosφ The cosine of latitude multiplicate by {@code excentricity}.
     * @param  exp  The exponent, which is usually (but not always) {@link #ratexp}.
     */
    private static double srat(final double esinp, final double exp) {
        return pow((1.0 - esinp) / (1.0 + esinp), exp);
    }

    /**
     * Computes the derivative of the {@link #srat(double, double)} method divided by {@code srat}.
     * Callers must multiply the return value by {@code srat} in order to get the actual value.
     *
     * @param  sinφ the sine of latitude multiplied by {@code excentricity}.
     * @param  cosφ The cosine of latitude multiplied by {@code excentricity}.
     * @param  exp  The exponent, which is usually (but not always) {@link #ratexp}.
     * @return The {@code srat} derivative at the latitude.
     *
     * @since 3.19
     */
    private static double dsrat_dφ(final double esinφ, final double ecosφ, double exp) {
        return -2*exp*ecosφ / (1.0 - esinφ*esinφ);
    }
}
