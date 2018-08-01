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
import java.sql.SQLException;


/**
 * Information about a read or write operation in progress.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class Transaction implements AutoCloseable {
    /**
     * Information common to all tables.
     */
    final Database database;

    /**
     * The connection to the database.
     */
    final Connection connection;

    /**
     * Creates a new instance.
     */
    Transaction(final Database database, final Connection connection) {
        this.database   = database;
        this.connection = connection;
    }

    /**
     * Closes the connection.
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
