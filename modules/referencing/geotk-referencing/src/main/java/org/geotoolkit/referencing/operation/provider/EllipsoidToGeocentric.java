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

import java.util.Collections;
import javax.measure.unit.SI;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.metadata.Identifier;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.GeocentricTransform;
import org.apache.sis.internal.referencing.provider.PseudoMercator;

import static org.geotoolkit.parameter.Parameters.*;


/**
 * The provider for "<cite>Geographic/geocentric conversions</cite>" (EPSG:9602). This provider
 * constructs transforms from {@linkplain GeographicCRS geographic} to {@linkplain GeocentricCRS
 * geocentric} coordinate reference systems.
 * <p>
 * By default, this provider creates a transform from a three-dimensional ellipsoidal coordinate
 * system, which is the behavior implied in OGC's WKT. However a Geotk-specific {@code "dim"}
 * parameter allows to transform from a two-dimensional ellipsoidal coordinate system instead.
 * <p>
 * <strong>WARNING:</strong> The EPSG code is the same than the {@link GeocentricToEllipsoid}
 * one. To avoid ambiguity, use the OGC name instead: {@code "Ellipsoid_To_Geocentric"}.
 *
 * <!-- PARAMETERS EllipsoidToGeocentric -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Ellipsoid_To_Geocentric}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code semi_major}</td><td></td></tr>
 *   <tr><td>{@code semi_minor}</td><td></td></tr>
 *   <tr><td>{@code dim}</td><td>3</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 4.0
 *
 * @see GeocentricTransform
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.0
 * @module
 */
@Immutable
public class EllipsoidToGeocentric extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5690807111952562344L;

    /**
     * The operation parameter descriptor for the {@code "semi_major"} parameter value.
     * Valid values range from 0 to infinity. This parameter is mandatory and has no
     * default value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SEMI_MAJOR =
            (ParameterDescriptor<Double>) new PseudoMercator().getParameters().descriptor("semi_major"); // TODO

    /**
     * The operation parameter descriptor for the {@code "semi_minor"} parameter value.
     * Valid values range from 0 to infinity. This parameter is mandatory and has no
     * default value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SEMI_MINOR =
            (ParameterDescriptor<Double>) new PseudoMercator().getParameters().descriptor("semi_minor");

    /**
     * The operation parameter descriptor for the number of geographic dimension (2 or 3).
     * This is a Geotk-specific argument. The default value is 3, which is the value
     * implied in OGC's WKT.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Integer> DIM = DefaultParameterDescriptor.create(
                Collections.singletonMap(NAME_KEY,
                    new NamedIdentifier(Citations.GEOTOOLKIT, "dim")),
                3, 2, 3, false);

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Ellipsoid_To_Geocentric</code></td></tr>
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
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup("Ellipsoid_To_Geocentric");

    /**
     * Constructs the parameters group.
     */
    static ParameterDescriptorGroup createDescriptorGroup(final String ogc) {
        return UniversalParameters.createDescriptorGroup(new Identifier[] {
                new NamedIdentifier(Citations.OGC,  ogc),
                new NamedIdentifier(Citations.EPSG, "Geographic/geocentric conversions"),
                new IdentifierCode (Citations.EPSG, 9602),
                new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                    Vocabulary.Keys.GEOCENTRIC_TRANSFORM))
            }, null, new ParameterDescriptor<?>[] {
                SEMI_MAJOR, SEMI_MINOR, DIM
            }, MapProjectionDescriptor.ADD_EARTH_RADIUS);
    }

    /**
     * The providers for the 2D and 3D cases.
     */
    private final EllipsoidToGeocentric[] complements;

    /**
     * Constructs a provider with default parameters.
     */
    public EllipsoidToGeocentric() {
        super(PARAMETERS);
        complements = new EllipsoidToGeocentric[2];
        complements[0] = new EllipsoidToGeocentric(2, complements);
        complements[1] = new EllipsoidToGeocentric(3, complements);
    }

    /**
     * Constructs a provider for the 2-dimensional or 3-dimensional case.
     */
    private EllipsoidToGeocentric(final int dimension, final EllipsoidToGeocentric[] complements) {
        super(dimension, 3, PARAMETERS);
        this.complements = complements;
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Returns {@code 2} if the given parameter group contains a {@linkplain #DIM} parameter
     * having value 2. If the parameter value is 3 or if there is no parameter value, then
     * this method returns {@code 3}, which is consistent with the default value.
     *
     * @throws InvalidParameterValueException if the dimension parameter has an invalid value.
     */
    static int dimension(final ParameterValueGroup values) throws InvalidParameterValueException {
        final Integer dimension = integerValue(DIM, values);
        if (dimension != null) {
            switch (dimension) {
                case 2: // fall through;
                case 3: return dimension;
                default: {
                    final String name = DIM.getName().getCode();
                    throw new InvalidParameterValueException(Errors.format(Errors.Keys.
                            ILLEGAL_ARGUMENT_2, name, dimension), name, dimension);
                }
            }
        }
        return 3;
    }

    /**
     * Creates a transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    public MathTransform createMathTransform(MathTransformFactory factory, final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final double semiMajor = doubleValue(SEMI_MAJOR, values);
        final double semiMinor = doubleValue(SEMI_MINOR, values);
        final int    dimension = dimension(values);
        MathTransform transform = GeocentricTransform.create(semiMajor, semiMinor, SI.METRE, dimension != 2);
        return transform;
    }

    /**
     * Returns the same operation method, but for different dimensions.
     *
     * @param  sourceDimensions The desired number of input dimensions.
     * @param  targetDimensions The desired number of output dimensions.
     * @return The redimensioned operation method, or {@code this} if no change is needed.
     */
    @Override
    public OperationMethod redimension(final int sourceDimensions, final int targetDimensions) {
        ArgumentChecks.ensureBetween("sourceDimensions", 2, 3, sourceDimensions);
        ArgumentChecks.ensureBetween("targetDimensions", 3, 3, targetDimensions);
        return complements[sourceDimensions - 2];
    }
}
