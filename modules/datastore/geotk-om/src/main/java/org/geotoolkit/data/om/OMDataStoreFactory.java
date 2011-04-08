/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.data.om;

import org.opengis.parameter.ParameterDescriptor;
import org.apache.commons.dbcp.BasicDataSource;

import java.io.IOException;
import java.util.logging.Logger;

import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.jdbc.DBCPDataSource;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;

import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import static org.geotoolkit.jdbc.JDBCDataStoreFactory.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OMDataStoreFactory extends AbstractDataStoreFactory {

    /**
     * Parameter for database port
     */
    public static final ParameterDescriptor<Integer> PORT =
             new DefaultParameterDescriptor<Integer>("port","Port",Integer.class,5432, false);

    /**
     * Parameter identifying the OM datastore
     */
    public static final ParameterDescriptor<String> DBTYPE =
             new DefaultParameterDescriptor<String>("dbtype","DbType",String.class, null, true);

    /**
     * Parameter for database type (postgres, derby, ...)
     */
    public static final ParameterDescriptor<String> SGBDTYPE =
             new DefaultParameterDescriptor<String>("sgbdtype","SGBDType",String.class, "postgres",true);

    /**
     * Parameter for database url for derby database
     */
    public static final ParameterDescriptor<String> DERBYURL =
             new DefaultParameterDescriptor<String>("derbyurl","DerbyURL",String.class, null,false);

    /**
     * Parameter for database host
     */
    public static final ParameterDescriptor<String> HOST =
             new DefaultParameterDescriptor<String>("host","Host", String.class, "localhost",false);

    /**
     * Parameter for database name
     */
    public static final ParameterDescriptor<String> DATABASE =
             new DefaultParameterDescriptor<String>("database","Database", String.class, null, false);

    /**
     * Parameter for database user name
     */
    public static final ParameterDescriptor<String> USER =
             new DefaultParameterDescriptor<String>("user","User", String.class, null,false);

    /**
     * Parameter for database user password
     */
    public static final ParameterDescriptor<String> PASSWD =
             new DefaultParameterDescriptor<String>("password","Password", String.class, null, false);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("OMParameters",
                DBTYPE,HOST,PORT,DATABASE,USER,PASSWD,NAMESPACE, SGBDTYPE, DERBYURL);

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.om");
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return "OGC Observation And Measurement datastore factory";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public boolean canProcess(final ParameterValueGroup params) {
        boolean valid = super.canProcess(params);
        if(valid){
            Object value = params.parameter(DBTYPE.getName().toString()).getValue();
            if(value != null && value instanceof String){
                return value.toString().equals("OM");
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public DataStore createDataStore(final ParameterValueGroup params) throws DataStoreException {
        try{
            //create a datasource
            final BasicDataSource dataSource = new BasicDataSource();

            // some default data source behaviour
            dataSource.setPoolPreparedStatements(true);

            // driver
            dataSource.setDriverClassName(getDriverClassName(params));

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

            // some datastores might need this
            dataSource.setAccessToUnderlyingConnectionAllowed(true);

            final ManageableDataSource source = new DBCPDataSource(dataSource);
            return new OMDataStore(source);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public DataStore createNewDataStore(final ParameterValueGroup params) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String getDriverClassName(final ParameterValueGroup params){
        final String type  = (String) params.parameter(SGBDTYPE.getName().toString()).getValue();
        if (type.equals("derby")) {
            return "org.apache.derby.jdbc.EmbeddedDriver";
        } else {
            return "org.postgresql.Driver";
        }
    }

    private String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        final String type  = (String) params.parameter(SGBDTYPE.getName().toString()).getValue();
        if (type.equals("derby")) {
            final String derbyURL = (String) params.parameter(DERBYURL.getName().toString()).getValue();
            return derbyURL;
        } else {
            final String host  = (String) params.parameter(HOST.getName().toString()).getValue();
            final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
            final String db    = (String) params.parameter(DATABASE.getName().toString()).getValue();
            return "jdbc:postgresql" + "://" + host + ":" + port + "/" + db;
        }
    }

    @Override
    public ConformanceResult availability() {
        DefaultConformanceResult result =  new DefaultConformanceResult();
        result.setPass(true);
        return result;
    }
   

}
