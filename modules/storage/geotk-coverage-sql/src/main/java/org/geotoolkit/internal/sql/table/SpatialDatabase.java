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
package org.geotoolkit.internal.sql.table;

import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import javax.sql.DataSource;

import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.util.FactoryException;

import org.apache.sis.internal.referencing.GeodeticObjectBuilder;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;

import org.geotoolkit.referencing.factory.wkt.DirectPostgisFactory;
import org.geotoolkit.referencing.factory.wkt.AuthorityFactoryProvider;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.Factory;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.resources.Errors;

import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.metadata.Citations;
import org.opengis.metadata.Identifier;


/**
 * A specialization of {@link Database} which specify the {@link CoordinateReferenceSystem}
 * of the horizontal, vertical and temporal extents.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
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
     * The spatio-temporal CRS without the vertical component.
     */
    public final CoordinateReferenceSystem horizTemporalCRS;

    /**
     * The vertical CRS with time.
     */
    public final CoordinateReferenceSystem vertTemporalCRS;

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
        horizTemporalCRS  = toCopy.horizTemporalCRS;
        vertTemporalCRS   = toCopy.vertTemporalCRS;
        pixelInCell       = toCopy.pixelInCell;
    }

    /**
     * Creates a new instance using the provided data source and configuration properties.
     * A default Coordinate Reference System is used.
     * <p>
     * If the given properties contains only one entry, and the key for this entry is
     * {@value org.geotoolkit.internal.sql.table.ConfigurationKey#PARAMETERS}, then the
     * value will be used as {@link org.opengis.parameter.ParameterValueGroup}.
     *
     * @param  datasource The data source, or {@code null} for creating it from the URL.
     * @param  properties The configuration properties, or {@code null} if none.
     */
    public SpatialDatabase(final DataSource datasource, final Properties properties) {
        this(datasource, properties, CommonCRS.Temporal.TRUNCATED_JULIAN.crs());
    }

    /**
     * Creates a new instance using the provided data source, temporal CRS and configuration
     * properties.
     * <p>
     * If the given properties contains only one entry, and the key for this entry is
     * {@value org.geotoolkit.internal.sql.table.ConfigurationKey#PARAMETERS}, then the
     * value will be used as {@link org.opengis.parameter.ParameterValueGroup}.
     *
     * @param  datasource The data source, or {@code null} for creating it from the URL.
     * @param  properties The configuration properties, or {@code null} if none.
     * @param  temporalCRS The temporal vertical reference system, or {@code null} if none.
     * @throws IllegalStateException if problem during Spatio Temporal CRS creation.
     */
    public SpatialDatabase(final DataSource datasource, final Properties properties, final TemporalCRS temporalCRS) {
        super(datasource, properties);
        this.horizontalSRID = 4326;
        this.horizontalCRS  = CommonCRS.WGS84.normalizedGeographic();
        this.verticalCRS    = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        this.temporalCRS    = DefaultTemporalCRS.castOrCopy(temporalCRS);
        this.spatialCRS     = AbstractCRS.castOrCopy(CommonCRS.WGS84.geographic3D()).forConvention(AxesConvention.RIGHT_HANDED);
        try {
            spatioTemporalCRS   = createSpatioTemporalCRS(spatialCRS,    temporalCRS, true);
            horizTemporalCRS    = createSpatioTemporalCRS(horizontalCRS, temporalCRS, true);
            vertTemporalCRS     = createSpatioTemporalCRS(verticalCRS,   temporalCRS, true);
        } catch (FactoryException ex) { //-- TODO leave exception propagation.
            throw new IllegalStateException(ex);
        }
        pixelInCell         = PixelInCell.CELL_CORNER;
    }

    /**
     * Creates a new spatio-temporal CRS from the given spatial CRS and the given temporal CRS.
     * If any of those argument is null, the other one is returned directly.
     *
     * @param  spatialCRS  The spatial component, or {@code null}.
     * @param  temporalCRS The vertical component, or {@code null}.
     * @param  world {@code true} if the spatial CRS is valid for the world extent.
     * @return The spatio-temporal CRS.
     */
    private static CoordinateReferenceSystem createSpatioTemporalCRS(
            final CoordinateReferenceSystem spatialCRS, final TemporalCRS temporalCRS, final boolean world)
            throws FactoryException {
        if (temporalCRS == null) return spatialCRS;
        if (spatialCRS == null) return temporalCRS;
        final Map<String,Object> id = new HashMap<>(4);
        id.put(CoordinateReferenceSystem.NAME_KEY, spatialCRS.getName().getCode() +
                " + Time(" + temporalCRS.getName().getCode() + ')');
        if (world) {
            id.put(CoordinateReferenceSystem.DOMAIN_OF_VALIDITY_KEY, Extents.WORLD);
        }
        return new GeodeticObjectBuilder().addName(spatialCRS.getName().getCode() +
                " + Time(" + temporalCRS.getName().getCode() + ')')
                                //-- TODO .addDomainOfValidity(Extents.WORLD)
                                          .createCompoundCRS(spatialCRS, temporalCRS);
    }

    /**
     * Creates a new instance using the provided data source, spatio-temporal CRS and configuration
     * properties.
     * <p>
     * If the given properties contains only one entry, and the key for this entry is
     * {@value org.geotoolkit.internal.sql.table.ConfigurationKey#PARAMETERS}, then the
     * value will be used as {@link org.opengis.parameter.ParameterValueGroup}.
     *
     * @param  datasource The data source, or {@code null} for creating it from the URL.
     * @param  properties  The configuration properties, or {@code null} if none.
     * @param  spatialCRS  The spatial CRS, not including the temporal component.
     * @param  temporalCRS The temporal CRS, or {@code null} if none.
     * @throws FactoryException If an error occurred while fetching the SRID of the horizontal CRS.
     */
    public SpatialDatabase(final DataSource datasource, final Properties properties,
            final CoordinateReferenceSystem spatialCRS, final TemporalCRS temporalCRS)
            throws FactoryException
    {
        super(datasource, properties);
        ensureNonNull("spatialCRS", spatialCRS);
        this.horizontalCRS = CRS.getHorizontalComponent(spatialCRS);
        this.verticalCRS   = CRS.getVerticalComponent(spatialCRS, true);
        this.temporalCRS   = (DefaultTemporalCRS) temporalCRS; // TODO DefaultTemporalCRS.castOrCopy(temporalCRS);
        this.spatialCRS    = spatialCRS;
        this.pixelInCell   = PixelInCell.CELL_CORNER;
        horizTemporalCRS   = createSpatioTemporalCRS(horizontalCRS, temporalCRS, false);
        vertTemporalCRS    = createSpatioTemporalCRS(verticalCRS,   temporalCRS, false);
        if (horizontalCRS == spatialCRS) {
            spatioTemporalCRS = horizTemporalCRS;
        } else {
            spatioTemporalCRS = createSpatioTemporalCRS(spatialCRS, temporalCRS, false);
        }
        /*
         * Try to get the PostGIS SRID from the horizontal CRS. First, search for an explicit
         * PostGIS code. If none are found, lookup for the EPSG code and convert that code to
         * a PostGIS code (this is usually the same, but not necessarily).
         */
        if (horizontalCRS == null) {
            horizontalSRID = 0;
            return;
        }
        final Identifier code = IdentifiedObjects.getIdentifier(horizontalCRS, Citations.POSTGIS);
        if (code != null) try {
            horizontalSRID = Integer.parseInt(code.getCode());
            return;
        } catch (NumberFormatException e) {
            throw new FactoryException(Errors.format(Errors.Keys.NotAnInteger_1, code), e);
        }
        /*
         * No PostGIS code. Search for an EPSG code...
         */
        Integer id = org.apache.sis.referencing.IdentifiedObjects.lookupEPSG(horizontalCRS);
        if (id != null) {
            try (Connection c = getDataSource(true).getConnection()) {
                final DirectPostgisFactory postgis = new DirectPostgisFactory(null, c);
                id = postgis.getPrimaryKey(CoordinateReferenceSystem.class, id.toString());
            } catch (SQLException e) {
                throw new FactoryException(e);
            }
            if (id != null) {
                horizontalSRID = id;
                return;
            }
        }
        throw new FactoryException(Errors.format(Errors.Keys.UndefinedProperty_1, "SRID"));
    }

    /**
     * Derives a {@link CRSFactory} from the {@link #getCRSAuthorityFactory() authority factory}.
     * If the authority factory is already a {@code CRSFactory} instance, then it is returned.
     * Otherwise the implementation hints are inspected. If no {@code CRSFactory} is found, then
     * the default instance is fetched from the {@link AuthorityFactoryFinder}.
     *
     * @return The CRS factory.
     * @throws FactoryException If the factory can not be created.
     *
     * @since 3.19
     */
    public final CRSFactory getCRSFactory() throws FactoryException {
        final CRSAuthorityFactory factory = getCRSAuthorityFactory();
        if (factory instanceof CRSFactory) {
            return (CRSFactory) factory;
        }
        Hints hints = null;
        if (factory instanceof Factory) {
            final Map<RenderingHints.Key,?> impl = ((Factory) factory).getImplementationHints();
            final Object candidate = impl.get(Hints.CRS_FACTORY);
            if (candidate instanceof CRSFactory) {
                return (CRSFactory) candidate;
            }
            hints = new Hints(impl);
        }
        return AuthorityFactoryFinder.getCRSFactory(hints);
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
