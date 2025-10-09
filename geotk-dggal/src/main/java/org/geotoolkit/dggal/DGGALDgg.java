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
package org.geotoolkit.dggal;

import com.google.common.geometry.S2Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.dggal.panama.DggalDggrs;
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
final class DGGALDgg extends AbstractDiscreteGlobalGrid<DGGALDggh> {

    private final List<Zone> roots;

    public DGGALDgg(DGGALDggh dggh, int level) {
        super(dggh, level);

        if (level == 0) {
            try {
                final long[] roots = dggh.dggrs.dggal.listZones(0, null);
                this.roots = new ArrayList<>(roots.length);
                for (int i = 0; i < roots.length; i++) {
                    this.roots.add(new DGGALZone(dggh.dggrs, roots[i]));
                }
            } catch (Throwable ex) {
                throw new DGGALBindingException(ex);
            }
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

        try {
            final long zid = hierarchy.dggrs.dggal.getZoneFromWGS84Centroid(level, new double[]{
                Math.toRadians(dp.getCoordinate(1)),
                Math.toRadians(dp.getCoordinate(0))});
            return hierarchy.getZone(zid);
        } catch (Throwable ex) {
            throw new DGGALBindingException(ex);
        }
    }

    @Override
    public Stream<Zone> getZones(Envelope env) throws TransformException {
        return super.getZones(env);
    }

    @Override
    public Stream<Zone> getZones(Zone parent) throws TransformException {
        if (!(parent instanceof DGGALZone d)) throw new IllegalArgumentException("Zone in not from DGGAL");

        try {
            final DggalDggrs dggal = hierarchy.dggrs.dggal;
            final long[] subzones = dggal.getSubZones(parent.getLongIdentifier(), level - d.getLocationType().getRefinementLevel());
            return LongStream.of(subzones).mapToObj((long value) -> new DGGALZone(hierarchy.dggrs, value));
        } catch (Throwable ex) {
            throw new TransformException(ex);
        }
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
    protected long getZoneLongIdentifier(double[] source, int soffset) {
        try {
            return hierarchy.dggrs.dggal.getZoneFromWGS84Centroid(level, new double[]{
                Math.toRadians(source[soffset+1]),
                Math.toRadians(source[soffset])});
        } catch (Throwable ex) {
            throw new DGGALBindingException(ex);
        }
    }

    @Override
    protected void getZonePosition(long zoneId, double[] target, int toffset) {
        double[] centroid;
        try {
            centroid = hierarchy.dggrs.dggal.getZoneWGS84Centroid(zoneId);
        } catch (Throwable ex) {
            throw new DGGALBindingException(ex);
        }
        target[0] = Math.toDegrees(centroid[1]);
        target[1] = Math.toDegrees(centroid[0]);
    }

}
