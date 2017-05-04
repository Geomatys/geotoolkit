/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.shapefile.shx;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

import org.geotoolkit.data.shapefile.shp.ShapefileHeader;

import static org.geotoolkit.data.shapefile.ShapefileFeatureStoreFactory.*;
import org.geotoolkit.data.dbf.Closeable;

/**
 * ShxReader parser for .shx files.<br>
 *
 * For details on the index file, see <br>
 * <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf"><b>"ESRI(r)
 * Shapefile - A Technical Description"</b><br> * <i>'An ESRI White Paper .
 * May 1997'</i></a>
 *
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @module
 */
public final class ShxReader implements Closeable{

    private static final int RECS_IN_BUFFER = 2000;

     /**
     * Stores the creation stack trace if assertion are enable.
     */
    protected Throwable creationStack;
    private final FileChannel channel;
    private final ByteBuffer buffer;
    private final boolean useMemoryMappedBuffer;
    private final ShapefileHeader header;

    private int channelOffset;
    private int lastIndex = -1;
    private int recOffset;
    private int recLen;
    private int[] content;

    private volatile boolean closed = false;

    /**
     * Load the index file from the given channel.
     *
     * @param shpFiles The channel to read from.
     * @throws IOException If an error occurs.
     */
    public ShxReader(final ReadableByteChannel shxChannel, final boolean useMemoryMappedBuffer)
            throws IOException {

        // init the tracer if we need to debug a connection leak
        assert (creationStack = new IllegalStateException().fillInStackTrace()) != null;

        this.useMemoryMappedBuffer = useMemoryMappedBuffer;
        final ReadableByteChannel byteChannel = shxChannel;

        try {
            header = readHeader(byteChannel);

            //windows do not handle memory mapped buffer correctly
            //the buffer is released by the GC very late, which causes some file locks to remain.
//            if (byteChannel instanceof FileChannel) {
//
//                this.channel = (FileChannel) byteChannel;
//                if (useMemoryMappedBuffer) {
//                    LOGGER.finest("Memory mapping file...");
//                    this.buffer = this.channel.map(FileChannel.MapMode.READ_ONLY,
//                            0, this.channel.size());
//
//                    this.channelOffset = 0;
//                } else {
//                    LOGGER.finest("Reading from file...");
//                    this.buffer = ByteBuffer.allocateDirect(8 * RECS_IN_BUFFER);
//                    this.channelOffset = 0;
//                }
//
//            } else {
                this.channel = null;
                this.buffer = null;
                LOGGER.finest("Loading all shx...");
                readRecords(byteChannel);
                byteChannel.close();
//            }
        } catch (Throwable e) {
            if (byteChannel != null) {
                byteChannel.close();
            }
            throw new IOException(e);
        }
    }

    /**
     * Get the header of this index file.
     *
     * @return The header of the index file.
     */
    public ShapefileHeader getHeader() {
        return header;
    }

    private void check() {
        if (closed) {
            throw new IllegalStateException("Index file has been closed");
        }

    }

    private static ShapefileHeader readHeader(final ReadableByteChannel channel) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        while (buffer.remaining() > 0) {
            channel.read(buffer);
        }
        buffer.flip();
        return ShapefileHeader.read(buffer, true);
    }

    private void readRecords(final ReadableByteChannel channel) throws IOException {
        check();
        final int remaining = (header.getFileLength() * 2) - 100;
        final ByteBuffer buffer = ByteBuffer.allocateDirect(remaining);
        buffer.order(ByteOrder.BIG_ENDIAN);
        while (buffer.remaining() > 0) {
            channel.read(buffer);
        }
        buffer.flip();
        content = new int[remaining / 4]; // 2 integer for each record
        final IntBuffer ints = buffer.asIntBuffer();
        ints.get(content);
    }

    private void readRecord(final int index) throws IOException {
        check();
        final int pos = 100 + index * 8;
        if (!useMemoryMappedBuffer) {
            if (pos - channelOffset < 0
                    || channelOffset + buffer.limit() <= pos
                    || lastIndex == -1) {
                LOGGER.finest("Filling buffer...");
                channelOffset = pos;
                channel.position(pos);
                buffer.clear();
                channel.read(buffer);
                buffer.flip();
            }
        }

        buffer.position(pos - channelOffset);
        recOffset = buffer.getInt();
        recLen    = buffer.getInt();
        lastIndex = index;
    }

    @Override
    public void close() throws IOException {

        closed = true;
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        this.content = null;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void finalize() throws Throwable {
        if (!closed) {
            LOGGER.log(Level.WARNING,
                    "UNCLOSED ITERATOR : There is code leaving shx reader open, "
                    + "this may cause memory leaks or data integrity problems !");
            if (creationStack != null) {
                LOGGER.log(Level.WARNING,
                        "The unclosed shx reader originated on this stack trace", creationStack);
            }
            this.close();
        }
        super.finalize();
    }

    /**
     * Get the number of records in this index.
     *
     * @return The number of records.
     */
    public int getRecordCount() {
        return (header.getFileLength() * 2 - 100) / 8;
    }

    /**
     * Get the offset of the record (in 16-bit words).
     *
     * @param index The index, from 0 to getRecordCount - 1
     * @return The offset in 16-bit words.
     * @throws IOException
     */
    public int getOffset(final int index) throws IOException {

        if (this.channel != null) {
            if (this.lastIndex != index) {
                this.readRecord(index);
            }
            return this.recOffset;
        } else {
            return content[2 * index];
        }
    }

    /**
     * Get the offset of the record (in real bytes, not 16-bit words).
     *
     * @param index The index, from 0 to getRecordCount - 1
     * @return The offset in bytes.
     * @throws IOException
     */
    public int getOffsetInBytes(final int index) throws IOException {
        return this.getOffset(index) * 2;
    }

    /**
     * Get the content length of the given record in bytes, not 16 bit words.
     *
     * @param index The index, from 0 to getRecordCount - 1
     * @return The lengh in bytes of the record.
     * @throws IOException
     */
    public int getContentLength(final int index) throws IOException {

        if (this.channel != null) {
            if (this.lastIndex != index) {
                this.readRecord(index);
            }
            return this.recLen;
        } else {
            return content[2 * index + 1];
        }
    }

}
