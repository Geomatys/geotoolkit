/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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
import java.sql.SQLException;


/**
 * Thrown when an inconsistency has been found in a record. This exception occurs for example
 * when a negative value has been found in a database column where only positive values were
 * expected.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
public class IllegalRecordException extends CatalogException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8491590864510381052L;

    /**
     * Creates an exception with no cause and no details message.
     */
    public IllegalRecordException() {
        super();
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public IllegalRecordException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause The cause for this exception.
     */
    public IllegalRecordException(final Exception cause) {
        super(cause);
    }

    /**
     * Creates an exception from the specified result set. The table and column names are
     * obtained from the {@code results} argument if non-null. <strong>Note that the result
     * set will be closed</strong>, because this exception is always thrown when an error
     * occured while reading this result set.
     *
     * @param message The details message.
     * @param table   The table that produced the result set, or {@code null} if unknown.
     * @param results The result set in which a problem occured, or {@code null} if none.
     * @param column  The column index where a problem occured (number starts at 1), or {@code 0} if unknow.
     * @param key     The key value for the record where a problem occured, or {@code null} if none.
     *                The key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can't be read from the result set.
     */
    IllegalRecordException(final String message, final Table table, final ResultSet results,
                           final int column, final Comparable<?> key) throws SQLException
    {
        super(message);
        setMetadata(table, results, column, key);
    }

    /**
     * Creates an exception from the specified result set. The table and column names are
     * obtained from the {@code results} argument if non-null. <strong>Note that the result
     * set will be closed</strong>, because this exception is always thrown when an error
     * occured while reading this result set.
     *
     * @param cause   The cause for this exception.
     * @param table   The table that produced the result set, or {@code null} if unknown.
     * @param results The result set in which a problem occured, or {@code null} if none.
     * @param column  The column index where a problem occured (number starts at 1), or {@code 0} if unknow.
     * @param key     The key value for the record where a problem occured, or {@code null} if none.
     *                The key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can't be read from the result set.
     */
    IllegalRecordException(final Exception cause, final Table table, final ResultSet results,
                           final int column, final Comparable<?> key) throws SQLException
    {
        super(cause);
        setMetadata(table, results, column, key);
    }
}
