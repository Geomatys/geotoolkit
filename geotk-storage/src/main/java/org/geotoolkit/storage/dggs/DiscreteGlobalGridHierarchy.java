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

/**
 * Series of discrete global grids organized in a hierarchy of successive levels of zone refinement,
 * using a specific set of parameters fully establishing the geometry of all zones.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-dggh
 */
public interface DiscreteGlobalGridHierarchy {

    /**
     * Ordered list of grids by refinement level.
     *
     * @return list of grids, never null
     */
    List<DiscreteGlobalGrid> getGrids();

}
