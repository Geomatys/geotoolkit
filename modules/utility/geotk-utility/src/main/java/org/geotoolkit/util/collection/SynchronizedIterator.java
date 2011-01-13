/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2011, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Iterator;
import org.geotoolkit.lang.ThreadSafe;


/**
 * An iterator synchronized on the given lock. The functionality is equivalent to the one provided
 * by {@link java.util.Collections#synchronizedSet}'s iterator, except that the synchronization is
 * performed on an arbitrary lock.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.5
 * @module
 */
@ThreadSafe
final class SynchronizedIterator<E> implements Iterator<E> {
    /**
     * The wrapped iterator.
     */
    private final Iterator<E> iterator;

    /**
     * The lock.
     */
    private final Object lock;

    SynchronizedIterator(final Iterator<E> iterator, final Object lock) {
        this.iterator = iterator;
        this.lock = lock;
    }

    /**
     * Returns {@code true} if there is more elements to iterate over.
     */
    @Override
    public boolean hasNext() {
        synchronized (lock) {
            return iterator.hasNext();
        }
    }

    /**
     * Returns the next element in iteratior order.
     */
    @Override
    public E next() {
        synchronized (lock) {
            return iterator.next();
        }
    }

    /**
     * Removes the last iterated element.
     */
    @Override
    public void remove() {
        synchronized (lock) {
            iterator.remove();
        }
    }
}
