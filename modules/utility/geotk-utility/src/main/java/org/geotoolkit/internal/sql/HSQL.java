/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Convenience methods related to the HSQL database.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from 2.2)
 * @module
 */
public final class HSQL {
    /**
     * The classname of the driver.
     */
    public static final String DRIVER_CLASS = "org.hsqldb.jdbcDriver";

    /**
     * The prefix of URL for HSQL databases.
     */
    static final String PROTOCOL = "jdbc:hsqldb:";

    /**
     * The regular expression pattern for searching the "FROM (" clause.
     * This is the pattern for the opening parenthesis.
     */
    private static final Pattern OPENING_PATTERN =
            Pattern.compile("\\s+FROM\\s*\\(",
            Pattern.CASE_INSENSITIVE);

    /**
     * Do not allow instantiation of this class.
     */
    private HSQL() {
    }

    /**
     * Constructs the full path to a HSQL database in the given directory. The
     * {@linkplain File#getName() name} of the given {@code path} shall be the
     * database name (without extension), and the {@linkplain File#getParentFile() parent}
     * of the {@code path} shall be the directory where the database is saved.
     *
     * {@note We do not use <code>File.toURI()</code> because HSQL doesn't seem to
     *        expect an encoded URL (e.g. <code>"%20"</code> instead of spaces).}
     *
     * @param  path The path (without extension) to the database.
     * @return The URL.
     */
    public static String createURL(final File path) {
        final StringBuilder url = new StringBuilder(PROTOCOL).append("file:");
        final String p = path.getAbsolutePath().replace(File.separatorChar, '/');
        if (!p.startsWith("/")) {
            url.append('/');
        }
        return url.append(p).toString();
    }

    /**
     * Given a database URL, gets the path to the database.
     * This is the converse of {@link #createURL(File)}.
     *
     * @param  databaseURL The database URL.
     * @return The path, or {@code null} if the given URL is not a HSQL URL.
     */
    public static File getFile(final String databaseURL) {
        int offset = PROTOCOL.length();
        if (databaseURL != null && databaseURL.regionMatches(true, 0, PROTOCOL, 0, offset)) {
            final int s = databaseURL.indexOf(':', offset);
            if (s >= 0) {
                final String p = databaseURL.substring(offset, s);
                if (p.equalsIgnoreCase("mem")) {
                    return null;
                }
                if (p.equalsIgnoreCase("file")) {
                    offset = s + 1;
                }
            }
            return new File(databaseURL.substring(offset));
        }
        return null;
    }

    /**
     * Makes the given database read-only. The {@code path} argument is the same one than
     * the one given to the {@link #createURL(File)} method.
     *
     * @param  path The path (without extension) to the database.
     * @throws IOException If an error occurred while reading or writing the property file.
     */
    public static void setReadOnly(final File path) throws IOException {
        final File file = new File(path.getParentFile(), path.getName() + ".properties");
        final InputStream propertyIn = new FileInputStream(file);
        final Properties properties = new Properties();
        properties.load(propertyIn);
        propertyIn.close();
        if (!"true".equals(properties.put("readonly", "true"))) {
            final OutputStream out = new FileOutputStream(file);
            properties.store(out, "HSQL database configuration");
            out.close();
        }
    }

    /**
     * If the query contains a {@code "FROM ("} expression, remove the parenthesis.
     *
     * @param  query The SQL statement to adapt.
     * @return The The adapted SQL statement, or {@code query} if no change was needed.
     */
    public static String adaptSQL(String query) {
        final Matcher matcher = OPENING_PATTERN.matcher(query);
        if (matcher.find()) {
            final int opening = matcher.end()-1;
            final int length  = query.length();
            int closing = opening;
            for (int count=0; ; closing++) {
                if (closing >= length) {
                    // Should never happen with well formed SQL statement.
                    // If it happen anyway, don't change anything and let
                    // the HSQL driver produces a "syntax error" message.
                    return query;
                }
                switch (query.charAt(closing)) {
                    case '(': count++; break;
                    case ')': count--; break;
                    default : continue;
                }
                if (count == 0) {
                    break;
                }
            }
            query = query.substring(0,         opening) +
                    query.substring(opening+1, closing) +
                    query.substring(closing+1);
        }
        return query;
    }

    /**
     * Shutdown the database. This method does not close the given connection, as this
     * is usually not needed and should be done by the caller if he really want to do so.
     *
     * @param  connection The connection to use for shutting down the database.
     * @throws SQLException
     */
    public static void shutdown(final Connection connection) throws SQLException {
        final Statement stmt = connection.createStatement();
        stmt.execute("SHUTDOWN");
        stmt.close();
    }
}
