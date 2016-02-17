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
package org.geotoolkit.temporal.reference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.geotoolkit.temporal.object.DefaultCalendarDate;
import org.geotoolkit.temporal.object.DefaultClockTime;
import org.geotoolkit.temporal.object.DefaultDateAndTime;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultJulianDate;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.temporal.Calendar;
import static org.junit.Assert.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.CalendarEra;
import org.opengis.temporal.Clock;
import org.opengis.temporal.ClockTime;
import org.opengis.temporal.DateAndTime;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Instant;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultCalendarTest extends org.geotoolkit.test.TestBase {

    private Calendar calendar1;
    private Calendar calendar2;
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {

        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "Gregorian calendar");
        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "Julian calendar");

        //----------------------- Time Basis ----------------------//
         TemporalReferenceSystem frame1 = FACTORY.createTemporalReferenceSystem(name1, new DefaultExtent());
        NamedIdentifier clockName1 = new NamedIdentifier(Citations.CRS, "Gregorian calendar");
        Number[] clockTime1 = {0, 0, 0};
        ClockTime clocktime1 = new DefaultClockTime(frame1, null, clockTime1);
        ClockTime utcReference1 = new DefaultClockTime(frame1, null, clockTime1);
        Clock clock1 = FACTORY.createClock(clockName1, new DefaultExtent(), new SimpleInternationalString("clock1 reference event"), clocktime1, utcReference1);
                               //---------------//

        //-------------------- Reference Frames --------------------//
        TemporalReferenceSystem frame2 = FACTORY.createTemporalReferenceSystem(name2, new DefaultExtent());

        int[] calendarDate1 = {1900, 1, 1};
        int[] calendarDate2 = {400, 1, 1};

        CalendarDate referenceDate1 = FACTORY.createCalendarDate(frame1, IndeterminateValue.BEFORE, new SimpleInternationalString("Gregorian calendar"), calendarDate1);
        CalendarDate referenceDate2 = FACTORY.createCalendarDate(frame2, IndeterminateValue.NOW, new SimpleInternationalString("Babylonian calendar"), calendarDate2);

        JulianDate julianReference = FACTORY.createJulianDate(frame1, IndeterminateValue.NOW, 123456789);//new DefaultJulianDate(frame1, IndeterminateValue.NOW, 123456789);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(1900, 0, 1);
        Instant begining1 = FACTORY.createInstant(cal.getTime());

        cal.set(2000, 9, 17);
        Instant ending1 = FACTORY.createInstant(cal.getTime());

        cal.set(2000, 1, 1);
        Instant begining2 = FACTORY.createInstant(cal.getTime());

        cal.set(2012, 1, 1);
        Instant ending2 = FACTORY.createInstant(cal.getTime());

        //-- map period
        Period epochOfUse1 = FACTORY.createPeriod(begining1, ending1);
        Period epochOfUse2 = FACTORY.createPeriod(begining2, ending2);

        CalendarEra calendarEra1 = FACTORY.createCalendarEra(new SimpleInternationalString("Cenozoic"), new SimpleInternationalString("no event for Cenozoic"), referenceDate1, julianReference, epochOfUse1);
        CalendarEra calendarEra2 = FACTORY.createCalendarEra(new SimpleInternationalString("Mesozoic"), new SimpleInternationalString("no event for Mesozoic"), referenceDate2, julianReference, epochOfUse2);

        List<CalendarEra> referenceFrame = new ArrayList<>();
        referenceFrame.add(calendarEra1);
        referenceFrame.add(calendarEra2);

                               //---------------//

        calendar1 = FACTORY.createCalendar(name1, new DefaultExtent(), referenceFrame, clock1);
        calendar2 = FACTORY.createCalendar(name2, new DefaultExtent(), referenceFrame, clock1);
    }

    @After
    public void tearDown() {
        calendar1 = null;
        calendar2 = null;
    }

    /**
     * Test of dateTrans method, of class DefaultCalendar.
     */
    @Test
    public void testDateTrans_CalendarDate_ClockTime() {
        int[] cal = {2012, 9, 10};
        CalendarDate calendarDate = new DefaultCalendarDate(calendar1, IndeterminateValue.NOW, new SimpleInternationalString("new Era"), cal);
        Number[] clock = {12, 10, 5.488};
        ClockTime clockTime = new DefaultClockTime(calendar1, IndeterminateValue.NOW, clock);
        JulianDate result = calendar1.dateTrans(calendarDate, clockTime);
        assertTrue(calendar2.dateTrans(calendarDate, clockTime).equals(result));
    }

    /**
     * Test of dateTrans method, of class DefaultCalendar.
     */
    @Test
    public void testDateTrans_DateAndTime() {
        int[] cal = {2012, 9, 10};
        Number[] clock = {12, 10, 5.488};
        DateAndTime dateAndTime = new DefaultDateAndTime(calendar1, null, null, cal, clock);
        JulianDate result = ((DefaultCalendar) calendar1).dateTrans(dateAndTime);
        assertTrue(((DefaultCalendar) calendar1).dateTrans(dateAndTime).equals(result));
    }

    /**
     * Test of julTrans method, of class DefaultCalendar.
     */
    @Test
    public void testJulTrans() {
        //@todo this method is not supported yet!
    }

    /**
     * Test of getBasis method, of class DefaultCalendar.
     */
    @Test
    public void testGetBasis() {
        Collection<CalendarEra> result = (Collection<CalendarEra>) calendar1.getReferenceFrame();
        assertEquals(calendar2.getReferenceFrame(), result);
    }

    /**
     * Test of getClock method, of class DefaultCalendar.
     */
    @Test
    public void testGetClock() {
        Clock result = calendar1.getTimeBasis();
        assertEquals(calendar2.getTimeBasis(), result);
    }
