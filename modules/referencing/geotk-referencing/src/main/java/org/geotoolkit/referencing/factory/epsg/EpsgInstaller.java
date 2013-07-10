/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTransientException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.concurrent.Callable;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.internal.sql.Dialect;
import org.apache.sis.util.NullArgumentException;

import static org.geotoolkit.internal.referencing.CRSUtilities.EPSG_VERSION;


/**
 * Installs the EPSG database. By default this class performs the following operations:
 * <p>
 * <ol>
 *   <li>Gets the {@linkplain ThreadedEpsgFactory#getDefaultURL() default URL} to the EPSG
 *       database on the local machine. This database doesn't need to exist.</li>
 *   <li>Gets a connection to that database, which is assumed empty.</li>
 *   <li>Executes the SQL scripts embedded in the
 *       <a href="http://www.geotoolkit.org/modules/referencing/geotk-referencing/index.html">geotk-epsg</a>
 *       module. Those scripts are derived from the ones distributed on the
 *       <a href="http://www.epsg.org">www.epsg.org</a> web site.</li>
 * </ol>
 * <p>
 * This default behavior can be changed by the setter methods defined in this class.
 * They allow to use a different set of scripts, or to execute them on a different
 * database (for example <a href="http://www.postgresql.org">PostgreSQL</a>).
 * <p>
 * The scripts are read and the database is created only when the {@link #call()} method
 * is invoked. This method can be run in a background thread by an
 * {@linkplain java.util.concurrent.ExecutorService executor service}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
public class EpsgInstaller implements Callable<EpsgInstaller.Result> {
    /**
     * The schema where the installer will create the tables in the JavaDB database.
     * The value is {@value}. When the {@code geotk-epsg.jar} file is reachable on
     * the classpath, the referencing module will look for this schema in order to
     * decide if it needs to install the EPSG database or not.
     *
     * @see #setSchema(String)
     */
    public static final String DEFAULT_SCHEMA = "epsg";

    /**
     * The schema where to put the tables, or {@code null} if none.
     * The default value is {@value #DEFAULT_SCHEMA}.
     */
    private String schema = DEFAULT_SCHEMA;

    /**
     * The directory which contain the EPSG scripts. If {@code null}, then the scripts
     * will be read from the {@code geotk-epsg.jar} file. If this JAR is not reachable,
     * then an exception will be thrown.
     */
    private File scriptsDirectory;

    /**
     * The JDBC URL to the database. If {@code null}, a default URL to a JavaDB database on the
     * local machine will be used. This default URL will point toward the Geotk configuration
     * directory, which is platform-dependent ({@code ".geotoolkit"} on Linux).
     */
    private String databaseURL;

    /**
     * The user for the database connection (optional).
     */
    private String user;

    /**
     * The password for the database connection. Ignored if the {@linkplain #user} is {@code null}.
     */
    private String password;

    /**
     * A connection given explicitly by the caller, or {@code null} for using the
     * above URL, user name and password instead.
     */
    private Connection userConnection;

    /**
     * Creates a new installer. By default, the scripts will be read from the {@code geotk-epsg.jar}
     * file and the target database is determined by the {@linkplain ThreadedEpsgFactory#getDefaultURL()
     * default URL} (typically a local database using JavaDB).
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
     *   <li>Optional but recommended: {@code EPSG_v6_18.mdb_Indexes_PostgreSQL.sql} using
     *       a copy of the script embedded in the {@code geotk-epsg.jar} file.</li>
     * </ul>
     * <p>
     * The suffix may be different (for example {@code "_MySQL.sql"} instead of
     * {@code "_PostgreSQL.sql"}) and the version number may be different. If the directory
     * contrains the scripts for different versions of the database, then the scripts for
     * the latest version are used.
     * <p>
     * If this method is never invoked or if the given directory is {@code null}, then the
     * scripts will be read from the {@code geotk-epsg.jar} file. If that JAR is not reachable,
     * then an exception will be thrown at {@link #call()} invocation time.
     *
     * @param directory The directory of the EPSG SQL scripts to execute, or {@code null}
     */
    public synchronized void setScriptDirectory(final File directory) {
        scriptsDirectory = directory;
    }

    /**
     * Sets the URL to the database, using the JDBC syntax. If this method is never invoked or
     * if the given URL is {@code null}, then the {@linkplain ThreadedEpsgFactory#getDefaultURL()
     * default URL} is used. If this method is invoked with any URL different than the default one,
     * then a {@linkplain ThreadedEpsgFactory#CONFIGURATION_FILE configuration file} will need to
     * be provided explicitly after the database creation in order to get the referencing module
     * to use that database.
     * <p>
     * If the given scripts are aimed to be executed verbatism, then the {@link #setSchema(String)}
     * method should be invoked with a {@code null} argument in order to prevent this installer to
     * move the tables in the {@value #DEFAULT_SCHEMA} schema.
     * <p>
     * If the given scripts were downloaded from
     * <a href="http://www.epsg.org">www.epsg.org</a>, then consider adding the
     * <a href="http://hg.geotoolkit.org/geotoolkit/files/tip/modules/referencing/geotk-epsg/src/main/resources/org/geotoolkit/referencing/factory/epsg/Indexes.sql">Indexes.sql</a>
     * file in the scripts directory, for performance reasons.
     * <p>
     * If a user and password were previously defined, they are left unchanged.
     *
     * @param url The URL to the database, or {@code null} for JavaDB on the local machine.
     */
    public synchronized void setDatabase(final String url) {
        databaseURL = url;
        userConnection = null;
    }

    /**
     * Sets the URL to the database, together with the user and password. The URL to
     * the database is handled in the same way than {@link #setDatabase(String)}.
     *
     * @param url The URL to the database, or {@code null} for JavaDB on the local machine.
     * @param user The user, or {@code null} if none.
     * @param password The password, or {@code null} if none.
     */
    public synchronized void setDatabase(final String url, final String user, final String password) {
        this.databaseURL = url;
        this.user        = user;
        this.password    = password;
        userConnection = null;
    }

    /**
     * Sets the connection to the database. This method is exclusive with the other
     * {@code setMetadata} methods, in that invoking this method resets the database
     * URL and user name to values fetched from the given connection.
     * <p>
     * It is the caller's responsibility to close the given connection when it is
     * no longer needed.
     *
     * @param  connection   The connection to use for installing the EPSG database.
     * @throws SQLException If the given connection can not be used.
     *
     * @since 3.11
     */
    public synchronized void setDatabase(final Connection connection) throws SQLException {
        if (connection == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_1, "connection"));
        }
        final DatabaseMetaData metadata = connection.getMetaData();
        databaseURL    = metadata.getURL();
        user           = metadata.getUserName();
        password       = null;
        userConnection = connection;
    }

    /**
     * Sets the schema where the installer will create the tables. The default value is
     * {@value #DEFAULT_SCHEMA}. If a different schema is specified, then consider providing
     * explicitly a {@linkplain ThreadedEpsgFactory#CONFIGURATION_FILE configuration file}
     * after the database creation.
     *
     * @param schema The schema where to create the tables, or {@code null} or an empty string if none.
     */
    public synchronized void setSchema(String schema) {
        if (schema != null) {
            schema = schema.trim();
            if (schema.isEmpty()) {
                schema = null;
            }
        }
        this.schema = schema;
    }

    /**
     * Returns the schema where are expected to be the EPSG tables. This is the value defined
     * by the last call to {@link #setSchema(String)}, converted to lower or upper cases
     * depending how the underlying database stores unquoted identifier.
     * <p>
     * The EPSG schema is unquoted on purpose, because the "EPSG" characters are not in mixed
     * cases ("epsg" is good as well) and we want to keep some identifiers in their "natural"
     * form for the underlying database (so the user see the case he is used to, and there is
     * less quotes to type). This is different from the table names where there is mixed case,
     * and the table names are more difficult to read if we don't preserve that case.
     *
     * @param  md The database metadata, used for determining the identifier case.
     * @return The schema name, or {@code ""} if there is none.
     * @throws SQLException If an error occurred while querying metadata.
     *
     * @since 3.05
     */
    private String getSchema(final DatabaseMetaData md) throws SQLException {
        // Note: the same condition is coded in EpsgScriptRunner constructor.
        if (!md.supportsSchemasInTableDefinitions() || !md.supportsSchemasInDataManipulation()) {
            return null;
        }
        String sc = schema;
        if (sc == null) {
            sc = "";
        } else if (md.storesUpperCaseIdentifiers()) {
            sc = sc.toUpperCase(Locale.CANADA);
        } else if (md.storesLowerCaseIdentifiers()) {
            sc = sc.toLowerCase(Locale.CANADA);
        }
        return sc;
    }

    /**
     * Returns the connection to the database. It is caller's responsibility to close
     * this connection.
     *
     * @param  create {@code true} if this method should create the database directory (JavaDB only).
     * @return The connection to the database.
     * @throws IOException If the default URL is used but we failed to create the destination directory.
     * @throws SQLException If the connection can not be obtained because of a JDBC error.
     *
     * @since 3.05
     */
    private Connection getConnection(final boolean create) throws IOException, SQLException {
        if (databaseURL == null) {
            databaseURL = ThreadedEpsgFactory.getDefaultURL(create);
        }
        final Connection connection;
        if (user == null) {
            connection = DriverManager.getConnection(databaseURL);
        } else {
            connection = DriverManager.getConnection(databaseURL, user, password);
        }
        return connection;
    }

    /**
     * Closes the given connection and shutdowns the given database. The shutdown process is
     * specific to the Derby and HSQL databases. In the particular case of HSQL, the database
     * is set to "read only" mode (this setting can be applied only after shutdown).
     *
     * @see org.geotoolkit.internal.sql.DefaultDataSource#shutdown()
     */
    private static void shutdown(final Connection connection, final String databaseURL) throws SQLException {
        final Dialect dialect = Dialect.forURL(databaseURL);
        if (dialect != null) {
            dialect.shutdown(connection, databaseURL, true);
        } else {
            connection.close();
        }
    }

    /**
     * Verifies if the database exists. This method does not verify if the database content
     * is consistent. It merely tests if the database contains at least one table in the
     * EPSG schema.
     *
     * @return {@code true} if the database exists, or {@code false} otherwise.
     * @throws FactoryException If an error occurred while querying the database.
     *
     * @since 3.05
     */
    public synchronized boolean exists() throws FactoryException {
        boolean connected = false;
        Exception failure;
        try (Connection connection = getConnection(false)) {
            connected = true;
            final DatabaseMetaData md = connection.getMetaData();
            return AnsiDialectEpsgFactory.exists(md, getSchema(md));
        } catch (IOException | SQLTransientException e) {
            failure = e;
        } catch (SQLException e) {
            /*
             * The JavaDB SQL state for a database not found in XJ004, but this is specific
             * to JavaDB (the standard SQL states are in the 0-4 and A-H ranges), so we can
             * not rely on that. For now we assume that any non-transient failure to get the
             * connection means that the database does not exit.
             */
            if (!connected) {
                return false;
            }
            failure = e;
        }
        throw new FactoryException(failure.getLocalizedMessage(), failure);
    }

    /**
     * Processes to the creation of the EPSG database.
     *
     * @return The result of the EPSG database creation.
     * @throws FactoryException If an error occurred while running the scripts.
     */
    @Override
    public synchronized Result call() throws FactoryException {
        Exception failure;
        EpsgScriptRunner runner = null;
        try {
            Connection connection = userConnection;
            if (connection == null) {
                connection = getConnection(true);
            }
            /*
             * Now execute the script using the given connection, either looking for scripts
             * in the given directory or looking for scripts embedded in the JAR file.
             */
            try {
                runner = new EpsgScriptRunner(connection);
                return call(runner);
            } finally {
                if (connection != userConnection) {
                    shutdown(connection, databaseURL);
                }
            }
        } catch (SQLException | IOException e) {
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
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    final synchronized Result call(final EpsgScriptRunner runner) throws SQLException, IOException {
        final long start = System.currentTimeMillis();
        int numRows = 0;
        if (schema != null) {
            runner.setSchema(schema);
        }
        if (scriptsDirectory != null) {
            numRows += runner.run(scriptsDirectory);
        } else {
            /*
             * Use the scripts embedded in the JAR file. We log this operation only (not
             * the other case where scripts are read from a directory) because this case
             * occurs typically implicitly, the first time a CRS has been requested. This
             * is the opposite of the other case which occurs as a result of explicit call.
             */
            final LogRecord log = Loggings.format(Level.INFO,
                    Loggings.Keys.CREATING_CACHED_EPSG_DATABASE_1, EPSG_VERSION);
            log.setSourceMethodName("call");
            log.setSourceClassName(EpsgInstaller.class.getName());
            log.setLoggerName(ThreadedEpsgFactory.LOGGER.getName());
            ThreadedEpsgFactory.LOGGER.log(log);
            for (String script : runner.getScriptFiles()) {
                script += ".sql";
                final InputStream in = EpsgScriptRunner.class.getResourceAsStream(script);
                if (in == null) {
                    throw new FileNotFoundException(Errors.format(
                            Errors.Keys.FILE_DOES_NOT_EXIST_1, script));
                }
                numRows += runner.run(in);
                // The stream will be closed by the run method.
            }
        }
        runner.close(userConnection == null);
        return new Result(numRows, System.currentTimeMillis() - start);
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
         * The elapsed time, in milliseconds.
         */
        public final long elapsedTime;

        /**
         * Do not allow instantiation from outside this package.
         */
        Result(final int numRows, final long time) {
            this.numRows = numRows;
            this.elapsedTime = time;
        }

        /**
         * Returns a string representation of this result.
         * This representation may change in any future version.
         */
        @Override
        public String toString() {
            return Descriptions.format(Descriptions.Keys.INSERTED_ROWS_2, numRows, elapsedTime/1000.0);
        }
    }
}
