/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic iterator for a single object.
 * 
 * Iterator ite = SingletonIterator.wrap(object);
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class SingletonIterator<E> implements Iterator<E> {

    private final E singleton;
    private boolean hasNext = true;

    private SingletonIterator(final E singleton){
        this.singleton = singleton;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        if(hasNext){
            hasNext = false;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public E next() {
        if(!hasNext){
            return singleton;
        }
        throw new NoSuchElementException("Out of iterator limit.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing object from a singleton iterator is not authorized.");
    }

    public static <T extends Object> Iterator<T> wrap(final T singleton){
        return new SingletonIterator<T>(singleton);
    }

}
