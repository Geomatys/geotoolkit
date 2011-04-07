/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.util.Map;
import java.sql.Connection;
import java.util.regex.Pattern;

import org.geotoolkit.factory.Hints;


/**
 * Adapts SQL statements for Oracle SQL. The Oracle database engine doesn't accept the
 * "{@code AS}" keyword, which just need to be removed.
 *
 * @author John Grange
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 3.18 (derived from 2.1)
 * @module
 */
final class OracleDialectEpsgFactory extends AnsiDialectEpsgFactory {
    /**
     * The pattern to use for removing "{@code AS}" elements from the SQL statements.
     */
    private final Pattern pattern = Pattern.compile("\\sAS\\s");

    /**
     * Constructs an authority factory using the given connection.
     */
    public OracleDialectEpsgFactory(final Hints userHints, final Connection connection) {
        super(userHints, connection);
    }

    /**
     * Constructs an authority factory using an existing map.
     */
    OracleDialectEpsgFactory(Hints userHints, Connection connection, Map<String,String> toANSI) {
        super(userHints, connection, toANSI);
    }

    /**
     * Modifies the given SQL string to be suitable for an Oracle databases.
     * This removes {@code " AS "} elements from the SQL statements as
     * these don't work in oracle.
     *
     * @param statement The statement in MS-Access syntax.
     * @return The SQL statement to use, suitable for an Oracle database.
     */
    @Override
    protected String adaptSQL(final String statement) {
        return pattern.matcher(super.adaptSQL(statement)).replaceAll(" ");
    }
}
