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

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.Objects;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.opengis.util.CodeList;
import org.opengis.util.InternationalString;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.referencing.IdentifiedObject;

import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.metadata.iso.citation.Citations;

import static org.geotoolkit.util.Utilities.*;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.util.ArgumentChecks.ensureCanCast;


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
 * @version 3.18
 *
 * @see Parameter
 * @see DefaultParameterDescriptorGroup
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultParameterDescriptor<T> extends AbstractParameterDescriptor
        implements ParameterDescriptor<T>
{
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -295668622297737705L;

    /**
     * Some frequently used {@link Double} values. As of Java 6, those values don't
     * seem to be cached by {@link Double#valueOf} like JDK does for integers.
     */
    private static final Map<Double,Double> CACHE = new HashMap<>(13);
    static {
        cache(   0.0);
        cache(   1.0);
        cache( -90.0);
        cache( +90.0);
        cache(-180.0);
        cache(+180.0);
        cache(-180.0*60*60);
        cache(+180.0*60*60);
        cache(Double.NEGATIVE_INFINITY);
        cache(Double.POSITIVE_INFINITY);
    }

    /**
     * Helper method for the construction of the {@link #CACHE} map.
     */
    private static void cache(final Double value) {
        if (CACHE.put(value,value) != null) {
            throw new AssertionError(value);
        }
    }

    /**
     * If the given value is presents in the cache, returns the cached value.
     */
    private static <T> T cached(final T value) {
        @SuppressWarnings("unchecked")
        final T candidate = (T) (Object) CACHE.get(value);
        return (candidate != null) ? candidate : value;
    }

    /**
     * The class that describe the type of the parameter.
     * This is the value class that the user specified at construction time.
     */
    private final Class<T> valueClass;

    /**
     * A immutable, finite set of valid values (usually from a {@linkplain CodeList code list})
     * or {@code null} if it doesn't apply. This set is immutable.
     */
    private final Set<T> validValues;

    /**
     * The default value for the parameter, or {@code null}.
     */
    private final T defaultValue;

    /**
     * The minimum parameter value, or {@code null}.
     */
    private final Comparable<T> minimum;

    /**
     * The maximum parameter value, or {@code null}.
     */
    private final Comparable<T> maximum;

    /**
     * The unit for default, minimum and maximum values, or {@code null}.
     */
    private final Unit<?> unit;

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
        valueClass   = descriptor.getValueClass();
        validValues  = descriptor.getValidValues();
        defaultValue = descriptor.getDefaultValue();
        minimum      = descriptor.getMinimumValue();
        maximum      = descriptor.getMaximumValue();
        unit         = descriptor.getUnit();
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
            final Class<T> valueClass, final T defaultValue, final boolean required)
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
        super(properties, required ? 1 : 0, 1);
        this.valueClass   = valueClass;
        this.defaultValue = cached(defaultValue);
        this.minimum      = cached(minimum);
        this.maximum      = cached(maximum);
        this.unit         = unit;
        ensureNonNull("valueClass", valueClass);
        ensureCanCast("defaultValue", valueClass, defaultValue);
        ensureCanCast("minimum",      valueClass, minimum);
        ensureCanCast("maximum",      valueClass, maximum);
        if (minimum!=null && maximum!=null) {
            if (minimum.compareTo(valueClass.cast(maximum)) > 0) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_RANGE_2, minimum, maximum));
            }
        }
        if (validValues != null) {
            final Set<T> valids = new HashSet<>(Math.max(XCollections.hashMapCapacity(validValues.length), 8));
            for (int i=0; i<validValues.length; i++) {
                final T value = cached(validValues[i]);
                ensureCanCast("validValues", valueClass, value);
                valids.add(value);
            }
            this.validValues = XCollections.unmodifiableSet(valids);
        } else {
            this.validValues = null;
        }
        AbstractParameter.ensureValidValue(this, defaultValue);
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
     * The maximum number of times that values for this parameter group or parameter can be
     * included. For a {@linkplain DefaultParameterDescriptor single parameter}, the value
     * is always 1.
     *
     * @return The maximum occurrence.
     *
     * @see #getMinimumOccurs
     */
    @Override
    public int getMaximumOccurs() {
        return 1;
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
        if (valueClass == Double.class) {
            return (ParameterValue<T>) new FloatParameter((ParameterDescriptor<Double>) this);
        }
        return new Parameter<>(this);
    }

    /**
     * Returns the class that describe the type of the parameter.
     *
     * @return The parameter value class.
     */
    @Override
    public Class<T> getValueClass() {
        return valueClass;
    }

    /**
     * If this parameter allows only a finite set of values, returns this set. This set is
     * usually a {@linkplain CodeList code list} or enumerations. This method returns
     * {@code null} if this parameter doesn't limits values to a finite set.
     *
     * @return A finite set of valid values (usually from a {@linkplain CodeList code list}),
     *         or {@code null} if it doesn't apply.
     */
    @Override
    public Set<T> getValidValues() {
        return validValues;
    }

    /**
     * Returns the default value for the parameter. The return type can be any type
     * including a {@link Number} or a {@link String}. If there is no default value,
     * then this method returns {@code null}.
     *
     * @return The default value, or {@code null} in none.
     */
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the minimum parameter value. If there is no minimum value, or if minimum
     * value is inappropriate for the {@linkplain #getValueClass parameter type}, then
     * this method returns {@code null}.
     *
     * @return The minimum parameter value (often an instance of {@link Double}), or {@code null}.
     */
    @Override
    public Comparable<T> getMinimumValue() {
        return minimum;
    }

    /**
     * Returns the maximum parameter value. If there is no maximum value, or if maximum
     * value is inappropriate for the {@linkplain #getValueClass parameter type}, then
     * this method returns {@code null}.
     *
     * @return The minimum parameter value (often an instance of {@link Double}), or {@code null}.
     */
    @Override
    public Comparable<T> getMaximumValue() {
        return maximum;
    }

    /**
     * Returns the unit for {@linkplain #getDefaultValue default},
     * {@linkplain #getMinimumValue minimum} and
     * {@linkplain #getMaximumValue maximum} values.
     * This attribute apply only if the values is of numeric type
     * (usually an instance of {@link Double}).
     *
     * @return The unit for numeric value, or {@code null} if it doesn't apply to the value type.
     */
    @Override
    public Unit<?> getUnit() {
        return unit;
    }

    /**
     * Compares the specified object with this parameter for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                default: {
                    /*
                     * Tests for name, since parameters with different name have
                     * completely different meaning. For example there is no difference
                     * between "semi_major" and "semi_minor" parameters except the name.
                     * We don't perform this comparison if the user asked for metadata
                     * comparison, because in such case the names have already been
                     * compared by the subclass.
                     */
                    final IdentifiedObject that = (IdentifiedObject) object;
                    if (!nameMatches(that. getName().getCode()) &&
                        !IdentifiedObjects.nameMatches(that, getName().getCode()))
                    {
                        return false;
                    }
                    // Fall through
                }
                case BY_CONTRACT: {
                    final ParameterDescriptor<?> that = (ParameterDescriptor<?>) object;
                    return Objects.    equals(getValidValues(),  that.getValidValues())  &&
                           Objects.deepEquals(getDefaultValue(), that.getDefaultValue()) &&
                           Objects.    equals(getMinimumValue(), that.getMinimumValue()) &&
                           Objects.    equals(getMaximumValue(), that.getMaximumValue()) &&
                           Objects.    equals(getUnit(),         that.getUnit());
                }
                case STRICT: {
                    final DefaultParameterDescriptor<?> that = (DefaultParameterDescriptor<?>) object;
                    return Objects.    equals(this.validValues,  that.validValues)  &&
                           Objects.deepEquals(this.defaultValue, that.defaultValue) &&
                           Objects.    equals(this.minimum,      that.minimum)      &&
                           Objects.    equals(this.maximum,      that.maximum)      &&
                           Objects.    equals(this.unit,         that.unit);
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(valueClass, hash(deepHashCode(defaultValue),
               hash(minimum, hash(maximum, hash(unit, super.computeHashCode())))));
    }

    /**
     * Returns a string representation of this descriptor. The string returned by this
     * method is for information purpose only and may change in future version.
     *
     * @since 3.17
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(this))
                .append("[\"").append(getName().getCode()).append("\", ")
                .append(getMinimumOccurs() == 0 ? "optional" : "mandatory");
        buffer.append(", class=").append(Classes.getShortName(valueClass));
        if (minimum != null || maximum != null) {
            buffer.append(", valid=[").append(minimum != null ? minimum : "-\u221E")
                  .append(" \u2026 ") .append(maximum != null ? maximum :  "\u221E").append(']');
        } else if (validValues != null) {
            buffer.append(", valid=").append(validValues);
        }
        if (defaultValue != null) {
            buffer.append(", default=").append(defaultValue);
        }
        if (unit != null) {
            buffer.append(", unit=").append(unit);
        }
        return buffer.append(']').toString();
    }
}
