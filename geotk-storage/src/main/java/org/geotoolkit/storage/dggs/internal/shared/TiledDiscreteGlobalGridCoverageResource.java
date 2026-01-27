/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage.dggs.internal.shared;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.measure.IncommensurableException;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class TiledDiscreteGlobalGridCoverageResource extends AbstractResource implements DiscreteGlobalGridResource{

    public TiledDiscreteGlobalGridCoverageResource() {
        super(null);
    }

    @Override
    public final NumberRange<Integer> getAvailableDepths() {
        final NumberRange<Integer> tileDepthRange = getTileAvailableDepths();
        final int minDepth = (int) (tileDepthRange.getMinDouble(true) + getTileRelativeDepth());
        final int maxDepth = (int) (tileDepthRange.getMaxDouble(true) + getTileRelativeDepth());
        return NumberRange.create(minDepth, true, maxDepth, true);
    }

    @Override
    public final int getDefaultDepth() {
        return (int) (getTileAvailableDepths().getMinDouble(true) + getTileRelativeDepth());
    }

    @Override
    public final int getMaxRelativeDepth() {
        return getTileRelativeDepth();
    }

    public abstract NumberRange<Integer> getTileAvailableDepths();

    public abstract int getTileRelativeDepth();

    @Override
    public DiscreteGlobalGridCoverage read(CodedGeometry query, int... range) throws DataStoreException {
        final DiscreteGlobalGridGeometry localGridGeometry = getGridGeometry();
        final DiscreteGlobalGridReferenceSystem localDggrs = localGridGeometry.getReferenceSystem();
        final NumberRange<Integer> availableDepths = getAvailableDepths();

        //convert the query to this DGGRS and depth
        DiscreteGlobalGridGeometry dggrsQuery = DiscreteGlobalGridResource.toDiscreteGlobalGridGeometry(query);
        try {
            dggrsQuery = dggrsQuery.transformTo(localDggrs, availableDepths, getTileRelativeDepth());
        } catch (TransformException | IncommensurableException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        //at this point the query contains a list of data zones we have
        //convert it to tile zones
        final Object[] parentZoneIds;
        if (dggrsQuery.getRelativeDepth() != null && dggrsQuery.getRelativeDepth() == getTileRelativeDepth()) {
            //we can reuse the query parent zones ids directly
            parentZoneIds = dggrsQuery.getBaseZoneIds();
        } else {
            //we must loop on all data zones to find all the parents we need
            final DiscreteGlobalGridHierarchy dggh = localDggrs.getGridSystem().getHierarchy();
            final HashSet<Object> pzoneIds = new HashSet<>();
            final List<Object> zoneIds = dggrsQuery.getZoneIds();
            final int parentLevel = dggh.getZone(zoneIds.get(0)).getLocationType().getRefinementLevel() - getTileRelativeDepth();
            for (Object zid : zoneIds) {
                pzoneIds.add(dggh.getZone(zid).getFirstParent(parentLevel).getIdentifier());
            }
            parentZoneIds = pzoneIds.toArray();
        }

        final List<DiscreteGlobalGridCoverage> tiles = new ArrayList<>();
        for (Object pid : parentZoneIds) {
            DiscreteGlobalGridCoverage coverage = getZoneTile(pid);
            if (coverage != null) tiles.add(coverage);
        }

        try {
            return new TiledDiscreteGlobalGridCoverage(tiles.toArray(DiscreteGlobalGridCoverage[]::new));
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * Get a tile.
     *
     * @param identifierOrZone must be a valid tile zone identifieri n tile level range
     * @return tile or null if the tile do not exist
     * @throws DataStoreException
     */
    public abstract DiscreteGlobalGridCoverage getZoneTile(Object identifierOrZone) throws DataStoreException;

}
