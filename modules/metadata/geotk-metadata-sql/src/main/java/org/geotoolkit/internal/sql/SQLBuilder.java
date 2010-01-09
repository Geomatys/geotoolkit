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
package org.geotoolkit.internal.sql;

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
     * The database dialect. This is used for a few database-dependent syntax.
     */
    private final Dialect dialect;

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
        dialect = Dialect.guess(metadata);
        quote   = metadata.getIdentifierQuoteString();
        escape  = metadata.getSearchStringEscape();
    }

    /**
     * Creates a new {@code SQLBuilder} initialized to the same metadata than the given builder.
     *
     * @param metadata The builder from which to copy metadata.
     */
    public SQLBuilder(final SQLBuilder metadata) {
        dialect = metadata.dialect;
        quote   = metadata.quote;
        escape  = metadata.escape;
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
     * Appends the given integer.
     *
     * @param  n The integer to append.
     * @return This builder, for method call chaining.
     */
    public SQLBuilder append(final int n) {
        buffer.append(n);
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
            return this;
        }
        buffer.append('=');
        return appendValue(value);
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
        } else if (value instanceof Boolean) {
            buffer.append(((Boolean) value).booleanValue() ? "TRUE" : "FALSE");
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
     * Returns a SQL statement for adding a column in a table.
     * The returned statement is of the form:
     *
     * {@preformat sql
     *   ALTER TABLE "schema"."table" ADD COLUMN "column" type
     * }
     *
     * Where {@code type} is some SQL keyword like {@code INTEGER} or {@code VARCHAR}
     * depending on the {@code type} argument.
     *
     * @param  schema     The schema for the table.
     * @param  table      The table to alter with the new column.
     * @param  column     The column to add.
     * @param  type       The column type, or {@code null} for {@code VARCHAR}.
     * @param  maxLength  The maximal length (used for {@code VARCHAR} only).
     * @return A SQL statement for creating the column.
     */
    public String createColumn(final String schema, final String table, final String column,
            final Class<?> type, final int maxLength)
    {
        clear().append("ALTER TABLE ").appendIdentifier(schema, table)
               .append(" ADD COLUMN ").appendIdentifier(column).append(' ');
        final String sqlType = TypeMapper.keywordFor(type);
        if (sqlType != null) {
            append(sqlType);
        } else {
            append("VARCHAR(").append(maxLength).append(')');
        }
        return toString();
    }

    /**
     * Returns a SQL statement for creating a foreigner key constraint.
     * The returned statement is of the form:
     *
     * {@preformat sql
     *   ALTER TABLE "schema"."table" ADD CONSTRAINT "table_column_fkey" FOREIGN KEY("column")
     *   REFERENCES "schema"."target" (primaryKey) ON UPDATE CASCADE ON DELETE RESTRICT
     * }
     *
     * Note that the primary key is NOT quoted on intend. If quoted are desired, then they must
     * be added explicitly before to call this method.
     *
     * @param  schema     The schema for both tables.
     * @param  table      The table to alter with the new constraint.
     * @param  column     The column to alter with the new constraint.
     * @param  target     The table to reference.
     * @param  primaryKey The primary key in the target table.
     * @param  cascade    {@code true} if updates in primary key should be cascaded.
     *                    This apply to updates only; delete is always restricted.
     * @return A SQL statement for creating the foreigner key constraint.
     */
    public String createForeignKey(final String schema, final String table, final String column,
            final String target, final String primaryKey, boolean cascade)
    {
        if (Dialect.DERBY.equals(dialect)) {
            // Derby does not support "ON UPDATE CASCADE". It must be RESTRICT.
            cascade = false;
        }
        buffer.setLength(0);
        final String name = buffer.append(table).append('_').append(column).append("_fkey").toString();
        return clear().append("ALTER TABLE ").appendIdentifier(schema, table).append(" ADD CONSTRAINT ")
                .appendIdentifier(name).append(" FOREIGN KEY(").appendIdentifier(column).append(") REFERENCES ")
                .appendIdentifier(schema, target).append(" (").append(primaryKey)
                .append(") ON UPDATE ").append(cascade ? "CASCADE" : "RESTRICT")
                .append(" ON DELETE RESTRICT").toString();
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
