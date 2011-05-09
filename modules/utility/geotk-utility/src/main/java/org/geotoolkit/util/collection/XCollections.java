/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.util.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.io.Serializable;

import org.geotoolkit.lang.Static;


/**
 * Static methods working on {@link Collection} objects. This is an extension to the
 * Java {@link Collections} utility class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 3.10 (derived from 3.00)
 * @module
 */
public final class XCollections extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private XCollections() {
    }

    /**
     * Clears the given collection, if non-null. If the collection is null, then this method does
     * nothing. This is a convenience method when a null collection is a synonymous of empty.
     *
     * @param collection The collection to clear, or {@code null}.
     *
     * @since 3.18
     */
    public static void clear(final Collection<?> collection) {
        if (collection != null) {
            collection.clear();
        }
    }

    /**
     * Clears the given map, if non-null. If the map is null, then this method does nothing.
     * This is a convenience method when a null map is a synonymous of empty.
     *
     * @param map The map to clear, or {@code null}.
     *
     * @since 3.18
     */
    public static void clear(final Map<?,?> map) {
        if (map != null) {
            map.clear();
        }
    }

    /**
     * Returns {@code true} if the given collection is either null or
     * {@linkplain Collection#isEmpty() empty}. If this method returns {@code false},
     * then the given collection is guaranteed to be non-null and to contain at least
     * one element.
     *
     * @param collection The collection to test, or {@code null}.
     * @return {@code true} if the given collection is null or empty, or {@code false} otherwise.
     *
     * @since 3.18
     */
    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return (collection == null) || collection.isEmpty();
    }

    /**
     * Returns {@code true} if the given map is either null or {@linkplain Map#isEmpty() empty}.
     * If this method returns {@code false}, then the given map is guaranteed to be non-null and
     * to contain at least one element.
     *
     * @param map The map to test, or {@code null}.
     * @return {@code true} if the given map is null or empty, or {@code false} otherwise.
     *
     * @since 3.18
     */
    public static boolean isNullOrEmpty(final Map<?,?> map) {
        return (map == null) || map.isEmpty();
    }

    /**
     * Returns a {@linkplain Queue queue} which is always empty and accepts no element.
     *
     * @param <E> The type of elements in the empty collection.
     * @return An empty collection.
     *
     * @see Collections#emptyList()
     * @see Collections#emptySet()
     */
    @SuppressWarnings({"unchecked","rawtype"})
    public static <E> Queue<E> emptyQueue() {
        return EmptyQueue.INSTANCE;
    }

    /**
     * Returns a {@linkplain SortedSet sorted set} which is always empty and accepts no element.
     *
     * @param <E> The type of elements in the empty collection.
     * @return An empty collection.
     *
     * @see Collections#emptyList()
     * @see Collections#emptySet()
     */
    @SuppressWarnings({"unchecked","rawtype"})
    public static <E> SortedSet<E> emptySortedSet() {
        return EmptySortedSet.INSTANCE;
    }

    /**
     * Returns the specified array as an immutable set, or {@code null} if the array is null.
     *
     * @param  <E> The type of array elements.
     * @param  array The array to copy in a set. May be {@code null}.
     * @return A set containing the array elements, or {@code null} if the given array was null.
     *
     * @see Collections#unmodifiableSet(Set)
     *
     * @since 3.17
     */
    public static <E> Set<E> immutableSet(final E... array) {
        if (array == null) {
            return null;
        }
        switch (array.length) {
            case 0:  return Collections.emptySet();
            case 1:  return Collections.singleton(array[0]);
            default: return Collections.unmodifiableSet(new LinkedHashSet<E>(Arrays.asList(array)));
        }
    }

    /**
     * The comparator to be returned by {@code #listComparator} and similar methods. Can not be
     * public because of parameterized types: we need a method for casting to the expected type.
     * This is the same trick than {@link Collections#emptySet()} for example.
     *
     * @since 3.18 (derived from 2.5)
     */
    @SuppressWarnings("rawtypes")
    private static final class Compare implements Comparator<Collection<Comparable>>, Serializable {
        /**
         * The unique instance.
         */
        static final Comparator<Collection<Comparable>> INSTANCE = new Compare();

        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -8926770873102046405L;

        /**
         * Compares to collections of comparable objects.
         */
        @Override
        @SuppressWarnings("unchecked")
        public int compare(final Collection<Comparable> c1, final Collection<Comparable> c2) {
            final Iterator<Comparable> i1 = c1.iterator();
            final Iterator<Comparable> i2 = c2.iterator();
            int c;
            do {
                final boolean h1 = i1.hasNext();
                final boolean h2 = i2.hasNext();
                if (!h1) return h2 ? -1 : 0;
                if (!h2) return +1;
                final Comparable e1 = i1.next();
                final Comparable e2 = i2.next();
                c = e1.compareTo(e2);
            } while (c == 0);
            return c;
        }
    };

    /**
     * Returns a comparator for lists of comparable elements. The first element of each list
     * are {@linkplain Comparable#compareTo compared}. If one is <cite>greater than</cite> or
     * <cite>less than</cite> the other, the result of that comparison is returned. Otherwise
     * the second element are compared, and so on until either non-equal elements are found,
     * or end-of-list are reached. In the later case, the shortest list is considered
     * <cite>less than</cite> the longest one.
     * <p>
     * If both lists have the same length and equal elements in the sense of
     * {@link Comparable#compareTo}, then the comparator returns 0.
     *
     * @param <T> The type of elements in both lists.
     * @return The ordering between two lists.
     *
     * @since 3.18 (derived from 2.5)
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T extends Comparable<T>> Comparator<List<T>> listComparator() {
        return (Comparator) Compare.INSTANCE;
    }

    /**
     * Returns a comparator for sorted sets of comparable elements. The elements are compared in
     * iteration order as {@linkplain #forLists for lists}.
     *
     * @param <T> The type of elements in both sets.
     * @return The ordering between two sets.
     *
     * @since 3.18 (derived from 2.5)
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T extends Comparable<T>> Comparator<SortedSet<T>> sortedSetComparator() {
        return (Comparator) Compare.INSTANCE;
    }

    /**
     * Returns a comparator for arbitrary collections of comparable elements. The elements are
     * compared in iteration order as {@linkplain #forLists for lists}. <strong>This comparator
     * make sense only for collections having determinist order</strong> like
     * {@link java.util.TreeSet}, {@link java.util.LinkedHashSet} or queues.
     * Do <strong>not</strong> use it with {@link java.util.HashSet}.
     *
     * @param <T> The type of elements in both collections.
     * @return The ordering between two collections.
     *
     * @since 3.18 (derived from 2.5)
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T extends Comparable<T>> Comparator<Collection<T>> collectionComparator() {
        return (Comparator) Compare.INSTANCE;
    }

    /**
     * Returns the capacity to be given to the {@link java.util.HashMap#HashMap(int) HashMap}
     * constructor for holding the given number of elements. This method computes the capacity
     * for the default <cite>load factor</cite>, which is 0.75.
     * <p>
     * The same calculation can be used for {@link java.util.LinkedHashMap} and
     * {@link java.util.HashSet} as well, which are built on top of {@code HashMap}.
     *
     * @param elements The number of elements to be put into the hash map or hash set.
     * @return The optimal initial capacity to be given to the hash map constructor.
     */
    public static int hashMapCapacity(int elements) {
        final int r = elements >>> 2;
        if (elements != (r << 2)) {
            elements++;
        }
        return elements + r;
    }
}
