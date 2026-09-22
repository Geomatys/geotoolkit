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

import java.util.Collections;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Index of a dataset stored as a single block : compact and contiguous layouts,
 * and the Single Chunk indexing type of the version 4 data layout message.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class SingleChunkIndex implements ChunkIndex {

    private final Chunk chunk;

    /**
     * Creates an index holding a single chunk.
     *
     * @param chunk the only chunk of the dataset, or null when storage has not
     *        been allocated.
     */
    public SingleChunkIndex(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public Chunk getChunk(HDF5DataInput channel, long[] scaled) {
        for (long s : scaled) {
            if (s != 0) return null;
        }
        return chunk;
    }

    @Override
    public List<Chunk> getChunks(HDF5DataInput channel) {
        return chunk == null ? Collections.emptyList() : Collections.singletonList(chunk);
    }
}
