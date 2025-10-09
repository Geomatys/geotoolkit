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
package org.geotoolkit.referencing.rs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.opengis.metadata.Identifier;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DefaultCompoundRS implements CompoundRS {

    private final Identifier name;
    private final List<ReferenceSystem> rss;
    private final CoordinateReferenceSystem leaningCrs;

    public DefaultCompoundRS(Identifier name, List<ReferenceSystem> rss) {
        if (rss.size() < 2) {
            throw new IllegalArgumentException("Provide at least two systems to create a CompoundRS.");
        }

        if (name == null) {
            final StringBuilder sb = new StringBuilder("CompundLS [");
            for (int i = 0, n = rss.size(); i < n; i++) {
                final ReferenceSystem cdt = rss.get(i);
                if (i != 0) sb.append(',');
                sb.append(cdt.getName().toString());
            }
            sb.append(']');
            name = new DefaultIdentifier(sb.toString());
        }
        this.name = name;
        this.rss = Collections.unmodifiableList(rss);

        final List<CoordinateReferenceSystem> crss = new ArrayList<>(rss.size()+1);
        for (int i = 0; i < rss.size(); i++) {
            final ReferenceSystem srs = rss.get(i);
            if (srs instanceof CoordinateReferenceSystem crs) {
                crss.add(crs);
            } else if (srs instanceof DiscreteGlobalGridReferenceSystem dggrs) {
                crss.add(dggrs.getGridSystem().getCrs());
            } else {
                throw new UnsupportedOperationException("todo");
            }
        }
        try {
            leaningCrs = CRS.compound(crss.toArray(CoordinateReferenceSystem[]::new));
        } catch (FactoryException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public List<ReferenceSystem> getComponents() {
        return rss;
    }

    @Override
    public Identifier getName() {
        return name;
    }

    public synchronized CoordinateReferenceSystem getLeaningCrs() {
        return leaningCrs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CompoundRS[ " + name.toString()+" ");
        final List<ReferenceSystem> components = getComponents();
        for (int i = 0, n = components.size(); i < n; i++) {
            if (i > 0)sb.append(',');
            final ReferenceSystem rs = components.get(i);
            sb.append(rs.toString().replaceAll("\n", "    \n"));
        }
        sb.append(']');
        return sb.toString();
    }
}
