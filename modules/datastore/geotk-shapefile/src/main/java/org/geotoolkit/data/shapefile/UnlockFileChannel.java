/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * A FileChannel that delegates all calls to the underlying FileChannel but for
 * {@link #implCloseChannel()} it also calls ShapefileFiles.unlock method to
 * release the lock on the URL.
 * 
 * @author jesse
 * @module pending
 */
public class UnlockFileChannel extends FileChannel implements ReadableByteChannel {

    private final FileChannel wrapped;
    private final ShpFiles shapefileFiles;
    private final URL url;
    private final Object holder;
    private final boolean write;
    private boolean closed;

    public UnlockFileChannel(final FileChannel channel, final ShpFiles shapefileFiles,
            final URL url, final Object holder, final boolean write) {
        this.wrapped = channel;
        this.shapefileFiles = shapefileFiles;
        this.url = url;
        this.holder = holder;
        this.closed = false;
        this.write = write;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void force(final boolean metaData) throws IOException {
        wrapped.force(metaData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FileLock lock(final long position, final long size, final boolean shared)
            throws IOException {
        return wrapped.lock(position, size, shared);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MappedByteBuffer map(final MapMode mode, final long position, final long size)
            throws IOException {
        return wrapped.map(mode, position, size);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long position() throws IOException {
        return wrapped.position();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FileChannel position(final long newPosition) throws IOException {
        return wrapped.position(newPosition);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int read(final ByteBuffer dst, final long position) throws IOException {
        return wrapped.read(dst, position);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        return wrapped.read(dst);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long read(final ByteBuffer[] dsts, final int offset, final int length)
            throws IOException {
        return wrapped.read(dsts, offset, length);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long size() throws IOException {
        return wrapped.size();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long transferFrom(final ReadableByteChannel src, final long position, final long count)
            throws IOException {
        return wrapped.transferFrom(src, position, count);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long transferTo(final long position, final long count, final WritableByteChannel target)
            throws IOException {
        return wrapped.transferTo(position, count, target);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FileChannel truncate(final long size) throws IOException {
        return wrapped.truncate(size);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FileLock tryLock(final long position, final long size, final boolean shared)
            throws IOException {
        return wrapped.tryLock(position, size, shared);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int write(final ByteBuffer src, final long position) throws IOException {
        return wrapped.write(src, position);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int write(final ByteBuffer src) throws IOException {
        return wrapped.write(src);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long write(final ByteBuffer[] srcs, final int offset, final int length)
            throws IOException {
        return wrapped.write(srcs, offset, length);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void implCloseChannel() throws IOException {
        try {
            wrapped.close();
        } finally {
            if (!closed) {
                closed = true;
                if (!write) {
                    shapefileFiles.unlockRead(url, holder);
                } else {
                    shapefileFiles.unlockWrite(url, holder);
                }
            }
        }

    }

}
