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

import java.util.Collection;
import java.awt.geom.AffineTransform;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.operation.Matrix;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterNameException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.measure.Angle;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.referencing.operation.matrix.Matrix2;
import org.geotoolkit.referencing.operation.provider.HotineObliqueMercator;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.apache.sis.math.MathFunctions.atanh;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;
import static org.geotoolkit.referencing.operation.provider.ObliqueMercator.LATITUDE_OF_CENTRE;
import static org.geotoolkit.referencing.operation.provider.ObliqueMercator.LONGITUDE_OF_CENTRE;


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
 *   <td><th>Name</th><th>Authority</th><th>Remarks</th></tr>
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
 * @version 3.20
 *
 * @see Mercator
 * @see TransverseMercator
 *
 * @since 2.1
 * @module
 */
@Immutable
public class ObliqueMercator extends UnitaryProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 5382294977124711214L;

    /**
     * Maximum difference allowed when comparing real numbers.
     */
    private static final double EPSILON = 1E-6, FINER_EPSILON = 1E-10;

    /**
     * Parameters used in Oblique Mercator projections. Those parameters determine the rotation
     * in addition to the scale and translation determined by the parameters declared in the
     * super-class.
     *
     * @author Rueben Schulz (UBC)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    protected static class Parameters extends UnitaryProjection.Parameters {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -5356116159749775517L;

        /**
         * Latitude of the projection centre. This is similar to the {@link #latitudeOfOrigin
         * latitudeOfOrigin}, but the latitude of origin is the Earth equator on aposphere for
         * the oblique Mercator.
         */
        public double latitudeOfCentre;

        /**
         * Longitude of the projection centre. This is <strong>NOT</strong> equal to the
         * {@link #centralMeridian centralMeridian}, which is the meridian where the central
         * line intersects the Earth equator on aposphere.
         * <p>
         * This parameter applies to the "azimuth" case only and shall be set to
         * {@linkplain Double#NaN NaN} for the "two points" case.
         */
        public double longitudeOfCentre;

        /**
         * The rectified bearing of the central line, in degrees. This is set to {@linkplain Double#NaN NaN}
         * if the {@link org.geotoolkit.referencing.operation.provider.ObliqueMercator#RECTIFIED_GRID_ANGLE
         * RECTIFIED_GRID_ANGLE} parameter value is not provided.
         */
        public double rectifiedGridAngle;

        /**
         * The latitude of the 1st point used to specify the central line, in degrees.
         * This parameter applies to the "two points" case only and shell be set to
         * {@linkplain Double#NaN NaN} for the "azimuth" case.
         */
        public double latitudeOf1stPoint;

        /**
         * The longitude of the 1st point used to specify the central line, in degrees.
         * This parameter applies to the "two points" case only and shall be set to
         * {@linkplain Double#NaN NaN} for the "azimuth" case.
         */
        public double longitudeOf1stPoint;

        /**
         * The latitude of the 2nd point used to specify the central line, in degrees.
         * This parameter applies to the "two points" case only and shall be set to
         * {@linkplain Double#NaN NaN} for the "azimuth" case.
         */
        public double latitudeOf2ndPoint;

        /**
         * The longitude of the 2nd point used to specify the central line, in radians.
         * This parameter applies to the "two points" case only and shall be set to
         * {@linkplain Double#NaN NaN} for the "azimuth" case.
         */
        public double longitudeOf2ndPoint;

        /**
         * Creates parameters initialized to values extracted from the given parameter group.
         *
         * @param  descriptor The descriptor of parameters that are legal for the projection being
         *         constructed.
         * @param  values The parameter values in standard units.
         * @throws ParameterNotFoundException if a mandatory parameter is missing.
         */
        public Parameters(final ParameterDescriptorGroup descriptor,
                          final ParameterValueGroup values) throws ParameterNotFoundException
        {
            super(descriptor, values);
            final Collection<GeneralParameterDescriptor> expected = descriptor.descriptors();
            latitudeOfCentre    = latitudeOfOrigin;
            longitudeOfCentre   = centralMeridian;
            rectifiedGridAngle  = doubleValue(expected, RECTIFIED_GRID_ANGLE, values);
            latitudeOf1stPoint  = doubleValue(expected, LAT_OF_1ST_POINT,     values);
            longitudeOf1stPoint = doubleValue(expected, LONG_OF_1ST_POINT,    values);
            latitudeOf2ndPoint  = doubleValue(expected, LAT_OF_2ND_POINT,     values);
            longitudeOf2ndPoint = doubleValue(expected, LONG_OF_2ND_POINT,    values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ParameterValueGroup getParameterValues() {
            final ParameterValueGroup values = super.getParameterValues();
            final ParameterDescriptorGroup descriptor = getParameterDescriptors();
            final Collection<GeneralParameterDescriptor> expected = descriptor.descriptors();
            // Note: we don't need a "if (twoPoint) ... else" statement since
            // the "set" method will actually set the value only if applicable.
            set(expected, RECTIFIED_GRID_ANGLE, values, rectifiedGridAngle);
            set(expected, LAT_OF_1ST_POINT,     values, latitudeOf1stPoint);
            set(expected, LONG_OF_1ST_POINT,    values, longitudeOf1stPoint);
            set(expected, LAT_OF_2ND_POINT,     values, latitudeOf2ndPoint);
            set(expected, LONG_OF_2ND_POINT,    values, longitudeOf2ndPoint);
            return values;
        }

        /**
         * Infers from the parameter values if the azimuth is defined by two points on the central
         * line.
         *
         * @return {@code true} if using two points on the central line to specify the azimuth.
         * @throws IllegalArgumentException if the parameter values appear to be a mix of the
         *         "azimuth" and the "two points" cases.
         */
        public boolean usesTwoPoints() throws IllegalArgumentException {
            final boolean isTwoPoints = isNaN(azimuth) && isNaN(rectifiedGridAngle);
            final String problem;
                 if (isTwoPoints == isNaN(latitudeOf1stPoint))  problem = "latitudeOf1stPoint";
            else if (isTwoPoints == isNaN(longitudeOf1stPoint)) problem = "longitudeOf1stPoint";
            else if (isTwoPoints == isNaN(latitudeOf2ndPoint))  problem = "latitudeOf2ndPoint";
            else if (isTwoPoints == isNaN(longitudeOf2ndPoint)) problem = "longitudeOf2ndPoint";
            else {
                return isTwoPoints;
            }
            if (isTwoPoints) {
                throw new ParameterNotFoundException(Errors.format(
                        Errors.Keys.NO_PARAMETER_1, problem), problem);
            } else {
                throw new InvalidParameterNameException(Errors.format(
                        Errors.Keys.UNEXPECTED_PARAMETER_1, problem), problem);
            }
        }

        /**
         * Ensures that the arguments are valid. This method it invoked before {@link #validate()};
         * it does not yet initialize the normalize/denormalize affine transforms.
         *
         * @param  isTwoPoints {@code true} for the "two points" case,
         *         or {@code false} for the "azimuth" case.
         * @throws IllegalArgumentException if a field has an illegal value.
         */
        final void validate(final boolean isTwoPoints) throws IllegalArgumentException {
            /*
             * Checks that 'latitudeOfCentre' is not +- 90 degrees.
             * Not checking if 'latitudeOfCentere' is 0, since equations behave correctly.
             */
            ensureLatitudeInRange (LATITUDE_OF_CENTRE,  latitudeOfCentre, false);
            ensureLongitudeInRange(LONGITUDE_OF_CENTRE, longitudeOfCentre, true);
            if (!isTwoPoints) {
                return;
            }
            ensureLatitudeInRange (LAT_OF_1ST_POINT,  latitudeOf1stPoint, false);
            ensureLongitudeInRange(LONG_OF_1ST_POINT, longitudeOf1stPoint, true);
            ensureLatitudeInRange (LAT_OF_2ND_POINT,  latitudeOf2ndPoint,  true);
            ensureLongitudeInRange(LONG_OF_2ND_POINT, longitudeOf2ndPoint, true);
            /*
             * Ensures that (phi1 != phi2), (phi1 != 0°) and (phi2 != -90°),
             * as specified in class javadoc.
             */
            final ParameterDescriptor<Double> desc;
            final Object value;
            if (abs(latitudeOf1stPoint - latitudeOf2ndPoint) < FINER_EPSILON) {
                desc  = LAT_OF_1ST_POINT;
                value = LAT_OF_2ND_POINT.getName().getCode();
            } else if (abs(latitudeOf1stPoint) < FINER_EPSILON) {
                desc  = LAT_OF_1ST_POINT;
                value = new Latitude(latitudeOf1stPoint);
            } else if (abs(latitudeOf2ndPoint + 90) < FINER_EPSILON) {
                desc  = LAT_OF_2ND_POINT;
                value = new Latitude(latitudeOf2ndPoint);
            } else {
                return;
            }
            final String name = desc.getName().getCode();
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, name, value), name, value);
        }
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
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = new Parameters(descriptor, values);
        final ObliqueMercator projection = new ObliqueMercator(parameters);
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected ObliqueMercator(final Parameters parameters) {
        super(parameters);
        final boolean isTwoPoints = parameters.usesTwoPoints();
        parameters.validate(isTwoPoints);
        /*
         * Computes some common constants. All those constants
         * depend only on the latitude of centre, in radians.
         */
        final double latitudeOfCentre = toRadians(parameters.latitudeOfCentre);
        final double com   = sqrt(1 - excentricitySquared);
        final double sinφ0 = sin(latitudeOfCentre);
        final double cosφ0 = cos(latitudeOfCentre);
        double t = cosφ0 * cosφ0; // t is used as a temporary variable.
        B = sqrt(1 + excentricitySquared * (t*t) / (1 - excentricitySquared));
        final double con = 1 - excentricitySquared * (sinφ0 * sinφ0);
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
        final double longitudeOfCentre = toRadians(parameters.longitudeOfCentre);
        final double centralMeridian, azimuth, rectifiedGridAngle, gamma0;
        if (isTwoPoints) {
            final double latitudeOf1stPoint  = toRadians(parameters.latitudeOf1stPoint);
            final double latitudeOf2ndPoint  = toRadians(parameters.latitudeOf2ndPoint);
            final double longitudeOf1stPoint = toRadians(parameters.longitudeOf1stPoint);
                  double longitudeOf2ndPoint = toRadians(parameters.longitudeOf2ndPoint);
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
                longitudeOf2ndPoint = toRadians(parameters.longitudeOf2ndPoint += copySign(360, diff));
                diff = longitudeOf1stPoint - longitudeOf2ndPoint;
            }
            t  = 0.5*(longitudeOf1stPoint + longitudeOf2ndPoint);
            t -= atan(J * tan(0.5*B*diff)/P) / B;
            centralMeridian = rollLongitude(t, PI);
            /*
             * Computes the rectified grid angle (in radians).
             * Copies the value to the parameters object (in degrees).
             */
            diff = rollLongitude(longitudeOf1stPoint - centralMeridian, PI);
            gamma0 = atan(2 * sin(B * diff) / (Fp - 1/Fp));
            azimuth = rectifiedGridAngle = asin(D * sin(gamma0));
            parameters.azimuth = parameters.rectifiedGridAngle = toDegrees(rectifiedGridAngle);
        } else {
            /*
             * Computes coefficients for the "azimuth" case. The 1st and 2nd points are
             * expected to be (NaN,NaN) since they are specific to the "two points" case.
             * This was verified by the call to "parameters.usesTwoPoints()" above.
             */
            azimuth = toRadians(parameters.azimuth);
            if ((azimuth > -1.5*PI && azimuth < -0.5*PI) ||
                (azimuth >  0.5*PI && azimuth <  1.5*PI))
            {
                final String name = AZIMUTH.getName().getCode();
                final Angle value = new Angle(parameters.azimuth);
                throw new InvalidParameterValueException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, name, value), name, value);
            }
            if (isNaN(parameters.rectifiedGridAngle)) {
                parameters.rectifiedGridAngle = parameters.azimuth;
            }
            rectifiedGridAngle = toRadians(parameters.rectifiedGridAngle);
            gamma0 = asin(sin(azimuth) / D);
            t = 0.5 * (F - 1/F) * tan(gamma0);
            if (abs(t) > 1) { // Check for asin(±1.00000001)
                if (abs(abs(t) - 1) > EPSILON) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.TOLERANCE_ERROR));
                }
                t = copySign(1, t);
            }
            centralMeridian = longitudeOfCentre - asin(t) / B;
        }
        parameters.centralMeridian = toDegrees(centralMeridian);
        singamma0 = sin(gamma0);
        cosgamma0 = cos(gamma0);
        v_pole_n  = log(tan(0.5 * (PI/2 - gamma0)));
        v_pole_s  = log(tan(0.5 * (PI/2 + gamma0)));
        final double ArB = A / B;
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         */
        parameters.validate();
        final AffineTransform denormalize = parameters.normalize(false);
        denormalize.rotate(-rectifiedGridAngle);
        if (!parameters.nameMatches(HotineObliqueMercator.PARAMETERS) &&
            !parameters.nameMatches(HotineObliqueMercator.TwoPoint.PARAMETERS))
        {
            final double u_c;
            if (abs(abs(azimuth) - PI/2) < FINER_EPSILON) {
                // LongitudeOfCentre = NaN in twoPoint, but azimuth cannot be 90 here (lat1 != lat2)
                u_c = A * (longitudeOfCentre - centralMeridian);
            } else {
                u_c = copySign(ArB * atan2(sqrt(D*D - 1), cos(azimuth)), latitudeOfCentre);
            }
            denormalize.translate(0, -u_c);
        }
        denormalize.scale(ArB, ArB);
        finish();
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
                throw new ProjectionException(Errors.Keys.INFINITE_VALUE_1, "v");
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
        dstPts[dstOff  ] = unrollLongitude(λ);
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
