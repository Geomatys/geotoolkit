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

package org.geotoolkit.data.session;

import java.util.Collection;
import java.util.Map;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.opengis.feature.Feature;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 * This object holds a serie of alteration made against the feature store
 * but thoses are not pushed on the feature store until the commit() method has been called.
 *
 * If we had follow the WFS specification this class would have been named transaction
 * but we choose to use the name Session given by JSR-170 and JSR-283 (Java Content Repository).
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface Session {

    /**
     * Get the feature store attached to this session.
     * @return FeatureStore, never null
     */
    FeatureStore getFeatureStore();

    /**
     * Check if the session is asynchrone.
     * If it is asynchrone then a call to commit is necessary to push all
     * changes to the feature store.
     * 
     * @return true if this session is asynchrone
     */
    boolean isAsynchrone();

    /**
     * Get session version. 
     * This version will be used on all queries passing through this session.
     * If a session is set, writing operations will systematicaly raise an exception.
     * 
     * @return Version, can be null
     */
    Version getVersion();
    
    /**
     * Request a collection of features that match the given query.
     *
     * @param query collections query
     * @return FeatureCollection , never null
     */
    FeatureCollection getFeatureCollection(Query query);

    /**
     * Get a feature iterator that can be used only for reading.
     * Use add, update and remove methods for other purposes.
     *
     * @param query
     * @return FeatureIterator
     * @throws DataStoreException
     */
    FeatureIterator getFeatureIterator(Query query) throws DataStoreException;

    /**
     * Same behavior as @see FeatureStore#updateFeatures(org.opengis.feature.type.Name, java.util.Collection)
     * but makes modification in the session diff if this one is asynchrone.
     * @param groupName
     * @param newFeatures
     * @throws org.apache.sis.storage.DataStoreException
     */
    void addFeatures(String groupName, Collection<? extends Feature> newFeatures) throws DataStoreException;

    /**
     * Same behavior as @see FeatureStore#updateFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     * but makes modification in the session diff if this one is asynchrone.
     * @param groupName
     * @param filter
     * @param values
     * @throws org.apache.sis.storage.DataStoreException
     */
    void updateFeatures(String groupName, Filter filter, Map<String, ?> values) throws DataStoreException;

    /**
     * Same behavior as @see FeatureStore#removeFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter)
     * but makes modification in the session diff if this one is asynchrone.
     * @param groupName
     * @param filter
     * @throws org.apache.sis.storage.DataStoreException
     */
    void removeFeatures(String groupName, Filter filter) throws DataStoreException;

    /**
     * Returns true if this session holds pending (that is, unsaved) changes; otherwise returns false. 
     * @return
     */
    boolean hasPendingChanges();

    /**
     * Apply all the changes made in this session on the featurestore.
     *
     * @throws DataStoreException
     */
    void commit() throws DataStoreException;

    /**
     * Revert all changes made in this session.
     */
    void rollback();

    /**
     * Same behavior as @see FeatureStore#getCount(org.geotoolkit.data.query.Query)
     * but take in consideration the session modifications.
     * @param query
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    long getCount(Query query) throws DataStoreException;

    /**
     * Same behavior as @see FeatureStore#getEnvelope(org.geotoolkit.data.query.Query)
     * but take in consideration the session modifications.
     * @param query
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    Envelope getEnvelope(Query query) throws DataStoreException;

    /**
     * Add a storage listener which will be notified when schema are added, modified or deleted
     * and when features are added, modified or deleted.
     *
     * This includes events from the feature store and events from the session.
     *
     * @param listener to add
     */
    void addStorageListener(StorageListener listener);

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    void removeStorageListener(StorageListener listener);

}
