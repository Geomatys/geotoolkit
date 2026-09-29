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

import org.geotoolkit.hdf.IOStructure;

/**
 * Parent class of all messages. A message is a piece of metadata stored in a
 * data object header, identified by its message type code.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsec_fmt3_dataobject_hdr_msg">IV.A.2. Disk Format: Level 2A2 - Data Object Header Messages</a>
 * @author Johann Sorel (Geomatys)
 */
public abstract class Message extends IOStructure {

    /** Creates an empty message, filled by {@link #read(org.geotoolkit.hdf.io.HDF5DataInput)}. */
    public Message() {
    }

    /**
     * Creates an empty message of the given type, still to be {@linkplain #read(org.geotoolkit.hdf.io.HDF5DataInput) read}.
     *
     * @param code message type code.
     * @param version message version.
     * @return a new, empty message.
     * @throws IllegalArgumentException if the message type or version is unknown.
     */
    public static Message forCode(int code, int version) {
        return switch (code) {
            case 0x0000 -> new NillMessage();
            case 0x0001 -> switch (version) {
                    case 1,2 -> new DataspaceMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0002 -> switch (version) {
                    case 0 -> new LinkInfo();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0003 -> new DatatypeMessage();
            case 0x0004 -> new FillValueOldMessage();
            case 0x0005 -> switch (version) {
                    case 1,2,3 -> new FillValueMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0006 -> switch (version) {
                    case 1 -> new LinkMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0007 -> switch (version) {
                    case 1 -> new ExternalFileListMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0008 -> switch (version) {
                    case 1,2,3,4,5 -> new DataLayoutMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0009 -> new BogusMessage();
            case 0x000A -> switch (version) {
                    case 0 -> new GroupInfoMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x000B -> switch (version) {
                    case 1,2 -> new FilterPipelineMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x000C -> switch (version) {
                    case 1,2,3 -> new AttributeMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x000D -> new ObjectCommentMessage();
            case 0x000E -> new ModificationTimeMessageOld();
            case 0x000F -> switch (version) {
                    case 0 -> new SharedMessageTableMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0010 -> new ObjectHeaderContinuationMessage();
            case 0x0011 -> new SymbolTableMessage();
            case 0x0012 -> switch (version) {
                    case 1 -> new ObjectModificationTimeMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0013 -> switch (version) {
                    case 0 -> new BtreeKValuesMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0014 -> switch (version) {
                    case 0 -> new DriverInfoMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0015 -> switch (version) {
                    case 0 -> new AttributeInfoMessage();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0016 -> switch (version) {
                    case 0 -> new ObjectReferenceCount();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            case 0x0017 -> switch (version) {
                    case 0 -> new FileSpaceInfoV0();
                    case 1 -> new FileSpaceInfoV1();
                    default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
                };
            default -> throw new IllegalArgumentException("Unknowned message type " + code +" version " + version);
        };
    }
}
