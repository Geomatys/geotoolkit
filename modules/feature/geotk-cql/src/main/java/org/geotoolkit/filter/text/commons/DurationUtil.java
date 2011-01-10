/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.commons;

import java.util.Calendar;
import java.util.Date;


/**
 * Duration utility class
 * <p>
 * Maintains convenient methods to manipulate the duration information.
 * </p>
 *
 * @module pending
 * @since 2.5
 *
 * @author Mauricio Pazos - Axios Engineering
 * @author Gabriel Roldan - Axios Engineering
 */
final class DurationUtil {
    private static final int YEARS = 0;
    private static final int MONTHS = 1;
    private static final int DAYS = 2;

    private static final int HOURS = 0;
    private static final int MINUTES = 1;
    private static final int SECONDS = 2;

    private DurationUtil() {
        // utility class
    }

    /**
     * Extract from duration string the values of years, month and days
     *
     * @param duration
     * @return int[3] with years,months,days, if some value are not present -1
     *         will be returned.
     */
    private static int[] extractDurationDate(final String duration) {
        // initializes duration date container
        /** Y,M,D */
        final int[] durationDate = new int[3];

        for (int i = 0; i < durationDate.length; i++) {
            durationDate[i] = -1;
        }

        // if has not duration date return array with -1 values
        int cursor = duration.indexOf('P');

        if (cursor == -1) {
            return durationDate;
        }

        // extracts duration date and set duration array
        cursor++;

        // years
        final int endYears = duration.indexOf('Y', cursor);

        if (endYears >= 0) {
            final String strYears = duration.substring(cursor, endYears);
            final int years = Integer.parseInt(strYears);
            durationDate[YEARS] = years;

            cursor = endYears + 1;
        }

        // months
        final int endMonths = duration.indexOf('M', cursor);

        if (endMonths >= 0) {
            final String strMonths = duration.substring(cursor, endMonths);
            final int months = Integer.parseInt(strMonths);
            durationDate[MONTHS] = months;

            cursor = endMonths + 1;
        }

        // days
        final int endDays = duration.indexOf('D', cursor);

        if (endDays >= 0) {
            final String strDays = duration.substring(cursor, endDays);
            final int days = Integer.parseInt(strDays);
            durationDate[DAYS] = days;
        }

        return durationDate;
    }

    /**
     * Extract from duration string the values of hours, minutes and seconds
     *
     * @param duration
     * @return int[3] with hours, minutes and seconds if some value are not
     *         present -1 will be returned.
     */
    private static int[] extractDurationTime(final String duration) {
        final int[] durations = new int[3];
        for (int i = 0; i < durations.length; i++) {
            durations[i] = -1;
        }

        int cursor = duration.indexOf('T');

        if (cursor == -1) {
            return durations;
        }

        cursor++;

        // hours
        final int endHours = duration.indexOf('H', cursor);

        if (endHours >= 0) {
            final String strHours = duration.substring(cursor, endHours);
            final int hours = Integer.parseInt(strHours);
            durations[HOURS] = hours;

            cursor = endHours + 1;
        }

        // minute
        final int endMinutes = duration.indexOf('M', cursor);

        if (endMinutes >= 0) {
            final String strMinutes = duration.substring(cursor, endMinutes);
            final int minutes = Integer.parseInt(strMinutes);
            durations[MINUTES] = minutes;

            cursor = endMinutes + 1;
        }

        // seconds
        final int endSeconds = duration.indexOf('S', cursor);

        if (endSeconds >= 0) {
            final String strSeconds = duration.substring(cursor, endSeconds);
            final int seconds = Integer.parseInt(strSeconds);
            durations[SECONDS] = seconds;
        }

        return durations;
    }

    /**
     * Add duration to date
     *
     * @param date
     *            a Date
     * @param duration
     *            a String formated like "P##Y##M##D"
     *
     * @return a Date
     *
     */
    public static Date addDurationToDate(final Date date, final String duration) throws NumberFormatException {
        final int positive = 1;

        Date computedDate = computeDateFromDurationDate(date, duration, positive);

        computedDate = computeDateFromDurationTime(computedDate, duration, positive);

        return computedDate;
    }

    /**
     * Adds years, month and days (duration) to initial date.
     *
     * @param date
     *            initial date
     * @param duration
     *            a String with format: PddYddMddD
     * @return Date a computed date. if duration have not got duration "P"
     *         return date value.
     *
     */
    private static Date computeDateFromDurationDate(final Date date, final String duration, final int sign) {
        final int[] durationDate = extractDurationDate(duration);

        if (isNull(durationDate)) {
            return date;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // years
        if (durationDate[YEARS] >= 0) {
            calendar.add(Calendar.YEAR, sign * durationDate[YEARS]);
        }

        // months
        if (durationDate[MONTHS] >= 0) {
            calendar.add(Calendar.MONTH, sign * durationDate[MONTHS]);
        }

        // days
        if (durationDate[DAYS] >= 0) {
            calendar.add(Calendar.DATE, sign * durationDate[DAYS]);
        }

        return calendar.getTime();
    }

    /**
     * durDate is null if all his values are -1
     *
     * @param durDate
     * @return true if has some greater than or equal 0
     */
    private static boolean isNull(final int[] durDate) {
        for (int i = 0; i < durDate.length; i++) {
            if (durDate[i] >= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Add or subtract time duration to initial date.
     *
     * @param date
     *            initial date
     * @param duration
     *            a String with format: TddHddMddS
     * @param sign
     *            1 or -1 (add or subract)
     * @return Date a computed date. if duration have not got duration "T"
     *         return date value.
     */
    private static Date computeDateFromDurationTime(final Date date, final String duration,
        final int sign) {
        final int[] DURATION_TIME = extractDurationTime(duration);

        if (isNull(DURATION_TIME)) {
            return date;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // hours
        if (DURATION_TIME[HOURS] >= 0) {
            calendar.add(Calendar.HOUR, sign * DURATION_TIME[HOURS]);
        }

        // minute
        if (DURATION_TIME[MINUTES] >= 0) {
            calendar.add(Calendar.MINUTE, sign * DURATION_TIME[MINUTES]);
        }

        // seconds
        if (DURATION_TIME[SECONDS] >= 0) {
            calendar.add(Calendar.SECOND, sign * DURATION_TIME[SECONDS]);
        }

        return calendar.getTime();
    }

    /**
     * Subtracts duration to date
     *
     * @param date
     *            a Date
     * @param duration
     *            a String formated like "P##Y##M##D"
     *
     * @return a Date
     */
    public static Date subtractDurationToDate(final Date date, final String duration) {
        final int negative = -1;

        Date computedDate = computeDateFromDurationDate(date, duration, negative);

        computedDate = computeDateFromDurationTime(computedDate, duration, negative);

        return computedDate;
    }
}
