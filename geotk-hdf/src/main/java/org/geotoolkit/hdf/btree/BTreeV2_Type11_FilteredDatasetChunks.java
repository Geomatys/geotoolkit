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
 * Record of a version 2 B-tree of type 11, used for indexing the chunks of datasets
 * with filters and with more than one dimension of unlimited extent.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_infra_btrees_v2">III.A.2. Disk Format: Level 1A2 - Version 2 B-trees</a>
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
    private long chunkSize;
    /**
     * This field is the filter mask which indicates the filter to skip for the
     * dataset chunk. Each filter has an index number in the pipeline and if
     * that filter is skipped, the bit corresponding to its index is set.
     */
    private long filterMask;
    /**
     * This field is the scaled offset of the chunk within the dataset. n is the
     * number of dimensions for the dataset. The first scaled offset stored in
     * the list is for the slowest changing dimension, and the last scaled offset
     * stored is for the fastest changing dimension.
     */
    private long[] dimensionScaledOffsets;

    /** Creates an empty record, filled by {@link #read(HDF5DataInput, int, int)}. */
    public BTreeV2_Type11_FilteredDatasetChunks() {
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
     * Returns the size in bytes of the chunk in the file, filters applied.
     *
     * @return the size in bytes of the chunk in the file, filters applied
     */
    public long getChunkSize() {
        return chunkSize;
    }

    /**
     * Returns the filter mask, bit <var>i</var> being set if pipeline filter <var>i</var> is skipped.
     *
     * @return the filter mask, bit <var>i</var> being set if pipeline filter <var>i</var> is skipped
     */
    public long getFilterMask() {
        return filterMask;
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
     * Returns the width in bytes of the chunk size field of such a record.
     * <p>
     * The width is not stored anywhere; it is whatever is left in a record once
     * the address, the filter mask and the scaled offsets are accounted for.
     *
     * @param recordSize size of a record, from the B-tree header
     * @param offsetSize size of an address in this file
     * @param datasetNumberOfDimensions rank of the dataset
     * @return the width in bytes of the chunk size field of such a record
     */
    public static int chunkSizeWidth(int recordSize, int offsetSize, int datasetNumberOfDimensions) {
        return recordSize - offsetSize - 4 - 8 * datasetNumberOfDimensions;
    }

    /**
     * Reads the record.
     *
     * @param channel channel positioned on the record
     * @param datasetNumberOfDimensions number of dataset dimensions
     * @param chunkSizeWidth width of the chunk size field, see {@link #chunkSizeWidth(int, int, int)}
     * @throws IOException if an I/O error occurred or the chunk size width is invalid.
     */
    public void read(HDF5DataInput channel, int datasetNumberOfDimensions, int chunkSizeWidth) throws IOException {

        address = channel.readOffset();
        if (chunkSizeWidth < 1 || chunkSizeWidth > 8) {
            throw new IOException("Inconsistent filtered chunk record, computed chunk size width "
                    + chunkSizeWidth);
        }
        chunkSize = channel.readUnsignedInteger(chunkSizeWidth);
        filterMask = channel.readUnsignedInt();
        dimensionScaledOffsets = new long[datasetNumberOfDimensions];
        for (int i = 0; i < datasetNumberOfDimensions; i++) {
            dimensionScaledOffsets[i] = channel.readLong();
        }
    }
}
