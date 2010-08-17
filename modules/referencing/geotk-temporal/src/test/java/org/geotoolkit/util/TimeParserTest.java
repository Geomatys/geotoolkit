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
package org.geotoolkit.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Test for the time parameter in a WMS request.
 *
 * @author Cédric Briançon
 */
public class TimeParserTest extends TestCase {
    /**
     * A time period for testing.
     */
    private final static String PERIOD = "2007-01-01T12Z/2007-01-31T12Z/P1DT12H";

    /**
     * Tests only the increment part of the time parameter.
     *
     * @throws ParseException if the string can't be parsed.
     */
    @Test
    public void testPeriod() throws ParseException {
        final long millisInDay = TimeParser.MILLIS_IN_DAY;
        assertEquals(               millisInDay,  TimeParser.parsePeriod("P1D"));
        assertEquals(             3*millisInDay,  TimeParser.parsePeriod("P3D"));
        assertEquals(            14*millisInDay,  TimeParser.parsePeriod("P2W"));
        assertEquals(             8*millisInDay,  TimeParser.parsePeriod("P1W1D"));
        assertEquals(               millisInDay,  TimeParser.parsePeriod("PT24H"));
        assertEquals(Math.round(1.5*millisInDay), TimeParser.parsePeriod("P1.5D"));
    }

    /**
     * Compares the dates obtained by parsing the time parameter with the expected values.
     *
     * @throws ParseException if the string can't be parsed.
     */
    @Test
    public void testInterval() throws ParseException {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        final List<Date> dates = new ArrayList<Date>();
        TimeParser.parse(PERIOD, TimeParser.MILLIS_IN_DAY, dates);
        // Verify that the list contains at least one element.
        assertFalse(dates.isEmpty());
        assertEquals(format.parse("2007-01-01T12Z"), dates.get(0));
        assertEquals(format.parse("2007-01-03T00Z"), dates.get(1));
        assertEquals(format.parse("2007-01-04T12Z"), dates.get(2));
        assertEquals(format.parse("2007-01-06T00Z"), dates.get(3));
        assertEquals(format.parse("2007-01-07T12Z"), dates.get(4));
        assertEquals(format.parse("2007-01-09T00Z"), dates.get(5));
        assertEquals(format.parse("2007-01-10T12Z"), dates.get(6));
        assertEquals(format.parse("2007-01-12T00Z"), dates.get(7));
    }
}
