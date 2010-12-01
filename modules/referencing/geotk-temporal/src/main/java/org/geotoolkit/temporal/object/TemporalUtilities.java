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

import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.geotoolkit.temporal.reference.DefaultTemporalCoordinateSystem;
import org.geotoolkit.util.SimpleInternationalString;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.collection.UnSynchronizedCache;
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
public final class TemporalUtilities {

    private static final Logger LOGGER = Logging.getLogger(TemporalUtilities.class);
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

    /**
     * Hack for french datas, must find another way to do so.
     * handle all local ? impossible
     * handle the current local ? 
     * If the server has a different local then the client ? won't work
     */
    private static final List<String> FR_POOL = new ArrayList<String>(){
        @Override
        public int indexOf(Object o) {
            final String candidate = (String) o;
            for(int i=0,n=FR_POOL.size(); i<n;i++){
                if(FR_POOL.get(i).equalsIgnoreCase(candidate)){
                    return i;
                }
            }
            return -1;
        }
    };

    /**
     * Caution : those objects are not thread safe, take care to synchronize when you use them.
     */
    private static final SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd");
    static {
        //we don't hour here so we put the timeZone to GMT+0
        sdf2.setTimeZone(TimeZone.getTimeZone("GMT+0"));
    }
    private static final SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static final SimpleDateFormat sdf4 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final Map<String,TimeZone> TIME_ZONES = new UnSynchronizedCache<String, TimeZone>(50){
        @Override
        public TimeZone get(Object o) {
            @SuppressWarnings("element-type-mismatch")
            TimeZone tz = super.get(o);
            if(tz == null){
                tz = TimeZone.getTimeZone((String)o);
                put((String)o, tz);
            }
            return tz;
        }
    };


    static{
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

    private TemporalUtilities(){}

    /**
     * Returns a Date object from an ISO-8601 representation string. (String defined with pattern yyyy-MM-dd'T'HH:mm:ss.SSSZ or yyyy-MM-dd).
     * @param dateString
     * @return Date result of parsing the given string
     * @throws ParseException
     */
    public static Date getDateFromString(String dateString) throws ParseException {

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
                    //e.g : 1985-04-12T10:15:30+04:00
                    timezoneStr = timezoneStr.replace(":", "");
                    dateString = dateString.substring(0, tzIndex + 1).concat(timezoneStr);
                } else if (timezoneStr.length() == 2) {
                    //e.g : 1985-04-12T10:15:30-04
                    dateString = dateString.concat("00");
                }
            } else if (dateString.charAt(dateString.length()-1) == 'Z') {
                //e.g : 1985-04-12T10:15:30Z
                dateString = dateString.substring(0, dateString.length() - 1).concat("+0000");
            } else {
                //e.g : 1985-04-12T10:15:30
                defaultTimezone = true;
            }

            
            if (dateString.indexOf('.') > 0) {
                //simple date format is not thread safe
                synchronized (sdf3){
                    return sdf3.parse(dateString);
                }
            }

