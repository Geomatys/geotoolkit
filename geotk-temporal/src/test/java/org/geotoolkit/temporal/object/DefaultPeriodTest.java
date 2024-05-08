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
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.referencing.NamedIdentifier;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.temporal.Period;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 */
public class DefaultPeriodTest {

    private Period period1;
    private Period period2;

    public DefaultPeriodTest() {
        NamedIdentifier name = new NamedIdentifier(null, "Period");
        final Map<String, Object> properties = new HashMap<>();
        properties.put(IdentifiedObject.NAME_KEY, name);

        Calendar cal = Calendar.getInstance();
        cal.set(1995, 1, 1);
        var begining1 = new DefaultInstant(properties, cal.getTime().toInstant());
        cal.set(2000, 1, 1);
        var ending1 = new DefaultInstant(properties, cal.getTime().toInstant());
        cal.set(2000, 1, 1);
        var begining2 = new DefaultInstant(properties, cal.getTime().toInstant());
        cal.set(2012, 1, 1);
        var ending2 = new DefaultInstant(properties, cal.getTime().toInstant());
        period1 = new DefaultPeriod(properties, begining1, ending1);
        period2 = new DefaultPeriod(properties, begining2, ending2);
    }

    /**
     * Test of getBeginning method, of class DefaultPeriod.
     */
    @Test
    public void testGetBeginning() {
        var result = period1.getBeginning();
        assertFalse(period2.getBeginning().equals(result));
    }

    /**
     * Test of getEnding method, of class DefaultPeriod.
     */
    @Test
    public void testGetEnding() {
        var result = period1.getEnding();
        assertFalse(period2.getEnding().equals(result));
    }

    /**
     * Test of equals method, of class DefaultPeriod.
     */
    @Test
    public void testEquals() {
        assertNotNull(period1.equals(null));
        assertEquals(period1, period1);
        assertFalse(period1.equals(period2));
    }

    /**
     * Test of hashCode method, of class DefaultPeriod.
     */
    @Test
    public void testHashCode() {
        int result = period1.hashCode();
        assertFalse(period2.hashCode() == result);
    }

    /**
     * Test of toString method, of class DefaultPeriod.
     */
    @Test
    public void testToString() {
        String result = period1.toString();
        assertFalse(period2.toString().equals(result));
    }
}
