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

import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import org.geotoolkit.util.collection.UnSynchronizedCache;

/**
 * ISO-8601 parser for date that match the patterns :
 * <ul>
 * <li>yyyy</li>
 * <li>yyyyZ</li>
 * <li>yyyy'Z'</li>
 * <li>yyyy-MM</li>
 * <li>yyyy-MMZ</li>
 * <li>yyyy-MM'Z'</li>
 * <li>yyyy-MM-dd</li>
 * <li>yyyy-MM-ddZ</li>
 * <li>yyyy-MM-dd'Z'</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss</li>
 * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSS</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
 * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
 * <li>yyyyMM</li>
 * <li>yyyyMMZ</li>
 * <li>yyyyMM'Z'</li>
 * <li>yyyyMMdd</li>
 * <li>yyyyMMddZ</li>
 * <li>yyyyMMdd'Z'</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @author Olivier Terral (Geomatys)
 */
public class ISODateParser {

    private static final TimeZone GMT0 = TimeZone.getTimeZone("GMT+0");
    private final Calendar calendar = Calendar.getInstance();
    private static final int DECIMAL = 10;

    /**
     * Tracks how much precision was actually present in the last parsed
     * String, so we can later choose the narrowest matching java.time type.
     */
    private static enum Precision {
        YEAR, MONTH, DAY, SECOND, MILLISECOND
    }

    //parsed values
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int sec;
    private int mil;
    private TimeZone timeZone;
    private Precision precision;

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

    public ISODateParser() {
    }

    private void parse(final String date) {
        //reset values
        year = 0;
        month = 1;
        day = 1;
        hour = 0;
        min = 0;
        sec = 0;
        mil = 0;
        timeZone = null;
        precision = Precision.YEAR;

        //start and end index of the current block.
        final TimeZone[] bufferTZ = new TimeZone[1];
        int index1, index2;

        //skip the first character in case we have a negative year
        if((index1 = date.indexOf('-',1)) < 0){

            //start at 1, avoid potential -yyyy
            index1 = searchTimeZone(date, 1, bufferTZ);

            if (index1 <= 5) {
                //date is like :
                // -yyyy
                // yyyy
                // yyyyZ
                // yyyy'Z'
                year = Integer.parseInt(date, 0, index1, DECIMAL);
                precision = Precision.YEAR;

            } else {
                //date is like :
                // -yyyyMM
                // yyyyMM
                // yyyyMMZ
                // yyyyMM'Z'
                int idxNegative = 0;
                if (date.indexOf('-') == 0)
                    idxNegative = 1;

                year = Integer.parseInt(date, 0, idxNegative + 4, DECIMAL);

                if (index1 <= 7) {
                    month = Integer.parseInt(date, idxNegative + 4, index1, DECIMAL);
                    precision = Precision.MONTH;

                } else {
                    //date is like :
                    // yyyyMMdd
                    // yyyyMMddZ
                    // yyyyMMdd'Z'
                    month = Integer.parseInt(date, idxNegative + 4, idxNegative + 6, DECIMAL);

                    if (index1 <= 9) {
                        day = Integer.parseInt(date, idxNegative + 6, index1, DECIMAL);

                    } else {
                        //date is like :
                        // yyyyMMdd
                        // yyyyMMddZ
                        // yyyyMMdd'Z'
                        day = Integer.parseInt(date, idxNegative + 6, idxNegative + 8, DECIMAL);

                        //@TODO Manage hour,min,sec,mill cases
                    }
                    precision = Precision.DAY;
                }
            }

        }else{
            year = Integer.parseInt(date, 0, index1, DECIMAL);
            index1++;

            if((index2 = date.indexOf('-', index1)) < 0){
                //date is like :
                // yyyy-MM
                // yyyy-MMZ
                // yyyy-MM'Z'

                index2 = searchTimeZone(date, index1, bufferTZ);

                month = Integer.parseUnsignedInt(date, index1, index2, DECIMAL);
                precision = Precision.MONTH;

            }else{
                month = Integer.parseUnsignedInt(date, index1, index2, DECIMAL);
                index2++;

                if((index1 = date.indexOf('T', index2)) < 0){
                    //date is like :
                    // yyyy-MM-dd
                    // yyyy-MM-dd'Z'
                    // yyyy-MM-ddZ

                    index1 = searchTimeZone(date, index2, bufferTZ);

                    day = Integer.parseUnsignedInt(date, index2, index1, DECIMAL);
                    precision = Precision.DAY;
                }else{
                    day = Integer.parseUnsignedInt(date, index2, index1, DECIMAL);
                    index1++;

                    index2 = date.indexOf(':', index1);
                    hour = Integer.parseUnsignedInt(date, index1, index2, DECIMAL);
                    index2++;

                    index1 = date.indexOf(':', index2);
                    min = Integer.parseUnsignedInt(date, index2, index1, DECIMAL);
                    index1++;

                    index2 = date.indexOf('.',index1);
                    if(index2 > 0){
                        //we have milliseconds
                        sec = Integer.parseUnsignedInt(date, index1, index2, DECIMAL);
                        index2++;

                        index1 = searchTimeZone(date, index2, bufferTZ);

                        mil = Integer.parseUnsignedInt(date, index2, index1, DECIMAL);
                        precision = Precision.MILLISECOND;
                    }else{
                        index2 = searchTimeZone(date, index1, bufferTZ);
                        sec = Integer.parseUnsignedInt(date, index1, index2, DECIMAL);
                        precision = Precision.SECOND;
                    }
                }
            }
        }

        timeZone = bufferTZ[0];
    }

