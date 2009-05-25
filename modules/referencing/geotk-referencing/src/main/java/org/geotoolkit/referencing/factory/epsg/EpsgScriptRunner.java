/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengis.referencing.FactoryException;

import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.internal.jdbc.ScriptRunner;


/**
 * Run SQL scripts for EPSG database on PostgreSQL, mySQL or Oracle. It can also
 * be used for other flavors (not officially supported by EPSG) like JavaDB.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class EpsgScriptRunner extends ScriptRunner {
    /**
     * The pattern for an instruction like:
     *
     * {@preformat sql
     *     UPDATE epsg_datum
     *     SET datum_name = replace(datum_name, CHAR(182), CHAR(10));
     * }
     */
    static final String REPLACE_STATEMENT =
            "\\s*UPDATE\\s+[\\w\\.\" ]+\\s+SET\\s+(\\w+)\\s*=\\s*replace\\s*\\(\\s*\\1\\W+.*";

    /**
     * {@code true} if the database supports the {@code "COMMIT"} instruction.
     */
    private final boolean supportsCommit;

    /**
     * {@code true} if the database supports schema.
     */
    private final boolean supportsSchemas;

    /**
     * Non-null if there is SQL statements to skip. This is the case of
     * {@code UPDATE ... SET x = REPLACE(x, ...)} functions, since Derby
     * does not supports the {@code REPLACE} function.
     */
    private final Matcher skip;

    /**
     * Creates a new runner which will execute the statements using the given connection.
     *
     * @param connection The connection to the database.
     * @throws SQLException If an error occured while executing a SQL statement.
     */
    public EpsgScriptRunner(final Connection connection) throws SQLException {
        super(connection);
        suffixes.add("Tables");
        suffixes.add("Data");
        suffixes.add("FKeys");
        suffixes.add("Indexes");
        /*
         * Checks for supported data type.
         */
        boolean supportsText = false;
        final DatabaseMetaData metadata = connection.getMetaData();
        ResultSet result = metadata.getTypeInfo();
        while (result.next()) {
            String type = result.getString("TYPE_NAME");
            if (type.equalsIgnoreCase("TEXT")) {
                supportsText = true;
                break;
            }
        }
        result.close();
        /*
         * Checks for supported functions.
         */
        boolean supportsReplace = false;
        final String functions = metadata.getStringFunctions();
        for (final StringTokenizer tk = new StringTokenizer(functions, ","); tk.hasMoreTokens();) {
            final String token = tk.nextToken().trim();
            if (token.equalsIgnoreCase("REPLACE")) {
                supportsReplace = true;
                break;
            }
        }
        if (supportsReplace) {
            skip = null;
        } else {
            skip = Pattern.compile(REPLACE_STATEMENT, Pattern.CASE_INSENSITIVE).matcher("");
        }
        /*
         * Some databases do not support the TEXT data type (for example JavaDB).
         * In such case (detected by the above loop), replace TEXT by VARCHAR with
         * an empirically determined size limit.
         */
        if (!supportsText) {
            replacements.put("TEXT", "VARCHAR(4000)");
        }
        switch (dialect) {
            /*
             * PostgreSQL expects the "CHAR" function to be actually spelled "CHR". We can not rely
             * on the metadata.getFunctions(...) method because it is not implemented in PostgreSQL
             * JDBC driver version 8.3-603.jdbc4, and metadata.getStringFunctions() wrongly reports
             * "char".
             */
            case POSTGRESQL: {
                replacements.put("CHAR", "CHR");
                supportsCommit = false;
                break;
            }
            /*
             * JavaDB requires that every columns with the "UNIQUE" constraint are explicitly
             * declared as "NOT NULL". Most columns are declared that way in the EPSG scripts
             * except the "coord_axis_code" column in the "epsg_coordinateaxis" table  (as of
             * EPSG database version 6.18).
             *
             * Note: replacing systematically "UNIQUE" by "UNIQUE NOT NULL" cause a duplication
             * of the "NOT NULL" part when it was already included in the EPSG script.  However
             * JavaDB seems to accept this redundancy.
             */
            case DERBY: {
                replacements.put("UNIQUE", "UNIQUE NOT NULL");
                supportsCommit = false;
                break;
            }
            default: {
                supportsCommit = true;
                break;
            }
        }
        supportsSchemas = metadata.supportsSchemasInTableDefinitions() &&
                          metadata.supportsSchemasInDataManipulation();
    }

    /**
     * Returns {@code true} if the file of the given name is an EPSG SQL script.
     * In addition of the conditions documented in the super-class, this method
     * requires that the name starts with {@code "EPSG"}.
     */
    @Override
    public boolean accept(final File directory, final String name) {
        return super.accept(directory, name) && name.startsWith("EPSG");
    }

    /**
     * Sets the schema and replaces the {@code "epsg_"} prefix in table names by the
     * MS-Access table name in the given schema. Invoking this method create the schema
     * immediately in the database.
     * <p>
     * This method should be invoked only once. It does nothing if the database does not
     * supports schema.
     *
     * @param schema The schema (usually {@code "epsg"}).
     * @throws SQLException If the schema can not be created.
     */
    public void setSchema(final String schema) throws SQLException {
        if (!supportsSchemas) {
            return;
        }
        /*
         * Creates the schema on the database. We do that before to setup
         * the 'toSchema' map, while the map still null.
         */
        execute(new StringBuilder("CREATE SCHEMA ").append(schema));
        /*
         * Setup the map which will be used for renaming the table names.
         */
        final StringBuilder buffer = new StringBuilder(schema).append('.').append(identifierQuote);
        final int base = buffer.length();
        final String[] toANSI = AnsiDialectEpsgFactory.ACCESS_TO_ANSI;
        for (int i=0; i<toANSI.length;) {
            String access = toANSI[i++];
            String ansi   = toANSI[i++];
            if (!ansi.startsWith(AnsiDialectEpsgFactory.TABLE_PREFIX)) {
                continue;
            }
            /*
             * Gets the MS-Access name without the brackets with buffer.append(access, 1, length-2).
             * Puts that name into a schema."name" form.
             */
            buffer.setLength(base);
            assert access.charAt(0) == '[' && access.charAt(access.length()-1) == ']' : access;
            final String replacement = buffer.append(access, 1, access.length()-1).append(identifierQuote).toString();
            if (replacements.put(ansi, replacement) != null) {
                throw new AssertionError(ansi);
            }
        }
    }

    /**
     * Modifies the SQL statement before to execute it, or ommit unsupported statements.
     *
     * @throws SQLException If an error occured while executing the SQL statement.
     */
    @Override
    protected int execute(final StringBuilder sql) throws SQLException {
        if (!supportsCommit) {
            if (StringUtilities.equalsIgnoreCase("COMMIT", sql)) {
                return 0;
            }
        }
        if (skip != null) {
            if (skip.reset(sql).matches()) {
                return 0;
            }
        }
        return super.execute(sql);
    }

    /**
     * Runs the EPSG scripts from the command lines. This method expect a maximum of 4 arguments:
     * <p>
     * <ol>
     *   <li>The directory which contains the EPSG scripts (mandatory).</li>
     *   <li>The JDBC URL to the database. If omitted, a default URL to a JavaDB database
     *       will be used. This default URL will point toward the Geotoolkit configuration
     *       directory, which is platform-dependent ({@code ".geotoolkit" on Linux).</li>
     *   <li>The user for the database connection (optional).</li>
     *   <li>The password for the database connection. Mandatory if a user has been specified.</li>
     * </ol>
     *
     * @param  args The command line arguments.
     * @throws FactoryException If an error occured while running the scripts.
     */
    public static void main(String[] args) throws FactoryException {
        String url;
        if (args.length >= 2) {
            url = args[1].trim();
        } else {
            url = ThreadedEpsgFactory.getDefaultURL();
        }
        final boolean isJavadb = url.startsWith("jdbc:derby:");
        if (isJavadb) {
            url += ";create=true";
        }
        Exception failure;
        EpsgScriptRunner runner = null;
        try {
            final Connection connection;
            switch (args.length) {
                case 1: // fall through
                case 2: connection = DriverManager.getConnection(url); break;
                case 4: connection = DriverManager.getConnection(url, args[2], args[3]); break;
                default: {
                    final PrintStream out = System.out;
                    out.println("Expected arguments: DIRECTORY [URL] [USER] [PASSWORD]");
                    out.println("  where DIRECTORY is the path to SQL scripts");
                    out.println("  and URL specifies the JDBC connection to the database.");
                    return;
                }
            }
            try {
                runner = new EpsgScriptRunner(connection);
                runner.setEncoding("ISO-8859-1");
                runner.setSchema("epsg");
                runner.run(new File(args[0]));
                runner.close();
                if (isJavadb) try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException e) {
                    // This is the expected exception.
                }
                return;
            } finally {
                connection.close();
            }
        } catch (SQLException e) {
            failure = e;
        } catch (IOException e) {
            failure = e;
        }
        String message = failure.getLocalizedMessage();
        if (runner != null) {
            message = message + '\n' + runner.getCurrentPosition();
        }
        throw new FactoryException(message, failure);
    }
}
