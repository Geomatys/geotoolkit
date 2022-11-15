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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * The shared object header message table is used to locate object header
 * messages that are shared between two or more object headers in the file.
 * Shared object header messages are stored and indexed in the file in one of
 * two ways: indexed sequentially in a shared header message list or indexed
 * with a v2 B-tree. The shared messages themselves are either stored in a
 * fractal heap (when two or more objects share the message), or remain in an
 * object’s header (when only one object uses the message currently, but the
 * message can be shared in the future).
 * <p>
 * The shared object header message table contains a list of shared message index
 * headers. Each index header records information about the version of the index
 * format, the index storage type, flags for the message types indexed, the
 * number of messages in the index, the address where the index resides, and the
 * fractal heap address if shared messages are stored there.
 * <p>
 * Each index can be either a list or a v2 B-tree and may transition between
 * those two forms as the number of messages in the index varies. Each shared
 * message record contains information used to locate the shared message from
 * either a fractal heap or an object header. The types of messages that can be
 * shared are: Dataspace, Datatype, Fill Value, Filter Pipeline and Attribute.
 * <p>
 * The shared object header message table is pointed to from a shared message
 * table message in the superblock extension for a file. This message stores the
 * version of the table format, along with the number of index headers in the
 * table.
 * <p>
 * Shared messages are indexed either with a shared message record list,
 * described below, or using a v2 B-tree (using record type 7). The number of
 * records in the shared message record list is determined in the index’s entry
 * in the shared object header message table.
 *
 * @see III.I. Disk Format: Level 1I - Shared Object Header Message Table
 * @author Johann Sorel (Geomatys)
 */
public final class SharedObjectHeaderMessageTable extends IOStructure {

    /**
     * The ASCII character string “SMTB” is used to indicate the beginning of
     * the Shared Object Header Message table. This gives file consistency
     * checking utilities a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "SMTB".getBytes(StandardCharsets.US_ASCII);

    public List<Index> indexes;

    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        int nbEntries = 0;
        indexes = new ArrayList<>(nbEntries);
        for (int i = 0; i < nbEntries; i++) {
            Index index = new Index();
            /*
            This is the version number for the list of shared object header
            message indexes and this document describes version 0.
            */
            channel.ensureVersion(0);
            index.indexType = channel.readUnsignedByte();
            index.messageTypeFlags = channel.readUnsignedShort();
            index.minimumMessageSize = channel.readInt();
            index.listCutOff = channel.readUnsignedShort();
            index.v2BtreeCutoff = channel.readUnsignedShort();
            index.numberOfMessages = channel.readUnsignedShort();
            index.indexAddress = channel.readOffset();
            index.fractalHeapAddress = channel.readOffset();
            indexes.add(index);
        }

        int checksum = channel.readInt();
    }

    public static class Index {
        /**
         * The type of index can be an unsorted list or a v2 B-tree.
         */
        private int indexType;
        /**
         * This field indicates the type of messages tracked in the index, as follows:
         * <ul>
         * <li>0 : If set, the index tracks Dataspace Messages. </li>
         * <li>1 : If set, the message tracks Datatype Messages. </li>
         * <li>2 : If set, the message tracks Fill Value Messages. </li>
         * <li>3 : If set, the message tracks Filter Pipeline Messages. </li>
         * <li>4 : If set, the message tracks Attribute Messages. </li>
         * <li>5-15 : Reserved (zero). </li>
         * </ul>
         * An index can track more than one type of message, but each type of message can only by in one index.
         */
        private int messageTypeFlags;
        /**
         * This is the message size sharing threshold for the index. If the
         * encoded size of the message is less than this value, the message is
         * not shared.
         */
        private int minimumMessageSize;
        /**
         * This is the cutoff value for the indexing of messages to switch from
         * a list to a v2 B-tree. If the number of messages is greater than this
         * value, the index should be a v2 B-tree.
         */
        private int listCutOff;
        /**
         * This is the cutoff value for the indexing of messages to switch from
         * a v2 B-tree back to a list. If the number of messages is less than
         * this value, the index should be a list.
         */
        private int v2BtreeCutoff;
        /**
         * The number of shared messages being tracked for the index.
         */
        private int numberOfMessages;
        /**
         * This field is the address of the list or v2 B-tree where the
         * index nodes reside.
         */
        private long indexAddress;
        /**
         * This field is the address of the fractal heap if shared messages
         * are stored there.
         */
        private long fractalHeapAddress;
    }
}
