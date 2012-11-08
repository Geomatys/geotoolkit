/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.provider;

import javax.measure.unit.SI;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.GeocentricTransform;
import org.geotoolkit.internal.referencing.MathTransformDecorator;

import static org.geotoolkit.parameter.Parameters.*;


/**
 * The provider for "<cite>Geographic/geocentric conversions</cite>" (EPSG:9602). This provider
 * constructs transforms from {@linkplain GeocentricCRS geocentric} to {@linkplain GeographicCRS
 * geographic} coordinate reference systems.
 * <p>
 * By default, this provider creates a transform to a three-dimensional ellipsoidal coordinate
 * system, which is the behavior implied in OGC's WKT. However a Geotk-specific {@code "dim"}
 * parameter allows to transform to a two-dimensional ellipsoidal coordinate system instead.
 * <p>
 * <strong>WARNING:</strong> The EPSG code is the same than the {@link EllipsoidToGeocentric}
 * one. To avoid ambiguity, use the OGC name instead: {@code "Geocentric_To_Ellipsoid"}.
 *
 * <!-- PARAMETERS GeocentricToEllipsoid -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Geocentric_To_Ellipsoid}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code dim}</td><td>3</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see GeocentricTransform
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.0
 * @module
 */
@Immutable
public class GeocentricToEllipsoid extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8459294628751497567L;

    /**
     * The operation parameter descriptor for the {@code "semi_major"} parameter value.
     * Valid values range from 0 to infinity.
     */
    static final ParameterDescriptor<Double> SEMI_MAJOR = EllipsoidToGeocentric.SEMI_MAJOR;

    /**
     * The operation parameter descriptor for the {@code "semi_minor"} parameter value.
     * Valid values range from 0 to infinity.
     */
    static final ParameterDescriptor<Double> SEMI_MINOR = EllipsoidToGeocentric.SEMI_MINOR;

    /**
     * The operation parameter descriptor for the number of geographic dimension (2 or 3).
     * This is a Geotk-specific argument. The default value is 3, which is the value
     * implied in OGC's WKT.
     */
    static final ParameterDescriptor<Integer> DIM = EllipsoidToGeocentric.DIM;

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Geocentric_To_Ellipsoid</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Geographic/geocentric conversions</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Geocentric transform</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9602</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_major</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-major axis</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>semi_minor</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-minor axis</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2…3]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>3</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS =
            EllipsoidToGeocentric.createDescriptorGroup("Geocentric_To_Ellipsoid");

    /**
     * If this provider is for the 3D case, then {@code complement} is the provider for the 2D case.
     * Conversely if this provider is for the 2D case, then {@code complement} is the provider for
     * the 3D case.
     */
    private final GeocentricToEllipsoid complement;

    /**
     * Constructs a provider with default parameters.
     */
    public GeocentricToEllipsoid() {
        super(3, 3, PARAMETERS);
        complement = new GeocentricToEllipsoid(this);
    }

    /**
     * Constructs a provider for the given target dimension.
     *
     * @param complement The provider for the 3D case.
     */
    private GeocentricToEllipsoid(final GeocentricToEllipsoid complement) {
        super(3, 2, PARAMETERS);
        this.complement = complement;
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Creates a transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    public MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final double semiMajor = doubleValue(SEMI_MAJOR, values);
        final double semiMinor = doubleValue(SEMI_MINOR, values);
        final int    dimension = EllipsoidToGeocentric.dimension(values);
        MathTransform transform = GeocentricTransform.create(semiMajor, semiMinor, SI.METRE, dimension != 2);
        try {
            transform = transform.inverse();
        } catch (NoninvertibleTransformException e) {
            throw new AssertionError(e); // Should never happen in Geotk implementation.
        }
        if (dimension != targetDimension.intValue()) {
            transform = new MathTransformDecorator(transform, complement);
        }
        return transform;
    }
}
