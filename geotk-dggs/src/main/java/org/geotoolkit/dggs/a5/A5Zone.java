/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025-2026, Geomatys
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
package org.geotoolkit.dggs.a5;

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.sis.maths.SampleSystem;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.dggs.DiscreteGlobalGridSystems;
import org.apache.sis.referencing.dggs.RefinementLevel;
import org.apache.sis.referencing.dggs.Zone;
import org.apache.sis.referencing.dggs.internal.shared.AbstractZone;
import org.apache.sis.storage.rs.internal.shared.s2.S2;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.referencing.crs.GeographicCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class A5Zone extends AbstractZone<A5Dggrs> {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final long hash;

    public A5Zone(A5Dggrs dggrs, long hash) {
        super(dggrs);
        this.hash = hash;
    }

    @Override
    public Object getIdentifier() {
        return hash;
    }

    @Override
    public CharSequence getTextIdentifier() {
        return A5Dggh.idAsText(hash);
    }

    @Override
    public long getLongIdentifier() {
        return hash;
    }

    @Override
    public String getShapeType() {
        final int level = A5.getResolution(hash);
        switch (level) {
            case -1 : return "sphere"; // the world cell
            case 0 : return "regular pentagon"; // the dodecahedron faces
            default: return "irregular pentagon";
        }
    }

    @Override
    public Double getAreaMetersSquare() {
        final double surfaceArea = DiscreteGlobalGridSystems.computeSurface((GeographicCRS) dggrs.getGridSystem().getCrs());
        final int level = A5.getResolution(hash);
        if (level < 0) return surfaceArea; // the world cell
        return surfaceArea / A5.getNumCells(level);
    }

    @Override
    public RefinementLevel getLocationType() {
        final int level = A5.getResolution(hash);
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Zone getFirstParent() {
        final int level = A5.getResolution(hash);
        if (level <= 0) return null;
        final long parent = A5.cellToParent(hash, level-1);
        return new A5Zone(dggrs, parent);
    }

    @Override
    public Zone getFirstParent(int refinementLevel) {
        final int level = A5.getResolution(hash);
        if (refinementLevel < 0 || refinementLevel >= level) return null;
        return new A5Zone(dggrs, A5.cellToParent(hash, refinementLevel));
    }

    @Override
    public Collection<? extends Zone> getParents() {
        final int level = A5.getResolution(hash);
        if (level > 0) {
            final long parent = A5.cellToParent(hash, level-1);
            return List.of(new A5Zone(dggrs, parent));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        final int level = A5.getResolution(hash);
        final int maxLevel = dggrs.getGridSystem().getHierarchy().getGrids().size();
        if (level+1 >= maxLevel) return Collections.EMPTY_LIST;
        final long[] children = A5.cellToChildren(hash, level+1);
        final List<A5Zone> zones = new ArrayList<>(children.length);
        for (long l : children) {
            zones.add(new A5Zone(dggrs, l));
        }
        return zones;
    }

    /**
     * Returns the zones sharing an edge with this zone.
     */
    @Override
    public Collection<? extends Zone> getNeighbors() {
        if (A5.getResolution(hash) < 0) return Collections.EMPTY_LIST; // the world cell
        final long[] disk = A5.uncompact(A5.gridDisk(hash, 1));
        final List<A5Zone> zones = new ArrayList<>(disk.length);
        for (long l : disk) {
            if (l != hash && !A5.isCompactionMarker(l)) {
                zones.add(new A5Zone(dggrs, l));
            }
        }
        return zones;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        if (hash == A5.WORLD_CELL) return null; //root sphere
        //final double[][] boundary = A5.cellToBoundary(hash); //default version produces a lot of vertices
        final double[][] boundary = A5.cellToBoundary(hash, false, 1);

        if (boundary == null) return null;
        final List<S2Point> contour = new ArrayList<>(boundary.length);
        for (int i = 0, n = boundary.length; i < n ; i++) {
            contour.add(S2LatLng.fromDegrees(boundary[i][1], boundary[i][0]).toPoint());
        }
        return S2.toGeographicExtent(new S2Polygon(new S2Loop(contour)));
    }

    @Override
    public DirectPosition getPosition() {
        final double[] lonlat = A5.cellToLonLat(hash);
        return new DirectPosition2D(CRS84.getCoordinateReferenceSystem(), lonlat[0], lonlat[1]);
    }

}
