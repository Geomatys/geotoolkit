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

import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.DefaultInstant;
import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.temporal.Duration;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalGeometricPrimitive;


/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module
 */
public class DefaultTemporalGeometricPrimitiveTest extends org.geotoolkit.test.TestBase {

    private TemporalGeometricPrimitive temporalGeomericPrimitive1;
    private TemporalGeometricPrimitive temporalGeomericPrimitive2;
    private Calendar cal = Calendar.getInstance();

    @Before
    public void setUp() {

//        cal.set(1981, 6, 25);
//        Date date = cal.getTime();
//
//        position1 = new DefaultPosition(date);
//        position2 = new DefaultPosition(new Date());
//        temporalGeomericPrimitive1 = new DefaultInstant(position1);
//        temporalGeomericPrimitive2 = new DefaultInstant(position2);
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

//        //calcul Distance with instant objects
//        Calendar cal = Calendar.getInstance();
//        cal.set(2000, 0, 1);
//        Position position = new DefaultPosition(cal.getTime());
//        other = new DefaultInstant(position);
//        Duration result = temporalGeomericPrimitive1.distance(other);
//        assertFalse(temporalGeomericPrimitive2.distance(other).equals(result));
//
//        //calcul Distance with instant and period
//        cal.set(2009, 1, 1);
//        Instant i1 = new DefaultInstant(new DefaultPosition(cal.getTime()));
//        cal.set(2012, 1, 1);
//        Instant i2 = new DefaultInstant(new DefaultPosition(cal.getTime()));
//        other = new DefaultPeriod(i1, i2);
//        result = temporalGeomericPrimitive1.distance(other);
//        assertFalse(temporalGeomericPrimitive2.distance(other).equals(result));
//
//        //calcul Distance between Period objects
//        temporalGeomericPrimitive1 = new DefaultPeriod(new DefaultInstant(position1), new DefaultInstant(position2));
//        temporalGeomericPrimitive2 = new DefaultPeriod(i1, new DefaultInstant(position2));
//        result = temporalGeomericPrimitive1.distance(other);
//        assertTrue(temporalGeomericPrimitive2.distance(other).equals(result));

    }

    /**
     * Test of length method, of class DefaultTemporalGeometricPrimitive.
     */
    @Test
    public void testLength() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(2033, 0, 1);
//        temporalGeomericPrimitive1 = new DefaultPeriod(new DefaultInstant(position1), new DefaultInstant(position2));
//        temporalGeomericPrimitive2 = new DefaultPeriod(new DefaultInstant(position2), new DefaultInstant(new DefaultPosition(cal.getTime())));
//        Duration result = temporalGeomericPrimitive1.length();
//        assertFalse(temporalGeomericPrimitive2.length().equals(result));
    }
}
