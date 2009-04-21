/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.sql.Driver;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Loggings;


/**
 * A set of utilities methods related to JDBC (<cite>Java Database Connectivity</cite>).
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@Static
public final class JDBC {
    /**
     * Lists of JDBC drivers already loaded.
     */
    private static final Set<String> DRIVERS = new HashSet<String>();

    /**
     * Do not allow instantiation of this class.
     */
    private JDBC() {
    }

    /**
     * Attempts to load the specified JDBC driver, if not already done. If this method has already
     * been invoked for the specified driver, then it does nothing and returns {@code null}.
     * Otherwise, it attempts to load the specified driver and returns a log record initialized
     * with a message at the {@link Level#CONFIG CONFIG} level on success, or at the
     * {@link Level#WARNING WARNING} level on failure.
     *
     * @param  driver The JDBC driver to load, as a fully qualified Java class name.
     * @return A log message with driver information, or {@code null} if the driver
     *         was already loaded.
     */
    public static LogRecord loadDriver(final String driver) {
        LogRecord log = null;
        if (driver != null) {
            synchronized (DRIVERS) {
                if (!DRIVERS.contains(driver)) {
                    try {
                        final Driver d = (Driver) Class.forName(driver).newInstance();
                        log = Loggings.format(Level.CONFIG, Loggings.Keys.LOADED_JDBC_DRIVER_$3,
                                driver, d.getMajorVersion(), d.getMinorVersion());
                        DRIVERS.add(driver);
                    } catch (Exception exception) {
                        log = new LogRecord(Level.WARNING, exception.toString());
                    }
                }
            }
        }
        return log;
    }
}
