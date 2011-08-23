/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2011, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.operation.projection;

import java.awt.geom.Point2D;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.referencing.operation.matrix.Matrix2;

import static java.lang.Math.*;
import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.referencing.operation.provider.Krovak.*;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters.*;


/**
 * Krovak Oblique Conformal Conic projection (EPSG code 9819). See the
 * <A HREF="http://www.posc.org/Epicentre.2_2/DataModel/ExamplesofUsage/eu_cs34e2.html">Krovak on POSC</A>
 * for an overview. See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Krovak}</li>
 * </ul>
 *
 * {@section Description}
 *
 * This projection is used in the Czech Republic and Slovakia under the name "Krovak" projection.
 * The geographic coordinates on the ellipsoid are first reduced to conformal coordinates on the
 * conformal (Gaussian) sphere. These spherical coordinates are then projected onto the oblique
 * cone and converted to grid coordinates. The pseudo standard parallel is defined on the conformal
 * sphere after its rotation, to obtain the oblique aspect of the projection. It is then the parallel
 * on this sphere at which the map projection is true to scale; on the ellipsoid it maps as a complex
 * curve.
 * <p>
 * The compulsory parameters are just the ellipsoid characteristics. All other parameters are
 * optional and have defaults to match the common usage with Krovak projection.
 * <p>
 * {@linkplain org.opengis.referencing.crs.ProjectedCRS Projected CRS} using the Krovak projection
 * are usually defined with (<var>southing</var>, <var>westing</var>) axis orientations. ESRI uses
 * those orientations by default. However in Geotk, every projection must have (<var>easting</var>,
 * <var>northing</var>) orientations - axis reversal are handled by the concatenation of affine
 * transforms. Consequently in order to get the usual axis order, a Krovak projected CRS
 * <strong>must</strong> defines axis order explicitly (as required by the OGC standard)
 * like in the example below:
 *
 * {@preformat wkt
 *     PROJCS["S-JTSK (Ferro) / Krovak",
 *       GEOCS[...],
 *     PROJECTION["Krovak"]
 *     PARAMETER["semi_major", 6377397.155],
 *     PARAMETER["semi_minor", 6356078.963],
 *     UNIT["metre", 1],
 *     AXIS["y", SOUTH],
 *     AXIS["x", WEST]]
 * }
 *
 * The default Krovak projection implemented by this class - having (<var>easting</var>,
 * <var>northing</var>) axis orientations - is cold Krovak GIS version.
 *
 * {@section References}
 * <ul>
 *   <li>Proj-4.4.7 available at <A HREF="http://www.remotesensing.org/proj">www.remotesensing.org/proj</A><br>
 *       Relevant files is: {@code PJ_krovak.c}</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas" available at,
 *       <A HREF="http://www.remotesensing.org/geotiff/proj_list/guid7.html">http://www.remotesensing.org/geotiff/proj_list/guid7.html</A></li>
 * </ul>
 *
 * @author Jan Jezek (HSRS)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 2.4
 * @module
 */
