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
 * Description: This message stores the file space management strategy, the
 * paged aggregation settings and the addresses of the free-space managers.
 * This is version 1 of the message.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_fsinfo">IV.A.2.x. The File Space Info Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class FileSpaceInfoV1 extends Message {

    /**
     * This is the file space strategy used to manage file space. There are
     * four types:
     * <ul>
     *   <li>0 : {@code H5F_FSPACE_STRATEGY_FSM_AGGR}</li>
     *   <li>1 : {@code H5F_FSPACE_STRATEGY_PAGE}</li>
     *   <li>2 : {@code H5F_FSPACE_STRATEGY_AGGR}</li>
     *   <li>3 : {@code H5F_FSPACE_STRATEGY_NONE}</li>
     * </ul>
     */
    private int strategy;
    /** True or false in persisting free-space. */
    private int persistingFreespace;
    /**
     * This is the smallest free-space section size that the free-space manager
     * will track.
     */
    private long freespaceSectionThreshold;
    /**
     * This is the file space page size, which is used when the paged
     * aggregation feature is enabled.
     */
    private int fileSpacePageSize;
    /**
     * This is the smallest free-space section size at the end of a page that
     * the free-space manager will track. This is used when the paged
     * aggregation feature is enabled.
     */
    private int pageEndMetadataThreshold;
    /**
     * The EOA before the allocation of free-space manager header and section
     * info for the self-referential free-space managers when persisting
     * free-space.
     * <p>
     * Note that self-referential free-space managers are managers that
     * involve file space allocation for the managers' free-space header and
     * section info.
     */
    private long eoa;
    /**
     * These are the addresses of the six small-sized free-space managers for
     * the six file space allocation types: {@code H5FD_MEM_SUPER}, {@code H5FD_MEM_BTREE},
     * {@code H5FD_MEM_DRAW}, {@code H5FD_MEM_GHEAP}, {@code H5FD_MEM_LHEAP}
     * and {@code H5FD_MEM_OHDR}.
     * <p>
     * Note that these six fields exist only if the value for the field
     * “Persisting free-space” is true.
     */
    private long addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_SUPER;
    private long addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_BTREE;
    private long addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_DRAW;
    private long addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_GHEAP;
    private long addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_LHEAP;
    private long addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_OHDR;
    /**
     * These are the addresses of the six large-sized free-space managers for
     * the six file space allocation types: {@code H5FD_MEM_SUPER}, {@code H5FD_MEM_BTREE},
     * {@code H5FD_MEM_DRAW}, {@code H5FD_MEM_GHEAP}, {@code H5FD_MEM_LHEAP}
     * and {@code H5FD_MEM_OHDR}.
     * <p>
     * Note that these six fields exist only if the value for the field
     * “Persisting free-space” is true.
     */
    private long addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_SUPER;
    private long addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_BTREE;
    private long addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_DRAW;
    private long addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_GHEAP;
    private long addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_LHEAP;
    private long addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_OHDR;

    /** Creates an empty message, filled by {@link #read(HDF5DataInput)}. */
    public FileSpaceInfoV1() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        This is version 1 of this message.
         */
        channel.ensureVersion(1);
        strategy = channel.readUnsignedByte();
        persistingFreespace = channel.readUnsignedByte();
        freespaceSectionThreshold = channel.readLength();
        fileSpacePageSize = channel.readInt();
        pageEndMetadataThreshold = channel.readUnsignedShort();
        eoa = channel.readOffset();
        addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_SUPER = channel.readOffset();
        addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_BTREE = channel.readOffset();
        addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_DRAW = channel.readOffset();
        addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_GHEAP = channel.readOffset();
        addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_LHEAP = channel.readOffset();
        addressOfSmallSizedFreeSpaceManagerForH5FD_MEM_OHDR = channel.readOffset();
        addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_SUPER = channel.readOffset();
        addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_BTREE = channel.readOffset();
        addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_DRAW = channel.readOffset();
        addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_GHEAP = channel.readOffset();
        addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_LHEAP = channel.readOffset();
        addressOfLargeSizedFreeSpaceManagerForH5FD_MEM_OHDR = channel.readOffset();
    }
}
