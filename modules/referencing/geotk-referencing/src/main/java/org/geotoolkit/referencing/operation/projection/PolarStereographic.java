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
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.OperationMethod;
import org.geotoolkit.resources.Errors;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;
import static org.geotoolkit.referencing.operation.provider.PolarStereographic.*;


/**
 * The polar case of the {@linkplain Stereographic} projection (EPSG codes 9810, 9829).
 * This default implementation uses USGS equation (i.e. iteration) for computing
 * the {@linkplain #inverseTransform inverse transform}.
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @author Rémi Maréchal (Geomatys)
 *
 * @see EquatorialStereographic
 * @see ObliqueStereographic
 *
 * @since 2.0
 * @module
 */
public class PolarStereographic extends Stereographic {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = -6635298308431138524L;

    /**
     * Creates a Polar Stereographic projection from the given parameters. The descriptor argument
     * is usually {@link org.geotoolkit.referencing.operation.provider.PolarStereographic#PARAMETERS},
     * but is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Polar Stereographic projection.
     *
     * @param  descriptor Typically {@code Polar Stereographic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = Parameters.castOrWrap(values);
        final PolarStereographic projection = create(descriptor, parameters);
        try {
            return (MathTransform2D) projection.createMapProjection(
                    org.apache.sis.internal.system.DefaultFactories.forBuildin(
                            org.opengis.referencing.operation.MathTransformFactory.class));
        } catch (org.opengis.util.FactoryException e) {
            throw new IllegalArgumentException(e); // TODO
        }
    }

    /**
     * Creates a Polar Stereographic projection from the given parameters.
     *
     * @param parameters The parameters of the projection to be created.
     * @return The map projection.
     */
    static PolarStereographic create(final OperationMethod descriptor, final Parameters parameters) {
        final boolean isSpherical = isSpherical(parameters);
        if (nameMatches(parameters, North.PARAMETERS)) {
            if (isSpherical) {
                return new PolarStereographic.Spherical(descriptor, parameters, false, Boolean.FALSE);
            } else {
                return new PolarStereographic(descriptor, parameters, false, Boolean.FALSE);
            }
        } else if (nameMatches(parameters, South.PARAMETERS)) {
            if (isSpherical) {
                return new PolarStereographic.Spherical(descriptor, parameters, false, Boolean.TRUE);
            } else {
                return new PolarStereographic(descriptor, parameters, false, Boolean.TRUE);
            }
        } else if (nameMatches(parameters, VariantB.PARAMETERS)) {
            if (isSpherical) {
                return new PolarStereographic.Spherical(descriptor, parameters, false, null);
            } else {
                return new PolarStereographic.Series(descriptor, parameters, false, null);
            }
        } if (isSpherical) {
            return new PolarStereographic.Spherical(descriptor, parameters, true, null);
        } else {
            return new PolarStereographic.Series(descriptor, parameters, true, null);
        }
    }

    /**
     * Constructs an oblique stereographic projection (USGS equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected PolarStereographic(final OperationMethod method, final Parameters parameters) {
        this(method, parameters, nameMatches(parameters, PARAMETERS), null);
    }

    /**
     * Gets {@code "standard_parallel_1"} parameter value. This parameter should be mutually
     * exclusive with {@code "latitude_of_origin"}, but this is not a strict requirement for
     * the constructor.
     *
     * {@preformat text
     *   ┌───────────────────────────────────┬────────────────────┬─────────────┐
     *   │ Projection                        │ Parameter          │ Force pole  │
     *   ├───────────────────────────────────┼────────────────────┼─────────────┤
     *   │ Polar Stereographic (variant A)   │ Latitude of origin │ auto detect │
     *   │ Polar Stereographic (variant B)   │ Standard Parallel  │ auto detect │
     *   │ Stereographic North Pole          │ Standard Parallel  │ North pole  │
     *   │ Stereographic South Pole          │ Standard Parallel  │ South pole  │
     *   └───────────────────────────────────┴────────────────────┴─────────────┘
     * }
     *
     * "Standard Parallel" (a.k.a. "Latitude true scale") default to 90°N for
     * every cases (including Polar A, but it is meanless in this case), except
     * for "Stereographic South Pole" where it default to 90°S.
     */
    private double latitudeTrueScale(final Parameters parameters,
                final boolean isVariantA, final Boolean forceSouthPole)
    {
        final ParameterDescriptor<Double> trueScaleDescriptor =
                Boolean.TRUE.equals(forceSouthPole) ? South.STANDARD_PARALLEL : North.STANDARD_PARALLEL;
        final double latitudeTrueScale;
        if (isVariantA) {
            // Polar A case
            latitudeTrueScale = copySign(90, getAndStore(parameters, LATITUDE_OF_ORIGIN));
        } else {
            // Any cases except Polar A
            latitudeTrueScale = getAndStore(parameters, trueScaleDescriptor);
        }
        return toRadians(latitudeTrueScale);
    }

