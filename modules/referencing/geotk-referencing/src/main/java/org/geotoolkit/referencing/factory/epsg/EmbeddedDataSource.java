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
package org.geotoolkit.referencing.factory.epsg;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;

import org.geotoolkit.internal.jdbc.DefaultDataSource;


/**
 * The data source for the embedded EPSG database. This data source should be instantiated
 * only if {@link ThreadedEpsgFactory#createDataSource(Properties)} has detected that the
 * {@code geotk-epsg.jar} file is reacheable on the classpath.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class EmbeddedDataSource extends DefaultDataSource {
    /**
     * The data sources created up to date. We reuse existing instances in order
     * to initialize the {@link #tested} field only once. This map will typically
     * contains only one instance, but we use a map anyway in case the URL change
     * during the JVM lifetime.
     * <p>
     * An other reason for reusing existing instance is that we must ensure that
     * {@link #createIfEmpty} is synchronized in such a way that there is no
     * concurrent execution of that method for the same database..
     */
    private static final Map<String,EmbeddedDataSource> SOURCES = new HashMap<String,EmbeddedDataSource>(4);

    /**
     * {@code true} if the presence of the EPSG database has been tested.
     */
    private boolean tested;

    /**
     * Creates a new data source for the given URL.
     */
    private EmbeddedDataSource(final String url) {
        super(url);
    }

    /**
     * Returns the data source for the given URL.
     */
    static EmbeddedDataSource instance(final String url) {
        synchronized (SOURCES) {
            EmbeddedDataSource source = SOURCES.get(url);
            if (source == null) {
                source = new EmbeddedDataSource(url);
                SOURCES.put(url, source);
            }
            return source;
        }
    }

    /**
     * Delegates to {@link DriverManager}.
     */
    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = super.getConnection();
        createIfEmpty(connection);
        return connection;
    }

    /**
     * Delegates to {@link DriverManager}.
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        final Connection connection = super.getConnection(username, password);
        createIfEmpty(connection);
        return connection;
    }

    /**
     * Creates the EPSG database if it doesn't exist.
     * <p>
     * This method must be synchronized - we must disallow concurrent execution of
     * this method for the same database.
     */
    private synchronized void createIfEmpty(final Connection connection) throws SQLException {
        if (tested) {
            return;
        }
        tested = true; // Set first - if we fail, the failure will be considered definitive.
        final DatabaseMetaData metadata = connection.getMetaData();
        /*
         * In current implementation, EpsgScriptRunner.setSchema(String) does not quote
         * the schema name. So we need to change the case before to check for the schema.
         */
        String schema = EpsgInstaller.DEFAULT_SCHEMA;
        if (metadata.storesLowerCaseIdentifiers()) {
            schema = schema.toLowerCase(Locale.US);
        } else if (metadata.storesUpperCaseIdentifiers()) {
            schema = schema.toUpperCase(Locale.US);
        }
        final ResultSet result = metadata.getSchemas(null, schema);
        final boolean exists = result.next();
        assert !exists || schema.equals(result.getString("TABLE_SCHEM"));
        result.close();
        if (!exists) {
            final EpsgInstaller installer = new EpsgInstaller();
            try {
                installer.call(new EpsgScriptRunner(connection));
            } catch (IOException exception) {
                throw new SQLNonTransientException(exception);
            }
        }
    }
}
