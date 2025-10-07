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

import com.google.common.geometry.S2Polygon;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.collection.BackingStoreException;
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
final class H3Dgg extends AbstractDiscreteGlobalGrid {

    private final List<Zone> roots;

    public H3Dgg(H3Dggh dggh, int level) {
        super(dggh, level);

        if (level == 0) {
            roots = H3Dggrs.H3.getRes0Cells().stream().map((Long t) -> {
                try {
                    return dggh.getZone(t);
                } catch (TransformException ex) {
                    throw new BackingStoreException(ex);
                }
            }).toList();
        } else {
            roots = null;
        }
    }

    @Override
    public Zone getZone(DirectPosition dp) throws TransformException {
        final CoordinateReferenceSystem baseCrs = hierarchy.dggrs.getGridSystem().getCrs();
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

        final long hash = H3Dggrs.H3.latLngToCell(dp.getCoordinate(1), dp.getCoordinate(0), level);
        return new H3Zone((H3Dggrs) hierarchy.dggrs, hash);
    }

    @Override
    public Stream<Zone> getZones(Envelope env) throws TransformException {
        return super.getZones(env);
        //Bugged
        /*
        env = Envelopes.transform(env, baseCrs);
        final double minX = env.getMinimum(0);
        final double minY = env.getMinimum(1);
        final double maxX = env.getMaximum(0);
        final double maxY = env.getMaximum(1);

        final List<Long> candidate = H3Dggrs.H3.polygonToCells(List.of(
                new LatLng(minY, minX),
                new LatLng(minY, maxX),
                new LatLng(maxY, maxX),
                new LatLng(maxY, minX),
                new LatLng(minY, minX)),
                Collections.EMPTY_LIST, level);
        return candidate.stream().map((h) -> new H3Zone(dggrs, h));
        */
    }

    @Override
    public Stream<Zone> getZones(GeographicExtent extent) throws TransformException {
        //Bugged we can't trust H3 intersection results
//        final List<S2Point> vertices = geometry.getLoops().get(0).orientedVertices();
//        final int size = vertices.size();
//        final List<LatLng> latlons = new ArrayList<>(size);
//        for (int i = 0; i < size; i++) {
//            final S2LatLng latlon = new S2LatLng(vertices.get(i));
//            latlons.add(new LatLng(latlon.latDegrees(), latlon.lngDegrees()));
//        }
//        latlons.add(latlons.get(0));
//
//        //we use PolygonToCellsFlags.containment_overlapping_bbox instead of PolygonToCellsFlags.containment_overlapping
//        //because the containment_overlapping fails to find some zones
//        final List<Long> children = H3Dggrs.H3.polygonToCellsExperimental(latlons, null, level, PolygonToCellsFlags.containment_overlapping_bbox);
//        //for unknown reason the api do not always return the zones at the requested resolution level
//        final List<H3Zone> candidates = new ArrayList<>(children.size());
//        for (int i = 0, n = children.size(); i < n; i++) {
//            final long zid = children.get(i);
//            int zlevel = H3Dggrs.H3.getResolution(zid);
//            if (zlevel == level) {
//                candidates.add(new H3Zone(dggrs, zid));
//            } else if (zlevel < zid) {
//                H3Dggrs.H3.
//            }
//        }
//
//        //we need to make a more accurate test
//        for ()
//        return children.stream().map((Long t) -> new H3Zone(dggrs, t));

        if (extent == null && level == 0) {
            return roots.stream();
        }

        //search from root
        final S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(extent);
        try (Stream<Zone> zones = hierarchy.getGrids().get(0).getZones()) {
            return DiscreteGlobalGridSystems.spatialSearch(zones.toList(), level, geometry);
        }
    }

}
