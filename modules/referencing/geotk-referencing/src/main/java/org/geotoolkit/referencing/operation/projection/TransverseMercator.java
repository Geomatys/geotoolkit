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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.OperationMethod;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.geotoolkit.resources.Errors;

import org.apache.sis.parameter.Parameters;
import static java.lang.Math.*;
import static org.apache.sis.math.MathFunctions.atanh;


/**
 * <cite>Transverse Mercator</cite> projection (EPSG codes 9807, 9808). See the
 * <A HREF="http://mathworld.wolfram.com/MercatorProjection.html">Mercator projection on MathWorld</A>
 * for an overview. See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.TransverseMercator}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.TransverseMercator.SouthOrientated}</li>
 * </ul>
 *
 * {@section Description}
 *
 * This is a cylindrical projection, in which the cylinder has been rotated 90&deg;. Instead of
 * being tangent to the equator (or to an other standard latitude), it is tangent to a central
 * meridian. Deformation are more important as we are going further from the central meridian.
 * The Transverse Mercator projection is appropriate for region which have a greater extent
 * north-south than east-west.
 * <p>
 * The elliptical equations used here are series approximations, and their accuracy decreases as
 * points move farther from the central meridian of the projection. The forward equations here are
 * accurate to less than a millimetre &plusmn;10 degrees from the central meridian, a few
 * millimetres &plusmn;15 degrees from the central meridian and a few centimetres &plusmn;20
 * degrees from the central meridian. The spherical equations are not approximations and should
 * always give the correct values.
 * <p>
 * There are a number of versions of the Transverse Mercator projection including the Universal
 * (UTM) and Modified (MTM) Transverses Mercator projections. In these cases the earth is divided
 * into zones. For the UTM the zones are 6 degrees wide, numbered from 1 to 60 proceeding east from
 * 180 degrees longitude, and between latitude 84 degrees North and 80 degrees South. The central
 * meridian is taken as the center of the zone and the latitude of origin is the equator. A scale
 * factor of 0.9996 and false easting of 500000 metres is used for all zones and a false northing
 * of 10000000 metres is used for zones in the southern hemisphere.
 *
 * {@section References}
 * <ul>
 *   <li> Proj-4.4.6 available at <A HREF="http://www.remotesensing.org/proj">www.remotesensing.org/proj</A><br>
 *        Relevant files are: {@code PJ_tmerc.c}, {@code pj_mlfn.c}, {@code pj_fwd.c} and {@code pj_inv.c}.</li>
 *   <li> John P. Snyder (Map Projections - A Working Manual,<br>
 *        U.S. Geological Survey Professional Paper 1395, 1987).</li>
 *   <li> "Coordinate Conversions and Transformations including Formulas",<br>
 *        EPSG Guidance Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @author Rémi Maréchal (Geomatys)
 *
 * @see Mercator
 * @see ObliqueMercator
 *
 * @since 1.0
 * @module
 */
public class TransverseMercator extends CassiniOrMercator {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4717976245811852528L;

    /**
     * Convenience method computing the zone code from the central meridian.
     * Information about zones convention must be specified in argument. Two
     * widely set of arguments are of Universal Transverse Mercator (UTM) and
     * Modified Transverse Mercator (MTM) projections:
     * <p>
     * UTM projection (zones numbered from 1 to 60):
     *
     * {@preformat java
     *     getZone(-177, 6);
     * }
     *
     * MTM projection (zones numbered from 1 to 120):
     *
     * {@preformat java
     *     getZone(-52.5, -3);
     * }
     *
     * @param  centralLongitudeZone1 Longitude in the middle of zone 1, in decimal degrees
     *         relative to Greenwich. Positive longitudes are toward east, and negative
     *         longitudes toward west.
     * @param  zoneWidth Number of degrees of longitudes in one zone. A positive value
     *         means that zones are numbered from west to east (i.e. in the direction of
     *         positive longitudes). A negative value means that zones are numbered from
     *         east to west.
     * @return The zone number. First zone is numbered 1.
     */
    private static int computeZone(final double centralMeridian, final double centralLongitudeZone1, final double zoneWidth) {
        final double zoneCount = abs(360 / zoneWidth);
        double t;
        t  = centralLongitudeZone1 - 0.5*zoneWidth; // Longitude at the beginning of the first zone.
        t  = toDegrees(centralMeridian) - t;        // Degrees of longitude between the central longitude and longitude 1.
        t  = floor(t/zoneWidth + ANGLE_TOLERANCE);  // Number of zones between the central longitude and longitude 1.
        t -= zoneCount*floor(t/zoneCount);          // If negative, bring back to the interval 0 to (zoneCount-1).
        return ((int) t)+1;
    }

