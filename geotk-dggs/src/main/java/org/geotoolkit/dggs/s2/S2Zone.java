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
package org.geotoolkit.dggs.s2;

import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.referencing.dggs.RefinementLevel;
import org.geotoolkit.referencing.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class S2Zone implements Zone {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final S2Dggrs dggrs;
    private final long hash;

    public S2Zone(S2Dggrs dggrs, long hash) {
        this.dggrs = dggrs;
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
        return S2Dggh.idAsText(hash);
    }

    @Override
    public Collection<? extends InternationalString> getAlternativeGeographicIdentifiers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getShapeType() {
        return "square";
    }

    @Override
    public Double getAreaMetersSquare() {
        return new S2Cell(new S2CellId(hash)).exactArea();
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
        final int level = new S2CellId(hash).level();
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Collection<? extends Zone> getParents() {
        final S2CellId cellId = new S2CellId(hash);
        if (cellId.isFace()) return Collections.EMPTY_LIST;
        return List.of(new S2Zone(dggrs, cellId.parent().id()));
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        final S2CellId cellId = new S2CellId(hash);
        if (cellId.isLeaf()) return Collections.EMPTY_LIST;
        return List.of(
                new S2Zone(dggrs, cellId.child(0).id()),
                new S2Zone(dggrs, cellId.child(1).id()),
                new S2Zone(dggrs, cellId.child(2).id()),
                new S2Zone(dggrs, cellId.child(3).id())
        );
    }

    @Override
    public Collection<? extends Zone> getNeighbors() {
        final S2CellId cellId = new S2CellId(hash);
        final S2CellId[] neighbors = new S2CellId[4];
        cellId.getEdgeNeighbors(neighbors);
        return List.of(
                new S2Zone(dggrs, neighbors[0].id()),
                new S2Zone(dggrs, neighbors[1].id()),
                new S2Zone(dggrs, neighbors[2].id()),
                new S2Zone(dggrs, neighbors[3].id())
        );
    }

    @Override
    public TemporalExtent getTemporalExtent() {
        return null;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        final S2CellId cellId = new S2CellId(hash);
        final S2Cell cell = new S2Cell(cellId);
        final List<S2Point> contour = List.of(
            cell.getVertex(0),
            cell.getVertex(1),
            cell.getVertex(2),
            cell.getVertex(3));
        return DiscreteGlobalGridSystems.toGeographicExtent(new S2Polygon(new S2Loop(contour)));
    }

    @Override
    public DirectPosition getPosition() {
        final S2CellId cellId = new S2CellId(hash);
        final S2LatLng latLng = cellId.toLatLng();
        return new DirectPosition2D(CRS84.getCoordinateReferenceSystem(), latLng.lngDegrees(), latLng.latDegrees());
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
        final S2Zone other = (S2Zone) obj;
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
