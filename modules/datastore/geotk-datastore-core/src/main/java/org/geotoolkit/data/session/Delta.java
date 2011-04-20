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

import java.util.Map;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.opengis.feature.type.Name;

import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
interface Delta {

    /**
     * 
     * @return the type affected by this delta
     */
    Name getType();

    /**
     * Whenever a previous delta is commited, new or updated features
     * can have their id changed. id changed are given in this map.
     * @param idUpdates
     */
    void update(Map<String,String> idUpdates);

    Query modify(Query query);

    FeatureIterator modify(Query query, FeatureIterator iterator) throws DataStoreException;
    
    long modify(Query query, long count) throws DataStoreException;

    Envelope modify(Query query, Envelope env) throws DataStoreException;

    /**
     * @param store
     * @return Map of potentiel id changes once the commit is done
     * @throws DataStoreException
     */
    Map<String,String> commit(DataStore store) throws DataStoreException;

    void dispose();

}
