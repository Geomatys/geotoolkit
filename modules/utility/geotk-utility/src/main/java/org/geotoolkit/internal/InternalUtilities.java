/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.collection.XCollections;


/**
 * Various utility methods.
 *
 * {@section Strings}
 * Utility methods working on {@link String} or {@link CharSequence}. Some methods duplicate
 * functionalities already provided in {@code String}, but working on {@code CharSequence}.
 * This avoid the need to convert a {@code CharSequence} to a {@code String} for some simple
 * tasks that do not really need such conversion.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18 (derived from 3.00)
 * @module
 */
public final class InternalUtilities extends Static {
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
    public static <K,V> Map<K,V> subset(final Map<?,?> map, final Class<V> valueType, final K... keys)
            throws ClassCastException
    {
        Map<K,V> copy = null;
        if (map != null) {
            copy = new HashMap<K,V>(XCollections.hashMapCapacity(Math.min(map.size(), keys.length)));
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
}
