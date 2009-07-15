/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.rtree.fs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.DataDefinition.Field;
import org.geotoolkit.index.rtree.Entry;
import org.geotoolkit.index.rtree.Node;
import org.geotoolkit.index.rtree.PageStore;

import com.vividsolutions.jts.geom.Envelope;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/fs/FileSystemPageStore.java $
 */
public class FileSystemPageStore extends PageStore {
    protected static final byte B_SHORT = (byte) 1;
    protected static final byte B_INTEGER = (byte) 2;
    protected static final byte B_LONG = (byte) 3;
    protected static final byte B_FLOAT = (byte) 4;
    protected static final byte B_DOUBLE = (byte) 5;
    protected static final byte B_STRING = (byte) 6;
    private static final int DEF_MAX = 50;
    private static final int DEF_MIN = 25;
    private static final short DEF_SPLIT = SPLIT_QUADRATIC;
    private static final int FILE_VERSION = 19730103;
    private Parameters params = new Parameters();
    private RandomAccessFile raFile;
    private FileChannel channel;
    private ByteBuffer header;
    private FileSystemNode root;

    /**
     * Loads an index from the specified <code>File</code>, if the file
     * doesn't exists or is 0 length, a new index will be created with default
     * values for maxNodeEntries, minNodeEntries and splitAlgorithm
     * 
     * @param file
     *                The file that stores the index
     * 
     * @throws TreeException
     */
    public FileSystemPageStore(File file) throws TreeException {
        super();

        if (file.isDirectory()) {
            throw new TreeException("Cannot use a directory as index!");
        }

        this.params.setMaxNodeEntries(DEF_MAX);
        this.params.setMinNodeEntries(DEF_MIN);
        this.params.setSplitAlg(DEF_SPLIT);

        try {
            this.init(file);
        } catch (IOException e) {
            throw new TreeException(e);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param file
     * @param def
     * 
     * @throws TreeException
     */
    public FileSystemPageStore(File file, DataDefinition def)
            throws TreeException {
        this(file, def, DEF_MAX, DEF_MIN, DEF_SPLIT);
    }

    /**
     * Create and index with the specified values, if the file exists then an
     * <code>RTreeException</code> will be thrown.
     * 
     * @param file
     *                The file to store the index
     * @param def
     *                DOCUMENT ME!
     * @param maxNodeEntries
     * @param minNodeEntries
     * @param splitAlg
     * 
     * @throws TreeException
     */
    public FileSystemPageStore(File file, DataDefinition def,
            int maxNodeEntries, int minNodeEntries, short splitAlg)
            throws TreeException {
        super(def, maxNodeEntries, minNodeEntries, splitAlg);

        if (file.exists() && (file.length() != 0)) {
            throw new TreeException("Cannot set dataDefinition, "
                    + "maxNodesEntries and "
                    + "minNodeEntries to the existing index " + file);
        }

        if (file.isDirectory()) {
            throw new TreeException("Cannot use a directory as index!");
        }

        this.params.setDataDef(def);
        this.params.setMaxNodeEntries(maxNodeEntries);
        this.params.setMinNodeEntries(minNodeEntries);
        this.params.setSplitAlg(splitAlg);

        try {
            this.init(file);
        } catch (IOException e) {
            throw new TreeException(e);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param file
     * 
     * @throws IOException
     * @throws TreeException
     */
    private void init(File file) throws IOException, TreeException {
        this.raFile = new RandomAccessFile(file, "rw");
        this.channel = raFile.getChannel();

        this.params.setChannel(this.channel);

        try {
            if (file.length() > 0) {
                this.loadIndex();
            } else {
                this.prepareIndex();
            }
        } catch (TreeException e) {
            // close the channel, or we won't be able to delete the grx file
            channel.close();
            throw new TreeException(e);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws IOException
     * @throws TreeException
     */
    private void loadIndex() throws IOException, TreeException {
        Charset charset = Charset.forName("US-ASCII");

        ByteBuffer buf = ByteBuffer.allocate(8);
        this.channel.read(buf);
        buf.position(0);

        if (buf.getInt() != FILE_VERSION) {
            throw new TreeException("Wrong file version, shoud be "
                    + FILE_VERSION);
        }

        // Get the header size
        int headerSize = buf.getInt();
        buf.position(0);

        this.header = ByteBuffer.allocate(headerSize);
        this.header.put(buf);
        this.header.mark();
        this.channel.read(this.header);
        this.header.reset();

        this.params.setMaxNodeEntries(this.header.getInt());
        this.params.setMinNodeEntries(this.header.getInt());
        this.params.setSplitAlg(this.header.getShort());

        // Get the charset used for string encoding
        int chLen = this.header.getInt();
        byte[] bytes = new byte[chLen];
        this.header.get(bytes);

        CharBuffer dummy = charset.decode(ByteBuffer.wrap(bytes));
        dummy.position(0);

        String defCharset = dummy.toString();

        DataDefinition def = new DataDefinition(defCharset);
        byte type = 0;
        int remaining = this.header.remaining();

        for (int i = 0; i < (remaining - 8); i += 5) {
            type = this.header.get();

            if (type == B_STRING) {
                def.addField(this.header.getInt());
            } else {
                if (type == B_SHORT) {
                    def.addField(Short.class);
                } else if (type == B_INTEGER) {
                    def.addField(Integer.class);
                } else if (type == B_LONG) {
                    def.addField(Long.class);
                } else if (type == B_FLOAT) {
                    def.addField(Float.class);
                } else if (type == B_DOUBLE) {
                    def.addField(Double.class);
                }

                this.header.getInt(); // Not used
            }
        }

        this.params.setDataDef(def);

        long rootOffset = this.header.getLong();

        this.root = new FileSystemNode(this.params, rootOffset);
    }

    /**
     * DOCUMENT ME!
     * 
     * @throws IOException
     * @throws TreeException
     */
    private void prepareIndex() throws IOException, TreeException {
        if (this.params.getDataDef() == null) {
            throw new TreeException("Data definition cannot be null "
                    + "when creating a new index.");
        }

        Charset charset = Charset.forName("US-ASCII");
        ByteBuffer chBuf = charset.encode(this.params.getDataDef().getCharset()
                .name());
        chBuf.position(0);

        int headerSize = 22 + chBuf.capacity()
                + (5 * this.params.getDataDef().getFieldsCount()) + 8;

        this.header = ByteBuffer.allocate(headerSize);

        this.header.putInt(FILE_VERSION);
        this.header.putInt(headerSize);
        this.header.putInt(this.params.getMaxNodeEntries());
        this.header.putInt(this.params.getMinNodeEntries());
        this.header.putShort(this.params.getSplitAlg());

        // Store data definition
        this.header.putInt(chBuf.capacity());
        this.header.put(chBuf);

        Field field = null;
        Class clazz = null;

        for (int i = 0; i < this.params.getDataDef().getFieldsCount(); i++) {
            field = this.params.getDataDef().getField(i);
            clazz = field.getFieldClass();

            if (clazz.isAssignableFrom(Short.class)) {
                this.header.put(B_SHORT);
            } else if (clazz.isAssignableFrom(Integer.class)) {
                this.header.put(B_INTEGER);
            } else if (clazz.isAssignableFrom(Long.class)) {
                this.header.put(B_LONG);
            } else if (clazz.isAssignableFrom(Float.class)) {
                this.header.put(B_FLOAT);
            } else if (clazz.isAssignableFrom(Double.class)) {
                this.header.put(B_DOUBLE);
            } else if (clazz.isAssignableFrom(String.class)) {
                this.header.put(B_STRING);
            }

            this.header.putInt(field.getLen());
        }

        this.header.putLong(headerSize); // The initial root offset

        this.header.position(0);
        this.channel.write(this.header);

        if (this.params.getForceChannel()) {
            this.channel.force(true);
        }

        // Create the root
        this.root = new FileSystemNode(this.params);
        this.root.setLeaf(true);
        this.root.save();
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getRoot()
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#setRoot(org.geotools.index.rtree.Node)
     */
    public void setRoot(Node node) throws TreeException {
        try {
            FileSystemNode n = (FileSystemNode) node;
            n.setParent(null);
            this.root = n;

            this.header.position(this.header.limit() - 8);
            this.header.putLong(n.getOffset());

            this.header.position(0);

            synchronized (this.channel) {
                this.channel.position(0);
                this.channel.write(this.header);

                if (this.params.getForceChannel()) {
                    this.channel.force(true);
                }
            }
        } catch (IOException e) {
            throw new TreeException(e);
        }
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getEmptyNode(boolean)
     */
    public Node getEmptyNode(boolean isLeaf) {
        FileSystemNode node = new FileSystemNode(params);
        node.setLeaf(isLeaf);

        return node;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getNode(long,
     *      org.geotools.index.rtree.Node)
     */
    public Node getNode(Entry parentEntry, Node parent) throws TreeException {
        Node node = null;
        long offset = ((Long) parentEntry.getData()).longValue();

        try {
            node = new FileSystemNode(this.params, offset);
            node.setParent(parent);
        } catch (IOException e) {
            throw new TreeException(e);
        }

        return node;
    }

    /**
     * @see org.geotools.index.rtree.PageStore#createEntryPointingNode(org.geotools.index.rtree.Node)
     */
    public Entry createEntryPointingNode(Node node) {
        FileSystemNode fn = (FileSystemNode) node;

        return new Entry(new Envelope(fn.getBounds()), new Long(fn.getOffset()));
    }

    /**
     * @see org.geotools.index.rtree.PageStore#free(org.geotools.index.rtree.Node)
     */
    public void free(Node node) {
        try {
            FileSystemNode fn = (FileSystemNode) node;
            fn.free();
        } catch (IOException e) {
            // Ignore
        }
    }

    /**
     * @see org.geotools.index.rtree.PageStore#close()
     */
    public void close() throws TreeException {
        try {
            this.header.position(0);

            synchronized (this.channel) {
                this.channel.position(0);
                this.channel.write(this.header);
                this.channel.force(true);
            }

            this.raFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new TreeException(e);
        }
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getMaxNodeEntries()
     */
    public int getMaxNodeEntries() {
        return this.params.getMaxNodeEntries();
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getMinNodeEntries()
     */
    public int getMinNodeEntries() {
        return this.params.getMinNodeEntries();
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getSplitAlgorithm()
     */
    public short getSplitAlgorithm() {
        return this.params.getSplitAlg();
    }

    /**
     * @see org.geotools.index.rtree.PageStore#getKeyDefinition()
     */
    public DataDefinition getDataDefinition() {
        return this.params.getDataDef();
    }

    /**
     * If this is set to <code>true</code>, then every write to the index
     * will call a force() on the associated channel
     * 
     * @param b
     *                true or false
     */
    public void setForceChannel(boolean b) {
        this.params.setForceChannel(b);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return The state of Force channel parameter
     */
    public boolean getForceChannel() {
        return this.params.getForceChannel();
    }
}
