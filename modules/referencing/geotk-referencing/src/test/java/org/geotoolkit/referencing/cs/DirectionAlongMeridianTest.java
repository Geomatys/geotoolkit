/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.cs;

import org.opengis.referencing.cs.AxisDirection;

import org.junit.*;
import org.geotoolkit.referencing.ReferencingTestCase;


/**
 * Tests the {@link DirectionAlongMeridian} class.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
public final class DirectionAlongMeridianTest extends ReferencingTestCase {
    /**
     * For floating point comparisons.
     */
    private static final double EPS = 1E-10;

    /**
     * Tests the {@link DirectionAlongMeridian#parse} method.
     */
    @Test
    public void testParse() {
        DirectionAlongMeridian dir;
        String name;

        name = "South along 180°";
        dir  = DirectionAlongMeridian.parse(name);
        assertNotNull(dir);
        assertEquals(AxisDirection.SOUTH, dir.baseDirection);
        assertEquals(180, dir.meridian, 0);
        assertEquals(name, dir.toString());
        assertEquals(dir, DirectionAlongMeridian.parse("South along 180 deg"));

        name = "South along 90°E";
        dir  = DirectionAlongMeridian.parse(name);
        assertNotNull(dir);
        assertEquals(AxisDirection.SOUTH, dir.baseDirection);
        assertEquals(90, dir.meridian, 0);
        assertEquals(name, dir.toString());
        assertEquals(dir, DirectionAlongMeridian.parse("South along 90 deg East"));

        name = "South along 90°W";
        dir  = DirectionAlongMeridian.parse(name);
        assertNotNull(dir);
        assertEquals(AxisDirection.SOUTH, dir.baseDirection);
        assertEquals(-90, dir.meridian, 0);
        assertEquals(name, dir.toString());
        assertEquals(dir, DirectionAlongMeridian.parse("South along 90 deg West"));

        name = "North along 45°E";
        dir  = DirectionAlongMeridian.parse(name);
        assertNotNull(dir);
        assertEquals(AxisDirection.NORTH, dir.baseDirection);
        assertEquals(45, dir.meridian, 0);
        assertEquals(name, dir.toString());
        assertEquals(dir, DirectionAlongMeridian.parse("North along 45 deg East"));
    }

    /**
     * Tests the ordering, which also involve a test of angle measurement.
     */
    @Test
    public void testOrdering() {
        assertOrdered("North along  90 deg East",   "North along   0 deg");
        assertOrdered("North along  75 deg West",   "North along 165 deg West");
        assertOrdered("South along  90 deg West",   "South along   0 deg");
        assertOrdered("South along 180 deg",        "South along  90 deg West");
        assertOrdered("North along 130 deg West",   "North along 140 deg East");
    }

    /**
     * Tests if the following directions have an angle of 90° between each other.
     */
    private static void assertOrdered(final String dir1, final String dir2) {
        final DirectionAlongMeridian m1 = DirectionAlongMeridian.parse(dir1);
        final DirectionAlongMeridian m2 = DirectionAlongMeridian.parse(dir2);
        assertEquals(+90, m1.getAngle(m2), EPS);
        assertEquals(-90, m2.getAngle(m1), EPS);
        assertEquals( -1, m1.compareTo(m2));
        assertEquals( +1, m2.compareTo(m1));
        assertFalse (m1.equals(m2));
    }
}
