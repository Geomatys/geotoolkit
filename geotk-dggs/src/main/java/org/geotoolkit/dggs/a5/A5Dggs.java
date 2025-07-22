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
package org.geotoolkit.dggs.a5;

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
final class A5Dggs implements DiscreteGlobalGridSystem {

    @Override
    public DiscreteGlobalGridHierarchy getHierarchy() {
        return new A5Dggh();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return CommonCRS.WGS84.normalizedGeographic();
    }

    @Override
    public String getBasePolyhedron() {
        return "Regular dodecahedron";
    }

    @Override
    public int getRefinementRatio() {
        return 4;
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
        return List.of("sphere","pentagon","triangle");
    }

    @Override
    public PolyhedronParameters getParameters() {
        return new PolyhedronParameters(new PolyhedronOrientation(0, 93.0, 72.0, ""));
    }

    @Override
    public List<RefinementStrategy> getRefinementStrategy() {
        return List.of();
    }

    @Override
    public CellConstraints getCellConstraints() {
        return new CellConstraints(false, true, false, false, true);
    }

}
