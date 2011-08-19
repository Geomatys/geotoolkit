/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import java.awt.geom.Point2D;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.matrix.Matrix2;

import static java.lang.Math.*;


/**
 * American Polyconic Projection (EPSG codes 9818). See the
 * <A HREF="http://mathworld.wolfram.com/PolyconicProjection.html">Polyconic projection on MathWorld</A>
 * for an overview. See the following provider for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Polyconic}</li>
 * </ul>
 *
 * {@section Description}
 * <ul>
 *   <li>Neither conformal nor equal-area.</li>
 *   <li>Parallels of latitude (except for Equator) are arcs of circles, but are not concentrics.</li>
 *   <li>Central Meridian and Equator are straight lines; all other meridians are complex curves.</li>
 *   <li>Scale is true along each parallel and along the central meridian, but no parallel is "standard".</li>
 *   <li>Free of distortion only along the central meridian.</li>
 * </ul>
 *
 * {@section References}
 * <ul>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7, Version 40.</li>
 * </ul>
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.11
 * @module
 */
@Immutable
public class Polyconic extends CassiniOrMercator {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4178027711158788385L;

    /**
     * Creates a Polyconic projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.Polyconic#PARAMETERS},
     * but is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Polyconic projection.
     *
     * @param  descriptor Typically one of {@code Polyconic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Polyconic projection;
        final Parameters parameters = new Parameters(descriptor, values);
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new Polyconic(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Polyconic(final Parameters parameters) {
        super(parameters);
    }

    /**
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     * <p>
     * <b>Note:</b> This method produces NaN at poles in the spherical cases
     * (may occur if assertions are enabled).
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double λ = rollLongitude(srcPts[srcOff]);
        final double φ = srcPts[srcOff + 1];
        final double sinφ = sin(φ);
        final double cosφ = cos(φ);
        final double msfn = msfn(sinφ, cosφ) / sinφ;
        /*
         * If y == 0, then we have (1/0) == infinity. Then we would have below
         * y = 0 + infinity * (1 - 1)  ==  infinity * zero  ==  indetermination.
         * Actually the indetermination resolve as being just leaving y unchanged
         * (same for x, except for longitude rolling).
         *
         * In Proj4 this was handled by a check for a threshold: if (abs(y) > 1E-10).
         * In Geotk, we try to avoid threshold as much as possible in order to have
         * more continuous function.
         */
        if (Double.isInfinite(msfn)) {
            dstPts[dstOff] = λ;
            return;
        }
        final double λsinφ = λ*sinφ;
        dstPts[dstOff+1] = msfn*(1 - cos(λsinφ)) + mlfn(φ, sinφ, cosφ);
        dstPts[dstOff  ] = msfn * sin(λsinφ);
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double x = srcPts[srcOff  ];
        final double y = srcPts[srcOff+1];
        final double λ;
        double φ;
        if (abs(y) <= EPSILON) {
            φ = 0;
            λ = x;
            /*
             * The general formulas below will not work for this case because of
             * indeterminations of the kind 0*infinity.
             */
        } else {
            φ = y;
            double dφ;
            final double r  = y*y + x*x;
            final double y2 = 2*y;
            int i = MAXIMUM_ITERATIONS;
            do {
                if (--i < 0) {
                    throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
                }
                final double cosφ = cos(φ);
                if (abs(cosφ) < ITERATION_TOLERANCE) {
                    // Continuing would lead to c = infinity, and later to an
                    // indetermination (infinity / infinity).
                    break;
                }
                final double sinφ = sin(φ);
                final double sinφcosφ = sinφ * cosφ;
                double mlp = sqrt(1 - excentricitySquared * (sinφ*sinφ));
                final double c   = mlp * sinφ/cosφ;
                final double ml  = mlfn(φ, sinφ, cosφ);
                final double mlb = ml*ml + r;
                mlp  = (1 - excentricitySquared) / (mlp*mlp*mlp);
                dφ = (2*ml + c*mlb - y2*(c*ml + 1)) /
                        (excentricitySquared * sinφcosφ * (mlb - y2*ml)/c +
                        (y2 - 2*ml) * (c*mlp - 1/sinφcosφ) - 2*mlp);
                φ += dφ;
            } while (abs(dφ) > ITERATION_TOLERANCE);
            final double c = sin(φ);
            λ = asin(x*tan(φ) * sqrt(1 - excentricitySquared*(c*c))) / c;
        }
        dstPts[dstOff  ] = unrollLongitude(λ);
        dstPts[dstOff+1] = φ;
    }


    /**
     * Provides the transform equations for the spherical case of the Polyconic projection.
     *
     * @author Simon Reynard (Geomatys)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.11
     *
     * @since 3.11
     * @module
     */
    @Immutable
    static final class Spherical extends Polyconic {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 8669570024272104893L;

        /**
         * The latitude of origin, in radians.
         */
        private final double phi0;

        /**
         * Constructs a new map projection from the supplied parameters.
         *
         * @param parameters The parameters of the projection to be created.
         */
        Spherical(final Parameters parameters) {
            super(parameters);
            phi0 = toRadians(parameters.latitudeOfOrigin);
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
            final double E = λ * sin(φ);
            final double cot = 1 / tan(φ);
            final double x = sin(E) * cot;
            final double y = φ - phi0 + cot * (1 - cos(E));
            assert checkTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff  ] = x;
            dstPts[dstOff+1] = y;
        }

        /**
         * Computes using ellipsoidal formulas and compares with the
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
            final double x = unrollLongitude(srcPts[srcOff]);
            final double y = srcPts[srcOff + 1];
            double λ;
            double φ;
            if (abs(y) <= EPSILON) {
                λ = x;
                φ = 0;
            } else {
                φ = y;
                double dφ;
                final double B = x*x + y*y;
                int i = MAXIMUM_ITERATIONS;
                do {
                    if (--i < 0) {
                        throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
                    }
                    final double tanφ = tan(φ);
                    dφ = (y * (φ*tanφ + 1) - φ - 0.5*(φ*φ + B) * tanφ) / ((φ - y) / tanφ - 1);
                    φ -= dφ;
                } while(abs(dφ) > ITERATION_TOLERANCE);
                λ = asin(x*tan(φ)) / sin(φ);
            }
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, λ, φ);
            dstPts[dstOff  ] = λ;
            dstPts[dstOff+1] = φ;
        }

        /**
         * Computes using ellipsoidal formulas and compares with the
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

        /**
         * Gets the derivative of this transform at a point.
         *
         * @param  point The coordinate point where to evaluate the derivative.
         * @return The derivative at the specified point as a 2&times;2 matrix.
         * @throws ProjectionException if the derivative can't be evaluated at the specified point.
         *
         * @since 3.19
         */
        @Override
        public Matrix derivative(final Point2D point) throws ProjectionException {
            final double λ     = rollLongitude(point.getX());
            final double φ     = point.getY();
            final double sinφ  = sin(φ);
            final double cosφ  = cos(φ);
            final double sin2φ = sinφ * sinφ;
            final double cot2φ = (cosφ*cosφ) / sin2φ;
            final double E     = λ*sinφ;
            final double cosE  = cos(E);
            final double sinE  = sin(E);
            final Matrix derivative = new Matrix2(
                    cosφ  * (cosE),                            // ∂x/∂λ
                    cot2φ * (cosE*E - sinE) - sinE,            // ∂x/∂φ
                    cosφ  * (sinE),                            // ∂y/∂λ
                    cot2φ * (sinE*E) + (cosE - 1)/sin2φ + 1);  // ∂y/∂φ
            assert Assertions.checkDerivative(derivative, super.derivative(point));
            return derivative;
        }
    }

    /**
     * Gets the derivative of this transform at a point.
     *
     * @param  point The coordinate point where to evaluate the derivative.
     * @return The derivative at the specified point as a 2&times;2 matrix.
     * @throws ProjectionException if the derivative can't be evaluated at the specified point.
     *
     * @since 3.19
     */
    @Override
    public Matrix derivative(final Point2D point) throws ProjectionException {
        final double λ     = rollLongitude(point.getX());
        final double φ     = point.getY();
        final double sinφ  = sin(φ);
        final double cosφ  = cos(φ);
        final double msfn  = msfn(sinφ, cosφ);
        final double msfnd = msfn / sinφ;
        if (Double.isInfinite(msfnd)) {
            // Returns an identity transform for consistency
            // with the case implemented in transform(...).
            return new Matrix2();
        }
        final double dmsdφ = dmsfn_dφ(sinφ, cosφ, msfn) - cosφ/sinφ;
        final double X2    = λ*sinφ;
        final double sinX2 = sin(X2);
        final double cosX2 = cos(X2);
        final double dX2dφ = λ*cosφ;
        return new Matrix2(
                msfn * cosX2,  msfnd * (dX2dφ*cosX2 + dmsdφ*(  sinX2)),
                msfn * sinX2,  msfnd * (dX2dφ*sinX2 + dmsdφ*(1-cosX2)) + dmlfn_dφ(sinφ*sinφ, cosφ*cosφ));
    }

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse.
     */
    @Override
    double getErrorEstimate(final double λ, final double φ) {
        return 0;
    }
}
