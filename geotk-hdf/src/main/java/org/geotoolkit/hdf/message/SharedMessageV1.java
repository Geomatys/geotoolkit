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
package org.geotoolkit.hdf.message;

import java.io.IOException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Data object header messages are small pieces of metadata that are stored in
 * the data object header for each object in an HDF5 file. Data object header
 * messages provide the metadata required to describe an object and its contents,
 * as well as optional pieces of metadata that annotate the meaning or purpose
 * of the object.
 *
 * Data object header messages are either stored directly in the data object header
 * for the object or are shared between multiple objects in the file. When a
 * message is shared, a flag in the Message Flags indicates that the actual
 * Message Data portion of that message is stored in another location (such as
 * another data object header, or a heap in the file) and the Message Data field
 * contains the information needed to locate the actual information for the message.
 *
 * @see IV.A.2. Disk Format: Level 2A2 - Data Object Header Messages
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageV1 extends Message {

    /**
     * The type of shared message location:
     * 0 : Message stored in another object’s header (a committed message).
     */
    private int type;
    /**
     * The address of the object header containing the message to be shared.
     */
    private long address;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number is used when there are changes in the format of a
        shared object message and is described here:
        0 : Never used.
        1 : Used by the library before version 1.6.1.
         */
        channel.ensureVersion(1);
        type = channel.readUnsignedByte();
        channel.skipFully(6);
        address = channel.readOffset();
    }
}
