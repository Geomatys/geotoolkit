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

import java.util.Map;
import java.util.HashMap;
import javax.sql.DataSource;

import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;

import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;


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
     * The horizontal CRS used in the database. It must matches the {@code srid} of the
     * {@code "horizontalExtent"} geometry column in the {@code "GridGeometries"} table.
     */
    static final SingleCRS HORIZONTAL_CRS = DefaultGeographicCRS.WGS84;

    /**
     * The vertical CRS used in envelopes. This CRS is not used in the database
     * (since vertical CRS are declared explicitly), so it can be modified freely.
     * Make sure however that {@link #SPATIO_TEMPORAL_CRS} is coherent with it.
     */
    static final VerticalCRS VERTICAL_CRS = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;

    /**
     * The temporal CRS used for envelopes. This CRS is not used in the database
     * (since we use the {@code TIMESTAMP} type), so it can be modified freely.
     */
    static final TemporalCRS TEMPORAL_CRS = DefaultTemporalCRS.TRUNCATED_JULIAN;

    /**
     * The default 4-dimensional Coordinate Reference System used for queries in database. This
     * is a compound of {@link DefaultGeographicCRS#WGS84_3D WGS84_3D} with {@link #TEMPORAL_CRS}.
     */
    static final CoordinateReferenceSystem SPATIO_TEMPORAL_CRS;
    static {
        final Map<String,Object> properties = new HashMap<String,Object>(4);
        properties.put(CoordinateReferenceSystem.NAME_KEY, "WGS84");
        properties.put(CoordinateReferenceSystem.DOMAIN_OF_VALIDITY_KEY, DefaultExtent.WORLD);
        SPATIO_TEMPORAL_CRS = new DefaultCompoundCRS(properties,
                DefaultGeographicCRS.WGS84_3D, TEMPORAL_CRS);
    }

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
