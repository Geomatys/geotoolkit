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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.temporal.reference.DefaultTemporalCoordinateSystem;

import org.geotoolkit.util.logging.Logging;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.DateAndTime;
import org.opengis.temporal.Duration;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.OrdinalPosition;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalCoordinateSystem;

/**
 * This is a tool class to convert DateTime from ISO8601 to Date object.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public final class Utils {

    private static final Logger LOGGER = Logging.getLogger(Utils.class);

    /**
     * The number of millisecond in one year.
     */
    private static final long YEAR_MS = 31536000000L;
    /**
     * The number of millisecond in one month.
     */
    private static final long MONTH_MS = 2628000000L;
    /**
     * The number of millisecond in one week.
     */
    private static final long WEEK_MS = 604800000L;
    /**
     * The number of millisecond in one day.
     */
    private static final long DAY_MS = 86400000L;
    /**
     * The number of millisecond in one hour.
     */
    private static final long HOUR_MS = 3600000L;
    /**
     * The number of millisecond in one minute.
     */
    private static final long MIN_MS = 60000;
    /**
     * The number of millisecond in one second.
     */
    private static final long SECOND_MS = 1000;

    /**
     * The units for months.
     *
     * @todo <a href="http://kenai.com/jira/browse/JSR_275-41">JSR-275 bug</a>
     */
    public static final Unit<javax.measure.quantity.Duration> MONTH_UNIT = NonSI.DAY.times(MONTH_MS / DAY_MS);

    /**
     * The units for years.
     *
     * @todo <a href="http://kenai.com/jira/browse/JSR_275-41">JSR-275 bug</a>
     */
    public static final Unit<javax.measure.quantity.Duration> YEAR_UNIT = NonSI.DAY.times(YEAR_MS / DAY_MS);

    private Utils(){

    }

    /**
     * Returns a Date object from an ISO-8601 representation string. (String defined with pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ or yyyy-MM-dd).
     * @param dateString
     * @return Date result of parsing the given string
     */
    public static Date getDateFromString(String dateString) throws ParseException {
        final String dateFormat1 = "yyyy-MM-dd'T'HH:mm:ssZ";
        final String dateFormat2 = "yyyy-MM-dd";
        final String dateFormat3 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        final String dateFormat4 = "yyyy-MM-dd'T'HH:mm:ss";
        final SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat1);
        final SimpleDateFormat sdf2 = new java.text.SimpleDateFormat(dateFormat2);
        final SimpleDateFormat sdf3 = new java.text.SimpleDateFormat(dateFormat3);
        final SimpleDateFormat sdf4 = new java.text.SimpleDateFormat(dateFormat4);

        boolean defaultTimezone = false;

        if (dateString.contains("T")) {
            String timezoneStr;
            int index = dateString.lastIndexOf('+');
            if (index == -1) {
                index = dateString.lastIndexOf('-');
            }
            if (index > dateString.indexOf('T')) {
                timezoneStr = dateString.substring(index + 1);

                if (timezoneStr.contains(":")) {
                    //e.g : 1985-04-12T10:15:30+04:00
                    timezoneStr = timezoneStr.replace(":", "");
                    dateString = dateString.substring(0, index + 1).concat(timezoneStr);
                } else if (timezoneStr.length() == 2) {
                    //e.g : 1985-04-12T10:15:30-04
                    dateString = dateString.concat("00");
                }
            } else if (dateString.endsWith("Z")) {
                //e.g : 1985-04-12T10:15:30Z
                dateString = dateString.substring(0, dateString.length() - 1).concat("+0000");
            } else {
                //e.g : 1985-04-12T10:15:30
                defaultTimezone = true;
            }
            final String timezone = getTimeZone(dateString);
            sdf.setTimeZone(TimeZone.getTimeZone(timezone));

            if (dateString.contains(".")) {
                return sdf3.parse(dateString);
            }
            if ( ! defaultTimezone ) {
                return sdf.parse(dateString);
            }else {
                //applying default timezone
                return sdf4.parse(dateString);
            }
        }
        if (dateString.contains("-")) {
            return sdf2.parse(dateString);
        }
        return null;
    }

    public static String getTimeZone(final String dateString) {
        if (dateString.endsWith("Z")) {
            return "GMT+" + 0;
        }
        int index = dateString.lastIndexOf('+');
        if (index == -1) {
            index = dateString.lastIndexOf('-');
        }
        if (index > dateString.indexOf('T')) {
            return "GMT" + dateString.substring(index);
        }
        return TimeZone.getDefault().getID();
    }

    /**
     * Return a Date (long time) from a String description
     * 
     * @param periodDuration
     * @return duration in millisenconds represented by this string duration.
     */
    public static long getTimeInMillis(String periodDuration) {

        long time = 0;
        //we remove the 'P'
        periodDuration = periodDuration.substring(1);

        //we look if the period contains years (31536000000 ms)
        if (periodDuration.indexOf('Y') != -1) {
            final int nbYear = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('Y')));
            time += nbYear * YEAR_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('Y') + 1);
        }

        //we look if the period contains months (2628000000 ms)
        if (periodDuration.indexOf('M') != -1 &&
                (periodDuration.indexOf('T') == -1 || periodDuration.indexOf('T') > periodDuration.indexOf('M'))) {
            final int nbMonth = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('M')));
            time += nbMonth * MONTH_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('M') + 1);
        }

        //we look if the period contains weeks (604800000 ms)
        if (periodDuration.indexOf('W') != -1) {
            final int nbWeek = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('W')));
            time += nbWeek * WEEK_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('W') + 1);
        }

        //we look if the period contains days (86400000 ms)
        if (periodDuration.indexOf('D') != -1) {
            final int nbDay = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('D')));
            time += nbDay * DAY_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('D') + 1);
        }

        //if the periodDuration is not over we pass to the hours by removing 'T'
        if (periodDuration.indexOf('T') != -1) {
            periodDuration = periodDuration.substring(1);
        }

        //we look if the period contains hours (3600000 ms)
        if (periodDuration.indexOf('H') != -1) {
            final int nbHour = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('H')));
            time += nbHour * HOUR_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('H') + 1);
        }

        //we look if the period contains minutes (60000 ms)
        if (periodDuration.indexOf('M') != -1) {
            final int nbMin = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('M')));
            time += nbMin * MIN_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('M') + 1);
        }

        //we look if the period contains seconds (1000 ms)
        if (periodDuration.indexOf('S') != -1) {
            final int nbSec = Integer.parseInt(periodDuration.substring(0, periodDuration.indexOf('S')));
            time += nbSec * SECOND_MS;
            periodDuration = periodDuration.substring(periodDuration.indexOf('S') + 1);
        }

        if (periodDuration.length() != 0) {
            throw new IllegalArgumentException("The period descritpion is malformed");
        }
        return time;
    }

    /**
     * Convert a JulianDate to Date
     */
    public static Date julianToDate(final JulianDate jdt) {
        if (jdt == null) {
            return null;
        }
        Date response = null;

        final int gregDays = 15 + 31 * (10 + 12 * 1582);
        int jalpha, ja, jb, jc, jd, je, year, month, day;
        ja = (int) jdt.getCoordinateValue().intValue();
        if (ja >= gregDays) {
            jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
            ja = ja + 1 + jalpha - jalpha / 4;
        }

        jb = ja + 1524;
        jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
        jd = 365 * jc + jc / 4;
        je = (int) ((jb - jd) / 30.6001);
        day = jb - jd - (int) (30.6001 * je);
        month = je - 1;
        if (month > 12) {
            month = month - 12;
        }
        year = jc - 4715;
        if (month > 2) {
            year--;
        }
        if (year <= 0) {
            year--;
        }
        final Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        response = cal.getTime();
        return response;
    }

    /**
     * Convert a CalendarDate object to java.util.Date.
     * @param calDate
     */
    public static Date calendarDateToDate(final CalendarDate calDate) {
        if (calDate == null || !(calDate instanceof DefaultCalendarDate)){
            return null;
        }

        final DefaultCalendarDate caldate = (DefaultCalendarDate) calDate;
        final Calendar calendar = Calendar.getInstance();
        
        final int[] cal = calDate.getCalendarDate();
        int year = 0;
        int month = 0;
        int day = 0;
        if (cal.length > 3) {
            throw new IllegalArgumentException("The CalendarDate integer array is malformed ! see ISO 8601 format.");
        } else {
            year = cal[0];
            if (cal.length > 0) {
                month = cal[1];
            }
            if (cal.length > 1) {
                day = cal[2];
            }
            calendar.set(year, month, day);
            return calendar.getTime();
        }

    }

    /**
     * Convert a DateAndTime object to Date.
     * @param dateAndTime
     * @return converted DateAndTime in Date
     */
    public static Date dateAndTimeToDate(final DateAndTime dateAndTime) {
        if (dateAndTime == null && !(dateAndTime instanceof DefaultDateAndTime)) {
            return null;
        }

        final DefaultDateAndTime dateTime = (DefaultDateAndTime) dateAndTime;
        final Calendar calendar = Calendar.getInstance();

        final int[] cal = dateTime.getCalendarDate();
        int year = 0;
        int month = 0;
        int day = 0;
        if (cal.length > 3) {
            throw new IllegalArgumentException("The CalendarDate integer array is malformed ! see ISO 8601 format.");
        } else {
            year = cal[0];
            if (cal.length > 0) {
                month = cal[1];
            }
            if (cal.length > 1) {
                day = cal[2];
            }
        }

        final Number[] clock = dateTime.getClockTime();
        final Number hour;
        Number minute = 0;
        Number second = 0;
        if (clock.length > 3) {
            throw new IllegalArgumentException("The ClockTime Number array is malformed ! see ISO 8601 format.");
        } else {
            hour = clock[0];
            if (clock.length > 0) {
                minute = clock[1];
            }
            if (clock.length > 1) {
                second = clock[2];
            }
        }
        calendar.set(year, month, day, hour.intValue(), minute.intValue(), second.intValue());
        return calendar.getTime();
        
    }

    /**
     * Convert a TemporalCoordinate object to Date.
     * @param temporalCoord
     */
    public static Date temporalCoordToDate(final TemporalCoordinate temporalCoord) {
        if (temporalCoord == null) {
            return null;
        }
        final Calendar calendar = Calendar.getInstance();
        final DefaultTemporalCoordinate timeCoord = (DefaultTemporalCoordinate) temporalCoord;
        final Number value = timeCoord.getCoordinateValue();
        if (timeCoord.getFrame() instanceof TemporalCoordinateSystem) {
            final DefaultTemporalCoordinateSystem coordSystem = (DefaultTemporalCoordinateSystem) timeCoord.getFrame();
            final Date origin = coordSystem.getOrigin();
            final String interval = coordSystem.getInterval().toString();

            Long timeInMS = 0L;

            if (interval.equals("year")) {
                timeInMS = value.longValue() * YEAR_MS;
            } else if (interval.equals("month")) {
                timeInMS = value.longValue() * MONTH_MS;
            } else if (interval.equals("week")) {
                timeInMS = value.longValue() * WEEK_MS;
            } else if (interval.equals("day")) {
                timeInMS = value.longValue() * DAY_MS;
            } else if (interval.equals("hour")) {
                timeInMS = value.longValue() * HOUR_MS;
            } else if (interval.equals("minute")) {
                timeInMS = value.longValue() * MIN_MS;
            } else if (interval.equals("second")) {
                timeInMS = value.longValue() * SECOND_MS;
            } else {
                throw new IllegalArgumentException(" The interval of TemporalCoordinateSystem for this TemporalCoordinate object is unknown ! ");
            }
            timeInMS = timeInMS + origin.getTime();
            calendar.setTimeInMillis(timeInMS);
            return calendar.getTime();
        } else {
            throw new IllegalArgumentException("The frame of this TemporalCoordinate object must be an instance of TemporalCoordinateSystem");
        }
    }

    public static Date ordinalToDate(final OrdinalPosition ordinalPosition) {
        if (ordinalPosition == null) {
            return null;
        }
        final Calendar calendar = Calendar.getInstance();
        if (ordinalPosition.getOrdinalPosition() != null) {
            final Date beginEra = ordinalPosition.getOrdinalPosition().getBeginning();
            final Date endEra = ordinalPosition.getOrdinalPosition().getEnd();
            final Long middle = ((endEra.getTime() - beginEra.getTime()) / 2) + beginEra.getTime();
            calendar.setTimeInMillis(middle);
            return calendar.getTime();
        } else {
            return null;
        }
    }

    /**
     * @return the nearest Unit of a Duration.
     */
    public static Unit getUnitFromDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        final DefaultDuration dduration = (DefaultDuration) duration;
        final long mills = dduration.getTimeInMillis();
        long temp = mills / YEAR_MS;
        if (temp >= 1) {
            return YEAR_UNIT;
        }
        temp = mills / MONTH_MS;
        if (temp >= 1) {
            return MONTH_UNIT;
        }
        temp = mills / WEEK_MS;
        if (temp >= 1) {
            return NonSI.WEEK;
        }
        temp = mills / DAY_MS;
        if (temp >= 1) {
            return NonSI.DAY;
        }
        temp = mills / HOUR_MS;
        if (temp >= 1) {
            return NonSI.HOUR;
        }
        temp = mills / MIN_MS;
        if (temp >= 1) {
            return NonSI.MINUTE;
        }
        temp = mills / SECOND_MS;
        if (temp >= 1) {
            return SI.SECOND;
        }
        return null;
    }
    
    /**
     * this method creates a date from a string, support for many formats.
     * @param date
     * @return Date
     */
    public static Date createDate(String date) {
        if (date == null) {
            return new Date(new java.util.Date().getTime());
        }
        if (date.equals("") || date.contains("BC")) {
            return new Date(new java.util.Date().getTime());
        }

        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final Map<String, String> pool = new HashMap<String, String>();
        pool.put("janvier", "01");
        pool.put("février", "02");
        pool.put("mars", "03");
        pool.put("avril", "04");
        pool.put("mai", "05");
        pool.put("juin", "06");
        pool.put("juillet", "07");
        pool.put("août", "08");
        pool.put("septembre", "09");
        pool.put("octobre", "10");
        pool.put("novembre", "11");
        pool.put("décembre", "12");

        final Map<String, String> poolCase = new HashMap<String, String>();
        poolCase.put("Janvier", "01");
        poolCase.put("Février", "02");
        poolCase.put("Mars", "03");
        poolCase.put("Avril", "04");
        poolCase.put("Mai", "05");
        poolCase.put("Juin", "06");
        poolCase.put("Juillet", "07");
        poolCase.put("Août", "08");
        poolCase.put("Septembre", "09");
        poolCase.put("Octobre", "10");
        poolCase.put("Novembre", "11");
        poolCase.put("Décembre", "12");

        String year;
        String month;
        String day;
        Date tmp = new Date();

        if (date.contains("/")) {
            if (getOccurence(date, "/") == 2) {
                day = date.substring(0, date.indexOf('/'));
                date = date.substring(date.indexOf('/') + 1);
                month = date.substring(0, date.indexOf('/'));
                year = date.substring(date.indexOf('/') + 1);

                tmp = java.sql.Date.valueOf(year + "-" + month + "-" + day);
            } else {
                if (getOccurence(date, "/") == 1) {
                    month = date.substring(0, date.indexOf('/'));
                    year = date.substring(date.indexOf('/') + 1);
                    tmp = java.sql.Date.valueOf(year + "-" + month + "-" + "01");
                }
            }
        } else if (getOccurence(date, " ") == 2) {
            if (!date.contains("?")) {

                day = date.substring(0, date.indexOf(' '));
                date = date.substring(date.indexOf(' ') + 1);
                month = pool.get(date.substring(0, date.indexOf(' ')));
                year = date.substring(date.indexOf(' ') + 1);

                tmp = java.sql.Date.valueOf(year + "-" + month + "-" + day);
            } else {
                tmp = java.sql.Date.valueOf("2000" + "-" + "01" + "-" + "01");
            }
        } else if (getOccurence(date, " ") == 1 && getOccurence(date, "-") < 3) {
            try {
                final java.util.Date d = df.parse(date);
                return new Date(d.getTime());
            } catch (ParseException ex) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date +" with dateFormat : " + df);
            }
            month = poolCase.get(date.substring(0, date.indexOf(' ')));
            year = date.substring(date.indexOf(' ') + 1);
            tmp = java.sql.Date.valueOf(year + "-" + month + "-" + "01");


        } else if (getOccurence(date, "-") == 1) {

            month = date.substring(0, date.indexOf('-'));
            year = date.substring(date.indexOf('-') + 1);

            tmp = java.sql.Date.valueOf(year + "-" + month + "-" + "01");

        } else if (getOccurence(date, "-") == 2) {
            //if date is in format yyyy-mm-ddTHH:mm:ss
            try {
                final java.util.Date resultDate = getDateFromString(date);

                if (resultDate != null) {
                    return new Date(resultDate.getTime());
                }
            } catch (ParseException e) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date +" with getDateFromString method.");
            }

            if (date.substring(0, date.indexOf('-')).length() == 4) {
                year = date.substring(0, date.indexOf('-'));
                date = date.substring(date.indexOf('-') + 1); //mm-ddZ
                month = date.substring(0, date.indexOf('-'));
                date = date.substring(date.indexOf('-') + 1); // ddZ
                if (date.contains("Z")) {
                    date = date.substring(0, date.indexOf('Z'));
                }
                day = date;
                tmp = java.sql.Date.valueOf(year + "-" + month + "-" + day);
            } else {
                day = date.substring(0, date.indexOf('-'));
                date = date.substring(date.indexOf('-') + 1);
                month = date.substring(0, date.indexOf('-'));
                year = date.substring(date.indexOf('-') + 1);

                tmp = java.sql.Date.valueOf(year + "-" + month + "-" + day);
            }

        } else {
            if (getOccurence(date, "-") == 0) {
                year = date;
                tmp = java.sql.Date.valueOf(year + "-" + "01" + "-" + "01");
            }
        }

        return tmp;
    }
    
    /**
     * This method returns a number of occurences occ in the string s.
     */
    public static int getOccurence(String s, String occ) {
        if (!s.contains(occ)) {
            return 0;
        } else {
            int nbocc = 0;
            while (s.indexOf(occ) != -1) {
                s = s.substring(s.indexOf(occ) + 1);
                nbocc++;
            }
            return nbocc;
        }
    }
}

