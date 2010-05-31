/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.sml;

import java.io.IOException;

import org.apache.commons.dbcp.BasicDataSource;

import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.data.DataStore;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.jdbc.DBCPDataSource;
import org.geotoolkit.jdbc.ManageableDataSource;
import org.geotoolkit.metadata.iso.quality.DefaultConformanceResult;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;

import org.opengis.metadata.quality.ConformanceResult;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SMLDataStoreFactory extends AbstractDataStoreFactory {

    /**
     * Parameter for database port
     */
    public static final GeneralParameterDescriptor PORT = new DefaultParameterDescriptor("port","Port",Integer.class,5432, false);

    /**
     * Parameter identifying the OM datastore
     */
    public static final GeneralParameterDescriptor DBTYPE = new DefaultParameterDescriptor("dbtype","DbType",String.class, null,true);

    /**
     * Parameter for database type (postgres, derby, ...)
     */
    public static final GeneralParameterDescriptor SGBDTYPE = new DefaultParameterDescriptor("sgbdtype","SGBDType",String.class, "postgres",true);

    /**
     * Parameter for database url for derby database
     */
    public static final GeneralParameterDescriptor DERBYURL = new DefaultParameterDescriptor("derbyurl","DerbyURL",String.class, null, false);

    /**
     * Parameter for database host
     */
    public static final GeneralParameterDescriptor HOST = new DefaultParameterDescriptor("host","Host", String.class, "localhost", false);

    /**
     * Parameter for database name
     */
    public static final GeneralParameterDescriptor DATABASE = new DefaultParameterDescriptor("database","Database", String.class, null, false);

    /**
     * Parameter for database user name
     */
    public static final GeneralParameterDescriptor USER = new DefaultParameterDescriptor("user","User", String.class, null, false);

    /**
     * Parameter for database user password
     */
    public static final GeneralParameterDescriptor PASSWD = new DefaultParameterDescriptor("password","Password", String.class, null, false);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("SMLParameters",
                new GeneralParameterDescriptor[]{DBTYPE,HOST,PORT,DATABASE,USER,PASSWD,NAMESPACE, SGBDTYPE, DERBYURL});
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return "OGC Sensor Markup Language datastore factory";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public boolean canProcess(ParameterValueGroup params) {
        boolean valid = super.canProcess(params);
        if(valid){
            Object value = params.parameter(DBTYPE.getName().toString()).getValue();
            if(value != null && value instanceof String){
                return value.toString().equals("SML");
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public DataStore createDataStore(ParameterValueGroup params) throws DataStoreException {
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
            return new SMLDataStore(source);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public DataStore createNewDataStore(ParameterValueGroup params) throws DataStoreException {
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
