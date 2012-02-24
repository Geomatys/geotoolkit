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

import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Parameter;
import org.geotoolkit.internal.sql.table.Query;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.SpatialDatabase;

import static org.geotoolkit.internal.sql.table.QueryType.*;


/**
 * The query to execute for a {@link TileTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class TileQuery extends Query {
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    final Column series, filename, index, spatialExtent, dx, dy;

    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    final Parameter byLayer, byStartTime, byEndTime, byHorizontalSRID;

    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public TileQuery(final SpatialDatabase database) {
        super(database, "Tiles");
        final Column layer, startTime, endTime, horizontalSRID;
        final QueryType[] none  = {    };
        final QueryType[] list  = {LIST};
        final QueryType[] lsex  = {LIST, EXISTS};
        layer          = addForeignerColumn("layer", "Series", none);
        series         = addMandatoryColumn("series",          lsex);
        filename       = addMandatoryColumn("filename",        list);
        index          = addOptionalColumn ("index", 1,        list);
        startTime      = addMandatoryColumn("startTime",       none);
        endTime        = addMandatoryColumn("endTime",         none);
        spatialExtent  = addMandatoryColumn("extent",          list);
        dx             = addOptionalColumn ("dx", 0,           list);
        dy             = addOptionalColumn ("dy", 0,           list);
        horizontalSRID = addForeignerColumn("horizontalSRID", "GridGeometries", none);

        byLayer          = addParameter(layer,          lsex);
        byStartTime      = addParameter(startTime,      list);
        byEndTime        = addParameter(endTime,        list);
        byHorizontalSRID = addParameter(horizontalSRID, list);
        /*
         * Following conditions are the opposite of GridCoverageQuery because we wants
         * every tiles included in the range of the coverage, not tiles intercepting.
         */
        byStartTime.setComparator("IS NULL OR >=");
        byEndTime  .setComparator("IS NULL OR <=");
    }
}