    /**
     * Constructs a polar stereographic projection.
     *
     * @param parameters The parameters of the projection to be created.
     * @param isVariantA {@code true} for Polar Stereographic variant A,
     *        or {@code false} for all other cases.
     * @param forceSouthPole Forces projection to North pole if {@link Boolean#FALSE},
     *        to South pole if {@link Boolean#TRUE}, or do not force (i.e. detect
     *        from other parameter values) if {@code null}.
     */
    private PolarStereographic(final OperationMethod method, final Parameters parameters,
            final boolean isVariantA, final Boolean forceSouthPole)
    {
        /*
         * Sets unconditionally the latitude of origin to 90°N, because the South case will
         * be handle by reverting the sign of y through the (de)normalize affine transforms.
         */
        super(method, parameters, 90);
        double latitudeTrueScale = latitudeTrueScale(parameters, isVariantA, forceSouthPole);
        final boolean southPole;
        if (forceSouthPole != null) {
            southPole = forceSouthPole.booleanValue();
        } else {
            southPole = (latitudeTrueScale < 0);
        }
        latitudeTrueScale = abs(latitudeTrueScale); // May be anything in [0 ... π/2] range.
        final double k0;
        if (abs(latitudeTrueScale - PI/2) >= ANGLE_TOLERANCE) {
            // Derives from (21-32 and 21-33)
            final double t = sin(latitudeTrueScale);
            if (excentricity != 0) {
                k0 = msfn(t, cos(latitudeTrueScale)) / tsfn(latitudeTrueScale, t);
            } else {
                // Simplification of the above equation in the spherical case,
                // derived from (21-7) and (21-11).
                k0 = 1 + t;
            }
        } else {
            if (excentricity != 0) {
                // True scale at pole (part of (21-33))
                k0 = 2 / sqrt(pow(1+excentricity, 1+excentricity) * pow(1-excentricity, 1-excentricity));
            } else {
                // Simplification of the above equation in the spherical case.
                k0 = 2;
            }
        }
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        double yk0 = k0;
        if (southPole) {
            getContextualParameters().getMatrix(true).convertAfter(1, -1, null);
        } else {
            yk0 = -yk0;
        }
        getContextualParameters().getMatrix(false).convertBefore(0,  k0, null);
        getContextualParameters().getMatrix(false).convertBefore(1, yk0, null);
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
        final double ρ    = tsfn(φ, sinφ);
        final double x    = ρ * sinλ;
        final double y    = ρ * cosλ;
        if (dstPts != null) {
            dstPts[dstOff]   = x;
            dstPts[dstOff+1] = y;
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double dρ = ρ * dtsfn_dφ(φ, sinφ, cos(φ));
        return new Matrix2(y, dρ*sinλ,   // ∂x/∂λ , ∂x/∂φ
                          -x, dρ*cosλ);  // ∂y/∂λ , ∂y/∂φ
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
        /*
         * Compute latitude using iterative technique.
         */
        final double halfe = 0.5 * excentricity;
        double φ = 0;
        for (int i=MAXIMUM_ITERATIONS;;) {
            final double esinφ = excentricity * sin(φ);
            final double next = (PI/2) - 2*atan(ρ*pow((1-esinφ)/(1+esinφ), halfe));
            if (abs(φ - (φ=next)) < ITERATION_TOLERANCE) {
                break;
            }
            if (--i < 0) {
                throw new ProjectionException(Errors.format(Errors.Keys.NO_CONVERGENCE));
            }
        }
        dstPts[dstOff  ] = atan2(x, y);
        dstPts[dstOff+1] = φ;
    }




    /**
     * Provides the transform equations for the spherical case of the polar
     * stereographic projection.
     *
     * @author Gerald Evenden (USGS)
     * @author André Gosselin (MPO)
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    static final class Spherical extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 1655096575897215547L;

        /**
         * Constructs a spherical stereographic projection.
         *
         * @param parameters The parameters of the projection to be created.
         * @param isVariantA {@code true} for Polar Stereographic variant A,
         *        or {@code false} for all other cases.
         * @param forceSouthPole For projection to North pole if {@link Boolean#FALSE},
         *        to South pole if {@link Boolean#TRUE}, or do not force (i.e. detect
         *        from other parameter values) if {@code null}.
         */
        Spherical(final OperationMethod method, final Parameters parameters, final boolean isVariantA, final Boolean forceSouthPole) {
            super(method, parameters, isVariantA, forceSouthPole);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Matrix transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final boolean derivate) throws ProjectionException
        {
            final double λ     = srcPts[srcOff];
            final double φ     = srcPts[srcOff + 1];
            final double sinλ  = sin(λ);
            final double cosλ  = cos(λ);
            final double sinφp = sin(φ) + 1;
            final double F     = cos(φ) / sinφp; // == tan (pi/4 - phi/2)
            final double x     = F * sinλ; // (21-5)
            final double y     = F * cosλ; // (21-6)
            Matrix derivative = null;
            if (derivate) {
                derivative = new Matrix2(y, -sinλ / sinφp,   // ∂x/∂λ , ∂x/∂φ
                                        -x, -cosλ / sinφp);  // ∂y/∂λ , ∂y/∂φ
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
            x = atan2(x, y);
            y = PI/2 - abs(2*atan(ρ)); // (20-14) with phi1=90° and cos(y) = sin(π/2 + y).
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

    /**
     * Overrides {@link PolarStereographic} to use the a series for the {@link #inverseTransform
     * inverse transform} method. This is the equation specified by the EPSG. Allows for a
     * {@code "latitude_true_scale"} parameter to be used, but this parameter is not listed
     * by the EPSG and is not given as a parameter by the provider.
     * <p>
     * Compared to the default {@link PolarStereographic} implementation, the series implementation
     * is a little bit faster at the expense of a little bit less accuracy. The default
     * {@link PolarStereographic} implementation is a derivated work of Proj4, and is therefore
     * better tested.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    static final class Series extends PolarStereographic {
        /**
         * For compatibility with different versions during deserialization.
         */
        private static final long serialVersionUID = 2795404156883313290L;

        /**
         * Constants used for the inverse polar series
         */
        private final double a, b;

        /**
         * Constants used for the inverse polar series
         */
        private final double c, d;

        /**
         * Constructs a polar stereographic projection (series inverse equations).
         *
         * @param parameters The parameters of the projection to be created.
         * @param isVariantA {@code true} for Polar Stereographic variant A,
         *        or {@code false} for all other cases.
         * @param forceSouthPole For projection to North pole if {@link Boolean#FALSE},
         *        to South pole if {@link Boolean#TRUE}, or do not force (i.e. detect
         *        from other parameter values) if {@code null}.
         */
        Series(final OperationMethod method, final Parameters parameters, final boolean isVariantA, final Boolean forceSouthPole) {
            super(method, parameters, isVariantA, forceSouthPole);
            // See Snyde P. 19, "Computation of Series"
            final double e4 = excentricitySquared * excentricitySquared;
            final double e6 = e4 * excentricitySquared;
            final double e8 = e4 * e4;
            final double ci = 7/120.0 * e6 + 81/1120.0 * e8;
            final double di = 4279/161280.0 * e8;
            a = excentricitySquared*0.5 + 5/24.0*e4 + e6/12.0 + 13/360.0*e8 - ci;
            b = 2 * (7/48.0*e4 + 29/240.0*e6 + 811/11520.0*e8) - 4*di;
            c = ci * 4;
            d = di * 8;
            /*
             * Proj4 was calculating a k0 constant here. This constant divised by the one calculated
             * by the super-class (this division was required because the k0 calculated by the super-
             * class scales the denormalize affine transform) simplifies to:
             *
             *     k0 = sqrt(pow(1+excentricity, 1+excentricity)*
             *               pow(1-excentricity, 1-excentricity)) / 2
             *
             * This constant was used only as a divisor of χ in inverseTransform(...), but was
             * identical to the expression that multiplies χ just a few instructions further.
             * Consequently, it vanishes completely.
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
            double x = srcPts[srcOff];
            double y = srcPts[srcOff+1];
            final double t = hypot(x, y);
            final double χ = PI/2 - 2*atan(t);
            x = atan2(x, y);

            // See Snyde P. 19, "Computation of Series"
            final double sin2χ = sin(2 * χ);
            final double cos2χ = cos(2 * χ);
            y = χ + sin2χ*(a + cos2χ*(b + cos2χ*(c + d*cos2χ)));
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
                                              final double λ, final double φ)
                throws ProjectionException
        {
            super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkInverseTransform(dstPts, dstOff, λ, φ);
        }
    }
}
