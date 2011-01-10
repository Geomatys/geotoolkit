/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.temporal.object;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.temporal.PeriodDuration;
import org.opengis.util.InternationalString;

import static org.geotoolkit.temporal.object.TemporalConstants.*;

/**
 * Uses the format specified by ISO 8601 for exchanging information
 * about the duration of a period.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultPeriodDuration extends DefaultDuration implements PeriodDuration {

    private static final InternationalString DESIGNATOR = new SimpleInternationalString("P");
    private InternationalString years;
    private InternationalString months;
    private InternationalString weeks;
    private InternationalString days;
    private static final InternationalString TIME_INDICATOR = new SimpleInternationalString("T");
    private InternationalString hours;
    private InternationalString minutes;
    private InternationalString seconds;

    /**
     * Creates a new instances of PeriodDuration.
     * @param years
     * @param months
     * @param weeks
     * @param days
     * @param hours
     * @param minutes
     * @param seconds
     */
    public DefaultPeriodDuration(final InternationalString years, final InternationalString months, final InternationalString weeks, final InternationalString days,
            final InternationalString hours, final InternationalString minutes, final InternationalString seconds) {
        this.years = years;
        this.months = months;
        this.weeks = weeks;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Creates a new instance of PeriodDuration from a long value passed in parameter.
     * if the long contains milliseconds, this will be ignored because there is no MilliSeconds specified in the string format PnYnMnDTnHnMnS, see ISO 8601.
     * @param durationInMilliSeconds
     */
    public DefaultPeriodDuration(long durationInMilliSeconds) {
        InternationalString _years = null;
        InternationalString _months = null;
        InternationalString _week = null;
        InternationalString _days = null;
        InternationalString _hours = null;
        InternationalString _minutes = null;
        InternationalString _seconds = null;

        long temp = durationInMilliSeconds / YEAR_MS;
        if (temp >= 1) {
            _years = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * YEAR_MS;
        }
        this.years = _years;

        temp = durationInMilliSeconds / MONTH_MS;
        if (temp >= 1) {
            _months = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * MONTH_MS;
        }
        this.months = _months;

        temp = durationInMilliSeconds / WEEK_MS;
        if (temp >= 1) {
            _week = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * WEEK_MS;
        }
        this.weeks = _week;

        //we look if the gap is more than one day (86400000 ms)
        temp = durationInMilliSeconds / DAY_MS;
        if (temp >= 1) {
            _days = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * DAY_MS;
        }
        this.days = _days;

        temp = durationInMilliSeconds / HOUR_MS;
        if (temp >= 1) {
            _hours = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * HOUR_MS;
        }
        this.hours = _hours;

        temp = durationInMilliSeconds / MINUTE_MS;
        if (temp >= 1) {
            _minutes = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * MINUTE_MS;
        }
        this.minutes = _minutes;

        temp = durationInMilliSeconds / SECOND_MS;
        if (temp >= 1) {
            _seconds = new SimpleInternationalString(String.valueOf(temp));
            durationInMilliSeconds -= temp * SECOND_MS;
        }
        this.seconds = _seconds;

    /*if (durationInMilliSeconds != 0) {
    throw new IllegalArgumentException("PeriodDuration can't be found at the Millisecond precision in the pattern PnYnMnDTnHnMnS specified by ISO 8601.");
    }*/
    }

    /**
     * A mandatory element which designates that the returned string
     * represents the duration of a period.
     */
    @Override
    public InternationalString getDesignator() {
        return DESIGNATOR;
    }

    /**
     * A positive integer, followed by the character "Y",
     * which indicated the number of years in the period.
     */
    @Override
    public InternationalString getYears() {
        return years;
    }

    /**
     * A positive integer, followed by the character "M",
     * which indicated the number of months in the period.
     */
    @Override
    public InternationalString getMonths() {
        return months;
    }

    /**
     * A positive integer, followed by the character "D",
     * which indicated the number of days in the period.
     */
    @Override
    public InternationalString getDays() {
        return days;
    }

    /**
     * Included whenever the sequence includes values for
     * units less than a day.
     */
    @Override
    public InternationalString getTimeIndicator() {
        return TIME_INDICATOR;
    }

    /**
     * A positive integer, followed by the character "H",
     * which indicated the number of hours in the period.
     */
    @Override
    public InternationalString getHours() {
        return hours;
    }

    /**
     * A positive integer, followed by the character "M",
     * which indicated the number of minutes in the period.
     */
    @Override
    public InternationalString getMinutes() {
        return minutes;
    }

    /**
     * A positive integer, followed by the character "S",
     * which indicated the number of seconds in the period.
     */
    @Override
    public InternationalString getSeconds() {
        return seconds;
    }

    public void setYears(final InternationalString years) {
        this.years = years;
    }

    public void setMonths(final InternationalString months) {
        this.months = months;
    }

    public void setDays(final InternationalString days) {
        this.days = days;
    }

    public void setHours(final InternationalString hours) {
        this.hours = hours;
    }

    public void setMinutes(final InternationalString minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(final InternationalString seconds) {
        this.seconds = seconds;
    }

    public InternationalString getWeek() {
        return weeks;
    }

    public void setWeek(final InternationalString week) {
        this.weeks = week;
    }

    /**
     * Returns a duration in long. note there is no starting instant to accurate the returned value.
     * @return long duration in milliseconds
     */
    @Override
    public long getTimeInMillis() {
        String periodDescription = this.toString();
        long response = 0;
        //removing the 'P' character
        periodDescription = periodDescription.substring(1);

        //if the period contains years (31536000000 ms) the response will be incremented
        if (periodDescription.indexOf('Y') != -1) {
            int nbYear = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('Y')));
            response += nbYear * YEAR_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('Y') + 1);
        }

        //if the period contains months (2628000000 ms)
        if ((periodDescription.indexOf('M') != -1 && (periodDescription.indexOf('T') == -1)) ||
                ((periodDescription.indexOf('T') != -1) && 
                (periodDescription.indexOf('M') < periodDescription.indexOf('T')) && 
                ((periodDescription.indexOf('M') != -1)))) {
            int nbMonth = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('M')));
            response += nbMonth * MONTH_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('M') + 1);
        }

        //if the period contains weeks (604800000 ms)
        if (periodDescription.indexOf('W') != -1) {
            int nbWeek = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('W')));
            response += nbWeek * WEEK_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('W') + 1);
        }

        //if the period contains days (86400000 ms)
        if (periodDescription.indexOf('D') != -1) {
            int nbDay = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('D')));
            response += nbDay * DAY_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('D') + 1);
        }

        // removing 'T' character if exists
        if (periodDescription.indexOf('T') != -1) {
            periodDescription = periodDescription.substring(1);
        }

        //if the period contains hours (3600000 ms)
        if (periodDescription.indexOf('H') != -1) {
            int nbHour = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('H')));
            response += nbHour * HOUR_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('H') + 1);
        }

        //if the period contains minutes (60000 ms)
        if (periodDescription.indexOf('M') != -1) {
            int nbMin = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('M')));
            response += nbMin * MINUTE_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('M') + 1);
        }

        //if the period contains seconds (1000 ms)
        if (periodDescription.indexOf('S') != -1) {
            int nbSec = Integer.parseInt(periodDescription.substring(0, periodDescription.indexOf('S')));
            response += nbSec * SECOND_MS;
            periodDescription = periodDescription.substring(periodDescription.indexOf('S') + 1);
        }

        if (periodDescription.length() != 0) {
            throw new IllegalArgumentException("The period duration string is malformed");
        }
        return response;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof DefaultPeriodDuration) {
            final DefaultPeriodDuration that = (DefaultPeriodDuration) object;

            return Utilities.equals(this.days, that.days) &&
                    Utilities.equals(this.hours, that.hours) &&
                    Utilities.equals(this.minutes, that.minutes) &&
                    Utilities.equals(this.months, that.months) &&
                    Utilities.equals(this.seconds, that.seconds) &&
                    Utilities.equals(this.years, that.years);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.days != null ? this.days.hashCode() : 0);
        hash = 37 * hash + (this.hours != null ? this.hours.hashCode() : 0);
        hash = 37 * hash + (this.minutes != null ? this.minutes.hashCode() : 0);
        hash = 37 * hash + (this.months != null ? this.months.hashCode() : 0);
        hash = 37 * hash + (this.seconds != null ? this.seconds.hashCode() : 0);
        hash = 37 * hash + (this.years != null ? this.years.hashCode() : 0);
        return hash;
    }

    @Override
    /**
     * Returns a duration value String in format 8601. the pattern is PnYnMnDTnHnMnS.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(DESIGNATOR);
        if (years != null) {
            s.append(years).append('Y');
        }
        if (months != null) {
            s.append(months).append('M');
        }
        if (weeks != null) {
            s.append(weeks).append('W');
        }
        if (days != null) {
            s.append(days).append('D');
        }
        if (hours != null || minutes != null || seconds != null) {
            s.append(TIME_INDICATOR);
        }
        if (hours != null) {
            s.append(hours).append('H');
        }
        if (minutes != null) {
            s.append(minutes).append('M');
        }
        if (seconds != null) {
            s.append(seconds).append('S');
        }

        return s.toString();
    }
}
