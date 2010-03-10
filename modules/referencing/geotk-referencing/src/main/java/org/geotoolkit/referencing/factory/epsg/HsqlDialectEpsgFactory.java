/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.sql.HSQL;


/**
 * Adapts SQL statements for HSQL. The HSQL database engine doesn't understand the parenthesis
 * in {@code (INNER JOIN ... ON)} statements for the {@code "BursaWolfParameters"} query.
 * Unfortunately, those parenthesis are required by MS-Access. We need to removes them
 * programmatically here.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.10
 *
 * @since 3.10 (derived from 2.2)
 * @module
 */
final class HsqlDialectEpsgFactory extends AnsiDialectEpsgFactory {
    /**
     * Constructs an authority factory using the given connection.
     */
    public HsqlDialectEpsgFactory(final Hints userHints, final Connection connection) {
        super(userHints, connection);
    }

    /**
     * Constructs an authority factory using an existing map.
     */
    HsqlDialectEpsgFactory(Hints userHints, Connection connection, Map<String,String> toANSI) {
        super(userHints, connection, toANSI);
    }

    /**
     * If the query contains a {@code "FROM ("} expression, remove the parenthesis.
     */
    @Override
    public String adaptSQL(final String query) {
        return HSQL.adaptSQL(super.adaptSQL(query));
    }
}
