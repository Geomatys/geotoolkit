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
import static java.lang.Double.*;
import java.awt.geom.AffineTransform;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.measure.Latitude;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.provider.LambertConformal1SP;
import org.geotoolkit.referencing.operation.provider.LambertConformal2SP;

import static org.geotoolkit.internal.referencing.Identifiers.*;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters.ensureLatitudeInRange;


/**
 * Lambert Conical Conformal Projection (EPSG codes 9801, 9802, 9803). See the
 * <A HREF="http://mathworld.wolfram.com/LambertConformalConicProjection.html">Lambert conformal
 * conic projection on MathWorld</A> for an overview. See any of the following providers for a
 * list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal1SP}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal2SP}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal2SP.Belgium}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertConformal2SP.ESRI}</li>
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
 *       EPSG Guidence Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author André Gosselin (MPO)
 * @author Rueben Schulz (UBC)
 * @version 3.0
 *
 * @since 1.0
 * @module
 */
public class LambertConformal extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2067358524298002016L;

    /**
     * Constant for the belgium 2SP case. This is 29.2985 seconds, given here in radians.
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
     * supplied, it is user's responsability to ensure that it is suitable to a Lambert Conformal
     * projection.
     *
     * @param  descriptor Typically one of {@link LambertConformal1SP#PARAMETERS} or
     *         {@link LambertConformal2SP#PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.0
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
        double phi1, phi2;
        double latitudeOfOrigin = parameters.latitudeOfOrigin;
        final boolean belgium = parameters.nameMatches(LambertConformal2SP.Belgium.PARAMETERS);
        if (parameters.nameMatches(LambertConformal1SP.PARAMETERS)) {
            // EPSG says the 1SP case uses the latitude of origin as the SP.
            phi1 = phi2 = latitudeOfOrigin;
            ensureLatitudeInRange(LambertConformal1SP.LATITUDE_OF_ORIGIN, phi1, true);
        } else {
            switch (parameters.standardParallels.length) {
                default: {
                    throw unknownParameter("standard_parallel_3");
                }
                case 2: {
                    phi1 = parameters.standardParallels[0];
                    phi2 = parameters.standardParallels[1];
                    break;
                }
                case 1: {
                    phi2 = phi1 = parameters.standardParallels[0];
                    break;
                }
                case 0: {
                    phi2 = phi1 = latitudeOfOrigin;
                    break;
                }
            }
            ensureLatitudeInRange(LambertConformal2SP.STANDARD_PARALLEL_1, phi1, true);
            ensureLatitudeInRange(LambertConformal2SP.STANDARD_PARALLEL_2, phi2, true);
        }
        if (abs(phi1 + phi2) < ANGLE_TOLERANCE * (180/PI)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ANTIPODE_LATITUDES_$2,
                    new Latitude(phi1), new Latitude(phi2)));
        }
        phi1 = toRadians(phi1);
        phi2 = toRadians(phi2);
        latitudeOfOrigin = toRadians(latitudeOfOrigin);
        /*
         * Computes constants.
         */
        final double rho0, F;
        final double cosphi1 = cos(phi1);
        final double sinphi1 = sin(phi1);
        final boolean secant = abs(phi1 - phi2) > ANGLE_TOLERANCE; // Should be 'true' for 2SP case.
        final boolean isSpherical = parameters.isSpherical();
        if (isSpherical) {
            if (secant) {
                n = log(cosphi1 / cos(phi2)) /
                    log(tan(PI/4 + 0.5*phi2) / tan(PI/4 + 0.5*phi1));
            } else {
                n = sinphi1;
            }
            F = cosphi1 * pow(tan(PI/4 + 0.5*phi1), n) / n;
            if (abs(abs(latitudeOfOrigin) - PI/2) >= ANGLE_TOLERANCE) {
                rho0 = F * pow(tan(PI/4 + 0.5*latitudeOfOrigin), -n);
            } else {
                rho0 = 0.0;
            }
        } else {
            final double m1 = msfn(sinphi1, cosphi1);
            final double t1 = tsfn(phi1, sinphi1);
            if (secant) {
                final double sinphi2 = sin(phi2);
                final double m2 = msfn(sinphi2, cos(phi2));
                final double t2 = tsfn(phi2, sinphi2);
                n = log(m1/m2) / log(t1/t2);
            } else {
                n = sinphi1;
            }
            F = m1 * pow(t1, -n) / n;
            if (abs(abs(latitudeOfOrigin) - PI/2) >= ANGLE_TOLERANCE) {
                rho0 = F * pow(tsfn(latitudeOfOrigin, sin(latitudeOfOrigin)), n);
            } else {
                rho0 = 0.0;
            }
        }
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         *
         * In Geotoolkit 2, rho0 was added or substracted in the tranform methods.
         * In Geotoolkit, we move those linear operations to the affine transforms.
         * In addition of rho0, linear operations include the reversal of the sign
         * of y, etc.
         */
        final AffineTransform normalize   = parameters.normalize(true);
        final AffineTransform denormalize = parameters.normalize(false);
        if (belgium) {
            normalize.translate(-BELGE_A, 0);
        }
        normalize.scale(n, 1);
        parameters.validate();
        denormalize.translate(0, rho0);
        denormalize.scale(F, -F);
        finish();
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that the returned
     * descriptor is about the unitary projection, not the full one. Concequently the current
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
        setValue(STANDARD_PARALLEL_1, values, phi1);
        setValue(STANDARD_PARALLEL_2, values, phi2);
        return values;
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
        /*
         * NOTE: If some equation terms seem missing, this is because the linear operations
         * applied before the first non-linear one moved to the "normalize" affine transform,
         * and the linear operations applied after the last non-linear one moved to the
         * "denormalize" affine transform.
         */
        double y = srcPts[srcOff + 1];
        final double rho; // Snyder p. 108
        final double a = abs(y);
        if (a < PI/2) {
            rho = pow(tsfn(y, sin(y)), n);
        } else if (a < PI/2 + ANGLE_TOLERANCE) {
            rho = (y*n <= 0) ? POSITIVE_INFINITY : 0;
        } else {
            rho = NaN;
        }
        final double x = rollLongitude(srcPts[srcOff]);
        dstPts[dstOff]     = rho * sin(x);
        dstPts[dstOff + 1] = rho * cos(x);
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
        /*
         * NOTE: If some equation terms seem missing (e.g. "y = rho0 - y"), this is because
         * the linear operations applied before the first non-linear one moved to the inverse
         * of the "denormalize" transform, and the linear operations applied after the last
         * non-linear one moved to the inverse of the "normalize" transform.
         */
        double rho = hypot(x, y);  // Zero when the latitude is 90 degrees.
        /*
         * Proj4 explicitly tests if (rho > EPSILON) here. In Geotoolkit we skip this test,
         * since Math functions are defined in such strict way that the correct answer is
         * produced (and even a better answer in the case of NaN input). This is verified
         * in LambertConformatTest.testExtremes().
         *
         * if (n<0) {rho=-rho; x=-x; y=-y;} was also removed because F should have the same
         * sign than n, so the above sign reversal was intented to make F/rho positive. But
         * because we do not involve F here anymore (it is done in the affine transform),
         * we need to keep the current sign of rho which is positive, otherwise we get NaN
         * when used in the pow(rho, ...) expression below.
         */
        dstPts[dstOff] = unrollLongitude(atan2(x, y));
        dstPts[dstOff + 1] = cphi2(pow(rho, 1.0/n));
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
     * @version 3.0
     *
     * @since 2.1
     * @module
     */
    static final class Spherical extends LambertConformal {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -7005092237343502956L;

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
            double y = srcPts[srcOff + 1];
            final double rho;
            final double a = abs(y);
            if (a < PI/2) {
                rho = pow(tan(PI/4 + 0.5*y), -n);
            } else if (a < PI/2 + ANGLE_TOLERANCE) {
                rho = (y*n <= 0) ? POSITIVE_INFINITY : 0;
            } else {
                rho = NaN;
            }
            double x = rollLongitude(srcPts[srcOff]);
            y = rho * cos(x);
            x = rho * sin(x);

            assert checkTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff]     = x;
            dstPts[dstOff + 1] = y;
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
            double x = srcPts[srcOff];
            double y = srcPts[srcOff + 1];
            double rho = hypot(x, y);
            x = unrollLongitude(atan2(x, y));
            y = 2.0 * atan(pow(1/rho, 1.0/n)) - PI/2;

            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff] = x;
            dstPts[dstOff + 1] = y;
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
            final LambertConformal that = (LambertConformal) object;
            return equals(this.n, that.n, strict);
        }
        return false;
    }

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse.
     */
    @Override
    double getErrorEstimate(final double lambda, final double phi) {
        return 0;
    }
}
