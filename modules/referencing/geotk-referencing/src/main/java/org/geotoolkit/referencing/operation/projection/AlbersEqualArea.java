/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.measure.Latitude;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.referencing.operation.matrix.Matrix2;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.apache.sis.math.MathFunctions.atanh;
import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters.ensureLatitudeInRange;


/**
 * <cite>Albers Equal Area</cite> projection (EPSG code 9822). See the
 * <A HREF="http://mathworld.wolfram.com/AlbersEqual-AreaConicProjection.html">Albers Equal-Area
 * Conic projection on MathWorld</A> for an overview. See any of the following providers for a
 * list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.AlbersEqualArea}</li>
 * </ul>
 *
 * {@section Description}
 *
 * This is a conic projection with parallels being unequally spaced arcs of concentric circles,
 * more closely spaced at north and south edges of the map. Meridians are equally spaced radii
 * of the same circles and intersect parallels at right angles. As the name implies, this
 * projection minimizes distortion in areas.
 * <p>
 * The {@code "standard_parallel_2"} parameter is optional and will be given the same value as
 * {@code "standard_parallel_1"} if not set (creating a 1 standard parallel projection).
 *
 * {@section References}
 * <ul>
 *   <li>Proj-4.4.7 available at <A HREF="http://www.remotesensing.org/proj">www.remotesensing.org/proj</A><br>
 *       Relevant files are: {@code PJ_aea.c}, {@code pj_fwd.c} and {@code pj_inv.c}.</li>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.20
 *
 * @see <A HREF="http://srmwww.gov.bc.ca/gis/bceprojection.html">British Columbia Albers Standard Projection</A>
 *
 * @since 2.1
 * @module
 */
