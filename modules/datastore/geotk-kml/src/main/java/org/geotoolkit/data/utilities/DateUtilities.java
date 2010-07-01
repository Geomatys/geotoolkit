/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.data.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import org.geotoolkit.util.XInteger;
import org.geotoolkit.util.collection.UnSynchronizedCache;

/**
 * Fast parser for date that match the pattern :
 * yyyy
 * yyyyZ
 * yyyy'Z'
 * yyyy-MM
 * yyyy-MMZ
 * yyyy-MM'Z'
 * yyyy-MM-dd
 * yyyy-MM-ddZ
 * yyyy-MM-dd'Z'
 * yyyy-MM-dd'T'HH:mm:ss
 * yyyy-MM-dd'T'HH:mm:ssZ
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 * yyyy-MM-dd'T'HH:mm:ss.SSS
 * yyyy-MM-dd'T'HH:mm:ss.SSSZ
 * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DateUtilities {

    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");
    private final Calendar calendar = Calendar.getInstance();

    private final Map<String,TimeZone> TIME_ZONES = new UnSynchronizedCache<String, TimeZone>(10){
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

    public DateUtilities() {
        calendar.setTimeZone(GMT0);
        calendar.set(0, 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void parse(String date) {
        int startOfMonth, startOfDay, startOfMinute, startOfSecond, startOfMilli;
        int startOfTime, startOfZone;
        final TimeZone tz;
        final int year;
        final int month;
        final int day;
        final int hour;
        final int min;
        final int sec;
        final int mil;

        /*
         * Position where time start
         */
        startOfTime = date.indexOf('T');
        
        /*
         * Position where time zone start
         */
        if(startOfTime < 0){
            // Searching a Z letter
            startOfZone = date.indexOf('Z');
            // Otherise, searching a ':' character
            if(startOfZone < 0){
                startOfZone = date.indexOf(':')-3;
                // Checking of startOfZone position
                if (startOfZone > 0
                        && (date.charAt(startOfZone) != '-'
                        || date.charAt(startOfZone) != '+'))
                    throw new IllegalArgumentException("Unsupported date format.");
            }
        } else{
            startOfZone = date.indexOf('Z', startOfTime);
            if(startOfZone < 0)
                startOfZone = date.indexOf('-', startOfTime);
            if(startOfZone < 0)
                startOfZone = date.indexOf('+', startOfTime);
        }

        /*
         * Is the date only a year (gYear)?
         */
        if((startOfMonth = date.indexOf('-',1)) < 0){
            if (startOfTime >= 0){
                year = XInteger.parseIntSigned(date, 0, startOfTime);
            }else if (startOfZone >= 0){
                year = XInteger.parseIntSigned(date, 0, startOfZone);
            }else{
                year = XInteger.parseIntSigned(date, 0, date.length());
            }
            month = 0;
            day = 1;
        } else {
            year = XInteger.parseIntSigned(date, 0, startOfMonth);
            startOfMonth++;

            /*
             * Is the date only a year with a month (gYearMonth)?
             */
            if((startOfDay = date.indexOf('-', startOfMonth)) < 0) {
                if (startOfTime >= 0)
                    month = XInteger.parseIntUnsigned(date, startOfMonth, startOfTime) - 1;
                else if (startOfZone >= 0)
                    month = XInteger.parseIntUnsigned(date, startOfMonth, startOfZone) - 1;
                else
                    month = XInteger.parseIntUnsigned(date, startOfMonth, date.length()) - 1;
                day = 1;
            } else {
                month = XInteger.parseIntUnsigned(date, startOfMonth, startOfDay) - 1;
                startOfDay++;
                if (startOfTime >= 0)
                    day = XInteger.parseIntUnsigned(date, startOfDay, startOfTime);
                else if (startOfZone >= 0)
                    day = XInteger.parseIntUnsigned(date, startOfDay, startOfZone);
                else
                    day = XInteger.parseIntUnsigned(date, startOfDay, date.length());
            }
        }

        /*
         * Time
         */
        if (startOfTime >= 0){
            startOfTime++;
            startOfMinute = date.indexOf(':', startOfTime);
            hour = XInteger.parseIntUnsigned(date, startOfTime, startOfMinute);
            startOfMinute++;
            startOfSecond = date.indexOf(':', startOfMinute);
            min = XInteger.parseIntUnsigned(date, startOfMinute, startOfSecond);
            startOfSecond++;
            startOfMilli = date.indexOf('.', startOfSecond);
            if (startOfMilli >= 0){
                sec = XInteger.parseIntUnsigned(date, startOfSecond, startOfMilli);
                startOfMilli++;
                if (startOfZone >= 0)
                    mil = XInteger.parseIntUnsigned(date, startOfMilli, startOfZone);
                else
                    mil = 0;
            } else if (startOfZone >= 0){
                sec = XInteger.parseIntUnsigned(date, startOfSecond, startOfZone);
                mil = 0;
            } else {
                sec = XInteger.parseIntUnsigned(date, startOfSecond, date.length());
                mil = 0;
            }
        } else {
            hour = 0;
            min = 0;
            sec = 0;
            mil = 0;
        }

        /*
         * Zone
         */
        if (startOfZone >= 0){
            if (date.charAt(startOfZone) == 'Z')
                tz = GMT0;
            else
                tz = TIME_ZONES.get("GMT"+date.substring(startOfZone, date.length()));
        } else
            tz = TimeZone.getDefault();
            //tz = GMT0;
        
        calendar.setTimeZone(tz);
        if(year > 0){
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
        } else {
            calendar.set(Calendar.YEAR, -year);
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
        }
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, mil);
    }

    public static String getFormatedString(Calendar calendar, boolean forceDateTime){
        String result = "";
        
        int milli = calendar.get(Calendar.MILLISECOND);
        int seconds = calendar.get(Calendar.SECOND);
        int minutes = calendar.get(Calendar.MINUTE);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year;
        if(calendar.get(Calendar.ERA) == GregorianCalendar.BC){
            year = -calendar.get(Calendar.YEAR);
        } else {
            year = calendar.get(Calendar.YEAR);
        }

        boolean isOffset = (calendar.getTimeZone() != null);
        boolean isTime = !(hours == 0 && minutes == 0 && seconds == 0 && milli == 0);


        if(isOffset){
            int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
            int minutesOffset = zoneOffset / (60 * 1000);
            int hoursOffset = minutesOffset / 60;
            minutesOffset = (minutesOffset % 60)*60;
            String zhh = null, zmm = null, zs= null;

            if(minutesOffset < 10)
                zmm = "0"+minutesOffset;
            else
                zmm = Integer.toString(minutesOffset);

            if(hoursOffset < 10)
                zhh = "0"+hoursOffset;
            else
                zhh = Integer.toString(hoursOffset);

            if(zoneOffset > 0)
                result = "+"+zhh+":"+zmm;
            else if (zoneOffset < 0)
                result = "-"+zhh+":"+zmm;
            else
                result = "Z";
        }

        if (milli != 0){
            result = "."+milli+result;
        }

        if (isTime || forceDateTime){
            if(seconds < 10)
                result = ":0"+seconds+result;
            else
                result = ":"+seconds+result;
            if(minutes < 10)
                result = ":0"+minutes+result;
            else
                result = ":"+minutes+result;
            if(hours < 10)
                result = "T0"+hours+result;
            else
                result = "T"+hours+result;
        }

        String date;
        if (day > 1 || forceDateTime){
            date = year+"-"+
                ((month < 10) ? ("0"+month) : month) +"-"+
                ((day < 10) ? ("0"+day) : day);}
        else if (month > 1)
            date = year+"-"+
                ((month < 10) ? ("0"+month) : month);
        else
            date = Integer.toString(year);
        return date+result;
    }

    public String getFormatedString(boolean forceDateTime){
        return getFormatedString(calendar, forceDateTime);
    }

    /**
     *
     * @param str : String to parse
     * @return the calendar used by this parser : this calendar
     * will be reused if another parser call is done.
     */
    public Calendar getCalendar(String str) {
        parse(str);
        return calendar;
    }

    public Date parseToDate(String str) {
        parse(str);
        return calendar.getTime();
    }

    public long parseToMillis(String str) {
        parse(str);
        return calendar.getTimeInMillis();
    }

}
