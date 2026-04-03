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
import java.util.Optional;
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

    @Override
    public Optional<CodedGeometry> slice(ReferenceSystem rs) {
        for (CodedGeometry cg : components) {
            if (cg.getReferenceSystem().equals(rs)) {
                return Optional.of(cg);
            }
        }
        return super.slice(rs);
    }

    public static CodedGeometry compound(CodedGeometry ... grids) {
        if (grids.length == 0) return null;
        if (grids.length == 1) return grids[0];

        long[] low = null;
        long[] high = null;
        ReferenceSystem rs = null;
        CodeTransform gridToRS = null;

        for (int i = 0; i < grids.length; i++) {
            final CodedGeometry rgg = grids[i];
            if (i == 0) {
                if (rgg.isDefined(CodedGeometry.EXTENT)) {
                    low = rgg.getExtent().getLow().getCoordinateValues();
                    high = rgg.getExtent().getHigh().getCoordinateValues();
                }
                if (rgg.isDefined(CodedGeometry.GRID_TO_RS)) {
                    gridToRS = rgg.getGridToRS();
                }
                if (rgg.isDefined(CodedGeometry.RS)) {
                    rs = rgg.getReferenceSystem();
                }
            } else {
                if (low != null && rgg.isDefined(CodedGeometry.EXTENT)) {
                    final GridExtent subExtent = rgg.getExtent();
                    low = ArraysExt.concatenate(low, subExtent.getLow().getCoordinateValues());
                    high = ArraysExt.concatenate(high, subExtent.getHigh().getCoordinateValues());
                } else {
                    low = null; high = null;
                }
                if (gridToRS != null && rgg.isDefined(CodedGeometry.GRID_TO_RS)) {
                    final CodeTransform subGridToRS = rgg.getGridToRS();
                    gridToRS = CodeTransforms.compound(gridToRS, subGridToRS);
                } else {
                    gridToRS = null;
                }
                if (rs != null && rgg.isDefined(CodedGeometry.RS)) {
                    final ReferenceSystem subrs = rgg.getReferenceSystem();
                    rs = ReferenceSystems.createCompound(rs, subrs);
                } else {
                    rs = null;
                }
            }
        }

        final GridExtent extent = low != null ? new GridExtent(null, low, high, true) : null;
        return new CompoundCodedGeometry(rs, extent, gridToRS, null, grids);
    }
}
