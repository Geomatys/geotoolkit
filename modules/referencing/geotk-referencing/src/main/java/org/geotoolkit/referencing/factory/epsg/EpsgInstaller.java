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
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.concurrent.Callable;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;


/**
 * Installs the EPSG database. By default this class performs the following operations:
 * <p>
 * <ul>
 *   <li>Gets a connection to the embedded database (JavaDB), creating it on the local
 *       machine if necessary. The location of that database is defined by the
 *       {@linkplain ThreadedEpsgFactory#getDefaultURL() default URL}.</li>
 *   <li>Executes the SQL scripts embedded in the
 *       <a href="http://www.geotoolkit.org/modules/referencing/geotk-referencing/index.html">geotk-epsg</a>
 *       module. Those scripts are derived from the ones distributed on the
 *       <a href="http://www.epsg.org">www.epsg.org</a> web site.</li>
 * </ul>
 * <p>
 * This default behavior can be changed by the setter methods defined in this class.
 * They allow to use a different set of scripts, or to execute them on a different
 * database (for example <a href="http://www.postgresql.org">PostgreSQL</a>).
 * <p>
 * The scripts are read and the database is created only when the {@link #call()} method
 * is invoked.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class EpsgInstaller implements Callable<EpsgInstaller.Result> {
    /**
     * The schema where the installer will create the tables in the JavaDB database.
     * The value is {@value}.
     */
    public static final String SCHEMA = "epsg";

    /**
     * The directory which contain the EPSG scripts. If {@code null}, then the scripts
     * will be read from the {@code geotk-epsg.jar} file. If this JAR is not reacheable,
     * then an exception will be thrown.
     */
    private File scriptsDirectory;

    /**
     * The JDBC URL to the database. If {@code null}, a default URL to a JavaDB database on the
     * local machine will be used. This default URL will point toward the Geotoolkit configuration
     * directory, which is platform-dependent ({@code ".geotoolkit"} on Linux).
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
     * Creates a new installer. By default, the scripts will be read from the {@code geotk-epsg.jar}
     * file and the database is created on the local machine using JavaDB.
     */
    public EpsgInstaller() {
    }

    /**
     * Sets the directory which contain the SQL scripts to execute. This directory should contain
     * at least files similar to the following ones (files without {@code ".sql"} extension are
     * ignored):
     * <p>
     * <ul>
     *   <li>{@code EPSG_v6_18.mdb_Tables_PostgreSQL.sql}</li>
     *   <li>{@code EPSG_v6_18.mdb_Data_PostgreSQL.sql}</li>
     *   <li>{@code EPSG_v6_18.mdb_FKeys_PostgreSQL.sql}</li>
     *   <li>Optional but recommanded: {@code EPSG_v6_18.mdb_Indexes_PostgreSQL.sql} using
     *       a copy of the script embedded in the {@code geotk-epsg.jar} file.</li>
     * </ul>
     * <p>
     * The suffix may be different (for example {@code "_MySQL.sql"} instead of
     * {@code "_PostgreSQL.sql"}) and the version number may be different. If the directory
     * contrains the scripts for different versions of the database, then the scripts for
     * the latest version are used.
     * <p>
     * If this method is never invoked or if the given directory is {@code null}, then the
     * scripts will be read from the {@code geotk-epsg.jar} file. If that JAR is not reacheable,
     * then an exception will be thrown at {@link #call()} invocation time.
     *
     * @param directory The directory of the EPSG SQL scripts to execute, or {@code null}
     */
    public void setScriptDirectory(final File directory) {
        scriptsDirectory = directory;
    }

    /**
     * Sets the URL to the database, using the JDBC syntax. If this method is never invoked of
     * if the given URL is {@code null}, then the {@linkplain ThreadedEpsgFactory#getDefaultURL()
     * default URL} is used.
     * <p>
     * If a user and password were previously defined, they are left unchanged.
     *
     * @param url The URL to the database, or {@code null} for JavaDB on the local machine.
     */
    public void setDatabase(final String url) {
        databaseUrl = url;
    }

    /**
     * Sets the URL to the database, together with the user and password. The URL to
     * the database is handled in the same way than {@link #setDatabase(String)}.
     *
     * @param url The URL to the database, or {@code null} for JavaDB on the local machine.
     * @param user The user, or {@code null} if none.
     * @param password The password, or {@code null} if none.
     */
    public void setDatabase(final String url, final String user, final String password) {
        this.databaseUrl = url;
        this.user        = user;
        this.password    = password;
    }

    /**
     * Processes to the creation of the EPSG database.
     *
     * @return The result of the EPSG database creation.
     * @throws FactoryException If an error occured while running the scripts.
     */
    @Override
    public Result call() throws FactoryException {
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
             * Now execute the script using the given connection, either looking for scripts
             * in the given directory or looking for scripts embedded in the JAR file.
             */
            try {
                runner = new EpsgScriptRunner(connection);
                return call(runner);
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

    /**
     * Processes to the creation of the EPSG database using the given runner.
     * This method does not close the connection.
     *
     * @param  runner A newly initialized runner. It will be configured by this method.
     * @return The result of the EPSG database creation.
     * @throws IOException If an error occured while reading the input.
     * @throws SQLException If an error occured while executing a SQL statement.
     */
    final Result call(final EpsgScriptRunner runner) throws SQLException, IOException {
        int numRows = 0;
        runner.setSchema(SCHEMA);
        if (scriptsDirectory != null) {
            numRows += runner.run(scriptsDirectory);
        } else {
            /*
             * Use the scripts embedded in the JAR file. We log this operarion only (not
             * the other case where scripts are read from a directory) because this case
             * occurs typically implicitly, the first time a CRS has been requested. This
             * is the opposite of the other case which occurs as a result of explicit call.
             */
            final LogRecord log = Loggings.format(Level.INFO,
                    Loggings.Keys.CREATING_CACHED_EPSG_DATABASE_$1, ThreadedEpsgFactory.VERSION);
            log.setSourceMethodName("call");
            log.setSourceClassName(EpsgInstaller.class.getName());
            log.setLoggerName(ThreadedEpsgFactory.LOGGER.getName());
            ThreadedEpsgFactory.LOGGER.log(log);
            runner.splitMultirows = true;
            for (String script : EpsgScriptRunner.SCRIPTS) {
                script += ".sql";
                final InputStream in = EpsgScriptRunner.class.getResourceAsStream(script);
                if (in == null) {
                    throw new FileNotFoundException(Errors.format(
                            Errors.Keys.FILE_DOES_NOT_EXIST_$1, script));
                }
                numRows += runner.run(in);
                in.close();
            }
        }
        runner.close();
        return new Result(numRows);
    }

    /**
     * A simple data structure holding the result of an EPSG database creation.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    public static final class Result {
        /**
         * The number of row inserted.
         */
        public final int numRows;

        /**
         * Do not allow instantiation from outside this package.
         */
        Result(final int numRows) {
            this.numRows = numRows;
        }

        /**
         * Returns a string representation for debugging purpose.
         * This representation may change in any future version.
         */
        @Override
        public String toString() {
            return "Result[" + numRows + " rows]";
        }
    }
}
