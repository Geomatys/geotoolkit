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
import java.util.Map;

import org.geotoolkit.jdbc.JDBCDataStore;
import org.geotoolkit.jdbc.JDBCDataStoreFactory;
import org.geotoolkit.jdbc.SQLDialect;

public class PostgisNGDataStoreFactory extends JDBCDataStoreFactory {
    /**
     * Parameter for namespace of the datastore
     */
    public static final Param LOOSEBBOX = new Param("Loose bbox", Boolean.class,
                                                    "Perform only primary filter on bbox", false, Boolean.TRUE);

    /**
     * Parameter for database port
     */
    public static final Param PORT = new Param("port", Integer.class, "Port", true, 5432);

    /**
     * Wheter a prepared statements based dialect should be used, or not
     */
    public static final Param PREPARED_STATEMENTS = new Param("preparedStatements", Boolean.class,
                "Use prepared statements", false, Boolean.FALSE);

    @Override
    protected SQLDialect createSQLDialect(JDBCDataStore dataStore) {
        return new PostGISDialect(dataStore);
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
    protected JDBCDataStore createDataStoreInternal(final JDBCDataStore dataStore, final Map params)
        throws IOException {

        // setup loose bbox
        final PostGISDialect dialect = (PostGISDialect) dataStore.getSQLDialect();
        final Boolean loose = (Boolean) LOOSEBBOX.lookUp(params);
        dialect.setLooseBBOXEnabled(loose == null || Boolean.TRUE.equals(loose));

        // setup the ps dialect if need be
        final Boolean usePs = (Boolean) PREPARED_STATEMENTS.lookUp(params);
        if(Boolean.TRUE.equals(usePs)) {
            dataStore.setSQLDialect(new PostGISPSDialect(dataStore, dialect));
        }

        return dataStore;
    }

    @Override
    protected void setupParameters(final Map parameters) {
        super.setupParameters(parameters);
        parameters.put(LOOSEBBOX.key, LOOSEBBOX);
        parameters.put(PORT.key, PORT);
        parameters.put(PREPARED_STATEMENTS.key, PREPARED_STATEMENTS);
    }

    @Override
    protected String getValidationQuery() {
        return "select now()";
    }

    @Override
    protected String getJDBCUrl(final Map params) throws IOException {
        final String host = (String) HOST.lookUp(params);
        final String db = (String) DATABASE.lookUp(params);
        final int port = (Integer) PORT.lookUp(params);
        return "jdbc:postgresql" + "://" + host + ":" + port + "/" + db;
    }

}
