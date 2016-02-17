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

import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.Citations;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.temporal.factory.DefaultTemporalFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalPosition;
import org.opengis.temporal.TemporalReferenceSystem;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultTemporalPositionTest extends org.geotoolkit.test.TestBase {

    private TemporalPosition temporalPosition1;
    private TemporalPosition temporalPosition2;
    private final static DefaultTemporalFactory FACTORY = new DefaultTemporalFactory();

    @Before
    public void setUp() {
        NamedIdentifier name1 = new NamedIdentifier(Citations.CRS, "Gregorian calendar");
        NamedIdentifier name2 = new NamedIdentifier(Citations.CRS, "Julian calendar");

        TemporalReferenceSystem frame1 = FACTORY.createTemporalReferenceSystem(name1, new DefaultExtent());
        TemporalReferenceSystem frame2 = FACTORY.createTemporalReferenceSystem(name2, new DefaultExtent());
        temporalPosition1 = FACTORY.createTemporalPosition(frame1, IndeterminateValue.UNKNOWN);
        temporalPosition2 = FACTORY.createTemporalPosition(frame2, IndeterminateValue.NOW);
    }

    @After
    public void tearDown() {
        temporalPosition1 = null;
        temporalPosition2 = null;
    }

    /**
     * Test of getIndeterminatePosition method, of class DefaultTemporalPosition.
     */
    @Test
    public void testGetIndeterminatePosition() {
        IndeterminateValue result = temporalPosition1.getIndeterminatePosition();
        assertFalse(temporalPosition2.getIndeterminatePosition().equals(result));
    }

    /**
     * Test of getFrame method, of class DefaultTemporalPosition.
     */
    @Test
    public void testGetFrame() {
        TemporalReferenceSystem result = ((DefaultTemporalPosition) temporalPosition1).getFrame();
        assertFalse(((DefaultTemporalPosition) temporalPosition2).getFrame().equals(result));
    }

    /**
     * Test of equals method, of class DefaultTemporalPosition.
     */
    @Test
    public void testEquals() {
        assertFalse(temporalPosition1.equals(null));
        assertEquals(temporalPosition1, temporalPosition1);
        assertFalse(temporalPosition1.equals(temporalPosition2));
    }

    /**
     * Test of hashCode method, of class DefaultTemporalPosition.
     */
    @Test
    public void testHashCode() {
        int result = temporalPosition1.hashCode();
        assertFalse(temporalPosition2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultTemporalPosition.
     */
    @Test
    public void testToString() {
        String result = temporalPosition1.toString();
        assertFalse(temporalPosition2.toString().equals(result));
    }
}
