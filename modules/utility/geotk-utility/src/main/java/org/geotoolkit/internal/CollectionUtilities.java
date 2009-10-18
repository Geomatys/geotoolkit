/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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


/**
 * A set of utilities for {@link java.util.Collection}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.00
 * @module
 */
@Static
public final class CollectionUtilities {
    /**
     * Do not allow instantiation of this class.
     */
    private CollectionUtilities() {
    }

    /**
     * Returns a copy of the given array as an unmodifiable set.
     *
     * @param  <T> The type of elements.
     * @param  elements The elements to copy in a set.
     * @return An unmodifiable set which contains all the given elements.
     *
     * @todo Consider caching the set in a {@code WeakHashSet}, given that this method
     *       is often invoked for set of code list, which are typically shared by many
     *       callers.
     *
     * @since 3.05
     */
    public static <T> Set<T> unmodifiableSet(final T[] elements) {
        return Collections.unmodifiableSet(new LinkedHashSet<T>(Arrays.asList(elements)));
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
