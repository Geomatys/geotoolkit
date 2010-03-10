/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotoolkit.util.Strings;
import org.geotoolkit.internal.sql.ScriptRunner;


/**
 * Run SQL scripts for EPSG database on PostgreSQL, mySQL or Oracle. It can also
 * be used for other flavors (not officially supported by EPSG) like JavaDB.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
final class EpsgScriptRunner extends ScriptRunner {
    /**
     * The embedded SQL scripts to execute for creating the EPSG database, in that order.
     * The {@code ".sql"} suffix is omitted.
     */
    static final String[] SCRIPTS = {
        "Tables", "Data", "FKeys", "Indexes"
    };

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
     * The maximum number of rows per {@code INSERT} statement. Should be modified only when
     * reading the files modified by the {@code geotk-epsg-pack} module, otherwise we assume
     * that the user know what he is doing. This is used because attempts to insert too many
     * rows with a single statement on Derby database cause a {@link StackTraceOverflow}.
     */
    private int maxRowsPerInsert;

    /**
     * {@code true} if the Pilcrow character (¶ - decimal code 182) should be replaced by
     * Line Feed (LF - decimal code 10). This is a possible workaround when the database
     * does not support the {@code REPLACE(column, CHAR(182), CHAR(10))} SQL statement,
     * but accepts LF.
     */
    private final boolean replaceParagraphs;

    /**
     * Non-null if there is SQL statements to skip. This is the case of
     * {@code UPDATE ... SET x = REPLACE(x, ...)} functions, since Derby
     * does not supports the {@code REPLACE} function.
     */
    private final Matcher skip;

    /**
     * Creates a new runner which will execute the statements using the given connection.
     * The encoding default to {@code "ISO-8859-1"}, which is the encoding used for the
     * files provided by EPSG.
     *
     * @param connection The connection to the database.
     * @throws SQLException If an error occured while executing a SQL statement.
     */
    public EpsgScriptRunner(final Connection connection) throws SQLException {
        super(connection);
        setEncoding("ISO-8859-1");
        for (final String script : SCRIPTS) {
            suffixes.add(script);
        }
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
        replaceParagraphs = false; // Never supported for now.
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
             * HSQLDB doesn't seem to support the {@code UNIQUE} keyword in {@code CREATE TABLE}
             * statements. In addition, we must declare explicitly that we want the tables to be
             * cached on disk.
             */
            case HSQL: {
                replacements.put("CREATE TABLE", "CREATE CACHED TABLE");
                replacements.put("UNIQUE", "");
                supportsCommit = true;
                maxRowsPerInsert = 1;
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
        // Note: the same condition is also coded in EpsgInstaller.getSchema(...).
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
     * @throws IOException If an I/O operation was required and failed.
     */
    public void setSchema(final String schema) throws SQLException, IOException {
        if (!supportsSchemas) {
            return;
        }
        /*
         * Creates the schema on the database. We do that before to setup
         * the 'toSchema' map, while the map still null.
         *
         * Note that we don't quote the schema name, which is a somewhat arbitrary choice.
         * If we choose to quote them in some future version, then we need to update
         * EmbeddedDataSource.createIfEmpty(Connection).
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
     * Set the maximum number of rows per {@code INSERT} statement, provided that it was
     * not already set. This method can be invoked only once. It does nothing if the value
     * has already been set (for example by the constructor).
     */
    final void setMaxRowsPerInsert(final int max) {
        if (maxRowsPerInsert == 0) {
            maxRowsPerInsert = max;
        }
    }

    /**
     * Modifies the SQL statement before to execute it, or ommit unsupported statements.
     *
     * @throws SQLException If an error occured while executing the SQL statement.
     * @throws IOException If an I/O operation was required and failed.
     */
    @Override
    protected int execute(final StringBuilder sql) throws SQLException, IOException {
        if (!supportsCommit) {
            if (Strings.equalsIgnoreCase("COMMIT", sql)) {
                return 0;
            }
        }
        if (skip != null) {
            if (skip.reset(sql).matches()) {
                return 0;
            }
        }
        if (replaceParagraphs) {
            Strings.replace(sql, "\u00B6", "\n");
        }
        if (maxRowsPerInsert != 0 && Strings.startsWith(sql, "INSERT INTO", true)) {
            /*
             * The following code is very specific to the syntax of the scripts generated
             * by the geotk-epsg-pack module. It is executed only when running the scripts
             * embedded in the geotk-epsg module.
             */
            int position = sql.indexOf("\n");
            if (position >= 0) {
                /*
                 * Fetch the "INSERT INTO" part, which is expected to be on its own line.
                 * We will left this part of the buffer unchanged, and write only after
                 * the offset.
                 */
                final StringBuilder buffer = new StringBuilder(sql.substring(0, position)).append(' ');
                final int offset = buffer.length();
                int begin = position + 1;
                int count = 0;
                int nrows = maxRowsPerInsert;
                while ((position = sql.indexOf("\n", ++position)) >= 0) {
                    if (--nrows != 0 || position == begin) {
                        /*
                         * Continue to extract lines until we have reached the 'maxRowsPerInsert'
                         * amount. Also continue if we still have no line at all (position == begin).
                         */
                        continue;
                    }
                    int end = position;
                    if (sql.charAt(end-1) == ',') {
                        end--;
                    }
                    count += super.execute(buffer.append(sql.substring(begin, end)));
                    /*
                     * Prepare for inspecting next lines.
                     */
                    buffer.setLength(offset);
                    nrows = maxRowsPerInsert;
                    begin = position + 1;
                }
                // Executes the last statement.
                count += super.execute(buffer.append(sql.substring(begin)));
                return count;
            }
        }
        return super.execute(sql);
    }
}
