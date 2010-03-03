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

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Set;
import java.util.LinkedHashSet;

import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.internal.sql.TypeMapper;


/**
 * Base class for tables with a {@code getEntry(...)} method returning at most one entry.
 * The entries are uniquely identified by an identifier, which may be a string or an integer.
 * <p>
 * {@code SingletonTable} defines the {@link #getEntries()}, {@link #getEntry(String)} and
 * {@link #getEntry(int)} methods. Subclasses shall provide implementation for the following
 * methods:
 * <p>
 * <ul>
 *   <li>{@link #createEntry}<br>
 *       Creates an entry for the current row.</li>
 * </ul>
 * <p>
 * The entries created by this class are cached for faster access the next time a
 * {@code getEntry(...)} method is invoked again.
 *
 * @param <E> The kind of entries to be created by this table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public abstract class SingletonTable<E extends Entry> extends Table {
    /**
     * The main parameter to use for the identification of an entry, or {@code null} if unknown.
     */
    private final Parameter pkParam;

    /**
     * The entries created up to date. The keys shall be {@link Integer} or {@link String}
     * instances only. This field is never shared between different {@code Table} instances.
     */
    private final Cache<Comparable<?>,E> cache;

    /**
     * Creates a new table using the specified query. The optional {@code pkParam} argument
     * defines the parameter to use for looking an element by identifier. This is usually the
     * parameter for the value to search in the primary key column. This information is needed
     * for {@link #getEntry(String)} execution.
     *
     * @param  query The query to use for this table.
     * @param  pkParam The parameter for looking an element by name, or {@code null} if none.
     * @throws IllegalArgumentException if the specified parameters are not one of those
     *         declared for {@link QueryType#SELECT}.
     */
    protected SingletonTable(final Query query, final Parameter pkParam) {
        super(query);
        this.pkParam = pkParam;
        cache = new Cache<Comparable<?>,E>();
    }

    /**
     * Creates a new table connected to the same {@linkplain #getDatabase database} and using
     * the same {@linkplain #query query} than the specified table. Subclass constructors should
     * not modify the query, since it is shared.
     * <p>
     * This constructor shares also the cache. This is okay if the entries created by the
     * table does not depend on the table configuration.
     *
     * @param table The table to use as a template.
     */
    protected SingletonTable(final SingletonTable<E> table) {
        super(table);
        pkParam = table.pkParam;
        cache   = table.cache;
    }

    /**
     * Returns the columns index of the primary key, or 0 if none. Note that in this method,
     * the "<cite>primary key</cite>" is inferred from the {@code pkParam} argument given to
     * the constructor. This is usually the primary key defined in the database, but this is
     * not verified.
     *
     * @return The index of the primary key column, or 0 if none.
     */
    private int getPrimaryKeyColumn() {
        return (pkParam != null) ? pkParam.column.indexOf(getQueryType()) : 0;
    }

    /**
     * Sets the value of the parameter which is checking the primary key.
     *
     * @param  statement The statement in which to set the parameter value.
     * @param  identifier The value to set.
     * @throws SQLException If the parameter can not be set.
     */
    private void setPrimaryKeyParameter(final PreparedStatement statement, final Comparable<?> identifier)
            throws SQLException
    {
        final int pkIndex = indexOf(pkParam);
        if (identifier instanceof Number) {
            statement.setInt(pkIndex, ((Number) identifier).intValue());
        } else {
            statement.setString(pkIndex, identifier.toString());
        }
    }

    /**
     * Returns {@code true} if the given column in the result set is numeric.
     *
     * @param  results The result set.
     * @param  pkIndex The index of the column to inspect (typically the primary key), or 0 if none.
     * @return {@code true} If the given column in the given result set is numeric.
     * @throws SQLException If an error occured while fetching the metadata.
     */
    private static boolean isNumeric(final ResultSet results, final int pkIndex) throws SQLException {
        if (pkIndex != 0) {
            final Class<?> type = TypeMapper.toJavaType(results.getMetaData().getColumnType(pkIndex));
            if (type != null) {
                return Number.class.isAssignableFrom(type);
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if {@link #getEntries} should accept the given element.
     * The default implementation always returns {@code true}.
     *
     * @param  entry En element created by {@link #getEntries}.
     * @return {@code true} if the element should be added to the set returned by {@link #getEntries}.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from the database.
     */
    protected boolean accept(final E entry) throws CatalogException, SQLException {
        return true;
    }

    /**
     * Creates an {@link Element} object for the current {@linkplain ResultSet result set} row.
     * This method is invoked automatically by {@link #getEntry(String)} and {@link #getEntries()}.
     *
     * @param  results  The result set to use for fetching data. Only the current row should be
     *                  used, i.e. {@link ResultSet#next} should <strong>not</strong> be invoked.
     * @return The element for the current row in the specified {@code results}.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from the database.
     */
    protected abstract E createEntry(final ResultSet results) throws CatalogException, SQLException;

    /**
     * Invokes the user's {@link #createEntry(ResultSet)} method, but wraps {@link SQLException}
     * into {@link ServerException} because the later provides more informations.
     *
     * @throws CatalogException If an error occured during {@link #createEntry(ResultSet)}.
     * @throws SQLException If an error occured during {@link CatalogException#setMetadata}.
     *         Note that this is not an error occuring during normal execution, but rather
     *         an error occuring while querying database metadata for building the exception.
     */
    private E createEntry(final ResultSet results, final Comparable<?> key)
            throws CatalogException, SQLException
    {
        CatalogException exception;
        try {
            return createEntry(results);
        } catch (CatalogException cause) {
            if (cause.isMetadataInitialized()) {
                throw cause;
            }
            exception = cause;
        } catch (SQLException cause) {
            exception = new CatalogException(cause);
        }
        exception.setMetadata(this, results, getPrimaryKeyColumn(), key);
        exception.clearColumnName();
        throw exception;
    }

    /**
     * Returns an element for the given identifier.
     *
     * @param  identifier The name or numeric identifier of the element to fetch.
     * @return The element for the given identifier, or {@code null} if {@code identifier} was null.
     * @throws NoSuchRecordException if no record was found for the specified key.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from the database.
     */
    public final E getEntry(final Comparable<?> identifier) throws NoSuchRecordException, CatalogException, SQLException {
        if (identifier == null) {
            return null;
        }
        E entry = cache.peek(identifier);
        if (entry == null) {
            final Cache.Handler<E> handler = cache.lock(identifier);
            try {
                entry = handler.peek();
                if (entry == null) synchronized (getLock()) {
                    final LocalCache.Stmt ce = getStatement(QueryType.SELECT);
                    final PreparedStatement statement = ce.statement;
                    setPrimaryKeyParameter(statement, identifier);
                    final ResultSet results = statement.executeQuery();
                    while (results.next()) {
                        final E candidate = createEntry(results, identifier);
                        if (entry == null) {
                            entry = candidate;
                        } else if (!entry.equals(candidate)) {
                            // The ResultSet will be closed by the constructor below.
                            throw new DuplicatedRecordException(this, results, getPrimaryKeyColumn(), identifier);
                        }
                    }
                    if (entry == null) {
                        // The ResultSet will be closed by the constructor below.
                        throw new NoSuchRecordException(this, results, getPrimaryKeyColumn(), identifier);
                    }
                    results.close();
                    ce.release();
                }
            } finally {
                handler.putAndUnlock(entry);
            }
        }
        return entry;
    }

    /**
     * Returns all entries available in the database. The returned set may or may not be
     * serializable or modifiable, at implementation choice. If allowed, modification in
     * the returned set will not alter this table.
     *
     * @return The set of entries. May be empty, but neven {@code null}.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from the database.
     */
    public final Set<E> getEntries() throws CatalogException, SQLException {
        return getEntries(QueryType.LIST);
    }

    /**
     * Returns all entries available in the database using the specified query type.
     *
     * @param  type The query type, usually {@link QueryType#LIST}.
     * @return The set of entries. May be empty, but never {@code null}.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from the database.
     */
    private Set<E> getEntries(final QueryType type) throws CatalogException, SQLException {
        final Set<E> entries = new LinkedHashSet<E>();
        synchronized (getLock()) {
            final LocalCache.Stmt ce = getStatement(type);
            final ResultSet results = ce.statement.executeQuery();
            final int pkIndex = getPrimaryKeyColumn();
            final boolean isNumeric = isNumeric(results, pkIndex);
            while (results.next()) {
                final Comparable<?> identifier;
                if (isNumeric) {
                    identifier = results.getInt(pkIndex);
                } else {
                    identifier = results.getString(pkIndex);
                }
                E entry = cache.peek(identifier);
                if (entry == null) {
                    final Cache.Handler<E> handler = cache.lock(identifier);
                    try {
                        entry = handler.peek();
                        if (entry == null) {
                            entry = createEntry(results, identifier);
                        }
                    } finally {
                        handler.putAndUnlock(entry);
                    }
                }
                if (accept(entry) && !entries.add(entry)) {
                    // The ResultSet will be closed by the constructor below.
                    throw new DuplicatedRecordException(this, results, pkIndex, identifier);
                }
            }
            results.close();
            ce.release();
        }
        return entries;
    }

    /**
     * Checks if an entry exists for the given name. This method do not attempt to create
     * the entry and doesn't check if the entry is valid.
     *
     * @param  identifier The identifier of the entry to fetch.
     * @return {@code true} if an entry of the given identifier was found.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from the database.
     */
    public boolean exists(final Comparable<?> identifier) throws CatalogException, SQLException {
        if (identifier == null) {
            return false;
        }
        if (cache.containsKey(identifier)) {
            return true;
        }
        final boolean hasNext;
        synchronized (getLock()) {
            final LocalCache.Stmt ce = getStatement(QueryType.EXISTS);
            final PreparedStatement statement = ce.statement;
            setPrimaryKeyParameter(statement, identifier);
            final ResultSet results = statement.executeQuery();
            hasNext = results.next();
            results.close();
            ce.release();
        }
        return hasNext;
    }

    /**
     * Deletes the entry for the given identifier.
     *
     * @param  identifier The identifier of the entry to delete.
     * @return The number of entries deleted.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from or writting to the database.
     */
    public int delete(final Comparable<?> identifier) throws CatalogException, SQLException {
        if (identifier == null) {
            return 0;
        }
        final int count;
        boolean success = false;
        synchronized (getLock()) {
            transactionBegin();
            try {
                final LocalCache.Stmt ce = getStatement(QueryType.DELETE);
                final PreparedStatement statement = ce.statement;
                setPrimaryKeyParameter(statement, identifier);
                count = update(statement);
                ce.release();
                success = true;
            } finally {
                transactionEnd(success);
            }
        }
        // Update the cache only on successfuly deletion.
        cache.remove(identifier);
        return count;
    }

    /**
     * Deletes many elements. "Many" depends on the configuration set by {@link #configure}.
     * It may be the whole table. Note that this action may be blocked if the user doesn't
     * have the required database authorisations, or if some records are still referenced in
     * foreigner tables.
     *
     * @return The number of elements deleted.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occured will reading from or writting to the database.
     */
    public int clear() throws CatalogException, SQLException {
        final int count;
        boolean success = false;
        synchronized (getLock()) {
            transactionBegin();
            try {
                final LocalCache.Stmt ce = getStatement(QueryType.CLEAR);
                count = update(ce.statement);
                ce.release();
                success = true;
            } finally {
                transactionEnd(success);
            }
        }
        // Update the cache only on successfuly deletion.
        cache.clear();
        return count;
    }

    /**
     * Executes the specified SQL {@code INSERT}, {@code UPDATE} or {@code DELETE} statement.
     * As a special case, this method does not execute the statement during testing and debugging
     * phases. In the later case, this method rather prints the statement to the stream specified
     * to {@link Database#setUpdateSimulator}.
     *
     * @param  statement The statement to execute.
     * @return The number of elements updated.
     * @throws SQLException if an error occured.
     */
    private int update(final PreparedStatement statement) throws SQLException {
        final Database database = getDatabase();
        database.ensureOngoingTransaction();
        final PrintWriter out = database.getUpdateSimulator();
        if (out != null) {
            out.println(statement);
            return 0;
        } else {
            return statement.executeUpdate();
        }
    }

    /**
     * Executes the specified SQL {@code INSERT}, {@code UPDATE} or {@code DELETE} statement,
     * which is expected to insert exactly one record. As a special case, this method does not
     * execute the statement during testing and debugging phases. In the later case, this method
     * rather prints the statement to the stream specified to {@link Database#setUpdateSimulator}.
     *
     * @param  statement The statement to execute.
     * @return {@code true} if the singleton has been found and updated.
     * @throws IllegalUpdateException if more than one elements has been updated.
     * @throws SQLException if an error occured.
     */
    protected final boolean updateSingleton(final PreparedStatement statement)
            throws IllegalUpdateException, SQLException
    {
        final int count = update(statement);
        if (count > 1) {
            throw new IllegalUpdateException(getLocale(), count);
        }
        return count != 0;
    }
}
