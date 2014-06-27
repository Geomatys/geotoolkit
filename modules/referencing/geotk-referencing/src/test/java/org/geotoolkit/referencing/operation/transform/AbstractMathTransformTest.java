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
package org.geotoolkit.referencing.operation.transform;

import org.junit.Test;

import static org.junit.Assert.*;
import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.transform.AbstractMathTransform.rollLongitude;


/**
 * Tests the {@link AbstractMathTransform} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 4.00
 *
 * @since 3.00
 */
public final strictfp class AbstractMathTransformTest {
    /**
     * Tests the {@link AbstractMathTransform#rollLongitude} method.
     */
    @Test
    public void testRollLongitude() {
        final double tolerance = 1E-10;
        assertEquals(  30, toDegrees(rollLongitude(toRadians(  30), PI)), tolerance);
        assertEquals( 179, toDegrees(rollLongitude(toRadians( 179), PI)), tolerance);
        assertEquals(-179, toDegrees(rollLongitude(toRadians( 181), PI)), tolerance);
        assertEquals( -90, toDegrees(rollLongitude(toRadians( 270), PI)), tolerance);
        assertEquals(   2, toDegrees(rollLongitude(toRadians( 362), PI)), tolerance);
        assertEquals( -30, toDegrees(rollLongitude(toRadians( -30), PI)), tolerance);
        assertEquals(-179, toDegrees(rollLongitude(toRadians(-179), PI)), tolerance);
        assertEquals( 178, toDegrees(rollLongitude(toRadians(-182), PI)), tolerance);
        assertEquals(  90, toDegrees(rollLongitude(toRadians(-270), PI)), tolerance);
        assertEquals(  -5, toDegrees(rollLongitude(toRadians(-365), PI)), tolerance);

        assertEquals(  30, rollLongitude(  30, 180), tolerance);
        assertEquals( 178, rollLongitude(-182, 180), tolerance);
        assertEquals(  -5, rollLongitude(-365, 180), tolerance);
        assertEquals(-365, rollLongitude(-365, Double.POSITIVE_INFINITY), tolerance);
    }
}
