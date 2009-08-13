/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.jdbc;

import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.geotoolkit.util.logging.Logging;


/**
 * An entry in {@link StatementPool}.
 *
 * {@section Synchronization}
 * This class is <strong>not</strong> thread-safe. Callers must perform their own synchronization
 * in such a way that only one query is executed on the same connection (JDBC connections can not
 * be assumed thread-safe).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
public class StatementEntry {
    /**
     * The timeout before to close a prepared statement.
     * This is set to 10 seconds.
     */
    static final int TIMEOUT = 10000;

    /**
     * The expiration time of this result. It must be initialized by the caller
     * to the current time + {@link #TIMEOUT}.
     */
    long expireTime;

    /**
     * The statement for a specific table.
     */
    protected final PreparedStatement statement;

    /**
     * Constructs a metadata result for the specified statement.
     *
     * @param statement The prepared statement.
     */
    public StatementEntry(final PreparedStatement statement) {
        this.statement = statement;
    }

    /**
     * Notifies this object that it has been used.
     */
    final void touch() {
        expireTime = System.currentTimeMillis() + TIMEOUT;
    }

    /**
     * Closes the statement and free all resources. After this method
     * has been invoked, this object can't be used anymore.
     *
     * @throws SQLException If an error occured while closing the statement.
     */
    public void close() throws SQLException {
        statement.close();
    }

    /**
     * Closes the statement and free all resources. In case of failure while closing JDBC objects,
     * the message is logged but the process continue since we are not supposed to use the statement
     * anymore. This method is invoked from other methods that can not throws an SQL exception.
     */
    final void closeQuietly() {
        try {
            close();
        } catch (SQLException e) {
            /*
             * Use the logger of the package that subclass this entry, and pretent that the
             * message comme from PreparedStatement.close() which is the closest we can get
             * to a public API.
             */
            Logging.recoverableException(Logging.getLogger(getClass()), PreparedStatement.class, "close", e);
        }
    }
}
