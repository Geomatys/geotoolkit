/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025-2026, Geomatys
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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.apache.sis.referencing.dggs.Zone;
import org.apache.sis.referencing.dggs.internal.shared.AbstractDiscreteGlobalGrid;
import org.apache.sis.storage.dggs.DiscreteGlobalGridSystems;
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
final class A5Dgg extends AbstractDiscreteGlobalGrid<A5Dggh> {

    public A5Dgg(A5Dggh dggh, int level) {
        super(dggh, level);
    }

    @Override
    public Zone getZone(DirectPosition dp) throws TransformException {
        final CoordinateReferenceSystem baseCrs = hierarchy.dggrs.dggs.getCrs();
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

        final long hash = A5.lonLatToCell(new double[]{dp.getCoordinate(0), dp.getCoordinate(1)}, level);
        return new A5Zone(hierarchy.dggrs, hash);
    }

    /**
     * Returns the number of zones at this level, saturated to {@link Long#MAX_VALUE}
     * for the finest level which has more than 2^63 zones.
     */
    @Override
    public long getZoneCount() {
        final BigInteger count = A5.getNumCellsExact(level);
        return count.bitLength() < Long.SIZE ? count.longValue() : Long.MAX_VALUE;
    }

    @Override
    public Stream<Zone> getZones(GeographicExtent extent) throws TransformException {
        //the 12 dodecahedron faces
        final long[] res0 = A5.getRes0Cells();
        final List<Zone> roots = new ArrayList<>(res0.length);
        for (long cell : res0) {
            roots.add(new A5Zone(hierarchy.dggrs, cell));
        }
        if (extent == null && level == 0) {
            return roots.stream();
        }

        //search from root
        final S2Polygon geometry = DiscreteGlobalGridSystems.toS2Polygon(extent);
        return DiscreteGlobalGridSystems.spatialSearch(roots, level, geometry);
    }

    @Override
    protected long getZoneLongIdentifier(double[] source, int soffset) {
        return A5.lonLatToCell(new double[]{source[soffset], source[soffset+1]}, level);
    }

    @Override
    protected void getZonePosition(long zoneId, double[] target, int toffset) {
        final double[] lonlat = A5.cellToLonLat(zoneId);
        target[toffset] = lonlat[0];
        target[toffset+1] = lonlat[1];
    }

}
