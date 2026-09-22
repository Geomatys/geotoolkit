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
 * The format for the ID used to locate an object in the global heap.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsec_fmt3_infra_globalheap">III.E. Disk Format: Level 1E - Global Heap</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GlobalHeapId extends IOStructure {

    /**
     * This field is the address of the global heap collection where the data
     * object is stored.
     */
    public long collectionAddress;
    /**
     * This field is the index of the data object within the global heap collection.
     */
    public int objectIndex;

    /** Creates an empty global heap ID, filled by {@link #read(HDF5DataInput)}. */
    public GlobalHeapId() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        collectionAddress = channel.readOffset();
        objectIndex = channel.readInt();
    }
}
