/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;

/**
 * Provides an implementation of Iterator that will filter
 * contents using the provided filter.
 * <p>
 * This is a *Generic* iterator not limited to Feature, this
 * will become more interesting as Filter is able to evaulate
 * itself with more things then just Features.
 * </p>
 * <p>
 * This also explains the use of Collection (where you may
 * have expected a FeatureCollection). However
 * <code>FeatureCollectoin.close( iterator )</code> will be
 * called on the internal delgate.
 * </p>
 *
 * @author Jody Garnett, Refractions Research, Inc.
 * @module pending
 */
public class FilteredIterator<F extends Feature> implements Iterator<F> {

    /** Used to close the delgate, or null */
    private final FeatureCollection<? extends FeatureType, F> collection;
    private final Iterator<F> delegate;
    private final Filter filter;
    private F next;

    public FilteredIterator(final Iterator<F> iterator, final Filter filter) {
        this.collection = null;

        if(iterator == null) throw new NullPointerException("Iterator can not be null");
        if(filter == null) throw new NullPointerException("Filter can not be null");

        this.delegate = iterator;
        this.filter = filter;
    }

    public FilteredIterator(final FeatureCollection<? extends FeatureType, F> collection, final Filter filter) {
        this.collection = collection;
        this.delegate = collection.iterator();

        if(delegate == null) throw new NullPointerException("Provided collection can not generate an Iterator");
        if(filter == null) throw new NullPointerException("Filter can not be null");

        this.filter = filter;
        next = getNext();
    }

    /** Package protected, please use SubFeatureCollection.close( iterator ) */
    void close() {
        if (collection != null) {
            collection.close(delegate);
        }
        next = null;
    }

    private F getNext() {
        while (delegate.hasNext()) {
            final F item = delegate.next();
            if (filter.evaluate(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return next != null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        F current = next;
        next = getNext();
        return current;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        delegate.remove();
    }
    
}
