/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For internal use of JDBC modules.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class JDBCFeatureStoreUtilities {
    
    private JDBCFeatureStoreUtilities(){
    }
    
    /**
     * Utility method for closing a result set.
     * <p>
     * This method closed the result set "safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param cx The connection to close.
     * @param st The statement to close.
     * @param rs The result set to close.
     */
    public static void closeSafe(final Logger logger,final Connection cx, final Statement st, final ResultSet rs){
        closeSafe(logger,cx);
        closeSafe(logger,st);
        closeSafe(logger,rs);
    }

    /**
     * Utility method for closing a result set.
     * <p>
     * This method closed the result set "safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param rs The result set to close.
     */
    public static void closeSafe(final Logger logger,final ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.close();
        } catch (SQLException e) {
            final String msg = "Error occurred closing result set";
            logger.warning(msg);

            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, msg, e);
            }
        }
    }

    /**
     * Utility method for closing a statement.
     * <p>
     * This method closed the statement"safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param st The statement to close.
     */
    public static void closeSafe(final Logger logger,final Statement st) {
        if (st == null) {
            return;
        }

        try {
            st.close();
        } catch (SQLException e) {
            final String msg = "Error occurred closing statement";
            logger.warning(msg);

            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, msg, e);
            }
        }
    }

    /**
     * Utility method for closing a connection.
     * <p>
     * This method closed the connection "safely" in that it never throws an
     * exception. Any exceptions that do occur are logged at {@link Level#FINER}.
     * </p>
     * @param cx The connection to close.
     */
    public static void closeSafe(final Logger logger, final Connection cx) {
        if (cx == null) {
            return;
        }

        try {
            cx.close();
            logger.fine("CLOSE CONNECTION");
        } catch (SQLException e) {
            final String msg = "Error occurred closing connection";
            logger.warning(msg);

            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, msg, e);
            }
        }
    }
    
}
