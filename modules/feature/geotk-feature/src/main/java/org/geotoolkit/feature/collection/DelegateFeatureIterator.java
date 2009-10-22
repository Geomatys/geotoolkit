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
package org.geotoolkit.feature.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

/**
 * A feature iterator that completely delegates to a normal
 * Iterator, simply allowing Java 1.4 code to escape the caste (sic)
 * system.
 * <p>
 * This implementation is not suitable for use with collections
 * that make use of system resources. As an alterantive please
 * see ResourceFetaureIterator.
 * </p>
 * @author Jody Garnett, Refractions Research, Inc.
 * @module pending
 */
public class DelegateFeatureIterator<F extends Feature> implements FeatureIterator<F> {

    private final Iterator<F> delegate;
    private final FeatureCollection<? extends FeatureType, F> collection;

    /**
     * Wrap the provided iterator up as a FeatureIterator.
     *
     * @param iterator Iterator to be used as a delegate.
     */
    public DelegateFeatureIterator(FeatureCollection<? extends FeatureType, F> collection, Iterator<F> iterator) {
        if(collection == null) throw new NullPointerException("FeatureCollection can not be null");
        if(iterator == null) throw new NullPointerException("Iterator can not be null");
        this.delegate = iterator;
        this.collection = collection;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() throws NoSuchElementException {
        return delegate.next();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() {
        collection.close(delegate);
    }
    
}
