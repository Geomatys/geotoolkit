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
package org.geotoolkit.hdf.heap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.G. Disk Format: Level 1G - Fractal Heap
 * @author Johann Sorel (Geomatys)
 */
public final class FractalHeapIndirectBlock extends IOStructure {

    /**
     * The ASCII character string “FHIB” is used to indicate the beginning of a
     * fractal heap indirect block. This gives file consistency checking
     * utilities a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "FHIB".getBytes(StandardCharsets.US_ASCII);

    /**
     * This is the address for the fractal heap header that this block belongs to.
     * This field is principally used for file integrity checking.
     */
    private long heapHeaderAddress;
    /**
     * This is the offset of the block within the fractal heap’s address space
     * (in bytes). The number of bytes used to encode this field is the Maximum
     * Heap Size (in the heap’s header) divided by 8 and rounded up to the next
     * highest integer, for values that are not a multiple of 8. This value is
     * principally used for file integrity checking.
     */
    private int blockOffset;
    /**
     * This field is the address of the child direct block. The size of the
     * [uncompressed] direct block can be computed by its offset in the heap’s
     * linear address space.
     */
    public List<Integer> childDirectBlocks;
    /**
     * This is the size of the child direct block after passing through the I/O
     * filters defined for this heap (in bytes). If no I/O filters are present
     * for this heap, this field is not present.
     */
    public List<Integer> sizeOfFilteredDirectBlocks;
    /**
     * This is the I/O filter mask for the filtered direct block. This mask has
     * the same format as that used for the filter mask in chunked raw data
     * records in a v1 B-tree. If no I/O filters are present for this heap,
     * this field is not present.
     */
    public List<Integer> filterMaskForDirectBlocks;
    /**
     * This field is the address of the child indirect block. The size of the
     * indirect block can be computed by its offset in the heap’s linear
     * address space.
     */
    public List<Integer> childIndirectBlockAdresses;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        This document describes version 0.
        */
        channel.ensureVersion(0);
        heapHeaderAddress = channel.readOffset();

        /*
        all lists
        checksum
        */
        throw new IOException("TODO");
    }
}
