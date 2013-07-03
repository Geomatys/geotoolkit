/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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

import net.jcip.annotations.Immutable;
import org.geotoolkit.lang.ValueRange;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.Numbers.*;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.apache.sis.util.ArgumentChecks.ensureCanCast;


/**
 * A range of numbers. {@linkplain #union Union} and {@linkplain #intersect intersection}
 * are computed as usual, except that widening conversions will be applied as needed.
 * <p>
 * {@code NumberRange} has no units. For a range of physical measurements with units of
 * measure, see {@link MeasurementRange}.
 *
 * @param <T> The type of range elements as a subclass of {@link Number}.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Jody Garnett (Refractions)
 * @version 3.16
 *
 * @see org.geotoolkit.measure.RangeFormat
 *
 * @since 2.0
 * @module
 *
 * @deprecated moved to Apache SIS as {@link org.apache.sis.measure.NumberRange}.
 */
@Immutable
@Deprecated
public class NumberRange<T extends Number & Comparable<? super T>> extends Range<T> {
    //
    // IMPLEMENTATION NOTE: This class is full of @SuppressWarnings("unchecked") annotations.
    // Nevertheless we should never get ClassCastException - if we get some, this would be a
    // bug in this implementation. Users may get IllegalArgumentException however.
    //

    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -818167965963008231L;

    /**
     * Constructs an inclusive range of {@code byte} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Byte> create(final byte minimum, final byte maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code byte} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Byte> create(final byte minimum, final boolean isMinIncluded,
                                           final byte maximum, final boolean isMaxIncluded)
    {
        return new NumberRange<>(Byte.class,
                Byte.valueOf(minimum), isMinIncluded,
                Byte.valueOf(maximum), isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code short} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Short> create(final short minimum, final short maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code short} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Short> create(final short minimum, final boolean isMinIncluded,
                                            final short maximum, final boolean isMaxIncluded)
    {
        return new NumberRange<>(Short.class,
                Short.valueOf(minimum), isMinIncluded,
                Short.valueOf(maximum), isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code int} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Integer> create(final int minimum, final int maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code int} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Integer> create(final int minimum, final boolean isMinIncluded,
                                              final int maximum, final boolean isMaxIncluded)
    {
        return new NumberRange<>(Integer.class,
                Integer.valueOf(minimum), isMinIncluded,
                Integer.valueOf(maximum), isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code long} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Long> create(final long minimum, final long maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code long} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Long> create(final long minimum, final boolean isMinIncluded,
                                           final long maximum, final boolean isMaxIncluded)
    {
        return new NumberRange<>(Long.class,
                Long.valueOf(minimum), isMinIncluded,
                Long.valueOf(maximum), isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code float} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Float> create(final float minimum, final float maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code float} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Float> create(final float minimum, final boolean isMinIncluded,
                                            final float maximum, final boolean isMaxIncluded)
    {
        return new NumberRange<>(Float.class,
                valueOf(minimum, Float.NEGATIVE_INFINITY), isMinIncluded,
                valueOf(maximum, Float.POSITIVE_INFINITY), isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@code double} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Double> create(final double minimum, final double maximum) {
        return create(minimum, true, maximum, true);
    }

    /**
     * Constructs a range of {@code double} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range.
     *
     * @since 2.5
     */
    public static NumberRange<Double> create(final double minimum, final boolean isMinIncluded,
                                             final double maximum, final boolean isMaxIncluded)
    {
        return new NumberRange<>(Double.class,
                valueOf(minimum, Double.NEGATIVE_INFINITY), isMinIncluded,
                valueOf(maximum, Double.POSITIVE_INFINITY), isMaxIncluded);
    }

