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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.parameter;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.util.CodeList;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;

import org.apache.sis.measure.Range;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.metadata.iso.citation.Citations;


/**
 * The definition of a parameter used by an operation method. For
 * {@linkplain org.geotoolkit.referencing.crs.AbstractCRS Coordinate Reference Systems}
 * most parameter values are numeric, but other types of parameter values are possible.
 * <p>
 * For numeric values, the {@linkplain #getValueClass value class} is usually
 * {@link Double}, {@link Integer} or some other Java wrapper class.
 * <p>
 * This class contains numerous convenience constructors. But all of them ultimately invoke
 * {@linkplain #DefaultParameterDescriptor(Map,Class,Object[],Object,Comparable,Comparable,Unit,boolean)
 * a single, full-featured constructor}. All other constructors are just shortcuts.
 *
 * @param <T> The type of elements to be returned by {@link ParameterValue#getValue}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 4.00
 *
 * @see Parameter
 * @see DefaultParameterDescriptorGroup
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.parameter.DefaultParameterDescriptor}.
 */
@Immutable
@Deprecated
public class DefaultParameterDescriptor<T> extends org.apache.sis.parameter.DefaultParameterDescriptor<T> {
    /**
     * Serial number for inter-operability with different versions.
     */
//  private static final long serialVersionUID = -295668622297737705L;

    /**
     * Constructs a descriptor with the same values than the specified one. This copy constructor
     * can be used in order to wrap an arbitrary implementation into a Geotk one.
     *
     * @param descriptor The descriptor to copy.
     *
     * @since 2.2
     */
    public DefaultParameterDescriptor(final ParameterDescriptor<T> descriptor) {
        super(descriptor);
    }

    /**
     * Constructs a descriptor for a mandatory parameter having a set of valid values.
     * The descriptor has no minimal or maximal value and no unit.
     *
     * @param name         The parameter name.
     * @param valueClass   The class that describe the type of the parameter.
     * @param validValues  A finite set of valid values (usually from a {@linkplain CodeList
     *                     code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null} if none.
     */
    public DefaultParameterDescriptor(final String   name,
                                      final Class<T> valueClass,
                                      final T[]      validValues,
                                      final T        defaultValue)
    {
        this(Collections.singletonMap(NAME_KEY, name),
             valueClass, validValues, defaultValue, null, null, null, true);
    }

    /**
     * Constructs a descriptor from an authority and a name.
     *
     * @param authority    The authority (example: {@link Citations#OGC OGC}).
     * @param name         The parameter name.
     * @param valueClass   The class that describes the type of the parameter value.
     * @param validValues  A finite set of valid values (usually from a {@linkplain CodeList
     *                     code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null} if none.
     * @param minimum      The minimum parameter value (inclusive), or {@code null} if none.
     * @param maximum      The maximum parameter value (inclusive), or {@code null} if none.
     * @param unit         The unit of measurement for the default, minimum and maximum values,
     *                     or {@code null} if none.
     * @param required     {@code true} if this parameter is required, or {@code false} if it is optional.
     *
     * @since 2.2
     */
    public DefaultParameterDescriptor(final Citation      authority,
                                      final String        name,
                                      final Class<T>      valueClass,
                                      final T[]           validValues,
                                      final T             defaultValue,
                                      final Comparable<T> minimum,
                                      final Comparable<T> maximum,
                                      final Unit<?>       unit,
                                      final boolean       required)
    {
        this(Collections.singletonMap(NAME_KEY, new NamedIdentifier(authority, name)),
             valueClass, validValues, defaultValue, minimum, maximum, unit, required);
    }

    /**
     * Constructs a descriptor from a name and a default value.
     *
     * @param  name         The parameter name.
     * @param  remarks      An optional description as a {@link String} or an {@link InternationalString}.
     * @param  valueClass   The class that describe the type of the parameter.
     * @param  defaultValue The default value.
     * @param  required     {@code true} if this parameter is required, {@code false} otherwise.
     *
     * @since 2.5
     */
    public DefaultParameterDescriptor(final String name, final CharSequence remarks,
            final Class<T> valueClass, final T defaultValue, final boolean required) // LGPL
    {
        this(properties(name, remarks), valueClass, codeList(valueClass),
                defaultValue, null, null, null, required);
    }

