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
package org.geotoolkit.storage.dggs;

import java.util.List;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.referencing.operation.TransformException;

/**
 * DGGRS coverage geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridGeometry {

    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final List<ZonalIdentifier> zones;

    //computed
    private NumberRange<Integer> range;

    public DiscreteGlobalGridGeometry(DiscreteGlobalGridReferenceSystem dggrs, List<ZonalIdentifier> zones) {
        ArgumentChecks.ensureNonNull("dggrs", dggrs);
        ArgumentChecks.ensureNonNull("zones", zones);
        this.dggrs = dggrs;
        this.zones = zones;
    }

    /**
     * Returns the DiscreteGlobalGridReferenceSystem.
     *
     * @return DiscreteGlobalGridReferenceSystem, never null
     */
    public DiscreteGlobalGridReferenceSystem getDiscreteGlobalGridReferenceSystem() {
        return dggrs;
    }

    /**
     * List of zones selected in the geometry.
     *
     * @return List of zone identifiers, never null
     */
    public List<ZonalIdentifier> getZones(){
        return zones;
    }

    /**
     * @return refinement range of zone in the coverage geometry.
     */
    public synchronized NumberRange<Integer> getRefinementRange() {
        if (range != null) return range;

        //find min and max refinement levels in the cells
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        int minRefinement = dggrs.getGridSystem().getHierarchy().getGrids().size();
        int maxRefinement = 0;
        try {
            for (ZonalIdentifier zone : zones) {
                final int level = coder.decode(zone).getLocationType().getRefinementLevel();
                if (level < minRefinement) minRefinement = level;
                if (level > maxRefinement) maxRefinement = level;
            }
        } catch (TransformException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        range = NumberRange.create(minRefinement, true, maxRefinement, true);
        return range;
    }


}
