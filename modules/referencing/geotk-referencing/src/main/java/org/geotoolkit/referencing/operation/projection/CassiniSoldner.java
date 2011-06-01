/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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

import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;

import static java.lang.Math.*;


/**
 * Cassini-Soldner Projection (EPSG code 9806). See the
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
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@Immutable
public class CassiniSoldner extends CassiniOrMercator {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4710150547701615178L;

    /**
     * Contants used for the forward and inverse transform for the eliptical
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
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final CassiniSoldner projection;
        final Parameters parameters = new Parameters(descriptor, values);
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new CassiniSoldner(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected CassiniSoldner(final Parameters parameters) {
        super(parameters);
    }

    /**
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double λ = rollLongitude(srcPts[srcOff]);
        final double φ = srcPts[srcOff + 1];
        final double sinφ = sin(φ);
        final double cosφ = cos(φ);
        final double tanφ = sinφ / cosφ;
        final double n  = 1 / sqrt(1 - excentricitySquared*sinφ*sinφ);
        final double t  = tanφ * tanφ;
        final double a1 = λ * cosφ;
        final double c  = cosφ * cosφ * excentricitySquared / (1 - excentricitySquared);
        final double a2 = a1 * a1;
        dstPts[dstOff  ] = n*a1*(1 - a2*t*(C1 - (8 - t + 8*c)*a2*C2));
        dstPts[dstOff+1] = mlfn(φ, sinφ, cosφ) + n*tanφ*a2*(0.5 + (5 - t + 6*c)*a2*C3);
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double x = srcPts[srcOff];
        final double y = srcPts[srcOff + 1];
        final double ph1 = inv_mlfn(y);
        final double tn  = tan(ph1);
        final double t   = tn * tn;
        double n = sin(ph1);
        double r = 1 / (1 - excentricitySquared*n*n);
        n = sqrt(r);
        r *= (1 - excentricitySquared)*n;
        final double dd  = x / n;
        final double d2  = dd*dd;
        dstPts[dstOff  ] = unrollLongitude(dd*(1 + t*d2*(-C4+(1 + 3*t)*d2*C5)) / cos(ph1));
        dstPts[dstOff+1] = ph1 - (n*tn/r)*d2*(0.5-(1 + 3*t)*d2*C3);
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
    @Immutable
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
            final double λ = rollLongitude(srcPts[srcOff]);
            final double φ = srcPts[srcOff + 1];
            final double x = asin (cos(φ) * sin(λ));
            final double y = atan2(tan(φ) , cos(λ));
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
            final double λ = srcPts[srcOff];
            if (abs(λ) < ASSERTION_DOMAIN) {
                super.transform(srcPts, srcOff, dstPts, dstOff);
                return Assertions.checkTransform(dstPts, dstOff, x, y, 1E-4);
            } else {
                return true;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void inverseTransform(final double[] srcPts, final int srcOff,
                                        final double[] dstPts, final int dstOff)
                throws ProjectionException
        {
            final double x = srcPts[srcOff];
            final double y = srcPts[srcOff + 1];
            final double φ = asin(sin(y) * cos(x));
            final double λ = unrollLongitude(atan2(tan(x), cos(y)));
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff]   = λ;
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
