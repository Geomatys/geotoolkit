/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;

import org.geotoolkit.internal.sql.Dialect;
import org.geotoolkit.internal.sql.DefaultDataSource;


/**
 * The data source for the embedded EPSG database. This data source should be instantiated
 * only if {@link ThreadedEpsgFactory#createDataSource(Properties)} has detected that the
 * {@code geotk-epsg.jar} file is reachable on the classpath.
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
    private static final Map<String,EmbeddedDataSource> SOURCES = new HashMap<>(4);

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
        Connection connection;
        do {
            connection = super.getConnection();
        } while (!createIfEmpty(connection));
        return connection;
    }

    /**
     * Delegates to {@link DriverManager}.
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection;
        do {
            connection = super.getConnection(username, password);
        } while (!createIfEmpty(connection));
        return connection;
    }

    /**
     * Creates the EPSG database if it doesn't exist.
     * <p>
     * This method must be synchronized - we must disallow concurrent execution of
     * this method for the same database.
     *
     * @return {@code true} if the connection still valid after this method call,
     *         or {@code false} if it has been closed and needs to be recreated.
     */
    private synchronized boolean createIfEmpty(final Connection connection) throws SQLException {
        if (!tested) {
            tested = true; // Set first - if we fail, the failure will be considered definitive.
            if (!AnsiDialectEpsgFactory.exists(connection.getMetaData(), null)) {
                final EpsgInstaller installer = new EpsgInstaller();
                try {
                    installer.call(new EpsgScriptRunner(connection));
                    final Dialect dialect = Dialect.forURL(url);
                    if (dialect == Dialect.HSQL) {
                        dialect.shutdown(connection, url, true);
                        return false;
                    }
                } catch (IOException exception) {
                    throw new SQLNonTransientException(exception);
                }
            }
        }
        return true;
    }
}
