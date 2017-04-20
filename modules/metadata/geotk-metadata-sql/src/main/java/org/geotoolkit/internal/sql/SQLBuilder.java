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
package org.geotoolkit.internal.sql;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;


/**
 * Utility methods for building SQL statements.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class SQLBuilder extends org.apache.sis.internal.metadata.sql.SQLBuilder {
    /**
     * Creates a new {@code SQLBuilder} initialized from the given database metadata.
     *
     * @param  metadata The database metadata.
     * @throws SQLException If an error occurred while fetching the database metadata.
     */
    public SQLBuilder(final DatabaseMetaData metadata) throws SQLException {
        super(metadata, true);
    }

    /**
     * Creates a new {@code SQLBuilder} initialized to the same metadata than the given builder.
     *
     * @param metadata The builder from which to copy metadata.
     */
    public SQLBuilder(final SQLBuilder metadata) {
        super(metadata);
    }

    /**
     * Appends an identifier, with quotes only if the {@code quote} argument is {@code true}.
     *
     * @param  identifier The identifier to append.
     * @param  quote {@code true} for adding quotes.
     * @return This builder, for method call chaining.
     *
     * @since 3.11
     */
    public SQLBuilder appendIdentifier(final String identifier, final boolean quote) {
        return (SQLBuilder) (quote ? appendIdentifier(identifier) : append(identifier));
    }
}
