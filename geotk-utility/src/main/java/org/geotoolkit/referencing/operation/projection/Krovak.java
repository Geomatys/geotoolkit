/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.operation.projection;

import java.util.Map;
import java.util.EnumMap;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.OperationMethod;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.apache.sis.parameter.Parameters;

import org.apache.sis.referencing.operation.transform.ContextualParameters.MatrixRole;
import static java.lang.Math.*;
import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.referencing.operation.provider.Krovak.*;


/**
 * <cite>Krovak Oblique Conformal Conic</cite> projection (EPSG code 9819). See the
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
 *   PROJCS["S-JTSK (Ferro) / Krovak",
 *       GEOCS[...],
 *     PROJECTION["Krovak"],
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
 *
 * @since 2.4
 * @module
 */
public class Krovak extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8359105634355342212L;

    private static Map<ParameterRole, ParameterDescriptor<Double>> roles() {
        final EnumMap<ParameterRole, ParameterDescriptor<Double>> roles = new EnumMap<>(ParameterRole.class);
        roles.put(ParameterRole.CENTRAL_MERIDIAN, LONGITUDE_OF_CENTRE);
        roles.put(ParameterRole.SCALE_FACTOR,     SCALE_FACTOR);
        roles.put(ParameterRole.FALSE_EASTING,    FALSE_EASTING);
        roles.put(ParameterRole.FALSE_NORTHING,   FALSE_NORTHING);
        return roles;
    }

    /**
     * When to stop the iteration.
     */
    @SuppressWarnings("hiding")
    private static final double ITERATION_TOLERANCE = 1E-11;

    /**
     * Sinus and cosine of the azimuth. The azimuth is measured at the centre line passing
     * through the centre of the projection, and is equal to the co-latitude of the cone
     * axis at point of intersection with the ellipsoid.
     */
    private final double sinAzim, cosAzim;

    /**
     * Useful variables calculated from parameters defined by user. They depend on the
     * latitude of origin, the latitude of pseudo standard parallel and the eccentricity.
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
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final Krovak projection;
        final Parameters parameters = Parameters.castOrWrap(values);
        projection = new Krovak(descriptor, parameters);
        try {
            return (MathTransform2D) projection.createMapProjection(
                    org.apache.sis.internal.system.DefaultFactories.forBuildin(
                            org.opengis.referencing.operation.MathTransformFactory.class));
        } catch (org.opengis.util.FactoryException e) {
            throw new IllegalArgumentException(e); // TODO
        }
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Krovak(final OperationMethod method, final Parameters parameters) {
        super(method, parameters, roles());
        double latitudeOfOrigin = getAndStore(parameters, LATITUDE_OF_CENTRE);
        latitudeOfOrigin = toRadians(latitudeOfOrigin);
        final double pseudoStandardParallel = toRadians(getAndStore(parameters, PSEUDO_STANDARD_PARALLEL));
        final double azimuth = toRadians(getAndStore(parameters, AZIMUTH));
        sinAzim = sin(azimuth);
        cosAzim = cos(azimuth);
        n       = sin(pseudoStandardParallel);
        tanS2   = tan(pseudoStandardParallel/2 + PI/4);

        final double sinLat, cosLat, cosL2, u0;
        sinLat = sin(latitudeOfOrigin);
        cosLat = cos(latitudeOfOrigin);
        cosL2  = cosLat * cosLat;
        alfa   = sqrt(1 + ((eccentricitySquared * (cosL2*cosL2)) / (1 - eccentricitySquared)));
        hae    = alfa * eccentricity / 2;
        u0     = asin(sinLat / alfa);

        final double g, esl;
        esl = eccentricity * sinLat;
        g   = pow((1 - esl) / (1 + esl), (alfa * eccentricity) / 2);
        k1  = pow(tan(latitudeOfOrigin/2 + PI/4), alfa) * g / tan(u0/2 + PI/4);
        ka  = pow(1/k1, -1/alfa);
        ro0 = pow(tanS2, -n);
        final double radius = sqrt(1 - eccentricitySquared) / (1 - (eccentricitySquared * (sinLat*sinLat))); // TODO: radiusOfConformanceSphere.
        final double rop = radius / (ro0 * tan(pseudoStandardParallel));
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        getContextualParameters().getMatrix(MatrixRole.NORMALIZATION).convertAfter(0, -alfa, null);
        getContextualParameters().getMatrix(MatrixRole.DENORMALIZATION).convertBefore(0, -rop, null);
        getContextualParameters().getMatrix(MatrixRole.DENORMALIZATION).convertBefore(1, -rop, null);
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
        final double Δv    = srcPts[srcOff];
        final double φ     = srcPts[srcOff+1];
        final double sinΔv = sin(Δv);
        final double cosΔv = cos(Δv);
        final double esinφ = eccentricity * sin(φ);
        final double tan1  = tan(φ/2 + PI/4);
        final double gφ    = pow((1-esinφ) / (1+esinφ), hae);
        final double V     = pow(tan1, alfa) / k1 * gφ;
        final double U     = 2 * (atan(V) - PI/4);
        final double sinU  = sin(U);
        final double cosU  = cos(U);
        final double sinS  = cosAzim*sinU + sinAzim*cosU*cosΔv;
        final double cosS  = sqrt(1 - sinS*sinS);
        final double S     = asin(sinS);
        final double Ɛt    = cosU*sinΔv / cosS;
        final double Ɛ     = n * asin(Ɛt);
        final double cosƐ  = cos(Ɛ);
        final double sinƐ  = sin(Ɛ);
        final double tan2  = tan(S/2 + PI/4);
        final double ρ     = pow(tan2, n);
        if (dstPts != null) {
            // x and y are reverted.
            dstPts[dstOff  ] = sinƐ / ρ;
            dstPts[dstOff+1] = cosƐ / ρ;
        }
        if (!derivate) {
            return null;
        }
        //
        // End of map projection. Now compute the derivative.
        //
        final double dgφ    = eccentricitySquared*cos(φ) / (1 - esinφ*esinφ);
        final double dU_dφ  = alfa * (1/tan1 + tan1 - 2*dgφ) / (1/V + V);
        final double dS_dλ  = (-sinAzim*cosU*sinΔv) / cosS;
        final double dS_dφ  = (-sinAzim*sinU*cosΔv + cosAzim*cosU)*dU_dφ / cosS;
        final double t      = n / (cosS*cosS*sqrt(1 - Ɛt*Ɛt));
        final double dƐ_dλ  = cosU  * t * (dS_dλ*sinΔv*sinS +       cosΔv*cosS);
        final double dƐ_dφ  = sinΔv * t * (dS_dφ*sinS*cosU - dU_dφ*sinU*cosS);
        final double m      = (-n/2) * (1/tan2 + tan2);
        return new Matrix2(
                (m*dS_dλ * sinƐ + dƐ_dλ * cosƐ) / ρ,
                (m*dS_dφ * sinƐ + dƐ_dφ * cosƐ) / ρ,
                (m*dS_dλ * cosƐ - dƐ_dλ * sinƐ) / ρ,
                (m*dS_dφ * cosƐ - dƐ_dφ * sinƐ) / ρ);
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
            final double esf = eccentricity * sin(φ1);
            φ = 2 * (atan(kau * pow((1 + esf) / (1 - esf), eccentricity/2)) - PI/4);
            if (abs(φ1 - φ) <= ITERATION_TOLERANCE) {
                break;
            }
            if (--i < 0) {
                throw new ProjectionException(Errors.format(Errors.Keys.NoConvergence));
            }
        }
        dstPts[dstOff  ] = Δv;
        dstPts[dstOff+1] = φ;
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
