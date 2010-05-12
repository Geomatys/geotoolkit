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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static java.util.Calendar.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FastDateParserTest {

    public FastDateParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testConformance() throws ParseException {
        final FastDateParser fdp = new FastDateParser();

        String str;
        Calendar calendar = Calendar.getInstance();
        int year = 1995;
        int month = 10; //starts at 0
        int day = 23;
        int hour = 16;
        int min = 41;
        int sec = 36;
        int mil = 512;

        //test simple date -----------------------------------------------------

        str = "1995-11-23";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getDefault(), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(0,  calendar.get(HOUR_OF_DAY));
        assertEquals(0,   calendar.get(MINUTE));
        assertEquals(0,   calendar.get(SECOND));
        assertEquals(0,     calendar.get(MILLISECOND));

        //test no 'Z' for local GMT --------------------------------------------

        str = "1995-11-23T16:41:36";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getDefault(), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(hour,  calendar.get(HOUR_OF_DAY));
        assertEquals(min,   calendar.get(MINUTE));
        assertEquals(sec,   calendar.get(SECOND));
        assertEquals(0,     calendar.get(MILLISECOND));

        str = "1995-11-23T16:41:36.512";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getDefault(), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(hour,  calendar.get(HOUR_OF_DAY));
        assertEquals(min,   calendar.get(MINUTE));
        assertEquals(sec,   calendar.get(SECOND));
        assertEquals(mil,   calendar.get(MILLISECOND));

        //test 'Z' for GMT+0 ---------------------------------------------------

        str = "1995-11-23T16:41:36Z";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getTimeZone("GMT+0"), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(hour,  calendar.get(HOUR_OF_DAY));
        assertEquals(min,   calendar.get(MINUTE));
        assertEquals(sec,   calendar.get(SECOND));
        assertEquals(0,     calendar.get(MILLISECOND));

        str = "1995-11-23T16:41:36.512Z";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getTimeZone("GMT+0"), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(hour,  calendar.get(HOUR_OF_DAY));
        assertEquals(min,   calendar.get(MINUTE));
        assertEquals(sec,   calendar.get(SECOND));
        assertEquals(mil,   calendar.get(MILLISECOND));

        //test Z for GMT+2 -----------------------------------------------------

        str = "1995-11-23T16:41:36+2";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getTimeZone("GMT+2"), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(hour,  calendar.get(HOUR_OF_DAY));
        assertEquals(min,   calendar.get(MINUTE));
        assertEquals(sec,   calendar.get(SECOND));
        assertEquals(0,     calendar.get(MILLISECOND));

        str = "1995-11-23T16:41:36.512+2";

        calendar = fdp.getCalendar(str);
        assertEquals(TimeZone.getTimeZone("GMT+2"), calendar.getTimeZone());
        assertEquals(year,  calendar.get(YEAR));
        assertEquals(month, calendar.get(MONTH));
        assertEquals(day,   calendar.get(DAY_OF_MONTH));
        assertEquals(hour,  calendar.get(HOUR_OF_DAY));
        assertEquals(min,   calendar.get(MINUTE));
        assertEquals(sec,   calendar.get(SECOND));
        assertEquals(mil,   calendar.get(MILLISECOND));


    }


    @Test
    public void testSpeed() throws ParseException {
        long before, after;
        String str;
        final Calendar date = Calendar.getInstance();
        int year = 1995;
        int month = 10; //starts at 0
        int day = 23;
        int hour = 16;
        int min = 41;
        int sec = 36;
        int mil = 512;
        final int nb = 10000;

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        final FastDateParser fdp = new FastDateParser();

        //complete date --------------------------------------------------------

        str = "1995-11-23T16:41:36Z";
        date.setTime(sdf.parse(str));
        date.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        assertEquals(year, date.get(YEAR));
        assertEquals(month, date.get(MONTH));
        assertEquals(day, date.get(DAY_OF_MONTH));
        assertEquals(hour, date.get(HOUR_OF_DAY));
        assertEquals(min, date.get(MINUTE));
        assertEquals(sec, date.get(SECOND));
        assertEquals(0, date.get(MILLISECOND));

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            sdf.parse(str);
        }
        after = System.currentTimeMillis();
        final long sdfComplete = (after-before);
        System.out.println("Simple date format perform in = " + sdfComplete +" ms");

        date.setTime(fdp.parseToDate(str));
        date.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        assertEquals(year, date.get(YEAR));
        assertEquals(month, date.get(MONTH));
        assertEquals(day, date.get(DAY_OF_MONTH));
        assertEquals(hour, date.get(HOUR_OF_DAY));
        assertEquals(min, date.get(MINUTE));
        assertEquals(sec, date.get(SECOND));
        assertEquals(0, date.get(MILLISECOND));

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            fdp.parseToDate(str);
        }
        after = System.currentTimeMillis();
        final long fdpComplete = (after-before);
        System.out.println("Fast date parser perform in = " + fdpComplete +" ms");

        assertTrue(fdpComplete < sdfComplete);

        // not all char field --------------------------------------------------

        str = "5-1-3T6:1:6Z";
        date.setTime(sdf.parse(str));
        date.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        assertEquals(5, date.get(YEAR));
        assertEquals(0, date.get(MONTH));
        assertEquals(3, date.get(DAY_OF_MONTH));
        assertEquals(6, date.get(HOUR_OF_DAY));
        assertEquals(1, date.get(MINUTE));
        assertEquals(6, date.get(SECOND));
        assertEquals(0, date.get(MILLISECOND));

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            sdf.parse(str);
        }
        after = System.currentTimeMillis();
        final long sdfPart = (after-before);
        System.out.println("Simple date format perform in = " + sdfPart +" ms");

        date.setTime(fdp.parseToDate(str));
        date.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        assertEquals(5, date.get(YEAR));
        assertEquals(0, date.get(MONTH));
        assertEquals(3, date.get(DAY_OF_MONTH));
        assertEquals(6, date.get(HOUR_OF_DAY));
        assertEquals(1, date.get(MINUTE));
        assertEquals(6, date.get(SECOND));
        assertEquals(0, date.get(MILLISECOND));

        before = System.currentTimeMillis();
        for(int i=0; i<nb; i++){
            fdp.parseToDate(str);
        }
        after = System.currentTimeMillis();
        final long fdpPart = (after-before);
        System.out.println("Fast date parser perform in = " + fdpPart +" ms");

        assertTrue(fdpPart < sdfPart);

    }

}
