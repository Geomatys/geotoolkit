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
package org.geotoolkit.dggs.healpix;

import cds.healpix.HashComputer;
import cds.healpix.Healpix;
import cds.healpix.HealpixNested;
import com.google.common.geometry.S2Polygon;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractDiscreteGlobalGrid;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class NHealpixDgg extends AbstractDiscreteGlobalGrid<NHealpixDggh> {

    private final List<Zone> roots;
    private final HealpixNested healpixNested;
    private final HashComputer hashComputer;

    public NHealpixDgg(NHealpixDggh dggh, int level) {
        super(dggh, level);
        healpixNested = Healpix.getNested(level);
        hashComputer = healpixNested.newHashComputer();

        if (level == 0) {
            roots = List.of(
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 0)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 1)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 2)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 3)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 4)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 5)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 6)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 7)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 8)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 9)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 10)),
                new NHealpixZone(hierarchy.dggrs, FitsSerialization.getHash(1, 11)));
        } else {
            roots = null;
        }
    }

    @Override
    public Zone getZone(DirectPosition dp) throws TransformException {
        final CoordinateReferenceSystem baseCrs = hierarchy.dggrs.dggs.getCrs();
        final CoordinateReferenceSystem dpcrs = dp.getCoordinateReferenceSystem();
        if (dpcrs != null && !Utilities.equalsIgnoreMetadata(baseCrs, dpcrs)) {
            MathTransform trs;
            try {
                trs = CRS.findOperation(dpcrs, baseCrs, null).getMathTransform();
                dp = trs.transform(dp, null);
            } catch (FactoryException ex) {
                throw new TransformException(ex.getMessage(), ex);
            }
        }

        double lon = Math.toRadians(dp.getCoordinate(0));
        double lat = Math.toRadians(dp.getCoordinate(1));
        if (lon < 0) lon += Math.PI + Math.PI;

        final long hash = hashComputer.hash(lon, lat);
        final long zid = FitsSerialization.getHash(level+1, hash);
        return new NHealpixZone(hierarchy.dggrs, zid);
    }

    @Override
    public Stream<Zone> getZones(Envelope env) throws TransformException {
        return super.getZones(env);

        //Bugged, raises assertion errors
        /*
        final double minX = env.getMinimum(0);
        final double minY = env.getMinimum(1);
        final double maxX = env.getMaximum(0);
        final double maxY = env.getMaximum(1);

        //api does not say, but when looking at the code, first point should not be duplicated at the end of the list
        final HealpixNestedBMOC candidate = polygonComputer.overlappingCenters(new double[][]{
                {minX, minY},
                {minX, maxY},
                {maxX, maxY},
                {maxX, minY}});
        final List<Zone> zones = new ArrayList<>();
        final Iterator<HealpixNestedBMOC.CurrentValueAccessor> iterator = candidate.iterator();
        while (iterator.hasNext()) {
            final HealpixNestedBMOC.CurrentValueAccessor acc = iterator.next();
            final NHealpixZone z = new NHealpixZone(dggrs, acc.getDepth(), acc.getHash());
            if (z.getOrder() != level) {
                zones.addAll(z.getChildrenAtRelativeDepth(level-z.getOrder()).toList());
            } else {
                zones.add(z);
            }
        }
        return zones.stream();
        */
    }

    @Override
    public Stream<Zone> getZones(GeographicExtent extent) throws TransformException {
        if (extent == null && level == 0) {
            return roots.stream();
        }

        //search from root
        final S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(extent);
        try (Stream<Zone> zones = hierarchy.getGrids().get(0).getZones()) {
            return DiscreteGlobalGridSystems.spatialSearch(zones.toList(), level, geometry);
        }
    }

    @Override
    public Stream<Zone> getZones(Zone parent) throws TransformException {
        final int parentDepth = parent.getLocationType().getRefinementLevel();
        if (parent.getLocationType().getRefinementLevel() > level) {
            throw new IllegalArgumentException("Parent zone is at a lower level then this grid");
        }
        return parent.getChildrenAtRelativeDepth(level-parentDepth);
    }
}
