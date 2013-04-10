/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.util.*;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.collection.CollectionsExt;


/**
 * Static methods working on {@link Collection} objects. This is an extension to the
 * Java {@link Collections} utility class providing:
 * <p>
 * <ul>
 *   <li>Null-safe {@link #clear(Collection) clear}, {@link #isNullOrEmpty(Collection) isNullOrEmpty}
 *       and {@link #addIfNonNull(Collection, Object) addIfNonNull} methods.</li>
 *   <li>{@link #unmodifiableSet(Set) unmodifiableSet} and {@linkplain #unmodifiableMap(Map) unmodifiableMap}
 *       methods slightly more compact than the standard ones when the new collection is not
 *       required to be a view over the given collection.</li>
 *   <li>{@link #asCollection(Object) asCollection} for wrapping arbitrary objects to list or collection.</li>
 *   <li>List and collection {@linkplain #listComparator() comparators}.</li>
 *   <li>{@link #copy(Collection) copy} method for taking a snapshot of an arbitrary implementation
 *       into a stable object.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
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
     *
     * @deprecated Moved to {@link CollectionsExt#isNullOrEmpty(Collection)}.
     */
    @Deprecated
    public static boolean isNullOrEmpty(final Collection<?> collection) {
        return CollectionsExt.isNullOrEmpty(collection);
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
     *
     * @deprecated Moved to {@link CollectionsExt#isNullOrEmpty(Map)}.
     */
    @Deprecated
    public static boolean isNullOrEmpty(final Map<?,?> map) {
        return CollectionsExt.isNullOrEmpty(map);
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
     * Returns a {@linkplain SortedSet sorted set} which is always empty and accepts no element.
     *
     * @param <E> The type of elements in the empty collection.
     * @return An empty collection.
     *
     * @see Collections#emptyList()
     * @see Collections#emptySet()
     *
     * @deprecated Moved to {@link CollectionsExt#emptySortedSet()}.
     */
    @Deprecated
    public static <E> SortedSet<E> emptySortedSet() {
        return CollectionsExt.emptySortedSet();
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
     *
     * @deprecated Moved to {@link CollectionsExt#immutableSet(E[])}.
     */
    @Deprecated
    @SafeVarargs
    public static <E> Set<E> immutableSet(final E... array) {
        return CollectionsExt.immutableSet(array);
    }

    /**
     * Returns a unmodifiable version of the given set. This method is different than the
     * standard {@link Collections#unmodifiableSet(Set)} in that it tries to returns a more
     * efficient object when there is zero or one element. <em>The set returned by this
     * method may or may not be a view of the given set</em>. Consequently this method
     * shall be used <strong>only</strong> if the given set will <strong>not</strong> be
     * modified after this method call. In case of doubt, use the standard
     * {@link Collections#unmodifiableSet(Set)} method instead.
     *
     * @param  <E>  The type of elements in the set.
     * @param  set  The set to make unmodifiable, or {@code null}.
     * @return A unmodifiable version of the given set, or {@code null} if the given set was null.
     *
     * @since 3.20
     *
     * @deprecated Moved to {@link CollectionsExt#unmodifiableOrCopy(Set)}.
     */
    @Deprecated
    public static <E> Set<E> unmodifiableSet(Set<E> set) {
        return CollectionsExt.unmodifiableOrCopy(set);
    }

    /**
     * Returns a unmodifiable version of the given map. This method is different than the
     * standard {@link Collections#unmodifiableMap(Map)} in that it tries to returns a more
     * efficient object when there is zero or one element. <em>The map returned by this
     * method may or may not be a view of the given map</em>. Consequently this method
     * shall be used <strong>only</strong> if the given map will <strong>not</strong> be
     * modified after this method call. In case of doubt, use the standard
     * {@link Collections#unmodifiableMap(Map)} method instead.
     *
     * @param  <K>  The type of keys in the map.
     * @param  <V>  The type of values in the map.
     * @param  map  The map to make unmodifiable, or {@code null}.
     * @return A unmodifiable version of the given map, or {@code null} if the given map was null.
     *
     * @since 3.18 (derived from 3.17)
     *
     * @deprecated Moved to {@link CollectionsExt#unmodifiableOrCopy(Map)}.
     */
    @Deprecated
    public static <K,V> Map<K,V> unmodifiableMap(Map<K,V> map) {
        return CollectionsExt.unmodifiableOrCopy(map);
    }

    /**
     * Returns the given value as a collection. Special cases:
     * <p>
     * <ul>
     *   <li>If the value is null, then this method returns an {@linkplain Collections#emptyList() empty list}.</li>
     *   <li>If the value is an instance of {@link Collection}, then it is returned unchanged.</li>
     *   <li>If the value is an array of objects, then it is returned {@linkplain Arrays#asList(Object[]) as a list}.</li>
     *   <li>If the value is an instance of {@link Iterable}, {@link Iterator} or {@link Enumeration}, copies the values in a new list.</li>
     *   <li>Otherwise the value is returned as a {@linkplain Collections#singletonList(Object) singleton list}.</li>
     * </ul>
     * <p>
     * Note that in the {@link Iterator} and {@link Enumeration} cases, the given value object
     * is not valid anymore after this method call since it has been used for the iteration.
     * <p>
     * If the returned object needs to be a list, then this method can be chained
     * with {@link #asList(Collection)} as below:
     *
     * {@preformat java
     *     List<?> list = asList(asCollection(object));
     * }
     *
     * @param  value The value to return as a collection, or {@code null}.
     * @return The value as a collection, or wrapped in a collection (never {@code null}).
     *
     * @since 3.20
     *
     * @deprecated Moved to {@link CollectionsExt#toCollection(Object)}.
     */
    @Deprecated
    public static Collection<?> asCollection(final Object value) {
        return CollectionsExt.toCollection(value);
    }

    /**
     * Casts o copies the given collection to a list. Special cases:
     * <p>
     * <ul>
     *   <li>If the given collection is {@code null}, then this method returns {@code null}.</li>
     *   <li>If the given collection is already a list, then it is returned unchanged.</li>
     *   <li>Otherwise the elements are copied in a new list, which is returned.</li>
     * </ul>
     * <p>
     * If the argument is not an instance of {@code Collection}, then this method can be chained
     * with {@link #asCollection(Object)} for handling a wider range of types:
     *
     * {@preformat java
     *     List<?> list = asList(asCollection(object));
     * }
     *
     * @param  <T> The type of elements in the given collection.
     * @param  collection The collection to cast or copy to a list.
     * @return The given collection as a list, or a copy of the given collection.
     *
     * @since 3.20
     *
     * @deprecated Moved to {@link CollectionsExt#toList(Collection)}.
     */
    @Deprecated
    public static <T> List<T> asList(final Collection<T> collection) {
        return CollectionsExt.toList(collection);
    }

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
     *
     * @deprecated Moved to {@link CollectionsExt#listComparator()}.
     */
    @Deprecated
    public static <T extends Comparable<T>> Comparator<List<T>> listComparator() {
        return CollectionsExt.listComparator();
    }

    /**
     * Returns a comparator for arbitrary collections of comparable elements. The elements are
     * compared in iteration order as for the {@linkplain #listComparator list comparator}.
     *
     * <em>This comparator make sense only for collections having determinist order</em>
     * like {@link java.util.TreeSet}, {@link java.util.LinkedHashSet} or queues.
     * Do <strong>not</strong> use it with {@link java.util.HashSet}.
     *
     * @param <T> The type of elements in both collections.
     * @return The ordering between two collections.
     *
     * @since 3.18 (derived from 2.5)
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T extends Comparable<T>> Comparator<Collection<T>> collectionComparator() {
        return (Comparator) CollectionsExt.listComparator();
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
     *
     * @deprecated Moved to {@link CollectionsExt#hashMapCapacity(int)}.
     */
    @Deprecated
    public static int hashMapCapacity(int elements) {
        return CollectionsExt.hashMapCapacity(elements);
    }

    /**
     * Copies the content of the given collection to a standard Java collection. This method can be
     * used when a in-memory, unsynchronized and modifiable copy of a collection is desired without
     * prior knowledge of the collection type. The following table gives the type mapping applied
     * by the method:
     * <p>
     * <table border="1" cellspacing="0" cellpadding="2">
     * <tr bgcolor="lightblue"><th>Input type</th><th>Output type</th></tr>
     * <tr><td>{@link SortedSet}</td><td>{@link TreeSet}</td></tr>
     * <tr><td>{@link HashSet}</td><td>{@link HashSet}</td></tr>
     * <tr><td>Other {@link Set}</td><td>{@link LinkedHashSet}</td></tr>
     * <tr><td>{@link Queue}</td><td>{@link LinkedList}</td></tr>
     * <tr><td>{@link List} or other {@link Collection}</td><td>{@link ArrayList}</td></tr>
     * </table>
     *
     * @param  <E> The type of elements in the collection.
     * @param  collection The collection to copy, or {@code null}.
     * @return A copy of the given collection, or {@code null} if the given collection was null.
     *
     * @since 3.18 (derived from 3.00)
     *
     * @deprecated Moved to {@link CollectionsExt#modifiableCopy(Collection)}.
     */
    @Deprecated
    public static <E> Collection<E> copy(final Collection<E> collection) {
        return CollectionsExt.modifiableCopy(collection);
    }

    /**
     * Copies the content of the given map to a standard Java map. This method can be used when a
     * in-memory, unsynchronized and modifiable copy of a map is desired without prior knowledge
     * of the map type. The following table gives the type mapping applied by the method:
     * <p>
     * <table border="1" cellspacing="0" cellpadding="2">
     * <tr bgcolor="lightblue"><th>Input type</th><th>Output type</th></tr>
     * <tr><td>{@link SortedMap}</td><td>{@link TreeMap}</td></tr>
     * <tr><td>{@link HashMap}</td><td>{@link HashMap}</td></tr>
     * <tr><td>Other {@link Map}</td><td>{@link LinkedHashMap}</td></tr>
     * </table>
     *
     * @param  <K> The type of keys in the map.
     * @param  <V> The type of values in the map.
     * @param  map The map to copy, or {@code null}.
     * @return A copy of the given map, or {@code null} if the given map was null.
     *
     * @since 3.18 (derived from 3.00)
     *
     * @deprecated Moved to {@link CollectionsExt#modifiableCopy(Map)}.
     */
    @Deprecated
    public static <K,V> Map<K,V> copy(final Map<K,V> map) {
        return CollectionsExt.modifiableCopy(map);
    }
}
