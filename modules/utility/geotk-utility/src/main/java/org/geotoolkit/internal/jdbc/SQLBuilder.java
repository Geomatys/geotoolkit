/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;


/**
 * Utility methods for building SQL statements.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
public final class SQLBuilder {
    /**
     * The characters used for quoting identifiers, or an empty string if none.
     */
    private final String quote;

    /**
     * The string that can be used to escape wildcard characters. This is the
     * value returned by {@link DatabaseMetaData#getSearchStringEscape()}.
     */
    private final String escape;

    /**
     * The buffer where the SQL query is to be created.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Creates a new {@code SQLBuilder} initialized from the given database metadata.
     *
     * @param  metadata The database metadata.
     * @throws SQLException If an error occured while fetching the database metadata.
     */
    public SQLBuilder(final DatabaseMetaData metadata) throws SQLException {
        quote  = metadata.getIdentifierQuoteString();
        escape = metadata.getSearchStringEscape();
    }

    /**
     * Creates a new {@code SQLBuilder} initialized to the same metadata than the given builder.
     *
     * @param metadata The builder from which to copy metadata.
     */
    public SQLBuilder(final SQLBuilder metadata) {
        quote  = metadata.quote;
        escape = metadata.escape;
    }

    /**
     * Returns {@code true} if the builder is currently empty.
     *
     * @return {@code true} if the builder is empty.
     */
    public boolean isEmpty() {
        return buffer.length() == 0;
    }

    /**
     * Clears this builder and make it ready for creating a new SQL statement.
     *
     * @return This builder, for method call chaining.
     */
    public SQLBuilder clear() {
        buffer.setLength(0);
        return this;
    }

    /**
     * Appends the given character.
     *
     * @param  c The character to append.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder append(final char c) {
        buffer.append(c);
        return this;
    }

    /**
     * Appends the given text verbatism. The text should be SQL keywords
     * like {@code "SELECT * FROM"}.
     *
     * @param  keywords The keywords to append.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder append(final String keywords) {
        buffer.append(keywords);
        return this;
    }

    /**
     * Appends an identifier for an element in the given schema. The identifier will be put
     * between the quote characters. The schema will be put only if non-null.
     *
     * @param  schema The schema, or {@code null} if none.
     * @param  identifier The identifier to append.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder appendIdentifier(final String schema, final String identifier) {
        if (schema != null) {
            appendIdentifier(schema).append('.');
        }
        return appendIdentifier(identifier);
    }

    /**
     * Appends an identifier. The identifier will be put between the quote characters.
     *
     * @param  identifier The identifier to append.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder appendIdentifier(final String identifier) {
        buffer.append(quote).append(identifier).append(quote);
        return this;
    }

    /**
     * Appends a value in a {@code SELECT} statement. The {@code "="} string will
     * be inserted before the value.
     *
     * @param  value The value to append, or {@code null}.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder appendCondition(final Object value) {
        if (value == null) {
            buffer.append("IS NULL");
        } else if (value instanceof Number) {
            buffer.append('=').append(value);
        } else {
            buffer.append("='").append(doubleQuotes(value)).append('\'');
        }
        return this;
    }

    /**
     * Appends a value in an {@code INSERT} statement.
     *
     * @param  value The value to append, or {@code null}.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder appendValue(final Object value) {
        if (value == null) {
            buffer.append("NULL");
        } else if (value instanceof Number) {
            buffer.append(value);
        } else {
            buffer.append('\'').append(doubleQuotes(value)).append('\'');
        }
        return this;
    }

    /**
     * Appends a string as an escaped {@code LIKE} argument. This method does
     * not put any {@code '} character, and does not accept null argument.
     * <p>
     * This method does not double the simple quotes of the given string on intend, because
     * it may be used in a {@code PreparedStatement}. If the simple quotes need to be doubled,
     * then {@link #doubleQuotes(Object)} should be invoked explicitly.
     *
     * @param  value The value to append.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder appendEscaped(final String value) {
        final StringTokenizer tokens = new StringTokenizer(value, "_%", true);
        while (tokens.hasMoreTokens()) {
            buffer.append(tokens.nextToken());
            if (!tokens.hasMoreTokens()) {
                break;
            }
            buffer.append(escape).append(tokens.nextToken());
        }
        return this;
    }

    /**
     * Returns the string representation of the given value with simple quote doubled.
     *
     * @param  value The object for which to double the quotes in the string representation.
     * @return A string representation of the given object with simple quotes doubled.
     */
    public static String doubleQuotes(final Object value) {
        return value.toString().replace("'", "''");
    }

    /**
     * Returns the SQL statement.
     */
    @Override
    public String toString() {
        return buffer.toString();
    }
}
