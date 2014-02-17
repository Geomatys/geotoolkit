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

import com.vividsolutions.jts.geom.Envelope;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileSystemQuadTree extends QuadTree {

    public static FileSystemQuadTree load(final File file) throws IOException, StoreException{
        final FileInputStream fis = new FileInputStream(file);
        final FileChannel channel = fis.getChannel();

        final IndexHeader header = new IndexHeader(channel);
        final ByteOrder order = header.getByteOrder();
        final ByteBuffer buf = ByteBuffer.allocate(8);
        buf.order(order);
        channel.read(buf);
        buf.flip();

        return new FileSystemQuadTree(buf.getInt(), buf.getInt(), fis, channel, order);
    }

    private final FileInputStream fis;
    private final FileChannel channel;

    public FileSystemQuadTree(final int numShapes, final int maxDepth, final FileInputStream fis, 
                              final FileChannel channel, final ByteOrder order) throws IOException{
        super(numShapes,maxDepth);
        this.fis = fis;
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
            fis.close();
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
