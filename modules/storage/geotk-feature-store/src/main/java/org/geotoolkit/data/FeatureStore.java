/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2014, Geomatys
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.storage.DataStoreFactory;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A Featurestore is a storage object which manage a serie of FeatureTypes.
 * Depending on it's underlying storage system, the feature store may offer
 * possibility to add new types or allow writing operations.
 *
 * Performances can be completly different from one implementation to another.
 * Consider using the memory feature store is you need to work fast on a small amought
 * of datas.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface FeatureStore extends AutoCloseable {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    ParameterValueGroup getConfiguration();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    DataStoreFactory getFactory();

    /**
     * Get version history for given feature type.
     * @return VersionControl for given type.
     */
    VersionControl getVersioning(String typeName) throws VersioningException;

    /**
     * Create a session, that session may be synchrone or asynchrone.
     * If you choose it to be synchrone, every changes made in the session are directly
     * send to the feature store.
     * If you choose asynchrone mode then all changes will be send
     * only on a call to commit().
     * Commit and rollback has no effect on a synchrone session.
     *
     * @param asynchrone true if you want a session that pushes changes only on commit
     */
    Session createSession(boolean asynchrone);

    /**
     * Create a session, that session may be synchrone or asynchrone.
     * If you choose it to be synchrone, every changes made in the session are directly
     * send to the feature store.
     * If you choose asynchrone mode then all changes will be send
     * only on a call to commit().
     * Commit and rollback has no effect on a synchrone session.
     *
     * A Version gat be passed to explore features at a given state in history.
     * If store do not supported versioning, version won't have any effect.
     *
     * @param asynchrone true if you want a session that pushes changes only on commit
     * @param version wanted version, use to nagivate in history.
     */
    Session createSession(boolean asynchrone, Version version);

    /**
     * Get a collection of all available names.
     * @return {@literal Set<Name>}, never null, but can be empty.
     */
    Set<GenericName> getNames() throws DataStoreException;

    /**
     * Create a new feature type.
     *
     * @param featureType new type
     * @throws DataStoreException if type already exist or can not create schema.
     */
    void createFeatureType(FeatureType featureType) throws DataStoreException;

    /**
     * Update a feature type, should preserve attribute with the same
     * name and set default values to new attributes.
     * If the attributes type have changed, the feature store should do the best
     * effort to try to convert values.
     *
     * @param featureType new type schema
     * @throws DataStoreException if schema does not exist or can not be modified.
     */
    void updateFeatureType(FeatureType featureType) throws DataStoreException;

    /**
     * Delete feature type with given name.
     *
     * @param typeName type name to delete
     * @throws DataStoreException if schema does not exist or can not be deleted.
     */
    void deleteFeatureType(String typeName) throws DataStoreException;

    /**
     * Convenient way to aquire a schema by ignoring the namespace.
     * This should return the first schema which local part name match the
     * given typeName.
     *
     * @param typeName name of the searched type
     * @return FeatureType type for the given name
     * @throws DataStoreException if typeName doesn't exist or feature store internal error.
     */
    FeatureType getFeatureType(String typeName) throws DataStoreException;

    /**
     * Get all feature types used for given type name.
     * A type or any sub property may have sub types.
     * This method will return the main type at index 0 and all variations
     * afterward.
     * If the type is simple or has no sub types then this method will
     * return a list with one element which is the same as the one from {@link #getFeatureType(org.geotoolkit.feature.type.Name)}.
     *
     * @param typeName name of the searched type
     * @return List of complex type
     */
    List<FeatureType> getFeatureTypeHierarchy(String typeName) throws DataStoreException;

    /**
     * Some kind of queries may define a custom language statement.
     * In those cases the feature type can only be determinate by the feature store.
     */
    FeatureType getFeatureType(Query query) throws DataStoreException, MismatchedFeatureException;

    /**
     * Ask if the given type is editable. if true you can
     * get use the modification methods for this type.
     *
     * @param typeName name of the searched type
     * @return true if the type features can be edited.
     * @throws DataStoreException if typeName doesn't exist or feature store internal error.
     */
    boolean isWritable(String typeName) throws DataStoreException;

    /**
     * Retrieve informations about the query capabilites of this feature store.
     * Some feature store may not be enough "intelligent" to support all
     * parameters in the query.
     * This capabilities can be used to fetch the list of what it can handle.
     *
     * @todo move query capabilities from old feature store model
     */
    QueryCapabilities getQueryCapabilities();

    /**
     * Get the number of features that match the query.
     *
     * @param query the count query.
     * @return number of features that match the query
     */
    long getCount(Query query) throws DataStoreException;

    /**
     * Get the envelope of all features matching the given query.
     *
     * @param query features to query
     * @return Envelope, may be null if no features where found or there are no
     *  geometry fields.
     */
    Envelope getEnvelope(Query query) throws DataStoreException;

    /**
     * Add a collection of features in a group of features.
     *
     * @param groupName group where features must be added
     * @param newFeatures collection of new features
     * @return List of featureId of the added features, may be null or inexact
     * if the feature store can not handle persistent ids.
     */
    List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures) throws DataStoreException;

    /**
     * Add a collection of features in a group of features.
     *
     * @param groupName group where features must be added
     * @param newFeatures collection of new features
     * @param hints writer hints
     * @return List of featureId of the added features, may be null or inexact
     * if the feature store can not handle persistent ids.
     */
    List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException;

    /**
     * Update a set of features that match the given filter and replace
     * there attributes values by those in the given map.
     *
     * @param groupName group where features must be updated
     * @param filter updating filter, all features that match the filter will be updated
     * @param values map of values to update
     */
    void updateFeatures(String groupName, Filter filter, Map<String,?> values) throws DataStoreException;

    /**
     *
     * @param groupName group where features must be deleted
     * @param filter deleting filter, all features that match the filter will be removed
     */
    void removeFeatures(String groupName, Filter filter) throws DataStoreException;

    /**
     * Get a feature reader to iterate on.
     *
     * @param query requested parameters
     * @return FeatureReader, never null but can be empty
     */
    FeatureReader getFeatureReader(Query query) throws DataStoreException;

    /**
     * Aquire a writer on a given feature type.
     *
     * @param query requested parameters
     */
    FeatureWriter getFeatureWriter(Query query) throws DataStoreException;

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

    /**
     * refresh metaModel (in case someone else had changed by an other way)
     */
    void refreshMetaModel() throws DataStoreException;

    void close() throws DataStoreException;

}
