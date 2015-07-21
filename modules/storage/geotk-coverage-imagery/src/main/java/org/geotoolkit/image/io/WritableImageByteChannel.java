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
package org.geotoolkit.image.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import javax.imageio.stream.ImageOutputStream;

/**
 * Wraps an ImageOutputStream as a SeekableByeChannel.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WritableImageByteChannel implements SeekableByteChannel{

    private final ImageOutputStream stream;
    private boolean open = false;

    public WritableImageByteChannel(ImageOutputStream stream) {
        this.stream = stream;
        this.stream.mark();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        final int remaining = dst.remaining();
        final byte[] array = new byte[remaining];
        final int nb = stream.read(array);
        dst.put(array);
        return nb;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        final int remaining = src.remaining();
        final byte[] array = new byte[remaining];
        src.get(array);
        stream.write(array);
        return remaining;
    }

    @Override
    public long position() throws IOException {
        return stream.getStreamPosition();
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        stream.seek(newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return 0;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        open = false;
        stream.flush();
        stream.close();
    }

}
