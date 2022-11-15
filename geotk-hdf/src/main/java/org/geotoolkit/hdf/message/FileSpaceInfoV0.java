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
 * Header Message Name: File Space Info
 * <p>
 * Header Message Type: 0x0017
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description : See specification
 *
 * @see IV.A.2.x. The File Space Info Message
 * @author Johann Sorel (Geomatys)
 */
public final class FileSpaceInfoV0 extends Message {

    /**
     * This is the file space strategy used to manage file space. There are
     * four types:
     * Value Description
     * 1 : H5F_FILE_SPACE_ALL_PERSIST
     * 2 : H5F_FILE_SPACE_ALL
     * 3 : H5F_FILE_SPACE_AGGR_VFD
     * 4 : H5F_FILE_SPACE_VFD
     */
    private int strategy;
    /**
     * This is the smallest free-space section size that the free-space manager
     * will track.
     */
    private int threshold;
    /**
     * These are the six free-space manager addresses for the six file space
     * allocation types:
     * H5FD_MEM_SUPER
     * H5FD_MEM_BTREE
     * H5FD_MEM_DRAW
     * H5FD_MEM_GHEAP
     * H5FD_MEM_LHEAP
     * H5FD_MEM_OHDR
     *
     * Note that these six fields exist only if the value for the field
     * “Strategy” is H5F_FILE_SPACE_ALL_PERSIST.
     */
    private long freespaceManagerAddressForH5FD_MEM_SUPER;
    private long freespaceManagerAddressForH5FD_MEM_BTREE;
    private long freespaceManagerAddressForH5FD_MEM_DRAW;
    private long freespaceManagerAddressForH5FD_MEM_GHEAP;
    private long freespaceManagerAddressForH5FD_MEM_LHEAP;
    private long freespaceManagerAddressForH5FD_MEM_OHDR;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        /*
        This is version 0 of this message.
         */
        channel.ensureVersion(0);
        strategy = channel.readUnsignedByte();
        threshold = channel.readUnsignedShort();
        freespaceManagerAddressForH5FD_MEM_SUPER = channel.readOffset();
        freespaceManagerAddressForH5FD_MEM_BTREE = channel.readOffset();
        freespaceManagerAddressForH5FD_MEM_DRAW = channel.readOffset();
        freespaceManagerAddressForH5FD_MEM_GHEAP = channel.readOffset();
        freespaceManagerAddressForH5FD_MEM_LHEAP = channel.readOffset();
        freespaceManagerAddressForH5FD_MEM_OHDR = channel.readOffset();
    }
}
