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
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.A.2. Disk Format: Level 1A2 - Version 2 B-trees
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV2_Type11_FilteredDatasetChunks {

    /**
     * This field is the address of the dataset chunk in the file.
     */
    private long address;
    /**
     * This field is the size of the dataset chunk in bytes.
     */
    private int chunkSize;
    /**
     * This field is the filter mask which indicates the filter to skip for the
     * dataset chunk. Each filter has an index number in the pipeline and if
     * that filter is skipped, the bit corresponding to its index is set.
     */
    private int filterMask;
    /**
     * This field is the scaled offset of the chunk within the dataset. n is the
     * number of dimensions for the dataset. The first scaled offset stored in
     * the list is for the slowest changing dimension, and the last scaled offset
     * stored is for the fastest changing dimension.
     */
    private long[] dimensionScaledOffsets;

    public void read(HDF5DataInput channel, int datasetNumberOfDimensions) throws IOException {

        address = channel.readOffset();

        if (true) {
            throw new IOException("TODO field chunkSize");
        }

        filterMask = channel.readInt();
        dimensionScaledOffsets = new long[datasetNumberOfDimensions];
        for (int i = 0; i < datasetNumberOfDimensions; i++) {
            dimensionScaledOffsets[i] = channel.readLong();
        }
    }
}
