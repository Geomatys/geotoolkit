/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.awt.geom.Point2D;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.resources.Errors;

import static java.lang.Math.*;


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
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @see PolarStereographic
 * @see EquatorialStereographic
 *
 * @since 2.4
 * @module
 */
@Immutable
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
    protected ObliqueStereographic(final Parameters parameters) {
        super(parameters, parameters.latitudeOfOrigin);
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
        parameters.normalize(true).scale(C, 1);
        parameters.validate();
        parameters.normalize(false).scale(r, r);
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
        final double ρ = hypot(x, y);
        if (abs(ρ) < EPSILON) {
            x = 0.0;
            y = phic0;
        } else {
            final double ce   = 2 * atan(ρ);
            final double sinc = sin(ce);
            final double cosc = cos(ce);
            x = atan2(x*sinc, ρ*cosc0*cosc - y*sinc0*sinc);
            y = (cosc * sinc0) + (y * sinc * cosc0 / ρ);
            if (abs(y) >= 1) {
                y = copySign(PI/2, y);
            } else {
                y = asin(y);
            }
        }
        // Begin pj_inv_gauss(...) method inlined
        double num = pow(tan(0.5*y + PI/4)/K, 1/C);
        for (int i=MAXIMUM_ITERATIONS;;) {
            double φ = 2 * atan(num * srat(excentricity*sin(y), -0.5*excentricity)) - PI/2;
            if (abs(φ - y) < ITERATION_TOLERANCE) {
                break;
            }
            y = φ;
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
     * Gets the derivative of this transform at a point.
     *
     * @param  point The coordinate point where to evaluate the derivative.
     * @return The derivative at the specified point as a 2&times;2 matrix.
     * @throws ProjectionException if the derivative can't be evaluated at the specified point.
     */
    @Override
    public Matrix derivative(final Point2D point) throws ProjectionException {
        final double λ = rollLongitude(point.getX());
        final double φ = point.getY();
        final double sinλ     = sin(λ);
        final double cosλ     = cos(λ);
        final double sinE     = sin(0.5*φ + PI/4);
        final double cosE     = cos(0.5*φ + PI/4);
        final double esinφ    = excentricity*sin(φ);
        final double ecosφ    = excentricity*cos(φ);
        final double srat     = srat(esinφ, ratexp);
        final double T        = pow(sinE/cosE, C);
        final double dT_dφ    = 0.5*C/(sinE*cosE);
        final double sratKT   = K*T*srat;
        final double sinU     = (sratKT*sratKT - 1) / (1 + sratKT*sratKT);
        final double cosU     = 2*sratKT / (1 + sratKT*sratKT);
        final double dU_dφ    = 2*sratKT*(dT_dφ + dsrat_dφ(esinφ, ecosφ, ratexp)) / (1 + sratKT*sratKT);

        //X = cosU * sinx / k;
        final double k = 1 + sinc0*sinU + cosc0*cosU*cosλ;
        final double dk_dλ = -cosc0*cosU*sinλ;
        final double dk_dφ = dU_dφ*(sinc0*cosU-cosc0*cosλ*sinU);

        //Y = R / k;
        final double R = (cosc0*sinU - sinc0*cosU*cosλ);
        final double dR_dλ = sinc0*cosU*sinλ;
        final double dR_dφ = dU_dφ*(cosc0*cosU+sinc0*cosλ*sinU);
        return new Matrix2(
                cosU*(cosλ/k-dk_dλ*sinλ/(k*k)),
                sinλ*(-dU_dφ*sinU*k-dk_dφ*cosU)/(k*k),
                (dR_dλ*k-dk_dλ*R)/(k*k),
                (dR_dφ*k-dk_dφ*R)/(k*k));
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
