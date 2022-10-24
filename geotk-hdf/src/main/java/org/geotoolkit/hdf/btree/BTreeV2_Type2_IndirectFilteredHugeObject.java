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
package org.geotoolkit.hdf.btree;

import java.io.IOException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.A.2. Disk Format: Level 1A2 - Version 2 B-trees
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV2_Type2_IndirectFilteredHugeObject {

    /**
     * The address of the filtered huge object in the file.
     */
    private long filteredHugeObjectAddress;
    /**
     * The length of the filtered huge object in the file.
     */
    private long filteredHugeObjectLength;
    /**
     * A 32-bit bit field indicating which filters have been skipped for this chunk.
     * Each filter has an index number in the pipeline (starting at 0, with the
     * first filter to apply) and if that filter is skipped, the bit corresponding
     * to its index is set.
     */
    private int filterMask;
    /**
     * The size of the de-filtered huge object in memory.
     */
    private long filteredHugeObjectMemorySize;
    /**
     * The heap ID for the huge object.
     */
    private long hugeObjectID;

    public void read(HDF5DataInput channel) throws IOException {

        filteredHugeObjectAddress = channel.readOffset();
        filteredHugeObjectLength = channel.readLength();
        filterMask = channel.readInt();
        filteredHugeObjectMemorySize = channel.readLength();
        hugeObjectID = channel.readLength();
    }
}
