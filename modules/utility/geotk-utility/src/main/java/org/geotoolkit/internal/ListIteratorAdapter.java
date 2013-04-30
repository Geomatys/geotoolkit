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
package org.geotoolkit.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import org.apache.sis.util.resources.Errors;


/**
 * @deprecated Unfinished implementation - some tests are still failing.
 * This class was originally targeted to Apache SIS, then abandoned because
 * the plan became too difficult to do right. Still class still have bugs
 * (see {@code ListIteratorAdapterTest.testRandomReadWrite()}).
 * We put this class in Geotk for now in case we have a new need for this work.
 *
 * Provides {@link ListIterator} for arbitrary {@link Collection} instances.
 * This adapter is used when the collection may or may not be a {@link List}.
 * The factory methods are:
 *
 * <ul>
 *   <li>{@link #first(Iterable)} : initial call to {@link #next()} will return the first element.</li>
 *   <li>{@link #last(Collection)} : initial call to {@link #previous()} will return the last element.</li>
 * </ul>
 *
 * {@section Restrictions}
 * This iterator requires the iteration order to be stable. For example it is okay for {@code EnumSet}
 * or {@code LinkedHashSet}, but not for {@code HashSet}. If iteration order is not stable, then calls
 * to {@link #remove()} may throw {@link ConcurrentModificationException}. However no random removals
 * should occurs.
 *
 * <p>This iterator does not support the {@link #add(Object)} and {@link #set(Object)} operations.</p>
 *
 * @param <E> The type of elements to be returned by the iterator.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @since   3.22
 * @version 3.22
 * @module
 */
public final class ListIteratorAdapter<E> implements ListIterator<E> {
    /**
     * The wrapped iterator. A single instance will be used if there is no removal operations.
     * However if a value is removed after a call to {@link #previous()}, then we will need to
     * get a new iterator in order to re-iterate over the previous elements.
     */
    private Iterator<E> iterator;

    /**
     * The collection from which to get the iterator. We keep this reference in order
     * to fetch a new iterator if we need to remove a previous element.
     */
    private final Iterable<E> collection;

    /**
     * The previous elements returned by this iterator.
     */
    private E[] previous;

    /**
     * Number of valid elements in the {@link #previous} array.
     */
    private int countOfPrevious;

    /**
     * Index of the next value to be returned by {@link #iterator}.
     */
    private int iteratorPosition;

    /**
     * Index to be returned by {@link #nextIndex()}.
     */
    private int nextIndex;

    /**
     * {@code true} if it is okay to invoke {@link #remove()}.
     */
    private boolean canRemove;

    /**
     * Creates a new adapter for the given iterator.
     */
    @SuppressWarnings("unchecked")
    private ListIteratorAdapter(final Iterator<E> iterator, final Iterable<E> collection, final int initialCapacity) {
        this.iterator   = iterator;
        this.collection = collection;
        this.previous   = (E[]) new Object[initialCapacity];
    }

    /**
     * Returns a list iterator for the given collection with an initial position of 0.
     *
     * @param  <E> The type of elements in the collection.
     * @param  collection The collection for which to get an iterator.
     * @return A list iterator for the given collection with an initial position of 0.
     */
    public static <E> ListIterator<E> first(final Collection<E> collection) {
        if (collection == null) {
            return Collections.emptyListIterator();
        }
        if (collection instanceof List<?>) {
            return ((List<E>) collection).listIterator();
        }
        final Iterator<E> iterator = collection.iterator();
        if (iterator instanceof ListIterator<?>) {
            return (ListIterator<E>) iterator;
        }
        return new ListIteratorAdapter<>(iterator, collection, collection.size());
    }

    /**
     * Returns a list iterator for the given collection with an initial position of 0.
     *
     * @param  <E> The type of elements in the collection.
     * @param  collection The collection for which to get an iterator.
     * @return A list iterator for the given collection with an initial position of 0.
     */
    public static <E> ListIterator<E> last(final Collection<E> collection) {
        if (collection == null) {
            return Collections.emptyListIterator();
        }
        int i = collection.size();
        if (collection instanceof List<?>) {
            return ((List<E>) collection).listIterator(i);
        }
        Iterator<E> iterator = collection.iterator();
        if (!(iterator instanceof ListIterator<?>)) {
            iterator = new ListIteratorAdapter<>(iterator, collection, i);
        }
        while (--i >= 0) {
            iterator.next();
        }
        return (ListIterator<E>) iterator;
    }

    /**
     * Returns the index of the element to be returned by {@link #next()},
     * or the list size if the iterator is at the end of the list.
     */
    @Override
    public int nextIndex() {
        return nextIndex;
    }

    /**
     * Returns the index of the element to be returned by {@link #previous()},
     * or -1 if the iterator is at the beginning of the list.
     */
    @Override
    public int previousIndex() {
        return nextIndex - 1;
    }

    /**
     * Returns {@code true} if {@link #next()} can return an element.
     */
    @Override
    public boolean hasNext() {
        return (nextIndex < iteratorPosition) || iterator.hasNext();
    }

    /**
     * Returns {@code true} if {@link #previous()} can return an element.
     */
    @Override
    public boolean hasPrevious() {
        return nextIndex != 0;
    }

    /**
     * Returns the next element.
     */
    @Override
    public E next() {
        if (nextIndex < iteratorPosition) {
            return previous[nextIndex++];
        }
        final E element = iterator.next();
        if (iteratorPosition < countOfPrevious) {
            if (previous[iteratorPosition] != element) {
                throw new ConcurrentModificationException();
            }
        } else {
            if (iteratorPosition == previous.length) {
                previous = Arrays.copyOf(previous, iteratorPosition*2);
            }
            previous[iteratorPosition] = element;
            countOfPrevious = iteratorPosition + 1;
        }
        nextIndex = ++iteratorPosition;
        canRemove = true;
        return element;
    }

    /**
     * Returns the previous element.
     */
    @Override
    public E previous() {
        if (nextIndex != 0) {
            final E element = previous[--nextIndex];
            canRemove = true;
            return element;
        }
        throw new NoSuchElementException();
    }

    /**
     * Removes the element returned by the last call to {@link #next()} or {@link #previous()}.
     */
    @Override
    public void remove() {
        if (!canRemove) {
            throw new IllegalStateException();
        }
        if (nextIndex == iteratorPosition) {
            nextIndex = --iteratorPosition;
            iterator.remove();
        } else {
            iterator = collection.iterator();
            int i = nextIndex;
            try {
                do iterator.next();
                while (--i >= 0);
            } catch (NoSuchElementException e) {
                throw new ConcurrentModificationException(e);
            }
            iterator.remove();
            iteratorPosition = nextIndex;
            System.arraycopy(previous, nextIndex+1, previous, nextIndex, countOfPrevious - (nextIndex + 1));
        }
        previous[--countOfPrevious] = null;
        canRemove = false;
    }

    /**
     * Unsupported operation.
     *
     * @param element Ignored.
     */
    @Override
    public void set(E element) {
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UnsupportedOperation_1, "set"));
    }

    /**
     * Unsupported operation.
     *
     * @param element Ignored.
     */
    @Override
    public void add(E element) {
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UnsupportedOperation_1, "add"));
    }
}
