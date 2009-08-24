/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.AbstractSet;


/**
 * An immutable set built from an iterator, which will be filled only when needed. This
 * implementation do <strong>not</strong> check if all elements in the iterator are really
 * unique; we assume that it was already verified by the caller.
 *
 * @param <E> The type of elements in the set.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public final class LazySet<E> extends AbstractSet<E> {
    /**
     * The iterator to use for filling this set.
     */
    private final Iterator<? extends E> iterator;

    /**
     * The elements in this set. This array will grown as needed.
     */
    private E[] elements;

    /**
     * The current size of this set. This size will increases as long as there is some elements
     * remaining in the iterator. This is <strong>not</strong> the size returned by {@link #size()}.
     */
    private int size;

    /**
     * Constructs a set to be filled using the specified iterator.
     * Iteration in the given iterator will occurs only when needed.
     *
     * @param iterator The iterator to use for filling the set.
     */
    @SuppressWarnings("unchecked")
    public LazySet(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
        elements = (E[]) new Object[4];
    }

    /**
     * Adds the next element from the iterator to this set. This method doesn't check
     * if more element were available; the check must have been done before to invoke
     * this method.
     */
    private void addNext() {
        if (size >= elements.length) {
            elements = Arrays.copyOf(elements, size*2);
        }
        elements[size++] = iterator.next();
    }

    /**
     * Returns an iterator over the elements contained in this set.
     * This is not the same iterator than the one given to the constructor.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iter();
    }

    /**
     * Returns the number of elements in this set. Invoking this method
     * force the set to immediately iterates through all remaining elements.
     */
    @Override
    public int size() {
        while (iterator.hasNext()) {
            addNext();
        }
        return size;
    }

    /**
     * Tests if this set has no elements.
     */
    @Override
    public boolean isEmpty() {
        return size == 0 && !iterator.hasNext();
    }

    /**
     * Returns {@code true} if an element exists at the given index.
     * The element is not loaded immediately.
     *
     * <strong>NOTE: This method is for use by iterators only.</strong>
     * It is not suited for more general usage since it doesn't check
     * for negative index and for skipped elements.
     */
    final boolean exists(final int index) {
        return index < size || iterator.hasNext();
    }

    /**
     * Returns the element at the specified position in this set.
     *
     * @param index The index at which to get an element.
     * @return The element at the requested index.
     */
    public E get(final int index) {
        while (index >= size) {
            if (!iterator.hasNext()) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            addNext();
        }
        return elements[index];
    }

    /**
     * The iterator implementation for the {@linkplain LazySet lazy set}.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     */
    private final class Iter implements Iterator<E> {
        /**
         * Index of the next element to be returned.
         */
        private int cursor;

        /**
         * Checks if there is more elements.
         */
        @Override
        public boolean hasNext() {
            return exists(cursor);
        }

        /**
         * Returns the next element.
         */
        @Override
        public E next() {
            return get(cursor++);
        }

        /**
         * Always throws an exception, since {@link LazySet} are immutable.
         */
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }
}
