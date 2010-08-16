/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.logging.LoggedFormat;


/**
 * Utility methods to parse {@code java.lang.String} objects which describe 
 * instants or periods in the ISO-8601 format 
 * (e.g. {@code 2009-01-20T17:04:00Z} ) into {@link java.util.Date} objects.
 * <p>
 * TODO: Explain relationship to {@code DateFormat} and to {@code Util}.
 * </p>
 * 
 * <p>
 * TODO: Improve and extend to handle the simple cases.
 * </p>
 *
 * @author Cédric Briançon     (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @author Adrian Custer       (Geomatys)
 */
public final class TimeParser {
	
    /**
     * Amount of milliseconds in a day.
     */
    static final long MILLIS_IN_DAY = 24*60*60*1000;

    /**
     * All patterns that are correct regarding the ISO-8601 norm.
     */
    private static final String[] PATTERNS = {
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm'Z'",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd'T'HH'Z'",
        "yyyy-MM-dd'T'HH",
        "yyyy-MM-dd",
        "yyyy-MM",
        "yyyy"
    };

    /**
     * The date format for each pattern.
     */
    private static final Map<TimeParser,DateFormat> PARSERS = new HashMap<TimeParser,DateFormat>(16);
    static {
        final TimeZone timezone = TimeZone.getTimeZone("UTC");
        for (int i=0; i<PATTERNS.length; i++) {
            final String pattern = PATTERNS[i];
            final DateFormat format = new SimpleDateFormat(pattern, Locale.CANADA);
            format.setTimeZone(timezone);
            if (PARSERS.put(new TimeParser(pattern), format) != null) {
                throw new AssertionError(pattern); // Should never occurs.
            }
            if (i == 0) { // The default format to be used if none if found.
                PARSERS.put(null, format);
            }
        }
    }

    /**
     * The number of date fields.
     */
    private final int numDateFields;

    /**
     * The number of time fields, including milliseconds (preceded by {@code '.'} separator
     * instead of {@code ':'}).
     */
    private final int numTimeFields;

    /**
     * {@code true} if there is a time zone.
     */
    private final boolean hasTimeZone;

    /**
     * Creates the parser for the given pattern (which may be a formatted date).
     */
    private TimeParser(final String pattern) {
        final int length = pattern.length();
        int numDateFields = 1;
        int numTimeFields = 0;
        boolean hasTimeZone = false;
        for (int i=0; i<length; i++) {
            switch (pattern.charAt(i)) {
                case '-': if (numTimeFields == 0) numDateFields++;  break;
                case 'T': if (numTimeFields == 0) numTimeFields=1;  break;
                case ':': if (numTimeFields != 0) numTimeFields++;  break;
                case '.': if (numTimeFields >= 3) numTimeFields++;  break;
                case 'Z': if (numTimeFields != 0) hasTimeZone=true; break;
                default : break;
            }
        }
        this.numDateFields = numDateFields;
        this.numTimeFields = numTimeFields;
        this.hasTimeZone   = hasTimeZone;
    }
    
    
    
    
    /**
     * Parses the date given in parameter. The date format should comply to ISO-8601 standard.
     * The string may contain either a single date or a start time, an end time and a period.
     * In the first case, this method returns a singleton containing only the parsed date. In
     * the second case, this method returns a list including all dates from start time up to
     * the end time with the interval specified in the {@code value} string.
     *
     * @param  value The date, time and period to parse.
     * @param  defaultPeriod The default period (in milliseconds) if it is needed but not specified.
     * @param  dates The destination list where to append the parsed dates.
     * @return A list of dates, or an empty list of the {@code value} string is null or empty.
     * @throws ParseException if the string can not be parsed.
     */
    public static void parse(String value, final long defaultPeriod, final List<Date> dates)
            throws ParseException
    {
        if (value == null) {
            return;
        }
        value = value.trim();
        if (value.length() == 0) {
            return;
        }
        final StringTokenizer periods = new StringTokenizer(value, ",");
        while (periods.hasMoreTokens()) {
            final StringTokenizer elements = new StringTokenizer(periods.nextToken().trim(), "/");
            if (!elements.hasMoreTokens()) {
                // Empty string possibly between two "/" (should not occurs)
                continue;
            }
            final Date start = parseDate(elements.nextToken());
            if (!elements.hasMoreTokens()) {
                // A single date is specified (most common case).
                dates.add(start);
                continue;
            }
            // Period like "yyyy-MM-ddTHH:mm:ssZ/yyyy-MM-ddTHH:mm:ssZ/P1D"
            final Date end = parseDate(elements.nextToken());
            final long period;
            if (elements.hasMoreTokens()) {
                period = parsePeriod(elements.nextToken());
            } else {
                period = defaultPeriod;
            }
            long time = start.getTime();
            final long endTime = end.getTime();
            while (time <= endTime) {
                dates.add(new Date(time));
                time += period;
            }
        }
    }

