/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.Numbers;
import org.geotoolkit.resources.Errors;


/**
 * Simple mathematical functions in addition to the ones provided in {@link Math}.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.0
 * @module
 */
public final class XMath extends Static {
    /**
     * The sequence of prime numbers computed so far. Will be expanded as needed.
     * We limit ourself to 16 bits numbers because they are sufficient for computing
     * divisors of any 32 bits number.
     */
    private static short[] primes = new short[] {2, 3};

    /**
     * Maximum length allowed for the {@link #primes} array. This is the index
     * of the first prime number that can not be stored as 16 bits unsigned.
     */
    private static final int MAX_PRIMES_LENGTH = 6542;

    /**
     * Do not allow instantiation of this class.
     */
    private XMath() {
    }

    /**
     * Returns the number adjacent to the given value, as one of the nearest representable numbers
     * of the given type. First this method selects the nearest adjacent value in the direction of
     * positive infinity if {@code amount} is positive, or in the direction of negative infinity if
     * {@code amount} is negative. Then this operation is repeated as many time as the absolute value
     * of {@code amount}. More specifically:
     *
     * <ul>
     *   <li><p>If {@code type} is an integer type ({@link Integer}, {@link Short}, <i>etc.</i>),
     *       then this method returns {@code value + amount}. If {@code value} had a fractional part,
     *       then this part is truncated before the addition is performed.</p></li>
     *
     *   <li><p>If {@code type} is {@link Double}, then this method is equivalent to invoking
     *       <code>{@linkplain Math#nextUp(double) Math.nextUp}(value)</code> if {@code amount}
     *       is positive, or {@code -Math.nextUp(-value)} if {@code amount} is negative, and to
     *       repeat this operation {@code abs(amount)} times.</p></li>
     *
     *   <li><p>If {@code type} is {@link Float}, then this method is equivalent to invoking
     *       <code>{@linkplain Math#nextUp(float) Math.nextUp}((float) value)</code> if {@code amount}
     *       is positive, or {@code -Math.nextUp((float) -value)} if {@code amount} is negative,
     *       and to repeat this operation {@code abs(amount)} times.</p></li>
     * </ul>
     *
     * @param type    The type. Should be the class of {@link Double}, {@link Float},
     *                {@link Long}, {@link Integer}, {@link Short} or {@link Byte}.
     * @param value   The number for which to find an adjacent number.
     * @param amount  -1 to return the previous representable number,
     *                +1 to return the next representable number,
     *                or a multiple of the above.
     * @return One of previous or next representable number as a {@code double}.
     * @throws IllegalArgumentException if {@code type} is not one of supported types.
     */
    public static double adjacentForType(final Class<? extends Number> type, double value, int amount)
            throws IllegalArgumentException
    {
        if (Numbers.isInteger(type)) {
            if (amount == 0) {
                return Math.rint(value);
            } else if (amount > 0) {
                value = Math.floor(value);
            } else {
                value = Math.ceil(value);
            }
            return value + amount;
        }
        final boolean down = amount < 0;
        if (down) {
            amount = -amount;
            value  = -value;
        }
        if (type == Double.class) {
            while (--amount >= 0) {
                value = Math.nextUp(value);
            }
        } else if (type == Float.class) {
            float vf = (float) value;
            while (--amount >= 0) {
                vf = Math.nextUp(vf);
            }
            value = vf;
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNSUPPORTED_DATA_TYPE_$1, type));
        }
        if (down) {
            value = -value;
        }
        return value;
    }

    /**
     * Returns the <var>i</var><sup>th</sup> prime number. This method returns (2, 3, 5, 7, 11...)
     * for index (0, 1, 2, 3, 4, ...). This method is designed for relatively small prime numbers
     * only; don't use it for large values.
     *
     * @param  index The prime number index, starting at index 0 for prime number 2.
     * @return The prime number at the specified index.
     * @throws IndexOutOfBoundsException if the specified index is too large.
     *
     * @ess MathFunctions#nextPrimeNumber(int)
     * @see java.math.BigInteger#isProbablePrime
     */
    public static synchronized int primeNumber(final int index) throws IndexOutOfBoundsException {
        // 6541 is the largest index returning a 16 bits unsigned prime number.
        if (index < 0 || index >= MAX_PRIMES_LENGTH) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        short[] primes = XMath.primes;
        if (index >= primes.length) {
            int i = primes.length;
            int n = primes[i - 1] & 0xFFFF;
            primes = Arrays.copyOf(primes, Math.min((index | 0xF) + 1, MAX_PRIMES_LENGTH));
            do {
next:           while (true) {
                    n += 2;
                    for (int j=1; j<i; j++) {
                        if (n % (primes[j] & 0xFFFF) == 0) {
                            continue next;
                        }
                        // We could stop the search at the first value greater than sqrt(n), but
                        // given that the array is relatively short (because we limit ourself to
                        // 16 bits prime numbers), it probably doesn't worth.
                    }
                    assert n < 0xFFFF : i;
                    primes[i] = (short) n;
                    break;
                }
            } while (++i < primes.length);
            XMath.primes = primes;
        }
        return primes[index] & 0xFFFF;
    }
}
