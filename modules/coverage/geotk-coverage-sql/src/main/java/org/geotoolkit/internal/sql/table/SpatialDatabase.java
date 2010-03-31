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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import javax.sql.DataSource;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory;
import org.geotoolkit.referencing.factory.wkt.AuthorityFactoryProvider;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.resources.Errors;


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
     * The horizontal SRID of {@link #horizontalCRS}, as declared in the PostGIS geometry column.
     */
    public final int horizontalSRID;

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
     * The horizontal and vertical CRS, without the temporal component.
     */
    public final CoordinateReferenceSystem spatialCRS;

    /**
     * The complete CRS, including the vertical and temporal components if any.
     */
    public final CoordinateReferenceSystem spatioTemporalCRS;

    /**
     * Whatever default grid range computation should be performed on transforms
     * relative to pixel center or relative to pixel corner. The former is OGC
     * convention while the later is Java convention.
     */
    public final PixelInCell pixelInCell;

    /**
     * The authority factory connected to the PostGIS {@code "spatial_ref_sys"} table.
     * Will be created when first needed.
     */
    private transient CRSAuthorityFactory crsFactory;

    /**
     * The math transform factory, created only when first needed.
     */
    private transient MathTransformFactory mtFactory;

    /**
     * Creates a new instance using the same configuration than the given instance.
     * The new instance will have its own, initially empty, cache.
     *
     * @param toCopy The existing instance to copy.
     */
    public SpatialDatabase(final SpatialDatabase toCopy) {
        super(toCopy);
        horizontalSRID    = toCopy.horizontalSRID;
        horizontalCRS     = toCopy.horizontalCRS;
        verticalCRS       = toCopy.verticalCRS;
        temporalCRS       = toCopy.temporalCRS;
        spatialCRS        = toCopy.spatialCRS;
        spatioTemporalCRS = toCopy.spatioTemporalCRS;
        pixelInCell       = toCopy.pixelInCell;
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
        this.horizontalSRID = 4326;
        this.horizontalCRS  = DefaultGeographicCRS.WGS84;
        this.verticalCRS    = DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT;
        this.temporalCRS    = DefaultTemporalCRS.wrap(temporalCRS);
        this.spatialCRS     = DefaultGeographicCRS.WGS84_3D;
        final Map<String,Object> id = new HashMap<String,Object>(4);
        id.put(CoordinateReferenceSystem.NAME_KEY, "WGS84");
        id.put(CoordinateReferenceSystem.DOMAIN_OF_VALIDITY_KEY, DefaultExtent.WORLD);
        spatioTemporalCRS = new DefaultCompoundCRS(id, spatialCRS, temporalCRS);
        pixelInCell = PixelInCell.CELL_CORNER;
    }

    /**
     * Creates a new instance using the provided data source, spatio-temporal CRS and configuration
     * properties.
     *
     * @param  datasource  The data source.
     * @param  properties  The configuration properties, or {@code null} if none.
     * @param  spatialCRS  The spatial CRS, not including the temporal component.
     * @param  temporalCRS The temporal CRS, or {@code null} if none.
     * @throws FactoryException If an error occured while fetching the SRID of the horizontal CRS.
     */
    public SpatialDatabase(final DataSource datasource, final Properties properties,
            final CoordinateReferenceSystem spatialCRS, final TemporalCRS temporalCRS)
            throws FactoryException
    {
        super(datasource, properties);
        Table.ensureNonNull("spatialCRS", spatialCRS);
        this.horizontalCRS     = CRS.getHorizontalCRS(spatialCRS);
        this.verticalCRS       = CRS.getVerticalCRS(spatialCRS);
        this.temporalCRS       = DefaultTemporalCRS.wrap(temporalCRS);
        this.spatialCRS        = spatialCRS;
        this.pixelInCell       = PixelInCell.CELL_CORNER;
        this.spatioTemporalCRS = (temporalCRS == null) ? spatialCRS : new DefaultCompoundCRS(
                AbstractIdentifiedObject.getProperties(spatialCRS), spatialCRS, temporalCRS);
        /*
         * Try to get the PostGIS SRID from the horizontal CRS. First, search for an explicit
         * PostGIS code. If none are found, lookup for the EPSG code and convert that code to
         * a PostGIS code (this is usually the same, but not necessarly).
         */
        if (horizontalCRS == null) {
            horizontalSRID = 0;
            return;
        }
        final String code = CRS.lookupIdentifier(Citations.POSTGIS, horizontalCRS, false);
        if (code != null) try {
            horizontalSRID = Integer.parseInt(code);
            return;
        } catch (NumberFormatException e) {
            throw new FactoryException(Errors.format(Errors.Keys.NOT_AN_INTEGER_$1, code), e);
        }
        /*
         * No PostGIS code. Search for an EPSG code...
         */
        Integer id = CRS.lookupEpsgCode(horizontalCRS, true);
        if (id != null) {
            try {
                final Connection c = datasource.getConnection();
                final DirectPostgisFactory postgis = new DirectPostgisFactory(null, c);
                id = postgis.getPrimaryKey(CoordinateReferenceSystem.class, id.toString());
                c.close();
            } catch (SQLException e) {
                throw new FactoryException(e);
            }
            if (id != null) {
                horizontalSRID = id;
                return;
            }
        }
        throw new FactoryException(Errors.format(Errors.Keys.UNDEFINED_PROPERTY_$1, "SRID"));
    }

    /**
     * Returns the CRS authority factory backed by the PostGIS {@code "spatial_ref_sys"} table.
     * The factory is determined by the hints given at construction time.
     *
     * @return The shared CRS authority factory.
     * @throws FactoryException If the factory can not be created.
     */
    public final synchronized CRSAuthorityFactory getCRSAuthorityFactory() throws FactoryException {
        if (crsFactory == null) {
            crsFactory = new AuthorityFactoryProvider(hints).createFromPostGIS(getDataSource(true));
        }
        return crsFactory;
    }

    /**
     * Returns the math transform factory.
     * The factory is determined by the hints given at construction time.
     *
     * @return The shared math transform factory.
     */
    public final synchronized MathTransformFactory getMathTransformFactory() {
        if (mtFactory == null) {
            mtFactory = AuthorityFactoryFinder.getMathTransformFactory(hints);
        }
        return mtFactory;
    }
}
