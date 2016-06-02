/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.EnumMap;
import java.awt.geom.AffineTransform;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.Matrix;
import org.apache.sis.measure.Angle;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.matrix.Matrix2;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.projection.ProjectionException;
import org.geotoolkit.referencing.operation.provider.HotineObliqueMercator;

import org.apache.sis.referencing.operation.transform.ContextualParameters.MatrixRole;
import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.apache.sis.math.MathFunctions.atanh;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;


/**
 * <cite>Oblique Mercator</cite> projection (EPSG codes 9812, 9815). See the
 * <A HREF="http://mathworld.wolfram.com/MercatorProjection.html">Mercator projection on MathWorld</A>
 * for an overview. See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.ObliqueMercator}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.ObliqueMercator.TwoPoint}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.HotineObliqueMercator}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.HotineObliqueMercator.TwoPoint}</li>
 * </ul>
 *
 * {@section Description}
 *
 * The Oblique Mercator projection is a conformal, oblique, cylindrical projection with
 * the cylinder touching the ellipsoid (or sphere) along a great circle path (the central line).
 * The {@linkplain Mercator} and {@linkplain TransverseMercator Transverse Mercator} projections
 * can be thought of as special cases of the oblique Mercator, where the central line is along the
 * equator or a meridian, respectively. The Oblique Mercator projection has been used in
 * Switzerland, Hungary, Madagascar, Malaysia, Borneo and the panhandle of Alaska.
 * <p>
 * The Oblique Mercator projection uses a (<var>U</var>,<var>V</var>) coordinate system, with the
 * <var>U</var> axis along the central line. During the forward projection, coordinates from the
 * ellipsoid are projected conformally to a sphere of constant total curvature, called the
 * "aposphere", before being projected onto the plane. The projection coordinates are further
 * converted to a (<var>X</var>,<var>Y</var>) coordinate system by rotating the calculated
 * (<var>u</var>,<var>v</var>) coordinates to give output (<var>x</var>,<var>y</var>) coordinates.
 * The rotation value is usually the same as the projection azimuth (the angle, east of north, of
 * the central line), but some cases allow a separate rotation parameter.
 * <p>
 * There are two forms of the oblique Mercator, differing in the origin of their grid coordinates.
 * The <cite>Hotine Oblique Mercator</cite> (EPSG code 9812) has grid coordinates start at the
 * intersection of the central line and the equator of the aposphere. The <cite>Oblique Mercator</cite>
 * (EPSG code 9815) is the same, except the grid coordinates begin at the central point (where the
 * latitude of center and central line intersect). ESRI separates these two case by appending
 * {@code "Natural_Origin"} (for the {@code "Hotine_Oblique_Mercator"}) and {@code "Center"}
 * (for the {@code "Oblique_Mercator"}) to the projection names.
 * <p>
 * Two different methods are used to specify the central line for the oblique Mercator:
 * 1) a central point and an azimuth, east of north, describing the central line and
 * 2) two points on the central line. The EPSG does not use the two point method, while ESRI
 * separates the two cases by putting {@code "Azimuth"} and {@code "Two_Point"} in their projection
 * names. Both cases use the point where the {@code "latitude_of_center"} parameter crosses the
 * central line as the projection's central point. The {@code "central_meridian"} is not a
 * projection parameter, and is instead calculated as the intersection between the central
 * line and the equator of the aposphere.
 * <p>
 * For the azimuth method, the central latitude cannot be &plusmn;90 degrees and the central line
 * cannot be at a maximum or minimum latitude at the central point. In the two point method, the
 * latitude of the first and second points cannot be equal. Also, the latitude of the first point
 * and central point cannot be &plusmn;90 degrees. Furthermore, the latitude of the first point
 * cannot be 0 and the latitude of the second point cannot be -90 degrees. A change of 10<sup>-7</sup>
 * radians can allow calculation at these special cases. Snyder's restriction of the central latitude
 * being 0 has been removed, since the equations appear to work correctly in this case.
 * <p>
 * Azimuth values of 0 and &plusmn;90 degrees are allowed (and used in Hungary and Switzerland),
 * though these cases would usually use a Mercator or Transverse Mercator projection instead.
 * Azimuth values &gt; 90 degrees cause errors in the equations.
 * <p>
 * The oblique Mercator is also called the "<cite>Rectified Skew Orthomorphic</cite>" (RSO). It
 * appears that the only difference from the oblique Mercator is that the RSO allows the rotation
 * from the (<var>U</var>,<var>V</var>) to (<var>X</var>,<var>Y</var>) coordinate system to be
 * different from the azimuth. This separate parameter is called {@code "rectified_grid_angle"}
 * (or {@code "XY_Plane_Rotation"} by ESRI) and is also included in the EPSG's parameters for the
 * Oblique Mercator and Hotine Oblique Mercator. The rotation parameter is optional in all the
 * non-two point projections and will be set to the azimuth if not specified.
 * <p>
 * Projection cases and aliases implemented by the {@link ObliqueMercator} are:
 * <table border="1">
 *   <tr><th>Name</th><th>Authority</th><th>Remarks</th></tr>
 *   <tr><td>{@code Oblique_Mercator}</td><td>EPSG:9815</td>
 *       <td>grid coordinates begin at the central point,
 *           has {@code "rectified_grid_angle"} parameter.</td></tr>
 *   <tr><td>{@code Hotine_Oblique_Mercator_Azimuth_Center}</td><td>ESRI</td>
 *       <td>grid coordinates begin at the central point.</td></tr>
 *   <tr><td>{@code Rectified_Skew_Orthomorphic_Center}</td><td>ESRI</td>
 *       <td>grid coordinates begin at the central point,
 *           has {@code "rectified_grid_angle"} parameter.</td></tr>
 *   <tr><td>{@code Hotine_Oblique_Mercator}</td><td>EPSG:9812</td>
 *       <td>grid coordinates begin at the intersection of the central line and aposphere equator,
 *           has {@code "rectified_grid_angle"} parameter.</td></tr>
 *   <tr><td>{@code Hotine_Oblique_Mercator_Azimuth_Natural_Origin}</td><td>ESRI</td>
 *       <td>grid coordinates begin at the intersection of the central line and aposphere equator.</li>
 *   <tr><td>{@code Rectified_Skew_Orthomorphic_Natural_Origin}</td><td>ESRI</td>
 *       <td>grid coordinates begin at the intersection of the central line and aposphere equator,
 *           has {@code "rectified_grid_angle"} parameter.</td></tr>
 *   <tr><td>{@code Hotine_Oblique_Mercator_Two_Point_Center}</td><td>ESRI</td>
 *       <td>grid coordinates begin at the central point.</td></tr>
 *   <tr><td>{@code Hotine_Oblique_Mercator_Two_Point_Natural_Origin}</td><td>ESRI</td>
 *       <td>grid coordinates begin at the intersection of the central line and aposphere equator.</td></tr>
 * </table>
 *
 * {@section References}
 * <ul>
 *   <li>Proj-4 available at <A HREF="http://www.remotesensing.org/proj">www.remotesensing.org/proj</A><br>
 *       Relevant files are: {@code PJ_omerc.c}, {@code pj_tsfn.c},
 *       {@code pj_fwd.c}, {@code pj_inv.c} and {@code lib_proj.h}</li>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidance Note Number 7 part 2, Version 24.</li>
 *   <li>Gerald Evenden, 2004, <a href="http://members.verizon.net/~vze2hc4d/proj4/omerc.pdf">
 *       Documentation of revised Oblique Mercator</a></li>
 * </ul>
 *
 * @author Gerald Evenden (USGS)
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @see Mercator
 * @see TransverseMercator
 *
 * @since 2.1
 * @module
 */
