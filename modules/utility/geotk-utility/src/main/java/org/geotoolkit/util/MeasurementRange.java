/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import net.jcip.annotations.Immutable;

import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.Numbers.*;


/**
 * A range of numbers associated with a unit of measurement. Unit conversions are applied as
 * needed by {@linkplain #union union} and {@linkplain #intersect intersection} operations.
 *
 * @param <T> The type of range elements as a subclass of {@link Number}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @see org.geotoolkit.measure
 * @see org.geotoolkit.measure.RangeFormat
 *
 * @since 2.4
 * @module
 *
 * @deprecated moved to Apache SIS as {@link org.apache.sis.measure.MeasurementRange}.
 */
@Immutable
@Deprecated
public class MeasurementRange<T extends Number & Comparable<? super T>> extends NumberRange<T> {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 3980319420337513745L;

    /**
     * The units of measurement, or {@code null} if unknown.
     */
    private final Unit<?> units;

    /**
     * Constructs an inclusive range of {@code float} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @param  units    The units of measurement, or {@code null} if unknown.
     * @return The measurement range.
     *
     * @since 2.5
     */
    public static MeasurementRange<Float> create(float minimum, float maximum, Unit<?> units) {
        return create(minimum, true, maximum, true, units);
    }

    /**
     * Constructs a range of {@code float} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the Range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the Range.
     * @param  units          The units of measurement, or {@code null} if unknown.
     * @return The measurement range.
     *
     * @since 2.5
     */
    public static MeasurementRange<Float> create(float minimum, boolean isMinIncluded,
                                                 float maximum, boolean isMaxIncluded, Unit<?> units)
    {
        return new MeasurementRange<>(Float.class,
                Float.valueOf(minimum), isMinIncluded,
                Float.valueOf(maximum), isMaxIncluded, units);
    }

    /**
     * Constructs an inclusive range of {@code double} values.
     *
     * @param  minimum  The minimum value, inclusive.
     * @param  maximum  The maximum value, <strong>inclusive</strong>.
     * @param  units    The units of measurement, or {@code null} if unknown.
     * @return The measurement range.
     */
    public static MeasurementRange<Double> create(double minimum, double maximum, Unit<?> units) {
        return create(minimum, true, maximum, true, units);
    }

    /**
     * Constructs a range of {@code double} values.
     *
     * @param  minimum        The minimum value.
     * @param  isMinIncluded  Defines whether the minimum value is included in the Range.
     * @param  maximum        The maximum value.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the Range.
     * @param  units          The units of measurement, or {@code null} if unknown.
     * @return The measurement range.
     */
    public static MeasurementRange<Double> create(double minimum, boolean isMinIncluded,
                                                  double maximum, boolean isMaxIncluded, Unit<?> units)
    {
        return new MeasurementRange<>(Double.class,
                Double.valueOf(minimum), isMinIncluded,
                Double.valueOf(maximum), isMaxIncluded, units);
    }

    /**
     * Constructs a range using the smallest type of {@link Number} that can hold the given values.
     * This method performs the same work than {@link NumberRange#createBestFit
     * NumberRange.createBestFit(...)} with an additional {@code units} argument.
     *
     * @param  minimum        The minimum value, or {@code null} for negative infinity.
     * @param  isMinIncluded  Defines whether the minimum value is included in the range.
     * @param  maximum        The maximum value, or {@code null} for positive infinity.
     * @param  isMaxIncluded  Defines whether the maximum value is included in the range.
     * @param  units          The units of measurement, or {@code null} if unknown.
     * @return The new range, or {@code null} if both {@code minimum} and {@code maximum}
     *         are {@code null}.
     *
     * @see NumberRange#createBestFit(Number, boolean, Number, boolean)
     *
     * @since 3.09
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public static MeasurementRange<?> createBestFit(final Number minimum, final boolean isMinIncluded,
            final Number maximum, final boolean isMaxIncluded, final Unit<?> units)
    {
        final Class<? extends Number> type = widestClass(
                narrowestClass(minimum), narrowestClass(maximum));
        return (type == null) ? null :
            new MeasurementRange(type, cast(minimum, type), isMinIncluded,
                                       cast(maximum, type), isMaxIncluded, units);
    }

    /**
     * Constructs a range of {@link Number} objects.
     *
     * @param type          The element class, usually one of {@link Byte}, {@link Short},
     *                      {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param minimum       The minimum value.
     * @param isMinIncluded Defines whether the minimum value is included in the Range.
     * @param maximum       The maximum value.
     * @param isMaxIncluded Defines whether the maximum value is included in the Range.
     * @param units         The units of measurement, or {@code null} if unknown.
     */
    public MeasurementRange(final Class<T> type,
                            final T minimum, final boolean isMinIncluded,
                            final T maximum, final boolean isMaxIncluded,
                            final Unit<?> units)
    {
        super(type, minimum, isMinIncluded, maximum, isMaxIncluded);
        this.units = units;
    }

    /**
     * Constructs a range with the same values than the specified range.
     *
     * @param range The range to copy. The elements must be {@link Number} instances.
     * @param units The units of measurement, or {@code null} if unknown.
     */
    public MeasurementRange(final Range<T> range, final Unit<?> units) {
        super(range);
        this.units = units;
    }

    /**
     * Constructs a range with the same values than the specified range,
     * casted to the specified type.
     *
     * @param type The element class, usually one of {@link Byte}, {@link Short},
     *             {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param range The range to copy. The elements must be {@link Number} instances.
     * @param units   The units of measurement, or {@code null} if unknown.
     */
    private MeasurementRange(Class<T> type, Range<? extends Number> range, final Unit<?> units) {
        super(type, range);
        this.units = units;
    }

