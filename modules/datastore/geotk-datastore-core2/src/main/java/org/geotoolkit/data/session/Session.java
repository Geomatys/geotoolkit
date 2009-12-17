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

import java.io.IOException;
import java.util.Collection;

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

    void update(Name groupName, AttributeDescriptor[] type, Object[] value, Filter filter) throws DataStoreException;

    void remove(Name groupName, Filter filter) throws DataStoreException;

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

    SessionDiff getDiff();
    
}