public class ObliqueMercator extends UnitaryProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 5382294977124711214L;

    /**
     * Maximum difference allowed when comparing real numbers.
     */
    private static final double EPSILON = 1E-6, FINER_EPSILON = 1E-10;

    private static Map<ParameterRole, ParameterDescriptor<Double>> roles() {
        final EnumMap<ParameterRole, ParameterDescriptor<Double>> roles = new EnumMap<>(ParameterRole.class);
//      Central meridian intentionally excluded. Will be handled in the constructor.
//      roles.put(ParameterRole.CENTRAL_MERIDIAN, org.geotoolkit.referencing.operation.provider.ObliqueMercator.LONGITUDE_OF_CENTRE);
        roles.put(ParameterRole.SCALE_FACTOR,     org.geotoolkit.referencing.operation.provider.ObliqueMercator.SCALE_FACTOR);
        roles.put(ParameterRole.FALSE_EASTING,    org.geotoolkit.referencing.operation.provider.ObliqueMercator.FALSE_EASTING);
        roles.put(ParameterRole.FALSE_NORTHING,   org.geotoolkit.referencing.operation.provider.ObliqueMercator.FALSE_NORTHING);
        return roles;
    }

    /**
     * Constants used in the transformation. Those coefficients
     * depend only on {@link Parameters#latitudeOfCentre}.
     */
    private final double B, E;

    /**
     * <var>v</var> values when the input latitude is a pole. Those values are derived
     * from {@code gamma0} only, so they don't need to be compared in the {@code equals}
     * method if {@code singamma0} and {@code cosgamma0} are compared.
     */
    private final double v_pole_n, v_pole_s;

    /**
     * Sine and Cosine values for gamma0 (the angle between the meridian
     * and central line at the intersection between the central line and
     * the Earth equator on aposphere).
     */
    private final double singamma0, cosgamma0;

    /**
     * Creates an Oblique Mercator projection from the given parameters.
     * The descriptor argument is usually the {@code PARAMETERS} constant defined in
     * {@link org.geotoolkit.referencing.operation.provider.ObliqueMercator} or a subclass,
     * but is not restricted to. If a different descriptor is supplied, it is user's
     * responsibility to ensure that it is suitable to an Oblique Mercator projection.
     *
     * @param  descriptor Typically
     *         {@link org.geotoolkit.referencing.operation.provider.ObliqueMercator#PARAMETERS},
     *         or the parameters from one of its subclasses.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = Parameters.castOrWrap(values);
        final ObliqueMercator projection = new ObliqueMercator(descriptor, parameters);
        try {
            return (MathTransform2D) projection.createMapProjection(
                    org.apache.sis.internal.system.DefaultFactories.forBuildin(
                            org.opengis.referencing.operation.MathTransformFactory.class));
        } catch (org.opengis.util.FactoryException e) {
            throw new IllegalArgumentException(e); // TODO
        }
    }

    /**
     * Infers from the parameter values if the azimuth is defined by two points on the central
     * line.
     *
     * @return {@code true} if using two points on the central line to specify the azimuth.
     * @throws IllegalArgumentException if the parameter values appear to be a mix of the
     *         "azimuth" and the "two points" cases.
     */
