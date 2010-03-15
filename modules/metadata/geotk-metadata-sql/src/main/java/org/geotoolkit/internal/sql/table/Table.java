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
package org.geotoolkit.internal.sql.table;

import java.sql.SQLException;
import java.sql.SQLDataException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.util.Localized;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.resources.Errors;


/**
 * Base class for a table in a coverage {@linkplain Database database}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
@ThreadSafe(concurrent=true)
public class Table implements Localized {
    /**
     * The query executed by this table.
     *
     * @see #getStatement(QueryType)
     */
    protected final Query query;

    /**
     * Information about a query being executed.
     * Those information can not be shared between different threads.
     *
     * @see #getStatement(QueryType)
     * @see #getStatement(String)
     */
    private static final class Session {
        /**
         * The pool of prepared statements. All tables used in the same thread share the same
         * {@code LocalCache} instance. However different threads use different instances.
         * <p>
         * All JDBC operations must be synchronized on this object, in order to prevent the
         * background cleaner thread to close the statements before we finished to use them.
         * <p>
         * This field is provided only for convenience. It shall be the same reference than
         * {@link Database#getLocalCache()} all the time.
         */
        final LocalCache cache;

        /**
         * The query type for the current {@linkplain #statement}.
         */
        QueryType type;

        /**
         * Incremented every time the {@link Table} configuration changed. This is compared
         * with {@link LocalCache.Stmt#stamp} in order to determine if the statement needs
         * to be set.
         */
        int modificationCount;

        /**
         * Creates a new instance.
         */
        Session(final LocalCache cache) {
            this.cache = cache;
            modificationCount = 1;
        }
    }

    /**
     * Holds thread-local information about a query being executed.
     */
    private final ThreadLocal<Session> session = new ThreadLocal<Session>() {
        @Override protected Session initialValue() {
            return new Session(getDatabase().getLocalCache());
        }
    };

    /**
     * {@code true} if this table is unmodifiable (i.e. no {@code set} method are allowed).
     * A table is always modifiable upon construction, but may become unmodifiable at some
     * later stage (when {@link #freeze()} is invoked).
     */
    private volatile boolean unmodifiable;

    /**
     * Creates a new table using the specified query. The query given in argument should be some
     * subclass with {@code addFooColumn(...)} and {@code addParameter(...)} methods invoked in
     * its constructor.
     *
     * @param query The query to use for this table.
     */
    protected Table(final Query query) {
        ensureNonNull("query", query);
        this.query = query;
    }

    /**
     * Creates a new table connected to the same {@linkplain #getDatabase database} and using
     * the same {@linkplain #query} than the specified table. Subclass constructors should
     * not modify the query, since it is shared.
     *
     * @param table The table to use as a template.
     */
    protected Table(final Table table) {
        query = table.query;
    }

    /**
     * Returns the database that contains this table. This is the database
     * specified at construction time.
     *
     * @return The database (never {@code null}).
     * @throws IllegalStateException If this table is not connected to a database.
     */
    protected final Database getDatabase() throws IllegalStateException {
        final Database database = query.database;
        if (database == null) {
            throw new IllegalStateException(errors().getString(Errors.Keys.NO_DATA_SOURCE));
        }
        return database;
    }

    /**
     * Returns a property for the given key. This method tries to get the property
     * from the {@linkplain #getDatabase() database} if available, or return the
     * {@linkplain ConfigurationKey#defaultValue default value} otherwise.
     *
     * @param  key The property key, usually one of {@link Database} constants.
     * @return The property value, or {@code null} if none.
     */
    protected final String getProperty(final ConfigurationKey key) {
        final Database database = query.database;
        return (database != null) ? database.getProperty(key) : key.defaultValue;
    }

    /**
     * Returns the lock to use for any call to a {@code getStatement(...)} method. Note that
     * this lock will <strong>not</strong> block concurrent usage of {@code Table}. This is
     * only a lock for preventing the connection to be closed before the query is finished.
     *
     * @return The lock to use in a {@code synchronized} statement.
     */
    protected final Object getLock() {
        return session.get().cache;
    }

    /**
     * Returns {@code true} if the prepared statement to be created by
     * {@link #getStatement(String)} should be able to return auto-generated keys.
     * <p>
     * The default implementation returns {@code false} in every cases. Tables with primary keys
     * (like {@link SingletonTable}) may return a different value during {@code INSERT} statements
     * if they don't supply the primary key values themself.
     */
    boolean wantsAutoGeneratedKeys() {
        return false;
    }

    /**
     * Returns a prepared statement for the given SQL query. If the specified {@code query} is the
     * same one than last time this method has been invoked, then this method returns the same
     * {@link PreparedStatement} instance (if it still available).
     * <p>
     * If a new statement is created, or if the table has {@linkplain #fireStateChanged
     * changed its state} since the last call, then this method invokes {@link #configure}.
     * <p>
     * This method must be invoked in a synchronized block as below. Note that the lock
     * will not prevent concurrent usage of this table. This is just a lock for preventing
     * the JDBC resources to be reclaimed while they are still in use.
     *
     * {@preformat java
     *     synchronized(getLock()) {
     *         // Perform all JDBC work here.
     *     }
     * }
     *
     * @param  query The SQL query to prepare.
     * @return The prepared statement.
     * @throws CatalogException if the statement can not be configured.
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    final LocalCache.Stmt getStatement(final String query) throws CatalogException, SQLException {
        final Session s = session.get();
        assert Thread.holdsLock(s.cache);
        final LocalCache.Stmt ce = s.cache.prepareStatement(this, query);
        if (s.modificationCount != ce.stamp) {
            final QueryType type = s.type;
            configure(type, ce.statement);
            ce.stamp = s.modificationCount;
            final Level level = (type != null) ? type.getLoggingLevel() : Level.FINE;
            final Logger logger = getLogger();
            if (logger.isLoggable(level)) {
                final LogRecord record = new LogRecord(level, ce.toString());
                record.setSourceClassName(getClass().getName());
                final String method;
                switch (type) {
                    case SELECT: method = "getEntry"; break;
                    case LIST:   method = "getEntries"; break;
                    default:     method = "getStatement"; break;
                }
                record.setSourceMethodName(method);
                record.setLoggerName(logger.getName());
                logger.log(record);
            }
        }
        return ce;
    }

    /**
     * Returns a prepared statement for the given query type.
     * <p>
     * This method must be invoked in a synchronized block as below. Note that the lock
     * will not prevent concurrent usage of this table. This is just a lock for preventing
     * the JDBC resources to be reclaimed while they are still in use.
     *
     * {@preformat java
     *     synchronized(getLock()) {
     *         // Perform all JDBC work here.
     *     }
     * }
     *
     * @param  type The query type.
     * @return The prepared statement.
     * @throws CatalogException if the statement can not be configured.
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    protected final LocalCache.Stmt getStatement(final QueryType type) throws CatalogException, SQLException {
        final String sql;
        switch (type) {
            default:         sql = query.select(type); break;
            case INSERT:     sql = query.insert(type); break;
            case DELETE:     sql = query.delete(type); break;
            case DELETE_ALL: sql = query.delete(type); break;
        }
        session.get().type = type;
        return getStatement(sql);
    }

    /**
     * Invoked before an arbitrary amount of {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statements. This method <strong>must</strong> be invoked in a
     * {@code try} ... {@code finally} block as below:
     *
     * {@preformat java
     *     synchronized(getLock()) {
     *         boolean success = false;
     *         transactionBegin();
     *         try {
     *            // Do some operation here...
     *             success = true;  // Must be the very last line in the try block.
     *         } finally {
     *             transactionEnd(success);
     *         }
     *     }
     * }
     *
     * @throws SQLException if the operation failed.
     */
    protected final void transactionBegin() throws SQLException {
        getDatabase().transactionBegin();
    }

    /**
     * Invoked after the {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statements finished.
     *
     * @param  success {@code true} if the operation succeed and should be commited,
     *         or {@code false} if we should rollback.
     * @throws SQLException if the commit or the rollback failed.
     */
    protected final void transactionEnd(final boolean success) throws SQLException {
        getDatabase().transactionEnd(success);
    }

    /**
     * Invoked automatically by {@link #getStatement(String)} for a newly created statement, or
     * for an existing statement when this table {@linkplain #fireStateChanged changed its state}.
     * Subclasses should override this method if they need to set SQL parameters according the
     * table state. The default implementation does nothing.
     *
     * @param  type The query type.
     * @param  statement The statement to configure (never {@code null}).
     * @throws CatalogException if the statement can not be configured.
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    void configure(final QueryType type, final PreparedStatement statement)
            throws CatalogException, SQLException
    {
    }

    /**
     * Returns the column at the specified index, or {@code null} if none.
     *
     * @param  index The column index (number starts at 1).
     * @return The column, or {@code null} if none.
     */
    final Column getColumn(final int index) {
        if (index >= 1) {
            final List<Column> columns = query.getColumns(session.get().type);
            if (columns != null && index <= columns.size()) {
                return columns.get(index - 1);
            }
        }
        return null;
    }

    /**
     * Returns the current query type. This is {@code null} if {@link #getStatement(QueryType)}
     * has never been invoked. But once at least one query has been initiated, it should never
     * be null.
     */
    final QueryType getQueryType() {
        return session.get().type;
    }

    /**
     * Delegates to <code>column.{@linkplain Column#indexOf(QueryType) indexOf}(type)</code>,
     * except that an exception is thrown if the specified column is not applicable to the
     * current query type. The {@code type} value is the argument given to the last call to
     * {@link #getStatement(QueryType)}.
     *
     * @param  column The column.
     * @return The column index (starting with 1).
     * @throws SQLException if the specified column is not applicable.
     */
    protected final int indexOf(final Column column) throws SQLException {
        final QueryType type = getQueryType();
        final int index = column.indexOf(type);
        if (index > 0) {
            return index;
        }
        final IndexedResourceBundle errors = errors();
        throw new SQLDataException(
                errors.getString(Errors.Keys.UNSUPPORTED_OPERATION_$1, type) + ". " +
                errors.getString(Errors.Keys.CANT_READ_DATABASE_TABLE_$2, column.table, column.name));
    }

    /**
     * Delegates to <code>parameter.{@linkplain Parameter#indexOf indexOf}(type)</code>,
     * except that an exception is thrown if the specified parameter is not applicable to the
     * current query type. The {@code type} value is the argument given to the last call to
     * {@link #getStatement(QueryType)}.
     *
     * @param  parameter The parameter.
     * @return The parameter index (starting with 1).
     * @throws SQLException if the specified parameter is not applicable.
     */
    protected final int indexOf(final Parameter parameter) throws SQLException {
        final QueryType type = getQueryType();
        final int index = parameter.indexOf(type);
        if (index > 0) {
            return index;
        }
        throw new SQLDataException(errors().getString(Errors.Keys.UNSUPPORTED_OPERATION_$1, type));
    }

    /**
     * Returns a calendar using the {@linkplain Database#getTimeZone() database time zone}.
     * This calendar should be used for fetching dates from the database as in the example
     * below:
     *
     * {@preformat java
     *     Calendar   calendar = getCalendar();
     *     Timestamp startTime = resultSet.getTimestamp(1, calendar);
     *     Timestamp   endTime = resultSet.getTimestamp(2, calendar);
     * }
     *
     * This calendar should be used for storing dates as well.
     *
     * @return The calendar for date calculation in this table.
     */
    protected final Calendar getCalendar() {
        final Database database = getDatabase();
        final Calendar calendar = database.getCalendar();
        assert calendar.getTimeZone().equals(database.getTimeZone());
        return calendar;
    }

    /**
     * Notifies that the state of this table changed. Subclasses should invoke this method every
     * time some {@code setXXX(...)} method has been invoked on this {@code Table} object.
     *
     * {@section Overriding}
     * If a subclass override this method, then it must invoke
     * {@code super.fireStateChanged(property)} first.
     *
     * @param property The name of the property that changed.
     * @throws CatalogException If this table is not modifiable.
     */
    protected void fireStateChanged(final String property) throws CatalogException {
        /*
         * Set the change flag anyway, because this method is invoked when the
         * change has already been applied so it is too late for preventing it.
         */
        session.get().modificationCount++;
        if (unmodifiable) {
            throw new CatalogException(errors().getString(Errors.Keys.UNMODIFIABLE_OBJECT_$1, getClass()));
        }
    }

    /**
     * Returns {@code true} if this table is modifiable.
     *
     * @return {@code true} if this table is modifiable.
     */
    final boolean isModifiable() {
        return !unmodifiable;
    }

    /**
     * Marks this table as unmodifiable.
     */
    final void freeze() {
        unmodifiable = true;
    }

    /**
     * Notifies that a recoverable error occured.
     *
     * @param method The method name in which the error occured.
     * @param exception The error.
     */
    final void unexpectedException(final String method, final Throwable exception) {
        Logging.unexpectedException(getLogger(), getClass(), method, exception);
    }

    /**
     * Ensures that the given argument is non-null. This is a convenience method for argument checks
     * in constructors. This method does not take in account the table locale because the table is
     * typically not fully constructed when this method is invoked.
     *
     * @param  name  The argument name.
     * @param  value The argument value.
     * @throws IllegalArgumentException if the given value is {@code null}.
     */
    protected static void ensureNonNull(final String name, final Object value) {
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Returns the resources to use for formatting error messages.
     *
     * @return The {@link Errors} resource bundle.
     */
    protected final IndexedResourceBundle errors() {
        return Errors.getResources(getLocale());
    }

    /**
     * Returns the logger to use. The default implementation looks for the package name
     * of the implementing class.
     *
     * @return The logger to use.
     */
    protected Logger getLogger() {
        return Logging.getLogger(getClass());
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
    public final Locale getLocale() {
        final Database database = query.database;
        return (database != null) ? database.getLocale() : null;
    }

    /**
     * Returns a string representation of this table, mostly for debugging purpose.
     */
    @Override
    public String toString() {
        /*
         * Implementation note:  the string representation does not contain any thread-local
         * information, because debugger in IDE typically invokes this method in a different
         * thread than the application we want to debug.
         */
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName());
        buffer.append('[');
        if (unmodifiable) {
            buffer.append("unmodifiable");
        }
        return buffer.append(']').toString();
    }
}
