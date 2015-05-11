/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;


/**
 * Checks the existence of identifiers (usually primary keys) in a set of tables.
 * This class implements a very naive algorithm and is used only when some reasonably
 * meaningful ID are wanted. If "meaningful" ID is not a requirement, then it is much
 * more efficient to rely on the ID numbers generated automatically by the database.
 * <p>
 * This class checks if a given identifier exists in the database. If it exists, then
 * it searches for an unused {@code "proposal-n"} identifier, where {@code "proposal"}
 * is the given identifier and {@code "n"} is a number. The algorithm in this class
 * takes advantage of the fact that alphabetical order is not the same than numerical
 * order for scanning a slightly smaller amount of records (however the advantage is
 * significant only in some special cases - generally speaking this class is not for
 * table having thousands of identifier beginning with the given prefix). However the
 * selected numbers are not guaranteed to be in increasing order if there is "holes"
 * in the sequence of numbers (i.e. if some old records have been deleted). Generating
 * strictly increasing sequence is not a goal of this class, since it would be too costly.
 *
 * {@section Assumptions}
 * <ul>
 *   <li>{@code SELECT DISTINCT ID FROM "Table" WHERE ID LIKE 'proposal%' ORDER BY ID;} is
 *       assumed efficient. For example in the case of a PostgreSQL database, it requires
 *       PostgreSQL 8.0 or above with a {@code btree} index and C locale.</li>
 *   <li>The ordering of the {@code '-'} and {@code '0'} to {@code '9'} characters compared
 *       to other characters is the same than ASCII. This condition needs to hold only for
 *       those particular characters (the ordering between letters doesn't matter).</li>
 * </ul>
 *
 * @param <K> The type of keys in the pool of prepared statements.
 * @param <V> The type of values in the pool of prepared statements.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.03
 * @module
 */
public abstract class IdentifierGenerator<K, V extends StatementEntry> {
    /**
     * The most straightforward implementation of {@link IdentifierGenerator}.
     * The keys are the table names, which imply that those name must not be
     * used for any other purpose in the pool.
     * <p>
     * <strong>Don't use tables of the same name in different schema</strong>, since the
     * default implementation uses only the table name as keys in the statement pool map.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.11
     *
     * @since 3.03
     * @module
     */
    public static final class Simple extends IdentifierGenerator<String,StatementEntry> {
        /**
         * Creates a new generator using the given pool of prepared statements.
         * See {@link IdentifierGenerator} constructor for more details on the arguments.
         *
         * @param pool   The pool of prepared statements.
         * @param column The name of the identifier (primary key) column.
         * @throws SQLException If the connection to the database can not be established.
         */
        public Simple(StatementPool<? super String, StatementEntry> pool, String column) throws SQLException {
            super(pool, column);
        }

        /**
         * Creates a new generator using the given pool of prepared statements.
         * See {@link IdentifierGenerator} constructor for more details on the arguments.
         *
         * @param pool   The pool of prepared statements.
         * @param column The name of the identifier (primary key) column.
         * @param buffer A helper object for building SQL statements, determined from database metadata.
         */
        public Simple(StatementPool<? super String, StatementEntry> pool, String column, SQLBuilder buffer) {
            super(pool, column, buffer);
        }

        /**
         * Returns the table name unchanged, which is used directly as a key.
         */
        @Override
        protected String key(final String schema, final String table) {
            return table;
        }

        /**
         * Wraps the given statement in a plain {@code StatementEntry} instance.
         */
        @Override
        protected StatementEntry value(final String key, final PreparedStatement query) {
            return new StatementEntry(query);
        }
    }

    /**
     * The character to be used as a separator between the prefix and the sequence number.
     */
    static final char SEPARATOR = '-';

    /**
     * The pool of prepared statement.
     */
    final StatementPool<? super K, V> pool;

    /**
     * The name of the identifier (primary key) column. If the name should be quoted,
     * then the quotes must be explicitly specified; this class will <strong>not</strong>
     * add the quotes by itself, because some applications really want unquoted identifiers.
     */
    private final String column;

    /**
     * A helper object for building SQL statements, determined from database metadata.
     */
    private final SQLBuilder buffer;

