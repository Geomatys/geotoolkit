/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2000-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.referencing.operation.matrix.Matrix2;

import static java.lang.Math.*;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;


/**
 * <cite>Orthographic</cite> projection. See the
 * <A HREF="http://mathworld.wolfram.com/OrthographicProjection.html">Orthographic projection on
 * MathWorld</A> for an overview. See any of the following providers for a list of programmatic
 * parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Orthographic}</li>
 * </ul>
 *
 * {@section Description}
 * This is a perspective azimuthal (planar) projection that is neither conformal nor equal-area.
 * It resembles a globe and only one hemisphere can be seen at a time, since it is a perspective
 * projection from infinite distance. While not useful for accurate measurements, this projection
 * is useful for pictorial views of the world. Only the spherical form is given here.
 *
 * {@section References}
 * <ul>
 *   <li>Proj-4.4.7 available at <A HREF="http://www.remotesensing.org/proj">www.remotesensing.org/proj</A>.<br>
 *       Relevant files are: {@code PJ_ortho.c}, {@code pj_fwd.c} and {@code pj_inv.c}.</li>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.20
 *
 * @since 2.0
 * @module
 */
@Immutable
public class Orthographic extends UnitaryProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 5036668705538661687L;

    /**
     * Maximum difference allowed when comparing real numbers.
     */
    private static final double EPSILON = 1E-6;

    /**
     * 0 if equatorial, 1 if polar, any other value if oblique. In the equatorial case,
     * {@link #latitudeOfOrigin} is zero, {@link #sinφ0} is zero and {@link #cosφ0}
     * is one.
     */
    private final byte type;

    /**
     * The latitude of origin, in radians.
     */
    private final double latitudeOfOrigin;

    /**
     * The sine of the {@link #latitudeOfOrigin}.
     */
    private final double sinφ0;

    /**
     * The cosine of the {@link #latitudeOfOrigin}.
     */
    private final double cosφ0;

    /**
     * Creates an Orthographic projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.Orthographic#PARAMETERS}, but
     * is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to an Orthographic projection.
     *
     * @param  descriptor Typically {@code Orthographic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final Orthographic projection = new Orthographic(parameters);
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Orthographic(final Parameters parameters) {
        super(parameters);
        double latitudeOfOrigin = toRadians(parameters.latitudeOfOrigin);
        boolean north=false, south=false;
        /*
         * Detect the special cases (equtorial or polar). In the polar case, we use the
         * same formulas for the North pole than the ones for the South pole, with only
         * the sign of y reversed.
         */
        if (abs(abs(latitudeOfOrigin) - PI/2) <= ANGLE_TOLERANCE) {
            // Polar case. The latitude of origin must be set to a positive value even for the
            // South case because the "normalize" affine transform will reverse the sign of φ.
            if (latitudeOfOrigin >= 0) {
                north = true;
            } else {
                south = true;
            }
            latitudeOfOrigin = PI/2;
            type = 1;
        } else if (latitudeOfOrigin == 0) {
            type = 0; // Equatorial case
        } else {
            type = 2; // Oblique case.
        }
        this.latitudeOfOrigin = latitudeOfOrigin;
        sinφ0 = sin(latitudeOfOrigin);
        cosφ0 = cos(latitudeOfOrigin);
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        if (south) {
            parameters.normalize(true).scale(1, -1);
        }
        parameters.validate();
        final AffineTransform denormalize = parameters.normalize(false);
        if (!parameters.isSpherical()) {
            /*
             * In principle the elliptical case is not supported. If nevertheless the user gave
             * an ellipsoid, use the same Earth radius than the one computed in Equirectangular.
             */
            double p = sin(abs(latitudeOfOrigin));
            p = sqrt(1 - excentricitySquared) / (1 - (p*p)*excentricitySquared);
            denormalize.scale(p, p);
        }
        if (north) {
            denormalize.scale(1, -1);
        }
        finish();
    }

    /**
     * Returns {@code true} since this projection is implemented using spherical formulas.
     */
    @Override
    boolean isSpherical() {
        return true;
    }

    /**
     * Returns the parameter descriptors for this unitary projection. Note that
     * the returned descriptor is about the unitary projection, not the full one.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return org.geotoolkit.referencing.operation.provider.Orthographic.PARAMETERS;
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
        final double cosφ = cos(φ);
        final double cosλ = cos(λ);
        final double sinλ = sin(λ);
        final double threshold, y;
        switch (type) {
            default: { // Oblique
                final double sinφ = sin(φ);
                threshold = sinφ0*sinφ + cosφ0*cosφ*cosλ;
                y = cosφ0*sinφ - sinφ0*cosφ*cosλ;
                break;
            }
            case 0: { // Equatorial
                threshold = cosφ * cosλ;
                y = sin(φ);
                break;
            }
            case 1: { // Polar (South case, applicable to North because of (de)normalize transforms)
                threshold = φ;
                y = cosφ * cosλ;
                break;
            }
        }
        if (threshold < -EPSILON) {
            throw new ProjectionException(Errors.Keys.POINT_OUTSIDE_HEMISPHERE);
        }
        if (dstPts != null) {
            dstPts[dstOff  ] = cosφ * sinλ;
            dstPts[dstOff+1] = y;
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double m00, m01, m10, m11;
        final double sinφ = sin(φ);
        m00 =  cosφ * cosλ;
        m01 = -sinφ * sinλ;
        switch (type) {
            default: { // Oblique
                m10 = sinφ0 * cosφ * sinλ;
                m11 = cosφ0 * cosφ + sinφ0*cosλ*sinφ;
                break;
            }
            case 0: { // Equatorial
                m10 = 0;
                m11 = cosφ;
                break;
            }
            case 1: { // Polar (South case, applicable to North because of (de)normalize transforms)
                m10 = -cosφ * sinλ;
                m11 = -sinφ * cosλ;
                break;
            }
        }
        return new Matrix2(m00, m01, m10, m11);
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
        final double ρ = hypot(x, y);
        double sinc = ρ;
        if (sinc > 1) {
            if (sinc - 1 > ANGLE_TOLERANCE) {
                throw new ProjectionException(Errors.Keys.POINT_OUTSIDE_HEMISPHERE);
            }
            sinc = 1;
        }
        double φ;
        if (ρ <= EPSILON) {
            φ = latitudeOfOrigin;
            x = 0;
        } else {
            if (type != 1) {
                final double cosc = sqrt(1 - sinc * sinc);
                if (type != 0) {
                    // Oblique case
                    φ = (cosc * sinφ0) + (y * sinc * cosφ0 / ρ);
                    x  *= sinc * cosφ0;
                    y   = (cosc - sinφ0 * φ) * ρ; // equivalent to part of (20-15)
                } else {
                    // Equatorial case
                    φ = y * sinc / ρ;
                    x  *= sinc;
                    y   = cosc * ρ;
                }
                φ = (abs(φ) >= 1) ? copySign(PI/2, φ) : asin(φ);
            } else {
                // South pole case, applicable to North case because of (de)normalize transforms.
                φ = acos(sinc); // equivalent to asin(cos(c)) over the range [0:1]
            }
            x = atan2(x, y);
        }
        dstPts[dstOff  ] = unrollLongitude(x);
        dstPts[dstOff+1] = φ;
    }

    /**
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            final Orthographic that = (Orthographic) object;
            return epsilonEqual(latitudeOfOrigin, that.latitudeOfOrigin, mode);
            // All other fields are derived from the latitude of origin.
        }
        return false;
    }
}
