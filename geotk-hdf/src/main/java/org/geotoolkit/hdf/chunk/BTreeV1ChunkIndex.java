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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.hdf.btree.BTreeV1;
import org.geotoolkit.hdf.btree.BTreeV1Chunk;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Chunk index backed by the version 1 B-tree of the classic format.
 * <p>
 * The tree is walked once and its records are kept in a map keyed by scaled
 * coordinates, which turns the former full scan per read into a lookup.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV1ChunkIndex implements ChunkIndex {

    private final long btreeAddress;
    private final int[] chunkDimensions;
    private final ChunkGrid grid;
    private final boolean filtered;

    private Map<List<Long>,Chunk> chunks;

    /**
     * Creates an index over a version 1 B-tree, read lazily.
     *
     * @param btreeAddress address of the version 1 B-tree root
     * @param chunkDimensions chunk dimensions as stored in the layout message
     * @param grid chunk grid of the dataset
     * @param filtered whether the dataset has a filter pipeline
     */
    public BTreeV1ChunkIndex(long btreeAddress, int[] chunkDimensions, ChunkGrid grid, boolean filtered) {
        this.btreeAddress = btreeAddress;
        this.chunkDimensions = chunkDimensions;
        this.grid = grid;
        this.filtered = filtered;
    }

    private synchronized Map<List<Long>,Chunk> load(HDF5DataInput channel) throws IOException {
        if (chunks == null) {
            channel.mark();
            try {
                channel.seek(btreeAddress);
                final BTreeV1 datatree = new BTreeV1();
                datatree.read(channel, chunkDimensions);
                final BTreeV1.DataNode node = (BTreeV1.DataNode) datatree.root;
                final Map<List<Long>,Chunk> map = new HashMap<>();
                for (BTreeV1Chunk chunk : node.getChunks()) {
                    chunk.filtered = filtered;
                    map.put(key(grid.scaledOfElement(chunk.offset.getLow().getCoordinateValues())), chunk);
                }
                chunks = map;
            } finally {
                channel.reset();
            }
        }
        return chunks;
    }

    private static List<Long> key(long[] scaled) {
        final Long[] boxed = new Long[scaled.length];
        for (int i = 0; i < scaled.length; i++) {
            boxed[i] = scaled[i];
        }
        return Arrays.asList(boxed);
    }

    @Override
    public Chunk getChunk(HDF5DataInput channel, long[] scaled) throws IOException {
        return load(channel).get(key(scaled));
    }

    @Override
    public List<Chunk> getChunks(HDF5DataInput channel) throws IOException {
        return new ArrayList<>(load(channel).values());
    }
}
