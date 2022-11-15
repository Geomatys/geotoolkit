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
package org.geotoolkit.hdf;

import java.io.IOException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.I. Disk Format: Level 1I - Shared Object Header Message Table
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageRecordStoredInObjectHeader extends IOStructure {

    /**
     * This has a value of 1 indicating that the message is stored in an object header.
     */
    private int messageLocation;
    /**
     * This is the hash value for the message.
     */
    private int hashValue;
    /**
     * This is the message type in the object header.
     */
    private int messageType;
    /**
     * This is the creation index of the message within the object header.
     */
    private int creationIndex;
    /**
     * This is the address of the object header where the message is located.
     */
    private long objectHeaderAddress;


    @Override
    public void read(HDF5DataInput channel) throws IOException {

        messageLocation = channel.readUnsignedByte();
        hashValue = channel.readInt();
        channel.skipFully(1);
        messageType = channel.readUnsignedByte();
        creationIndex = channel.readUnsignedByte();
        objectHeaderAddress = channel.readOffset();
    }
}
