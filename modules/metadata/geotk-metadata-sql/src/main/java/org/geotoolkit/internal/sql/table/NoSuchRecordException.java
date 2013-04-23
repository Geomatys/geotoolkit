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
import org.geotoolkit.resources.Errors;


/**
 * Throws when a record was not found for the specified key.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public class NoSuchRecordException extends CatalogException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -3105861955682823122L;

    /**
     * Creates an exception with the given message.
     *
     * @param message The detail message.
     */
    public NoSuchRecordException(final String message) {
        super(message);
    }

    /**
     * Creates an exception from the specified result set. The table and column names are
     * obtained from the {@code results} argument if non-null. <strong>Note that the result
     * set will be closed</strong>, because this exception is always thrown when an error
     * occurred while reading this result set.
     *
     * @param table   The table that produced the result set, or {@code null} if unknown.
     * @param results The result set used in order to look for a record.
     * @param column  The column index of the primary key (first column index is 1).
     * @param key     The key value for the record that was not found, or {@code null} if none.
     *                The key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can't be read from the result set.
     */
    NoSuchRecordException(final Table table, final ResultSet results, final int column, final Comparable<?> key)
            throws SQLException
    {
        setMetadata(table, results, column, key);
    }

    /**
     * Returns a localized message created from the information provided at construction time.
     */
    @Override
    public String getLocalizedMessage() {
        // Do not invoke super.getLocalizedMessage() because
        // the super-class implementation never returns null.
        String message = getMessage();
        if (message == null) {
            message = errors().getString(Errors.Keys.NO_SUCH_RECORD_IN_TABLE_2, getTableName(), getPrimaryKey());
        }
        return message;
    }
}
