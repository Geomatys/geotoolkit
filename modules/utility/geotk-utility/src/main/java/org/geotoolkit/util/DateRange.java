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

import java.util.Date;
import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import net.jcip.annotations.Immutable;

import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Errors;


/**
 * A range of dates. The elements in this range are {@link Date} objects.
 * Consequently the precision of {@code DateRange} objects is milliseconds.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see org.geotoolkit.measure.RangeFormat
 *
 * @since 2.5
 * @module
 */
@Immutable
public class DateRange extends Range<Date> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6400011350250757942L;

    /**
     * Creates a new date range for the given dates. Start time and end time are inclusive.
     *
     * @param startTime The start time (inclusive), or {@code null} if none.
     * @param endTime   The end time (inclusive), or {@code null} if none.
     */
    public DateRange(final Date startTime, final Date endTime) {
        super(Date.class, clone(startTime), clone(endTime));
    }

    /**
     * Creates a new date range for the given dates.
     *
     * @param startTime     The start time, or {@code null} if none.
     * @param isMinIncluded {@code true} if the start time is inclusive.
     * @param endTime       The end time, or {@code null} if none.
     * @param isMaxIncluded {@code true} if the end time is inclusive.
     */
    public DateRange(final Date startTime, boolean isMinIncluded,
                     final Date   endTime, boolean isMaxIncluded)
    {
        super(Date.class, clone(startTime), isMinIncluded,
                          clone(  endTime), isMaxIncluded);
    }

    /**
     * Creates a date range from the specified measurement range. Units are converted as needed.
     *
     * @param  range The range to convert.
     * @param  origin The date to use as the origin.
     * @throws ConversionException if the given range doesn't have a
     *         {@linkplain MeasurementRange#getUnits unit} compatible with milliseconds.
     */
    public DateRange(final MeasurementRange<?> range, final Date origin) throws ConversionException {
        this(range, getConverter(range.getUnits()), origin.getTime());
    }

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors").
     */
    private DateRange(final MeasurementRange<?> range, final UnitConverter converter, final long origin)
            throws ConversionException
    {
        super(Date.class,
              new Date(origin + Math.round(converter.convert(range.getMinimum()))), range.isMinIncluded(),
              new Date(origin + Math.round(converter.convert(range.getMaximum()))), range.isMaxIncluded());
    }

    /**
     * Creates a new date range using the given values. This method is invoked by the
     * parent class for creating the result of an intersection or union operation.
     *
     * @since 3.20
     */
    @Override
    final DateRange create(final Date minValue, final boolean isMinIncluded,
                           final Date maxValue, final boolean isMaxIncluded)
    {
        return new DateRange(minValue, isMinIncluded, maxValue, isMaxIncluded);
    }

    /**
     * Returns an initially empty array of the given length.
     *
     * @since 3.20
     */
    @Override
    final DateRange[] newArray(final int length) {
        return new DateRange[length];
    }

    /**
     * Ensures that {@link #elementClass} is compatible with the type expected by this range class.
     * Invoked for argument checking by the super-class constructor.
     */
    @Override
    final void checkElementClass() throws IllegalArgumentException {
        // No need to call super.checkElementClass() because Date implements Comparable.
        if (!Date.class.isAssignableFrom(elementClass)) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_CLASS_$2, elementClass, Date.class));
        }
    }

    /**
     * Casts the given {@code Range} object to a {@code DateRange}. This method shall be invoked
     * only in context where we have verified that the range element class is compatible. This
     * verification is performed by {@link Range#ensureCompatible(Range)} method.
     */
    private static DateRange cast(final Range<?> range) {
        if (range == null || range instanceof DateRange) {
            return (DateRange) range;
        }
        return new DateRange((Date) ((Object) range.getMinValue()), range.isMinIncluded(),
                             (Date) ((Object) range.getMaxValue()), range.isMaxIncluded());
    }

    /**
     * Returns a clone of the specified date.
     */
    private static Date clone(final Date date) {
        return (date != null) ? (Date) date.clone() : null;
    }

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors").
     */
    private static UnitConverter getConverter(final Unit<?> source) throws ConversionException {
        if (source == null) {
            throw new ConversionException(Errors.format(Errors.Keys.NO_UNIT));
        }
        return source.getConverterToAny(Units.MILLISECOND);
    }

    /**
     * Returns the start time.
     */
    @Override
    public Date getMinValue() {
        return clone(super.getMinValue());
    }

    /**
     * Returns the end time.
     */
    @Override
    public Date getMaxValue() {
        return clone(super.getMaxValue());
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.20
     */
    @Override
    public DateRange union(final Range<?> range) throws IllegalArgumentException {
        return cast(super.union(range));
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.20
     */
    @Override
    public DateRange intersect(final Range<?> range) throws IllegalArgumentException {
        return cast(super.intersect(range));
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.20
     */
    @Override
    public DateRange[] subtract(final Range<?> range) throws IllegalArgumentException {
        return (DateRange[]) super.subtract(range);
        // Should never throw ClassCastException because super.subtract(Range) invokes newArray(int)
        // and create(...), which are overridden in this class with DateRange return type.
    }
}
