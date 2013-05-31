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
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.data.session.AddDelta;
import org.geotoolkit.data.session.DefaultSession;
import org.geotoolkit.data.session.ModifyDelta;
import org.geotoolkit.data.session.RemoveDelta;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.version.Version;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Id;

/**
 * Provide JDBC transaction support for asynchrone sessions.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JDBCSession extends DefaultSession {

    private Connection transaction = null;
    
    public JDBCSession(DefaultJDBCFeatureStore store, boolean async, Version version) {
        super(store, async, version);
    }

    @Override
    protected AddDelta createAddDelta(Session session, Name typeName, Collection<? extends Feature> features) {
        if(isAsynchrone()){
            return new JDBCAddDelta(session, typeName, features);
        }else{
            return super.createAddDelta(session, typeName, features);
        }
    }

    @Override
    protected ModifyDelta createModifyDelta(Session session, Name typeName, Id filter, Map<? extends AttributeDescriptor, ? extends Object> values) {
        if(isAsynchrone()){
            return new JDBCModifyDelta(session, typeName, filter, values);
        }else{
            return super.createModifyDelta(session, typeName, filter, values);
        }
        
    }

    @Override
    protected RemoveDelta createRemoveDelta(Session session, Name typeName, Id filter) {
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
    
}
