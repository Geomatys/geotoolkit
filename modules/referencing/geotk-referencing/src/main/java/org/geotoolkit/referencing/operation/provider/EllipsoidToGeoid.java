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
 */
package org.geotoolkit.referencing.operation.provider;

import java.util.Collections;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;

import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptorGroup;
import static org.geotoolkit.referencing.operation.transform.EarthGravitationalModel.*;


/**
 * The provider for "<cite>Ellipsoid to Geoid</cite>" vertical transformation.
 * This transformation uses a Earth Gravitational Model.
 *
 * <!-- PARAMETERS EllipsoidToGeoid -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Ellipsoid_To_Geoid}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code Datum}</td><td>{@code "WGS84"}</td></tr>
 *   <tr><td>{@code Order}</td><td>180</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.3
 * @module
 */
@Immutable
public class EllipsoidToGeoid extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 914369333205211248L;

    /**
     * The operation parameter descriptor for the datum.
     * Valid values are {@code "WGS84"} and {@code "WGS72"}.
     */
    public static final ParameterDescriptor<String> DATUM = new DefaultParameterDescriptor<>(
            Collections.singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT,
                    Vocabulary.formatInternational(Vocabulary.Keys.DATUM))),
            String.class, new String[] {"WGS84", "WGS72"}, "WGS84", null, null, null, true);

    /**
     * The operation parameter descriptor for the maximum degree and order. The default value is
     * {@value org.geotoolkit.referencing.operation.transform.EarthGravitationalModel#DEFAULT_ORDER}.
     */
    public static final ParameterDescriptor<Integer> ORDER = DefaultParameterDescriptor.create(
            Collections.singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT,
                    Vocabulary.formatInternational(Vocabulary.Keys.ORDER))),
            DEFAULT_ORDER, 2, 180, false);

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Ellipsoid_To_Geoid</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Datum</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code String}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Default value:</b></td><td>{@code "WGS84"}</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Order</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2 … 180]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>180</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(new NamedIdentifier[] {
            new NamedIdentifier(Citations.GEOTOOLKIT, "Ellipsoid_To_Geoid")
        }, null, new ParameterDescriptor<?>[] {
            DATUM, ORDER
        }, 0);

    /**
     * Constructs a provider.
     */
    public EllipsoidToGeoid() {
        super(3, 3, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<? extends Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     * @throws FactoryException if this method failed to load the coefficient file.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException, FactoryException
    {
        final DefaultGeodeticDatum datum;
        final String name = stringValue(DATUM, values);
        if ("WGS84".equalsIgnoreCase(name)) {
            datum = DefaultGeodeticDatum.WGS84;
        } else if ("WGS72".equalsIgnoreCase(name)) {
            datum = DefaultGeodeticDatum.WGS72;
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNSUPPORTED_DATUM_1, name));
        }
        final Integer order = integerValue(ORDER, values);
        int nmax = (order != null) ? order : DEFAULT_ORDER;
        return create(datum, nmax);
    }
}
