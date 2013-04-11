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

import java.awt.geom.AffineTransform;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;

import org.apache.sis.measure.Latitude;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.referencing.operation.provider.LambertConformal1SP;
import org.geotoolkit.referencing.operation.provider.LambertConformal2SP;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters.ensureLatitudeInRange;


/**
 * <cite>Lambert Conical Conformal</cite> projection (EPSG codes 9801, 9802, 9803). See the
 * <A HREF="http://mathworld.wolfram.com/LambertConformalConicProjection.html">Lambert conformal
 * conic projection on MathWorld</A> for an overview. See any of the following providers for a
 * list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal1SP}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal2SP}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal2SP.Belgium}</li>
 * </ul>
 *
 * {@section Description}
 *
 * Areas and shapes are deformed as one moves away from standard parallels. The angles are true
 * in a limited area. This projection is used for the charts of North America and some European
 * countries.
 * <p>
 * This implementation provides transforms for four cases of the lambert conic
 * conformal projection:
 * <p>
 * <ul>
 *   <li>{@code Lambert_Conformal_Conic_1SP} (EPSG code 9801)</li>
 *   <li>{@code Lambert_Conformal_Conic_2SP} (EPSG code 9802)</li>
 *   <li>{@code Lambert_Conic_Conformal_2SP_Belgium} (EPSG code 9803)</li>
 *   <li>{@code Lambert_Conformal_Conic} - An alias for the ESRI 2SP case
 *       that includes a {@code "scale_factor"} parameter</li>
 * </ul>
 * <p>
 * For the 1SP case the latitude of origin is used as the standard parallel (SP). To use 1SP with
 * a latitude of origin different from the SP, use the 2SP and set the SP1 to the single SP. The
 * {@code standard_parallel_2"} parameter is optional and will be given the same value as
 * {@code "standard_parallel_1"} if not set (creating a 1 standard parallel projection).
 *
 * {@section References}
 * <ul>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author André Gosselin (MPO)
 * @author Rueben Schulz (UBC)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.20
 *
 * @since 1.0
 * @module
 */
