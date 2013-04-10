/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.concurrent.TimeUnit;

import org.apache.sis.util.Localized;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.logging.PerformanceLevel;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Base class for a table in a coverage {@linkplain Database database}. This class is not
 * thread-safe. For usage in multi-threads environment, the copy constructor shall be used
 * for creating a different instance of the {@code Table} subclass for each thread.
 *
 * {@section Synchronization}
 * In principle, {@code Table} implementations don't need to be synchronized since they are
 * not aimed to be used concurrently by different threads. If concurrent usage is desired,
 * then each thread shall have its own instance. Nevertheless a few synchronization are still
 * needed:
 *
 * <ul>
 *   <li><p>JDBC commands shall be executed inside a {@code synchronized(getLocalCache())} block.
 *       This is necessary in order to avoid the disposer background thread to close the
 *       JDBC statement while it still in use.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public abstract class Table implements Localized {
    /**
     * The query executed by this table.
     *
     * @see #getStatement(QueryType)
     */
    protected final Query query;

    /**
     * The query type for the current {@linkplain #statement}.
     */
    private QueryType type;

    /**
     * Incremented every time the {@link Table} configuration changed. This is compared
     * with {@link LocalCache.Stmt#stamp} in order to determine if the statement needs
     * to be set.
     */
    private int modificationCount;

    /**
     * {@cdoe true} if this table is available for reuse.
     *
     * @see #release()
     */
    transient volatile boolean canReuse;

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
     * copy <strong>only the final fields</strong>, not the fields that may be modified by
     * setter methods. This is because mutable fields may not be correctly published in a
     * multi-thread context. In addition, subclass constructors shall not modify any shared
     * object like {@link Query}.
     *
     * @param table The table to use as a template.
     */
    protected Table(final Table table) {
        query = table.query;
    }

    /**
     * Returns a copy of this table. Subclasses should invoke their copy-constructor here.
     */
    @Override
    protected abstract Table clone();

    /**
     * Returns the database that contains this table. This is the database
     * specified at construction time.
     *
     * @return The database (never {@code null}).
     * @throws IllegalStateException If this table is not connected to a database.
     */
    public final Database getDatabase() throws IllegalStateException {
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
     * Returns the lock to use for any call to a {@code getStatement(...)} method. This lock is
     * only a lock for preventing the connection to be closed before the query is finished.  It
     * is <strong>not</strong> for providing thread-safety to {@code Table} instances - since the
     * lock is thread-local, it is useless for that purpose.
     *
     * @return The lock to use in a {@code synchronized} statement.
     */
    protected final LocalCache getLocalCache() {
        return getDatabase().getLocalCache();
    }

    /**
     * Returns {@code true} if the prepared statement to be created by
     * {@link #getStatement(String)} should be able to return auto-generated keys.
     * <p>
     * The default implementation returns {@code false} in every cases. Tables with primary keys
     * (like {@link SingletonTable}) may return a different value during {@code INSERT} statements
     * if they don't supply the primary key values themselves.
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
     * This method must be invoked in a synchronized block as documented in
     * {@link #getStatement(LocalCache, QueryType)}. This is verified only if
     * assertions are enabled.
     *
     * @param  query The SQL query to prepare.
     * @return The prepared statement.
     * @throws SQLException if a SQL error occurred while configuring the statement.
     */
    private LocalCache.Stmt getStatement(final LocalCache cache, final String query) throws SQLException {
        assert Thread.holdsLock(cache);
        final LocalCache.Stmt ce = cache.prepareStatement(this, query);
        ce.startTime = System.nanoTime();
        if (modificationCount != ce.stamp) {
            final QueryType type = this.type;
            configure(cache, type, ce.statement);
            ce.stamp = modificationCount;
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
     *     final LocalCache lc = getLocalCache();
     *     synchronized(lc) {
     *         final LocalCache.Stmt ce = getStatement(lc, type);
     *         final PreparedStatement statement = ce.statement;
     *         final ResultSet results = statement.executeQuery();
     *         while (results.next()) {
     *             // ...
     *         }
     *         results.close();
     *         release(lc, ce);
     *     }
     * }
     *
     * @param  lc The value returned by {@link #getLocalCache()}.
     * @param  type The query type.
     * @return The prepared statement.
     * @throws SQLException if a SQL error occurred while configuring the statement.
     */
    protected final LocalCache.Stmt getStatement(final LocalCache lc, final QueryType type)
            throws SQLException
    {
        final String sql;
        switch (type) {
            default:         sql = query.select(lc, type); break;
            case INSERT:     sql = query.insert(lc, type); break;
            case DELETE:     sql = query.delete(lc, type); break;
            case DELETE_ALL: sql = query.delete(lc, type); break;
        }
        this.type = type;
        return getStatement(lc, sql);
    }

    /**
     * Same as {@link #getStatement(LocalCache, QueryType)}, but with a column parameter which
     * affect the SQL statement to be created. This apply only to queries performing aggregation,
     * like {@link QueryType#COUNT}.
     *
     * @param  lc The value returned by {@link #getLocalCache()}.
     * @param  type The query type.
     * @param  column The column on which to perform aggregation.
     * @return The prepared statement.
     * @throws SQLException if a SQL error occurred while configuring the statement.
     */
    protected final LocalCache.Stmt getStatement(final LocalCache lc, final QueryType type, final Column column)
            throws SQLException
    {
        final String sql;
        switch (type) {
            case COUNT: sql = query.count(lc, type, column); break;
            default: throw new IllegalArgumentException(errors().getString(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "type", type));
        }
        this.type = type;
        fireStateChanged(); // Because the column may be different on every call.
        return getStatement(lc, sql);
    }

    /**
     * Releases the given statement. This method shall be invoked after a
     * {@link #getStatement(QueryType)} method call when the statement is
     * not needed anymore.
     *
     * @param lc The value returned by {@link #getLocalCache()}.
     * @param statement The statement to release.
     * @throws SQLException If an error occurred while releasing the statement.
     */
    protected final void release(final LocalCache lc, final LocalCache.Stmt statement) throws SQLException {
        assert Thread.holdsLock(lc);
        Database.release(lc, statement);
        final Logger logger = getLogger();
        final long duration = System.nanoTime() - statement.startTime;
        final Level level = PerformanceLevel.forDuration(duration, TimeUnit.NANOSECONDS);
        if (logger.isLoggable(level)) {
            final Locale locale = getLocale();
            final LogRecord record = new LogRecord(level, String.format(locale, "(%s: %.4f s) %s",
                    Vocabulary.getResources(locale).getString(Vocabulary.Keys.DURATION),
                    duration / 1E9, statement));
            record.setSourceClassName(getClass().getName());
            record.setSourceMethodName(type.method);
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    /**
     * Invoked before an arbitrary amount of {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statements. This method <strong>must</strong> be invoked in a
     * {@code try} ... {@code finally} block as below:
     *
     * {@preformat java
     *     final LocalCache lc = getLocalCache();
     *     synchronized(lc) {
     *         boolean success = false;
     *         transactionBegin(lc);
     *         try {
     *            // Do some operation here...
     *             success = true;  // Must be the very last line in the try block.
     *         } finally {
     *             transactionEnd(lc, success);
     *         }
     *     }
     * }
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @throws SQLException if the operation failed.
     */
    protected final void transactionBegin(final LocalCache lc) throws SQLException {
        getDatabase().transactionBegin(lc);
    }

    /**
     * Invoked after the {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statements finished.
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @param  success {@code true} if the operation succeed and should be committed,
     *         or {@code false} if we should rollback.
     * @throws SQLException if the commit or the rollback failed.
     */
    protected final void transactionEnd(final LocalCache lc, final boolean success) throws SQLException {
        getDatabase().transactionEnd(lc, success);
    }

    /**
     * Invoked automatically by {@link #getStatement(String)} for a newly created statement, or
     * for an existing statement when this table {@linkplain #fireStateChanged changed its state}.
     * Subclasses should override this method if they need to set SQL parameters according the
     * table state. The default implementation does nothing.
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @param  type The query type.
     * @param  statement The statement to configure (never {@code null}).
     * @throws SQLException if a SQL error occurred while configuring the statement.
     */
    protected void configure(LocalCache lc, QueryType type, PreparedStatement statement) throws SQLException {
    }

    /**
     * Returns the column at the specified index, or {@code null} if none.
     *
     * @param  index The column index (number starts at 1).
     * @return The column, or {@code null} if none.
     */
    final Column getColumn(final int index) {
        if (index >= 1) {
            final List<Column> columns = query.getColumns(getQueryType());
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
        return type;
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
     * @param  lc The value returned by {@link #getLocalCache()}.
     * @return The calendar for date calculation in this table.
     */
    protected final Calendar getCalendar(final LocalCache lc) {
        final Database database = getDatabase();
        final Calendar calendar = database.getCalendar(lc);
        assert calendar.getTimeZone().equals(database.getTimeZone());
        return calendar;
    }

    /**
     * Notifies that the state of this table changed. Subclasses should invoke this method every
     * time some {@code setXXX(...)} method has been invoked on this {@code Table} object.
     *
     * @param property The name of the property that changed, or {@code null} if unknown.
     */
    protected void fireStateChanged(final String property) {
        fireStateChanged();
    }

    /**
     * Implementation of {@link #fireStateChanged(String)} invoked directly when the state that
     * changed is not a property stored by this {@code Table} instance.
     */
    private void fireStateChanged() {
        final Database database = query.database;
        do {
            if (database != null) {
                modificationCount = database.modificationCount.incrementAndGet();
            } else {
                modificationCount++;
            }
        } while (modificationCount == LocalCache.Stmt.UNINITIALIZED); // Paranoiac check.
    }

    /**
     * Notifies that a recoverable error occurred.
     *
     * @param method The method name in which the error occurred.
     * @param exception The error.
     */
    final void unexpectedException(final String method, final Throwable exception) {
        Logging.unexpectedException(getLogger(), getClass(), method, exception);
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
     * Sets the {@linkplain LogRecord#setLoggerName logger name},
     * {@linkplain LogRecord#setSourceClassName source class name} and
     * {@linkplain LogRecord#setSourceMethodName source method name} in the given record,
     * and {@linkplain Logger#log(LogRecord) logs} it.
     *
     * @param methodName The name of the caller method.
     * @param record     The record to log.
     */
    protected final void log(final String methodName, final LogRecord record) {
        final Logger logger = getLogger();
        record.setLoggerName(logger.getName());
        record.setSourceClassName(getClass().getName());
        record.setSourceMethodName(methodName);
        logger.log(record);
    }

    /**
     * Returns the logger to use. The default implementation looks for the package name
     * of the implementing class.
     *
     * @return The logger to use (never {@code null}).
     *
     * @since 3.16
     */
    public final Logger getLogger() {
        final Database database = query.database;
        if (database != null) {
            final Logger logger = database.getLogger();
            if (logger != null) {
                return logger;
            }
        }
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
     * Marks this table as available for reuse. This method can be invoked after a call
     * to {@link Database#getTable(Class)} when the caller is sure that he will not use
     * this table anymore. Example:
     *
     * {@preformat java
     *     Table table = Database.getTable(...);
     *     // use the table
     *     table.release();
     * }
     *
     * In case of doubt, do not invoke this method.
     */
    public final void release() {
        canReuse = true;
    }
}