            if(defaultTimezone){
                //applying default timezone
                //simple date format is not thread safe
                synchronized(sdf4){
                    return sdf4.parse(dateString);
                }
            }else{
                final String timezone = getTimeZone(dateString);
                //simple date format is not thread safe
                synchronized(sdf1){
                    sdf1.setTimeZone(TIME_ZONES.get(timezone));
                    return sdf1.parse(dateString);
                }
            }

        }else if(dateString.indexOf('-') > 0) {
            //simple date format is not thread safe
            synchronized(sdf2){
                return sdf2.parse(dateString);
            }
        }

        return null;
    }

    public static String getTimeZone(final String dateString) {
        if (dateString.charAt(dateString.length()-1) == 'Z') {
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
     * Returns a DefaultPeriodDuration instance parsed from a string that respect ISO8601 format
     * ie: PnYnMnDTnHnMnS where n is an integer
     * @TODO maybe should check by Pattern of string before and should throw an exception when it is bad format
     *
     * @param periodDuration
     * @return duration in millisenconds represented by this string duration.
     */
    public static Duration getDurationFromString(String periodDuration) {

        if(periodDuration == null) {
            return null;
        }

        String nbYear = null, nbMonth = null, nbWeek = null, nbDay = null, nbHour = null, nbMin = null, nbSec = null;

        // remove first char 'P'
        periodDuration = periodDuration.substring(1);

        // looking for the period years
        if (periodDuration.indexOf('Y') != -1) {
            nbYear = periodDuration.substring(0, periodDuration.indexOf('Y'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('Y') + 1);
        }

        //looking for the period months
        if (periodDuration.indexOf('M') != -1
                && (periodDuration.indexOf('T') == -1 || periodDuration.indexOf('T') > periodDuration.indexOf('M'))) {
            nbMonth = periodDuration.substring(0, periodDuration.indexOf('M'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('M') + 1);
        }

        //looking for the period weeks
        if (periodDuration.indexOf('W') != -1) {
            nbWeek = periodDuration.substring(0, periodDuration.indexOf('W'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('W') + 1);
        }

        //looking for the period days
        if (periodDuration.indexOf('D') != -1) {
            nbDay = periodDuration.substring(0, periodDuration.indexOf('D'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('D') + 1);
        }

        //if the periodDuration is not over we pass to the hours by removing 'T'
        if (periodDuration.indexOf('T') != -1) {
            periodDuration = periodDuration.substring(1);
        }

        //looking for the period hours
        if (periodDuration.indexOf('H') != -1) {
            nbHour = periodDuration.substring(0, periodDuration.indexOf('H'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('H') + 1);
        }

        //looking for the period minutes
        if (periodDuration.indexOf('M') != -1) {
            nbMin = periodDuration.substring(0, periodDuration.indexOf('M'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('M') + 1);
        }

        //looking for the period seconds
        if (periodDuration.indexOf('S') != -1) {
            nbSec = periodDuration.substring(0, periodDuration.indexOf('S'));
            periodDuration = periodDuration.substring(periodDuration.indexOf('S') + 1);
        }

        if (periodDuration.length() != 0) {
            throw new IllegalArgumentException("The period descritpion is malformed, should not respect ISO8601 : " + periodDuration);
        }
        return new DefaultPeriodDuration(new SimpleInternationalString(nbYear),
                new SimpleInternationalString(nbMonth),
                new SimpleInternationalString(nbWeek),
                new SimpleInternationalString(nbDay),
                new SimpleInternationalString(nbHour),
                new SimpleInternationalString(nbMin),
                new SimpleInternationalString(nbSec));
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
     * Try to parse a date from different well knowed writing types.
     * 
     * @param date String to parse
     * @return resulting parsed Date.
     * @throws ParseException if String is not valid.
     * @throws NullPointerException if String is null.
     */
    public static Date parseDate(String date) throws ParseException, NullPointerException{

        if(date.endsWith("BC")){
            throw new ParseException("Date is marked as Before Christ, not possible to parse it", date.length());
        }

        final int[] slashOccurences = StringUtilities.getIndexes(date, '/');

        if(slashOccurences.length == 1){
            // date is like : 11/2050
            final int month = parseInt(date.substring(0, slashOccurences[0])) -1;
            final int year = parseInt(date.substring(slashOccurences[0]+1, date.length()));
            final Calendar cal = Calendar.getInstance();
            cal.set(year,month,1,0,0,0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();

        }else if(slashOccurences.length == 2){
            // date is like : 23/11/2050
            final int day = parseInt(date.substring(0, slashOccurences[0]));
            final int month = parseInt(date.substring(slashOccurences[0]+1, slashOccurences[1])) -1;
            final int year = parseInt(date.substring(slashOccurences[1]+1));
            final Calendar cal = Calendar.getInstance();
            cal.set(year,month,day,0,0,0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        final int[] spaceOccurences = StringUtilities.getIndexes(date, ' ');
        final int[] dashOccurences = StringUtilities.getIndexes(date, '-');

        if(spaceOccurences.length == 2){
            //date is like : 18 janvier 2050
            final int day = parseInt(date.substring(0, spaceOccurences[0]));
            final int month = FR_POOL.indexOf(date.substring(spaceOccurences[0]+1, spaceOccurences[1]));
            final int year = parseInt(date.substring(spaceOccurences[1]+1));
            final Calendar cal = Calendar.getInstance();
            cal.set(year,month,day,0,0,0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();

        }else if(spaceOccurences.length == 1 && dashOccurences.length < 3) {
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return df.parse(date);
            } catch (ParseException ex) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date +" with dateFormat : " + df);
            }

            //date is like : Janvier 2050
            final int month = FR_POOL.indexOf(date.substring(0,spaceOccurences[0]));
            final int year = parseInt(date.substring(spaceOccurences[0]+1));
            final Calendar cal = Calendar.getInstance();
            cal.set(year,month,1,0,0,0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();

        }else if(dashOccurences.length == 1) {
            if (dashOccurences[0] == 2) {
                //date is like : 05-2050
                final int month = parseInt(date.substring(0, dashOccurences[0])) - 1;
                final int year = parseInt(date.substring(dashOccurences[0] + 1));
                final Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            } else {
                //date is like : 2050-05
                final int year = parseInt(date.substring(0, dashOccurences[0]));
                final int month = parseInt(date.substring(dashOccurences[0] + 1)) -1;
                final Calendar cal = Calendar.getInstance();
                cal.set(year, month, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }

        }else if(dashOccurences.length >= 2) {
            //if date is in format yyyy-mm-ddTHH:mm:ss
            try {
                final java.util.Date resultDate = getDateFromString(date);
                if (resultDate != null) {
                    return resultDate;
                }
            } catch (ParseException e) {
                LOGGER.log(Level.FINE, "Could not parse date : " + date +" with getDateFromString method.");
            }

            if(dashOccurences[0] == 4) {
                //date is like 2010-11-23Z
                final int year = parseInt(date.substring(0,dashOccurences[0]));
                final int month = parseInt(date.substring(dashOccurences[0]+1, dashOccurences[1])) -1;

                final int day;
                if (date.endsWith("Z")) {
                    day = parseInt(date.substring(dashOccurences[1]+1,date.length()-1));
                }else{
                    day = parseInt(date.substring(dashOccurences[1]+1));
                }

                final Calendar cal = Calendar.getInstance();
                cal.set(year,month,day,0,0,0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }else{
                //date is like 23-11-2010
                final int day = parseInt(date.substring(0, dashOccurences[0]));
                final int month = parseInt(date.substring(dashOccurences[0]+1, dashOccurences[1])) -1;
                final int year = parseInt(date.substring(dashOccurences[1]+1));
                final Calendar cal = Calendar.getInstance();
                cal.set(year,month,day,0,0,0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }

        }else if(dashOccurences.length == 0) {
            //date is like 2010
            final int year = parseInt(date);
            final Calendar cal = Calendar.getInstance();
            cal.set(year,0,1,0,0,0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }

        throw new ParseException("Invalid date format : " + date, 0);
    }

    /**
     * @see TemporalUtilities#parseDate(java.lang.String)
     * 
     * @param date : string to parse.
     * @param neverNull : will return today's date if parsing fail, otherwise return null if
     * parsing fails.
     * @return result of the parsed string or today's date or null if neverNull is false.
     */
    public static Date parseDateSafe(String date, boolean neverNull){
        if(date != null){
            try {
                return parseDate(date);
            } catch (ParseException ex) {
                //do nothing
            }
        }
        return (neverNull) ? new Date() : null;
    }

    private static int parseInt(String candidate) throws ParseException{
        try{
            return Integer.parseInt(candidate);
        }catch(NumberFormatException ex){
            ParseException pex = new ParseException(ex.getLocalizedMessage(), 0);
            pex.initCause(ex);
            throw pex;
        }
    }

    /**
     * Return a time description on the form "Ny Nm Nd Nh Nmin Ns Nms" from a millisecond time.
     *
     * @param time A time value in millisecond
     * @return A string on the form "Xmin Ys Zms".
     */
    public static String durationToString(long time){

        if(time == 0){
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

        final StringBuilder sb = new StringBuilder();
        if(years > 0)   sb.append(years).append("y ");
        if(months > 0)  sb.append(months).append("m ");
        if(days > 0)    sb.append(days).append("d ");
        if(hours > 0)   sb.append(hours).append("h ");
        if(minuts > 0)  sb.append(minuts).append("min ");
        if(seconds > 0) sb.append(seconds).append("s ");
        if(millis > 0)  sb.append(millis).append("ms");

        final int size = sb.length();        
        if (sb.charAt(size - 1) == ' ') {
            return sb.substring(0, size-1);
        }else{
            return sb.toString();
        }
    }

}

