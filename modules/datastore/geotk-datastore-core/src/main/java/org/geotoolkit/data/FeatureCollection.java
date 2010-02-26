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

package org.geotoolkit.data;

import java.util.Collection;
import java.util.Map;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.util.collection.CloseableIterator;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 * A java collection that may hold only features.
 * This interface offer additional methods to manipulate it's content in
 * a more normalised manner, with filter, envelope and so one.
 *
 * Still it can be used a normal java collection.
 *
 * Warning : don't forget to catch DatastoreRuntimeException that might
 * occured on some methods.
 *
 * @author Johann Sorel (Geomatys)
 * @param <F> extends Feature
 * @module pending
 */
public interface FeatureCollection<F extends Feature> extends Collection<F> {

    /**
     * A feature collection is created with an id.
     * This can be used for different purposes.
     *
     * @return String, never null
     */
    String getID();

    /**
     * A collection may be linked to a session, this implies that changes maid
     * in the collection may not be send to the datastore now.
     * A session.commit() call must be done.
     * 
     * @return Session or null if not related to a session.
     * @deprecated use getSource() instead
     */
    @Deprecated
    Session getSession();

    /**
     * A collection always takes it's datas from somewhere, it can be any kind
     * of Datastore.
     *
     * @return feature source of this collection.
     */
    Source getSource();


    /**
     * If all features in this collection are of the same type then
     * this method will return this feature type.
     * This is uses for performance reasons to avoid redondunt type test.
     *
     * @return Feature type or null if features doesn't have always the same type.
     */
    FeatureType getFeatureType();

    /**
     * Get the envelope of all features in this collection.
     *
     * @return envelope or null if there are no features or no geometrics attributs
     * available.
     * @throws DataStoreException
     */
    Envelope getEnvelope() throws DataStoreException;

    /**
     * Check if we can modify this collection.
     *
     * @return true is edition operation are possible on this collection, false otherwise.
     */
    boolean isWritable();

    /**
     * Aquiere a sub collection of features that match the query.
     * The query type name is ignore here, it will inhirite the current collection
     * type name.
     *
     * @param query
     * @return FeatureCollection , never null.
     * @throws DataStoreException
     */
    FeatureCollection<F> subCollection(Query query) throws DataStoreException;

    /**
     * Override Iterator to return a limited type FeatureIterator.
     * 
     * @return FeatureIterator
     * @throws DataStoreRuntimeException
     */
    @Override
    FeatureIterator<F> iterator() throws DataStoreRuntimeException;

    /**
     * If the collection has several sources for origine. We can iterate
     * on each source feature using this iterator.
     * This might be usefull to aquiere separete ids from each source.
     * Having this navigation possibility is a necessity otherwise we wouldn't be
     * able to allow modification on such resulting collections.
     *
     * This method is the counterpart of javax.jcr.query.QueryResult.getRows
     * from JSR-283 (Java Content Repository 2).
     */
    CloseableIterator<FeatureCollectionRow> getRows() throws DataStoreException;

    /**
     * Get an iterator using some extra hints to configure the reader parameters.
     * 
     * @param hints : Extra hints
     * @return FeatureIterator
     * @throws DataStoreRuntimeException
     */
    FeatureIterator<F> iterator(Hints hints) throws DataStoreRuntimeException;

    /**
     * Convinient method to update a single attribut.
     * @see #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    void update(Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException;

    /**
     * Update all featurss that matchthe given filter and update there attributs values
     * with the values from the given map.
     *
     * @param filter : updating filter
     * @param values : new attributs values
     * @throws DataStoreException
     */
    void update(Filter filter, Map< ? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException;

    /**
     * Remove all features from this collection that match the given filter.
     * @param filter : removing filter
     * @throws DataStoreException
     */
    void remove(Filter filter) throws DataStoreException;

    /**
     * Add a storage listener which will be notified when schema are added, modified or deleted
     * and when features are added, modified or deleted.
     * @param listener to add
     */
    void addStorageListener(StorageListener listener);

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    void removeStorageListener(StorageListener listener);

}