//    private static boolean usesTwoPoints() throws IllegalArgumentException {
//        final boolean isTwoPoints = isNaN(azimuth) && isNaN(rectifiedGridAngle);
//        final String problem;
//             if (isTwoPoints == isNaN(latitudeOf1stPoint))  problem = "latitudeOf1stPoint";
//        else if (isTwoPoints == isNaN(longitudeOf1stPoint)) problem = "longitudeOf1stPoint";
//        else if (isTwoPoints == isNaN(latitudeOf2ndPoint))  problem = "latitudeOf2ndPoint";
//        else if (isTwoPoints == isNaN(longitudeOf2ndPoint)) problem = "longitudeOf2ndPoint";
//        else {
//            return isTwoPoints;
//        }
//        if (isTwoPoints) {
//            throw new ParameterNotFoundException(Errors.format(
//                    Errors.Keys.NoParameter_1, problem), problem);
//        } else {
//            throw new InvalidParameterNameException(Errors.format(
//                    Errors.Keys.UnexpectedParameter_1, problem), problem);
//        }
//    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected ObliqueMercator(final OperationMethod method, final Parameters parameters) {
        super(method, parameters, roles());
        // Note for SIS: parameter descriptor for "central_meridian" shall be null.

        double latitudeOfCentre   = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.ObliqueMercator.LATITUDE_OF_CENTRE));
        double longitudeOfCentre  = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.ObliqueMercator.LONGITUDE_OF_CENTRE));
        double rectifiedGridAngle = Double.NaN;
        double azimuth            = Double.NaN;
        try {
            rectifiedGridAngle = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.ObliqueMercator.RECTIFIED_GRID_ANGLE));
        } catch (IllegalStateException e) {
            // TODO: we need a better mechanism for detecting if those parameters were provided.
        }
        try {
            azimuth = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.ObliqueMercator.AZIMUTH));
        } catch (IllegalStateException e) {
            // TODO: we need a better mechanism for detecting if those parameters were provided.
        }
        boolean isTwoPoints = isNaN(azimuth) && isNaN(rectifiedGridAngle);
        /*
         * Computes some common constants. All those constants
         * depend only on the latitude of centre, in radians.
         */
        final double com   = sqrt(1 - eccentricitySquared);  // TODO: == b
        final double sinφ0 = sin(latitudeOfCentre);
        final double cosφ0 = cos(latitudeOfCentre);
        double t = cosφ0 * cosφ0; // t is used as a temporary variable.
        B = sqrt(1 + eccentricitySquared * (t*t) / (1 - eccentricitySquared));
        final double con = 1 - eccentricitySquared * (sinφ0 * sinφ0);
        final double A = B * com / con;
        final double D = B * com / (cosφ0 * sqrt(con));
        double F = D*D - 1;
        if (F < 0) {
            F = 0;
        } else {
            F = copySign(sqrt(F), latitudeOfCentre);
        }
        F += D;
        E = F * pow(tsfn(latitudeOfCentre, sinφ0), B);
        /*
         * Computes the constants that depend on the "twoPoint" vs "azimuth" case. In the
         * two points case, we compute them from (LAT_OF_1ST_POINT, LONG_OF_1ST_POINT) and
         * (LAT_OF_2ND_POINT, LONG_OF_2ND_POINT).  For the "azimuth" case, we compute them
         * from LONGITUDE_OF_CENTRE and AZIMUTH.
         *
         * All angles that are stored in local variables are in radians. Some of them will
         * be copied back to the parameters object, where angles are in decimal degrees.
         */
        final double centralMeridian, gamma0;
        if (isTwoPoints) {
            final double latitudeOf1stPoint  = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.UniversalParameters.LAT_OF_1ST_POINT));
            final double latitudeOf2ndPoint  = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.UniversalParameters.LAT_OF_2ND_POINT));
            final double longitudeOf1stPoint = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.UniversalParameters.LONG_OF_1ST_POINT));
                  double longitudeOf2ndPoint = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.UniversalParameters.LONG_OF_2ND_POINT));
            final double H  = pow(tsfn(latitudeOf1stPoint, sin(latitudeOf1stPoint)), B);
            final double L  = pow(tsfn(latitudeOf2ndPoint, sin(latitudeOf2ndPoint)), B);
            final double Fp = E / H;
            final double P  = (L - H) / (L + H);
            double J = E * E;
            J = (J - L*H) / (J + L*H);
            /*
             * Computes the new value for the central meridian (in radians). Result
             * will be copied in the parameters object after this "if/else" block.
             */
            double diff = longitudeOf1stPoint - longitudeOf2ndPoint;
            if (abs(diff) > PI) {
                longitudeOf2ndPoint += copySign(2*Math.PI, diff);
                diff = longitudeOf1stPoint - longitudeOf2ndPoint;
            }
            t  = 0.5*(longitudeOf1stPoint + longitudeOf2ndPoint);
            t -= atan(J * tan(0.5*B*diff)/P) / B;
            centralMeridian = org.geotoolkit.math.XMath.roll(t, PI);
            /*
             * Computes the rectified grid angle (in radians).
             * Copies the value to the parameters object (in degrees).
             */
            diff = org.geotoolkit.math.XMath.roll(longitudeOf1stPoint - centralMeridian, PI);
            gamma0 = atan(2 * sin(B * diff) / (Fp - 1/Fp));
            azimuth = rectifiedGridAngle = asin(D * sin(gamma0));
            getContextualParameters().parameter(org.geotoolkit.referencing.operation.provider.ObliqueMercator.AZIMUTH.getName().getCode())
                    .setValue(toDegrees(azimuth));
            getContextualParameters().parameter(org.geotoolkit.referencing.operation.provider.ObliqueMercator.RECTIFIED_GRID_ANGLE.getName().getCode())
                    .setValue(toDegrees(rectifiedGridAngle));
        } else {
            /*
             * Computes coefficients for the "azimuth" case. The 1st and 2nd points are
             * expected to be (NaN,NaN) since they are specific to the "two points" case.
             * This was verified by the call to "parameters.usesTwoPoints()" above.
             */
            if ((azimuth > -1.5*PI && azimuth < -0.5*PI) ||
                (azimuth >  0.5*PI && azimuth <  1.5*PI))
            {
                final String name = org.geotoolkit.referencing.operation.provider.ObliqueMercator.AZIMUTH.getName().getCode();
                final Angle value = new Angle(toDegrees(azimuth));
                throw new InvalidParameterValueException(Errors.format(
                        Errors.Keys.IllegalArgument_2, name, value), name, value);
            }
            if (isNaN(rectifiedGridAngle)) {
                rectifiedGridAngle = azimuth;
                getContextualParameters().parameter(org.geotoolkit.referencing.operation.provider.ObliqueMercator.RECTIFIED_GRID_ANGLE.getName().getCode())
                        .setValue(toDegrees(rectifiedGridAngle));
            }
            gamma0 = asin(sin(azimuth) / D);
            t = 0.5 * (F - 1/F) * tan(gamma0);
            if (abs(t) > 1) { // Check for asin(±1.00000001)
                if (abs(abs(t) - 1) > EPSILON) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.ToleranceError));
                }
                t = copySign(1, t);
            }
            centralMeridian = longitudeOfCentre - asin(t) / B;
        }
        singamma0 = sin(gamma0);
        cosgamma0 = cos(gamma0);
        v_pole_n  = log(tan(0.5 * (PI/2 - gamma0)));
        v_pole_s  = log(tan(0.5 * (PI/2 + gamma0)));
        final double ArB = A / B;
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        getContextualParameters().getMatrix(MatrixRole.NORMALIZATION).convertAfter(0, null, -centralMeridian);
        final MatrixSIS denormalize = getContextualParameters().getMatrix(MatrixRole.DENORMALIZATION);
        if (rectifiedGridAngle != 0) {
            // TODO: This complicated code is only a workaround for the absence of "rotate" method in MatrixSIS.
            // We should provide a "rotate" method in a future SIS version instead.
            final AffineTransform tmp = AffineTransforms2D.castOrCopy(denormalize);
            tmp.rotate(-rectifiedGridAngle);
            final Matrix m = AffineTransforms2D.toMatrix(tmp);
            for (int i=0; i<3; i++) {
                for (int j=0; j<3; j++) {
                    denormalize.setElement(j, i, m.getElement(j, i));
                }
            }
        }
        if (!nameMatches(parameters, HotineObliqueMercator.PARAMETERS) &&
            !nameMatches(parameters, HotineObliqueMercator.TwoPoint.PARAMETERS))
        {
            final double u_c;
            if (abs(abs(azimuth) - PI/2) < FINER_EPSILON) {
                // LongitudeOfCentre = NaN in twoPoint, but azimuth cannot be 90 here (lat1 != lat2)
                u_c = A * (longitudeOfCentre - centralMeridian);
            } else {
                u_c = copySign(ArB * atan2(sqrt(D*D - 1), cos(azimuth)), latitudeOfCentre);
            }
            denormalize.convertBefore(1, null, -u_c);
        }
        denormalize.convertBefore(0, ArB, null);
        denormalize.convertBefore(1, ArB, null);
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
        final double λ = srcPts[srcOff];
        final double φ = srcPts[srcOff + 1];
        final double x, y;
        Matrix derivative = null;
        if (abs(abs(φ) - PI/2) > ANGLE_TOLERANCE) {
            final double sinφ = sin(φ);
            final double Q    = E / pow(tsfn(φ, sinφ), B);
            final double iQ   = 1 / Q;
            final double S    = 0.5 * (Q - iQ);
            final double Sp   = 0.5 * (Q + iQ);
            final double V    = sin(B * λ);
            final double U    = (S*singamma0 - V*cosgamma0) / Sp;
            if (abs(abs(U) - 1) < EPSILON) {
                throw new ProjectionException(Errors.format(Errors.Keys.InfiniteValue_1, "v"));
            }
            final double dV_dλ = cos(B * λ);
            if (abs(dV_dλ) < FINER_EPSILON) {
                y = λ * (B*B);
            } else {
                y = atan2((S * cosgamma0 + V * singamma0), dV_dλ);
            }
            x = atanh(-U); // = 0.5 * log((1-U) / (1+U));
            if (derivate) {
                final double m10, m11;
                final double dQ_dφ = -B * Q * dtsfn_dφ(φ, sinφ, cos(φ));
                final double dU_dλ = -B * (cosgamma0 / Sp) * dV_dλ;
                final double dU_dφ = dQ_dφ * (singamma0 + (singamma0 + U)/(Q*Q) - U) / (2*Sp);
                if (abs(dV_dλ) < FINER_EPSILON) {
                    m10 = B*B; // y = λ * (B*B);
                    m11 = 0;
                } else {
                    final double dS_dφ = 0.5*dQ_dφ * (1 + 1/(Q*Q));
                    final double M = (S*cosgamma0 + V*singamma0);
                    final double L = hypot(dV_dλ, M);
                    final double P = L + dV_dλ;
                    final double D = (P*P + M*M);
                    m10 = 2 * B * (dV_dλ * (singamma0*P + (V - singamma0*M) * M/L) + V*M) / D; // Y = atan2(M, T)
                    m11 = 2 * cosgamma0 * dS_dφ * (P - M*M/L) / D;
                }
                final double R = U*U - 1;
                derivative = new Matrix2(
                        dU_dλ / R,  // x = 0.5 * log((1-U) / (1+U))
                        dU_dφ / R,
                        m10, m11);
            }
        } else {
            x = (φ > 0 ? v_pole_n : v_pole_s);
            y = φ;
            if (derivate) {
                derivative = new Matrix2(0, 0, 0, 1);
            }
        }
        if (dstPts != null) {
            dstPts[dstOff]   = x;
            dstPts[dstOff+1] = y;
        }
        return derivative;
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
        final double x  = srcPts[srcOff  ];
        final double y  = srcPts[srcOff+1];
        final double Qp = exp(-x);
        final double t  = 1 / Qp;
        final double Sp = 0.5 * (Qp - t);
        final double Vp = sin(y);
        final double Up = (Vp * cosgamma0 + Sp * singamma0) / (0.5 * (Qp + t));
        double λ, φ;
        if (abs(abs(Up) - 1) < EPSILON) {
            λ = 0.0;
            φ = copySign(PI/2, Up);
        } else {
            λ = -atan2((Sp*cosgamma0 - Vp*singamma0), cos(y)) / B;
            φ = cphi2(pow(E / sqrt((1 + Up) / (1 - Up)), 1/B));
        }
        dstPts[dstOff  ] = λ;
        dstPts[dstOff+1] = φ;
    }

    /**
     * Compares the given object with this transform for equivalence.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            final ObliqueMercator that = (ObliqueMercator) object;
            return epsilonEqual(this.B, that.B, mode) &&
                   epsilonEqual(this.E, that.E, mode) &&
                   epsilonEqual(this.singamma0, that.singamma0, mode) &&
                   epsilonEqual(this.cosgamma0, that.cosgamma0, mode);
        }
        return false;
    }
}
