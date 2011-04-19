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
package org.geotoolkit.util.collection;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.NoSuchElementException;
import java.lang.ref.Reference;

import net.jcip.annotations.ThreadSafe;


/**
 * The set of entries in the {@link Cache#map}. On iteration, handlers will be skipped
 * and the values of weak references are returned instead of the {@link Reference} object.
 * <p>
 * This class is not needed for the normal working of {@link Cache}. it is used only if
 * the user wants to see the cache entries through the standard Java collection API.
 *
 * @param <K> The type of key objects.
 * @param <V> The type of value objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@ThreadSafe // Assuming that the set given to the constructor is concurrent.
final class CacheEntries<K,V> extends AbstractSet<Map.Entry<K,V>> {
    /**
     * The set of entries in the {@link Cache#map}.
     */
    private final Set<Map.Entry<K,Object>> entries;

    /**
     * Wraps the given set of entries of a {@link Cache#map}.
     */
    CacheEntries(final Set<Map.Entry<K,Object>> entries) {
        this.entries = entries;
    }

    /**
     * Returns {@code true} if the set is empty. Overloaded because {@code ConcurrentHashMap}
     * has a more efficient implementation of this method than testing {@code size() == 0}.
     */
    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Returns the number of entries.
     */
    @Override
    public int size() {
        return entries.size();
    }

    /**
     * Returns an iterator over the entries.
     */
    @Override
    public Iterator<Map.Entry<K,V>> iterator() {
        return new Iter<K,V>(entries.iterator());
    }

    /**
     * An iterator over the entries in the {@link Cache#map}. Handlers will be skipped and the
     * values of weak references are returned instead of the {@link Reference} object.
     */
    private static final class Iter<K,V> implements Iterator<Map.Entry<K,V>> {
        /**
         * The iterator over the entries wrapped by {@link CacheEntries}.
         */
        private final Iterator<Map.Entry<K,Object>> it;

        /**
         * The next entry to returns, or {@code null} if we reached the end of iteration.
         */
        private Map.Entry<K,V> next;

        /**
         * Creates a new iterator wrapping the given iterator from {@link CacheEntries#entries}.
         */
        Iter(final Iterator<Map.Entry<K,Object>> it) {
            this.it = it;
            advance();
        }

        /**
         * Advances the iterator to the next entry to be returned.
         */
        private void advance() {
            while (it.hasNext()) {
                final Map.Entry<K,Object> entry = it.next();
                Object value = entry.getValue();
                if (value == null || value instanceof Cache.Handler<?>) {
                    continue;
                }
                if (value instanceof Reference<?>) {
                    value = ((Reference<?>) value).get();
                    if (value == null) {
                        continue;
                    }
                }
                @SuppressWarnings("unchecked")
                final V result = (V) value;
                next = new SimpleEntry<K,V>(entry.getKey(), result);
                return;
            }
            next = null;
        }

        /**
         * Returns {@code true} if there is more element to returns.
         */
        @Override
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Returns the next element.
         */
        @Override
        public Map.Entry<K, V> next() {
            final Map.Entry<K,V> n = next;
            if (n != null) {
                advance();
                return n;
            }
            throw new NoSuchElementException();
        }

        /**
         * Unsupported operation, because the wrapped iterator is not after the proper element
         * (it is after the next one).
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
