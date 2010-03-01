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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;

/**
 * A Datastore is a storage object which manage a serie of FeatureTypes.
 * Depending on it's underlying storage system, the datastore may offer
 * possibility to add new types or allow writing operations.
 *
 * Performances can be completly different from one implementation to another.
 * Consider using the memory datastore is you need to work fast on a small amought
 * of datas.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface DataStore {

    /**
     * Create a session, that session may be synchrone or asynchrone.
     * If you choose it to be synchrone, every changes made in the session are directly
     * send to the datastore.
     * If you choose asynchrone mode then all changes will be send
     * only on a call to commit().
     * Commit and rollback has no effect on a synchrone session.
     *
     * @param asynchrone : true if you want a session that pushes changes only on commit
     * @return Session
     */
    Session createSession(boolean asynchrone);

    /**
     * Convinient way to aquiere all names by ignoring the namespaces.
     * 
     * @return String array
     * @throws DataStoreException
     */
    String[] getTypeNames() throws DataStoreException;

    /**
     * Get a collection of all available names.
     * @return Set<Name> , never null, but can be empty.
     * @throws DataStoreException
     */
    Set<Name> getNames() throws DataStoreException;

    /**
     * Create a new schema.
     *
     * @param typeName , new type name
     * @param featureType , new type schema
     * @throws DataStoreException if schema already exist or can not create schema.
     */
    void createSchema(Name typeName, FeatureType featureType) throws DataStoreException;

    /**
     * Update a schema, should preserve attribut with the same
     * name and set default values to new attributs.
     * If the attributs type have changed, the datastore should do the best
     * effort to try to convert values.
     *
     * @param typeName , type name to update
     * @param featureType , new type schema
     * @throws DataStoreException if schema does not exist or can not be modified.
     */
    void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException;

    /**
     * Delete a schema.
     * 
     * @param typeName , type name to delete
     * @throws DataStoreException if schema does not exist or can not be deleted.
     */
    void deleteSchema(Name typeName) throws DataStoreException;

    /**
     * Convinient way to aquiere a schema by ignoring the namespace.
     * This should return the first schema which localpart name match the
     * given typeName.
     * 
     * @return FeatureType
     * @throws DataStoreException
     */
    FeatureType getFeatureType(String typeName) throws DataStoreException;

    /**
     * Get the feature type for the give name.
     *
     * @param typeName name of the searched type
     * @return FeatureType type for the given name
     * @throws DataStoreException if typeName doesnt exist or datastore internal error.
     */
    FeatureType getFeatureType(Name typeName) throws DataStoreException;

    /**
     * Ask if the given type is editable. if true you can
     * get use the modification methods for this type.
     *
     * @param typeName name of the searched type
     * @return true if the type features can be edited.
     * @throws DataStoreException if typeName doesnt exist or datastore internal error.
     */
    boolean isWritable(Name typeName) throws DataStoreException;

    /**
     * Retrieve informations about the query capabilites of this datastore.
     * Some datastore may not be enough "intelligent" to support all
     * parameters in the query.
     * This capabilities can be used to fetch the list of what it can handle.
     *
     * @return QueryCapabilities
     * @todo move query capabilities from old datastore model
     */
    QueryCapabilities getQueryCapabilities();

    /**
     * Get the number of features that match the query.
     *
     * @param query the count query.
     * @return number of features that match the query
     * @throws DataStoreException
     */
    long getCount(Query query) throws DataStoreException;

    /**
     * Get the envelope of all features matching the given query.
     * 
     * @param query : features to query
     * @return Envelope or null if no features where found.
     * @throws IOException : error occured while reading
     */
    Envelope getEnvelope(Query query) throws DataStoreException;

    /**
     * Add a collection of features in a group of features.
     *
     * @param groupName , group where features must be added
     * @param newFeatures , collection of new features
     * @return List of featureId of the added features, may be null or inexact
     * if the datastore can not handle persistant ids.
     * @throws DataStoreException
     */
    List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException;

    /**
     * Convinient method to update a single attribut.
     * @see #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    void updateFeatures(Name groupName, Filter filter, PropertyDescriptor desc, Object value) throws DataStoreException;

    /**
     * Update a set of features that match the given filter and replace
     * there attributs values by thoses in the given map.
     *
     * @param groupName , group where features must be updated
     * @param filter , updating filter, all features that match the filter will be updated
     * @param values , map of values to update
     * @throws DataStoreException
     */
    void updateFeatures(Name groupName, Filter filter, Map< ? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException;

    /**
     *
     * @param groupName , group where features must be deleted
     * @param filter , deleting filter, all features that match the filter will be removed
     * @throws DataStoreException
     */
    void removeFeatures(Name groupName, Filter filter) throws DataStoreException;

    /**
     * Get a feature reader to iterate on.
     *
     * @param query , requested parameters
     * @return FeatureReader , never null but can be empty
     * @throws DataStoreException
     */
    FeatureReader getFeatureReader(Query query) throws DataStoreException;

    /**
     * Aquiere a writer on a given featuretype in modify mode.
     *
     * @param typeName , type name
     * @param filter , limit features to only thoses that match this filter
     * @return FeatureWriter , never null but can be empty.
     * @throws DataStoreException
     */
    FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException;

    /**
     * Aquiere a writer on a given featuretype in append mode.
     *
     * @param typeName , type name
     * @return FeatureWriter , empty.
     * @throws DataStoreException
     */
    FeatureWriter getFeatureWriterAppend(Name typeName) throws DataStoreException;

    /**
     * Dispose the datastore caches and underlying resources.
     * The datastore should not be used after this call or it may raise errors.
     */
    void dispose();

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
