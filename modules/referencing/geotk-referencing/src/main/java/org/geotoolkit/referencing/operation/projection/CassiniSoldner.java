/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.OperationMethod;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;


/**
 * <cite>Cassini-Soldner</cite> projection (EPSG code 9806). See the
 * <A HREF="http://mathworld.wolfram.com/CassiniProjection.html">Cassini projection on MathWorld</A>
 * for an overview. See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.CassiniSoldner}</li>
 * </ul>
 *
 * {@section Description}
 *
 * The Cassini-Soldner Projection is the ellipsoidal version of the Cassini projection for the
 * sphere. It is not conformal but as it is relatively simple to construct it was extensively
 * used in the last century and is still useful for mapping areas with limited longitudinal extent.
 * It has now largely been replaced by the conformal {@linkplain TransverseMercator Transverse
 * Mercator} which it resembles. Like this, it has a straight central meridian along which the
 * scale is true, all other meridians and parallels are curved, and the scale distortion increases
 * rapidly with increasing distance from the central meridian.
 *
 * @author Mauro Bartolomeoli
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 3.00
 * @module
 */
public class CassiniSoldner extends CassiniOrMercator {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4710150547701615178L;

    /**
     * Constants used for the forward and inverse transform for the elliptical
     * case of the Cassini-Soldner.
     */
    private static final double C1 = 0.16666666666666666666,
                                C2 = 0.08333333333333333333,
                                C3 = 0.41666666666666666666,
                                C4 = 0.33333333333333333333,
                                C5 = 0.66666666666666666666;

