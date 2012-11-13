/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.postgresql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.geotoolkit.coverage.AbstractCoverageStore;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStoreFinder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.referencing.factory.epsg.ThreadedEpsgFactory;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GeotoolKit Coverage Store using PostgreSQL Raster model.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGCoverageStore extends AbstractCoverageStore{
    
    private ThreadedEpsgFactory epsgfactory;
    private DataSource source;
    private int fetchSize;
    private String schema;

    public PGCoverageStore(final ParameterValueGroup params, final DataSource source){
        super(params);
        this.source = source;
        
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setDatabaseSchema(String schema) {
        this.schema = schema;
    }

    public String getDatabaseSchema() {
        return schema;
    }

    public DataSource getDataSource() {
        return source;
    }
    
    public synchronized ThreadedEpsgFactory getEPSGFactory() throws SQLException{
        if(epsgfactory == null){
            epsgfactory = new ThreadedEpsgFactory(source);
        }
        return epsgfactory;
    }

    @Override
    public CoverageStoreFactory getFactory() {
        return CoverageStoreFinder.getFactoryById(PGCoverageStoreFactory.NAME);
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        final Set<Name> names = new HashSet<Name>();
        final String ns = getDefaultNamespace();
        
        final StringBuilder query = new StringBuilder();
        
        query.append("SELECT name FROM ");
        query.append(encodeTableName("Layer"));

        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            cnx = source.getConnection();
            stmt = cnx.createStatement();
            rs = stmt.executeQuery(query.toString());
            while (rs.next()){
                names.add(new DefaultName(ns,rs.getString(1)));
            }
        } catch (SQLException ex) {
            throw new DataStoreException(ex);
        } finally {
            closeSafe(cnx,stmt,rs);
        }
        return names;
    }

    @Override
    public CoverageReference getCoverageReference(Name name) throws DataStoreException {
        typeCheck(name);
        return new PGCoverageReference(this, name);
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        
        final StringBuilder query = new StringBuilder();        
        query.append("INSERT INTO ");
        query.append(encodeTableName("Layer"));
        query.append("(name) VALUES ('");
        query.append(name.getLocalPart());
        query.append("')");
        
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            cnx = source.getConnection();
            stmt = cnx.createStatement();
            stmt.executeUpdate(query.toString());
        } catch (SQLException ex) {
            throw new DataStoreException(ex);
        } finally {
            closeSafe(cnx,stmt,rs);
        }
        
        return getCoverageReference(new DefaultName(getDefaultNamespace(), name.getLocalPart()));
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    int getLayerId(String name) throws SQLException {
        final StringBuilder query = new StringBuilder();        
        query.append("SELECT id FROM ");
        query.append(encodeTableName("Layer"));
        query.append(" WHERE name='");
        query.append(name);
        query.append("'");
        
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            cnx = source.getConnection();
            stmt = cnx.createStatement();            
            rs = stmt.executeQuery(query.toString());
            if(rs.next()){
                return rs.getInt(1);
            }else{
                throw new SQLException("No layer for name : "+name);
            }
        } finally {
            closeSafe(cnx,stmt,rs);
        }
        
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Connection utils ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    String encodeTableName(String name){
        final String schema = getDatabaseSchema();
        if(schema == null){
            return "\""+name+"\"";
        }else{
            return "\""+schema+"\".\""+name+"\"";
        }
    }
    
    public void closeSafe(final Connection cx, final Statement st, final ResultSet rs){
        closeSafe(cx);
        closeSafe(st);
        closeSafe(rs);
    }

    public void closeSafe(final ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (SQLException e) {
            final String msg = "Error occurred closing result set";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    public void closeSafe(final Statement st) {
        if (st == null) {
            return;
        }

        try {
            st.close();
        } catch (SQLException e) {
            final String msg = "Error occurred closing statement";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }

    public void closeSafe(final Connection cx) {
        if (cx == null) {
            return;
        }

        try {
            cx.close();
            getLogger().fine("CLOSE CONNECTION");
        } catch (SQLException e) {
            final String msg = "Error occurred closing connection";
            getLogger().warning(msg);

            if (getLogger().isLoggable(Level.FINER)) {
                getLogger().log(Level.FINER, msg, e);
            }
        }
    }
    
    @Override
    public void dispose() {
        if (source instanceof ManageableDataSource) {
            try {
                final ManageableDataSource mds = (ManageableDataSource) source;
                source = null;
                mds.close();
            } catch (SQLException e) {
                // it's ok, we did our best..
                getLogger().log(Level.FINE, "Could not close dataSource", e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (source != null) {
            getLogger().severe("There's code using JDBC based coverage store and " +
                    "not disposing them. This may lead to temporary loss of database connections. " +
                    "Please make sure all data access code calls CoverageStore.dispose() " +
                    "before freeing all references to it");
            dispose();
        }
        super.finalize();
    }

}
