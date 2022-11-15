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
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * @see III.E. Disk Format: Level 1E - Global Heap
 * @author Johann Sorel (Geomatys)
 */
public final class GlobalHeapObject extends IOStructure {

    /**
     * Each object has a unique identification number within a collection.
     * The identification numbers are chosen so that new objects have the
     * smallest value possible with the exception that the identifier 0 always
     * refers to the object which represents all free space within the collection.
     */
    public int heapObjectIndex;
    /**
     * All heap objects have a reference count field. An object which is
     * referenced from some other part of the file will have a positive reference
     * count. The reference count for Object 0 is always zero.
     */
    public int referenceCount;
    /**
     * This is the size of the object data stored for the object. The actual
     * storage space allocated for the object data is rounded up to a
     * multiple of eight.
     */
    public long objectSize;
    /**
     * Object data position in the channel.
     *
     * The object data is treated as a one-dimensional array of bytes to be
     * interpreted by the caller.
     */
    public long streamDataPosition;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        heapObjectIndex = channel.readUnsignedShort();
        referenceCount = channel.readUnsignedShort();
        channel.skipFully(4);
        objectSize = channel.readLength();
        streamDataPosition = channel.getStreamPosition();
        if (heapObjectIndex != 0) {
            //object 0 is a marker which indicates the remaining space in the heap.
            channel.skipFully(Math.toIntExact(objectSize));
        }
    }

}
