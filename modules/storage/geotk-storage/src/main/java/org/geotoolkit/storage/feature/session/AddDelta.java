/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Geomatys
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

package org.geotoolkit.storage.feature.session;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.referencing.NamedIdentifier;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.feature.FeatureStore;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.storage.feature.FeatureCollection;
import org.geotoolkit.storage.feature.FeatureIterator;
import org.geotoolkit.storage.feature.query.Query;
import org.geotoolkit.storage.feature.query.QueryBuilder;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.Envelope;

/**
 * Delta which add a collection of features.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 * @todo make this concurrent
 */
public class AddDelta extends AbstractDelta{

    protected final FeatureCollection features;

    /**
     *
     * @param features  can be empty, even so it would be useless,
     * We do not check the size since this collection may be relying on
     * a feature store which may be slow or changing with time.
     * this features from the given collection will be copied.
     */
    public AddDelta(final Session session, final String typeName, final Collection<? extends Feature> features){
        super(session,typeName);
        ensureNonNull("type name", typeName);
        ensureNonNull("features", features);

        FeatureType ft;
        try {
            ft = session.getFeatureStore().getFeatureType(typeName);
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.data.session").log(Level.WARNING, null, ex);
            ft = null;
        }

        final NamedIdentifier ident = new NamedIdentifier(NamesExt.create("temp"));
        this.features = FeatureStoreUtilities.collection(ident, ft);

        //we must copy the features since they might be changed later
        final Iterator<? extends Feature> ite = features.iterator();
        try{
            while(ite.hasNext()){
                Feature sf = ite.next();
                sf = FeatureExt.deepCopy(sf);
                this.features.add(sf);
            }
        }finally{
            if(ite instanceof Closeable){
                try {
                    ((Closeable) ite).close();
                } catch (IOException ex) {
                    Logging.getLogger("org.geotoolkit.data.session").log(Level.WARNING, "Error while closing iterator : ", ex);
                }
            }
        }


    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Query modify(final Query query) {
        //add doesnt modify a query
        return query;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator modify(final Query query, final FeatureIterator reader) throws DataStoreException {
        if(!query.getTypeName().equals(type)) return reader;

        //remove the filter, it is handle at the end by the session
        //we can not filter here since some modify operation can follow
        //and change the filter result
        final QueryBuilder qb = new QueryBuilder(query);
        qb.setFilter(Filter.INCLUDE);

        final FeatureIterator affected = features.subset(qb.buildQuery()).iterator();

        final SortBy[] sort = query.getSortBy();
        if(sort != null && sort.length > 0){
            return FeatureStreams.combine(query.getSortBy(), reader, affected);
        }else{
            return FeatureStreams.sequence(reader, affected);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long modify(final Query query, final long count) throws DataStoreException{
        if(!query.getTypeName().equals(type)) return count;

        final int affected = features.subset(query).size();

        return count + affected;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope modify(final Query query, final Envelope env) throws DataStoreException {
        if(!query.getTypeName().equals(type)) return env;

        final Envelope affected = features.subset(query).getEnvelope().get();
        final JTSEnvelope2D combine = new JTSEnvelope2D(env);
        combine.expandToInclude(new JTSEnvelope2D(affected));

        return combine;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<String, String> commit(final FeatureStore store) throws DataStoreException {
        final List<FeatureId> createdIds = store.addFeatures(type.toString(), features);

        //iterator and list should have the same size
        final Map<String,String> updates = new HashMap<String, String>();
        final FeatureIterator ite = features.iterator();
        int i=0;
        try{
            if(createdIds != null && !createdIds.isEmpty()){
                while(ite.hasNext()){
                    final Feature f = ite.next();
                    final String id = FeatureExt.getId(f).getID();
                        updates.put(id, createdIds.get(i).getID());
                    i++;
                }
            }
        }finally{
            ite.close();
        }

        features.clear();
        return updates;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        features.clear();
    }

    @Override
    public void update(Map<String, String> idUpdates) {
        //nothing to update
    }

}
