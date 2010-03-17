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
package org.geotoolkit.internal.sql.table;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import javax.sql.DataSource;

import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.CRS;


/**
 * A specialization of {@link Database} which specify the {@link CoordinateReferenceSystem}
 * of the horizontal, vertical and temporal extents.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
@ThreadSafe(concurrent = true)
public class SpatialDatabase extends Database {
    /**
     * The horizontal coordinate reference system used for performing the search in the database.
     * It must match the CRS used in the geometry columns indexed by PostGIS.
     */
    public final SingleCRS horizontalCRS;

    /**
     * The vertical reference system, or {@code null} if none.
     */
    public final VerticalCRS verticalCRS;

    /**
     * The temporal reference system, or {@code null} if none.
     */
    public final DefaultTemporalCRS temporalCRS;

    /**
     * The complete CRS, including the vertical and temporal components if any.
     */
    public final CoordinateReferenceSystem spatioTemporalCRS;

    /**
     * Creates a new instance using the same configuration than the given instance.
     * The new instance will have its own, initially empty, cache.
     *
     * @param toCopy The existing instance to copy.
     */
    public SpatialDatabase(final SpatialDatabase toCopy) {
        super(toCopy);
        this.horizontalCRS     = toCopy.horizontalCRS;
        this.verticalCRS       = toCopy.verticalCRS;
        this.temporalCRS       = toCopy.temporalCRS;
        this.spatioTemporalCRS = toCopy.spatioTemporalCRS;
    }

    /**
     * Creates a new instance using the provided data source and configuration properties.
     * A default Coordinate Reference System is used.
     *
     * @param  datasource The data source.
     * @param  properties The configuration properties, or {@code null} if none.
     */
    public SpatialDatabase(final DataSource datasource, final Properties properties) {
        this(datasource, properties, DefaultTemporalCRS.TRUNCATED_JULIAN);
    }

    /**
     * Creates a new instance using the provided data source, temporal CRS and configuration
     * properties.
     *
     * @param  datasource The data source.
     * @param  properties The configuration properties, or {@code null} if none.
     * @param  temporalCRS The temporal vertical reference system, or {@code null} if none.
     */
    public SpatialDatabase(final DataSource datasource, final Properties properties, final TemporalCRS temporalCRS) {
        super(datasource, properties);
        this.horizontalCRS = DefaultGeographicCRS.WGS84;
        this.verticalCRS   = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
        this.temporalCRS   = DefaultTemporalCRS.wrap(temporalCRS);
        final Map<String,Object> id = new HashMap<String,Object>(4);
        id.put(CoordinateReferenceSystem.NAME_KEY, "WGS84");
        id.put(CoordinateReferenceSystem.DOMAIN_OF_VALIDITY_KEY, DefaultExtent.WORLD);
        spatioTemporalCRS = new DefaultCompoundCRS(id,
                DefaultGeographicCRS.WGS84_3D, temporalCRS);
    }

    /**
     * Creates a new instance using the provided data source, spatio-temporal CRS and configuration
     * properties.
     *
     * @param  datasource The data source.
     * @param  horizontalCRS The horizontal coordinate reference system used in PostGIS tables.
     * @param  verticalCRS The vertical reference system, or {@code null} if none.
     * @param  temporalCRS The temporal vertical reference system, or {@code null} if none.
     * @param  spatioTemporalCRS The complete CRS, including the vertical and temporal components.
     * @param  properties The configuration properties, or {@code null} if none.
     */
    private SpatialDatabase(final DataSource datasource, final Properties properties,
            final CoordinateReferenceSystem spatioTemporalCRS)
    {
        super(datasource, properties);
        Table.ensureNonNull("spatioTemporalCRS", spatioTemporalCRS);
        this.horizontalCRS     = CRS.getHorizontalCRS(spatioTemporalCRS);
        this.verticalCRS       = CRS.getVerticalCRS(spatioTemporalCRS);
        this.temporalCRS       = DefaultTemporalCRS.wrap(CRS.getTemporalCRS(spatioTemporalCRS));
        this.spatioTemporalCRS = spatioTemporalCRS;
    }
}
