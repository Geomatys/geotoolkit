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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.List;
import org.geotoolkit.hdf.chunk.Chunk;
import org.geotoolkit.hdf.message.FilterPipelineMessage;

/**
 * Read only channel over the decoded content of a {@link Chunk}.
 * <p>
 * The stored bytes are read and the filter pipeline reversed lazily, on the
 * first read operation; the whole decoded chunk is then kept in memory.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChunkSeekableByteChannel implements SeekableByteChannel{

    private final Chunk chunk;
    private final HDF5DataInput channel;
    private final List<FilterPipelineMessage.FilterDescription> filters;
    private byte[] back;
    private long position = 0;
    private boolean open = true;

    /**
     * Creates a channel over the given chunk.
     *
     * @param chunk chunk to read
     * @param channel file channel
     * @param filters applied in reversed order on the chunk datas.
     */
    public ChunkSeekableByteChannel(Chunk chunk, HDF5DataInput channel, List<FilterPipelineMessage.FilterDescription> filters) {
        this.chunk = chunk;
        this.channel = channel;
        this.filters = filters;
    }

    /**
     * Read a chunk from the file and reverse its filter pipeline.
     *
     * @param chunk chunk to read
     * @param channel file channel
     * @param filters filter pipeline of the dataset, applied in reverse order
     * @return the decoded chunk bytes
     * @throws IOException if an error occurred while reading or decoding the chunk.
     */
    public static byte[] decode(Chunk chunk, HDF5DataInput channel,
            List<FilterPipelineMessage.FilterDescription> filters) throws IOException {
        byte[] array;
        synchronized (channel) {
            channel.seek(chunk.address);
            array = channel.readNBytes(Math.toIntExact(chunk.size));
        }
        /*
        Filters are reversed in the opposite order they were applied, and a
        filter whose bit is set in the chunk filter mask was skipped when
        writing this particular chunk, so it must not be reversed. This
        happens for partial edge chunks.
        */
        for (int i = filters.size() - 1; i >= 0; i--) {
            if (chunk.isFilterSkipped(i)) continue;
            array = filters.get(i).getFilter().decode(array);
        }
        chunk.uncompressedSize = array.length;
        return array;
    }

    private byte[] getBack() throws IOException {
        if (back == null) {
            back = decode(chunk, channel, filters);
        }
        return back;
    }

    @Override
    public long size() throws IOException {
        if (chunk.uncompressedSize == -1) {
            if (filters.isEmpty()) {
                chunk.uncompressedSize = chunk.size;
            } else {
                //no other choice then to decompress data
                getBack();
            }
        }
        return chunk.uncompressedSize;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        final byte[] array = getBack();
        final int offset = (int) position;
        final int remaining = back.length - offset;
        //a position past the end must report the end, not compute a negative length
        if (remaining <= 0) return -1;
        final int nb = Math.min(remaining, dst.remaining());
        dst.put(array, offset, nb);
        position += nb;
        return nb;
    }

    @Override
    public long position() throws IOException {
        if (!open) throw new ClosedChannelException();
        return position;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        if (!open) throw new ClosedChannelException();
        if (newPosition < 0) throw new IllegalArgumentException("Position is negative");
        this.position = newPosition;
        return this;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!open) throw new ClosedChannelException();
        throw new NonWritableChannelException();
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        if (!open) throw new ClosedChannelException();
        throw new NonWritableChannelException();
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        open = false;
    }

}