    /**
     * Creates a Cassini-Soldner projection from the given parameters. The descriptor argument
     * is usually {@link org.geotoolkit.referencing.operation.provider.CassiniSoldner#PARAMETERS},
     * but is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Cassini-Soldner projection.
     *
     * @param  descriptor Typically {@code CassiniSoldner.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final CassiniSoldner projection;
        final Parameters parameters = Parameters.castOrWrap(values);
        if (isSpherical(parameters)) {
            projection = new Spherical(descriptor, parameters);
        } else {
            projection = new CassiniSoldner(descriptor, parameters);
        }
        try {
            return (MathTransform2D) projection.createMapProjection(
                    org.apache.sis.internal.system.DefaultFactories.forBuildin(
                            org.opengis.referencing.operation.MathTransformFactory.class));
        } catch (org.opengis.util.FactoryException e) {
            throw new IllegalArgumentException(e); // TODO
        }
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected CassiniSoldner(final OperationMethod method, final Parameters parameters) {
        super(method, parameters);
    }

    /**
     * Converts the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinate (units in radians)
     * and stores the result in {@code dstPts} (linear distance on a unit sphere). In addition,
     * opportunistically computes the projection derivative if {@code derivate} is {@code true}.
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
        final double sinφ   = sin(φ);
        final double cosφ   = cos(φ);
        final double tanφ   = sinφ / cosφ;
        final double sinφ2  = sinφ*sinφ;
        final double cosφ2  = cosφ*cosφ;
        final double tanφ2  = tanφ * tanφ;
        final double λcosφ  = λ * cosφ;
        final double λcosφ2 = λcosφ * λcosφ;
        final double rn2    = 1 - excentricitySquared * sinφ2;
        final double rn     = sqrt(rn2);
        final double c      = cosφ2 * excentricitySquared / (1 - excentricitySquared);
        if (dstPts != null) {
            dstPts[dstOff  ] = λcosφ*(1 - λcosφ2*tanφ2*(C1 - (8 - tanφ2 + 8*c)*λcosφ2*C2)) / rn;
            dstPts[dstOff+1] = mlfn(φ, sinφ, cosφ) + tanφ*λcosφ2*(0.5 + (5 - tanφ2 + 6*c)*λcosφ2*C3) / rn;
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double sincosφ = sinφ*cosφ;
        final double λ2sinφ2 = λ*λ * sinφ2;
        final double λ2cosφ2 = λ*λ * cosφ2;
        final double en2     = excentricitySquared / rn2;
        final double a = (-C2 * (8*(c + 1) - tanφ2)) * λ2cosφ2;
        final double b = ( C3 * (6* c + 5  - tanφ2)) * λ2cosφ2 + 0.5;
        final double D = ( C1 + a*(1 - tanφ2*(1 - C2*cosφ*(8*c + 1/cosφ2)))) * λ2cosφ2;
        return new Matrix2(
                  (cosφ/rn) * (1 - λ2sinφ2*(5*a + 3*C1)),
                (λ*sinφ/rn) * (1 - λ2sinφ2*(  a +   C1)) * (en2*cosφ2 - 2*D - 1),
             (λ*sincosφ/rn) * (4*b - 1),
               (λ2cosφ2/rn) * (b*(en2*sinφ2 - 2*tanφ2 + 1/cosφ2) - C3*λ2sinφ2*(24*c + 12)) + dmlfn_dφ(sinφ2, cosφ2));
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(final double[] srcPts, final int srcOff,
                                    final double[] dstPts, final int dstOff)
            throws ProjectionException
    {
        final double x  = srcPts[srcOff];
        final double y  = srcPts[srcOff + 1];
        final double φ1 = inv_mlfn(y);
        final double tn = tan(φ1);
        final double t  = tn * tn;
        double n = sin(φ1);
        double r = 1 / (1 - excentricitySquared * (n*n));
        n = sqrt(r);
        r *= (1 - excentricitySquared)*n;
        final double dd  = x / n;
        final double d2  = dd*dd;
        dstPts[dstOff  ] = dd*(1 + t*d2*(-C4+(1 + 3*t)*d2*C5)) / cos(φ1);
        dstPts[dstOff+1] = φ1 - (n*tn/r)*d2*(0.5-(1 + 3*t)*d2*C3);
    }


    /**
     * Provides the transform equations for the spherical case of the Cassini-Soldner projection.
     *
     * @author Mauro Bartolomeoli
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    static final class Spherical extends CassiniSoldner {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 8808830539248891527L;

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
            final double λ    = srcPts[srcOff];
            final double φ    = srcPts[srcOff + 1];
            final double sinλ = sin(λ);
            final double cosλ = cos(λ);
            final double sinφ = sin(φ);
            final double cosφ = cos(φ);
            final double tanφ = sinφ / cosφ;
            final double x    = asin (cosφ * sinλ);
            final double y    = atan2(tanφ , cosλ);
            Matrix derivative = null;
            if (derivate) {
                final double mλφ  = hypot(cosλ, tanφ);
                final double mλφp = mλφ + cosλ;
                final double dyd  = (mλφp*mλφp + tanφ*tanφ)*cosφ / 2;
                final double dxd  = sqrt(1 - (cosφ*cosφ) * (sinλ*sinλ));
                derivative = new Matrix2(
                         cosλ *                  (cosφ / dxd),    // ∂x/∂λ
                        -sinλ *                  (sinφ / dxd),    // ∂x/∂φ
                         sinλ * (1 + cosλ/mλφ) * (sinφ / dyd),    // ∂y/∂λ
                        (mλφp - tanφ*tanφ/mλφ) / (cosφ * dyd));   // ∂y/∂φ
            }
            // Following part is common to all spherical projections: verify, store and return.
            assert Assertions.checkDerivative(derivative, super.transform(srcPts, srcOff, dstPts, dstOff, derivate))
                && Assertions.checkTransform(dstPts, dstOff, x, y, 1E-4); // dstPts = result from ellipsoidal formulas.
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
            final double x = srcPts[srcOff  ];
            final double y = srcPts[srcOff+1];
            final double φ = asin(sin(y) * cos(x));
            final double λ = atan2(tan(x), cos(y));
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff  ] = λ;
            dstPts[dstOff+1] = φ;
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
            if (abs(λ) < ASSERTION_DOMAIN && abs(φ) < 85*(PI/180)) {
                super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
                return Assertions.checkInverseTransform(dstPts, dstOff, λ, φ, 0.1);
            } else {
                return true;
            }
        }
    }
}
