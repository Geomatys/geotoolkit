/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.feature.FeatureVisitor;
import org.opengis.util.ProgressListener;

/**
 * A FeatureCollection which completley delegates to another FeatureCollection.
 * <p>
 * This class should be subclasses by classes which must somehow decorate 
 * another FeatureCollection<SimpleFeatureType, SimpleFeature> and override the relevant methods. 
 * </p>
 * @author Justin Deoliveira, The Open Planning Project, jdeolive@openplans.org
 * @since 2.5
 *
 */
public class DecoratingFeatureCollection<T extends FeatureType, F extends Feature> implements
        FeatureCollection<T, F> {

    /**
     * the delegate
     */
    protected FeatureCollection<T, F> delegate;

    protected DecoratingFeatureCollection(FeatureCollection<T, F> delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void accepts(FeatureVisitor visitor, ProgressListener progress)
            throws IOException {
        delegate.accepts(visitor, progress);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(F o) {
        return delegate.add(o);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(Collection c) {
        return delegate.addAll(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(FeatureCollection c) {
        return delegate.addAll(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener(CollectionListener listener)
            throws NullPointerException {
        delegate.addListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        delegate.clear();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(FeatureIterator<F> close) {
        delegate.close(close);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(Iterator<F> close) {
        delegate.close(close);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean containsAll(Collection c) {
        return delegate.containsAll(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<F> features() {
        return delegate.features();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSEnvelope2D getBounds() {
        return delegate.getBounds();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getSchema() {
        return delegate.getSchema();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator iterator() {
        return delegate.iterator();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean removeAll(Collection c) {
        return delegate.removeAll(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener(CollectionListener listener)
            throws NullPointerException {
        delegate.removeListener(listener);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean retainAll(Collection c) {
        return delegate.retainAll(c);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<T, F> sort(SortBy order) {
        return delegate.sort(order);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<T, F> subCollection(Filter filter) {
        return delegate.subCollection(filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object[] toArray(Object[] a) {
        return delegate.toArray(a);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return delegate.getID();
    }
}
