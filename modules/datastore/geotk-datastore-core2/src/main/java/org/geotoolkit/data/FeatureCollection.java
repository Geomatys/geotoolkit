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
import org.geotoolkit.data.session.Session;

import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @param <F> extends Feature
 * @module pending
 */
public interface FeatureCollection<F extends Feature> extends Collection<F> {

    String getID();

    /**
     * A collection may be linked to a session, this implies that changes maid
     * in the collection may not be send to the datastore now.
     * A session.commit() call must be done.
     * @return Session or null if not related to a session.
     */
    Session getSession();

    FeatureType getSchema();

    Envelope getEnvelope() throws DataStoreException;

    boolean isWritable();

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
     * Convinient method to update a single attribut.
     * @see #update(org.opengis.feature.type.Name, org.opengis.filter.Filter, java.util.Map)
     */
    void update(Filter filter, AttributeDescriptor desc, Object value) throws DataStoreException;

    void update(Filter filter, Map< ? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException;

    void remove(Filter filter) throws DataStoreException;

    void addListener();

    void removeListener();

}
