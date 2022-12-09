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

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class SequenceSeekableByteChannel implements SeekableByteChannel {

    private final SeekableByteChannel first;
    private final SeekableByteChannel second;
    private final long separation;
    private long position = 0;
    private boolean open = true;

    public SequenceSeekableByteChannel(SeekableByteChannel first, SeekableByteChannel second) throws IOException {
        this.first = first;
        this.second = second;
        this.separation = first.size();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        SeekableByteChannel channel = position < separation ? first : second;
        int nb = channel.read(dst);
        if (nb < 0) return nb;
        position += nb;
        return nb;
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        if (position < separation) first.position(newPosition);
        else second.position(newPosition - separation);
        this.position = newPosition;
        return this;
    }

    @Override
    public long size() throws IOException {
        return first.size() + second.size();
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
        first.close();
        second.close();
        open = false;
    }

}
