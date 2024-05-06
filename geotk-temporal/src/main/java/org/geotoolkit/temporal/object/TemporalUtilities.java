/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.util.Map;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.util.SimpleInternationalString;

import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.collection.UnSynchronizedCache;


import static org.geotoolkit.temporal.object.TemporalConstants.*;

/**
 * This is a tool class to convert DateTime from ISO8601 to Date object.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public final class TemporalUtilities {

    private static final Logger LOGGER = Logger
            .getLogger("org.geotoolkit.temporal.object");
    private static final String DEFAULT_TIMEZONE = TimeZone.getDefault()
            .getID();

    /**
     * Hack for french datas, must find another way to do so. handle all local ?
     * impossible handle the current local ? If the server has a different local
     * then the client ? won't work
     */
    private static final List<String> FR_POOL = new ArrayList<String>() {
        @Override
        public int indexOf(Object o) {
            final String candidate = (String) o;
            for (int i = 0, n = FR_POOL.size(); i < n; i++) {
                if (FR_POOL.get(i).equalsIgnoreCase(candidate)) {
                    return i;
                }
            }
            return -1;
        }
    };

    /**
     * Caution : those objects are not thread safe, take care to synchronize
     * when you use them.
     */
    private static final SimpleDateFormat sdf1 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ");
    private static final SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd");
    static {
        // we don't hour here so we put the timeZone to GMT+0
        // 02/04/2012  why GMT+0 ? adding additional dateFormat sdf6
        sdf2.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }
    private static final SimpleDateFormat sdf3 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static final SimpleDateFormat sdf4 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat sdf5 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ");

    private static final SimpleDateFormat sdf6 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd");
    private static final SimpleDateFormat sdf7 = new java.text.SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
    static {
        sdf6.setTimeZone(TimeZone.getDefault());
    }

    private static final Map<String, TimeZone> TIME_ZONES = new UnSynchronizedCache<String, TimeZone>(
            50) {
        @Override
        public TimeZone get(Object o) {
            @SuppressWarnings("element-type-mismatch")
            TimeZone tz = super.get(o);
            if (tz == null) {
                tz = TimeZone.getTimeZone((String) o);
                put((String) o, tz);
            }
            return tz;
        }
    };

    static {
        FR_POOL.add("janvier");
        FR_POOL.add("février");
        FR_POOL.add("mars");
        FR_POOL.add("avril");
        FR_POOL.add("mai");
        FR_POOL.add("juin");
        FR_POOL.add("juillet");
        FR_POOL.add("août");
        FR_POOL.add("septembre");
        FR_POOL.add("octobre");
        FR_POOL.add("novembre");
        FR_POOL.add("décembre");
    }

    private TemporalUtilities() {
    }

    /**
     * Returns a Date object from an ISO-8601 representation string. (String
     * defined with pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ or yyyy-MM-dd).
     *
     * @param dateString
     *
     * @return Date result of parsing the given string
     * @throws ParseException
     */
    public static Date getDateFromString(final String dateString) throws ParseException {
       return getDateFromString(dateString, false);
    }

    /**
     * Returns a Date object from an ISO-8601 representation string. (String
     * defined with pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ or yyyy-MM-dd).
     *
     * @param dateString
     * @param noGMTO
     *            : will use date parser with default timezone for input with no time
     *            (dd-MM-yyyy) instead of GMT+0.
     * @return Date result of parsing the given string
     * @throws ParseException
     */
    public static Date getDateFromString(String dateString, final boolean noGMTO)
            throws ParseException {

        boolean defaultTimezone = false;
        final int indexT = dateString.indexOf('T');
        if (indexT > 0) {
            int tzIndex = dateString.lastIndexOf('+');
            if (tzIndex == -1) {
                tzIndex = dateString.lastIndexOf('-');
            }
            if (tzIndex > indexT) {
                String timezoneStr = dateString.substring(tzIndex + 1);

                if (timezoneStr.indexOf(':') > 0) {
                    // e.g : 1985-04-12T10:15:30+04:00
                    timezoneStr = timezoneStr.replace(":", "");
                    dateString = dateString.substring(0, tzIndex + 1).concat(
                            timezoneStr);
                } else if (timezoneStr.length() == 2) {
                    // e.g : 1985-04-12T10:15:30-04
                    dateString = dateString.concat("00");
                }
            } else if (dateString.charAt(dateString.length() - 1) == 'Z') {
                // e.g : 1985-04-12T10:15:30Z
                dateString = dateString.substring(0, dateString.length() - 1)
                        .concat("+0000");
            } else {
                // e.g : 1985-04-12T10:15:30
                defaultTimezone = true;
            }
            if (dateString.indexOf('.') > 0) {
                // simple date format is not thread safe
                synchronized (sdf3) {
                    return sdf3.parse(dateString);
                }
            }
            if (defaultTimezone) {
                // applying default timezone
                // simple date format is not thread safe
                synchronized (sdf4) {
                    return sdf4.parse(dateString);
                }
            } else {
                final String timezone = getTimeZone(dateString);
                // simple date format is not thread safe
                synchronized (sdf1) {
                    sdf1.setTimeZone(TIME_ZONES.get(timezone));
                    return sdf1.parse(dateString);
                }
            }
        } else if (dateString.indexOf('-') > 0) {
            // simple date format is not thread safe
            if (noGMTO) {
                synchronized (sdf6) {
                    return sdf6.parse(dateString);
                }
            } else {
                synchronized (sdf2) {
                    return sdf2.parse(dateString);
                }
            }
        }
        throw new ParseException("Unable to parse given string as a date with regular date formats", 0);
    }

    private static String getTimeZone(final String dateString) {
        if (dateString.charAt(dateString.length() - 1) == 'Z') {
            return "GMT+0";
        }
        int index = dateString.lastIndexOf('+');
        if (index == -1) {
            index = dateString.lastIndexOf('-');
        }
        if (index > dateString.indexOf('T')) {
            return "GMT" + dateString.substring(index);
        }
        return DEFAULT_TIMEZONE;
    }

    /**
     * Returns a DefaultPeriodDuration instance parsed from a string that
     * respect ISO8601 format ie: PnYnMnDTnHnMnS where n is an integer
     *
     * @TODO maybe should check by Pattern of string before and should throw an
     *       exception when it is bad format
     *
     * @param periodDuration
     * @return duration in millisenconds represented by this string duration.
     */
    public static DefaultDuration getDurationFromString(String periodDuration) {
        if (periodDuration == null) {
            return null;
        }
        String nbYear = null, nbMonth = null, nbWeek = null, nbDay = null, nbHour = null, nbMin = null, nbSec = null;

        // remove first char 'P'
        periodDuration = periodDuration.substring(1);

        // looking for the period years
        if (periodDuration.indexOf('Y') != -1) {
            nbYear = periodDuration.substring(0, periodDuration.indexOf('Y'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('Y') + 1);
        }
        // looking for the period months
        if (periodDuration.indexOf('M') != -1
                && (periodDuration.indexOf('T') == -1 || periodDuration
                        .indexOf('T') > periodDuration.indexOf('M'))) {
            nbMonth = periodDuration.substring(0, periodDuration.indexOf('M'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('M') + 1);
        }
        // looking for the period weeks
        if (periodDuration.indexOf('W') != -1) {
            nbWeek = periodDuration.substring(0, periodDuration.indexOf('W'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('W') + 1);
        }
        // looking for the period days
        if (periodDuration.indexOf('D') != -1) {
            nbDay = periodDuration.substring(0, periodDuration.indexOf('D'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('D') + 1);
        }
        // if the periodDuration is not over we pass to the hours by removing
        // 'T'
        if (periodDuration.indexOf('T') != -1) {
            periodDuration = periodDuration.substring(1);
        }
        // looking for the period hours
        if (periodDuration.indexOf('H') != -1) {
            nbHour = periodDuration.substring(0, periodDuration.indexOf('H'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('H') + 1);
        }
        // looking for the period minutes
        if (periodDuration.indexOf('M') != -1) {
            nbMin = periodDuration.substring(0, periodDuration.indexOf('M'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('M') + 1);
        }
        // looking for the period seconds
        if (periodDuration.indexOf('S') != -1) {
            nbSec = periodDuration.substring(0, periodDuration.indexOf('S'));
            periodDuration = periodDuration.substring(periodDuration
                    .indexOf('S') + 1);
        }
        if (periodDuration.length() != 0) {
            throw new IllegalArgumentException(
                    "The period descritpion is malformed, should not respect ISO8601 : "
                            + periodDuration);
        }
        return new DefaultPeriodDuration(
                nbYear!=null?new SimpleInternationalString(nbYear):null,
                nbMonth!=null?new SimpleInternationalString(nbMonth):null,
                nbWeek!=null?new SimpleInternationalString(nbWeek):null,
                nbDay!=null?new SimpleInternationalString(nbDay):null,
                nbHour!=null?new SimpleInternationalString(nbHour):null,
                nbMin!=null?new SimpleInternationalString(nbMin):null,
                nbSec!=null?new SimpleInternationalString(nbSec):null);
    }

    /**
     * Try to parse a date from different well knowed writing types.
     * CAUTION : time zone will be local TimeZone unless the date string specify it.
     *
     * @param date
     *            String to parse
     * @return resulting parsed Date.
     * @throws ParseException
     *             if String is not valid.
     * @throws NullPointerException
     *             if String is null.
     * @deprecated use parseDateCal
     */
    @Deprecated
    public static Date parseDate(final String date) throws ParseException,
            NullPointerException {
        return parseDate(date, false);

    }

    /**
     * Try to parse a date from different well knowed writing types.
     * CAUTION : time zone will be local TimeZone unless the date string specify it.
     *
     * @param date
     *            String to parse
     * @param noGMTO
     *            : will use date parser with default timezone for input with no time
     *            (dd-MM-yyyy) instead of GMT+0.
     * @return resulting parsed Date.
     * @throws ParseException
     *             if String is not valid.
     * @throws NullPointerException
     *             if String is null.
     * @deprecated use parseDateCal
     */
    @Deprecated
    public static Date parseDate(final String date, final boolean noGMTO) throws ParseException,
            NullPointerException {
        final Calendar cal = parseDateCal(date);
        return cal.getTime();
    }

    /**
     * Try to parse a date from different well knowed writing types.
     * Calendar time zone will be local TimeZone unless the date string specify it.
     *
     * @param date
     *            String to parse
     * @return Calendar
     * @throws ParseException
     * @throws NullPointerException
     */
    public static Calendar parseDateCal(final String date) throws ParseException,
            NullPointerException {

        if (date.endsWith("BC")) {
            throw new ParseException(
                    "Date is marked as Before Christ, not possible to parse it",
                    date.length());
        }

        final int[] slashOccurences = StringUtilities.getIndexes(date, '/');

        if (slashOccurences.length == 1) {
            // date is like : 11/2050
            final int month = parseInt(date.substring(0, slashOccurences[0])) - 1;
            final int year = parseInt(date.substring(slashOccurences[0] + 1,
                    date.length()));
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;

        } else if (slashOccurences.length == 2) {
            // date is like : 23/11/2050
            final int day = parseInt(date.substring(0, slashOccurences[0]));
            final int month = parseInt(date.substring(slashOccurences[0] + 1,
                    slashOccurences[1])) - 1;
            final int year = parseInt(date.substring(slashOccurences[1] + 1));
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }

        final int[] spaceOccurences = StringUtilities.getIndexes(date, ' ');
        final int[] dashOccurences = StringUtilities.getIndexes(date, '-');
        final int[] dotsOccurences = StringUtilities.getIndexes(date, ':');

        if(dotsOccurences.length==4 && spaceOccurences.length==1){
            // date is like 2011:08:11 11:22:22
            // this form has been found in geotiff datatime fields
            final int year = parseInt(date.substring(0, dotsOccurences[0]));
            final int month = parseInt(date.substring(dotsOccurences[0] + 1, dotsOccurences[1])) - 1;
            final int day = parseInt(date.substring(dotsOccurences[1] + 1, spaceOccurences[0]));
            final int hour = parseInt(date.substring(spaceOccurences[0] + 1, dotsOccurences[2]));
            final int min = parseInt(date.substring(dotsOccurences[2] + 1, dotsOccurences[3]));
            final int sec = parseInt(date.substring(dotsOccurences[3] + 1));

            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, hour, min, sec);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }else if (spaceOccurences.length == 2) {
            // date is like : 18 janvier 2050
            final int day = parseInt(date.substring(0, spaceOccurences[0]));
            final int month = FR_POOL.indexOf(date.substring(
                    spaceOccurences[0] + 1, spaceOccurences[1]));
            final int year = parseInt(date.substring(spaceOccurences[1] + 1));
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;

        } else if (spaceOccurences.length == 1 && dashOccurences.length < 3) {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                df.parse(date);
                return df.getCalendar();
            } catch (ParseException ex) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date
                        + " with dateFormat : " + df);
            }

            // date is like : Janvier 2050
            final int month = FR_POOL.indexOf(date.substring(0,
                    spaceOccurences[0]));
            final int year = parseInt(date.substring(spaceOccurences[0] + 1));
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;

        } else if (dashOccurences.length == 1) {
            if (dashOccurences[0] == 2) {
                // date is like : 05-2050
                final int month = parseInt(date.substring(0, dashOccurences[0])) - 1;
                final int year = parseInt(date.substring(dashOccurences[0] + 1));
                final Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal;
            } else {
                // date is like : 2050-05
                final int year = parseInt(date.substring(0, dashOccurences[0]));
                final int month = parseInt(date
                        .substring(dashOccurences[0] + 1)) - 1;
                final Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal;
            }

        } else if (dashOccurences.length >= 2) {
            // if date is in format yyyy-mm-ddTHH:mm:ss
            try {
                final ISODateParser fp = new ISODateParser();
                final Calendar resultDate = fp.getCalendar(date);
                if (resultDate != null) {
                    return resultDate;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date
                        + " with getDateFromString method.");
            }

            if (dashOccurences[0] == 4) {
                // date is like 2010-11-23Z
                final int year = parseInt(date.substring(0, dashOccurences[0]));
                final int month = parseInt(date.substring(
                        dashOccurences[0] + 1, dashOccurences[1])) - 1;

                final int day;
                if (date.endsWith("Z")) {
                    day = parseInt(date.substring(dashOccurences[1] + 1,
                            date.length() - 1));
                } else {
                    day = parseInt(date.substring(dashOccurences[1] + 1));
                }

                final Calendar cal = Calendar.getInstance();
                cal.set(year, month, day, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal;
            } else {
                // date is like 23-11-2010
                final int day = parseInt(date.substring(0, dashOccurences[0]));
                final int month = parseInt(date.substring(
                        dashOccurences[0] + 1, dashOccurences[1])) - 1;
                final int year = parseInt(date.substring(dashOccurences[1] + 1));
                final Calendar cal = Calendar.getInstance();
                cal.set(year, month, day, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal;
            }

        } else if (dashOccurences.length == 0) {
            // date is like 2010
            final int year = parseInt(date);
            final Calendar cal = Calendar.getInstance();
            cal.set(year, 0, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }

        throw new ParseException("Invalid date format : " + date, 0);
    }


    /**
     * @see TemporalUtilities#parseDate(java.lang.String)
     * CAUTION : time zone will be local TimeZone unless the date string specify it.
     *
     * @param date
     *            : string to parse.
     * @param neverNull
     *            : will return today's date if parsing fail, otherwise return
     *            null if parsing fails.
     * @return result of the parsed string or today's date or null if neverNull
     *         is false.
     */
    public static Date parseDateSafe(final String date, final boolean neverNull) {
        return parseDateSafe(date, neverNull, false);
    }

    /**
     * @see TemporalUtilities#parseDate(java.lang.String)
     * CAUTION : time zone will be local TimeZone unless the date string specify it.
     *
     * @param date
     *            : string to parse.
     * @param neverNull
     *            : will return today's date if parsing fail, otherwise return
     *            null if parsing fails.
     * @param noGMTO
     *            : will use date parser with default timezone for input with no time
     *            (dd-MM-yyyy) instead of GMT+0.
     * @return result of the parsed string or today's date or null if neverNull
     *         is false.
     */
    public static Date parseDateSafe(final String date, final boolean neverNull, final boolean noGMTO) {
        if (date != null) {
            try {
                return parseDate(date, noGMTO);
            } catch (ParseException ex) {
                // do nothing
            }
        }
        return (neverNull) ? new Date() : null;
    }

    private static int parseInt(final String candidate) throws ParseException {
        try {
            return Integer.parseInt(candidate);
        } catch (NumberFormatException ex) {
            ParseException pex = new ParseException(ex.getLocalizedMessage(), 0);
            pex.initCause(ex);
            throw pex;
        }
    }

    /**
     * Return a time description on the form "Ny Nm Nd Nh Nmin Ns Nms" from a
     * millisecond time.
     *
     * @param time
     *            A time value in millisecond
     * @return A string on the form "Xmin Ys Zms".
     */
    public static String durationToString(long time) {
        if (time == 0) {
            return "0ms";
        }
        final long years = time / YEAR_MS;
        time = time % YEAR_MS;
        final long months = time / MONTH_MS;
        time = time % MONTH_MS;
        final long days = time / DAY_MS;
        time = time % DAY_MS;
        final long hours = time / HOUR_MS;
        time = time % HOUR_MS;
        final long minuts = time / MINUTE_MS;
        time = time % MINUTE_MS;
        final long seconds = time / SECOND_MS;
        time = time % SECOND_MS;
        final long millis = time;

        final var sb = new StringBuilder();
        if (years > 0)
            sb.append(years).append("y ");
        if (months > 0)
            sb.append(months).append("m ");
        if (days > 0)
            sb.append(days).append("d ");
        if (hours > 0)
            sb.append(hours).append("h ");
        if (minuts > 0)
            sb.append(minuts).append("min ");
        if (seconds > 0)
            sb.append(seconds).append("s ");
        if (millis > 0)
            sb.append(millis).append("ms");

        final int size = sb.length();
        if (sb.charAt(size - 1) == ' ') {
            return sb.substring(0, size - 1);
        } else {
            return sb.toString();
        }
    }

    /**
     * Format date using pattern yyyy-MM-dd'T'HH:mm:ss
     *
     * @param date
     *            : date to format
     * @return ISO 8601 string or empty string if date is null
     */
    public static String toISO8601(final Date date) {
        if (date != null) {
            synchronized (sdf4) {
                return sdf4.format(date);
            }
        }
        LOGGER.log(Level.INFO,
                "ISO 8601 format can not proceed because date is null.");
        return "";
    }

    /**
     * Format date with TimeZone using pattern yyyy-MM-dd'T'HH:mm:ss
     *
     * @param date
     *            : date to format
     * @param timezone
     *            : timezone for date
     * @return ISO 8601 string or empty string if date is null
     */
    public static String toISO8601(final Date date, TimeZone timezone) {
        if (date != null) {
            synchronized (sdf5) {
                if (timezone != null) {
                    sdf5.setTimeZone(timezone);
                } else {
                    SimpleTimeZone tz = new SimpleTimeZone(0, "Out Timezone");
                    sdf5.setTimeZone(tz);
                }

                return sdf5.format(date);
            }
        }
        LOGGER.log(Level.INFO,
                "ISO 8601 format can not proceed because date is null.");
        return "";
    }

    /**
     * Format date with TimeZone using pattern yyyy-MM-dd'T'HH:mm:ssZ
     *
     * @param date
     *            : date to format
     * @param timezone
     *            : timezone for date
     * @return ISO 8601 string or empty string if date is null
     */
    public static String toISO8601Z(final Date date, TimeZone timezone) {
        if (date != null) {
            synchronized (sdf7) {
                if (timezone != null) {
                    sdf7.setTimeZone(timezone);
                } else {
                    SimpleTimeZone tz = new SimpleTimeZone(0, "Out Timezone");
                    sdf7.setTimeZone(tz);
                }

                return sdf7.format(date);
            }
        }
        LOGGER.log(Level.INFO,
                "ISO 8601 format can not proceed because date is null.");
        return "";
    }

    /**
     * Compare the the number of milliseconds since January 1, 1970, 00:00:00 GMT between two dates instead of object equality.
     * This allow two equals date of different implementation (like Timestamp and Date) to be compared.
     *
     * @param d1 first date.
     * @param d2 second date
     * @return {@code true} if the number of milliseconds since January 1, 1970, 00:00:00 GMT of the two dates are equals.
     */
    public static boolean dateEquals(Date d1, Date d2) {
        if (d1 != null && d2 != null) {
            return d1.getTime() == d2.getTime();
        }
        return Objects.equals(d1, d2);
    }

}
