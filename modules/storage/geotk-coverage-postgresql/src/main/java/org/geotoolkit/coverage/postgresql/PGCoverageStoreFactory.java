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

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.postgresql.exception.SchemaExistsException;
import org.geotoolkit.jdbc.DBCPDataSource;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.referencing.factory.sql.EPSGFactory;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.geotoolkit.internal.sql.AuthenticatedDataSource;
import org.geotoolkit.internal.sql.DefaultDataSource;

/**
 * GeotoolKit Coverage Store using PostgreSQL Raster model factory.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PGCoverageStoreFactory extends AbstractCoverageStoreFactory{

    /** factory identification **/
    public static final String NAME = "pgraster";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

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


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("PGRasterParameters").createGroup(
                IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,USER,PASSWORD,NAMESPACE,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.description);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.title);
    }

    @Override
    public PGCoverageStore open(ParameterValueGroup params) throws DataStoreException {

        // datasource
        // check if the DATASOURCE parameter was supplied, it takes precendence
        DataSource ds = (DataSource) params.parameter(DATASOURCE.getName().toString()).getValue();
        if(ds == null){
            try {
                ds = createDataSource(params);
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(),ex);
            }
        }

        final PGCoverageStore store = new PGCoverageStore(params, ds);

        // fetch size
        Integer fetchSize = (Integer) params.parameter(FETCHSIZE.getName().toString()).getValue();
        if (fetchSize != null && fetchSize > 0) {
            store.setFetchSize(fetchSize);
        }

        //database schema
        final String schema = (String) params.parameter(SCHEMA.getName().toString()).getValue();

        if (schema != null) {
            store.setDatabaseSchema(schema);
        }

        return store;
    }

    @Override
    public PGCoverageStore create(ParameterValueGroup params) throws DataStoreException {

        final String jdbcurl = getJDBCUrl(params);

        //create epsg model
        final String dbURL      = jdbcurl;
        final String user       = (String) params.parameter(USER.getName().getCode()).getValue();
        final String password   = (String) params.parameter(PASSWORD.getName().getCode()).getValue();

        final DataSource ds = new AuthenticatedDataSource(new DefaultDataSource(dbURL), user, password, null);
        final Map<String,Object> properties = new HashMap<>();
        properties.put("dataSource", ds);

        PGCoverageStore store = null;
        Connection cnx = null;
        Statement stmt = null;
        ResultSet rs = null;
        try (final EPSGFactory installer = new EPSGFactory(properties)) {
            store = open(params);
            cnx = store.getDataSource().getConnection();
            rs = cnx.getMetaData().getSchemas();
            boolean epsgExists = false;
            final String schema = store.getDatabaseSchema();
            while (rs.next()) {
                final String currentSchema = rs.getString(1);
                if (currentSchema.contains("epsg")) {
                    epsgExists = true;
                } else if (currentSchema.contains(schema)) {
                    throw new SchemaExistsException(schema);
                }
            }

            // Only creates this schema if not present.
            if (!epsgExists) {
                try (Connection c = ds.getConnection()) {
                    installer.install(c);
                }
            }

            String sql = IOUtilities.toString(PGCoverageStoreFactory.class
                    .getResourceAsStream("/org/geotoolkit/coverage/postgresql/pgcoverage.sql"));

            stmt = cnx.createStatement();

            if(schema != null && !schema.isEmpty()){
                sql = sql.replaceAll("CREATE TABLE ", "CREATE TABLE \""+schema+"\".");
                sql = sql.replaceAll("REFERENCES ", "REFERENCES \""+schema+"\".");

                //create schema
                stmt.executeUpdate("CREATE SCHEMA \""+schema+"\";");
            }

            final String[] parts = sql.split(";");

            for(String part : parts){
                stmt.executeUpdate(part.trim());
            }

            return store;
        } catch (SQLException ex) {
            if(store != null){
                store.close();
            }
            throw new DataStoreException(ex);
        } catch (IOException ex) {
            if(store != null){
                store.close();
            }
            throw new DataStoreException(ex);
        } catch (FactoryException ex) {
            if(store != null){
                store.close();
            }
            throw new DataStoreException(ex);
        }finally{
            if(store != null){
                store.closeSafe(cnx, stmt, rs);
            }
        }
    }

    private String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    private String getValidationQuery() {
        return "select now()";
    }

    private String getJDBCUrl(final ParameterValueGroup params) {
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();
        return "jdbc:postgresql://" + host + ":" + port + "/" + db+"";
    }

    /**
     * Creates the datasource for the coverage store.
     */
    private DataSource createDataSource(final ParameterValueGroup params) throws IOException {
        //create a datasource
        final BasicDataSource dataSource = new BasicDataSource();

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

        // might need this
        dataSource.setAccessToUnderlyingConnectionAllowed(true);

        return new DBCPDataSource(dataSource);
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.PYRAMID, true, false, true);
    }
}
