/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import org.geotoolkit.index.quadtree.AbstractNode;
import org.geotoolkit.index.quadtree.IndexStore;
import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;

import static org.geotoolkit.index.quadtree.fs.IndexHeader.*;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileSystemIndexStore implements IndexStore {

    private final File file;
    private byte byteOrder;

    /**
     * Constructor. The byte order defaults to NEW_MSB_ORDER
     * 
     * @param file
     */
    public FileSystemIndexStore(final File file) {
        this.file = file;
        this.byteOrder = NEW_MSB_ORDER;
    }

    /**
     * Constructor
     * 
     * @param file
     * @param byteOrder
     */
    public FileSystemIndexStore(final File file, final byte byteOrder) {
        this.file = file;
        this.byteOrder = byteOrder;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store(final QuadTree tree) throws StoreException {
        // For efficiency, trim the tree
        tree.trim();

        // Open the stream...
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();

            final ByteBuffer buf = ByteBuffer.allocate(8);

            if (this.byteOrder > NATIVE_ORDER) {
                QuadTree.LOGGER.finest("Writing file header");

                final IndexHeader header = new IndexHeader(byteOrder);
                header.writeTo(buf);
                buf.flip();
                channel.write(buf);
            }

            final ByteOrder order = byteToOrder(this.byteOrder);

            buf.clear();
            buf.order(order);

            buf.putInt(tree.getNumShapes());
            buf.putInt(tree.getMaxDepth());
            buf.flip();

            channel.write(buf);

            this.writeNode(tree, tree.getRoot(), channel, order);
        } catch (IOException e) {
            throw new StoreException(e);
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                throw new StoreException(e);
            }finally{
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new StoreException(e);
                }
            }
        }
    }

    /**
     * Wites a tree node to the qix file
     * 
     * @param node
     *                The node
     * @param channel
     *                DOCUMENT ME!
     * @param order
     *                byte order
     * 
     * @throws IOException
     * @throws StoreException
     *                 DOCUMENT ME!
     */
    private void writeNode(final QuadTree tree, final AbstractNode node, final FileChannel channel, final ByteOrder order)
            throws IOException, StoreException {
        final int offset = this.getSubNodeOffset(node);

        final int numShapeIds = node.getNumShapeIds();
        final int numSubNodes = node.getNumSubNodes();

        final ByteBuffer buf = ByteBuffer.allocate((4 * 8) + (3 * 4) + (numShapeIds * 4));
        buf.order(order);
        buf.putInt(offset);

        final double[] env = node.getEnvelope();
        buf.putDouble(env[0]);
        buf.putDouble(env[1]);
        buf.putDouble(env[2]);
        buf.putDouble(env[3]);

        buf.putInt(numShapeIds);

        for (int i=0; i<numShapeIds; i++) {
            buf.putInt(node.getShapeId(i));
        }

        buf.putInt(numSubNodes);
        buf.flip();

        channel.write(buf);

        for (int i=0; i<numSubNodes; i++) {
            this.writeNode(tree, node.getSubNode(i), channel, order);
        }
    }

    /**
     * Calculates the offset
     * 
     * @param node
     * 
     * 
     * @throws StoreException
     *                 DOCUMENT ME!
     */
    private int getSubNodeOffset(final AbstractNode node) throws StoreException {
        int offset = 0;

        for (int i=0,n=node.getNumSubNodes(); i<n; i++) {
            final AbstractNode tmp = node.getSubNode(i);
            offset += (4 * 8); // Envelope size
            offset += ((tmp.getNumShapeIds() + 3) * 4); // Entries size + 3
            offset += this.getSubNodeOffset(tmp);
        }

        return offset;
    }

    /**
     * Loads a quadrtee stored in a '.qix' file. <b>WARNING:</b> The resulting
     * quadtree will be immutable; if you perform an insert, an
     * <code>UnsupportedOperationException</code> will be thrown.
     * 
     * @see IndexStore#load(org.geotoolkit.data.shapefile.shp.IndexFile)
     */
    @Override
    public QuadTree load() throws StoreException {
        QuadTree tree = null;

        try {
            if (QuadTree.LOGGER.isLoggable(Level.FINEST)) {
                QuadTree.LOGGER.log(Level.FINEST, "Opening QuadTree {0}", this.file.getCanonicalPath());
            }

            tree = FileSystemQuadTree.load(file);

            QuadTree.LOGGER.finest("QuadTree opened");
        } catch (IOException e) {
            throw new StoreException(e);
        }

        return tree;
    }

    static FileSystemNode readNode(final FileChannel channel, final ByteOrder order) throws IOException {
        return readNode(new ScrollingBuffer(channel, order));
    }

    static FileSystemNode readNode(final ScrollingBuffer buf)
            throws IOException {

        // offset(4) + envelope(32) + nbIds(4)
        buf.refillBuffer(40);

        // offset
        final int offset = buf.original.getInt();

        // envelope
        final double x1 = buf.original.getDouble();
        final double y1 = buf.original.getDouble();
        final double x2 = buf.original.getDouble();
        final double y2 = buf.original.getDouble();

        // shapes in this node
        final int numShapesId = buf.original.getInt();
        final int[] ids = new int[numShapesId];
        buf.getIntArray(ids);
        final int numSubNodes = buf.getInt();

        // let's create the new node
        final FileSystemNode node = new FileSystemNode(
                x1,y1,x2,y2, buf,(int)buf.getPosition(),offset);
        node.setShapesId(ids);
        node.setNumSubNodes(numSubNodes);

        return node;
    }

}
