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

import com.google.common.geometry.S2Polygon;
import java.util.stream.Stream;
import org.apache.sis.geometries.math.Vector2D;
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
final class A5Dgg extends AbstractDiscreteGlobalGrid {

    public A5Dgg(A5Dggh dggh, int level) {
        super(dggh, level);
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

        final long hash = A5.lonLatToCell(new Vector2D.Double(dp.getCoordinate(0), dp.getCoordinate(1)), level);
        return new A5Zone((A5Dggrs) hierarchy.dggrs, hash);
    }

    @Override
    public Stream<Zone> getZones(GeographicExtent extent) throws TransformException {
        if (extent == null && level == 0) {
            return Stream.of(hierarchy.getZone(0l));
        }

        //search from root
        final S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(extent);
        try (Stream<Zone> zones = hierarchy.getGrids().get(0).getZones()) {
            return DiscreteGlobalGridSystems.spatialSearch(zones.toList(), level, geometry);
        }
    }

}
