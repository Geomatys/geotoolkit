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
public final class BTreeV2_Type8_IndexedAttributesAttributeName {

    /**
     * This is an 8-byte sequence of bytes and is the heap ID for the attribute in the object’s attribute fractal heap.
     */
    private byte[] heapId;
    /**
     * The object header message flags for the attribute message.
     */
    private int messageFlags;
    /**
     * This field is the creation order value for the attribute.
     */
    private int creationOrder;
    /**
     * This field is hash value of the name for the attribute.
     * The hash value is the Jenkins’ lookup3 checksum algorithm applied to the attribute’s name.
     */
    private int hash;

    public void read(HDF5DataInput channel) throws IOException {

        heapId = channel.readNBytes(8);
        messageFlags = channel.readUnsignedByte();
        creationOrder = channel.readInt();
        hash = channel.readInt();
    }
}
