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
package org.geotoolkit.dggs.h3;

import java.util.List;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.dggs.CellConstraints;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystem;
import org.geotoolkit.storage.dggs.PolyhedronOrientation;
import org.geotoolkit.storage.dggs.RefinementStrategy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.storage.dggs.PolyhedronParameters;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class H3Dggs implements DiscreteGlobalGridSystem {

    @Override
    public DiscreteGlobalGridHierarchy getHierarchy() {
        return new H3Dggh();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return CommonCRS.WGS84.normalizedGeographic();
    }

    @Override
    public String getBasePolyhedron() {
        // https://h3geo.org/docs/core-library/overview/
        return "icosahedron";
    }

    @Override
    public int getRefinementRatio() {
        return 7;
    }

    @Override
    public int getSpatialDimensions() {
        return 2;
    }

    @Override
    public int getTemporalDimensions() {
        return 0;
    }

    @Override
    public List<String> getZoneTypes() {
        return List.of("hexagon", "pentagon");
    }

    @Override
    public PolyhedronParameters getParameters() {
        return new PolyhedronParameters(new PolyhedronOrientation(0, 0, 0, ""));
    }

    @Override
    public List<RefinementStrategy> getRefinementStrategy() {
        return List.of(RefinementStrategy.centredChildCell);
    }

    @Override
    public CellConstraints getCellConstraints() {
        return new CellConstraints(false, true, true, true, true);
    }
}
