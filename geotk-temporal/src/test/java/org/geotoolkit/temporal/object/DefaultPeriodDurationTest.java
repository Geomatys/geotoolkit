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

import java.time.temporal.ChronoUnit;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 */
public class DefaultPeriodDurationTest {

    private DefaultDuration periodDuration1;
    private DefaultDuration periodDuration2;

    public DefaultPeriodDurationTest() {
        periodDuration1 = new DefaultDuration();
        periodDuration1.set(ChronoUnit.YEARS, 48);
        periodDuration1.set(ChronoUnit.MONTHS, 8);
    //  periodDuration1.set(ChronoUnit.WEEKS, 0);
        periodDuration1.set(ChronoUnit.DAYS, 4);
        periodDuration1.set(ChronoUnit.HOURS, 14);
        periodDuration1.set(ChronoUnit.MINUTES, 7);
        periodDuration1.set(ChronoUnit.SECONDS, 29);
        periodDuration1.set(ChronoUnit.MILLIS, 548);
        periodDuration2 = new DefaultDuration();
        periodDuration2.setTimeInMillis(1535148449548L);
    }

    /**
     * Test of get method.
     */
    @Test
    public void testGet() {
        assertEquals(periodDuration1.get(ChronoUnit.YEARS),
                     periodDuration2.get(ChronoUnit.YEARS));
        assertEquals(periodDuration1.get(ChronoUnit.MONTHS),
                     periodDuration2.get(ChronoUnit.MONTHS));
        assertEquals(periodDuration1.get(ChronoUnit.WEEKS),
                     periodDuration2.get(ChronoUnit.WEEKS));
        assertEquals(periodDuration1.get(ChronoUnit.DAYS),
                     periodDuration2.get(ChronoUnit.DAYS));
        assertEquals(periodDuration1.get(ChronoUnit.HOURS),
                     periodDuration2.get(ChronoUnit.HOURS));
        assertEquals(periodDuration1.get(ChronoUnit.MINUTES),
                     periodDuration2.get(ChronoUnit.MINUTES));
        assertEquals(periodDuration1.get(ChronoUnit.SECONDS),
                     periodDuration2.get(ChronoUnit.SECONDS));
    }

    /**
     * Test of getTimeInMillis method.
     */
    @Test
    public void testGetTimeInMillis() {
        assertEquals(periodDuration2.getTimeInMillis(),
                     periodDuration1.getTimeInMillis());
    }

    /**
     * Test of equals method.
     */
    @Test
    public void testEquals() {
        assertEquals(periodDuration1, periodDuration1);
        assertEquals(periodDuration1, periodDuration2);
    }

    /**
     * Test of hashCode method.
     */
    @Test
    public void testHashCode() {
        assertEquals(periodDuration1.hashCode(), periodDuration2.hashCode());
    }

    /**
     * Test of toString method.
     */
    @Test
    public void testToString() {
        assertEquals(periodDuration1.toString(), periodDuration2.toString());
    }
}
