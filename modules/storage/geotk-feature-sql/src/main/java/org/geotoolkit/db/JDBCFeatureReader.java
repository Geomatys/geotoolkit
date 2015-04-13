/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2014, Geomatys
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import static org.geotoolkit.db.JDBCFeatureStoreUtilities.*;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.identity.FeatureId;

/**
 * JDBC Feature reader, both simple and complexe features.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JDBCFeatureReader implements FeatureReader {
    
    protected final FeatureType type;
    protected final DefaultJDBCFeatureStore store;
    protected final PrimaryKey pkey;
    protected final String sql;
    protected final String fidBase;
    protected final Hints hints;
    
    //array of properties for faster access when simple type
    protected final PropertyDescriptor[] properties;
    protected final Object[] values;
    
    /**
     * statement,result set that is being worked from.
     */
    protected final Statement st;
    protected final ResultSet rs;
    protected final Connection cx;
    protected final boolean release ;
    /** the next feature */
    private Feature feature = null;
    protected boolean closed = false;
    
    public JDBCFeatureReader(final DefaultJDBCFeatureStore store, final String sql, 
            final FeatureType type, Connection cnx, boolean release, final Hints hints) throws SQLException,DataStoreException {
        ArgumentChecks.ensureNonNull("Connection", cnx);
        final Name typeName = type.getName();
        final String name = typeName.getLocalPart();
        this.fidBase = name + ".";
        
        this.type = type;
        this.store = store;
        PrimaryKey pk = store.getDatabaseModel().getPrimaryKey(typeName);
        this.pkey = (pk==null)? new PrimaryKey("qom") : pk;
        this.properties = this.type.getDescriptors().toArray(new PropertyDescriptor[0]);
        this.values = new Object[this.properties.length];
        
        this.sql = sql;        
        this.cx = cnx;
        this.st = cx.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        this.st.setFetchSize(store.getFetchSize());
        try {
            this.rs = this.st.executeQuery(sql);
        } catch (SQLException sqle){
            throw new SQLException(sqle.getMessage()+" with query :"+ sql,sqle);
        }
        this.hints = hints;
        this.release = release;
    }
    
    public JDBCFeatureReader(final JDBCFeatureReader other) throws SQLException {
        this.type = other.type;
        this.store = other.store;
        this.pkey = other.pkey;
        this.sql = other.sql;        
        this.fidBase = other.fidBase;
        this.hints = other.hints;
        this.st = other.st;
        this.rs = other.rs;
        this.cx = other.cx;
        this.release = other.release;
        this.properties = other.properties;
        this.values = new Object[this.properties.length];
    }
    
    @Override
    public FeatureType getFeatureType() {
        return type;
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        findNext();
        final Feature f = feature;
        feature = null;
        return f;
    }

    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        findNext();
        return feature != null;
    }

    private void findNext(){
        if(feature!=null) return;
        
        try {
            if(rs.next()){
                feature = toFeature(rs);
            }
        } catch (SQLException e) {
            throw new FeatureStoreRuntimeException(e);
        } catch (DataStoreException e) {
            throw new FeatureStoreRuntimeException(e);
        }
    }
    
    protected Feature toFeature(ResultSet rs) throws SQLException, DataStoreException{
        final FeatureId fid = new DefaultFeatureId(fidBase + pkey.encodeFID(rs));
        if(type instanceof SimpleFeatureType){
            for(int i=0;i<values.length;i++){
                final PropertyDescriptor pdesc = properties[i];
                values[i] = JDBCComplexFeature.readSimpleValue(store.getDialect(), rs, i+1, pdesc);
            }
            return new DefaultSimpleFeature((SimpleFeatureType)type, fid, values.clone(), false);
        }else{
            return new JDBCComplexFeature(store, rs, type, fid);
        }
    }
    
    @Override
    public void close() {
        closed = true;
        closeSafe(store.getLogger(),(release)?cx:null,st,rs);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    protected void finalize() throws Throwable {
        if(release && !closed){
            store.getLogger().log(Level.WARNING, "A JDBC Reader has not been closed");
            close();
        }
        super.finalize();
    }
    
    
    
}
