/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.filter;

import java.io.IOException;

/**
 * This filter is in reality something like interleaved to band sample model.
 * it reorders bytes.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Shuffle implements Filter {

    private final int dataSize;

    public Shuffle(int dataSize) {
        this.dataSize = dataSize;
    }

    @Override
    public byte[] encode(byte[] data) throws IOException {
        //same algo
        return decode(data);
    }

    @Override
    public byte[] decode(byte[] data) throws IOException {
        if (dataSize < 2) return data;

        final int nbData = data.length / dataSize;
        //copies all bytes, if any extra byte is set, they remain unchanged
        final byte[] out = data.clone();
        final int[] offsets = new int[dataSize];
        offsets[0] = 0;
        for (int i = 1; i < dataSize; i++) {
            offsets[i] = offsets[i-1] + nbData;
        }

        for (int x = 0, xp = 0; x < nbData; x++) {
            for (int i = 0; i < dataSize; i++, xp++) {
                out[xp] = data[offsets[i] + x];
            }
        }
        return out;
    }

    /**
     * Shuffle do not change data size.
     */
    @Override
    public long uncompressedSize(byte[] chunkdata) throws IOException {
        return chunkdata.length;
    }

}
