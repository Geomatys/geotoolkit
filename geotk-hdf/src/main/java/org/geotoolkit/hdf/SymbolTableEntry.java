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
import java.util.Optional;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.btree.BTreeV1;
import org.geotoolkit.hdf.heap.LocalHeap;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Each symbol table entry in a symbol table node is designed to allow for very
 * fast browsing of stored objects. Toward that design goal, the symbol table
 * entries include space for caching certain constant metadata from the object header.
 *
 * @see III.C. Disk Format: Level 1C - Symbol Table Entry
 * @author Johann Sorel (Geomatys)
 */
public final class SymbolTableEntry extends IOStructure {

    /**
     * This is the byte offset into the group’s local heap for the name of the link.
     * The name is null terminated.
     */
    private long linkNameOffset;
    /**
     * Every object has an object header which serves as a permanent location
     * for the object’s metadata. In addition to appearing in the object header,
     * some of the object’s metadata can be cached in the scratch-pad space.
     */
    private long objectHeaderAddress;
    /**
     * The cache type is determined from the object header.
     * It also determines the format for the scratch-pad space:
     * <ul>
     * <li>0 : No data is cached by the group entry. This is guaranteed to be
     * the case when an object header has a link count greater than one.</li>
     * <li>1 : Group object header metadata is cached in the scratch-pad space.
     * This implies that the symbol table entry refers to another group.</li>
     * <li>2 : The entry is a symbolic link. The first four bytes of the
     * scratch-pad space are the offset into the local heap for the link value.
     * The object header address will be undefined.</li>
     * </ul>
     */
    private int cacheType;
    /**
     * If the Cache Type field contains the value one (1)
     * <p>
     * This is the file address for the root of the group’s B-tree.
     */
    private long addressOfBtree;
    /**
     * If the Cache Type field contains the value one (1)
     * <p>
     * This is the file address for the group’s local heap, in which are stored
     * the group’s symbol names.
     */
    private long addressOfNameHeap;
    /**
     * If the Cache Type field contains the value two (2)
     * <p>
     * The value of a symbolic link (that is, the name of the thing to which
     * it points) is stored in the local heap. This field is the 4-byte offset
     * into the local heap for the start of the link value, which is null terminated.
     */
    public int offsetToLinkValue;

    //cache values
    private String name;
    private ObjectHeader objectHeader;
    private BTreeV1 btree;
    private LocalHeap localHeap;

    /**
     * The cache type is determined from the object header.
     * It also determines the format for the scratch-pad space:
     * <ul>
     * <li>0 : No data is cached by the group entry. This is guaranteed to be
     * the case when an object header has a link count greater than one.</li>
     * <li>1 : Group object header metadata is cached in the scratch-pad space.
     * This implies that the symbol table entry refers to another group.</li>
     * <li>2 : The entry is a symbolic link. The first four bytes of the
     * scratch-pad space are the offset into the local heap for the link value.
     * The object header address will be undefined.</li>
     * </ul>
     */
    public int getCacheType() {
        return cacheType;
    }

    /**
     * Every object has an object header which serves as a permanent location
     * for the object’s metadata. In addition to appearing in the object header,
     * some of the object’s metadata can be cached in the scratch-pad space.
     */
    public long getObjectHeaderAddress() {
        return objectHeaderAddress;
    }

    public synchronized ObjectHeader getHeader(HDF5DataInput channel) throws IOException, DataStoreException {
        if (objectHeader == null && channel.isDefinedOffset(objectHeaderAddress)) {
            channel.mark();
            channel.seek(objectHeaderAddress);
            final int objectHeaderVersion = channel.readUnsignedByte();
            objectHeader = ObjectHeader.forVersion(objectHeaderVersion);
            channel.seek(channel.getStreamPosition() - 1);
            objectHeader.read(channel);
            channel.reset();
        }
        return objectHeader;
    }

    /**
     *
     * @param channel
     * @param heap this must be the heap of the parent group
     * @return
     * @throws IOException
     */
    public synchronized Optional<String> getName(HDF5DataInput channel, LocalHeap heap) throws IOException {
        if (name == null && channel.isDefinedOffset(linkNameOffset)) {
            if (heap != null) {
                name = heap.getString(channel, linkNameOffset);
            }
        }
        return Optional.ofNullable(name);
    }

    public synchronized Optional<BTreeV1> getBTree(HDF5DataInput channel) throws IOException, DataStoreException {
        if (btree == null && cacheType == 1) {
            channel.mark();
            channel.seek(addressOfBtree);
            btree = (BTreeV1) IOStructure.loadIdentifiedObject(channel);
            channel.reset();
        }
        return Optional.ofNullable(btree);
    }

    public synchronized Optional<LocalHeap> getLocalHeap(HDF5DataInput channel) throws IOException, DataStoreException {
        if (btree == null && cacheType == 1) {
            channel.mark();
            channel.seek(addressOfNameHeap);
            localHeap = (LocalHeap) IOStructure.loadIdentifiedObject(channel);
            channel.reset();
        }
        return Optional.ofNullable(localHeap);
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        linkNameOffset = channel.readOffset();
        objectHeaderAddress = channel.readOffset();
        cacheType = channel.readInt();
        channel.skipFully(4);

        /**
         * scratch pad :
         * This space is used for different purposes, depending on the value of the
         * Cache Type field. Any metadata about an object represented in the
         * scratch-pad space is duplicated in the object header for that object.
         * <p>
         * Furthermore, no data is cached in the group entry scratch-pad space if
         * the object header for the object has a link count greater than one.
         */
        if (cacheType == 0) {
            //contains nothing
            channel.skipFully(16);
        } else if (cacheType == 1) {
            addressOfBtree = channel.readOffset();
            addressOfNameHeap = channel.readOffset();
            channel.skipFully(16 - channel.getOffsetSize()*2);
        } else if (cacheType == 2) {
            offsetToLinkValue = channel.readInt();
            channel.skipFully(12);
        }
    }
}
