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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.OperationMethod;
import org.geotoolkit.resources.Errors;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.referencing.operation.provider.ObliqueStereographic.PARAMETERS;


/**
 * <cite>Stereographic</cite> projection. See the
 * <A HREF="http://mathworld.wolfram.com/StereographicProjection.html">Stereographic projection on
 * MathWorld</A> for an overview. See any of the following providers for a list of programmatic
 * parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Stereographic}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.PolarStereographic}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.PolarStereographic.North}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.PolarStereographic.South}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.PolarStereographic.VariantB}</li>
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
 * words, the latitude of origin has precedence on the projection classification. If omitted,
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
 *       EPSG Guidance Note Number 7, Version 19.</li>
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
 * @author Rémi Maréchal (Geomatys)
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
    private static final long serialVersionUID = 948619442800459872L;

    /**
     * Maximum difference allowed when comparing real numbers.
     */
    static final double EPSILON = 1E-6;

    /**
     * The latitude of origin, in radians.
     */
    final double φ0;

    /**
     * Constants used for the oblique projections. All those constants are completely
     * determined by {@link #φ0}. Consequently, there is no need to test them in
     * {@link #hashCode} or {@link #equals(Object, ComparisonMode)} methods.
     */
    final double sinφ0, cosφ0;

    /**
     * Constants computed from the latitude of origin and the excentricity.
     * It is equal to {@link #φ0} in the spherical and equatorial case.
     */
    private final double χ1;

    /**
     * Constants used for the oblique projections. All those constants are completely determined
     * by {@link #φ0} and {@link #excentricity}. Consequently, there is no need to test them in
     * {@link #hashCode} or {@link #equals(Object, ComparisonMode)} methods.
     */
    private final double sinχ1, cosχ1;

    /**
     * Creates a Stereographic projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.Stereographic#PARAMETERS}, but
     * is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Stereographic projection.
     *
     * @param  descriptor Typically {@code Stereographic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = Parameters.castOrWrap(values);
        final double latitudeOfOrigin = toRadians(parameters.doubleValue(org.geotoolkit.referencing.operation.provider.Stereographic.LATITUDE_OF_ORIGIN));
        final org.apache.sis.referencing.operation.projection.NormalizedProjection projection;
        if (abs(latitudeOfOrigin - PI/2) < ANGLE_TOLERANCE) {
            projection = new org.apache.sis.referencing.operation.projection.PolarStereographic(
                    new org.apache.sis.internal.referencing.provider.PolarStereographicA(), parameters);
            //projection = PolarStereographic.create(descriptor, parameters);
        } else {
            final boolean isSpherical = isSpherical(parameters);
            final boolean isEPSG = nameMatches(parameters, PARAMETERS);
            if (abs(latitudeOfOrigin) < ANGLE_TOLERANCE) {
                if (isSpherical) {
                    projection = new EquatorialStereographic.Spherical(descriptor, parameters);
                } else if (!isEPSG) {
                    projection = new EquatorialStereographic(descriptor, parameters);
                } else {
                    projection = new ObliqueStereographic(descriptor, parameters);
                }
            } else if (isSpherical) {
                projection = new Stereographic.Spherical(descriptor, parameters);
            } else if (!isEPSG) {
                projection = new Stereographic(descriptor, parameters);
            } else {
                projection = new ObliqueStereographic(descriptor, parameters);
            }
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
     * Constructs an oblique stereographic projection (USGS equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Stereographic(final OperationMethod method, final Parameters parameters) {
        this(method, parameters, Double.NaN);
        double k0 = 2*msfn(sinφ0, cosφ0) / cosχ1;   // part of (14 - 15)
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
        getContextualParameters().getMatrix(false).convertBefore(0, k0, null);
        getContextualParameters().getMatrix(false).convertBefore(1, k0, null);
    }

    /**
     * Constructs an oblique stereographic projection.
     *
     * @param parameters The parameters of the projection to be created.
     * @param latitudeOfOrigin the latitude of origin, in decimal degrees.
     */
    Stereographic(final OperationMethod method, final Parameters parameters, double latitudeOfOrigin) {
        super(method, parameters, null);
        if (Double.isNaN(latitudeOfOrigin)) {
            latitudeOfOrigin = getAndStore(parameters, org.geotoolkit.referencing.operation.provider.Stereographic.LATITUDE_OF_ORIGIN);
        }
        double phi0 = toRadians(latitudeOfOrigin);
        if (abs(phi0) < ANGLE_TOLERANCE) { // Equatorial
            phi0    = 0;
            cosφ0 = 1;
            sinφ0 = 0;
            χ1    = 0;
            cosχ1 = 1;
            sinχ1 = 0;
        } else {  // Oblique
            cosφ0 = cos(phi0);
            sinφ0 = sin(phi0);
            χ1    = 2 * atan(ssfn(phi0, sinφ0)) - PI/2;
            cosχ1 = cos(χ1);
            sinχ1 = sin(χ1);
        }
        this.φ0 = phi0;
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
        final double λ    = srcPts[srcOff];
        final double φ    = srcPts[srcOff + 1];
        final double sinφ = sin(φ);
        final double sinλ = sin(λ);
        final double cosλ = cos(λ);
        final double ssfn = ssfn(φ, sinφ);
        if (dstPts != null) {
            final double χ    = 2*atan(ssfn) - PI/2;
            final double sinχ = sin(χ);
            final double cosχ = cos(χ);
            final double cosχ_cosλ = cosχ * cosλ;
            final double A = 1 + sinχ1*sinχ + cosχ1*cosχ_cosλ;
            dstPts[dstOff  ] = (cosχ * sinλ) / A;
            dstPts[dstOff+1] = (cosχ1 * sinχ - sinχ1 * cosχ_cosλ) / A;
        }
        /*
         * The multiplication by (k0 / cosχ1) is performed by the "denormalize" affine transform.
         */
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double cosφ = cos(φ);
        final double sd   = ssfn - 1/ssfn;
        final double s2p  = 1 + ssfn*ssfn;
        final double dχφ  = 2*dssfn_dφ(φ, sinφ, cosφ) * ssfn;
        final double A    = s2p/ssfn + sd*sinχ1 + 2*cosλ*cosχ1;
        final double dAλ  = -2*cosχ1*sinλ / A; // The "/A" is actually not part of the derivative.
        final double dAφ  = (2*sinχ1 - sd*cosλ*cosχ1) / (s2p*A); // Same as above comment.
        final double d    = (cosχ1*sd - 2*cosλ*sinχ1);
        return new Matrix2(
                2*(cosλ - sinλ*dAλ) / A,
                -sinλ*dχφ * (sd/s2p + 2*dAφ) / A,
                (2*sinλ*sinχ1 - dAλ*d) / A,
                dχφ * ((2*cosχ1 + sinχ1*cosλ*sd)/s2p - dAφ*d) / A);
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
        /*
         * (x,y) is multiplied by (cosχ1 / k0), so ρ below is multiplied by the same factor
         * compared to Proj4 code. This allow a few simplifications in the formulas. For example
         * in the computation of ce: atan2(ρ*cosχ1, k0)
         * simplifies to:            atan(ρ).
         */
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        final double ρ = hypot(x, y);
        final double ce    = 2 * atan(ρ);
        final double cosce = cos(ce);
        final double since = sin(ce);
        final double χ   = (ρ < EPSILON) ? χ1 : asin(cosce*sinχ1 + (y*since*cosχ1 / ρ));
        final double tp    = tan(PI/4 + 0.5*χ);

        // parts of (21-36) used to calculate longitude
        final double t  = x*since;
        final double ct = ρ*cosχ1*cosce - y*sinχ1*since;

        // Compute latitude using iterative technique (3-4)
        final double halfe = 0.5*excentricity;
        double φ = χ;
        for (int i=MAXIMUM_ITERATIONS;;) {
            final double esinφ = excentricity * sin(φ);
            final double next = 2*atan(tp*pow((1+esinφ)/(1-esinφ), halfe)) - PI/2;
            if (abs(φ - (φ=next)) < ITERATION_TOLERANCE) {
                break;
            }
            if (--i < 0) {
                throw new ProjectionException(Errors.format(Errors.Keys.NoConvergence));
            }
        }
        dstPts[dstOff  ] = atan2(t, ct);
        dstPts[dstOff+1] = φ;
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
            final double λ = srcPts[srcOff];
            final double φ = srcPts[srcOff + 1];
            final double sinφ = sin(φ);
            final double cosφ = cos(φ);
            final double sinλ = sin(λ);
            final double cosλ = cos(λ);
            final double c0cφ = cosφ0*cosφ;
            final double s0sφ = sinφ0*sinφ;
            final double F = 1 + c0cφ*cosλ + s0sφ;                      // (21-4)
            final double x = cosφ * sinλ / F;                           // (21-2)
            final double y = (cosφ0 * sinφ - sinφ0 * cosφ * cosλ) / F;  // (21-3)
            Matrix derivative = null;
            if (derivate) {
                final double c0sφ = cosφ0*sinφ;
                final double s0cφ = sinφ0*cosφ;
                final double dFdλ = (c0cφ*sinλ)        / F; // Actually (∂F/∂λ)/-F
                final double dFdφ = (c0sφ*cosλ - s0cφ) / F; // Actually (∂F/∂φ)/-F
                final double dcsφ =  c0sφ - s0cφ*cosλ;
                derivative = new Matrix2(
                        cosφ*(dFdλ*sinλ + cosλ) / F,
                        sinλ*(dFdφ*cosφ - sinφ) / F,
                             (dFdλ*dcsφ + (s0cφ*sinλ)) / F,
                             (dFdφ*dcsφ + (s0sφ*cosλ + c0cφ)) / F);
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
            final double x = srcPts[srcOff  ];
            final double y = srcPts[srcOff+1];
            final double ρ = hypot(x, y);
            double λ, φ;
            if (abs(ρ) < EPSILON) {
                φ = φ0;
                λ = 0.0;
            } else {
                final double c    = 2 * atan(ρ);
                final double cosc = cos(c);
                final double sinc = sin(c);
                final double ct   = ρ*cosφ0*cosc - y*sinφ0*sinc; // (20-15)
                final double t    = x*sinc;                      // (20-15)
                φ = asin(cosc*sinφ0 + y*sinc*cosφ0/ρ);           // (20-14)
                λ = atan2(t, ct);
            }
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, λ, φ);
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
            super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkInverseTransform(dstPts, dstOff, λ, φ);
        }
    }

    /**
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            final Stereographic that = (Stereographic) object;
            return epsilonEqual(this.φ0, that.φ0, mode);
            // All other fields are derived from the latitude of origin.
        }
        return false;
    }
}
