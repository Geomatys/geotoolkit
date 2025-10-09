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
package org.geotoolkit.referencing.dggs;

import java.util.List;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Integrated system comprising a hierarchy of discrete global grids, spatiotemporal referencing by zonal
 * identifiers and functions for quantization, zonal query, and interoperability.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#term-dggs
 */
public interface DiscreteGlobalGridSystem {

    /**
     * @return structure of the DGGS grids
     */
    DiscreteGlobalGridHierarchy getHierarchy();

    /**
     * @return base CRS
     */
    CoordinateReferenceSystem getCrs();

    /**
     * @return name of the DGGS base polyhedron geometry
     */
    String getBasePolyhedron();

    /**
     * @return number of subdivision at each refinement level
     */
    int getRefinementRatio();

    /**
     * @return characteristics of the cell geometry refinement
     */
    List<RefinementStrategy> getRefinementStrategy();

    /**
     * List of characteristics that constraint the grid cells in this DGGS in decreasing order of priority.
     *
     * @return list, never null.
     */
    List<GridConstraints> getGridConstraints();

    /**
     * @return number of spatial dimensions
     */
    int getSpatialDimensions();

    /**
     * @return number of temporal dimensions
     */
    int getTemporalDimensions();

    /**
     * @return name of the possible geometry shapes in the DGGS
     */
    List<String> getZoneTypes();

    /**
     * @return alignement parameters of the base polyhedron compared to the CRS
     */
    PolyhedronParameters getParameters();
}
