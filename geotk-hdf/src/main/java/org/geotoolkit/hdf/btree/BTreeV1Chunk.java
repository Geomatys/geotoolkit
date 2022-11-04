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
package org.geotoolkit.hdf.btree;

import java.io.IOException;
import java.util.Arrays;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * For nodes of node type 1 (chunked raw data nodes), the key is formatted as follows:
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV1Chunk {

    /**
     * Bytes 1-4: Size of chunk in bytes.
     */
    public long size;
    /**
     * Bytes 4-8: Filter mask, a 32-bit bit field indicating which filters have
     * been skipped for this chunk. Each filter has an index number in the
     * pipeline (starting at 0, with the first filter to apply) and if that
     * filter is skipped, the bit corresponding to its index is set.
     */
    public long filterMask;
    /**
     * (D + 1) 64-bit fields: The offset of the chunk within the dataset where
     * D is the number of dimensions of the dataset, and the last value is the
     * offset within the dataset’s datatype and should always be zero. For
     * example, if a chunk in a 3-dimensional dataset begins at the position
     * [5,5,5], there will be three such 64-bit values, each with the value of
     * 5, followed by a 0 value.
     */
    public long[] offset;

    public long address;
    /**
     * Opportuniste uncompressed chunk size.
     */
    public long uncompressedSize = -1;

    public void read(HDF5DataInput channel, int dimension) throws IOException {
        size = channel.readUnsignedInt();
        filterMask = channel.readUnsignedInt();
        offset = new long[dimension];
        for (int i = 0; i < dimension; i++) {
            offset[i] = channel.readLong();
        }
        long last = channel.readLong();
        if (last != 0) {
            throw new IOException("Last dimension should be 0 but was " + last);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Chunk{");
        sb.append("size:").append(size);
        sb.append(",filterMask:").append(filterMask);
        sb.append(",address:").append(address);
        sb.append(",offset:").append(Arrays.toString(offset));
        sb.append("}");
        return sb.toString();
    }

}
