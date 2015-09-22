/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.metadata.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.internal.sql.StatementEntry;


/**
 * The result of a query for metadata attributes. This object {@linkplain PreparedStatement
 * prepares a statement} once for ever for a given table. When a particular record in this
 * table is fetched, the {@link ResultSet} is automatically constructed. If many attributes
 * are fetched consecutively for the same record, then the same {@link ResultSet} is reused.
 *
 * {@section Synchronization}
 * This class is <strong>not</strong> thread-safe. Callers must perform their own synchronization
 * in such a way that only one query is executed on the same connection (JDBC connections can not
 * be assumed thread-safe).
 *
 * @author Touraïvane (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.03
 *
 * @since 3.03 (derived from 2.1)
 * @module
 */
final class MetadataResult extends StatementEntry {
    /**
     * The implemented interface, used for formatting error messages.
     */
    private final Class<?> type;

    /**
     * The results, or {@code null} if not yet determined.
     */
    private ResultSet results;

    /**
     * The identifier (usually the primary key) for current results.
     * If the record to fetch doesn't have the same identifier, then
     * the {@link #results} will need to be closed and reconstructed.
     */
    private String identifier;

    /**
     * Constructs a metadata result from the specified connection.
     *
     * @param  type The implemented interface.
     * @param  statement The prepared statement to be executed.
     */
    MetadataResult(final Class<?> type, final PreparedStatement statement) {
        super(statement);
        this.type = type;
    }

    /**
     * Closes the current {@link ResultSet}. Before doing so, we make an opportunist check for
     * duplicated values in the table. If a duplicate is found, a warning is logged. The log
     * message pretends to be emitted by the interface constructor, which doesn't exist. But
     * this is the closest we can get from a public API. Otherwise the emitter is deep behind
     * {@link java.lang.reflect.Proxy}, which generates on-the-fly implementations of the
     * interface (so we are not completely wrong neither).
     */
    private void closeResultSet() throws SQLException {
        final boolean hasNext = results.next();
        results.close();
        results = null;
        if (hasNext) {
            final LogRecord record = Errors.getResources(null).getLogRecord(
                    Level.WARNING, Errors.Keys.DuplicatedValuesForKey_1, identifier);
            record.setSourceClassName(type.getCanonicalName());
            record.setSourceMethodName("<init>");
            Logging.getLogger("org.geotoolkit.sql").log(record);
        }
        identifier = null;
    }

    /**
     * Returns the result set for the record having the given identifier.
     *
     * @param  identifier The object identifier, usually the primary key value.
     * @return The result set.
     * @throws SQLException if an SQL operation failed.
     */
    private ResultSet getResultSet(final String identifier) throws SQLException {
        if (results != null) {
            if (this.identifier.equals(identifier)) {
                return results;
            }
            closeResultSet();
        }
        statement.setString(1, identifier);
        results = statement.executeQuery();
        if (!results.next()) {
            final String table = results.getMetaData().getTableName(1);
            results.close();
            results = null;
            throw new SQLException(Errors.format(Errors.Keys.NoSuchRecord_2, table, identifier));
        }
        this.identifier = identifier;
        return results;
    }

    /**
     * Returns the attribute value in the given column for the given record.
     *
     * @param  identifier The object identifier, usually the primary key value.
     * @param  columnName The column name of the attribute to search.
     * @return The attribute value.
     * @throws SQLException if an SQL operation failed.
     */
    public Object getObject(final String identifier, final String columnName) throws SQLException {
        return getResultSet(identifier).getObject(columnName);
    }

    /**
     * Closes this statement and free all resources. After this method
     * has been invoked, this object can't be used anymore.
     *
     * @throws SQLException If an error occurred while closing the statement.
     */
    @Override
    public void close() throws SQLException {
        if (results != null) {
            closeResultSet();
        }
        super.close();
    }
}
