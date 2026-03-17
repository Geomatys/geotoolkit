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
package org.geotoolkit.storage.rs;

import java.util.List;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.storage.rs.internal.shared.CodeTransforms;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.ReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CompoundCodedGeometry extends CodedGeometry {

    private final CodedGeometry[] components;

    private CompoundCodedGeometry(ReferenceSystem rs, GridExtent extent, CodeTransform gridToRS, GeographicExtent geoExtent, CodedGeometry ... grids) {
        super(rs, extent, gridToRS, geoExtent);
        this.components = grids.clone();
    }

    public List<CodedGeometry> getComponents() {
        return List.of(components);
    }

    public static CodedGeometry compound(CodedGeometry ... grids) {
        if (grids.length == 0) return null;
        if (grids.length == 1) return grids[0];


        long[] low = grids[0].getExtent().getLow().getCoordinateValues();
        long[] high = grids[0].getExtent().getHigh().getCoordinateValues();
        CodeTransform gridToRS = grids[0].getGridToRS();
        ReferenceSystem rs = grids[0].getReferenceSystem();

        for (int i = 1; i < grids.length; i++) {
            final CodedGeometry rgg = grids[i];
            final GridExtent subExtent = rgg.getExtent();
            final CodeTransform subGridToRS = rgg.getGridToRS();
            final ReferenceSystem subrs = rgg.getReferenceSystem();
            low = ArraysExt.concatenate(low, subExtent.getLow().getCoordinateValues());
            high = ArraysExt.concatenate(high, subExtent.getHigh().getCoordinateValues());
            gridToRS = CodeTransforms.compound(gridToRS, subGridToRS);
            rs = ReferenceSystems.createCompound(rs, subrs);
        }

        final GridExtent extent = new GridExtent(null, low, high, true);
        return new CompoundCodedGeometry(rs, extent, gridToRS, null, grids);
    }
}
