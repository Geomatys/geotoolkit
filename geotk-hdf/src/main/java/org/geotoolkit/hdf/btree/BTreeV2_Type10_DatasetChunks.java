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
 * Record of a version 2 B-tree of type 10, used for indexing the chunks of datasets
 * with no filters and with more than one dimension of unlimited extent.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_infra_btrees_v2">III.A.2. Disk Format: Level 1A2 - Version 2 B-trees</a>
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV2_Type10_DatasetChunks {

    /**
     * This field is the address of the dataset chunk in the file.
     */
    private long address;
    /**
     * This field is the scaled offset of the chunk within the dataset. n is
     * the number of dimensions for the dataset. The first scaled offset stored
     * in the list is for the slowest changing dimension, and the last scaled
     * offset stored is for the fastest changing dimension. Scaled offset is
     * calculated by dividing the chunk dimension sizes into the chunk offsets.
     */
    private long[] dimensionScaledOffsets;

    /** Creates an empty record, filled by {@link #read(HDF5DataInput, int)}. */
    public BTreeV2_Type10_DatasetChunks() {
    }

    /**
     * Returns the address of the chunk in the file.
     *
     * @return the address of the chunk in the file
     */
    public long getAddress() {
        return address;
    }

    /**
     * Returns the position of the chunk on the chunk grid, slowest changing dimension first.
     *
     * @return the position of the chunk on the chunk grid, slowest changing dimension first
     */
    public long[] getDimensionScaledOffsets() {
        return dimensionScaledOffsets;
    }

    /**
     * Reads the record.
     *
     * @param channel channel positioned on the record
     * @param datasetNumberOfDimensions number of dataset dimensions
     * @throws IOException if an I/O error occurred.
     */
    public void read(HDF5DataInput channel, int datasetNumberOfDimensions) throws IOException {

        address = channel.readOffset();
        dimensionScaledOffsets = new long[datasetNumberOfDimensions];
        for (int i = 0; i < datasetNumberOfDimensions; i++) {
            dimensionScaledOffsets[i] = channel.readLong();
        }
    }
}
