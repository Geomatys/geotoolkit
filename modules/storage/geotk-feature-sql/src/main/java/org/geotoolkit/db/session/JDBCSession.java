/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import org.apache.sis.feature.FeatureExt;

import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreContentEvent;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.AddDelta;
import org.geotoolkit.data.session.DefaultSession;
import org.geotoolkit.data.session.Delta;
import org.geotoolkit.data.session.ModifyDelta;
import org.geotoolkit.data.session.RemoveDelta;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.version.Version;
import org.opengis.feature.Feature;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.identity.Identifier;

/**
 * Provide JDBC transaction support for asynchrone sessions.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class JDBCSession extends DefaultSession {

    /**
     * Arbitrary max number of ID in a Filter used to remove Features.
     * This is to avoid "stack depth limit exceeded" exception when
     * removing a lot of Features in a single SQL query.
     */
    private static final int MAX_ID_IN_REQUEST = 5000;

    private Connection transaction = null;
    
    public JDBCSession(DefaultJDBCFeatureStore store, boolean async, Version version) {
        super(store, async, version);
    }

    @Override
    protected AddDelta createAddDelta(Session session, String typeName, Collection<? extends Feature> features) {
        if(isAsynchrone()){
            return new JDBCAddDelta(session, typeName, features);
        }else{
            return super.createAddDelta(session, typeName, features);
        }
    }

    @Override
    protected ModifyDelta createModifyDelta(Session session, String typeName, Id filter, Map<String, ? extends Object> values) {
        if(isAsynchrone()){
            return new JDBCModifyDelta(session, typeName, filter, values);
        }else{
            return super.createModifyDelta(session, typeName, filter, values);
        }
        
    }

    @Override
    protected RemoveDelta createRemoveDelta(Session session, String typeName, Id filter) {
        if(isAsynchrone()){
            return new JDBCRemoveDelta(session, typeName, filter);
        }else{
            return super.createRemoveDelta(session, typeName, filter);
        }
    }

    @Override
    public DefaultJDBCFeatureStore getFeatureStore() {
        return (DefaultJDBCFeatureStore) super.getFeatureStore();
    }

    @Override
    public synchronized void commit() throws DataStoreException {
        final List<Delta> deltas = getDiff().getDeltas();
        final Set<GenericName> deltaChanges = new HashSet<>();
        for (Delta delta : deltas) {
            deltaChanges.add(store.getFeatureType(delta.getType()).getName());
        }

        getDiff().commit(store);
        
        //everything is ok, close transaction and diff
        if(transaction!=null){
            try {
                transaction.commit();
            } catch (SQLException ex) {
                throw new DataStoreException(ex.getMessage(),ex);
            }
        }
        closeTransaction();
        
        fireSessionChanged();

        for (GenericName deltaChange : deltaChanges) {
            ((DefaultJDBCFeatureStore)store).forwardContentEvent(FeatureStoreContentEvent.createUpdateEvent(store, deltaChange, null));
        }
    }

    public synchronized Connection getTransaction() throws DataStoreException{
        if(transaction==null){
            try{
                transaction = getFeatureStore().getDataSource().getConnection();
                transaction.setAutoCommit(false);
            }catch(SQLException ex){
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        return transaction;
    }
    
    @Override
    public synchronized void rollback() {
        super.rollback();
        closeTransaction();
    }
    
    private void closeTransaction(){
        if(transaction!=null){
            try {
                transaction.rollback();
            } catch (SQLException ex) {
                getFeatureStore().getLogger().log(Level.WARNING, ex.getMessage(), ex);
            } finally{
                try {
                    transaction.close();
                } catch (SQLException ex) {
                    getFeatureStore().getLogger().log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            transaction = null;
        }
    }

    /**
     * {@inheritDoc }
     * Override here split remove query in order to avoid
     * "stack depth limit exceeded" exception.
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        checkVersion();
        //will raise an error if the name doesn't exist
        store.getFeatureType(groupName);

        if(isAsynchrone()){

            List<Id> removeIdFilters = new ArrayList<Id>();

            //split Id filter
            if(filter instanceof Id) {
                final Id removed = (Id)filter;
                if (removed.getIDs().size() > MAX_ID_IN_REQUEST) {

                    Set<Identifier> identifiers = new HashSet<Identifier>();
                    for (Identifier id : removed.getIdentifiers()) {
                        identifiers.add(id);

                        //flush in list of filters
                        if (identifiers.size() == MAX_ID_IN_REQUEST) {
                            removeIdFilters.add(FF.id(identifiers));
                            identifiers.clear();
                        }
                    }
                    if(!identifiers.isEmpty()) {
                        removeIdFilters.add(FF.id(identifiers));
                    }

                } else {
                    removeIdFilters.add(removed);
                }

            } else {
                Set<Identifier> identifiers = new HashSet<Identifier>();
                final QueryBuilder qb = new QueryBuilder(groupName);
                qb.setFilter(filter);
                final FeatureIterator ite = getFeatureIterator(qb.buildQuery());
                try{
                    while(ite.hasNext()){
                        identifiers.add(FeatureExt.getId(ite.next()));

                        //flush in list of filters
                        if (identifiers.size() == MAX_ID_IN_REQUEST) {
                            removeIdFilters.add(FF.id(identifiers));
                            identifiers.clear();
                        }
                    }

                    if(!identifiers.isEmpty()) {
                        removeIdFilters.add(FF.id(identifiers));
                    }
                }finally{
                    ite.close();
                }

                if(removeIdFilters.isEmpty()){
                    //no feature match this filter, no need to create to remove delta
                    return;
                }
            }

            for (final Id removeIdFilter : removeIdFilters) {
                getDiff().add(createRemoveDelta(this, groupName, removeIdFilter));
            }
            fireSessionChanged();
        }else{
            store.removeFeatures(groupName, filter);
        }
    }
    
}
