/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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
import static java.lang.Double.*;
import java.awt.geom.AffineTransform;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;


/**
 * Lambert Azimuthal Equal Area projection (EPSG codes 9820, 9821). See the
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
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
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
     * but is not restricted to. If a different descriptor is supplied, it is user's responsability
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
        if (parameters.nameMatches(org.geotoolkit.referencing.operation.provider.
                LambertAzimuthalEqualArea.Spherical.PARAMETERS))
        {
            parameters.ensureSpherical();
        }
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
        final double sinphi = sin(latitudeOfOrigin);
        final boolean isSpherical = isSpherical();
        if (isSpherical) {
            sinb1 = sinphi;
            cosb1 = cos(latitudeOfOrigin);
        } else {
            sinb1 = qsfn(sinphi) / qp;
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
                final double dd = cos(latitudeOfOrigin) / (sqrt(1 - excentricitySquared*(sinphi*sinphi))*rq*cosb1);
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
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        final double lambda = rollLongitude(srcPts[srcOff]);
        final double phi    = srcPts[srcOff + 1];
        final double coslam = cos(lambda);
        final double sinlam = sin(lambda);
        final double sinphi = sin(phi);
        final double q = qsfn(sinphi);
        final double c;
        double x, y;
        if (!pole) {
            final double sinb = q / qp;
            final double cosb = sqrt(1 - sinb*sinb);
            if (oblique) {
                c = 1.0 + sinb * sinb1 + cosb * coslam * cosb1;
                y = rq * (sinb * cosb1 - cosb * coslam * sinb1);
                x = rq * cosb * sinlam;
                // xmf was (rq * dd) and ymf was (rq / dd), but the
                // dd part is now handled by the affine transform.
            } else {
                c = 1.0 + cosb * coslam;
                y = sinb * (0.5*qp);
                x = cosb * sinlam;
                // Proj4 had (xmf, ymf) terms here, but xmf simplifies to (rq * 1/rq) == 1
                // (see the comments in the constructor) and ymf simplify to rq² = 0.5*qp.
            }
            final double b = sqrt(2/c);
            x *= b;
            y *= b;
        } else {
            /*
             * Polar case. If this is the North case, then:
             *
             *  - Before this block, sign of phi has been reversed by the normalize affine
             *    transform. Concequence of the above, sign of q is also reversed.
             *
             *  - After this block, sign of y will be reversed by the denormalize affine
             *    transform, so it should not be reversed here.
             *
             * A little bit of algebra shows that the formulas become identical to the South case
             * exept for the sign of c (which doesn't matter), so only South case needs to be here.
             */
            c = phi - PI/2;
            final double b = sqrt(qp + q);
            /*
             * Proj4 tested for (qp + q) > 0, but this can be negative only if the given
             * latitude is greater (in absolute value) than 90°. By removing this check,
             * we let Java produces NaN in such cases, which is probably a right thing.
             */
            x = b * sinlam;
            y = b * coslam;
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
        dstPts[dstOff]     = x;
        dstPts[dstOff + 1] = y;
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
            final double rho = hypot(x, y);
            if (rho < EPSILON) {
                // This check is required because otherwise, the equations
                // in the "else" block would contains 0/0 expressions.
                dstPts[dstOff] = 0.0;
                dstPts[dstOff + 1] = latitudeOfOrigin;
                return;
            }
            double sCe, cCe;
            sCe = 2.0 * asin(0.5 * rho / rq);
            cCe = cos(sCe);
            sCe = sin(sCe);
            x *= sCe;
            if (oblique) {
                ab = cCe * sinb1 + y * sCe * cosb1 / rho;
                y  = rho * cosb1 * cCe - y * sinb1 * sCe;
            } else {
                ab = y * sCe / rho;
                y  = rho * cCe;
            }
        }
        double t = abs(ab);
        if (t > 1 && t <= (1 + ANGLE_TOLERANCE)) {
            t = copySign(PI/2, ab);
        } else {
            t = asin(ab);
        }
        dstPts[dstOff + 1] = t + APA0 * sin(t += t) + APA1 * sin(t += t) + APA2 * sin(t + t);
        dstPts[dstOff] = unrollLongitude(atan2(x, y));
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
    static final class Spherical extends LambertAzimuthalEqualArea {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 2091431369806844342L;

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
            final double lambda = rollLongitude(srcPts[srcOff]);
            final double phi    = srcPts[srcOff + 1];
            final double coslam = cos(lambda);
            double x,y;
            if (!pole) {
                final double sinphi = sin(phi);
                final double cosphi = cos(phi);
                /*
                 * 'x' (actually not really x before the end of this block,  but rather an
                 * intermediate scale coefficient elsewhere called 'k') is checked against
                 * zero as a safety and because Proj4 does that way.  The formulas already
                 * leads naturally to NaN in Java because division by 0 produces infinity,
                 * and the following multiplication by zero (x == 0 implies sinphi == 0 at
                 * least in the equatorial case) would produce NaN. But the explicit check
                 * makes sure that we set both ordinates to NaN.
                 */
                if (!oblique) {
                    x = 1.0 + cosphi * coslam;
                    if (x >= EPSILON) {
                        x = sqrt(2.0 / x);
                        y = x * sinphi;
                    } else {
                        x = y = NaN;
                    }
                } else {
                    y = cosphi * coslam;
                    x = 1.0 + sinb1 * sinphi + cosb1 * y;
                    if (x >= EPSILON) {
                        x = sqrt(2.0 / x);
                        y = x * (cosb1 * sinphi - sinb1 * y);
                    } else {
                        x = y = NaN;
                    }
                }
                x *= cosphi * sin(lambda);
            } else if (abs(phi - PI/2) >= EPSILON) {
                /*
                 * Polar projection (North and South cases).
                 * Reminder: in the North case, the sign of phi is reversed before this block
                 * and the sign of y will be reversed after this block by the affine transforms.
                 */
                y = 2 * cos(PI/4 - 0.5*phi);
                x = y * sin(lambda);
                y *= coslam;
            } else {
                /*
                 * Attempt to project the opposite pole. Actually the above formula would works
                 * and returns an acceptable answer, but every points on a circle of radius 2
                 * would fit. Proj4 was returning an error code; in Geotk we said that no
                 * single point is the answer.
                 */
                x = y = NaN;
            }
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
            double lambda, phi;
            final double rho = hypot(x, y);
            phi = 2.0 * asin(0.5 * rho);
            if (pole) {
                /*
                 * Reminder in the North case:
                 *   - Before this block, sign of y has been reverted by the affine transform.
                 *   - After this block, sign of phi will be reverted by the affine transform.
                 */
                phi -= (PI / 2);
                lambda = atan2(x, y);
            } else {
                final double sinz = sin(phi);
                final double cosz = cos(phi);
                if (!oblique) {
                    phi = abs(rho) <= EPSILON ? 0 : asin(y * sinz / rho);
                    y = rho * cosz;
                } else {
                    y *= sinz;
                    phi = abs(rho) <= EPSILON ? latitudeOfOrigin : asin(cosz*sinb1 + y*cosb1/rho);
                    y = rho*cosz*cosb1 - y*sinb1;
                }
                x *= sinz;
                lambda = atan2(x, y);
            }
            lambda = unrollLongitude(lambda);
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, lambda, phi);
            dstPts[dstOff] = lambda;
            dstPts[dstOff + 1] = phi;
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
            final LambertAzimuthalEqualArea that = (LambertAzimuthalEqualArea) object;
            return equals(this.latitudeOfOrigin, that.latitudeOfOrigin, strict);
            // All other coefficients are derived from the latitude of origin and excentricity.
        }
        return false;
    }

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse.
     */
    @Override
    double getErrorEstimate(final double lambda, final double phi) {
        return 1E-8;
    }
}
