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
     * {@code true} if the transaction is adding or updating data.
     */
    private boolean writing;

    /**
     * Creates a new instance.
     */
    Transaction(final Database database, final Connection connection){
        this.database   = database;
        this.connection = connection;
    }

    /**
     * Notifies that a writing process is about to begin.
     */
    final void writeStart() throws SQLException  {
        connection.setAutoCommit(false);
        writing = true;
    }

    /**
     * Notifies that a writing process completed successfully.
     */
    final void writeEnd() throws SQLException  {
        connection.commit();
        connection.setAutoCommit(true);
        writing = false;
    }

    /**
     * Closes the connection. If a writing process {@linkplain #writeStart() started} but did not
     * {@linkplain #writeEnd() ended}, then a rollback is done before to close the connection.
     */
    @Override
    public void close() throws SQLException {
        if (writing) {
            connection.rollback();
            writing = false;
        }
        connection.close();
    }
}