    /**
     * Constructs a range using the smallest type of {@link Number} that can hold the
     * given values. The given numbers don't need to be of the same type since they will
     * be {@linkplain org.geotoolkit.util.converter.Numbers#cast(Number, Class) casted}
     * as needed. More specifically:
     * <p>
     * <ul>
     *   <li>If the values are between {@value java.lang.Byte#MIN_VALUE} and
     *       {@value java.lang.Byte#MAX_VALUE} inclusive, then the given values are converted
     *       to {@link Byte} objects and a {@code NumberRange} is created from them.</li>
     *   <li>Otherwise if the values are between {@value java.lang.Short#MIN_VALUE} and
     *       {@value java.lang.Short#MAX_VALUE} inclusive, then the given values are converted
     *       to {@link Short} objects and a {@code NumberRange} is created from them.</li>
     *   <li>Otherwise the {@link Integer} type is tested in the same way, then the
     *       {@link Long} type, and finally the {@link Float} type.</li>
     *   <li>If none of the above types is suitable, then the {@link Double} type is used.</li>
     * </ul>
     *
     * @param  minimum        The minimum value, or {@code null} for negative infinity.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value, or {@code null} for positive infinity.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @return The new range, or {@code null} if both {@code minimum} and {@code maximum}
     *         are {@code null}.
     *
     * @since 3.06
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static NumberRange<?> createBestFit(final Number minimum, final boolean isMinIncluded,
                                               final Number maximum, final boolean isMaxIncluded)
    {
        final Class<? extends Number> type = widestClass(
                narrowestClass(minimum), narrowestClass(maximum));
        return (type == null) ? null :
            new NumberRange(type, cast(minimum, type), isMinIncluded,
                                  cast(maximum, type), isMaxIncluded);
    }

    /**
     * Constructs an inclusive range of {@link Number} objects.
     *
     * @param  type     The element class, usually one of {@link Byte}, {@link Short},
     *                  {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     */
    public NumberRange(final Class<T> type, final T minimum, final T maximum) {
        super(type, minimum, maximum);
    }

    /**
     * Constructs a range of {@link Number} objects.
     *
     * @param  type           The element class, usually one of {@link Byte}, {@link Short},
     *                        {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     */
    public NumberRange(final Class<T> type,
                       final T minimum, final boolean isMinIncluded,
                       final T maximum, final boolean isMaxIncluded)
    {
        super(type, minimum, isMinIncluded, maximum, isMaxIncluded);
    }

    /**
     * Creates a new range from the given annotation.
     *
     * @param type  The element class, usually one of {@link Byte}, {@link Short},
     *              {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param range The range of value.
     *
     * @since 3.04
     */
    public NumberRange(final Class<T> type, final ValueRange range) {
        super(type, cast(valueOf(range.minimum(), Double.NEGATIVE_INFINITY), type), range.isMinIncluded(),
                    cast(valueOf(range.maximum(), Double.POSITIVE_INFINITY), type), range.isMaxIncluded());
    }

    /**
     * Constructs a range with the same values than the specified range,
     * casted to the specified type.
     *
     * @param  type  The element class, usually one of {@link Byte}, {@link Short},
     *               {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param  range The range to copy. The elements must be {@link Number} instances.
     * @throws IllegalArgumentException if the values are not convertible to the specified class.
     */
    NumberRange(final Class<T> type, final Range<? extends Number> range)
            throws IllegalArgumentException
    {
        this(type, cast(range.getMinValue(), type), range.isMinIncluded(),
                   cast(range.getMaxValue(), type), range.isMaxIncluded());
    }

    /**
     * Constructs a range with the same type and the same values than the
     * specified range. This is a copy constructor.
     *
     * @param range The range to copy. The elements must be {@link Number} instances.
     *
     * @since 2.4
     */
    public NumberRange(final Range<T> range) {
        super(range.getElementType(),
              range.getMinValue(), range.isMinIncluded(),
              range.getMaxValue(), range.isMaxIncluded());
    }

    /**
     * Creates a new range using the same element class than this range. This method will
     * be overridden by subclasses in order to create a range of a more specific type.
     */
    @Override
    NumberRange<T> create(final T minValue, final boolean isMinIncluded,
                          final T maxValue, final boolean isMaxIncluded)
    {
        return new NumberRange<>(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded);
    }

    /**
     * Returns the {@code Float} wrapper of the given primitive {@code float},
     * or {@code null} if it equals to the infinity value.
     */
    private static Float valueOf(final float value, final float infinity) {
        return (value != infinity) ? Float.valueOf(value) : null;
    }

    /**
     * Returns the {@code Double} wrapper of the given primitive {@code double},
     * or {@code null} if it equals to the infinity value.
     */
    private static Double valueOf(final double value, final double infinity) {
        return (value != infinity) ? Double.valueOf(value) : null;
    }

    /**
     * Ensures that {@link #elementClass} is compatible with the type expected by this range class.
     * Invoked for argument checking by the super-class constructor.
     */
    @Override
    final void checkElementClass() throws IllegalArgumentException {
        ensureNumberClass(elementClass);
        super.checkElementClass(); // Check that the type implements also Comparable.
    }

    /**
     * Returns the type of minimum and maximum values.
     */
    @SuppressWarnings("unchecked")
    private static Class<? extends Number> getElementType(final Range<?> range) {
        ensureNonNull("range", range);
        final Class<?> type = range.elementClass;
        ensureNumberClass(type);
        /*
         * Safe because we checked in the above line. We could have used Class.asSubclass(Class)
         * instead but we want an IllegalArgumentException in case of failure rather than a
         * ClassCastException.
         */
        return (Class<? extends Number>) type;
    }

