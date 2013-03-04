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
import org.apache.sis.math.MathFunctions;
import org.geotoolkit.lang.Static;
import org.geotoolkit.lang.Workaround;
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
     * The square root of 2, which is {@value}.
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#SQRT_2}.
     */
    @Deprecated
    public static final double SQRT2 = MathFunctions.SQRT_2;

    /**
     * Bit mask to isolate the sign bit of non-{@link Double#NaN NaN} values in a {@code double}.
     * For any value other than {@code NaN}, the following code evaluate to 0 if the given value
     * is positive:
     *
     * {@preformat java
     *     Double.doubleToRawLongBits(value) & SIGN_BIT_MASK;
     * }
     *
     * Note that this idiom differentiates positive zero from negative zero. It should be used
     * only when such difference matter.
     *
     * @see #isPositive(double)
     * @see #isNegative(double)
     *
     * @since 3.20
     *
     * @deprecated No replacement (this is considered internal mechanic).
     */
    @Deprecated
    public static final long SIGN_BIT_MASK = org.apache.sis.internal.util.Utilities.SIGN_BIT_MASK;

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
     * Returns the magnitude of the given vector. This is defined by:
     *
     * {@preformat math
     *     sqrt(vector[0]² + vector[1]² + … + vector[length-1]²)
     * }
     *
     * {@section Implementation note}
     * In the special case where only one element is different than zero, this method
     * returns directly the {@linkplain Math#abs(double) absolute value} of that element
     * without computing {@code sqrt(v²)}, in order to avoid rounding error. This special case
     * has been implemented because this method is often invoked for computing the length of
     * {@linkplain org.opengis.coverage.grid.RectifiedGrid#getOffsetVectors() offset vectors},
     * typically aligned with the axes of a {@linkplain org.opengis.referencing.cs.CartesianCS
     * Cartesian coordinate system}.
     *
     * @param  vector The vector for which to compute the magnitude.
     * @return The magnitude of the given vector.
     *
     * @see Math#hypot(double, double)
     *
     * @since 3.09
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#magnitude(double[])}.
     */
    @Deprecated
    public static double magnitude(final double... vector) {
        return MathFunctions.magnitude(vector);
    }

    /**
     * Computes 10 raised to the power of <var>x</var>. This method delegates to
     * <code>{@linkplain #pow10(int) pow10}((int) x)</code> if <var>x</var> is an
     * integer, or to <code>{@linkplain Math#pow(double, double) Math.pow}(10, x)</code>
     * otherwise.
     *
     * @param x The exponent.
     * @return 10 raised to the given exponent.
     *
     * @see #pow10(int)
     * @see Math#pow(double, double)
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#pow10(double)}.
     */
    @Deprecated
    public static double pow10(final double x) {
        return MathFunctions.pow10(x);
    }

    /**
     * Computes 10 raised to the power of <var>x</var>. This method tries to be slightly more
     * accurate than <code>{@linkplain Math#pow Math.pow}(10,x)</code>, sometime at the cost
     * of performance.
     * <p>
     * The {@code Math.pow(10,x)} method doesn't always return the closest IEEE floating point
     * representation. More accurate calculations are slower and usually not necessary, but the
     * base 10 is a special case since it is used for scaling axes or formatting human-readable
     * output, in which case the precision may matter.
     *
     * @param x The exponent.
     * @return 10 raised to the given exponent.
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#pow10(int)}.
     */
    @Deprecated
    @Workaround(library="JDK", version="1.4")
    public static strictfp double pow10(final int x) {
        return MathFunctions.pow10(x);
    }

    /**
     * Returns the inverse hyperbolic tangent of the given value.
     * This is the inverse of the {@linkplain Math#tanh(double) tanh} method.
     * The range of input values is [-1…1]. Special cases:
     * <p>
     * </ul>
     *   <li>For <var>x</var> = NaN, this method returns {@linkplain Double#NaN NaN}.</li>
     *   <li>For <var>x</var> = -1, this method returns {@linkplain Double#NEGATIVE_INFINITY negative infinity}.</li>
     *   <li>For <var>x</var> = +1, this method returns {@linkplain Double#POSITIVE_INFINITY positive infinity}.</li>
     * </ul>
     *
     * @param  x The value for which to compute the inverse hyperbolic tangent.
     * @return The inverse hyperbolic tangent of the given value.
     *
     * @see Math#tanh(double)
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#atanh(double)}.
     */
    @Deprecated
    public static double atanh(final double x) {
        return MathFunctions.atanh(x);
    }

    /**
     * Returns {@code true} if the given value is positive, <em>excluding</em> negative zero.
     * Special cases:
     * <p>
     * <ul>
     *   <li>If the value is {@code +0.0}, returns {@code true}</li>
     *   <li>If the value is {@code -0.0}, returns <b>{@code false}</b></li>
     *   <li>If the value is {@link Double#NaN NaN}, returns {@code false}</li>
     * </ul>
     * <p>
     * As seen from the above cases, this method distinguishes positive zero from negative zero.
     * The handling of zero values is the difference between invoking {@code isPositive(double)}
     * and testing if (<var>value</var> &gt;= 0).
     *
     * @param  value The value to test.
     * @return {@code true} if the given value is positive, excluding negative zero.
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#isPositive(double)}.
     */
    @Deprecated
    public static boolean isPositive(final double value) {
        return MathFunctions.isPositive(value);
    }

    /**
     * Returns {@code true} if the given value is negative, <em>including</em> negative zero.
     * Special cases:
     * <p>
     * <ul>
     *   <li>If the value is {@code +0.0}, returns {@code false}</li>
     *   <li>If the value is {@code -0.0}, returns <b>{@code true}</b></li>
     *   <li>If the value is {@link Double#NaN NaN}, returns {@code false}</li>
     * </ul>
     * <p>
     * As seen from the above cases, this method distinguishes positive zero from negative zero.
     * The handling of zero values is the difference between invoking {@code isNegative(double)}
     * and testing if (<var>value</var> &lt; 0).
     *
     * @param  value The value to test.
     * @return {@code true} if the given value is negative, including negative zero.
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#isNegative(double)}.
     */
    @Deprecated
    public static boolean isNegative(final double value) {
        return MathFunctions.isNegative(value);
    }

    /**
     * Returns {@code true} if the given values have the same sign, differentiating positive
     * and negative zeros. Special cases:
     * <p>
     * <ul>
     *   <li>{@code +0.0} and {@code -0.0} are considered to have opposite sign</li>
     *   <li>If any value is {@link Double#NaN NaN}, returns {@code false}</li>
     * </ul>
     * <p>
     *
     * @param  v1 The first value.
     * @param  v2 The second value, to compare the sign with the first value.
     * @return {@code true} if the given values are not NaN and have the same sign.
     *
     * @see Math#signum(double)
     *
     * @since 3.20
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#isSameSign(double, double)}.
     */
    @Deprecated
    public static boolean isSameSign(final double v1, final double v2) {
        return MathFunctions.isSameSign(v1, v2);
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero or {@code NaN} and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     *
     * @see Math#signum(double)
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#sgn(double)}.
     */
    @Deprecated
    public static int sgn(final double x) {
        return MathFunctions.sgn(x);
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero or {@code NaN} and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     *
     * @see Math#signum(float)
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#sgn(float)}.
     */
    @Deprecated
    public static int sgn(final float x) {
        return MathFunctions.sgn(x);
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#sgn(long)}.
     */
    @Deprecated
    public static int sgn(long x) {
        return MathFunctions.sgn(x);
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#sgn(int)}.
     */
    @Deprecated
    public static int sgn(int x) {
        return MathFunctions.sgn(x);
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#sgn(short)}.
     */
    @Deprecated
    public static short sgn(short x) {
        return MathFunctions.sgn(x);
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#sgn(byte)}.
     */
    @Deprecated
    public static byte sgn(byte x) {
        return MathFunctions.sgn(x);
    }

    /**
     * Returns the first floating-point argument with the sign reversed if the second floating-point
     * argument is negative. This method is similar to <code>{@linkplain Math#copySign(double,double)
     * Math.copySign}(value, sign)</code> except that the sign is combined with an <cite>exclusive
     * or</cite> operation instead than being copied.
     * <p>
     * This method computes the same result than the formula below (using only standard functions
     * from {@link Math}) except that zeros and {@link Double#NaN} values for the {@code sign}
     * argument are treated as a positive or negative numbers.
     *
     * {@preformat java
     *     return magnitude * signum(sign);
     * }
     *
     * @param  value The parameter providing the value that may need a sign change.
     * @param  sign The parameter providing the sign to <cite>xor</cite> with the value.
     * @return The provided value with its sign reversed if the {@code sign} parameter is negative.
     *
     * @see Math#copySign(double, double)
     *
     * @since 3.00
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#xorSign(double, double)}.
     */
    @Deprecated
    public static double xorSign(final double value, final double sign) {
        return MathFunctions.xorSign(value, sign);
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
     * Returns a {@link Float#NaN NaN} number for the specified index. Valid NaN numbers have
     * bit fields ranging from {@code 0x7f800001} through {@code 0x7fffffff} or {@code 0xff800001}
     * through {@code 0xffffffff}. The standard {@link Float#NaN} has bit fields {@code 0x7fc00000}.
     * See {@link Float#intBitsToFloat} for more details on NaN bit values.
     * <p>
     * <b>Tip:</b> if the caller needs to ensure that the index is never out of bounds, he can
     * set the parameter value to {@code index % 0x200000}.
     *
     * @param  index The index, from -2097152 to 2097151 inclusive.
     * @return One of the legal {@link Float#NaN NaN} values as a float.
     * @throws IndexOutOfBoundsException if the specified index is out of bounds.
     *
     * @see Float#intBitsToFloat
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#toNanFloat(int)}.
     */
    @Deprecated
    public static float toNaN(int index) throws IndexOutOfBoundsException {
        return MathFunctions.toNanFloat(index);
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

    /**
     * Returns the divisors of the specified number as positive integers. For any value other
     * than {@code O} (which returns an empty array), the first element in the returned array
     * is always {@code 1} and the last element is always the absolute value of {@code number}.
     *
     * @param number The number for which to compute the divisors.
     * @return The divisors in strictly increasing order.
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#divisors(int)}.
     */
    @Deprecated
    public static int[] divisors(int number) {
        return MathFunctions.divisors(number);
    }

    /**
     * Returns the divisors which are common to all the specified numbers.
     *
     * @param  numbers The numbers for which to compute the divisors.
     * @return The divisors common to all the given numbers, in strictly increasing order.
     *
     * @since 3.15
     *
     * @deprecated Moved to Apache SIS {@link MathFunctions#commonDivisors(int[])}.
     */
    @Deprecated
    public static int[] commonDivisors(final int... numbers) {
        return MathFunctions.commonDivisors(numbers);
    }
}
