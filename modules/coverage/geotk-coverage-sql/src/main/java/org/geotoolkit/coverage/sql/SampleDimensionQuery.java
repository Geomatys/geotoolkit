/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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

import org.geotoolkit.internal.sql.Ordering;
import static org.geotoolkit.coverage.sql.QueryType.*;


/**
 * The query to execute for a {@link SampleDimensionTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class SampleDimensionQuery extends Query {
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    final Column band, name, units;

    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    final Parameter byFormat;

    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public SampleDimensionQuery(final Database database) {
        super(database, "SampleDimensions");
        final Column format;
        final QueryType[] usage = {LIST};
        format   = addMandatoryColumn("format",      usage);
        band     = addOptionalColumn ("band", 1,     usage);
        name     = addMandatoryColumn("name",        usage);
        units    = addOptionalColumn ("units", null, usage);
        byFormat = addParameter(format, usage);
        band.setOrdering(Ordering.ASC, usage);
    }
}
