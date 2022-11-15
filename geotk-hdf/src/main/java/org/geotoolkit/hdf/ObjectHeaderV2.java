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
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Note that the “total number of messages” field has been dropped from the data
 * object header prefix in this version. The number of messages in the data
 * object header is just determined by the messages encountered in all the object
 * header blocks.
 *
 * Note also that the fields and messages in this version of data object headers
 * have no alignment or padding bytes inserted - they are stored packed together.
 *
 * @see IV.A.1.b. Version 2 Data Object Header Prefix
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectHeaderV2 extends IOStructure implements ObjectHeader {

    /**
     * The ASCII character string “OHDR” is used to indicate the beginning of an
     * object header. This gives file consistency checking utilities a better
     * chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "OHDR".getBytes(StandardCharsets.US_ASCII);
    /**
     * This field is a bit field indicating additional information about the object header.
     * <ul>
     * <li>0-1 : This two bit field determines the size of the Size of Chunk #0 field. The values are:
     *  0 : The Size of Chunk #0 field is 1 byte.
     *  1 : The Size of Chunk #0 field is 2 bytes.
     *  2 :The Size of Chunk #0 field is 4 bytes.
     *  3 :The Size of Chunk #0 field is 8 bytes.
     * </li>
     * <li>2 : If set, attribute creation order is tracked.</li>
     * <li>3 : If set, attribute creation order is indexed.</li>
     * <li>4 : If set, non-default attribute storage phase change values are stored.</li>
     * <li>5 : If set, access, modification, change and birth times are stored.</li>
     * <li>6-7 : Reserved</li>
     * </ul>
     */
    private int flags;
    /**
     * This 32-bit value represents the number of seconds after the UNIX epoch
     * when the object’s raw data was last accessed (in other words, read or written).
     *
     * This field is present if bit 5 of flags is set.
     */
    private int accessTime;
    /**
     * This 32-bit value represents the number of seconds after the UNIX epoch
     * when the object’s raw data was last modified (in other words, written).
     *
     * This field is present if bit 5 of flags is set.
     */
    private int modificationTime;
    /**
     * This 32-bit value represents the number of seconds after the UNIX epoch
     * when the object’s metadata was last changed.
     *
     * This field is present if bit 5 of flags is set.
     */
    private int changeTime;
    /**
     * This 32-bit value represents the number of seconds after the UNIX epoch
     * when the object was created.
     *
     * This field is present if bit 5 of flags is set.
     */
    private int birthTime;
    /**
     * This is the maximum number of attributes to store in the compact format
     * before switching to the indexed format.
     *
     * This field is present if bit 4 of flags is set.
     */
    private int maximumCompactAttributes;
    /**
     * This is the minimum number of attributes to store in the indexed format
     * before switching to the compact format.
     *
     * This field is present if bit 4 of flags is set.
     */
    private int minimumDenseAttributes;

    private List<MsgLink> messages;

    @Override
    public List<org.geotoolkit.hdf.message.Message> getMessages() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static class MsgLink {

        /**
         * Same format as version 1 of the object header, described above.
         */
        private int messageType;
        /**
         * This value specifies the number of bytes of header message data
         * following the header message type and length information for the
         * current message. The size of messages in this version does not
         * include any padding bytes.
         */
        private int sizeOfData;
        /**
         * Same format as version 1 of the object header, described above.
         */
        private int flags;
        /**
         * This field stores the order that a message of a given type was created in.
         * This field is present if bit 2 of flags is set.
         */
        private int creationOrder;
        /**
         * Same format as version 1 of the object header, described above.
         */
        private byte[] data;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        channel.ensureSignature(SIGNATURE);
        /*
        This field has a value of 2 indicating version 2 of the object header.
         */
        channel.ensureVersion(2);
        flags = channel.readUnsignedByte();
        accessTime = channel.readInt();
        modificationTime = channel.readInt();
        changeTime = channel.readInt();
        birthTime = channel.readInt();
        maximumCompactAttributes = channel.readUnsignedShort();
        minimumDenseAttributes = channel.readUnsignedShort();
        /*
         * This unsigned value specifies the number of bytes of header message
         * data following this field that contain object header information.
         * This value does not include the size of object header continuation
         * blocks for this object elsewhere in the file.
         * The length of this field varies depending on bits 0 and 1 of the flags field.
         */
        int sizeOfChunk0 = channel.readUnsignedByte();

        /*
        A gap in an object header chunk is inferred by the end of the messages
        for the chunk before the beginning of the chunk’s checksum. Gaps are
        always smaller than the size of an object header message prefix
        (message type + message size + message flags).

        Gaps are formed when a message (typically an attribute message) in an earlier
        chunk is deleted and a message from a later chunk that does not quite
        fit into the free space is moved into the earlier chunk.
        */
        throw new IOException("TODO");
    }
}
