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

import java.util.Date;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalCoordinate;
import org.opengis.temporal.TemporalCoordinateSystem;
import static org.junit.Assert.*;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultTemporalCoordinateTest extends org.geotoolkit.test.TestBase {

    private TemporalCoordinate temporalCoordinate1;
    private TemporalCoordinate temporalCoordinate2;
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {
        NamedIdentifier name = new NamedIdentifier(Citations.CRS, "Gregorian calendar");
        Number coordinateValue = 100;
        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, new SimpleInternationalString("Julian calendar"));

        TemporalCoordinateSystem frame1 = FACTORY.createTemporalCoordinateSystem(name, new DefaultExtent(), new Date(200000000), Units.DAY);
        TemporalCoordinateSystem frame2 = FACTORY.createTemporalCoordinateSystem(name1, new DefaultExtent(), new Date(200001000), Units.HOUR);

        temporalCoordinate1 = FACTORY.createTemporalCoordinate(frame1, IndeterminateValue.NOW, coordinateValue);
        temporalCoordinate2 = FACTORY.createTemporalCoordinate(frame2, IndeterminateValue.AFTER, coordinateValue);
    }

    @After
    public void tearDown() {
        temporalCoordinate1 = null;
        temporalCoordinate2 = null;
    }

    /**
     * Test of getCoordinateValue method, of class DefaultTemporalCoordinate.
     */
    @Test
    public void testGetCoordinateValue() {
        Number result = temporalCoordinate1.getCoordinateValue();
        assertTrue(temporalCoordinate2.getCoordinateValue() == result);
    }

    /**
     * Test of setCoordinateValue method, of class DefaultTemporalCoordinate.
     */
    @Test
    public void testSetCoordinateValue() {
        Number result = temporalCoordinate1.getCoordinateValue();
        ((DefaultTemporalCoordinate) temporalCoordinate1).setCoordinateValue(250);
        assertFalse(temporalCoordinate1.getCoordinateValue() == result);
    }

    /**
     * Test of equals method, of class DefaultTemporalCoordinate.
     */
    @Test
    public void testEquals() {
        assertFalse(temporalCoordinate1.equals(null));
        assertEquals(temporalCoordinate1, temporalCoordinate1);
        assertFalse(temporalCoordinate1.equals(temporalCoordinate2));
    }

    /**
     * Test of hashCode method, of class DefaultTemporalCoordinate.
     */
    @Test
    public void testHashCode() {
        int result = temporalCoordinate1.hashCode();
        assertFalse(temporalCoordinate2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultTemporalCoordinate.
     */
    @Test
    public void testToString() {
        String result = temporalCoordinate1.toString();
        assertFalse(temporalCoordinate2.toString().equals(result));
    }
}
