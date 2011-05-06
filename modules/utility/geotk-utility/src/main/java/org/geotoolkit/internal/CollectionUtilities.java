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

import java.util.*;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.collection.XCollections;


/**
 * A set of utilities for {@link java.util.Collection}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.00
 * @module
 */
public final class CollectionUtilities extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private CollectionUtilities() {
    }

    /**
     * Returns a copy of the given array as a non-empty immutable set.
     * If the given array is empty, then this method returns {@code null}.
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
            copy = unmodifiableMap(copy);
        }
        return copy;
    }

    /**
     * Returns a unmodifiable version of the given map. This method is different than
     * {@link Collections#unmodifiableMap(Map)} in that it tries to returns a more efficient
     * object when there is zero or one elements. <strong>The map returned by this method may
     * or may not be a view of the given map</strong>. Consequently this method shall be used
     * <strong>only if the given map will not be modified after this method call</strong>. In
     * case of doubt, use the standard {@link Collections#unmodifiableMap(Map)}.
     *
     * @param  <K>  The type of keys in the map.
     * @param  <V>  The type of values in the map.
     * @param  map  The map to make unmodifiable, or {@code null}.
     * @return A unmodifiable version of the given map.
     *
     * @since 3.17
     */
    public static <K,V> Map<K,V> unmodifiableMap(Map<K,V> map) {
        if (map != null) switch (map.size()) {
            case 0: {
                map = Collections.emptyMap();
                break;
            }
            case 1: {
                final Map.Entry<K,V> entry = map.entrySet().iterator().next();
                map = Collections.singletonMap(entry.getKey(), entry.getValue());
                break;
            }
            default: {
                map = Collections.unmodifiableMap(map);
                break;
            }
        }
        return map;
    }

    /**
     * Copies the given map or collection. The {@code clone()} method is used for {@link ArrayList},
     * {@link HashSet}, {@link TreeSet}, {@link HashMap}, {@link TreeMap} types and their subclasses
     * because those methods are implemented efficiently. For all other types, the copy constructor
     * is used. For example we do not use the clone method of {@link LinkedList} because the copy is
     * assumed short-lived and {@link ArrayList} is more efficient to construct. The same argument
     * applies when the collection is an instance from the {@link java.util.concurrent} package.
     * <p>
     * If the given argument is not a collection, then it is returned unchanged.
     *
     * @param  collection The collection or map to copy.
     * @return A copy of the given object, or the object itself if its type has not been recognized.
     */
    public static Object cloneOrCopy(final Object collection) {
        if (collection instanceof ArrayList<?>) {
            return ((ArrayList<?>) collection).clone();
        }
        if (collection instanceof HashSet<?>) {
            return ((HashSet<?>) collection).clone();
        }
        if (collection instanceof TreeSet<?>) {
            return ((TreeSet<?>) collection).clone();
        }
        if (collection instanceof HashMap<?,?>) {
            return ((HashMap<?,?>) collection).clone();
        }
        if (collection instanceof TreeMap<?,?>) {
            return ((TreeMap<?,?>) collection).clone();
        }
        return copy(collection);
    }

    /**
     * Copies the given map or collection using the copy constructor in all cases,
     * If the given argument is not a collection, then it is returned unchanged.
     *
     * @param  collection The collection or map to copy.
     * @return A copy of the given object, or the object itself if its type has not been recognized.
     */
    public static Object copy(final Object collection) {
        if (collection instanceof List<?>) {
            return new ArrayList<Object>((List<?>) collection);
        }
        if (collection instanceof SortedSet<?>) {
            return new TreeSet<Object>((SortedSet<?>) collection);
        }
        if (collection instanceof Set<?>) {
            return new LinkedHashSet<Object>((Set<?>) collection);
        }
        if (collection instanceof SortedMap<?,?>) {
            return new TreeMap<Object,Object>((SortedMap<?,?>) collection);
        }
        if (collection instanceof Map<?,?>) {
            return new LinkedHashMap<Object,Object>((Map<?,?>) collection);
        }
        return collection;
    }
}