    /**
     * Constructs a descriptor from a set of properties. The properties map is given unchanged to the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param properties   Set of properties. Should contains at least {@code "name"}.
     * @param valueClass   The class that describes the type of the parameter value.
     * @param validValues  A finite set of valid values (usually from a {@linkplain CodeList
     *                     code list}) or {@code null} if it doesn't apply.
     * @param defaultValue The default value for the parameter, or {@code null} if none.
     * @param minimum      The minimum parameter value (inclusive), or {@code null} if none.
     * @param maximum      The maximum parameter value (inclusive), or {@code null} if none.
     * @param unit         The unit of measurement for the default, minimum and maximum values,
     *                     or {@code null} if none.
     * @param required     {@code true} if this parameter is required, or {@code false} if it is optional.
     */
    public DefaultParameterDescriptor(final Map<String,?> properties,
                                      final Class<T>      valueClass,
                                      final T[]           validValues,
                                      final T             defaultValue,
                                      final Comparable<T> minimum,
                                      final Comparable<T> maximum,
                                      final Unit<?>       unit,
                                      final boolean       required)
    {
        super(complete(properties, validValues), valueClass,
                (unit != null) ? new MeasurementRange((Class) valueClass, (Number) minimum, true, (Number) maximum, true, unit) :
                (minimum != null || maximum != null) ? new Range(valueClass, minimum, true, maximum, true) : null,
                defaultValue, required);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,?> complete(Map<String,?> properties, final Object[] validValues) {
        if (validValues != null) {
            final Map<String,Object> copy = new HashMap<>(properties);
            copy.put(VALID_VALUES_KEY, validValues);
            properties = copy;
        }
        return properties;
    }

    /**
     * Constructs a descriptor for a mandatory parameter in a range of integer values.
     *
     * @param  name         The parameter name.
     * @param  defaultValue The default value for the parameter.
     * @param  minimum      The minimum parameter value, or {@link Integer#MIN_VALUE} if none.
     * @param  maximum      The maximum parameter value, or {@link Integer#MAX_VALUE} if none.
     * @return The parameter descriptor for the given range of values.
     *
     * @since 2.5
     */
    public static DefaultParameterDescriptor<Integer> create(final String name,
            final int defaultValue, final int minimum, final int maximum)
    {
        return create(Collections.singletonMap(NAME_KEY, name),
                defaultValue, minimum, maximum, true);
    }

    /**
     * Constructs a descriptor for a parameter in a range of integer values.
     *
     * @param  properties   The parameter properties (name, identifiers, alias...).
     * @param  defaultValue The default value for the parameter.
     * @param  minimum      The minimum parameter value, or {@link Integer#MIN_VALUE} if none.
     * @param  maximum      The maximum parameter value, or {@link Integer#MAX_VALUE} if none.
     * @param  required     {@code true} if this parameter is required, {@code false} otherwise.
     * @return The parameter descriptor for the given range of values.
     *
     * @since 2.5
     */
    public static DefaultParameterDescriptor<Integer> create(final Map<String,?> properties,
            final int defaultValue, final int minimum, final int maximum, final boolean required)
    {
        return new DefaultParameterDescriptor<>(properties,
                 Integer.class, null, Integer.valueOf(defaultValue),
                 (minimum == Integer.MIN_VALUE) ? null : Integer.valueOf(minimum),
                 (maximum == Integer.MAX_VALUE) ? null : Integer.valueOf(maximum), null, required);
    }

    /**
     * Constructs a descriptor for a mandatory parameter in a range of floating point values.
     *
     * @param  name         The parameter name.
     * @param  defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param  minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param  maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param  unit         The unit for default, minimum and maximum values.
     * @return The parameter descriptor for the given range of values.
     *
     * @since 2.5
     */
    public static DefaultParameterDescriptor<Double> create(final String name,
            final double defaultValue, final double minimum, final double maximum, final Unit<?> unit)
    {
        return create(Collections.singletonMap(NAME_KEY, name),
                defaultValue, minimum, maximum, unit, true);
    }

    /**
     * Constructs a descriptor for a parameter in a range of floating point values.
     *
     * @param  properties   The parameter properties (name, identifiers, alias...).
     * @param  defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param  minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param  maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param  unit         The unit of measurement for default, minimum and maximum values.
     * @param  required     {@code true} if this parameter is required, {@code false} otherwise.
     * @return The parameter descriptor for the given range of values.
     *
     * @since 2.5
     */
    public static DefaultParameterDescriptor<Double> create(final Map<String,?> properties,
            final double defaultValue, final double minimum, final double maximum,
            final Unit<?> unit, final boolean required)
    {
        return new DefaultParameterDescriptor<>(properties, Double.class, null,
                Double.isNaN(defaultValue)          ? null : Double.valueOf(defaultValue),
                minimum == Double.NEGATIVE_INFINITY ? null : Double.valueOf(minimum),
                maximum == Double.POSITIVE_INFINITY ? null : Double.valueOf(maximum), unit, required);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static <T> T[] codeList(final Class<T> valueClass) {
        T[] codeList = null;
        if (CodeList.class.isAssignableFrom(valueClass)) {
            try {
                @SuppressWarnings("unchecked") // Type checked with reflection.
                final T[] tmp = (T[]) valueClass.getMethod("values",
                        (Class<?>[]) null).invoke(null, (Object[]) null);
                codeList = tmp;
            } catch (ReflectiveOperationException exception) {
                // No code list defined. Not a problem; we will just
                // not provide any set of code to check against.
            }
        }
        return codeList;
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,CharSequence> properties(final String name, final CharSequence remarks) {
        final Map<String,CharSequence> properties;
        if (remarks == null ){
            properties = Collections.singletonMap(NAME_KEY, (CharSequence) name);
        } else {
            properties = new HashMap<>(4);
            properties.put(NAME_KEY,    name);
            properties.put(REMARKS_KEY, remarks);
        }
        return properties;
    }

    /**
     * Creates a new instance of {@code ParameterValue} initialized with the
     * {@linkplain #getDefaultValue default value}. The {@linkplain DefaultParameterDescriptor
     * parameter descriptor} for the created parameter value will be {@code this} object.
     *
     * @return A parameter initialized to the default value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ParameterValue<T> createValue() {
        if (super.getValueClass() == Double.class) {
            return (ParameterValue<T>) new FloatParameter((ParameterDescriptor<Double>) this);
        }
        return new Parameter<>(this);
    }
}
