/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.sql;

import javax.sql.DataSource;

import org.opengis.referencing.datum.PixelInCell;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.internal.sql.table.Database;


/**
 * A {@link GridCoverageReader} implementation which use a SQL database for managing a
 * collection of images. The connection to the database is specified by a {@link DataSource}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
abstract class SQLCoverageReader extends GridCoverageReader {
    /**
     * Whatever default grid range computation should be performed on transforms
     * relative to pixel center or relative to pixel corner. The former is OGC
     * convention while the later is Java convention.
     */
    static final PixelInCell PIXEL_IN_CELL = PixelInCell.CELL_CORNER;

    /**
     * The object which will manage the connections to the database.
     */
    private final Database database;

    /**
     * Creates a new reader using the given database.
     *
     * @param database The database.
     */
    public SQLCoverageReader(final Database database) {
        this.database = database;
    }
}
