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

package org.geotoolkit.index.quadtree.fs;

import org.locationtech.jts.geom.Envelope;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FileSystemQuadTree extends QuadTree {

    @Deprecated
    public static FileSystemQuadTree load(final File file) throws IOException, StoreException{
       return load(file.toPath());
    }

    public static FileSystemQuadTree load(final Path file) throws IOException, StoreException{
        final FileChannel channel = FileChannel.open(file, StandardOpenOption.READ);

        final IndexHeader header = new IndexHeader(channel);
        final ByteOrder order = header.getByteOrder();
        final ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(order);
        channel.read(buf);
        buf.flip();

        return new FileSystemQuadTree(buf.getInt(), buf.getInt(), channel, order);
    }

    private final FileChannel channel;

    public FileSystemQuadTree(final int numShapes, final int maxDepth, final FileChannel channel,
                              final ByteOrder order) throws IOException{
        super(numShapes,maxDepth);
        this.channel = channel;
        setRoot(FileSystemIndexStore.readNode(channel, order));
    }

    @Override
    public void insert(final int recno, final Envelope bounds) {
        throw new UnsupportedOperationException("File quadtrees are immutable");
    }

    @Override
    public boolean trim() {
        return false;
    }

    @Override
    public void close() throws StoreException {
        super.close();
        try {
            channel.close();
        } catch (IOException e) {
            throw new StoreException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try{
            close();
        }finally{
            super.finalize();
        }
    }

}
