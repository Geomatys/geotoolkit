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

import cds.healpix.CompassPoint;
import cds.healpix.FlatHashIterator;
import cds.healpix.Healpix;
import cds.healpix.NeighbourList;
import cds.healpix.NeighbourSelector;
import cds.healpix.VerticesAndPathComputer;
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
import org.apache.sis.geometries.math.Vectors;
import org.apache.sis.referencing.CommonCRS;
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
final class NHealpixZone implements Zone {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final NHealpixDggrs dggrs;
    private final long hash;
    private final int level;
    private final long npixel;

    public NHealpixZone(NHealpixDggrs dggrs, long hash) {
        this.dggrs = dggrs;
        this.hash = hash;
        this.level = FitsSerialization.getOrder(hash)-1;
        this.npixel = FitsSerialization.getPixel(hash);
    }

    public NHealpixZone(NHealpixDggrs dggrs, int level, long npixel) {
        this.dggrs = dggrs;
        this.hash = FitsSerialization.getHash(level+1, npixel);
        this.level = level;
        this.npixel = npixel;
    }

    public int getOrder() {
        return level;
    }

    public long getNpixel() {
        return npixel;
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
        return NHealpixDggh.idAsText(hash);
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
        final double surfaceArea = DiscreteGlobalGridSystems.computeSurface((GeographicCRS) dggrs.getGridSystem().getCrs());
        switch (level) {
            case 0 : return surfaceArea / 12; // root cells
            default : return (surfaceArea / 12) / Math.pow(4, level-1);
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
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Collection<? extends Zone> getParents() {
        if (level == 0) return Collections.EMPTY_SET;
        return List.of(
                new NHealpixZone(dggrs, level-1, npixel /4)
        );
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        final int maxLevel = dggrs.getGridSystem().getHierarchy().getGrids().size();
        if (level+1 >= maxLevel) return Collections.EMPTY_LIST;
        final int clevel = level +1;
        final long base = npixel*4;
        return List.of(
                new NHealpixZone(dggrs, clevel, base),
                new NHealpixZone(dggrs, clevel, base+1),
                new NHealpixZone(dggrs, clevel, base+2),
                new NHealpixZone(dggrs, clevel, base+3)
        );
    }

    @Override
    public Collection<? extends Zone> getNeighbors() {
        final NeighbourSelector selector = Healpix.getNested(level).newNeighbourSelector();
        final NeighbourList neighbours = selector.neighbours(npixel);
        final FlatHashIterator iterator = neighbours.iterator();
        final List<NHealpixZone> zones = new ArrayList<>(neighbours.size());
        while (iterator.hasNext()) {
            zones.add(new NHealpixZone(dggrs, level, iterator.next()));
        }
        return zones;
    }

    @Override
    public TemporalExtent getTemporalExtent() {
        return null;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        final VerticesAndPathComputer nested = Healpix.getNested(level).newVerticesAndPathComputer();
        final Vector2D.Double north = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.N));
        final Vector2D.Double south = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.S));
        final Vector2D.Double east = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.E));
        final Vector2D.Double west = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.W));
        final List<S2Point> contour = List.of(
                S2LatLng.fromDegrees(south.y, south.x).toPoint(),
                S2LatLng.fromDegrees(east.y, east.x).toPoint(),
                S2LatLng.fromDegrees(north.y, north.x).toPoint(),
                S2LatLng.fromDegrees(west.y, west.x).toPoint()
        );
        return DiscreteGlobalGridSystems.toGeographicExtent(new S2Polygon(new S2Loop(contour)));
    }

    @Override
    public DirectPosition getPosition() {
        final VerticesAndPathComputer nested = Healpix.getNested(level).newVerticesAndPathComputer();
        final double[] center = nested.center(npixel);
        return Vectors.asDirectPostion(toLonLat(center));
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
        final NHealpixZone other = (NHealpixZone) obj;
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

    /**
     *
     * @param radPoint values in range {[0 .. 2Pi], [-Pi/2 .. +Pi/2]}
     * @return vaues in range {[-180 .. 180],[-90 .. 90]}
     */
    private Vector2D.Double toLonLat(double[] radPoint) {
        double lon = radPoint[0];
        if (lon >= Math.PI) lon -= (Math.PI + Math.PI);
        double lat = radPoint[1];
        return new Vector2D.Double(CRS84,
                Math.toDegrees(lon),
                Math.toDegrees(lat)
        );
    }

}
