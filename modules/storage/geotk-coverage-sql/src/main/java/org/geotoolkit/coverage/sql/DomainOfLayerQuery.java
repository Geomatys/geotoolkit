/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.Parameter;
import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Query;
import org.geotoolkit.internal.sql.table.QueryType;

import static org.geotoolkit.internal.sql.table.QueryType.*;


/**
 * The query to execute for a {@link DomainOfLayerTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class DomainOfLayerQuery extends Query {
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    final Column layer, startTime, endTime, west, east, south, north, xResolution, yResolution;

    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    final Parameter byLayer;

    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public DomainOfLayerQuery(final Database database) {
        super(database, "DomainOfLayers");
        final QueryType[] sl = {SELECT, LIST};
        layer       = addMandatoryColumn("layer",       sl);
        startTime   = addMandatoryColumn("startTime",   sl);
        endTime     = addMandatoryColumn("endTime",     sl);
        west        = addMandatoryColumn("west",        sl);
        east        = addMandatoryColumn("east",        sl);
        south       = addMandatoryColumn("south",       sl);
        north       = addMandatoryColumn("north",       sl);
        xResolution = addMandatoryColumn("xResolution", sl);
        yResolution = addMandatoryColumn("yResolution", sl);
        byLayer     = addParameter(layer, SELECT);
    }
}
