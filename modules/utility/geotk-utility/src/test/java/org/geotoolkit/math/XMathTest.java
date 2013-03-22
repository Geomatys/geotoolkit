/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import static org.geotoolkit.math.XMath.*;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link XMath} static methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 2.5
 */
public final strictfp class XMathTest {
    /**
     * Small number for floating point comparisons.
     */
    private static final double EPS = 1E-12;

    /**
     * Tests the {@link XMath#primeNumber} method.
     */
    @Test
    public void testPrimeNumber() {
        final int[] primes = {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53,
            59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113
        };
        for (int i=0; i<primes.length; i++) {
            assertEquals(primes[i], primeNumber(i));
        }
    }
}