@Immutable
public class LambertConformal extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2067358524298002016L;

    /**
     * Constant for the Belgium 2SP case. This is 29.2985 seconds, given here in radians.
     */
    private static final double BELGE_A = 0.00014204313635987700;

    /**
     * Internal coefficients for computation, depending only on values of standards parallels.
     */
    final double n;

    /**
     * Creates a Lambert projection from the given parameters. The descriptor argument is
     * usually one of the {@code PARAMETERS} constants defined in {@link LambertConformal1SP}
     * or {@link LambertConformal2SP}, but is not restricted to. If a different descriptor is
     * supplied, it is user's responsibility to ensure that it is suitable to a Lambert Conformal
     * projection.
     *
     * @param  descriptor Typically one of {@link LambertConformal1SP#PARAMETERS} or
     *         {@link LambertConformal2SP#PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final LambertConformal projection;
        final Parameters parameters = new Parameters(descriptor, values);
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new LambertConformal(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected LambertConformal(final Parameters parameters) {
        super(parameters);
        double φ1, φ2;
        double latitudeOfOrigin = parameters.latitudeOfOrigin;
        final boolean belgium = parameters.nameMatches(LambertConformal2SP.Belgium.PARAMETERS);
        if (parameters.nameMatches(LambertConformal1SP.PARAMETERS)) {
            // EPSG says the 1SP case uses the latitude of origin as the SP.
            φ1 = φ2 = latitudeOfOrigin;
            ensureLatitudeInRange(LambertConformal1SP.LATITUDE_OF_ORIGIN, φ1, true);
        } else {
            switch (parameters.standardParallels.length) {
                default: {
                    throw unknownParameter("standard_parallel_3");
                }
                case 2: {
                    φ1 = parameters.standardParallels[0];
                    φ2 = parameters.standardParallels[1];
                    break;
                }
                case 1: {
                    φ2 = φ1 = parameters.standardParallels[0];
                    break;
                }
                case 0: {
                    φ2 = φ1 = latitudeOfOrigin;
                    break;
                }
            }
            ensureLatitudeInRange(LambertConformal2SP.STANDARD_PARALLEL_1, φ1, true);
            ensureLatitudeInRange(LambertConformal2SP.STANDARD_PARALLEL_2, φ2, true);
        }
        if (abs(φ1 + φ2) < ANGLE_TOLERANCE * (180/PI)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.LATITUDES_ARE_OPPOSITE_$2,
                    new Latitude(φ1), new Latitude(φ2)));
        }
        φ1 = toRadians(φ1);
        φ2 = toRadians(φ2);
        latitudeOfOrigin = toRadians(latitudeOfOrigin);
        /*
         * Computes constants.
         */
        final double ρ0, F;
        final double cosφ1 = cos(φ1);
        final double sinφ1 = sin(φ1);
        final boolean secant = abs(φ1 - φ2) > ANGLE_TOLERANCE; // Should be 'true' for 2SP case.
        final boolean isSpherical = parameters.isSpherical();
        if (isSpherical) {
            if (secant) {
                n = log(cosφ1 / cos(φ2)) /
                    log(tan(PI/4 + 0.5*φ2) / tan(PI/4 + 0.5*φ1));
            } else {
                n = sinφ1;
            }
            F = cosφ1 * pow(tan(PI/4 + 0.5*φ1), n) / n;
            if (abs(abs(latitudeOfOrigin) - PI/2) >= ANGLE_TOLERANCE) {
                ρ0 = F * pow(tan(PI/4 + 0.5*latitudeOfOrigin), -n);
            } else {
                ρ0 = 0.0;
            }
        } else {
            final double m1 = msfn(sinφ1, cosφ1);
            final double t1 = tsfn(φ1, sinφ1);
            if (secant) {
                final double sinφ2 = sin(φ2);
                final double m2 = msfn(sinφ2, cos(φ2));
                final double t2 = tsfn(φ2, sinφ2);
                n = log(m1/m2) / log(t1/t2);
            } else {
                n = sinφ1;
            }
            F = m1 * pow(t1, -n) / n;
            if (abs(abs(latitudeOfOrigin) - PI/2) >= ANGLE_TOLERANCE) {
                ρ0 = F * pow(tsfn(latitudeOfOrigin, sin(latitudeOfOrigin)), n);
            } else {
                ρ0 = 0.0;
            }
        }
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         *
         * In GeoTools 2, ρ0 was added or subtracted in the tranform methods.
         * In Geotk, we move those linear operations to the affine transforms.
         * In addition of ρ0, linear operations include the reversal of the sign
         * of y, etc.
         */
        final AffineTransform normalize   = parameters.normalize(true);
        final AffineTransform denormalize = parameters.normalize(false);
        if (belgium) {
            normalize.translate(-BELGE_A, 0);
        }
        normalize.scale(n, 1);
        parameters.validate();
        denormalize.translate(0, ρ0);
        denormalize.scale(F, -F);
        finish();
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that the returned
     * descriptor is about the unitary projection, not the full one. Consequently the current
     * implementation returns the descriptor of {@link LambertConformal2SP} in all cases,
     * because the 1SP case is implemented as 2SP with both standard parallels set to the
     * latitude of origin.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return LambertConformal2SP.PARAMETERS;
    }

    /**
     * Returns a copy of the parameter values for this projection. The default implementation
     * returns the parameters defined in the {@linkplain UnitaryProjection#getParameterValues
     * super-class}, with the addition of standard parallels. No other parameters are set,
     * because the above ones are the only significant ones for this unitary projection.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final double[] standardParallels = parameters.standardParallels;
        final int n = standardParallels.length;
        final double phi0 = parameters.latitudeOfOrigin;
        final double phi1 = (n != 0) ? standardParallels[0] : phi0;
        final double phi2 = (n >= 2) ? standardParallels[1] : phi1;
        final ParameterValueGroup values = super.getParameterValues();
        getOrCreate(STANDARD_PARALLEL_1, values).setValue(phi1);
        getOrCreate(STANDARD_PARALLEL_2, values).setValue(phi2);
        return values;
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
        /*
         * NOTE: If some equation terms seem missing, this is because the linear operations
         * applied before the first non-linear one moved to the "normalize" affine transform,
         * and the linear operations applied after the last non-linear one moved to the
         * "denormalize" affine transform.
         */
        final double λ    = rollLongitude(srcPts[srcOff]);
        final double φ    = srcPts[srcOff + 1];
        final double a    = abs(φ);
        final double sinλ = sin(λ);
        final double cosλ = cos(λ);
        final double sinφ;
        final double ρ; // Snyder p. 108
        if (a < PI/2) {
            sinφ = sin(φ);
            ρ = pow(tsfn(φ, sinφ), n);
        } else if (a < PI/2 + ANGLE_TOLERANCE) {
            sinφ = 1;
            ρ = (φ*n <= 0) ? POSITIVE_INFINITY : 0;
        } else {
            ρ = sinφ = NaN;
        }
        final double x = ρ * sinλ;
        final double y = ρ * cosλ;
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
        final double dρ; // Snyder p. 108
        if (sinφ != 1) {
            dρ = n * dtsfn_dφ(φ, sinφ, cos(φ)) * ρ;
        } else {
            dρ = ρ;
        }
        return new Matrix2(y, dρ*sinλ,   // ∂x/∂λ , ∂x/∂φ
                          -x, dρ*cosλ);  // ∂y/∂λ , ∂y/∂φ
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
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        /*
         * NOTE: If some equation terms seem missing (e.g. "y = ρ0 - y"), this is because
         * the linear operations applied before the first non-linear one moved to the inverse
         * of the "denormalize" transform, and the linear operations applied after the last
         * non-linear one moved to the inverse of the "normalize" transform.
         */
        final double ρ = hypot(x, y);  // Zero when the latitude is 90 degrees.
        /*
         * Proj4 explicitly tests if (ρ > EPSILON) here. In Geotk we skip this test,
         * since Math functions are defined in such strict way that the correct answer is
         * produced (and even a better answer in the case of NaN input). This is verified
         * in LambertConformatTest.testExtremes().
         *
         * if (n<0) {ρ=-ρ; x=-x; y=-y;} was also removed because F should have the same
         * sign than n, so the above sign reversal was intended to make F/ρ positive. But
         * because we do not involve F here anymore (it is done in the affine transform),
         * we need to keep the current sign of ρ which is positive, otherwise we get NaN
         * when used in the pow(ρ, ...) expression below.
         */
        dstPts[dstOff  ] = unrollLongitude(atan2(x, y));
        dstPts[dstOff+1] = cphi2(pow(ρ, 1.0/n));
    }


    /**
     * Provides the transform equations for the spherical case of the Lambert Conformal projection.
     * <p>
     * <b>Implementation note:</b> this class contains explicit checks for latitude values at
     * poles. See the discussion in the {@link Mercator.Spherical} javadoc for an explanation.
     * The following is specific to the Lambert Conformal projection.
     * <p>
     * Comparison of observed behavior at poles between the spherical and ellipsoidal cases,
     * if no special checks are applied:
     *
     * {@preformat text
     *     ┌───────┬─────────────────────────────┬───────────────────────────┐
     *     │       │ Spherical                   │ Ellipsoidal               │
     *     ├───────┼─────────────────────────────┼───────────────────────────┤
     *     │ North │ Approximative (y = small)   │ Exact answer  (y = 0.0)   │
     *     │ South │ Exact answer  (y = +∞)      │ Approximative (y = big)   │
     *     └───────┴─────────────────────────────┴───────────────────────────┘
     * }
     *
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @author André Gosselin (MPO)
     * @author Rueben Schulz (UBC)
     * @version 3.00
     *
     * @since 2.1
     * @module
     */
    @Immutable
    static final class Spherical extends LambertConformal {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -7005092237343502956L;

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
        public Matrix transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff,
                                final boolean derivate) throws ProjectionException
        {
            final double λ    = rollLongitude(srcPts[srcOff]);
            final double φ    = srcPts[srcOff+1];
            final double a    = abs(φ);
            final double sinλ = sin(λ);
            final double cosλ = cos(λ);
            final double ρ;
            if (a < PI/2) {
                ρ = pow(tan(PI/4 + 0.5*φ), -n);
            } else if (a < PI/2 + ANGLE_TOLERANCE) {
                ρ = (φ*n <= 0) ? POSITIVE_INFINITY : 0;
            } else {
                ρ = NaN;
            }
            final double x = ρ * sinλ;
            final double y = ρ * cosλ;
            Matrix derivative = null;
            if (derivate) {
                final double dρ;
                if (a < PI/2) {
                    dρ = -n*ρ/cos(φ);
                } else {
                    dρ = NaN;
                }
                derivative = new Matrix2(y, dρ*sinλ,   // ∂x/∂λ , ∂x/∂φ
                                        -x, dρ*cosλ);  // ∂y/∂λ , ∂y/∂φ
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
            x = unrollLongitude(atan2(x, y));
            y = 2.0 * atan(pow(1/ρ, 1.0/n)) - PI/2;
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
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            final LambertConformal that = (LambertConformal) object;
            return epsilonEqual(this.n, that.n, mode);
        }
        return false;
    }
}
