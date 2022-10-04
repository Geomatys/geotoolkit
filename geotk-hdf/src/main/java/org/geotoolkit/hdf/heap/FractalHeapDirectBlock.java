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
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.G. Disk Format: Level 1G - Fractal Heap
 * @author Johann Sorel (Geomatys)
 */
public final class FractalHeapDirectBlock extends IOStructure {

    /**
     * The ASCII character string “FHDB” is used to indicate the beginning of a
     * fractal heap direct block. This gives file consistency checking utilities
     * a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "FHDB".getBytes(StandardCharsets.US_ASCII);

    /**
     * This is the address for the fractal heap header that this block belongs
     * to. This field is principally used for file integrity checking.
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
     * This is the checksum for the direct block.
     * This field is only present if bit 1 of Flags in the heap’s header is set.
     */
    private int Checksum;
    /**
     * This section of the direct block stores the actual data for objects in
     * the heap. The size of this section is determined by the direct block’s
     * size minus the size of the other fields stored in the direct block (for
     * example, the Signature, Version, and others including the Checksum if
     * it is present).
     */
    private byte[] objectData;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        This document describes version 0.
        */
        channel.ensureVersion(0);
        heapHeaderAddress = channel.readOffset();
        /*
        blockOffset

        checksum

        object data
        */

        throw new IOException("TODO");
    }
}
