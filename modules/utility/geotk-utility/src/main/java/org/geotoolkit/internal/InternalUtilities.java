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

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.collection.XCollections;
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
     * {@link ComparisonMode#APPROXIMATIVE}.
     * <p>
     * Historically, this was the relative tolerance threshold for considering two matrixes
     * as {@linkplain org.geotoolkit.referencing.operation.matrix.XMatrix#equals(Object,
     * ComparisonMode) equal}. This value has been determined empirically in order to allow
     * {@link org.geotoolkit.referencing.operation.transform.ConcatenatedTransform} to detect the
     * cases where two {@link org.geotoolkit.referencing.operation.transform.LinearTransform}
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
     * Returns an identity string for the given value. This method returns a string similar to
     * the one returned by the default implementation of {@link Object#toString()}, except that
     * a simple class name (without package name) is used instead than the fully-qualified name.
     *
     * @param  value The object for which to get the identity string, or {@code null}.
     * @return The identity string for the given object.
     *
     * @since 3.17
     */
    public static String identity(final Object value) {
        return Classes.getShortClassName(value) + '@' + Integer.toHexString(System.identityHashCode(value));
    }

    /**
     * Returns {@code true} if {@code ymin} is the south pole and {@code ymax} is the north pole.
     *
     * @param ymin The minimal latitude to test.
     * @param ymax The maximal latitude to test.
     * @return {@code true} if the given latitudes are south pole to noth pole respectively.
     *
     * @since 3.20
     */
    public static boolean isPoleToPole(final double ymin, final double ymax) {
        return abs(ymin + 90) <= ANGULAR_TOLERANCE && abs(ymax - 90) <= ANGULAR_TOLERANCE;
    }

    /**
     * Returns {@code true} if the given values are approximatively equal.
     * Two NaN values are considered equal.
     *
     * @param  v1 The first value to compare.
     * @param  v2 The second value to compare.
     * @param  epsilon The tolerance threshold, which must be positive.
     * @return {@code true} If both values are approximatively equal.
     *
     * @since 3.20
     */
    public static boolean epsilonEqual(final double v1, final double v2, final double epsilon) {
        return (abs(v1 - v2) <= epsilon) || Double.doubleToLongBits(v1) == Double.doubleToLongBits(v2);
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
            default:            return Utilities.equals(v1, v2);
            case APPROXIMATIVE: return epsilonEqual(v1, v2);
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
     * Returns {@code true} if the following objects are floating point numbers ({@link Float} or
     * {@link Double} types) and approximatively equal. If the given object are not floating point
     * numbers, then this method returns {@code false} unconditionally on the assumption that
     * strict equality has already been checked before this method call.
     *
     * @param  v1 The first value to compare.
     * @param  v2 The second value to compare.
     * @return {@code true} If both values are real number and approximatively equal.
     *
     * @since 3.18
     */
    public static boolean floatEpsilonEqual(final Object v1, final Object v2) {
        return (v1 instanceof Float || v1 instanceof Double) &&
               (v2 instanceof Float || v2 instanceof Double) &&
               epsilonEqual(((Number) v1).doubleValue(), ((Number) v2).doubleValue());
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
     * Converts a {@code float} value to {@code double} value while preserving the string
     * representation in base 10. The result may be different from the value that we would
     * get from a normal conversion - which preserve the value in base 2, but it may be
     * closer to the user intend.
     * <p>
     * <b>Example:</b> {@code 99.99f} converted to {@code double} by the normal cast operation
     * produces {@code 99.98999786376953}, while the user's intend was probably {@code 99.99}.
     * <p>
     * The current algorithm is inefficient, but we define this method so we have a single
     * place where to improve it if needed.
     *
     * @param  value The value to convert.
     * @return The converted value.
     *
     * @since 3.19
     */
    public static double convert10(final float value) {
        return Double.parseDouble(Float.toString(value));
    }

    /**
     * Returns a copy of the given array as a non-empty immutable set.
     * If the given array is empty, then this method returns {@code null}.
     * <p>
     * This method is not public provided in the public API because the recommended
     * practice is usually to return an empty collection rather than {@code null}.
     *
     * @param  <T> The type of elements.
     * @param  elements The elements to copy in a set.
     * @return An unmodifiable set which contains all the given elements.
     *
     * @since 3.17
     */
    @SafeVarargs
    public static <T> Set<T> nonEmptySet(final T... elements) {
        final Set<T> asSet = XCollections.immutableSet(elements);
        return (asSet != null && asSet.isEmpty()) ? null : asSet;
    }

    /**
     * Returns an unmodifiable map which contains a copy of the given map, only for the given keys.
     * The value for the given keys shall be of the given type. Other values can be of any types,
     * since they will be ignored.
     *
     * @param  <K>  The type of keys in the map.
     * @param  <V>  The type of values in the map.
     * @param  map  The map to copy, or {@code null}.
     * @param  valueType The base type of retained values.
     * @param  keys The keys of values to retain.
     * @return A copy of the given map containing only the given keys, or {@code null}
     *         if the given map was null.
     * @throws ClassCastException If at least one retained value is not of the expected type.
     *
     * @since 3.17
     */
    @SafeVarargs
    public static <K,V> Map<K,V> subset(final Map<?,?> map, final Class<V> valueType, final K... keys)
            throws ClassCastException
    {
        Map<K,V> copy = null;
        if (map != null) {
            copy = new HashMap<>(XCollections.hashMapCapacity(Math.min(map.size(), keys.length)));
            for (final K key : keys) {
                final V value = valueType.cast(map.get(key));
                if (value != null) {
                    copy.put(key, value);
                }
            }
            copy = XCollections.unmodifiableMap(copy);
        }
        return copy;
    }

    /**
     * Returns the first non-null element in the given iterable, or {@code null} if none.
     * This method makes sense only for collections having determinist iteration order like
     * {@link List} and {@link SortedSet} interfaces, or {@link LinkedHashSet} implementation.
     *
     * @param  <E> The type of elements in the iterable.
     * @param  collection Where to search for the first non-null element.
     * @return The first non-null element, or {@code null} if none or if the given iterable is null.
     *
     * @since 3.20
     */
    public static <E> E firstNonNull(final Iterable<E> collection) {
        if (collection != null) {
            for (final E element : collection) {
                if (element != null) {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * Returns the separator to use between numbers. Current implementation returns the coma
     * character, unless the given number already use the coma as the decimal separator.
     *
     * @param  format The format used for formatting numbers.
     * @return The character to use as a separator between numbers.
     *
     * @since 3.11
     */
    public static char getSeparator(final NumberFormat format) {
        if (format instanceof DecimalFormat) {
            final char c = ((DecimalFormat) format).getDecimalFormatSymbols().getDecimalSeparator();
            if (c == ',') {
                return ';';
            }
        }
        return ',';
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
                Errors.Keys.ILLEGAL_ARGUMENT_2, "color", color));
    }
}
