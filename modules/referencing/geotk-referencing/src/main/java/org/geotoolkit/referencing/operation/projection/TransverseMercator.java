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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.resources.Errors;
import org.opengis.parameter.ParameterNotFoundException;
import static org.geotoolkit.referencing.operation.provider.TransverseMercator.*;


/**
 * Transverse Mercator Projection (EPSG codes 9807, 9808). See the
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
 * meridian. Deformation are more important as we are going futher from the central meridian.
 * The Transverse Mercator projection is appropriate for region wich have a greater extent
 * north-south than east-west.
 * <p>
 * The elliptical equations used here are series approximations, and their accuracy decreases as
 * points move farther from the central meridian of the projection. The forward equations here are
 * accurate to less than a millimetre &plusmn;10 degrees from the central meridian, a few
 * millimetres &plusmn;15 degrees from the central meridian and a few centimetres &plusmn;20
 * degrees from the central meridian. The spherical equations are not approximations and should
 * always give the correct values.
 * <p>
 * There are a number of versions of the transverse mercator projection including the Universal
 * (UTM) and Modified (MTM) Transverses Mercator projections. In these cases the earth is divided
 * into zones. For the UTM the zones are 6 degrees wide, numbered from 1 to 60 proceeding east from
 * 180 degrees longitude, and between lats 84 degrees North and 80 degrees South. The central
 * meridian is taken as the center of the zone and the latitude of origin is the equator. A scale
 * factor of 0.9996 and false easting of 500000 metres is used for all zones and a false northing
 * of 10000000 metres is used for zones in the southern hemisphere.
 *
 * {@section References}
 * <ul>
 *   <li> Proj-4.4.6 available at <A HREF="http://www.remotesensing.org/proj">www.remotesensing.org/proj</A><br>
 *        Relevent files are: {@code PJ_tmerc.c}, {@code pj_mlfn.c}, {@code pj_fwd.c} and {@code pj_inv.c}.</li>
 *   <li> John P. Snyder (Map Projections - A Working Manual,<br>
 *        U.S. Geological Survey Professional Paper 1395, 1987).</li>
 *   <li> "Coordinate Conversions and Transformations including Formulas",<br>
 *        EPSG Guidence Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.0
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
     * Parameters of a Transverse Mercator projection. This class contains
     * convenience methods for computing the zone of current projection.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.0
     *
     * @since 3.0
     * @module
     */
    protected static class Parameters extends UnitaryProjection.Parameters {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -1689301305119562861L;

        /**
         * Creates parameters initialized to values extracted from the given parameter group.
         *
         * @param  descriptor The descriptor of parameters that are legal
         *         for the projection being constructed.
         * @param  values The parameter values in standard units.
         * @throws ParameterNotFoundException if a mandatory parameter is missing.
         */
        public Parameters(final ParameterDescriptorGroup descriptor,
                          final ParameterValueGroup values)
                throws ParameterNotFoundException
        {
            super(descriptor, values);
        }

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
        private int getZone(final double centralLongitudeZone1, final double zoneWidth) {
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
        private double getCentralMedirian(final double centralLongitudeZone1, final double zoneWidth) {
            double t;
            t  = centralLongitudeZone1 + (getZone(centralLongitudeZone1, zoneWidth)-1)*zoneWidth;
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
        public int getZone() throws IllegalStateException {
            // UTM
            if (scaleFactor == 0.9996 && falseEasting == 500000) {
                return getZone(-177, 6);
            }
            // MTM
            if (scaleFactor == 0.9999 && falseEasting == 304800){
                return getZone(-52.5, -3);
            }
            // unknown
            throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOW_PROJECTION_TYPE));
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
        public double getCentralMeridian() throws IllegalStateException {
            // UTM
            if (scaleFactor == 0.9996 && falseEasting == 500000) {
                return getCentralMedirian(-177, 6);
            }
            // MTM
            if (scaleFactor == 0.9999 && falseEasting == 304800){
                return getCentralMedirian(-52.5, -3);
            }
            // unknown
            throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOW_PROJECTION_TYPE));
        }
    }

    /**
     * A derived quantity of excentricity, computed by <code>e'² = (a²-b²)/b² = es/(1-es)</code>
     * where <var>a</var> is the semi-major axis length and <var>b</bar> is the semi-minor axis
     * length.
     */
    private final double esp;

    /**
     * Contants used for the forward and inverse transform for the eliptical
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
     * but is not restricted to. If a different descriptor is supplied, it is user's responsability
     * to ensure that it is suitable to a Transverse Mercator projection.
     *
     * @param  descriptor Typically {@code TransverseMercator.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.0
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final TransverseMercator projection;
        final Parameters parameters = new Parameters(descriptor, values);
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new TransverseMercator(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected TransverseMercator(final Parameters parameters) {
        super(parameters);
        esp = excentricitySquared / (1 - excentricitySquared);
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
        final double x = rollLongitude(srcPts[srcOff]);
        final double y = srcPts[srcOff + 1];
        final double sinphi = sin(y);
        final double cosphi = cos(y);
        double t = (abs(cosphi) > ANGLE_TOLERANCE) ? sinphi/cosphi : 0;
        t *= t;
        double al = cosphi*x;
        double als = al*al;
        al /= sqrt(1 - excentricitySquared*(sinphi*sinphi));
        final double n = esp * cosphi*cosphi;

        dstPts[dstOff] = al*(FC1 + FC3 * als*(1 - t + n +
            FC5 * als * ( 5 + t*(t - 18) + n*(14 - 58*t) +
            FC7 * als * (61 + t*(t*(179 - t) - 479)))));

        // NOTE: meridional distance at latitudeOfOrigin is always 0.
        dstPts[dstOff + 1] = mlfn(y, sinphi, cosphi) + sinphi*al*x*
            FC2 * (1 +
            FC4 * als * (5 - t + n*(9 + 4*n) +
            FC6 * als * (61 + t * (t - 58) + n*(270 - 330*t) +
            FC8 * als * (1385 + t * ( t*(543 - t) - 3111)))));
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        double x = srcPts[srcOff];
        double y = srcPts[srcOff + 1];
        final double phi = inv_mlfn(y);
        if (abs(phi) >= PI/2) {
            y = copySign(PI/2, y);
            x = 0;
        } else {
            final double sinphi = sin(phi);
            final double cosphi = cos(phi);
            double t = (abs(cosphi) > ANGLE_TOLERANCE) ? sinphi/cosphi : 0;
            final double n = esp * cosphi*cosphi;
            double con = 1 - excentricitySquared*(sinphi*sinphi);
            final double d = x * sqrt(con);
            con *= t;
            t *= t;
            double ds = d*d;

            y = phi - (con*ds / (1 - excentricitySquared)) *
                FC2 * (1 - ds *
                FC4 * (5 + t*(3 - 9*n) + n*(1 - 4*n) - ds *
                FC6 * (61 + t*(90 - 252*n + 45*t) + 46*n - ds *
                FC8 * (1385 + t*(3633 + t*(4095 + 1574*t))))));

            x = d  * (FC1 - ds * FC3 * (1 + 2*t + n -
                ds *  FC5 * (5 + t*(28 + 24* t + 8*n) + 6*n -
                ds *  FC7 * (61 + t*(662 + t*(1320 + 720*t)))))) / cosphi;
        }
        dstPts[dstOff] = unrollLongitude(x);
        dstPts[dstOff + 1] = y;
    }


    /**
     * Provides the transform equations for the spherical case of the Transverse Mercator projection.
     *
     * @author André Gosselin (MPO)
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.0
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
            double cosphi = cos(y);
            double b = cosphi * sin(x);
            /*
             * Using Snyder's equation for calculating y, instead of the one used in Proj4.
             * Potential problems when y and x = 90 degrees, but behaves ok in tests.
             */
            y = atan2(tan(y), cos(x));     // Snyder 8-3
            x = 0.5 * log((1+b) / (1-b));  // Snyder 8-1
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
            final double lambda = srcPts[srcOff];
            if (abs(lambda) < ASSERTION_DOMAIN) {
                super.transform(srcPts, srcOff, dstPts, dstOff);
                return Assertions.checkTransform(dstPts, dstOff, x, y);
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
            double x = srcPts[srcOff];
            double y = srcPts[srcOff + 1];
            final double sinhX = sinh(x);
            final double cosD = cos(y);
            // 'copySign' corrects for the fact that we made everything positive using sqrt(...)
            y = copySign(asin(sqrt((1 - cosD*cosD) / (1 + sinhX*sinhX))), y);
            x = unrollLongitude(atan2(sinhX, cosD));
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
            if (abs(lambda) < ASSERTION_DOMAIN) {
                super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
                return Assertions.checkInverseTransform(dstPts, dstOff, lambda, phi);
            } else {
                return true;
            }
        }
    }

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse.
     * We expect negligible error when in the domain of validity of this projection,
     * and we disable the test when outside.
     */
    @Override
    double getErrorEstimate(final double lambda, final double phi) {
        return (abs(lambda) < ASSERTION_DOMAIN && abs(phi) < PI/4) ? 0 : Double.NaN;
    }
}
