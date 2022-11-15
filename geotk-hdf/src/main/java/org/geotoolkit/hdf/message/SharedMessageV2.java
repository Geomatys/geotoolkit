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
 *
 * @see IV.A.2. Disk Format: Level 2A2 - Data Object Header Messages
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageV2 extends Message {

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
        2 : Used by the library of version 1.6.1 and after.
         */
        channel.ensureVersion(2);
        type = channel.readUnsignedByte();
        address = channel.readOffset();
    }
}
