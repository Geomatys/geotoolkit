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
 * Header Message Name: Link Info
 * <p>
 * Header Message Type: 0x002
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description: The link info message tracks variable information about
 * the current state of the links for a “new style” group’s behavior. Variable
 * information will be stored in this message and constant information will be
 * stored in the Group Info message.
 *
 * @see IV.A.2.c. The Link Info Message
 * @author Johann Sorel (Geomatys)
 */
public final class LinkInfo extends Message {

    /**
     * This field determines various optional aspects of the link info message:
     * 0 : If set, creation order for the links is tracked.
     * 1 : If set, creation order for the links is indexed.
     * 2-7 : Reserved
     */
    private int flags;
    /**
     * This 64-bit value is the maximum creation order index value stored for a
     * link in this group.
     * This field is present if bit 0 of flags is set.
     */
    private long maximumCreationIndex;
    /**
     * This is the address of the fractal heap to store dense links. Each link
     * stored in the fractal heap is stored as a Link Message.
     *
     * If there are no links in the group, or the group’s links are stored
     * “compactly” (as object header messages), this value will be the undefined address.
     */
    private long fractalHeapAddress;
    /**
     * This is the address of the version 2 B-tree to index names of links.
     * If there are no links in the group, or the group’s links are stored
     * “compactly” (as object header messages), this value will be the
     * undefined address.
     */
    private long addressOfv2btreeNameIndex;
    /**
     * This is the address of the version 2 B-tree to index creation order of links.
     *
     * If there are no links in the group, or the group’s links are stored
     * “compactly” (as object header messages), this value will be the undefined address.
     *
     * This field exists if bit 1 of flags is set.
     */
    private long addressOfv2btreeCreationOrderIndex;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        flags = channel.readUnsignedByte();
        if ((flags & 1) != 0) maximumCreationIndex = channel.readLong();
        fractalHeapAddress = channel.readOffset();
        addressOfv2btreeNameIndex = channel.readOffset();
        if ((flags & 2) != 0) addressOfv2btreeCreationOrderIndex = channel.readOffset();
    }
}
