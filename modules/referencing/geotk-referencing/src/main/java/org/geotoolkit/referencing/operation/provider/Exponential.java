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
package org.geotoolkit.referencing.operation.provider;


import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.metadata.content.TransferFunctionType;

import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.resources.Vocabulary;

import org.apache.sis.measure.Units;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptorGroup;


/**
 * The provider for {@link ExponentialTransform1D}.
 *
 * <!-- PARAMETERS Exponential -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Exponential}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code base}</td><td>10</td></tr>
 *   <tr><td>{@code scale}</td><td>1</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see ExponentialTransform1D
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.0
 * @module
 */
public class Exponential extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5838840021166379987L;

    /**
     * The operation parameter descriptor for the {@linkplain ExponentialTransform1D#base base}
     * parameter value. Valid values range from 0 to infinity. The default value is 10.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> BASE = Logarithmic.BASE;

    /**
     * The operation parameter descriptor for the {@linkplain ExponentialTransform1D#scale scale}
     * parameter value. Valid values range is unrestricted. The default value is 1.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SCALE;
    static {
        final ParameterBuilder builder = new ParameterBuilder().setCodeSpace(Citations.GEOTOOLKIT, null).setRequired(true);
        SCALE = builder.addName("scale").create(1, Units.UNITY);
    }

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Exponential</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>base</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>10</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>scale</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>1</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(Vocabulary.Keys.Exponential))
            }, null, new ParameterDescriptor<?>[] {
                BASE, SCALE
            }, 0);

    /**
     * Creates a provider for logarithmic transforms.
     */
    public Exponential() {
        super(1, 1, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Conversion> getOperationType() {
        return Conversion.class;
    }

    /**
     * Creates a logarithmic transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    public MathTransform1D createMathTransform(MathTransformFactory factory, final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final TransferFunction f = new TransferFunction();
        f.setType(TransferFunctionType.EXPONENTIAL);
        f.setBase(Parameters.castOrWrap(values).getValue(BASE));
        f.setScale(Parameters.castOrWrap(values).getValue(SCALE));
        return f.getTransform();
    }
}
