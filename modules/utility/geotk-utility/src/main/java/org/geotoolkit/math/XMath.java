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

    /**
     * Clamps each value of an array between min value and max value.
     *
     * @param val the array of values to clamp, values are modified
     * @param min the minimum value
     * @param max the maximum value
     */
    public static void applyClamp(double[] val, double min, double max) {
        switch (val.length) {
            case 4 :
                val[3] = clamp(val[3], min, max);
            case 3 :
                val[2] = clamp(val[2], min, max);
            case 2 :
                val[1] = clamp(val[1], min, max);
            case 1 :
                val[0] = clamp(val[0], min, max);
                break;
            default :
                for (int i=0; i<val.length; i++) {
                    val[i] = clamp(val[i], min, max);
                }
        }
    }
}
