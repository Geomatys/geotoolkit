/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.temporal.object;

import java.util.Arrays;
import java.util.Objects;
import org.geotoolkit.util.StringUtilities;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.DateAndTime;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.InternationalString;

/**
 * Provides a single data type for identifying a temporal position with a resolution
 * of less than a day.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultDateAndTime extends DefaultTemporalPosition implements DateAndTime {

    /**
     * This is the name of the calendar era to which the date is referenced.
     */
    private InternationalString calendarEraName;
    /**
     * This is a sequence of positive integers in which the first integeridentifies a specific instance of the unit used at the highest level of the calendar hierarchy,
     * the second integer identifies a specific instance of the unit used at the next lower level in the hierarchy, and so on.
     * The format defined in ISO 8601 for dates in the Gregorian calendar may be used for any date that is composed of values for year, month and day.
     */
    private int[] calendarDate;
    /**
     * This is a sequence of positive numbers with a structure similar to a CalendarDate.
     */
    private Number[] clockTime;

    public DefaultDateAndTime(final TemporalReferenceSystem frame, final IndeterminateValue indeterminatePosition, final InternationalString calendarEraName, final int[] calendarDate, final Number[] clockTime) {
        super(frame, indeterminatePosition);
        this.calendarDate = calendarDate;
        this.calendarEraName = calendarEraName;
        this.clockTime = clockTime;
    }

    /**
     * A sequence of numbers with a structure similar to that of {@link CalendarDate#getCalendarDate
     * CalendarDate}. The first number integer identifies a specific instance of the unit used at the
     * highest level of the clock hierarchy, the second number identifies a specific instance of the
     * unit used at the next lower level, and so on. All but the last number in the sequence shall be
     * integers; the last number may be integer or real.
     *
     */
    @Override
    public Number[] getClockTime() {
        return clockTime;
    }

    @Override
    public InternationalString getCalendarEraName() {
        return calendarEraName;
    }

    /**
     * Provides a sequence of integers in which the first integer identifies a specific instance
     * of the unit used at the highest level of the calendar hierarchy, the second integer
     * identifies a specific instance of the unit used at the next lower level in the hierarchy,
     * and so on. The format defined in ISO 8601 for dates in the Gregorian calendar may be
     * used for any date that is composed of values for year, month and day.
     *
     */
    @Override
    public int[] getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarEraName(final InternationalString calendarEraName) {
        this.calendarEraName = calendarEraName;
    }

    public void setCalendarDate(final int[] calendarDate) {
        this.calendarDate = calendarDate;
    }

    public void setClockTime(final Number[] clockTime) {
        this.clockTime = clockTime;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultDateAndTime && super.equals(object)) {
            final DefaultDateAndTime that = (DefaultDateAndTime) object;

            return Arrays.equals(this.calendarDate, that.calendarDate) &&
                    Objects.equals(this.calendarEraName, that.calendarEraName) &&
                    Arrays.equals(this.clockTime, that.clockTime);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.calendarDate != null ? Arrays.hashCode(this.calendarDate) : 0);
        hash = 37 * hash + (this.calendarEraName != null ? this.calendarEraName.hashCode() : 0);
        hash = 37 * hash + (this.clockTime != null ? Arrays.hashCode(this.clockTime) : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("DateAndTime:").append('\n');
        if (calendarEraName != null) {
            s.append("calendarEraName:").append(calendarEraName).append('\n');
        }
        if (calendarDate != null) {
            s.append("calendarDate:").append(StringUtilities.toCommaSeparatedValues(calendarDate)).append('\n');
            }
        if (clockTime != null) {
            s.append("clockTime:").append(StringUtilities.toCommaSeparatedValues((Object[])clockTime)).append('\n');
            }
        return s.toString();
    }
}
