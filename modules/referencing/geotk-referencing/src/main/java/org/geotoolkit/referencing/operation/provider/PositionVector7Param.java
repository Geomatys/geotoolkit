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
 */
package org.geotoolkit.referencing.operation.provider;

import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.GeocentricTransform;
import org.geotoolkit.referencing.operation.transform.GeocentricAffineTransform;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptor;


/**
 * The provider for "<cite>Position Vector 7-parameters transformation</cite>" (EPSG:9606).
 * In addition to the EPSG parameters, this provider defines some OGC/Geotk-specific parameters.
 * Those parameters begin with the {@code "src_"} or {@code "tgt_"} prefix, and modify the math
 * transform as below:
 * <p>
 * <ul>
 *   <li>If a {@code "src_*"} parameter is present, then an {@link EllipsoidToGeocentric}
 *       transform will be concatenated before the geocentric operation.</li>
 *   <li>If a {@code "tgt_*"} parameter is present, then an {@link GeocentricToEllipsoid}
 *       transform will be concatenated after the geocentric operation.</li>
 * </ul>
 *
 * <!-- PARAMETERS PositionVector7Param -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Position Vector transformation (geog2D domain)}</p>
 * <table class="geotk">
 *   <tr><th>Parameter name</th><th>Default value</th></tr>
 *   <tr><td>{@code dx}</td><td>0 metres</td></tr>
 *   <tr><td>{@code dy}</td><td>0 metres</td></tr>
 *   <tr><td>{@code dz}</td><td>0 metres</td></tr>
 *   <tr><td>{@code ex}</td><td>0 ''</td></tr>
 *   <tr><td>{@code ey}</td><td>0 ''</td></tr>
 *   <tr><td>{@code ez}</td><td>0 ''</td></tr>
 *   <tr><td>{@code ppm}</td><td>0</td></tr>
 *   <tr><td>{@code src_semi_major}</td><td></td></tr>
 *   <tr><td>{@code src_semi_minor}</td><td></td></tr>
 *   <tr><td>{@code tgt_semi_major}</td><td></td></tr>
 *   <tr><td>{@code tgt_semi_minor}</td><td></td></tr>
 *   <tr><td>{@code src_dim}</td><td>2</td></tr>
 *   <tr><td>{@code tgt_dim}</td><td>2</td></tr>
 * </table></blockquote>
 * <!-- END OF PARAMETERS -->
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.2
 * @module
 */
