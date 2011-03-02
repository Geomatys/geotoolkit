/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
import java.nio.channels.FileChannel;
import org.geotoolkit.data.shapefile.shp.ShapeType;
import org.geotoolkit.data.shapefile.shp.ShapefileHeader;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Handle writing of shx files.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ShxWriter {

    private final FileChannel channel;
    private final ByteBuffer buffer;

    public ShxWriter(final FileChannel channel){
        ensureNonNull("file channel", channel);
        this.channel = channel;
        buffer = ByteBuffer.allocateDirect(100);
    }

    /**
     * Returns the underlying channel. Use with caution.
     * 
     * @return the underlying channel
     */
    public FileChannel getChannel() {
        return channel;
    }

    /**
     * Moves in the FileChannel to the position where header starts.
     * This is position(0) in the fileChannel.
     */
    public void moveToHeaderStart() throws IOException {
        //move to the beginning of the channel
        channel.position(0);
    }

    /**
     * Moves in the FileChannel to the position where records starts.
     * This is position(100) in the fileChannel, just after the header.
     */
    public void moveToRecordStart() throws IOException {
        //move just after the header
        channel.position(100);
    }

    /**
     * SHP and SHX files share the same header structure.
     * This method will delegate call to ShapefileHeader.write .
     *
     * @see org.geotoolkit.data.shapefile.shp.ShapefileHeader
     */
    public void writeHeader(final ShapeType type, final int length, final double minX, final double minY,
            final double maxX, final double maxY) throws IOException{
        ShapefileHeader.write(buffer, type, length, minX, minY, maxX, maxY);
        drain();
    }

    /**
     * Write a record at the current position in the FileChannel.
     * @param offset : first value to write corresponf the the feature offset
     * @param length : first value to write corresponf the the feature length
     * @throws IOException
     */
    public void writeRecord(final int offset, final int length) throws IOException{
        buffer.putInt(offset);
        buffer.putInt(length);
        drain();
    }

    /**
     * Close the underlying Channels.
     */
    public void close() throws IOException {
        if (channel.isOpen()) {
            channel.close();
        }
    }

    /**
     * Drain buffer into underlying channel.
     */
    private void drain() throws IOException {
        buffer.flip();
        while (buffer.remaining() > 0)
            channel.write(buffer);
        buffer.flip().limit(buffer.capacity());
    }

}
