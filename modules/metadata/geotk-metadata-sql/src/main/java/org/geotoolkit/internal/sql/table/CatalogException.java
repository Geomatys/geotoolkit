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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLNonTransientException;
import java.util.Locale;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.apache.sis.util.Classes;


/**
 * Base class for exceptions that may occur while querying the catalog.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Eve (IRD)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public class CatalogException extends SQLNonTransientException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3838293108990270182L;

    /**
     * The table name where a problem occurred, or {@code null} if unknown.
     */
    private String table;

    /**
     * The column name where a problem occurred, or {@code null} if unknown.
     */
    private String column;

    /**
     * The primary key for the record where a problem occurred, or {@code null} if unknown.
     */
    private Comparable<?> key;

    /**
     * The locale to use for formatting error messages, or {@code null} for the default.
     */
    private Locale locale;

    /**
     * Creates an exception with no cause and no details message.
     */
    public CatalogException() {
        super();
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public CatalogException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause The cause for this exception.
     */
    public CatalogException(final Exception cause) {
        super(cause);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param message The detail message.
     * @param cause The cause for this exception.
     */
    public CatalogException(final String message, final Exception cause) {
        super(message, cause);
    }

    /**
     * Returns {@code true} if {@link #setMetadata} has been invoked with non-null values.
     */
    final boolean isMetadataInitialized() {
        return (table != null) || (column != null) || (key != null);
    }

    /**
     * Sets the table and column names from the given column.
     *
     * @param column  The column where a problem occurred.
     * @param key     The key value for the record where a problem occurred, or {@code null} if none.
     *                The key shall be either a {@link String} or {@link Integer} instance.
     */
    final void setMetadata(final Column column, final Comparable<?> key) {
        this.table  = column.table;
        this.column = column.name;
        this.key    = key;
    }

    /**
     * Sets the table and column names from the {@linkplain ResultSetMetaData result set metadata}.
     * <strong>Note that the result set will be closed</strong>, because this exception is always
     * thrown when an error occurred while reading this result set.
     *
     * @param table   The table that produced the result set, or {@code null} if unknown.
     * @param results The result set in which a problem occurred, or {@code null} if none.
     * @param column  The column index where a problem occurred (number starts at 1), or {@code 0} if unknown.
     * @param key     The key value for the record where a problem occurred, or {@code null} if none.
     *                The key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can't be read from the result set.
     */
    final void setMetadata(final Table table, final ResultSet results, final int column, final Comparable<?> key)
            throws SQLException
    {
        boolean noTable=true, noColumn=true;
        if (results != null && column != 0 && !results.isClosed()) {
            final ResultSetMetaData metadata = results.getMetaData();
            if (metadata != null) {
                this.table  = metadata.getTableName (column);
                this.column = metadata.getColumnName(column);
                noTable  = (this.table  == null) || (this.table  = this.table .trim()).isEmpty();
                noColumn = (this.column == null) || (this.column = this.column.trim()).isEmpty();
            }
            results.close();
        }
        /*
         * We tried to use the database metadata in priority,  on the assumption that they
         * are closer to the SQL statement really executed (we could have a bug in the way
         * we created our SQL statement). But some JDBC drivers don't provide information.
         * In the later case, we fallback on the information found in our Column objects.
         */
        if (table != null) {
            locale = table.getLocale();
            if (noTable || noColumn) {
                final Column c = table.getColumn(column);
                if (c != null) {
                    if (noTable)  this.table  = c.table;
                    if (noColumn) this.column = c.name;
                }
            }
        }
        this.key = key;
    }

    /**
     * Clears the column name. Invoked when this name is not reliable.
     */
    final void clearColumnName() {
        column = null;
    }

    /**
     * Returns the table name where a problem occurred, or {@code null} if unknown.
     *
     * @return The table where a problem occurred, or {@code null} if unknown.
     */
    public String getTableName() {
        return table;
    }

    /**
     * Returns the column name where a problem occurred, or {@code null} if unknown.
     *
     * @return The column where a problem occurred, or {@code null} if unknown.
     */
    public String getColumnName() {
        return column;
    }

    /**
     * Returns the primary key for the record where a problem occurred, or {@code null} if unknown.
     * The primary key is either a {@link String} or an {@link Integer} instance.
     *
     * @return The identifier of the entry where a problem occurred, or {@code null} if unknown.
     */
    public Comparable<?> getPrimaryKey() {
        return key;
    }

    /**
     * Returns the resources to use for formatting error messages.
     */
    final IndexedResourceBundle errors() {
        return Errors.getResources(locale);
    }

    /**
     * Returns a concatenation of the {@linkplain #getMessage details message} and the table
     * and column name where the error occurred.
     */
    @Override
    public String getLocalizedMessage() {
        String message = super.getLocalizedMessage();
        if (message == null) {
            final Throwable cause = getCause();
            if (cause != null) {
                message = cause.getLocalizedMessage();
                if (message == null) {
                    message = Classes.getShortClassName(cause);
                }
            }
        }
        final String table  = getTableName();
        final String column = getColumnName();
        final Comparable<?> key = getPrimaryKey();
        if (table != null) {
            final int localKey;
            final Comparable<?>[] args;
            if (column != null) {
                if (key != null) {
                    localKey = Errors.Keys.CANT_READ_DATABASE_RECORD_$3;
                    args = new Comparable<?>[] {table, column, key};
                } else {
                    localKey = Errors.Keys.CANT_READ_DATABASE_TABLE_$2;
                    args = new String[] {table, column};
                }
            } else {
                if (key != null) {
                    localKey = Errors.Keys.CANT_READ_DATABASE_RECORD_$2;
                    args = new Comparable<?>[] {table, key};
                } else {
                    localKey = Errors.Keys.CANT_READ_DATABASE_TABLE_$1;
                    args = new String[] {table};
                }
            }
            final String explain = errors().getString(localKey, args);
            if (message != null) {
                message = explain + ' ' + message;
            }
        }
        return message;
    }
}
