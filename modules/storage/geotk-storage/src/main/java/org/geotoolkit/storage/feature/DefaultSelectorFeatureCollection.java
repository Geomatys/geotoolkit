/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.storage.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.storage.feature.query.QueryUtilities;
import org.geotoolkit.storage.feature.session.Session;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.MismatchedFeatureException;
import org.opengis.filter.Filter;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Feature collection that takes it's source from a single selector.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultSelectorFeatureCollection extends AbstractFeatureCollection{

    private final Query query;

    public DefaultSelectorFeatureCollection(final NamedIdentifier id, final Query query, final Session session){
        super(id, session);
        this.query = query;
    }

    @Override
    public Session getSession() {
        return session;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureCollection subset(final Query query) throws DataStoreException {
        return getSession().getFeatureCollection(QueryUtilities.subQuery(this.query, query));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getType() throws FeatureStoreRuntimeException{
        try {
            return getSession().getFeatureStore().getFeatureType(query);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } catch (MismatchedFeatureException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator iterator(final Hints hints) throws FeatureStoreRuntimeException{

        final Query iteQuery;
        if(hints != null){
            final QueryBuilder qb = new QueryBuilder(this.query);
            final Hints hts = new Hints(this.query.getHints());
            hts.add(hints);
            qb.setHints(hts);
            iteQuery = qb.buildQuery();
        }else{
            iteQuery = this.query;
        }

        try {
            return getSession().getFeatureIterator(iteQuery);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int size() throws FeatureStoreRuntimeException {
        try {
            return (int) getSession().getCount(query);
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<Envelope> getEnvelope() throws FeatureStoreRuntimeException{
        try {
            return Optional.ofNullable(getSession().getEnvelope(query));
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean add(final Feature e) {
        return addAll(Collections.singletonList(e));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean addAll(final Collection<? extends Feature> clctn) {
        try {
            getSession().addFeatures(query.getTypeName(), clctn);
            return true;
        } catch (DataStoreException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public boolean isWritable(){
        try {
            return getSession().getFeatureStore().isWritable(query.getTypeName().toString());
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.data").log(Level.WARNING, null, ex);
            return false;
        }
    }

    @Override
    public boolean remove(final Object o) throws FeatureStoreRuntimeException{

        if(isWritable()){
            if(o instanceof Feature){
                ResourceId filter = FeatureExt.getId((Feature) o);
                try {
                    getSession().removeFeatures(query.getTypeName(), filter);
                    return true;
                } catch (DataStoreException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            }else{
                //trying to remove an object which is not a feature
                //it has no effect
                //should we be strict and raise an error ? or log it ?
            }

        }else{
            throw new FeatureStoreRuntimeException("this collection is readable only");
        }
        return false;
    }

    @Override
    public boolean removeAll(final Collection<?> clctn) {

        if(isWritable()){
            final Set<Filter<Object>> ids = new HashSet<>();
            final Iterator<?> ite = clctn.iterator();
            try{
                while(ite.hasNext()){
                    final Object o = ite.next();
                    if(o instanceof Feature){
                        ids.add(FeatureExt.getId((Feature) o));
                    }
                }
            }finally{
                if(ite instanceof CloseableIterator){
                    ((CloseableIterator)ite).close();
                }
            }

            Filter filter;
            switch (ids.size()) {
                case 0:  filter = null; break;
                case 1:  filter = ids.iterator().next(); break;
                default: filter = FilterUtilities.FF.or(ids); break;
            }
            if (filter != null) try {
                getSession().removeFeatures(query.getTypeName(), filter);
                return true;
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }

        }else{
            throw new FeatureStoreRuntimeException("this collection is readable only");
        }
        return false;
    }

    @Override
    public void clear() {
        if(isWritable()){
            try {
                getSession().removeFeatures(query.getTypeName(), query.getFilter());
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
        }else{
            throw new FeatureStoreRuntimeException("this collection is readable only");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void update(final Filter filter, final Map<String,?> values) throws DataStoreException {
        if (filter == Filter.include()) {
            getSession().updateFeatures(query.getTypeName(),query.getFilter(),values);
        }else{
            getSession().updateFeatures(query.getTypeName(), FilterUtilities.FF.and((Filter) query.getFilter(), filter),values);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove(final Filter filter) throws DataStoreException {
        if (filter == Filter.include()) {
            getSession().removeFeatures(query.getTypeName(),query.getFilter());
        }else{
            getSession().removeFeatures(query.getTypeName(), FilterUtilities.FF.and((Filter) query.getFilter(), filter));
        }
    }
}
