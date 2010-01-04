/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import org.geotoolkit.jdbc.dialect.PreparedStatementSQLDialect;
import org.geotoolkit.jdbc.dialect.SQLDialect;
import java.io.IOException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.DataStoreException;
import org.geotoolkit.data.jdbc.datasource.DBCPDataSource;
import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.type.DefaultFeatureTypeFactory;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.parameter.DefaultParameterDescriptor;

import com.vividsolutions.jts.geom.GeometryFactory;

import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Abstract implementation of DataStoreFactory for jdbc datastores.
 * <p>
 *
 * </p>
 * @author Justin Deoliveira, The Open Planning Project
 *
 * @module pending
 */
public abstract class JDBCDataStoreFactory extends AbstractDataStoreFactory {

    /** parameter for database type */
    public static final GeneralParameterDescriptor DBTYPE =
            new DefaultParameterDescriptor("dbtype","Type",String.class,null,true);

    /** parameter for database host */
    public static final GeneralParameterDescriptor HOST =
            new DefaultParameterDescriptor("host","Host",String.class,"localhost",true);

    /** parameter for database port */
    public static final GeneralParameterDescriptor PORT =
            new DefaultParameterDescriptor("port","Port",Integer.class,null,true);

    /** parameter for database instance */
    public static final GeneralParameterDescriptor DATABASE =
            new DefaultParameterDescriptor("database","Database",String.class,null,false);

    /** parameter for database schema */
    public static final GeneralParameterDescriptor SCHEMA =
            new DefaultParameterDescriptor("schema","Schema",String.class,null,false);

    /** parameter for database user */
    public static final GeneralParameterDescriptor USER =
            new DefaultParameterDescriptor("user","user name to login as",String.class,null,true);

    /** parameter for database password */
    public static final GeneralParameterDescriptor PASSWD =
            new DefaultParameterDescriptor("passwd","password used to login",String.class,null,true);

    /** parameter for data source */
    public static final GeneralParameterDescriptor DATASOURCE =
            new DefaultParameterDescriptor("Data Source","Data Source",DataSource.class,null,false);

    /** Maximum number of connections in the connection pool */
    public static final GeneralParameterDescriptor MAXCONN =
            new DefaultParameterDescriptor("max connections","maximum number of open connections",Integer.class,10,false);

    /** Minimum number of connections in the connection pool */
    public static final GeneralParameterDescriptor MINCONN =
            new DefaultParameterDescriptor("min connections","minimum number of pooled connection",Integer.class,1,false);

    /** If connections should be validated before using them */
    public static final GeneralParameterDescriptor VALIDATECONN =
            new DefaultParameterDescriptor("validate connections","check connection is alive before using it",Boolean.class,false,false);

    /** If connections should be validated before using them */
    public static final GeneralParameterDescriptor FETCHSIZE =
            new DefaultParameterDescriptor("fetch size","number of records read with each iteraction with the dbms",Integer.class,1000,false);
    
    /** Maximum amount of time the pool will wait when trying to grab a new connection **/
    public static final GeneralParameterDescriptor MAXWAIT =
            new DefaultParameterDescriptor("Connection timeout","number of seconds the connection pool wait for login",Integer.class,20,false);

    @Override
    public String getDisplayName() {
        return getDescription();
    }

