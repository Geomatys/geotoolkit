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

import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;

import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.transform.MolodenskyTransform;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.internal.referencing.Identifiers.createDescriptorGroup;


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
 * <p><b>Operation name:</b> Abridged_Molodenski</p>
 * <table bgcolor="#F4F8FF" cellspacing="0" cellpadding="0">
 *   <tr bgcolor="#B9DCFF"><th>Parameter Name</th><th>Default value</th></tr>
 *   <tr><td>dim</td><td>&nbsp;&nbsp;2</td></tr>
 *   <tr><td>src_dim</td><td>&nbsp;&nbsp;2</td></tr>
 *   <tr><td>tgt_dim</td><td>&nbsp;&nbsp;2</td></tr>
 *   <tr><td>dx</td><td>&nbsp;&nbsp;0 metres</td></tr>
 *   <tr><td>dy</td><td>&nbsp;&nbsp;0 metres</td></tr>
 *   <tr><td>dz</td><td>&nbsp;&nbsp;0 metres</td></tr>
 *   <tr><td>src_semi_major</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>src_semi_minor</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>tgt_semi_major</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>tgt_semi_minor</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>Semi-major axis length difference</td><td>&nbsp;&nbsp;</td></tr>
 *   <tr><td>Flattening difference</td><td>&nbsp;&nbsp;</td></tr>
 * </table>
 * <!-- END OF PARAMETERS -->
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see MolodenskyTransform
 *
 * @since 2.1
 * @module
 */
@Immutable
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
     * <table bgcolor="#F4F8FF" border="1" cellspacing="0" cellpadding="6">
     *   <tr bgcolor="#B9DCFF" valign="top"><td colspan="2">
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>Abridged_Molodenski</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Abridged Molodensky</code></td></tr>
     *       <tr><th align="left">&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>Abridged Molodensky transform</code></td></tr>
     *       <tr><th align="left">Identifier:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>9605</code></td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Integer</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[2…3]</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>src_dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Integer</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[2…3]</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>Geotk:</code></td><td><code>tgt_dim</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Integer</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[2…3]</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>2</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>dx</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>X-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>dy</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Y-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>dz</code></td></tr>
     *       <tr><th align="left">Alias:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Z-axis translation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *       <tr><th align="left">Default value:&nbsp;&nbsp;</th><td>0 metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>src_semi_major</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>src_semi_minor</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>tgt_semi_major</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>OGC:</code></td><td><code>tgt_semi_minor</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>mandatory</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[0…∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Semi-major axis length difference</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>(-∞ … ∞) metres</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr valign="top"><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Name:&nbsp;&nbsp;</th><td><code>EPSG:</code></td><td><code>Flattening difference</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table border="0" cellspacing="0" cellpadding="0">
     *       <tr><th align="left">Type:&nbsp;&nbsp;</th><td><code>Double</code></td></tr>
     *       <tr><th align="left">Obligation:&nbsp;&nbsp;</th><td>optional</td></tr>
     *       <tr><th align="left">Value range:&nbsp;&nbsp;</th><td>[-1 … 1]</td></tr>
     *     </table>
     *   </td></tr>
     * </table>
     */
    @SuppressWarnings("hiding")
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
        new ReferenceIdentifier[] {
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
        });

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
