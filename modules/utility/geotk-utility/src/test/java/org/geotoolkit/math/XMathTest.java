/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.math;

import org.junit.Test;

import static org.junit.Assert.*;
import static java.lang.StrictMath.*;
import static org.geotoolkit.math.XMath.roll;


/**
 * Tests the {@link XMath} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class XMathTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests the {@link XMath#roll} method.
     */
    @Test
    public void testRollLongitude() {
        final double tolerance = 1E-10;
        assertEquals(  30, toDegrees(roll(toRadians(  30), PI)), tolerance);
        assertEquals( 179, toDegrees(roll(toRadians( 179), PI)), tolerance);
        assertEquals(-179, toDegrees(roll(toRadians( 181), PI)), tolerance);
        assertEquals( -90, toDegrees(roll(toRadians( 270), PI)), tolerance);
        assertEquals(   2, toDegrees(roll(toRadians( 362), PI)), tolerance);
        assertEquals( -30, toDegrees(roll(toRadians( -30), PI)), tolerance);
        assertEquals(-179, toDegrees(roll(toRadians(-179), PI)), tolerance);
        assertEquals( 178, toDegrees(roll(toRadians(-182), PI)), tolerance);
        assertEquals(  90, toDegrees(roll(toRadians(-270), PI)), tolerance);
        assertEquals(  -5, toDegrees(roll(toRadians(-365), PI)), tolerance);

        assertEquals(  30, roll(  30, 180), tolerance);
        assertEquals( 178, roll(-182, 180), tolerance);
        assertEquals(  -5, roll(-365, 180), tolerance);
        assertEquals(-365, roll(-365, Double.POSITIVE_INFINITY), tolerance);
    }
}
