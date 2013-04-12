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
import org.apache.sis.metadata.ValueExistencePolicy;


/**
 * Map of restrictions for a given implementation class. This map is read-only.
 * If a metadata instance was specified at construction time, then this map is
 * <cite>live</cite>: any change in the underlying metadata will be immediately
 * reflected in this map.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 */
final class RestrictionMap extends MetadataMap<ValueRestriction> {
    /**
     * The behavior of this map toward null or empty values.
     */
    final org.apache.sis.metadata.ValueExistencePolicy content;

    /**
     * If non-null, the metadata instance to validate. This map will contain only
     * the restrictions that are violated by a property value of this metadata.
     */
    private final Object metadata;

    /**
     * Creates a restriction map for the specified accessor.
     *
     * @param accessor The accessor to use for the metadata.
     * @param metadata The metadata instance to validate, or {@code null} if none.
     * @param content  The behavior of this map toward null or empty values.
     * @param keyNames Determines the string representation of keys in the map.
     */
    RestrictionMap(final PropertyAccessor accessor, final Object metadata,
            final org.apache.sis.metadata.ValueExistencePolicy content, final org.apache.sis.metadata.KeyNamePolicy keyNames)
    {
        super(accessor, keyNames);
        this.metadata = metadata;
        this.content  = content;
    }

    /**
     * Returns the restriction at the given index, or {@code null} if none. If a metadata
     * instance has been given at construction time, then this method returns only the
     * restrictions that are violated by the property value at the given index.
     */
    final ValueRestriction restriction(final int index) {
        ValueRestriction restriction = accessor.restriction(index);
        if (restriction != null && metadata != null) {
            restriction = restriction.violation(accessor.get(index, metadata));
        }
        return restriction;
    }

    /**
     * Returns the number of elements in this map.
     */
    @Override
    public int size() {
        final int count = accessor.count();
        if (content == ValueExistencePolicy.ALL) {
            return count;
        }
        int n = 0;
        for (int i=0; i<count; i++) {
            if (restriction(i) != null) {
                n++;
            }
        }
        return n;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key.
     */
    @Override
    public boolean containsKey(final Object key) {
        return get(key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     */
    @Override
    public ValueRestriction get(final Object key) {
        if (key instanceof String) {
            return restriction(accessor.indexOf((String) key));
        }
        return null;
    }

    /**
     * Returns an iterator over the entries contained in this map.
     */
    @Override
    final Iterator<Map.Entry<String,ValueRestriction>> iterator() {
        return new Iter();
    }




    /**
     * The iterator over the elements contained in a {@link Entries} set.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.04
     *
     * @since 3.04
     */
    private final class Iter extends MetadataMap<ValueRestriction>.Iter {
        /**
         * The next property, or {@code null} if the iteration is over.
         */
        private Map.Entry<String,ValueRestriction> next;

        /**
         * Index of the element after the current {@linkplain #next} entry.
         */
        private int index;

        /**
         * {@code true} if {@link #next} has been calculated.
         */
        private boolean hasNext;

        /**
         * Creates an iterator.
         */
        Iter() {
        }

        /**
         * Returns {@code true} if the iteration has more elements.
         */
        @Override
        public boolean hasNext() {
            if (hasNext) {
                return true;
            }
            final int count = accessor.count();
            while (index < count) {
                final int index = this.index++;
                final ValueRestriction restriction = restriction(index);
                final boolean skip;
                switch (content) {
                    case ALL: {
                        skip = false; // Never skip entries.
                        break;
                    }
                    case NON_EMPTY:
                    case NON_NULL: {
                        skip = (restriction == null); // Skip null values.
                        break;
                    }
                    default: {
                        throw new AssertionError(content);
                    }
                }
                if (!skip) {
                    next = new SimpleEntry<>(accessor.name(index, keyNames), restriction);
                    hasNext = true;
                    return true;
                }
            }
            next = null;
            return false;
        }

        /**
         * Returns the next element in the iteration.
         */
        @Override
        public Map.Entry<String,ValueRestriction> next() {
            if (hasNext()) {
                hasNext = false; // For forcing the computation of next element.
                return next;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
