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
package org.geotoolkit.hdf.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.io.stream.ChannelDataInput;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.hdf.heap.GlobalHeap;
import org.geotoolkit.hdf.heap.GlobalHeapId;
import org.geotoolkit.hdf.heap.GlobalHeapObject;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class HDF5ChannelDataInput implements HDF5DataInput {

    private final ChannelDataInput input;
    private int offsetSize;
    private int lengthSize;

    private final Map<Long,GlobalHeap> globalheaps = new HashMap<>();

    public HDF5ChannelDataInput(byte[] datas) throws DataStoreException {
        ArgumentChecks.ensureNonNull("datas", datas);
        final StorageConnector cnx = new StorageConnector(ByteBuffer.wrap(datas).asReadOnlyBuffer());
        this.input = cnx.getStorageAs(ChannelDataInput.class);
    }

    public HDF5ChannelDataInput(ChannelDataInput channel) {
        ArgumentChecks.ensureNonNull("input", channel);
        this.input = channel;
    }

    @Override
    public synchronized GlobalHeap getGlobalHeap(long address) throws IOException {
        GlobalHeap heap = globalheaps.get(address);
        if (heap != null) return heap;
        heap = new GlobalHeap();
        input.mark();
        input.seek(address);
        heap.read(this);
        input.reset();
        globalheaps.put(address, heap);
        return heap;
    }

    @Override
    public GlobalHeapObject getGlobalHeapObject(GlobalHeapId id) throws IOException {
        final GlobalHeap globalHeap = getGlobalHeap(id.collectionAddress);
        return globalHeap.getHeapObject(id.objectIndex);
    }

    @Override
    public void skipFully(long nb) throws IOException {
        input.seek(input.getStreamPosition() + nb);
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return input.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return input.readUnsignedShort();
    }

    @Override
    public byte readByte() throws IOException {
        return input.readByte();
    }

    @Override
    public int readShort() throws IOException {
        return input.readShort();
    }

    @Override
    public int readInt() throws IOException {
        return input.readInt();
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return input.readUnsignedInt();
    }

    @Override
    public double readDouble() throws IOException {
        return input.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return input.readFloat();
    }

    @Override
    public byte[] readNBytes(int nb) throws IOException {
        return input.readBytes(nb);
    }

    @Override
    public int[] readInts(int nb) throws IOException {
        return input.readInts(nb);
    }

    @Override
    public long readLong() throws IOException {
        return input.readLong();
    }

    @Override
    public void setOffsetSize(int offsetSize) {
        this.offsetSize = offsetSize;
    }

    @Override
    public int getOffsetSize() {
        return offsetSize;
    }

    @Override
    public void setLengthSize(int lengthSize) {
        this.lengthSize = lengthSize;
    }

    @Override
    public int getLengthSize() {
        return lengthSize;
    }

    @Override
    public long readOffset() throws IOException {
        switch (offsetSize) {
            case 1 : return input.readUnsignedByte();
            case 2 : return input.readUnsignedShort();
            case 4 : return input.readUnsignedInt();
            case 8 : return input.readLong();
            default: throw new IOException("Incorrect offset size " + offsetSize);
        }
    }

    @Override
    public long readLength() throws IOException {
        switch (lengthSize) {
            case 1 : return input.readUnsignedByte();
            case 2 : return input.readUnsignedShort();
            case 4 : return input.readUnsignedInt();
            case 8 : return input.readLong();
            default: throw new IOException("Incorrect offset size " + offsetSize);
        }
    }

    @Override
    public void mark() throws IOException {
        input.mark();
    }

    @Override
    public void reset() throws IOException {
        input.reset();
    }

    @Override
    public long getStreamPosition() throws IOException {
        return input.getStreamPosition();
    }

    @Override
    public void seek(long position) throws IOException {
        input.seek(position);
    }

    @Override
    public ByteOrder order() throws IOException {
        return input.buffer.order();
    }

    @Override
    public void order(ByteOrder order) throws IOException {
        input.buffer.order(order);
    }

    @Override
    public long readBits(int nb) throws IOException {
        return input.readBits(nb);
    }

    @Override
    public void skipRemainingBits() throws IOException {
        input.skipRemainingBits();
    }

    /**
     * Read an unsigned int of variable byte length.
     */
    @Override
    public int readUnsignedInt(int nbBytes) throws IOException {
        switch (nbBytes) {
            case 1 : return input.readUnsignedByte();
            case 2 : return input.readUnsignedShort();
            case 4 : return input.readInt();
            default: throw new IOException("Unsupported size " + nbBytes);
        }
    }

    @Override
    public void close() throws IOException {
        input.channel.close();
    }
}
