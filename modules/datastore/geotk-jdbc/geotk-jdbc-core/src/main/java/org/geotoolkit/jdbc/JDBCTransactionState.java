/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.data.Transaction;
import org.geotoolkit.data.Transaction.State;


public final class JDBCTransactionState implements State {
    /**
     * the datastore
     */
    final JDBCDataStore dataStore;
    /**
     * the current transaction
     */
    Transaction tx;
    /**
     * The current connection
     */
    Connection cx;

    public JDBCTransactionState(final Connection cx, final JDBCDataStore dataStore) {
        this.cx = cx;
        this.dataStore = dataStore;
    }

    @Override
    public void setTransaction(final Transaction tx) {
        if (tx != null && this.tx != null) {
            throw new IllegalStateException("New transaction set without " +
                    "closing old transaction first.");
        }

        if (tx == null) {
            if (cx != null) {
                try {
                    cx.close();
                } catch (SQLException e) {
                    //TODO: perhaps we should log this at the finest level
                }
            } else {
                dataStore.getLogger().warning("Transaction is attempting to " +
                        "close an already closed connection");
            }
            cx = null;
        }

        this.tx = tx;
    }

    @Override
    public void addAuthorization(final String AuthID) throws IOException {
    }

    @Override
    public void commit() throws IOException {
        try {
            cx.commit();
        } catch (SQLException e) {
            String msg = "Error occured on commit";
            throw (IOException) new IOException(msg).initCause(e);
        }
    }

    @Override
    public void rollback() throws IOException {
        try {
            cx.rollback();
        } catch (SQLException e) {
            String msg = "Error occured on rollback";
            throw (IOException) new IOException(msg).initCause(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (cx != null && !cx.isClosed()) {
            Logging.getLogger("org.geotoolkit.jdbc").severe("State finalized with open connection.");
        }
    }
}
