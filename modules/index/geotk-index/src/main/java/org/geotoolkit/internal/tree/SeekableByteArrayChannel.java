/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.tree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import org.apache.sis.util.ArgumentChecks;

/**
 * A readable and writable channel backed by an array.
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp class SeekableByteArrayChannel implements SeekableByteChannel {

    /**
     * Bytes array where to write the data.
     * The length of this array is the capacity.
     */
    private byte[] data;

    /**
     * Number of valid bytes in the {@link #data} array.
     */
    private int limit;

    /**
     * Current position in the {@link #data} array.
     */
    private int position;

    /**
     * Sets to {@code true} after {@link #close()} has been invoked.
     */
    private boolean isClosed;
    
    /**
     * Creates a channel which will store all written data in a byte array which have length initialized at 1 mb.<br><br>
     * 
     * <strong>Usually use this constructor in writing mode.</strong>
     */
    public SeekableByteArrayChannel() {
        this(new byte[1000000], 0);//-- set limit at zero to simulate empty channel.
    }
    
    /**
     * Creates a channel which will store all written data in the given array.<br>
     * Moreover the length of this internal stored data array may be increase 
     * in case where you try to write more data than this array length, and also initialize channel limit at data array size.<br><br>
     * 
     * <strong>Usually use this constructor in reading mode.</strong>
     *
     * @param data Bytes array where to write the data.
     */
    public SeekableByteArrayChannel(final byte[] data) {
        this(data, data.length);
    }
    
    /**
     * Creates a channel which will store all written data in the given array.<br>
     * Moreover the length of this internal stored data array may be increase 
     * in case where you try to write more data than this array length.
     * 
     * @param data empty or already filled Tree informations.
     * @param limit size of this channel. Usually, 0 for writting treeAccess mode and data.size or lesser for reading treeAccess mode.
     */
    public SeekableByteArrayChannel(final byte[] data, final int limit) {
        ArgumentChecks.ensureNonNull("SeekableByteArrayChannel : data", data);
        ArgumentChecks.ensureBetween("SeekableByteArrayChannel : limit", 0, data.length, limit);
        this.data  = data;
        this.limit = limit;
    }
    
    /**
     * Reads a sequence of bytes from this channel into the given buffer.
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        ensureOpen();
        if (position >= limit) {
            return -1;
        }
        final int length = StrictMath.min(dst.remaining(), limit - position);
        dst.put(data, position, length);
        position += length;
        return length;
    }

    /**
     * Writes a sequence of bytes to this channel from the given buffer.
     */
    @Override
    public int write(final ByteBuffer src) throws IOException {
        ensureOpen();
        final int length = src.remaining();
        ensureDataContain(length);
        src.get(data, position, length);
        position += length;
        limit = StrictMath.max(limit, position);
        return length;
    }

    /**
     * Returns this channel position.
     */
    @Override
    public long position() throws IOException {
        ensureOpen();
        return position;
    }

    /**
     * Sets this channel position.
     */
    @Override
    public SeekableByteChannel position(final long newPosition) throws IOException {
        ensureOpen();
        ArgumentChecks.ensureBetween("position", 0, size(), newPosition);
        position = (int) newPosition;
        return this;
    }

    /**
     * Returns the current size.
     */
    @Override
    public long size() throws IOException {
        ensureOpen();
        return limit;
    }

    /**
     * Truncates the data to the given size.
     */
    @Override
    public SeekableByteChannel truncate(final long size) throws IOException {
        ensureOpen();
        ArgumentChecks.ensureBetween("position", 0, limit, size);
        limit = (int) size;
        return this;
    }

    /**
     * Tells whether or not this channel is open.
     */
    @Override
    public boolean isOpen() {
        return !isClosed;
    }

    /**
     * Closes this channel.
     */
    @Override
    public void close() throws IOException {
        isClosed = true;
    }
    
    /**
     * Verifies that the channel is open.
     */
    private void ensureOpen() throws IOException {
        if (isClosed) {
            throw new ClosedChannelException();
        }
    }
    
    /**
     * Verify that internal byte data array own enough space to store length byte.
     * If it has already enough space, do nothing, else multiply current data array size by 2.
     * 
     * @param length byte number which will be written.
     */
    private void ensureDataContain(final int length) {
        ArgumentChecks.ensurePositive("ensure data contain : length", length);
        if (length > data.length - limit) 
            data = Arrays.copyOf(data, StrictMath.max(1000000, data.length << 1));
    }
    
    /**
     * Returns internal stored datas array truncated at {@link #limit} size.  
     * 
     * @return datas array truncated at {@link #limit} size.
     */
    public byte[] getData() {
        return Arrays.copyOf(data, limit);
    }
}
