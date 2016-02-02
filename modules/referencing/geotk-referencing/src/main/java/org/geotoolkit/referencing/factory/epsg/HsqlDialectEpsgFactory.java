/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.apache.sis.referencing.factory.sql.SQLTranslator;


/**
 * Adapts SQL statements for HSQL. The HSQL database engine doesn't understand the parenthesis
 * in {@code (INNER JOIN ... ON)} statements for the {@code "BursaWolfParameters"} query.
 * Unfortunately, those parenthesis are required by MS-Access. We need to removes them
 * programmatically here.
 *
 * @author Martin Desruisseaux (IRD)
 * @module
 */
final class HsqlDialectEpsgFactory extends SQLTranslator {
    /**
     * The regular expression pattern for searching the "FROM (" clause.
     * This is the pattern for the opening parenthesis.
     */
    private static final Pattern OPENING_PATTERN =
            Pattern.compile("\\s+FROM\\s*\\(",
            Pattern.CASE_INSENSITIVE);

    /**
     * Constructs an authority factory using the given metadata.
     */
    public HsqlDialectEpsgFactory(final DatabaseMetaData metadata) throws SQLException {
        super(metadata, null, null);
    }

    /**
     * If the query contains a {@code "FROM ("} expression, remove the parenthesis.
     *
     * @param  query The SQL statement to adapt.
     * @return The The adapted SQL statement, or {@code query} if no change was needed.
     */
    @Override
    public String apply(String query) {
        query = super.apply(query);
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
            query = new StringBuilder(query.length())
                    .append(query, 0,         opening)
                    .append(query, opening+1, closing)
                    .append(query, closing+1, query.length()).toString();
        }
        return query;
    }
}
