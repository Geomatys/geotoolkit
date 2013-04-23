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
package org.geotoolkit.parameter;

import java.net.URI;
import java.util.Objects;
import javax.measure.unit.Unit;
import javax.media.jai.ParameterList;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.InvalidParameterTypeException;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;


/**
 * A particular parameter in a JAI's {@link ParameterList}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.2
 * @module
 */
final class ImagingParameter<T> extends AbstractParameterValue<T> {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -170895429717041733L;

    /**
     * The JAI's parameter list used as the backing store for parameter values.
     */
    private final ParameterList parameters;

    /**
     * Creates a new parameter from the specified list.
     */
    private ImagingParameter(final ParameterDescriptor<T> descriptor, final ParameterList parameters) {
        super(descriptor);
        this.parameters = parameters;
    }

    /**
     * Creates a new parameter from the specified list. Constructor provided as a static
     * method instead than a classical object constructor in order to allow instantiation
     * with {@code <?>} parameter type.
     */
    public static <T> ImagingParameter<T> create(final ParameterDescriptor<T> descriptor,
                                                 final ParameterList parameters)
    {
        return new ImagingParameter<>(descriptor, parameters);
    }

    /**
     * Returns the exception to be throws for an operation on a wrong parameter type.
     */
    private InvalidParameterTypeException invalidType(final ClassCastException cause) {
        return new InvalidParameterTypeException(
                Errors.format(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_1, getType()),
                cause, getName(descriptor));
    }

    /**
     * Returns the unlocalized operation name. This is different from
     * {@link AbstractParameter#getName}, which may returns a localized name.
     */
    private String getName() {
        return descriptor.getName().getCode();
    }

    /**
     * Returns the parameter type.
     */
    private Class<T> getType() {
        return getDescriptor().getValueClass();
    }

    /**
     * Returns {@code null} since JAI's parameters have no units.
     */
    @Override
    public Unit<?> getUnit() {
        return null;
    }

    /**
     * Always throws an exception, since this parameter has no unit.
     */
    @Override
    public double doubleValue(final Unit<?> unit) throws InvalidParameterTypeException {
        throw unitlessParameter(descriptor);
    }

