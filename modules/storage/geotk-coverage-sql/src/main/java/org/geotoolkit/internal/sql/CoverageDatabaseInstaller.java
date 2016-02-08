/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.opengis.util.FactoryException;

import org.apache.sis.util.CharSequences;
import org.apache.sis.util.StringBuilders;
import org.geotoolkit.internal.io.Host;
import org.geotoolkit.coverage.sql.CoverageDatabase;
import org.apache.sis.referencing.factory.sql.EPSGFactory;


/**
 * Runs the Coverage database installation scripts on a given database.
 * This runner assumes that the file encoding is {@code "UTF-8"}.
 * <p>
 * Callers should set the public fields declared in this class before to
 * invoke {@link #install()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.11
 * @module
 */
public class CoverageDatabaseInstaller extends ScriptRunner {
    /**
     * The default login used for read/write operations.
     */
    public static final String ADMINISTRATOR = "geoadmin";

    /**
     * The default login used for read only operations.
     */
    public static final String USER = "geouser";

    /**
     * The default coverages schema.
     */
    public static final String SCHEMA = "coverages";

    /**
     * The default metadata schema.
     *
     * @since 3.14
     */
    public static final String METADATA_SCHEMA = "metadata";

    /**
     * The enums created by the SQL scripts. They will need to be erased from the SQL
     * scripts before {@linkplain #execute execution} if the database doesn't support
     * enums.
     */
    private static final String[] ENUMS = {
        "\"PackMode\"",
        "\"MI_TransferFunctionTypeCode\""
    };

    /**
     * Whatever enums are supported. Enums are not a standard feature;
     * consequently they are supported only for a few specific databases.
     *
     * @since 3.14
     */
    private final boolean supportsEnumType;

    /**
     * {@code true} if the {@code CREATE TRUSTED PROCEDURAL LANGUAGE 'plpgsql'}
     * instruction needs to be executed.
     *
     * @since 3.16
     */
    private final boolean needsCreateLanguage;

    /**
     * {@code true} if the "geoadmin" and "geouser" roles should be created.
     */
    public boolean createRoles;

    /**
     * {@code true} if the EPSG database should be copied.
     */
    public boolean createEPSG;

    /**
     * The directory of PostGIS installation scripts, or {@code null} if none.
     */
    public File postgisDir;

    /**
     * The coverages schema.
     */
    public String schema;

    /**
     * The name of the administrator role. This role should have read and write access.
     */
    public String admin;

    /**
     * The name of the user role. This role should have only read access.
     */
    public String user;

    /**
     * The runner under execution, or {@code null} for this runner. Used in order
     * to get information about the SQL instruction that failed.
     */
    private transient ScriptRunner runner;

    /**
     * Creates a new runner which will execute the statements using the given connection.
     *
     * @param connection The connection to the database.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    public CoverageDatabaseInstaller(final Connection connection) throws SQLException {
        super(connection);
        if (dialect != Dialect.POSTGRESQL) {
            throw new UnsupportedOperationException(dialect.toString());
        }
        final DatabaseMetaData metadata = connection.getMetaData();
        needsCreateLanguage = dialect.needsCreateLanguage(metadata);
        supportsEnumType    = dialect.supportsEnumType(metadata);
        setEncoding("UTF-8");
    }

    /**
     * Invoked after each step has been performed. The values range from 0 to 100 inclusive.
     * This is only a very approximative information. The default implementation does nothing.
     *
     * @param percent The progress as a value from 0 to 100 inclusive.
     * @param schema The name of the schema about to be created, or {@code null}.
     */
    protected void progress(int percent, String schema) {
    }

    /**
     * If the given value is null or empty, returns the default value.
     * Otherwise if the given value is different than the default value, quote it.
     */
    private static String ensureNonNull(String value, String defaultValue, final String quote) {
        if (value == null || (value = value.trim()).isEmpty()) {
            value = defaultValue;
        } else if (!value.equals(defaultValue)) {
            value = quote + value + quote;
        }
        return value;
    }

