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

import org.geotoolkit.internal.sql.Ordering;
import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.Parameter;
import org.geotoolkit.internal.sql.table.Query;
import org.geotoolkit.internal.sql.table.QueryType;

import static org.geotoolkit.internal.sql.table.QueryType.*;


/**
 * The query to execute for a {@link FormatTable}.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 3.09 (derived from Seagis)
 * @module
 */
final class FormatQuery extends Query {
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    final Column name, plugin, packMode, comments;

    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    final Parameter byName, byPlugin;

    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public FormatQuery(final Database database) {
        super(database, "Formats");
        final QueryType[] usage = {SELECT, LIST, INSERT};
        name     = addMandatoryColumn("name",     EXISTS, SELECT, LIST, LIST_ID, INSERT);
        plugin   = addMandatoryColumn("plugin",   usage);
        packMode = addMandatoryColumn("packMode", usage);
        comments = addOptionalColumn ("comments", null, SELECT, LIST);
        byName   = addParameter(name, EXISTS, SELECT, DELETE);
        byPlugin = addParameter(plugin, LIST);
        byPlugin.setSearchValue("ANY(?)", LIST);
        name.setOrdering(Ordering.ASC, LIST, LIST_ID);
    }
}
