/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.resources.Errors;
import static org.geotoolkit.referencing.operation.provider.ObliqueStereographic.PARAMETERS;


/**
 * Stereographic Projection. See the
 * <A HREF="http://mathworld.wolfram.com/StereographicProjection.html">Stereographic projection on
 * MathWorld</A> for an overview. See any of the following providers for a list of programmatic
 * parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Stereographic}</li>
 * </ul>
 *
 * {@section Description}
 *
 * The directions starting from the central point are true, but the areas and the lengths become
 * increasingly deformed as one moves away from the center. This projection is used to represent
 * polar areas. It can be adapted for other areas having a circular form.
 * <p>
 * This implementation, and its subclasses, provides transforms for six cases of the
 * stereographic projection:
 * <p>
 * <ul>
 *   <li>{@code "Oblique_Stereographic"} (EPSG code 9809), alias {@code "Double_Stereographic"}
 *       in ESRI software</li>
 *   <li>{@code "Stereographic"} in ESRI software (<strong>NOT</strong> EPSG code 9809)</li>
 *   <li>{@code "Polar_Stereographic"} (EPSG code 9810, uses a series calculation for the
 *       inverse)</li>
 *   <li>{@code "Polar_Stereographic (variant B)"} (EPSG code 9829, uses a series calculation
 *       for the inverse)</li>
 *   <li>{@code "Stereographic_North_Pole"} in ESRI software (uses iteration for the inverse)</li>
 *   <li>{@code "Stereographic_South_Pole"} in ESRI software (uses iteration for the inverse)</li>
 * </ul>
 * <p>
 * Both the {@code "Oblique_Stereographic"} and {@code "Stereographic"} projections are "double"
 * projections involving two parts: 1) a conformal transformation of the geographic coordinates
 * to a sphere and 2) a spherical Stereographic projection. The EPSG considers both methods to
 * be valid, but considers them to be a different coordinate operation methods.
 * <p>
 * The {@code "Stereographic"} case uses the USGS equations of Snyder. This employs a simplified
 * conversion to the conformal sphere that computes the conformal latitude of each point on the
 * sphere.
 * <p>
 * The {@code "Oblique_Stereographic"} case uses equations from the EPSG. This uses a more
 * generalized form of the conversion to the conformal sphere; using only a single conformal
 * sphere at the origin point. Since this is a "double" projection, it is sometimes called
 * the "Double Stereographic". The {@code "Oblique_Stereographic"} is used in New Brunswick
 * (Canada) and the Netherlands.
 * <p>
 * The {@code "Stereographic"} and {@code "Double_Stereographic"} names are used in ESRI's
 * ArcGIS 8.x product. The {@code "Oblique_Stereographic"} name is the EPSG name for the
 * later only.
 *
 * {@note Tests points calculated with ArcGIS's <code>"Double_Stereographic"</code> are not always
 *        equal to points calculated with the <code>"Oblique_Stereographic"</code>. However, where
 *        there are differences, two different implementations of these equations (EPSG guidence
 *        note 7 and <code>libproj</code>) calculate the same values as we do. Until these
 *        differences are resolved, please be careful when using this projection.}
 *
 * If a {@link org.geotoolkit.referencing.operation.provider.Stereographic#LATITUDE_OF_ORIGIN
 * "latitude_of_origin"} parameter is supplied and is not consistent with the projection
 * classification (for example a latitude different from &plusmn;90&deg; for the polar case),
 * then the oblique or polar case will be automatically inferred from the latitude. In other
 * words, the latitude of origin has precedence on the projection classification. If ommited,
 * then the default value is 90&deg;N for {@code "Polar_Stereographic"} and 0&deg; for
 * {@code "Oblique_Stereographic"}.
 * <p>
 * Polar projections that use the series equations for the inverse calculation will be little bit
 * faster, but may be a little bit less accurate. If a polar {@code "latitude_of_origin"} is used
 * for the {@code "Oblique_Stereographic"} or {@code "Stereographic"}, the iterative equations will
 * be used for inverse polar calculations.
 * <p>
 * The {@code "Polar Stereographic (variant B)"}, {@code "Stereographic_North_Pole"},
 * and {@code "Stereographic_South_Pole"} cases include a
 * {@link org.geotoolkit.referencing.operation.provider.PolarStereographic.VariantB#STANDARD_PARALLEL
 * "standard_parallel_1"} parameter. This parameter sets the latitude with a scale factor equal to
 * the supplied scale factor. The {@code "Polar Stereographic (variant A)"} forces its
 * {@code "latitude_of_origin"} parameter to &plusmn;90&deg;, depending on the hemisphere.
 *
 * {@section References}
 * <ul>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidence Note Number 7, Version 19.</li>
 *   <li>Gerald Evenden. <A HREF="http://members.bellatlantic.net/~vze2hc4d/proj4/sterea.pdf">
 *       "Supplementary PROJ.4 Notes - Oblique Stereographic Alternative"</A></li>
 *   <li>Krakiwsky, E.J., D.B. Thomson, and R.R. Steeves. 1977.<br>
 *       A Manual for Geodetic Coordinate Transformations in the Maritimes.<br>
 *       Geodesy and Geomatics Engineering, UNB. Technical Report No. 48.</li>
 *   <li>Thomson, D.B., M.P. Mepham and R.R. Steeves. 1977.<br>
 *       The Stereographic Double Projection.<br>
 *       Geodesy and Geomatics Engineereng, UNB. Technical Report No. 46.</li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.00
 *
 * @see <A HREF="http://www.remotesensing.org/geotiff/proj_list/random_issues.html#stereographic">Some Random Stereographic Issues</A>
 *
 * @since 1.0
 * @module
 */
