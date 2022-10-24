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
 * Header Message Name: Link
 * <p>
 * Header Message Type: 0x0006
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may be repeated.
 * <p>
 * Description:
 * This message encodes the information for a link in a group’s object header, when the group is storing its links “compactly”, or in the group’s fractal heap, when the group is storing its links “densely”.
 * <p>
 * A group is storing its links compactly when the fractal heap address in the Link Info Message is set to the “undefined address” value.
 *
 * @see IV.A.2.g. The Link Message
 * @author Johann Sorel (Geomatys)
 */
public final class LinkMessage extends Message {

    /**
     * This field contains information about the link and controls the presence
     * of other fields below.
     * 0-1 : Determines the size of the Length of Link Name field.
     *     0 : The size of the Length of Link Name field is 1 byte.
     *     1 : The size of the Length of Link Name field is 2 bytes.
     *     2 : The size of the Length of Link Name field is 4 bytes.
     *     3 : The size of the Length of Link Name field is 8 bytes.
     * 2 : Creation Order Field Present: if set, the Creation Order field is present. If not set, creation order information is not stored for links in this group.
     * 3 : Link Type Field Present: if set, the link is not a hard link and the Link Type field is present. If not set, the link is a hard link.
     * 4 : Link Name Character Set Field Present: if set, the link name is not represented with the ASCII character set and the Link Name Character Set field is present. If not set, the link name is represented with the ASCII character set.
     * 5-7 : Reserved (zero).
     */
    private int flags;
    /**
     * This field is present if bit 3 of Flags is set.
     *
     * This is the link class type and can be one of the following values:
     * 0 : A hard link (should never be stored in the file)
     * 1 : A soft link.
     * 2-63 : Reserved for future HDF5 internal use.
     * 64 : An external link.
     * 65-255 : Reserved, but available for user-defined link types.
     */
    private int linkType;
    /**
     * This field is present if bit 2 of Flags is set.
     *
     * This 64-bit value is an index of the link’s creation time within the group.
     * Values start at 0 when the group is created an increment by one for each
     * link added to the group. Removing a link from a group does not change
     * existing links’ creation order field.
     */
    private long creationOrder;
    /**
     * This field is present if bit 4 of Flags is set.
     *
     * This is the character set for encoding the link’s name:
     * 0 : ASCII character set encoding (this should never be stored in the file)
     * 1 : UTF-8 character set encoding
     */
    private int linkNameCharacterSet;
    /**
     * This is the length of the link’s name. The size of this field depends on
     * bits 0 and 1 of Flags.
     */
    private int lengthOfLinkName;
    /**
     * This is the name of the link, non-NULL terminated.
     */
    private byte[] linkName;
    /**
     * The format of this field depends on the link type.
     */
    private byte[] linkInformation;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 1.
        */
        channel.ensureVersion(1);
        flags = channel.readUnsignedByte();
        linkType = channel.readUnsignedByte();
        creationOrder = channel.readLong();
        linkNameCharacterSet = channel.readUnsignedByte();
        lengthOfLinkName = channel.readUnsignedByte();
        linkName = channel.readNBytes(lengthOfLinkName);
        throw new IOException("TODO");
    }
}
