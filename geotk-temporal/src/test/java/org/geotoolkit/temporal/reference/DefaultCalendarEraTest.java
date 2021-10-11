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

import java.util.Calendar;
import java.util.Collection;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.metadata.Identifier;
import org.opengis.temporal.CalendarDate;
import org.opengis.temporal.CalendarEra;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Instant;
import org.opengis.temporal.JulianDate;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.InternationalString;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultCalendarEraTest extends org.geotoolkit.test.TestBase {

    private CalendarEra calendarEra1;
    private CalendarEra calendarEra2;
    private Calendar cal = Calendar.getInstance();
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();
    private TemporalReferenceSystem frame1, frame2;

    @Before
    public void setUp() {
        NamedIdentifier name1 = new NamedIdentifier(null, "Julian calendar");
        frame1 = FACTORY.createTemporalReferenceSystem(name1, new DefaultExtent());

        NamedIdentifier name2 = new NamedIdentifier(null, "Babylonian calendar");
        frame2 = FACTORY.createTemporalReferenceSystem(name2, new DefaultExtent());

        int[] calendarDate1 = {1900, 1, 1};
        int[] calendarDate2 = {400, 1, 1};

        CalendarDate referenceDate1 = FACTORY.createCalendarDate(frame1, IndeterminateValue.BEFORE, new SimpleInternationalString("Gregorian calendar"), calendarDate1);
        CalendarDate referenceDate2 = FACTORY.createCalendarDate(frame2, IndeterminateValue.NOW, new SimpleInternationalString("Babylonian calendar"), calendarDate2);

        JulianDate julianReference = FACTORY.createJulianDate(frame1, IndeterminateValue.NOW, 123456789);//new DefaultJulianDate(frame1, IndeterminateValue.NOW, 123456789);

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

        calendarEra1 = FACTORY.createCalendarEra(new SimpleInternationalString("Cenozoic"), new SimpleInternationalString("no event for Cenozoic"), referenceDate1, julianReference, epochOfUse1);
        calendarEra2 = FACTORY.createCalendarEra(new SimpleInternationalString("Mesozoic"), new SimpleInternationalString("no event for Mesozoic"), referenceDate2, julianReference, epochOfUse2);
    }

    @After
    public void tearDown() {
        calendarEra1 = null;
        calendarEra2 = null;
    }

    /**
     * Test of getName method, of class DefaultCalendarEra.
     */
    @Test
    public void testGetName() {
        Identifier result = calendarEra1.getName();
        assertFalse(calendarEra2.getName().equals(result));
    }

    /**
     * Test of getReferenceEvent method, of class DefaultCalendarEra.
     */
    @Test
    public void testGetReferenceEvent() {
        InternationalString result = calendarEra1.getReferenceEvent();
        assertFalse(calendarEra2.getReferenceEvent().equals(result));
    }

    /**
     * Test of getReferenceDate method, of class DefaultCalendarEra.
     */
    @Test
    public void testGetReferenceDate() {
        CalendarDate result = calendarEra1.getReferenceDate();
        assertFalse(calendarEra2.getReferenceDate().equals(result));

    }

    /**
     * Test of getJulianReference method, of class DefaultCalendarEra.
     */
    @Test
    public void testGetJulianReference() {
        JulianDate result = calendarEra1.getJulianReference();
        assertEquals(calendarEra2.getJulianReference(), result);
    }

    /**
     * Test of getEpochOfUse method, of class DefaultCalendarEra.
     */
    @Test
    public void testGetEpochOfUse() {
        Period result = calendarEra1.getEpochOfUse();
        assertFalse(calendarEra2.getEpochOfUse().equals(result));
    }

//    /**
//     * Test of setName method, of class DefaultCalendarEra.
//     */
//    @Test
//    public void testSetName() {
//        InternationalString result = calendarEra1.getName();
//        ((DefaultCalendarEra)calendarEra1).setName(new SimpleInternationalString("new Era"));
//        assertFalse(calendarEra1.getName().equals(result));
//    }

    /**
     * Test of setReferenceEvent method, of class DefaultCalendarEra.
     */
    @Test
    public void testSetReferenceEvent() {
        InternationalString result = calendarEra1.getReferenceEvent();
        ((DefaultCalendarEra)calendarEra1).setReferenceEvent(new SimpleInternationalString("new Era description"));
        assertFalse(calendarEra1.getReferenceEvent().equals(result));
    }

    /**
     * Test of setReferenceDate method, of class DefaultCalendarEra.
     */
    @Test
    public void testSetReferenceDate() {
        CalendarDate result = calendarEra1.getReferenceDate();
        int[] date = {1950,6,10};
        ((DefaultCalendarEra)calendarEra1).setReferenceDate(FACTORY.createCalendarDate(frame1, IndeterminateValue.UNKNOWN, new SimpleInternationalString("new reference Date"), date));
        assertFalse(calendarEra1.getReferenceDate().equals(result));
    }

    /**
     * Test of setJulianReference method, of class DefaultCalendarEra.
     */
    @Test
    public void testSetJulianReference() {
        JulianDate result = calendarEra1.getJulianReference();
        ((DefaultCalendarEra)calendarEra1).setJulianReference(FACTORY.createJulianDate(frame1, IndeterminateValue.UNKNOWN, 785410));
        assertFalse(calendarEra1.getJulianReference().equals(result));
    }

    /**
     * Test of setEpochOfUse method, of class DefaultCalendarEra.
     */
    @Test
    public void testSetEpochOfUse() {
        Period result = calendarEra1.getEpochOfUse();
        cal.set(1900, 10, 10);
        final Instant nBeg = FACTORY.createInstant(cal.getTime());
        final Instant nEnd = FACTORY.createInstant(cal.getTime());
        ((DefaultCalendarEra)calendarEra1).setEpochOfUse(FACTORY.createPeriod(nBeg, nEnd));
        assertFalse(calendarEra1.getEpochOfUse().equals(result));
    }

    /**
     * Test of getDatingSystem method, of class DefaultCalendarEra.
     */
    @Test
    public void testGetDatingSystem() {
        Collection<org.opengis.temporal.Calendar> result = ((DefaultCalendarEra)calendarEra1).getDatingSystem();
        assertEquals(((DefaultCalendarEra)calendarEra2).getDatingSystem(),result);
    }

    /**
     * Test of equals method, of class DefaultCalendarEra.
     */
    @Test
    public void testEquals() {
        assertFalse(calendarEra1.equals(null));
        assertEquals(calendarEra1, calendarEra1);
        assertFalse(calendarEra1.equals(calendarEra2));
    }

    /**
     * Test of hashCode method, of class DefaultCalendarEra.
     */
    @Test
    public void testHashCode() {
        int result = calendarEra1.hashCode();
        assertFalse(calendarEra2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultCalendarEra.
     */
    @Test
    public void testToString() {
        String result = calendarEra1.toString();
        assertFalse(calendarEra2.toString().equals(result));
    }
}
