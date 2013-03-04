/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.geotoolkit.lang.Decorator;
import org.apache.sis.util.ArgumentChecks;


/**
 * An iterator deferring to the iteration end any object which comply to some criterion.
 * This class needs:
 * <p>
 * <ul>
 *   <li>An iterator to wrap (given at construction time)</li>
 *   <li>An implementation of the {@link #isDeferred(Object) isDeferred(T)} method.</li>
 * </ul>
 * <p>
 * When the {@link #next()} method is invoked, {@code DeferringIterator} first delegates to
 * the {@code next()} method of the wrapped iterator, then gives the obtained element to the
 * {@link #isDeferred(Object) isDeferred} method. If {@code isDeferred} returns {@code true},
 * then the element is returned immediately. Otherwise the element is enqueued and will be
 * returned in queue insertion order only after all non-deferred elements have been returned
 * by this iterator.
 *
 * @param <E> The type of elements to be returned by the iterator.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
@Decorator(Iterator.class)
public abstract class DeferringIterator<E> implements Iterator<E> {
    /**
     * The iterator given to the constructor, or the {@linkplain #fallbacks} iterator.
     */
    private Iterator<E> iterator;

    /**
     * The object of classes not loaded by the desired class loader.
     * This list is initially null and is created only if needed.
     */
    private List<E> fallbacks;

    /**
     * {@code true} if the {@linkplain #iterator} is iterating over the fallbacks.
     */
    private boolean usingFallbacks;

    /**
     * Creates a new deferring iterator.
     *
     * @param iterator The iterator to wrap.
     */
    protected DeferringIterator(final Iterator<E> iterator) {
        ArgumentChecks.ensureNonNull("iterator", iterator);
        this.iterator = iterator;
    }

    /**
     * Returns {@code true} if the given element should be deferred at the end of the iteration.
     * This method is invoked by the {@link #next()} method for all objects returned by the
     * wrapped iterator. If this method returns {@code true}, then the given element will be
     * returned immediately by the {@code next()} method. Otherwise the element is enqueued
     * and will be returned only after all non-deferred elements have been returned.
     *
     * @param  element The object to test. May be {@code null} if the wrapped iterator can
     *         returns null elements.
     * @return {@code true} if the given object should be deferred at the end of the iteration.
     */
    protected abstract boolean isDeferred(E element);

    /**
     * Returns {@code true} if the iteration has more elements.
     */
    @Override
    public boolean hasNext() {
        boolean hasNext = iterator.hasNext();
        if (!hasNext && !usingFallbacks && fallbacks != null) {
            hasNext = fallbacks().hasNext();
        }
        return hasNext;
    }

    /**
     * Returns the next element in the iteration.
     */
    @Override
    public E next() {
        do {
            final E next = iterator.next();
            if (usingFallbacks || !isDeferred(next)) {
                return next;
            }
            if (fallbacks == null) {
                fallbacks = new ArrayList<E>();
            }
            fallbacks.add(next);
        } while (iterator.hasNext());
        return fallbacks().next();
    }

    /**
     * Switches to the fallback iterator. This method is invoked when the iteration
     * using the iterator given at construction time is finished.
     */
    private Iterator<E> fallbacks() {
        iterator       = fallbacks.iterator();
        fallbacks      = null;
        usingFallbacks = true;
        return iterator;
    }

    /**
     * Removes the last element returned by the iterator. This method is supported only when
     * the following conditions hold:
     * <p>
     * <ul>
     *   <li>The iterator is not iterating over the deferred elements</li>
     *   <li>The wrapped iterator given to the constructor supports element removal.</li>
     * </ul>
     *
     * @throws UnsupportedOperationException If the iterator is iterating over deferred elements,
     *         or if the wrapped iterator does not support this operation.
     */
    @Override
    public void remove() throws UnsupportedOperationException {
        if (usingFallbacks) {
            throw new UnsupportedOperationException();
        }
        iterator.remove();
    }
}
