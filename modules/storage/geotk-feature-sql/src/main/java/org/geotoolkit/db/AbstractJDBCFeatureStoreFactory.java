/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.geotoolkit.data.AbstractFeatureStoreFactory;
import org.geotoolkit.db.dialect.SQLDialect;
import org.geotoolkit.jdbc.DBCPDataSource;
import org.apache.sis.metadata.iso.quality.DefaultConformanceResult;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataStore;
import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Abstract FeatureStoreFactory for databases.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractJDBCFeatureStoreFactory extends AbstractFeatureStoreFactory {

    /** parameter for database host */
    public static final ParameterDescriptor<String> HOST = new ParameterBuilder()
            .addName("host")
            .addName(Bundle.formatInternational(Bundle.Keys.host))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.host_remarks))
            .setRequired(true)
            .create(String.class, "localhost");

    /** parameter for database port */
    public static final ParameterDescriptor<Integer> PORT = new ParameterBuilder()
            .addName("port")
            .addName(Bundle.formatInternational(Bundle.Keys.port))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.port_remarks))
            .setRequired(true)
            .create(Integer.class, null);
    
    /** parameter for database instance */
    public static final ParameterDescriptor<String> DATABASE = new ParameterBuilder()
            .addName("database")
            .addName(Bundle.formatInternational(Bundle.Keys.database))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.database_remarks))
            .setRequired(false)
            .create(String.class, null);
    
    /** parameter for database schema */
    public static final ParameterDescriptor<String> SCHEMA = new ParameterBuilder()
            .addName("schema")
            .addName(Bundle.formatInternational(Bundle.Keys.schema))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.schema_remarks))
            .setRequired(false)
            .create(String.class, null);

    /** parameter for database user */
    public static final ParameterDescriptor<String> USER = new ParameterBuilder()
            .addName("user")
            .addName(Bundle.formatInternational(Bundle.Keys.user))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.user_remarks))
            .setRequired(true)
            .create(String.class, null);

    /** parameter for database password */
    public static final ParameterDescriptor<String> PASSWORD = new ParameterBuilder()
            .addName("password")
            .addName(Bundle.formatInternational(Bundle.Keys.password))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.password_remarks))
            .setRequired(true)
            .create(String.class, null);

    /** parameter for data source */
    public static final ParameterDescriptor<DataSource> DATASOURCE = new ParameterBuilder()
            .addName("Data Source")
            .addName(Bundle.formatInternational(Bundle.Keys.datasource))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.datasource_remarks))
            .setRequired(false)
            .create(DataSource.class, null);

    /** Set to true to have only simple feature types. 
     * relations won't be rebuilded as complexe features.
     * Default is true.
     */
    public static final ParameterDescriptor<Boolean> SIMPLETYPE = new ParameterBuilder()
            .addName("simple types")
            .addName(Bundle.formatInternational(Bundle.Keys.simpletype))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.simpletype_remarks))
            .setRequired(true)
            .create(Boolean.class, Boolean.TRUE);

    /** Maximum number of connections in the connection pool */
    public static final ParameterDescriptor<Integer> MAXCONN = new ParameterBuilder()
            .addName("max connections")
            .addName(Bundle.formatInternational(Bundle.Keys.max_connections))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.max_connections_remarks))
            .setRequired(false)
            .create(Integer.class, 10);

    /** Minimum number of connections in the connection pool */
    public static final ParameterDescriptor<Integer> MINCONN = new ParameterBuilder()
            .addName("min connections")
            .addName(Bundle.formatInternational(Bundle.Keys.min_connections))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.min_connections_remarks))
            .setRequired(false)
            .create(Integer.class, 1);

    /** If connections should be validated before using them */
    public static final ParameterDescriptor<Boolean> VALIDATECONN = new ParameterBuilder()
            .addName("validate connections")
            .addName(Bundle.formatInternational(Bundle.Keys.validate_connections))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.validate_connections_remarks))
            .setRequired(false)
            .create(Boolean.class, Boolean.FALSE);

    /** If connections should be validated before using them */
    public static final ParameterDescriptor<Integer> FETCHSIZE = new ParameterBuilder()
            .addName("fetch size")
            .addName(Bundle.formatInternational(Bundle.Keys.fetch_size))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.fetch_size_remarks))
            .setRequired(false)
            .create(Integer.class, 1000);
    
    /** Maximum amount of time the pool will wait when trying to grab a new connection **/
    public static final ParameterDescriptor<Integer> MAXWAIT = new ParameterBuilder()
            .addName("Connection timeout")
            .addName(Bundle.formatInternational(Bundle.Keys.timeout))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.timeout_remarks))
            .setRequired(false)
            .create(Integer.class, 20);
    
    /** parameter for table to load **/
    public static final ParameterDescriptor<String> TABLE = new ParameterBuilder()
            .addName("Table Name")
            .addName(Bundle.formatInternational(Bundle.Keys.table))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.table_remarks))
            .setRequired(false)
            .create(String.class, null);

    /**
     * Create the database port descriptor, and set default parameter value.
     * @return a databse port descriptor.
     */
    public static ParameterDescriptor<Integer> createFixedPort(Integer value) {
        return new ParameterBuilder()
                    .addName(PORT.getName().getCode())
                    .addName(PORT.getAlias().iterator().next())
                    .setRemarks(PORT.getRemarks())
                    .setRequired(true)
                    .create(Integer.class, value);
    }

    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        final boolean valid = super.canProcess(params);
        
        if(!valid){
            //check if the datasource is set
            try{
                final DataSource ds = (DataSource) params.parameter(DATASOURCE.getName().toString()).getValue();
                if(ds == null){
                    return false;
                }
            }catch(ParameterNotFoundException ex){
                //parameter does not exist
                return false;
            }
        }
        
        return valid;
    }

    @Override
    public JDBCFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        
        final DefaultJDBCFeatureStore featureStore = toFeatureStore(params,
                getIdentification().getCitation().getIdentifiers().iterator().next().getCode());
        prepareStore(featureStore, params);
        return featureStore;
    }
    
    /**
     * Configure feature store datasource and dialect.
     * 
     * @param featureStore
     * @param params
     * @throws DataStoreException 
     */
    public void prepareStore(DefaultJDBCFeatureStore featureStore, final ParameterValueGroup params) throws DataStoreException{
        
        // datasource
        final DataSource ds = (DataSource) params.parameter(DATASOURCE.getName().getCode()).getValue();
        try {
            featureStore.setDataSource((ds != null) ? ds : createDataSource(params));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
        
        // dialect
        final SQLDialect dialect = createSQLDialect(featureStore);
        featureStore.setDialect(dialect);
    }

    protected DefaultJDBCFeatureStore toFeatureStore(final ParameterValueGroup params,String factoryId){
        return new DefaultJDBCFeatureStore(params,factoryId);
    }

    /**
     * Create a JDBC database : this will not create a database but just
     * create the schema if possible.
     * 
     * @param params
     * @return
     * @throws DataStoreException 
     */
    @Override
    public DataStore create(final ParameterValueGroup params) throws DataStoreException {
        
        JDBCFeatureStore store = null;
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            store = open(params);
            cnx = store.getDataSource().getConnection();
            rs = cnx.getMetaData().getSchemas();
            final String schema = store.getDatabaseSchema();
            while (rs.next()) {
                final String currentSchema = rs.getString(1);
                if (currentSchema.equals(schema)) {
                    throw new DataStoreException("Schema " + schema+ " already exist. Unable to create DataStore.");
                }
            }

            stmt = cnx.createStatement();

            if (schema != null && !schema.isEmpty()) {
                //create schema
                stmt.executeUpdate("CREATE SCHEMA \"" + schema + "\";");
            }

            return store;
        } catch (SQLException ex) {
            if (store != null) {
                store.close();
            }
            throw new DataStoreException(ex);
        } finally {
            if (store != null) {
                JDBCFeatureStoreUtilities.closeSafe(store.getLogger(),cnx, stmt, rs);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ConformanceResult availability() {
        final DefaultConformanceResult result = (DefaultConformanceResult)super.availability();
        if(Boolean.FALSE.equals(result.pass())) return result;
        
        try {
            //check jdbc driver
            Class.forName(getDriverClassName());
        } catch (ClassNotFoundException e) {
            result.setPass(false);
        }
        return result;
    }

    /**
     * @return String , name used in the construction of the JDBC url.
     */
    protected abstract String getJDBCURLDatabaseName();

    /**
     * @return String , complete JDBC driver class name.
     */
    protected abstract String getDriverClassName();

    /**
     * Creates the dialect that the featurestore uses for communication with the
     * underlying database.
     *
     * @param featureStore The featurestore.
     */
    protected abstract SQLDialect createSQLDialect(final JDBCFeatureStore featureStore);

    /**
     * Create a datasource using given parameters.
     */
    protected DataSource createDataSource(final ParameterValueGroup params) throws IOException {
        //create a datasource
        final BasicDataSource dataSource = new BasicDataSource();

        // some default data source behaviour
        dataSource.setPoolPreparedStatements(false);

        // driver
        dataSource.setDriverClassName(getDriverClassName());

        // url
        dataSource.setUrl(getJDBCUrl(params));

        // username
        final String user = (String) params.parameter(USER.getName().toString()).getValue();
        dataSource.setUsername(user);

        // password
        final String passwd = (String) params.parameter(PASSWORD.getName().toString()).getValue();
        if (passwd != null) {
            dataSource.setPassword(passwd);
        }

        // max wait
        final Integer maxWait = (Integer) params.parameter(MAXWAIT.getName().toString()).getValue();
        if (maxWait != null && maxWait != -1) {
            dataSource.setMaxWait(maxWait * 1000);
        }

        // connection pooling options
        final Integer minConn = (Integer) params.parameter(MINCONN.getName().toString()).getValue();
        if ( minConn != null ) {
            dataSource.setMinIdle(minConn);
        }

        final Integer maxConn = (Integer) params.parameter(MAXCONN.getName().toString()).getValue();
        if ( maxConn != null ) {
            dataSource.setMaxActive(maxConn);
        }

        final Boolean validate = (Boolean) params.parameter(VALIDATECONN.getName().toString()).getValue();
        if(validate != null && validate && getValidationQuery() != null) {
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(getValidationQuery());
        }

        // allow manipulating connections for possible tuning.
        dataSource.setAccessToUnderlyingConnectionAllowed(true);

        return new DBCPDataSource(dataSource);
    }

    /**
     * @return String : a fast query which can be send to the server to ensure
     * the connextion is still valid.
     */
    protected abstract String getValidationQuery();

    /**
     * Build JDBC url string = jdbc:<database>://<host>:<port>/<dbname>
     */
    protected String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();

        final StringBuilder sb = new StringBuilder("jdbc:");
        sb.append(getJDBCURLDatabaseName());
        sb.append("://");
        sb.append(host);
        if(port != null){
            sb.append(':').append(port);
        }
        if(db != null){
            sb.append('/').append(db);
        }
        return sb.toString();
    }
}