    /**
     * Starts the installation. In case of failure, use {@link #toString()} for information
     * about the line which caused the error.
     *
     * @return The number of rows added or modified as a result of the script execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     * @throws FactoryException If an error occurred during the installation of the EPSG database.
     */
    public int install() throws IOException, SQLException, FactoryException {
        final DatabaseMetaData md = getConnection().getMetaData();
        final String quote = md.getIdentifierQuoteString();
        user   = ensureNonNull(user,   USER,          quote);
        admin  = ensureNonNull(admin,  ADMINISTRATOR, quote);
        schema = ensureNonNull(schema, SCHEMA,        quote);
        /*
         * Creates users and language.
         */
        progress(0, null);
        int n = runFile("prepare.sql");
        /*
         * Creates the postgis schema.
         */
        if (postgisDir != null) {
            progress(5, PostgisInstaller.DEFAULT_SCHEMA);
            final PostgisInstaller postgis = new PostgisInstaller(getConnection());
            runner = postgis;
            n += postgis.run(postgisDir);
            postgis.close(false); // Close the statement, not the connection.
            progress(30, PostgisInstaller.DEFAULT_SCHEMA);
            n += runFile("postgis-update.sql");
            runner = null;
        }
        /*
         * Creates the epsg schema.
         */
        if (createEPSG) {
            progress(40, "epsg");
            final EPSGFactory installer = new EPSGFactory(null);    // TODO: specify the DataSource.
            installer.install(getConnection());
        }
        /*
         * Creates the metadata schema.
         */
        progress(75, METADATA_SCHEMA);
        n += runFile("metadata-create.sql");
        /*
         * Creates the coverages schema.
         */
        progress(80, SCHEMA);
        n += runFile("coverages-create.sql");
        if (dialect == Dialect.POSTGRESQL) {
            String database = new Host(md.getURL()).path;
            if (database != null) {
                n = run("ALTER DATABASE " + quote + database + quote + " SET search_path=public, " +
                        schema + ", " + METADATA_SCHEMA + ", " + PostgisInstaller.DEFAULT_SCHEMA + END_OF_STATEMENT + '\n' +
                        "COMMENT ON DATABASE " + quote + database + quote + " IS 'Geotoolkit.org source of coverages.'" + END_OF_STATEMENT);
            }
        }
        progress(100, null);
        return n;
    }

    /**
     * Runs the given resource file.
     *
     * @param  file The resource file to run.
     * @return The number of rows added or modified as a result of the script execution.
     * @throws IOException If an error occurred while reading the input.
     * @throws SQLException If an error occurred while executing a SQL statement.
     */
    private int runFile(final String file) throws IOException, SQLException {
        runner = null;
        final InputStream in = CoverageDatabase.class.getResourceAsStream(file);
        if (in == null) {
            throw new FileNotFoundException(file);
        }
        return run(in);
        // The stream will be closed by the run method.
    }

    /**
     * Executes the given SQL statement. This method may modify the SQL script if enums, roles
     * or languages should not be created. Note that this method is invoked only for the scripts
     * provided in the {@code geotk-coverage-sql} package; the PostGIS and EPSG scripts use their
     * one installer and consequently are unaffected by the changes performed by this method.
     *
     * @param  sql The SQL statement to execute.
     * @return The number of rows added or modified as a result of the statement execution.
     * @throws SQLException If an error occurred while executing the SQL statement.
     * @throws IOException If an I/O operation was required and failed.
     */
    @Override
    protected int execute(final StringBuilder sql) throws SQLException, IOException {
        final CreateStatementType create = CreateStatementType.fromSQL(sql);
        if (create != null) switch (create) {
            case ROLE:     if (!createRoles)    return 0; else break;
            case LANGUAGE: if (!needsCreateLanguage) return 0; else break;
            case CAST:     // Fall through
            case ENUM:     if (!supportsEnumType)   return 0; else break;
            case TABLE: {
                if (!supportsEnumType) {
                    for (final String e : ENUMS) {
                        StringBuilders.replace(sql, e, "varchar");
                    }
                }
                break;
            }
        }
        /*
         * We can't add comments on enum, since they were not created.
         */
        if (!supportsEnumType && CharSequences.regionMatches(sql, 0, "COMMENT ON TYPE")) {
            return 0;
        }
        StringBuilders.replace(sql, USER,          user);
        StringBuilders.replace(sql, ADMINISTRATOR, admin);
        StringBuilders.replace(sql, SCHEMA,        schema);
        return super.execute(sql);
    }

    /**
     * Returns the current position (current file and current line in that file). The main purpose
     * of this method is to provides informations on the position where an exception occurred.
     */
    @Override
    public String getCurrentPosition() {
        if (runner != null) {
            return runner.getCurrentPosition();
        }
        return super.getCurrentPosition();
    }

    /**
     * Returns a string representation of this runner for debugging purpose. This method
     * may be invoked after a {@link SQLException} occurred in order to determine the line
     * in the SQL script that caused the error.
     */
    @Override
    public String toString() {
        if (runner != null) {
            return runner.toString();
        }
        return super.toString();
    }
}