//
//    /**
//     * Test of setBasis method, of class DefaultCalendar.
//     */
//    @Test
//    public void testSetBasis() throws ParseException {
//        Collection<CalendarEra> result = (Collection<CalendarEra>) calendar1.getReferenceFrames();
//        int[] calendarDate = {1, 1, 1};
//        CalendarEra calendarEra = new DefaultCalendarEra(new SimpleInternationalString("Babylonian calendar"),
//                new SimpleInternationalString("Ascension of Nebuchadnezzar II to the throne of Babylon"),
//                new DefaultCalendarDate(calendar1, null, null, calendarDate),
//                new DefaultJulianDate(calendar1, null, 1721423.25),
//                new DefaultPeriod(new DefaultInstant(new DefaultPosition(new DefaultJulianDate(calendar1, null, 2087769))),
//                new DefaultInstant(new DefaultPosition(new DefaultJulianDate(calendar1, null, 2299160)))));
//        Collection<CalendarEra> collection = new ArrayList<CalendarEra>();
//        collection.add(calendarEra);
//        ((DefaultCalendar) calendar1).setReferenceFrames(collection);
//        assertFalse(calendar1.getReferenceFrames().equals(result));
//    }
//
//    /**
//     * Test of setClock method, of class DefaultCalendar.
//     */
//    @Test
//    public void testSetClock() {
//        Clock result = calendar1.getTimeBasis();
//        ((DefaultCalendar) calendar1).setTimeBasis(null);
//        assertEquals(calendar1.getTimeBasis(), result);
//    }

    /**
     * Test of equals method, of class DefaultCalendar.
     */
    @Test
    public void testEquals() {
        assertFalse(calendar1.equals(null));
        assertEquals(calendar1, calendar1);
        assertFalse(calendar1.equals(calendar2));
    }

    /**
     * Test of hashCode method, of class DefaultCalendar.
     */
    @Test
    public void testHashCode() {
        int result = calendar1.hashCode();
        assertFalse(calendar2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultCalendar.
     */
    @Test
    public void testToString() {
        String result = calendar1.toString();
        assertFalse(calendar2.toString().equals(result));
    }
}
