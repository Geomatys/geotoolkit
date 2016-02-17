/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gml.v321;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.geotoolkit.gml.xml.v321.TimePositionType;

//Junit dependencies
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TimePositionTypeTest extends org.geotoolkit.test.TestBase {

    private static final Date date = new Date( (System.currentTimeMillis()/ 1000) * 1000); // remove ms

    private static final SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat f3 = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeClass
    public static void setUpClass() throws Exception {

    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void dateParsingTest() throws Exception {

        final String s1 = f1.format(date);

        TimePositionType tp = new TimePositionType(s1);
        assertEquals(date, tp.getDate());

        final String s2 = f2.format(date);
        tp = new TimePositionType(s2);
        assertEquals(date, tp.getDate());



        final String s3 = f3.format(date);
        tp = new TimePositionType(s3);

        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final Date dateNoTime = cal.getTime();

        assertEquals(dateNoTime, tp.getDate());
    }

    @Test
    public void setValueTest() throws Exception {
        String s = null;
        TimePositionType tp = new TimePositionType(s);

        final Date d = f3.parse("2010-01-01");
        tp.setValue(d);
        assertEquals(tp.getValues(), Arrays.asList("2010-01-01"));

        final Date d2 = f2.parse("2010-01-01 01:01:02");
        tp.setValue(d2);
        assertEquals(tp.getValues(), Arrays.asList("2010-01-01T01:01:02.00"));
    }
}
