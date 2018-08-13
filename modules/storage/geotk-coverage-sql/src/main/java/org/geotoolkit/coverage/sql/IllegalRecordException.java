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
import java.sql.SQLException;


/**
 * Thrown when an inconsistency has been found in a record. This exception occurs for example
 * when a negative value has been found in a database column where only positive values were
 * expected.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
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
     * @param message  the detail message.
     */
    public IllegalRecordException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param message  the detail message.
     * @param cause    the cause for this exception.
     */
    public IllegalRecordException(final String message, final Exception cause) {
        super(message, cause);
    }

    /**
     * Creates an exception from the specified result set. The table and column names are
     * obtained from the {@code results} argument if non-null. <strong>Note that the result
     * set will be closed</strong>, because this exception is always thrown when an error
     * occurred while reading this result set.
     *
     * @param message  the details message.
     * @param results  the result set in which a problem occurred, or {@code null} if none.
     * @param column   the column index where a problem occurred (number starts at 1), or {@code 0} if unknown.
     * @param key      the key value for the record where a problem occurred, or {@code null} if none.
     *                 the key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can not be read from the result set.
     */
    IllegalRecordException(final String message, final ResultSet results,
                           final int column, final Object key) throws SQLException
    {
        super(message);
        setMetadata(results, column, key);
    }

    /**
     * Creates an exception from the specified result set. The table and column names are
     * obtained from the {@code results} argument if non-null. <strong>Note that the result
     * set will be closed</strong>, because this exception is always thrown when an error
     * occurred while reading this result set.
     *
     * @param cause    the cause for this exception.
     * @param results  the result set in which a problem occurred, or {@code null} if none.
     * @param column   the column index where a problem occurred (number starts at 1), or {@code 0} if unknown.
     * @param key      the key value for the record where a problem occurred, or {@code null} if none.
     *                 the key shall be either a {@link String} or {@link Integer} instance.
     * @throws SQLException if the metadata can not be read from the result set.
     */
    IllegalRecordException(final Exception cause, final ResultSet results,
                           final int column, final Object key) throws SQLException
    {
        super(cause);
        setMetadata(results, column, key);
    }
}
