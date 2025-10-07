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

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.referencing.dggs.RefinementLevel;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractZone;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.extent.BoundingPolygon;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class DGGALZone extends AbstractZone<DGGALDggrs> {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final long hash;

    public DGGALZone(DGGALDggrs dggrs, long hash) {
        super(dggrs);
        this.hash = hash;
    }

    @Override
    public Object getIdentifier() {
        return hash;
    }

    @Override
    public long getLongIdentifier() {
        return hash;
    }

    @Override
    public CharSequence getTextIdentifier() {
        try {
            return dggrs.dggal.getZoneTextID(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getShapeType() {
        return "?";
    }

    @Override
    public Double getAreaMetersSquare() {
        try {
            return dggrs.dggal.getZoneArea(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RefinementLevel getLocationType() {
        final int level;
        try {
            level = dggrs.dggal.getZoneLevel(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public boolean isNeighbor(Object zone) {
        try {
            return dggrs.dggal.areZonesNeighbors(hash, dggrs.dggs.dggh.toLongIdentifier(zone)) != 0;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isSibling(Object zone) {
        try {
            return dggrs.dggal.areZonesSiblings(hash, dggrs.dggs.dggh.toLongIdentifier(zone)) != 0;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isAncestorOf(Object zone, int maxRelativeDepth) {
        try {
            return dggrs.dggal.isZoneAncestorOf(hash, dggrs.dggs.dggh.toLongIdentifier(zone), maxRelativeDepth) != 0;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isDescendantOf(Object zone, int maxRelativeDepth) {
        try {
            return dggrs.dggal.isZoneDescendantOf(hash, dggrs.dggs.dggh.toLongIdentifier(zone), maxRelativeDepth) != 0;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean overlaps(Object zone) {
        try {
            return dggrs.dggal.doZonesOverlap(hash, dggrs.dggs.dggh.toLongIdentifier(zone)) != 0;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Collection<? extends Zone> getParents() {
        long[] candidates;
        try {
            candidates = dggrs.dggal.getZoneParents(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        final List<Zone> zones = new ArrayList<>(candidates.length);
        for (long cdt : candidates) {
            zones.add(new DGGALZone(dggrs, cdt));
        }
        return zones;
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        long[] candidates;
        try {
            candidates = dggrs.dggal.getZoneChildren(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        final List<Zone> zones = new ArrayList<>(candidates.length);
        for (long cdt : candidates) {
            zones.add(new DGGALZone(dggrs, cdt));
        }
        return zones;
    }

    @Override
    public Collection<? extends Zone> getNeighbors() {
        long[] candidates;
        try {
            candidates = dggrs.dggal.getZoneNeighbors(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        final List<Zone> zones = new ArrayList<>(candidates.length);
        for (long cdt : candidates) {
            zones.add(new DGGALZone(dggrs, cdt));
        }
        return zones;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        double[] boundary;
        try {
            boundary = dggrs.dggal.getZoneWGS84Vertices(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }

        if (boundary == null) return null;
        final List<S2Point> contour = new ArrayList<>(boundary.length/2);
        for (int i = 0, n = boundary.length; i < n ; i+=2) {
            contour.add(S2LatLng.fromRadians(boundary[i], boundary[i+1]).toPoint());
        }
        final S2Loop loop = new S2Loop(contour);
        if (!loop.isNormalized()) {
            //reverse the direction
            loop.invert();
        }
        return DiscreteGlobalGridSystems.toGeographicExtent(new S2Polygon(loop));
    }

    @Override
    public DirectPosition getPosition() {
        double[] centroid;
        try {
            centroid = dggrs.dggal.getZoneWGS84Centroid(hash);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
        return new DirectPosition2D(CRS84.getCoordinateReferenceSystem(),
                Math.toDegrees(centroid[1]),
                Math.toDegrees(centroid[0]));
    }
}
