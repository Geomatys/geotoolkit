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

import cds.healpix.Healpix;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.internal.shared.AbstractDiscreteGlobalGridHierarchy;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class NHealpixDggh extends AbstractDiscreteGlobalGridHierarchy<NHealpixDggrs> {

    private final DiscreteGlobalGrid[] grids;

    NHealpixDggh(NHealpixDggrs dggrs) {
        super(dggrs);
        grids = new DiscreteGlobalGrid[Healpix.DEPTH_MAX];
        for (int i = 0; i < grids.length; i++) {
            grids[i] = new NHealpixDgg(this, i);
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
    public String toTextIdentifier(Object zoneId) throws IllegalArgumentException {
        if (zoneId instanceof CharSequence cs) {
            return cs.toString();
        } else if (zoneId instanceof Long l) {
            return idAsText(l);
        } else if (zoneId instanceof NHealpixZone z) {
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
        } else if (zoneId instanceof NHealpixZone z) {
            return z.getLongIdentifier();
        } else {
            throw new IllegalArgumentException("Identifer not supported");
        }
    }

    @Override
    public Zone getZone(Object identifier) throws IllegalArgumentException {
        if (identifier instanceof NHealpixZone z) return z;
        return new NHealpixZone(dggrs, toLongIdentifier(identifier));
    }

    static final String idAsText(final long hash) {
        return Long.toHexString(hash);
    }

    static final long idAsLong(final CharSequence cs) {
        return Long.parseUnsignedLong(cs.toString(), 16);
    }

}
