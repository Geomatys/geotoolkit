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
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Version 2 (v2) B-trees are “traditional” B-trees with one major difference.
 * Instead of just using a simple pointer (or address in the file) to a child
 * of an internal node, the pointer to the child node contains two additional
 * pieces of information: the number of records in the child node itself, and
 * the total number of records in the child node and all its descendants.
 * Storing this additional information allows fast array-like indexing to locate
 * the nth record in the B-tree.
 * <p>
 * The entry into a version 2 B-tree is a header which contains global information
 * about the structure of the B-tree. The root node address field in the header
 * points to the B-tree root node, which is either an internal or leaf node,
 * depending on the value in the header’s depth field. An internal node consists
 * of records plus pointers to further leaf or internal nodes in the tree.
 * A leaf node consists of solely of records. The format of the records depends
 * on the B-tree type (stored in the header).
 *
 * @see III.A.2. Disk Format: Level 1A2 - Version 2 B-trees
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV2 extends IOStructure {

    /**
     * The ASCII character string “BTHD” is used to indicate the header of a
     * version 2 (v2) B-tree node.
     */
    public static final byte[] SIGNATURE = "BTHD".getBytes(StandardCharsets.US_ASCII);

    /**
     * This field indicates the type of B-tree:
     * <ul>
     * <li>0 : This B-tree is used for testing only. This value should not be used for storing records in actual HDF5 files.</li>
     * <li>1 : This B-tree is used for indexing indirectly accessed, non-filtered ‘huge’ fractal heap objects.</li>
     * <li>2 : This B-tree is used for indexing indirectly accessed, filtered ‘huge’ fractal heap objects.</li>
     * <li>3 : This B-tree is used for indexing directly accessed, non-filtered ‘huge’ fractal heap objects.</li>
     * <li>4 : This B-tree is used for indexing directly accessed, filtered ‘huge’ fractal heap objects.</li>
     * <li>5 : This B-tree is used for indexing the ‘name’ field for links in indexed groups.</li>
     * <li>6 : This B-tree is used for indexing the ‘creation order’ field for links in indexed groups.</li>
     * <li>7 : This B-tree is used for indexing shared object header messages.</li>
     * <li>8 : This B-tree is used for indexing the ‘name’ field for indexed attributes.</li>
     * <li>9 : This B-tree is used for indexing the ‘creation order’ field for indexed attributes.</li>
     * <li>10 : This B-tree is used for indexing chunks of datasets with no filters and with more than one dimension of unlimited extent.</li>
     * <li>11 : This B-tree is used for indexing chunks of datasets with filters and more than one dimension of unlimited extent. </li>
     * </ul>
     */
    private int type;
    /**
     * This is the size in bytes of all B-tree nodes.
     */
    private int nodeSize;
    /**
     * This field is the size in bytes of the B-tree record.
     */
    private int recordSize;
    /**
     * This is the depth of the B-tree.
     */
    private int depth;
    /**
     * The percent full that a node needs to increase above before it is split.
     */
    private int splitPercent;
    /**
     * The percent full that a node needs to be decrease below before it is split.
     */
    private int mergePercent;
    /**
     * This is the address of the root B-tree node. A B-tree with no records
     * will have the undefined address in this field.
     */
    private long rootNodeAddress;
    /**
     * This is the number of records in the root node.
     */
    private int numberOfRecordsInRootNode;
    /**
     * This is the total number of records in the entire B-tree.
     */
    private long totalNumberOfRecords;

    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        The version number for this B-tree header. This document describes version 0.
        */
        channel.ensureVersion(0);
        type = channel.readUnsignedByte();
        nodeSize = channel.readInt();
        recordSize = channel.readUnsignedShort();
        depth = channel.readUnsignedShort();
        splitPercent = channel.readUnsignedByte();
        mergePercent = channel.readUnsignedByte();
        rootNodeAddress = channel.readOffset();
        numberOfRecordsInRootNode = channel.readUnsignedShort();
        totalNumberOfRecords = channel.readLength();
        int checksum = channel.readInt(); //This is the checksum for the B-tree header.
    }
}
