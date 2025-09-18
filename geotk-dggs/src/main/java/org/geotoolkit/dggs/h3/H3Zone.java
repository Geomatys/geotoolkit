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

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.uber.h3core.AreaUnit;
import com.uber.h3core.util.LatLng;
import java.util.ArrayList;
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
final class H3Zone implements Zone {

    private static final SampleSystem CRS84 = SampleSystem.of(CommonCRS.WGS84.normalizedGeographic());

    private final H3Dggrs dggrs;
    private final long hash;

    public H3Zone(H3Dggrs dggrs, long hash) {
        this.dggrs = dggrs;
        this.hash = hash;
    }

    @Override
    public Object getIdentifier() {
        return hash;
    }

    @Override
    public CharSequence getTextIdentifier() {
        return H3Dggh.idAsText(hash);
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
    public Collection<H3Zone> getParents() {
        final int level = H3Dggrs.H3.getResolution(hash);
        if (level == 0) return Collections.EMPTY_LIST;

        final long directParentId = H3Dggrs.H3.cellToParent(hash, level-1);
        final H3Zone directParent = new H3Zone(dggrs, directParentId);

        final long positioninDirectParent = H3Dggrs.H3.cellToChildPos(hash, level-1);
        if (positioninDirectParent == 0) {
            //this is the center child cell, it has only one parent
            return List.of(directParent);
        } else {
            //search in the parent neighbors
            final List<H3Zone> parents = new ArrayList<>();
            parents.add(directParent);
            for (H3Zone candidate : directParent.getNeighbors()) {
                if (candidate.isAdjacentChild(hash)) {
                    parents.add(candidate);
                }
            }
            return parents;
        }
    }

    /**
     * Returns the H3 hierarchival parent, one or null.
     */
    public H3Zone getHierarchicalParent() {
        final int level = H3Dggrs.H3.getResolution(hash);
        if (level == 0) return null;
        final long parent = H3Dggrs.H3.cellToParent(hash, level-1);
        return new H3Zone(dggrs, parent);
    }

    @Override
    public Collection<H3Zone> getChildren() {
        final int level = H3Dggrs.H3.getResolution(hash);
        final int maxLevel = dggrs.getGridSystem().getHierarchy().getGrids().size();
        if (level+1 >= maxLevel) return Collections.EMPTY_LIST;

        //we want all children covering this zone
        //the order of children is constant see https://h3geo.org/docs/library/index/cell
        final List<Long> children = H3Dggrs.H3.cellToChildren(hash, level+1);

        if (children.size() == 7) {
            //hexagon cell

            //we want the cells of the neighbors which are missing to fill the area
            List<Long> child1Neighors = H3Dggrs.H3.gridDisk(children.get(1), 1);
            List<Long> child2Neighors = H3Dggrs.H3.gridDisk(children.get(2), 1);
            List<Long> child3Neighors = H3Dggrs.H3.gridDisk(children.get(3), 1);
            List<Long> child4Neighors = H3Dggrs.H3.gridDisk(children.get(4), 1);
            List<Long> child5Neighors = H3Dggrs.H3.gridDisk(children.get(5), 1);
            List<Long> child6Neighors = H3Dggrs.H3.gridDisk(children.get(6), 1);
            child1Neighors.removeAll(children);
            child2Neighors.removeAll(children);
            child3Neighors.removeAll(children);
            child4Neighors.removeAll(children);
            child5Neighors.removeAll(children);
            child6Neighors.removeAll(children);
            //keep only cell which are retained in the adjacent
            List<Long> child1NeighorsBis = new ArrayList<>(child1Neighors);
            child1Neighors.retainAll(child3Neighors);
            child3Neighors.retainAll(child2Neighors);
            child2Neighors.retainAll(child6Neighors);
            child6Neighors.retainAll(child4Neighors);
            child4Neighors.retainAll(child5Neighors);
            child5Neighors.retainAll(child1NeighorsBis);

            final List<H3Zone> subzones = new ArrayList<>(13);
            subzones.add(new H3Zone(dggrs, children.get(0)));
            subzones.add(new H3Zone(dggrs, children.get(1)));
            subzones.add(new H3Zone(dggrs, children.get(2)));
            subzones.add(new H3Zone(dggrs, children.get(3)));
            subzones.add(new H3Zone(dggrs, children.get(4)));
            subzones.add(new H3Zone(dggrs, children.get(5)));
            subzones.add(new H3Zone(dggrs, children.get(6)));
            subzones.add(new H3Zone(dggrs, child1Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child2Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child3Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child4Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child5Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child6Neighors.get(0)));

            return subzones;

        } else {
            //pentagon cell
            //in this mode the index 3 has been removed, so 1 and 2 or near each other

            //we want the cells of the neighbors which are missing to fill the area
            List<Long> child1Neighors = H3Dggrs.H3.gridDisk(children.get(1), 1);
            List<Long> child2Neighors = H3Dggrs.H3.gridDisk(children.get(2), 1);
            List<Long> child3Neighors = H3Dggrs.H3.gridDisk(children.get(3), 1);
            List<Long> child4Neighors = H3Dggrs.H3.gridDisk(children.get(4), 1);
            List<Long> child5Neighors = H3Dggrs.H3.gridDisk(children.get(5), 1);
            child1Neighors.removeAll(children);
            child2Neighors.removeAll(children);
            child3Neighors.removeAll(children);
            child4Neighors.removeAll(children);
            child5Neighors.removeAll(children);
            //keep only cell which are retained in the adjacent
            List<Long> child1NeighorsBis = new ArrayList<>(child1Neighors);
            child1Neighors.retainAll(child2Neighors);
            child2Neighors.retainAll(child4Neighors);
            child4Neighors.retainAll(child3Neighors);
            child3Neighors.retainAll(child5Neighors);
            child5Neighors.retainAll(child1NeighorsBis);

            final List<H3Zone> subzones = new ArrayList<>(13);
            subzones.add(new H3Zone(dggrs, children.get(0)));
            subzones.add(new H3Zone(dggrs, children.get(1)));
            subzones.add(new H3Zone(dggrs, children.get(2)));
            subzones.add(new H3Zone(dggrs, children.get(3)));
            subzones.add(new H3Zone(dggrs, children.get(4)));
            subzones.add(new H3Zone(dggrs, children.get(5)));
            subzones.add(new H3Zone(dggrs, child1Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child2Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child3Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child4Neighors.get(0)));
            subzones.add(new H3Zone(dggrs, child5Neighors.get(0)));

            return subzones;
        }

    }

    private boolean isAdjacentChild(long candidate) {
        final int level = H3Dggrs.H3.getResolution(hash);

        final List<Long> children = H3Dggrs.H3.cellToChildren(hash, level+1);

        if (children.size() == 7) {
            //hexagon cell

            //we want the cells of the neighbors which are missing to fill the area
            if (H3Dggrs.H3.gridDisk(children.get(1), 1).contains(candidate)){
                return H3Dggrs.H3.gridDisk(children.get(5), 1).contains(candidate)
                    || H3Dggrs.H3.gridDisk(children.get(3), 1).contains(candidate);
            } else if (H3Dggrs.H3.gridDisk(children.get(2), 1).contains(candidate)) {
                return H3Dggrs.H3.gridDisk(children.get(3), 1).contains(candidate)
                    || H3Dggrs.H3.gridDisk(children.get(6), 1).contains(candidate);
            } else if (H3Dggrs.H3.gridDisk(children.get(4), 1).contains(candidate)) {
                return H3Dggrs.H3.gridDisk(children.get(6), 1).contains(candidate)
                    || H3Dggrs.H3.gridDisk(children.get(5), 1).contains(candidate);
            } else {
                return false;
            }

        } else {
            //pentagon cell
            //in this mode the index 3 has been removed, so 1 and 2 or near each other

            //we want the cells of the neighbors which are missing to fill the area
            if (H3Dggrs.H3.gridDisk(children.get(1), 1).contains(candidate)){
                return H3Dggrs.H3.gridDisk(children.get(5), 1).contains(candidate)
                    || H3Dggrs.H3.gridDisk(children.get(2), 1).contains(candidate);
            } else if (H3Dggrs.H3.gridDisk(children.get(2), 1).contains(candidate)) {
                return H3Dggrs.H3.gridDisk(children.get(4), 1).contains(candidate);

            } else if (H3Dggrs.H3.gridDisk(children.get(3), 1).contains(candidate)) {
                return H3Dggrs.H3.gridDisk(children.get(4), 1).contains(candidate)
                    || H3Dggrs.H3.gridDisk(children.get(5), 1).contains(candidate);
            } else {
                return false;
            }
        }
    }

    /**
     * Returns the H3 hierarchival childrens, 7 of them.
     * Note : they do not fully cover the parent cell.
     */
    public List<H3Zone> getHierarchicalChildren() {
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
    public Collection<H3Zone> getNeighbors() {
        final List<Long> neighors = H3Dggrs.H3.gridDisk(hash, 1);
        final List<H3Zone> zones = new ArrayList<>(neighors.size()-1);
        for (Long l : neighors) {
            if (l != hash) zones.add(new H3Zone(dggrs, l));
        }
        return zones;
    }

    @Override
    public TemporalExtent getTemporalExtent() {
        return null;
    }

    @Override
    public BoundingPolygon getGeographicExtent() {
        final List<LatLng> boundary = H3Dggrs.H3.cellToBoundary(hash);
        if (boundary == null) return null;
        final List<S2Point> contour = new ArrayList<>(boundary.size());
        for (LatLng ll : boundary) {
            contour.add(S2LatLng.fromDegrees(ll.lat, ll.lng).toPoint());
        }
        return DiscreteGlobalGridSystems.toGeographicExtent(new S2Polygon(new S2Loop(contour)));
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