    /**
     * Creates a new range using the same element class than this range.
     */
    @Override
    MeasurementRange<T> create(final T minValue, final boolean isMinIncluded,
                               final T maxValue, final boolean isMaxIncluded)
    {
        return new MeasurementRange<>(elementClass, minValue, isMinIncluded, maxValue, isMaxIncluded, units);
    }

    /**
     * Returns the units of measurement, or {@code null} if unknown.
     *
     * @return The units of measurement, or {@code null}.
     */
    @Override
    public Unit<?> getUnits() {
        return units;
    }

    /**
     * Converts this range to the specified units. If this measurement range has null units,
     * then the specified target units are simply assigned to the returned range with no
     * other changes.
     *
     * @param  targetUnits the target units, or {@code null} for keeping the units unchanged.
     * @return The converted range, or {@code this} if no conversion is needed.
     * @throws ConversionException if the target units are not compatible with
     *         this {@linkplain #getUnits range units}.
     */
    public MeasurementRange<T> convertTo(final Unit<?> targetUnits) throws ConversionException {
        return convertAndCast(elementClass, targetUnits);
    }

    /**
     * Casts this range to the specified type.
     *
     * @param <N>   The class to cast to.
     * @param  type The class to cast to. Must be one of {@link Byte}, {@link Short},
     *              {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @return The casted range, or {@code this} if this range already uses the specified type.
     */
    @Override
    public <N extends Number & Comparable<? super N>> MeasurementRange<N> castTo(Class<N> type) {
        return convertAndCast(this, type);
    }

    /**
     * Casts the specified range to the specified type. If this class is associated to a unit of
     * measurement, then this method convert the {@code range} units to the same units than this
     * instance.
     *
     * @param type The class to cast to. Must be one of {@link Byte}, {@link Short},
     *             {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @return The casted range, or {@code range} if no cast is needed.
     */
    @Override
    <N extends Number & Comparable<? super N>>
    MeasurementRange<N> convertAndCast(final Range<? extends Number> range, final Class<N> type)
            throws IllegalArgumentException
    {
        if (range instanceof MeasurementRange<?>) {
            final MeasurementRange<?> casted = (MeasurementRange<?>) range;
            try {
                return casted.convertAndCast(type, units);
            } catch (ConversionException e) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.INCOMPATIBLE_UNIT_1, casted.units), e);
            }
        }
        return new MeasurementRange<>(type, range, units);
    }

    /**
     * Casts this range to the specified type and converts to the specified units.
     *
     * @param  type The class to cast to. Must be one of {@link Byte}, {@link Short},
     *             {@link Integer}, {@link Long}, {@link Float} or {@link Double}.
     * @param  targetUnit the target units, or {@code null} for no change.
     * @return The casted range, or {@code this}.
     * @throws ConversionException if the target units are not compatible with
     *         this {@linkplain #getUnits range units}.
     */
    @SuppressWarnings("unchecked")
    private <N extends Number & Comparable<? super N>> MeasurementRange<N>
            convertAndCast(final Class<N> type, final Unit<?> targetUnits) throws ConversionException
    {
        if (targetUnits == null || targetUnits.equals(units)) {
            if (type.equals(elementClass)) {
                return (MeasurementRange<N>) this;
            } else {
                return new MeasurementRange<>(type, this, units);
            }
        }
        if (units == null) {
            return new MeasurementRange<>(type, this, targetUnits);
        }
        final UnitConverter converter = units.getConverterToAny(targetUnits);
        if (converter.equals(UnitConverter.IDENTITY)) {
            return new MeasurementRange<>(type, this, targetUnits);
        }
        boolean isMinIncluded = isMinIncluded();
        boolean isMaxIncluded = isMaxIncluded();
        Double minimum = converter.convert(getMinimum());
        Double maximum = converter.convert(getMaximum());
        if (minimum.compareTo(maximum) > 0) {
            final Double td = minimum;
            minimum = maximum;
            maximum = td;
            final boolean tb = isMinIncluded;
            isMinIncluded = isMaxIncluded;
            isMaxIncluded = tb;
        }
        return new MeasurementRange<>(type,
                cast(minimum, type), isMinIncluded,
                cast(maximum, type), isMaxIncluded, targetUnits);
    }

    /**
     * Returns an initially empty array of the given length.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"}) // Generic array creation.
    MeasurementRange<T>[] newArray(final int length) {
        return new MeasurementRange[length];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeasurementRange<?> union(final Range<?> range) throws IllegalArgumentException {
        return (MeasurementRange<?>) super.union(range);
        // Should never throw ClassCastException because super.union(Range) invokes create(...),
        // which is overridden in this class with MeasurementRange return type.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeasurementRange<?> intersect(final Range<?> range) throws IllegalArgumentException {
        return (MeasurementRange<?>) super.intersect(range);
        // Should never throw ClassCastException because super.intersect(Range) invokes
        // convertAndCast(...),  which is overridden in this class with MeasurementRange
        // return type.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MeasurementRange<?>[] subtract(final Range<?> range) throws IllegalArgumentException {
        return (MeasurementRange<?>[]) super.subtract(range);
        // Should never throw ClassCastException because super.subtract(Range) invokes newArray(int)
        // and create(...), which are overridden in this class with MeasurementRange return type.
    }

    /**
     * Compares this range with the specified object for equality.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            if (object instanceof MeasurementRange<?>) {
                final MeasurementRange<?> that = (MeasurementRange<?>) object;
                return Objects.equals(this.units, that.units);
            }
            return true;
        }
        return false;
    }
}
