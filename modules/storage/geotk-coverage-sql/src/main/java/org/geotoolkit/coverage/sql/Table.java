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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Locale;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.sis.util.resources.IndexedResourceBundle;
import org.geotoolkit.resources.Errors;


/**
 * Base class for a table in a raster database. This class is not thread-safe.
 * For usage in multi-threads environment, different instances shall be created for each thread.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
abstract class Table implements AutoCloseable {
    /**
     * Name of the schema where all tables are stored.
     */
    static final String SCHEMA = "rasters";

    /**
     * Information about the read or write operation in progress.
     */
    final Transaction transaction;

    /**
     * The statement to be executed, or {@code null} if not yet created.
     */
    private PreparedStatement statement;

    /**
     * The {@linkplain #statement} in SQL language, for checking if current prepared statement can be reused.
     * This is slightly redundant with prepared statement pool provided by JDBC driver. We use that as a cheap
     * check for a common case before to rely on the more heavy prepared statement pool.
     */
    private String currentSQL;

    /**
     * Creates a new table.
     */
    Table(final Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Returns the connection to the database.
     */
    final Connection getConnection() {
        return transaction.connection;
    }

    /**
     * Returns a prepared statement for the given query.
     */
    @SuppressWarnings("StringEquality")
    final PreparedStatement prepareStatement(final String sql) throws SQLException {
        if (currentSQL != sql) {        // Identity check okay for the purpose of this package.
            close();
            statement  = getConnection().prepareStatement(sql);
            currentSQL = sql;
        }
        return statement;
    }

    /**
     * Returns a prepared statement for the given update to be executed on a table having
     * an auto-generated identifier.
     */
    @SuppressWarnings("StringEquality")
    final PreparedStatement prepareStatement(final String sql, final String keyColumn) throws SQLException {
        if (currentSQL != sql) {        // Identity check okay for the purpose of this package.
            close();
            statement  = getConnection().prepareStatement(sql, new String[] {keyColumn});
            currentSQL = sql;
        }
        return statement;
    }

    /**
     * Creates a calendar for queries of dates in the database.
     */
    final Calendar newCalendar() {
        return new GregorianCalendar(transaction.database.timezone, Locale.CANADA);
    }

    static Instant toInstant(final Date date) {
        return (date != null) ? date.toInstant() : null;
    }

    /**
     * Returns the resources to use for formatting error messages.
     *
     * @return the {@link Errors} resource bundle.
     */
    final IndexedResourceBundle errors() {
        return Errors.getResources(transaction.database.locale);
    }

    /**
     * Closes the prepared statements created by this table.
     */
    @Override
    public void close() throws SQLException {
        final PreparedStatement s = statement;
        if (s != null) {
            statement  = null;
            currentSQL = null;
            s.close();
        }
    }
}
