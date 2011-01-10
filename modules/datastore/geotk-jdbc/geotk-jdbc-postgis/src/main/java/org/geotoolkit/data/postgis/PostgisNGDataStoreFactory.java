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
package org.geotoolkit.data.postgis;

import java.io.IOException;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.jdbc.JDBCDataStoreFactory;
import org.geotoolkit.jdbc.dialect.SQLDialect;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

public class PostgisNGDataStoreFactory extends JDBCDataStoreFactory {
    /**
     * Parameter for namespace of the datastore
     */
    public static final GeneralParameterDescriptor LOOSEBBOX =
            new DefaultParameterDescriptor("Loose bbox","Perform only primary filter on bbox",Boolean.class,true,false);

    /**
     * Parameter for database port
     */
    public static final GeneralParameterDescriptor PORT =
            new DefaultParameterDescriptor("port","Port",Integer.class,5432,true);

    /**
     * Wheter a prepared statements based dialect should be used, or not
     */
    public static final GeneralParameterDescriptor PREPARED_STATEMENTS =
            new DefaultParameterDescriptor("preparedStatements","Use prepared statements",Boolean.class,false,false);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("PostGISParameters",
                new GeneralParameterDescriptor[]{DBTYPE,HOST,PORT,DATABASE,SCHEMA,USER,PASSWD,NAMESPACE,
                DATASOURCE,MAXCONN,MINCONN,VALIDATECONN,FETCHSIZE,MAXWAIT,LOOSEBBOX,PREPARED_STATEMENTS});

    @Override
    protected SQLDialect createSQLDialect(final JDBCDataStore dataStore) {
        return new PostGISDialect(dataStore);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    protected String getDatabaseID() {
        return "postgisng";
    }

    @Override
    public String getDisplayName() {
        return "PostGIS NG";
    }

    @Override
    public String getDescription() {
        return "PostGIS Database";
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public JDBCDataStore createDataStore(final ParameterValueGroup params)
        throws DataStoreException {
        JDBCDataStore dataStore = super.createDataStore(params);

        final PostGISDialect dialect;

        // setup the ps dialect if need be
        final Boolean usePs = (Boolean) params.parameter(PREPARED_STATEMENTS.getName().toString()).getValue();
        if(Boolean.TRUE.equals(usePs)) {
            dialect = new PostGISPSDialect(dataStore);
            dataStore.setDialect(dialect);
        }else{
            dialect = (PostGISDialect) dataStore.getDialect();
        }

        // setup loose bbox
        final Boolean loose = (Boolean) params.parameter(LOOSEBBOX.getName().toString()).getValue();
        dialect.setLooseBBOXEnabled(loose == null || Boolean.TRUE.equals(loose));

        return dataStore;
    }

    @Override
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected String getJDBCUrl(final ParameterValueGroup params) throws IOException {
        final String host = (String) params.parameter(HOST.getName().toString()).getValue();
        final Integer port = (Integer) params.parameter(PORT.getName().toString()).getValue();
        final String db = (String) params.parameter(DATABASE.getName().toString()).getValue();
        return "jdbc:postgresql://" + host + ":" + port + "/" + db;
    }

}