    /**
     * Returns the numeric value of the coordinate operation parameter.
     */
    @Override
    public double doubleValue() throws InvalidParameterTypeException {
        final String   name = getName();
        final Class<T> type = getType();
        try {
            if (type.equals(Float  .class)) parameters.getFloatParameter(name);
            if (type.equals(Long   .class)) parameters.getLongParameter (name);
            if (type.equals(Integer.class)) parameters.getIntParameter  (name);
            if (type.equals(Short  .class)) parameters.getShortParameter(name);
            if (type.equals(Byte   .class)) parameters.getByteParameter (name);
            return parameters.getDoubleParameter(name);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Returns the positive integer value of an operation parameter.
     */
    @Override
    public int intValue() throws InvalidParameterTypeException {
        final String   name = getName();
        final Class<T> type = getType();
        try {
            if (type.equals(Short.class)) parameters.getShortParameter(name);
            if (type.equals(Byte .class)) parameters.getByteParameter (name);
            return parameters.getIntParameter(name);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Returns the boolean value of an operation parameter.
     */
    @Override
    public boolean booleanValue() throws InvalidParameterTypeException {
        final String name = getName();
        try {
            return parameters.getBooleanParameter(name);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Returns the string value of an operation parameter.
     */
    @Override
    public String stringValue() throws InvalidParameterTypeException {
        final String name = getName();
        try {
            // Really cast to CharSequence (even if not needed for toString())
            // because we want the ClassCastException if the type mismatch.
            return ((CharSequence) parameters.getObjectParameter(name)).toString();
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Always throws an exception, since this parameter has no unit.
     */
    @Override
    public double[] doubleValueList(Unit<?> unit) throws InvalidParameterTypeException {
        throw unitlessParameter(descriptor);
    }

    /**
     * Returns an ordered sequence of two or more numeric values of an operation parameter list.
     */
    @Override
    public double[] doubleValueList() throws InvalidParameterTypeException {
        final String name = getName();
        try {
            return (double[]) parameters.getObjectParameter(name);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Returns an ordered sequence of two or more integer values of an operation parameter list.
     */
    @Override
    public int[] intValueList() throws InvalidParameterTypeException {
        final String name = getName();
        try {
            return (int[]) parameters.getObjectParameter(name);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Returns a reference to a file or a part of a file containing one or more parameter value.
     *
     * @todo Add automatic conversions, if it appears useful for JAI parameters.
     */
    @Override
    public URI valueFile() throws InvalidParameterTypeException {
        final String name = getName();
        try {
            return (URI) parameters.getObjectParameter(name);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
    }

    /**
     * Returns the parameter value as an object. The object type is typically a {@link Double},
     * {@link Integer}, {@link Boolean}, {@link String}, {@link URI}, {@code double[]} or
     * {@code int[]}.
     */
    @Override
    public T getValue() {
        final String name = getName();
        final Object value;
        try {
            value = parameters.getObjectParameter(name);
        } catch (IllegalStateException ignore) {
            /*
             * Thrown when the value still ParameterListDescriptor.NO_PARAMETER_DEFAULT.
             * In this framework, the desired behavior in this case is to returns null.
             */
            return null;
        }
        return getType().cast(value);
    }

    /**
     * Always throws an exception, since this parameter has no unit.
     */
    @Override
    public void setValue(final double value, Unit<?> unit) throws InvalidParameterValueException {
        throw unitlessParameter(descriptor);
    }

    /**
     * Sets the parameter value as a floating point.
     */
    @Override
    public void setValue(final double value) throws InvalidParameterValueException {
        final String   name = getName();
        final Class<T> type = getType();
        try {
            if (type.equals(Float  .class)) {parameters.setParameter(name, (float) value); return;}
            if (type.equals(Long   .class)) {parameters.setParameter(name, (long)  value); return;}
            if (type.equals(Integer.class)) {parameters.setParameter(name, (int)   value); return;}
            if (type.equals(Short  .class)) {parameters.setParameter(name, (short) value); return;}
            if (type.equals(Byte   .class)) {parameters.setParameter(name, (byte)  value); return;}
            parameters.setParameter(name, value);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
        fireValueChanged();
    }

    /**
     * Sets the parameter value as an integer.
     */
    @Override
    public void setValue(final int value) throws InvalidParameterValueException {
        final String   name = getName();
        final Class<T> type = getType();
        try {
            if (type.equals(Short.class)) {parameters.setParameter(name, (short) value); return;}
            if (type.equals(Byte .class)) {parameters.setParameter(name, (byte)  value); return;}
            parameters.setParameter(name, value);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
        fireValueChanged();
    }

    /**
     * Sets the parameter value as a boolean.
     */
    @Override
    public void setValue(final boolean value) throws InvalidParameterValueException {
        final String name = getName();
        try {
            parameters.setParameter(name, value);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
        fireValueChanged();
    }

    /**
     * Sets the parameter value as an object. The object type is typically a {@link Double},
     * {@link Integer}, {@link Boolean}, {@link String}, {@link URI}, {@code double[]}
     * or {@code int[]}.
     */
    @Override
    public void setValue(final Object value) throws InvalidParameterValueException {
        final String name = getName();
        try {
            parameters.setParameter(name, value);
        } catch (ClassCastException exception) {
            throw invalidType(exception);
        }
        fireValueChanged();
    }

    /**
     * Always throws an exception, since this parameter has no unit.
     */
    @Override
    public void setValue(double[] values, Unit<?> unit) throws InvalidParameterValueException {
        throw unitlessParameter(descriptor);
    }

    /**
     * Compares the specified object with this parameter for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
            final ImagingParameter<?> that = (ImagingParameter<?>) object;
            return Objects.deepEquals(this.getValue(), that.getValue());
        }
        return false;
    }

    /**
     * Returns a hash value for this parameter.
     */
    @Override
    public int hashCode() {
        return Utilities.hash(Utilities.deepHashCode(getValue()), super.hashCode()) ^ (int) serialVersionUID;
    }

    /**
     * Returns a clone of this parameter. Actually returns a different classes, since this
     * parameter is not really cloneable (it would requires a clone of {@link #parameters} first).
     */
    @Override
    public Parameter<T> clone() {
        final Parameter<T> parameter = new Parameter<>(getDescriptor());
        parameter.setValue(getValue());
        return parameter;
    }
}
