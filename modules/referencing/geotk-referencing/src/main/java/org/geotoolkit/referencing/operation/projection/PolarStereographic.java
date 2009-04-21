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
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.resources.Errors;
import static org.geotoolkit.referencing.operation.provider.PolarStereographic.*;


/**
 * The polar case of the {@linkplain Stereographic stereographic} projection.
 * This default implementation uses USGS equation (i.e. iteration) for computing
 * the {@linkplain #inverseTransform inverse transform}.
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.0
 *
 * @see EquatorialStereographic
 * @see ObliqueStereographic
 *
 * @since 2.4
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
     * but is not restricted to. If a different descriptor is supplied, it is user's responsability
     * to ensure that it is suitable to a Polar Stereographic projection.
     *
     * @param  descriptor Typically {@code Polar Stereographic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.0
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final PolarStereographic projection = create(parameters);
        return projection.createConcatenatedTransform();
    }

    /**
     * Creates a Polar Stereographic projection from the given parameters.
     *
     * @param parameters The parameters of the projection to be created.
     * @return The map projection.
     */
    static PolarStereographic create(final Parameters parameters) {
        final boolean isSpherical = parameters.isSpherical();
        if (parameters.nameMatches(North.PARAMETERS)) {
            if (isSpherical) {
                return new PolarStereographic.Spherical(parameters, false, Boolean.FALSE);
            } else {
                return new PolarStereographic(parameters, false, Boolean.FALSE);
            }
        } else if (parameters.nameMatches(South.PARAMETERS)) {
            if (isSpherical) {
                return new PolarStereographic.Spherical(parameters, false, Boolean.TRUE);
            } else {
                return new PolarStereographic(parameters, false, Boolean.TRUE);
            }
        } else if (parameters.nameMatches(VariantB.PARAMETERS)) {
            if (isSpherical) {
                return new PolarStereographic.Spherical(parameters, false, null);
            } else {
                return new PolarStereographic.Series(parameters, false, null);
            }
        } if (isSpherical) {
            return new PolarStereographic.Spherical(parameters, true, null);
        } else {
            return new PolarStereographic.Series(parameters, true, null);
        }
    }

    /**
     * Constructs an oblique stereographic projection (USGS equations).
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected PolarStereographic(final Parameters parameters) {
        this(parameters, parameters.nameMatches(PARAMETERS), null);
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
    private static double latitudeTrueScale(final Parameters parameters,
                final boolean isVariantA, final Boolean forceSouthPole)
    {
        final ParameterDescriptor<Double> trueScaleDescriptor =
                Boolean.TRUE.equals(forceSouthPole) ? South.STANDARD_PARALLEL : North.STANDARD_PARALLEL;
        final double latitudeTrueScale;
        if (isVariantA) {
            // Polar A case
            latitudeTrueScale = copySign(90, parameters.latitudeOfOrigin);
        } else {
            // Any cases except Polar A
            latitudeTrueScale = (parameters.standardParallels.length != 0) ?
                    parameters.standardParallels[0] : trueScaleDescriptor.getDefaultValue();
        }
        Parameters.ensureLatitudeInRange(trueScaleDescriptor, latitudeTrueScale, true);
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
    private PolarStereographic(final Parameters parameters,
            final boolean isVariantA, final Boolean forceSouthPole)
    {
        /*
         * Sets inconditionnaly the latitude of origin to 90°N, because the South case will
         * be handle by reverting the sign of y through the (de)normalize affine transforms.
         */
        super(parameters, 90);
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
            parameters.normalize(true).scale(1, -1);
        } else {
            yk0 = -yk0;
        }
        parameters.validate();
        parameters.normalize(false).scale(k0, yk0);
        finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double x = rollLongitude(srcPts[srcOff]);
        final double y = srcPts[srcOff + 1];
        final double rho = tsfn(y, sin(y));
        dstPts[dstOff  ] = rho * sin(x);
        dstPts[dstOff+1] = rho * cos(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        final double rho = hypot(x, y);
        /*
         * Compute latitude using iterative technique.
         */
        final double halfe = 0.5 * excentricity;
        double phi = 0;
        for (int i=MAXIMUM_ITERATIONS;;) {
            final double esinphi = excentricity * sin(phi);
            final double next = (PI/2) - 2*atan(rho*pow((1-esinphi)/(1+esinphi), halfe));
            if (abs(phi - (phi=next)) < ITERATION_TOLERANCE) {
                break;
            }
            if (--i < 0) {
                throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
            }
        }
        dstPts[dstOff  ] = atan2(x, y);
        dstPts[dstOff+1] = phi;
    }




    /**
     * Provides the transform equations for the spherical case of the polar
     * stereographic projection.
     *
     * @author Gerald Evenden (USGS)
     * @author André Gosselin (MPO)
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.0
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
        Spherical(final Parameters parameters, final boolean isVariantA, final Boolean forceSouthPole) {
            super(parameters, isVariantA, forceSouthPole);
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
            final double f = cos(y) / (1+sin(y)); // == tan (pi/4 - phi/2)
            y = f * cos(x); // (21-6)
            x = f * sin(x); // (21-5)
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
            x = atan2(x, y);
            y = PI/2 - abs(2*atan(rho)); // (20-14) with phi1=90° and cos(y) = sin(π/2 + y).
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
     * Overides {@link PolarStereographic} to use the a series for the {@link #inverseTransform
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
     * @version 3.0
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
        private final double A, B;

        /**
         * Constants used for the inverse polar series
         */
        private double C, D;

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
        Series(final Parameters parameters, final boolean isVariantA, final Boolean forceSouthPole) {
            super(parameters, isVariantA, forceSouthPole);
            // See Snyde P. 19, "Computation of Series"
            final double e4 = excentricitySquared * excentricitySquared;
            final double e6 = e4 * excentricitySquared;
            final double e8 = e4 * e4;
            C = 7/120.0 * e6 + 81/1120.0 * e8;
            D = 4279/161280.0 * e8;
            A = excentricitySquared*0.5 + 5/24.0*e4 + e6/12.0 + 13/360.0*e8 - C;
            B = 2 * (7/48.0*e4 + 29/240.0*e6 + 811/11520.0*e8) - 4*D;
            C *= 4;
            D *= 8;
            /*
             * Proj4 was calculating a k0 constant here. This constant divised by the one calculated
             * by the super-class (this division was required because the k0 calculated by the super-
             * class scales the denormalize affine transform) simplifies to:
             *
             *     k0 = sqrt(pow(1+excentricity, 1+excentricity)*
             *               pow(1-excentricity, 1-excentricity)) / 2
             *
             * This constant was used only as a divisor of chi in inverseTransform(...), but was
             * identical to the expression that multiplies chi just a few instructions further.
             * Concequently, it vanishes completly.
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
            final double t = hypot(x, y);
            final double chi = PI/2 - 2*atan(t);
            x = atan2(x, y);

            // See Snyde P. 19, "Computation of Series"
            final double sin2chi = sin(2 * chi);
            final double cos2chi = cos(2 * chi);
            y = chi + sin2chi*(A + cos2chi*(B + cos2chi*(C + D*cos2chi)));
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
