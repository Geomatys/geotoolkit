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
 *
 * @see III.A.2. Disk Format: Level 1A2 - Version 2 B-trees
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV2InternalNode extends IOStructure {

    /**
     * The ASCII character string “BTIN” is used to indicate the internal node of a B-tree.
     */
    public static final byte[] SIGNATURE = "BTIN".getBytes(StandardCharsets.US_ASCII);

    /**
     * This field is the type of the B-tree node. It should always be the
     * same as the B-tree type in the header.
     */
    private int type;

    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        The version number for this B-tree internal node.
        This document describes version 0.
        */
        channel.ensureVersion(0);
        type = channel.readUnsignedByte();

        /*
        Records

        The size of this field is determined by the number of records for this
        node and the record size (from the header).
        The format of records depends on the type of B-tree.

        Child Node Pointer

        This field is the address of the child node pointed to by the internal node.

        Number of Records in Child Node

        This is the number of records in the child node pointed to by the corresponding Node Pointer.
        The number of bytes used to store this field is determined by the maximum possible number of records able to be stored in the child node.

        The maximum number of records in a child node is computed in the following way:
        Subtract the fixed size overhead for the child node (for example, its signature, version, checksum, and so on and one pointer triplet of information for the child node (because there is one more pointer triplet than records in each internal node)) from the size of nodes for the B-tree.
        Divide that result by the size of a record plus the pointer triplet of information stored to reach each child node from this node.

        Note that leaf nodes do not encode any child pointer triplets, so the maximum number of records in a leaf node is just the node size minus the leaf node overhead, divided by the record size.

        Also note that the first level of internal nodes above the leaf nodes do not encode the Total Number of Records in Child Node value in the child pointer triplets (since it is the same as the Number of Records in Child Node), so the maximum number of records in these nodes is computed with the equation above, but using (Child Pointer, Number of Records in Child Node) pairs instead of triplets.

        The number of bytes used to encode this field is the least number of bytes required to encode the maximum number of records in a child node value for the child nodes below this level in the B-tree.

        For example, if the maximum number of child records is 123, one byte will be used to encode these values in this node; if the maximum number of child records is 20000, two bytes will be used to encode these values in this node; and so on. The maximum number of bytes used to encode these values is 8 (in other words, an unsigned 64-bit integer).

        Total Number of Records in Child Node

        This is the total number of records for the node pointed to by the corresponding Node Pointer and all its children. This field exists only in nodes whose depth in the B-tree node is greater than 1 (in other words, the “twig” internal nodes, just above leaf nodes, do not store this field in their child node pointers).

        The number of bytes used to store this field is determined by the maximum possible number of records able to be stored in the child node and its descendants.

        The maximum possible number of records able to be stored in a child node and its descendants is computed iteratively, in the following way: The maximum number of records in a leaf node is computed, then that value is used to compute the maximum possible number of records in the first level of internal nodes above the leaf nodes. Multiplying these two values together determines the maximum possible number of records in child node pointers for the level of nodes two levels above leaf nodes. This process is continued up to any level in the B-tree.

        The number of bytes used to encode this value is computed in the same way as for the Number of Records in Child Node field.

        */

        int cheksum = channel.readInt();

        throw new IOException("TODO");
    }
}
