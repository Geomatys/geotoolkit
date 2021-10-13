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

import java.util.Objects;
import org.geotoolkit.util.StringUtilities;
import org.opengis.temporal.ClockTime;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 *A data type that shall be used to identify a temporal position within a day.
 * Because {@linkplain org.opengis.temporal.TemporalPosition temporal position} cannot by itself completely
 * identify a single temporal position; it shall be used with {@linkplain org.opengis.temporal.CalendarDate
 * calendar date} for that purpose. It may be also used to identify the time of occurrence
 * of an event that recurs every day.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultClockTime extends DefaultTemporalPosition implements ClockTime {

    /**
     * This is a sequence of positive numbers with a structure similar to a CalendarDate.
     */
    private Number[] clockTime;

    public DefaultClockTime(final TemporalReferenceSystem frame, final IndeterminateValue indeterminatePosition, final Number[] clockTime) {
        super(frame, indeterminatePosition);
        this.clockTime = clockTime;
    }

    /**
     * A sequence of numbers with a structure similar to that of {@link org.opengis.temporal.CalendarDate#getCalendarDate
     * CalendarDate}. The first number integer identifies a specific instance of the unit used at the
     * highest level of the clock hierarchy, the second number identifies a specific instance of the
     * unit used at the next lower level, and so on. All but the last number in the sequence shall be
     * integers; the last number may be integer or real.
     *
     * @return
     */
    @Override
    public Number[] getClockTime() {
        return clockTime;
    }

    public void setClockTime(final Number[] clockTime) {
        this.clockTime = clockTime;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultClockTime && super.equals(object)) {
            final DefaultClockTime that = (DefaultClockTime) object;

            return Objects.equals(this.clockTime, that.clockTime);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.clockTime != null ? this.clockTime.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("ClockTime:").append('\n');
        if (clockTime != null) {
            s.append("clockTime:").append(StringUtilities.toCommaSeparatedValues((Object[])clockTime)).append('\n');
        }
        return s.toString();
    }
}
