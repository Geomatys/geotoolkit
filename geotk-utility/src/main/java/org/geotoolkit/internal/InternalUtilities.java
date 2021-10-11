/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;

import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.resources.Errors;

import static java.lang.Math.*;


/**
 * Various utility methods not to be put in public API.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.18 (derived from 3.00)
 * @module
 */
public final class InternalUtilities extends Static {
    /**
     * Relative difference tolerated when comparing floating point numbers using
     * {@link ComparisonMode#APPROXIMATE}.
     * <p>
     * Historically, this was the relative tolerance threshold for considering two matrixes
     * as {@linkplain org.geotoolkit.referencing.operation.matrix.XMatrix#equals(Object,
     * ComparisonMode) equal}. This value has been determined empirically in order to allow
     * {@link org.apache.sis.referencing.operation.transform.MathTransforms} to detect the
     * cases where two {@link org.apache.sis.referencing.operation.transform.LinearTransform}
     * are equal for practical purpose. This threshold can be used as below:
     *
     * {@preformat java
     *     Matrix m1 = ...;
     *     Matrix m2 = ...;
     *     if (MatrixUtilities.epsilonEqual(m1, m2, EQUIVALENT_THRESHOLD, true)) {
     *         // Consider that matrixes are equal.
     *     }
     * }
     *
     * By extension, the same threshold value is used for comparing other floating point values.
     *
     * @since 3.20
     */
    public static final double COMPARISON_THRESHOLD = 1E-14;

    /**
     * Default tolerance threshold for comparing ordinate values in a projected CRS,
     * assuming that the unit of measurement is metre. This is not a tolerance for
     * testing map projection accuracy.
     *
     * @since 3.20
     */
    public static final double LINEAR_TOLERANCE = 1.0;

    /**
     * Default tolerance threshold for comparing ordinate values in a geographic CRS,
     * assuming that the unit of measurement is decimal degrees and using the standard
     * nautical mile length.
     *
     * @since 3.20
     */
    public static final double ANGULAR_TOLERANCE = LINEAR_TOLERANCE / (1852 * 60);

    /**
     * Workaround for rounding errors.
     */
    private static final double EPS = 1E-8;

    /**
     * Floating point tolerance in <cite>Unit in Last Place</cite> (ULP).
     * Used in order to determine if an integer can be rounded.
     */
    private static final int ULP_TOLERANCE = 4;

    /**
     * Do not allow instantiation of this class.
     */
    private InternalUtilities() {
    }

    /**
     * Returns {@code true} if the given values are approximatively equal given the
     * comparison mode.
     *
     * @param  v1 The first value to compare.
     * @param  v2 The second value to compare.
     * @param  mode The comparison mode to use for comparing the numbers.
     * @return {@code true} If both values are approximatively equal.
     *
     * @since 3.18
     */
    public static boolean epsilonEqual(final double v1, final double v2, final ComparisonMode mode) {
        switch (mode) {
            default: return org.geotoolkit.util.Utilities.equals(v1, v2);
            case APPROXIMATE: return epsilonEqual(v1, v2);
            case DEBUG: {
                final boolean equal = epsilonEqual(v1, v2);
                assert equal : "v1=" + v1 + " v2=" + v2 + " Δv=" + abs(v1-v2);
                return equal;
            }
        }
    }

    /**
     * Returns {@code true} if the given values are approximatively equal, up to the
     * {@linkplain #COMPARISON_THRESHOLD comparison threshold}.
     *
     * @param  v1 The first value to compare.
     * @param  v2 The second value to compare.
     * @return {@code true} If both values are approximatively equal.
     *
     * @since 3.18
     */
    public static boolean epsilonEqual(final double v1, final double v2) {
        final double threshold = COMPARISON_THRESHOLD * max(abs(v1), abs(v2));
        if (threshold == Double.POSITIVE_INFINITY || Double.isNaN(threshold)) {
            return Double.doubleToLongBits(v1) == Double.doubleToLongBits(v2);
        }
        return abs(v1 - v2) <= threshold;
    }

    /**
     * Compares the given objects in {@link ComparisonMode#DEBUG} mode.
     *
     * @param  o1 The first object to compare.
     * @param  o2 The second object to compare.
     * @return {@code true} if the given objects are equal.
     * @throws AssertionError If the given objects are not equal and the cause can be
     *         specified in the exception message.
     *
     * @since 3.20
     */
    public static boolean debugEquals(final Object o1, final Object o2) throws AssertionError {
        return Utilities.deepEquals(o1, o2, ComparisonMode.DEBUG);
    }

