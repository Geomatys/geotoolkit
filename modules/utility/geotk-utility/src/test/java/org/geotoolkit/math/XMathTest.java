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

import org.apache.sis.util.ArraysExt;
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
     * Tests the {@link XMath#magnitude} method.
     *
     * @since 3.09
     */
    @Test
    public void testMagnitude() {
        assertEquals(0, XMath.magnitude(), EPS);
        assertEquals(4, XMath.magnitude(0, -4, 0), EPS);
        assertEquals(5, XMath.magnitude(0, -4, 0, 3, 0), EPS);
        assertEquals(5, XMath.magnitude(3, 1, -2, 1, -3, -1), EPS);
    }

    /**
     * Tests the {@link XMath#pow10} method.
     */
    @Test
    public void testPow10() {
        for (int i=-304; i<=304; i++) {
            assertEquals(Double.parseDouble("1E"+i), pow10(i), 0);
        }
    }

    /**
     * Tests the {@link XMath#atanh(double)} method in the [-1 … +1] range.
     *
     * @since 3.20
     */
    @Test
    public void testAtanh() {
        for (int i=-10; i<=10; i++) {
            final double x = 0.1 * i;
            final double y = atanh(x);
            switch (i) {
                case -10: assertEquals(Double.NEGATIVE_INFINITY, y, EPS); break;
                default:  assertEquals(x, Math.tanh(y),             EPS); break;
                case +10: assertEquals(Double.POSITIVE_INFINITY, y, EPS); break;
            }
        }
    }

    /**
     * Tests the {@link XMath#xorSign} method.
     */
    @Test
    public void testXorSign() {
        assertEquals( 10, xorSign( 10,  0.5), 0);
        assertEquals(-10, xorSign(-10,  0.5), 0);
        assertEquals( 10, xorSign(-10, -0.5), 0);
        assertEquals(-10, xorSign( 10, -0.5), 0);
    }

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

    /**
     * Tests the {@link XMath#divisors} method.
     */
    @Test
    public void testDivisors() {
        for (int i=0; i<10000; i++) {
            final int[] divisors = divisors(i);
            assertTrue(ArraysExt.isSorted(divisors, true));
            for (int j=0; j<divisors.length; j++) {
                assertEquals(0, i % divisors[j]);
            }
            if (i == 0){
                assertEquals(0, divisors.length);
            } else {
                assertEquals(1, divisors[0]);
                assertEquals(i, divisors[divisors.length - 1]);
            }
        }
        assertArrayEquals(new int[] {
            1, 2, 4, 5, 8, 10, 16, 20, 25, 40, 50, 80, 100, 125, 200, 250, 400, 500, 1000, 2000
        }, divisors(2000));

        assertArrayEquals(new int[] {
            1, 61, 71, 4331
        }, divisors(4331));

        assertArrayEquals(new int[] {
            1, 2, 3, 4, 5, 6, 8, 10, 12, 13, 15, 20, 24, 25, 26, 30, 39, 40, 50, 52, 60, 65, 75,
            78, 100, 104, 120, 130, 150, 156, 195, 200, 260, 300, 312, 325, 390, 520, 600, 650,
            780, 975, 1300, 1560, 1950, 2600, 3900, 7800
        }, divisors(7800));
    }

    /**
     * Tests the {@link XMath#commonDivisors} method.
     *
     * @since 3.15
     */
    @Test
    public void testCommonDivisors() {
        assertArrayEquals(new int[] {
            1, 5
        }, commonDivisors(2000, 15));
    }
}
