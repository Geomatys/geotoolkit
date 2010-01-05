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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.AbstractFeatureCollection;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.Envelope;

/**
 * Implementation of a collection working against a session.
 *
 * @author Johann Sorel (Geomatys)
 */
public class SessionFeatureCollection extends AbstractFeatureCollection<Feature>{

    private final Session session;
    private final Query query;

    public SessionFeatureCollection(Session session, String id, Query query){
        super(id,null);
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
        this.query = query;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection<Feature> subCollection(Query query) throws DataStoreException {
        return session.getFeatureCollection(QueryUtilities.subQuery(this.query, query));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType() throws DataStoreRuntimeException{
        try {
            FeatureType ft = session.getDataStore().getFeatureType(query.getTypeName());
            return FeatureTypeUtilities.createSubType((SimpleFeatureType) ft, query.getPropertyNames(), query.getCoordinateSystemReproject());
        } catch (DataStoreException ex) {
            throw new DataStoreRuntimeException(ex);
        } catch (SchemaException ex) {
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
        } catch (DataStoreException ex) {
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
        } catch (DataStoreException ex) {
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
        } catch (DataStoreException ex) {
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
            session.addFeatures(query.getTypeName(), clctn);
            return true;
        } catch (DataStoreException ex) {
            throw new DataStoreRuntimeException(ex);
        }
    }

    @Override
    public boolean isWritable(){
        try {
            return session.getDataStore().isWritable(query.getTypeName());
        } catch (DataStoreException ex) {
            Logger.getLogger(SessionFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean remove(Object o) throws DataStoreRuntimeException{

        if(isWritable()){
            if(o instanceof Feature){
                Id filter = FactoryFinder.getFilterFactory(null).id(Collections.singleton(((Feature)o).getIdentifier()));
                try {
                    session.removeFeatures(query.getTypeName(), filter);
                    return true;
                } catch (DataStoreException ex) {
                    throw new DataStoreRuntimeException(ex);
                }
            }else{
                //trying to remove an object which is not a feature
                //it has no effect
                //should we be strict and raise an error ? or log it ?
            }

        }else{
            throw new DataStoreRuntimeException("this collection is readable only");
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> clctn) {
        final boolean writable;

        if(isWritable()){
            final Set<Identifier> ids = new HashSet<Identifier>();

            for(Object o : clctn){
                if(o instanceof Feature){
                    ids.add(((Feature)o).getIdentifier());
                }
            }

            if(!ids.isEmpty()){
                Id filter = FactoryFinder.getFilterFactory(null).id(ids);
                try {
                    session.removeFeatures(query.getTypeName(), filter);
                    return true;
                } catch (DataStoreException ex) {
                    throw new DataStoreRuntimeException(ex);
                }
            }

        }else{
            throw new DataStoreRuntimeException("this collection is readable only");
        }
        return false;
    }

    @Override
    public void clear() {

        if(isWritable()){
            try {
                session.removeFeatures(query.getTypeName(), query.getFilter());
            } catch (DataStoreException ex) {
                throw new DataStoreRuntimeException(ex);
            }
        }else{
            throw new DataStoreRuntimeException("this collection is readable only");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(Filter filter, Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        if(filter == Filter.INCLUDE){
            session.updateFeatures(query.getTypeName(),query.getFilter(),values);
        }else{
            session.updateFeatures(query.getTypeName(),FactoryFinder.getFilterFactory(null).and(query.getFilter(), filter),values);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove(Filter filter) throws DataStoreException {
        if(filter == Filter.INCLUDE){
            session.removeFeatures(query.getTypeName(),query.getFilter());
        }else{
            session.removeFeatures(query.getTypeName(),FactoryFinder.getFilterFactory(null).and(query.getFilter(), filter));
        }
    }

}
