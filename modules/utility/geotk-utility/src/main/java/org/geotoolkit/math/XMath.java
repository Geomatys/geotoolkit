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

import org.geotoolkit.lang.Static;
import org.apache.sis.util.Numbers;
import org.geotoolkit.resources.Errors;


/**
 * Simple mathematical functions in addition to the ones provided in {@link Math}.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Thomas Rouby (Geomatys)
 * @version 3.20
 *
 * @since 1.0
 * @module
 */
public final class XMath extends Static {
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
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNSUPPORTED_DATA_TYPE_1, type));
        }
        if (down) {
            value = -value;
        }
        return value;
    }

    /**
     * Clamps a value between min value and max value.
     *
     * @param val the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static int clamp(int val, int min, int max) {
        return Math.min(Math.max(val, min), max);
    }

    /**
     * Clamps each value of an array between min value and max value.
     *
     * @param val the array of values to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static int[] clamp(int[] val, int min, int max) {
        final int[] ret = new int[val.length];
        for (int i=0; i<val.length; i++) {
            ret[i] = clamp(val[i], min, max);
        }
        return ret;
    }

    /**
     * Clamps a value between min value and max value.
     *
     * @param val the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static long clamp(long val, long min, long max) {
        return Math.min(Math.max(val, min), max);
    }

    /**
     * Clamps each value of an array between min value and max value.
     *
     * @param val the array of values to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static long[] clamp(long[] val, long min, long max) {
        final long[] ret = new long[val.length];
        for (int i=0; i<val.length; i++) {
            ret[i] = clamp(val[i], min, max);
        }
        return ret;
    }

    /**
     * Clamps a value between min value and max value.
     *
     * @param val the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static float clamp(float val, float min, float max) {
        return Math.min(Math.max(val, min), max);
    }

    /**
     * Clamps each value of an array between min value and max value.
     *
     * @param val the array of values to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static float[] clamp(float[] val, float min, float max) {
        final float[] ret = new float[val.length];
        for (int i=0; i<val.length; i++) {
            ret[i] = clamp(val[i], min, max);
        }
        return ret;
    }

    /**
     * Clamps a value between min value and max value.
     *
     * @param val the value to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static double clamp(double val, double min, double max) {
        return Math.min(Math.max(val, min), max);
    }

    /**
     * Clamps each value of an array between min value and max value.
     *
     * @param val the array of values to clamp
     * @param min the minimum value
     * @param max the maximum value
     * @return val clamped between min and max
     */
    public static double[] clamp(double[] val, double min, double max) {
        final double[] ret = new double[val.length];
        for (int i=0; i<val.length; i++) {
            ret[i] = clamp(val[i], min, max);
        }
        return ret;
    }
}
