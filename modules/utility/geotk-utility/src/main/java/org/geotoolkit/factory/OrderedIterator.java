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
package org.geotoolkit.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * An iterator which move at the end of the iteration any class not loaded by the context
 * class loader or one of its parents/children.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see Factories#orderForClassLoader(Iterator, ClassLoader)
 *
 * @since 3.20
 * @module
 */
final class OrderedIterator <T> implements Iterator<T> {
    /**
     * The desired class loader (never {@code null}).
     */
    final ClassLoader classLoader;

    /**
     * The iterator given to the constructor, or the {@linkplain #fallbacks} iterator.
     */
    private Iterator<T> iterator;

    /**
     * The object of classes not loaded by the desired class loader.
     * This list is initially null and is created only if needed.
     */
    private List<T> fallbacks;

    /**
     * {@code true} if the {@linkplain #iterator} is iterating over the fallbacks.
     */
    private boolean usingFallbacks;

    /**
     * Creates a new ordered iterator.
     *
     * @param  classLoader The desired class loader.
     * @param  iterator The iterator to wrap.
     */
    OrderedIterator(final ClassLoader classLoader, final Iterator<T> iterator) {
        this.classLoader = classLoader;
        this.iterator    = iterator;
    }

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
    public T next() {
        do {
            final T next = iterator.next();
            if (next == null || usingFallbacks) {
                return next;
            }
            final ClassLoader nc = next.getClass().getClassLoader(); // May be null.
            for (ClassLoader c=classLoader; c != null;) {
                c = c.getParent(); // May be null, which we want to test.
                if (c == nc) {
                    // Loaded by the desired class loader or one of its parents.
                    // This is the case of standard services (PNG or JPEG images).
                    return next;
                }
            }
            for (ClassLoader c=nc; c!=null; c=c.getParent()) {
                if (c == classLoader) {
                    // Loaded by the desired class loader or one of its children.
                    // This is the case of services defined by the library.
                    return next;
                }
            }
            // In the tree of ClassLoaders, the 'nc' classloader is not on the
            // same "branch" than the desired classloader.
            if (fallbacks == null) {
                fallbacks = new ArrayList<>();
            }
            fallbacks.add(next);
        } while (iterator.hasNext());
        return fallbacks().next();
    }

    /**
     * Switches to the fallback iterator. This method is invoked when the iteration
     * using the iterator given at construction time is finished.
     */
    private Iterator<T> fallbacks() {
        iterator       = fallbacks.iterator();
        fallbacks      = null;
        usingFallbacks = true;
        return iterator;
    }

    /**
     * Removes the last element returned by the iterator, except if we were iterating
     * over the fallbacks. In the later case, it is to late for removing the element.
     */
    @Override
    public void remove() throws UnsupportedOperationException {
        if (usingFallbacks) {
            throw new UnsupportedOperationException();
        }
        iterator.remove();
    }
}
