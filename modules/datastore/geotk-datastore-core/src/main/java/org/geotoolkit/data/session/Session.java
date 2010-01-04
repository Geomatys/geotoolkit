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

    DataStore getDataStore();

    /**
     * Check if the session is asynchrone.
     * If it is asynchrone then a call to commit is necessary to push all
     * changes to the datastore.
     * 
     * @return true if this session is asynchrone
     */
    boolean isAsynchrone();

    FeatureCollection features(Query query);

    /**
     * Get a feature iterator that can be used only for reading.
     * Use add, update and remove methods for other purposes.
     *
     * @param query
     * @return FeatureIterator
     * @throws DataStoreException
     */
    FeatureIterator getFeatureIterator(Query query) throws DataStoreException;

    void add(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException;

    /**
     * Convinient method to update a single attribut.
     * @see #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    void update(Name groupName, Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException;

    void update(Name groupName, Filter filter, Map< ? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException;

    void remove(Name groupName, Filter filter) throws DataStoreException;

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

    long getCount(Query query) throws DataStoreException;

    Envelope getEnvelope(Query query) throws DataStoreException;
    
}
