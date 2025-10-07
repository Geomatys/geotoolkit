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
package org.geotoolkit.storage.rs.internal.shared;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.rs.Code;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import org.geotoolkit.util.StringUtilities;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DefaultCode implements Code {

    private final ReferenceSystem rs;
    private final Object[] ordinates;

    public DefaultCode(ReferenceSystem rs, Object ... ordinates) {
        this.rs = rs;
        this.ordinates = ordinates;
    }

    @Override
    public ReferenceSystem getReferenceSystem() {
        return rs;
    }

    @Override
    public int getDimension() {
        return ordinates.length;
    }

    public Object[] getOrdinates() {
        return ordinates.clone();
    }

    @Override
    public Object getOrdinate(int dimension) {
        return ordinates[dimension];
    }

    @Override
    public String toString() {
        final List<String> parts = new ArrayList<>();

        final ReferenceSystem rs = getReferenceSystem();
        final List<ReferenceSystem> singleComponents = ReferenceSystems.getSingleComponents(rs, true);
        for (int i = 0; i < singleComponents.size(); i++) {
            final ReferenceSystem srs = singleComponents.get(i);
            if (srs instanceof TemporalCRS tcrs) {
                Instant instant = DefaultTemporalCRS.castOrCopy(tcrs).toInstant(((Number)ordinates[i]).doubleValue());
                parts.add(instant + " " + tcrs.getName().toString());
            } else if (srs instanceof CoordinateReferenceSystem crs) {
                parts.add(ordinates[i] + " " + crs.getName().toString());
            } else if (srs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                parts.add(ordinates[i] + " " + dggrs.getName().toString());
            } else {
                throw new UnsupportedOperationException("todo");
            }
        }

        return StringUtilities.toStringTree("Location", parts);
    }

}
