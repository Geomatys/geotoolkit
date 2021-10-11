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
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.IncommensurableException;

import org.apache.sis.measure.Units;
import org.apache.sis.measure.Range;
import org.apache.sis.measure.MeasurementRange;
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
        super(Date.class, clone(startTime), true, clone(endTime), true);
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
     * @throws IncommensurableException if the given range doesn't have a
     *         {@linkplain MeasurementRange#getUnits unit} compatible with milliseconds.
     */
    public DateRange(final MeasurementRange<?> range, final Date origin) throws IncommensurableException {
        this(range, getConverter(range.unit()), origin.getTime());
    }

    /**
     * Workaround for RFE #4093999 ("Relax constraint on placement of this()/super()
     * call in constructors").
     */
    private DateRange(final MeasurementRange<?> range, final UnitConverter converter, final long origin)
            throws IncommensurableException
    {
        super(Date.class,
              new Date(origin + Math.round(converter.convert(range.getMinDouble()))), range.isMinIncluded(),
              new Date(origin + Math.round(converter.convert(range.getMaxDouble()))), range.isMaxIncluded());
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
        return new DateRange((Date) range.getMinValue(), range.isMinIncluded(),
                             (Date) range.getMaxValue(), range.isMaxIncluded());
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
    private static UnitConverter getConverter(final Unit<?> source) throws IncommensurableException {
        if (source == null) {
            throw new IncommensurableException(Errors.format(Errors.Keys.NoUnit));
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
    public DateRange union(final Range<Date> range) throws IllegalArgumentException {
        return cast(super.union(range));
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.20
     */
    @Override
    public DateRange intersect(final Range<Date> range) throws IllegalArgumentException {
        return cast(super.intersect(range));
    }

    /**
     * {@inheritDoc}
     *
     * @since 3.20
     */
    @Override
    public DateRange[] subtract(final Range<Date> range) throws IllegalArgumentException {
        final Range<Date>[] ranges = super.subtract(range);
        final DateRange[] result = new DateRange[ranges.length];
        for (int i=0; i<result.length; i++) {
            result[i] = cast(ranges[i]);
        }
        return result;
    }
}
