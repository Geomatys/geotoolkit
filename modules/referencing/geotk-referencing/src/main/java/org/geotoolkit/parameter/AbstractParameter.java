/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.io.Writer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import javax.measure.unit.Unit;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.InvalidParameterValueException;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.io.wkt.FormattableObject;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.converter.Classes;


/**
 * Abstract parameter value or group of parameter values.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see AbstractParameterDescriptor
 *
 * @since 2.0
 * @module
 */
public abstract class AbstractParameter extends FormattableObject
           implements GeneralParameterValue, Serializable, Cloneable
{
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 8458179223988766398L;

    /**
     * The abstract definition of this parameter or group of parameters.
     */
    final GeneralParameterDescriptor descriptor;

    /**
     * Constructs a parameter value from the specified descriptor.
     *
     * @param descriptor The abstract definition of this parameter or group of parameters.
     */
    protected AbstractParameter(final GeneralParameterDescriptor descriptor) {
        this.descriptor = descriptor;
        ensureNonNull("descriptor", descriptor);
    }

    /**
     * Returns the abstract definition of this parameter or group of parameters.
     */
    @Override
    public GeneralParameterDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Makes sure that an argument is non-null. This method was already defined in
     * {@link org.geotoolkit.referencing.AbstractIdentifiedObject}, but is defined here again
     * in order to get a more appropriate stack trace, and for access by class which do not
     * inherit from {@link org.geotoolkit.referencing.AbstractIdentifiedObject}.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws NullArgumentException if {@code object} is null.
     */
    static void ensureNonNull(final String name, final Object object) throws NullArgumentException {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Makes sure an array element is non-null. This is
     * a convenience method for subclass constructors.
     *
     * @param  name  Argument name.
     * @param  array The array to look at.
     * @param  index Index of the element to check.
     * @throws NullArgumentException if {@code array[i]} is null.
     */
    static void ensureNonNull(final String name, final Object[] array, final int index)
            throws NullArgumentException
    {
        if (array[index] == null) {
            throw new NullArgumentException(Errors.format(
                    Errors.Keys.NULL_ARGUMENT_$1, name + '[' + index + ']'));
        }
    }

    /**
     * Ensures that the specified value is of the specified class.
     *
     * @param  expectedClass the expected class.
     * @param  value The expected value, or {@code null}.
     * @throws IllegalArgumentException if {@code value} is non-null and has a non-assignable class.
     */
    static <T> void ensureValidClass(final Class<?> expectedClass, final Object value)
            throws IllegalArgumentException
    {
        if (value != null) {
            final Class<?> valueClass = value.getClass();
            if (!expectedClass.isAssignableFrom(valueClass)) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_CLASS_$2, valueClass, expectedClass));
            }
        }
    }

    /**
     * Ensures that the given value is valid according the specified parameter descriptor.
     * This convenience method ensures that {@code value} is assignable to the
     * {@linkplain ParameterDescriptor#getValueClass expected class}, is between the
     * {@linkplain ParameterDescriptor#getMinimumValue minimum} and
     * {@linkplain ParameterDescriptor#getMaximumValue maximum} values and is one of the
     * {@linkplain ParameterDescriptor#getValidValues set of valid values}.
     * If the value fails any of those tests, then an exception is thrown.
     * <p>
     * This method is similar to {@link Parameters#isValid} except that the exception contains an
     * error message formatted with a description of the failure reason.
     *
     * @param  <T> The type of parameter value. The given {@code value} should typically be an
     *         instance of this class. This is not required by this method signature but is
     *         checked by this method implementation.
     * @param  descriptor The parameter descriptor to check against.
     * @param  value The value to check, or {@code null}.
     * @return The value casted to the descriptor parameterized type.
     * @throws InvalidParameterValueException if the parameter value is invalid.
     */
    static <T> T ensureValidValue(final ParameterDescriptor<T> descriptor, final Object value)
            throws InvalidParameterValueException
    {
        if (value == null) {
            return null;
        }
        final String error;
        final Class<T> type = descriptor.getValueClass();
        if (!type.isInstance(value)) {
            error = Errors.format(Errors.Keys.ILLEGAL_OPERATION_FOR_VALUE_CLASS_$1, Classes.getClass(value));
        } else {
            @SuppressWarnings({"unchecked","rawtypes"}) // Type checked with the above "if" statement.
            final Comparable<Object> minimum = (Comparable) descriptor.getMinimumValue();
            @SuppressWarnings({"unchecked","rawtypes"})
            final Comparable<Object> maximum = (Comparable) descriptor.getMaximumValue();
            if ((minimum != null && minimum.compareTo(value) > 0) ||
                (maximum != null && maximum.compareTo(value) < 0))
            {
                error = Errors.format(Errors.Keys.VALUE_OUT_OF_BOUNDS_$3, value, minimum, maximum);
            } else {
                final Set<?> validValues = descriptor.getValidValues();
                if (validValues!=null && !validValues.contains(value)) {
                    error = Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_$2, getName(descriptor), value);
                } else {
                    return type.cast(value);
                }
            }
        }
        throw new InvalidParameterValueException(error, getName(descriptor), value);
    }

    /**
     * Returns an exception initialized with a "Unitless parameter" error message for the
     * specified descriptor.
     */
    static IllegalStateException unitlessParameter(final GeneralParameterDescriptor descriptor) {
        return new IllegalStateException(Errors.format(
                Errors.Keys.UNITLESS_PARAMETER_$1, getName(descriptor)));
    }

    /**
     * Convenience method returning the name of the specified descriptor. This method is used
     * mostly for output to be read by human, not for processing. Consequently, we may consider
     * to returns a localized name in a future version.
     */
    static String getName(final GeneralParameterDescriptor descriptor) {
        return descriptor.getName().getCode();
    }

    /**
     * Returns the unit type as one of error message code. Used for checking unit with a better
     * error message formatting if needed.
     */
    static int getUnitMessageID(final Unit<?> unit) {
        if (Units.isLinear  (unit)) return Errors.Keys.NON_LINEAR_UNIT_$1;
        if (Units.isAngular (unit)) return Errors.Keys.NON_ANGULAR_UNIT_$1;
        if (Units.isTemporal(unit)) return Errors.Keys.NON_TEMPORAL_UNIT_$1;
        if (Units.isScale   (unit)) return Errors.Keys.NON_SCALE_UNIT_$1;
        return Errors.Keys.INCOMPATIBLE_UNIT_$1;
    }

    /**
     * Returns a copy of this parameter value or group.
     */
    @Override
    public AbstractParameter clone() {
        try {
            return (AbstractParameter) super.clone();
        } catch (CloneNotSupportedException exception) {
            // Should not happen, since we are cloneable
            throw new AssertionError(exception);
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
        if (object!=null && object.getClass().equals(getClass())) {
            final AbstractParameter that = (AbstractParameter) object;
            return Utilities.equals(this.descriptor, that.descriptor);
        }
        return false;
    }

    /**
     * Returns a hash value for this parameter. This value doesn't need
     * to be the same in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        return descriptor.hashCode() ^ (int) serialVersionUID;
    }

    /**
     * Returns a string representation of this parameter. The default implementation
     * delegates the work to {@link #write}, which should be overridden by subclasses.
     */
    @Override
    public final String toString() {
        final TableWriter table = new TableWriter(null, 1);
        table.setMultiLinesCells(true);
        try {
            write(table);
        } catch (IOException exception) {
            // Should never happen, since we write to a StringWriter.
            throw new AssertionError(exception);
        }
        return table.toString();
    }

    /**
     * Writes the content of this parameter to the specified table. This method make it easier
     * to align values properly than overriding the {@link #toString} method. The table's columns
     * are defined as below:
     * <p>
     * <ol>
     *   <li>The parameter name</li>
     *   <li>The separator</li>
     *   <li>The parameter value</li>
     * </ol>
     * <p>
     * The default implementation is suitable for most cases. However, subclasses are free to
     * override this method with the following idiom:
     *
     * {@preformat java
     *     table.write("parameter name");
     *     table.nextColumn()
     *     table.write('=');
     *     table.nextColumn()
     *     table.write("parameter value");
     *     table.nextLine()
     * }
     *
     * @param  table The table where to format the parameter value.
     * @throws IOException if an error occurs during output operation.
     */
    protected void write(final TableWriter table) throws IOException {
        table.write(getName(descriptor));
        table.nextColumn();
        if (this instanceof ParameterValue<?>) {
            /*
             * Provides a default implementation for parameter value. This implementation doesn't
             * need to be a Geotoolkit's one. Putting a default implementation here avoid duplication
             * in all subclasses implementing the same interface.
             */
            table.write('=');
            table.nextColumn();
            append(table, ((ParameterValue<?>) this).getValue());
        } else if (this instanceof ParameterValueGroup) {
            /*
             * Provides a default implementation for parameter value group, for the same reasons
             * then the previous block. Reminder: the above 'instanceof' check for interface, not
             * for subclass. This explain why we use it instead of method overriding.
             */
            table.write(':');
            table.nextColumn();
            TableWriter inner = null;
            for (final GeneralParameterValue value : ((ParameterValueGroup) this).values()) {
                if (value instanceof AbstractParameter) {
                    if (inner == null) {
                        inner = new TableWriter(table, 1);
                    }
                    ((AbstractParameter) value).write(inner);
                } else {
                    // Unknow implementation. It will break the formatting. Too bad...
                    if (inner != null) {
                        inner.flush();
                        inner = null;
                    }
                    table.write(value.toString());
                    table.write('\n');
                }
            }
            if (inner != null) {
                inner.flush();
            }
        } else {
            /*
             * No know parameter value for this default implementation.
             */
        }
        table.nextLine();
    }

    /**
     * Append the specified value to a stream. If the value is an array, then
     * the array element are appended recursively (i.e. the array may contains
     * sub-array).
     */
    private static void append(final Writer buffer, final Object value) throws IOException {
        if (value == null) {
            buffer.write("null");
        } else if (value.getClass().isArray()) {
            buffer.write('{');
            final int length = Array.getLength(value);
            final int limit = Math.min(5, length);
            for (int i=0; i<limit; i++) {
                if (i != 0) {
                    buffer.write(", ");
                }
                append(buffer, Array.get(value, i));
            }
            if (length > limit) {
                buffer.write(", ...");
            }
            buffer.write('}');
        } else {
            final boolean isNumeric = (value instanceof Number);
            if (!isNumeric) {
                buffer.write('"');
            }
            buffer.write(value.toString());
            if (!isNumeric) {
                buffer.write('"');
            }
        }
    }

    /**
     * Formats the inner part of this parameter as
     * <A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PARAMETER"><cite>Well
     * Known Text</cite> (WKT)</A>. This method doesn't need to be overridden, since the formatter
     * already know how to {@linkplain Formatter#append(GeneralParameterValue) format parameters}.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        return "PARAMETER";
    }
}