@Immutable
public class Krovak extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8359105634355342212L;

    /**
     * When to stop the iteration.
     */
    @SuppressWarnings("hiding")
    private static final double ITERATION_TOLERANCE = 1E-11;

    /**
     * Sinus and cosinus of the azimuth. The azimuth is measured at the centre line passing
     * through the centre of the projection, and is equal to the co-latitude of the cone
     * axis at point of intersection with the ellipsoid.
     */
    private final double sinAzim, cosAzim;

    /**
     * Useful variables calculated from parameters defined by user. They depend on the
     * latitude of origin, the latitude of pseudo standard parallel and the excentricity.
     */
    private final double n, tanS2, alfa, hae, k1, ka, ro0;

    /**
     * Creates a Krovak projection from the given parameters. The descriptor argument is usually
     * {@link org.geotoolkit.referencing.operation.provider.Krovak#PARAMETERS}, but is not restricted
     * to. If a different descriptor is supplied, it is user's responsibility to ensure that it is
     * suitable to a Krovak projection.
     *
     * @param  descriptor Typically {@code Krovak.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Krovak projection;
        final Parameters parameters = new Parameters(descriptor, values);
        projection = new Krovak(parameters);
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Krovak(final Parameters parameters) {
        super(parameters);
        double latitudeOfOrigin = parameters.latitudeOfOrigin;
        ensureLatitudeInRange(LATITUDE_OF_CENTRE, latitudeOfOrigin, false);
        latitudeOfOrigin = toRadians(latitudeOfOrigin);
        double pseudoStandardParallel;
        switch (parameters.standardParallels.length) {
            default: {
                throw unknownParameter(Identifiers.STANDARD_PARALLEL_2);
            }
            case 0: {
                pseudoStandardParallel = PSEUDO_STANDARD_PARALLEL.getDefaultValue();
                break;
            }
            case 1: {
                pseudoStandardParallel = parameters.standardParallels[0];
                break;
            }
        }
        pseudoStandardParallel = toRadians(pseudoStandardParallel);
        final double azimuth = toRadians(parameters.azimuth);
        sinAzim = sin(azimuth);
        cosAzim = cos(azimuth);
        n       = sin(pseudoStandardParallel);
        tanS2   = tan(pseudoStandardParallel/2 + PI/4);

        final double sinLat, cosLat, cosL2, u0;
        sinLat = sin(latitudeOfOrigin);
        cosLat = cos(latitudeOfOrigin);
        cosL2  = cosLat * cosLat;
        alfa   = sqrt(1 + ((excentricitySquared * (cosL2*cosL2)) / (1 - excentricitySquared)));
        hae    = alfa * excentricity / 2;
        u0     = asin(sinLat / alfa);

        final double g, esl;
        esl = excentricity * sinLat;
        g   = pow((1 - esl) / (1 + esl), (alfa * excentricity) / 2);
        k1  = pow(tan(latitudeOfOrigin/2 + PI/4), alfa) * g / tan(u0/2 + PI/4);
        ka  = pow(1/k1, -1/alfa);
        ro0 = pow(tanS2, -n);
        final double radius = sqrt(1 - excentricitySquared) / (1 - (excentricitySquared * (sinLat*sinLat)));
        final double rop = radius / (ro0 * tan(pseudoStandardParallel));
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        parameters.normalize(true).scale(-alfa, 1);
        parameters.validate();
        parameters.normalize(false).scale(-rop, -rop);
        finish();
    }

    /**
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     */
    @Override
    protected void transform(final double[] srcPts, final int srcOff,
                             final double[] dstPts, final int dstOff)
            throws ProjectionException
    {
        final double Δv   = srcPts[srcOff];
        final double φ    = srcPts[srcOff+1];
        final double esp  = excentricity * sin(φ);
        final double gfi  = pow(((1 - esp) / (1 + esp)), hae);
        final double U    = 2 * (atan(pow(tan(φ/2 + PI/4), alfa) / k1 * gfi) - PI/4);
        final double cosU = cos(U);
        final double S    = asin(cosAzim*sin(U) + sinAzim*cosU*cos(Δv));
        final double ε    = n * asin(cosU * sin(Δv) / cos(S));
        final double ρ    = pow(tan(S/2 + PI/4), n);

        // x and y are reverted.
        dstPts[dstOff  ] = sin(ε) / ρ;
        dstPts[dstOff+1] = cos(ε) / ρ;
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
        final double x = srcPts[srcOff];
        final double y = srcPts[srcOff + 1];
        // x -> southing, y -> westing
        final double ρ    = hypot(x, y);
        final double D    = atan2(x, y) / n;
        final double S    = 2 * (atan(pow(ro0/ρ, 1/n) * tanS2) - PI/4);
        final double cosS = cos(S);
        final double U    = asin(cosAzim*sin(S) - sinAzim*cosS*cos(D));
        final double kau  = ka * pow(tan(U/2 + PI/4), 1/alfa);
        final double Δv   = asin(cosS * sin(D)/cos(U));
        double φ = 0;

        // iteration calculation
        for (int i=MAXIMUM_ITERATIONS;;) {
            final double φ1 = φ;
            final double esf = excentricity * sin(φ1);
            φ = 2 * (atan(kau * pow((1 + esf) / (1 - esf), excentricity/2)) - PI/4);
            if (abs(φ1 - φ) <= ITERATION_TOLERANCE) {
                break;
            }
            if (--i < 0) {
                throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
            }
        }
        dstPts[dstOff  ] = Δv;
        dstPts[dstOff+1] = φ;
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
        final double λ = rollLongitude(point.getX());
        final double φ = point.getY();
        final double sinφ   = sin(φ);
        final double sinλ   = sin(λ);
        final double cosλ   = cos(λ);
        final double tan1   = tan(φ/2 + PI/4);
        final double esinφ  = excentricity * sinφ;
        final double dgφ    = excentricitySquared*cos(φ) / (1 - esinφ*esinφ);
        final double V      = pow(tan1, alfa) * pow((1-esinφ) / (1+esinφ), hae) / k1;
        final double U      = 2 * (atan(V) - PI/4);
        final double cosU   = cos(U);
        final double sinU   = sin(U);
        final double sinS   = cosAzim*sinU + sinAzim*cosU*cosλ;
        final double cosS   = sqrt(1 - sinS*sinS);
        final double dU_dφ  = alfa * (1/tan1 + tan1 - 2*dgφ) / (1/V + V);
        final double dS_dλ  = (-sinAzim*cosU*sinλ) / cosS;
        final double dS_dφ  = (-sinAzim*sinU*cosλ + cosAzim*cosU)*dU_dφ / cosS;
        final double Ɛt     = cosU*sinλ / cosS;
        final double Ɛ      = n * asin(Ɛt);
        final double cosƐ   = cos(Ɛ);
        final double sinƐ   = sin(Ɛ);
        final double t      = n / (cosS*cosS*sqrt(1 - Ɛt*Ɛt));
        final double dƐ_dλ  = cosU * t * (dS_dλ*sinλ*sinS +       cosλ*cosS);
        final double dƐ_dφ  = sinλ * t * (dS_dφ*sinS*cosU - dU_dφ*sinU*cosS);
        final double tan2   = tan(asin(sinS)/2 + PI/4);
        final double ρ      = pow(tan2, n);
        final double m      = (-n/2) * (1/tan2 + tan2);
        return new Matrix2(
                (m*dS_dλ * sinƐ + dƐ_dλ * cosƐ) / ρ,
                (m*dS_dφ * sinƐ + dƐ_dφ * cosƐ) / ρ,
                (m*dS_dλ * cosƐ - dƐ_dλ * sinƐ) / ρ,
                (m*dS_dφ * cosƐ - dƐ_dφ * sinƐ) / ρ);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(sinAzim, hash(n, super.computeHashCode()));
    }

    /**
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            final Krovak that = (Krovak) object;
            return epsilonEqual(sinAzim, that.sinAzim, mode) &&
                   epsilonEqual(cosAzim, that.cosAzim, mode) &&
                   epsilonEqual(n,       that.n,       mode) &&
                   epsilonEqual(tanS2,   that.tanS2,   mode) &&
                   epsilonEqual(alfa,    that.alfa,    mode) &&
                   epsilonEqual(hae,     that.hae,     mode) &&
                   epsilonEqual(k1,      that.k1,      mode) &&
                   epsilonEqual(ka,      that.ka,      mode) &&
                   epsilonEqual(ro0,     that.ro0,     mode);
        }
        return false;
    }
}
