/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.referencing.operation.Matrix;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;

import org.apache.sis.util.ComparisonMode;
import org.apache.sis.referencing.operation.matrix.Matrix2;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;


/**
 * <cite>Lambert Azimuthal Equal Area</cite> projection (EPSG codes 9820, 1027, <del>9821</del>). See the
 * <A HREF="http://mathworld.wolfram.com/LambertAzimuthalEqual-AreaProjection.html">Lambert Azimuthal
 * Equal-Area projection on MathWorld</A> for an overview. See any of the following providers for a
 * list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea}</li>
 * </ul>
 *
 * {@section References}
 * <ul>
 *   <li> A. Annoni, C. Luzet, E.Gubler and J. Ihde - Map Projections for Europe</li>
 *   <li> John P. Snyder (Map Projections - A Working Manual,<br>
 *        U.S. Geological Survey Professional Paper 1395)</li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author Beate Stollberg
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
@Immutable
public class LambertAzimuthalEqualArea extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1639914708790574760L;

    /**
     * Epsilon for the comparison of small quantities.
     */
    private static final double EPSILON = 1E-10;

    /**
     * Constants for authalic latitude.
     */
    private static final double P00 = 0.33333333333333333333,
                                P01 = 0.17222222222222222222,
                                P02 = 0.10257936507936507936,
                                P10 = 0.06388888888888888888,
                                P11 = 0.06640211640211640211,
                                P20 = 0.01641501294219154443;

    /**
     * {@code true} if the projection is at a pole.
     */
    final boolean pole;

    /**
     * If {@code pole} is {@code true}, then this field {@code true} is true for the North pole
     * and {@code false} for the South pole.
     */
    final boolean north;

    /**
     * If {@code pole} is {@code false}, then this field {@code true} is true for the oblique
     * case and {@code false} for the equatorial case.
     */
    final boolean oblique;

    /**
     * Latitude of origin, in radians.
     */
    final double latitudeOfOrigin;

    /**
     * Parameters derived from the latitude of origin and the excentricity. In the
     * spherical case, they are straight sinus and cosinus of the latitude of origin.
     */
    final double sinb1, cosb1;

    /**
     * Constant parameters. They depend only on the excentricity, not on the
     * latitude of origin. Consequently they are not used in the spherical case.
     */
    final double qp, rq;

    /**
     * Coefficients for authalic latitude. They depend only on the excentricity,
     * not on the latitude of origin. They are all zero in the spherical case.
     */
    private final double APA0, APA1, APA2;

    /**
     * Creates an Albers Equal Area projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea#PARAMETERS},
     * but is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Lambert Azimuthal Equal Area projection.
     *
     * @param  descriptor Typically {@code LambertAzimuthalEqualArea.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final LambertAzimuthalEqualArea projection;
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new LambertAzimuthalEqualArea(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected LambertAzimuthalEqualArea(final Parameters parameters) {
        super(parameters);
        /*
         * Detects the mode (oblique, etc.).
         */
        latitudeOfOrigin = toRadians(parameters.latitudeOfOrigin);
        final double t = abs(latitudeOfOrigin);
        if (abs(t - PI/2) < EPSILON) {
            pole    = true;
            north   = (latitudeOfOrigin >= 0.0);
            oblique = false;
        } else {
            pole    = false;
            north   = false;
            oblique = (abs(t) >= EPSILON);
        }
        /*
         * Computes the constants for authalic latitude. Those constants depend
         * only on the excentricity. Note that in the spherical cases:
         *
         *   - All APA coefficients simplifies to zero.
         *   - qp = 2  (result of qsfn(1) when e² = 0).
         *   - rq = 1  (concequence of the above).
         */
        final double es2 = excentricitySquared * excentricitySquared;
        final double es3 = excentricitySquared * es2;
        APA0 = P02 * es3 + P01 * es2 + P00 * excentricitySquared;
        APA1 = P11 * es3 + P10 * es2;
        APA2 = P20 * es3;
        qp   = qsfn(1);
        rq   = sqrt(0.5 * qp);
        /*
         * Now computes the coefficients that depend on the latitude of origin.
         * Note that in the equatorial case:
         *
         *     - sinb1 = 0
         *     - cosb1 = 1
         *
         * The polar case is sinb1 = 2/qp for North, -2/qp for South.
         */
        final double sinφ = sin(latitudeOfOrigin);
        final boolean isSpherical = isSpherical();
        if (isSpherical) {
            sinb1 = sinφ;
            cosb1 = cos(latitudeOfOrigin);
        } else {
            sinb1 = qsfn(sinφ) / qp;
            cosb1 = sqrt(1.0 - sinb1 * sinb1);
        }
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        final AffineTransform   normalize = parameters.normalize(true);
        final AffineTransform denormalize = parameters.normalize(false);
        parameters.validate();
        if (north) {
            normalize  .scale(1, -1);
            denormalize.scale(1, -1);
        } else if (!isSpherical) {
            if (oblique) {
                /*
                 * Conceptually we can consider that this block is executed for all oblique cases,
                 * including spherical. However in the spherical case, the equation simplifies to
                 * dd = 1/rq. With rq = 1 (see above), we get dd = 1.
                 */
                final double dd = cos(latitudeOfOrigin) / (sqrt(1 - excentricitySquared*(sinφ*sinφ))*rq*cosb1);
                denormalize.scale(dd, 1/dd);
            } else {
                /*
                 * In the equatorial case the above equation simplify to dd = 1/rq. However the
                 * equatorial case is treated differently, so we do not put those coefficients
                 * in the affine transform.
                 */
            }
        }
        finish();
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that
     * the returned descriptor is about the unitary projection, not the full one.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea.PARAMETERS;
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
        final double q = qsfn(sinφ);
        final double b,c;
        double x, y;
        if (!pole) {
            final double sinb = q / qp;
            final double cosb = sqrt(1 - sinb*sinb);
            if (oblique) {
                c = 1.0 + sinb * sinb1 + cosb * cosλ * cosb1;
                y = rq * (sinb * cosb1 - cosb * cosλ * sinb1);
                x = rq * cosb * sinλ;
                // xmf was (rq * dd) and ymf was (rq / dd), but the
                // dd part is now handled by the affine transform.
            } else {
                c = 1.0 + cosb * cosλ;
                y = sinb * (0.5*qp);
                x = cosb * sinλ;
                // Proj4 had (xmf, ymf) terms here, but xmf simplifies to (rq * 1/rq) == 1
                // (see the comments in the constructor) and ymf simplify to rq² = 0.5*qp.
            }
            b = sqrt(2/c);
            x *= b;
            y *= b;
        } else {
            /*
             * Polar case. If this is the North case, then:
             *
             *  - Before this block, sign of φ has been reversed by the normalize affine
             *    transform. Concequence of the above, sign of q is also reversed.
             *
             *  - After this block, sign of y will be reversed by the denormalize affine
             *    transform, so it should not be reversed here.
             *
             * A little bit of algebra shows that the formulas become identical to the South case
             * exept for the sign of c (which doesn't matter), so only South case needs to be here.
             */
            c = φ - PI/2;
            b = sqrt(qp + q);
            /*
             * Proj4 tested for (qp + q) > 0, but this can be negative only if the given
             * latitude is greater (in absolute value) than 90°. By removing this check,
             * we let Java produces NaN in such cases, which is probably a right thing.
             */
            x = b * sinλ;
            y = b * cosλ;
        }
        if (abs(c) < EPSILON) {
            /*
             * Projecting the antipode of origin. For example if the origin is the North pole,
             * then projection of South pole would be all the points on the circle of radius 2.
             * We can not return a single point for that (or actually we could if we took the
             * longitude value in account despite the fact that all longitudes still at the same
             * point when the latitude is at a pole).
             */
            x = y = NaN;
        }
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
        final double cosφ  = cos(φ);
        final double dq_dφ = dqsfn_dφ(sinφ, cosφ);
        if (pole) {
            final double db_dφ = 0.5 * dq_dφ / b;
            return new Matrix2(y, db_dφ*sinλ,
                              -x, db_dφ*cosλ);
        }
        final double sinb      = q / qp;
        final double dsinb_dφ  = dq_dφ / qp;
        final double cosb      = sqrt(1.0 - sinb*sinb);
        final double dcosb_dφ  = -dsinb_dφ * (sinb/cosb);
        final double sinλcosb  = sinλ *  cosb;
        final double cosλcosb  = cosλ *  cosb;
        final double cosλdcosb = cosλ * dcosb_dφ;
        /*
         * In equatorial case, sinb1=0 and cosb1=1. We could do a special case
         * with the simplification, but the result is not that much simpler.
         */
        final double T     =    cosb1*sinb - sinb1*cosλcosb;
        final double dT_dλ =    sinb1*sinλcosb;
        final double db_dλ =    cosb1*sinλcosb / (2*c);
        final double dT_dφ =   (cosb1*dsinb_dφ - sinb1*cosλdcosb);
        final double db_dφ =  -(sinb1*dsinb_dφ + cosb1*cosλdcosb) / (2*c);
        final double f     =    2*rq / sqrt(2*c);
        return new Matrix2(
                f * (cosλ     + db_dλ*sinλ)*cosb,
                f * (dcosb_dφ + db_dφ*cosb)*sinλ,
                f * (dT_dλ    + db_dλ*T),
                f * (dT_dφ    + db_dφ*T));
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
        final double ab;
        if (pole) {
            // See comments in the transform(...) method for an explanation about why we
            // don't need a special case for the North pole. Similar rational applies here.
            ab = (x*x + y*y)/qp - 1;
        } else {
            /*
             * (x /= dd) and (y *= dd) have been performed by the affine transform, but only in
             * the oblique case. For the equatorial case we need to apply it ourself.  Reminder
             * from the constructor: dd = 1/rd in the equatorial case.
             */
            if (!oblique) {
                x *= rq;
                y /= rq;
            }
            final double ρ = hypot(x, y);
            if (ρ < EPSILON) {
                // This check is required because otherwise, the equations
                // in the "else" block would contains 0/0 expressions.
                dstPts[dstOff] = 0.0;
                dstPts[dstOff + 1] = latitudeOfOrigin;
                return;
            }
            double sCe, cCe;
            sCe = 2.0 * asin(0.5 * ρ / rq);
            cCe = cos(sCe);
            sCe = sin(sCe);
            x *= sCe;
            if (oblique) {
                ab = cCe * sinb1 + y * sCe * cosb1 / ρ;
                y  = ρ * cosb1 * cCe - y * sinb1 * sCe;
            } else {
                ab = y * sCe / ρ;
                y  = ρ * cCe;
            }
        }
        double t = abs(ab);
        if (t > 1 && t <= (1 + ANGLE_TOLERANCE)) {
            t = copySign(PI/2, ab);
        } else {
            t = asin(ab);
        }
        dstPts[dstOff+1] = t + APA0 * sin(t += t) + APA1 * sin(t += t) + APA2 * sin(t + t);
        dstPts[dstOff  ] = unrollLongitude(atan2(x, y));
    }


    /**
     * Provides the transform equations for the spherical case.
     *
     * @author Gerald Evenden (USGS)
     * @author Beate Stollberg
     * @author Martin Desruisseaux (IRD, Geomatys)
     * @version 3.00
     *
     * @since 2.4
     * @module
     */
    @Immutable
    static final class Spherical extends LambertAzimuthalEqualArea {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 2091431369806844342L;

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
            final double φ    = srcPts[srcOff + 1];
            final double sinλ = sin(λ);
            final double cosλ = cos(λ);
            double x,y;
            if (!pole) {
                final double sinφ = sin(φ);
                final double cosφ = cos(φ);
                /*
                 * 'x' (actually not really x before the end of this block,  but rather an
                 * intermediate scale coefficient elsewhere called 'k') is checked against
                 * zero as a safety and because Proj4 does that way.  The formulas already
                 * leads naturally to NaN in Java because division by 0 produces infinity,
                 * and the following multiplication by zero (x == 0 implies sinφ == 0 at
                 * least in the equatorial case) would produce NaN. But the explicit check
                 * makes sure that we set both ordinates to NaN.
                 */
                if (!oblique) {
                    x = 1.0 + cosφ * cosλ;
                    if (x >= EPSILON) {
                        x = sqrt(2.0 / x);
                        y = x * sinφ;
                    } else {
                        x = y = NaN;
                    }
                } else {
                    y = cosφ * cosλ;
                    x = 1.0 + sinb1 * sinφ + cosb1 * y;
                    if (x >= EPSILON) {
                        x = sqrt(2.0 / x);
                        y = x * (cosb1 * sinφ - sinb1 * y);
                    } else {
                        x = y = NaN;
                    }
                }
                x *= cosφ * sinλ;
            } else if (abs(φ - PI/2) >= EPSILON) {
                /*
                 * Polar projection (North and South cases).
                 * Reminder: in the North case, the sign of φ is reversed before this block
                 * and the sign of y will be reversed after this block by the affine transforms.
                 */
                y = 2 * cos(PI/4 - 0.5*φ);
                x = y * sinλ;
                y *= cosλ;
            } else {
                /*
                 * Attempt to project the opposite pole. Actually the above formula would works
                 * and returns an acceptable answer, but every points on a circle of radius 2
                 * would fit. Proj4 was returning an error code; in Geotk we said that no
                 * single point is the answer.
                 */
                x = y = NaN;
            }
            Matrix derivative = null;
            if (derivate) {
                final double m00, m01, m10, m11;
                if (pole) {
                    final double U = 2 * cos(PI/4 - 0.5*φ);
                    final double dU_dφ = sin(PI/4 - 0.5*φ);
                    m00 =  cosλ * U;
                    m10 = -sinλ * U;
                    m01 =  sinλ * dU_dφ;
                    m11 =  cosλ * dU_dφ;
                } else {
                    final double sinφ = sin(φ);
                    final double cosφ = cos(φ);
                    final double cosφcosλ = cosφ * cosλ;
                    final double cosφsinλ = cosφ * sinλ;
                    final double sinφcosλ = sinφ * cosλ;
                    double b = 1 + sinb1*sinφ + cosb1*cosφcosλ;
                    final double S = sqrt(2 / b);
                    b *= b*S;
                    final double dS_dλ = (cosb1*cosφsinλ) / b;
                    final double dS_dφ = (cosb1*sinφcosλ - sinb1*cosφ) / b;

                    m00 = dS_dλ * cosφsinλ + cosφcosλ*S;
                    m01 = dS_dφ * cosφsinλ - sinφ*sinλ*S;
                    m10 = cosb1 *  dS_dλ*sinφ           - sinb1*(dS_dλ*cosφcosλ - cosφsinλ*S);
                    m11 = cosb1 * (dS_dφ*sinφ + cosφ*S) - sinb1*(dS_dφ*cosφcosλ - sinφcosλ*S);
                }
                derivative = new Matrix2(m00, m01, m10, m11);
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
            double λ, φ;
            final double ρ = hypot(x, y);
            φ = 2.0 * asin(0.5 * ρ);
            if (pole) {
                /*
                 * Reminder in the North case:
                 *   - Before this block, sign of y has been reverted by the affine transform.
                 *   - After this block, sign of φ will be reverted by the affine transform.
                 */
                φ -= (PI / 2);
                λ = atan2(x, y);
            } else {
                final double sinz = sin(φ);
                final double cosz = cos(φ);
                if (!oblique) {
                    φ = abs(ρ) <= EPSILON ? 0 : asin(y * sinz / ρ);
                    y = ρ * cosz;
                } else {
                    y *= sinz;
                    φ = abs(ρ) <= EPSILON ? latitudeOfOrigin : asin(cosz*sinb1 + y*cosb1/ρ);
                    y = ρ*cosz*cosb1 - y*sinb1;
                }
                x *= sinz;
                λ = atan2(x, y);
            }
            λ = unrollLongitude(λ);
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, λ, φ);
            dstPts[dstOff  ] = λ;
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
            final LambertAzimuthalEqualArea that = (LambertAzimuthalEqualArea) object;
            return epsilonEqual(this.latitudeOfOrigin, that.latitudeOfOrigin, mode);
            // All other coefficients are derived from the latitude of origin and excentricity.
        }
        return false;
    }
}