    /**
     * Rounds the specified value, providing that the difference between the original value and
     * the rounded value is not greater than the specified amount of floating point units. This
     * method can be used for hiding floating point error likes 2.9999999996.
     *
     * @param  value The value to round.
     * @param  scale The scale by which to multiply the value before to round it.
     * @param  maxULP The maximal change allowed in ULPs (Unit in the Last Place).
     * @return The rounded value, of {@code value} if it was not close enough to an integer.
     *
     * @since 3.20
     */
    public static double adjustForRoundingError(final double value, double scale, final int maxULP) {
        scale = Math.abs(scale);
        final double target = Math.rint(value * scale) / scale;
        return (Math.abs(value - target) <= maxULP*Math.ulp(value)) ? target : value;
    }

    /**
     * Work-around for rounding error. This method invokes {@link #adjustForRoundingError(double,
     * double, int)} with arbitrary values that may change in any future version. Current values
     * were determined empirically from practical experience with IFREMER and other data.
     *
     * @param value The value to fix.
     * @return The "fixed" value.
     *
     * @since 3.20
     */
    public static double adjustForRoundingError(final double value) {
        return adjustForRoundingError(value, 360, ULP_TOLERANCE);
    }

    /**
     * Sets the {@linkplain NumberFormat#getMinimumFractionDigits() minimum fraction digits} and
     * {@linkplain NumberFormat#getMaximumFractionDigits() maximum fraction digits} of the given
     * format objects for "acceptable" formatting of the given value. The work performed by this
     * method is heuristic and may change in any future version.
     *
     * @param format The format to configure.
     * @param value  The sample value to use for formatting the given format.
     * @param maxPrecision The maximal precision to use (e.g. 6).
     *
     * @since 3.20
     */
    public static void configure(final NumberFormat format, double value, final int maxPrecision) {
        value = abs(value);
        if (format instanceof DecimalFormat) {
            value *= ((DecimalFormat) format).getMultiplier();
        }
        int precision;
        for (precision=0; precision<maxPrecision; precision++) {
            final double check = rint(value*1E+4) % 1E+4;
            if (!(check > value*EPS)) { // 'step' may be NaN
                break;
            }
            value *= 10;
        }
        format.setMinimumFractionDigits(precision);
        format.setMaximumFractionDigits(precision);
    }

    /**
     * Gets the ARGB values for the given hexadecimal value. If the given code begins with the
     * {@code '#'} character, then this method accepts the following hexadecimal patterns:
     * <p>
     * <ul>
     *   <li>{@code "#AARRGGBB"}: an explicit ARGB code used verbatim.</li>
     *   <li>{@code "#RRGGBB"}: a fully opaque RGB color.</li>
     *   <li>{@code "#ARGB"}: an abbreviation for {@code "#AARRGGBB"}.</li>
     *   <li>{@code "#RGB"}: an abbreviation for {@code "#RRGGBB"}.
     *       For example #0BC means #00BBCC.</li>
     * </ul>
     *
     * @param  color The color code to parse.
     * @throws NumberFormatException If the given code can not be parsed.
     * @return The ARGB code.
     *
     * @see java.awt.Color#decode(String)
     *
     * @since 3.19
     */
    @SuppressWarnings("fallthrough")
    public static int parseColor(String color) throws NumberFormatException {
        color = color.trim();
        if (color.startsWith("#")) {
            final String code = color.substring(1);
            // Parses as a long in order to accept ARGB codes in the 80000000 to FF000000 range.
            // The check for the string length will ensure that we are not outside those bounds.
            int value = (int) Long.parseLong(code, 16);
            switch (code.length()) {
                case 3: value |= 0xF000; // Fallthrough
                case 4: {
                    int t;
                    return (((t=(value & 0xF000)) | (t << 4)) << 12) |
                           (((t=(value & 0x0F00)) | (t << 4)) <<  8) |
                           (((t=(value & 0x00F0)) | (t << 4)) <<  4) |
                           (((t=(value & 0x000F)) | (t << 4)));
                }
                case 6: value |= 0xFF000000; // Fallthrough
                case 8: return value;
            }
        } else {
            /*
             * Parses the string as an opaque color unless an alpha value was provided.
             * This matches closing the default java.awt.Color.decode(String) behavior,
             * which considers every colors as opaque. We relax slightly the condition
             * by allowing non-zero alpha values. The inconvenient is that specifying
             * a fully transparent color is not possible with syntax - please use the
             * above "#" syntax instead.
             */
            final long n = Long.decode(color);
            int value = (int) n;
            if (value == n) {
                if ((value & 0xFF000000) == 0) {
                    value |= 0xFF000000;
                }
                return value;
            }
        }
        throw new NumberFormatException(Errors.format(
                Errors.Keys.IllegalArgument_2, "color", color));
    }
}
