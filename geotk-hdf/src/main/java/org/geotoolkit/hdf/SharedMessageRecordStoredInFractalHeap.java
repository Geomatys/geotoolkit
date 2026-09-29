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
 * Shared message record of an index node, for a message stored in the fractal heap of the index.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsec_fmt3_infra_sohm">III.I. Disk Format: Level 1I - Shared Object Header Message Table</a>
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageRecordStoredInFractalHeap extends IOStructure {

    /**
     * This has a value of 0 indicating that the message is stored in the heap.
     */
    private int messageLocation;
    /**
     * This is the hash value for the message.
     */
    private int hashValue;
    /**
     * This is the number of times the message is used in the file.
     */
    private int referenceCount;
    /**
     * This is an 8-byte fractal heap ID for the message as stored in the fractal heap for the index.
     */
    public Object fractalHeapId;


    /** Creates an empty structure, filled by {@link #read(HDF5DataInput)}. */
    public SharedMessageRecordStoredInFractalHeap() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        messageLocation = channel.readUnsignedByte();
        hashValue = channel.readInt();
        referenceCount = channel.readInt();
        /*
        fractalHeapId
        */
        throw new IOException("TODO");
    }
}
