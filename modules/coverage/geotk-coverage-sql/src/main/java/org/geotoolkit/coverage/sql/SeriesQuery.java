/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.Parameter;
import org.geotoolkit.internal.sql.table.Query;
import org.geotoolkit.internal.sql.table.QueryType;

import static org.geotoolkit.internal.sql.table.QueryType.*;


/**
 * The query to execute for a {@link SeriesTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class SeriesQuery extends Query {
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    final Column identifier, layer, pathname, extension, format, comments;

    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    final Parameter byIdentifier, byLayer;

    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public SeriesQuery(final Database database) {
        super(database, "Series");
        final QueryType[] sle = {SELECT, LIST, EXISTS};
        final QueryType[] sli = {SELECT, LIST, INSERT};
        identifier   = addMandatoryColumn("identifier", sle);
        layer        = addMandatoryColumn("layer",   INSERT);
        pathname     = addMandatoryColumn("pathname",   sli);
        extension    = addMandatoryColumn("extension",  sli);
        format       = addMandatoryColumn("format",     sli);
        comments     = addOptionalColumn ("comments", null, SELECT, LIST);
        byIdentifier = addParameter(identifier, SELECT, EXISTS, DELETE);
        byLayer      = addParameter(layer, LIST, DELETE_ALL);
    }
}
