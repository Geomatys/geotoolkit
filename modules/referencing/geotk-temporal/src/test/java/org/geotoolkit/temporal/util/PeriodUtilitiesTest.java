/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.temporal.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author legal
 */
public class PeriodUtilitiesTest extends org.geotoolkit.test.TestBase {

    public PeriodUtilitiesTest() {
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

    /**
     * Test of getDatesRespresentation method, of class PeriodUtilities.
     */
    @Test
    public void getDatesRespresentation() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        PeriodUtilities instance = new PeriodUtilities(df);
        SortedSet<Date> dates;


        /**
         * Test 1: isolated Date
         */
        dates    = new TreeSet<>();
        dates.add(df.parse("2003-01-07T00:00:00Z"));

        String expResult = "2003-01-07T00:00:00Z";
        String result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);

        /**
         * Test 2: isolated Date
         */
        dates    = new TreeSet<>();
        dates.add(df.parse("2003-01-07T00:00:00Z"));
        dates.add(df.parse("2003-01-21T00:00:00Z"));

        expResult = "2003-01-07T00:00:00Z,2003-01-21T00:00:00Z";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);

        /**
         * Test 3: three isolated date
         */
        dates = new TreeSet<>();
        dates.add(df.parse("2007-06-06T14:00:00Z"));
        dates.add(df.parse("2007-06-13T14:00:00Z"));
        dates.add(df.parse("2007-06-20T14:00:00Z"));

        expResult = "2007-06-06T14:00:00Z/2007-06-20T14:00:00Z/P1W";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);

        /**
         * Test 4: one period
         */
        dates    = new TreeSet<>();
        dates.add(df.parse("2004-01-28T00:00:00Z"));
        dates.add(df.parse("2004-02-04T00:00:00Z"));
        dates.add(df.parse("2004-02-11T00:00:00Z"));
        dates.add(df.parse("2004-02-18T00:00:00Z"));
        dates.add(df.parse("2004-02-25T00:00:00Z"));
        dates.add(df.parse("2004-03-03T00:00:00Z"));
        dates.add(df.parse("2004-03-10T00:00:00Z"));
        dates.add(df.parse("2004-03-17T00:00:00Z"));

        expResult = "2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);

        /**
         * Test 5: two period not joined
         */
        dates    = new TreeSet<>();

        //first period
        dates.add(df.parse("2004-01-28T00:00:00Z"));
        dates.add(df.parse("2004-02-04T00:00:00Z"));
        dates.add(df.parse("2004-02-11T00:00:00Z"));
        dates.add(df.parse("2004-02-18T00:00:00Z"));
        dates.add(df.parse("2004-02-25T00:00:00Z"));
        dates.add(df.parse("2004-03-03T00:00:00Z"));
        dates.add(df.parse("2004-03-10T00:00:00Z"));
        dates.add(df.parse("2004-03-17T00:00:00Z"));

        //second period
        dates.add(df.parse("2005-11-09T00:00:00Z"));
        dates.add(df.parse("2005-11-16T00:00:00Z"));
        dates.add(df.parse("2005-11-23T00:00:00Z"));
        dates.add(df.parse("2005-11-30T00:00:00Z"));

        expResult = "2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W,2005-11-09T00:00:00Z/2005-11-30T00:00:00Z/P1W";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);


        /**
         * Test 6: two period joined
         */
        dates    = new TreeSet<>();

        //first period
        dates.add(df.parse("2004-01-28T00:00:00Z"));
        dates.add(df.parse("2004-02-04T00:00:00Z"));
        dates.add(df.parse("2004-02-11T00:00:00Z"));
        dates.add(df.parse("2004-02-18T00:00:00Z"));
        //second period
        dates.add(df.parse("2004-02-25T00:00:00Z"));
        dates.add(df.parse("2004-03-04T00:00:00Z"));
        dates.add(df.parse("2004-03-12T00:00:00Z"));
        dates.add(df.parse("2004-03-20T00:00:00Z"));




        expResult = "2004-01-28T00:00:00Z/2004-02-25T00:00:00Z/P1W,2004-02-25T00:00:00Z/2004-03-20T00:00:00Z/P1W1D";
        result = instance.getDatesRespresentation(dates);
        System.out.println("expected =" + expResult + '\n' +
                           "result   =" + result);
        assertEquals(expResult, result);

        /**
         * Test 7: isolated dates + period1 + isolated dates + period2 + isolated dates
         */
        dates    = new TreeSet<>();

        //isolated date
        dates.add(df.parse("2003-01-07T00:00:00Z"));

        //isolated date
        dates.add(df.parse("2004-01-07T00:00:00Z"));

        //first period
        dates.add(df.parse("2004-01-28T00:00:00Z"));
        dates.add(df.parse("2004-02-04T00:00:00Z"));
        dates.add(df.parse("2004-02-11T00:00:00Z"));
        dates.add(df.parse("2004-02-18T00:00:00Z"));
        dates.add(df.parse("2004-02-25T00:00:00Z"));
        dates.add(df.parse("2004-03-03T00:00:00Z"));
        dates.add(df.parse("2004-03-10T00:00:00Z"));
        dates.add(df.parse("2004-03-17T00:00:00Z"));

        //isolated date
        dates.add(df.parse("2005-03-02T00:00:00Z"));

        //isolated date
        dates.add(df.parse("2005-07-20T00:00:00Z"));

        //second period
        dates.add(df.parse("2005-11-09T00:00:00Z"));
        dates.add(df.parse("2005-11-16T00:00:00Z"));
        dates.add(df.parse("2005-11-23T00:00:00Z"));
        dates.add(df.parse("2005-11-30T00:00:00Z"));

        //isolated date
        dates.add(df.parse("2009-10-31T00:00:00Z"));

        //isolated date
        dates.add(df.parse("2010-10-31T00:00:00Z"));

        //isolated date
        dates.add(df.parse("2011-10-31T00:00:00Z"));

        expResult = "2003-01-07T00:00:00Z,2004-01-07T00:00:00Z,2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W,2005-03-02T00:00:00Z,2005-07-20T00:00:00Z,2005-11-09T00:00:00Z/2005-11-30T00:00:00Z/P1W,2009-10-31T00:00:00Z/2011-10-31T00:00:00Z/P12M";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);


    }

    /**
     * Test of getPeriodDescription method, of class PeriodUtilities.
     */
    @Test
    public void getPeriodDescription() throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        df2.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        PeriodUtilities instance = new PeriodUtilities(df);

        SortedSet<Date> dates;

        /**
         * Test 1: one hour period
         */
        dates    = new TreeSet<>();
        dates.add(df.parse("2004-01-28T00:00:00Z"));
        dates.add(df.parse("2004-01-28T01:00:00Z"));
        dates.add(df.parse("2004-01-28T02:00:00Z"));
        dates.add(df.parse("2004-01-28T03:00:00Z"));
        dates.add(df.parse("2004-01-28T04:00:00Z"));
        dates.add(df.parse("2004-01-28T05:00:00Z"));
        dates.add(df.parse("2004-01-28T06:00:00Z"));
        dates.add(df.parse("2004-01-28T07:00:00Z"));
        long gap =3600000L;

        String expResult = "2004-01-28T00:00:00Z/2004-01-28T07:00:00Z/PT1H";
        String result = instance.getPeriodDescription(dates, gap);
        assertEquals(expResult, result);

        /**
         * Test 2: one week period
         */
        dates    = new TreeSet<>();
        dates.add(df.parse("2004-01-28T00:00:00Z"));
        dates.add(df.parse("2004-02-04T00:00:00Z"));
        dates.add(df.parse("2004-02-11T00:00:00Z"));
        dates.add(df.parse("2004-02-18T00:00:00Z"));
        dates.add(df.parse("2004-02-25T00:00:00Z"));
        dates.add(df.parse("2004-03-03T00:00:00Z"));
        dates.add(df.parse("2004-03-10T00:00:00Z"));
        dates.add(df.parse("2004-03-17T00:00:00Z"));
        gap = 604800000L;

        expResult = "2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W";
        result = instance.getPeriodDescription(dates, gap);
        assertEquals(expResult, result);

        /**
         * Test 3: one week and one day period
         */
        dates    = new TreeSet<>();

        dates.add(df.parse("2004-02-25T00:00:00Z"));
        dates.add(df.parse("2004-03-04T00:00:00Z"));
        dates.add(df.parse("2004-03-12T00:00:00Z"));
        dates.add(df.parse("2004-03-20T00:00:00Z"));

        expResult = "2004-02-25T00:00:00Z/2004-03-20T00:00:00Z/P1W1D";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);
        
        /**
         * Test 4: one week and one day period one second
         */
        dates    = new TreeSet<>();

        dates.add(df.parse("2004-02-25T00:00:01Z"));
        dates.add(df.parse("2004-03-04T00:00:02Z"));
        dates.add(df.parse("2004-03-12T00:00:03Z"));
        dates.add(df.parse("2004-03-20T00:00:04Z"));

        expResult = "2004-02-25T00:00:01Z/2004-03-20T00:00:04Z/P1W1DT1S";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);
        
        /**
         * Test 5: one week and one day period one second 250 millisecond
         */
        instance = new PeriodUtilities(df2);
         
        dates    = new TreeSet<>();
        dates.add(df2.parse("2004-02-25T00:00:01.250Z"));
        dates.add(df2.parse("2004-03-04T00:00:02.500Z"));
        dates.add(df2.parse("2004-03-12T00:00:03.750Z"));
        dates.add(df2.parse("2004-03-20T00:00:05.000Z"));
        
        expResult = "2004-02-25T00:00:01.250Z/2004-03-20T00:00:05.000Z/P1W1DT1.250S";
        result = instance.getDatesRespresentation(dates);
        assertEquals(expResult, result);

    }

    /**
     * Test of getDatesFromPeriodDescription method, of class PeriodUtilities.
     */
    @Test
    public void getDatesFromPeriodDescription() throws Exception {


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        df2.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        PeriodUtilities instance = new PeriodUtilities(df);
        SortedSet<Date> expResult;

        /**
         * Test 1: isolated Date
         */
        expResult    = new TreeSet<>();
        expResult.add(df.parse("2003-01-07T00:00:00Z"));

        String periods = "2003-01-07T00:00:00Z";
        SortedSet<Date> result = instance.getDatesFromPeriodDescription(periods);
        assertEquals(expResult, result);

        /**
         * Test 2: one period
         */
        expResult    = new TreeSet<>();
        expResult.add(df.parse("2004-01-28T00:00:00Z"));
        expResult.add(df.parse("2004-02-04T00:00:00Z"));
        expResult.add(df.parse("2004-02-11T00:00:00Z"));
        expResult.add(df.parse("2004-02-18T00:00:00Z"));
        expResult.add(df.parse("2004-02-25T00:00:00Z"));
        expResult.add(df.parse("2004-03-03T00:00:00Z"));
        expResult.add(df.parse("2004-03-10T00:00:00Z"));
        expResult.add(df.parse("2004-03-17T00:00:00Z"));

        periods = "2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W";
        result = instance.getDatesFromPeriodDescription(periods);
        assertEquals(expResult, result);

        /**
         * Test 3: two period not joined
         */
        expResult    = new TreeSet<>();

        //first period
        expResult.add(df.parse("2004-01-28T00:00:00Z"));
        expResult.add(df.parse("2004-02-04T00:00:00Z"));
        expResult.add(df.parse("2004-02-11T00:00:00Z"));
        expResult.add(df.parse("2004-02-18T00:00:00Z"));
        expResult.add(df.parse("2004-02-25T00:00:00Z"));
        expResult.add(df.parse("2004-03-03T00:00:00Z"));
        expResult.add(df.parse("2004-03-10T00:00:00Z"));
        expResult.add(df.parse("2004-03-17T00:00:00Z"));

        //second period
        expResult.add(df.parse("2005-11-09T00:00:00Z"));
        expResult.add(df.parse("2005-11-16T00:00:00Z"));
        expResult.add(df.parse("2005-11-23T00:00:00Z"));
        expResult.add(df.parse("2005-11-30T00:00:00Z"));

        periods = "2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W,2005-11-09T00:00:00Z/2005-11-30T00:00:00Z/P1W";
        result = instance.getDatesFromPeriodDescription(periods);
        assertEquals(expResult, result);


        /**
         * Test 3: two period joined
         */
        expResult    = new TreeSet<>();

        //first period
        expResult.add(df.parse("2004-01-28T00:00:00Z"));
        expResult.add(df.parse("2004-02-04T00:00:00Z"));
        expResult.add(df.parse("2004-02-11T00:00:00Z"));
        expResult.add(df.parse("2004-02-18T00:00:00Z"));
        //second period
        expResult.add(df.parse("2004-02-25T00:00:00Z"));
        expResult.add(df.parse("2004-03-04T00:00:00Z"));
        expResult.add(df.parse("2004-03-12T00:00:00Z"));
        expResult.add(df.parse("2004-03-20T00:00:00Z"));




        periods = "2004-01-28T00:00:00Z/2004-02-25T00:00:00Z/P1W,2004-02-25T00:00:00Z/2004-03-20T00:00:00Z/P1W1D";
        result = instance.getDatesFromPeriodDescription(periods);
        System.out.println("expected =" + expResult + '\n' +
                           "result   =" + result);
        assertEquals(expResult, result);

        /**
         * Test 5: isolated dates + period1 + isolated dates + period2 + isolated dates
         */
        expResult    = new TreeSet<>();

        //isolated date
        expResult.add(df.parse("2003-01-07T00:00:00Z"));

        //isolated date
        expResult.add(df.parse("2004-01-07T00:00:00Z"));

        //first period
        expResult.add(df.parse("2004-01-28T00:00:00Z"));
        expResult.add(df.parse("2004-02-04T00:00:00Z"));
        expResult.add(df.parse("2004-02-11T00:00:00Z"));
        expResult.add(df.parse("2004-02-18T00:00:00Z"));
        expResult.add(df.parse("2004-02-25T00:00:00Z"));
        expResult.add(df.parse("2004-03-03T00:00:00Z"));
        expResult.add(df.parse("2004-03-10T00:00:00Z"));
        expResult.add(df.parse("2004-03-17T00:00:00Z"));

        //isolated date
        expResult.add(df.parse("2005-03-02T00:00:00Z"));

        //isolated date
        expResult.add(df.parse("2005-07-20T00:00:00Z"));

        //second period
        expResult.add(df.parse("2005-11-09T00:00:00Z"));
        expResult.add(df.parse("2005-11-16T00:00:00Z"));
        expResult.add(df.parse("2005-11-23T00:00:00Z"));
        expResult.add(df.parse("2005-11-30T00:00:00Z"));

        //isolated date
        expResult.add(df.parse("2009-10-31T00:00:00Z"));

        //isolated date
        expResult.add(df.parse("2010-10-31T00:00:00Z"));

        //isolated date
        expResult.add(df.parse("2011-10-31T00:00:00Z"));

        periods = "2003-01-07T00:00:00Z,2004-01-07T00:00:00Z,2004-01-28T00:00:00Z/2004-03-17T00:00:00Z/P1W,2005-03-02T00:00:00Z,2005-07-20T00:00:00Z,2005-11-09T00:00:00Z/2005-11-30T00:00:00Z/P1W,2009-10-31T00:00:00Z,2010-10-31T00:00:00Z,2011-10-31T00:00:00Z";
        result = instance.getDatesFromPeriodDescription(periods);
        assertEquals(expResult, result);
        
        /**
         * Test 4: one week and one day period one second
         */
        periods = "2004-02-25T00:00:01Z/2004-03-20T00:00:04Z/P1W1DT1S";
        expResult    = new TreeSet<>();

        expResult.add(df.parse("2004-02-25T00:00:01Z"));
        expResult.add(df.parse("2004-03-04T00:00:02Z"));
        expResult.add(df.parse("2004-03-12T00:00:03Z"));
        expResult.add(df.parse("2004-03-20T00:00:04Z"));

        result = instance.getDatesFromPeriodDescription(periods);
        assertEquals(expResult, result);
        
        
        /**
         * Test 5: one week and one day period one second 250 millisecond
         */
        instance = new PeriodUtilities(df2);
        
        periods = "2004-02-25T00:00:01.250Z/2004-03-20T00:00:05.000Z/P1W1DT1.250S";

        expResult    = new TreeSet<>();
        expResult.add(df2.parse("2004-02-25T00:00:01.250Z"));
        expResult.add(df2.parse("2004-03-04T00:00:02.500Z"));
        expResult.add(df2.parse("2004-03-12T00:00:03.750Z"));
        expResult.add(df2.parse("2004-03-20T00:00:05.000Z"));
        
        result = instance.getDatesFromPeriodDescription(periods);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimeFromPeriodDescription method, of class PeriodUtilities.
     */
    @Test
    public void getTimeFromPeriodDescription() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        PeriodUtilities instance = new PeriodUtilities(df);

        /**
         * test 1 : period of one week and one day
         */
        String periodDescription = "P1W1D";
        long expResult = 604800000L + 86400000L;
        long result = instance.getTimeFromPeriodDescription(periodDescription);
        assertEquals(expResult, result);

    }

}
