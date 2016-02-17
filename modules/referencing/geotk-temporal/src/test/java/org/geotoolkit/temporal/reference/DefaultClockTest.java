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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.geotoolkit.temporal.object.DefaultClockTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.Calendar;
import org.opengis.temporal.Clock;
import org.opengis.temporal.ClockTime;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.InternationalString;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultClockTest extends org.geotoolkit.test.TestBase {

    private Clock clock1;
    private Clock clock2;
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {

        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "Gregorian calendar");
        TemporalReferenceSystem frame1 = FACTORY.createTemporalReferenceSystem(name1, new DefaultExtent());

        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "Julian calendar");
        TemporalReferenceSystem frame2 = FACTORY.createTemporalReferenceSystem(name2, new DefaultExtent());

        Number[] clockTime1 = {0, 0, 0};
        Number[] clockTime2 = {12, 0, 0.0};

        ClockTime clocktime1 = FACTORY.createClockTime(frame1, null, clockTime1);
        ClockTime clocktime2 = FACTORY.createClockTime(frame2, null, clockTime2);

        ClockTime utcReference1 = FACTORY.createClockTime(frame1, null, clockTime1);
        ClockTime utcReference2 = FACTORY.createClockTime(frame2, null, clockTime2);

        clock1 = FACTORY.createClock(name1, new DefaultExtent(), new SimpleInternationalString("clock1 reference event"), clocktime1, utcReference1);
        clock2 = FACTORY.createClock(name2, new DefaultExtent(), new SimpleInternationalString("clock2 reference event"), clocktime2, utcReference2);
    }

    @After
    public void tearDown() {
        clock1 = null;
        clock2 = null;
    }

    /**
     * Test of getReferenceEvent method, of class DefaultClock.
     */
    @Test
    public void testGetReferenceEvent() {
        InternationalString result = clock1.getReferenceEvent();
        assertFalse(clock2.getReferenceEvent().equals(result));
    }

    /**
     * Test of getReferenceTime method, of class DefaultClock.
     */
    @Test
    public void testGetReferenceTime() {
        ClockTime result = clock1.getReferenceTime();
        assertFalse(clock2.getReferenceTime().equals(result));
    }

    /**
     * Test of getUTCReference method, of class DefaultClock.
     */
    @Test
    public void testGetUTCReference() {
        ClockTime result = clock1.getUTCReference();
        assertFalse(clock2.getUTCReference().equals(result));
    }

    /**
     * Test of clkTrans method, of class DefaultClock.
     */
    @Test
    public void testClkTrans() {
        //@todo this method is not implemented yet!
    }

    /**
     * Test of utcTrans method, of class DefaultClock.
     */
    @Test
    public void testUtcTrans() {
        //@todo this method is not implemented yet!
    }
//
//    /**
//     * Test of setReferenceEvent method, of class DefaultClock.
//     */
//    @Test
//    public void testSetReferenceEvent() {
//        InternationalString result = clock1.getReferenceEvent();
//        ((DefaultClock) clock1).setReferenceEvent(new SimpleInternationalString(""));
//        assertFalse(clock1.getReferenceEvent().equals(result));
//    }

//    /**
//     * Test of setReferenceTime method, of class DefaultClock.
//     */
//    @Test
//    public void testSetReferenceTime() {
//        ClockTime result = clock1.getReferenceTime();
//        ((DefaultClock) clock1).setReferenceTime(new DefaultClockTime(clock1, IndeterminateValue.UNKNOWN, null));
//        assertFalse(clock1.getReferenceTime().equals(result));
//    }
//
//    /**
//     * Test of setUtcReference method, of class DefaultClock.
//     */
//    @Test
//    public void testSetUtcReference() {
//        ClockTime result = clock1.getUTCReference();
//        ((DefaultClock) clock1).setUtcReference(new DefaultClockTime(clock1, IndeterminateValue.UNKNOWN, null));
//        assertFalse(clock1.getUTCReference().equals(result));
//    }

    /**
     * Test of getDateBasis method, of class DefaultClock.
     */
    @Test
    public void testGetDateBasis() {
        Collection<Calendar> result = ((DefaultClock) clock1).getDateBasis();
        assertEquals(((DefaultClock) clock2).getDateBasis(), result);
    }

    /**
     * Test of equals method, of class DefaultClock.
     */
    @Test
    public void testEquals() {
        assertFalse(clock1.equals(null));
        assertEquals(clock1, clock1);
        assertFalse(clock1.equals(clock2));
    }

    /**
     * Test of hashCode method, of class DefaultClock.
     */
    @Test
    public void testHashCode() {
        int result = clock1.hashCode();
        assertFalse(clock2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultClock.
     */
    @Test
    public void testToString() {
        String result = clock1.toString();
        assertFalse(clock2.toString().equals(result));
    }
}
