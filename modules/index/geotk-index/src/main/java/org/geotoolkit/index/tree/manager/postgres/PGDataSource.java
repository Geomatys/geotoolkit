/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.manager.postgres;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;

/**
 *
 * @author Christophe Mourrette
 * @author Guilhem Legal (Geomatys)
 */
public class PGDataSource {

    private static DataSource ds = null;

    private final static String POSTGRES_DATABASE_KEY = "org.geotoolkit.index.tree.manager.SQLRtreeManager.database";

    public static boolean isSetPGDataSource() {
        return getDataSource() != null;
    }

    public static DataSource getDataSource() {
        final String databaseURL = System.getProperty(POSTGRES_DATABASE_KEY);
        if (ds == null && databaseURL != null) {
            final PGPoolingDataSource source = new PGPoolingDataSource();
            final Map<String, String> infos = extractDbInfo(databaseURL);
            source.setServerName(infos.get("host"));
            source.setDatabaseName(infos.get("database"));
            source.setUser(infos.get("user"));
            source.setPassword(infos.get("password"));
            //        source.setMaxConnections(10);
            ds = source;
        }
        return ds;
    }

    public static void setDataSource(DataSource datasource) {
        ds = datasource;
    }

    /**
     * postgres://cstl:admin@localhost:5432/cstl-test
     *
     * @param databaseURL
     * @return
     */
    public static Map<String, String> extractDbInfo(String databaseURL) {
        Map<String, String> results = new HashMap<>();
        URI dbUri;
        try {
            dbUri = new URI(databaseURL);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("", e);
        }
        if (dbUri.getUserInfo() != null) {
            final String username = dbUri.getUserInfo().split(":")[0];
            final String password = dbUri.getUserInfo().split(":")[1];
            results.put("username", username);
            results.put("password", password);
            results.put("host", dbUri.getHost());
            results.put("port", Integer.toString(dbUri.getPort()));
            String path = dbUri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1, path.length());
            }
            results.put("database", path);
        }
        return results;
    }

}
