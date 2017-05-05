/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.Version;

/**
 * Miscellaneous methods.
 * <p>
 * This class also provides convenience methods for computing {@linkplain Object#hashCode hash code}
 * values. All those methods expect a {@code seed} argument, which is the hash code value computed
 * for previous fields in a class. For the initial seed (the one for the field for which to compute
 * an hash code), an arbitrary value must be provided. We suggest a different number for different
 * class in order to reduce the risk of collision between "empty" instances of different classes.
 * {@linkplain java.io.Serializable} classes can use {@code (int) serialVersionUID} for example.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 4.00
 *
 * @since 1.2
 * @module
 */
public final class Utilities extends Static {
    /**
     * A prime number used for hash code computation. Value 31 is often used because
     * some modern compilers can optimize {@code x*31} as {@code (x << 5) - x}
     * (Josh Bloch, <cite>Effective Java</cite>).
     */
    private static final int PRIME_NUMBER = 31;

    /**
     * The version of this Geotoolkit.org distribution.
     */
    public static final Version VERSION = new Version("4.00-SNAPSHOT");

    /**
     * Forbid object creation.
     */
    private Utilities() {
    }

    /**
     * Returns {@code true} if the given floats are equals. Positive and negative zero are
     * considered different, while a NaN value is considered equal to all other NaN values.
     *
     * @param o1 The first value to compare.
     * @param o2 The second value to compare.
     * @return {@code true} if both values are equal.
     *
     * @see Float#equals(Object)
     */
    public static boolean equals(final float o1, final float o2) {
        return Float.floatToIntBits(o1) == Float.floatToIntBits(o2);
    }

    /**
     * Returns {@code true} if the given doubles are equals. Positive and negative zero are
     * considered different, while a NaN value is considered equal to all other NaN values.
     *
     * @param o1 The first value to compare.
     * @param o2 The second value to compare.
     * @return {@code true} if both values are equal.
     *
     * @see Double#equals(Object)
     */
    public static boolean equals(final double o1, final double o2) {
        return Double.doubleToLongBits(o1) == Double.doubleToLongBits(o2);
    }

    /**
     * Alters the given seed with the hash code value computed from the given value.
     *
     * @param  value The value whose hash code to compute.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     */
    public static int hash(final boolean value, final int seed) {
        // Use the same values than Boolean.hashCode()
        return seed * PRIME_NUMBER + (value ? 1231 : 1237);
    }

    /**
     * Alters the given seed with the hash code value computed from the given value.
     *
     * @param  value The value whose hash code to compute.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     */
    public static int hash(final char value, final int seed) {
        return seed * PRIME_NUMBER + (int) value;
    }

    /**
     * Alters the given seed with the hash code value computed from the given value.
     * {@code byte} and {@code short} primitive types are handled by this method as
     * well through implicit widening conversion.
     *
     * @param  value The value whose hash code to compute.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     */
    public static int hash(final int value, final int seed) {
        return seed * PRIME_NUMBER + value;
    }

    /**
     * Alters the given seed with the hash code value computed from the given value.
     * {@code byte} and {@code short} primitive types are handled by this method as
     * well through implicit widening conversion.
     *
     * @param  value The value whose hash code to compute.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     */
    public static int hash(final long value, final int seed) {
        return seed * PRIME_NUMBER + (((int) value) ^ ((int) (value >>> 32)));
    }

    /**
     * Alters the given seed with the hash code value computed from the given value.
     *
     * @param  value The value whose hash code to compute.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     */
    public static int hash(final float value, final int seed) {
        return seed * PRIME_NUMBER + Float.floatToIntBits(value);
    }

    /**
     * Alters the given seed with the hash code value computed from the given value.
     *
     * @param  value The value whose hash code to compute.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     */
    public static int hash(final double value, final int seed) {
        return hash(Double.doubleToLongBits(value), seed);
    }

    /**
     * Alters the given seed with the hash code value computed from the given value. The given
     * object may be null. This method do <strong>not</strong> iterates recursively in array
     * elements. If array needs to be hashed, use one of {@link Arrays} method or
     * {@link #deepHashCode deepHashCode} instead.
     * <p>
     * <b>Note on assertions:</b> There is no way to ensure at compile time that this method
     * is not invoked with an array argument, while doing so would usually be a program error.
     * Performing a systematic argument check would impose a useless overhead for correctly
     * implemented {@link Object#hashCode} methods. As a compromise we perform this check at
     * runtime only if assertions are enabled. Using assertions for argument check in a public
     * API is usually a deprecated practice, but we make an exception for this particular method.
     *
     * @param  value The value whose hash code to compute, or {@code null}.
     * @param  seed  The hash code value computed so far. If this method is invoked for the first
     *               field, then any arbitrary value (preferably different for each class) is okay.
     * @return An updated hash code value.
     * @throws AssertionError If assertions are enabled and the given value is an array.
     */
    public static int hash(final Object value, int seed) throws AssertionError {
        seed *= PRIME_NUMBER;
        if (value != null) {
            assert !value.getClass().isArray() : name(value);
            seed += value.hashCode();
        }
        return seed;
    }

    /**
     * Returns the class name of the given object.
     * Used in assertions only.
     */
    private static String name(final Object object) {
        return object.getClass().getSimpleName();
    }

    /**
     * Utility method to avoid to fill an empty list during equals.
     * But we want to consider equals a null list and an empty one, for JAXB purpose
     * @param l1 first list to compare
     * @param l2 second list to compare
     * @return True if the two list are equals, or if one is null and the other empty
     */
    public static boolean listNullEquals(List l1, List l2) {
        if (l1 == null && l2 != null && l2.isEmpty()) {
            return true;
        } else if (l2 == null && l1 != null && l1.isEmpty()) {
            return true;
        } else {
            return Objects.equals(l1, l2);
        }

    }
}