    /**
     * Parses date given in parameter according the ISO-8601 standard. This parameter
     * should follow a syntax defined in the {@link #PATTERNS} array to be validated.
     *
     * @param value The date to parse.
     * @return A date found in the request.
     * @throws ParseException if the string can not be parsed.
     */
    private static Date parseDate(String value) throws ParseException {
        value = value.trim();
        DateFormat format = PARSERS.get(new TimeParser(value));
        if (format == null) {
            // Gets a default format.
            format = PARSERS.get(null);
        }
        /*
         * We do not use the standard method DateFormat.parse(String), because if the parsing
         * stops before the end of the string, the remaining characters are just ignored and
         * no exception is thrown. So we have to ensure that the whole string is correct for
         * the format.
         */
        final ParsePosition position = new ParsePosition(0);
        final Date time;
        synchronized (format) {
            time = format.parse(value, position);
        }
        final int index = position.getIndex();
        final int length = value.length();
        if (index != length) {
            final int errorIndex = Math.max(index, position.getErrorIndex());
            throw new ParseException(LoggedFormat.formatUnparsable(value, index, errorIndex, null), errorIndex);
        }
        return time;
    }

    /**
     * Parses the increment part of a period and returns it in milliseconds.
     *
     * @param period A string representation of the time increment according the ISO-8601:1988(E)
     *        standard. For example: {@code "P1D"} = one day.
     * @return The increment value converted in milliseconds.
     * @throws ParseException if the string can not be parsed.
     *
     * @todo Handle months in a better way than just taking the average month length.
     */
    static long parsePeriod(final String period) throws ParseException {
        final int length = period.length();
        if (length!=0 && Character.toUpperCase(period.charAt(0)) != 'P') {
            throw new ParseException(Errors.format(Errors.Keys.UNPARSABLE_STRING_$2,
                    period, period.substring(0,1)), 0);
        }
        long millis = 0;
        boolean time = false;
        int lower = 0;
        while (++lower < length) {
            char letter = Character.toUpperCase(period.charAt(lower));
            if (letter == 'T') {
                time = true;
                if (++lower >= length) {
                    break;
                }
            }
            int upper = lower;
            letter = period.charAt(upper);
            while (!Character.isLetter(letter) || letter == 'e' || letter == 'E') {
                if (++upper >= length) {
                    throw new ParseException(Errors.format(Errors.Keys.UNEXPECTED_END_OF_STRING), lower);
                }
                letter = period.charAt(upper);
            }
            letter = Character.toUpperCase(letter);
            final String number = period.substring(lower, upper);
            final double value;
            try {
                value = Double.parseDouble(number);
            } catch (NumberFormatException exception) {
                final ParseException e = new ParseException(Errors.format(
                        Errors.Keys.UNPARSABLE_NUMBER_$1, number), lower);
                e.initCause(exception);
                throw e;
            }
            final double factor;
            if (time) {
                switch (letter) {
                    case 'S': factor =       1000; break;
                    case 'M': factor =    60*1000; break;
                    case 'H': factor = 60*60*1000; break;
                    default: throw new ParseException("Unknown time symbol: " + letter, upper);
                }
            } else {
                switch (letter) {
                    case 'D': factor =               MILLIS_IN_DAY; break;
                    case 'W': factor =           7 * MILLIS_IN_DAY; break;
                    case 'M': factor =          30 * MILLIS_IN_DAY; break;
                    case 'Y': factor =      365.25 * MILLIS_IN_DAY; break;
                    default: throw new ParseException("Unknown period symbol: " + letter, upper);
                }
            }
            millis += Math.round(value * factor);
            lower = upper;
        }
        return millis;
    }

    /**
     * Convert a string containing a date into a {@link Date}, respecting the ISO 8601 standard.
     *
     * @param strTime Date as a string.
     * @return A date parsed from a string, or {@code null} if it doesn't respect the ISO 8601.
     * @throws java.text.ParseException
     */
    public static Date toDate(final String strTime) throws ParseException {
        if (strTime == null) {
            return null;
        }
        final List<Date> dates = new ArrayList<Date>();
        TimeParser.parse(strTime, 0L, dates);
        return (dates != null && !dates.isEmpty()) ? dates.get(0) : null;
    }

    /**
     * Required for internal working only.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof TimeParser) {
            final TimeParser that = (TimeParser) other;
            return this.numDateFields == that.numDateFields &&
                   this.numTimeFields == that.numTimeFields &&
                   this.hasTimeZone   == that.hasTimeZone;
        }
        return false;
    }

    /**
     * Required for internal working only.
     */
    @Override
    public int hashCode() {
        // For hasTimeZone we use the same values than Boolean.hashCode().
        return numDateFields + 37*numTimeFields + (hasTimeZone ? 1231 : 1237);
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append('[').append(numDateFields).append(',').append(numTimeFields);
        final DateFormat format = PARSERS.get(this);
        if (format instanceof SimpleDateFormat) {
            builder.append(",\"").append(((SimpleDateFormat) format).toPattern()).append('"');
        }
        return builder.append(']').toString();
    }
}
