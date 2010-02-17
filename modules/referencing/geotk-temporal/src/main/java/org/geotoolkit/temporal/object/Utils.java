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
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalPosition;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalReferenceSystem;


import static org.geotoolkit.temporal.object.TemporalConstants.*;

/**
 * This is a tool class to convert DateTime from ISO8601 to Date object.
 * 
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public final class Utils {

    private static final Logger LOGGER = Logging.getLogger(Utils.class);
    private static final String DEFAULT_TIMEZONE = TimeZone.getDefault().getID();


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
            time += nbMin * MINUTE_MS;
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

        final int gregDays = 15 + 31 * (10 + 12 * 1582);
        int jalpha, ja, jb, jc, jd, je, year, month, day;
        ja = jdt.getCoordinateValue().intValue();
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

        return new Date(year*YEAR_MS + month*MONTH_MS + day*DAY_MS);
    }

    /**
     * Convert a CalendarDate object to java.util.Date.
     * @param calDate
     */
    public static Date calendarDateToDate(final CalendarDate calDate) {
        if (calDate == null){
            return null;
        }

        final int[] cal = calDate.getCalendarDate();

        if (cal.length > 3)
            throw new IllegalArgumentException("The CalendarDate integer array is malformed ! see ISO 8601 format.");

        return new Date(
                (cal.length>0 ? cal[0] : 0) * YEAR_MS +
                (cal.length>1 ? cal[1] : 0) * MONTH_MS +
                (cal.length>2 ? cal[2] : 0) * DAY_MS
                );
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

        final int[] cal = dateTime.getCalendarDate();
        final Number[] clock = dateTime.getClockTime();

        if (cal.length > 3)
            throw new IllegalArgumentException("The CalendarDate integer array is malformed ! see ISO 8601 format.");
        if (clock.length > 3)
            throw new IllegalArgumentException("The ClockTime Number array is malformed ! see ISO 8601 format.");

        return new Date(
                (cal.length>0 ? cal[0] : 0) * YEAR_MS +
                (cal.length>1 ? cal[1] : 0) * MONTH_MS +
                (cal.length>2 ? cal[2] : 0) * DAY_MS +
                (clock.length>0 ? clock[0].intValue() : 0) * HOUR_MS +
                (clock.length>1 ? clock[1].intValue() : 0) * MINUTE_MS +
                (clock.length>2 ? clock[2].intValue() : 0) * SECOND_MS
                );
    }

    /**
     * Convert a TemporalCoordinate object to Date.
     * @param temporalCoord
     * @return Date
     */
    public static Date temporalCoordToDate(final TemporalCoordinate temporalCoord) {
        if (temporalCoord == null) {
            return null;
        }

        final DefaultTemporalCoordinate timeCoord;
        if(temporalCoord instanceof DefaultTemporalCoordinate){
            timeCoord = (DefaultTemporalCoordinate) temporalCoord;
        }else{
            throw new IllegalArgumentException("Can not convert a temporal coordinate which is not a DefaultTemporalCoordinate.");
        }

        final long value = timeCoord.getCoordinateValue().longValue();
        final TemporalReferenceSystem frame = timeCoord.getFrame();
        if (frame instanceof DefaultTemporalCoordinateSystem) {
            final DefaultTemporalCoordinateSystem coordSystem = (DefaultTemporalCoordinateSystem) frame;
            final Date origin = coordSystem.getOrigin();
            final String interval = coordSystem.getInterval().toString();

            long timeInMS = 0L;

            if (YEAR_STR.equals(interval)) {
                timeInMS = value * YEAR_MS;
            } else if (MONTH_STR.equals(interval)) {
                timeInMS = value * MONTH_MS;
            } else if (WEEK_STR.equals(interval)) {
                timeInMS = value * WEEK_MS;
            } else if (DAY_STR.equals(interval)) {
                timeInMS = value * DAY_MS;
            } else if (HOUR_STR.equals(interval)) {
                timeInMS = value * HOUR_MS;
            } else if (MINUTE_STR.equals(interval)) {
                timeInMS = value * MINUTE_MS;
            } else if (SECOND_STR.equals(interval)) {
                timeInMS = value * SECOND_MS;
            } else {
                throw new IllegalArgumentException(" The interval of TemporalCoordinateSystem for this TemporalCoordinate object is unknown ! ");
            }
            timeInMS = timeInMS + origin.getTime();
            return new Date(timeInMS);
        } else {
            throw new IllegalArgumentException("The frame of this TemporalCoordinate object must be an instance of DefaultTemporalCoordinateSystem");
        }
    }

    public static Date ordinalToDate(final OrdinalPosition ordinalPosition) {
        if (ordinalPosition == null) {
            return null;
        }
        final OrdinalEra era = ordinalPosition.getOrdinalPosition();
        if (era != null) {
            final Date beginEra = era.getBeginning();
            final Date endEra = era.getEnd();
            final long middle = (endEra.getTime() + beginEra.getTime()) / 2 ;
            return new Date(middle);
        } else {
            return null;
        }
    }

    /**
     * @param duration to evaluate
     * @return the nearest Unit of a Duration.
     */
    public static Unit getUnitFromDuration(Duration duration) {
        if (duration == null) {
            return null;
        }

        final DefaultDuration dduration;
        if(duration instanceof DefaultDuration){
            dduration = (DefaultDuration) duration;
        }else{
            throw new IllegalArgumentException("Can not evaluate best unit for Duration which is not a DefaultDuration.");
        }

        final long mills = dduration.getTimeInMillis();
        long temp = mills / YEAR_MS;
        if (temp > 0) {
            return YEAR_UNIT;
        }
        temp = mills / MONTH_MS;
        if (temp > 0) {
            return MONTH_UNIT;
        }
        temp = mills / WEEK_MS;
        if (temp > 0) {
            return NonSI.WEEK;
        }
        temp = mills / DAY_MS;
        if (temp > 0) {
            return NonSI.DAY;
        }
        temp = mills / HOUR_MS;
        if (temp > 0) {
            return NonSI.HOUR;
        }
        temp = mills / MINUTE_MS;
        if (temp > 0) {
            return NonSI.MINUTE;
        }
        temp = mills / SECOND_MS;
        if (temp > 0) {
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
            return new Date();
        }
        if (date.isEmpty() || date.contains("BC")) {
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
            if (getOccurence(date, '/') == 2) {
                day = date.substring(0, date.indexOf('/'));
                date = date.substring(date.indexOf('/') + 1);
                month = date.substring(0, date.indexOf('/'));
                year = date.substring(date.indexOf('/') + 1);

                tmp = java.sql.Date.valueOf(year + "-" + month + "-" + day);
            } else {
                if (getOccurence(date, '/') == 1) {
                    month = date.substring(0, date.indexOf('/'));
                    year = date.substring(date.indexOf('/') + 1);
                    tmp = java.sql.Date.valueOf(year + "-" + month + "-" + "01");
                }
            }
        } else if (getOccurence(date, ' ') == 2) {
            if (!date.contains("?")) {

                day = date.substring(0, date.indexOf(' '));
                date = date.substring(date.indexOf(' ') + 1);
                month = pool.get(date.substring(0, date.indexOf(' ')));
                year = date.substring(date.indexOf(' ') + 1);

                tmp = java.sql.Date.valueOf(year + "-" + month + "-" + day);
            } else {
                tmp = java.sql.Date.valueOf("2000" + "-" + "01" + "-" + "01");
            }
        } else if (getOccurence(date, ' ') == 1 && getOccurence(date, '-') < 3) {
            try {
                final java.util.Date d = df.parse(date);
                return new Date(d.getTime());
            } catch (ParseException ex) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date +" with dateFormat : " + df);
            }
            month = poolCase.get(date.substring(0, date.indexOf(' ')));
            year = date.substring(date.indexOf(' ') + 1);
            tmp = java.sql.Date.valueOf(year + "-" + month + "-" + "01");


        } else if (getOccurence(date, '-') == 1) {

            month = date.substring(0, date.indexOf('-'));
            year = date.substring(date.indexOf('-') + 1);

            tmp = java.sql.Date.valueOf(year + "-" + month + "-" + "01");

        } else if (getOccurence(date, '-') == 2) {
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
            if (getOccurence(date, '-') == 0) {
                year = date;
                tmp = java.sql.Date.valueOf(year + "-" + "01" + "-" + "01");
            }
        }

        return tmp;
    }

    /**
     * This method returns a number of occurences occ in the string s.
     * @param s : String to search in
     * @param occ : Occurence to search
     * @return number of occurence
     */
    public static int getOccurence(String s, char occ) {
        int cnt = 0;
        int pos = s.indexOf(occ);
        for(; pos >= 0; pos = s.indexOf(occ, pos+1)){
            cnt++;
        }
        return cnt;
    }

    /**
     * This method returns a number of occurences occ in the string s.
     * @param s : String to search in
     * @param occ : Occurence to search
     * @return number of occurence
     */
    public static int getOccurence(String s, String occ) {
        int cnt = 0;
        int pos = s.indexOf(occ);
        for(; pos >= 0; pos = s.indexOf(occ, pos+1)){
            cnt++;
        }
        return cnt;
    }

}

