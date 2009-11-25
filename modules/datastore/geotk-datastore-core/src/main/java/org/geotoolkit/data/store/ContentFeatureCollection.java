/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geotoolkit.data.FeatureEvent;
import org.geotoolkit.data.FeatureListener;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.CollectionEvent;
import org.geotoolkit.data.collection.CollectionListener;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.collection.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.FeatureVisitor;
import org.opengis.util.ProgressListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

/**
 * A FeatureCollection that completly delegates to a backing FetaureSource.
 *
 * @author Jody Garnett (Refractions Research, Inc.)
 * @module pending
 */
public class ContentFeatureCollection implements FeatureCollection<SimpleFeatureType, SimpleFeature> {

    /**
     * feature store the collection originated from.
     */
    protected final ContentFeatureSource featureSource;
    protected final Query query;
    /**
     * feature (possibly retyped from feautre source original) type
     */
    protected final SimpleFeatureType featureType;
    /**
     * state of the feature source
     */
    protected ContentState state;
    /** Internal listener storage list */
    protected List<CollectionListener> listeners = new ArrayList<CollectionListener>(2);
    /** Set of open resource iterators */
    protected final Set open = new HashSet();
    /**
     * feature listener which listens to the feautre source and
     * forwards events to its listeners.
     */
    FeatureListener listener = new FeatureListener() {

        @Override
        public void changed(final FeatureEvent featureEvent) {
            if (listeners.isEmpty()) {
                return;
            }

            final FeatureCollection<SimpleFeatureType, SimpleFeature> collection = ContentFeatureCollection.this;
            final CollectionEvent event = new CollectionEvent(collection, featureEvent);

            final CollectionListener[] notify = listeners.toArray(new CollectionListener[listeners.size()]);
            for (CollectionListener listener : notify) {
                try {
                    listener.collectionChanged(event);
                } catch (Throwable t) {
                    //TODO: log this
                    //ContentDataStore.LOGGER.log( Level.WARNING, "Problem encountered during notification of "+event, t );
                }
            }
        }
    };

    protected ContentFeatureCollection(final ContentFeatureSource featureSource, final Query query) {
        this.featureSource = featureSource;
        this.query = query;

        //add the feautre source listener
        featureSource.addFeatureListener(listener);

        //retype feature type if necessary
        if (!query.retrieveAllProperties()) {
            this.featureType =
                    SimpleFeatureTypeBuilder.retype(featureSource.getSchema(), query.getPropertyNames());
        } else {
            this.featureType = featureSource.getSchema();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema() {
        return featureType;
    }

    /**
     * Accepts a visitor, which then visits each feature in the collection.
     * @throws IOException
     */
    @Override
    public void accepts(final FeatureVisitor visitor, final ProgressListener progress) throws IOException {
        featureSource.accepts(query, visitor, progress);
    }

    /**
     * Adds a listener for collection events.
     *
     * @param listener The listener to add
     */
    @Override
    public void addListener(final CollectionListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for collection events.
     *
     * @param listener The listener to remove
     */
    @Override
    public void removeListener(final CollectionListener listener) {
        listeners.remove(listener);
    }

    // Iterators
    public static class WrappingFeatureIterator implements FeatureIterator<SimpleFeature> {

        final FeatureReader<SimpleFeatureType, SimpleFeature> delegate;

        public WrappingFeatureIterator(final FeatureReader<SimpleFeatureType, SimpleFeature> delegate) {
            this.delegate = delegate;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean hasNext() {
            try {
                return delegate.hasNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        /**
         * {@inheritDoc }
         */
        @Override
        public SimpleFeature next() throws java.util.NoSuchElementException {
            try {
                return delegate.next();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void close() {
            try {
                delegate.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<SimpleFeature> features() {
        try {
            return new WrappingFeatureIterator(featureSource.getReader(query));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(final FeatureIterator<SimpleFeature> iterator) {
        iterator.close();
    }

    public static class WrappingIterator implements Iterator {

        final FeatureReader<SimpleFeatureType, SimpleFeature> delegate;

        public WrappingIterator(final FeatureReader<SimpleFeatureType, SimpleFeature> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            try {
                return delegate.hasNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public Object next() {
            try {
                return delegate.next();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator iterator() {
        try {
            return new WrappingIterator(featureSource.getReader(query));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close(final Iterator close) {
        try {
            ((WrappingIterator) close).delegate.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSEnvelope2D getBounds() {
        try {
            return featureSource.getBounds(query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() {
        try {
            return featureSource.getCount(query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(final SimpleFeature o) {
        return addAll(Collections.singletonList(o));
    }

    ContentFeatureStore ensureFeatureStore() {
        if (featureSource instanceof ContentFeatureStore) {
            return (ContentFeatureStore) featureSource;
        }

        throw new UnsupportedOperationException("read only");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(final Collection c) {
        final ContentFeatureStore featureStore = ensureFeatureStore();

        try {
            final List<FeatureId> ids = featureStore.addFeatures(c);
            return ids.size() == c.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(final FeatureCollection c) {
        ContentFeatureStore featureStore = ensureFeatureStore();
        try {
            List<FeatureId> ids;
            ids = featureStore.addFeatures(c);
            return ids.size() == c.size();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() {
        final ContentFeatureStore featureStore = ensureFeatureStore();

        try {
            featureStore.removeFeatures(query.getFilter());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> sort(final org.opengis.filter.sort.SortBy sort) {
        Query query = QueryBuilder.sorted(featureType.getName(), new org.opengis.filter.sort.SortBy[]{sort});
        query = QueryUtilities.mixQueries(this.query, query, null);
        return new ContentFeatureCollection(featureSource, query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<SimpleFeatureType, SimpleFeature> subCollection(final Filter filter) {
        Query query = QueryBuilder.filtered(featureType.getName(), filter);
        query = QueryUtilities.mixQueries(this.query, query, null);
        return new ContentFeatureCollection(featureSource, query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean containsAll(final Collection collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean removeAll(final Collection collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean retainAll(final Collection collection) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object[] toArray(final Object[] array) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        throw new UnsupportedOperationException();
    }

}
