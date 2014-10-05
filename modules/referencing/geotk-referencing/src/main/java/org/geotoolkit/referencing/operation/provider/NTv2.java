/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.metadata.Citations;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.transform.NTv2Transform;
import org.geotoolkit.referencing.operation.MathTransformProvider;


/**
 * The provider for "<cite>National Transformation</cite>" version 2 (ESPG:9615).
 * The math transform implementations instantiated by this provider may be any of
 * the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.transform.NTv2Transform}</li>
 * </ul>
 *
 * <!-- PARAMETERS NTv2 -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code NTv2}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code Latitude and longitude difference file}</td><td></td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * {@section Grid data}
 * This transform requires data that are not bundled by default with Geotk. Run the
 * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module
 * for downloading and installing the grid data.
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 3.12
 * @module
 */
public class NTv2 extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4707304160205218546L;

    /**
     * The operation parameter descriptor for the <cite>Latitude and longitude difference file</cite>
     * parameter value. The file extension is typically {@code ".gsb"}. There is no default value.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<String> DIFFERENCE_FILE = new DefaultParameterDescriptor<>(
            Citations.EPSG, "Latitude and longitude difference file", String.class, null, null, null, null, null, true);

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk.
     * Note that the file extension for the parameter is typically {@code ".gsb"}.
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>NTv2</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9615</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude and longitude difference file</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code String}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = UniversalParameters.createDescriptorGroup(
        new Identifier[] {
            new NamedIdentifier(Citations.EPSG, "NTv2"),
            new IdentifierCode (Citations.EPSG,  9615)
        }, null, new ParameterDescriptor<?>[] {
            DIFFERENCE_FILE
        }, 0);

    /**
     * Constructs a provider.
     */
    public NTv2() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @throws FactoryException If the grid files can not be loaded.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values) throws FactoryException {
        return new NTv2Transform(Parameters.stringValue(DIFFERENCE_FILE, values));
    }
}
