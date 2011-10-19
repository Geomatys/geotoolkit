/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import java.awt.geom.AffineTransform;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.referencing.operation.provider.Mercator1SP;
import org.geotoolkit.referencing.operation.provider.Mercator2SP;
import org.geotoolkit.referencing.operation.provider.PseudoMercator;
import org.geotoolkit.referencing.operation.provider.MillerCylindrical;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters.ensureLatitudeInRange;


/**
 * Mercator Cylindrical Projection (EPSG codes 9804, 9805). See the
 * <A HREF="http://mathworld.wolfram.com/MercatorProjection.html">Mercator projection on MathWorld</A>
 * for an overview. See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link Mercator1SP}</li>
 *   <li>{@link Mercator2SP}</li>
 *   <li>{@link PseudoMercator}</li>
 *   <li>{@link MillerCylindrical}</li>
 * </ul>
 *
 * {@section Description}
 *
 * The parallels and the meridians are straight lines and cross at right angles; this projection
 * thus produces rectangular charts. The scale is true along the equator (by default) or along two
 * parallels equidistant of the equator (if a scale factor other than 1 is used).
 * <p>
 * This projection is used to represent areas close to the equator. It is also often used for
 * maritime navigation because all the straight lines on the chart are <em>loxodrome</em> lines,
 * i.e. a ship following this line would keep a constant azimuth on its compass.
 * <p>
 * This implementation handles both the 1 and 2 standard parallel cases.
 * For {@code Mercator_1SP} (EPSG code 9804), the line of contact is the equator.
 * For {@code Mercator_2SP} (EPSG code 9805) lines of contact are symmetrical
 * about the equator.
 *
 * {@section Behavior at poles}
 * The projection of 90&deg;N gives {@linkplain Double#POSITIVE_INFINITY positive infinity}.
 * The projection of 90&deg;S gives {@linkplain Double#NEGATIVE_INFINITY negative infinity}.
 * Projection of a latitude outside the [-90-&epsilon; ... 90+&epsilon;]&deg; range produces
 * {@linkplain Double#NaN NaN}.
 *
 * {@section References}
 * <ul>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @author Simon Reynard (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.18
 *
 * @see TransverseMercator
 * @see ObliqueMercator
 *
 * @since 1.0
 * @module
 */
