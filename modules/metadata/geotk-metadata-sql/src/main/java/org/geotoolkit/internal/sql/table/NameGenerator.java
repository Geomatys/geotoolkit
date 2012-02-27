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
package org.geotoolkit.internal.sql.table;

import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.geotoolkit.internal.sql.StatementPool;
import org.geotoolkit.internal.sql.IdentifierGenerator;


/**
 * Generator of named identifiers. This is used only for {@link String} identifiers.
 * Numerical identifiers shall use an auto-increment field instead.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
final class NameGenerator extends IdentifierGenerator<String, LocalCache.Stmt> {
    /**
     * Creates a new generator using the given pool of prepared statements.
     * See {@link IdentifierGenerator} constructor for more details on the arguments.
     *
     * @param pool   The pool of prepared statements.
     * @param column The name of the identifier (primary key) column.
     */
    NameGenerator(StatementPool<? super String, LocalCache.Stmt> pool, String column) throws SQLException {
        super(pool, column);
    }

    /**
     * Creates a new generator using the same pool and the same {@link SQLBuilder} than the given
     * generator, but for a different column. Because the two generators share the same resources,
     * the shall be used only in the same thread.
     */
    NameGenerator(final NameGenerator other, final String column) {
        super(other, column);
    }

    /**
     * Returns the key to use for the given table. This method generates a pseudo-SQL
     * statement, in order to distinguish this query from the other queries stored in
     * the pool.
     */
    @Override
    protected String key(final String schema, final String table) {
        final StringBuilder buffer = new StringBuilder("IDENTIFIER ");
        if (schema != null) {
            buffer.append(schema).append('.');
        }
        return buffer.append(table).toString();
    }

    /**
     * Wraps the given statement.
     */
    @Override
    protected LocalCache.Stmt value(final String key, final PreparedStatement query) {
        return new LocalCache.Stmt(query, key);
    }

    /**
     * Returns {@code true} since the column name should be quoted.
     */
    @Override
    protected boolean quoteColumn() {
        return true;
    }
}
