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
package org.geotoolkit.dggs.healpix;

import java.util.List;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridSystem;
import org.geotoolkit.referencing.dggs.GridConstraints;
import org.geotoolkit.referencing.dggs.PolyhedronOrientation;
import org.geotoolkit.referencing.dggs.RefinementStrategy;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.referencing.dggs.PolyhedronParameters;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class HealpixDggs implements DiscreteGlobalGridSystem {

    final HealpixDggh dggh;

    public HealpixDggs(HealpixDggrs dggrs) {
        this.dggh = new HealpixDggh(dggrs);
    }

    @Override
    public DiscreteGlobalGridHierarchy getHierarchy() {
        return dggh;
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return CommonCRS.WGS84.normalizedGeographic();
    }

    @Override
    public String getBasePolyhedron() {
        return "distorted rhombic dodecahedron";
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
        return List.of("square");
    }

    @Override
    public PolyhedronParameters getParameters() {
        return new PolyhedronParameters(new PolyhedronOrientation(0, 0, 0, ""));
    }

    @Override
    public List<RefinementStrategy> getRefinementStrategy() {
        return List.of(RefinementStrategy.nestedChildCell);
    }

    @Override
    public List<GridConstraints> getGridConstraints() {
        return List.of(GridConstraints.cellConformal, GridConstraints.cellEquiAngular, GridConstraints.cellEquiDistant, GridConstraints.cellEquiSized);
    }
}
