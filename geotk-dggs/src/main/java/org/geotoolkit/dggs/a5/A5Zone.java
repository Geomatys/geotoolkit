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
package org.geotoolkit.dggs.a5;

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.dggs.a5.internal.Cell;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.referencing.dggs.RefinementLevel;
import org.geotoolkit.referencing.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class A5Zone implements Zone {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final A5Dggrs dggrs;
    private final long hash;

    public A5Zone(A5Dggrs dggrs, long hash) {
        this.dggrs = dggrs;
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
    public Collection<? extends InternationalString> getAlternativeGeographicIdentifiers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getShapeType() {
        final int level = A5.getResolution(hash);
        switch (level) {
            case 0: return "sphere";
            case 1 : return "regular pentagon";
            case 2 : return "triangle";
            default: return "irregular pentagon";
        }
    }

    @Override
    public Double getAreaMetersSquare() {
        final double surfaceArea = DiscreteGlobalGridSystems.computeSurface((GeographicCRS) dggrs.getGridSystem().getCrs());
        final int level = A5.getResolution(hash);
        switch (level) {
            case 0 : return surfaceArea; // the sphere
            case 1 : return surfaceArea / 12; // the pentagons
            case 2 : return surfaceArea / 60; // the quintants
            case 3 : return surfaceArea / 240; // the pentagons
            default : return (surfaceArea / 240) / Math.pow(4, level-3);
        }
    }

    @Override
    public Double volumeMetersCube() {
        return null;
    }

    @Override
    public Double temporalDurationSeconds() {
        return null;
    }

    @Override
    public RefinementLevel getLocationType() {
        final int level = A5.getResolution(hash);
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Collection<? extends Zone> getParents() {
        final int level = A5.getResolution(hash);
        if (level != 0) {
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
        final List<Long> children = A5.cellToChildren(hash, level+1);
        final List<A5Zone> zones = new ArrayList<>(children.size());
        for (Long l : children) {
            zones.add(new A5Zone(dggrs, l));
        }
        return zones;
    }

    @Override
    public Collection<? extends Zone> getNeighbors() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public TemporalExtent getTemporalExtent() {
        return null;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        if (hash == 0) return null; //root sphere
        //final Vector2D.Double[] boundary = A5.cellToBoundary(hash); //default version produces a lot of vertices
        final Vector2D.Double[] boundary = Cell.cellToBoundary(hash, new Cell.CellToBoundaryOptions(false, 1));

        if (boundary == null) return null;
        final List<S2Point> contour = new ArrayList<>(boundary.length);
        for (int i = 0, n = boundary.length; i < n ; i++) {
            contour.add(S2LatLng.fromDegrees(boundary[i].y, boundary[i].x).toPoint());
        }
        return DiscreteGlobalGridSystems.toGeographicExtent(new S2Polygon(new S2Loop(contour)));
    }

    @Override
    public DirectPosition getPosition() {
        final Vector2D.Double lonlat = A5.cellToLonLat(hash);
        return new DirectPosition2D(CRS84.getCoordinateReferenceSystem(), lonlat.x, lonlat.y);
    }

    @Override
    public Party getAdministrator() {
        return dggrs.getOverallOwner();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final A5Zone other = (A5Zone) obj;
        if (!Objects.equals(this.hash, other.hash)) {
            return false;
        }
        return Objects.equals(this.dggrs, other.dggrs);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.dggrs);
        hash = 53 * hash + Objects.hashCode(this.hash);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getGeographicIdentifier();
    }
}
