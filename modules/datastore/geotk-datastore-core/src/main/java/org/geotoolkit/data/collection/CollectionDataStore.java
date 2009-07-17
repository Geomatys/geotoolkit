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

import java.io.IOException;
import java.util.Iterator;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataSourceException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.Query;
import org.geotoolkit.data.SchemaNotFoundException;
import org.geotoolkit.data.Transaction;
import org.geotoolkit.feature.collection.CollectionEvent;
import org.geotoolkit.feature.collection.CollectionListener;
import org.geotoolkit.feature.collection.FeatureCollection;
import org.geotoolkit.feature.collection.FeatureIterator;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.feature.FeatureCollectionUtilities;


/**
 * Simple data store wrapper for feature collections. Allows to use feature collections in the user
 * interface layer and everything else where a data store or a feature source is needed.
 */
public class CollectionDataStore extends AbstractDataStore {

    private final SimpleFeatureType featureType;
    private final FeatureCollection<SimpleFeatureType, SimpleFeature> collection;

    /**
     * Builds a data store wrapper around an empty collection.
     *
     * @param collection
     */
    public CollectionDataStore(final SimpleFeatureType schema) {
        this.collection = FeatureCollectionUtilities.createCollection();
        this.featureType = schema;
        collection.addListener(new FeatureCollectionListener());
    }

    /**
     * Builds a data store wrapper on top of a feature collection
     *
     * @param collection
     */
    public CollectionDataStore(final FeatureCollection<SimpleFeatureType, SimpleFeature> collection) {
        this.collection = collection;

        if (collection.size() == 0) {
            this.featureType = FeatureTypeUtilities.EMPTY;
        } else {
            Iterator iter = null;
            try {
                iter = collection.iterator();
                this.featureType = ((SimpleFeature) iter.next()).getFeatureType();
            } finally {
                if (iter != null) {
                    collection.close(iter);
                }
            }
        }

        collection.addListener(new FeatureCollectionListener());
    }

    /**
     * @see org.geotools.data.DataStore#getTypeNames()
     */
    @Override
    public String[] getTypeNames() {
        return new String[]{featureType.getTypeName()};
    }

    /**
     * @see org.geotools.data.DataStore#getSchema(java.lang.String)
     */
    @Override
    public SimpleFeatureType getSchema(final String typeName) throws IOException {
        if ((typeName != null) && typeName.equals(featureType.getTypeName())) {
            return featureType;
        }

        throw new IOException(typeName + " not available");
    }

    /**
     * Provides  FeatureReader<SimpleFeatureType, SimpleFeature> over the entire contents of <code>typeName</code>.
     * 
     * <p>
     * Implements getFeatureReader contract for AbstractDataStore.
     * </p>
     *
     * @param typeName
     *
     *
     * @throws IOException If typeName could not be found
     * @throws DataSourceException See IOException
     *
     * @see org.geotools.data.AbstractDataStore#getFeatureSource(java.lang.String)
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(final String typeName)
            throws IOException {
        return new DelegateFeatureReader<SimpleFeatureType, SimpleFeature>(getSchema(typeName), collection.features());
    }

    /**
     * Returns the feature collection held by this data store
     *
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getCollection() {
        return collection;
    }

    /**
     * @throws SchemaNotFoundException 
     * @see org.geotools.data.AbstractDataStore#getBounds(java.lang.String,
     *      org.geotools.data.Query)
     */
    @Override
    protected JTSEnvelope2D getBounds(final Query query) throws SchemaNotFoundException {
        final String featureTypeName = query.getTypeName();
        if (!featureType.getTypeName().equals(featureTypeName)) {
            throw new SchemaNotFoundException(featureTypeName);
        }

        return getBoundsInternal(query);
    }

    /**
     * @param query
     */
    protected JTSEnvelope2D getBoundsInternal(final Query query) {
        final FeatureIterator<SimpleFeature> iterator = collection.features();
        final JTSEnvelope2D envelope = new JTSEnvelope2D(featureType.getCoordinateReferenceSystem());

        if (iterator.hasNext()) {
            
            final Filter filter = query.getFilter();

            int count = 1;
            final int maxFeatures = query.getMaxFeatures();
            while (iterator.hasNext() && (count < maxFeatures)) {
                final SimpleFeature feature = iterator.next();

                if (filter.evaluate(feature)) {
                    count++;
                    envelope.expandToInclude(((Geometry) feature.getDefaultGeometry()).getEnvelopeInternal());
                }
            }
        }
        return envelope;

    }

    /**
     * @see org.geotools.data.AbstractDataStore#getCount(java.lang.String, org.geotools.data.Query)
     */
    @Override
    protected int getCount(final Query query) throws IOException {
        final String featureTypeName = query.getTypeName();
        if (!featureType.getTypeName().equals(featureTypeName)) {
            throw new SchemaNotFoundException(featureTypeName);
        }
        
        final FeatureIterator<SimpleFeature> iterator = collection.features();
        final Filter filter = query.getFilter();

        int count = 0;
        final int maxFeatures = query.getMaxFeatures();
        while (iterator.hasNext() && (count < maxFeatures)) {
            if (filter.evaluate(iterator.next())) {
                count++;
            }
        }

        return count;
    }

    /**
     * Simple listener that forwards collection events into data store events
     *
     * @author aaime
     */
    private class FeatureCollectionListener implements CollectionListener {

        @Override
        public void collectionChanged(final CollectionEvent tce) {
            final String typeName = featureType.getTypeName();
            final JTSEnvelope2D bounds = getBoundsInternal(Query.ALL);

            switch (tce.getEventType()) {
                case CollectionEvent.FEATURES_ADDED:
                    listenerManager.fireFeaturesAdded(typeName, Transaction.AUTO_COMMIT, bounds, false);
                    break;
                case CollectionEvent.FEATURES_CHANGED:
                    listenerManager.fireFeaturesChanged(typeName, Transaction.AUTO_COMMIT, bounds, false);
                    break;
                case CollectionEvent.FEATURES_REMOVED:
                    listenerManager.fireFeaturesRemoved(typeName, Transaction.AUTO_COMMIT, bounds, false);
                    break;
            }
        }
    }
}
