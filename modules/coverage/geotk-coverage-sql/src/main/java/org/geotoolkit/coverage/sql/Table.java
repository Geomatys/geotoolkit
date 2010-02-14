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

import java.sql.SQLException;
import java.sql.SQLDataException;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.resources.Errors;


/**
 * Base class for a table in a coverage {@linkplain Database database}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
@ThreadSafe(concurrent=true)
class Table {
    /**
     * The logger for table-related events.
     */
    static final Logger LOGGER = Logging.getLogger(Table.class);

    /**
     * The query executed by this table.
     *
     * @see #getStatement(QueryType)
     */
    final Query query;

    /**
     * Information about a query being executed.
     * Those information can not be shared between different threads.
     *
     * @see #getStatement(QueryType)
     * @see #getStatement(String)
     */
    private static final class Session {
        /**
         * The query type for the current {@linkplain #statement}.
         */
        QueryType type;

        /**
         * The last used query in SQL language.
         * This is the query used for the {@link #statement} creation.
         */
        String sql;

        /**
         * The prepared statement for fetching data.
         */
        PreparedStatement statement;

        /**
         * {@code true} if the {@linkplain #statement} needs to be
         * {@linkplain Table#configure configured}. This is the case
         * when a new statement has just been created.
         */
        boolean changed;
    }

    /**
     * Holds thread-local information about a query being executed.
     */
    private final ThreadLocal<Session> session = new ThreadLocal<Session>() {
        @Override protected Session initialValue() {
            return new Session();
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
    Table(final Query query) {
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
    Table(final Table table) {
        query = table.query;
    }

    /**
     * Returns the database that contains this table. This is the database
     * specified at construction time.
     *
     * @return The database (never {@code null}).
     * @throws IllegalStateException If this table is not connected to a database.
     */
    final Database getDatabase() throws IllegalStateException {
        final Database database = query.database;
        if (database == null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.NO_DATA_SOURCE));
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
    final String getProperty(final ConfigurationKey key) {
        final Database database = query.database;
        return (database != null) ? database.getProperty(key) : key.defaultValue;
    }

    /**
     * Formats the given statement. If the given statement is an implementation which is known
     * to have a well suited {@code toString()} method (like the PostgreSQL driver), then that
     * method is invoked directly. Otherwise this query is formatted as a fallback (in which
     * case the parameter values are missing).
     * <p>
     * This method is used only for logging or debugging purpose.
     *
     * @param  statement The prepared statement to format.
     * @param  query The SQL query used for preparing the statement.
     * @return A string representation of the given prepared statement.
     */
    private static String format(final PreparedStatement statement, final String query) {
        final String className = statement.getClass().getName();
        if (className.startsWith("org.postgresql")) {
            return statement.toString();
        }
        return query;
    }

    /**
     * Returns a prepared statement for the given SQL query. If the specified {@code query} is the
     * same one than last time this method has been invoked, then this method returns the same
     * {@link PreparedStatement} instance (if it still available). Otherwise this method closes
     * the previous statement and creates a new one.
     * <p>
     * If a new statement is created, or if the table has {@linkplain #fireStateChanged
     * changed its state} since the last call, then this method invokes {@link #configure}.
     *
     * @param  query The SQL query to prepare.
     * @return The prepared statement.
     * @throws CatalogException if the statement can not be configured.
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    final PreparedStatement getStatement(final String query) throws CatalogException, SQLException {
        final Session s = session.get();
        PreparedStatement statement = s.statement;
        if (!query.equals(s.sql)) {
            if (statement != null) {
                s.sql       = null;
                s.statement = null; // Must be done first in case the following line fails.
                statement.close();
            }
            s.statement = statement = getDatabase().getConnection().prepareStatement(query);
            s.changed   = true;
            s.sql       = query;
        }
        if (statement != null) {
            if (s.changed) {
                final QueryType type = s.type;
                configure(type, statement);
                final Level level = (type != null) ? type.getLoggingLevel() : Level.FINE;
                if (LOGGER.isLoggable(level)) {
                    final LogRecord record = new LogRecord(level, format(statement, query));
                    record.setSourceClassName(getClass().getName());
                    final String method;
                    switch (type) {
                        case SELECT: method = "getEntry"; break;
                        case LIST:   method = "getEntries"; break;
                        default:     method = "getStatement"; break;
                    }
                    record.setSourceMethodName(method);
                    record.setLoggerName(LOGGER.getName());
                    LOGGER.log(record);
                }
            }
        }
        return statement;
    }

    /**
     * Returns a prepared statement for the given query type.
     *
     * @param  type The query type.
     * @return The prepared statement.
     * @throws CatalogException if the statement can not be configured.
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    final PreparedStatement getStatement(final QueryType type) throws CatalogException, SQLException {
        final String sql;
        switch (type) {
            default:     sql = query.select(type); break;
            case INSERT: sql = query.insert(type); break;
            case DELETE: sql = query.delete(type); break;
            case CLEAR:  sql = query.delete(type); break;
        }
        session.get().type = type;
        return getStatement(sql);
    }

    /**
     * Invoked before an arbitrary amount of {@code INSERT}, {@code UPDATE} or {@code DELETE}
     * SQL statements. This method <strong>must</strong> be invoked in a {@code try} ...
     * {@code finally} block as below:
     *
     * {@preformat java
     *     boolean success = false;
     *     transactionBegin();
     *     try {
     *        // Do some operation here...
     *         success = true;  // Must be the very last line in the try block.
     *     } finally {
     *         transactionEnd(success);
     *     }
     * }
     *
     * @throws SQLException if the operation failed.
     */
    final void transactionBegin() throws SQLException {
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
    final void transactionEnd(final boolean success) throws SQLException {
        getDatabase().transactionEnd(success);
    }

    /**
     * Invoked automatically by {@link #getStatement(String)} for a newly created statement, or
     * for an existing statement when this table {@linkplain #fireStateChanged changed its state}.
     *
     * {@section Overriding}
     * Subclasses should override this method if they need to set SQL parameters according the
     * table state. Overriding methods should invoke {@code super.configure(type, statement)}
     * first.
     *
     * @param  type The query type.
     * @param  statement The statement to configure (never {@code null}).
     * @throws CatalogException if the statement can not be configured.
     * @throws SQLException if a SQL error occured while configuring the statement.
     */
    void configure(final QueryType type, final PreparedStatement statement)
            throws CatalogException, SQLException
    {
        session.get().changed = false;
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
     * Delegates to <code>column.{@linkplain Column#indexOf(QueryType) indexOf}(type)</code>,
     * except that an exception is thrown if the specified column is not applicable to the
     * current query type. The {@code type} value is the argument given to the last call to
     * {@link #getStatement(QueryType)}.
     *
     * @param  column The column.
     * @return The column index (starting with 1).
     * @throws SQLException if the specified column is not applicable.
     */
    final int indexOf(final Column column) throws SQLException {
        final QueryType type = session.get().type;
        final int index = column.indexOf(type);
        if (index > 0) {
            return index;
        }
        throw new SQLDataException(
                Errors.format(Errors.Keys.UNSUPPORTED_OPERATION_$1, type) + ". " +
                Errors.format(Errors.Keys.CANT_READ_DATABASE_TABLE_$2, column.table, column.name));
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
    final int indexOf(final Parameter parameter) throws SQLException {
        final QueryType type = session.get().type;
        final int index = parameter.indexOf(type);
        if (index > 0) {
            return index;
        }
        throw new SQLDataException(Errors.format(Errors.Keys.UNSUPPORTED_OPERATION_$1, type));
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
    final Calendar getCalendar() {
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
    void fireStateChanged(final String property) throws CatalogException {
        /*
         * Set the change flag anyway, because this method is invoked when the
         * change has already been applied so it is too late for preventing it.
         */
        session.get().changed = true;
        if (unmodifiable) {
            throw new CatalogException(Errors.format(Errors.Keys.UNMODIFIABLE_OBJECT_$1, getClass()));
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
        Logging.unexpectedException(LOGGER, getClass(), method, exception);
    }

    /**
     * Ensures that the given argument is non-null. This is a convenience method for argument checks.
     *
     * @param  name  The argument name.
     * @param  value The argument value.
     * @throws IllegalArgumentException if the given value is {@code null}.
     */
    static void ensureNonNull(final String name, final Object value) {
        if (value == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
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
