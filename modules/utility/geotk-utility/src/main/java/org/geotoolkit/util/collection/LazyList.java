/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.AbstractSequentialList;
import java.util.NoSuchElementException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;


/**
 * An immutable list filled when needed from the values returned by an iterator.
 *
 * @param <E> The type of elements in the list.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class LazyList<E> extends AbstractSequentialList<E> {
    /**
     * The iterator to use for filling this set.
     * Will be set to {@code null} when the iteration is over.
     */
    private Iterator<? extends E> iterator;

    /**
     * The elements in this set. This array will grown as needed.
     */
    private E[] elements;

    /**
     * Index past the last valid element. This value will increases as long as there is some
     * elements remaining in the iterator.
     */
    private int next;

    /**
     * Constructs a list to be filled using the specified iterator.
     * Iteration will occurs only when needed.
     *
     * @param iterator The iterator to use for filling this list.
     */
    @SuppressWarnings("unchecked")
    public LazyList(final Iterator<? extends E> iterator) {
        this.iterator = iterator;
        elements = (E[]) new Object[4];
    }

    /**
     * Adds the next element from the iterator to this list. This method doesn't check if more
     * elements are available; the check must have been done before to invoke this method.
     */
    private void addNext() {
        if (next >= elements.length) {
            elements = Arrays.copyOf(elements, next*2);
        }
        elements[next++] = iterator.next();
    }

    /**
     * Returns {@code true} if this list has no elements.
     */
    @Override
    public final boolean isEmpty() {
        if (next != 0) {
            return false;
        }
        if (iterator != null) {
            if (iterator.hasNext()) {
                return false;
            }
            trimToSize();
        }
        return true;
    }

    /**
     * Returns the number of elements in this list. Invoking this method
     * force the set to immediately iterates through all remaining elements.
     */
    @Override
    public final int size() {
        if (iterator != null) {
            while (iterator.hasNext()) {
                addNext();
            }
            trimToSize();
        }
        return next;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param  index The index at which to obtain an element.
     * @return The element at the given index.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public final E get(final int index) throws IndexOutOfBoundsException {
        if (index >= 0) {
            if (index < next) {
                return elements[index];
            }
            if (iterator != null) {
                while (iterator.hasNext()) {
                    addNext();
                    if (index < next) {
                        return elements[index];
                    }
                }
                trimToSize();
            }
        }
        throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.IndexOutOfBounds_1, index));
    }

    /**
     * Returns an iterator over the elements in this list.
     *
     * @param  index Index of first element to be returned from the iterator.
     * @return An iterator over the elements in this list.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public ListIterator<E> listIterator(final int index) throws IndexOutOfBoundsException {
        if (index >= 0) {
            if (index < next) {
                return new Iter(index);
            }
            if (iterator != null) {
                while (iterator.hasNext()) {
                    if (index == next) {
                        return new Iter(index);
                    }
                    addNext();
                }
                trimToSize();
            }
        }
        throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.IndexOutOfBounds_1, index));
    }

    /**
     * Invoked when we have reached the end of iteration.
     */
    private void trimToSize() {
        iterator = null; // Lets GC do its work.
        elements = ArraysExt.resize(elements, next);
    }

    /**
     * Returns {@code true} if an element exists at the given index.
     * The element is not loaded immediately.
     * <p>
     * <strong>NOTE: This method is for use by iterators only.</strong>
     * It is not suited for more general usage since it doesn't check
     * for negative index and for skipped elements.
     */
    final boolean exists(final int index) {
        return index < next || (iterator != null && iterator.hasNext());
    }

    /**
     * The iterator implementation for the {@linkplain LazySet lazy set}.
     */
    private final class Iter implements ListIterator<E> {
        /** Index of the next element to be returned. */
        private int cursor;

        /** Creates an iterator starting at the given index. */
        public Iter(final int index) {
            cursor = index;
        }

        @Override
        public boolean hasNext() {
            return exists(cursor);
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public E next() {
            if (!exists(cursor)) {
                throw new NoSuchElementException();
            }
            return get(cursor++);
        }

        @Override
        public E previous() {
            if (cursor == 0) {
                throw new NoSuchElementException();
            }
            return get(--cursor);
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
