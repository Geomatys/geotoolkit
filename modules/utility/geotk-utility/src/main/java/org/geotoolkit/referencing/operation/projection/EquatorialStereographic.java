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

import static java.lang.Math.*;


/**
 * The USGS equatorial case of the {@linkplain Stereographic} projection.
 * This is a special case of oblique stereographic projection for a latitude of origin
 * set to 0&deg;.
 *
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @author Rémi Maréchal (Geomatys)
 */
class EquatorialStereographic extends Stereographic {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5098015759558831875L;

    /**
     * Constructs an equatorial stereographic projection (EPSG equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected EquatorialStereographic(final OperationMethod method, final Parameters parameters) {
        super(method, parameters);
        assert φ0 == 0 : φ0;
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
        final double λ    = srcPts[srcOff];
        final double φ    = srcPts[srcOff + 1];
        final double sinφ = sin(φ);
        final double sinλ = sin(λ);
        final double cosλ = cos(λ);
        final double ssfn = ssfn(φ, sinφ);
        /*
         * The multiplication by k0 is performed by the "denormalize" affine transform.
         */
        if (dstPts != null) {
            final double χ    = 2*atan(ssfn) - PI/2;
            final double cosχ = cos(χ);
            final double A    = 1 + cosχ*cosλ;    // typo in (12-29)
            final double x    = (cosχ * sinλ) / A;
            final double y    = sin(χ) / A;
            assert super.transform(srcPts, srcOff, dstPts, dstOff, false) == null
                && Assertions.checkTransform(dstPts, dstOff, x, y);
            dstPts[dstOff]   = x;
            dstPts[dstOff+1] = y;
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double A        = (1 + ssfn*ssfn);
        final double dχ_dφ    = 2*dssfn_dφ(φ, sinφ, cos(φ))*ssfn / A;
        final double sinχ     = (ssfn*ssfn - 1) / A;
        final double cosχ     = 2*ssfn / A;
        final double cosχcosλ = cosχ*cosλ;
        final double cosχsinλ = cosχ*sinλ;
        final double sinχcosλ = sinχ*cosλ;
        final double F = 1 + cosχcosλ;
        return new Matrix2(
                (cosχsinλ*cosχsinλ/F + cosχcosλ)        / F,
                (sinχcosλ*cosχsinλ/F - sinχ*sinλ)*dχ_dφ / F,
                 cosχsinλ*sinχ / (F*F),
                (sinχcosλ*sinχ/F + cosχ)*dχ_dφ / F);
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
         * Constructs a new map projection from the supplied parameters.
         *
         * @param parameters The parameters of the projection to be created.
         */
        protected Spherical(final OperationMethod method, final Parameters parameters) {
            super(method, parameters);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Matrix transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final boolean derivate) throws ProjectionException
        {
            final double λ        = srcPts[srcOff];
            final double φ        = srcPts[srcOff + 1];
            final double sinφ     = sin(φ);
            final double sinλ     = sin(λ);
            final double cosφ     = cos(φ);
            final double cosλ     = cos(λ);
            final double cosφcosλ = cosφ*cosλ;
            final double cosφsinλ = cosφ*sinλ;
            final double F        = 1 + cosφcosλ;   // Inverse of (21-14)
            final double x        = cosφsinλ / F;   // (21-2)
            final double y        = sinφ     / F;   // (21-13)
            Matrix derivative = null;
            if (derivate) {
                final double sinφcosλ = sinφ*cosλ;
                derivative = new Matrix2(
                        (cosφsinλ*cosφsinλ/F + cosφcosλ)  / F,
                        (sinφcosλ*cosφsinλ/F - sinφ*sinλ) / F,
                         cosφsinλ*sinφ / (F*F),
                        (sinφcosλ*sinφ/F + cosφ) / F);
            }
            // Following part is common to all spherical projections: verify, store and return.
            assert Assertions.checkDerivative(derivative, super.transform(srcPts, srcOff, dstPts, dstOff, derivate))
                && Assertions.checkTransform(dstPts, dstOff, x, y); // dstPts = result from ellipsoidal formulas.
            if (dstPts != null) {
                dstPts[dstOff  ] = x;
                dstPts[dstOff+1] = y;
            }
            return derivative;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void inverseTransform(final double[] srcPts, final int srcOff,
                                        final double[] dstPts, final int dstOff)
                throws ProjectionException
        {
            double x = srcPts[srcOff  ];
            double y = srcPts[srcOff+1];
            final double ρ = hypot(x, y);
            if (ρ < EPSILON) {
                y = 0; // φ0
                x = 0;
            } else {
                final double c    = 2*atan(ρ);
                final double sinc = sin(c);
                final double ct   = ρ*cos(c);
                final double t    = x*sinc;
                y = asin(y * sinc/ρ);  // (20-14)  with φ1=0
                x = atan2(t, ct);
            }
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff  ] = x;
            dstPts[dstOff+1] = y;
        }

        /**
         * Computes using ellipsoidal formulas and compare with the
         * result from spherical formulas. Used in assertions only.
         */
        private boolean checkInverseTransform(final double[] srcPts, final int srcOff,
                                              final double[] dstPts, final int dstOff,
                                              final double λ, final double φ)
                throws ProjectionException
        {
            super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkInverseTransform(dstPts, dstOff, λ, φ);
        }
    }
}
