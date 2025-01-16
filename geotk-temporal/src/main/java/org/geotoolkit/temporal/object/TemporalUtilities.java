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

import java.time.LocalDate;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.util.StringUtilities;
import static org.geotoolkit.temporal.object.TemporalConstants.*;

/**
 * This is a tool class to convert DateTime from ISO8601 to Date object.
 *
 * @author Mehdi Sidhoum (Geomatys)
 */
public final class TemporalUtilities {

    private static final Logger LOGGER = Logger
            .getLogger("org.geotoolkit.temporal.object");

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

    public static Optional<Temporal> toTemporal(Object value) {
        if (value instanceof Instant t) {
            return Optional.of(t);
        }
        if (value instanceof Date t) {
            return Optional.of(t.toInstant());
        }
        if (value instanceof org.opengis.temporal.Instant t) {
            return Optional.ofNullable(t.getPosition());
        }
        return Optional.empty();
    }

    public static Temporal toTemporal(Date t) {
        return org.apache.sis.temporal.TemporalDate.toTemporal(t);
    }

    public static Temporal toTemporal(org.opengis.temporal.Instant t) {
        return (t != null) ? t.getPosition() : null;
    }

    public static Instant toInstant(Temporal t) {
        return org.apache.sis.temporal.TemporalDate.toInstant(t, ZoneOffset.UTC);
    }

    public static Instant toInstant(org.opengis.temporal.Instant t) {
        return toInstant(toTemporal(t));
    }

    public static Date toDate(Temporal t) {
        return org.apache.sis.temporal.TemporalDate.toDate(t);
    }

    public static Date toDate(org.opengis.temporal.Instant t) {
        return toDate(toInstant(t));
    }

    /**
     * Try to parse a date from different well knowed writing types.
     * Calendar time zone will be local TimeZone unless the date string specify it.
     *
     * @param date
     *            String to parse
     */
    public static Calendar parseDateCal(final String date) throws ParseException {
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
        if (date != null) {
            try {
                return parseDateCal(date).getTime();
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
        return d1 == d2;
    }
}
