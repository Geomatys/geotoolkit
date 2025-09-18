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
package org.geotoolkit.dggs.mgrs;

import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractDiscreteGlobalGridHierarchy;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class MgrsDggh extends AbstractDiscreteGlobalGridHierarchy {

    private final DiscreteGlobalGrid[] grids;

    MgrsDggh(MgrsDggrs dggrs) {
        super(dggrs);
        grids = new DiscreteGlobalGrid[6];
        for (int i = 0; i < grids.length; i++) {
            grids[i] = new MgrsDgg(this, i);
        }
    }

    @Override
    public DiscreteGlobalGrid get(int level) {
        return grids[level];
    }

    @Override
    public int size() {
        return grids.length;
    }

    @Override
    public boolean supportLongIdentifiers() {
        return false;
    }

    @Override
    public String toTextIdentifier(Object zoneId) throws IllegalArgumentException {
        if (zoneId instanceof CharSequence cs) {
            return cs.toString();
        } else {
            throw new IllegalArgumentException("Identifer not supported");
        }
    }

    @Override
    public long toLongIdentifier(Object zoneId) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Long identifiers not supported");
    }

    @Override
    public Zone getZone(Object identifier) throws TransformException {
        return new MgrsZone((MgrsDggrs) dggrs, toTextIdentifier(identifier));
    }

}
