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

import org.geotoolkit.storage.rs.AddressIterator;
import org.geotoolkit.storage.rs.ReferencedGridCoverage;
import org.geotoolkit.storage.rs.WritableAddressIterator;

/**
 * A coverage which is structured by a collection of Zone defined by a DiscreteGlobalGridReferenceSystem.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class DiscreteGlobalGridCoverage extends ReferencedGridCoverage {

    /**
     * Returns the coverage geometry.
     *
     * @return geometry of the coverage
     */
    @Override
    public abstract DiscreteGlobalGridGeometry getGeometry();

    /**
     * Create an iterator over the coverage zones.
     * No assumption should be made on the iteration order.
     *
     * @return iterator, not null
     */
    @Override
    public abstract AddressIterator createIterator();

    /**
     * Create a writable iterator over the coverage zones.
     * No assumption should be made on the iteration order.
     *
     * @return writable iterator, not null
     */
    @Override
    public abstract WritableAddressIterator createWritableIterator();
}