@Immutable
public class Mercator extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 2564172914329253286L;

    /**
     * Creates a Mercator projection from the given parameters. The descriptor argument is
     * usually one of the {@code PARAMETERS} constants defined in {@link Mercator1SP} or
     * {@link Mercator2SP}, but is not restricted to. If a different descriptor is supplied,
     * it is user's responsibility to ensure that it is suitable to a Mercator projection.
     *
     * @param  descriptor Typically one of {@link Mercator1SP#PARAMETERS},
     *         {@link Mercator2SP#PARAMETERS} or {@link MillerCylindrical#PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Mercator projection;
        final Parameters parameters = new Parameters(descriptor, values);
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else if (parameters.nameMatches(PseudoMercator.PARAMETERS)) {
            projection = new Spherical(parameters, true);
        } else {
            projection = new Mercator(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Mercator(final Parameters parameters) {
        super(parameters);
        final boolean miller = parameters.nameMatches(MillerCylindrical.PARAMETERS);
        switch (parameters.standardParallels.length) {
            default: {
                // A "standard_parallel_2" argument is presents.
                throw unknownParameter(Identifiers.STANDARD_PARALLEL_2);
            }
            case 0: {
                // No standard parallel. Instead, uses the scale factor explicitly provided.
                break;
            }
            case 1: {
                /*
                 * "scale_factor" is not a parameter in the Mercator_2SP case and is computed from
                 * the "standard_parallel". The Parameters constructor should have initialized the
                 * scale factor to 1, but we still use the *= operator rather than = in case some
                 * user implementation provided an explicit scale factor.
                 */
                double standardParallel = abs(parameters.standardParallels[0]);
                ensureLatitudeInRange(Mercator2SP.STANDARD_PARALLEL, standardParallel, false);
                parameters.standardParallels[0] = standardParallel;
                standardParallel = toRadians(standardParallel);
                if (parameters.isSpherical()) {
                    parameters.scaleFactor *= cos(standardParallel);
                }  else {
                    parameters.scaleFactor *= msfn(sin(standardParallel), cos(standardParallel));
                }
                break;
            }
        }
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        parameters.validate();
        final AffineTransform normalize   = parameters.normalize(true);
        final AffineTransform denormalize = parameters.normalize(false);
        /*
         * A correction that allows us to employ a latitude of origin that is not
         * correspondent to the equator. See Snyder and al. for reference, page 47.
         * The scale correction is multiplied with the global scale, which allows
         * MapProjection superclass to merge this correction with the scale factor
         * in a single multiplication.
         */
        final double φ     = toRadians(parameters.latitudeOfOrigin);
        final double sinφ  = sin(φ);
        final double scale = cos(φ) / sqrt(1 - excentricitySquared * (sinφ*sinφ));
        denormalize.scale(scale, miller ? scale*1.25 : scale);
        /*
         * Moves the longitude rotation from "normalize" to "denormalize" transform.  This is
         * possible in the particular case of the Mercator projection because its "transform"
         * method passes the longitude unchanged. By keeping the "normalize" affine as simple
         * as possible, we increase the chances of efficient concatenation of an inverse with
         * a forward projections.
         */
        if (Boolean.FALSE.equals(parameters.rollLongitude)) {
            final double dx = normalize.getTranslateX();
            if (dx != 0 && normalize.getShearX() == 0 && normalize.getShearY() == 0) {
                normalize.setTransform(normalize.getScaleX(), 0, 0, normalize.getScaleY(), 0, normalize.getTranslateY());
                denormalize.translate(dx, 0);
            }
        }
        if (miller) {
            normalize.scale(1, 0.8);
        }
        finish();
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that the returned
     * descriptor is about the unitary projection, not the full one. Consequently the current
     * implementation returns the descriptor of {@link Mercator1SP} in all cases, because the
     * 2SP case is implemented as 1SP with a different scale factor.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return Mercator1SP.PARAMETERS;
    }

    // No need to override getParameterValues() because no additional
    // parameter are significant to a unitary Mercator projection.

    /**
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff, boolean derivate)
            throws ProjectionException
    {
        double x = srcPts[srcOff];
        double y = srcPts[srcOff + 1];
        // Projection of zero is zero. However the formulas below have a slight rounding error
        // which produce values close to 1E-10, so we will avoid them when y=0. In addition of
        // avoiding rounding error, this also preserve the sign (positive vs negative zero).
        if (y != 0) {
            // See the javadoc of the Spherical inner class for a note
            // about why we perform explicit checks for the pole cases.
            final double a = abs(y);
            if (a < PI/2) {
                y = -log(tsfn(y, sin(y)));
            } else {
                y = copySign(a <= (PI/2 + ANGLE_TOLERANCE) ? POSITIVE_INFINITY : NaN, y);
            }
            dstPts[dstOff + 1] = y;
        }
        dstPts[dstOff] = rollLongitude(x);
        return null;
    }

    /**
     * Converts a list of coordinate point ordinal values.
     *
     * {@note We override the super-class method only as an optimization in the special case
     *        where the target coordinates are written at the same locations than the source
     *        coordinates. In such case, we can take advantage of the fact that the λ value
     *        is not modified by the unitary Mercator projection.}
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        if (srcPts != dstPts || srcOff != dstOff || rollLongitude()) {
            super.transform(srcPts, srcOff, dstPts, dstOff, numPts);
            return;
        }
        if (verifyCoordinateRanges()) {
            verifyGeographicRanges(srcPts, srcOff, numPts);
        }
        dstOff--;
        while (--numPts >= 0) {
            double y = dstPts[dstOff += 2]; // Same as srcPts[srcOff + 1].
            if (y != 0) {
                // See the javadoc of the Spherical inner class for a note
                // about why we perform explicit checks for the pole cases.
                final double a = abs(y);
                if (a < PI/2) {
                    y = -log(tsfn(y, sin(y)));
                } else {
                    y = copySign(a <= (PI/2 + ANGLE_TOLERANCE) ? POSITIVE_INFINITY : NaN, y);
                }
                dstPts[dstOff] = y;
            }
        }
        // Invoking Assertions.checkReciprocal(...) here would be
        // useless since it works only for non-overlapping arrays.
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
        final double y = srcPts[srcOff + 1];                // Must be before writing x.
        dstPts[dstOff] = unrollLongitude(srcPts[srcOff]);   // Must be before writing y.
        dstPts[dstOff+1] = cphi2(exp(-y));
    }


    /**
     * Provides the transform equations for the spherical case of the Mercator projection.
     * <p>
     * <b>Implementation note:</b> this class contains explicit checks for latitude values at
     * poles. If floating point arithmetic had infinite precision, those checks would not be
     * necessary since the formulas lead naturally to infinite values at poles, which is the
     * correct answer. In practice the infinite value emerges by itself at only one pole, and
     * the other one produces a high value (approximatively 1E+16). This is because there is
     * no accurate representation of PI/2 in base 2, and consequently {@code tan(PI/2)} does
     * not returns the infinite value.
     * <p>
     * The {@code Spherical} formula has the opposite behavior than {@link Mercator} regarding
     * which pole returns the infinite value. In {@code Spherical}, this is the South pole. In
     * {@code Mercator} (ellipsoidal case), this is the North pole. Using explicit checks allow
     * us to enforce the same behavior for the two implementations.
     *
     * @author Martin Desruisseaux (MPO, IRD, Geomatys)
     * @author Rueben Schulz (UBC)
     * @version 3.15
     *
     * @since 2.1
     * @module
     */
    @Immutable
    static final class Spherical extends Mercator {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 2383414176395616561L;

        /**
         * {@code true} if we are in the "Pseudo Mercator" case.
         */
        private final boolean pseudo;

        /**
         * Constructs a new map projection from the supplied parameters.
         *
         * @param parameters The parameters of the projection to be created.
         */
        protected Spherical(final Parameters parameters) {
            this(parameters, false);
        }

        /**
         * Constructs a new map projection from the supplied parameters.
         *
         * @param parameters The parameters of the projection to be created.
         * @param pseudo {@code true} if we are in the "Pseudo Mercator" case.
         */
        Spherical(final Parameters parameters, final boolean pseudo) {
            super(parameters);
            this.pseudo = pseudo;
            if (!pseudo) {
                parameters.ensureSpherical();
            }
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
        public ParameterDescriptorGroup getParameterDescriptors() {
            return pseudo ? PseudoMercator.PARAMETERS : super.getParameterDescriptors();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Matrix transform(final double[] srcPts, final int srcOff,
                                final double[] dstPts, final int dstOff, boolean derivate)
                throws ProjectionException
        {
            double x = rollLongitude(srcPts[srcOff]);
            double y = srcPts[srcOff + 1];
            // Projection of zero is zero. However the formulas below have a slight rounding error
            // which produce values close to 1E-10, so we will avoid them when y=0. In addition of
            // avoiding rounding error, this also preserve the sign (positive vs negative zero).
            if (y != 0) {
                // See class javadoc for a note about explicit check for poles.
                final double a = abs(y);
                if (a < PI/2) {
                    y = log(tan(PI/4 + 0.5*y));
                } else {
                    y = copySign(a <= (PI/2 + ANGLE_TOLERANCE) ? POSITIVE_INFINITY : NaN, y);
                }
                assert pseudo || checkTransform(srcPts, srcOff, dstPts, dstOff, x, y);
                dstPts[dstOff + 1] = y;
            }
            dstPts[dstOff] = x;
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * {@note This method must be overriden because the <code>Mercator</code> class
         *        overrides the <code>UnitaryProjection</code> implementation.}
         */
        @Override
        public void transform(final double[] srcPts, int srcOff,
                              final double[] dstPts, int dstOff, int numPts)
                throws TransformException
        {
            if (srcPts != dstPts || srcOff != dstOff || rollLongitude()) {
                super.transform(srcPts, srcOff, dstPts, dstOff, numPts);
                return;
            }
            if (verifyCoordinateRanges()) {
                verifyGeographicRanges(srcPts, srcOff, numPts);
            }
            dstOff--;
            while (--numPts >= 0) {
                double y = dstPts[dstOff += 2]; // Same as srcPts[srcOff...].
                if (y != 0) {
                    final double a = abs(y);
                    if (a < PI/2) {
                        y = log(tan(PI/4 + 0.5*y));
                    } else {
                        y = copySign(a <= (PI/2 + ANGLE_TOLERANCE) ? POSITIVE_INFINITY : NaN, y);
                    }
                    dstPts[dstOff] = y;
                }
            }
            // Invoking Assertions.checkReciprocal(...) here would be
            // useless since it works only for non-overlapping arrays.
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
            super.transform(srcPts, srcOff, dstPts, dstOff, false);
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
            y = PI/2 - 2.0*atan(exp(-y));
            assert pseudo || checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
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

        /**
         * Gets the derivative of this transform at a point.
         *
         * @param  point The coordinate point where to evaluate the derivative.
         * @return The derivative at the specified point as a 2&times;2 matrix.
         * @throws ProjectionException if the derivative can't be evaluated at the specified point.
         *
         * @since 3.12
         */
        @Override
        public Matrix derivative(final Point2D point) throws ProjectionException {
            final Matrix derivative = new Matrix2(1, 0, 0, 1/cos(point.getY()));
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
     * @since 3.18
     */
    @Override
    public Matrix derivative(final Point2D point) throws ProjectionException {
        final double φ = point.getY();
        final double sinφ = sin(φ);
        final double cosφ = cos(φ);
        final double esinφ = sinφ * excentricity;
        final double t = (1 - sinφ) / cosφ;
        return new Matrix2(1, 0, 0, 0.5*(t + 1/t) - excentricitySquared*cosφ / (1 - esinφ*esinφ));
    }

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse.
     * In the case of Mercator projection the error is close to zero everywhere,
     * even at poles.
     */
    @Override
    double getErrorEstimate(final double λ, final double φ) {
        return 0;
    }
}
