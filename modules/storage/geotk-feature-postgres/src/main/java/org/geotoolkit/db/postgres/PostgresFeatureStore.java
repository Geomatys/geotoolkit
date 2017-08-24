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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.JDBCFeatureStoreUtilities;
import org.geotoolkit.db.dialect.SQLQueryBuilder;
import org.geotoolkit.internal.sql.ScriptRunner;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.version.VersionControl;
import org.geotoolkit.version.VersioningException;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Extends default jdbc feature store with versioning and subsampling capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresFeatureStore extends DefaultJDBCFeatureStore{

    private static final QueryCapabilities PG_CAPA = new DefaultQueryCapabilities(false, true, new String[]{Query.GEOTK_QOM, CUSTOM_SQL});

    //historisation informations
    private Boolean hasHSFunctions;
    private PostgresQueryBuilder querybuilder = null;

    public PostgresFeatureStore(String host, int port, String database, String schema, String user, String password) throws DataStoreException {
        super(toParameters(host,port,database,schema,user,password), PostgresFeatureStoreFactory.NAME);
        ((PostgresFeatureStoreFactory)getProvider()).prepareStore(this, parameters);
    }

    public PostgresFeatureStore(ParameterValueGroup params, String factoryId) {
        super(params, factoryId);
    }

    private static ParameterValueGroup toParameters(String host, int port,
            String database, String schema, String user, String password){
        final Parameters params = Parameters.castOrWrap(PostgresFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(PostgresFeatureStoreFactory.HOST).setValue(host);
        params.getOrCreate(PostgresFeatureStoreFactory.PORT).setValue(port);
        params.getOrCreate(PostgresFeatureStoreFactory.DATABASE).setValue(database);
        params.getOrCreate(PostgresFeatureStoreFactory.SCHEMA).setValue(schema);
        params.getOrCreate(PostgresFeatureStoreFactory.USER).setValue(user);
        params.getOrCreate(PostgresFeatureStoreFactory.PASSWORD).setValue(password);
        return params;
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return PG_CAPA;
    }

    @Override
    protected SQLQueryBuilder getQueryBuilder() {
         if(querybuilder == null){
            querybuilder = new PostgresQueryBuilder(this);
        }
        return querybuilder;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Versioning control //////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public VersionControl getVersioning(String typeName) throws VersioningException {
        final FeatureType type;
        try {
            type = getFeatureType(typeName);
        } catch (DataStoreException ex) {
            throw new VersioningException(ex.getMessage(),ex);
        }

        return new PostgresVersionControl(this, type);
    }

    /**
     * Search for historisation functions.
     * @return true if HS_ functions are available
     * @throws VersioningException
     */
    public synchronized boolean hasHSFunctions() throws VersioningException {
        if(hasHSFunctions!=null) return hasHSFunctions;

        //search if historization procedure are present
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            cnx = getDataSource().getConnection();
            stmt = cnx.createStatement();
            rs = stmt.executeQuery("select count(proname) from pg_proc where upper(\"proname\") like 'HS\\_%'");
            rs.next();
            hasHSFunctions = rs.getInt(1) > 0;
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(),ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(getLogger(), cnx,stmt,rs);
        }

        return hasHSFunctions;
    }

    /**
     * Install the ISO-13249:7 History functions.
     * @throws VersioningException
     */
    public synchronized void installHSFunctions() throws VersioningException {
        hasHSFunctions = null;

        Connection cnx = null;
        try{
            cnx = getDataSource().getConnection();
            final ScriptRunner scriptRunner = new ScriptRunner(cnx);
            scriptRunner.run(PostgresFeatureStore.class.getResourceAsStream("/org/geotoolkit/db/postgres/HS_Functions.sql"));
        }catch(IOException ex){
            throw new VersioningException(ex.getMessage(),ex);
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(),ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(getLogger(), cnx);
        }
    }

    /**
     * Uninstall the ISO-13249:7 History functions.
     * @throws VersioningException
     */
    public synchronized void dropHSFunctions() throws VersioningException {
        hasHSFunctions = null;

        Connection cnx = null;
        try{
            cnx = getDataSource().getConnection();
            final ScriptRunner scriptRunner = new ScriptRunner(cnx);
            scriptRunner.run(PostgresFeatureStore.class.getResourceAsStream("/org/geotoolkit/db/postgres/HS_DropFunctions.sql"));
        }catch(IOException ex){
            throw new VersioningException(ex.getMessage(),ex);
        }catch(SQLException ex){
            throw new VersioningException(ex.getMessage(),ex);
        }finally{
            JDBCFeatureStoreUtilities.closeSafe(getLogger(), cnx);
        }
    }

    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        try {
            getVersioning(typeName).dropVersioning();
        } catch (VersioningException ex) {
            throw new DataStoreException(ex);
        }
         super.deleteFeatureType(typeName);

    }

    /**
     * Delete postgres schema.
     *
     * @param name The postgres schema name.
     * @throws DataStoreException
     */
    public void dropPostgresSchema(final String name) throws DataStoreException {
        Statement stmt = null;
        Connection cnx = null;
        String sql = null;
        try {
            cnx = getDataSource().getConnection();
            sql = "DROP SCHEMA \""+ name +"\" CASCADE;";
            stmt = cnx.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            throw new DataStoreException("Failed to delete features : " + ex.getMessage() + "\nSQL Query :" + sql, ex);
        } finally {
            JDBCFeatureStoreUtilities.closeSafe(getLogger(), cnx, stmt, null);
        }
    }
}