    /**
     * Convenience method returning the meridian in the middle of current zone. This meridian is
     * typically the central meridian. This method may be invoked to make sure that the central
     * meridian is correctly set.
     *
     * @param  centralLongitudeZone1 Longitude in the middle of zone 1, in decimal degrees
     *         relative to Greenwich. Positive longitudes are toward east, and negative
     *         longitudes toward west.
     * @param  zoneWidth Number of degrees of longitudes in one zone. A positive value
     *         means that zones are numbered from west to east (i.e. in the direction of
     *         positive longitudes). A negative value means that zones are numbered from
     *         east to west.
     * @return The central meridian.
     */
    private static double computeCentralMedirian(final double centralMeridian, final double centralLongitudeZone1, final double zoneWidth) {
        double t;
        t  = centralLongitudeZone1 + (getZone(centralMeridian, centralLongitudeZone1, zoneWidth)-1)*zoneWidth;
        t -= 360 * floor((t+180) / 360); // Bring back into [-180..+180] range.
        return t;
    }

    /**
     * Convenience method computing the zone code from the central meridian. This method uses
     * the {@linkplain #scaleFactor scale factor} and {@linkplain #falseEasting false easting}
     * to decide if this is a UTM or MTM case.
     *
     * @return The zone number. Numbering starts at 1.
     * @throws IllegalStateException if the case of the projection cannot be determined.
     */
    private static int getZone(final double centralMeridian, final double scaleFactor, final double falseEasting) throws IllegalStateException {
        // UTM
        if (scaleFactor == 0.9996 && falseEasting == 500000) {
            return computeZone(centralMeridian, -177, 6);
        }
        // MTM
        if (scaleFactor == 0.9999 && falseEasting == 304800){
            return computeZone(centralMeridian, -52.5, -3);
        }
        // unknown
        throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOWN_PROJECTION_TYPE));
    }

    /**
     * Convenience method returning the meridian in the middle of current zone. This meridian is
     * typically the central meridian. This method may be invoked to make sure that the central
     * meridian is correctly set.
     * <p>
     * This method uses the {@linkplain #scaleFactor scale factor} and {@linkplain #falseEasting
     * false easting} to decide if this is a UTM or MTM case.
     *
     * @return The central meridian, in decimal degrees.
     * @throws IllegalStateException if the case of the projection cannot be determined.
     */
    private static double getCentralMeridian(final double centralMeridian, final double scaleFactor, final double falseEasting) throws IllegalStateException {
        // UTM
        if (scaleFactor == 0.9996 && falseEasting == 500000) {
            return computeCentralMedirian(centralMeridian, -177, 6);
        }
        // MTM
        if (scaleFactor == 0.9999 && falseEasting == 304800){
            return computeCentralMedirian(centralMeridian, -52.5, -3);
        }
        // unknown
        throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOWN_PROJECTION_TYPE));
    }

    /**
     * A derived quantity of excentricity, computed by <code>e'² = (a²-b²)/b² = es/(1-es)</code>
     * where <var>a</var> is the semi-major axis length and <var>b</bar> is the semi-minor axis
     * length.
     */
    private final double esp;

    /**
     * Constants used for the forward and inverse transform for the elliptical
     * case of the Transverse Mercator.
     */
    private static final double
            FC1 = 1.00000000000000000000000,  // 1/1
            FC2 = 0.50000000000000000000000,  // 1/2
            FC3 = 0.16666666666666666666666,  // 1/6
            FC4 = 0.08333333333333333333333,  // 1/12
            FC5 = 0.05000000000000000000000,  // 1/20
            FC6 = 0.03333333333333333333333,  // 1/30
            FC7 = 0.02380952380952380952380,  // 1/42
            FC8 = 0.01785714285714285714285;  // 1/56

    /**
     * Creates a Transverse Mercator projection from the given parameters. The descriptor argument
     * is usually {@link org.geotoolkit.referencing.operation.provider.TransverseMercator#PARAMETERS},
     * but is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Transverse Mercator projection.
     *
     * @param  descriptor Typically {@code TransverseMercator.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final TransverseMercator projection;
        final Parameters parameters = Parameters.castOrWrap(values);
        if (isSpherical(parameters)) {
            projection = new Spherical(descriptor, parameters);
        } else {
            projection = new TransverseMercator(descriptor, parameters);
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
    protected TransverseMercator(final OperationMethod method, final Parameters parameters) {
        super(method, parameters);
        esp = excentricitySquared / (1 - excentricitySquared);
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
        final double λ      = srcPts[srcOff];
        final double φ      = srcPts[srcOff + 1];
        final double sinφ   = sin(φ);
        final double cosφ   = cos(φ);
        final double tanφ   = sinφ/cosφ;
        final double sinφ2  = sinφ*sinφ;
        final double cosφ2  = cosφ*cosφ;
        final double λcosφ  = λ * cosφ;
        final double λcosφ2 = λcosφ * λcosφ;
        final double t      = (abs(cosφ) > ANGLE_TOLERANCE) ? tanφ*tanφ : 0;
        final double n      = esp * cosφ2;
        final double sqess  = sqrt(1 - excentricitySquared*sinφ2);
        if (dstPts != null) {
            final double al = λcosφ / sqess;
            dstPts[dstOff] = al*(FC1 + FC3 * λcosφ2*(1 - t + n +
                FC5 * λcosφ2 * ( 5 + t*(t - 18) + n*(14 - 58*t) +
                FC7 * λcosφ2 * (61 + t*(t*(179 - t) - 479)))));

            // NOTE: meridional distance at latitudeOfOrigin is always 0.
            dstPts[dstOff + 1] = mlfn(φ, sinφ, cosφ) + sinφ*al*λ*
                FC2 * (1 +
                FC4 * λcosφ2 * (5 - t + n*(9 + 4*n) +
                FC6 * λcosφ2 * (61 + t * (t - 58) + n*(270 - 330*t) +
                FC8 * λcosφ2 * (1385 + t * (t*(543 - t) - 3111)))));
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double λ2        = λ*λ;
        final double dt_dφ     = (abs(cosφ) > ANGLE_TOLERANCE) ? 2*tanφ*(1 + t) : 0;
        final double t58       = (14 - 58*t);
        final double t11       = ( 9 - 11*t)*30;
        final double λcosφ2_dλ =  2 * λcosφ * cosφ;
        final double λcosφ2_dφ = -2 * λcosφ * sinφ * λ;
        final double λcosφ_dφ  = λ*sinφ * (excentricitySquared - 1) / (1 - excentricitySquared*sinφ2);
        final double dn_dφ     = -2*n*tanφ;
        final double  aX       = (( 179 -   t)*t -  479)*t +   61;
        final double  aY       = (( 543 -   t)*t - 3111)*t + 1385;
        final double daX_dφ    = (( 358 - 3*t)*t -  479)*dt_dφ;
        final double daY_dφ    = ((1086 - 3*t)*t - 3099)*dt_dφ;
        final double  bX       = 5 + (t - 18)*t + cosφ2*(esp*t58 + FC7*λ2*aX);
        final double dbX_dλ    = FC7 * (λcosφ2_dλ *  aX);
        final double dbX_dφ    = FC7 * (λcosφ2_dφ *  aX  + daX_dφ*λcosφ2) + (2*t + 58*n - 18)*dt_dφ + t58*dn_dφ;
        final double dcX_dλ    = FC5 * (λcosφ2_dλ *  bX  + dbX_dλ*λcosφ2);
        final double dcX_dφ    = FC5 * (λcosφ2_dφ *  bX  + dbX_dφ*λcosφ2) - dt_dφ + dn_dφ;
        final double  cX       = FC5 * (λcosφ2    *  bX) - t + n + 1;
        final double ddX_dλ    = FC3 * (λcosφ2_dλ *  cX  + dcX_dλ * λcosφ2);
        final double ddX_dφ    = FC3 * (λcosφ2_dφ *  cX  + dcX_dφ * λcosφ2);
        final double  dX       = FC3 * (λcosφ2    *  cX) + FC1;
        final double  bY       = FC8 * (λcosφ2    *  aY) + (t - 58)*t + t11*n + 61;
        final double dbY_dφ    = FC8 * (λcosφ2_dφ *  aY  + daY_dφ*λcosφ2) + 2*(t - 145*n - 29)*dt_dφ + t11*dn_dφ;
        final double dcY_dλ    = FC6 *  λcosφ2_dλ * (bY  + FC8*aY * λcosφ2);
        final double dcY_dφ    = FC6 * (λcosφ2_dφ *  bY  + dbY_dφ * λcosφ2) + (9 + 8*n)*dn_dφ - dt_dφ;
        final double  dy       = FC6 * (λcosφ2    *  bY) + (9 + 4*n)*n - t + 5;
        final double  dY       = FC4 * (λcosφ2    *  dy) + 1;
        final double ddY_dλ    = FC4 * (λcosφ2_dλ *  dy + dcY_dλ*λcosφ2);
        final double ddY_dφ    = FC4 * (λcosφ2_dφ *  dy + dcY_dφ*λcosφ2);
        return new Matrix2(
                (    cosφ*dX + ddX_dλ*λcosφ) / sqess,
                (λcosφ_dφ*dX + ddX_dφ*λcosφ) / sqess,
                FC2*sinφ*λcosφ * (2*dY + ddY_dλ*λ) / sqess,
                FC2*((λcosφ2 + λ*sinφ*λcosφ_dφ)*dY + λ2*sinφ*cosφ*ddY_dφ)/sqess + dmlfn_dφ(sinφ2, cosφ2));
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
        double x = srcPts[srcOff  ];
        double y = srcPts[srcOff+1];
        final double φ = inv_mlfn(y);
        if (abs(φ) >= PI/2) {
            y = copySign(PI/2, y);
            x = 0;
        } else {
            final double sinφ = sin(φ);
            final double cosφ = cos(φ);
            double t = (abs(cosφ) > ANGLE_TOLERANCE) ? sinφ / cosφ : 0;
            final double n = esp * cosφ*cosφ;
            double con = 1 - excentricitySquared * (sinφ*sinφ);
            final double d = x * sqrt(con);
            con *= t;
            t *= t;
            double ds = d*d;

            y = φ - (con*ds / (1 - excentricitySquared)) *
                FC2 * (1 - ds *
                FC4 * (5 + t*(3 - 9*n) + n*(1 - 4*n) - ds *
                FC6 * (61 + t*(90 - 252*n + 45*t) + 46*n - ds *
                FC8 * (1385 + t*(3633 + t*(4095 + 1574*t))))));

            x = d  * (FC1 - ds * FC3 * (1 + 2*t + n -
                ds *  FC5 * (5 + t*(28 + 24* t + 8*n) + 6*n -
                ds *  FC7 * (61 + t*(662 + t*(1320 + 720*t)))))) / cosφ;
        }
        dstPts[dstOff  ] = x;
        dstPts[dstOff+1] = y;
    }


    /**
     * Provides the transform equations for the spherical case of the Transverse Mercator projection.
     *
     * @author André Gosselin (MPO)
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.00
     *
     * @since 2.1
     * @module
     */
    static final class Spherical extends TransverseMercator {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 8903592710452235162L;

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
            final double b    = cosφ * sinλ;
            /*
             * Using Snyder's equation for calculating y, instead of the one used in Proj4.
             * Potential problems when y and x = 90 degrees, but behaves ok in tests.
             */
            final double y = atan2(tanφ, cosλ); // Snyder 8-3
            final double x = atanh(b);          // Snyder 8-1
            Matrix derivative = null;
            if (derivate) {
                final double sct  = cosλ*cosλ + tanφ*tanφ;
                final double bm = b*b - 1;
                derivative = new Matrix2(
                        -(cosφ * cosλ) / bm,        // ∂x/∂λ
                         (sinφ * sinλ) / bm,        // ∂x/∂φ
                        tanφ * sinλ / sct,          // ∂y/∂λ
                        cosλ / (cosφ*cosφ * sct));  // ∂y/∂φ
            }
            // Following part is common to all spherical projections: verify, store and return.
            assert !(abs(λ) < ASSERTION_DOMAIN) ||
                  (Assertions.checkDerivative(derivative, super.transform(srcPts, srcOff, dstPts, dstOff, derivate))
                && Assertions.checkTransform(dstPts, dstOff, x, y)); // dstPts = result from ellipsoidal formulas.
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
            final double sinhX = sinh(x);
            final double cosD = cos(y);
            // 'copySign' corrects for the fact that we made everything positive using sqrt(...)
            y = copySign(asin(sqrt((1 - cosD*cosD) / (1 + sinhX*sinhX))), y);
            x = atan2(sinhX, cosD);
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
            if (abs(λ) < ASSERTION_DOMAIN) {
                super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
                return Assertions.checkInverseTransform(dstPts, dstOff, λ, φ);
            } else {
                return true;
            }
        }
    }
}
