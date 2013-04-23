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

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.internal.sql.Ordering;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Debug;


/**
 * A SQL query build from {@linkplain Column columns} and {@linkplain Parameter parameters}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.15
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public class Query {
    /**
     * The SQL statement used for table join, spaces included. Some database
     * implementations require {@code "INNER JOIN"} instead of {@code "JOIN"}.
     */
    private static final String JOIN = " JOIN ";

    /**
     * A flag for column without {@linkplain #defaultValue default value}.
     * This is used instead of {@code null} because null may be a valid default value.
     */
    private static final Object MANDATORY = new Object();

    /**
     * An empty array of columns.
     */
    private static final Column[] EMPTY_COLUMNS = new Column[0];

    /**
     * An empty array of parameters.
     */
    private static final Parameter[] EMPTY_PARAMETERS = new Parameter[0];

    /**
     * The database for which this query is created, or {@code null} if none.
     */
    final Database database;

    /**
     * The name of the schema containing the table. This is the same value than
     * {@link Database#schema}, unless explicitly specified at construction time.
     *
     * @since 3.11
     */
    protected final String schema;

    /**
     * The name of the main table.
     */
    protected final String table;

    /**
     * The columns in this query. New columns are added by invoking the
     * {@link #addMandatoryColumn(String, QueryType[])} method or some
     * similar method.
     */
    private Column[] columns = EMPTY_COLUMNS;

    /**
     * The parameters in this query. New parameters are added by invoking
     * the {@link #addParameter(Column, QueryType[])} method.
     */
    private Parameter[] parameters = EMPTY_PARAMETERS;

    /**
     * The ordering for each column. We stores this information in the query rather than
     * in the column because the column order is significant.
     * <p>
     * Values shall be {@code "ASC"} or {@code "DESC"}.
     */
    final Map<Column,Ordering> ordering = new LinkedHashMap<Column,Ordering>();

    /**
     * SQL queries cached up to date.
     */
    private final Map<QueryType,String> cachedSQL = new EnumMap<QueryType,String>(QueryType.class);

    /**
     * Creates an initially empty query for a table in the default schema.
     *
     * @param database The database for which this query is created, or {@code null}.
     * @param table    The main table name.
     */
    protected Query(final Database database, final String table) {
        this(database, table, null);
    }

    /**
     * Creates an initially empty query for a table in the given schema.
     *
     * @param database The database for which this query is created, or {@code null}.
     * @param schema   The schema containing the table, or {@code null} for the default.
     * @param table    The main table name.
     */
    protected Query(final Database database, final String table, final String schema) {
        this.database = database;
        this.table    = table;
        this.schema   = (schema == null && database != null) ? database.schema : schema;
    }

    /**
     * Returns {@code true} if the SQL statements should include child tables.
     * The default value is {@code true}. If this method returns {@code false},
     * then the SQL statement will be of the form {@code SELECT ... FROM ONLY ...}
     * intead than the usual {@code SELECT ... FROM ...}.
     *
     * @return {@code true} if the query should include child tables (the default).
     */
    public boolean isIncludingChildTables() {
        return true;
    }

    /**
     * Adds the given element, and returns the previous list of elements. The returned list will
     * <strong>not</strong> contains the newly added element, since this is the previous list.
     * <p>
     * This method is used by the {@link ColumnOrParameter} constructor only.
     */
    final ColumnOrParameter[] add(final ColumnOrParameter element) {
        cachedSQL.clear();
        final ColumnOrParameter[] old;
        if (element instanceof Column) {
            old = columns;
            columns = Arrays.copyOf(columns, old.length + 1);
            columns[old.length] = (Column) element;
        } else if (element instanceof Parameter) {
            old = parameters;
            parameters = Arrays.copyOf(parameters, old.length + 1);
            parameters[old.length] = (Parameter) element;
        } else {
            throw new AssertionError(element); // Should never happen.
        }
        return old;
    }

    /**
     * Creates a new mandatory column with the specified name.
     *
     * @param  name  The column name.
     * @param  types Types of the queries where the column shall appears.
     * @return The newly added column.
     */
    protected final Column addMandatoryColumn(String name, QueryType... types) {
        return new Column(this, table, name, MANDATORY, types);
        // The addition into this query is performed by the Column constructor.
    }

    /**
     * Creates a new optional column with the specified name and default value.
     *
     * @param  name  The column name.
     * @param  defaultValue The default value if the column is not present in the database.
     *               It can be a {@link Number}, a {@link String} or {@code null}.
     * @param  types Types of the queries where the column shall appears.
     * @return The newly added column.
     */
    protected final Column addOptionalColumn(String name, Comparable<?> defaultValue, QueryType... types) {
        return new Column(this, table, name, defaultValue, types);
        // The addition into this query is performed by the Column constructor.
    }

    /**
     * Creates a new mandatory column from the specified table with the specified name.
     *
     * @param name  The column name.
     * @param table The name of the table that contains the column.
     * @param types Types of the queries where the column shall appears.
     * @return The newly added column.
     */
    protected final Column addForeignerColumn(String name, String table, QueryType... types) {
        return new Column(this, table, name, MANDATORY, types);
        // The addition into this query is performed by the Column constructor.
    }

    /**
     * Creates a new optional column from the specified table with the specified name and default
     * value.
     *
     * @param name  The column name.
     * @param table The name of the table that contains the column.
     * @param defaultValue The default value if the column is not present in the database.
     *              It can be a {@link Number}, a {@link String} or {@code null}.
     * @param types Types of the queries where the column shall appears.
     * @return The newly added column.
     */
    protected final Column addForeignerColumn(String name, String table, Comparable<?> defaultValue, QueryType... types) {
        return new Column(this, table, name, defaultValue, types);
        // The addition into this query is performed by the Column constructor.
    }

    /**
     * Adds a new parameter for the specified query.
     *
     * @param column The column on which the parameter is applied.
     * @param types Types of the queries where the parameter shall appears.
     * @return The newly added parameter.
     */
    protected final Parameter addParameter(final Column column, final QueryType... types) {
        return new Parameter(this, column, types);
    }

    /**
     * Returns the columns for the specified type. For a statement created from the
     * <code>{@linkplain #select(QueryType) select}(type)</code> query, the value returned by
     * <code>{@linkplain ResultSet#getString(int) ResultSet.getString}(i)</code> corresponds to
     * the {@linkplain Column column} at index <var>i</var>-1 in the list.
     *
     * @param  type The query type.
     * @return An immutable list of columns.
     */
    public final List<Column> getColumns(final QueryType type) {
        return new ColumnOrParameterList<Column>(type, columns);
    }

    /**
     * Returns the parameters for the specified type. For a statement created from the
     * <code>{@linkplain #select(QueryType) select}(type)</code> query, the parameter set by
     * <code>{@linkplain PreparedStatement#setString(int,String) PreparedStatement.setString}(i, ...)</code>
     * corresponds to the {@linkplain Parameter parameter} at index <var>i</var>-1 in the list.
     *
     * @param  type The query type.
     * @return An immutable list of parameters.
     */
    public final List<Parameter> getParameters(final QueryType type) {
        return new ColumnOrParameterList<Parameter>(type, parameters);
    }

    /**
     * Returns the column names for the specified table.
     *
     * @param  metadata The database metadata.
     * @param  table The table name.
     * @return The columns in the specified table.
     * @throws SQLException if an error occurred while reading the database.
     */
    private Set<String> getColumnNames(final DatabaseMetaData metadata, final String table)
            throws SQLException
    {
        final Set<String> columns = new HashSet<String>();
        final ResultSet results = metadata.getColumns(database.catalog, schema, table, null);
        while (results.next()) {
            columns.add(results.getString("COLUMN_NAME"));
        }
        results.close();
        return columns;
    }

    /**
     * Creates the SQL statement for selecting all records.
     * No SQL parameters are expected for this statement.
     *
     * @param  buffer     The buffer in which to write the SQL statement.
     * @param  type       The query type.
     * @param  metadata   The database metadata, used for inspection of primary and foreigner keys.
     * @param  joinParameters {@code true} if we should take parameters in account for determining
     *         the {@code JOIN ... ON} clauses.
     * @throws SQLException if an error occurred while reading the database.
     */
    private void selectAll(final StringBuilder buffer, final QueryType type,
                           final DatabaseMetaData metadata, final boolean joinParameters)
            throws SQLException
    {
        /*
         * Lists all columns after the "SELECT" clause.
         * Keep trace of all involved tables in the process.
         */
        final String quote = metadata.getIdentifierQuoteString().trim();
        Map<String,CrossReference> tables = new LinkedHashMap<String,CrossReference>();
        Map<String,Set<String>> columnNames = null;
        String separator = "SELECT ";
        for (final Column column : columns) {
            if (column.indexOf(type) == 0) {
                // Column not to be included for the requested query type.
                continue;
            }
            final String table = column.table; // Because often requested.
            /*
             * Checks if the column exists in the table. This check is performed only if the column
             * is optional. For mandatory columns, we will unconditionally insert the column in the
             * SELECT clause and lets the SQL driver throws the appropriate exception later.
             */
            final boolean columnExists;
            if (column.defaultValue == MANDATORY) {
                columnExists = true;
            } else {
                if (columnNames == null) {
                    columnNames = new HashMap<String,Set<String>>();
                }
                Set<String> columns = columnNames.get(table);
                if (columns == null) {
                    columns = getColumnNames(metadata, table);
                    columnNames.put(table, columns);
                }
                columnExists = columns.contains(column.name);
                if (!columnExists) {
                    Logging.log(Query.class, "select", // "select" is the public method invoking this one.
                            Loggings.format(Level.CONFIG, Loggings.Keys.TABLE_COLUMN_NOT_FOUND_3,
                            column.name, table, column.defaultValue));
                }
            }
            /*
             * Appends the column name in the SELECT clause, or the default value if the column
             * doesn't exist in the current database.
             */
            buffer.append(separator);
            if (columnExists) {
                final String function = column.getFunction(type);
                appendFunctionPrefix(buffer, function);
                column.appendName(buffer, quote);
                appendFunctionSuffix(buffer, function);
            } else {
                // Don't put quote for number, boolean and null values.
                final boolean needQuotes = (column.defaultValue instanceof CharSequence);
                String defaultValue = String.valueOf(column.defaultValue); // May be "null"
                if (needQuotes) {
                    buffer.append('\'');
                } else {
                    defaultValue = defaultValue.toUpperCase(Locale.ENGLISH);
                }
                buffer.append(defaultValue);
                if (needQuotes) {
                    buffer.append('\'');
                }
            }
            /*
             * Declares the alias if needed. This part is mandatory if the
             * column doesn't exist and has been replaced by a default value.
             */
            if (!columnExists) {
                column.appendName(buffer.append(" AS "), quote);
            }
            separator = ", ";
            tables.put(table, null); // ForeignerKeys will be determined later.
        }
        if (joinParameters) {
            for (final Parameter parameter : parameters) {
                if (parameter.indexOf(type) != 0) {
                    tables.put(parameter.column.table, null);
                }
            }
        }
        /*
         * Optionally update the table order. First, we search for foreigner keys. We will use
         * this information later both for altering the table order and in order to construct
         * the "JOIN ... ON" clauses.
         */
        final String catalog = database.catalog;
        final String schema  = this.schema;
        if (tables.size() >= 2) {
            for (final Map.Entry<String,CrossReference> entry : tables.entrySet()) {
                final String table = entry.getKey();
                final ResultSet pks = metadata.getExportedKeys(catalog, schema, table);
                while (pks.next()) {
                    assert catalog == null || catalog.equals(pks.getString("PKTABLE_CAT"  )) : catalog;
                    assert schema  == null || schema .equals(pks.getString("PKTABLE_SCHEM")) : schema;
                    assert table   == null || table  .equals(pks.getString("PKTABLE_NAME" )) : table;
                    final String pkColumn = pks.getString("PKCOLUMN_NAME");
                    // Consider only the tables from the same catalog.
                    if (catalog != null && !catalog.equals(pks.getString("FKTABLE_CAT"))) {
                        continue;
                    }
                    // Consider only the tables from the same schema.
                    if (schema != null && !schema.equals(pks.getString("FKTABLE_SCHEM"))) {
                        continue;
                    }
                    // Consider only the tables that are present in the SELECT statement.
                    final String fkTable = pks.getString("FKTABLE_NAME");
                    if (!tables.containsKey(fkTable) || table.equals(fkTable)) {
                        continue;
                    }
                    final String fkColumn = pks.getString("FKCOLUMN_NAME");
                    if (pks.getShort("KEY_SEQ") != 1) {
                        // Current implementation do not support multi-columns foreigner key.
                        pks.close();
                        throw new SQLException("Clé étrangère sur plusieurs colonnes dans la table \"" + table + "\".");
                    }
                    final Column pk = new Column(  table, pkColumn);
                    final Column fk = new Column(fkTable, fkColumn);
                    final CrossReference ref = new CrossReference(fk, pk);
                    final CrossReference old = entry.setValue(ref);
                    if (old != null && !ref.equals(old)) {
                        // Current implementation supports only one foreigner key per table.
                        pks.close();
                        throw new SQLException("Multiple clés étrangères pour la table \"" + table + "\".");
                    }
                }
                pks.close();
            }
            /*
             * Copies the table in a new map with a potentially different order.
             * We try to move last the tables that use foreigner keys.
             */
            final Map<String,CrossReference> ordered = new LinkedHashMap<String,CrossReference>();
scan:       while (!tables.isEmpty()) {
                for (final Iterator<Map.Entry<String,CrossReference>> it=tables.entrySet().iterator(); it.hasNext();) {
                    final Map.Entry<String,CrossReference> entry = it.next();
                    final String table = entry.getKey();
                    final CrossReference ref = entry.getValue();
                    if (ref == null || ordered.containsKey(ref.foreignerKey.table)) {
                        // This table is unreferenced, or is referenced by a table already listed
                        // in the "FROM" or "JOIN" clause. Copies it to the ordered table list.
                        ordered.put(table, ref);
                        it.remove();
                        continue scan;
                    }
                }
                // None of the remaining tables can be moved.
                // Stop and copy unconditionally the remaining.
                break;
            }
            ordered.putAll(tables);
            tables = ordered;
        }
        /*
         * Writes the "FROM" and "JOIN" clauses.
         */
        separator = isIncludingChildTables() ? " FROM " : " FROM ONLY ";
        for (final Map.Entry<String,CrossReference> entry : tables.entrySet()) {
            final String table = entry.getKey();
            buffer.append(separator);
            if (schema != null) {
                buffer.append(quote).append(schema).append(quote).append('.');
                buffer.append(quote).append(table).append(quote);
            } else {
                buffer.append(quote).append(table).append(quote);
            }
            if (separator != JOIN) { // NOSONAR: identity comparison is ok here.
                separator = JOIN;
                assert entry.getValue() == null : entry;
                continue;
            }
            /*
             * At this point, we know that our "SELECT" clause uses more than one table.
             * Infer the "JOIN ... ON ..." statements from the primary and foreigner keys.
             */
            final CrossReference ref = entry.getValue();
            if (ref == null) {
                throw new SQLException(Errors.getResources(database.getLocale())
                        .getString(Errors.Keys.NO_FOREIGNER_KEY_1, table));
            }
            assert table.equals(ref.primaryKey.table) : table;
            buffer.append(" ON ");
            ref.foreignerKey.appendFullName(buffer, quote);
            buffer.append('=');
            ref.primaryKey.appendFullName(buffer, quote);
        }
    }

    /**
     * Appends SQL parameter to the given SQL statement.
     *
     * @param  buffer The buffer in which to write the SQL statement.
     * @param  type The query type.
     * @param  metadata The database metadata.
     * @throws SQLException if an error occurred while reading the database.
     */
    private void appendParameters(final StringBuilder buffer, final QueryType type,
                                  final DatabaseMetaData metadata) throws SQLException
    {
        final String quote = metadata.getIdentifierQuoteString().trim();
        String separator = " WHERE ";
        for (final Parameter p : parameters) {
            if (p.indexOf(type) != 0) {
                buffer.append(separator).append('(');
                p.appendCondition(buffer, quote, type);
                buffer.append(')');
                separator = " AND ";
            }
        }
    }

    /**
     * Appends the {@code "ORDER BY"} clause to the given SQL statement.
     *
     * @param  buffer The buffer in which to write the SQL statement.
     * @param  type The query type.
     * @param  metadata The database metadata.
     * @throws SQLException if an error occurred while reading the database.
     */
    private void appendOrdering(final StringBuilder buffer, final QueryType type,
                                final DatabaseMetaData metadata) throws SQLException
    {
        final String quote = metadata.getIdentifierQuoteString().trim();
        String separator = " ORDER BY ";
        for (final Column column : ordering.keySet()) {
            final Ordering ordering = column.getOrdering(type);
            if (ordering != null) {
                column.appendName(buffer.append(separator), quote);
                if (ordering != Ordering.ASC) {
                    buffer.append(' ').append(ordering.name());
                }
                separator = ", ";
            }
        }
    }

    /**
     * Returns the database metadata. This method can be invoked in a block synchronized on
     * {@link Database#getLocalCache()}. This synchronization must be performed by
     * the {@code Query} user; we can not perform it inside the {@code Query} class.
     */
    private static DatabaseMetaData getMetaData(final LocalCache cache) throws SQLException {
        assert Thread.holdsLock(cache); // Necessary for blocking the cleaner thread.
        return cache.connection().getMetaData();
    }

    /**
     * Creates the SQL statement for the query of the given type with no {@code WHERE} clause.
     * This is mostly used for debugging purpose.
     *
     * @param  lc The value returned by {@link Table#getLocalCache()}.
     * @param  type The query type.
     * @return The SQL statement.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Debug
    final String selectAll(final LocalCache lc, final QueryType type) throws SQLException {
        final DatabaseMetaData metadata = getMetaData(lc);
        final StringBuilder buffer = new StringBuilder();
        selectAll(buffer, type, metadata, false);
        appendOrdering(buffer, type, metadata);
        return buffer.toString();
    }

    /**
     * Creates the SQL statement for the query of the given type.
     *
     * @param  lc The value returned by {@link Table#getLocalCache()}.
     * @param  type The query type.
     * @return The SQL statement.
     * @throws SQLException if an error occurred while reading the database.
     */
    public String select(final LocalCache lc, final QueryType type) throws SQLException {
        String sql;
        synchronized (cachedSQL) {
            sql = cachedSQL.get(type);
            if (sql == null) {
                final DatabaseMetaData metadata = getMetaData(lc);
                final StringBuilder buffer = new StringBuilder();
                selectAll       (buffer, type, metadata, true);
                appendParameters(buffer, type, metadata);
                appendOrdering  (buffer, type, metadata);
                sql = buffer.toString();
                cachedSQL.put(type, sql);
            }
        }
        return sql;
    }

    /**
     * Creates the SQL statement for inserting elements in the table.
     * This method should be invoked only for queries of type {@link QueryType#INSERT}.
     *
     * @param  lc The value returned by {@link Table#getLocalCache()}.
     * @param  type The query type (should be {@link QueryType#INSERT}).
     * @return The SQL statement, or {@code null} if there is no column in the query.
     * @throws SQLException if an error occurred while reading the database.
     */
    public String insert(final LocalCache lc, final QueryType type) throws SQLException {
        String sql;
        synchronized (cachedSQL) {
            sql = cachedSQL.get(type);
            if (sql == null) {
                final DatabaseMetaData metadata = getMetaData(lc);
                final String quote = metadata.getIdentifierQuoteString().trim();
                final Set<String> columnNames = getColumnNames(metadata, table);
                final StringBuilder buffer = new StringBuilder("INSERT INTO ");
                appendTable(buffer, quote);
                String separator = " (";
                int count = 0;
                final String[] functions = new String[columns.length];
                for (final Column column : columns) {
                    if (!table.equals(column.table) || !columnNames.contains(column.name)) {
                        // Column not to be included for an insert statement.
                        continue;
                    }
                    final int index = column.indexOf(type);
                    if (index == 0) {
                        /*
                         * We require the column to be explicitly declared as to be included in an INSERT
                         * statement. This is in order to reduce the risk of unintentional write into the
                         * database, and also because some columns are expected to be left to their default
                         * value (sometime computed by trigger, e.g. GridGeometries.horizontalExtent).
                         */
                        continue;
                    }
                    functions[count] = column.getFunction(type);
                    if (++count != index) {
                        // Safety check.
                        throw new IllegalStateException(String.valueOf(column));
                    }
                    column.appendName(buffer.append(separator), quote);
                    separator = ", ";
                }
                if (count == 0) {
                    return null;
                }
                buffer.append(") VALUES");
                separator = " (";
                for (int i=0; i<count; i++) {
                    final String function = functions[i];
                    appendFunctionPrefix(buffer, function);
                    buffer.append(separator).append('?');
                    appendFunctionSuffix(buffer, function);
                    separator = ", ";
                }
                sql = buffer.append(')').toString();
                cachedSQL.put(type, sql);
            }
        }
        return sql;
    }

    /**
     * Creates the SQL statement for deleting elements from the table.
     * This method should be invoked only for queries of type {@link QueryType#DELETE}.
     *
     * @param  lc The value returned by {@link Table#getLocalCache()}.
     * @param  type The query type (should be {@link QueryType#DELETE}).
     * @return The SQL statement, or {@code null} if none.
     * @throws SQLException if an error occurred while reading the database.
     */
    public String delete(final LocalCache lc, final QueryType type) throws SQLException {
        String sql;
        synchronized (cachedSQL) {
            sql = cachedSQL.get(type);
            if (sql == null) {
                final DatabaseMetaData metadata = getMetaData(lc);
                final String quote = metadata.getIdentifierQuoteString().trim();
                final StringBuilder buffer = new StringBuilder("DELETE FROM ");
                if (!isIncludingChildTables()) {
                    buffer.append("ONLY ");
                }
                appendTable(buffer, quote);
                appendParameters(buffer, type, metadata);
                sql = buffer.toString();
                cachedSQL.put(type, sql);
            }
        }
        return sql;
    }

    /**
     * Creates the SQL statement for counting elements in a table. The query type is used
     * only for determining the parameters - it is not used for the column values.
     *
     * @param  lc The value returned by {@link Table#getLocalCache()}.
     * @param  type The query type (typically {@link QueryType#COUNT}).
     * @param  column The column to be counted.
     * @return The SQL statement, or {@code null} if none.
     * @throws SQLException if an error occurred while reading the database.
     *
     * @since 3.10
     */
    public String count(final LocalCache lc, final QueryType type, final Column column) throws SQLException {
        final DatabaseMetaData metadata = getMetaData(lc);
        final String quote = metadata.getIdentifierQuoteString().trim();
        final StringBuilder buffer = new StringBuilder("SELECT ");
        column.appendName(buffer, quote);
        column.appendName(buffer.append(", COUNT("), quote);
        buffer.append(") FROM ");
        if (!isIncludingChildTables()) {
            buffer.append("ONLY ");
        }
        appendTable(buffer, quote);
        appendParameters(buffer, type, metadata);
        column.appendName(buffer.append(" GROUP BY "), quote);
        return buffer.toString();
    }

    /**
     * Appends the {@linkplain #table table name} to the specified buffer. The catalog and
     * schema name are prefixed if needed.
     */
    private void appendTable(final StringBuilder buffer, final String quote) {
        if (database.catalog != null) {
            buffer.append(quote).append(database.catalog).append(quote).append('.');
        }
        if (schema != null) {
            buffer.append(quote).append(schema).append(quote).append('.');
        }
        buffer.append(quote).append(table).append(quote);
    }

    /**
     * Appends the specified function before its operands.
     */
    private static void appendFunctionPrefix(final StringBuilder buffer, final String function) {
        if (function != null) {
            if (!function.startsWith("::")) {
                buffer.append(function).append('(');
            }
        }
    }

    /**
     * Appends the specified function after its operands.
     */
    private static void appendFunctionSuffix(final StringBuilder buffer, final String function) {
        if (function != null) {
            if (function.startsWith("::")) {
                buffer.append(function);
            } else {
                buffer.append(')');
            }
        }
    }
}
