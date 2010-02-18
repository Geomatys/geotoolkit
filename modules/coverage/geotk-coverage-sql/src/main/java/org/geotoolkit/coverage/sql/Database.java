/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
import java.sql.SQLNonTransientException;
import javax.sql.DataSource;
import java.io.PrintWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Properties;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.reflect.Constructor;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.Localized;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.sql.StatementPool;
import org.geotoolkit.internal.sql.AuthenticatedDataSource;


/**
 * Connection to a catalog database through JDBC (<cite>Java Database Connectivity</cite>).
 * The connection is specified by a {@link DataSource}, which should create pooled connections.
 *
 * {@section Concurrency}
 * This class is thread-safe and concurrent. However it is recommanded to access it only from a
 * limited number of threads (for example from a {@link java.util.concurrent.ThreadPoolExecutor})
 * and to recycle those threads, because this class uses a new connection for each thread.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
@ThreadSafe(concurrent=true)
class Database implements Localized {
    /**
     * The default 4-dimensional Coordinate Reference System used for queries in database.
     * This is a compound of {@link DefaultGeographicCRS#WGS84_3D WGS84_3D} with
     * {@link DefaultTemporalCRS#TRUNCATED_JULIAN TRUNCATED_JULIAN}.
     */
    static CoordinateReferenceSystem DEFAULT_CRS;
    static {
        final Map<String,Object> properties = new HashMap<String,Object>(4);
        properties.put(CoordinateReferenceSystem.NAME_KEY, "WGS84");
        properties.put(CoordinateReferenceSystem.DOMAIN_OF_VALIDITY_KEY, DefaultExtent.WORLD);
        DEFAULT_CRS = new DefaultCompoundCRS(properties,
                DefaultGeographicCRS.WGS84_3D,
                DefaultTemporalCRS.TRUNCATED_JULIAN);
    }

    /**
     * The data source, which is mandatory. It is recommanded to provide a data source that
     * create pooled connections, because connections may be created and closed often.
     */
    private final AuthenticatedDataSource source;

    /**
     * The database catalog where the tables are defined, or {@code null} if none.
     */
    final String catalog;

    /**
     * The database schema where the tables are defined, or {@code null} if none.
     * If {@code null}, then the tables will be located using the default mechanism
     * on the underlying database. On PostgreSQL, the search order is determined by
     * the {@code "search_path"} database variable.
     */
    final String schema;

    /**
     * The timezone to use for reading and writing dates in the database. This timezone can be
     * set by the {@link ConfigurationKey#TIMEZONE} property and will be used by the calendar
     * returned by {@link Table#getCalendar()}.
     */
    private final TimeZone timezone;

    /**
     * Provides information for SQL statements being executed.
     * Those information can not be shared between different threads.
     * <p>
     * This class opportunistically extends {@code StatementPool}, which duplicates the work of
     * connection pools provided in modern JDBC driver. But we use {@code StatementPool} anyway
     * because we want the {@code org.geotoolkit.coverage.sql} package to work raisonably well
     * in the absence of connection pool, and because the {@code StatementPool} timer provides
     * a convenient way to let the connections being closed automatically without worrying if
     * the connection is still in use. The alternative would be to maintain a {@code usageCount}
     * variable incremented when a {@link Table} uses the connection, and decremented when it is
     * done - but even this alternative would close the connection just before we get a new one,
     * for example when creating a {@code LayerEntry} and immediately invoking one of its method
     * which deferred the database query.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.09
     *
     * @since 3.09
     * @module
     */
    @SuppressWarnings("serial")
    private static final class Session extends StatementPool<LocalCache.SQL, LocalCache.Stmt>
            implements LocalCache
    {
        /**
         * If non-null, SQL {@code INSERT}, {@code UPDATE} or {@code DELETE} statements will
         * not be executed but will rather be printed to this stream. This is used for testing
         * and debugging purpose only.
         * <p>
         * This field is thread-local because only one thread can write at a given time.
         * If two different threads want to write, one thread will block the other thanks
         * to the {@link #transactionLock}, but we may want to set its "update simulator"
         * status while we are waiting for the lock to become available.
         */
        PrintWriter updateSimulator;

        /**
         * The calendar to use for reading and writing dates in a database. This calendar
         * is created when first needed and shall use the {@link Database#timezone}.
         *
         * @see Database#getCalendar()
         */
        Calendar calendar;

        /**
         * The locale to use for formatting messages, or {@code null} for the system-default.
         */
        Locale locale;

        /**
         * Creates a new instance for the given data source.
         * We will cache a maximum of 8 prepared statements.
         */
        Session(final DataSource source) {
            super(8, source);
        }

        /**
         * Returns a prepared statement for the given SQL query.
         * See {@link LocalCache} javadoc for usage example.
         */
        @Override
        public Stmt prepareStatement(final Table table, final String sql) throws SQLException {
            final SQL key = new SQL(table, sql);
            Stmt value = remove(key);
            if (value == null) {
                value = new Stmt(connection().prepareStatement(sql), key);
            }
            return value;
        }
    }

    /**
     * Holds thread-local information for SQL statements being executed.
     */
    private final ThreadLocal<Session> session = new ThreadLocal<Session>() {
        @Override protected Session initialValue() {
            return new Session(source);
        }
    };

    /**
     * Lock for transactions performing write operations. The {@link Connection#commit} or
     * {@link Connection#rollback} method will be invoked when the lock count reach zero.
     *
     * @see #transactionBegin()
     * @see #transactionEnd(boolean)
     */
    private final ReentrantLock transactionLock = new ReentrantLock(true);

    /**
     * The tables created up to date. Every access to this map
     * will need to be synchronized on {@code tables}.
     */
    private final Map<Class<? extends Table>, Table> tables = new HashMap<Class<? extends Table>, Table>();

    /**
     * The coordinate reference system used for performing the search in the database.
     * It must match the CRS used in the geometry columns indexed by PostGIS.
     */
    final CoordinateReferenceSystem crs;

    /**
     * The properties given at construction time, or {@code null} if none.
     */
    private final Properties properties;

    /**
     * Creates a new instance using the same configuration than the given instance.
     * The new instance will have its own, initially empty, cache.
     *
     * @param toCopy The existing instance to copy.
     */
    public Database(final Database toCopy) {
        this.crs        = toCopy.crs;
        this.source     = toCopy.source;
        this.properties = toCopy.properties;
        this.timezone   = toCopy.timezone;
        this.catalog    = toCopy.catalog;
        this.schema     = toCopy.schema;
    }

    /**
     * Creates a new instance using the provided data source and configuration properties.
     * If a properties map is specified, then the keys enumerated in {@link ConfigurationKey}
     * will be used.
     *
     * @param  source The data source.
     * @param  properties The configuration properties, or {@code null} if none.
     */
    public Database(final DataSource source, final Properties properties) {
        this(source, DEFAULT_CRS, properties);
    }

    /**
     * Creates a new instance using the provided data source, CRS and configuration properties.
     * If a properties map is specified, then the keys enumerated in {@link ConfigurationKey}
     * will be used.
     *
     * @param  datasource The data source.
     * @param  crs The coordinate reference system used in PostGIS tables.
     * @param  properties The configuration properties, or {@code null} if none.
     *
     * @todo This constructor is not yet public because we need to be more explicit on the CRS
     *       constraints (basically (x,y,z,t) axes order) and test that it really works.
     */
    private Database(final DataSource datasource, final CoordinateReferenceSystem crs,
            final Properties properties)
    {
        Table.ensureNonNull("datasource", datasource);
        Table.ensureNonNull("crs", crs);
        final String username, password, tz;
        this.crs        = crs;
        this.properties = properties; // Must be set before to ask for properties.
        username = getProperty(ConfigurationKey.USER);
        password = getProperty(ConfigurationKey.PASSWORD);
        tz       = getProperty(ConfigurationKey.TIMEZONE);
        timezone = !tz.equalsIgnoreCase("local") ? TimeZone.getTimeZone(tz) : TimeZone.getDefault();
        catalog  = getProperty(ConfigurationKey.CATALOG);
        schema   = getProperty(ConfigurationKey.SCHEMA);
        source   = new AuthenticatedDataSource(datasource, username, password, Boolean.TRUE);
    }

    /**
     * Returns a property. The key is usually a constant like {@link ConfigurationKey#TIMEZONE}.
     *
     * @param  key The key for the property to fetch.
     * @return The property value, or {@code null} if none and there is no default value.
     */
    final String getProperty(final ConfigurationKey key) {
        // No need to synchronize since 'Properties' is already synchronized.
        return (properties != null) ? properties.getProperty(key.key, key.defaultValue) : key.defaultValue;
    }

    /**
     * Returns the locale to use for formatting messages. This is used mostly for error messages
     * in exceptions, and for warnings during {@linkplain javax.imageio.ImageRead image read}
     * operations.
     *
     * @return The locale for message formatting, or {@code null} for the
     *         {@linkplain Locale#getDefault() system default}.
     */
    @Override
    public Locale getLocale() {
        return session.get().locale;
    }

    /**
     * Sets the locale to use for formatting messages. The given locale applies only
     * to the {@linkplain Thread#currentThread() current thread}.
     *
     * @param locale The new locale for message formatting, or {@code null} for the
     *        {@linkplain Locale#getDefault() system default}.
     */
    public void setLocale(final Locale locale) {
        session.get().locale = locale;
    }

    /**
     * Returns the timezone in which the dates in the database are expressed. This information
     * can be specified through the {@link ConfigurationKey#TIMEZONE} property. It is used in
     * order to convert the dates from the database timezone to UTC.
     *
     * @return The time zone for dates to appear in database records.
     *
     * @see Table#getCalendar()
     */
    public TimeZone getTimeZone() {
        return (TimeZone) timezone.clone();
    }

    /**
     * Creates and returns a new calendar using the database timezone.
     *
     * {@section Implementation note}
     * We use {@link Locale#CANADA} because it is close to the format typically used in
     * databases ({@code "yyyy/mm/dd"}). We don't use {@link Calendar#getInstance()}
     * because it may be a totally different and incompatible calendar for our purpose.
     */
    final Calendar getCalendar() {
        final Session s = session.get();
        Calendar calendar = s.calendar;
        if (calendar == null) {
            s.calendar = calendar = new GregorianCalendar(timezone, Locale.CANADA);
        }
        return calendar;
    }

    /**
     * Returns the data source which has been specified to the constructor.
     *
     * @return The data source.
     */
    public DataSource getDataSource() {
        return source.wrapped;
    }

    /**
     * Returns the {@link LocalCache} instance for the current thread. This is the interface to
     * use for getting the JDBC connection and prepared statements. See the {@link LocalCache}
     * javadoc for usage examples.
     */
    final LocalCache getLocalCache() {
        return session.get();
    }

    /**
     * Put the given statement back in the pool. This method is invoked by
     * {@link LocalCache.Stmt#release()} only. The later is the API to use.
     */
    final void release(final LocalCache.Stmt entry) throws SQLException {
        if (session.get().put(entry.key, entry) != null) {
            throw new AssertionError(); // Should never happen.
        }
    }

    /**
     * Returns a table of the specified type.
     *
     * @param  <T> The table class.
     * @param  type The table class.
     * @return An instance of a table of the specified type.
     * @throws NoSuchTableException if the specified type is unknown to this database.
     */
    final <T extends Table> T getTable(final Class<T> type) throws NoSuchTableException {
        synchronized (tables) {
            T table = type.cast(tables.get(type));
            if (table == null) {
                try {
                    final Constructor<T> c = type.getConstructor(Database.class);
                    c.setAccessible(true);
                    table = c.newInstance(this);
                } catch (Exception exception) { // Too many exeptions for enumerating them.
                    throw new NoSuchTableException(Classes.getShortName(type), exception);
                }
                table.freeze();
                tables.put(type, table);
            }
            assert !table.isModifiable() : table;
            return table;
        }
    }

    /**
     * If non-null, SQL {@code INSERT}, {@code UPDATE} or {@code DELETE} statements will not be
     * executed but will rather be printed to this stream. This is used for testing and debugging
     * purpose only.
     *
     * @param out Where to print SQL statements which would perform changes in the database content.
     */
    final void setUpdateSimulator(final PrintWriter out) {
        session.get().updateSimulator = out;
    }

    /**
     * Returns the value set by the last call to {@link #setUpdateSimulator},
     * or {@code null} if none.
     *
     * @return Where to print SQL statements which would perform changes in the database content.
     */
    final PrintWriter getUpdateSimulator() {
        return session.get().updateSimulator;
    }

    /**
     * Invoked before an arbitrary amount of {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statement. This method <strong>must</strong> be invoked in a {@code try} ... {@code
     * finally} block as below:
     *
     * {@preformat java
     *     final LocalCache cache = getLocalCache();
     *     synchronized (cache) {
     *         boolean success = false;
     *         transactionBegin();
     *         try {
     *             // Do some operation here...
     *             success = true;
     *         } finally {
     *             transactionEnd(success);
     *         }
     *     }
     * }
     *
     * @throws SQLException If the operation failed.
     */
    final void transactionBegin() throws SQLException {
        boolean success = false;
        transactionLock.lock();
        try {
            if (transactionLock.getHoldCount() == 1) {
                final LocalCache sp = getLocalCache();
                assert Thread.holdsLock(sp); // Necessary for blocking the cleaner thread.
                final Connection connection = sp.connection();
                connection.setReadOnly(false);
                connection.setAutoCommit(false);
                success = true;
            }
        } finally {
            if (!success) {
                transactionLock.unlock();
            }
        }
    }

    /**
     * Invoked after the {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statement finished.
     *
     * @param  success {@code true} if the operation succeed and should be commited,
     *         or {@code false} if we should rollback.
     * @throws SQLException If the commit or the rollback failed.
     */
    final void transactionEnd(final boolean success) throws SQLException {
        ensureOngoingTransaction();
        try {
            if (transactionLock.getHoldCount() == 1) {
                final LocalCache sp = getLocalCache();
                assert Thread.holdsLock(sp); // Necessary for blocking the cleaner thread.
                final Connection connection = sp.connection();
                if (success) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
                connection.setAutoCommit(true);
                connection.setReadOnly(true);
            }
        } finally {
            transactionLock.unlock();
        }
    }

    /**
     * Ensures that the current thread is allowed to performs a transaction.
     */
    final void ensureOngoingTransaction() throws SQLException {
        if (!transactionLock.isHeldByCurrentThread()) {
            throw new SQLNonTransientException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.THREAD_DOESNT_HOLD_LOCK));
        }
    }

    /**
     * Closes the connection used by the current thread and restore the attributes to their
     * initial state. This method affect the current thread only; other threads (if any) are
     * not affected.
     *
     * @throws SQLException If an error occured while closing the connection.
     */
    public void reset() throws SQLException {
        session.get().close();
        session.remove();
    }
}
