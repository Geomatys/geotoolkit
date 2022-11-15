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
import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Each HDF5 file has a global heap which stores various types of information
 * which is typically shared between datasets. The global heap was designed to
 * satisfy these goals:
 * <ul>
 * <li>A.Repeated access to a heap object must be efficient without resulting in
 * repeated file I/O requests. Since global heap objects will typically be shared
 * among several datasets, it is probable that the object will be accessed repeatedly.</li>
 * <li>B. Collections of related global heap objects should result in fewer and
 * larger I/O requests. For instance, a dataset of object references will have
 * a global heap object for each reference.
 * Reading the entire set of object references should result in a few large
 * I/O requests instead of one small I/O request for each reference.</li>
 * <li>It should be possible to remove objects from the global heap and the
 * resulting file hole should be eligible to be reclaimed for other uses.</li>
 * </ul>
 * The implementation of the heap makes use of the memory management already
 * available at the file level and combines that with a new object called a
 * collection to achieve goal B. The global heap is the set of all collections.
 * Each global heap object belongs to exactly one collection, and each collection
 * contains one or more global heap objects. For the purposes of disk I/O and
 * caching, a collection is treated as an atomic object, addressing goal A.
 * <p>
 * When a global heap object is deleted from a collection (which occurs when its
 * reference count falls to zero), objects located after the deleted object in
 * the collection are packed down toward the beginning of the collection, and
 * the collection’s global heap object 0 is created (if possible), or its size
 * is increased to account for the recently freed space. There are no gaps
 * between objects in each collection, with the possible exception of the final
 * space in the collection, if it is not large enough to hold the header for the
 * collection’s global heap object 0. These features address goal C.
 * <p>
 * The HDF5 Library creates global heap collections as needed, so there may be
 * multiple collections throughout the file. The set of all of them is abstractly
 * called the “global heap”, although they do not actually link to each other,
 * and there is no global place in the file where you can discover all of the
 * collections. The collections are found simply by finding a reference to one
 * through another object in the file. For example, data of variable-length
 * datatype elements is stored in the global heap and is accessed via a global
 * heap ID. The format for global heap IDs is described at the end of this section.
 * <p>
 * For more information on global heaps for virtual datasets, see
 * “Disk Format: Level 1F - Global Heap Block for Virtual Datasets.”
 *
 * @see III.E. Disk Format: Level 1E - Global Heap
 * @author Johann Sorel (Geomatys)
 */
public final class GlobalHeap extends IOStructure {

    /**
     * The ASCII character string “GCOL” is used to indicate the beginning of a
     * collection. This gives file consistency checking utilities a better
     * chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "GCOL".getBytes(StandardCharsets.US_ASCII);

    /**
     * The objects are stored in any order with no intervening unused space.
     * <p>
     * Global Heap Object 0 (zero), when present, represents the free space in
     * the collection. Free space always appears at the end of the collection.
     * If the free space is too small to store the header for Object 0
     * (described below) then the header is implied and is not written.
     *
     * The field Object Size for Object 0 indicates the amount of possible free
     * space in the collection including the 16-byte header size of Object 0.
     */
    private Map<Integer,GlobalHeapObject> globalHeapObjects = new HashMap<>();

    public GlobalHeapObject getHeapObject(int index) {
        return globalHeapObjects.get(index);
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        Each collection has its own version number so that new collections can be
        added to old files. This document describes version one (1) of the
        collections (there is no version zero (0)).
         */
        channel.ensureVersion(1);
        channel.skipFully(3);
        /*
        This is the size in bytes of the entire collection including this field.
        The default (and minimum) collection size is 4096 bytes which is a typical
        file system block size. This allows for 127 16-byte heap objects plus
        their overhead (the collection header of 16 bytes and the 16 bytes of
        information about each heap object).
        */
        final long collectionSize = channel.readLength();
        final long endPosition = channel.getStreamPosition() + collectionSize - 8;

        while ((endPosition - channel.getStreamPosition()) >= 16) {
            final GlobalHeapObject gho = new GlobalHeapObject();
            gho.read(channel);
            if (globalHeapObjects.put(gho.heapObjectIndex, gho) != null) {
                throw new IOException("Corrupted global heap, two objects have the same index");
            }
            if (gho.heapObjectIndex == 0) {
                break;
            }
            channel.realign(8);
        }

    }
}
