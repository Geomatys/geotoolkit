/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.btree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.util.StringUtilities;

/**
 * Version 1 B-trees in HDF5 files are an implementation of the B-link tree.
 * The sibling nodes at a particular level in the tree are stored in a doubly-linked list.
 * See the “Efficient Locking for Concurrent Operations on B-trees” paper by
 * Phillip Lehman and S. Bing Yao as published in the ACM Transactions on
 * Database Systems, Vol. 6, No. 4, December 1981.
 * <p>
 * The B-trees implemented by the file format contain one more key than the
 * number of children. In other words, each child pointer out of a B-tree node
 * has a left key and a right key. The pointers out of internal nodes point to
 * sub-trees while the pointers out of leaf nodes point to symbol nodes and raw
 * data chunks. Aside from that difference, internal nodes and leaf nodes are
 * identical.
 * <p>
 *  Conceptually, each B-tree node looks like this:
 * key[0] child[0] key[1] child[1] key[2] ... ... key[N-1] child[N-1] key[N]
 * <p>
 * where child[i] is a pointer to a sub-tree (at a level above Level 0) or to data (at Level 0).
 * Each key[i] describes an item stored by the B-tree (a chunk or an object of a group node).
 * The range of values represented by child[i] is indicated by key[i] and key[i+1].
 * <p>
 * The following question must next be answered: “Is the value described by key[i]
 * contained in child[i-1] or in child[i]?” The answer depends on the type of tree.
 * In trees for groups (node type 0), the object described by key[i] is the greatest
 * object contained in child[i-1] while in chunk trees (node type 1) the chunk
 * described by key[i] is the least chunk in child[i].
 * <p>
 * That means that key[0] for group trees is sometimes unused; it points to offset
 * zero in the heap, which is always the empty string and compares as “less-than”
 * any valid object name.
 * <p>
 * And key[N] for chunk trees is sometimes unused; it contains a chunk offset which
 * compares as “greater-than” any other chunk offset and has a chunk byte size of
 * zero to indicate that it is not actually allocated.
 *
 *
 * @see III.A.1. Disk Format: Level 1A1 - Version 1 B-trees
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV1 extends IOStructure {

    /**
     * The ASCII character string “TREE” is used to indicate the beginning of a B-tree node.
     * This gives file consistency checking utilities a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "TREE".getBytes(StandardCharsets.US_ASCII);

    public Header header;
    public Node root;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        read(channel, new int[0]);
    }

    /**
     *
     * @param channel
     * @param chunkDimension only used for nodeType == 1
     * @throws IOException
     */
    public void read(HDF5DataInput channel, int[] chunkDimension) throws IOException {
        header = new Header();
        header.read(channel);
        if (header.nodeType == 0) {
            root = new GroupNode();
            root.read(channel, header, chunkDimension);
        } else if (header.nodeType == 1) {
            if (header.nodeLevel == 0) {
                root = new LeafChunksNode();
                root.read(channel, header, chunkDimension);
            } else {
                root = new ParentChunksNode();
                root.read(channel, header, chunkDimension);
            }
        } else {
            throw new IOException("Unexpected node type");
        }
    }

    @Override
    public String toString() {
        return IOStructure.reflectionToString(this);
    }

    /**
     * A node of the btree.
     * May contain other nodes.
     */
    public static abstract class Node {

        abstract void read(HDF5DataInput channel, Header header, int[] chunkDimension) throws IOException;
    }

    public static final class GroupNode extends Node {

        /**
         * For nodes of node type 0
         * (group nodes), the key is formatted as follows:
         * A single field of Size of Lengths bytes:
         * Indicates the byte offset into the local heap for the first object name
         * in the subtree which that key describes.
         */
        public long[] groupChildKeys;
        /**
         * Child Pointer
         * The tree node contains file addresses of subtrees or data depending on
         * the node level. Nodes at Level 0 point to data addresses, either raw
         * data chunks or group nodes. Nodes at non-zero levels point to other
         * nodes of the same B-tree.
         *
         * For raw data chunk nodes, the child pointer is the address of a single
         * raw data chunk. For group nodes, the child pointer points to a symbol
         * table, which contains information for multiple symbol table entries.
         */
        public long[] groupChildAddresses;

        @Override
        public void read(HDF5DataInput channel, Header header, int[] chunkDimension) throws IOException {
            groupChildKeys = new long[header.entriesUsed + 1];
            groupChildAddresses = new long[header.entriesUsed];
            for (int i = 0; i < header.entriesUsed; i++) {
                groupChildKeys[i] = channel.readLength();
                groupChildAddresses[i] = channel.readOffset();
            }
            groupChildKeys[header.entriesUsed] = channel.readLength();
        }

        @Override
        public String toString() {
            return "GroupNode [" + groupChildAddresses.length+ "entries]";
        }
    }

    public static abstract class DataNode extends Node {

        public abstract List<BTreeV1Chunk> getChunks();
    }

    public static final class ParentChunksNode extends DataNode {

        public DataNode[] dataKeys;

        @Override
        public void read(HDF5DataInput channel, Header header, int[] chunkDimension) throws IOException {
            //dataset node
            dataKeys = new DataNode[header.entriesUsed];
            for (int i = 0; i < header.entriesUsed; i++) {
                final BTreeV1Chunk key = new BTreeV1Chunk();
                key.read(channel, chunkDimension);
                key.address = channel.readOffset();
                channel.mark();
                channel.seek(key.address);
                final BTreeV1 subtree = new BTreeV1();
                subtree.read(channel, chunkDimension);
                dataKeys[i] = (DataNode) subtree.root;
                channel.reset();
            }

            //contains one more unused key
            channel.skipFully(4 + 4 + (chunkDimension.length + 1) * 8);
        }

        @Override
        public List<BTreeV1Chunk> getChunks() {
            final List<BTreeV1Chunk> chunks = new ArrayList<>();
            Arrays.asList(dataKeys).forEach((DataNode t) -> chunks.addAll(t.getChunks()));
            return chunks;
        }

        @Override
        public String toString() {
            return StringUtilities.toStringTree("ParentChunksNode", Arrays.asList(dataKeys));
        }

    }

    public static final class LeafChunksNode extends DataNode {

        public BTreeV1Chunk[] dataKeys;

        @Override
        public void read(HDF5DataInput channel, Header header, int[] chunkDimension) throws IOException {
            //dataset node
            dataKeys = new BTreeV1Chunk[header.entriesUsed];
            for (int i = 0; i < header.entriesUsed; i++) {
                dataKeys[i] = new BTreeV1Chunk();
                dataKeys[i].read(channel, chunkDimension);
                dataKeys[i].address = channel.readOffset();
            }
            //contains one more unused key
            channel.skipFully(4 + 4 + (chunkDimension.length + 1) * 8);
        }

        @Override
        public List<BTreeV1Chunk> getChunks() {
            return Arrays.asList(dataKeys);
        }

        @Override
        public String toString() {
            return "ChunksNode [" + dataKeys.length+ "chunks]";
        }
    }

    public static final class Header {

        /**
         * Each B-tree points to a particular type of data.
         * This field indicates the type of data as well as implying the maximum
         * degree K of the tree and the size of each Key field.
         * <ul>
         *   <li>0 : This tree points to group nodes.</li>
         *   <li>1 : This tree points to raw data chunk nodes.</li>
         * </ul>
         */
        public int nodeType;
        /**
         * The node level indicates the level at which this node appears in the tree
         * (leaf nodes are at level zero). Not only does the level indicate whether
         * child pointers point to sub-trees or to data, but it can also be used to
         * help file consistency checking utilities reconstruct damaged trees.
         */
        public int nodeLevel;
        /**
         * This determines the number of children to which this node points.
         * All nodes of a particular type of tree have the same maximum degree,
         * but most nodes will point to less than that number of children.
         * The valid child pointers and keys appear at the beginning of the node
         * and the unused pointers and keys appear at the end of the node.
         * The unused pointers and keys have undefined values.
         */
        public int entriesUsed;
        /**
         * This is the relative file address of the left sibling of the current node.
         * If the current node is the left-most node at this level then this field is
         * the undefined address.
         */
        public long addressOfLeftSibling;
        /**
         * This is the relative file address of the right sibling of the current node.
         * If the current node is the right-most node at this level then this field
         * is the undefined address.
         */
        public long addressOfRightSibling;

        public void read(HDF5DataInput channel) throws IOException {
            channel.ensureSignature(SIGNATURE);
            nodeType = channel.readUnsignedByte();
            nodeLevel = channel.readUnsignedByte();
            entriesUsed = channel.readUnsignedShort();
            addressOfLeftSibling = channel.readOffset();
            addressOfRightSibling = channel.readOffset();
        }
    }
}
