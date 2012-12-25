/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import java.util.Objects;
import java.io.Serializable;
import javax.measure.unit.Unit;
import net.jcip.annotations.Immutable;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.CheckedContainer;

import static org.geotoolkit.util.converter.Numbers.isInteger;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A range between a minimum and maximum comparable. The minimum/maximum may be included, excluded
 * or unbounded. The later case is indicated by {@code null} values on one or both ends.
 * <p>
 * This class is a method compatible replacement for the <cite>Java Advanced Imaging</cite>
 * {@link javax.media.jai.util.Range} class with the following differences:
 *
 * <ul>
 *   <li><p>Unbounded ranges (i.e. {@code null} minimal or maximal values) are considered
 *       <em>exclusive</em> rather than inclusive, since an iteration over the values will
 *       never reach the infinite bound. This interpretation brings some simplification in
 *       implementation and usage (e.g. a loop over the values should not attempt to process
 *       the {@code null} value).</p></li>
 *
 *   <li><p>{@link #subtract(Range)} returns an empty array if the whole range is subtracted.</p></li>
 * </ul>
 *
 * The exact {@linkplain #getElementType element class} doesn't need to be known at compile time.
 * Widening conversions are allowed as needed (subclasses like {@link NumberRange} do that). This
 * class is weakly parameterized in order to allow this flexibility. If any constructor or method
 * is invoked with an argument value of illegal class, then an {@link IllegalArgumentException} is
 * thrown. The {@link ClassCastException} is thrown only in case of bug in the {@code Range} class
 * or subclasses implementation.
 * <p>
 * The {@linkplain #toString() string representation} of a {@code Range} is
 * defined in a locale-insensitive way. In order to format a range using the
 * current {@linkplain java.util.Locale locale}, or for parsing a range, use
 * {@link org.geotoolkit.measure.RangeFormat}.
 *
 * @param <T> The type of range elements, typically {@link java.util.Date} or some subclass
 *            of {@link Number}.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see javax.media.jai.util.Range
 * @see org.geotoolkit.measure.RangeFormat
 *
 * @since 2.5
 * @module
 */
@Immutable
public class Range<T extends Comparable<? super T>> implements CheckedContainer<T>, Serializable  {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5393896130562660517L;

    /**
     * The class of elements.
     */
    final Class<T> elementClass;

    /**
     * The minimal and maximal value.
     */
    final T minValue, maxValue;

    /**
     * Whatever the minimal or maximum value is included.
     */
    private final boolean isMinIncluded, isMaxIncluded;

    /**
     * Creates a new range bounded by a single inclusive value. The {@linkplain #getMinValue minimum}
     * and {@linkplain #getMaxValue maximum} values are set to the given one.
     *
     * @param elementClass
     *          The class of the range elements.
     * @param value
     *          The minimal and maximum value (inclusive), or {@code null} for an unbounded range.
     */
    public Range(final Class<T> elementClass, final T value) {
        this(elementClass, value, true, value, true);
    }

    /**
     * Creates a new range bounded by the given inclusive values.
     *
     * @param elementClass The class of the range elements.
     * @param minValue     The minimal value (inclusive), or {@code null} if none.
     * @param maxValue     The maximal value (inclusive), or {@code null} if none.
     */
    public Range(final Class<T> elementClass, final T minValue, final T maxValue) {
        this(elementClass, minValue, true, maxValue, true);
    }

    /**
     * Creates a new range bounded by the given values.
     *
     * @param elementClass
     *          The class of the range elements.
     * @param minValue
     *          The minimal value, or {@code null} if none.
     * @param isMinIncluded
     *          {@code true} if the minimal value is inclusive, or {@code false} if exclusive.
     * @param maxValue
     *          The maximal value, or {@code null} if none.
     * @param isMaxIncluded
     *          {@code true} if the maximal value is inclusive, or {@code false} if exclusive.
     */
    public Range(final Class<T> elementClass,
                 final T minValue, final boolean isMinIncluded,
                 final T maxValue, final boolean isMaxIncluded)
    {
        ensureNonNull("elementClass", elementClass);
        this.elementClass = elementClass;
        /*
         * The "included" flags must be forced to 'false' if 'minValue' or 'maxValue' are null.
         * This is required for proper working of algorithms implemented in this class.
         */
        this.minValue      = minValue;
        this.maxValue      = maxValue;
        this.isMinIncluded = isMinIncluded && minValue != null;
        this.isMaxIncluded = isMaxIncluded && maxValue != null;
        checkElementClass();
        if (minValue != null) ensureCompatible(minValue.getClass());
        if (maxValue != null) ensureCompatible(maxValue.getClass());
    }

    /**
     * Creates a new range using the same element class than this range. This method will
     * be overridden by subclasses in order to create a range of a more specific type.
     */
    Range<T> create(final T minValue, final boolean isMinIncluded,
                    final T maxValue, final boolean isMaxIncluded)
    {
        return new Range<>(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
    }

    /**
     * Ensures that the given range uses the same element class than this range.
     */
    @SuppressWarnings("unchecked")
    private Range<? extends T> ensureCompatible(final Range<?> range) throws IllegalArgumentException {
        ensureNonNull("range", range);
        ensureCompatible(range.elementClass);
        return (Range<? extends T>) range;
    }

    /**
     * Ensures that the given type is compatible with the type expected by this range.
     */
    private void ensureCompatible(final Class<?> type) throws IllegalArgumentException {
        if (!elementClass.isAssignableFrom(type)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_CLASS_$2, type, elementClass));
        }
    }

    /**
     * Ensures that {@link #elementClass} is compatible with the type expected by this range class.
     * Used at construction time for argument check. To be overridden by {@link NumberRange} and
     * {@link DateRange}.
     */
    void checkElementClass() throws IllegalArgumentException {
        if (!Comparable.class.isAssignableFrom(elementClass)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_CLASS_$2, elementClass, Comparable.class));
        }
    }

    /**
     * Returns an initially empty array of the given length. To be overridden by {@link NumberRange}
     * and subclasses in order to create arrays of more specific type.
     */
    @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
    Range<T>[] newArray(final int length) {
        return new Range[length];
    }

    /**
     * Returns the class of elements in this range. The element class extends {@link Comparable}.
     *
     * @return The class of elements in this range.
     *
     * @see javax.media.jai.util.Range#getElementClass()
     *
     * @since 3.20 (derived from 2.5)
     */
    @Override
    public Class<T> getElementType() {
        return elementClass;
    }

    /**
     * Returns the minimal value, or {@code null} if unbounded. If {@link #isMinIncluded}
     * is {@code true}, then the value is considered included in the set. Otherwise it is
     * considered excluded.
     *
     * @return The minimal value.
     *
     * @see javax.media.jai.util.Range#getMinValue
     */
    public T getMinValue() {
        return minValue;
    }

    /**
     * Indicates if {@link #getMinValue} is included in the range.
     *
     * @return {@code true} if the minimal value is inclusive.
     *
     * @see javax.media.jai.util.Range#isMinIncluded
     */
    public boolean isMinIncluded() {
        return isMinIncluded;
    }

    /**
     * Returns the maximal value, or {@code null} if unbounded. If {@link #isMaxIncluded}
     * is {@code true}, then the value is considered included in the set. Otherwise it is
     * considered excluded.
     *
     * @return The maximal value.
     *
     * @see javax.media.jai.util.Range#getMaxValue
     */
    public T getMaxValue() {
        return maxValue;
    }

    /**
     * Indicates if {@link #getMaxValue} is included in the range.
     *
     * @return {@code true} if the maximal value is inclusive.
     *
     * @see javax.media.jai.util.Range#isMaxIncluded
     */
    public boolean isMaxIncluded() {
        return isMaxIncluded;
    }

    /**
     * Returns {@code true} if this range is empty. A range is empty if the
     * {@linkplain #getMinValue minimum value} is smaller than the
     * {@linkplain #getMaxValue maximum value}, or if they are equals while
     * at least one of them is exclusive.
     *
     * @return {@code true} if this range is empty.
     *
     * @see javax.media.jai.util.Range#isEmpty
     */
    public boolean isEmpty() {
        if (minValue == null || maxValue == null) {
            return false; // Unbounded: can't be empty.
        }
        final int c = minValue.compareTo(maxValue);
        if (c < 0) {
            return false; // Minimum is smaller than maximum.
        }
        // If min and max are equal, empty if at least one of them is exclusive.
        return c != 0 || !isMinIncluded || !isMaxIncluded;
    }

    /**
     * Returns {@code true} if this range contains the given value. A range never contains the
     * {@code null} value. This is consistent with the <a href="#skip-navbar_top">class javadoc</a>
     * stating that null {@linkplain #getMinValue minimum} or {@linkplain #getMaxValue maximum}
     * values are exclusive.
     *
     * @param  value The value to check for inclusion in this range.
     * @return {@code true} if the given value is included in this range.
     * @throws IllegalArgumentException is the given value can not be converted to a valid type
     *         through widening conversion.
     */
    public boolean contains(final Comparable<?> value) throws IllegalArgumentException {
        if (value == null) {
            return false;
        }
        ensureCompatible(value.getClass());
        @SuppressWarnings("unchecked")
        final T c = (T) value;
        return containsNC(c);
    }

    /**
     * Implementation of {@link #contains(T)} to be invoked directly by subclasses.
     * "NC" stands for "No Cast" - this method do not try to cast the value to a compatible type.
     */
    final boolean containsNC(final T value) {
        if (minValue != null) {
            final int c = minValue.compareTo(value);
            if (c >= 0) {
                if (c != 0 || !isMinIncluded) {
                    return false;
                }
            }
        }
        if (maxValue != null) {
            final int c = maxValue.compareTo(value);
            if (c <= 0) {
                if (c != 0 || !isMaxIncluded) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the supplied range is fully contained within this range.
     *
     * @param  range The range to check for inclusion in this range.
     * @return {@code true} if the given range is included in this range.
     * @throws IllegalArgumentException is the given range can not be converted to a valid type
     *         through widening conversion, or if the units of measurement are not convertible.
     */
    public boolean contains(final Range<?> range) throws IllegalArgumentException {
        return containsNC(ensureCompatible(range));
    }

    /**
     * Implementation of {@link #contains(Range)} to be invoked directly by subclasses.
     * "NC" stands for "No Cast" - this method do not try to cast the value to a compatible type.
     */
    final boolean containsNC(final Range<? extends T> range) {
        return (minValue == null || compareMinTo(range.minValue, range.isMinIncluded ? 0 : +1) <= 0) &&
               (maxValue == null || compareMaxTo(range.maxValue, range.isMaxIncluded ? 0 : -1) >= 0);
   }

    /**
     * Returns {@code true} if this range intersects the given range.
     *
     * @param  range The range to check for intersection with this range.
     * @return {@code true} if the given range intersects this range.
     * @throws IllegalArgumentException is the given range can not be converted to a valid type
     *         through widening conversion, or if the units of measurement are not convertible.
     *
     * @see javax.media.jai.util.Range#intersects
     */
    public boolean intersects(final Range<?> range) throws IllegalArgumentException {
        return intersectsNC(ensureCompatible(range));
    }

    /**
     * Implementation of {@link #intersects(Range)} to be invoked directly by subclasses.
     * "NC" stands for "No Cast" - this method do not try to cast the value to a compatible type.
     */
    final boolean intersectsNC(final Range<? extends T> range) {
        return compareMinTo(range.maxValue, range.isMaxIncluded ? 0 : -1) <= 0 &&
               compareMaxTo(range.minValue, range.isMinIncluded ? 0 : +1) >= 0;
    }

    /**
     * Returns the intersection between this range and the provided range.
     *
     * @param  range The range to intersect.
     * @return The intersection of this range with the provided range.
     * @throws IllegalArgumentException is the given range can not be converted to a valid type
     *         through widening conversion, or if the units of measurement are not convertible.
     *
     * @see javax.media.jai.util.Range#intersect
     */
    public Range<?> intersect(final Range<?> range) throws IllegalArgumentException {
        return intersectNC(ensureCompatible(range));
    }

    /**
     * Implementation of {@link #intersect(Range)} to be invoked directly by subclasses.
     * "NC" stands for "No Cast" - this method do not try to cast the value to a compatible type.
     */
    final Range<? extends T> intersectNC(final Range<? extends T> range)
            throws IllegalArgumentException
    {
        final Range<? extends T> intersect, min, max;
        min = compareMinTo(range.minValue, range.isMinIncluded ? 0 : +1) < 0 ? range : this;
        max = compareMaxTo(range.maxValue, range.isMaxIncluded ? 0 : -1) > 0 ? range : this;
        if (min == max) {
            intersect = min;
        } else {
            intersect = create(min.minValue, min.isMinIncluded, max.maxValue, max.isMaxIncluded);
        }
        assert intersect.isEmpty() == !intersects(range) : intersect;
        return intersect;
    }

    /**
     * Returns the range of values that are in this range but not in the given range.
     * This method returns an array of length 0, 1 or 2:
     * <p>
     * <ul>
     *   <li>If the given range contains fully this range, returns an array of length 0.</li>
     *   <li>If the given range is in the middle of this range, then the subtraction results in
     *       two disjoint ranges which will be returned as two elements in the array.</li>
     *   <li>Otherwise returns an array of length 1.</li>
     * </ul>
     *
     * @param  range The range to subtract.
     * @return This range without the given range.
     * @throws IllegalArgumentException is the given range can not be converted to a valid type
     *         through widening conversion, or if the units of measurement are not convertible.
     *
     * @see javax.media.jai.util.Range#subtract
     */
    public Range<?>[] subtract(final Range<?> range) throws IllegalArgumentException {
        return subtractNC(ensureCompatible(range));
    }

    /**
     * Implementation of {@link #subtract(Range)} to be invoked directly by subclasses.
     * "NC" stands for "No Cast" - this method do not try to cast the value to a compatible type.
     */
    final Range<T>[] subtractNC(final Range<? extends T> range) throws IllegalArgumentException {
        final Range<T> subtract;
        if (!intersects(range)) {
            subtract = this;
        } else {
            final boolean clipMin = compareMinTo(range.minValue, range.isMinIncluded ? 0 : +1) >= 0;
            final boolean clipMax = compareMaxTo(range.maxValue, range.isMaxIncluded ? 0 : -1) <= 0;
            if (clipMin) {
                if (clipMax) {
                    // The given range contains fully this range.
                    assert range.contains(this) : range;
                    return newArray(0);
                }
                subtract = create(range.maxValue, !range.isMaxIncluded, maxValue, isMaxIncluded);
            } else {
                if (!clipMax) {
                    final Range<T>[] array = newArray(2);
                    array[0] = create(minValue, isMinIncluded, range.minValue, !range.isMinIncluded);
                    array[1] = create(range.maxValue, !range.isMaxIncluded, maxValue, isMaxIncluded);
                    return array;
                }
                subtract = create(minValue, isMinIncluded, range.minValue, !range.isMinIncluded);
            }
        }
        assert contains(subtract) : subtract;
        assert !subtract.intersects(range) : subtract;
        final Range<T>[] array = newArray(1);
        array[0] = subtract;
        return array;
    }

    /**
     * Returns the union of this range with the given range.
     *
     * @param  range The range to add to this range.
     * @return The union of this range with the given range.
     * @throws IllegalArgumentException is the given range can not be converted to a valid type
     *         through widening conversion, or if the units of measurement are not convertible.
     *
     * @see javax.media.jai.util.Range#union
     */
    public Range<?> union(final Range<?> range) throws IllegalArgumentException {
        return unionNC(ensureCompatible(range));
    }

    /**
     * Implementation of {@link #union(Range)} to be invoked directly by subclasses.
     * "NC" stands for "No Cast" - this method do not try to cast the value to a compatible type.
     */
    final Range<?> unionNC(final Range<? extends T> range) throws IllegalArgumentException {
        final Range<? extends T> union, min, max;
        min = compareMinTo(range.minValue, range.isMinIncluded ? 0 : +1) > 0 ? range : this;
        max = compareMaxTo(range.maxValue, range.isMaxIncluded ? 0 : -1) < 0 ? range : this;
        if (min == max) {
            union = min;
        } else {
            union = create(min.minValue, min.isMinIncluded, max.maxValue, max.isMaxIncluded);
        }
        assert union.contains(min) : min;
        assert union.contains(max) : max;
        return union;
    }

    /**
     * Compares the provided value with the {@linkplain #getMinValue minimum value},
     * taking in account the included or excluded state.
     *
     * @param  value The value to compare to this range {@linkplain #getMinValue minimum}.
     * @param  delta 0 if the value is inclusive, -1 if exclusive and the first inclusive value is
     *         lower, or +1 if exclusive and the first inclusive value is higher than the given one.
     * @return 0 if the given value is equal to the {@link #minValue} and both are included,
     *         negative if {@link #minValue} is lower, positive if {@link #minValue} is higher.
     */
    final int compareMinTo(final T value, final int delta) {
        if (value == null) {
            /*
             * The given value is infinity. It could be positive or negative infinity (i.e. greater
             * or lower than this range value), which we can infer from the 'delta' value in this
             * Range implementation. 'delta' should never be 0 since infinities are exclusive.
             */
            return delta;
        }
        if (minValue == null) {
            /*
             * This range bound is negative infinity while the given value is not. Note that we
             * perform this test after the 'value' test because if both values are infinity, we
             * want to retain the interpretation given by the argument (conceptually, the given
             * infinity still included in this range if 'delta' is -1).
             */
            return -1;
        }
        final int c = minValue.compareTo(value);
        if (c == 0) {
            if (isMinIncluded) {
                /*
                 * Returns 0 if 'value' is inclusive as well, or -1 if 'value' is exclusive
                 * and smaller than first inclusive value (indicated by 'delta' = +1).
                 */
                return -delta;
            }
            if (delta <= 0) {
                /*
                 * The range 'minValue' is exclusive, so the first inclusive value is greater.
                 * The only case where we don't return +1 is if the given argument is in the
                 * same case (first inclusive value is greater the given value).
                 */
                return +1;
            }
        }
        return c;
    }

    /**
     * Compares the provided value with the {@linkplain #getMaxValue maximum value},
     * taking in account the included or excluded state.
     *
     * @param  value The value to compare to this range {@linkplain #getMaxValue maximum}.
     * @param  delta 0 if the value is inclusive, -1 if exclusive and the first inclusive value is
     *         lower, or +1 if exclusive and the first inclusive value is higher than the given one.
     * @return 0 if the given value is equal to the {@link #maxValue} and both are included,
     *         negative if {@link #maxValue} is lower, positive if {@link #maxValue} is higher.
     */
    final int compareMaxTo(final T value, final int delta) {
        // Same comments than 'compareMinTo' with logic reversed.
        if (value == null) {
            return delta;
        }
        if (maxValue == null) {
            return +1;
        }
        final int c = maxValue.compareTo(value);
        if (c == 0) {
            if (isMaxIncluded) {
                return -delta;
            }
            if (delta >= 0) {
                return -1;
            }
        }
        return c;
    }

    /**
     * Compares this range with the given object for equality.
     *
     * @param  object The object to compare with this range for equality.
     * @return {@code true} if the given object is equal to this range.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && object.getClass() == getClass()) {
            final Range<?> other = (Range<?>) object;
            if (Objects.equals(elementClass, other.elementClass)) {
                if (isEmpty()) {
                    return other.isEmpty();
                }
                return Objects.equals(minValue, other.minValue) &&
                       Objects.equals(maxValue, other.maxValue) &&
                       isMinIncluded == other.isMinIncluded &&
                       isMaxIncluded == other.isMaxIncluded;
            }
        }
        return false;
    }

    /**
     * Returns a hash code value for this range.
     */
    @Override
    public int hashCode() {
        int result = (int) serialVersionUID;
        if (!isEmpty()) {
            result += elementClass.hashCode();
            result = Utilities.hash(isMaxIncluded, result);
            result = Utilities.hash(isMinIncluded, result);
            result = Utilities.hash(maxValue, result);
            result = Utilities.hash(minValue, result);
        }
        return result;
    }

    /**
     * To be overridden by {@link MeasurementRange} only.
     */
    Unit<?> getUnits() {
        return null;
    }

    /**
     * Returns {@code true} if the given number is formatted with only one character.
     * We will use less space if the minimum and maximum values are formatted using
     * only one digit. This method assumes that we have verified that the element type
     * is an integer type before to invoke this method.
     */
    private static boolean isCompact(final Comparable<?> value, final boolean ifNull) {
        if (value == null) {
            return ifNull;
        }
        final long n = ((Number) value).longValue();
        return n >= 0 && n < 10;
    }

    /**
     * Returns a string representation of this range. The string representation is defined
     * as below:
     * <p>
     * <ul>
     *   <li>If the range is empty, then this method returns {@code "[]"}.</li>
     *   <li>Otherwise if the minimal value is equals to the maximal values, then
     *       the string representation of that value is returned directly.</li>
     *   <li>Otherwise the string representation of the minimal and maximal values
     *       are formatted like {@code [min \u2026 max]} for inclusive bounds or
     *       {@code (min \u2026 max)} for exclusive bounds, or a mix of both styles.
     *       The \u221E symbol is used in place of {@code min} or {@code max} for
     *       unbounded ranges.</li>
     * </ul>
     * <p>
     * If this range is a {@link MeasurementRange}, then the unit of measurement is
     * appended to the above string representation.
     *
     * @see org.geotoolkit.measure.RangeFormat
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        final T minValue = this.minValue;
        final T maxValue = this.maxValue;
        if (minValue != null && minValue.equals(maxValue)) {
            String value = minValue.toString();
            final Unit<?> units = getUnits();
            if (units != null) {
                value = value + ' ' + units;
            }
            return value;
        }
        final StringBuilder buffer = new StringBuilder(20);
        buffer.append(isMinIncluded ? '[' : '(');
        if (minValue == null) {
            buffer.append("-\u221E"); // Infinity
        } else {
            buffer.append(minValue);
        }
        // Compact representation for integers, more space for real numbers.
        if (isInteger(elementClass) && isCompact(minValue, false) && isCompact(maxValue, true)) {
            buffer.append('\u2026');   // "..."
        } else {
            buffer.append(" \u2026 "); // " ... "
        }
        if (maxValue == null) {
            buffer.append('\u221E'); // Infinity
        } else {
            buffer.append(maxValue);
        }
        buffer.append(isMaxIncluded ? ']' : ')');
        final Unit<?> units = getUnits();
        if (units != null) {
            buffer.append(' ').append(units);
        }
        return buffer.toString();
    }
}
