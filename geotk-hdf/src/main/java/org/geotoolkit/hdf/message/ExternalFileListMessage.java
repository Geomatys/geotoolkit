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
 * Header Message Name: External Data Files
 * <p>
 * Header Message Type: 0x0007
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * The external data storage message indicates that the data for an object is
 * stored outside the HDF5 file. The filename of the object is stored as a
 * Universal Resource Location (URL) of the actual filename containing the data.
 * An external file list record also contains the byte offset of the start of
 * the data within the file and the amount of space reserved in the file for
 * that data.
 *
 * @see IV.A.2.h. The Data Storage - External Data Files Message
 * @author Johann Sorel (Geomatys)
 */
public final class ExternalFileListMessage extends Message {

    /**
     * The total number of slots allocated in the message. Its value must be at
     * least as large as the value contained in the Used Slots field.
     * (The current library simply uses the number of Used Slots for this message)
     */
    private int allocatedSlots;
    /**
     * The number of initial slots which contains valid information.
     */
    private int usedSlots;
    /**
     * This is the address of a local heap which contains the names for the
     * external files (The local heap information can be found in Disk Format
     * Level 1D in this document). The name at offset zero in the heap is always
     * the empty string.
     */
    private long heapAddress;

    /**
     * The slot definitions are stored in order according to the array addresses
     * they represent.
     */
    private Object slotDefinitions;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number information is used for changes in the format of
        External Data Storage Message and is described here:
        0 : Never used.
        1 : The current version used by the library.
         */
        channel.ensureVersion(1);
        channel.skipFully(3);
        allocatedSlots = channel.readUnsignedShort();
        usedSlots = channel.readUnsignedShort();
        heapAddress = channel.readOffset();
        throw new IOException("TODO");
    }

    private class ExternalFileListSlot {
        /**
         * The byte offset within the local name heap for the name of the file.
         * File names are stored as a URL which has a protocol name, a host name,
         * a port number, and a file name: protocol:port//host/file. If the
         * protocol is omitted then “file:” is assumed. If the port number is
         * omitted then a default port for that protocol is used. If both the
         * protocol and the port number are omitted then the colon can also be
         * omitted. If the double slash and host name are omitted then
         * “localhost” is assumed. The file name is the only mandatory part,
         * and if the leading slash is missing then it is relative to the
         * application’s current working directory (the use of relative names is
         * not recommended).
         */
        private long nameOffsetinLocalHeap;
        /**
         * This is the byte offset to the start of the data in the specified
         * file. For files that contain data for a single dataset this will
         * usually be zero.
         */
        private long offsetinExternalDataFile;
        /**
         * This is the total number of bytes reserved in the specified file for
         * raw data storage. For a file that contains exactly one complete
         * dataset which is not extendable, the size will usually be the exact
         * size of the dataset. However, by making the size larger one allows
         * HDF5 to extend the dataset. The size can be set to a value larger
         * than the entire file since HDF5 will read zeroes past the end of the
         * file without failing.
         */
        private long dataSizeinExternalFile;

        public void read(HDF5DataInput channel) throws IOException {
            nameOffsetinLocalHeap = channel.readLength();
            offsetinExternalDataFile = channel.readLength();
            dataSizeinExternalFile = channel.readLength();
        }
    }
}
