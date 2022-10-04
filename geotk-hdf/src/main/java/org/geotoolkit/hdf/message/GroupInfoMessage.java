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
 * Header Message Name: Group Info
 * <p>
 * Header Message Type: 0x000A
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * This message stores information for the constants defining a “new style”
 * group’s behavior. Constant information will be stored in this message and
 * variable information will be stored in the Link Info message.
 * <p>
 * Note: the “estimated entry” information below is used when determining the
 * size of the object header for the group when it is created.
 *
 * @see IV.A.2.k. The Group Info Message
 * @author Johann Sorel (Geomatys)
 */
public final class GroupInfoMessage extends Message {

    /**
     * This is the group information flag with the following definition:
     * 0 : If set, link phase change values are stored.
     * 1 : If set, the estimated entry information is non-default and is stored.
     * 2-7 : Reserved
     */
    private int flags;
    /**
     * This field is present if bit 0 of Flags is set.
     *
     * The is the maximum number of links to store “compactly” (in the group’s
     * object header).
     */
    private int linkPhaseChangeMaximumCompactValue;
    /**
     * This field is present if bit 0 of Flags is set.
     *
     * This is the minimum number of links to store “densely” (in the group’s
     * fractal heap). The fractal heap’s address is located in the Link Info
     * message.
     */
    private int linkPhaseChangeMinimumDenseValue;
    /**
     * This field is present if bit 1 of Flags is set.
     *
     * This is the estimated number of entries in groups.
     * If this field is not present, the default value of 4 will be used for
     * the estimated number of group entries.
     */
    private int estimatedNumberOfEntries;
    /**
     * This field is present if bit 1 of Flags is set.
     *
     * This is the estimated length of entry name.
     * If this field is not present, the default value of 8 will be used for
     * the estimated link name length of group entries.
     */
    private int estimatedLinkNameLengthofEntries;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        flags = channel.readUnsignedByte();
        if ((flags & 1) != 0) {
            linkPhaseChangeMaximumCompactValue = channel.readUnsignedShort();
            linkPhaseChangeMinimumDenseValue = channel.readUnsignedShort();
        }
        if ((flags & 2) != 0) {
            estimatedNumberOfEntries = channel.readUnsignedShort();
            estimatedLinkNameLengthofEntries = channel.readUnsignedShort();
        }
    }
}
