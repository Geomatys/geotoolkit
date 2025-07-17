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

import org.geotoolkit.storage.coverage.BandedCoverageExt;

/**
 * A coverage which is structured by a collection of Zone defined by a DiscreteGlobalGridReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DiscreteGlobalGridCoverage extends BandedCoverageExt {

    /**
     * Returns the coverage geometry.
     *
     * @return geometry of the coverage
     */
    public abstract DiscreteGlobalGridGeometry getGeometry();

    /**
     * Create an iterator over the coverage zones.
     * No assumption should be made on the iteration order.
     *
     * @return iterator, not null
     */
    public abstract ZoneIterator createIterator();

    /**
     * Create a writable iterator over the coverage zones.
     * No assumption should be made on the iteration order.
     *
     * @return writable iterator, not null
     */
    public abstract WritableZoneIterator createWritableIterator();
}
