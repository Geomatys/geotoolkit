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

import java.util.Map;
import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataSourceException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.data.FeatureCollectionUtilities;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Collections;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.SchemaException;
import org.opengis.feature.type.Name;

/**
 * Simple data store wrapper for feature collections. Allows to use feature collections in the user
 * interface layer and everything else where a data store or a feature source is needed.
 * @module pending
 */
public class CollectionDataStore extends AbstractDataStore {

    private final SimpleFeatureType featureType;
    private final FeatureCollection<SimpleFeatureType, SimpleFeature> collection;

    /**
     * Builds a data store wrapper around an empty collection.
     *
     * @param schema feature type
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
     * Returns the feature collection held by this data store
     */
    public FeatureCollection<SimpleFeatureType, SimpleFeature> getCollection() {
        return collection;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected JTSEnvelope2D getBounds(final Query query) throws SchemaException {
        final String featureTypeName = query.getTypeName().getLocalPart();
        if (!featureType.getTypeName().equals(featureTypeName)) {
            throw SchemaException.notFound(featureTypeName);
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
            final Integer maxFeatures = query.getMaxFeatures();
            while (iterator.hasNext() && (maxFeatures == null || count < maxFeatures)) {
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
     * {@inheritDoc }
     */
    @Override
    protected int getCount(final Query query) throws IOException {
        final String featureTypeName = query.getTypeName().getLocalPart();
        if (!featureType.getTypeName().equals(featureTypeName)) {
            throw SchemaException.notFound(featureTypeName);
        }

        final FeatureIterator<SimpleFeature> iterator = collection.features();
        final Filter filter = query.getFilter();

        int count = 0;
        final Integer maxFeatures = query.getMaxFeatures();
        while (iterator.hasNext() && (maxFeatures == null || count < maxFeatures)) {
            if (filter.evaluate(iterator.next())) {
                count++;
            }
        }

        return count;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected Map<Name, SimpleFeatureType> getTypes() throws IOException {
        return Collections.singletonMap(featureType.getName(), featureType);
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
     * @see org.geotoolkit.data.AbstractDataStore#getFeatureSource(java.lang.String)
     */
    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(Query query) throws IOException {
        return DataUtilities.wrapToReader(getSchema(query.getTypeName()), collection.features());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void createSchema(SimpleFeatureType featureType) throws IOException {
        throw new IOException("Create schema not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateSchema(Name typeName, SimpleFeatureType featureType) throws IOException {
        throw new IOException("Update schema not supported");
    }

    /**
     * Simple listener that forwards collection events into data store events
     */
    private class FeatureCollectionListener implements CollectionListener {

        @Override
        public void collectionChanged(final CollectionEvent tce) {
            final Name name = featureType.getName();
            final String typeName = name.getLocalPart();
            final JTSEnvelope2D bounds = getBoundsInternal(QueryBuilder.all(name));

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
