/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.factory.wkt;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.geotoolkit.factory.Hints;
import org.apache.sis.util.Disposable;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.ThreadedAuthorityFactory;
import org.geotoolkit.referencing.factory.NoSuchFactoryException;


/**
 * Provides caching services for a {@link DirectPostgisFactory}. Also provides multi-thread
 * concurrency, but this is not the main purpose of this class.
 * <p>
 * This class implements only the {@link CRSAuthorityFactory} interface because the PostGIS
 * {@code "spatial_ref_sys"} table is usually not designed for handling anything else that
 * CRS definitions.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
final class CachingPostgisFactory extends ThreadedAuthorityFactory implements CRSAuthorityFactory, Disposable {
    /**
     * Provides connection to the PostGIS database.
     */
    private final DataSource datasource;

    /**
     * Creates a instance using the given datasource. Current implementation caches a maximum
     * of 10 CRS by strong references (more may be cached by weak references), and allows a
     * maximum of 2 concurrent threads searching in the database (there is no maximum number
     * of threads when the requested CRS is presents in the cache).
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     * @param datasource Provides connection to the PostGIS database.
     */
    CachingPostgisFactory(final Hints userHints, final DataSource datasource) {
        super(userHints, 10, 2);
        this.datasource = datasource;
        WKTParsingAuthorityFactory.copyRelevantHints(userHints, hints);
        setTimeout(2000); // Intentionally short timeout (2 seconds).
    }

    /**
     * Creates the backing store authority factory. This method is invoked the first time a
     * {@code createXXX(...)} method is invoked. It may also be invoked again if additional
     * factories are needed in different threads, or if all factories have been disposed
     * after the timeout.
     *
     * @return The backing store to uses in {@code createXXX(...)} methods.
     * @throws NoSuchFactoryException if the backing store has not been found.
     * @throws FactoryException if the creation of backing store failed for an other reason.
     */
    @Override
    protected AbstractAuthorityFactory createBackingStore() throws FactoryException {
        final Hints localHints = EMPTY_HINTS.clone();
        synchronized (this) {
            localHints.putAll(hints);
        }
        final Connection connection;
        try {
            connection = datasource.getConnection();
        } catch (SQLException e) {
            if ("08001".equals(e.getSQLState())) {
                // Connection failed (typically because the server is unknown).
                throw new NoSuchFactoryException(e);
            }
            throw new FactoryException(e);
        }
        try {
            return new DirectPostgisFactory(localHints, connection);
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException more) {
                e.setNextException(more);
            }
            throw new FactoryException(e);
        }
    }

    /**
     * Closes all JDBC connections created by this factory. This method is provided because
     * instances of this class are typically created explicitly, rather than being registered
     * in a {@code FactoryRegistry}. In such case, the user needs to dispose it explicitly as
     * well. This method is accessible through the {@link Disposable} interface.
     */
    @Override
    public void dispose() {
        dispose(false);
    }
}