    /**
     * Creates a new generator using the given pool of prepared statements.
     *
     * @param  pool The pool of prepared statements.
     * @param  column The name of the identifier (primary key) column. If the name should be quoted,
     *         then the {@link #quoteColumn()} method shall be overridden. This class does not
     *         add the quotes by itself, because some applications really want unquoted identifiers.
     * @throws SQLException If the connection to the database can not be established.
     */
    public IdentifierGenerator(final StatementPool<? super K, V> pool, final String column) throws SQLException {
        this(pool, column, new SQLBuilder(pool.connection().getMetaData()));
    }

    /**
     * Creates a new generator using the given pool of prepared statements.
     *
     * @param pool The pool of prepared statements.
     * @param column The name of the identifier (primary key) column. If the name should be quoted,
     *        then the {@link #quoteColumn()} method shall be overridden. This class does not
     *        add the quotes by itself, because some applications really want unquoted identifiers.
     * @param buffer A helper object for building SQL statements, determined from database metadata.
     *        This is an opportunist argument to be given only if a buffer already exists.
     */
    public IdentifierGenerator(final StatementPool<? super K, V> pool, final String column, final SQLBuilder buffer) {
        this.pool   = pool;
        this.column = column;
        this.buffer = buffer;
    }

    /**
     * Creates a new generator using the same pool and the same {@link SQLBuilder} than the given
     * generator, but for a different column. Because the two generators share the same resources,
     * the shall be used only in the same thread.
     *
     * @param other The other generator from which to share the pool and the SQL builder.
     * @param column The name of the identifier (primary key) column. If the name should be quoted,
     *        then the {@link #quoteColumn()} method shall be overridden. This class does not
     *        add the quotes by itself, because some applications really want unquoted identifiers.
     */
    protected IdentifierGenerator(final IdentifierGenerator<? super K, V> other, final String column) {
        this(other.pool, column, other.buffer);
    }

    /**
     * Returns the key to use for fetching an entry for the given table in the {@code StatementPool}.
     *
     * @param  schema The table schema, or {@code null} if none.
     * @param  table The table for which to get a {@code StatementEntry}.
     * @return The key to use.
     * @throws SQLException If a connection with the database was required and failed.
     */
    protected abstract K key(final String schema, final String table) throws SQLException;

    /**
     * Creates a new {@code StatementEntry} for the given {@code PreparedStatement}.
     *
     * @param  key   The key for which the statement has been prepared.
     * @param  query The prepared statement to be given to the entry.
     * @return The {@code StatementEntry} for the given statement.
     * @throws SQLException If an error occurred while creating the entry.
     */
    protected abstract V value(final K key, final PreparedStatement query) throws SQLException;

    /**
     * Returns {@code true} if the column name should be quoted.
     * The default value is {@code false}.
     *
     * @return {@code true} if the column name should be quoted.
     *
     * @since 3.11
     */
    protected boolean quoteColumn() {
        return false;
    }

    /**
     * Searches for an identifier in the given table. If the given proposal is already in use,
     * then this method will search for an identifier of the form {@code "proposal-n"} not in
     * use, where {@code "n"} is a number.
     *
     * @param  schema The schema, or {@code null} if none.
     * @param  table The table where to search for an identifier. This table
     *         name should not be quoted; quotes will be added if needed.
     * @param  proposal The proposed identifier. It will be returned if not currently used.
     * @return An identifier which doesn't exist at the time this method has been invoked.
     * @throws SQLException If an error occurred while searching for an identifier.
     */
    public final String identifier(final String schema, final String table, String proposal) throws SQLException {
        synchronized (pool) {
            final K key = key(schema, table);
            V entry = pool.remove(key);
            if (entry == null) {
                final boolean quote = quoteColumn();
                entry = value(key, pool.connection().prepareStatement(buffer.clear().append("SELECT DISTINCT ")
                        .appendIdentifier(column, quote).append(" FROM ").appendIdentifier(schema, table).append(" WHERE ")
                        .appendIdentifier(column, quote).append(" LIKE ? ORDER BY ").appendIdentifier(column, quote).toString()));
            }
            entry.statement.setString(1, buffer.clear().appendEscaped(proposal).append('%').toString());
            try (ResultSet rs = entry.statement.executeQuery()) {
                if (rs.next()) {
                    String current = rs.getString(1);
                    if (current.equals(proposal)) {
                        /*
                         * The proposed identifier is already used. If there is no other identifiers,
                         * just append "-1" are we are done. Otherwise we need to search for a "hole"
                         * in the sequence of number suffixes.
                         */
                        final int parseAt = proposal.length() + 1;
                        final int[] result = new int[2]; // Initialized to 0.
                        int expected = 0;
searchValidRecord:      while (rs.next()) {
                            current = rs.getString(1);
                            assert current.startsWith(proposal) : current;
                            while (current.length() > parseAt) {
                                char c = current.charAt(parseAt-1);
                                if (c < SEPARATOR) continue searchValidRecord;
                                if (c > SEPARATOR) break searchValidRecord;
                                c = current.charAt(parseAt);
                                /*
                                 * Intentionally exclude any record having leading zeros,
                                 * since it would confuse our algorithm.
                                 */
                                if (c < '1') continue searchValidRecord;
                                if (c > '9') break searchValidRecord;
                                final String prefix = current.substring(0, parseAt);
                                current = search(rs, current, prefix, ++expected, parseAt, result);
                                if (current == null) {
                                    break searchValidRecord;
                                }
                            }
                        }
                        int n = result[1]; // The hole found during iteration.
                        if (n == 0) {
                            n = result[0] + 1; // If no hole, use the maximal number + 1.
                        }
                        proposal = proposal + SEPARATOR + n;
                    }
                }
            }
            if (pool.put(key, entry) != null) {
                throw new AssertionError();
            }
        }
        return proposal;
    }

