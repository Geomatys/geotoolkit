/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.io.Writer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.apache.sis.io.TableAppender;

import org.geotoolkit.nio.IOUtilities;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.logging.Logging;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * Copies the content of a table from a database to an other database. This class is used when
 * there is two copies of a database (typically an experimental copy and an operational copy)
 * and we want to copy the content of the experimental database to the operational database.
 * <p>
 * <b>Mandatory parameters:</b>
 * <ul>
 *   <li>Connection to the source and target databases</li>
 *   <li>List of tables to synchronize</li>
 * </ul>
 * <p>
 * <b>Optional parameters:</b>
 * <ul>
 *   <li>{@link Policy}: Whatever to empty the tables before to re-insert all entries.
 *       Used when table content need to be replaced, not just updated with new entries.</li>
 *   <li>{@link #pretend}: Whatever to print the SQL statements to standard output
 *       instead than executing them.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public final class Synchronizer {
    /**
     * Logging level to use for SQL operations.
     */
    private static final Level SELECT = Level.FINE, UPDATE = Level.FINE;

    /**
     * The kind of synchronization.
     */
    public static enum Policy {
        INSERT_ONLY, INSERT_OR_UPDATE, DELETE_BEFORE_INSERT
    };

    /**
     * The connection to the source and target databases.
     */
    private final Connection source, target;

    /**
     * The metadata for the source and target databases. Will be fetched when first needed.
     */
    private transient DatabaseMetaData sourceMetadata, targetMetadata;

    /**
     * The source and target catalogs, or {@code null} if none.
     */
    public String sourceCatalog, targetCatalog;

    /**
     * The source and target schemas, or {@code null} if none.
     */
    public String sourceSchema, targetSchema;

    /**
     * Where to print reports.
     */
    private final Writer out;

    /**
     * If {@code true}, no changes will be applied to the database. The {@code DELETE} and
     * {@code INSERT} statements will not be executed. This is useful for testing purpose,
     * or for getting the reports or log record without performing the action.
     */
    private boolean pretend;

    /**
     * Sets to {@code true} for canceling the operation.
     */
    public volatile boolean cancel;

    /**
     * Creates a synchronizer from the given source database to the given target database.
     *
     * @param source The connection to the source database.
     * @param target The connection to the target database.
     * @param out Where to print reports.
     */
    public Synchronizer(final Connection source, final Connection target, final Writer out) {
        this.source = source;
        this.target = target;
        this.out    = out;
    }

    /**
     * Creates a synchronizer from the given source database to the given target database.
     * The reports will be sent to the standard output stream.
     *
     * @param  source The URL to the source database.
     * @param  target The URL to the target database.
     * @throws SQLException If the connection to a database can not be established.
     */
    public Synchronizer(final String source, final String target) throws SQLException {
        this.source = DriverManager.getConnection(source);
        this.target = DriverManager.getConnection(target);
        this.out    = IOUtilities.standardWriter();
        this.source.setReadOnly(true);
    }

    /**
     * Appends a table name to the given buffer using the given quote character.
     * The schema is optional and can be null.
     */
    private static void appendTableName(final StringBuilder buffer, final String schema,
                                        final String table, final String quote)
    {
        buffer.append(quote);
        if (schema != null) {
            buffer.append(schema).append(quote).append('.').append(quote);
        }
        buffer.append(table).append(quote);
    }

    /**
     * Returns {@code true} if the given array contains the given value. This is usually
     * an inefficient way to make this checks when invoked in a loop. But for this class,
     * the given array will be very short (often only one element, usually not more than
     * three), so it should be sufficient.
     *
     * @param array The array where to check for a value. Elements doesn't need to be
     *              sorted (and often they are not).
     * @param value The value to search in the given array.
     */
    private static boolean contains(final int[] array, final int value) {
        for (int i=0; i<array.length; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the primary keys in the target database for the given table. If the primary keys
     * span over more than one column, then the columns are returned in sequence order. If the
     * table has no primary key, then this method returns an array of length 0.
     */
    private String[] getPrimaryKeys(final String table) throws SQLException {
        final String catalog = targetCatalog;
        final String schema  = targetSchema;
        String[] columns;
        try (ResultSet results = targetMetadata.getPrimaryKeys(catalog, schema, table)) {
            columns = CharSequences.EMPTY_ARRAY;
            while (results.next()) {
                if (catalog!=null && !catalog.equals(results.getString("TABLE_CAT"))) {
                    continue;
                }
                if (schema!=null && !schema.equals(results.getString("TABLE_SCHEM"))) {
                    continue;
                }
                if (!table.equals(results.getString("TABLE_NAME"))) {
                    continue;
                }
                final String column = results.getString("COLUMN_NAME");
                final int index = results.getShort("KEY_SEQ");
                if (index > columns.length) {
                    columns = Arrays.copyOf(columns, index);
                }
                columns[index - 1] = column;
            }
        }
        return columns;
    }

    /**
     * Returns the index of the specified column, or 0 if not found.
     *
     * @param  metadata The metadata to search into.
     * @param  name The column to search for.
     * @return The index of the specified column.
     */
    private static int getColumnIndex(final ResultSetMetaData metadata, final String column)
            throws SQLException
    {
        final int count = metadata.getColumnCount();
        for (int i=1; i<=count; i++) {
            if (column.equals(metadata.getColumnName(i))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns the index of the specified columns. If a column is not found, its corresponding
     * index will be left to 0.
     */
    private static int[] getColumnIndex(final ResultSetMetaData metadata, final String[] columns)
            throws SQLException
    {
        final int[] index = new int[columns.length];
        for (int i=0; i<columns.length; i++) {
            index[i] = getColumnIndex(metadata, columns[i]);
        }
        return index;
    }

    /**
     * Deletes the content of the specified table in the target database.
     *
     * @param  table The name of the table in which to delete the records.
     * @param  condition A SQL condition (to be put after a {@code WHERE} clause) for
     *         the records to be deleted, or {@code null} for deleting the whole table.
     *
     * @throws SQLException if a reading or writing operation failed.
     */
    private void delete(final String table, final String condition) throws SQLException {
        final String quote = targetMetadata.getIdentifierQuoteString();
        final StringBuilder buffer = new StringBuilder("DELETE FROM ");
        appendTableName(buffer, targetSchema, table, quote);
        if (condition != null) {
            buffer.append(" WHERE ").append(condition);
        }
        final String sql = buffer.toString();
        try (Statement targetStatement = target.createStatement()) {
            final int count = pretend ? 0 : targetStatement.executeUpdate(sql);
            log(UPDATE, "delete", sql + '\n' + count + " lignes supprimées.");
        }
    }

    /**
     * Copies the content of the specified table from source to the target database.
     * If a record already exists for the same primary key, the action will be determined
     * by the given policy.
     *
     * @param  table The name of the table to copy.
     * @param  condition A SQL condition (to be put after a {@code WHERE} clause) for
     *         the records to be copied, or {@code null} for copying the whole table.
     * @param  onExisting What to do with existing entries before to write new ones.
     * @throws SQLException if a reading or writing operation failed.
     * @throws IOException if an error occurred while writing reports on this operation.
     */
    private void insert(final String table, final String condition, final Policy onExisting)
            throws SQLException, IOException
    {
        /*
         * Creates the SQL statement for the SELECT query, opens the source ResultSet and
         * gets the metadata (especially the column names). This ResultSet will stay open
         * until the end of this method. We will use the column names later in order to
         * build the INSERT statement for the target database.
         */
        final String quoteSource = sourceMetadata.getIdentifierQuoteString();
        final StringBuilder buffer = new StringBuilder("SELECT * FROM ");
        appendTableName(buffer, sourceSchema, table, quoteSource);
        if (condition != null) {
            buffer.append(" WHERE ").append(condition);
        }
        String sql = buffer.toString();
        PreparedStatement existing = null;
        TableAppender mismatchs = null;
        try (Statement sourceStatement = source.createStatement();
             ResultSet sourceResultSet = sourceStatement.executeQuery(sql))
        {
            final ResultSetMetaData metadata = sourceResultSet.getMetaData();
            final String[] sourceColumns = new String[metadata.getColumnCount()];
            for (int i=0; i<sourceColumns.length;) {
                sourceColumns[i] = metadata.getColumnName(++i);
            }
            log(SELECT, "insert", sql);
            /*
             * Gets the primary keys of the target table. We don't check for primary keys in the
             * source table since it may be a view. Then gets the index (counting from 1) in the
             * source table for those primary keys.
             */
            final String[] pkColumns = getPrimaryKeys(table);
            final int[] pkSourceIndex = new int[pkColumns.length];
            for (int i=0; i<pkColumns.length; i++) {
                final String name = pkColumns[i];
                if ((pkSourceIndex[i] = getColumnIndex(metadata, name)) == 0) {
                    throw new SQLException("Primary key \"" + name + "\" defined in the target \"" +
                            table + "\" table is not found in the source table.");
                }
            }
            final int[] nonpkSourceIndex = new int[sourceColumns.length - pkSourceIndex.length];
            for (int i=0,j=0; i<sourceColumns.length;) {
                if (!contains(pkSourceIndex, ++i)) {
                    nonpkSourceIndex[j++] = i;
                }
            }
            assert !contains(nonpkSourceIndex, 0);
            /*
             * Creates the SQL statement for the SELECT or UPDATE query in the target database.
             * This is used in order to search for existing entries before to insert a new one.
             * This operation can be performed only if the target table contains at least one
             * primary key column.
             */
            final boolean update = nonpkSourceIndex.length != 0 && onExisting == Policy.INSERT_OR_UPDATE;
            final String quoteTarget = targetMetadata.getIdentifierQuoteString();
            if (pkColumns.length != 0 && onExisting != Policy.DELETE_BEFORE_INSERT) {
                buffer.setLength(0);
                appendTableName(buffer.append(update ? "UPDATE " : "SELECT * FROM "), targetSchema, table, quoteTarget);
                if (update) {
                    buffer.append(" SET ");
                    boolean afterFirst = false;
                    for (int i=0; i<nonpkSourceIndex.length; i++) {
                        if (afterFirst) buffer.append(',');
                        else afterFirst = true;
                        final String name = sourceColumns[nonpkSourceIndex[i] - 1];
                        buffer.append(quoteTarget).append(name).append(quoteTarget).append("=?");
                    }
                }
                String separator = " WHERE ";
                for (int i=0; i<pkColumns.length; i++) {
                    final String name = pkColumns[i];
                    buffer.append(separator).append(quoteTarget).append(name).append(quoteTarget).append("=?");
                    separator = " AND ";
                }
                sql = buffer.toString();
                existing = target.prepareStatement(sql);
            }
            /*
             * Creates the target prepared statement for the INSERT queries. The parameters will
             * need to be filled in the same order than the column from the source SELECT query.
             */
            buffer.setLength(0);
            appendTableName(buffer.append("INSERT INTO "), targetSchema, table, quoteTarget);
            buffer.append(" (");
            for (int i=0; i<sourceColumns.length; i++) {
                if (i != 0) buffer.append(',');
                buffer.append(quoteTarget).append(sourceColumns[i]).append(quoteTarget);
            }
            buffer.append(") VALUES (");
            for (int i=0; i<sourceColumns.length; i++) {
                if (i != 0) buffer.append(',');
                buffer.append('?');
            }
            sql = buffer.append(')').toString();
            try (PreparedStatement insertStatement = target.prepareStatement(sql)) {
                /*
                 * Reads all records from the source table and check if a corresponding records exists
                 * in the target table. If such record exists and have identical content, then nothing
                 * is done. If the content is not identical, then a warning is printed.
                 */
                int[] sourceToTarget = null;
                final Object[] primaryKeyValues = new Object[pkColumns.length];
                while (sourceResultSet.next()) {
                    if (cancel) break;
                    if (existing != null) {
                        int param = 0;
                        if (update) {
                            for (int i=0; i<nonpkSourceIndex.length; i++) {
                                final Object value = sourceResultSet.getObject(nonpkSourceIndex[i]);
                                existing.setObject(++param, value);
                            }
                        }
                        for (int i=0; i<pkSourceIndex.length; i++) {
                            final Object value = sourceResultSet.getObject(pkSourceIndex[i]);
                            existing.setObject(++param, value);
                            primaryKeyValues[i] = value;
                        }
                        int count = 0;
                        if (update) {
                            count = existing.executeUpdate();
                        } else {
                            try (ResultSet targetResultSet = existing.executeQuery()) {
                                if (sourceToTarget == null) {
                                    sourceToTarget = getColumnIndex(targetResultSet.getMetaData(), sourceColumns);
                                }
                                while (targetResultSet.next()) {
                                    for (int i=0; i<sourceToTarget.length; i++) {
                                        final int index = sourceToTarget[i];
                                        if (index == 0) {
                                            // Compares only the columns present in both tables.
                                            continue;
                                        }
                                        final String source = sourceResultSet.getString(i+1);
                                        final String target = targetResultSet.getString(index);
                                        if (!Objects.equals(source, target)) {
                                            if (mismatchs == null) {
                                                mismatchs = createMismatchTable(table, pkColumns);
                                            } else {
                                                mismatchs.nextLine();
                                            }
                                            for (int j=0; j<primaryKeyValues.length; j++) {
                                                mismatchs.append(String.valueOf(primaryKeyValues[j]));
                                                mismatchs.nextColumn();
                                            }
                                            mismatchs.append(sourceColumns[i]); mismatchs.nextColumn();
                                            mismatchs.append(source);           mismatchs.nextColumn();
                                            mismatchs.append(target);           mismatchs.nextLine();
                                        }
                                    }
                                    count++;
                                }
                            }
                        }
                        if (count != 0) {
                            continue;
                        }
                    }
                    /*
                     * At this point, we know that we have a new element.
                     * Now insert the new record in the target table.
                     */
                    for (int i=1; i<=sourceColumns.length; i++) {
                        insertStatement.setObject(i, sourceResultSet.getObject(i));
                    }
                    final int count = pretend ? 1 : insertStatement.executeUpdate();
                    if (count == 1) {
                        log(UPDATE, "insert", insertStatement.toString());
                    } else {
                        log(Level.WARNING, "insert", count + " enregistrements ajoutés.");
                    }
                }
            }
        } finally {
            if (existing != null) {
                existing.close();
            }
        }
        if (mismatchs != null) {
            mismatchs.nextLine('\u2500');
            mismatchs.flush();
        }
    }

    /**
     * Creates an initially empty (except for the header) table of mismatches.
     *
     * @param  table     The table name.
     * @param  pkColumns The column names.
     * @return A new table of mismatch.
     * @throws IOException if an error occurred while writing to the output stream.
     */
    private TableAppender createMismatchTable(final String table, final String[] pkColumns)
            throws IOException
    {
        final String lineSeparator = System.lineSeparator();
        out.write(lineSeparator);
        out.write(table);
        out.write(lineSeparator);
        final TableAppender mismatchs = new TableAppender(out);
        mismatchs.nextLine('\u2500');
        for (int j=0; j<pkColumns.length; j++) {
            mismatchs.append(pkColumns[j]);
            mismatchs.nextColumn();
        }
        mismatchs.append("Colonne");
        mismatchs.nextColumn();
        mismatchs.append("Valeur à copier");
        mismatchs.nextColumn();
        mismatchs.append("Valeur existante");
        mismatchs.nextLine();
        mismatchs.nextLine('\u2500');
        return mismatchs;
    }

    /**
     * Copies or replaces the content of the specified table. The {@linkplain Map#keySet map keys}
     * shall contains the set of every tables to take in account; table not listed in this set will
     * be untouched. The associated values are the SLQ conditions to put in the {@code WHERE} clauses.
     * <p>
     * This method process {@code table} as well as dependencies found in {@code tables}.
     * Processed dependencies are removed from the {@code tables} map.
     *
     * @param table   The table to process.
     * @param tables  The (<var>table</var>, <var>condition</var>) mapping. This map will be modified.
     * @param onExisting What to do with existing entries before to write new ones.
     *
     * @throws SQLException if an error occurred while reading or writing in the database.
     * @throws IOException if an error occurred while writing reports on this operation.
     */
    private void copy(final String table, final Map<String,String> tables, final Policy onExisting)
            throws SQLException, IOException
    {
        String condition = tables.remove(table);
        if (condition != null) {
            condition = condition.trim();
            if (condition.isEmpty()) {
                condition = null;
            }
        }
        if (onExisting == Policy.DELETE_BEFORE_INSERT) {
            delete(table, condition);
        }
        /*
         * Before to insert any new records, check if this table has some foreigner keys
         * toward other table.  If such tables are found, we will process them before to
         * add any record to the current table.
         */
        final String catalog = targetCatalog;
        final String schema  = targetSchema;
        try (ResultSet dependencies = targetMetadata.getImportedKeys(catalog, schema, table)) {
            while (dependencies.next()) {
                String dependency = dependencies.getString("PKTABLE_CAT");
                if (catalog!=null && !catalog.equals(dependency)) {
                    continue;
                }
                dependency = dependencies.getString("PKTABLE_SCHEM");
                if (schema!=null && !schema.equals(dependency)) {
                    continue;
                }
                dependency = dependencies.getString("PKTABLE_NAME");
                if (tables.containsKey(dependency)) {
                    copy(dependency, tables, onExisting);
                }
            }
        }
        insert(table, condition, onExisting);
    }

    /**
     * Copies or replaces the content of the specified tables. The {@linkplain Map#keySet map keys}
     * shall contains the set of every tables to take in account; table not listed in this set will
     * be untouched. The associated values are the SLQ conditions to put in the {@code WHERE} clauses.
     *
     * @param  onExisting What to do with existing entries before to write new ones.
     * @param  tables The (<var>table</var>, <var>condition</var>) mapping. This map will be modified.
     * @throws SQLException if an error occurred while reading or writing in the database.
     * @throws IOException if an error occurred while writing reports on this operation.
     */
    public void copy(final Policy onExisting, final Map<String,String> tables)
            throws SQLException, IOException
    {
        final String catalog = targetCatalog;
        final String schema  = targetSchema;
        sourceMetadata = source.getMetaData();
        targetMetadata = target.getMetaData();
search: while (!tables.isEmpty()) {
nextTable: for (final String table : tables.keySet()) {
                if (cancel) break search;
                // Skips all tables that have dependencies.
                try (ResultSet dependents = targetMetadata.getExportedKeys(catalog, schema, table)) {
                    while (dependents.next()) {
                        if ((catalog==null || catalog.equals(dependents.getString("FKTABLE_CAT"))) &&
                            (schema ==null || schema .equals(dependents.getString("FKTABLE_SCHEM"))))
                        {
                            final String dependent = dependents.getString("FKTABLE_NAME");
                            if (tables.containsKey(dependent)) {
                                continue nextTable;
                            }
                        }
                    }
                }
                // We have found a table which have no dependencies (a leaf).
                copy(table, tables, onExisting);
                continue search;
            }
            // We have been unable to find any leaf. Take a chance: process the first table.
            // An exception is likely to be throw, but we will have tried.
            for (final String table : tables.keySet()) {
                copy(table, tables, onExisting);
                continue search;
            }
        }
    }

    /**
     * Copies or replaces the content of the specified tables, without conditions.
     *
     * @param  onExisting What to do with existing entries before to write new ones.
     * @param  tables The list of tables to update.
     * @throws SQLException if an error occurred while reading or writing in the database.
     * @throws IOException if an error occurred while writing reports on this operation.
     */
    public void copy(final Policy onExisting, final String... tables) throws SQLException, IOException {
        final Map<String,String> map = new LinkedHashMap<>(hashMapCapacity(tables.length));
        for (final String table : tables) {
            map.put(table, null);
        }
        copy(onExisting, map);
    }

    /**
     * Closes the database connections.
     *
     * @throws SQLException If an error occurred while closing the connections.
     */
    public void close() throws SQLException {
        sourceMetadata = null;
        targetMetadata = null;
        target.close();
        source.close();
    }

    /**
     * Writes an event to the logger.
     */
    private static void log(final Level level, final String method, final String message) {
        final LogRecord record = new LogRecord(level, message);
        record.setSourceClassName(Synchronizer.class.getName());
        record.setSourceMethodName(method);
        Logging.getLogger("org.geotoolkit.sql").log(record);
    }
}
