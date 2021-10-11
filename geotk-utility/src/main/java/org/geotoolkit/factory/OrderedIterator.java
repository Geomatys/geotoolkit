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

import java.util.Iterator;

import org.geotoolkit.util.collection.DeferringIterator;


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
final class OrderedIterator<T> extends DeferringIterator<T> {
    /**
     * The desired class loader (never {@code null}).
     */
    final ClassLoader classLoader;

    /**
     * Creates a new ordered iterator.
     *
     * @param  classLoader The desired class loader.
     * @param  iterator The iterator to wrap.
     */
    OrderedIterator(final ClassLoader classLoader, final Iterator<T> iterator) {
        super(iterator);
        this.classLoader = classLoader;
    }

    /**
     * Returns {@code true} if the given element does not use the expected class loader.
     */
    @Override
    protected boolean isDeferred(final T element) {
        if (element == null) {
            return false;
        }
        final ClassLoader nc = element.getClass().getClassLoader(); // May be null.
        for (ClassLoader c=classLoader; c != null;) {
            c = c.getParent(); // May be null, which we want to test.
            if (c == nc) {
                // Loaded by the desired class loader or one of its parents.
                // This is the case of standard services (PNG or JPEG images).
                return false;
            }
        }
        for (ClassLoader c=nc; c!=null; c=c.getParent()) {
            if (c == classLoader) {
                // Loaded by the desired class loader or one of its children.
                // This is the case of services defined by the library.
                return false;
            }
        }
        // In the tree of ClassLoaders, the 'nc' classloader is not on the
        // same "branch" than the desired classloader.
        return true;
    }
}