    /**
     * Searches for an available identifier, assuming that the elements in the given
     * {@code ResultSet} are sorted in alphabetical (not numerical) order.
     *
     * @param rs
     *      The result set from which to get next records. Its cursor position is the
     *      <strong>second</strong> record to inspect (i.e. a record has already been
     *      extracted before the call to this method).
     * @param current
     *      The ID of the record which has been extracted before the call to this method.
     *      It must start with {@code prefix} while not equals to {@code prefix}.
     * @param prefix
     *      The prefix that an ID must have in order to be accepted.
     * @param expected
     *      The next expected number. If this number is not found, then it will be assumed available.
     * @param parseAt
     *      Index of the first character to parse in the ID in order to get its sequential number.
     * @param result
     *      An array of length 2. The first element will be the greatest sequential number found
     *      during the search, and the second element (if different than 0) will be the proposed
     *      number.
     * @return
     *      The ID that stopped the search (which is going to be the first element of the next
     *      iteration), or {@code null} if we should stop the search.
     * @throws SQLException
     *      If an error occurred while querying the database.
     */
    private static String search(final ResultSet rs, String current, final String prefix,
            int expected, final int parseAt, final int[] result) throws SQLException
    {
        /*
         * The first condition below should have been verified by the caller. If that
         * condition holds, then the second condition is a consequence of the DISTINCT
         * keyword in the SELECT statement, which should ensure !current.equals(prefix).
         */
        assert current.startsWith(prefix);
        assert current.length() > prefix.length() : current;
        do {
            final int n;
            try {
                n = Integer.parseInt(current.substring(parseAt));
            } catch (NumberFormatException e) {
                /*
                 * We expect only records with an identifier compliant with our syntax. If we
                 * encounter a non-compliant identifier, just ignore it. There is no risk of
                 * key collision since we are not going to generate a non-compliant ID.
                 */
                if (rs.next()) {
                    current = rs.getString(1);
                    continue;
                }
                return null;
            }
            /*
             * If we found a higher number than the expected one, then we found a "hole" in the
             * sequence of numbers. Remember the value of the hole and returns null for stopping
             * the search.
             */
            if (n > expected) {
                result[1] = expected;
                return null;
            }
            if (n != expected) {
                // Following should never happen (I think).
                throw new SQLNonTransientException(current);
            }
            expected++;
            /*
             * Remember the highest value found so far. This will be used only
             * if we failed to find any "hole" in the sequence of numbers.
             */
            if (n > result[0]) {
                result[0] = n;
            }
            if (!rs.next()) {
                return null;
            }
            /*
             * Gets the next record, skipping every ones starting with the current one.
             * For example if the current record is "proposal-1", then the following block
             * will skip "proposal-10", "proposal-11", etc. until it reaches "proposal-2".
             */
            final String next = current.substring(0, prefix.length() + 1);
            current = rs.getString(1);
            if (current.startsWith(next)) {
                current = search(rs, current, next, n*10, parseAt, result);
                if (current == null) {
                    return null;
                }
            }
        } while (current.startsWith(prefix));
        return current;
    }
}
