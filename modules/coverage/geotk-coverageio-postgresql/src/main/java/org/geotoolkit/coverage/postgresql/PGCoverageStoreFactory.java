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
import java.util.Collections;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.geotoolkit.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.jdbc.DBCPDataSource;
import org.geotoolkit.metadata.iso.DefaultIdentifier;
import org.geotoolkit.metadata.iso.citation.DefaultCitation;
import org.geotoolkit.metadata.iso.identification.DefaultServiceIdentification;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

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
    
    /**
     * Parameter for database port
     */
    public static final ParameterDescriptor<Integer> PORT =
             new DefaultParameterDescriptor<Integer>("port","Port",Integer.class,5432,true);

    /** parameter for database host */
    public static final ParameterDescriptor<String> HOST =
             new DefaultParameterDescriptor<String>("host","Host",String.class,"localhost",true);

    /** parameter for database instance */
    public static final ParameterDescriptor<String> DATABASE =
             new DefaultParameterDescriptor<String>("database","Database",String.class,null,false);

    /** parameter for database schema */
    public static final ParameterDescriptor<String> SCHEMA =
             new DefaultParameterDescriptor<String>("schema","Schema",String.class,null,false);

    /** parameter for database user */
    public static final ParameterDescriptor<String> USER =
             new DefaultParameterDescriptor<String>("user","user name to login as",String.class,null,true);

    /** parameter for database password */
    public static final ParameterDescriptor<String> PASSWD =
             new DefaultParameterDescriptor<String>("passwd","password used to login",String.class,null,true);

    /** parameter for data source */
    public static final ParameterDescriptor<DataSource> DATASOURCE =
             new DefaultParameterDescriptor<DataSource>("Data Source","Data Source",DataSource.class,null,false);

    /** Maximum number of connections in the connection pool */
    public static final ParameterDescriptor<Integer> MAXCONN =
             new DefaultParameterDescriptor<Integer>("max connections","maximum number of open connections",Integer.class,10,false);

    /** Minimum number of connections in the connection pool */
    public static final ParameterDescriptor<Integer> MINCONN =
             new DefaultParameterDescriptor<Integer>("min connections","minimum number of pooled connection",Integer.class,1,false);

    /** If connections should be validated before using them */
    public static final ParameterDescriptor<Boolean> VALIDATECONN =
             new DefaultParameterDescriptor<Boolean>("validate connections","check connection is alive before using it",Boolean.class,false,false);

    /** If connections should be validated before using them */
    public static final ParameterDescriptor<Integer> FETCHSIZE =
             new DefaultParameterDescriptor<Integer>("fetch size","number of records read with each iteraction with the dbms",Integer.class,1000,false);
    
    /** Maximum amount of time the pool will wait when trying to grab a new connection **/
    public static final ParameterDescriptor<Integer> MAXWAIT =
             new DefaultParameterDescriptor<Integer>("Connection timeout","number of seconds the connection pool wait for login",Integer.class,20,false);

    
    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("PGRasterParameters",
                IDENTIFIER,HOST,PORT,DATABASE,SCHEMA,USER,PASSWD,NAMESPACE,
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
        return "GeotoolKit PostGIS Raster model.";
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        
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
    public CoverageStore createNew(ParameterValueGroup params) throws DataStoreException {
        throw new UnsupportedOperationException("Creation not supported.");
    }
    
    private String getDriverClassName() {
        return "org.postgresql.Driver";
    }
    
    private String getValidationQuery() {
        return "select now()";
    }

    private String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();
        return "jdbc:postgresql://" + host + ":" + port + "/" + db;
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

        // might need this
        dataSource.setAccessToUnderlyingConnectionAllowed(true);

        return new DBCPDataSource(dataSource);
    }
    
}
