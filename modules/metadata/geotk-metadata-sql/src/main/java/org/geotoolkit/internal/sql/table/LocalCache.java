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
package org.geotoolkit.internal.sql.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.geotoolkit.internal.sql.StatementPool;
import org.geotoolkit.internal.sql.StatementEntry;


/**
 * A thread-local pool of {@linkplain PreparedStatement prepared statements}. Every {@link Table}
 * instances created by the same {@link Database} will share the same cache when executed in the
 * same thread. However if more than one thread is using the same {@code Table} then each thread
 * will have its own {@code LocalCache}. With this approach, we guaranteed that a JDBC connection
 * is used only from the same thread.
 *
 * {@section Synchronization}
 * Every access to a method in this interface must be synchronized on {@code this}. This
 * synchronization is necessary for preventing the {@link StatementPool} cleaner background
 * thread to close a statement before the caller finished to use it. The lock shall be
 * released only when the statement is no longer needed, as below:
 *
 * {@preformat java
 *     LocalCache cache = database.getLocalCache();
 *     synchronized (cache) {
 *         LocalCache.Stmt ce = cache.prepareStatement(table, query);
 *         PreparedStatement statement = ce.statement;
 *         // Use the statement, but don't close it.
 *         release(ce);
 *     }
 * }
 *
 * Note that this is <strong>not</strong> a synchronization mechanism for {@link Table}.
 * The synchronization on {@code this} is only for blocking the cleaner thread; it has
 * no impact on other threads.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 * @module
 */
public interface LocalCache {
    /**
     * Returns the connection to the database. Use as below:
     *
     * {@preformat java
     *     LocalCache cache = database.getLocalCache();
     *     synchronized (cache) {
     *         Connection c = cache.connection();
     *         // Use the connection inside the synchronized block, but don't close it.
     *     }
     * }
     *
     * @return The connection to the database.
     * @throws SQLException if an error occurred while fetching the connection.
     */
    Connection connection() throws SQLException;

    /**
     * Returns a prepared statement for the given SQL query. Every call to this method
     * shall be performed inside a synchronized block as documented in the class javadoc.
     *
     * @param  creator The table which is invoking this method.
     * @param  sql The SQL query.
     * @return The prepared statement.
     * @throws SQLException if an error occurred while creating the statement.
     */
    Stmt prepareStatement(Table creator, String sql) throws SQLException;



    /**
     * The {@link StatementEntry} implementation used in this module. Each instance of this
     * class shall be used in a single thread only. See the {@link LocalCache} javadoc for
     * more information.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.10
     *
     * @since 3.09
     * @module
     */
    final class Stmt extends StatementEntry {
        /**
         * Special value for {@link #stamp} meaning that the prepared statement
         * has not yet been initialized.
         */
        static final int UNINITIALIZED = -1;

        /**
         * The SQL query used for creating the statement. They are used as keys in the
         * {@link org.geotoolkit.internal.sql.StatementPool}, and sometime for logging.
         */
        final String sql;

        /**
         * A value used by {@link Table} in order to determine if the {@link #statement}
         * parameters need to be modified.
         */
        int stamp = UNINITIALIZED;

        /**
         * The execution start time in nanoseconds, used for logging purpose only.
         *
         * @since 3.16
         */
        long startTime;

        /**
         * Constructs a metadata result for the specified statement.
         *
         * @param statement The prepared statement.
         * @param sql The SQL query used for creating the statement.
         */
        Stmt(final PreparedStatement statement, final String sql) {
            super(statement);
            this.sql = sql;
        }

        /**
         * Formats this statement. Some implementations (e.g. the PostgreSQL driver) format the
         * SQL statement together with the parameter values. If it seems to be the case (we check
         * that by searching for the first word, typically {@code SELECT} or {@code INSERT}), then
         * returns the string formatted by the driver. Otherwise conservatively returns the value
         * of the {@link #sql} field, but the later doesn't have the parameter values.
         * <p>
         * This method is used only for logging or debugging purpose.
         */
        @Override
        public String toString() {
            String keyword = sql.trim();
            final int length = keyword.length();
            for (int i=0; i<length; i++) {
                if (Character.isWhitespace(keyword.charAt(i))) {
                    keyword = keyword.substring(0, i);
                    break;
                }
            }
            String s = statement.toString();
            final int i = s.indexOf(keyword);
            if (i >= 0) {
                s = s.substring(i);
            } else {
                s = sql;
            }
            return s;
        }
    }
}
