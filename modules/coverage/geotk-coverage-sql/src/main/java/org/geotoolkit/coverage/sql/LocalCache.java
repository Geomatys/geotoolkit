/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.geotoolkit.internal.sql.StatementPool;
import org.geotoolkit.internal.sql.StatementEntry;


/**
 * A thread-local pool of {@linkplain PreparedStatement prepared statements}. Every {@link Table}
 * instances created by the same {@link Database} will share the same cache when executed in the
 * same thread. However if more than one thread is using the same {@code Table} then each thread
 * will have its own {@code LocalCache}. With this approach, we garanteed that a JDBC connection
 * is used only from the same thread.
 *
 * {@section Synchronization}
 * Every access to a method in this interface must be sychronized on {@code this}. This
 * synhronization is necessary for preventing the {@link StatementPool} cleaner background
 * thread to close a statement before the caller finished to use it. The lock shall be
 * released only when the statement is no longer needed, as below:
 *
 * {@preformat java
 *     LocalCache cache = database.getLocalCache();
 *     synchronized (cache) {
 *         LocalCache.Stmt ce = cache.prepareStatement(table, query);
 *         PreparedStatement statement = ce.statement;
 *         // Use the statement, but don't close it.
 *         ce.release();
 *     }
 * }
 *
 * Note that this is <strong>not</strong> the synchronization mechanism for concurrent
 * usage of {@link Table}. The synchronization on {@code this} is only for blocking the
 * cleaner thread; it has no impact on other threads.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.09
 * @module
 */
interface LocalCache {
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
     * @throws SQLException if an error occured while fetching the connection.
     */
    Connection connection() throws SQLException;

    /**
     * Returns a prepared statement for the given SQL query. Every call to this method
     * shall be performed inside a synchronized block as documented in the class javadoc.
     *
     * @param  creator The table which is invoking this method.
     * @param  sql The SQL query.
     * @return The prepared statement.
     * @throws SQLException if an error occured while creating the statement.
     */
    Stmt prepareStatement(Table creator, String sql) throws SQLException;




    /**
     * A (<var>table</var>, <var>SQL query</var>) tupple used as keys in {@link StatementPool}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.09
     *
     * @since 3.09
     * @module
     */
    static final class SQL {
        /**
         * The table which created the SQL query.
         */
        final Table table;

        /**
         * The SQL query used for the prepared statement.
         */
        final String sql;

        /**
         * Creates a new key.
         *
         * @param table The table which created the SQL query.
         * @param sql The SQL query used for the prepared statement.
         */
        SQL(final Table table, final String sql) {
            this.table = table;
            this.sql   = sql;
        }

        /**
         * Returns a hash code value for this key.
         */
        @Override
        public int hashCode() {
            return System.identityHashCode(table) ^ sql.hashCode();
        }

        /**
         * Returns {@code true} if this key is equals to the given object.
         */
        @Override
        public boolean equals(final Object other) {
            if (other instanceof SQL) {
                final SQL that = (SQL) other;
                return table == that.table && sql.equals(that.sql);
            }
            return false;
        }
    }



    /**
     * The {@link StatementEntry} implementation used in this module. Each instance of this
     * class shall be used in a single thread only. See the {@link LocalCache} javadoc for
     * more information.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.09
     *
     * @since 3.09
     * @module
     */
    static final class Stmt extends StatementEntry {
        /**
         * The SQL query used for creating the statement.
         */
        final SQL key;

        /**
         * A value used by {@link Table} in order to determine if the {@link #statement}
         * parameters need to be modified.
         */
        int stamp;

        /**
         * Constructs a metadata result for the specified statement.
         *
         * @param statement The prepared statement.
         * @param key The SQL query used for creating the statement.
         */
        Stmt(final PreparedStatement statement, final SQL key) {
            super(statement);
            this.key = key;
        }

        /**
         * Puts the statement back in the pool. Every call to this method shall be performed
         * inside a synchronized block as documented in the {@link LocalCache} class javadoc.
         *
         * @param  entry The statement to release.
         * @throws SQLException if an error occured while releasing the statement.
         */
        public void release() throws SQLException {
            key.table.getDatabase().release(this);
        }

        /**
         * Formats this statement. If the prepared statement is an implementation which is known
         * to have a well suited {@code toString()} method (like the PostgreSQL driver), then that
         * method is invoked directly. Otherwise this query is formatted as a fallback (in which
         * case the parameter values are missing).
         * <p>
         * This method is used only for logging or debugging purpose.
         */
        @Override
        public String toString() {
            final String className = statement.getClass().getName();
            if (className.startsWith("org.postgresql")) {
                return statement.toString();
            }
            return key.sql;
        }
    }
}
