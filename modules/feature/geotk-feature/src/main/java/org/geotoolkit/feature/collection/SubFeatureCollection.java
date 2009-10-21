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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureCollectionUtilities;

import org.geotoolkit.feature.FeatureUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;

/**
 * Used as a reasonable default implementation for subCollection.
 * <p>
 * Note: to implementors, this is not optimal, please do your own
 * thing - your users will thank you.
 * </p>
 *
 * @author Jody Garnett, Refractions Research, Inc.
 *
 * @source $URL$
 * @module pending
 */
public class SubFeatureCollection extends AbstractFeatureCollection {

    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    /** Original Collection */
    protected final FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
    
    /** Filter */
    protected Filter filter;

    public SubFeatureCollection(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        this(collection, Filter.INCLUDE);
    }

    public SubFeatureCollection(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection, Filter subfilter) {
        super(collection.getSchema(),null);
        if (subfilter == null) {
            subfilter = Filter.INCLUDE;
        }
        if (subfilter.equals(Filter.EXCLUDE)) {
            throw new IllegalArgumentException("A subcollection with Filter.EXCLUDE would be empty");
        }
        if (collection instanceof SubFeatureCollection) {
            final SubFeatureCollection filtered = (SubFeatureCollection) collection;
            if (subfilter.equals(Filter.INCLUDE)) {
                this.collection = filtered.collection;
                this.filter = filtered.filter();
            } else {
                this.collection = filtered.collection;
                this.filter = FF.and(filtered.filter(), subfilter);
            }
        } else {
            this.collection = collection;
            this.filter = subfilter;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator openIterator() {
        return new FilteredIterator<SimpleFeature>(collection, filter());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void closeIterator(final Iterator iterator) {
        if (iterator == null) {
            return;
        }

        if (iterator instanceof FilteredIterator) {
            FilteredIterator filtered = (FilteredIterator) iterator;
            filtered.close();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        int count = 0;
        Iterator i = null;
        try {
            for (i = iterator(); i.hasNext(); count++) {
                i.next();
            }
        } finally {
            close(i);
        }
        return count;
    }

    protected Filter filter() {
        if (filter == null) {
            filter = createFilter();
        }
        return filter;
    }

    /** Override to implement subsetting */
    protected Filter createFilter() {
        return Filter.INCLUDE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<SimpleFeature> features() {
        return new DelegateFeatureIterator<SimpleFeature>(this, iterator());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(final FeatureIterator<SimpleFeature> close) {
        if (close != null) {
            close.close();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> subCollection(final Filter filter) {
        if (filter.equals(Filter.INCLUDE)) {
            return this;
        }
        if (filter.equals(Filter.EXCLUDE)) {
            // TODO implement EmptyFeatureCollection( schema )
        }
        return new SubFeatureCollection(this, filter);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isEmpty() {
        Iterator iterator = iterator();
        try {
            return !iterator.hasNext();
        } finally {
            close(iterator);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void accepts(final org.opengis.feature.FeatureVisitor visitor, final org.opengis.util.ProgressListener progress) {
        Iterator iterator = null;
        // if( progress == null ) progress = new NullProgressListener();
        try {
            float size = size();
            float position = 0;
            progress.started();
            for (iterator = iterator(); !progress.isCanceled() && iterator.hasNext(); progress.progress(position++ / size)) {
                try {
                    final SimpleFeature feature = (SimpleFeature) iterator.next();
                    visitor.visit(feature);
                } catch (Exception erp) {
                    progress.exceptionOccurred(erp);
                }
            }
        } finally {
            progress.complete();
            close(iterator);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> sort(SortBy order) {
        return new SubFeatureList(collection, filter, order);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(SimpleFeature o) {
        return collection.add(o);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        final List toDelete = FeatureUtilities.list(this);
        removeAll(toDelete);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean remove(Object o) {
        return collection.remove(o);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return collection.getID();
    }

    public int getCount() throws IOException {
        return size();
    }

    public FeatureCollection<SimpleFeatureType, SimpleFeature> collection() throws IOException {
        return this;
    }
}
