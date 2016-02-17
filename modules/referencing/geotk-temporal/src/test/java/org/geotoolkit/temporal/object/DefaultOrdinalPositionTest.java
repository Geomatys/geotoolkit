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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.geotoolkit.temporal.reference.DefaultTemporalReferenceSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.temporal.IndeterminateValue;
import static org.junit.Assert.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.OrdinalEra;
import org.opengis.temporal.OrdinalPosition;
import org.opengis.temporal.TemporalReferenceSystem;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultOrdinalPositionTest extends org.geotoolkit.test.TestBase {

    private OrdinalPosition ordinalPosition1;
    private OrdinalPosition ordinalPosition2;
    private Calendar cal = Calendar.getInstance();
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {
        NamedIdentifier name = new NamedIdentifier(Citations.CRS, "Gregorian calendar");
        final Map<String, Object> properties = new HashMap<>();
        properties.put(IdentifiedObject.NAME_KEY, name);
        TemporalReferenceSystem frame = FACTORY.createTemporalReferenceSystem(name, new DefaultExtent());

        cal.set(500, 0, 1);
        Date beginning1 = cal.getTime();
        cal.set(1000, 0, 1);
        Date end1 = cal.getTime();
        OrdinalEra ordinalEra1 = FACTORY.createOrdinalEra(new SimpleInternationalString("ordinal era 1"), beginning1, end1, null);
        cal.set(1000, 1, 1);
        Date beginning2 = cal.getTime();
        cal.set(2000, 0, 1);
        Date end2 = cal.getTime();
        OrdinalEra ordinalEra2 = FACTORY.createOrdinalEra(new SimpleInternationalString("ordinal era 2"), beginning2, end2, null);

        ordinalPosition1 = FACTORY.createOrdinalPosition(frame, IndeterminateValue.UNKNOWN, ordinalEra1);
        ordinalPosition2 = FACTORY.createOrdinalPosition(frame, IndeterminateValue.AFTER, ordinalEra2);
    }

    @After
    public void tearDown() {
        ordinalPosition1 = null;
        ordinalPosition2 = null;
    }

    /**
     * Test of getOrdinalPosition method, of class DefaultOrdinalPosition.
     */
    @Test
    public void testGetOrdinalPosition() {
        OrdinalEra result = ordinalPosition1.getOrdinalPosition();
        assertFalse(ordinalPosition2.getOrdinalPosition().equals(result));
    }

    /**
     * Test of setOrdinalPosition method, of class DefaultOrdinalPosition.
     */
    @Test
    public void testSetOrdinalPosition() {
        OrdinalEra result = ordinalPosition1.getOrdinalPosition();
        cal.set(10, 0, 0);
        Date beginning = cal.getTime();
        cal.set(2012, 12, 23);
        Date end = cal.getTime();
        OrdinalEra ordinalEra = FACTORY.createOrdinalEra(new SimpleInternationalString("Era"), beginning, end, null);//null;//new DefaultOrdinalEra(new SimpleInternationalString("Era"), beginning, end);
        ((DefaultOrdinalPosition) ordinalPosition1).setOrdinalPosition(ordinalEra);
        assertFalse(ordinalPosition1.getOrdinalPosition().equals(result));
    }

    /**
     * Test of equals method, of class DefaultOrdinalPosition.
     */
    @Test
    public void testEquals() {
        assertFalse(ordinalPosition1.equals(null));
        assertEquals(ordinalPosition1, ordinalPosition1);
        assertFalse(ordinalPosition1.equals(ordinalPosition2));
    }

    /**
     * Test of hashCode method, of class DefaultOrdinalPosition.
     */
    @Test
    public void testHashCode() {
        int result = ordinalPosition1.hashCode();
        assertFalse(ordinalPosition2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultOrdinalPosition.
     */
    @Test
    public void testToString() {
        String result = ordinalPosition1.toString();
        assertFalse(ordinalPosition2.toString().equals(result));
    }
}
