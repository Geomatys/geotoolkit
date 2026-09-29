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
package org.geotoolkit.hdf.chunk;

import java.io.IOException;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Locates the chunks of a dataset.
 * <p>
 * Implementations exist for the version 1 B-tree of the classic format and for
 * the five chunk indexing types of the version 4 data layout message.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ChunkIndex {

    /**
     * Returns the chunk at the given scaled coordinates, or null when not allocated.
     * The dataset fill value applies to unallocated chunks.
     *
     * @param channel file channel
     * @param scaled chunk grid coordinates, see {@link ChunkGrid}
     * @return the chunk at the given scaled coordinates, or null when not allocated
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the index structures are invalid or unsupported.
     */
    Chunk getChunk(HDF5DataInput channel, long[] scaled) throws IOException, DataStoreException;

    /**
     * Returns all allocated chunks of the dataset.
     * May be costly; {@link #getChunk(HDF5DataInput, long[])} is the way to read a sub extent.
     *
     * @param channel file channel
     * @return all allocated chunks of the dataset
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the index structures are invalid or unsupported.
     */
    List<Chunk> getChunks(HDF5DataInput channel) throws IOException, DataStoreException;

    /**
     * Returns whether a bit of a page bitmap is set, meaning the page is present in the file.
     * Follows the {@code H5VM_bit_get} convention used by the fixed and extensible arrays :
     * bits are numbered from the most significant bit of each byte.
     *
     * @param bitmap bitmap bytes
     * @param index bit index
     * @return whether a bit of a page bitmap is set, meaning the page is present in the file
     */
    static boolean isBitSet(byte[] bitmap, long index) {
        return (bitmap[(int) (index / 8)] >> (7 - (index % 8)) & 0x01) != 0;
    }
}
