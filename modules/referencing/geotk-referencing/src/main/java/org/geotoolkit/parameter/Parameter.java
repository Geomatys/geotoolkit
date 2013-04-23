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

import java.io.File;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;

import org.opengis.util.CodeList;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.parameter.InvalidParameterValueException;

import org.apache.sis.measure.Units;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.io.IOUtilities;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A parameter value used by an operation method. Most CRS parameter values are numeric and can
 * be obtained by the {@link #intValue()} or {@link #doubleValue()} methods. But other types of
 * parameter values are possible and can be handled by the more generic {@link #getValue()} and
 * {@link #setValue(Object)} methods. The type and constraints on parameter values are given
 * by the {@linkplain #getDescriptor() descriptor}.
 * <p>
 * Instances of {@code ParameterValue} are created by the {@link ParameterDescriptor#createValue()}
 * method. The parameter type can be fetch with the following idiom:
 *
 * {@preformat java
 *     Class<? extends T> valueClass = parameter.getDescriptor().getValueClass();
 * }
 *
 * {@section Implementation note for subclasses}
 * Except for the constructors, the {@link #equals(Object)} and the {@link #hashCode()} methods,
 * all read and write operations ultimately delegates to the following methods:
 * <p>
 * <ul>
 *   <li>All getter methods will invoke {@link #getValue()} and - if needed - {@link #getUnit()},
 *       then performs their processing on the values returned by those methods.</li>
 *   <li>All setter methods will first convert (if needed) and verify the argument validity,
 *       then pass the values to the {@link #setSafeValue(Object, Unit)} method.</li>
 * </ul>
 * <p>
 * Consequently, the above-cited methods provide single points that subclasses can override
 * for modifying the behavior of all getter and setter methods.
 *
 * @param <T> The value type.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.20
 *
 * @see DefaultParameterDescriptor
 * @see ParameterGroup
 *
 * @since 2.0
 * @module
 */
public class Parameter<T> extends AbstractParameterValue<T> {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5837826787089486776L;

    /**
     * The value, or {@code null} if undefined. Except for the constructors, the
     * {@link #equals(Object)} and the {@link #hashCode()} methods, this field is
     * read only by {@link #getValue()} and written by {@link #setSafeValue(Object, Unit)}.
     */
    private T value;

    /**
     * The unit of measure for the value, or {@code null} if it doesn't apply. Except for the
     * constructors, the {@link #equals(Object)} and the {@link #hashCode()} methods, this field
     * is read only by {@link #getUnit()} and written by {@link #setSafeValue(Object, Unit)}.
     */
    private Unit<?> unit;

    /**
     * Constructs a parameter value from the specified descriptor.
     * The value will be initialized to the default value, if any.
     *
     * @param descriptor The abstract definition of this parameter.
     */
    public Parameter(final ParameterDescriptor<T> descriptor) {
        super(descriptor);
        value = descriptor.getDefaultValue();
        unit  = descriptor.getUnit();
    }

    /**
     * Constructs a parameter value from the specified descriptor and value.
     *
     * @param  descriptor The abstract definition of this parameter.
     * @param  value The parameter value.
     * @throws InvalidParameterValueException if the type of {@code value} is inappropriate
     *         for this parameter, or if the value is illegal for some other reason (for example
     *         the value is numeric and out of range).
     */
    public Parameter(final ParameterDescriptor<T> descriptor, final T value)
            throws InvalidParameterValueException
    {
        super(descriptor);
        unit = descriptor.getUnit();
        setValue(value);
    }

    /**
     * Constructs a parameter from the specified name and value.
     *
     * {@section Proposed alternative}
     * This convenience constructor creates a {@link DefaultParameterDescriptor} object. But
     * if such descriptor is available, then the preferred way to get a {@code ParameterValue}
     * is to invoke {@link ParameterDescriptor#createValue()}.
     *
     * @param  name  The parameter name.
     * @param  value The parameter value.
     * @return A new parameter instance for the given name and value.
     *
     * @since 2.5
     */
    public static Parameter<Integer> create(final String name, final int value) {
        final ParameterDescriptor<Integer> descriptor =
                new DefaultParameterDescriptor<Integer>(name, Integer.class, null, null);
        final Parameter<Integer> parameter = new Parameter<Integer>(descriptor);
        parameter.value = value;
        return parameter;
    }

    /**
     * Constructs a parameter from the specified name, value and unit.
     *
     * {@section Proposed alternative}
     * This convenience constructor creates a {@link DefaultParameterDescriptor} object. But
     * if such descriptor is available, then the preferred way to get a {@code ParameterValue}
     * is to invoke {@link ParameterDescriptor#createValue()}.
     *
     * @param name  The parameter name.
     * @param value The parameter value.
     * @param unit  The unit for the parameter value.
     * @return A new parameter instance for the given name and value.
     *
     * @since 2.5
     */
    public static Parameter<Double> create(final String name, final double value, Unit<?> unit) {
        /*
         * Normalizes the specified unit into one of "standard" units used in projections.
         * This is for the descriptor only; the parameter will use exactly the given unit.
         */
        if (unit != null) {
            if (Units.isLinear(unit)) {
                unit = SI.METRE;
            } else if (Units.isTemporal(unit)) {
                unit = NonSI.DAY;
            } else if (Units.isAngular(unit)) {
                unit = NonSI.DEGREE_ANGLE;
            }
        }
        final ParameterDescriptor<Double> descriptor = DefaultParameterDescriptor.create(
                name, Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, unit);
        final Parameter<Double> parameter = new Parameter<Double>(descriptor);
        parameter.value = value;
        parameter.unit  = unit;
        return parameter;
    }

    /**
     * Constructs a parameter from the specified code list.
     *
     * {@section Proposed alternative}
     * This convenience constructor creates a {@link DefaultParameterDescriptor} object. But
     * if such descriptor is available, then the preferred way to get a {@code ParameterValue}
     * is to invoke {@link ParameterDescriptor#createValue()}.
     *
     * @param  <T>   The parameter type.
     * @param  name  The parameter name.
     * @param  type  The parameter type.
     * @param  value The parameter value.
     * @return A new parameter instance for the given name and value.
     *
     * @since 2.5
     */
    public static <T extends CodeList<T>> Parameter<T> create(final String name, final Class<T> type, final T value) {
        final ParameterDescriptor<T> descriptor = new DefaultParameterDescriptor<T>(name, null, type, null, true);
        final Parameter<T> parameter = new Parameter<T>(descriptor);
        parameter.value = value;
        return parameter;
    }

    /**
     * Formats an error message for illegal method call for the current value type.
     */
    private String getClassTypeError() {
        return Errors.format(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_1,
               ((ParameterDescriptor<?>) descriptor).getValueClass());
    }

    /**
     * Returns the unit of measure of the {@linkplain #doubleValue() parameter value}.
     * If the parameter value has no unit (for example because it is a {@link String} type),
     * then this method returns {@code null}. Note that "no unit" doesn't means
     * "dimensionless".
     *
     * {@section Implementation note for subclasses}
     * All getter methods which need unit information will invoke this {@code getUnit()} method.
     * Subclasses can override this method if they need to compute the unit dynamically.
     *
     * @return The unit of measure, or {@code null} if none.
     *
     * @see #doubleValue()
     * @see #doubleValueList()
     * @see #getValue()
     */
    @Override
    public Unit<?> getUnit() {
        return unit;
    }

    /**
     * Returns the numeric value of the coordinate operation parameter in the specified unit
     * of measure. This convenience method applies unit conversions on the fly as needed.
     *
     * @param  unit The unit of measure for the value to be returned.
     * @return The numeric value represented by this parameter after conversion to type
     *         {@code double} and conversion to {@code unit}.
     * @throws IllegalArgumentException if the specified unit is invalid for this parameter.
     * @throws InvalidParameterTypeException if the value is not a numeric type.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #getUnit()
     * @see #setValue(double,Unit)
     * @see #doubleValueList(Unit)
     */
    @Override
    public double doubleValue(final Unit<?> unit) throws IllegalArgumentException, IllegalStateException {
        final Unit<?> actual = getUnit();
        if (actual == null) {
            throw unitlessParameter(descriptor);
        }
        ensureNonNull("unit", unit);
        final int expectedID = getUnitMessageID(actual);
        if (getUnitMessageID(unit) != expectedID) {
            throw new IllegalArgumentException(Errors.format(expectedID, unit));
        }
        try {
            return actual.getConverterToAny(unit).convert(doubleValue());
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
    }

    /**
     * Returns the numeric value represented by this operation parameter.
     * The units of measurement are specified by {@link #getUnit()}.
     *
     * {@note The behavior of this method is slightly different than the equivalent method in
     * the <code>FloatParameter</code> class, since this method throws an exception instead than
     * returning <code>NaN</code> if no value has been explicitely set. This method behaves that
     * way for consistency will other methods defined in this class, since all of them except
     * <code>getValue()</code> throw an exception in such case.}
     *
     * @return The numeric value represented by this parameter after conversion to type {@code double}.
     *         This method returns {@link Double#NaN} only if such "value" has been explicitely set.
     * @throws InvalidParameterTypeException if the value is not a numeric type.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #getUnit()
     * @see #setValue(double)
     * @see #doubleValueList()
     */
    @Override
    public double doubleValue() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        final String name = getName(descriptor);
        if (value == null) {
            // This is the kind of exception expected by org.geotoolkit.io.wkt.Formatter.
            // If a default value existed, it should has been copied by the constructor or setter methods.
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        // Reminder: the following is a specialization of IllegalStateException.
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns the positive integer value of an operation parameter, usually used
     * for a count. An integer value does not have an associated unit of measure.
     *
     * @return The numeric value represented by this parameter after conversion to type {@code int}.
     * @throws InvalidParameterTypeException if the value is not an integer type.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #setValue(int)
     * @see #intValueList()
     */
    @Override
    public int intValue() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof Number) {
            final int integer = ((Number) value).intValue();
            if (integer == ((Number) value).doubleValue()) {
                return integer;
            }
        }
        final String name = getName(descriptor);
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns the boolean value of an operation parameter.
     * A boolean value does not have an associated unit of measure.
     *
     * @return The boolean value represented by this parameter.
     * @throws InvalidParameterTypeException if the value is not a boolean type.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #setValue(boolean)
     */
    @Override
    public boolean booleanValue() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        final String name = getName(descriptor);
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns the string value of an operation parameter.
     * A string value does not have an associated unit of measure.
     *
     * @return The string value represented by this parameter.
     * @throws InvalidParameterTypeException if the value is not a string.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #getValue()
     * @see #setValue(Object)
     */
    @Override
    public String stringValue() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof CharSequence) {
            return value.toString();
        }
        final String name = getName(descriptor);
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns an ordered sequence of numeric values in the specified unit of measure.
     * This convenience method apply unit conversion on the fly as needed.
     *
     * @param  unit The unit of measure for the value to be returned.
     * @return The sequence of values represented by this parameter after conversion to type
     *         {@code double} and conversion to {@code unit}.
     * @throws IllegalArgumentException if the specified unit is invalid for this parameter.
     * @throws InvalidParameterTypeException if the value is not an array of {@code double}s.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #getUnit()
     * @see #setValue(double[],Unit)
     * @see #doubleValue(Unit)
     */
    @Override
    public double[] doubleValueList(final Unit<?> unit) throws IllegalArgumentException, IllegalStateException {
        final Unit<?> actual = getUnit();
        if (actual == null) {
            throw unitlessParameter(descriptor);
        }
        ensureNonNull("unit", unit);
        final int expectedID = getUnitMessageID(actual);
        if (getUnitMessageID(unit) != expectedID) {
            throw new IllegalArgumentException(Errors.format(expectedID, unit));
        }
        final UnitConverter converter;
        try {
            converter = actual.getConverterToAny(unit);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        final double[] values = doubleValueList().clone();
        for (int i=0; i<values.length; i++) {
            values[i] = converter.convert(values[i]);
        }
        return values;
    }

    /**
     * Returns an ordered sequence of two or more numeric values of an operation parameter
     * list, where each value has the same associated {@linkplain Unit unit of measure}.
     *
     * @return The sequence of values represented by this parameter.
     * @throws InvalidParameterTypeException if the value is not an array of {@code double}s.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #getUnit()
     * @see #setValue(Object)
     * @see #doubleValue()
     */
    @Override
    public double[] doubleValueList() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof double[]) {
            return (double[]) value;
        }
        final String name = getName(descriptor);
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns an ordered sequence of two or more integer values of an operation parameter list,
     * usually used for counts. These integer values do not have an associated unit of measure.
     *
     * @return The sequence of values represented by this parameter.
     * @throws InvalidParameterTypeException if the value is not an array of {@code int}s.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #setValue(Object)
     * @see #intValue()
     */
    @Override
    public int[] intValueList() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof int[]) {
            return (int[]) value;
        }
        final String name = getName(descriptor);
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns a reference to a file or a part of a file containing one or more parameter
     * values. When referencing a part of a file, that file must contain multiple identified
     * parts, such as an XML encoded document. Furthermore, the referenced file or part of a
     * file can reference another part of the same or different files, as allowed in XML documents.
     *
     * @return The reference to a file containing parameter values.
     * @throws InvalidParameterTypeException if the value is not a reference to a file or an URI.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     *
     * @see #getValue()
     * @see #setValue(Object)
     */
    @Override
    public URI valueFile() throws IllegalStateException {
        final T value = getValue();
        if (value instanceof URI) {
            return (URI) value;
        }
        if (value instanceof File) {
            return ((File) value).toURI();
        }
        Exception cause = null;
        try {
            if (value instanceof URL) {
                return ((URL) value).toURI();
            }
            if (value instanceof String) {
                return new URI(IOUtilities.encodeURI((String) value));
            }
        } catch (URISyntaxException exception) {
            cause = exception;
        }
        /*
         * Value can't be converted.
         */
        final String name = getName(descriptor);
        if (value == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), cause, name);
    }

    /**
     * Returns the parameter value as an object. The object type is typically a {@link Double},
     * {@link Integer}, {@link Boolean}, {@link String}, {@link URI}, {@code double[]} or
     * {@code int[]}. If no value has been set, then this method returns the
     * {@linkplain ParameterDescriptor#getDefaultValue() default value} (which may be null).
     *
     * {@section Implementation note for subclasses}
     * All getter methods will invoke this {@code getValue()} method. Subclasses can override
     * this method if they need to compute the value dynamically.
     *
     * @return The parameter value as an object, or {@code null} if no value has been set and
     *         there is no default value.
     *
     * @see #setValue(Object)
     */
    @Override
    public T getValue() {
        return value;
    }

    /**
     * Sets the parameter value as a floating point and its associated unit.
     * The default implementation verifies the argument validity, then invokes
     * {@link #setSafeValue(Object, Unit)}.
     *
     * @param  value The parameter value.
     * @param  unit The unit for the specified value.
     * @throws InvalidParameterValueException if the floating point type is inappropriate for this
     *         parameter, or if the value is illegal for some other reason (for example a value out
     *         of range).
     *
     * @see #setValue(double)
     * @see #doubleValue(Unit)
     */
    @Override
    public void setValue(final double value, final Unit<?> unit) throws InvalidParameterValueException {
        ensureNonNull("unit", unit);
        @SuppressWarnings("unchecked") // Checked by constructor.
        final ParameterDescriptor<T> descriptor = (ParameterDescriptor<T>) this.descriptor;
        final Unit<?> targetUnit = descriptor.getUnit();
        if (targetUnit == null) {
            throw unitlessParameter(descriptor);
        }
        final int expectedID = getUnitMessageID(targetUnit);
        if (getUnitMessageID(unit) != expectedID) {
            throw new InvalidParameterValueException(Errors.format(expectedID, unit),
                      descriptor.getName().getCode(), value);
        }
        final Double converted;
        try {
            converted = unit.getConverterToAny(targetUnit).convert(value);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        ensureValidValue(descriptor, converted);
        /*
         * Really store the original value, not the converted one, because we store the given
         * unit as well. Conversions will be applied on the fly by the getter method if needed.
         */
        setSafeValue(descriptor.getValueClass().cast(value), unit);
    }

    /**
     * Sets the parameter value as a floating point. The unit, if any, stay unchanged.
     * The default implementation verifies the argument validity, then invokes
     * {@link #setSafeValue(Object, Unit)}.
     *
     * @param value The parameter value.
     * @throws InvalidParameterValueException if the floating point type is inappropriate for this
     *         parameter, or if the value is illegal for some other reason (for example a value out
     *         of range).
     *
     * @see #setValue(double,Unit)
     * @see #doubleValue()
     */
    @Override
    @SuppressWarnings("unchecked") // Safe because type was checked by the constructor.
    public void setValue(final double value) throws InvalidParameterValueException {
        setSafeValue(ensureValidValue((ParameterDescriptor<T>) descriptor, Double.valueOf(value)), unit);
        // Really 'unit', not 'getUnit()' since units are not expected to be involved in this method.
        // We just want the current unit setting to be unchanged.
    }

    /**
     * Sets the parameter value as an integer. The default implementation performs a
     * choice based on the {@linkplain ParameterDescriptor#getValueClass() value class}:
     * <p>
     * <ul>
     *   <li>If the value type is {@code Float.class} or {@code Double.class},
     *       then this method delegates to {@link #setValue(double)}.</li>
     *   <li>Otherwise this method verifies the argument validity, then
     *       invokes {@link #setSafeValue(Object, Unit)}.</li>
     * </ul>
     *
     * @param  value The parameter value.
     * @throws InvalidParameterValueException if the integer type is inappropriate for this parameter,
     *         or if the value is illegal for some other reason (for example a value out of range).
     *
     * @see #intValue()
     */
    @Override
    public void setValue(final int value) throws InvalidParameterValueException {
        @SuppressWarnings("unchecked") // Checked by constructor.
        final ParameterDescriptor<T> descriptor = (ParameterDescriptor<T>) this.descriptor;
        final Class<T> type = descriptor.getValueClass();
        if (type == Double.class || type == Float.class) {
            setValue((double) value);
        } else {
            setSafeValue(ensureValidValue(descriptor, Integer.valueOf(value)), unit);
            // Really 'unit', not 'getUnit()' since we don't expect units in this context.
            // We just want the current unit setting to be unchanged as a paranoiac safety.
        }
    }

    /**
     * Sets the parameter value as a boolean.
     * The default implementation verifies the argument validity, then invokes
     * {@link #setSafeValue(Object, Unit)}.
     *
     * @param  value The parameter value.
     * @throws InvalidParameterValueException if the boolean type is inappropriate for this parameter.
     *
     * @see #booleanValue()
     */
    @Override
    @SuppressWarnings("unchecked") // Safe because type was checked by the constructor.
    public void setValue(final boolean value) throws InvalidParameterValueException {
        setSafeValue(ensureValidValue(((ParameterDescriptor<T>) descriptor), Boolean.valueOf(value)), unit);
        // Really 'unit', not 'getUnit()' since we don't expect units in this context.
        // We just want the current unit setting to be unchanged as a paranoiac safety.
    }

    /**
     * Sets the parameter value as an object. The object type is typically a {@link Double},
     * {@link Integer}, {@link Boolean}, {@link String}, {@link URI}, {@code double[]}
     * or {@code int[]}.
     * <p>
     * The default implementation verifies the argument validity, then invokes
     * {@link #setSafeValue(Object, Unit)}.
     *
     * @param  value The parameter value, or {@code null} to restore the default value.
     * @throws InvalidParameterValueException if the type of {@code value} is inappropriate
     *         for this parameter, or if the value is illegal for some other reason (for example
     *         the value is numeric and out of range).
     *
     * @see #getValue()
     */
    @Override
    @SuppressWarnings("unchecked") // Safe because type was checked by the constructor.
    public void setValue(final Object value) throws InvalidParameterValueException {
        setSafeValue(ensureValidValue((ParameterDescriptor<T>) descriptor, value), unit);
        // Really 'unit', not 'getUnit()' since we don't expect units in this context.
        // We just want the current unit setting to be unchanged as a paranoiac safety.
    }

    /**
     * Sets the parameter value as an array of floating point and their associated unit.
     * The default implementation verifies the argument validity, then invokes
     * {@link #setSafeValue(Object, Unit)}.
     *
     * @param  values The parameter values.
     * @param  unit The unit for the specified value.
     * @throws InvalidParameterValueException if the floating point type is inappropriate for this
     *         parameter, or if the value is illegal for some other reason (for example a value out
     *         of range).
     */
    @Override
    public void setValue(double[] values, final Unit<?> unit) throws InvalidParameterValueException {
        ensureNonNull("unit", unit);
        @SuppressWarnings("unchecked") // Checked by constructor.
        final ParameterDescriptor<T> descriptor = (ParameterDescriptor<T>) this.descriptor;
        final Unit<?> targetUnit = descriptor.getUnit();
        if (targetUnit == null) {
            throw unitlessParameter(descriptor);
        }
        final int expectedID = getUnitMessageID(targetUnit);
        if (getUnitMessageID(unit) != expectedID) {
            throw new IllegalArgumentException(Errors.format(expectedID, unit));
        }
        final double[] converted = values.clone();
        final UnitConverter converter;
        try {
            converter = unit.getConverterToAny(targetUnit);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        for (int i=0; i<converted.length; i++) {
            converted[i] = converter.convert(converted[i]);
        }
        setSafeValue(ensureValidValue(descriptor, converted), unit);
    }

    /**
     * Invoked by all {@code setXXX(…)} methods after the argument has been verified to be safe.
     * Subclasses can override this method if they want to perform more processing on the value
     * before its storage, or to be notified about value changes.
     *
     * @param value The new parameter value, or {@code null} for removing the value currently set.
     * @param unit  The unit associated to the new parameter value, or {@code null}.
     *
     * @since 3.20
     */
    protected void setSafeValue(final T value, final Unit<?> unit) {
        this.value = value;
        this.unit  = unit;
        fireValueChanged();
    }

    /**
     * Compares the specified object with this parameter for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final Parameter<?> that = (Parameter<?>) object;
            return Utilities.equals(this.value, that.value) &&
                   Utilities.equals(this.unit,  that.unit);
        }
        return false;
    }

    /**
     * Returns a hash value for this parameter.
     *
     * @return The hash code value. This value doesn't need to be the same
     *         in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        int code = 31 * super.hashCode();
        if (value != null) code +=   value.hashCode();
        if (unit  != null) code += 31*unit.hashCode();
        return code ^ (int)serialVersionUID;
    }

    /**
     * Returns a clone of this parameter.
     */
    @Override
    public Parameter<T> clone() {
        return (Parameter<T>) super.clone();
    }
}
