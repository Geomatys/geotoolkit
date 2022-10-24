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
public final class BTreeV2_Type1_IndirectHugeObject {

    /**
     * The address of the huge object in the file.
     */
    private long hugeObjectAddress;
    /**
     * The length of the huge object in the file.
     */
    private long hugeObjectLength;
    /**
     * The heap ID for the huge object.
     */
    private long hugeObjectID;

    public void read(HDF5DataInput channel) throws IOException {

        hugeObjectAddress = channel.readOffset();
        hugeObjectLength = channel.readLength();
        hugeObjectID = channel.readLength();
    }
}
