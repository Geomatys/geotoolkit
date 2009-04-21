/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.util.collection.WeakCollectionCleaner.Disposeable;


/**
 * A hashtable-based map implementation that uses {@linkplain SoftReference soft references},
 * leaving memory when an entry is not used anymore and memory is low. This map accepts the null
 * key, but doesn't accepts null values.
 * <p>
 * This map implementation actually maintains some of the first entries as hard references.
 * Only eldest entries are retained by soft references, in order to avoid too aggressive garbage
 * collection. The amount of entries to retain by hard reference is specified at {@linkplain
 * #SoftValueHashMap(int) construction time}.
 * <p>
 * This class can be used for simple, lightweight but <strong>non concurrent</strong> caching,
 * as in the example below:
 *
 * {@preformat java
 *     K key = ...
 *     V value;
 *     synchronized (map) {
 *         value = map.get(key);
 *         if (value != null) {
 *             value = ...; // Create the value here.
 *             map.put(key, value);
 *         }
 *     }
 * }
 *
 * The calculation of a new value should be fast, because it is performed inside a synchronized
 * statement blocking all other access to the map. This is okay if that particular map instance
 * is not expected to be used in a highly concurrent environment. If concurrency are wanted,
 * consider using {@link Cache} instead.
 *
 * {@section Unsupported operations}
 * The {@link #entrySet entrySet}, {@link #keySet keySet} and {@link #values values}
 * views are supported, but iterations over those views need to be performed inside
 * a {@code synchronized(map)} block, otherwise {@link ConcurrentModificationException}
 * may be thrown randomly depending on the garbage collector activity. Note that the
 * above operations are better supported by {@link Cache}, which is a more powerfull
 * alternative to this class.
 *
 * @param <K> The type of keys in the map.
 * @param <V> The type of values in the map.
 *
 * @author Simone Giannecchini (Geosolutions)
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @see WeakValueHashMap
 * @see Cache
 *
 * @since 2.3
 * @module
 *
 * @deprecated Replaced by {@link Cache}.
 */
@Deprecated
@ThreadSafe
public class SoftValueHashMap<K,V> extends AbstractMap<K,V> {
    /**
     * The default value for {@link #hardReferencesCount}.
     */
    private static final int DEFAULT_HARD_REFERENCE_COUNT = 20;

    /**
     * The map of hard or soft references. Values are either direct reference to the objects,
     * or wrapped in a {@code Reference} object.
     */
    private final Map<K,Object> hash = new HashMap<K,Object>();

    /**
     * The FIFO list of keys to hard references. Newest elements are first, and latest elements
     * are last. This list should never be longer than {@link #hardReferencesCount}.
     */
    private final LinkedList<K> hardCache = new LinkedList<K>();

    /**
     * The number of hard references to hold internally.
     */
    private final int hardReferencesCount;

    /**
     * The entries to be returned by {@link #entrySet()}, or {@code null} if not yet created.
     */
    private transient Set<Map.Entry<K,V>> entries;

    /**
     * Creates a map with the default hard references count.
     */
    public SoftValueHashMap() {
        hardReferencesCount = DEFAULT_HARD_REFERENCE_COUNT;
    }

    /**
     * Creates a map with the specified hard references count.
     *
     * @param hardReferencesCount The maximal number of hard references to keep.
     */
    public SoftValueHashMap(final int hardReferencesCount) {
        this.hardReferencesCount = hardReferencesCount;
    }

