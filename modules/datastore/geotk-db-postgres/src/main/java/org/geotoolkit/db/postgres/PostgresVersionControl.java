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
package org.geotoolkit.db.postgres;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.db.JDBCFeatureStoreUtilities;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.version.AbstractVersionControl;
import org.geotoolkit.version.Version;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Manage versioning for a given feature type.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class PostgresVersionControl extends AbstractVersionControl{

    private final PostgresFeatureStore featureStore;
    private final FeatureType featureType;
    private final SQLDialect dialect;
    private Boolean isVersioned = null;
    
    
    public PostgresVersionControl(PostgresFeatureStore featureStore, FeatureType featureType) {
        this.featureStore = featureStore;
        this.featureType = featureType;
        this.dialect = featureStore.getDialect();
    }

    @Override
    public synchronized boolean isEditable() {
        return true;
    }

    @Override
    public synchronized void startVersioning() throws VersioningException {
        if(isVersioned()){
            //versioning already active, do nothing
            return;
        }
        
        //install history functions, won't do anything if already present
        featureStore.installHSFunctions();
        
        final String schemaName = featureStore.getDatabaseSchema();
        final String tableName = featureType.getName().getLocalPart();
            
        Connection cnx = null;
        Statement stmt = null;
        try{
            cnx = featureStore.getDataSource().getConnection();
            stmt = cnx.createStatement();  
            
            final StringBuilder sb = new StringBuilder("SELECT \"HS_CreateHistory\"(");
            sb.append('\'');
            if(schemaName!=null && !schemaName.isEmpty()){
                sb.append(schemaName).append('.');
            }
            sb.append(tableName);
            sb.append('\'');
            sb.append(',');
            
            final List<String> hsColumnNames = new ArrayList<String>();
            for(PropertyDescriptor desc : featureType.getDescriptors()){
//                if(Boolean.TRUE.equals(desc.getUserData().get(HintsPending.PROPERTY_IS_IDENTIFIER))){
//                    //ignore this column is historisation
//                    continue;
//                }
                hsColumnNames.add("'"+desc.getName().getLocalPart()+"'");
            }
            sb.append("array[");
            for(int i=0,n=hsColumnNames.size();i<n;i++){
                if(i!=0) sb.append(',');
                sb.append(hsColumnNames.get(i));
            }
            
            sb.append("]);");
            stmt.executeQuery(sb.toString());
            
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(), ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(featureStore.getLogger(), cnx, stmt, null);
        }
        
        //clear cache
        isVersioned = null;
    }

    @Override
    public synchronized void dropVersioning() throws VersioningException {
        if(!isVersioned()){
            //versioning not active, do nothing
            return;
        }
        
        final String schemaName = featureStore.getDatabaseSchema();
        final String tableName = featureType.getName().getLocalPart();
        
        Connection cnx = null;
        Statement stmt = null;
        try{
            cnx = featureStore.getDataSource().getConnection();
            stmt = cnx.createStatement();  
            
            final StringBuilder sb = new StringBuilder("SELECT \"HS_DropHistory\"(");          
            dialect.encodeSchemaAndTableName(sb, schemaName, tableName);
            sb.append(");");
            stmt.executeQuery(sb.toString());
            
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(), ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(featureStore.getLogger(), cnx, stmt, null);
        }
        
        //clear cache
        isVersioned = null;
    }
    
    @Override
    public synchronized void trim(Version version) throws VersioningException {
        //TODO waiting for remy marechal work
        super.trim(version);
    }

    @Override
    public synchronized void revert(Version version) throws VersioningException {
        //TODO waiting for remy marechal work
        super.revert(version);
    }
    
    @Override
    public synchronized List<Version> list() throws VersioningException {
        if(!isVersioned()){
            return Collections.EMPTY_LIST;
        }
        
        final List<Version> versions = new ArrayList<Version>();
        
        final String schemaName = featureStore.getDatabaseSchema();
        final String tableName = getHSTableName();
        
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = featureStore.getDataSource().getConnection();
            stmt = cnx.createStatement();  
            
            final StringBuilder sb = new StringBuilder("SELECT distinct(");
            dialect.encodeColumnName(sb, "HS_Begin");
            sb.append(") FROM ");
            dialect.encodeSchemaAndTableName(sb, schemaName, tableName);
            sb.append(" ORDER BY ");
            dialect.encodeColumnName(sb, "HS_Begin");
            sb.append(" ASC");
            rs = stmt.executeQuery(sb.toString());
            while(rs.next()){
                final Timestamp ts = rs.getTimestamp(1);
                final Version v = new Version(this, ts.toString(), ts);
                versions.add(v);
            }
            
            rs.next();
            final int nb = rs.getInt(1);
            isVersioned = nb>0;
            
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(), ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(featureStore.getLogger(), cnx, stmt, null);
        }
        
        return versions;
    }

    @Override
    public synchronized boolean isVersioned() throws VersioningException {
        boolean hasHS = featureStore.hasHSFunctions();
        if(!hasHS) return false;
        
        if(isVersioned!=null) return isVersioned;
        
        //search for the versioning table
        final String schemaName = featureStore.getDatabaseSchema();
        final String tableName = getHSTableName();
        
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = featureStore.getDataSource().getConnection();
            stmt = cnx.createStatement();  
            
            final StringBuilder sb = new StringBuilder("SELECT count(*) from \"information_schema\".\"tables\" WHERE ");
            sb.append("\"table_schema\"=");
            dialect.encodeValue(sb, schemaName, String.class);
            sb.append(" AND \"table_name\"=");
            dialect.encodeValue(sb, tableName, String.class);
            rs = stmt.executeQuery(sb.toString());
            rs.next();
            final int nb = rs.getInt(1);
            isVersioned = nb>0;
            
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(), ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(featureStore.getLogger(), cnx, stmt, null);
        }
        
        return isVersioned;
    }
    
    private String getHSTableName(){
        return "HS_TBL_"+featureType.getName().getLocalPart();
    }
    
}
