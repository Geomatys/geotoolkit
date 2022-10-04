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
public final class BTreeV2_Type7_SharedObjectHeaderMessage_Subtype1_MessageInObjectHeader {

    /**
     * This field Indicates the location where the message is stored:
     * <ul>
     *   <li>0 : Shared message is stored in shared message index heap.</li>
     *   <li>1 : Shared message is stored in object header.</li>
     * </ul>
     */
    private int messageLocation;
    /**
     * This field is hash value of the shared message. The hash value is the
     * Jenkins’ lookup3 checksum algorithm applied to the shared message.
     */
    private int hash;
    /**
     * The object header message type of the shared message.
     */
    private int messageType;
    /**
     * This field indicates that the shared message is the nth message of its
     * type in the specified object header.
     */
    private int objectHeaderIndex;
    /**
     * The address of the object header containing the shared message.
     */
    private long objectHeaderAddress;

    public void read(HDF5DataInput channel) throws IOException {

        messageLocation = channel.readUnsignedByte();
        hash = channel.readInt();
        channel.skipFully(1);
        messageType = channel.readInt();
        objectHeaderIndex = channel.readInt();
        objectHeaderAddress = channel.readOffset();
    }
}