    /**
     * Set the
     * First object is the time zone, second is start index of the
     * time zone or the end of the string.
     */
    private int searchTimeZone(final String date, final int index2, final TimeZone[] buffer){
        int index1;

        if((index1 = date.indexOf('Z', index2)) > 0){ //search a Z, GMT+0
            buffer[0] = GMT0;
            return index1;
        }else if((index1 = date.indexOf('+', index2)) > 0){ //search a +, GMT+XXXX
            buffer[0] = TIME_ZONES.get("GMT"+date.substring(index1, date.length()));
            return index1;
        }else if((index1 = date.indexOf('-', index2)) > 0){ //search a -, GMT-XXXX
            buffer[0] = TIME_ZONES.get("GMT"+date.substring(index1, date.length()));
            return index1;
        }else{
            //no Z == local time zone
            buffer[0] = null;
            return date.length();
        }
    }

    /**
     *
     * @param str : String to parse
     * @return the calendar used by this parser : this calendar
     * will be reused if another parser call is done.
     */
    public Calendar getCalendar(final String str) {
        parse(str);

        if (year > 0){
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
            calendar.set(Calendar.YEAR, year);
        } else {
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
            calendar.set(Calendar.YEAR, -year);
        }
        calendar.setTimeZone(timeZone == null ? TimeZone.getDefault() : timeZone);

        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, mil);
        return calendar;
    }

    public Date parseToDate(final String str) {
        return getCalendar(str).getTime();
    }

    public long parseToMillis(final String str) {
        return getCalendar(str).getTimeInMillis();
    }

    /**
     * Parses the given ISO-8601 String and returns it as the narrowest
     * (most appropriate) {@code java.time} type able to represent the
     * precision actually present in the String :
     * <ul>
     * <li>{@code yyyy} &rarr; {@link java.time.Year}</li>
     * <li>{@code yyyy-MM} &rarr; {@link java.time.YearMonth}</li>
     * <li>{@code yyyy-MM-dd} &rarr; {@link java.time.LocalDate}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss[.SSS]} (no zone) &rarr; {@link java.time.LocalDateTime}</li>
     * <li>{@code yyyy-MM-dd'T'HH:mm:ss[.SSS]Z} (explicit zone) &rarr; {@link java.time.OffsetDateTime}</li>
     * </ul>
     *
     * @param str : String to parse
     * @return the most appropriate {@link java.time.temporal.Temporal}
     * representing the parsed String.
     */
    public Temporal parseToTemporal(final String str) {
        parse(str);

        switch (precision) {
            case YEAR:
                return java.time.Year.of(year);
            case MONTH:
                return java.time.YearMonth.of(year, month);
            case DAY:
                return java.time.LocalDate.of(year, month, day);
            default: {
                //SECOND or MILLISECOND precision
                final java.time.LocalDateTime ldt = java.time.LocalDateTime.of(
                        year, month, day,
                        hour,
                        min,
                        sec,
                        mil * 1_000_000);

                if (timeZone != null) {
                    return ldt.atZone(timeZone.toZoneId()).toOffsetDateTime();
                } else {
                    return ldt;
                }
            }
        }
    }

}
