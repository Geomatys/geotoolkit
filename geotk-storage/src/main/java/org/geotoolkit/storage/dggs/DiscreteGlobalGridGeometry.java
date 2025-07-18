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
import org.apache.sis.util.ArgumentChecks;

/**
 * DGGRS coverage geometry.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DiscreteGlobalGridGeometry {

    private final DiscreteGlobalGridReferenceSystem dggrs;
    private final List<ZonalIdentifier> zones;

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


}
