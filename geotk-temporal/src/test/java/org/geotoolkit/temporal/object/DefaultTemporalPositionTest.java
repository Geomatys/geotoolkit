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

import org.apache.sis.referencing.CommonCRS;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.TemporalPosition;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultTemporalPositionTest {

    private TemporalPosition temporalPosition1;
    private TemporalPosition temporalPosition2;

    @Before
    public void setUp() {
        TemporalCRS frame1 = CommonCRS.Temporal.JULIAN.crs();
        TemporalCRS frame2 = CommonCRS.Temporal.TRUNCATED_JULIAN.crs();
        temporalPosition1 = new DefaultTemporalPosition(frame1, IndeterminateValue.UNKNOWN);
        temporalPosition2 = new DefaultTemporalPosition(frame2, IndeterminateValue.NOW);
    }

    /**
     * Test of getIndeterminatePosition method, of class DefaultTemporalPosition.
     */
    @Test
    public void testGetIndeterminatePosition() {
        IndeterminateValue result = temporalPosition1.getIndeterminatePosition().orElse(null);
        assertFalse(temporalPosition2.getIndeterminatePosition().equals(result));
    }

    /**
     * Test of getFrame method, of class DefaultTemporalPosition.
     */
    @Test
    public void testGetFrame() {
        TemporalCRS result = ((DefaultTemporalPosition) temporalPosition1).getFrame();
        assertFalse(((DefaultTemporalPosition) temporalPosition2).getFrame().equals(result));
    }

    /**
     * Test of equals method, of class DefaultTemporalPosition.
     */
    @Test
    public void testEquals() {
        assertNotNull(temporalPosition1);
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
