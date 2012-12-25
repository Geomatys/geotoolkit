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

import java.net.URI;
import javax.measure.unit.Unit;
import javax.measure.converter.ConversionException;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A parameter value as a floating point (double precision) number.
 * This class provides the same functionalities than {@link Parameter}, except that:
 * <p>
 * <ul>
 *   <li>Values are always floating point numbers of type {@code double}.</li>
 *   <li>Units are the same than the {@linkplain ParameterDescriptor#getUnit() default units}.</li>
 * </ul>
 * <p>
 * When those conditions are meet, {@code FloatParameter} is slightly more efficient
 * than {@code Parameter} since it avoid the creation of {@link Double} wrapper objects.
 *
 * {@section Implementation note for subclasses}
 * Except for the constructors, the {@link #equals(Object)} and the {@link #hashCode()} methods,
 * all read and write operations ultimately delegates to the following methods:
 * <p>
 * <ul>
 *   <li>All getter methods will invoke {@link #doubleValue()} and - if needed - {@link #getUnit()},
 *       then performs their processing on the values returned by those methods.</li>
 *   <li>All setter methods will first check the argument validity,
 *       then pass the values to the {@link #setValue(double, Unit)} method.</li>
 * </ul>
 * <p>
 * Consequently, the above-cited methods provide single points that subclasses can override
 * for modifying the behavior of all getter and setter methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see DefaultParameterDescriptor
 * @see ParameterGroup
 *
 * @since 2.0
 * @module
 */
public class FloatParameter extends AbstractParameterValue<Double> {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 9027797654033417816L;

    /**
     * The value, or {@code NaN} if undefined. Except for the constructors, the
     * {@link #equals(Object)} and the {@link #hashCode()} methods, this field is
     * read only by {@link #doubleValue()} and written by {@link #setValue(double, Unit)}.
     */
    private double value;

    /**
     * The unit of measure for the value, or {@code null} if it doesn't apply. Except for the
     * constructors, the {@link #equals(Object)} and the {@link #hashCode()} methods, this field
     * is read only by {@link #getUnit()} and written by {@link #setValue(double, Unit)}.
     */
    private Unit<?> unit;

    /**
     * Constructs a parameter from the specified descriptor.
     *
     * @param  descriptor The abstract definition of this parameter.
     * @throws IllegalArgumentException if the value class is not {@code Double.class}.
     */
    public FloatParameter(final ParameterDescriptor<Double> descriptor) {
        super(descriptor);
        final Class<Double> type = descriptor.getValueClass();
        final Class<Double> expected = Double.class;
        if (!expected.equals(type) && !Double.TYPE.equals(type)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_CLASS_$2, type, expected));
        }
        value = defaultValue(descriptor);
        unit  = descriptor.getUnit();
    }

    /**
     * Returns the default value from the given parameter descriptor, or {@code NaN} if none.
     */
    private static double defaultValue(final ParameterDescriptor<Double> descriptor) {
        final Number value = (Number) descriptor.getDefaultValue();
        return (value != null) ? value.doubleValue() : Double.NaN;
    }

    /**
     * Constructs a parameter from the specified descriptor and value. This convenience
     * constructor is equivalents to the one-argument constructor followed by a call to
     * {@link #setValue(double)}.
     *
     * @param  descriptor The abstract definition of this parameter.
     * @param  value The parameter value.
     * @throws IllegalArgumentException if the value class is not {@code Double.class}.
     */
    public FloatParameter(final ParameterDescriptor<Double> descriptor, final double value) {
        this(descriptor);
        setValue(value);
    }

    /**
     * Returns the unit of measure of the {@linkplain #doubleValue() parameter value}.
     * The default value is {@link ParameterDescriptor#getUnit()}.
     *
     * {@section Implementation note for subclasses}
     * All getter methods which need unit information will invoke this {@code getUnit()} method.
     * Subclasses can override this method if they need to compute the unit dynamically.
     *
     * @return The unit of measure, or {@code null} if none.
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
     * @return The numeric value represented by this parameter after conversion to {@code unit},
     *         or {@link Double#NaN} if none.
     * @throws IllegalArgumentException if the specified unit is invalid for this parameter.
     */
    @Override
    public double doubleValue(final Unit<?> unit) throws IllegalArgumentException {
        ensureNonNull("unit", unit);
        final Unit<?> thisUnit = getUnit();
        if (thisUnit == null) {
            throw unitlessParameter(descriptor);
        }
        final int expectedID = getUnitMessageID(thisUnit);
        if (getUnitMessageID(unit) != expectedID) {
            throw new IllegalArgumentException(Errors.format(expectedID, unit));
        }
        try {
            return thisUnit.getConverterToAny(unit).convert(doubleValue());
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_$1, unit), e);
        }
    }

    /**
     * Returns the numeric value represented by this operation parameter.
     * The units of measurement are specified by {@link #getUnit()}.
     *
     * {@section Implementation note for subclasses}
     * All getter methods will invoke this {@code doubleValue()} method. Subclasses can override
     * this method if they need to compute the value dynamically.
     *
     * @return The numeric value represented by this parameter, or {@link Double#NaN} if none.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * Returns the numeric value casted to integer. If the current {@linkplain #doubleValue()
     * double value} can not be casted without precision loss, then this method throws an
     * exception.
     *
     * @return The numeric value represented by this parameter after conversion to type {@code int}.
     * @throws InvalidParameterTypeException If the value can not be casted to integer.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     */
    @Override
    public int intValue() throws IllegalStateException {
        final double value = doubleValue();
        final int integer = (int) value;
        if (integer == value) {
            return integer;
        }
        final String name = getName(descriptor);
        if (Double.isNaN(value)) {
            // If a default value existed, it should has been copied by the constructor or setter methods.
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_$1, name));
        }
        throw new InvalidParameterTypeException(getClassTypeError(), name);
    }

    /**
     * Returns {@code true} if the value is different from 0, or {@code false} otherwise.
     *
     * @return The boolean value represented by this parameter.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     */
    @Override
    public boolean booleanValue() throws IllegalStateException {
        final double value = doubleValue();
        if (Double.isNaN(value)) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_$1, getName(descriptor)));
        }
        return value != 0;
    }

    /**
     * Returns the string representation of the value.
     *
     * @return The string value represented by this parameter.
     * @throws IllegalStateException if the value is not defined and there is no default value.
     */
    @Override
    public String stringValue() throws IllegalStateException {
        final double value = doubleValue();
        if (Double.isNaN(value)) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_PARAMETER_$1, getName(descriptor)));
        }
        return String.valueOf(value);
    }

    /**
     * Wraps the value in an array of length 0 or 1.
     *
     * @param  unit The unit of measure for the value to be returned.
     * @return The sequence of values represented by this parameter after conversion to type
     *         {@code double} and conversion to {@code unit}.
     * @throws IllegalArgumentException if the specified unit is invalid for this parameter.
     */
    @Override
    public double[] doubleValueList(final Unit<?> unit) throws IllegalArgumentException {
        final double value = doubleValue(unit);
        return Double.isNaN(value) ? XArrays.EMPTY_DOUBLE: new double[] {value};
    }

    /**
     * Wraps the value in an array of length 0 or 1.
     *
     * @return The sequence of values represented by this parameter.
     */
    @Override
    public double[] doubleValueList() {
        final double value = doubleValue();
        return Double.isNaN(value) ? XArrays.EMPTY_DOUBLE: new double[] {value};
    }

    /**
     * Wraps the value in an array of length 1.
     *
     * @return The sequence of values represented by this parameter.
     * @throws InvalidParameterTypeException If the value can not be casted to integer.
     */
    @Override
    public int[] intValueList() throws InvalidParameterTypeException {
        return new int[] {intValue()};
    }

    /**
     * Always throws an exception, since this parameter is not an URI.
     *
     * @return Never return.
     * @throws InvalidParameterTypeException The value is not a reference to a file or an URI.
     */
    @Override
    public URI valueFile() throws InvalidParameterTypeException {
        throw new InvalidParameterTypeException(getClassTypeError(), getName(descriptor));
    }

    /**
     * Format an error message for illegal method call for the current value type.
     */
    private static String getClassTypeError() {
        return Errors.format(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_$1, Double.class);
    }

    /**
     * Returns the parameter value as a {@link Double}, or {@code null} if none.
     *
     * @return The parameter value as an object, or {@code null}
     *         if the current value is {@link Double#NaN}.
     */
    @Override
    public Double getValue() {
        final double value = doubleValue();
        return Double.isNaN(value) ? null : Double.valueOf(value);
    }

    /**
     * Sets the parameter value as a floating point and its associated unit.
     * The default implementation verifies the arguments validity, then stores the given values.
     *
     * {@section Implementation note for subclasses}
     * This method is invoked by all other {@code setXXX(…)} methods. Subclasses can override
     * this method if they want to perform more processing on the value before its storage,
     * or to be notified about value changes.
     *
     * @param  value The parameter value.
     * @param  unit The new unit of measurement.
     * @throws InvalidParameterValueException if the value is illegal for some reason
     *         (for example a value out of range).
     */
    @Override
    public void setValue(final double value, final Unit<?> unit) throws InvalidParameterValueException {
        double converted = value;
        @SuppressWarnings("unchecked") // Checked by constructor.
        final ParameterDescriptor<Double> descriptor = (ParameterDescriptor<Double>) this.descriptor;
        final Unit<?> targetUnit = descriptor.getUnit();
        if (targetUnit != unit) {
            if (targetUnit == null) {
                throw unitlessParameter(descriptor);
            } else {
                ensureNonNull("unit", unit);
                final int expectedID = getUnitMessageID(targetUnit);
                if (getUnitMessageID(unit) != expectedID) {
                    throw new InvalidParameterValueException(Errors.format(expectedID, unit),
                            descriptor.getName().getCode(), value);
                }
                try {
                    converted = unit.getConverterToAny(targetUnit).convert(value);
                } catch (ConversionException e) {
                    throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_$1, unit), e);
                }
            }
        }
        /*
         * Really store the original value, not the converted one, because we store the given
         * unit as well. Conversions will be applied on the fly by the getter method if needed.
         */
        if (Double.isNaN(value)) {
            this.value = defaultValue(descriptor);
        } else {
            ensureValidValue(descriptor, converted);
            this.value = value;
        }
        this.unit = unit; // Store only after successful validation.
        fireValueChanged();
    }

    /**
     * Sets the parameter value as a floating point.
     * The default implementation delegates to {@link #setValue(double, Unit)}.
     *
     * @param value The parameter value.
     * @throws InvalidParameterValueException if the value is illegal for some reason
     *         (for example a value out of range).
     */
    @Override
    public void setValue(final double value) throws InvalidParameterValueException {
        setValue(value, unit);
    }

    /**
     * Sets the parameter value as an integer.
     * The default implementation delegates to {@link #setValue(double)}.
     *
     * @param  value The parameter value.
     * @throws InvalidParameterValueException if the value is illegal for some reason
     *         (for example a value out of range).
     */
    @Override
    public void setValue(final int value) throws InvalidParameterValueException {
        setValue((double) value);
    }

    /**
     * Sets the parameter value as a boolean. The default implementation delegates to
     * {@link #setValue(double)} with value 1 for {@code true} or 0 or {@code false}.
     *
     * @param  value The parameter value.
     * @throws InvalidParameterValueException if the boolean type is inappropriate for this parameter.
     */
    @Override
    public void setValue(final boolean value) throws InvalidParameterValueException {
        setValue(value ? 1.0 : 0.0);
    }

    /**
     * Sets the parameter value as a {@link Double} object. The default implementation ensures
     * that the given value is a {@link Number}, then delegates to {@link #setValue(double)}.
     *
     * @param  value The parameter value, or {@code null} for {@link Double#NaN}.
     * @throws InvalidParameterValueException if the type of {@code value} is inappropriate
     *         for this parameter, or if the value is illegal for some other reason (for example
     *         the value is numeric and out of range).
     */
    @Override
    public void setValue(final Object value) throws InvalidParameterValueException {
        final double number;
        if (value == null) {
            number = Double.NaN;
        } else if (value instanceof Number) {
            number = ((Number) value).doubleValue();
        } else {
            throw new InvalidParameterValueException(getClassTypeError(), getName(descriptor), value);
        }
        setValue(number);
    }

    /**
     * If the length of the given array is 1, delegates to {@link #setValue(double, Unit)}.
     * Otherwise throws an exception, since this parameter does not accept arbitrary arrays.
     */
    @Override
    public void setValue(final double[] values, final Unit<?> unit) throws InvalidParameterValueException {
        ensureNonNull("values", values);
        if (values.length == 1) {
            setValue(values[0], unit);
        } else {
            throw new InvalidParameterValueException(getClassTypeError(), getName(descriptor), values);
        }
    }

    /**
     * Compares the specified object with this parameter for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            final FloatParameter that = (FloatParameter) object;
            return Double.doubleToLongBits(this.value) ==
                   Double.doubleToLongBits(that.value);
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
        return Utilities.hash(value, super.hashCode());
    }

    /**
     * Returns a clone of this parameter.
     */
    @Override
    public FloatParameter clone() {
        return (FloatParameter) super.clone();
    }
}
