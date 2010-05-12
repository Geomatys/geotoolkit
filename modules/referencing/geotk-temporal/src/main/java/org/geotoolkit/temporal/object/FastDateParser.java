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
package org.geotoolkit.temporal.object;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.geotoolkit.util.XInteger;
import org.geotoolkit.util.collection.UnSynchronizedCache;

/**
 * Fast parser for date that match the pattern : 
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
 * yyyy-MM-dd'T'HH:mm:ssZ
 * yyyy-MM-dd'T'HH:mm:ss.SSSZ
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FastDateParser {

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

    public FastDateParser() {
        calendar.setTimeZone(GMT0);
        calendar.set(0, 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void parse(String date) {
        int index1, index2;

        index1 = date.indexOf('-');
        final int year = XInteger.parseIntUnsigned(date, 0, index1);
        index1++;

        index2 = date.indexOf('-', index1);
        final int month = XInteger.parseIntUnsigned(date, index1, index2);
        index2++;

        index1 = date.indexOf('T', index2);
        final int day = XInteger.parseIntUnsigned(date, index2, index1);
        index1++;

        index2 = date.indexOf(':', index1);
        final int hour = XInteger.parseIntUnsigned(date, index1, index2);
        index2++;

        index1 = date.indexOf(':', index2);
        final int min = XInteger.parseIntUnsigned(date, index2, index1);
        index1++;

        final TimeZone tz;
        final int mil;
        final int sec;
        index2 = date.indexOf('.',index1);
        if(index2 > 0){
            //we have milliseconds
            sec = XInteger.parseIntUnsigned(date, index1, index2);
            index2++;

            if((index1 = date.indexOf('Z', index2)) > 0){ //search a Z, GMT+0
                tz = GMT0;
            }else if((index1 = date.indexOf('+', index2)) > 0){ //search a +, GMT+XXXX
                tz = TIME_ZONES.get("GMT"+date.substring(index1, date.length()));
            }else if((index1 = date.indexOf('-', index2)) > 0){ //search a -, GMT-XXXX
                tz = TIME_ZONES.get("GMT"+date.substring(index1, date.length()));
            }else{
                //no Z == local time zone
                tz = TimeZone.getDefault();
                index1 = date.length();
            }
            mil = XInteger.parseIntUnsigned(date, index2, index1);
        }else{

            if((index2 = date.indexOf('Z', index1)) > 0){ //search a Z, GMT+0
                tz = GMT0;
            }else if((index2 = date.indexOf('+', index1)) > 0){ //search a +, GMT+XXXX
                tz = TIME_ZONES.get("GMT"+date.substring(index2, date.length()));
            }else if((index2 = date.indexOf('-', index1)) > 0){ //search a -, GMT-XXXX
                tz = TIME_ZONES.get("GMT"+date.substring(index2, date.length()));
            }else{
                //no Z == local time zone
                tz = TimeZone.getDefault();
                index2 = date.length();
            }
            
            sec = XInteger.parseIntUnsigned(date, index1, index2);
            mil = 0;
        }

        calendar.setTimeZone(tz);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, mil);
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
