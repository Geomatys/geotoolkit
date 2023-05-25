/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.coverage.mosaic;

import java.util.BitSet;
import org.apache.sis.coverage.grid.GridExtent;

/**
 * N dimension BitSet.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BitSetND {

    private final BitSet bset;
    private final GridExtent extent;
    private final long[] low;
    private final long[] size;
    private final long[] offsets;
    private final int totalSize;

    public BitSetND(GridExtent extent) {
        this.totalSize = Math.toIntExact(count(extent));
        this.bset = new BitSet(totalSize);
        this.extent = extent;
        this.low = extent.getLow().getCoordinateValues();
        this.size = new long[this.low.length];
        this.offsets = new long[this.low.length];
        this.offsets[0] = 1;

        for (int i = 0; i < size.length; i++) {
            size[i] = extent.getSize(i);
            if (i > 0) {
                offsets[i] = offsets[i-1] * size[i-1];
            }
        }
    }

    public boolean get(long ... position) {
        return bset.get(toIndex(position));
    }

    public void clear(long ... position){
        bset.clear(toIndex(position));
    }

    public void set(long ... position) {
        bset.set(toIndex(position));
    }

    public void set(boolean value, long ... position) {
        bset.set(toIndex(position), value);
    }

    /**
     * Get next clear position or null if none.
     */
    public long[] nextClear(long ... position) {
        final int o = bset.nextClearBit(toIndex(position));
        if (o >= totalSize) return null;
        return fromIndex(o);
    }

    private int toIndex(long ... position) {
        long idx = 0;
        for (int i = 0; i < size.length; i++) {
            idx += offsets[i] * (position[i] - low[i]);
        }
        return Math.toIntExact(idx);
    }

    private long[] fromIndex(int index) {
        final long[] position = new long[size.length];
        long idx = index;
        for (int i = position.length-1; i >=0; i--) {
            long n = idx / offsets[i];
            position[i] = low[i] + n;
            idx %= offsets[i];
        }
        return position;
    }

    private static long count(GridExtent extent) {
        long nb = 1;
        for (int i = 0; i < extent.getDimension(); i++) {
            nb *= extent.getSize(i);
        }
        return nb;
    }

}