    /**
     * Ensures that the given class is {@link Number} or a subclass.
     */
    private static void ensureNumberClass(final Class<?> type) throws IllegalArgumentException {
        if (!Number.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_CLASS_2, type, Number.class));
        }
    }

    /**
     * Wraps the specified {@link Range} in a {@code NumberRange} object. If the specified
     * range is already an instance of {@code NumberRange}, then it is returned unchanged.
     *
     * @param  <N> The type of elements in the given range.
     * @param  range The range to wrap.
     * @return The same range than {@code range} as a {@code NumberRange} object.
     */
    public static <N extends Number & Comparable<? super N>> NumberRange<N> wrap(final Range<N> range) {
        if (range instanceof NumberRange<?>) {
            return (NumberRange<N>) range;
        }
        // The constructor will ensure that the range element class is a subclass of Number.
        return new NumberRange<>(range);
    }

    /**
     * Casts the specified range to the specified type. If this class is associated to a unit of
     * measurement, then this method convert the {@code range} units to the same units than this
     * instance. This method is overridden by {@link MeasurementRange} only in the way described
     * above.
     *
     * @param  type The class to cast to. Must be one of {@link Byte}, {@link Short},
     *              {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @return The casted range, or {@code range} if no cast is needed.
     * @throws IllegalArgumentException if the values are not convertible to the specified class.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    <N extends Number & Comparable<? super N>>
    NumberRange<N> convertAndCast(final Range<? extends Number> range, final Class<N> type)
            throws IllegalArgumentException
    {
        if (type.equals(range.getElementType())) {
            // Safe because we checked in the line just above.
            return (NumberRange<N>) wrap((Range) range);
        }
        return new NumberRange<>(type, range);
    }

    /**
     * Casts this range to the specified type.
     *
     * @param  <N>   The class to cast to.
     * @param  type  The class to cast to. Must be one of {@link Byte}, {@link Short},
     *               {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @return The casted range, or {@code this} if this range already uses the specified type.
     * @throws IllegalArgumentException if the values are not convertible to the specified class.
     */
    public <N extends Number & Comparable<? super N>> NumberRange<N> castTo(final Class<N> type)
            throws IllegalArgumentException
    {
        return convertAndCast(this, type);
    }

    /**
     * Returns an initially empty array of the given length.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
    NumberRange<T>[] newArray(final int length) {
        return new NumberRange[length];
    }

    /**
     * Returns {@code true} if the specified value is within this range.
     *
     * @param  value The value to check for inclusion.
     * @return {@code true} if the given value is within this range.
     * @throws IllegalArgumentException if the given value is not comparable.
     */
    public boolean contains(final Number value) throws IllegalArgumentException {
        if (value != null && !(value instanceof Comparable<?>)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.NOT_COMPARABLE_CLASS_1, value.getClass()));
        }
        return contains((Comparable<?>) value);
    }

    /**
     * Returns {@code true} if the specified value is within this range.
     * The given value must be a subclass of {@link Number}.
     *
     * @throws IllegalArgumentException if the given value is not a subclass of {@link Number}.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public boolean contains(Comparable value) throws IllegalArgumentException {
        if (value == null) {
            return false;
        }
        ensureCanCast("value", Number.class, value);
        /*
         * Suppress warning because we checked the class in the line just above, so we are safe.
         * We could have used Class.cast(Object) but we want an IllegalArgumentException with a
         * localized message.
         */
        Number number = (Number) value;
        final Class<? extends Number> type = widestClass(elementClass, number.getClass());
        number = cast(number, type);
        /*
         * The 'type' bounds should actually be <? extends Number & Comparable> since the method
         * signature expect a Comparable and we have additionally casted to a Number.  However I
         * have not found a way to express that safely in a local variable with Java 6.
         */
        return castTo((Class) type).containsNC((Comparable<?>) number);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public boolean contains(Range<?> range) throws IllegalArgumentException {
        final Class<? extends Number> type = widestClass(elementClass, getElementType(range));
        /*
         * The type bounds is actually <? extends Number & Comparable> but I'm unable to express
         * it as local variable as of Java 6. So we have to bypass the compiler check, but those
         * casts are actually safes, including Range because getElementType(range) would have
         * throw an exception otherwise.
         */
        range = convertAndCast((Range) range, (Class) type);
        return castTo((Class) type).containsNC(range);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public boolean intersects(Range<?> range) throws IllegalArgumentException {
        final Class<? extends Number> type = widestClass(elementClass, getElementType(range));
        /*
         * The type bounds is actually <? extends Number & Comparable> but I'm unable to express
         * it as local variable as of Java 6. So we have to bypass the compiler check, but those
         * casts are actually safes, including Range because getElementType(range) would have
         * throw an exception otherwise.
         */
        range = convertAndCast((Range) range, (Class) type);
        return castTo((Class) type).intersectsNC(range);
    }

    /**
     * {@inheritDoc}
     * Widening conversions will be applied as needed.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public NumberRange<?> union(Range<?> range) throws IllegalArgumentException {
        final Class<? extends Number> type = widestClass(elementClass, getElementType(range));
        range = convertAndCast((Range) range, (Class) type);
        return (NumberRange) castTo((Class) type).unionNC(range);
    }

    /**
     * {@inheritDoc}
     * Widening conversions will be applied as needed.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public NumberRange<?> intersect(Range<?> range) throws IllegalArgumentException {
        final Class<? extends Number> rangeType = getElementType(range);
        Class<? extends Number> type = widestClass(elementClass, rangeType);
        range = castTo((Class) type).intersectNC(convertAndCast((Range) range, (Class) type));
        /*
         * Use a finer type capable to holds the result (since the intersection
         * may have reduced the range), but not finer than the finest type of
         * the ranges used in the intersection calculation.
         */
        type = narrowestClass(elementClass, rangeType);
        type = widestClass(type, narrowestClass((Number) range.minValue));
        type = widestClass(type, narrowestClass((Number) range.maxValue));
        return convertAndCast((Range) range, (Class) type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    public NumberRange<?>[] subtract(Range<?> range) throws IllegalArgumentException {
        Class<? extends Number> type = widestClass(elementClass, getElementType(range));
        return (NumberRange[]) castTo((Class) type)
                .subtractNC(convertAndCast((Range) range, (Class) type));
    }

    /**
     * Returns the {@linkplain #getMinValue minimum value} as a {@code double}.
     * If this range is unbounded, then {@link Double#NEGATIVE_INFINITY} is returned.
     *
     * @return The minimum value.
     */
    @SuppressWarnings("unchecked")
    public double getMinimum() {
        final Number value = (Number) getMinValue();
        return (value != null) ? value.doubleValue() : Double.NEGATIVE_INFINITY;
    }

    /**
     * Returns the {@linkplain #getMinimum() minimum value} with the specified inclusive or
     * exclusive state. If this range is unbounded, then {@link Double#NEGATIVE_INFINITY} is
     * returned.
     *
     * @param inclusive
     *            {@code true} for the minimum value inclusive, or {@code false}
     *            for the minimum value exclusive.
     * @return The minimum value, inclusive or exclusive as requested.
     */
    public double getMinimum(final boolean inclusive) {
        double value = getMinimum();
        if (inclusive != isMinIncluded()) {
            value = next(getElementType(), value, inclusive);
        }
        return value;
    }

    /**
     * Returns the {@linkplain #getMaxValue maximum value} as a {@code double}.
     * If this range is unbounded, then {@link Double#POSITIVE_INFINITY} is returned.
     *
     * @return The maximum value.
     */
    @SuppressWarnings("unchecked")
    public double getMaximum() {
        final Number value = (Number) getMaxValue();
        return (value != null) ? value.doubleValue() : Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the {@linkplain #getMaximum() maximum value} with the specified inclusive or
     * exclusive state. If this range is unbounded, then {@link Double#POSITIVE_INFINITY} is
     * returned.
     *
     * @param inclusive
     *            {@code true} for the maximum value inclusive, or {@code false}
     *            for the maximum value exclusive.
     * @return The maximum value, inclusive or exclusive as requested.
     */
    public double getMaximum(final boolean inclusive) {
        double value = getMaximum();
        if (inclusive != isMaxIncluded()) {
            value = next(getElementType(), value, !inclusive);
        }
        return value;
    }

    /**
     * Returns the next value for the given type.
     *
     * @param  type  The element type.
     * @param  value The value to increment or decrement.
     * @param  up    {@code true} for incrementing, or {@code false} for decrementing.
     * @return The adjacent value.
     */
    private static double next(final Class<?> type, double value, final boolean up) {
        if (!up) {
            value = -value;
        }
        if (isInteger(type)) {
            value++;
        } else if (type.equals(Float.class)) {
            value = Math.nextUp((float) value);
        } else if (type.equals(Double.class)) {
            value = Math.nextUp(value);
        } else {
            // Thrown IllegalStateException instead than IllegalArgumentException because
            // the 'type' argument given to this method come from a NumberRange field.
            throw new IllegalStateException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, type));
        }
        if (!up) {
            value = -value;
        }
        return value;
    }
}
