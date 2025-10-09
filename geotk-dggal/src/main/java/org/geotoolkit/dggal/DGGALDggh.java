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
package org.geotoolkit.dggal;

import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractDiscreteGlobalGridHierarchy;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class DGGALDggh extends AbstractDiscreteGlobalGridHierarchy<DGGALDggrs> {

    private final DiscreteGlobalGrid[] grids;

    DGGALDggh(DGGALDggrs dggrs) throws Throwable {
        super(dggrs);
        grids = new DiscreteGlobalGrid[dggrs.dggal.getMaxDGGRSZoneLevel()];
        for (int i = 0; i < grids.length; i++) {
            grids[i] = new DGGALDgg(this, i);
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
        return true;
    }

    @Override
    public Zone getZone(Object identifier) throws IllegalArgumentException {
        if (identifier instanceof DGGALZone z) return z;
        return new DGGALZone(dggrs, toLongIdentifier(identifier));
    }

    @Override
    public String toTextIdentifier(Object zoneId) throws IllegalArgumentException {
        if (zoneId instanceof CharSequence cs) {
            return cs.toString();
        } else if (zoneId instanceof Long l) {
            return idAsText(l);
        } else if (zoneId instanceof DGGALZone z) {
            return z.getTextIdentifier().toString();
        } else {
            throw new IllegalArgumentException("Identifer not supported");
        }
    }

    @Override
    public long toLongIdentifier(Object zoneId) throws IllegalArgumentException {
        if (zoneId instanceof CharSequence cs) {
            return idAsLong(cs);
        } else if (zoneId instanceof Long l) {
            return l;
        } else if (zoneId instanceof DGGALZone z) {
            return z.getLongIdentifier();
        } else {
            throw new IllegalArgumentException("Identifer not supported");
        }
    }

    final String idAsText(final long hash) {
        try {
            return dggrs.dggal.getZoneTextID(hash);
        } catch (Throwable ex) {
            throw new DGGALBindingException(ex);
        }
    }

    final long idAsLong(final CharSequence cs) {
        try {
            return dggrs.dggal.getZoneFromTextID(cs.toString());
        } catch (Throwable ex) {
            throw new DGGALBindingException(ex);
        }
    }
}
