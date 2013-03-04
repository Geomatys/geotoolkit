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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLDataException;
import java.sql.PreparedStatement;
import java.util.Set;
import java.util.LinkedHashSet;

import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.collection.Cache;
import org.geotoolkit.internal.sql.TypeMapper;
import org.geotoolkit.resources.Errors;


/**
 * Base class for tables with a {@code getEntry(...)} method returning at most one entry.
 * The entries are uniquely identified by an identifier, which may be a string or an integer.
 * <p>
 * {@code SingletonTable} defines the {@link #getEntries()}, {@link #getEntry(String)} and
 * {@link #getEntry(int)} methods. Subclasses shall provide implementation for the following
 * methods:
 * <p>
 * <ul>
 *   <li>{@link #configure(QueryType, PreparedStatement)} (optional)</li>
 *   <li>{@link #createEntry(ResultSet)}: Creates an entry for the current row.</li>
 * </ul>
 * <p>
 * The entries created by this class are cached for faster access the next time a
 * {@code getEntry(...)} method is invoked again.
 *
 * @param <E> The kind of entries to be created by this table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public abstract class SingletonTable<E extends Entry> extends Table {
    /**
     * The main parameters to use for the identification of an entry, or an empty array if none.
     */
    private final Parameter[] pkParam;

    /**
     * The entries created up to date. The keys shall be {@link Integer}, {@link String} or
     * {@link MultiColumnsIdentifier} instances only. Note that this field is shared between
     * different {@code Table} instances of the same kind created for the same database.
     */
    private final Cache<Comparable<?>,E> cache;

    /**
     * A generator of {@link String} identifiers, created when first needed.
     */
    private transient NameGenerator generator;

    /**
     * Creates a new table using the specified query. The optional {@code pkParam} argument
     * defines the parameters to use for looking an element by identifier. This is usually the
     * parameter for the value to search in the primary key column. This information is needed
     * for {@link #getEntry(String)} execution.
     *
     * @param  query The query to use for this table.
     * @param  pkParam The parameters for looking an element by name.
     * @throws IllegalArgumentException if the specified parameters are not one of those
     *         declared for {@link QueryType#SELECT}.
     */
    protected SingletonTable(final Query query, final Parameter... pkParam) {
        super(query);
        this.pkParam = pkParam.clone();
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
     * Returns the 1-based column indices of the primary keys. Note that some elements in the
     * returned array may be 0 if the corresponding parameter is not applicable to the current
     * query type.
     * <p>
     * This method infers the "<cite>primary keys</cite>" from the {@code pkParam} argument
     * given to the constructor. This is usually the primary key defined in the database,
     * but this is not verified.
     *
     * @return The indices of the primary key columns.
     */
    private int[] getPrimaryKeyColumns() {
        final QueryType type = getQueryType();
        final int[] indices = new int[pkParam.length];
        for (int i=0; i<indices.length; i++) {
            indices[i] = pkParam[i].column.indexOf(type);
        }
        return indices;
    }

    /**
     * Returns the first value of {@link #getPrimaryKeyColumns()} which is different than 0,
     * or 0 if none. This is a convenience method used only for formatting exception messages.
     *
     * @return The index of the first primary key column, or 0 if none.
     */
    private int getPrimaryKeyColumn() {
        return getPrimaryKeyColumn(getPrimaryKeyColumns());
    }

    /**
     * Returns the first value of the given array which is different than zero.
     * If none is found, returns zero.
     */
    private static int getPrimaryKeyColumn(final int[] pkIndices) {
        for (final int column : pkIndices) {
            if (column != 0) {
                return column;
            }
        }
        return 0;
    }

    /**
     * Sets the value of the parameters associated to the primary key columns.
     *
     * @param  statement The statement in which to set the parameter value.
     * @param  identifier The identifier to set in the statement.
     * @throws SQLException If the parameter can not be set.
     */
    private void setPrimaryKeyParameter(final PreparedStatement statement, final Comparable<?> identifier)
            throws SQLException
    {
        final Comparable<?>[] identifiers;
        if (identifier instanceof MultiColumnIdentifier<?>) {
            identifiers = ((MultiColumnIdentifier<?>) identifier).getIdentifiers();
        } else {
            identifiers = new Comparable<?>[] {identifier};
        }
        if (identifiers.length != pkParam.length) {
            throw new CatalogException(errors().getString(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
        for (int i=0; i<identifiers.length; i++) {
            final Comparable<?> id = identifiers[i];
            final int pkIndex = indexOf(pkParam[i]);
            if (id instanceof Number) {
                statement.setInt(pkIndex, ((Number) id).intValue());
            } else {
                statement.setString(pkIndex, id.toString());
            }
        }
    }

    /**
     * Returns {@code true} if the given column in the result set is numeric.
     *
     * @param  results The result set.
     * @param  pkIndex The index of the column to inspect (typically the primary key), or 0 if none.
     * @return {@code true} If the given column in the given result set is numeric.
     * @throws SQLException If an error occurred while fetching the metadata.
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
     * Returns {@code true} if the prepared statement to be created by
     * {@link #getStatement(String)} should be able to return auto-generated keys.
     */
    @Override
    final boolean wantsAutoGeneratedKeys() {
        return getQueryType() == QueryType.INSERT;
    }

    /**
     * Creates an identifier for the current row in the given result set. This method needs to
     * be overridden by subclasses using {@link MultiColumnIdentifier}. Other subclasses don't
     * need to override this method: a {@link String} or {@link Integer} identifier will be
     * used as needed.
     * <p>
     * This method is invoked by {@link #getEntries()} only. It should not be invoked otherwise.
     *
     * @param  results The result set.
     * @param  pkIndices The indices of the column to inspect (typically the primary keys).
     * @return The {@linkplain MultiColumnIdentifier multi-column identifier}.
     * @throws SQLException If an error occurred while fetching the data.
     *
     * @since 3.10
     */
    protected Comparable<?> createIdentifier(ResultSet results, int[] pkIndices) throws SQLException {
        if (pkIndices.length == 1) {
            return null; // Special value to be handled by getEntries().
        }
        results.close();
        throw new CatalogException(errors().getString(Errors.Keys.UNSUPPORTED_OPERATION_$1, getQueryType()));
    }

    /**
     * Creates an {@link Element} object for the current {@linkplain ResultSet result set} row.
     * This method is invoked automatically by {@link #getEntry(String)} and {@link #getEntries()}.
     *
     * @param  lc The {@link #getLocalCache()} value.
     * @param  results The result set to use for fetching data. Only the current row should
     *         be used, i.e. {@link ResultSet#next} should <strong>not</strong> be invoked.
     * @param  identifier The identifier of the entry being created.
     * @return The element for the current row in the specified {@code results}.
     * @throws CatalogException if a logical error has been detected in the database content.
     * @throws SQLException if an error occurred will reading from the database.
     */
    protected abstract E createEntry(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws CatalogException, SQLException;

    /**
     * Invokes the user's {@link #createEntry(ResultSet)} method, but wraps {@link SQLException}
     * into {@link CatalogException} because the later provides more informations.
     *
     * @throws CatalogException If an error occurred during {@link #createEntry(ResultSet)}.
     * @throws SQLException If an error occurred during {@link CatalogException#setMetadata}.
     *         Note that this is not an error occurring during normal execution, but rather
     *         an error occurring while querying database metadata for building the exception.
     */
    private E createEntryCatchSQL(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws CatalogException, SQLException
    {
        CatalogException exception;
        try {
            return createEntry(lc, results, identifier);
        } catch (CatalogException cause) {
            if (cause.isMetadataInitialized()) {
                throw cause;
            }
            exception = cause;
        } catch (SQLException cause) {
            exception = new CatalogException(cause);
        }
        exception.setMetadata(this, results, getPrimaryKeyColumn(), identifier);
        exception.clearColumnName();
        throw exception;
    }

    /**
     * Returns an element for the given identifier.
     *
     * @param  identifier The name or numeric identifier of the element to fetch.
     * @return The element for the given identifier, or {@code null} if {@code identifier} was null.
     * @throws NoSuchRecordException if no record was found for the specified key.
     * @throws SQLException if an error occurred will reading from the database.
     */
    public E getEntry(final Comparable<?> identifier) throws NoSuchRecordException, SQLException {
        if (identifier == null) {
            return null;
        }
        E entry = cache.peek(identifier);
        if (entry == null) {
            final Cache.Handler<E> handler = cache.lock(identifier);
            try {
                entry = handler.peek();
                if (entry == null) {
                    final LocalCache lc = getLocalCache();
                    synchronized (lc) {
                        final LocalCache.Stmt ce = getStatement(lc, QueryType.SELECT);
                        final PreparedStatement statement = ce.statement;
                        setPrimaryKeyParameter(statement, identifier);
                        final ResultSet results = statement.executeQuery();
                        while (results.next()) {
                            final E candidate = createEntryCatchSQL(lc, results, identifier);
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
                        release(lc, ce);
                    }
                }
            } finally {
                handler.putAndUnlock(entry);
            }
        }
        return entry;
    }

    /**
     * Returns all entries available in the database.
     *
     * @return The set of entries. May be empty, but never {@code null}.
     * @throws SQLException if an error occurred will reading from the database.
     */
    public Set<E> getEntries() throws SQLException {
        final Set<E> entries = new LinkedHashSet<E>();
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce;
            try {
                ce = getStatement(lc, QueryType.LIST);
            } catch (SQLDataException e) {
                /*
                 * This happen if BoundedSingletonTable has been given an envelope filled with NaN
                 * values, for example because it has been projected from a different CRS far from
                 * the domain of validity. We handle such envelope as an out-of-bounds envelope,
                 * so there is no record that intersect the requested area.
                 */
                Logging.recoverableException(getLogger(), getClass(), "getEntries", e);
                return entries;
            }
            final int[] pkIndices = getPrimaryKeyColumns();
            final int pkIndex = getPrimaryKeyColumn(pkIndices);
            final ResultSet results = ce.statement.executeQuery();
            Boolean isNumeric = null;
            while (results.next()) {
                Comparable<?> identifier = createIdentifier(results, pkIndices);
                if (identifier == null) {
                    if (isNumeric == null) {
                        isNumeric = isNumeric(results, pkIndex);
                    }
                    if (isNumeric) {
                        identifier = results.getInt(pkIndex);
                    } else {
                        identifier = results.getString(pkIndex);
                    }
                }
                E entry = cache.peek(identifier);
                if (entry == null) {
                    final Cache.Handler<E> handler = cache.lock(identifier);
                    try {
                        entry = handler.peek();
                        if (entry == null) {
                            entry = createEntryCatchSQL(lc, results, identifier);
                        }
                    } finally {
                        handler.putAndUnlock(entry);
                    }
                }
                if (!entries.add(entry)) {
                    // The ResultSet will be closed by the constructor below.
                    throw new DuplicatedRecordException(this, results, pkIndex, identifier);
                }
            }
            results.close();
            release(lc, ce); // Push back to the pool only on success.
        }
        return entries;
    }

    /**
     * Returns the names of all entries available in the database. This method is much
     * more economical than {@link #getEntries()} when only the identifiers are wanted.
     *
     * @return The set of entry identifiers. May be empty, but never {@code null}.
     * @throws SQLException if an error occurred will reading from the database.
     */
    public Set<String> getIdentifiers() throws SQLException {
        final Set<String> identifiers = new LinkedHashSet<String>();
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.LIST_ID);
            final ResultSet results = ce.statement.executeQuery();
            final int index = getPrimaryKeyColumn();
            while (results.next()) {
                identifiers.add(results.getString(index));
            }
            results.close();
            release(lc, ce);
        }
        return identifiers;
    }

    /**
     * Checks if an entry exists for the given name. This method does not attempt to create
     * the entry and doesn't check if the entry is valid.
     *
     * @param  identifier The identifier of the entry to fetch.
     * @return {@code true} if an entry of the given identifier was found.
     * @throws SQLException if an error occurred will reading from the database.
     */
    public boolean exists(final Comparable<?> identifier) throws SQLException {
        if (identifier == null) {
            return false;
        }
        if (cache.containsKey(identifier)) {
            return true;
        }
        final boolean hasNext;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.EXISTS);
            final PreparedStatement statement = ce.statement;
            setPrimaryKeyParameter(statement, identifier);
            final ResultSet results = statement.executeQuery();
            hasNext = results.next();
            results.close();
            release(lc, ce);
        }
        return hasNext;
    }

    /**
     * Deletes the entry for the given identifier.
     *
     * @param  identifier The identifier of the entry to delete.
     * @return The number of entries deleted.
     * @throws SQLException if an error occurred will reading from or writing to the database.
     */
    public int delete(final Comparable<?> identifier) throws SQLException {
        if (identifier == null) {
            return 0;
        }
        final int count;
        boolean success = false;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            transactionBegin(lc);
            try {
                final LocalCache.Stmt ce = getStatement(lc, QueryType.DELETE);
                final PreparedStatement statement = ce.statement;
                setPrimaryKeyParameter(statement, identifier);
                count = update(statement);
                release(lc, ce);
                success = true;
            } finally {
                transactionEnd(lc, success);
            }
        }
        // Update the cache only on successfuly deletion.
        cache.remove(identifier);
        return count;
    }

    /**
     * Deletes many elements. "Many" depends on the configuration set by {@link #configure}.
     * It may be the whole table. Note that this action may be blocked if the user doesn't
     * have the required database authorizations, or if some records are still referenced in
     * foreigner tables.
     *
     * @return The number of elements deleted.
     * @throws SQLException if an error occurred will reading from or writing to the database.
     */
    public int deleteAll() throws SQLException {
        final int count;
        boolean success = false;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            transactionBegin(lc);
            try {
                final LocalCache.Stmt ce = getStatement(lc, QueryType.DELETE_ALL);
                count = update(ce.statement);
                release(lc, ce);
                success = true;
            } finally {
                transactionEnd(lc, success);
            }
        }
        // Update the cache only on successfuly deletion.
        cache.clear();
        return count;
    }

    /**
     * Executes the specified SQL {@code INSERT}, {@code UPDATE} or {@code DELETE} statement.
     *
     * @param  statement The statement to execute.
     * @return The number of elements updated.
     * @throws SQLException if an error occurred.
     */
    private int update(final PreparedStatement statement) throws SQLException {
        final Database database = getDatabase();
        database.ensureOngoingTransaction();
        return statement.executeUpdate();
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
     * @throws SQLException if an error occurred.
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

    /**
     * Searches for an identifier not already in use. If the given string is not in use, then
     * it is returned as-is. Otherwise, this method appends a decimal number to the specified
     * base and check if the resulting identifier is not in use. If it is, then the decimal
     * number is incremented until a unused identifier is found.
     * <p>
     * This method is suitable for {@link String} identifiers. Numerical identifiers shall
     * use an auto-increment field instead.
     *
     * @param  lc The value returned by {@link #getLocalCache()}.
     * @param  base The base for the identifier.
     * @return A unused identifier.
     * @throws SQLException if an error occurred while reading the database.
     *
     * @since 3.11
     */
    protected final String searchFreeIdentifier(final LocalCache lc, final String base) throws SQLException {
        if (generator == null) {
            if (pkParam.length == 0) {
                throw new UnsupportedOperationException();
            }
            generator = getDatabase().getIdentifierGenerator(lc, pkParam[0].column.name);
        }
        return generator.identifier(query.schema, query.table, base);
    }
}
