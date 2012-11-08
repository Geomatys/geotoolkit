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
import org.geotoolkit.lang.Workaround;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.util.converter.Numbers;
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
     */
    public static final double SQRT2 = 1.4142135623730951;

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
     */
    public static final long SIGN_BIT_MASK = Long.MIN_VALUE;

    /**
     * Table of some integer powers of 10. Used for fast computation of {@link #pow10(int)}.
     */
    private static final double[] POW10 = {
        1E+00, 1E+01, 1E+02, 1E+03, 1E+04, 1E+05, 1E+06, 1E+07, 1E+08, 1E+09,
        1E+10, 1E+11, 1E+12, 1E+13, 1E+14, 1E+15, 1E+16, 1E+17, 1E+18, 1E+19,
        1E+20, 1E+21, 1E+22
        // Do not add more elements, unless we verified that 1/x is accurate.
        // Last time we tried, it was not accurate anymore starting at 1E+23.
    };

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
     * Small tolerance factor for working around floating point rounding errors.
     */
    private static final double EPS = 1E-6;

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
     */
    public static double magnitude(final double... vector) {
        int i = vector.length;

        // If every elements in the array are zero, returns zero.
        double sum;
        do if (i == 0) return 0;
        while ((sum = vector[--i]) == 0);

        // We have found a non-zero element. If it is the only one, returns it directly.
        double v;
        do if (i == 0) return Math.abs(sum);
        while ((v = vector[--i]) == 0);

        // If there is exactly 2 elements, use Math.hypot which is more robust than our algorithm.
        double v2;
        do if (i == 0) return Math.hypot(sum, v);
        while ((v2 = vector[--i]) == 0);

        // Usual magnitude computation.
        sum = sum*sum + v*v + v2*v2;
        while (i != 0) {
            v = vector[--i];
            sum += v*v;
        }
        return Math.sqrt(sum);
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
     */
    public static double pow10(final double x) {
        final int ix = (int) x;
        if (ix == x) {
            return pow10(ix);
        } else {
            return Math.pow(10, x);
        }
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
     */
    @Workaround(library="JDK", version="1.4")
    public static strictfp double pow10(final int x) {
        if (x >= 0) {
            if (x < POW10.length) {
                return POW10[x];
            }
        } else if (x != Integer.MIN_VALUE) {
            final int nx = -x;
            if (nx < POW10.length) {
                return 1 / POW10[nx];
            }
        }
        try {
            /*
             * Double.parseDouble("1E"+x) gives as good or better numbers than Math.pow(10,x)
             * for ALL integer powers, but is slower. We hope that the current workaround is only
             * temporary. See http://developer.java.sun.com/developer/bugParade/bugs/4358794.html
             */
            return Double.parseDouble("1E" + x);
        } catch (NumberFormatException exception) {
            return StrictMath.pow(10, x);
        }
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
     */
    public static double atanh(final double x) {
        /*
         * The classical formulas is log((1+x)/(1-x))/2, but the following is more
         * accurate if the (1+x)/(1-x) ratio is close to 1, i.e. if x is close to 0.
         */
        return 0.5 * Math.log1p(2*x / (1-x));
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
     */
    public static boolean isPositive(final double value) {
        return (Double.doubleToRawLongBits(value) & SIGN_BIT_MASK) == 0 && !Double.isNaN(value);
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
     */
    public static boolean isNegative(final double value) {
        return (Double.doubleToRawLongBits(value) & SIGN_BIT_MASK) != 0 && !Double.isNaN(value);
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
     */
    public static boolean isSameSign(final double v1, final double v2) {
        return !Double.isNaN(v1) && !Double.isNaN(v2) &&
                ((Double.doubleToRawLongBits(v1) ^ Double.doubleToRawLongBits(v2)) & SIGN_BIT_MASK) == 0;
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
     */
    public static int sgn(final double x) {
        if (x > 0) return +1;
        if (x < 0) return -1;
        else       return  0;
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
     */
    public static int sgn(final float x) {
        if (x > 0) return +1;
        if (x < 0) return -1;
        else       return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     */
    public static int sgn(long x) {
        if (x > 0) return +1;
        if (x < 0) return -1;
        else       return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     */
    public static int sgn(int x) {
        if (x > 0) return +1;
        if (x < 0) return -1;
        else       return  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     */
    public static short sgn(short x) {
        if (x > 0) return (short) +1;
        if (x < 0) return (short) -1;
        else       return (short)  0;
    }

    /**
     * Returns the sign of <var>x</var>. This method returns
     *    -1 if <var>x</var> is negative,
     *     0 if <var>x</var> is zero and
     *    +1 if <var>x</var> is positive.
     *
     * @param x The number from which to get the sign.
     * @return {@code +1} if <var>x</var> is positive, {@code -1} if negative, or 0 otherwise.
     */
    public static byte sgn(byte x) {
        if (x > 0) return (byte) +1;
        if (x < 0) return (byte) -1;
        else       return (byte)  0;
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
     */
    public static double xorSign(final double value, final double sign) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(value) ^
                (Double.doubleToRawLongBits(sign) & SIGN_BIT_MASK));
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
     */
    public static float toNaN(int index) throws IndexOutOfBoundsException {
        index += 0x200000;
        if (index<0 || index>0x3FFFFF) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        final float value = Float.intBitsToFloat(0x7FC00000 + index);
        assert Float.isNaN(value) : value;
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
     */
    public static int[] divisors(int number) {
        if (number == 0) {
            return XArrays.EMPTY_INT;
        }
        number = Math.abs(number);
        int[] divisors = new int[16];
        divisors[0] = 1;
        int count = 1;
        /*
         * Searches for the first divisors among the prime numbers. We stop the search at the
         * square root of 'n' because every values above that point can be inferred from the
         * values before that point, i.e. if n=p1*p2 and p2 is greater than 'sqrt', than p1
         * most be lower than 'sqrt'.
         */
        final int sqrt = (int) (Math.sqrt(number) + EPS); // Really wants rounding toward 0.
        for (int p,i=0; (p=primeNumber(i)) <= sqrt; i++) {
            if (number % p == 0) {
                if (count == divisors.length) {
                    divisors = Arrays.copyOf(divisors, count*2);
                }
                divisors[count++] = p;
            }
        }
        /*
         * Completes the divisors past 'sqrt'. The numbers added here may or may not be prime
         * numbers. Side note: checking that they are prime numbers would be costly, but this
         * algorithm doesn't need that.
         */
        int source = count;
        if (count*2 > divisors.length) {
            divisors = Arrays.copyOf(divisors, count*2);
        }
        int d1 = divisors[--source];
        int d2 = number / d1;
        if (d1 != d2) {
            divisors[count++] = d2;
        }
        while (--source >= 0) {
            divisors[count++] = number / divisors[source];
        }
        /*
         * Checks the products of divisors found so far. For example if 2 and 3 are divisors,
         * checks if 6 is a divisor as well. The products found will themself be used for
         * computing new products.
         */
        for (int i=1; i<count; i++) {
            d1 = divisors[i];
            for (int j=i; j<count; j++) {
                d2 = d1 * divisors[j];
                if (number % d2 == 0) {
                    int p = Arrays.binarySearch(divisors, j, count, d2);
                    if (p < 0) {
                        p = ~p; // ~ operator, not minus
                        if (count == divisors.length) {
                            divisors = Arrays.copyOf(divisors, count*2);
                        }
                        System.arraycopy(divisors, p, divisors, p+1, count-p);
                        divisors[p] = d2;
                        count++;
                    }
                }
            }
        }
        divisors = XArrays.resize(divisors, count);
        assert XArrays.isSorted(divisors, true);
        return divisors;
    }

    /**
     * Returns the divisors which are common to all the specified numbers.
     *
     * @param  numbers The numbers for which to compute the divisors.
     * @return The divisors common to all the given numbers, in strictly increasing order.
     *
     * @since 3.15
     */
    public static int[] commonDivisors(final int... numbers) {
        if (numbers.length == 0) {
            return XArrays.EMPTY_INT;
        }
        /*
         * Get the smallest value. We will compute the divisors only for this value,
         * since we know that any value greater that the minimal value can not be a
         * common divisor.
         */
        int minValue = Integer.MAX_VALUE;
        for (int i=0; i<numbers.length; i++) {
            final int n = Math.abs(numbers[i]);
            if (n <= minValue) {
                minValue = n;
            }
        }
        int[] divisors = divisors(minValue);
        /*
         * Tests if the divisors we just found are also divisors of all other numbers.
         * Removes those which are not.
         */
        int count = divisors.length;
        for (int i=0; i<numbers.length; i++) {
            final int n = Math.abs(numbers[i]);
            if (n != minValue) {
                for (int j=count; --j>0;) { // Do not test j==0, since divisors[0] ==  1.
                    if (n % divisors[j] != 0) {
                        System.arraycopy(divisors, j+1, divisors, j, --count - j);
                    }
                }
            }
        }
        return XArrays.resize(divisors, count);
    }
}
