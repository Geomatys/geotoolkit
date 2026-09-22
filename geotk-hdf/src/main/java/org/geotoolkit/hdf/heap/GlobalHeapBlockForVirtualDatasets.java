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
 * The layout for the global heap block used with virtual datasets is described
 * below. For more information on global heaps,
 * see {@link GlobalHeap} (“Disk Format: Level 1E - Global Heap”).
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsec_fmt3_infra_globalheapvds">III.F. Disk Format: Level 1F - Global Heap Block for Virtual Datasets</a>
 * @author Johann Sorel (Geomatys)
 */
public final class GlobalHeapBlockForVirtualDatasets extends IOStructure {

    /**
     * The number of entries in the block.
     */
    private long numEntries;

    /** Creates an empty block, filled by {@link #read(HDF5DataInput)}. */
    public GlobalHeapBlockForVirtualDatasets() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        /*
        The version number for the block; the value is 0.
        */
        channel.ensureVersion(0);
        numEntries = channel.readLength();

        /*
        Source Filename #n
        The source file name where the source dataset is located.

        Source Dataset #n
        The source dataset name that is mapped to the virtual dataset.

        Source Selection #n
        The dataspace selection in the source dataset that is mapped to the virtual selection.

        Virtual Selection #n
        This is the dataspace selection in the virtual dataset that is mapped to the source selection.

        Checksum
        This is the checksum for the block.
        */

        throw new IOException("TODO");
    }
}
