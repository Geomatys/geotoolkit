/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.util;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;


/**
 * Provides static methods for performing argument checks. The methods in this class throw
 * one of the exceptions widely used in standard Java API ({@link NullPointerException},
 * {@link IllegalArgumentException} or {@link IndexOutOfBoundsException}) if the given
 * argument is invalid.
 * <p>
 * In addition to the argument value to check, the static methods in this class expect also
 * the programmatic name of the parameter being checked. An error message will be built for
 * the {@linkplain java.util.Locale#getDefault() default locale} using that parameter name
 * only if needed.
 * <p>
 * By convention, the value to check is always the last parameter given to every methods
 * in this class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 * @module
 */
@Static
public final class ArgumentChecks {
    /**
     * Forbid object creation.
     */
    private ArgumentChecks() {
    }

    /**
     * Makes sure that an argument is non-null. If the given {@code object} is null, then a
     * {@link NullArgumentException} is thrown with a localized message containing the given name.
     *
     * @param  name The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  object The user argument to check against null value.
     * @throws NullArgumentException if {@code object} is null.
     */
    public static void ensureNonNull(final String name, final Object object)
            throws NullArgumentException
    {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Makes sure that an array element is non-null. If {@code array[index]} is null, then a
     * {@link NullArgumentException} is thrown with a localized message containing the given name.
     *
     * @param  name The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  index Index of the element to check.
     * @param  array The user argument to check against null element.
     * @throws NullArgumentException if {@code array} or {@code array[index]} is null.
     */
    public static void ensureNonNull(final String name, final int index, final Object[] array)
            throws NullArgumentException
    {
        if (array == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
        if (array[index] == null) {
            throw new NullArgumentException(Errors.format(
                    Errors.Keys.NULL_ARGUMENT_$1, name + '[' + index + ']'));
        }
    }

    /**
     * Ensures that the given index is equals or greater than zero and lower than the given
     * upper value. This method is primarily designed for methods that expect only an index
     * argument. For this reason, this method does not expect the argument name.
     *
     * @param  upper The maximal index value, exclusive.
     * @param  index The index to check.
     * @throws IndexOutOfBoundsException If the given index is negative or not lower than the
     *         given upper value.
     */
    public static void ensureValidIndex(final int upper, final int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= upper) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, index));
        }
    }

    /**
     * Ensures that the given integer value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative.
     */
    public static void ensurePositive(final String name, final int value)
            throws IllegalArgumentException
    {
        if (value < 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, value));
        }
    }

    /**
     * Ensures that the given long value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative.
     */
    public static void ensurePositive(final String name, final long value)
            throws IllegalArgumentException
    {
        if (value < 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, value));
        }
    }

    /**
     * Ensures that the given floating point value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Float#NaN NaN} or negative.
     */
    public static void ensurePositive(final String name, final float value)
            throws IllegalArgumentException
    {
        if (!(value >= 0)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, value));
        }
    }

    /**
     * Ensures that the given floating point value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Double#NaN NaN} or negative.
     */
    public static void ensurePositive(final String name, final double value)
            throws IllegalArgumentException
    {
        if (!(value >= 0)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, name, value));
        }
    }

    /**
     * Ensures that the given integer value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative or equals to zero.
     */
    public static void ensureStrictlyPositive(final String name, final int value)
            throws IllegalArgumentException
    {
        if (value <= 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NOT_GREATER_THAN_ZERO_$2, name, value));
        }
    }

    /**
     * Ensures that the given long value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative or equals to zero.
     */
    public static void ensureStrictlyPositive(final String name, final long value)
            throws IllegalArgumentException
    {
        if (value <= 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NOT_GREATER_THAN_ZERO_$2, name, value));
        }
    }

    /**
     * Ensures that the given floating point value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Float#NaN NaN},
     *         zero or negative.
     */
    public static void ensureStrictlyPositive(final String name, final float value)
            throws IllegalArgumentException
    {
        if (!(value > 0)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NOT_GREATER_THAN_ZERO_$2, name, value));
        }
    }

    /**
     * Ensures that the given floating point value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Double#NaN NaN},
     *         zero or negative.
     */
    public static void ensureStrictlyPositive(final String name, final double value)
            throws IllegalArgumentException
    {
        if (!(value > 0)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NOT_GREATER_THAN_ZERO_$2, name, value));
        }
    }

    /**
     * Ensures that the given integer value is between the given bounds, inclusive.
     *
     * @param  name  The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  min   The minimal value, inclusive.
     * @param  max   The maximal value, inclusive.
     * @param  value The value to be tested.
     * @throws IllegalArgumentException if the given value is not in the given range.
     */
    public static void ensureBetween(final String name, final int min, final int max, final int value)
            throws IllegalArgumentException
    {
        if (value < min || value > max) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.VALUE_OUT_OF_BOUNDS_$4, name, value, min, max));
        }
    }

    /**
     * Ensures that the given long value is between the given bounds, inclusive.
     *
     * @param  name  The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  min   The minimal value, inclusive.
     * @param  max   The maximal value, inclusive.
     * @param  value The value to be tested.
     * @throws IllegalArgumentException if the given value is not in the given range.
     */
    public static void ensureBetween(final String name, final long min, final long max, final long value)
            throws IllegalArgumentException
    {
        if (value < min || value > max) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.VALUE_OUT_OF_BOUNDS_$4, name, value, min, max));
        }
    }

    /**
     * Ensures that the given floating point value is between the given bounds, inclusive.
     *
     * @param  name  The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  min   The minimal value, inclusive.
     * @param  max   The maximal value, inclusive.
     * @param  value The value to be tested.
     * @throws IllegalArgumentException if the given value is {@linkplain Float#NaN NaN}
     *         or not in the given range.
     */
    public static void ensureBetween(final String name, final float min, final float max, final float value)
            throws IllegalArgumentException
    {
        if (!(value >= min && value <= max)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.VALUE_OUT_OF_BOUNDS_$4, name, value, min, max));
        }
    }

    /**
     * Ensures that the given floating point value is between the given bounds, inclusive.
     *
     * @param  name  The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  min   The minimal value, inclusive.
     * @param  max   The maximal value, inclusive.
     * @param  value The value to be tested.
     * @throws IllegalArgumentException if the given value is {@linkplain Float#NaN NaN}
     *         or not in the given range.
     */
    public static void ensureBetween(final String name, final double min, final double max, final double value)
            throws IllegalArgumentException
    {
        if (!(value >= min && value <= max)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.VALUE_OUT_OF_BOUNDS_$4, name, value, min, max));
        }
    }
}