@Immutable
public class AlbersEqualArea extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3024658742514888646L;

    /**
     * Maximum difference allowed when comparing real numbers.  Experience suggests that the
     * value 1E-6 is slightly too big: its prevent the usage of normal formulas close to the
     * poles, while the formulas would still applicable, thus leading to results less accurate
     * than they could be.
     * <p>
     * Note that Geotk formulas are modified in such a way that the {@code ρ} value which
     * is compared to {@code EPSILON} is the equivalent of {@code ρ/abs(n)} in Proj4, where
     * {@code abs(n)} is typically a number between 0.8 and 1.
     */
    private static final double EPSILON = 1E-7;

    /**
     * Constants used by the spherical and elliptical Albers projection.
     */
    final double n, c;

    /**
     * An error condition indicating iteration will not converge for the
     * inverse ellipse. See Snyder (14-20)
     */
    private final double ec;

    /**
     * Creates a Albers Equal Area projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.AlbersEqualArea#PARAMETERS}, but
     * is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to an Albers Equal Area projection.
     *
     * @param  descriptor Typically {@code AlbersEqualArea.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final AlbersEqualArea projection;
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new AlbersEqualArea(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected AlbersEqualArea(final Parameters parameters) {
        super(parameters);
        double φ1, phi2;
        double latitudeOfOrigin = parameters.latitudeOfOrigin;
        switch (parameters.standardParallels.length) {
            default: {
                throw unknownParameter("standard_parallel_3");
            }
            case 2: {
                φ1 = parameters.standardParallels[0];
                phi2 = parameters.standardParallels[1];
                break;
            }
            case 1: {
                phi2 = φ1 = parameters.standardParallels[0];
                break;
            }
            case 0: {
                phi2 = φ1 = parameters.latitudeOfOrigin;
                break;
            }
        }
        ensureLatitudeInRange(org.geotoolkit.referencing.operation.provider.AlbersEqualArea.STANDARD_PARALLEL_1, φ1, true);
        ensureLatitudeInRange(org.geotoolkit.referencing.operation.provider.AlbersEqualArea.STANDARD_PARALLEL_2, phi2, true);
        if (abs(φ1 + phi2) < ANGLE_TOLERANCE * (180/PI)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.LATITUDES_ARE_OPPOSITE_$2,
                    new Latitude(φ1), new Latitude(phi2)));
        }
        φ1 = toRadians(φ1);
        phi2 = toRadians(phi2);
        latitudeOfOrigin = toRadians(latitudeOfOrigin);
        /*
         * Computes constants.
         */
        if (abs(φ1 + phi2) < ANGLE_TOLERANCE) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.LATITUDES_ARE_OPPOSITE_$2,
                    new Latitude(toDegrees(φ1)), new Latitude(toDegrees(phi2))));
        }
        double  sinφ   = sin(φ1);
        double  cosφ   = cos(φ1);
        double  n      = sinφ;
        boolean secant = (abs(φ1 - phi2) >= ANGLE_TOLERANCE);
        final double ρ0;
        if (parameters.isSpherical()) {
            if (secant) {
                n = 0.5 * (n + sin(phi2));
            }
            c  = cosφ * cosφ + n*2 * sinφ;
            ρ0 = sqrt(c - n*2 * sin(latitudeOfOrigin)) / n;
            ec = NaN;
        } else {
            double m1 = msfn(sinφ, cosφ);
            double q1 = qsfn(sinφ);
            if (secant) { // secant cone
                sinφ    = sin(phi2);
                cosφ    = cos(phi2);
                double m2 = msfn(sinφ, cosφ);
                double q2 = qsfn(sinφ);
                n = (m1*m1 - m2*m2) / (q2 - q1);
            }
            c = m1*m1 + n*q1;
            ρ0 = sqrt(c - n * qsfn(sin(latitudeOfOrigin))) / n;
            ec = 1 + (1-excentricitySquared) * atanh(excentricity) / excentricity;
        }
        this.n = n;
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        final AffineTransform normalize   = parameters.normalize(true);
        final AffineTransform denormalize = parameters.normalize(false);
        normalize.scale(n, 1);
        parameters.validate();
        denormalize.translate(0, ρ0);
        denormalize.scale(1/n, -1/n);
        finish();
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that
     * the returned descriptor is about the unitary projection, not the full one.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return org.geotoolkit.referencing.operation.provider.AlbersEqualArea.PARAMETERS;
    }

    /**
     * Returns a copy of the parameter values for this projection. The default implementation
     * returns the parameters defined in the {@linkplain UnitaryProjection#getParameterValues
     * super-class}, with the addition of standard parallels and the latitude of origin.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final double[] standardParallels = parameters.standardParallels;
        final int n = standardParallels.length;
        final double φ0 = parameters.latitudeOfOrigin;
        final double φ1 = (n != 0) ? standardParallels[0] : φ0;
        final double φ2 = (n >= 2) ? standardParallels[1] : φ1;
        final ParameterValueGroup values = super.getParameterValues();
        getOrCreate(LATITUDE_OF_ORIGIN,  values).setValue(parameters.latitudeOfOrigin);
        getOrCreate(STANDARD_PARALLEL_1, values).setValue(φ1);
        getOrCreate(STANDARD_PARALLEL_2, values).setValue(φ2);
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
        final double λ = rollLongitude(srcPts[srcOff]);
        final double φ = srcPts[srcOff + 1];
        final double cosλ = cos(λ);
        final double sinλ = sin(λ);
        final double sinφ = sin(φ);
        double ρ = c - n * qsfn(sinφ);
        if (ρ < 0.0) {
            if (ρ > -EPSILON) {
                ρ = 0.0;
            } else {
                throw new ProjectionException(Errors.Keys.TOLERANCE_ERROR);
            }
        }
        ρ = sqrt(ρ);
        if (dstPts != null) {
            dstPts[dstOff]     = ρ * sinλ;
            dstPts[dstOff + 1] = ρ * cosλ;
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        double esinφ2 = excentricity * sinφ;
        esinφ2 *= esinφ2;
        final double dρ_dφ = -0.5 * n*dqsfn_dφ(sinφ, cos(φ)) / ρ;
        return new Matrix2(cosλ * ρ, dρ_dφ * sinλ,  // ∂x/∂λ, ∂x/∂φ
                          -sinλ * ρ, dρ_dφ * cosλ); // ∂y/∂λ, ∂y/∂φ
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
        /*
         * Proj4 had a code like this:
         *
         *     if (n < 0.0) {
         *         x = -x;
         *         y = -y;
         *         ρ = -ρ;
         *     }
         *
         * This condition has disappeared in Geotk because (x,y) are premultiplied by n
         * (by the affine transform) before to enter in this method, so if n was negative
         * those values have already their sign reverted. In the case of ρ, it was divided
         * further by n, so it got its sign reverted too.
         */
        final double ρ = hypot(x, y);
        x = atan2(x, y);
        if (ρ <= EPSILON) {
            y = copySign(PI/2, n);
        } else {
            y = (c - ρ*ρ) / n;
            if (abs(ec - abs(y)) <= EPSILON) { // Necessary to avoid "no convergence" error.
                y = copySign(PI/2, y);
            } else {
                y = phi1(y);
            }
        }
        dstPts[dstOff  ] = unrollLongitude(x);
        dstPts[dstOff+1] = y;
    }


    /**
     * Provides the transform equations for the spherical case of the Albers Equal Area projection.
     *
     * @author Gerald Evenden (USGS)
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @author Rémi Maréchal (Geomatys)
     * @version 3.18
     *
     * @since 2.1
     * @module
     */
    static final class Spherical extends AlbersEqualArea {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 9090765015127854096L;

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
            final double λ = rollLongitude(srcPts[srcOff]);
            final double φ = srcPts[srcOff + 1];
            final double cosλ = cos(λ);
            final double sinλ = sin(λ);
            final double sinφ = sin(φ);
            double ρ = c - n*2 * sinφ;
            if (ρ < 0.0) {
                if (ρ > -EPSILON) {
                    ρ = 0.0;
                } else {
                    throw new ProjectionException(Errors.Keys.TOLERANCE_ERROR);
                }
            }
            ρ = sqrt(ρ);
            final double y = ρ * cosλ;
            final double x = ρ * sinλ;
            Matrix derivative = null;
            if (derivate) {
                final double dρ_dφ = -n*cos(φ) / ρ;
                derivative = new Matrix2(
                        cosλ * ρ, dρ_dφ * sinλ,    // ∂x/∂λ , ∂x/∂φ
                       -sinλ * ρ, dρ_dφ * cosλ);   // ∂y/∂λ , ∂y/∂φ
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
            double x = srcPts[srcOff];
            double y = srcPts[srcOff + 1];
            final double ρ = hypot(x, y);
            x = unrollLongitude(atan2(x, y));
            if (ρ <= EPSILON) {
                y = copySign(PI/2, n);
            } else {
                y = (c - ρ*ρ) / (n*2);
                if (abs(y) >= 1.0) {
                    y = copySign(PI/2, y);
                } else {
                    y = asin(y);
                }
            }
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
                                              final double λ, final double φ)
                throws ProjectionException
        {
            super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkInverseTransform(dstPts, dstOff, λ, φ);
        }
    }

    /**
     * Iteratively solves equation (3-16) from Snyder.
     *
     * @param qs arcsin(q/2), used in the first step of iteration
     * @return the latitude
     */
    final double phi1(final double qs) throws ProjectionException {
        final double tone_es = 1 - excentricitySquared;
        double φ = asin(0.5 * qs);
        if (excentricity < EPSILON) {
            return φ;
        }
        for (int i=0; i<MAXIMUM_ITERATIONS; i++) {
            final double sinφ  = sin(φ);
            final double cosφ  = cos(φ);
            final double esinφ = excentricity * sinφ;
            final double com   = 1.0 - esinφ*esinφ;
            final double dφ    = 0.5 * com*com/cosφ * (qs/tone_es - sinφ/com - atanh(esinφ)/excentricity);
            φ += dφ;
            if (abs(dφ) <= ITERATION_TOLERANCE) {
                return φ;
            }
        }
        throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
    }

    /**
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            final AlbersEqualArea that = (AlbersEqualArea) object;
            return epsilonEqual(n, that.n, mode) &&
                   epsilonEqual(c, that.c, mode);
        }
        return false;
    }
}
