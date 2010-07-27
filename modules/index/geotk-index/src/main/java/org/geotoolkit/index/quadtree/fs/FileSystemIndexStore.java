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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import org.geotoolkit.index.quadtree.DataReader;
import org.geotoolkit.index.quadtree.IndexStore;
import org.geotoolkit.index.quadtree.Node;
import org.geotoolkit.index.quadtree.QuadTree;
import org.geotoolkit.index.quadtree.StoreException;

import com.vividsolutions.jts.geom.Envelope;

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
    public FileSystemIndexStore(File file) {
        this.file = file;
        this.byteOrder = NEW_MSB_ORDER;
    }

    /**
     * Constructor
     * 
     * @param file
     * @param byteOrder
     */
    public FileSystemIndexStore(File file, byte byteOrder) {
        this.file = file;
        this.byteOrder = byteOrder;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store(QuadTree tree) throws StoreException {
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
            } catch (Exception e) {
            }

            try {
                fos.close();
            } catch (Exception e) {
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
    private void writeNode(QuadTree tree, Node node, FileChannel channel, ByteOrder order)
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
    private int getSubNodeOffset(Node node) throws StoreException {
        int offset = 0;

        for (int i=0,n=node.getNumSubNodes(); i<n; i++) {
            Node tmp = node.getSubNode(i);
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
    public QuadTree load(DataReader indexfile) throws StoreException {
        QuadTree tree = null;

        try {
            if (QuadTree.LOGGER.isLoggable(Level.FINEST)) {
                QuadTree.LOGGER.log(Level.FINEST, "Opening QuadTree {0}", this.file.getCanonicalPath());
            }

            final FileInputStream fis = new FileInputStream(file);
            final FileChannel channel = fis.getChannel();

            final IndexHeader header = new IndexHeader(channel);

            final ByteOrder order = byteToOrder(header.getByteOrder());
            final ByteBuffer buf = ByteBuffer.allocate(8);
            buf.order(order);
            channel.read(buf);
            buf.flip();

            tree = new QuadTree(buf.getInt(), buf.getInt(), indexfile) {
                @Override
                public void insert(int recno, Envelope bounds) {
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
            };

            tree.setRoot(FileSystemNode.readNode(channel, order));

            QuadTree.LOGGER.finest("QuadTree opened");
        } catch (IOException e) {
            throw new StoreException(e);
        }

        return tree;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param order
     * 
     */
    private static ByteOrder byteToOrder(byte order) {
        ByteOrder ret = null;

        switch (order) {
            case NATIVE_ORDER:
                ret = ByteOrder.nativeOrder();break;

            case LSB_ORDER:
            case NEW_LSB_ORDER:
                ret = ByteOrder.LITTLE_ENDIAN;break;

            case MSB_ORDER:
            case NEW_MSB_ORDER:
                ret = ByteOrder.BIG_ENDIAN;break;
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the byteOrder.
     */
    public int getByteOrder() {
        return this.byteOrder;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param byteOrder
     *                The byteOrder to set.
     */
    public void setByteOrder(byte byteOrder) {
        this.byteOrder = byteOrder;
    }
}
