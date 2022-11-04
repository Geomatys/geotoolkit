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
public final class ConstantSeekableByteChannel implements SeekableByteChannel {

    private static final byte[] ZEROS = new byte[4096];

    private final long size;
    private final byte[] pattern;
    private long position = 0;
    private boolean open = true;

    public ConstantSeekableByteChannel(long size, byte[] pattern) {
        this.size = size;

        reduce:
        if (pattern.length > 1) {
            //simplify to a single byte pattern if possible
            byte r = pattern[0];
            for (int i = 1; i < pattern.length; i++) {
                if (pattern[i] != r) break reduce;
            }
            pattern = new byte[]{r};
        }
        if (pattern.length == 1 && pattern[0] == 0) {
            pattern = ZEROS;
        }
        this.pattern = pattern;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (!open) throw new ClosedChannelException();
        final long remaining = size - position;
        if (remaining <= 0) return -1;
        final long len = Math.min(dst.remaining(), remaining);
        if (pattern == ZEROS) {
            final int r = (int) Math.min(len, ZEROS.length);
            dst.put(ZEROS, 0, r);
            position += r;
            return r;
        } else {
            int r = (int) Math.min(len, remaining);
            final int total = r;
            int patternOffset = (int) (position % pattern.length);
            while (r > 0) {
                int nb = Math.min(pattern.length - patternOffset, r);
                dst.put(pattern, patternOffset, nb);
                patternOffset = 0;
                r -= nb;
            }
            position += total;
            return total;
        }
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        open = false;
    }


    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        if (!open) throw new ClosedChannelException();
        if (position < 0) throw new IllegalArgumentException("Position is negative");
        this.position = newPosition;
        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
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

}
