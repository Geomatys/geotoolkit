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
 * A local heap is a collection of small pieces of data that are particular to
 * a single object in the HDF5 file. Objects can be inserted and removed from
 * the heap at any time. The address of a heap does not change once the heap is
 * created. For example, a group stores addresses of objects in symbol table
 * nodes with the names of links stored in the group’s local heap.
 * <p>
 * Objects within a local heap should be aligned on an 8-byte boundary.
 *
 * @see III.D. Disk Format: Level 1D - Local Heaps
 * @author Johann Sorel (Geomatys)
 */
public final class LocalHeap extends IOStructure {

    /**
     * The ASCII character string “HEAP” is used to indicate the beginning of a
     * heap. This gives file consistency checking utilities a better chance of
     * reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "HEAP".getBytes(StandardCharsets.US_ASCII);

    /**
     * The total amount of disk memory allocated for the heap data. This may be
     * larger than the amount of space required by the objects stored in the heap.
     * The extra unused space in the heap holds a linked list of free blocks.
     */
    public long dataSegmentSize;
    /**
     * This is the offset within the heap data segment of the first free block
     * (or the undefined address if there is no free block). The free block
     * contains Size of Lengths bytes that are the offset of the next free block
     * (or the value ‘1’ if this is the last free block) followed by Size of
     * Lengths bytes that store the size of this free block. The size of the
     * free block includes the space used to store the offset of the next free
     * block and the size of the current block, making the minimum size of a
     * free block 2 * Size of Lengths.
     */
    public long offsetToHeadofFreeList;
    /**
     * The data segment originally starts immediately after the heap header,
     * but if the data segment must grow as a result of adding more objects,
     * then the data segment may be relocated, in its entirety, to another
     * part of the file.
     */
    public long addressOfDataSegment;

    /**
     * Read a null terminated string from the local heap.
     * @param channel to read from
     * @param offset offset of the string in the heap
     * @return string value
     */
    public String getString(HDF5DataInput channel, long offset) throws IOException {
        channel.mark();
        try {
            channel.seek(addressOfDataSegment + offset);
            return channel.readNullTerminatedString(0, StandardCharsets.US_ASCII);
        } finally {
            channel.reset();
        }
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        Each local heap has its own version number so that new heaps can be added
        to old files. This document describes version zero (0) of the local heap.
         */
        channel.ensureVersion(0);
        channel.skipFully(3);
        dataSegmentSize = channel.readLength();
        offsetToHeadofFreeList = channel.readLength();
        addressOfDataSegment = channel.readOffset();
    }
}
