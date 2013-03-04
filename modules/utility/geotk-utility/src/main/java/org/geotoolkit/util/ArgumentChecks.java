/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;


/**
 * Provides static methods for performing argument checks. The methods in this class throw
 * one of the following exceptions (or a subclass of them) widely used in standard Java API:
 * <p>
 * <table>
 * <tr><th>Exception</th></th>Thrown by</th></tr>
 * <tr><td>{@link NullPointerException}</td>
 * <td>{@link #ensureNonNull(String, Object) ensureNonNull}</td></tr>
 *
 * <tr><td>{@link IndexOutOfBoundsException}</td>
 * <td>{@link #ensureValidIndex(int, int) ensureValidIndex}</td></tr>
 *
 * <tr><td>{@link IllegalArgumentException}</td>
 * <td>{@link #ensurePositive(String, int) ensurePositive},
 * {@link #ensureStrictlyPositive(String, int) ensureStrictlyPositive},
 * {@link #ensureBetween(String, int, int, int) ensureBetween}</td></tr>
 * </table>
 *
 * {@section Method Arguments}
 * By convention, the value to check is always the last parameter given to every methods
 * in this class. The other parameters may include the programmatic name of the argument
 * being checked. This programmatic name is used for building an error message localized
 * in the {@linkplain java.util.Locale#getDefault() default locale} if the check failed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.17
 * @module
 */
public final class ArgumentChecks extends Static {
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
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureNonNull(final String name, final Object object)
            throws NullArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureNonNull(name, object);
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
     * Ensures that the specified value is null or an instance assignable to the given type.
     * If this method does not thrown an exception, then the value can be casted to the class
     * represented by {@code expectedType} without throwing a {@link ClassCastException}.
     *
     * @param  <T> The compile-time type of the value.
     * @param  name The name of the argument to be checked, used only if an exception is thrown.
     *         Can be {@code null} if the name is unknown.
     * @param  expectedType the expected type (class or interface).
     * @param  value The value to check, or {@code null}.
     * @throws IllegalArgumentException if {@code value} is non-null and is not assignable
     *         to the given type.
     *
     * @since 3.18
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static <T> void ensureCanCast(final String name, final Class<? extends T> expectedType, final T value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureCanCast(name, expectedType, value);
    }

    /**
     * Ensures that the given index is equals or greater than zero and lower than the given
     * upper value. This method is primarily designed for methods that expect only an index
     * argument. For this reason, this method does not take the argument name.
     *
     * @param  upper The maximal index value, exclusive.
     * @param  index The index to check.
     * @throws IndexOutOfBoundsException If the given index is negative or not lower than the
     *         given upper value.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureValidIndex(final int upper, final int index) throws IndexOutOfBoundsException {
        org.apache.sis.util.ArgumentChecks.ensureValidIndex(upper, index);
    }

    /**
     * Ensures that the given integer value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensurePositive(final String name, final int value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensurePositive(name, value);
    }

    /**
     * Ensures that the given long value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensurePositive(final String name, final long value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensurePositive(name, value);
    }

    /**
     * Ensures that the given floating point value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Float#NaN NaN} or negative.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensurePositive(final String name, final float value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensurePositive(name, value);
    }

    /**
     * Ensures that the given floating point value is greater than or equals to zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Double#NaN NaN} or negative.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensurePositive(final String name, final double value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensurePositive(name, value);
    }

    /**
     * Ensures that the given integer value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative or equals to zero.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureStrictlyPositive(final String name, final int value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive(name, value);
    }

    /**
     * Ensures that the given long value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is negative or equals to zero.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureStrictlyPositive(final String name, final long value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive(name, value);
    }

    /**
     * Ensures that the given floating point value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Float#NaN NaN},
     *         zero or negative.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureStrictlyPositive(final String name, final float value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive(name, value);
    }

    /**
     * Ensures that the given floating point value is greater than zero.
     *
     * @param  name   The name of the argument to be checked, used only if an exception is thrown.
     * @param  value  The user argument to check.
     * @throws IllegalArgumentException if the given value is {@linkplain Double#NaN NaN},
     *         zero or negative.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureStrictlyPositive(final String name, final double value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive(name, value);
    }

    /**
     * Ensures that the given integer value is between the given bounds, inclusive.
     * This method is used for checking values that are <strong>not</strong> index.
     *
     * @param  name  The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  min   The minimal value, inclusive.
     * @param  max   The maximal value, inclusive.
     * @param  value The value to be tested.
     * @throws IllegalArgumentException if the given value is not in the given range.
     *
     * @see #ensureValidIndex(int, int)
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureBetween(final String name, final int min, final int max, final int value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureBetween(name, min, max, value);
    }

    /**
     * Ensures that the given long value is between the given bounds, inclusive.
     *
     * @param  name  The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  min   The minimal value, inclusive.
     * @param  max   The maximal value, inclusive.
     * @param  value The value to be tested.
     * @throws IllegalArgumentException if the given value is not in the given range.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureBetween(final String name, final long min, final long max, final long value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureBetween(name, min, max, value);
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
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureBetween(final String name, final float min, final float max, final float value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureBetween(name, min, max, value);
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
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureBetween(final String name, final double min, final double max, final double value)
            throws IllegalArgumentException
    {
        org.apache.sis.util.ArgumentChecks.ensureBetween(name, min, max, value);
    }

    /**
     * Ensures that the given direct position has the expected number of dimensions.
     * This method does nothing if the direct position is null.
     *
     * @param  name     The name of the argument to be checked. Used only in case an exception is thrown.
     * @param  position The direct position to check for its dimension.
     * @param  expected The expected number of dimensions.
     * @throws MismatchedDimensionException If the given direct position is non-null and does
     *         not have the expected number of dimensions.
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.util.ArgumentChecks}.
     */
    @Deprecated
    public static void ensureDimensionMatches(final String name, final DirectPosition position, final int expected)
            throws MismatchedDimensionException
    {
        org.apache.sis.util.ArgumentChecks.ensureDimensionMatches(name, expected, position);
    }
}
