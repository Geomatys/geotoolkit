/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
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
 * Sequence of iterator merge as a single iterator.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SequenceIterator<F extends Object> implements CloseableIterator<F> {

    private final Iterator<F>[] wrapped;
    private int currentIndex = 0;
    private Iterator<F> active = null;

    public SequenceIterator(Iterator<F> ... wrapped) {
        if (wrapped == null || wrapped.length == 0 || wrapped[0] == null) {
            throw new IllegalArgumentException("Iterators can not be empty or null");
        }
        this.wrapped = wrapped;
        active = wrapped[0];
    }

    @Override
    public F next() {
        if (active == null) {
            throw new NoSuchElementException("No more elements");
        } else {
            return active.next();
        }
    }

    @Override
    public void close() {
        for (Iterator ite : wrapped) {
            if(ite instanceof CloseableIterator){
                ((CloseableIterator)ite).close();
            }
        }
    }

    @Override
    public boolean hasNext() {

        if (active == null) {
            return false;
        }

        if (active.hasNext()) {
            return true;
        } else {
            if(active instanceof CloseableIterator){
                ((CloseableIterator)active).close();
            }
        }

        currentIndex++;
        while (currentIndex < wrapped.length) {
            active = wrapped[currentIndex];

            if (active.hasNext()) {
                return true;
            } else {
                if(active instanceof CloseableIterator){
                    ((CloseableIterator)active).close();
                }
            }

            currentIndex++;
        }

        return false;
    }

    @Override
    public void remove() {
        if (active != null) {
            active.remove();
        }
    }
}
