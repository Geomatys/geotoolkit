/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;
import org.opengis.metadata.Identifier;

import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.internal.io.Installation;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.NadconTransform;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.logging.Logging;


/**
 * The provider for "<cite>North American Datum Conversion</cite>" (EPSG:9613). The math
 * transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.transform.NadconTransform}</li>
 * </ul>
 *
 * <!-- PARAMETERS NADCON -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code NADCON}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code Latitude difference file}</td><td>{@code "conus.las"}</td></tr>
 *   <tr><td>{@code Longitude difference file}</td><td>{@code "conus.los"}</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * {@section Grid data}
 * This transform requires data that are not bundled by default with Geotk. Run the
 * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module
 * for downloading and installing the grid data.
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.1
 * @module
 */
@Immutable
public class NADCON extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4707304160205218546L;

    /**
     * The operation parameter descriptor for the <cite>Latitude difference file</cite>
     * parameter value. The default value is {@code "conus.las"}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<String> LAT_DIFF_FILE = new DefaultParameterDescriptor<>(
            Citations.EPSG, "Latitude difference file", String.class, null, "conus.las", null, null, null, true);

    /**
     * The operation parameter descriptor for the <cite>Longitude difference file</cite>
     * parameter value. The default value is {@code "conus.los"}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<String> LONG_DIFF_FILE = new DefaultParameterDescriptor<>(
            Citations.EPSG, "Longitude difference file", String.class, null, "conus.los", null, null, null, true);

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk:
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>NADCON</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>NADCON transform</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9613</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Latitude difference file</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code String}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Default value:</b></td><td>{@code "conus.las"}</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Longitude difference file</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code String}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Default value:</b></td><td>{@code "conus.los"}</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = UniversalParameters.createDescriptorGroup(
        new Identifier[] {
            new NamedIdentifier(Citations.EPSG, "NADCON"),
            new IdentifierCode (Citations.EPSG,  9613),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.NADCON_TRANSFORM))
        }, null, new ParameterDescriptor<?>[] {
            LAT_DIFF_FILE,
            LONG_DIFF_FILE
        }, 0);

    /**
     * Constructs a provider.
     */
    public NADCON() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns {@code true} if the NADCON data seem to be present. This method checks for the existence
     * of {@code "conus.las"} and {@code "conus.los"} (continental United States) files, using the same
     * search criterion than the one applied by the {@linkplain NadconTransform#NadconTransform(String,
     * String) transform constructor}.
     * <p>
     * Some optional data can be automatically downloaded and installed by running the
     * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
     *
     * @return {@code true} if NADCON data seem to be present.
     */
    public static boolean isAvailable() {
        try {
            return Installation.NADCON.exists(NadconTransform.class, "conus.las") &&
                   Installation.NADCON.exists(NadconTransform.class, "conus.los");
        } catch (IOException e) {
            Logging.recoverableException(NADCON.class, "isAvailable", e);
            return false;
        }
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
        final String latitudeGridFile  = Parameters.stringValue(LAT_DIFF_FILE,  values);
        final String longitudeGridFile = Parameters.stringValue(LONG_DIFF_FILE, values);
        return new NadconTransform(longitudeGridFile, latitudeGridFile);
    }
}
