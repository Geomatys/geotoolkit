/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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


/**
 * The USGS equatorial case of the {@linkplain Stereographic stereographic} projection.
 * This is a special case of oblique stereographic projection for a latitude of origin
 * set to 0&deg;.
 *
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.00
 *
 * @see PolarStereographic
 * @see ObliqueStereographic
 *
 * @since 2.0
 * @module
 */
public class EquatorialStereographic extends Stereographic {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5098015759558831875L;

    /**
     * Constructs an equatorial stereographic projection (EPSG equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected EquatorialStereographic(final Parameters parameters) {
        super(parameters);
        assert phi0 == 0 : phi0;
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
        final double chi = 2*atan(ssfn(y, sin(y))) - PI/2;
        final double cosChi = cos(chi);
        final double A = 1 + cosChi * cos(x);    // typo in (12-29)
        x = (cosChi * sin(x)) / A;
        y = sin(chi) / A;
        /*
         * The multiplication by k0 is performed by the "denormalize" affine transform.
         */
        assert checkTransform(srcPts, srcOff, dstPts, dstOff, x, y);
        dstPts[dstOff]   = x;
        dstPts[dstOff+1] = y;
    }

    /**
     * Computes using oblique formulas and compare with the
     * result from equatorial formulas. Used in assertions only.
     */
    private boolean checkTransform(final double[] srcPts, final int srcOff,
                                   final double[] dstPts, final int dstOff,
                                   final double x, final double y)
            throws ProjectionException
    {
        super.transform(srcPts, srcOff, dstPts, dstOff);
        return Assertions.checkTransform(dstPts, dstOff, x, y);
    }

    /**
     * Provides the transform equations for the spherical case of the
     * Equatorial Stereographic projection.
     *
     * @author Gerald Evenden (USGS)
     * @author André Gosselin (MPO)
     * @author Martin Desruisseaux (MPO, IRD)
     * @author Rueben Schulz (UBC)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    static final class Spherical extends EquatorialStereographic {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -4790138052004333003L;

        /**
         * Constructs a new map projection from the suplied parameters.
         *
         * @param parameters The parameters of the projection to be created.
         */
        protected Spherical(final Parameters parameters) {
            super(parameters);
            parameters.ensureSpherical();
        }

        /**
         * Returns {@code true} since this class uses spherical formulas.
         */
        @Override
        final boolean isSpherical() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void transform(final double[] srcPts, final int srcOff,
                                 final double[] dstPts, final int dstOff)
                throws ProjectionException
        {
            double x = rollLongitude(srcPts[srcOff]);
            double y = srcPts[srcOff + 1];
            final double coslat = cos(y);
            final double f = 1 + coslat * cos(x); // Inverse of (21-14)
            x = coslat * sin(x) / f;   // (21-2)
            y = sin(y)          / f;   // (21-13)

            assert checkTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff]   = x;
            dstPts[dstOff+1] = y;
        }

        /**
         * Computes using ellipsoidal formulas and compare with the
         * result from spherical formulas. Used in assertions only.
         */
        private boolean checkTransform(final double[] srcPts, final int srcOff,
                                       final double[] dstPts, final int dstOff,
                                       final double x, final double y)
                throws ProjectionException
        {
            super.transform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkTransform(dstPts, dstOff, x, y);
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
            if (rho < EPSILON) {
                y = 0; // phi0
                x = 0;
            } else {
                final double c    = 2*atan(rho);
                final double sinc = sin(c);
                final double ct   = rho*cos(c);
                final double t    = x*sinc;
                y = asin(y * sinc/rho);  // (20-14)  with phi1=0
                x = atan2(t, ct);
            }
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff]   = x;
            dstPts[dstOff+1] = y;
        }

        /**
         * Computes using ellipsoidal formulas and compare with the
         * result from spherical formulas. Used in assertions only.
         */
        private boolean checkInverseTransform(final double[] srcPts, final int srcOff,
                                              final double[] dstPts, final int dstOff,
                                              final double lambda, final double phi)
                throws ProjectionException
        {
            super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkInverseTransform(dstPts, dstOff, lambda, phi);
        }
    }
}
