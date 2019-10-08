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
package org.geotoolkit.coverage.mosaic;

import java.util.BitSet;
import java.util.Optional;
import java.util.function.IntFunction;
import org.apache.sis.coverage.grid.GridExtent;

/**
 * Extend java BitSet to 2D space.
 *
 * @author Johann Sorel (Geomatys)
 */
public class BitSet2D extends BitSet {

    private final int width;
    private final int height;
    private final int trueSize;

    public BitSet2D(int width, int height) {
        super(width * height);
        this.width = width;
        this.height = height;
        this.trueSize = width*height;
    }

    public boolean get2D(int x, int y) {
        return get(x + y * width);
    }

    public void clear2D(int x, int y) {
        clear(x + y * width);
    }

    public void set2D(int x, int y) {
        set(x + y * width);
    }

    public void set2D(int x, int y, boolean value) {
        set(x + y * width, value);
    }

    public void set2D(int x, int y, int width, int height, boolean value) {
        for (int my = y + height; y < my; y++) {
            int v = y * this.width + x;
            set(v, v + width, value);
        }
    }

    /**
     * Diverge from Bitset method, since the bitset size is possibly larger the size
     * given in constructor. Otherwise this would cause to see clear bits outside the width/height area.
     * @param fromIndex
     * @return
     */
    @Override
    public int nextClearBit(int fromIndex) {
        int o = super.nextClearBit(fromIndex);
        //Note : there seems to be a bug here, for a grid of 100x100 fully set
        //this method returns the index of bit 100000 which is outside the size of the bitset.
        if (o >= trueSize) return -1;
        return o;
    }

    /**
     * Get extent of setted bits.
     * @return optional of setted bits, empty if no bits are set.
     */
    public Optional<GridExtent> areaSetted() {
        return intersection(this::nextSetBit, this::previousSetBit);
    }

    /**
     * Get extent of cleared bits.
     * @return optional of cleared bits, empty if no bits are clear.
     */
    public Optional<GridExtent> areaCleared() {
        return intersection(this::nextClearBit, this::previousClearBit);
    }

    public Optional<GridExtent> intersectSetted(BitSet2D other) {
        return intersection(
                (int value) -> sharedBit(BitSet2D.this::nextSetBit, other::nextSetBit, value),
                (int value) -> sharedBit(BitSet2D.this::previousSetBit, other::previousSetBit, value));
    }

    public Optional<GridExtent> intersectCleared(BitSet2D other) {
        return intersection(
                (int value) -> sharedBit(BitSet2D.this::nextClearBit, other::nextClearBit, value),
                (int value) -> sharedBit(BitSet2D.this::previousClearBit, other::previousClearBit, value));
    }

    private static int sharedBit(IntFunction<Integer> bs1, IntFunction<Integer> bs2, int fromIndex) {
        int idx = bs1.apply(fromIndex);
        while (idx != -1) {
            int k2 = bs2.apply(idx);
            if (k2 == -1) {
                idx = -1;
                break;
            } else if (k2 != idx) {
                //continue search
                idx = bs1.apply(k2);
                if (idx == k2) {
                    //found a common bit
                    break;
                }
            } else {
                //found a common bit
                break;
            }
        }
        return idx;
    }

    private Optional<GridExtent> intersection(IntFunction<Integer> nextSearch, IntFunction<Integer> previousSearch) {
        //search first bit defined
        //this will give use the first lines to search
        int startIdx = nextSearch.apply(0);
        if (startIdx == -1) return Optional.empty();

        final int starty = startIdx / width;

        //search min and max x and last y
        int minx = width-1;
        int maxx = 0;

        int y = starty;
        for (int k = nextSearch.apply(y*width); k >= 0; k = nextSearch.apply((y+1)*width)) {
            int startx = k % width;
            y = k / width;
            int endx = previousSearch.apply( (y+1)*width - 1) % width;
            minx = Math.min(minx, startx);
            maxx = Math.max(maxx, endx);
        }

        return Optional.of(new GridExtent(null, new long[]{minx, starty}, new long[]{maxx,y}, true));
    }
}
