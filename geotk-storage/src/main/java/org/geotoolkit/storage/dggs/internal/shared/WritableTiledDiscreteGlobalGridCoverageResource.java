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

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class WritableTiledDiscreteGlobalGridCoverageResource extends TiledDiscreteGlobalGridCoverageResource {

    /**
     * Set tile values.
     *
     * @param identifierOrZone must be a valid tile zone identifieri n tile level range
     * @param data tile data, null to erase tile.
     * @throws DataStoreException
     */
    public abstract void setZoneTile(Object identifierOrZone, DiscreteGlobalGridCoverage data) throws DataStoreException;
}
