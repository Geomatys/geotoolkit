/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.sql;

import java.util.Map;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.util.logging.Logging;


/**
 * A pool of prepared statements with a maximal capacity. Oldest statements are automatically
 * closed and removed from the map when the number of statements exceed the maximal capacity.
 * Inactive statements are also closed after some timeout.
 *
 * {@section Synchronization}
 * Every access to this pool <strong>must</strong> be synchronized on {@code this}.
 * Synchronization is user-responsability; this class is not thread safe alone. It
 * will be verified if assertions are enabled.
 * <p>
 * Synchronization must be provided by the user because we typically need synchronized block
 * wider than the {@code get} and {@code put} scope. Execution of a prepared statement may
 * also need to be done inside the synchronized block, because a single JDBC connection can
 * not be assumed thread-safe.
 * <p>
 * Example of usage:
 *
 * {@preformat java
 *     StatementPool<String,StatementEntry> pool = ...;
 *     String key = ...;
 *     synchronized (pool) {
 *         // Get an entry, or create a new one if no entry is available.
 *         // Note that we remove the entry from the map for preventing
 *         // 'removeEldestEntry' to close it before we are done.
 *         StatementEntry entry = pool.remove(key);
 *         if (entry == null) {
 *             entry = new StatementEntry(someStatement);
 *         }
 *         // Use the statement and give it back to the pool once we are
 *         // done. We do not put it back in case of SQLException.
 *         entry.statement.doSomeStuff();
 *         if (pool.put(key, entry) != null) {
 *             throw new AssertionError();
 *         }
 *     }
 * }
 *
 * @param <K> The type of keys.
 * @param <V> The type of values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@SuppressWarnings("serial")
public final class StatementPool<K,V extends StatementEntry> extends LinkedHashMap<K,V> implements Runnable {
    /**
     * The maximum number of prepared statements to be kept in the pool.
     */
    private final int capacity;

    /**
     * The data source object using for fetching the connection to the database.
     */
    private final DataSource dataSource;

    /**
     * The connection to the database. This is automatically closed and set to
     * {@code null} when every statements have been closed.
     */
    private Connection connection;

    /**
     * Creates a new pool of the given capacity.
     *
     * @param capacity The maximum number of entries to be kept in the pool.
     * @param dataSource The datasource to the database.
     */
    public StatementPool(final int capacity, final DataSource dataSource) {
        super(Utilities.hashMapCapacity(capacity));
        this.capacity = capacity;
        this.dataSource = dataSource;
    }

    /**
     * Creates a new pool with the same configuration than the given pool.
     * The new pool will use its own connection - it will not be shared.
     *
     * @param source The pool from which to copy the configuration.
     */
    public StatementPool(final StatementPool<K,V> source) {
        super(Utilities.hashMapCapacity(source.capacity));
        capacity   = source.capacity;
        dataSource = source.dataSource;
    }

    /**
     * Returns the connection to the database, creating a new one if needed. This method should
     * be invoked inside a synchronized block wider than just the scope of this method in order
     * to ensure that the connection is used by only one thread at time. This is also necessary
     * for preventing the background thread to close the connection too early.
     *
     * @return The connection to the database.
     * @throws SQLException If an error occured while fetching the connection.
     */
    public final Connection connection() throws SQLException {
        assert Thread.holdsLock(this);
        Connection c = connection;
        if (c == null) {
            assert isEmpty();
            connection = c = dataSource.getConnection();
            Threads.executor(true).execute(this);
        }
        return c;
    }

    /**
     * Overriden in order to check for synchronization if assertions are enabled.
     * See class-javadoc for why the synchronization must be performed by the caller.
     */
    @Override
    public final V get(final Object key) {
        assert Thread.holdsLock(this);
        return super.get(key);
    }

    /**
     * Overriden in order to check for synchronization if assertions are enabled.
     * See class-javadoc for why the synchronization must be performed by the caller.
     * <p>
     * <b>NOTE:</b> If this method returns a non-null value, then the caller
     * should consider {@linkplain StatementEntry#close() closing} it.
     */
    @Override
    public final V put(final K key, final V value) {
        value.touch();
        assert Thread.holdsLock(this);
        return super.put(key, value);
    }

    /**
     * Overriden in order to check for synchronization if assertions are enabled.
     * See class-javadoc for why the synchronization must be performed by the caller.
     * <p>
     * <b>NOTE:</b> If this method returns a non-null value, then the caller
     * should consider {@linkplain StatementEntry#close() closing} it.
     */
    @Override
    public final V remove(final Object key) {
        assert Thread.holdsLock(this);
        return super.remove(key);
    }

    /**
     * If the map has reached its maximal capacity, removes the eldest entries.
     * This method is invoked indirectly by {@code put} and {@code putAll} operations,
     * <strong>which must be invoked in a synchronized block</strong>.
     */
    @Override
    protected final boolean removeEldestEntry(final Map.Entry<K,V> eldest) {
        assert Thread.holdsLock(this);
        if (size() > capacity) {
            eldest.getValue().closeQuietly();
            return true;
        }
        return false;
    }

    /**
     * Closes all statements and remove them from the map.
     * The connection is not closed.
     */
    @Override
    public final void clear() {
        assert Thread.holdsLock(this);
        for (final StatementEntry entry : values()) {
            entry.closeQuietly();
        }
        super.clear();
    }

    /**
     * Executed in a background thread for closing statements after their expiration time.
     * A new thread must be created every time the first statement is put in an empty map.
     * <p>
     * This method is public as an implementation side-effect and should never been invoked
     * directly.
     */
    @Override
    public final synchronized void run() {
        /*
         * An initial wait time is preferable because the map may be empty for a few
         * milliseconds (this occurs when MetadataSource has just been created).
         */
        long waitTime = StatementEntry.TIMEOUT;
sleep:  do {
            /*
             * Increase the wait time by an arbitrary amount in order to
             * increase the chances to close many statements in one pass.
             */
            waitTime += 500;
            try {
                wait(waitTime);
            } catch (InterruptedException e) {
                // Someone doesn't want to let us sleep. Check if there is any statement
                // to close, then go sleep again if the map is not empty.
            }
            final long currentTime = System.currentTimeMillis();
            final Iterator<V> it = values().iterator();
            while (it.hasNext()) {
                final V entry = it.next();
                waitTime = entry.expireTime - currentTime;
                if (waitTime > 0) {
                    // Some statements can not be disposed yet.
                    continue sleep;
                }
                it.remove();
                entry.closeQuietly();
            }
            // We have closed every statements. Terminate the thread.
            // A new one will be created later if new statements are created.
            break;
        } while (true);
        /*
         * No more prepared statements. Close the connection.
         */
        final Connection c = this.connection;
        connection = null;
        if (c != null) try {
            c.close();
        } catch (SQLException e) {
            Logging.recoverableException(Logging.getLogger(StatementPool.class), Connection.class, "close", e);
        }
    }

    /**
     * Closes all statements and remove them from the map.
     *
     * @throws SQLException If an error occured while closing the statements.
     */
    public final synchronized void close() throws SQLException {
        final Iterator<V> it = values().iterator();
        while (it.hasNext()) {
            it.next().close();
            it.remove();
        }
        final Connection c = this.connection;
        connection = null;
        if (c != null) {
            c.close();
        }
        // If the run() method is executed in an other thread and that thread
        // is waiting, wake it up in order to cause the thread to die now.
        notify();
    }
}
