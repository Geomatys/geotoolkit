/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.EnumMap;
import java.util.Map;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.OperationMethod;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.operation.projection.NormalizedProjection;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;
import static org.apache.sis.math.MathFunctions.atanh;


abstract class UnitaryProjection extends NormalizedProjection {
    /**
     * Tolerance in the correctness of argument values provided to the mathematical functions
     * defined in this class.
     *
     * @since 3.18
     */
    private static final double ARGUMENT_TOLERANCE = 1E-15;

    /**
     * Maximum difference allowed when comparing longitudes or latitudes in radians.
     * A tolerance of 1E-6 is about 0.2 second of arcs, which is about 6 kilometers
     * (computed from the standard length of nautical mile).
     * <p>
     * Some formulas use this tolerance value for testing sinus or cosinus of an angle.
     * In the sinus case, this is justified because <code>sin(&theta;) ≅ &theta;</code>
     * when &theta; is small. Similar reasoning applies to cosinus with
     * <code>cos(&theta;) ≅ &theta; + &pi;/2</code> when &theta; is small.
     */
    static final double ANGLE_TOLERANCE = 1E-6;

    /**
     * Difference allowed in iterative computations. A value of 1E-10 causes the
     * {@link #cphi2} function to compute the latitude at a precision of 1E-10 radians,
     * which is slightly smaller than one millimetre.
     */
    static final double ITERATION_TOLERANCE = 1E-10;

    /**
     * Maximum number of iterations for iterative computations.
     */
    static final int MAXIMUM_ITERATIONS = 15;

    /**
     * Maximum difference allowed when comparing real numbers (other cases). The value defined
     * here is consistent with the one that was used in {@link LambertAzimuthalEqualArea} for
     * the same purpose (not to be confused with the current {@code EPSILON} constant defined
     * in the above-mentioned class, which has been renamed), and the modified value used in
     * {@link AlbersEqualArea}.
     */
    static final double EPSILON = 1E-7;

