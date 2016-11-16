/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2016, Geomatys
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

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import org.apache.sis.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import static org.geotoolkit.db.JDBCFeatureStoreUtilities.*;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.factory.Hints;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.geometry.jts.JTS;
import org.opengis.coverage.Coverage;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;

/**
 * JDBC Feature reader, both simple and complexe features.
 * 
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class JDBCFeatureReader implements FeatureReader {
    
    protected final FeatureType type;
    protected final DefaultJDBCFeatureStore store;
    protected final PrimaryKey pkey;
    protected final String sql;
    protected final Hints hints;
    
    //array of properties for faster access when simple type
    protected final PropertyType[] properties;
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
        final GenericName typeName = type.getName();
        final String name = typeName.tip().toString();
        
        this.type = type;
        this.store = store;
        PrimaryKey pk = store.getDatabaseModel().getPrimaryKey(typeName.toString());
        this.pkey = (pk==null)? new PrimaryKey("qom") : pk;
        this.properties = this.type.getProperties(true).toArray(new PropertyType[0]);
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
        final Feature feature = type.newInstance();
        
        int k=0;
        for(final PropertyType ptype : type.getProperties(true)){
            if(ptype instanceof Operation){
                //do nothing
            }else if(ptype instanceof AttributeType){
                //single value attribut
                final Object value = readSimpleValue(store.getDialect(), rs, k+1, ptype);
                feature.setPropertyValue(ptype.getName().toString(), value);
                k++;
            }
        }

        return feature;
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
    

    public static Object readSimpleValue(final SQLDialect dialect, final ResultSet rs, int index, PropertyType desc) throws SQLException{
        if(AttributeConvention.isGeometryAttribute(desc)){
            final AttributeType gatt = (AttributeType) desc;
            final Class valueClass = gatt.getValueClass();
            if(Coverage.class.isAssignableFrom(valueClass)){
                //raster type
                final Coverage coverage;
                try {
                    coverage = dialect.decodeCoverageValue(gatt, rs, index);
                } catch (IOException e) {
                    throw new SQLException(e);
                }
                return coverage;
            }else{
                //vector type
                final Geometry geom;
                try {
                    geom = dialect.decodeGeometryValue(gatt, rs, index);
                } catch (IOException e) {
                    throw new SQLException(e);
                }

                if(geom != null && geom.getUserData() == null){
                    //set crs is not set
                    JTS.setCRS(geom, FeatureExt.getCRS(gatt));
                }
                return geom;
            }

        }else{
            return dialect.decodeAttributeValue((AttributeType)desc, rs, index);
        }
    }
    
}
