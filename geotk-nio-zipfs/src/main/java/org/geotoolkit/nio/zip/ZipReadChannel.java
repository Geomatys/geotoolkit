/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.nio.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import org.apache.sis.util.ArgumentChecks;

/**
 * Emulate a readable SeekableByteChannel oven an InputStream.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ZipReadChannel implements SeekableByteChannel {

    private final long size;
    private final InputStream input;
    private final ReadableByteChannel channel;
    //keep track of current position in the stream
    private long position = 0;

    /**
     * @param input zip entry data input, not null.
     * @param size zip entry uncompressed size.
     */
    ZipReadChannel(InputStream input, long size) {
        ArgumentChecks.ensureNonNull("input", input);
        this.input = input;
        this.size = size;
        this.channel = Channels.newChannel(input);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int nb = channel.read(dst);
        if (nb != -1) {
            position += nb;
        }
        return nb;
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        if (newPosition < position) {
            throw new IOException("Can not seek backward in zip entry");
        }
        //read datas until we reached wanted position or stream end
        long toSkip = newPosition - position;
        final int bufferSize = Math.min(Math.toIntExact(toSkip), 64000);
        final ByteBuffer bb = ByteBuffer.allocate(bufferSize);
        while (position < newPosition) {
            if (toSkip > bufferSize) {
                bb.position(0);
            } else {
                bb.position(bufferSize - Math.toIntExact(toSkip));
            }
            final int nbread = read(bb);
            if (nbread == -1) break;
            toSkip -= nbread;
        }
        return this;
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new IOException("Not supported.");
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new IOException("Not supported.");
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
        input.close();
    }
}
