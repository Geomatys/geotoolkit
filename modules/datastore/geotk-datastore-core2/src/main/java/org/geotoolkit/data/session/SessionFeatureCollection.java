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

import org.geotoolkit.data.DataStoreRuntimeException;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.Envelope;

/**
 * Implementation of a collection working against a session.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SessionFeatureCollection extends AbstractCollection<Feature> implements FeatureCollection<Feature>{

    private final Session session;
    private final String id;
    private final Query query;

    public SessionFeatureCollection(Session session, String id, Query query){
        if(session == null){
            throw new NullPointerException("Session can not be null.");
        }
        if(id == null){
            throw new NullPointerException("ID can not be null.");
        }
        if(query == null){
            throw new NullPointerException("Query can not be null.");
        }
        this.session = session;
        this.id = id;
        this.query = query;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getSchema() throws DataStoreRuntimeException{
        try {
            return session.getDataStore().getSchema(query.getTypeName());
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<Feature> iterator() throws DataStoreRuntimeException{
        try {
            return session.getFeatureIterator(query);
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() throws DataStoreRuntimeException {
        try {
            return (int) session.getCount(query);
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope() throws DataStoreRuntimeException{
        try {
            return session.getEnvelope(query);
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(Feature e) {
        return addAll(Collections.singletonList(e));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(Collection<? extends Feature> clctn) {
        try {
            session.add(query.getTypeName(), clctn);
            return true;
        } catch (IOException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void addListener() {
        //todo
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeListener() {
        //todo
    }

}