    /**
     * Ensures that the specified value is non-null.
     */
    private static void ensureNonNull(final Object value) {
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, "value"));
        }
    }

    /**
     * Performs a consistency check on this map. This method is used for tests and
     * assertions only.
     */
    final synchronized boolean isValid() {
        int count=0, size=0;
        for (final Map.Entry<K,?> entry : hash.entrySet()) {
            if (entry.getValue() instanceof Disposeable) {
                count++;
            } else {
                assert hardCache.contains(entry.getKey());
            }
            size++;
        }
        assert size == hash.size();
        assert hardCache.size() == Math.min(size, hardReferencesCount);
        return count == Math.max(size - hardReferencesCount, 0);
    }

    /**
     * Returns the number of entries in this map.
     */
    @Override
    public synchronized int size() {
        return hash.size();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     */
    @Override
    public synchronized boolean containsKey(final Object key) {
        return hash.containsKey(key);
    }

    /**
     * Returns {@code true} if this map maps one or more keys to this value.
     */
    @Override
    public synchronized boolean containsValue(final Object value) {
        ensureNonNull(value);
        /*
         * We must rely on the super-class default implementation, not on HashMap
         * implementation, because some references are wrapped into SoftReferences.
         */
        return super.containsValue(value);
    }

    /**
     * Returns the value to which this map maps the specified key. Returns {@code null} if
     * the map contains no mapping for this key, or the value has been garbage collected.
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or {@code null} if none.
     */
    @Override
    public synchronized V get(final Object key) {
        Object value = hash.get(key);
        if (value instanceof Disposeable) {
            /*
             * The value is a soft reference only if it was not used for a while and the map
             * contains more than 'hardReferenceCount' entries. Otherwise, it is an ordinary
             * reference and is returned directly. See the 'retainStrongly' method.
             *
             * If the value is a soft reference, get the referent and clear it immediately
             * for avoiding the reference to be enqueded. We abandon the soft reference and
             * reinject the referent as a strong reference in the hash map, since we try to
             * keep the last entries by strong references.
             */
            value = ((Reference) value).getAndClear();
            if (value != null) {
                /*
                 * Transforms the soft reference into a hard one. The cast should be safe
                 * because hash.get(key) should not have returned a non-null value if the
                 * key wasn't valid.
                 */
                @SuppressWarnings("unchecked")
                final K k = (K) key;
                hash.put(k, value);
                retainStrongly(k);
            } else {
                // The value has already been garbage collected.
                hash.remove(key);
            }
        }
        /*
         * The safety of this cast depends only on this implementation, not on users.
         * It should be safe if there is no bug in the way this class manages 'hash'.
         */
        @SuppressWarnings("unchecked")
        final V v = (V) value;
        return v;
    }

    /**
     * Declares that the value for the specified key must be retained by hard reference.
     * If there is already {@link #hardReferencesCount} hard references, then this method
     * replaces the oldest hard reference by a soft one.
     */
    private void retainStrongly(final K key) {
        assert Thread.holdsLock(this);
        assert !hardCache.contains(key) : key;
        hardCache.addFirst(key);
        if (hardCache.size() > hardReferencesCount) {
            // Remove the last entry if list longer than hardReferencesCount
            final K toRemove = hardCache.removeLast();
            final Object value = hash.get(toRemove);
            assert value != null && !(value instanceof Disposeable) : toRemove;
            @SuppressWarnings("unchecked")
            final V v = (V) value;
            hash.put(toRemove, new Reference(toRemove, v));
            assert hardCache.size() == hardReferencesCount;
        }
        assert isValid();
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key Key with which the specified value is to be associated.
     * @param value Value to be associated with the specified key. The value can't be null.
     * @return Previous value associated with specified key, or {@code null}
     *         if there was no mapping for key.
     */
    @Override
    public synchronized V put(final K key, final V value) {
        ensureNonNull(value);
        Object oldValue = hash.put(key, value);
        if (oldValue instanceof Disposeable) {
            oldValue = ((Reference) oldValue).getAndClear();
        } else if (oldValue != null) {
            /*
             * The value was retained by hard reference, which implies that the key must be in
             * the hard-cache list. Removes the key from the list, since we want to reinsert it
             * at the begining of the list in order to mark the value as the most recently used.
             * This method performs a linear search, which may be quite ineficient. But it still
             * efficient enough if the key was recently used, in which case it appears near the
             * begining of the list. We assume that this is a common case. We may revisit later
             * if profiling show that this is a performance issue.
             */
            if (!hardCache.remove(key)) {
                throw new AssertionError(key);
            }
        }
        retainStrongly(key);
        @SuppressWarnings("unchecked")
        final V v = (V) oldValue;
        return v;
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     *
     * @param map Mappings to be stored in this map.
     */
    @Override
    public synchronized void putAll(final Map<? extends K, ? extends V> map) {
        super.putAll(map);
    }

    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param  key Key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or {@code null}
     *         if there was no entry for key.
     */
    @Override
    public synchronized V remove(final Object key) {
        Object oldValue = hash.remove(key);
        if (oldValue instanceof Disposeable) {
            oldValue = ((Reference) oldValue).getAndClear();
        } else if (oldValue != null) {
            /*
             * See the comment in the 'put' method.
             */
            if (!hardCache.remove(key)) {
                throw new AssertionError(key);
            }
        }
        @SuppressWarnings("unchecked")
        final V v = (V) oldValue;
        return v;
    }

    /**
     * Removes all mappings from this map.
     */
    @Override
    public synchronized void clear() {
        for (final Object value : hash.values()) {
            if (value instanceof Disposeable) {
                ((Reference) value).getAndClear();
            }
        }
        hash.clear();
        hardCache.clear();
    }

    /**
     * Returns a set view of the mappings contained in this map.
     */
    @Override
    public synchronized Set<Map.Entry<K,V>> entrySet() {
        if (entries == null) {
            entries = new Entries();
        }
        return entries;
    }

    /**
     * Compares the specified object with this map for equality.
     *
     * @param object The object to compare with this map for equality.
     */
    @Override
    public synchronized boolean equals(final Object object) {
        return super.equals(object);
    }

    /**
     * Returns the hash code value for this map.
     */
    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a string representation of this map.
     */
    @Override
    public synchronized String toString() {
        return super.toString();
    }

    /**
     * Implementation of the entries set to be returned by {@link #entrySet()}.
     */
    private final class Entries extends AbstractSet<Map.Entry<K,V>> {
        /**
         * Returns an iterator over the elements contained in this collection.
         */
        @Override
        public Iterator<Map.Entry<K,V>> iterator() {
            synchronized (SoftValueHashMap.this) {
                return new Iter();
            }
        }

        /**
         * Returns the number of elements in this collection.
         */
        @Override
        public int size() {
            return SoftValueHashMap.this.size();
        }

        /**
         * Returns {@code true} if this collection contains the specified element.
         */
        @Override
        public boolean contains(final Object entry) {
            synchronized (SoftValueHashMap.this) {
                return super.contains(entry);
            }
        }

        /**
         * Returns an array containing all of the elements in this collection.
         */
        @Override
        public Object[] toArray() {
            synchronized (SoftValueHashMap.this) {
                return super.toArray();
            }
        }

        /**
         * Returns an array containing all of the elements in this collection.
         */
        @Override
        public <T> T[] toArray(final T[] array) {
            synchronized (SoftValueHashMap.this) {
                return super.toArray(array);
            }
        }

        /**
         * Removes a single instance of the specified element from this collection,
         * if it is present.
         */
        @Override
        public boolean remove(final Object entry) {
            synchronized (SoftValueHashMap.this) {
                return super.remove(entry);
            }
        }

        /**
         * Returns {@code true} if this collection contains all of the elements
         * in the specified collection.
         */
        @Override
        public boolean containsAll(final Collection<?> collection) {
            synchronized (SoftValueHashMap.this) {
                return super.containsAll(collection);
            }
        }

        /**
         * Adds all of the elements in the specified collection to this collection.
         */
        @Override
        public boolean addAll(final Collection<? extends Map.Entry<K,V>> collection) {
            synchronized (SoftValueHashMap.this) {
                return super.addAll(collection);
            }
        }

        /**
         * Removes from this collection all of its elements that are contained in
         * the specified collection.
         */
        @Override
        public boolean removeAll(final Collection<?> collection) {
            synchronized (SoftValueHashMap.this) {
                return super.removeAll(collection);
            }
        }

        /**
         * Retains only the elements in this collection that are contained in the
         * specified collection.
         */
        @Override
        public boolean retainAll(final Collection<?> collection) {
            synchronized (SoftValueHashMap.this) {
                return super.retainAll(collection);
            }
        }

        /**
         * Removes all of the elements from this collection.
         */
        @Override
        public void clear() {
            SoftValueHashMap.this.clear();
        }

        /**
         * Returns a string representation of this collection.
         */
        @Override
        public String toString() {
            synchronized (SoftValueHashMap.this) {
                return super.toString();
            }
        }
    }

    /**
     * The iterator to be returned by {@link Entries}.
     */
    private final class Iter implements Iterator<Map.Entry<K,V>> {
        /**
         * The iterator over the {@link #hash} entries.
         */
        private final Iterator<Map.Entry<K,Object>> iterator;

        /**
         * The next entry to be returned by the {@link #next} method, or {@code null}
         * if not yet computed of if the iteration is finished.
         */
        private transient Map.Entry<K,V> entry;

        /**
         * Creates an iterator.
         */
        Iter() {
            this.iterator = hash.entrySet().iterator();
        }

        /**
         * Sets {@link #entry} to the next entry to iterate. Returns {@code true} if
         * an entry has been found, or {@code false} if the iteration is finished.
         */
        @SuppressWarnings("unchecked")
        private boolean findNext() {
            assert Thread.holdsLock(SoftValueHashMap.this);
            while (iterator.hasNext()) {
                final Map.Entry<K,Object> candidate = iterator.next();
                Object value = candidate.getValue();
                if (value instanceof Disposeable) {
                    value = ((Reference) value).get();
                    entry = new SimpleEntry<K,V>(candidate.getKey(), (V) value);
                    return true;
                }
                if (value != null) {
                    entry = (Map.Entry<K,V>) candidate;
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns {@code true} if this iterator can return more value.
         */
        @Override
        public boolean hasNext() {
            synchronized (SoftValueHashMap.this) {
                return entry!=null || findNext();
            }
        }

        /**
         * Returns the next value. If some value were garbage collected after the
         * iterator was created, they will not be returned. Note however that a
         * {@link ConcurrentModificationException} may be throw if the iteration
         * is not synchronized on {@code SoftValueHashMap.this}.
         */
        @Override
        public Map.Entry<K,V> next() {
            synchronized (SoftValueHashMap.this) {
                if (entry == null && !findNext()) {
                    throw new NoSuchElementException();
                }
                final Map.Entry<K,V> next = entry;
                entry = null; // Flags that a new entry will need to be lazily fetched.
                return next;
            }
        }

        /**
         * Removes the last entry.
         */
        @Override
        public void remove() {
            synchronized (SoftValueHashMap.this) {
                iterator.remove();
            }
        }
    }

    /**
     * A soft reference to a map entry. Soft references are created only when the map contains
     * more than {@link #hardReferencesCount}, in order to avoid to put more pressure on the
     * garbage collector.
     */
    private final class Reference extends SoftReference<V> implements Disposeable {
        /**
         * The key for the entry to be removed when the soft reference is cleared.
         */
        private final K key;

        /**
         * Creates a soft reference for the specified key-value pair.
         */
        Reference(final K key, final V value) {
            super(value, WeakCollectionCleaner.DEFAULT.queue);
            this.key  = key;
        }

        /**
         * Gets and clear this reference object. This method performs no additional operation.
         * More specifically:
         * <ul>
         *   <li>It does not enqueue the reference.</li>
         *   <li>It does not remove the reference from the hash map.</li>
         * </ul>
         * This is because this method is invoked when the entry should have already be removed,
         * or is about to be removed.</li>
         */
        final Object getAndClear() {
            assert Thread.holdsLock(SoftValueHashMap.this);
            final Object value = get();
            clear();
            return value;
        }

        /**
         * Removes the entries from the backing hash map.
         */
        @Override
        public void dispose() {
            clear();
            synchronized (SoftValueHashMap.this) {
                final Object old = hash.remove(key);
                /*
                 * If the entry was used for an other value, then put back the old value. This
                 * case may occurs if a new value was set in the hash map before the old value
                 * was garbage collected.
                 */
                if (old != this && old != null) {
                    hash.put(key, old);
                }
            }
        }
    }
}
