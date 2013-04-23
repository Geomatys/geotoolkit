/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.geotoolkit.data.*;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.db.reverse.DataBaseModel;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultJDBCFeatureStore extends AbstractFeatureStore implements JDBCFeatureStore{
    
    private final DataBaseModel dbmodel; 
    private final String factoryId;
    private DataSource source;
    private SQLDialect dialect;
    private String baseSchema;
    
    //number of records to retrieve with each db call.
    private final int fetchSize;
    
    public DefaultJDBCFeatureStore(final ParameterValueGroup params,final String factoryId){
        super(params);
        this.factoryId = factoryId;
        
        fetchSize = (Integer)Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.FETCHSIZE, params).getValue();
        final boolean simpleTypes = (Boolean)Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.SIMPLETYPE, params).getValue();        
        dbmodel = new DataBaseModel(this, simpleTypes); 
        
        try{
            baseSchema = (String)Parameters.getOrCreate(AbstractJDBCFeatureStoreFactory.SCHEMA, params).getValue();
        }catch(ParameterNotFoundException ex){
            //parameter migth not exist on all database implementations
        }
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(factoryId);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDefaultNamespace() {
        return super.getDefaultNamespace();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Logger getLogger() {
        return super.getLogger();
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    public void setDialect(SQLDialect dialect) {
        this.dialect = dialect;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public DataSource getDataSource() {
        return source;
    }
    
    public void setDataSource(DataSource ds){
        this.source = ds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDatabaseSchema() {
        return baseSchema;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public DataBaseModel getDatabaseModel() {
        return dbmodel;
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        ensureOpen();
        return dbmodel.getNames();
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        ensureOpen();
        return dbmodel.getFeatureType(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public long getCount(Query query) throws DataStoreException {
        return super.getCount(query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void refreshMetaModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        ensureOpen();
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        ensureOpen();
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
        ensureOpen();
        throw new DataStoreException("Not supported yet.");
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Fallback on reader/write iterator methods ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
        
    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    ////////////////////////////////////////////////////////////////////////////
    // other utils /////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    
    /**
     * Check the feature store is open.
     */
    private void ensureOpen() throws DataStoreException{
        if(source == null){
            throw new DataStoreException("JDBC Feature store has been disposed already.");
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (source != null) {
            getLogger().log(Level.WARNING,
                    "JDBC feature store has not been disposed properly. "+
                    "This may cause connexions to remain open, "+
                    "dispose feature stores when not needed anymore before dereferencing.");
            dispose();
        }
        super.finalize();
    }

    @Override
    public void dispose() {
        if (source instanceof ManageableDataSource) {
            try {
                final ManageableDataSource mds = (ManageableDataSource) source;
                source = null;
                mds.close();
            } catch (SQLException e) {
                getLogger().log(Level.WARNING, "Failed to close datasource.", e);
            }
        }
    }

}
