/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.dggs;

import org.geotoolkit.storage.dggs.internal.shared.GridAsDiscreteGlobalGridResource;
import java.util.List;
import java.util.Optional;
import javax.measure.IncommensurableException;
import org.apache.sis.geometries.Geometry;
import org.apache.sis.geometries.operation.GeometryOperations;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.AngleFormat;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.measure.Units;
import org.apache.sis.referencing.internal.shared.GeodeticObjectBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.dggs.internal.shared.ComputedZoneIndexList;
import org.geotoolkit.storage.dggs.internal.shared.DiscreteGlobalGridCoverageAsFeatureSet;
import org.geotoolkit.storage.dggs.internal.shared.FeatureSetAsDiscreteGlobalGridResource;
import org.geotoolkit.storage.dggs.internal.shared.MemoryDiscreteGlobalGridResource;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Utility methods for DGGRS.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridSystems {

    public static List<ZonalIdentifier.Long> createComputedList(long start, long step, int count) {
        return new ComputedZoneIndexList(start, step, count);
    }

    /**
     * Compute the full ellipsoid surface in m².
     *
     * @todo current version use a sphere formula
     * @param gcrs to extract ellipsoid from
     * @return ellipsoid surface
     */
    public static double computeSurface(GeographicCRS gcrs) {
        final Ellipsoid ellipsoid = gcrs.getDatum().getEllipsoid();
        final double semiMajorAxis = ellipsoid.getSemiMajorAxis();
        final double semiMinorAxis = ellipsoid.getSemiMinorAxis();
        final double r = (semiMajorAxis + semiMinorAxis) / 2;
        final double surfaceArea = 4.0 * Math.PI * r * r;
        return surfaceArea;
    }

    /**
     * Create a local orthographic projection at given longitude and latitude.
     *
     * @todo move this somewhere else.
     */
    public static ProjectedCRS createOrthographicCRS(final GeographicCRS baseCRS,
                final double latitude, final double longitude) throws FactoryException
        {
            return newBuilder("ORTHOGRAPHIC", latitude, longitude)
                    .setConversionMethod("Orthographic")
                    .setParameter("Latitude of natural origin",  latitude,  Units.DEGREE)
                    .setParameter("Longitude of natural origin", longitude, Units.DEGREE)
                    .createProjectedCRS(baseCRS, null);
        }

    /**
     * Creates a new builder initialized to the projection name for the given coordinates.
     */
    private static final GeodeticObjectBuilder newBuilder(String name, final double latitude, final double longitude) {
        final AngleFormat  f = new AngleFormat("DD°MM′SS″");
        final StringBuffer b = new StringBuffer();
        b.append(name).append(" @ ");
        f.format(new Latitude (latitude),  b, null).append(' ');
        f.format(new Longitude(longitude), b, null);
        return new GeodeticObjectBuilder().addName(b.toString());
    }

    /**
     * Get envelope of a DGGRS coverage in a different CRS.
     */
    public static Optional<Envelope> getEnvelope(DiscreteGlobalGridCoverage coverage, CoordinateReferenceSystem crs) throws TransformException {
        GeneralEnvelope all = null;
        final ZoneIterator iterator = coverage.createIterator();
        while (iterator.next()) {
            final Zone zo = iterator.getZone();
            final Geometry geometry = zo.getGeometry();
            final Geometry trsGeom = GeometryOperations.SpatialEdition.transform(geometry, crs, null);
            final Envelope env = trsGeom.getEnvelope();
            if (env != null) {
                if (all == null) {
                    all = new GeneralEnvelope(env);
                } else {
                    all.add(env);
                }
            }
        }
        return Optional.ofNullable(all);
    }

    /**
     * View given grid coverage as a DiscreteGlobalGridResource.
     */
    public static DiscreteGlobalGridResource viewAsDggrs(GridCoverageResource base, DiscreteGlobalGridReferenceSystem dggrs) throws DataStoreException, IncommensurableException, TransformException {
        return new GridAsDiscreteGlobalGridResource(dggrs, base);
    }

    /**
     * View given FeatureSet as a DiscreteGlobalGridResource.
     */
    public static DiscreteGlobalGridResource viewAsDggrs(FeatureSet base, DiscreteGlobalGridReferenceSystem dggrs) throws DataStoreException, IncommensurableException {
        return new FeatureSetAsDiscreteGlobalGridResource(dggrs, base);
    }

    /**
     * View given DiscreteGlobalGridCoverage as a DiscreteGlobalGridResource.
     */
    public static DiscreteGlobalGridResource viewAsResource(DiscreteGlobalGridCoverage coverage) {
        return new MemoryDiscreteGlobalGridResource(coverage);
    }

    /**
     * View given DiscreteGlobalGridCoverage as a FeatureSet.
     */
    public static FeatureSet viewAsFeatureSet(DiscreteGlobalGridCoverage coverage) {
        return new DiscreteGlobalGridCoverageAsFeatureSet(coverage);
    }

}
