/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.apache.sis.referencing.factory.sql.SQLTranslator;


/**
 * Adapts SQL statements for Oracle SQL. The Oracle database engine doesn't accept the
 * "{@code AS}" keyword, which just need to be removed.
 *
 * @author John Grange
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @module
 */
final class OracleDialectEpsgFactory extends SQLTranslator {
    /**
     * Constructs an authority factory for the given metadata.
     */
    public OracleDialectEpsgFactory(final DatabaseMetaData metadata) throws SQLException {
        super(metadata, null, null);
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
    public String apply(final String statement) {
        return super.apply(statement).replace(" AS ", " ");
    }
}