public class Stereographic extends UnitaryProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 948619442800459871L;

    /**
     * Maximum difference allowed when comparing real numbers.
     */
    static final double EPSILON = 1E-6;

    /**
     * The latitude of origin, in radians.
     */
    final double phi0;

    /**
     * Constants used for the oblique projections. All those constants are completly
     * determined by {@link #phi0}. Consequently, there is no need to test them in
     * {@link #hashCode} or {@link #equivalent} methods.
     */
    final double sinphi0, cosphi0;

    /**
     * Constants computed from the latitude of origin and the excentricity.
     * It is equal to {@link #phi0} in the spherical and equatorial case.
     */
    private final double chi1;

    /**
     * Constants used for the oblique projections. All those constants are completly
     * determined by {@link #phi0} and {@link #excentricity}. Consequently, there is
     * no need to test them in {@link #hashCode} or {@link #equivalent} methods.
     */
    private final double sinchi1, coschi1;

    /**
     * Creates a Stereographic projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.Stereographic#PARAMETERS}, but
     * is not restricted to. If a different descriptor is supplied, it is user's responsability
     * to ensure that it is suitable to a Stereographic projection.
     *
     * @param  descriptor Typically {@code Stereographic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final double latitudeOfOrigin = toRadians(parameters.latitudeOfOrigin);
        final Stereographic projection;
        if (abs(latitudeOfOrigin - PI/2) < ANGLE_TOLERANCE) {
            projection = PolarStereographic.create(parameters);
        } else {
            final boolean isSpherical = parameters.isSpherical();
            final boolean isEPSG = parameters.nameMatches(PARAMETERS);
            if (abs(latitudeOfOrigin) < ANGLE_TOLERANCE) {
                if (isSpherical) {
                    projection = new EquatorialStereographic.Spherical(parameters);
                } else if (!isEPSG) {
                    projection = new EquatorialStereographic(parameters);
                } else {
                    projection = new ObliqueStereographic(parameters);
                }
            } else if (isSpherical) {
                projection = new Stereographic.Spherical(parameters);
            } else if (!isEPSG) {
                projection = new Stereographic(parameters);
            } else {
                projection = new ObliqueStereographic(parameters);
            }
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs an oblique stereographic projection (USGS equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Stereographic(final Parameters parameters) {
        this(parameters, parameters.latitudeOfOrigin);
        double k0 = 2*msfn(sinphi0, cosphi0) / coschi1;   // part of (14 - 15)
        assert (abs(k0-2) < EPSILON) == (isSpherical() || phi0 == 0) : k0;
        if (excentricity == 0) {
            k0 = 2; // For fixing rounding errors.
        }
        /*
         * k0 above should be equal to 2 in both the spherical and equatorial cases
         * (but the simplification happen through different paths).
         *
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        parameters.validate();
        parameters.normalize(false).scale(k0, k0);
        finish();
    }

    /**
     * Constructs an oblique stereographic projection. Callers must invoke {@link #finish}
     * when they have finished their work.
     *
     * @param parameters The parameters of the projection to be created.
     * @param latitudeOfOrigin the latitude of origin, in decimal degrees.
     */
    Stereographic(final Parameters parameters, final double latitudeOfOrigin) {
        super(parameters);
        double phi0 = toRadians(latitudeOfOrigin);
        if (abs(phi0) < ANGLE_TOLERANCE) { // Equatorial
            phi0    = 0;
            cosphi0 = 1;
            sinphi0 = 0;
            chi1    = 0;
            coschi1 = 1;
            sinchi1 = 0;
        } else {  // Oblique
            cosphi0 = cos(phi0);
            sinphi0 = sin(phi0);
            chi1    = 2 * atan(ssfn(phi0, sinphi0)) - PI/2;
            coschi1 = cos(chi1);
            sinchi1 = sin(chi1);
        }
        this.phi0 = phi0;
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
        final double x   = rollLongitude(srcPts[srcOff]);
        final double y   = srcPts[srcOff + 1];
        final double chi = 2*atan(ssfn(y, sin(y))) - PI/2;
        final double sinChi = sin(chi);
        final double cosChi = cos(chi);
        final double cosChi_cosLon = cosChi * cos(x);
        final double A = 1 + sinchi1*sinChi + coschi1*cosChi_cosLon;
        dstPts[dstOff  ] = (cosChi * sin(x)) / A;
        dstPts[dstOff+1] = (coschi1 * sinChi - sinchi1 * cosChi_cosLon) / A;
        /*
         * The multiplication by (k0 / coschi1) is performed by the "denormalize" affine transform.
         */
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        /*
         * (x,y) is multiplied by (coschi1 / k0),  so rho below is multiplied by the same factor
         * compared to Proj4 code. This allow a few simplifications in the formulas. For example
         * in the computation of ce: atan2(rho*coschi1, k0)
         * simplifies to:            atan(rho).
         */
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        final double rho   = hypot(x, y);
        final double ce    = 2 * atan(rho);
        final double cosce = cos(ce);
        final double since = sin(ce);
        final double chi   = (rho < EPSILON) ? chi1 : asin(cosce*sinchi1 + (y*since*coschi1 / rho));
        final double tp    = tan(PI/4 + 0.5*chi);

        // parts of (21-36) used to calculate longitude
        final double t  = x*since;
        final double ct = rho*coschi1*cosce - y*sinchi1*since;

        // Compute latitude using iterative technique (3-4)
        final double halfe = 0.5*excentricity;
        double phi = chi;
        for (int i=MAXIMUM_ITERATIONS;;) {
            final double esinphi = excentricity * sin(phi);
            final double next = 2*atan(tp*pow((1+esinphi)/(1-esinphi), halfe)) - PI/2;
            if (abs(phi - (phi=next)) < ITERATION_TOLERANCE) {
                break;
            }
            if (--i < 0) {
                throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
            }
        }
        dstPts[dstOff  ] = unrollLongitude(atan2(t, ct));
        dstPts[dstOff+1] = phi;
    }




    /**
     * Provides the transform equations for the spherical case of the Stereographic projection.
     *
     * @author Gerald Evenden (USGS)
     * @author André Gosselin (MPO)
     * @author Martin Desruisseaux (MPO, IRD)
     * @author Rueben Schulz (UBC)
     * @version 3.00
     *
     * @since 2.1
     * @module
     */
    static final class Spherical extends Stereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = -8558594307755820783L;

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
            final double sinlat = sin(y);
            final double coslon = cos(x);
            final double f = 1 + sinphi0*sinlat + cosphi0*coslat*coslon; // (21-4)
            x = coslat * sin(x) / f;                                     // (21-2)
            y = (cosphi0 * sinlat - sinphi0 * coslat * coslon) / f;      // (21-3)

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
            if (abs(rho) < EPSILON) {
                y = phi0;
                x = 0.0;
            } else {
                final double c    = 2 * atan(rho);
                final double cosc = cos(c);
                final double sinc = sin(c);
                final double ct   = rho*cosphi0*cosc - y*sinphi0*sinc; // (20-15)
                final double t    = x*sinc;                            // (20-15)
                y = asin(cosc*sinphi0 + y*sinc*cosphi0/rho);           // (20-14)
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

    /**
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equivalent(final MathTransform object, final boolean strict) {
        if (super.equivalent(object, strict)) {
            final Stereographic that = (Stereographic) object;
            return equals(this.phi0, that.phi0, strict);
            // All other fields are derived from the latitude of origin.
        }
        return false;
    }
}
