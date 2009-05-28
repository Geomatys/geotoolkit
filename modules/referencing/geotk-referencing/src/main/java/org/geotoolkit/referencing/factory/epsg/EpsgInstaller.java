/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.geotoolkit.resources.Errors;
import org.opengis.referencing.FactoryException;


/**
 * Installs the EPSG database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class EpsgInstaller {
    /**
     * The directory which contains the EPSG scripts. If {@code null}, then the script
     * will be read from the {@code geotk-epsg} JAR. If this JAR is not reacheable,
     * then an exception will be thrown.
     */
    private File scriptsDirectory;

    /**
     * The JDBC URL to the database. If {@code null}, a default URL to a embedded database will
     * be used. This default URL will point toward the Geotoolkit configuration directory, which
     * is platform-dependent ({@code ".geotoolkit"} on Linux).
     */
    private String databaseUrl;

    /**
     * The user for the database connection (optional).
     */
    private String user;

    /**
     * The password for the database connection. Ignored if the {@linkplain #user} is {@code null}.
     */
    private String password;

    /**
     * Creates a new installer which will executes the EPSG script in the given directory
     * for creating the tables in the given database.
     *
     * @param scriptsDirectory The directory which contains the EPSG scripts.
     * @param databaseUrl      The JDBC URL to the database.
     */
    public EpsgInstaller(final File scriptsDirectory, final String databaseUrl) {
        this.scriptsDirectory = scriptsDirectory;
        this.databaseUrl      = databaseUrl;
    }

    /**
     * Sets the user and password for connection to the database.
     *
     * @param user The user.
     * @param password The password.
     */
    public void setUser(final String user, final String password) {
        this.user     = user;
        this.password = password;
    }

    /**
     * Processes to the creation of the EPSG database.
     *
     * @throws FactoryException If an error occured while running the scripts.
     */
    public void create() throws FactoryException {
        if (databaseUrl == null) {
            databaseUrl = ThreadedEpsgFactory.getDefaultURL();
        }
        Exception failure;
        EpsgScriptRunner runner = null;
        try {
            final Connection connection;
            if (user == null) {
                connection = DriverManager.getConnection(databaseUrl);
            } else {
                connection = DriverManager.getConnection(databaseUrl, user, password);
            }
            /*
             * Nows executes the script using the given connection, either looking for scripts
             * in the given directory or looking for scripts embedded in the JAR file.
             */
            try {
                runner = new EpsgScriptRunner(connection);
                runner.setEncoding("ISO-8859-1");
                runner.setSchema("epsg");
                if (scriptsDirectory != null) {
                    runner.run(scriptsDirectory);
                } else {
                    runner.splitMultirows = true;
                    for (String script : EpsgScriptRunner.SCRIPTS) {
                        script += ".sql";
                        final InputStream in = EpsgScriptRunner.class.getResourceAsStream(script);
                        if (in == null) {
                            throw new FileNotFoundException(Errors.format(
                                    Errors.Keys.FILE_DOES_NOT_EXIST_$1, script));
                        }
                        runner.run(in);
                        in.close();
                    }
                }
                runner.close();
                return;
            } finally {
                connection.close();
                ThreadedEpsgFactory.shutdown(databaseUrl);
            }
        } catch (SQLException e) {
            failure = e;
        } catch (IOException e) {
            failure = e;
        }
        String message = failure.getLocalizedMessage();
        if (runner != null) {
            final String position = runner.getCurrentPosition();
            if (position != null) {
                message = message + '\n' + position;
            }
        }
        throw new FactoryException(message, failure);
    }
}
