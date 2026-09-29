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
 * Shared message, version 3 : locates a message stored in another object header
 * or in the shared object header message heap.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsec_fmt3_dataobject_hdr_msg">IV.A.2. Disk Format: Level 2A2 - Data Object Header Messages</a>
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageV3 extends Message {

    /**
     * The type of shared message location:
     * <ul>
     *   <li>0 : Message is not shared and is not shareable.</li>
     *   <li>1 : Message stored in file’s shared object header message heap (a shared message).</li>
     *   <li>2 : Message stored in another object’s header (a committed message).</li>
     *   <li>3 : Message stored is not shared, but is shareable.</li>
     * </ul>
     */
    private int type;
    /**
     * This field contains either a Size of Offsets-bytes address of the object
     * header containing the message to be shared, or an 8-byte fractal heap ID
     * for the message in the file’s shared object header message heap.
     */
    private Object location;

    /** Creates an empty message, filled by {@link #read(HDF5DataInput)}. */
    public SharedMessageV3() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number indicates changes in the format of shared object message
        and is described here:
        3 : Used by the library of version 1.8 and after. In this version,
        the Type field can indicate that the message is stored in the fractal heap.
         */
        channel.ensureVersion(3);
        type = channel.readUnsignedByte();
        if (type == 1) {
            location = channel.readOffset();
        } else {
            throw new IOException("TODO");
        }

    }
}
