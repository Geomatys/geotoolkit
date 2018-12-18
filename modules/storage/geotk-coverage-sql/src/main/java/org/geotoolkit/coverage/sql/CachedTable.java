/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.sis.util.collection.Cache;


/**
 * Base class for tables with a {@code getEntry(...)} method returning at most one entry.
 * The entries are uniquely identified by an identifier, which may be a string or an integer.
 * {@code SingletonTable} defines the {@link #getEntry(String)} and {@link #getEntry(int)}
 * methods. Subclasses shall provide implementation for the following method:
 *
 * <ul>
 *   <li>{@link #createEntry(ResultSet)}: Creates an entry for the current row.</li>
 * </ul>
 *
 * The entries created by this class are cached for faster access the next time a
 * {@code getEntry(…)} method is invoked again.
 *
 * @param  <K>  the identifier type, either {@link String} or {@link Integer}.
 * @param  <E>  the kind of entries to be created by this table.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
abstract class CachedTable<K,E> extends Table {
    /**
     * The table for which caching is performed.
     */
    enum Target {
        PRODUCT, SERIES, FORMAT, AXES, GRID_GEOMETRY
    }

    /**
     * The entries created up to date. The keys shall be {@link Integer} or {@link String} instances only.
     */
    private final Cache<K,E> cache;

    /**
     * Creates a new table.
     */
    @SuppressWarnings("unchecked")
    CachedTable(final Target table, final Transaction transaction) {
        super(transaction);
        cache = (Cache) transaction.database.cache(table);
    }

    /**
     * Returns the SQL statement for the {@code SELECT <column> WHERE <id> = ?} operation.
     * The statement shall contain exactly one parameter.
     */
    abstract String select();

    /**
     * Returns an element for the given identifier.
     *
     * @param  identifier the name or numeric identifier of the element to fetch.
     * @return the element for the given identifier, or {@code null} if {@code identifier} was null.
     * @throws SQLException if an error occurred will reading from the database.
     * @throws NoSuchRecordException if no record was found for the specified key.
     */
    final E getEntry(final K identifier) throws SQLException, CatalogException {
        if (identifier == null) {
            return null;
        }
        E entry = cache.peek(identifier);
        if (entry == null) {
            final Cache.Handler<E> handler = cache.lock(identifier);
            try {
                entry = handler.peek();
                if (entry == null) {
                    E first = null;
                    final PreparedStatement statement = prepareStatement(select());
                    if (identifier instanceof Integer) {
                        statement.setInt(1, (Integer) identifier);
                    } else {
                        statement.setString(1, identifier.toString());
                    }
                    try (ResultSet results = statement.executeQuery()) {
                        while (results.next()) {
                            final E candidate = createEntry(results, identifier);
                            if (first == null) {
                                first = candidate;
                            } else if (!first.equals(candidate)) {
                                throw new DuplicatedRecordException(results, 1, identifier);
                            }
                        }
                        if (first == null) {
                            // Throw the exception here for having a valid ResultSet (TODO: revisit this policy).
                            throw new NoSuchRecordException(results, 1, identifier);
                        }
                    }
                    entry = first;      // Cache only on success.
                }
            } finally {
                handler.putAndUnlock(entry);
            }
        }
        return entry;
    }

    /**
     * Creates an {@link Element} object for the current {@linkplain ResultSet result set} row.
     * This method is invoked automatically by {@link #getEntry(String)} and {@link #getEntry(int)}.
     *
     * @param  results the result set to use for fetching data. Only the current row should
     *         be used, i.e. {@link ResultSet#next} should <strong>not</strong> be invoked.
     * @param  identifier the identifier of the entry being created.
     * @return the element for the current row in the specified {@code results}.
     * @throws SQLException if an error occurred will reading from the database.
     * @throws CatalogException if a logical error has been detected in the database content.
     */
    abstract E createEntry(ResultSet results, K identifier) throws SQLException, CatalogException;

    /**
     * Removes the cached entry for the given identifier.
     */
    final void removeCached(final K identifier) {
        cache.remove(identifier);
    }
}
