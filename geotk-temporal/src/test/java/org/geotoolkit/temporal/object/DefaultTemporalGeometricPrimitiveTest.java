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
import static org.junit.Assert.*;
import org.opengis.temporal.Duration;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultTemporalGeometricPrimitiveTest {

    private Instant temporalGeomericPrimitive1;
    private Instant temporalGeomericPrimitive2;
    private final Calendar cal = Calendar.getInstance();

    @Before
    public void setUp() {

       cal.set(1981, 6, 25);
       Date date = cal.getTime();

       temporalGeomericPrimitive1 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), date);
       temporalGeomericPrimitive2 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id2"), new Date());
    }

    @After
    public void tearDown() {
        temporalGeomericPrimitive1 = null;
        temporalGeomericPrimitive2 = null;
    }

    /**
     * Test of distance method, of class DefaultTemporalGeometricPrimitive.
     */
    @Test
    public void testDistance() {
        TemporalGeometricPrimitive other;

        //calcul Distance with instant objects
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 0, 1);
        other = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), cal.getTime());
        Duration result = temporalGeomericPrimitive1.distance(other);
        assertFalse(temporalGeomericPrimitive2.distance(other).equals(result));

        //calcul Distance with instant and period
        cal.set(2009, 1, 1);
        Instant i1 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), cal.getTime());
        cal.set(2012, 1, 1);
        Instant i2 = new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), cal.getTime());
        other = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "tp1"), i1, i2);
        result = temporalGeomericPrimitive1.distance(other);
        assertFalse(temporalGeomericPrimitive2.distance(other).equals(result));

        //calcul Distance between Period objects
        Period tp1 = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "tp1"), temporalGeomericPrimitive1, temporalGeomericPrimitive2);
        Period tp2 = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "tp2"), i1, temporalGeomericPrimitive2);
        result = tp1.distance(other);
        assertTrue(tp2.distance(other).equals(result));

    }

    /**
     * Test of length method, of class DefaultTemporalGeometricPrimitive.
     */
    @Test
    public void testLength() {
        Calendar cal = Calendar.getInstance();
        cal.set(2033, 0, 1);
        Period tp1 = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "tp1"), temporalGeomericPrimitive1, temporalGeomericPrimitive2);
        Period tp2 = new DefaultPeriod(Collections.singletonMap(NAME_KEY, "tp2"), temporalGeomericPrimitive2, new DefaultInstant(Collections.singletonMap(NAME_KEY, "id1"), cal.getTime()));
        Duration result = tp1.length();
        assertFalse(tp2.length().equals(result));
    }
}
