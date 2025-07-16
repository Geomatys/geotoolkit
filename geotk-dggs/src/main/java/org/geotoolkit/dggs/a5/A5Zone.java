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
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.storage.dggs.RefinementLevel;
import org.geotoolkit.storage.dggs.ZonalIdentifier;
import org.geotoolkit.storage.dggs.Zone;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.extent.GeographicExtent;
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
    public ZonalIdentifier getIdentifier() {
        return new ZonalIdentifier.Long(hash);
    }

    @Override
    public long getIndexedIdentifier() {
        return hash;
    }

    @Override
    public InternationalString getGeographicIdentifier() {
        return new SimpleInternationalString("" + Long.toUnsignedString(hash));
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
    public Geometry getGeometry() {
        if (hash == 0) return null; //root sphere
        final Vector2D.Double[] boundary = A5.cellToBoundary(hash);
        if (boundary == null) return null;
        final List<Vector2D.Double> contour = List.of(boundary);
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
        if (hash == 0) return CRS.getDomainOfValidity(CommonCRS.WGS84.normalizedGeographic());  //root sphere
        return getGeometry().getEnvelope();
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
