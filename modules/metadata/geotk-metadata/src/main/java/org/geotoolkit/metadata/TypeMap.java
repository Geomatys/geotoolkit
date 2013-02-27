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
package org.geotoolkit.metadata;

import java.util.Map;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Map of property types for a given implementation class. This map is read-only.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.03
 * @module
 */
final class TypeMap extends MetadataMap<Class<?>> {
    /**
     * The kind of values in this map.
     */
    final TypeValuePolicy types;

    /**
     * Creates a type map for the specified accessor.
     *
     * @param accessor The accessor to use for the metadata.
     * @param types    The kind of values in this map.
     * @param keyNames Determines the string representation of keys in the map..
     */
    TypeMap(final PropertyAccessor accessor, final TypeValuePolicy types, final org.apache.sis.metadata.KeyNamePolicy keyNames) {
        super(accessor, keyNames);
        this.types = types;
    }

    /**
     * Returns the number of entries in this map.
     */
    @Override
    public int size() {
        return accessor.count();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(final Object key) {
        return (key instanceof String) && accessor.indexOf((String) key) >= 0;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     */
    @Override
    public Class<?> get(final Object key) {
        if (key instanceof String) {
            final int index = accessor.indexOf((String) key);
            return (index >= 0) ? accessor.type(index, types) : null;
        }
        return null;
    }

    /**
     * Returns an iterator over the entries contained in this map.
     */
    @Override
    final Iterator<Map.Entry<String,Class<?>>> iterator() {
        return new Iter();
    }

    /**
     * The iterator over the entries contained in a {@link Entries} set.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.04
     *
     * @since 3.03
     */
    private final class Iter extends MetadataMap<Class<?>>.Iter {
        /**
         * Index of the next element (initially 0).
         */
        private int next;

        /**
         * Creates en iterator.
         */
        Iter() {
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         */
        @Override
        public boolean hasNext() {
            return next < accessor.count();
        }

        /**
         * Returns the next element in the iteration.
         */
        @Override
        public Map.Entry<String,Class<?>> next() {
            final PropertyAccessor pa = accessor;
            final int n = next;
            if (n >= pa.count()) {
                throw new NoSuchElementException();
            }
            next++;
            return new SimpleEntry<String,Class<?>>(pa.name(n, keyNames), pa.type(n, types));
        }
    }
}
