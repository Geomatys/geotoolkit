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
 * Description: This message stores the file space management strategy and
 * the addresses of the free-space managers. This is version 0 of the message.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_fsinfo">IV.A.2.x. The File Space Info Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class FileSpaceInfoV0 extends Message {

    /**
     * This is the file space strategy used to manage file space. There are
     * four types:
     * <ul>
     *   <li>1 : {@code H5F_FILE_SPACE_ALL_PERSIST}</li>
     *   <li>2 : {@code H5F_FILE_SPACE_ALL}</li>
     *   <li>3 : {@code H5F_FILE_SPACE_AGGR_VFD}</li>
     *   <li>4 : {@code H5F_FILE_SPACE_VFD}</li>
     * </ul>
     */
    private int strategy;
    /**
     * This is the smallest free-space section size that the free-space manager
     * will track.
     */
    private int threshold;
    /**
     * These are the six free-space manager addresses for the six file space
     * allocation types: {@code H5FD_MEM_SUPER}, {@code H5FD_MEM_BTREE},
     * {@code H5FD_MEM_DRAW}, {@code H5FD_MEM_GHEAP}, {@code H5FD_MEM_LHEAP}
     * and {@code H5FD_MEM_OHDR}.
     * <p>
     * Note that these six fields exist only if the value for the field
     * “Strategy” is {@code H5F_FILE_SPACE_ALL_PERSIST}.
     */
    private long freespaceManagerAddressForH5FD_MEM_SUPER;
    private long freespaceManagerAddressForH5FD_MEM_BTREE;
    private long freespaceManagerAddressForH5FD_MEM_DRAW;
    private long freespaceManagerAddressForH5FD_MEM_GHEAP;
    private long freespaceManagerAddressForH5FD_MEM_LHEAP;
    private long freespaceManagerAddressForH5FD_MEM_OHDR;

    /** Creates an empty message, filled by {@link #read(HDF5DataInput)}. */
    public FileSpaceInfoV0() {
    }

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
