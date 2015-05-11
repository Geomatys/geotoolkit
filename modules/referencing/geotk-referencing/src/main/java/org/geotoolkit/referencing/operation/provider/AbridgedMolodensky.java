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


import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;

import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.transform.MolodenskyTransform;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptorGroup;


/**
 * The provider for "<cite>Abridged Molodensky transformation</cite>" (EPSG:9605). This provider
 * constructs transforms from {@linkplain GeographicCRS geographic} to geographic coordinate
 * reference systems, without passing though {@linkplain GeocentricCRS geocentric} one.
 * <p>
 * The translation terms (<var>dx</var>, <var>dy</var> and <var>dz</var>) are common to all authorities.
 * But remaining parameters are specified in different ways depending on the authority:
 * <p>
 * <ul>
 *   <li>EPSG defines "<cite>Semi-major axis length difference</cite>" and
 *       "<cite>Flattening difference</cite>" parameters.</li>
 *   <li>OGC rather defines "{@code src_semi_major}", "{@code src_semi_minor}",
 *       "{@code tgt_semi_major}", "{@code tgt_semi_minor}" and "{@code dim}" parameters.</li>
 *   <li>Geotk splits the OGC "{@code dim}" parameters in two separated
 *       "{@code src_dim}" and "{@code tgt_dim}" parameters.</li>
 * </ul>
 *
 * <!-- PARAMETERS AbridgedMolodensky -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Abridged_Molodenski}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code dim}</td><td>2</td></tr>
 *   <tr><td>{@code src_dim}</td><td>2</td></tr>
 *   <tr><td>{@code tgt_dim}</td><td>2</td></tr>
 *   <tr><td>{@code dx}</td><td>0 metres</td></tr>
 *   <tr><td>{@code dy}</td><td>0 metres</td></tr>
 *   <tr><td>{@code dz}</td><td>0 metres</td></tr>
 *   <tr><td>{@code src_semi_major}</td><td></td></tr>
 *   <tr><td>{@code src_semi_minor}</td><td></td></tr>
 *   <tr><td>{@code tgt_semi_major}</td><td></td></tr>
 *   <tr><td>{@code tgt_semi_minor}</td><td></td></tr>
 *   <tr><td>{@code Semi-major axis length difference}</td><td></td></tr>
 *   <tr><td>{@code Flattening difference}</td><td></td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see MolodenskyTransform
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.1
 * @module
 */
public class AbridgedMolodensky extends Molodensky {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3889456253400732280L;

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk.
     * Note that the "<cite>Semi-major axis length difference</cite>" and "<cite>Flattening
     * difference</cite>" parameters are exclusive with all {@code "src_*"} and {@code "tgt_*"}
     * parameters (see class javadoc).
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Abridged_Molodenski</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Abridged Molodensky</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Abridged Molodensky transform</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9605</code></td></tr>
     *     </table>
     *   </th></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2…3]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>src_dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2…3]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>tgt_dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Integer}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[2…3]</td></tr>
     *       <tr><td><b>Default value:</b></td><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dx</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>X-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dy</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Y-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>dz</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Z-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>src_semi_major</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>src_semi_minor</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>tgt_semi_major</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>tgt_semi_minor</code></td></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Semi-major axis length difference</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Flattening difference</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-1 … 1]</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
        new Identifier[] {
            new NamedIdentifier(Citations.OGC,  "Abridged_Molodenski"),
            new NamedIdentifier(Citations.EPSG, "Abridged Molodensky"),
            new IdentifierCode (Citations.EPSG,  9605),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.ABRIDGED_MOLODENSKY_TRANSFORM))
        }, null, new ParameterDescriptor<?>[] {
            DIM, SRC_DIM, TGT_DIM, DX, DY, DZ,
            SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
            TGT_SEMI_MAJOR, TGT_SEMI_MINOR,
            AXIS_LENGTH_DIFFERENCE,
            FLATTENING_DIFFERENCE
        }, 0);

    /**
     * Constructs a provider.
     */
    public AbridgedMolodensky() {
        // Following constructors register themselves in the "complements" array.
        this(2, 2, PARAMETERS, new AbridgedMolodensky[4]);
        new AbridgedMolodensky(2, 3, PARAMETERS, complements);
        new AbridgedMolodensky(3, 2, PARAMETERS, complements);
        new AbridgedMolodensky(3, 3, PARAMETERS, complements);
    }

    /**
     * Constructs a provider from a set of parameters.
     *
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters      The set of parameters (never {@code null}).
     * @param complements     Providers for all combinations between 2D and 3D cases.
     */
    private AbridgedMolodensky(final int sourceDimension, final int targetDimension,
               final ParameterDescriptorGroup parameters, final Molodensky[] complements)
    {
        super(sourceDimension, targetDimension, parameters, complements);
    }

    /**
     * Returns {@code true} for the abridged formulas.
     */
    @Override
    boolean isAbridged() {
        return true;
    }
}
