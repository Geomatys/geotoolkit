/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.util.collection;

import java.util.*;
import java.io.Serializable;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.collection.Containers;


/**
 * Static methods working on {@link Collection} objects.
 * This is an extension to the Java {@link Collections} and {@link Containers} utility classes.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.22
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
     * Adds the given element to the given collection only if the element is non-null.
     * If any of the given argument is null, then this method does nothing.
     *
     * @param  <E>        The type of elements in the collection.
     * @param  collection The collection in which to add elements, or {@code null}.
     * @param  element    The element to add in the collection, or {@code null}.
     * @return {@code true} if the given element has been added, or {@code false} otherwise.
     *
     * @since 3.20
     */
    public static <E> boolean addIfNonNull(final Collection<E> collection, final E element) {
        return (collection != null && element != null) && collection.add(element);
    }

    /**
     * Returns the specified array as an immutable set, or {@code null} if the array is null.
     * If the given array contains duplicated elements, i.e. elements that are equal in the
     * sense of {@link Object#equals(Object)}, then only the last instance of the duplicated
     * values will be included in the returned set.
     *
     * @param  <E> The type of array elements.
     * @param  array The array to copy in a set. May be {@code null}.
     * @return A set containing the array elements, or {@code null} if the given array was null.
     *
     * @see Collections#unmodifiableSet(Set)
     *
     * @since 3.17
     */
    @SafeVarargs
    public static <E> Set<E> immutableSet(final E... array) {
        if (array == null) {
            return null;
        }
        switch (array.length) {
            case 0:  return Collections.emptySet();
            case 1:  return Collections.singleton(array[0]);
            default: return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(array)));
        }
    }

    /**
     * Returns a unmodifiable version of the given set.
     * This method is different than the standard {@link Collections#unmodifiableSet(Set)}
     * in that it tries to returns a more efficient object when there is zero or one element.
     * Such small set occurs frequently in Apache SIS, especially for
     * {@link org.apache.sis.referencing.AbstractIdentifiedObject} names or identifiers.
     *
     * <p><em>The set returned by this method may or may not be a view of the given set</em>.
     * Consequently this method shall be used <strong>only</strong> if the given set will
     * <strong>not</strong> be modified after this method call. In case of doubt, use the
     * standard {@link Collections#unmodifiableSet(Set)} method instead.</p>
     *
     * @param  <E>  The type of elements in the set.
     * @param  set  The set to make unmodifiable, or {@code null}.
     * @return A unmodifiable version of the given set, or {@code null} if the given set was null.
     */
    public static <E> Set<E> unmodifiableOrCopy(Set<E> set) {
        return org.apache.sis.internal.util.CollectionsExt.unmodifiableOrCopy(set);
    }

    /**
     * Returns a unmodifiable version of the given map.
     * This method is different than the standard {@link Collections#unmodifiableMap(Map)}
     * in that it tries to returns a more efficient object when there is zero or one entry.
     * Such small maps occur frequently in Apache SIS.
     *
     * <p><em>The map returned by this method may or may not be a view of the given map</em>.
     * Consequently this method shall be used <strong>only</strong> if the given map will
     * <strong>not</strong> be modified after this method call. In case of doubt, use the
     * standard {@link Collections#unmodifiableMap(Map)} method instead.</p>
     *
     * @param  <K>  The type of keys in the map.
     * @param  <V>  The type of values in the map.
     * @param  map  The map to make unmodifiable, or {@code null}.
     * @return A unmodifiable version of the given map, or {@code null} if the given map was null.
     */
    public static <K,V> Map<K,V> unmodifiableOrCopy(Map<K,V> map) {
        return org.apache.sis.internal.util.CollectionsExt.unmodifiableOrCopy(map);
    }

    /**
     * The comparator to be returned by {@link Collections#listComparator()} and similar methods.
     */
    private static final class Compare<T extends Comparable<T>>
            implements Comparator<Collection<T>>, Serializable
    {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 7050753365408754641L;

        /**
         * The unique instance. Can not be public because of parameterized types: we need a method
         * for casting to the expected type. This is the same trick than the one used by the JDK
         * in the {@link Collections#emptySet()} method for instance.
         */
        @SuppressWarnings("rawtypes")
        static final Comparator INSTANCE = new Compare();

        /**
         * Do not allow instantiation other than the unique {@link #INSTANCE}.
         */
        private Compare() {
        }

        /**
         * Compares two collections of comparable objects.
         */
        @Override
        public int compare(final Collection<T> c1, final Collection<T> c2) {
            final Iterator<T> i1 = c1.iterator();
            final Iterator<T> i2 = c2.iterator();
            int c;
            do {
                final boolean h1 = i1.hasNext();
                final boolean h2 = i2.hasNext();
                if (!h1) return h2 ? -1 : 0;
                if (!h2) return +1;
                final T e1 = i1.next();
                final T e2 = i2.next();
                c = e1.compareTo(e2);
            } while (c == 0);
            return c;
        }
    };

    /**
     * Returns a comparator for lists of comparable elements. The first element of each list are
     * {@linkplain Comparable#compareTo(Object) compared}. If one is <cite>greater than</cite> or
     * <cite>less than</cite> the other, the result of that comparison is returned. Otherwise
     * the second element are compared, and so on until either non-equal elements are found,
     * or end-of-list are reached. In the later case, the shortest list is considered
     * <cite>less than</cite> the longest one.
     *
     * <p>If both lists have the same length and equal elements in the sense of
     * {@link Comparable#compareTo}, then the comparator returns 0.</p>
     *
     * @param  <T> The type of elements in both lists.
     * @return The ordering between two lists.
     *
     * @since 3.18 (derived from 2.5)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> Comparator<List<T>> listComparator() {
        return Compare.INSTANCE;
    }

    /**
     * Returns a comparator for sorted sets of comparable elements. The first element of each set
     * are {@linkplain Comparable#compareTo(Object) compared}. If one is <cite>greater than</cite>
     * or <cite>less than</cite> the other, the result of that comparison is returned. Otherwise
     * the second element are compared, and so on until either non-equal elements are found,
     * or end-of-set are reached. In the later case, the smallest set is considered
     * <cite>less than</cite> the largest one.
     *
     * {@note There is no method accepting an arbitrary <code>Set</code> or <code>Collection</code>
     *        argument because this comparator makes sense only for collections having determinist
     *        iteration order.}
     *
     * @param <T> The type of elements in both sets.
     * @return The ordering between two sets.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> Comparator<SortedSet<T>> sortedSetComparator() {
        return Compare.INSTANCE;
    }

    /**
     * The comparator to be returned by {@link Collections#valueComparator()}.
     */
    private static final class ValueComparator<K,V extends Comparable<V>>
            implements Comparator<Map.Entry<K,V>>, Serializable
    {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 807166038568740444L;

        /**
         * The unique instance. Can not be public because of parameterized types: we need a method
         * for casting to the expected type. This is the same trick than the one used by the JDK
         * in the {@link Collections#emptySet()} method for instance.
         */
        @SuppressWarnings("rawtypes")
        static final ValueComparator INSTANCE = new ValueComparator();

        /**
         * Do not allow instantiation other than the unique {@link #INSTANCE}.
         */
        private ValueComparator() {
        }

        /**
         * Compares the values of two entries.
         */
        @Override
        public int compare(final Map.Entry<K,V> e1, final Map.Entry<K,V> e2) {
            return e1.getValue().compareTo(e2.getValue());
        }
    }

    /**
     * Returns a comparator for map entries having comparable {@linkplain java.util.Map.Entry#getValue() values}.
     * For any pair of entries {@code e1} and {@code e2}, this method performs the comparison as below:
     *
     * {@preformat java
     *     return e1.getValue().compareTo(e2.getValue());
     * }
     *
     * This comparator can be used as a complement to {@link SortedSet}. While {@code SortedSet}
     * maintains keys ordering at all time, {@code valueComparator()} is typically used only at
     * the end of a process in which the values are the numerical calculation results.
     *
     * @param <K> The type of keys in the map entries.
     * @param <V> The type of values in the map entries.
     * @return A comparator for the values of the given type.
     */
    @SuppressWarnings("unchecked")
    public static <K,V extends Comparable<V>> Comparator<Map.Entry<K,V>> valueComparator() {
        return ValueComparator.INSTANCE;
    }
}
