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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Implicit chunk index : the chunks are laid out contiguously in the file, in
 * row major order, so a chunk address is pure arithmetic and no index structure
 * is stored at all.
 * <p>
 * This index requires early allocation and forbids a filter pipeline, so every
 * chunk exists and none is filtered.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#sec_fmt3_appendixc">Appendix C: Types of Indexes for Dataset Chunks</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ImplicitChunkIndex implements ChunkIndex {

    private final long baseAddress;
    private final long chunkByteSize;
    private final ChunkGrid grid;

    /**
     * Creates an index of chunks stored contiguously from the given address.
     *
     * @param baseAddress file address of the first chunk
     * @param chunkByteSize size in bytes of one chunk
     * @param grid chunk grid of the dataset
     */
    public ImplicitChunkIndex(long baseAddress, long chunkByteSize, ChunkGrid grid) {
        this.baseAddress = baseAddress;
        this.chunkByteSize = chunkByteSize;
        this.grid = grid;
    }

    @Override
    public Chunk getChunk(HDF5DataInput channel, long[] scaled) {
        final long[] counts = grid.getChunkCounts();
        for (int i = 0; i < scaled.length; i++) {
            if (scaled[i] < 0 || scaled[i] >= counts[i]) return null;
        }
        final Chunk chunk = new Chunk();
        chunk.address = baseAddress + grid.linearIndex(scaled) * chunkByteSize;
        chunk.size = chunkByteSize;
        chunk.uncompressedSize = chunkByteSize;
        chunk.filtered = false;
        chunk.offset = grid.extentOf(scaled);
        return chunk;
    }

    @Override
    public List<Chunk> getChunks(HDF5DataInput channel) {
        final long[] counts = grid.getChunkCounts();
        long total = 1;
        for (long c : counts) {
            total *= c;
        }
        final List<Chunk> chunks = new ArrayList<>();
        for (long i = 0; i < total; i++) {
            chunks.add(getChunk(channel, grid.scaledOf(i)));
        }
        return chunks;
    }
}