    /**
     * Provides default (<var>role</var> → <var>parameter</var>) associations for the given map projection.
     * This is a convenience method for a typical set of parameters found in map projections.
     * This method expects a {@code projection} argument containing descriptors for the given parameters
     * (using OGC names):
     *
     * <ul>
     *   <li>{@code "semi_major"}</li>
     *   <li>{@code "semi_minor"}</li>
     *   <li>{@code "central_meridian"}</li>
     *   <li>{@code "scale_factor"}</li>
     *   <li>{@code "false_easting"}</li>
     *   <li>{@code "false_northing"}</li>
     * </ul>
     *
     * <div class="note"><b>Note:</b>
     * Apache SIS uses EPSG names as much as possible, but this method is an exception to this rule.
     * In this particular case we use OGC names because they are identical for a wide range of projections.
     * For example there is at least {@linkplain #SCALE_FACTOR three different EPSG names} for the
     * <cite>"scale factor"</cite> parameter, which OGC defines only {@code "scale_factor"} for all of them.</div>
     *
     * @param  projection The map projection method for which to infer (<var>role</var> → <var>parameter</var>) associations.
     * @return The parameters associated to most role in this enumeration.
     * @throws ParameterNotFoundException if one of the above-cited parameters is not found in the given projection method.
     * @throws ClassCastException if a parameter has been found but is not an instance of {@code ParameterDescriptor<Double>}.
     */
    private static Map<ParameterRole, ParameterDescriptor<Double>> defaultMap(final OperationMethod projection)
            throws ParameterNotFoundException, ClassCastException
    {
        final ParameterDescriptorGroup parameters = projection.getParameters();
        final EnumMap<ParameterRole, ParameterDescriptor<Double>> roles = new EnumMap<>(ParameterRole.class);
        for (final ParameterRole role : ParameterRole.values()) {
            if (role != ParameterRole.LATITUDE_OF_CONFORMAL_SPHERE_RADIUS &&
                role != ParameterRole.FALSE_WESTING && role != ParameterRole.FALSE_SOUTHING)
            {
                final String name = role.name().toLowerCase();
                final GeneralParameterDescriptor p = parameters.descriptor(name);
                roles.put(role, Parameters.cast((ParameterDescriptor<?>) p, Double.class));
            }
        }
        return roles;
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected UnitaryProjection(final OperationMethod method, final org.apache.sis.parameter.Parameters parameters,
            Map<ParameterRole, ParameterDescriptor<Double>> roles)
    {
        super(method, parameters, (roles != null) ? roles : defaultMap(method));
    }

    final double getAndStore(final Parameters parameters, final ParameterDescriptor<Double> descriptor) {
        final double value = parameters.doubleValue(descriptor);    // Apply a unit conversion if needed.
        final Double defaultValue = descriptor.getDefaultValue();
        if (defaultValue == null || !defaultValue.equals(value)) {
            org.apache.sis.internal.referencing.provider.MapProjection.validate(descriptor, value);
            String name = IdentifiedObjects.getName(descriptor, parameters.getDescriptor().getName().getAuthority());
            if (name == null) {
                name = descriptor.getName().getCode();
            }
            getContextualParameters().parameter(name).setValue(value);
        }
        return value;
    }

    static boolean isSpherical(final Parameters parameters) {
        return parameters.parameter("semi_major").doubleValue() == parameters.parameter("semi_minor").doubleValue();
    }

    @Override
    protected abstract void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException;

    /**
     * Computes function <code>f(s,c,e²) = c/sqrt(1 - s²&times;e²)</code> needed for the true scale
     * latitude (Snyder 14-15), where <var>s</var> and <var>c</var> are the sine and cosine of
     * the true scale latitude, and <var>e²</var> is the {@linkplain #eccentricitySquared
     * eccentricity squared}.
     * <p>
     * Special cases:
     * <ul>
     *   <li>If φ is 0°, then this method returns 1.</li>
     *   <li>If φ is ±90°, then this method returns 0 provided that we are
     *       not in the spherical case (otherwise we get {@link Double#NaN}).</li>
     * </ul>
     *
     * @param sinφ The   sine of the φ latitude in radians.
     * @param cosφ The cosine of the φ latitude in radians.
     */
    final double msfn(final double sinφ, final double cosφ) {               // == cosφ / rν(sinφ)
        assert !(abs(sinφ*sinφ + cosφ*cosφ - 1) > ARGUMENT_TOLERANCE);
        return cosφ / sqrt(1.0 - (sinφ*sinφ) * eccentricitySquared);
    }

    /**
     * Computes part of function (3-1) from Snyder. This is numerically equivalent to
     * <code>{@linkplain #tsfn tsfn}(-φ, sinφ)</code>, but is defined as a separated
     * function for clarity and because the function properties are not the same.
     *
     * @param  φ    The latitude in radians.
     * @param  sinφ The sine of the φ argument. This is provided explicitly
     *              because in many cases, the caller has already computed this value.
     */
    final double ssfn(double φ, double sinφ) {
        assert !(abs(sinφ - sin(φ)) > ARGUMENT_TOLERANCE) : φ;
        sinφ *= eccentricity;
        return tan(PI/4 + 0.5*φ) * pow((1-sinφ) / (1+sinφ), 0.5*eccentricity);
    }

    /**
     * Computes the derivative of the {@link #ssfn(double, double)} method divided by {@code ssfn}.
     * Callers must multiply the return value by {@code ssfn} in order to get the actual value.
     *
     * @param  φ    The latitude.
     * @param  sinφ the sine of latitude.
     * @param  cosφ The cosine of latitude.
     * @return The {@code dssfn} derivative at the specified latitude.
     *
     * @since 3.18
     */
    final double dssfn_dφ(final double φ, final double sinφ, final double cosφ) {
        assert !(abs(sinφ - sin(φ)) > ARGUMENT_TOLERANCE) : φ;
        assert !(abs(cosφ - cos(φ)) > ARGUMENT_TOLERANCE) : φ;
        return (1/cosφ) - (eccentricitySquared*cosφ)/(1-eccentricitySquared*sinφ*sinφ);
        /*
         * NOTE: 0.5*(t + 1/t)   =   1/cosφ
         */
    }

    /**
     * Calculates <var>q</var>, Snyder equation (3-12).
     * This equation has the following properties:
     * <ul>
     *   <li>Input  in the [-1 ... +1] range.</li>
     *   <li>Output in the [-2 ... +2] range.</li>
     *   <li>Output is 0 when input is 0.</li>
     *   <li>Output of the same sign than input.</li>
     *   <li>{@code qsfn(-sinφ) == -qsfn(sinφ)}.</li>
     * </ul>
     *
     * @param sinφ Sine of the latitude <var>q</var> is calculated for.
     * @return <var>q</var> from Snyder equation (3-12).
     */
    final double qsfn(final double sinφ) {
        if (eccentricity < EPSILON) {
            return 2 * sinφ;
        }
        /*
         * Above check was required because the expression below would simplify to
         * sinφ - 0.5/0*log(1) where the right terms are infinity multiplied by
         * zero, thus producing NaN.
         */
        final double esinφ = eccentricity * sinφ;
        return (1 - eccentricitySquared) * (sinφ / (1 - esinφ*esinφ) + atanh(esinφ)/eccentricity);
    }

    /**
     * Gets the derivative of the {@link #qsfn(double)} method.
     *
     * @param  sinφ The sine of latitude.
     * @param  cosφ The cosines of latitude.
     * @return The {@code qsfn} derivative at the specified latitude.
     *
     * @since 3.18
     */
    final double dqsfn_dφ(final double sinφ, final double cosφ) {
        assert !(abs(sinφ*sinφ + cosφ*cosφ - 1) > ARGUMENT_TOLERANCE);
        double esinφ2 = eccentricity * sinφ;
        esinφ2 *= esinφ2;
        return (1 - eccentricitySquared) * (cosφ / (1 - esinφ2)) * (1 + ((1 + esinφ2) / (1 - esinφ2)));
    }
}
