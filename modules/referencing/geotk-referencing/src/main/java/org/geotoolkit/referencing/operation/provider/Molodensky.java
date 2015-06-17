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

import java.util.Collection;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.metadata.Identifier;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.MolodenskyTransform;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;

import static java.util.Collections.singletonMap;
import static org.geotoolkit.parameter.Parameters.doubleValue;
import static org.geotoolkit.parameter.Parameters.integerValue;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptor;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.createDescriptorGroup;


/**
 * The provider for "<cite>Molodensky transformation</cite>" (EPSG:9604). This provider constructs
 * transforms from {@linkplain GeographicCRS geographic} to geographic coordinate reference systems,
 * without passing though {@linkplain GeocentricCRS geocentric} one.
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
 * <!-- PARAMETERS Molodensky -->
 * <p>The following table summarizes the parameters recognized by this provider.
 * For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>
 * <blockquote><p><b>Operation name:</b> {@code Molodenski}</p>
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
 * @version 4.0
 *
 * @see MolodenskyTransform
 * @see <a href="{@docRoot}/../modules/referencing/operation-parameters.html">Geotk coordinate operations matrix</a>
 *
 * @since 2.1
 * @module
 */
public class Molodensky extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8126525068450868912L;

    /**
     * The operation parameter descriptor for the number of geographic dimension (2 or 3).
     * This argument applies on both the source and the target dimension. The default value
     * is 2.
     * <p>
     * <strong>Note: the default value may change in future versions</strong>, because the
     * EPSG database implicitly uses the Molodensky transform in 3-dimensional operations.
     * Users are well advised to always specify explicitely the dimension.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Integer> DIM = DefaultParameterDescriptor.create(
            singletonMap(NAME_KEY, new NamedIdentifier(Citations.OGC, "dim")),
            PositionVector7Param.DEFAULT_DIMENSION, 2, 3, false);

    /**
     * The operation parameter descriptor for the number of source geographic dimension (2 or 3).
     * This is a Geotk-specific argument. The standard parameter is {@link #DIM}, which set both
     * the source and target dimension.
     */
    static final ParameterDescriptor<Integer> SRC_DIM = PositionVector7Param.SRC_DIM;

    /**
     * The operation parameter descriptor for the number of target geographic dimension (2 or 3).
     * This is a Geotk-specific argument. The standard parameter is {@link #DIM}, which set both
     * the source and target dimension.
     */
    static final ParameterDescriptor<Integer> TGT_DIM = PositionVector7Param.TGT_DIM;

    /**
     * The operation parameter descriptor for the <cite>X-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dx dx}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> DX = PositionVector7Param.DX;

    /**
     * The operation parameter descriptor for the <cite>Y-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dy dy}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> DY = PositionVector7Param.DY;

    /**
     * The operation parameter descriptor for the <cite>Z-axis translation</cite>
     * ({@linkplain BursaWolfParameters#dz dz}) parameter value. Valid values range
     * from negative to positive infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> DZ = PositionVector7Param.DZ;

    /**
     * The operation parameter descriptor for the {@code "src_semi_major"} parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SRC_SEMI_MAJOR = createDescriptor(
            identifiers(PositionVector7Param.SRC_SEMI_MAJOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the {@code "src_semi_minor"} parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> SRC_SEMI_MINOR = createDescriptor(
            identifiers(PositionVector7Param.SRC_SEMI_MINOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the {@code "tgt_semi_major"} parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     * <p>
     * This parameter is mandatory, unless the {@link #AXIS_LENGTH_DIFFERENCE} parameter
     * is defined in which case the later is used.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> TGT_SEMI_MAJOR = createDescriptor(
            identifiers(PositionVector7Param.TGT_SEMI_MAJOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the {@code "tgt_semi_minor"} parameter value.
     * Valid values range from 0 to infinity. Units are {@linkplain SI#METRE metres}.
     * <p>
     * This parameter is mandatory, unless the {@link #FLATTENING_DIFFERENCE} parameter
     * is defined in which case the later is used.
     *
     * @deprecated Invoke <code>{@linkplain #PARAMETERS}.{@linkplain ParameterDescriptorGroup#descriptor(String)
     * descriptor(String)}</code> instead.
     */
    @Deprecated
    public static final ParameterDescriptor<Double> TGT_SEMI_MINOR = createDescriptor(
            identifiers(PositionVector7Param.TGT_SEMI_MINOR),
            Double.NaN, 0.0, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The operation parameter descriptor for the <cite>Semi-major axis length difference</cite>
     * optional parameter value. This parameter is defined by the EPSG database and can be used
     * in replacement of {@link #TGT_SEMI_MAJOR}.
     * <p>
     * Units are {@linkplain SI#METRE metres}.
     *
     * @since 3.19
     */
    static final ParameterDescriptor<Double> AXIS_LENGTH_DIFFERENCE = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "Semi-major axis length difference"),
            },
            Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, false);

    /**
     * The operation parameter descriptor for the <cite>Flattening difference</cite> optional
     * parameter value. This parameter is defined by the EPSG database and can be used in
     * replacement of {@link #TGT_SEMI_MINOR}.
     * <p>
     * Valid values range from -1 to +1, {@linkplain Unit#ONE dimensionless}.
     *
     * @since 3.19
     */
    static final ParameterDescriptor<Double> FLATTENING_DIFFERENCE = createDescriptor(
            new NamedIdentifier[] {
                new NamedIdentifier(Citations.EPSG, "Flattening difference"),
            },
            Double.NaN, -1.0, +1.0, Unit.ONE, false);

    /**
     * Helper method for parameter descriptor creation.
     */
    private static NamedIdentifier[] identifiers(final ParameterDescriptor<Double> parameter) {
        final Collection<GenericName> id = parameter.getAlias();
        return id.toArray(new NamedIdentifier[id.size()]);
    }

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
     *       <tr><td><b>Name:</b></td><td class="onright"><code>OGC</code>:</td><td class="onleft"><code>Molodenski</code></td></tr>
     *       <tr><td><b>Alias:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>Molodensky</code></td></tr>
     *       <tr><td></td><td class="onright"><code>Geotk</code>:</td><td class="onleft"><code>Molodensky transform</code></td></tr>
     *       <tr><td><b>Identifier:</b></td><td class="onright"><code>EPSG</code>:</td><td class="onleft"><code>9604</code></td></tr>
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
    public static final ParameterDescriptorGroup PARAMETERS = createDescriptorGroup(
            new Identifier[] {
                new NamedIdentifier(Citations.OGC,  "Molodenski"),
                new NamedIdentifier(Citations.EPSG, "Molodensky"),
                new IdentifierCode (Citations.EPSG,  9604),
                new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                    Vocabulary.Keys.MolodenskyTransform))
            }, null, new ParameterDescriptor<?>[] {
                DIM, SRC_DIM, TGT_DIM, DX, DY, DZ,
                SRC_SEMI_MAJOR, SRC_SEMI_MINOR,
                TGT_SEMI_MAJOR, TGT_SEMI_MINOR,
                AXIS_LENGTH_DIFFERENCE,
                FLATTENING_DIFFERENCE
            }, 0);

    /**
     * The providers for all combinations between 2D and 3D cases. Array length is 4.
     * Index is build with following rule:
     * <ul>
     *   <li>Bit 1: dimension of source coordinates (0 for 2D, 1 for 3D).</li>
     *   <li>Bit 0: dimension of target coordinates (0 for 2D, 1 for 3D).</li>
     * </ul>
     */
    final Molodensky[] complements;

    /**
     * Returns the index in the {@link #complements} array for the given source and target
     * dimensions, which must be either 2 or 3. This method assumes that the arguments have
     * already been checked for validity.
     */
    private static int index(final int sourceDimension, final int targetDimension) {
        return ((sourceDimension & 1) << 1) | (targetDimension & 1);
    }

    /**
     * Constructs a provider.
     */
    public Molodensky() {
        // Following constructors register themself in the "complements" array.
        super(PARAMETERS); // TODO (2,2)
        complements = new Molodensky[4];
        complements[index(2, 2)] = this;
        new Molodensky(2, 3, PARAMETERS, complements);
        new Molodensky(3, 2, PARAMETERS, complements);
        new Molodensky(3, 3, PARAMETERS, complements);
    }

    /**
     * Constructs a provider from a set of parameters.
     *
     * @param sourceDimension Number of dimensions in the source CRS of this operation method.
     * @param targetDimension Number of dimensions in the target CRS of this operation method.
     * @param parameters      The set of parameters (never {@code null}).
     * @param complements     Providers for all combinations between 2D and 3D cases.
     */
    Molodensky(final int sourceDimension, final int targetDimension,
               final ParameterDescriptorGroup parameters, final Molodensky[] complements)
    {
        super(sourceDimension, targetDimension, parameters);
        this.complements = complements;
        final int index = index(sourceDimension, targetDimension);
        if (complements[index] != null) {
            throw new AssertionError(index);
        }
        complements[index] = this;
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Returns the dimension declared in the given parameter value, or 0 if none. If
     * this method returns a non-zero value, then it is guaranteed to be either 2 or 3.
     *
     * @param  descriptor The descriptor of the dimension to get.
     * @param  values The values from which to get the dimension.
     * @return The dimension, or 0 if none.
     * @throws InvalidParameterValueException if the dimension parameter has an invalid value.
     */
    private static int dimension(final ParameterDescriptor<Integer> descriptor, final ParameterValueGroup values)
            throws InvalidParameterValueException
    {
        final Integer value = integerValue(descriptor, values);
        if (value == null) {
            return 0;
        }
        final int dimension = value; // Unboxing.
        if (dimension != 2 && dimension != 3) {
            final String name = descriptor.getName().getCode();
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.IllegalArgument_2, name, dimension), name, dimension);
        }
        return dimension;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @param  values The group of parameter values.
     * @return The created math transform.
     * @throws ParameterNotFoundException if a required parameter was not found.
     */
    @Override
    public MathTransform createMathTransform(MathTransformFactory factory, final ParameterValueGroup values)
            throws ParameterNotFoundException
    {
        Integer srcDim = getSourceDimensions();
        Integer tgtDim = getTargetDimensions();
        if (srcDim == null) srcDim = 2; // TODO: temporary patch.
        if (tgtDim == null) tgtDim = 2; // TODO: temporary patch.
        int dimension = dimension(DIM, values);
        if (dimension != 0) {
            srcDim = tgtDim = dimension;
        }
        if (srcDim != 3) {  // We will keep max value.
            dimension = dimension(SRC_DIM, values);
            if (dimension != 0) {
                srcDim = dimension;
            }
        }
        if (tgtDim != 3) {
            dimension = dimension(TGT_DIM, values);
            if (dimension != 0) {
                tgtDim = dimension;
            }
        }
        final double a = doubleValue(SRC_SEMI_MAJOR, values);
        final double b = doubleValue(SRC_SEMI_MINOR, values);
        final double ta, tb;
        double d = doubleValue(AXIS_LENGTH_DIFFERENCE, values);
        ta = Double.isNaN(d) ? doubleValue(TGT_SEMI_MAJOR, values) : a + d;
        d = doubleValue(FLATTENING_DIFFERENCE, values);
        if (Double.isNaN(d)) {
            tb = doubleValue(TGT_SEMI_MINOR, values);
        } else {
            tb = ta*(b/a - d);
        }
        final double dx = doubleValue(DX, values);
        final double dy = doubleValue(DY, values);
        final double dz = doubleValue(DZ, values);
        return MolodenskyTransform.create(isAbridged(),
                a, b, srcDim == 3, ta, tb, tgtDim == 3, dx, dy, dz);
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
        ArgumentChecks.ensureBetween("targetDimensions", 2, 3, targetDimensions);
        return complements[index(sourceDimensions, targetDimensions)];
    }

    /**
     * Returns {@code true} for the abridged formulas.
     * This method is overridden by {@link AbridgedMolodensky}.
     */
    boolean isAbridged() {
        return false;
    }
}
