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
package org.geotoolkit.dggs.h3;

import com.uber.h3core.AreaUnit;
import com.uber.h3core.util.LatLng;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.sis.geometries.Geometry;
import org.apache.sis.geometries.GeometryFactory;
import org.apache.sis.geometries.LinearRing;
import org.apache.sis.geometries.PointSequence;
import org.apache.sis.geometries.math.DataType;
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.TupleArray;
import org.apache.sis.geometries.math.TupleArrays;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.storage.dggs.RefinementLevel;
import org.geotoolkit.storage.dggs.ZonalIdentifier;
import org.geotoolkit.storage.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class H3Zone implements Zone {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final H3Dggrs dggrs;
    private final long hash;

    public H3Zone(H3Dggrs dggrs, long hash) {
        this.dggrs = dggrs;
        this.hash = hash;
    }

    @Override
    public ZonalIdentifier getIdentifier() {
        return new ZonalIdentifier.Long(hash);
    }

    @Override
    public long getIndexedIdentifier() {
        return hash;
    }

    @Override
    public InternationalString getGeographicIdentifier() {
        return new SimpleInternationalString("" + hash);
    }

    @Override
    public Collection<? extends InternationalString> getAlternativeGeographicIdentifiers() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getShapeType() {
        return "hexagon";
    }

    @Override
    public Double getAreaMetersSquare() {
        return H3Dggrs.H3.getHexagonAreaAvg(H3Dggrs.H3.getResolution(hash), AreaUnit.m2);
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
        final int level = H3Dggrs.H3.getResolution(hash);
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Collection<? extends Zone> getParents() {
        final int level = H3Dggrs.H3.getResolution(hash);
        if (level != 0) {
            final long parent = H3Dggrs.H3.cellToParent(hash, level-1);
            return List.of(new H3Zone(dggrs, parent));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        final int level = H3Dggrs.H3.getResolution(hash);
        final int maxLevel = dggrs.getGridSystem().getHierarchy().getGrids().size();
        if (level+1 >= maxLevel) return Collections.EMPTY_LIST;
        final List<Long> children = H3Dggrs.H3.cellToChildren(hash, level+1);
        final List<H3Zone> zones = new ArrayList<>(children.size());
        for (Long l : children) {
            zones.add(new H3Zone(dggrs, l));
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
    public Geometry getGeometry() {
        final List<LatLng> boundary = H3Dggrs.H3.cellToBoundary(hash);
        if (boundary == null) return null;
        final List<Vector2D.Double> contour = new ArrayList<>(boundary.size()+1);
        for (LatLng ll : boundary) {
            contour.add(new Vector2D.Double(ll.lng, ll.lat));
        }
        contour.add(new Vector2D.Double(boundary.get(0).lng, boundary.get(0).lat));
        final TupleArray positions = TupleArrays.of(contour, CRS84, DataType.DOUBLE);
        final PointSequence points = GeometryFactory.createSequence(positions);
        final LinearRing exterior = GeometryFactory.createLinearRing(points);
        return GeometryFactory.createPolygon(exterior, Collections.EMPTY_LIST);
    }

    @Override
    public GeographicExtent getGeographicExtent() {
        final Envelope env = getEnvelope();
        return new DefaultGeographicBoundingBox(env.getMinimum(0), env.getMaximum(0), env.getMinimum(1), env.getMaximum(1));
    }

    @Override
    public Envelope getEnvelope() {
        final Geometry geometry = getGeometry();
        if (geometry == null) {
            return CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic());
        }
        return geometry.getEnvelope();
    }

    @Override
    public DirectPosition getPosition() {
        final LatLng latLng = H3Dggrs.H3.cellToLatLng(hash);
        return new DirectPosition2D(CRS84.getCoordinateReferenceSystem(), latLng.lng, latLng.lat);
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
        final H3Zone other = (H3Zone) obj;
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
