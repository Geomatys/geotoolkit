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
import java.util.TimeZone;

import org.geotoolkit.util.XInteger;

/**
 * Fast parser for date that match the pattern : 
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FastDateParser {

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT+0");
    private final Calendar calendar = Calendar.getInstance();

    public FastDateParser() {
        calendar.setTimeZone(TIME_ZONE);
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

        final int mil;
        final int sec;
        index2 = date.indexOf('.',index1);
        if(index2 > 0){
            //we have milliseconds
            sec = XInteger.parseIntUnsigned(date, index1, index2);
            index2++;

            index1 = date.indexOf('Z', index2);
            mil = XInteger.parseIntUnsigned(date, index2, index1);
        }else{
            index2 = date.indexOf('Z', index1);
            sec = XInteger.parseIntUnsigned(date, index1, index2);
            mil = 0;
        }

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, mil);
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
