/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;
import static org.apache.sis.test.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the {@link Complex} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class ComplexTest {
    /**
     * Tolerance factor for comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * Raises the given complex to the given power and compares the result with
     * what we would get by multiplying the value by itself <var>n</var>-1 times.
     *
     * @param  value The complex value to raise to a power.
     * @param  n The power to raise the value at.
     * @param  label The label to display in case of failure.
     * @return The calculated value.
     */
    private static Complex power(final Complex value, final int n, final String label) {
        final Complex calculated = value.clone();
        calculated.power(value, n);
        if (n > 0) {
            // Multiply 'value' n times.
            final Complex byIteration = value.clone();
            for (int i=1; i<n; i++) {
                byIteration.multiply(byIteration, value);
            }
            assertEquals(label, byIteration.real, calculated.real, EPS);
            assertEquals(label, byIteration.imag, calculated.imag, EPS);

            // Divide 'calculated' n times.
            byIteration.copy(calculated);
            for (int i=1; i<n; i++) {
                byIteration.divide(byIteration, value);
            }
            if (value.real != 0 || value.imag != 0) {
                assertEquals(label, value.real, byIteration.real, EPS);
                assertEquals(label, value.imag, byIteration.imag, EPS);
            }
        }
        return calculated;
    }

    /**
     * Returns the real part of the given value.
     *
     * @param  value The complex value for which to get the real part.
     * @param  label The label to display in case of failure.
     * @return The real part.
     */
    private static double real(final Complex value, final String label) {
        assertEquals(label, 0, value.imag, EPS);
        return value.real;
    }

    /**
     * Tests {@link Complex#power(Complex, int)} method using real values only,
     * so we can compare with {@link StrictMath#pow(double, double)}.
     */
    @Test
    public void testRealPowers() {
        final double[] bases = {0, 1, 2, 2.5, -2.5};
        for (int i=0; i<bases.length; i++) {
            final double base = bases[i];
            final Complex value = new Complex(base, 0);
            for (int n=0; n<=6; n++) {
                final String label = String.valueOf(base) + '^' + n;
                assertEquals(label, pow(base, n), real(power(value, n, label), label), EPS);
            }
        }
    }

    /**
     * Tests {@link Complex#pow} method using complex values.
     */
    @Test
    public void testComplexPowers() {
        final double[] reals = {0, 1, 0, 2, 2.5, -2.5};
        final double[] imags = {0, 0, 1, 1, 1.5,  3.8};
        for (int i=0; i<reals.length; i++) {
            final Complex value = new Complex(reals[i], imags[i]);
            for (int n=0; n<=6; n++) {
                final String label = value.toString() + '^' + n;
                assertNotNull(label, power(value, n, label));
            }
        }
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        final Complex local = new Complex(5.6, 7.8);
        assertNotSame(local, assertSerializedEquals(local));
    }
}