@Immutable
public class PositionVector7Param extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -6398226638364450229L;

    /**
     * The default value for geographic source and target dimensions, which is 2.
     *
     * {@note If this default value is modified, then the handling of the 3D
     *        cases in <code>MolodenskyTransform</code> must be adjusted too.}
     */
    static final int DEFAULT_DIMENSION = 2;

    /**
     * The maximal value for a rotation, in arc-second.
     */
    private static final double MAX_ROTATION = 180*60*60;

    /**
     * The operation parameter descriptor for the number of source geographic dimension (2 or 3).
     * This is a Geotk-specific argument. If presents, an {@code "Ellipsoid_To_Geocentric"}
     * transform will be concatenated before the geocentric translation.
     */
    static final ParameterDescriptor<Integer> SRC_DIM = DefaultParameterDescriptor.create(
            singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT, "src_dim")),
            DEFAULT_DIMENSION, 2, 3, false);

    /**
     * The operation parameter descriptor for the number of target geographic dimension (2 or 3).
     * This is a Geotk-specific argument. If presents, a {@code "Geocentric_To_Ellipsoid"}
     * transform will be concatenated after the geocentric translation.
     */
    static final ParameterDescriptor<Integer> TGT_DIM = DefaultParameterDescriptor.create(
            singletonMap(NAME_KEY, new NamedIdentifier(Citations.GEOTOOLKIT, "tgt_dim")),
            DEFAULT_DIMENSION, 2, 3, false);

    /**
     * The operation parameter descriptor for the {@code "src_semi_major"} optional parameter value.
     * This is a Geotk-specific argument. If presents, an {@code "Ellipsoid_To_Geocentric"}
     * transform will be concatenated before the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    static final ParameterDescriptor<Double> SRC_SEMI_MAJOR = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "src_semi_major")
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, false);

    /**
     * The operation parameter descriptor for the {@code "src_semi_minor"} optional parameter value.
     * This is a Geotk-specific argument. If presents, an {@code "Ellipsoid_To_Geocentric"}
     * transform will be concatenated before the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    static final ParameterDescriptor<Double> SRC_SEMI_MINOR = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "src_semi_minor"),
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, false);

    /**
     * The operation parameter descriptor for the {@code "tgt_semi_major"} optional parameter value.
     * This is a Geotk-specific argument. If presents, a {@code "Geocentric_To_Ellipsoid"}
     * transform will be concatenated after the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    static final ParameterDescriptor<Double> TGT_SEMI_MAJOR = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "tgt_semi_major")
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, false);

    /**
     * The operation parameter descriptor for the {@code "tgt_semi_minor"} optional parameter value.
     * This is a Geotk-specific argument. If presents, a {@code "Geocentric_To_Ellipsoid"}
     * transform will be concatenated after the geocentric translation.
     * <p>
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     */
    static final ParameterDescriptor<Double> TGT_SEMI_MINOR = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC, "tgt_semi_minor")
            },
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, false);

    /**
     * The operation parameter descriptor for the <cite>X-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dx dx}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> DX = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "dx"),
                new NamedIdentifier(Citations.EPSG, "X-axis translation")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the <cite>Y-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dy dy}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> DY = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "dy"),
                new NamedIdentifier(Citations.EPSG, "Y-axis translation")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the <cite>Z-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dz dz}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> DZ = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "dz"),
                new NamedIdentifier(Citations.EPSG, "Z-axis translation")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the <cite>X-axis rotation</cite>
     * ({@linkplain BursaWolfParameters#ex ex}) parameter value. Units are
     * {@linkplain NonSI#SECOND_ANGLE arc-seconds}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> EX = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ex"),
                new NamedIdentifier(Citations.EPSG, "X-axis rotation")
            },
            0.0, -MAX_ROTATION, MAX_ROTATION, NonSI.SECOND_ANGLE, true);

    /**
     * The operation parameter descriptor for the <cite>Y-axis rotation</cite>
     * ({@linkplain BursaWolfParameters#ey ey}) parameter value. Units are
     * {@linkplain NonSI#SECOND_ANGLE arc-seconds}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> EY = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ey"),
                new NamedIdentifier(Citations.EPSG, "Y-axis rotation")
            },
            0.0, -MAX_ROTATION, MAX_ROTATION, NonSI.SECOND_ANGLE, true);

    /**
     * The operation parameter descriptor for the <cite>Z-axis rotation</cite>
     * ({@linkplain BursaWolfParameters#ez ez}) parameter value. Units are
     * {@linkplain NonSI#SECOND_ANGLE arc-seconds}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> EZ = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ez"),
                new NamedIdentifier(Citations.EPSG, "Z-axis rotation")
            },
            0.0, -MAX_ROTATION, MAX_ROTATION, NonSI.SECOND_ANGLE, true);

    /**
     * The operation parameter descriptor for the <cite>Scale difference</cite>
     * ({@linkplain BursaWolfParameters#ppm ppm}) parameter value. Valid values
     * range from negative to positive infinity. Units are
     * {@linkplain Units#PPM parts per million}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> PPM = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.OGC,  "ppm"),
                new NamedIdentifier(Citations.EPSG, "Scale difference")
            },
            0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Units.PPM, true);

    /**
     * The group of all parameters expected by this coordinate operation.
     * The following table lists the operation names and the parameters recognized by Geotk.
     * Note that all {@code "src_*"} and {@code "tgt_*"} parameters are OGC/Geotk-specific,
     * and modify the math transform as below:
     * <p>
     * <ul>
     *   <li>If a {@code "src_*"} parameter is present, then an {@link EllipsoidToGeocentric}
     *       transform will be concatenated before the geocentric translation.</li>
     *   <li>If a {@code "tgt_*"} parameter is present, then an {@link GeocentricToEllipsoid}
     *       transform will be concatenated after the geocentric translation.</li>
     * </ul>
     * <p>
     * <!-- GENERATED PARAMETERS - inserted by ProjectionParametersJavadoc -->
     * <table class="geotk" border="1">
     *   <tr><th colspan="2">
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Position Vector transformation (geog2D domain)</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Position Vector 7-param. transformation</code></td></tr>
     *       <tr><td></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Bursa-Wolf</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9606</code></td></tr>
     *     </table>
     *   </th></tr>
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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ex</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>X-axis rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-648000 … 648000] ''</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 ''</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ey</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Y-axis rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-648000 … 648000] ''</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 ''</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ez</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Z-axis rotation</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[-648000 … 648000] ''</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0 ''</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>ppm</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Scale difference</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>mandatory</td></tr>
     *       <tr><td><b>Value range:</b></td><td>(-∞ … ∞)</td></tr>
     *       <tr><td><b>Default value:</b></td><td>0</td></tr>
     *     </table>
     *   </td></tr>
     *   <tr><td>
     *     <table class="compact">
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>src_semi_major</code></td></tr>
     *     </table>
     *   </td><td>
     *     <table class="compact">
     *       <tr><td><b>Type:</b></td><td>{@code Double}</td></tr>
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
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
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
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
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
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
     *       <tr><td><b>Obligation:</b></td><td>optional</td></tr>
     *       <tr><td><b>Value range:</b></td><td>[0…∞) metres</td></tr>
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
     * </table>
     */
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(9606,
            "Position Vector transformation (geog2D domain)", "Position Vector 7-param. transformation");

    /**
     * Creates a parameters group using the 7 parameters.
     */
    static ParameterDescriptorGroup createDescriptorGroup(final int code, final String name, final String legacyName) {
        return UniversalParameters.createDescriptorGroup(new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.EPSG, name),
            new NamedIdentifier(Citations.EPSG, legacyName),
            new NamedIdentifier(Citations.EPSG, "Bursa-Wolf"),
            new IdentifierCode (Citations.EPSG, code)
        }, null, new ParameterDescriptor<?>[] {
            DX, DY, DZ, EX, EY, EZ, PPM,
            SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
            TGT_SEMI_MAJOR, TGT_SEMI_MINOR,
            SRC_DIM, TGT_DIM
        }, 0);
    }

    /**
     * Constructs the provider.
     */
    public PositionVector7Param() {
        this(PARAMETERS);
    }

    /**
     * Constructs a provider with the specified parameters.
     */
    PositionVector7Param(final ParameterDescriptorGroup parameters) {
        super(3, 3, parameters);
    }

    /**
     * Returns the operation type, which is a transformation.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        final BursaWolfParameters parameters = new BursaWolfParameters(null, null);
        fill(parameters, values);
        return concatenate(concatenate(new GeocentricAffineTransform(parameters, getParameters()),
                values, SRC_SEMI_MAJOR, SRC_SEMI_MINOR, SRC_DIM),
                values, TGT_SEMI_MAJOR, TGT_SEMI_MINOR, TGT_DIM);
    }

    /**
     * Fills the given Bursa-Wolf parameters according the specified values.
     * This method is invoked automatically by {@link #createMathTransform}.
     *
     * @param parameters The Bursa-Wold parameters to set.
     * @param values The parameter values to read. Those parameters will not be modified.
     */
    void fill(final BursaWolfParameters parameters, final ParameterValueGroup values) {
        parameters.tX = Parameters.doubleValue(DX, values);
        parameters.tY = Parameters.doubleValue(DY, values);
        parameters.tZ = Parameters.doubleValue(DZ, values);
        parameters.rX = Parameters.doubleValue(EX, values);
        parameters.rY = Parameters.doubleValue(EY, values);
        parameters.rZ = Parameters.doubleValue(EZ, values);
        parameters.dS = Parameters.doubleValue(PPM, values);
    }

    /**
     * Concatenates the supplied transform with an "ellipsoid to geocentric" or a
     * "geocentric to ellipsoid" step, if needed.
     */
    private static MathTransform concatenate(final MathTransform transform,
            final ParameterValueGroup values,
            final ParameterDescriptor<Double> major,
            final ParameterDescriptor<Double> minor,
            final ParameterDescriptor<Integer> dim)
    {
        double  semiMajor = Parameters.doubleValue(major, values);
        double  semiMinor = Parameters.doubleValue(minor, values);
        Integer dimension = Parameters.integerValue(dim,  values);
        boolean hasHeight = false;
        if (dimension == null) {
            if (Double.isNaN(semiMajor) && Double.isNaN(semiMinor)) {
                return transform;
            }
        } else {
            switch (dimension) {
                case DEFAULT_DIMENSION: break;
                case 3: hasHeight = true; break;
                default: {
                    final String name = dim.getName().getCode();
                    throw new InvalidParameterValueException(Errors.format(
                            Errors.Keys.ILLEGAL_ARGUMENT_2, name, dimension), name, dimension);
                }
            }
        }
        ensureValid(major, semiMajor);
        ensureValid(minor, semiMinor);
        final MathTransform step;
        step = GeocentricTransform.create(semiMajor, semiMinor, SI.METRE, hasHeight);
        // Note: dimension may be 0 if not user-provided, which is treated as 2.
        if (dim == SRC_DIM) {
            return MathTransforms.concatenate(step, transform);
        } else try {
            return MathTransforms.concatenate(transform, step.inverse());
        } catch (NoninvertibleTransformException e) {
            throw new AssertionError(e); // Should never happen in Geotk implementation.
        }
    }

    /**
     * Ensures the the specified parameter is valid.
     */
    private static void ensureValid(final ParameterDescriptor<?> param, double value) {
        if (!(value > 0)) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NO_PARAMETER_1, param.getName().getCode()));
        }
    }
}
