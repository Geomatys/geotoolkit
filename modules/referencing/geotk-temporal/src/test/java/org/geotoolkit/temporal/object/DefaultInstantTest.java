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
package org.geotoolkit.temporal.object;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.metadata.Citations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.temporal.Instant;
import static org.junit.Assert.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.temporal.Period;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultInstantTest extends org.geotoolkit.test.TestBase {

    private Instant instant1;
    private Instant instant2;
    private Calendar cal = Calendar.getInstance();

    @Before
    public void setUp() {
        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "position 1");
        final Map<String, Object> properties1 = new HashMap<>();
        properties1.put(IdentifiedObject.NAME_KEY, name1);
        cal.set(2000, 1, 1);
        instant1  = new DefaultInstant(properties1, cal.getTime());

        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "position 2");
        final Map<String, Object> properties2 = new HashMap<>();
        properties2.put(IdentifiedObject.NAME_KEY, name2);
        cal.set(1998, 1, 1);
        instant2  = new DefaultInstant(properties2, cal.getTime());
    }

    @After
    public void tearDown() {
        instant1 = null;
        instant2 = null;
    }

    /**
     * Test of getPosition method, of class DefaultInstant.
     */
    @Test
    public void testGetPosition() {
        Date result = instant1.getDate();
        assertFalse(instant2.getDate().equals(result));
    }

//    /**
//     * Test of getBegunBy method, of class DefaultInstant.
//     */
//    @Test
//    public void testGetBegunBy() {
//        Collection<Period> result = instant1.getBegunBy();
//        assertEquals(instant2.getBegunBy(), result);
//    }
//
//    /**
//     * Test of getEndedBy method, of class DefaultInstant.
//     */
//    @Test
//    public void testGetEndedBy() {
//        Collection<Period> result = instant1.getEndedBy();
//        assertEquals(instant2.getEndedBy(), result);
//    }

//    /**
//     * Test of setPosition method, of class DefaultInstant.
//     */
//    @Test
//    public void testSetPosition() {
//        Position result = instant1.getPosition();
//        Position position = new DefaultPosition(new Date());
//        ((DefaultInstant) instant1).setPosition(position);
//        assertFalse(instant1.getPosition().equals(result));
//    }
//
//    /**
//     * Test of setBegunBy method, of class DefaultInstant.
//     */
//    @Test
//    public void testSetBegunBy() {
//        Collection<Period> result = instant1.getBegunBy();
//        Collection<Period> begunby = null;
//        ((DefaultInstant) instant1).setBegunBy(begunby);
//        assertEquals(instant1.getBegunBy(), result);
//    }
//
//    /**
//     * Test of setEndBy method, of class DefaultInstant.
//     */
//    @Test
//    public void testSetEndBy() {
//        Collection<Period> result = instant1.getEndedBy();
//        Collection<Period> endedby = null;
//        ((DefaultInstant) instant1).setEndBy(endedby);
//        assertEquals(instant1.getEndedBy(), result);
//    }

    /**
     * Test of equals method, of class DefaultInstant.
     */
    @Test
    public void testEquals() {
        cal.set(2000, 1, 1);

        assertFalse(instant1.equals(null));
        assertEquals(cal.getTime().getTime(), instant1.getDate().getTime());
        assertFalse(instant1.equals(instant2));
    }

    /**
     * Test of hashCode method, of class DefaultInstant.
     */
    @Test
    public void testHashCode() {
        int result = instant1.hashCode();
        assertFalse(instant2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultInstant.
     */
    @Test
    public void testToString() {
        String result = instant1.toString();
        assertFalse(instant2.toString().equals(result));
    }
}
