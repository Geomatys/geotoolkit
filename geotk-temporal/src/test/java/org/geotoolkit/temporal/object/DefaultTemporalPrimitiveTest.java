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
import java.util.Collections;
import java.util.Date;
import static org.apache.sis.feature.AbstractIdentifiedType.NAME_KEY;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.temporal.Instant;
import static org.junit.Assert.*;
import org.opengis.temporal.IndeterminateValue;
import org.opengis.temporal.Period;
import org.opengis.temporal.RelativePosition;
import org.opengis.temporal.TemporalPrimitive;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultTemporalPrimitiveTest extends org.geotoolkit.test.TestBase {

    private TemporalPrimitive temporalPrimitive1;
    private TemporalPrimitive temporalPrimitive2;
    private final Calendar cal = Calendar.getInstance();

    @Before
    public void setUp() {

        cal.set(1981, 6, 25);
        Date date = cal.getTime();

        temporalPrimitive1 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), date);
        temporalPrimitive2 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), new Date());
    }

    @After
    public void tearDown() {
        temporalPrimitive1 = null;
        temporalPrimitive2 = null;
    }

    /**
     * Test of relativePosition method, of class DefaultTemporalPrimitive.
     */
    @Test
    public void testRelativePosition() {
        TemporalPrimitive other;

        //relative position between Instant objects
        cal.set(2000, 0, 1);
        other = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id3"), cal.getTime());
        RelativePosition result = temporalPrimitive1.relativePosition(other);
        assertFalse(temporalPrimitive2.relativePosition(other).equals(result));

        //relative position between Instant and Period
        Instant instant1 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id3"), cal.getTime());
        Instant instant2 = (DefaultInstant) temporalPrimitive2;

        other = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "idp"), instant1, instant2);
        result = temporalPrimitive1.relativePosition(other);
        assertFalse(temporalPrimitive2.relativePosition(other).equals(result));

        //relative position between Period onbjects
        Period temporalPeriod1 = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "idp"), (Instant) temporalPrimitive1, instant1);
        cal.setTime(instant2.getDate());
        cal.roll(Calendar.YEAR, true); // add one year to instant2 which is current date
        Period temporalPeriod2 = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "idp"), instant2, new DefaultInstant(Collections.singletonMap(NAME_KEY, "id3"), cal.getTime()));
        result = temporalPeriod1.relativePosition(other);
        assertFalse(temporalPeriod2.relativePosition(other).equals(result));


        // relative position with undeterminate value
        Instant now = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id3"), new DefaultTemporalPosition(IndeterminateValue.NOW));
        result = temporalPrimitive1.relativePosition(now);
        assertEquals(RelativePosition.BEFORE, result);

        result = now.relativePosition(temporalPrimitive1);
        assertEquals(RelativePosition.AFTER, result);
    }
}
