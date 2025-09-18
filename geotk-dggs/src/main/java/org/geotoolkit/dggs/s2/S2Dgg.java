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

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2CellUnion;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2RegionCoverer;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractDiscreteGlobalGrid;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.opengis.geometry.DirectPosition;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class S2Dgg extends AbstractDiscreteGlobalGrid {

    private final List<Zone> roots;

    public S2Dgg(S2Dggh dggh, int level) {
        super(dggh, level);

        if (level == 0) {
            roots = List.of(
                new S2Zone((S2Dggrs) hierarchy.dggrs, S2CellId.fromFace(0).id()),
                new S2Zone((S2Dggrs) hierarchy.dggrs, S2CellId.fromFace(1).id()),
                new S2Zone((S2Dggrs) hierarchy.dggrs, S2CellId.fromFace(2).id()),
                new S2Zone((S2Dggrs) hierarchy.dggrs, S2CellId.fromFace(3).id()),
                new S2Zone((S2Dggrs) hierarchy.dggrs, S2CellId.fromFace(4).id()),
                new S2Zone((S2Dggrs) hierarchy.dggrs, S2CellId.fromFace(5).id()));
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
        S2CellId cid = S2CellId.fromLatLng(S2LatLng.fromDegrees(dp.getCoordinate(1), dp.getCoordinate(0)));
        final long zid = cid.parent(level).id();
        return new S2Zone((S2Dggrs) hierarchy.dggrs, zid);
    }

    @Override
    public Stream<Zone> getZones(GeographicExtent extent) throws TransformException {
        if (extent == null && level == 0) {
            return roots.stream();
        }

        final S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(extent);
        if (geometry == null) {
            //search from root
            try (Stream<Zone> zones = hierarchy.getGrids().get(0).getZones()) {
                return DiscreteGlobalGridSystems.spatialSearch(zones.toList(), level, geometry);
            }

        } else {
            final S2RegionCoverer coverer = S2RegionCoverer.builder().setMinLevel(level).setMaxLevel(level).build();
            final S2CellUnion covering = coverer.getCovering(geometry);

            return covering.cellIds().stream().flatMap(new Function<S2CellId, Stream<S2CellId>>() {
                @Override
                public Stream<S2CellId> apply(S2CellId t) {
                    //enforce requested level, S2 may return upper levels
                    if (t.level() == level) return Stream.of(t);
                    return StreamSupport.stream(t.childrenAtLevel(level).spliterator(), false);
                }
            }).mapToLong(S2CellId::id).mapToObj((id) -> new S2Zone((S2Dggrs) hierarchy.dggrs, id));
        }

    }
}