    @Override
    public boolean canProcess(ParameterValueGroup params) {
        boolean valid = super.canProcess(params);
        if(valid){
            Object value = params.parameter(DBTYPE.getName().toString()).getValue();
            if(value != null && value instanceof String){
                return value.toString().equals(getDatabaseID());
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public final JDBCDataStore createDataStore(final ParameterValueGroup params) throws DataStoreException {
        // namespace
        final String namespace = (String) params.parameter(NAMESPACE.getName().toString()).getValue();

        final JDBCDataStore dataStore = new DefaultJDBCDataStore(namespace);

        // dialect
        final SQLDialect dialect = createSQLDialect(dataStore);
        dataStore.setDialect(dialect);

        // datasource
        // check if the DATASOURCE parameter was supplied, it takes precendence
        final DataSource ds = (DataSource) params.parameter(DATASOURCE.getName().toString()).getValue();
        try {
            dataStore.setDataSource((ds != null) ? ds : createDataSource(params, dialect));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }

        // fetch size
        Integer fetchSize = (Integer) params.parameter(FETCHSIZE.getName().toString()).getValue();
        if (fetchSize != null && fetchSize > 0) {
            dataStore.setFetchSize(fetchSize);
        }

        //database schema
        final String schema = (String) params.parameter(SCHEMA.getName().toString()).getValue();

        if (schema != null) {
            dataStore.setDatabaseSchema(schema);
        }

        // factories
        dataStore.setFilterFactory(FactoryFinder.getFilterFactory(null));
        dataStore.setGeometryFactory(new GeometryFactory());
        dataStore.setFeatureTypeFactory(new DefaultFeatureTypeFactory());
        dataStore.setFeatureFactory(FactoryFinder.getFeatureFactory(null));

        //call subclass hook and return
        return createDataStoreInternal(dataStore, params);
    }

    /**
     * Subclass hook to do additional initialization of a newly created datastore.
     * <p>
     * Typically subclasses will want to override this method in the case where
     * they provide additional datastore parameters, those should be processed
     * here.
     * </p>
     * <p>
     * This method is provided with an instance of the datastore. In some cases
     * subclasses may wish to create a new instance of the datastore, for instance
     * in order to wrap the original instance. This is supported but the new
     * datastore must be returned from this method. If not is such the case this
     * method should still return the original passed in.
     *
     * </p>
     * @param dataStore The newly created datastore.
     * @param params THe datastore parameters.
     *
     */
    protected JDBCDataStore createDataStoreInternal(final JDBCDataStore dataStore, ParameterValueGroup params)
        throws DataStoreException{
        return dataStore;
    }

    @Override
    public DataStore createNewDataStore(ParameterValueGroup params) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Determines if the datastore is available.
     * <p>
     * Subclasses may with to override or extend this method. This implementation
     * checks whether the jdbc driver class (provided by {@link #getDriverClassName()}
     * can be loaded.
     * </p>
     */
    @Override
    public ConformanceResult availability() {
        DefaultConformanceResult result = new DefaultConformanceResult();
        try {
            Class.forName(getDriverClassName());
            result.setPass(true);
        } catch (ClassNotFoundException e) {
            result.setPass(false);
        }
        return result;
    }

    /**
     * Returns a string to identify the type of the database.
     * <p>
     * Example: 'postgis'.
     * </p>
     */
    protected abstract String getDatabaseID();

    /**
     * Returns the fully qualified class name of the jdbc driver.
     * <p>
     * For example: org.postgresql.Driver
     * </p>
     */
    protected abstract String getDriverClassName();

    /**
     * Creates the dialect that the datastore uses for communication with the
     * underlying database.
     *
     * @param dataStore The datastore.
     */
    protected abstract SQLDialect createSQLDialect(final JDBCDataStore dataStore);

    /**
     * Creates the datasource for the data store.
     * <p>
     * This method creates a {@link BasicDataSource} instance and populates it
     * as follows:
     * <ul>
     *  <li>poolPreparedStatements -> false
     *  <li>driverClassName -> {@link #getDriverClassName()}
     *  <li>url -> 'jdbc:&lt;{@link #getDatabaseID()}>://&lt;{@link #HOST}>/&lt;{@link #DATABASE}>'
     *  <li>username -> &lt;{@link #USER}>
     *  <li>password -> &lt;{@link #PASSWD}>
     * </ul>
     * If different behaviour is needed, this method should be extended or
     * overridden.
     * </p>
     */
    protected DataSource createDataSource(final ParameterValueGroup params, final SQLDialect dialect) throws IOException {
        //create a datasource
        final BasicDataSource dataSource = new BasicDataSource();

        // some default data source behaviour
        dataSource.setPoolPreparedStatements(dialect instanceof PreparedStatementSQLDialect);

        // driver
        dataSource.setDriverClassName(getDriverClassName());

        // url
        dataSource.setUrl(getJDBCUrl(params));

        // username
        final String user = (String) params.parameter(USER.getName().toString()).getValue();
        dataSource.setUsername(user);

        // password
        final String passwd = (String) params.parameter(PASSWD.getName().toString()).getValue();
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

        // some datastores might need this
        dataSource.setAccessToUnderlyingConnectionAllowed(true);

        return new DBCPDataSource(dataSource);
    }

    /**
     * Override this to return a good validation query (a very quick one, such as one that
     * asks the database what time is it) or return null if the factory does not support
     * validation.
     * @return String fast query
     */
    protected abstract String getValidationQuery();

    /**
     * Builds up the JDBC url in a jdbc:<database>://<host>:<port>/<dbname>
     * Override if you need a different setup
     * @param params
     * @return String url
     * @throws IOException
     */
    protected String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        // jdbc url
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();

        String url = "jdbc:" + getDatabaseID() + "://" + host;
        if ( port != null ) {
            url += ":" + port;
        }

        if ( db != null ) {
            url += "/" + db;
        }
        return url;
    }
}
