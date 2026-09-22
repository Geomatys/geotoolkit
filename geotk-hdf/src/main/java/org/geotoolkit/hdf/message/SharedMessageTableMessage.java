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
 * Header Message Name: Shared Message Table
 * <p>
 * Header Message Type: 0x000F
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description: This message is used to locate the table of shared
 * object header message (SOHM) indexes. Each index consists of information to
 * find the shared messages from either the heap or object header. This message
 * is only found in the superblock extension.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_shared">IV.A.2.p. The Shared Message Table Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageTableMessage extends Message {

    /**
     * This field is the address of the master table for shared object header
     * message indexes.
     */
    private long sharedObjectHeaderMessageTableAddress;
    /** This field is the number of indices in the master table. */
    private int numberOfIndices;

    /** Creates an empty message, filled by {@link #read(HDF5DataInput)}. */
    public SharedMessageTableMessage() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        sharedObjectHeaderMessageTableAddress = channel.readOffset();
        numberOfIndices = channel.readUnsignedByte();
    }
}
