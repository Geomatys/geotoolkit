/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.internal.sql.table;

import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * An iterator over an array of {@link ColumnOrParameter}. This iterator filters the elements
 * in order to return only the ones of the {@link QueryType} given to the constructor.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class ColumnOrParameterIterator<E extends ColumnOrParameter> implements ListIterator<E> {
    /**
     * The query type for which this iterator is created.
     */
    private final QueryType type;

    /**
     * {@link Query#columns} or {@link Query#parameters} at the time this iterator has been created.
     */
    private final E[] elements;

    /**
     * The elements to be returned by {@link #previous()} and {@link #next()} methods.
     */
    private E previous, next;

    /**
     * The index of next element on {@link #elements}.
     */
    private int elementIndex;

    /**
     * The iterator index returned by public methods.
     */
    public int iteratorIndex;

    /**
     * Creates an iterator for the given query type.
     */
    ColumnOrParameterIterator(final QueryType type, final E[] elements, int index) {
        this.type = type;
        this.elements = elements;
        iteratorIndex = index;
        if (index >= 0) {
            while (elementIndex < elements.length) {
                final E candidate = elements[elementIndex];
                if (candidate.indexOf(type) != 0) {
                    previous = next;
                    next = candidate;
                    if (--index < 0) {
                        return;
                    }
                }
                elementIndex++;
            }
            previous = next;
            next = null;
        }
        if (index != 0) {
            throw new IndexOutOfBoundsException(String.valueOf(iteratorIndex));
        }
    }

    /**
     * Returns {@code true} if this iterator has more elements in the forward direction.
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Returns the next element in the list.
     */
    @Override
    public E next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        previous = next;
        next = null;
        while (++elementIndex < elements.length) {
            final E candidate = elements[elementIndex];
            if (candidate.indexOf(type) != 0) {
                next = candidate;
                break;
            }
        }
        iteratorIndex++;
        assert previous.indexOf(type) == iteratorIndex : previous;
        return previous;
    }

    /**
     * Returns {@code true} if this list iterator has more elements in the reverse direction.
     */
    @Override
    public boolean hasPrevious() {
        return previous != null;
    }

    /**
     * Returns the previous element in the list.
     */
    @Override
    public E previous() {
        if (previous == null) {
            throw new NoSuchElementException();
        }
        next = previous;
        previous = null;
        while (--elementIndex > 0) {
            final E candidate = elements[elementIndex - 1];
            if (candidate.indexOf(type) != 0) {
                previous = candidate;
                break;
            }
        }
        iteratorIndex--;
        return next;
    }

    /**
     * The index of the next element.
     */
    @Override
    public int nextIndex() {
        return iteratorIndex;
    }

    /**
     * The index of the previous element.
     */
    @Override
    public int previousIndex() {
        return iteratorIndex - 1;
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void set(E e) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void add(E e) {
        throw new UnsupportedOperationException();
    }
}
