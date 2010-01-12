/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.StorageListener;
import org.geotoolkit.data.query.Query;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 * This object holds a serie of alteration made against the datastore
 * but thoses are not pushed on the datastore until the commit() method has been called.
 *
 * If we had follow the WFS specification this class would have been named transaction
 * but we choose to use the name Session given by JSR-170 and JSR-283 (Java Content Repository).
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface Session {

    /**
     * Get the datastore attached to this session.
     * @return Datastore, never null
     */
    DataStore getDataStore();

    /**
     * Check if the session is asynchrone.
     * If it is asynchrone then a call to commit is necessary to push all
     * changes to the datastore.
     * 
     * @return true if this session is asynchrone
     */
    boolean isAsynchrone();

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
     * Same behavior as @see DataStore#updateFeatures(org.opengis.feature.type.Name, java.util.Collection)
     * but makes modification in the session diff if this one is asynchrone.
     */
    void addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException;

    /**
     * Convinient method to update a single attribut.
     * @see #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    void updateFeatures(Name groupName, Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException;

    /**
     * Same behavior as @see DataStore#updateFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     * but makes modification in the session diff if this one is asynchrone.
     */
    void updateFeatures(Name groupName, Filter filter, Map< ? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException;

    /**
     * Same behavior as @see DataStore#removeFeatures(org.opengis.feature.type.Name, org.opengis.filter.Filter)
     * but makes modification in the session diff if this one is asynchrone.
     */
    void removeFeatures(Name groupName, Filter filter) throws DataStoreException;

    /**
     * Returns true if this session holds pending (that is, unsaved) changes; otherwise returns false. 
     */
    boolean hasPendingChanges();

    /**
     * Apply all the changes made in this session on the datastore.
     *
     * @throws DataStoreException
     */
    void commit() throws DataStoreException;

    /**
     * Revert all changes made in this session.
     */
    void rollback();

    /**
     * Same behavior as @see DataStore#getCount(org.geotoolkit.data.query.Query)
     * but take in consideration the session modifications.
     */
    long getCount(Query query) throws DataStoreException;

    /**
     * Same behavior as @see DataStore#getEnvelope(org.geotoolkit.data.query.Query)
     * but take in consideration the session modifications.
     */
    Envelope getEnvelope(Query query) throws DataStoreException;

    /**
     * Add a storage listener which will be notified when schema are added, modified or deleted
     * and when features are added, modified or deleted.
     *
     * This includes events from the datastore and events from the session.
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
