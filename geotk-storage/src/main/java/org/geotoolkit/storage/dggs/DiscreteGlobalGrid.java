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

/**
 * Set of zones at the same refinement level, that uniquely and completely cover a globe.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-dgg
 */
public interface DiscreteGlobalGrid {

    /**
     * Numerical order of a discrete global grid in the tessellation sequence
     * <p>
     * The discrete global grid with the least number of zones has a refinement level of 0.
     *
     * @return refinement level
     * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-refinement-level
     */
    int getRefinementLevel();

}
