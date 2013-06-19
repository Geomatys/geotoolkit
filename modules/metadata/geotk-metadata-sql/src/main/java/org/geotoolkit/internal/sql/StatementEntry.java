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
package org.geotoolkit.internal.sql;

import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.apache.sis.util.logging.Logging;


/**
 * An entry in {@link StatementPool}.
 *
 * {@note We could define a <code>StatementAdapter</code> class which implement the
 * <code>PreparedStatement</code> interface  and delegate every method calls to the
 * wrapped statement. Then this <code>StatementEntry</code> class could extend that
 * <code>StatementAdapter</code> class. But for now we prefer to avoid the weight of
 * such a big adapter and force every usage of <code>StatementEntry</code> to work
 * directly with the wrapped statement.}
 *
 * {@section Synchronization}
 * This class is <strong>not</strong> thread-safe. Callers must perform their own synchronization
 * in such a way that only one query is executed on the same connection (JDBC connections can not
 * be assumed thread-safe). The synchronization block shall be the {@link StatementPool} which
 * contain this entry.
 *
 * {@section Closing}
 * This class does not implement {@link java.lang.AutoCloseable} because it is typically closed
 * by method (and even a thread) different than the one that created the {@code StatementEntry}
 * instance. It normally closed by {@link StatementPool#close()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
public class StatementEntry {
    /**
     * The expiration time of this result. This is read and updated by {@link StatementPool} only.
     */
    long expireTime;

    /**
     * The statement associated with this entry.
     * This is the statement given to the constructor.
     */
    public final PreparedStatement statement;

    /**
     * Constructs a metadata result for the specified statement.
     *
     * @param statement The prepared statement.
     */
    public StatementEntry(final PreparedStatement statement) {
        this.statement = statement;
    }

    /**
     * Closes the statement and free all resources. After this method
     * has been invoked, this object can't be used anymore.
     * <p>
     * This method should be invoked instead than a direct call to {@code statement.close()}
     * because some subclasses override this method in order to release additional resources
     * hold by this {@code StatementEntry}.
     * <p>
     * This method is usually not invoked by the method or thread that created the
     * {@code StatementEntry} instance. It is invoked by {@link StatementPool#close()} instead.
     *
     * @throws SQLException If an error occurred while closing the statement.
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
        } catch (Exception e) {
            // Catch Exception rather than SQLException because this method is invoked from semi-
            // critical code which need to never fail, otherwise some memory leak could occur.
            /*
             * Use the logger of the package that subclass this entry, and pretend that the
             * message comme from PreparedStatement.close() which is the closest we can get
             * to a public API.
             */
            Logging.recoverableException(DefaultDataSource.LOGGER, PreparedStatement.class, "close", e);
        }
    }
}
