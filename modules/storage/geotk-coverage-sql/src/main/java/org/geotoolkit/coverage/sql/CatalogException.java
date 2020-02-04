/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Classes;
import org.apache.sis.util.resources.IndexedResourceBundle;

import org.geotoolkit.resources.Errors;


/**
 * Base class for exceptions that may occur while querying the catalog.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Eve (Geomatys)
 */
public class CatalogException extends DataStoreException {
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
    private Object key;

    /**
     * The locale to use for formatting error messages, or {@code null} for the default.
     */
    private Locale locale;

    /**
     * Creates an exception with no cause and no details message.
     */
    CatalogException() {
        super();
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message  the detail message.
     */
    CatalogException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause  the cause for this exception.
     */
    CatalogException(final Exception cause) {
        super(cause);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param  message  the detail message.
     * @param  cause    the cause for this exception.
     */
    CatalogException(final String message, final Exception cause) {
        super(message, cause);
    }

    /**
     * Sets the table and column names from the {@linkplain ResultSetMetaData result set metadata}.
     *
     * @param  results  the result set in which a problem occurred, or {@code null} if none.
     * @param  column   the column index where a problem occurred (number starts at 1), or {@code 0} if unknown.
     * @param  key      the key value for the record where a problem occurred, or {@code null} if none.
     *                  the key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can't be read from the result set.
     */
    final void setMetadata(final ResultSet results, final int column, final Object key)
            throws SQLException
    {
        if (results != null && column != 0 && !results.isClosed()) {
            final ResultSetMetaData metadata = results.getMetaData();
            if (metadata != null) {
                this.table  = metadata.getTableName (column);
                this.column = metadata.getColumnName(column);
            }
        }
        this.key = key;
    }

    /**
     * Returns the table name where a problem occurred, or {@code null} if unknown.
     *
     * @return the table where a problem occurred, or {@code null} if unknown.
     */
    public String getTableName() {
        return table;
    }

    /**
     * Returns the column name where a problem occurred, or {@code null} if unknown.
     *
     * @return the column where a problem occurred, or {@code null} if unknown.
     */
    public String getColumnName() {
        return column;
    }

    /**
     * Returns the primary key for the record where a problem occurred, or {@code null} if unknown.
     * The primary key is either a {@link String} or an {@link Integer} instance.
     *
     * @return the identifier of the entry where a problem occurred, or {@code null} if unknown.
     */
    public Object getPrimaryKey() {
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
        final Object key = getPrimaryKey();
        if (table != null) {
            final short localKey;
            final Object[] args;
            if (column != null) {
                if (key != null) {
                    localKey = Errors.Keys.CantReadDatabaseRecord_3;
                    args = new Object[] {table, column, key};
                } else {
                    localKey = Errors.Keys.CantReadDatabaseTable_2;
                    args = new String[] {table, column};
                }
            } else {
                if (key != null) {
                    localKey = Errors.Keys.CantReadDatabaseRecord_2;
                    args = new Object[] {table, key};
                } else {
                    localKey = Errors.Keys.CantReadDatabaseTable_1;
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
