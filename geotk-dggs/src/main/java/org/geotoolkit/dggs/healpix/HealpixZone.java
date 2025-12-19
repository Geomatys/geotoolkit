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
import org.apache.sis.geometries.math.SampleSystem;
import org.apache.sis.geometries.math.Vector2D;
import org.apache.sis.geometries.math.Vectors;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.referencing.dggs.RefinementLevel;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractZone;
import org.geotoolkit.storage.rs.internal.shared.s2.S2;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.extent.BoundingPolygon;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.GeographicCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HealpixZone extends AbstractZone<HealpixDggrs> {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final long hash;
    private final int level;
    private final long npixel;

    public HealpixZone(HealpixDggrs dggrs, long hash) {
        super(dggrs);
        this.hash = hash;
        this.level = FitsSerialization.getOrder(hash)-1;
        this.npixel = FitsSerialization.getPixel(hash);
    }

    public HealpixZone(HealpixDggrs dggrs, int level, long npixel) {
        super(dggrs);
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
        return HealpixDggh.idAsText(hash);
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
    public RefinementLevel getLocationType() {
        return new RefinementLevel(dggrs, level);
    }

    @Override
    public Zone getFirstParent() {
        if (level == 0) return null;
        return new HealpixZone(dggrs, level-1, npixel /4);
    }

    @Override
    public Zone getFirstParent(int refinementLevel) {
        if (level == 0) return null;
        if (refinementLevel < 0 || refinementLevel >= level) throw new IllegalArgumentException("Invalid refinement level");
        long n = npixel;
        for (int i = refinementLevel; i < level; i++) {
            n /= 4;
        }
        return new HealpixZone(dggrs, refinementLevel, n);
    }

    @Override
    public Collection<? extends Zone> getParents() {
        if (level == 0) return Collections.EMPTY_SET;
        return List.of(new HealpixZone(dggrs, level-1, npixel /4)
        );
    }

    @Override
    public Collection<? extends Zone> getChildren() {
        final int maxLevel = dggrs.getGridSystem().getHierarchy().getGrids().size();
        if (level+1 >= maxLevel) return Collections.EMPTY_LIST;
        final int clevel = level +1;
        final long base = npixel*4;
        return List.of(new HealpixZone(dggrs, clevel, base),
                new HealpixZone(dggrs, clevel, base+1),
                new HealpixZone(dggrs, clevel, base+2),
                new HealpixZone(dggrs, clevel, base+3)
        );
    }

    @Override
    public Collection<? extends Zone> getNeighbors() {
        final NeighbourSelector selector = Healpix.getNested(level).newNeighbourSelector();
        final NeighbourList neighbours = selector.neighbours(npixel);
        final FlatHashIterator iterator = neighbours.iterator();
        final List<HealpixZone> zones = new ArrayList<>(neighbours.size());
        while (iterator.hasNext()) {
            zones.add(new HealpixZone(dggrs, level, iterator.next()));
        }
        return zones;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        final VerticesAndPathComputer nested = Healpix.getNested(level).newVerticesAndPathComputer();

        final Vector2D.Double north = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.N));
        final Vector2D.Double south = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.S));
        final Vector2D.Double east = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.E));
        final Vector2D.Double west = toLonLat(nested.vertex(npixel, CompassPoint.Cardinal.W));
        List<S2Point> contour = List.of(
                S2LatLng.fromDegrees(south.y, south.x).toPoint(),
                S2LatLng.fromDegrees(east.y, east.x).toPoint(),
                S2LatLng.fromDegrees(north.y, north.x).toPoint(),
                S2LatLng.fromDegrees(west.y, west.x).toPoint()
        );

        S2Loop loop = new S2Loop(contour);
        if (!loop.isNormalized()) {
            loop.invert();
        }

        return S2.toGeographicExtent(new S2Polygon(loop));
    }

    @Override
    public GeographicExtent getGeographicExtent(int subdivision) {
        final VerticesAndPathComputer nested = Healpix.getNested(level).newVerticesAndPathComputer();

        double[][] vertices = nested.pathAlongCellEdge(npixel, CompassPoint.Cardinal.N, false, subdivision); //we want them counter-clockwise to follow S2 convention
        List<S2Point> contour = new ArrayList<>(vertices.length-1); //do not duplicate last point
        for (int i = 0; i < vertices.length-1; i++) {
            Vector2D.Double v = toLonLatRad(vertices[i]);
            S2LatLng latlon = S2LatLng.fromRadians(v.y, v.x);
            if (!latlon.isValid()) {
                throw new IllegalArgumentException();
            }
            S2Point pt = latlon.toPoint();
            contour.add(pt);
        }

        S2Loop loop = new S2Loop(contour);
        if (!loop.isNormalized()) {
            loop.invert();
        }

        return S2.toGeographicExtent(new S2Polygon(loop));
    }

    @Override
    public DirectPosition getPosition() {
        final VerticesAndPathComputer nested = Healpix.getNested(level).newVerticesAndPathComputer();
        final double[] center = nested.center(npixel);
        return Vectors.asDirectPostion(toLonLat(center));
    }

    /**
     *
     * @param radPoint values in range {[0 .. 2Pi], [-Pi/2 .. +Pi/2]}
     * @return vaues in range {[-180 .. 180],[-90 .. 90]}
     */
    private Vector2D.Double toLonLat(double[] radPoint) {
        Vector2D.Double v = toLonLatRad(radPoint);
        v.x = Math.toDegrees(v.x);
        v.y = Math.toDegrees(v.y);
        return v;
    }

    /**
     *
     * @param radPoint values in range {[0 .. 2Pi], [-Pi/2 .. +Pi/2]}
     * @return vaues in range {[-180 .. 180],[-90 .. 90]}
     */
    private Vector2D.Double toLonLatRad(double[] radPoint) {
        double lon = radPoint[0];
        if (lon >= Math.PI) lon -= (Math.PI + Math.PI);
        double lat = radPoint[1];
        return new Vector2D.Double(CRS84,
                lon,
                lat
        );
    }

}
